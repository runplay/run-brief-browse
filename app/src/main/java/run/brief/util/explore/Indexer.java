package run.brief.util.explore;

import android.content.Context;
import android.os.Process;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import run.brief.b.BrowseService;
import run.brief.b.Device;
import run.brief.b.State;
import run.brief.beans.BriefSettings;
import run.brief.browse.R;
import run.brief.secure.Validator;
import run.brief.util.Cal;
import run.brief.util.Files;
import run.brief.util.explore.fm.FileManagerDisk;
import run.brief.util.log.BLog;

/**
 * Created by coops on 27/07/15.
 */
public class Indexer {

    private static final Indexer IND = new Indexer();

    private boolean isRunning=false;
    private boolean isRunningFullIndex=false;
    private List<String> indexQue = new ArrayList<String>();
    private Context context;

    public static final String downloadFolder="Download";
    public static final String[] tryfolders = {"DCIM","Pictures","Movies","Music"};



    public static boolean isRunningFullIndex() {
        return IND.isRunning;
    }
    public static boolean isRunning() {
        return IND.isRunning;
    }

    public static void refresh(Context context) {
        //Log.e("INDS","Indexder refresh call !! ......................................................................");
        BrowseService.ensureStartups(context);
        if(IND.context==null)
            IND.context=context;
        if(!IND.isRunning && Validator.isValidCaller()) {
            IND.isRunning=true;

            IndexerDb.init(context);
            Runnable go = new Runnable() {
                @Override
                public void run() {
                    if(Device.isMediaMounted()) {
                        BriefSettings set=State.getSettings();
                        if(set!=null) {
                            long lastindex = set.getLong(BriefSettings.LONG_LAST_INDEX_QUICK);
                            if (lastindex == 0) {
                                // if is less than 50kb
                                Log.e("INDS", "Indexder FIRST TIME RUN,  DB size on disk: ");
                                IND.isRunningFullIndex = true;
                                runFullIndex();
                                BriefSettings settings = State.getSettings();
                                settings.setLong(BriefSettings.LONG_LAST_INDEX_QUICK, Cal.getUnixTime());
                                settings.save();
                                IND.isRunningFullIndex = false;
                            }
                        }
                    }

                    IND.isRunning=false;
                }
            };
            go.run();
        }
    }
    public static void refresh(Context context, final String hintFilePath, long when) {
        IND.indexQue.add(hintFilePath);
        if(IND.context==null)
            IND.context=context;
        if(!IND.isRunning) {
            IND.isRunning=true;
            IndexerDb.init(context);
            refreshKnownPaths(hintFilePath,when);
            IND.isRunning=false;

        }
    }
    //Runnable runIndex

    private List<FileItem> indexdirs = new ArrayList<FileItem>();


    private static int countImages;
    private static int countDocs;
    private static int countMusic;
    private static int countVideo;
    private static int countText;
    private static int count;

    private static void clearCounts() {
        count=countText=countVideo=countMusic=countDocs=countImages=0;
    }

    private static int runQuickIndex() {


        for(String t:tryfolders) {
            runFolderIndex(Files.SDCARD_PATH+File.separator+t,20);
        }
        return 0;
    }

    private static void runFullIndex() {
        Thread runThread = new Thread(IND.fullIndexThread);
        runThread.setPriority(Thread.MIN_PRIORITY);
        runThread.run();
    }

    private Runnable fullIndexThread = new Runnable() {
        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            runFolderIndex(null,50);
        }
    };


    private static int runFolderIndex(String folder,int pauseMillis) {
        //Log.e("INDS","running FULL index");
        //Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        clearCounts();
        FileManagerDisk fm = null;
        if(folder==null)
            fm=new FileManagerDisk();
        else
            fm=new FileManagerDisk(folder);

        //fm.readDirectory(IND.context);
        List<String> rawfiles=fm.getRawDirectory();

        //List<FileItem> rootfiles = new ArrayList<>();//fm.getDirectory(IND.context);
        for(String raw: rawfiles) {
            FileItem rfile = new FileItem(fm.getPath()+File.separator+raw);
            if(rfile.getAbsoluteFile().isDirectory()) {
                IND.indexdirs.add(rfile);
            }  else {
                count = count+indexFile(rfile);
                count++;
            }
        }

        //Log.e("INDS","root, dir: "+IND.indexdirs.size()+" -- file counted: "+count);
        while(!IND.indexdirs.isEmpty()) {
            FileItem fi=IND.indexdirs.get(0);
            try {
                clearCounts();
                fm.setCurrentDirectory(IND.context, fi.getAbsolutePath());
                fm.readDirectory(IND.context);

                countImages = countDocs = countMusic = countVideo =countText = 0;

                List<FileItem> dirfiles = fm.getDirectory(IND.context);
                for (FileItem rfile : dirfiles) {

                    if (rfile.getAbsoluteFile().isDirectory()) {
                        IND.indexdirs.add(rfile);
                    } else {
                        try {
                            Indexer.class.wait(pauseMillis);
                        } catch(Exception e) {}
                        int didindex = indexFile(rfile);
                        if(didindex>0) {
                            count++;
                            int iconfile = Files.getFileRIcon(rfile.getAbsolutePath());
                            int category = getCategory(iconfile,rfile.getName());
                            if(category == Files.CAT_DOCUMENT)
                                countDocs++;
                            else if(category == Files.CAT_IMAGE)
                                countImages++;
                            else if(category == Files.CAT_TEXTFILE)
                                countText++;
                            else if(category==Files.CAT_VIDEO)
                                countVideo++;
                            else if(category==Files.CAT_SOUND)
                                countMusic++;

                        }
                        //count++;
                    }

                }

                int totals = countDocs+countImages+countText+countVideo+countMusic;

                if(count>0 && (100/count)*totals>49) {

                    final HashMap<Integer,Integer> sortme=new HashMap<Integer,Integer>();
                    sortme.put(countDocs,Files.CAT_DOCUMENT);
                    sortme.put(countImages,Files.CAT_IMAGE);
                    sortme.put(countText,Files.CAT_TEXTFILE);
                    sortme.put(countVideo,Files.CAT_VIDEO);
                    sortme.put(countMusic, Files.CAT_SOUND);



                    Object[] briefsSorted= new TreeSet<Integer>(sortme.keySet()).descendingSet().toArray();



                    int usecat =sortme.get(briefsSorted[0]);
                    IndexerFile ifile = new IndexerFile(fi.getName(),fi.getParentFile().getAbsolutePath());
                    ifile.setInt(IndexerFile.INT_BOOL_ISFOLDER, 1);
                    ifile.setInt(IndexerFile.INT_CATEGORY, usecat);
                    //BLog.e("add FOLDER : " + ifile.toString());
                    if(!IndexerDb.has(fi.getName(), fi.getParentFile().getAbsolutePath())) {
                        //Log.e("INDS", "add file: " + fit.toString() + " -- " + fit.icon);
                        IndexerDb.add(ifile);
                    } else {

                        IndexerDb.update(ifile);
                    }
                    try {
                        Indexer.class.wait(pauseMillis);
                    } catch(Exception e) {}
                }

            } catch(Exception e) {
                BLog.e("EXCEPTION", "!!!!!!!!!!!!!!!!!!  EXCEPTION: " + e.getMessage());
            }
            IND.indexdirs.remove(0);
        }
        Log.e("INDS","FINISHED   ------  running FULL index, total files: "+count);

        return count;
    }

    private static void refreshKnownPaths(String hintFilePath, long when) {
        if(hintFilePath!=null && !hintFilePath.equals("/")) {

            long start = Cal.getUnixTime();
            //BLog.e("RUNNING REFRESH for hintpath: "+hintFilePath);

            File hint = new File(hintFilePath);
            if (hint.exists()) {
                if(hint.isDirectory()) {
                    FileManagerDisk fm = new FileManagerDisk();
                    fm.setCurrentDirectory(IND.context, hintFilePath);
                    fm.readDirectory(IND.context);
                    List<FileItem> dirfiles = fm.getDirectory(IND.context);
                    int count = 0;
                    long now = (new Date()).getTime() - 120;
                    for (FileItem file : dirfiles) {
                        if (!file.isDirectory() && file.lastModified() > when) {
                            //BLog.e("NEW LASTMODIFIED DATE: "+file.getAbsolutePath());
                            indexFile(file);
                            count++;
                        } else {
                            refreshKnownPaths(hintFilePath + File.separator + file.getName(), when);
                        }
                    }
                    //BLog.e("FINISHED   ------  running REFRESH index, files: " + count);
                } else {
                    if(hint.lastModified() > when) {
                        //BLog.e("NEW LASTMODIFIED DATE: "+hint.getAbsolutePath());
                        indexFile(new FileItem(hint));
                    }
                }

            } else {
                //BLog.e( "HINT PATH NOT EXISTS: " + hintFilePath);
            }
            long end = Cal.getUnixTime();
            BLog.e("RUNNING REFRESH FINISHED: "+((end-start)/1000)+" secs");
        }
// TODO

    }
    public static void removeFileFromIndex(FileItem file) {
        IndexerDb.remove(new IndexerFile(file.getName(),file.getParent()));

    }

    public static int indexFile(FileItem file) {
        int iconfile = Files.getFileRIcon(file.getAbsolutePath());
        long mdoified = file.getAbsoluteFile().lastModified();
        int category = getCategory(iconfile,file.getName());
        if(file.exists() && category!=Files.CAT_UNKNOWN) {
            File f= file.getAbsoluteFile();
            IndexerFile ifile = new IndexerFile(0,file.getName(),file.getParentFile().getAbsolutePath(),category,f.length(),iconfile,f.lastModified());
            if(IndexerDb.getDb()==null)
                IndexerDb.init(IND.context);
            FileItem fit = ifile.getAsFileItem();
            if(!IndexerDb.has(file.getName(), file.getParentFile().getAbsolutePath())) {
                //Log.e("INDS", "add file: " + fit.toString() + " -- " + fit.icon);
                IndexerDb.add(ifile);
            } else {
                //Log.e("INDS", "!!!!!!!!!!!!!! UPDATE --  file: " + fit.toString() + " -- " + fit.icon);
                IndexerDb.update(ifile);
            }

            return 1;
        }
        return 0;
    }
    private static int getCategory(int iconfile, String filename) {
        int category = 0;
        switch(iconfile) {
            case R.drawable.f_jpg: category=Files.CAT_IMAGE; break;
            case R.drawable.f_gif: category=Files.CAT_IMAGE; break;
            case R.drawable.f_png: category=Files.CAT_IMAGE; break;
            case R.drawable.f_svg: category=Files.CAT_IMAGE; break;
            case R.drawable.f_bmp: category=Files.CAT_IMAGE; break;
            case R.drawable.f_psd: category=Files.CAT_IMAGE; break;
            case R.drawable.f_tiff: category=Files.CAT_IMAGE; break;
            case R.drawable.f_ai: category=Files.CAT_IMAGE; break;
            case R.drawable.f_eps: category=Files.CAT_IMAGE; break;
            case R.drawable.f_ps: category=Files.CAT_IMAGE; break;

            case R.drawable.f_mp3: category=Files.CAT_SOUND; break;
            case R.drawable.f_wav: category=Files.CAT_SOUND; break;
            case R.drawable.f_wma: category=Files.CAT_SOUND; break;

            case R.drawable.f_m4v: category=Files.CAT_VIDEO; break;
            case R.drawable.f_3gp: category=Files.CAT_VIDEO; break;
            case R.drawable.f_avi: category=Files.CAT_VIDEO; break;
            case R.drawable.f_mp4: category=Files.CAT_VIDEO; break;
            case R.drawable.f_mov: category=Files.CAT_VIDEO; break;
            case R.drawable.f_mpg: category=Files.CAT_VIDEO; break;
            case R.drawable.f_wmv: category=Files.CAT_VIDEO; break;

            case R.drawable.f_doc: category=Files.CAT_DOCUMENT; break;
            case R.drawable.f_docx: category=Files.CAT_DOCUMENT; break;
            case R.drawable.f_odt: category=Files.CAT_DOCUMENT; break;
            case R.drawable.f_pdf: category=Files.CAT_DOCUMENT; break;

            case R.drawable.f_xls: category=Files.CAT_SPREADSHEET; break;
            case R.drawable.f_ods: category=Files.CAT_SPREADSHEET; break;

            case R.drawable.f_ppt: category=Files.CAT_POWERPOINT; break;
            case R.drawable.f_odp: category=Files.CAT_POWERPOINT; break;

            case R.drawable.f_zip: category=Files.CAT_COMPRESSION; break;
            case R.drawable.f_7z: category=Files.CAT_COMPRESSION; break;
            case R.drawable.f_rar: category=Files.CAT_COMPRESSION; break;
            case R.drawable.f_gzip: category=Files.CAT_COMPRESSION; break;

            case R.drawable.f_txt: category=Files.CAT_TEXTFILE; break;
            case R.drawable.f_htm: category=Files.CAT_TEXTFILE; break;
            case R.drawable.f_js: category=Files.CAT_TEXTFILE; break;
            case R.drawable.f_jsp: category=Files.CAT_TEXTFILE; break;
            case R.drawable.f_css: category=Files.CAT_TEXTFILE; break;
            case R.drawable.f_sql: category=Files.CAT_TEXTFILE; break;


            default: category=Files.CAT_UNKNOWN;
        }
        if(category==0) {
            if(Files.isTextFile(filename))
                category=Files.CAT_TEXTFILE;
        }
        return category;
    }

}

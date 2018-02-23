package run.brief.util.explore.fm;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import run.brief.browse.R;
import run.brief.util.Files;
import run.brief.util.explore.FileItem;

/**
 * Created by coops on 04/08/15.
 */
public abstract class FileManagerBase implements FileManager {
    public static final int ORDER_ALPHA_ASC=0;
    public static final int ORDER_ALPHA_DESC=1;
    public static final int ORDER_DATE_ASC=2;
    public static final int ORDER_DATE_DESC=3;
    public static final int ORDER_ORIGINAL=4;

    private int ORDER_BY=ORDER_ALPHA_ASC;

    private int currentFileCount;
    private int currentFolderCount;

    private HashMap<String,FileItem> selectedFiles=new HashMap<String,FileItem>();
    private HashMap<String,FileItem> clipboardCopyFiles=new HashMap<String,FileItem>();

    public List<FileItem> fileList=new ArrayList<FileItem>();
    public boolean isImageDir=false;
    private int startAtPosition;


    public int getStartAtPosition() {
        return startAtPosition;
    }
    public void setStartAtPosition(int pos) {
        if(pos==0 || fileList.isEmpty())
            startAtPosition=0;
        else if(pos>fileList.size()-1)
            startAtPosition=fileList.size()-1;
        else
            startAtPosition=pos;
    }
    public HashMap<String,FileItem> getSelectedFiles() {
        return selectedFiles;
    }


    public void close() {
        fileList=new ArrayList<FileItem>();
    }

    public boolean addSelectedFile(FileItem f) {
        //FileItem fi=new FileItem(f.getName(), Files.getFileRIcon(f.getName()),f.getPath());
        selectedFiles.put(f.getAbsolutePath(), f);

        return true;
    }
    public boolean removeSelectedFile(FileItem f) {
        selectedFiles.remove(f.getAbsolutePath());
        return true;
    }
    public boolean clearSelectedFiles() {
        selectedFiles.clear();
        return true;
    }
    public boolean moveSelectedFilesToClipboard() {
        try {
            clipboardCopyFiles=selectedFiles;
            selectedFiles=new HashMap<String,FileItem>();
            return true;
        } catch(Exception e) {}
        return false;

    }

    public int getCurrentCount() {
        //StringBuilder sb = new StringBuilder("files: ");
        return currentFileCount+currentFolderCount;
    }

    public int getCurrentDirectoryCount() {
        //StringBuilder sb = new StringBuilder("files: ");
        return currentFolderCount;
    }
    public int getCurrentFileCount() {
        //StringBuilder sb = new StringBuilder("files: ");
       return currentFileCount;
    }
    public final HashMap<String,FileItem> getClipboardCopyFiles() {
        return clipboardCopyFiles;
    }
    public final List<FileItem> getClipboardCopyFilesAsList() {
        List<FileItem> clip = new ArrayList<FileItem>();
        Iterator<FileItem> files= getClipboardCopyFiles().values().iterator();
        while(files.hasNext()) {

            clip.add(files.next());
            //BLog.e("ADD: " +clip.get(clip.size()-1).getAbsolutePath());
        }
        return clip;
    }

    public boolean addSelectedClipboardCopyFile(File f) {
        FileItem fi=new FileItem(f.getAbsolutePath(), Files.getFileRIcon(f.getName()));
        clipboardCopyFiles.put(fi.getAbsolutePath(), fi);
        return true;
    }
    public boolean removeSelectedClipboardCopyFile(File f) {
        clipboardCopyFiles.remove(f.getAbsolutePath());
        return true;
    }
    public boolean clearSelectedClipboardCopyFiles() {
        clipboardCopyFiles.clear();
        return true;
    }


    public boolean isCurrentDirImages() {
        return isImageDir;
    }

    public void setOrderBy(Context context, int ORDER_BY) {
        this.ORDER_BY=ORDER_BY;
        reOrderFiles(context);
    }

    public void reOrderFiles(Context context) {

        List<FileItem> sfiles=new ArrayList<FileItem>();
        List<FileItem> sfolders=new ArrayList<FileItem>();

        for(FileItem fi: fileList) {
            if(fi.isDirectory())
                sfolders.add(fi);
            else
                sfiles.add(fi);
        }

        currentFileCount=sfiles.size();
        currentFolderCount=sfolders.size();


        switch(ORDER_BY) {
            case ORDER_ORIGINAL:
                break;
            case ORDER_ALPHA_ASC:
                //BLog.e("ORDER alpha asc");
                Collections.sort(sfiles, PersonComparator.ascending(PersonComparator.getComparator(PersonComparator.NAME_SORT, PersonComparator.DATE_SORT)));
                Collections.sort(sfolders, PersonComparator.ascending(PersonComparator.getComparator(PersonComparator.NAME_SORT, PersonComparator.DATE_SORT)));
                break;
            case ORDER_ALPHA_DESC:
                //BLog.e("ORDER alpha desc");
                Collections.sort(sfiles, PersonComparator.decending(PersonComparator.getComparator(PersonComparator.NAME_SORT, PersonComparator.DATE_SORT)));
                Collections.sort(sfolders, PersonComparator.decending(PersonComparator.getComparator(PersonComparator.NAME_SORT, PersonComparator.DATE_SORT)));
                break;
            case ORDER_DATE_ASC:
                try {
                    Collections.sort(sfiles, PersonComparator.ascending(PersonComparator.getComparator(PersonComparator.DATE_SORT)));
                    Collections.sort(sfolders, PersonComparator.ascending(PersonComparator.getComparator(PersonComparator.NAME_SORT)));
                } catch(Exception e) {
                    Toast.makeText(context, context.getString(R.string.file_explore_order_error), Toast.LENGTH_SHORT);
                }
                break;
            case ORDER_DATE_DESC:
                //BLog.e("ORDER date desc");
                try {
                    Collections.sort(sfiles, PersonComparator.decending(PersonComparator.getComparator(PersonComparator.DATE_SORT)));
                    Collections.sort(sfolders, PersonComparator.ascending(PersonComparator.getComparator(PersonComparator.NAME_SORT)));

                } catch(Exception e) {
                    Toast.makeText(context, context.getString(R.string.file_explore_order_error), Toast.LENGTH_SHORT);
                }
                break;
        }
        sfolders.addAll(sfiles);

        fileList= sfolders;
        //Collections.sort(fileList, PersonComparator.decending(PersonComparator.getComparator(PersonComparator.NAME_SORT, PersonComparator.DATE_SORT)));
    }
    enum PersonComparator implements Comparator<FileItem> {
        DATE_SORT {
            public int compare(FileItem o1, FileItem o2) {
                //if(Cal.toDate(o1.lastModified()).before(Cal.toDate(o2.lastModified())))
                if(o1.lastModified()>o2.lastModified())
                    return 1;
                if(o1.lastModified()<o2.lastModified())
                    return -1;
                return 0;
            }},
        NAME_SORT {
            public int compare(FileItem o1, FileItem o2) {
                return o1.file.toLowerCase(Locale.getDefault()).compareTo(o2.file.toLowerCase(Locale.getDefault()));
            }};

        public static Comparator<FileItem> decending(final Comparator<FileItem> other) {
            return new Comparator<FileItem>() {
                public int compare(FileItem o1, FileItem o2) {
                    return -1 * other.compare(o1, o2);
                }
            };
        }
        public static Comparator<FileItem> ascending(final Comparator<FileItem> other) {
            return new Comparator<FileItem>() {
                public int compare(FileItem o1, FileItem o2) {
                    return -1 * other.compare(o2, o1);
                }
            };
        }

        public static Comparator<FileItem> getComparator(final PersonComparator... multipleOptions) {
            return new Comparator<FileItem>() {
                public int compare(FileItem o1, FileItem o2) {
                    for (PersonComparator option : multipleOptions) {
                        int result = option.compare(o1, o2);
                        if (result != 0) {
                            return result;
                        }
                    }
                    return 0;
                }
            };
        }
    }
}

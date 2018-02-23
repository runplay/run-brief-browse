package run.brief.b;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import run.brief.beans.BriefSettings;
import run.brief.util.Cal;
import run.brief.util.Files;
import run.brief.util.UnZip;
import run.brief.util.Zip;
import run.brief.util.explore.FileItem;
import run.brief.util.explore.Indexer;
import run.brief.util.explore.IndexerDb;
import run.brief.util.log.BLog;


public final class BrowseService extends Service {
	
	private static BrowseService SERVICE;//=new BrowseService();
    private final IBinder mBinder = new LocalBinder();


    private static OnPcConnectReceiver pconreceiver;
    private static OnPcDisconnectReceiver pdisconreceiver;
    private static OnUserPresentReceiver ubreceiver;
    //private static OnUserPresentReceiver ubackreceiver;
    //private static OnUserPresentReceiver uforereceiver;


    private static FileObserver fileObserver;
    private Handler startupHandle= new Handler();
    private RunIndexingTask runIndexTask;

    private boolean PC_CONNECTED;

    private static boolean isAppStarted=false;

    public static void setIsAppStarted(boolean isStarted) {
        isAppStarted=isStarted;
    }
    public static boolean isAppStarted() {
        return isAppStarted;
    }

    public class LocalBinder extends Binder {
        public BrowseService getService() {
            return BrowseService.this;
        }
    }

    @Override
    public void onCreate() {
    	super.onCreate();
        //mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

    }

    public static void ensureStartups(Context context) {
        Files.setAppHomePath(context);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
    	if(SERVICE==null) {
    		SERVICE=this;
    	//doFirstTimeCheck();
    		


            BLog.e("SERVICE", "BrowseService started");

            //runner = new BrowseServiceRunner();
            //runner.i    intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            ensureStartups(getBaseContext());

            IntentFilter ubfilter = new IntentFilter(Intent.ACTION_USER_PRESENT);
            ubreceiver=new OnUserPresentReceiver();
            registerReceiver(ubreceiver, ubfilter);

            //IntentFilter ubackfilter = new IntentFilter(Intent.ACTION_USER_BACKGROUND);
            //ubackreceiver=new OnUserPresentReceiver();
            //registerReceiver(ubackreceiver, ubackfilter);

            //IntentFilter uforefilter = new IntentFilter(Intent.ACTION_USER_FOREGROUND);
            //uforereceiver=new OnUserPresentReceiver();
            //registerReceiver(uforereceiver, uforefilter);


            IntentFilter pconfilter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
            pconreceiver = new OnPcConnectReceiver();
            registerReceiver(pconreceiver, pconfilter);

            IntentFilter pdisconfilter = new IntentFilter(Intent.ACTION_POWER_DISCONNECTED);
            pdisconreceiver = new OnPcDisconnectReceiver();
            registerReceiver(pdisconreceiver, pdisconfilter);


            String fobp = Environment.getExternalStorageDirectory().toString();//Environment.getExternalStorageDirectory().toString()+File.separator+"Pictures";
            fileObserver = new FileObserver(fobp) { // set up a file observer to watch this directory on sd card

                @Override
                public void onEvent(int event, String file) {

                    if(file!=null && !file.endsWith(".os")) {

                        switch (event) {
                            case FileObserver.DELETE:
                            case FileObserver.DELETE_SELF:
                                BLog.e("FO", "Delete self ["+event+" : " +Environment.getExternalStorageDirectory().toString()+File.separator+ file + "]");
                                Indexer.removeFileFromIndex(new FileItem(Environment.getExternalStorageDirectory().toString() + File.separator + file));
                                break;
                            case FileObserver.MODIFY:
                                BLog.e("MODIFY event: " + event + "-" + file);
                            case FileObserver.CREATE:
                                //BLog.e("event: " + event + "-" + file);
                                SERVICE.eventReceived(Environment.getExternalStorageDirectory().toString()+File.separator+ file, Cal.getUnixTime());
                                break;
                            case FileObserver.OPEN:
                                SERVICE.recentlyOpenedFiles(Environment.getExternalStorageDirectory().toString()+File.separator+ file, Cal.getUnixTime());
                                break;

                        }

                        /*
                        switch (event) {
                            case FileObserver.DELETE_SELF:
                                BLog.e("FO", "Delete self ["+event+" : " +Environment.getExternalStorageDirectory().toString()+File.separator+ file + "]");
                                Indexer.removeFileFromIndex(new FileItem(Environment.getExternalStorageDirectory().toString() + File.separator + file));
                                break;
                            case FileObserver.MODIFY:
                            case FileObserver.CREATE:
                            case FileObserver.DELETE:
                            case FileObserver.OPEN:
                                BLog.e("FO", "File MODIFY, OPEN,  CREATE, DELETE [" + event + " : " + Environment.getExternalStorageDirectory().toString() + File.separator + file + "]");
                                Indexer.indexFile(new FileItem(Environment.getExternalStorageDirectory().toString() + File.separator + file));
                                break;

                        }
*/
                        //File f = new File(Environment.getExternalStorageDirectory().toString()+File.separator+ file);
                        //Indexer.refresh(SERVICE,Environment.getExternalStorageDirectory().toString()+File.separator+ file);
                    }


                }
            };
            fileObserver.startWatching();

            if(runIndexTask==null && Device.isMediaMounted()) {
                runIndexTask=new RunIndexingTask();
                runIndexTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);
            }

    	}

    	return START_STICKY;
    }


    
    @Override
    public void onDestroy() {
        super.onDestroy();
    	BLog.e("SERVICE", "BrowseService stopped");


        unregisterReceiver(pconreceiver);
        unregisterReceiver(pdisconreceiver);
        //unregisterReceiver(uforereceiver);
        unregisterReceiver(ubreceiver);
        //unregisterReceiver(ubackreceiver);

        fileObserver.stopWatching();
        SERVICE=null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //return mBinder;
        return null;
    }

    private class RunIndexingTask extends AsyncTask<Boolean, Void, Boolean> {

        private Context context;

        private void setActivity(Context context) {
            this.context=context;
        }
        @Override
        protected Boolean doInBackground(Boolean... params) {
            BLog.e("RunIndexingTask activated");
            IndexerDb.init(SERVICE);
            //IndexerDb.getDb().deleteAll();
            //State.getSettings().setInt();

            Indexer.refresh(SERVICE);


            return Boolean.TRUE;

        }
        @Override
        protected void onPostExecute(Boolean result) {

        }

    }
	
    public static boolean isBrowseServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            //Log.e("SERV",service.service.getClassName());
            if (BrowseService.class.getName().equals(service.service.getClassName())) {
            	//BLog.e("TESTSERVICE","BRIEF SERVICE TEST = IS NOT RUNNING");
                return true;
            }
        }
        return false;
    }
    private class OnPcConnectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized(this) {
                SERVICE.PC_CONNECTED=true;
                BLog.e("BriefService - OnPcConnectReceiver","onReceive()");
            }

        }
    }

    private class OnPcDisconnectReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized(this) {
                SERVICE.PC_CONNECTED=false;
                BLog.e("BriefService - OnPcDisconnectReceiver","onReceive()");

            }

        }
    }
    private class OnUserPresentReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized(this) {
                BLog.e("onReceive() ");
                BriefSettings settings = State.getSettings();
                if(intent.getAction().equals(Intent.ACTION_USER_BACKGROUND)) {
                    BLog.e("ACTION_USER_BACKGROUND ");
                } else if(intent.getAction().equals(Intent.ACTION_USER_FOREGROUND)) {
                    BLog.e("ACTION_USER_FOREGROUND ");
                } else {
                    BLog.e("ACTION_USER_PRESENT ");

                }
                //BLog.e("BriefService - OnUserPresentReceiver","onReceive()");

// TODO

            }

        }
    }




    private boolean isUnArchiving=false;
    private RunUnArchivingTask unArchiveTask;
    private int untotalfiles;
    private int uncurrentarchivecount;
    private HashMap<String,Boolean> unzippedfiles;
    //private FileItem unzipfile;

    public static void clearUnzippedFiles() {
        SERVICE.unzippedfiles=null;
    }
    public static final boolean isUnzipCompleteFor(String filename) {
        if(SERVICE.unzippedfiles!=null)
            return SERVICE.unzippedfiles.get(filename);
        return false;
    }
    public static boolean isUnArchiving() {
        if(SERVICE==null)
            return false;
        return SERVICE.isUnArchiving;
    }
    public static int getCurrentUnArchiveCount() {
        return SERVICE.uncurrentarchivecount;
    }
    public static int getCurrentUnArchiveTotal() {
        return SERVICE.untotalfiles;
    }
    public static void setCurrentUnArchivedCount(int count) {
        SERVICE.uncurrentarchivecount=count;
    }

    public static boolean unArchiveFiles(String archiveFilePath, String toFolder) {
        if(isUnArchiving())
            return false;

        SERVICE.isUnArchiving=true;
        SERVICE.unArchiveTask = SERVICE.new RunUnArchivingTask();
        SERVICE.unArchiveTask.setZipFile(new FileItem(archiveFilePath));
        SERVICE.unArchiveTask.setToFolder(toFolder);
        SERVICE.unArchiveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);
        return true;
    }

    private class RunUnArchivingTask extends AsyncTask<Boolean, Void, Boolean> {

        private FileItem zipfile;
        private String unzipfolder;

        private void setZipFile(FileItem zipfile) {
            this.zipfile=zipfile;
        }
        private void setToFolder(String unzipfolder) {
            this.unzipfolder=unzipfolder;
        }
        @Override
        protected Boolean doInBackground(Boolean... params) {
            SERVICE.unzippedfiles=new HashMap<String,Boolean>();
            UnZip.extract(zipfile.getAbsolutePath(), unzipfolder,SERVICE.unzippedfiles);
            return Boolean.TRUE;

        }
        @Override
        protected void onPostExecute(Boolean result) {
            SERVICE.isUnArchiving=false;
        }

    }





    private boolean isArchiving=false;
    private RunArchivingTask archiveTask;
    private int totalfiles;
    private int currentarchivecount;
    private FileItem zipfile;


    public static final FileItem getCurrentArhiveFileItem() {
        return SERVICE.zipfile;
    }
    public static boolean isArchiving() {
        if(SERVICE==null)
            return false;
        return SERVICE.isArchiving;
    }
    public static int getCurrentArchiveCount() {
        return SERVICE.currentarchivecount;
    }
    public static int getCurrentArchiveTotal() {
        return SERVICE.totalfiles;
    }
    public static void setCurrentArchivedCount(int count) {
        SERVICE.currentarchivecount=count;
    }

    public static boolean ArchiveFiles(String archiveFilePath, String archiveFilename, List<FileItem>files) {
        if(SERVICE.isArchiving())
            return false;

        SERVICE.zipfile = new FileItem(archiveFilePath+File.separator+archiveFilename);
        SERVICE.isArchiving=true;
        SERVICE.archiveTask = SERVICE.new RunArchivingTask();
        SERVICE.archiveTask.setZipFile(SERVICE.zipfile);
        SERVICE.archiveTask.setList(files);
        SERVICE.archiveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);
        return true;
    }

    private class RunArchivingTask extends AsyncTask<Boolean, Void, Boolean> {

        private FileItem zipfile;
        List<FileItem>files;

        private void setZipFile(FileItem zipfile) {
            this.zipfile=zipfile;
        }
        private void setList(List<FileItem>files) {
            this.files=files;
        }
        @Override
        protected Boolean doInBackground(Boolean... params) {
            //BLog.e("POINT", "1");
            //IndexerDb.init(SERVICE);
            //Log.e("DB", "DB SIZE: " + IndexerDb.getDb().getSizeOnDisk());
            try {
                wait(1000);
            } catch(Exception e) {}
            Zip.compress(zipfile.getParent(),zipfile.getName(),files);
            return Boolean.TRUE;

        }
        @Override
        protected void onPostExecute(Boolean result) {
            SERVICE.isArchiving=false;
        }

    }



    private Activity activity;



    // move files
    private boolean isMoving=false;
    private RunMovingTask moveTask;
    public static boolean isMoving() {
        if(SERVICE==null)
            return false;
        return SERVICE.isMoving;
    }
    public static boolean MoveFiles(Activity activity, List<FileItem>files, String moveToPath) {
        SERVICE.activity=activity;
        if(SERVICE.isMoving)
            return false;

        //SERVICE.zipfile = new FileItem(archiveFilePath+File.separator+archiveFilename);
        SERVICE.isMoving=true;
        SERVICE.moveTask = SERVICE.new RunMovingTask();
        SERVICE.moveTask.setMoveToPath(moveToPath);
        SERVICE.moveTask.setList(files);
        SERVICE.moveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);
        return true;
    }

    private class RunMovingTask extends AsyncTask<Boolean, Void, Boolean> {

        //private FileItem zipfile;
        List<FileItem>files;
        private String moveToPath;

        private void setList(List<FileItem>files) {
            this.files=files;
        }
        private void setMoveToPath(String path) {
            moveToPath=path;
        }
        @Override
        protected Boolean doInBackground(Boolean... params) {
            if(files!=null && !files.isEmpty()) {
                for (FileItem file : files) {
                    File renamed = new File(Files.getAvailableIncrementedFilePath(moveToPath+File.separator+file.getName()));
                    file.renameTo(renamed);
                }

            }
            //Indexer.refresh(SERVICE);


            return Boolean.TRUE;

        }
        @Override
        protected void onPostExecute(Boolean result) {

            SERVICE.isMoving=false;
            Bgo.refreshCurrentFragment(SERVICE.activity);
        }

    }



    // paste

    private boolean isPasting=false;
    private RunPastingTask pasteTask;
    public static boolean isPasting() {
        if(SERVICE==null)
            return false;
        return SERVICE.isPasting;
    }
    public static boolean PasteFiles(Activity activity, List<FileItem>files, String pasteToPath) {
        SERVICE.activity=activity;
        if(SERVICE.isPasting)
            return false;

        //SERVICE.zipfile = new FileItem(archiveFilePath+File.separator+archiveFilename);
        SERVICE.isPasting=true;
        SERVICE.pasteTask = SERVICE.new RunPastingTask();
        SERVICE.pasteTask.setPasteToPath(pasteToPath);
        SERVICE.pasteTask.setList(files);
        SERVICE.pasteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);
        return true;
    }

    private class RunPastingTask extends AsyncTask<Boolean, Void, Boolean> {

        //private FileItem zipfile;
        List<FileItem>files;
        private String pasteToPath;

        private void setList(List<FileItem>files) {
            this.files=files;
        }
        private void setPasteToPath(String path) {
            pasteToPath=path;
        }
        @Override
        protected Boolean doInBackground(Boolean... params) {
            if(files!=null && !files.isEmpty()) {
                InputStream inStream = null;
                OutputStream outStream = null;
                for (FileItem file : files) {

                    try{

                        //File afile =new File("C:\\folderA\\Afile.txt");
                        File bfile =new File(Files.getAvailableIncrementedFilePath(pasteToPath+File.separator+file.getName()));
                        bfile.createNewFile();
                        inStream = new FileInputStream(file);
                        outStream = new FileOutputStream(bfile);

                        byte[] buffer = new byte[1024];

                        int length;
                        //copy the file content in bytes
                        while ((length = inStream.read(buffer)) > 0){

                            outStream.write(buffer, 0, length);

                        }

                        inStream.close();
                        outStream.close();

                    }catch(IOException e){
                        BLog.e("paste path: " + e.getMessage());
                    }
                }




            }
            //Indexer.refresh(SERVICE);


            return Boolean.TRUE;

        }
        @Override
        protected void onPostExecute(Boolean result) {
            SERVICE.isPasting=false;
            Bgo.refreshCurrentFragment(SERVICE.activity);
        }

    }



    List<String> recentlyOpenedFiles = new ArrayList<String>();
    private static void recentlyOpenedFiles(String folder,long when) {
        for(int i=SERVICE.recentlyOpenedFiles.size()-1; i>=0; i--) {
            if(SERVICE.recentlyOpenedFiles.get(i).equals(folder))
                SERVICE.recentlyOpenedFiles.remove(i);
        }
        SERVICE.recentlyOpenedFiles.add(folder);
        if(SERVICE.recentlyOpenedFiles.size()>20)
            SERVICE.recentlyOpenedFiles.remove(0);
    }


    //HashMap<String,Long> checkFiles = new HashMap<String,Long>();
    private Map<String,Long> checkFiles = new ConcurrentHashMap<String,Long>();
    Handler indexHandler=new Handler();

    private static void eventReceived(String folder,long when) {
        //BLog.e(folder);
        if(SERVICE.checkFiles.get(folder)==null) {
            SERVICE.checkFiles.put(folder, Long.valueOf(when));

            SERVICE.indexHandler.removeCallbacks(SERVICE.runIndexFolderTask);
            SERVICE.indexHandler.postDelayed(SERVICE.runIndexFolderTask, 20000);
        }
    }
    private Runnable runIndexFolderTask = new Runnable() {
        @Override
        public void run() {
            if(!Indexer.isRunning()) {
                SERVICE.new ReIndexTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);
            } else {
                SERVICE.indexHandler.postDelayed(runIndexFolderTask,20000);
            }
        }
    };
    private class ReIndexTask extends AsyncTask<Boolean, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean... params) {

            BLog.e("running process messages");
            Set<String> folders = SERVICE.checkFiles.keySet();
            BLog.e(folders.toString());
            for(String key: folders) {
                long when = SERVICE.checkFiles.get(key);
                FileItem f = new FileItem(key);
                //if(when)
                //BLog.e("INDEXING FILE: "+key);
                Indexer.indexFile(new FileItem(key));
                //Indexer.refresh(SERVICE,key,when);

            }
            for(String key: folders) {
                SERVICE.checkFiles.remove(key);
            }
            return Boolean.TRUE;

        }
        @Override
        protected void onPostExecute(Boolean result) {

        }

    }

    /*




    public static void deleteFiles(List<FileItem> files) {
        if(Validator.isValidCaller()) {
            SERVICE.new LockerAddFilesTask().setFiles(files).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);
        }
    }

    private class LockerAddFilesTask extends AsyncTask<Boolean, Void, Boolean> {

        private int added=0;
        private List<FileItem> files;
        public LockerAddFilesTask setFiles(List<FileItem> files) {
            this.files=files;
            return this;
        }
        @Override
        protected Boolean doInBackground(Boolean... params) {
            if(files!=null && !files.isEmpty()) {
                for(FileItem f: files) {
                    ((File) f).delete();
                }
            }
            return Boolean.TRUE;

        }
        @Override
        protected void onPostExecute(Boolean result) {


        }

    };
    */
}

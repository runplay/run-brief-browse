package run.brief.util.explore.fm;

import android.content.Context;

import java.io.File;
import java.util.HashMap;

import run.brief.util.explore.FileItem;

/**
 * Created by coops on 04/08/15.
 */
public interface FileManager {




    //public void init(Context context);
    public void close();

    public File getCurrentDirectory();
    public FileItem getDirectoryItem(int index);

    public int getCurrentFileCount();
    public int getCurrentCount();
    public boolean isCurrentDirImages();

    public boolean addSelectedClipboardCopyFile(File f);
    public boolean removeSelectedFile(FileItem f);
    public boolean addSelectedFile(FileItem f);
    public boolean moveSelectedFilesToClipboard();
    public boolean readDirectory(final Context context);
    public HashMap<String,FileItem> getSelectedFiles();

    public void refresh(Context context);

}

package run.brief.util.explore.fm;


import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import run.brief.util.Files;
import run.brief.util.explore.FileItem;
import run.brief.util.explore.FileItemZip;

public class FileManagerZip implements FileManager {
	//private final FileManager FM = new FileManager();

	private String currentPath;

    //private boolean showSystemFiles=false;

	private ZipFile useZipFile;
	private boolean okFile=true;

    public List<FileItem> fileList=new ArrayList<FileItem>();

    public void close() {
        fileList=new ArrayList<FileItem>();
    }
    public int getCurrentCount() {
        //StringBuilder sb = new StringBuilder("files: ");
        return fileList.size();
    }

    public int getCurrentDirectoryCount() {
        //StringBuilder sb = new StringBuilder("files: ");
        return fileList.size();
    }


	public FileItem getDirectoryItem(int index) {
		//BLog.e("LLL","get FI index: "+index);
		if(fileList!= null && index<fileList.size())
			return fileList.get(index);
		return null;
	}

	public File getCurrentDirectory() {
		if(currentPath!= null)
			return new File(currentPath);
		return null;
	}
    public boolean isCurrentDirImages() {
        return false;
    }

    public int getCurrentFileCount() {
        //StringBuilder sb = new StringBuilder("files: ");
        return fileList.size();
    }
	public FileManagerZip(String zipFileAbsolutePath) {
		//zipFile=zipFileAbsolutePath;
		currentPath= zipFileAbsolutePath;
		try {
			useZipFile = new ZipFile(zipFileAbsolutePath);
		} catch(Exception e) {
			okFile=false;
		}
	}

	public boolean isZipFileOk() {
		return okFile;
	}
	public void refresh(Context context) {
		readDirectory(context);
	}

    public void setZipSubFolder(String subFolderPath) {
        currentPath=subFolderPath;
    }

	public void goUpDirectory(Context context) {

	}

    public HashMap<String,Boolean> unzippedfiles = new HashMap<String,Boolean>();
	//public void readDirectorySearch()


	public boolean readDirectory(final Context context) {

		fileList = new ArrayList<FileItem>();

		int countImages=0;

        if(isZipFileOk()) {

            Enumeration<? extends ZipEntry> entries = useZipFile.entries();

            int i=0;
            String currentZipPath=File.separator;
            String currentSubPath="";
            while (entries.hasMoreElements()) {
                ZipEntry f = entries.nextElement();

                if(i!=0 && f.isDirectory())
                    currentSubPath=File.separator;


                FileItemZip fz = new FileItemZip(f.getName(), f.getSize());
                fz.subpath=currentZipPath+currentSubPath;
                if (Files.isImage(fz.getName()))
                    countImages++;
                fz.icon = Files.getFileRIcon(fz.getName());

                if(i!=0 && f.isDirectory())
                    currentSubPath=File.separator+f.getName();

                fileList.add(fz);
                i++;
            }



            //reOrderFiles(context);
        }
        return true;

	}

	
	/*

	Following have no purpose yet

	 */
    public boolean addSelectedFile(FileItem f) {
        return true;
    }

    public boolean removeSelectedFile(FileItem f) {
        // has no purpose yet
        return true;
    }
    public HashMap<String,FileItem> getSelectedFiles() {
        // has no purpose yet
        return new HashMap<String,FileItem>();
    }
    public boolean clearSelectedFiles() {
        // has no purpose yet
        return true;
    }
    public boolean addSelectedClipboardCopyFile(File f) {
        return true;
    }
    public boolean removeSelectedClipboardCopyFile(File f) {
        return true;
    }
    public boolean clearSelectedClipboardCopyFiles() {
        return true;
    }
    public boolean moveSelectedFilesToClipboard() {
        return true;

    }
	
}

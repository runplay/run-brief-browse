package run.brief.util.explore.fm;


import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import run.brief.b.State;
import run.brief.b.StateObject;
import run.brief.util.Files;
import run.brief.util.explore.FileItem;

public class FileManagerFolders extends FileManagerBase {
	//private final FileManager FM = new FileManager();




	private final String loadPath= Environment.getExternalStorageDirectory().getAbsolutePath();
	private String currentPath;
	private String rootPath;
    private boolean showSystemFiles=false;

	private int startAtPosition;
	public int getStartAtPosition() {
		return startAtPosition;
	}
	public void setStartAtPosition(int pos) {
		startAtPosition=pos;
	}

	public FileManagerFolders() {
		currentPath= State.getStateObjectString(State.SECTION_FILE_EXPLORE, StateObject.STRING_FILE_PATH);
		if(currentPath==null)
			currentPath=loadPath;
	}
	public FileManagerFolders(String rootFolder) {
		rootPath=rootFolder;
		currentPath= rootFolder;
		if(currentPath==null)
			currentPath=loadPath;
	}

    public void setShowSystemFiles(Context context,boolean showFiles) {
        showSystemFiles=showFiles;
        readDirectory(context);
    }
    public boolean getShowSystemFiles() {
        return showSystemFiles;
    }


	public void setCurrentDirectory(Context context,String directory) {
		File dir = new File(directory);
		if(dir.isDirectory()) {
			currentPath=directory;
			//readDirectory(context);
		}
	}
	public String getPath() {
		return currentPath;
	}

	public void refresh(Context context) {
		readDirectory(context);
	}

	public boolean isCurrentPathRootPath() {
		return currentPath.equals(rootPath);
	}

	public void goUpDirectory(Context context) {
		if(!currentPath.equals(File.separator) && !currentPath.equals(rootPath)) {
			currentPath=currentPath.substring(0, currentPath.lastIndexOf(File.separator));
			if(currentPath.length()==0)
				currentPath= File.separator;
			readDirectory(context);
		}			
	}
	public File getDirectoryItemAsFile(int index) {
		if(fileList!= null && index<fileList.size())
			return new File(currentPath,fileList.get(index).file);
		return null;
	}
	public File getCurrentDirectory() {
		if(currentPath!= null)
			return new File(currentPath);
		return null;
	}







	public void init(Context context) {
		readDirectory(context);
	}


	public List<FileItem> getDirectory(Context context) {
		if(fileList==null)
			readDirectory(context);
		return fileList;
	}

	public FileItem getDirectoryItem(int index) {
		//BLog.e("LLL","get FI index: "+index);
		if(fileList!= null && index<fileList.size())
			return fileList.get(index);
		return null;
	}



	//public void readDirectorySearch()


	public boolean readDirectory(final Context context) {
		isImageDir=false;

		//BLog.e("FILEM", ""+currentPath);
		File dir = new File(currentPath);
		final String[] fList = dir.list();//.list(filter);

		int clen=0;
		
		int countImages=0;
		
		if(fList!=null) {
			clen=fList.length;
			fileList = new ArrayList<FileItem>();
			for (int i = 0; i < clen; i++) {


                    // Convert into file path
                    File sel = new File(currentPath, fList[i]);

                    if (sel.isDirectory()) {

						FileItem item = new FileItem(fList[i], Files.getFileRIcon(fList[i]), currentPath);


						if (sel.isDirectory()) {
							item.icon = Files.F_DIR; //R.drawable.directory_icon;
							//item.isDir = true;
						}
						//item.size=sel.length();

						fileList.add(item);
					}

			}

			if(countImages/clen>0.5D)
				isImageDir=true;

		} else {
			fileList = new ArrayList<FileItem>();
		}
        reOrderFiles(context);
		return true;

	}

	
	


	
}

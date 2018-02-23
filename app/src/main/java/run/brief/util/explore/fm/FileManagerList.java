package run.brief.util.explore.fm;


import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import run.brief.util.Files;
import run.brief.util.explore.FileItem;
import run.brief.util.json.JSONArray;

public class FileManagerList extends FileManagerBase {
	//private final FileManager FM = new FileManager();




	private final String loadPath= Environment.getExternalStorageDirectory().getAbsolutePath();
	private String currentPath=loadPath;
    private boolean showSystemFiles=false;

	public boolean isCutPasteFilesOnClipboard=false;



	private String[] rawList;

	public FileManagerList(List<FileItem> items) {
		fileList=items;
	}


	public void refresh(Context context) {
		readDirectory(context);
	}

	public File getDirectoryItemAsFile(int index) {
		if(fileList!= null && index<fileList.size())
			return new File(currentPath,fileList.get(index).file);
		return null;
	}
	public File getCurrentDirectory() {
		return null;
		//return new File(loadPath);
	}



	public JSONArray getSelectedFilesAsJSONArray() {
		JSONArray jarr = new JSONArray();
		Iterator<String> it = getSelectedFiles().keySet().iterator();
		while (it.hasNext()) {
			String pairs = (String)it.next();
			jarr.put(pairs);
			//it.remove(); // avoids a ConcurrentModificationException
		}
		//BLog.e("FEF",FileExploreAdapter.selectedFiles.size()+"--"+ jarr.toString());
		return jarr;
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

	private String inMemoryPath;
	public int loadDirectory() {
		//if(!currentPath.equals(inMemoryPath)) {
			inMemoryPath = currentPath;

			File dir = new File(currentPath);
			rawList = dir.list();
		//}
		return rawList.length;
	}
	//public void readDirectorySearch()


	public boolean readDirectory(final Context context) {
		isImageDir=false;

		loadDirectory();

		int clen=0;
		
		int countImages=0;
		
		if(rawList!=null) {
			clen=rawList.length;
			fileList = new ArrayList<FileItem>();
			for (int i = 0; i < clen; i++) {
                if(showSystemFiles || (!showSystemFiles && !rawList[i].startsWith(".")) ) {

                    // Convert into file path
                    File sel = new File(currentPath, rawList[i]);

                    if (Files.isImage(sel.getName()))
                        countImages++;

                    FileItem item = new FileItem(rawList[i], Files.getFileRIcon(rawList[i]), currentPath);


                    if (sel.isDirectory()) {
                        item.icon = Files.F_DIR; //R.drawable.directory_icon;
                        //item.isDir = true;
                    }
                    //item.size=sel.length();
					//item.
                    fileList.add(item);
                    // Set drawables
                }

			}
			double len = fileList.size();
			
			//BLog.e("FILEZ", "total: "+len+" - images: "+countImages);
			if(countImages/len>0.5D)
				isImageDir=true;

		} else {
			fileList = new ArrayList<FileItem>();
		}
        reOrderFiles(context);

		return true;
	}

	
	


	
}

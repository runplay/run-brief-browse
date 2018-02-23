package run.brief.util.explore.fm;


import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import run.brief.b.State;
import run.brief.b.StateObject;
import run.brief.util.Files;
import run.brief.util.explore.FileItem;
import run.brief.util.explore.IndexerDb;
import run.brief.util.explore.IndexerFile;
import run.brief.util.json.JSONArray;

public class FileManagerDisk extends FileManagerBase {
	//private final FileManager FM = new FileManager();




	private final String loadPath= Environment.getExternalStorageDirectory().getAbsolutePath();
	private String currentPath=loadPath;
    private boolean showSystemFiles=false;
    private HashMap<String,IndexerFile> folderCats;

	public boolean isCutPasteFilesOnClipboard=false;
	boolean useRaw=false;

	private List<HashMap<String,List<FileItem>>> cachedfolders = new ArrayList<HashMap<String,List<FileItem>>>();


	private List<String> rawList;

	public FileManagerDisk() {
		currentPath= State.getStateObjectString(State.SECTION_FILE_EXPLORE, StateObject.STRING_FILE_PATH);
		if(currentPath==null)
			currentPath=loadPath;
	}
	public FileManagerDisk(String startFolder) {
		currentPath= startFolder;
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


	public boolean setCurrentDirectory(Context context,String directory) {
		File dir = new File(directory);
		if(dir.isDirectory() && dir.canRead()) {
			currentPath=directory;
			return true;
			//readDirectory(context);
		}
		return false;
	}
	public String getPath() {
		return currentPath;
	}

	public void refresh(Context context) {
		readDirectory(context);
	}

	public void goUpDirectory(Context context) {
		if(!currentPath.equals(File.separator)) {
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
		return new File(currentPath);
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
	public List<String> getRawDirectory() {
		loadDirectory();

		return rawList;
	}

	public FileItem getDirectoryItem(int index) {
		//BLog.e("LLL","get FI index: "+index);
		if(useRaw) {
            if(rawList!=null && rawList.size()>index) {
                return new FileItem(currentPath + File.separator + rawList.get(index));
            } else {
                return null;
            }
		}

		if(fileList!= null && index<fileList.size())
			return fileList.get(index);
		return null;
	}

	//private String inMemoryPath;
	public int loadDirectory() {
        int countImages=0;
        isImageDir=false;
        //BLog.e("........................."+currentPath);
        File dir = new File(currentPath);

		if(dir.isDirectory() && dir.canRead()) {
			rawList = Arrays.asList(dir.list());
			//int len=rawList.size();
			//BLog.e(".........................2");
			if(!rawList.isEmpty()) {
				int count = 0;
				for (String str : rawList) {
					//BLog.e(str);
					if (Files.isImage(Files.removeBriefFileExtension(str)))
						countImages++;
					if (++count > 10)
						break;
				}

				//int max = 10 > count ? 10 : count;
				//BLog.e("---max: " + count + ", count img: " + countImages+" ----- "+(((double)countImages / count)>0.5D));
				if (countImages!=0 && ((double)countImages / count) > 0.5D)
					isImageDir = true;//}
			}
			Collections.reverse(rawList);
			return rawList.size();
		} else {

			return -1;
		}
	}
	//public void readDirectorySearch()


	public boolean readDirectory(final Context context)		{
		if(rawList==null)
			loadDirectory();

		useRaw=true;
        folderCats= IndexerDb.getDb().getSubFolderCategories(getPath());
        //BLog.e("FOUND CATS: "+folderCats.size()+"- "+folderCats.keySet().toString());
		int clen=0;


		if(rawList!=null) {
			//clen=rawList.length;
			fileList = new ArrayList<FileItem>();
			for (int i = 0; i < rawList.size(); i++) {
                if(showSystemFiles || (!showSystemFiles && !rawList.get(i).startsWith(".")) ) {

                    // Convert into file path
                    File sel = new File(currentPath, rawList.get(i));

                    FileItem item = new FileItem(rawList.get(i), Files.getFileRIcon(rawList.get(i)), currentPath);


                    if (sel.isDirectory()) {
                        IndexerFile idf=folderCats.get(item.getName());
                        if(idf!=null) {
							item.icon=Files.getFoldersRIcon(idf.getInt(IndexerFile.INT_CATEGORY));

                        } else {
                            item.icon = Files.F_DIR; //R.drawable.directory_icon;
                        }
                        //item.isDir = true;
                    }
                    fileList.add(item);
                }

			}
			double len = fileList.size();
			//BLog.e("CP: "+currentPath);
			if(currentPath.endsWith("/Camera")) {
				this.setOrderBy(context, FileManagerBase.ORDER_DATE_DESC);
			} else {

				this.setOrderBy(context, FileManagerBase.ORDER_ALPHA_ASC);
			}
			reOrderFiles(context);
			/*
			if(fileList.size()<300) {
				this.setOrderBy(context, FileManagerBase.ORDER_DATE_ASC);
				reOrderFiles(context);
			} else {
				this.setOrderBy(context, FileManagerBase.ORDER_ORIGINAL);
				reOrderFiles(context);
			}
*/
			//BLog.e("FILEZ", "total: "+len+" - images: "+countImagesue;

		} else {
			fileList = new ArrayList<FileItem>();
		}


		useRaw=false;
		rawList=null;
		return true;
	}

	
	


	
}

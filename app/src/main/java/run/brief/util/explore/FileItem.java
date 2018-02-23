package run.brief.util.explore;

import java.io.File;

import run.brief.util.Files;

public class FileItem extends File {
	/**
	 * 
	 */

	public static final int PARENT_TYPE_DISK=0;
	public static final int PARENT_TYPE_ZIP=1;
	public static final int PARENT_TYPE_SEARCH=2;

	public int PARENT_TYPE_=PARENT_TYPE_DISK;

	private static final long serialVersionUID = 1L;
	//public boolean isDir;
	public String file;
	public int icon;
	private int category;
	public long zipFileSize;

	//public long size;
	//public Date created;
	//public String absolutePath;
	//public boolean isChecked;
/*
	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
*/


	public FileItem(File file) {
		super(file.getAbsolutePath());
		this.file = this.getName();
		this.icon = Files.getFileRIcon(this.file);
		//this.absolutePath=absolutePath;
	}

	public FileItem(String filepath) {
		super(filepath);
		this.file = this.getName();
		this.icon = Files.getFileRIcon(this.file);
		//this.absolutePath=absolutePath;
	}
	public FileItem(String filepath, Integer icon) {
		super(filepath);
		this.file = this.getName();
		this.icon = icon;
		//this.absolutePath=absolutePath;
	}

	public FileItem(String filename, Integer icon, String path) {
		super(path+ File.separator+filename);
		this.file = filename;
		this.icon = icon;
		//this.absolutePath=absolutePath;
	}

	public int getCategory() {
		if(category==0) {

		}
		return category;
	}

	@Override
	public String toString() {
		return this.getAbsolutePath();
	}
}
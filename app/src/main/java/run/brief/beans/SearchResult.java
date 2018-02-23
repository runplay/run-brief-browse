package run.brief.beans;

import java.io.File;

public class SearchResult extends File {
	private static final long serialVersionUID = 1L;
	public boolean isDir;
	public String file;
	public int icon;
	public SearchResult(String filepath, Integer icon) {
		super(filepath);
		this.file = this.getName();
		this.icon = icon;
		//this.absolutePath=absolutePath;
	}

	public SearchResult(String filename, Integer icon, String path) {
		super(path+ File.separator+filename);
		this.file = filename;
		this.icon = icon;
		//this.absolutePath=absolutePath;
	}
	/*
	private int WITH_;
	private int TYPE_;
	private int index;
	private String key;
	private String resultText;
	private String resultHead;
	public int getWITH_() {
		return WITH_;
	}
	public void setWITH_(int wITH_) {
		WITH_ = wITH_;
	}
	public int getTYPE_() {
		return TYPE_;
	}
	public void setTYPE_(int tYPE_) {
		TYPE_ = tYPE_;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getResultText() {
		return resultText;
	}
	public void setResultText(String resultText) {
		this.resultText = resultText;
	}
	public String getResultHead() {
		return resultHead;
	}
	public void setResultHead(String resultHead) {
		this.resultHead = resultHead;
	}
	
	*/
}

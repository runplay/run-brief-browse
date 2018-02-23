package run.brief.util;

import java.io.File;

public class FileCreateTask {
	
	public static final int STATUS_NOT_STARTED=0;
	public static final int STATUS_CREATE_OK=1;
	public static final int STATUS_CREATE_FAILED=2;
	

	private String path;
	private String filename;

	private int status;
	private String statusMessage="Nothing done";
	private File file;
	
	public FileCreateTask() throws NoSuchMethodException {
		throw new NoSuchMethodException();
	}
	public FileCreateTask(String filepath, String filename) {
		this.path=filepath;
		this.filename=filename;
		if(ensureDirectory(path)) {
			File f= new File(path+ File.separator+filename);
			if(!f.exists()) {
				try {
					f.createNewFile();
				} catch(Exception e) {

				}
				
			}
			if(!f.exists()) {
				status=STATUS_CREATE_FAILED;
				statusMessage="Problem creating file, check storage is enabled";
			} else {
				status=STATUS_CREATE_OK;
				statusMessage="File created ok";
				file=f;
			}
		} else {
			status=STATUS_CREATE_FAILED;
			statusMessage="Problem creating file, check storage is enabled";
		}
		
	}
	private boolean ensureDirectory(String path) {
		File dir = new File(path);
		if(!dir.exists())
			return dir.mkdirs();
		return true;
	}
	public String getFilename() {
		return filename;
	}
	public String getFilepath() {
		return path;
	}
	public int getStatus() {
		return status;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public File getFile() {
		return file;
	}

}

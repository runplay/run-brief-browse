package run.brief.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import run.brief.secure.HomeFarm;
import run.brief.secure.Encrypt;
import run.brief.util.log.BLog;

public class FileWriteTask {
	
	public static final int STATUS_NOT_STARTED=0;
	public static final int STATUS_WRITE_OK=1;
	public static final int STATUS_WRITE_FAILED=2;
	

	private String filename;
	private String path;
	private String writeContent;
    private String useKey;

	private int status;
	private String statusMessage;
	
	public FileWriteTask() throws NoSuchMethodException {
		throw new NoSuchMethodException();
	}
	public FileWriteTask(String filepath, String filename, String writeContent) {
		this.path=filepath;
		this.filename=filename;
		this.writeContent=writeContent;
	}
    public FileWriteTask(String useKey, String filepath, String filename, String writeContent) {
        this.path=filepath;
        this.filename=filename;
        this.writeContent=writeContent;
        this.useKey=useKey;
    }
	
	public String getFilename() {
		return filename;
	}

    public boolean exists() {
        File f = new File(path+ File.separator+filename);
        //BLog.e("FILE",f.exists()+" : "+f.canRead()+" - "+f.canWrite()+ " - "+f.length());
        if(f.exists()) {
            //f=null;
            return true;
        }
        return false;
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


	public static boolean writeBytesToDisk(String filePath, byte[] btyes) {
		try {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		bos.write(btyes);
		bos.flush();
		bos.close();
		return true;
		} catch(Exception e) {}
		return false;
	}
	public static void writeToFile(String filePath, String content) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
			out.write(content);
			out.close();
		} catch (IOException e) {
		}
	}
	public boolean WriteSecureToSd() {
		if(EnsurePathAndFile()) {
			boolean completed=false;
			
			Writer writer=null;
			OutputStream out=null;
			try {
                //BLog.e("E1",HomeFarm.getPemKey()+"------~"+path+File.separator+filename);
				Encrypt crypt = new Encrypt(useKey!=null?useKey:HomeFarm.getPemKey());
                //BLog.e("E1","twwwooo");
				out = crypt.encryptStream(new FileOutputStream(path+ File.separator+filename));
                //BLog.e("E1","three");
				writer = new BufferedWriter(new OutputStreamWriter(out));
                //BLog.e("E1",writeContent);
				writer.write(Encrypt.encodeForEncription(writeContent));

                writer.flush();
                //BLog.e("E1","five");
                out.flush();
				out.close();

				completed=true;
			}
			catch(Exception e) {
                //BLog.e("ERRRRR","FWRITE: "+path+File.separator+filename+" -- "+e.getMessage());
	        	if(writer!=null) {
	        		try {
	        			writer.close();
	        		} catch(Exception ex) {}
	        	}
	        	if(out!=null) {
	        		try {
	        			out.close();
	        		} catch(Exception ex) {}
	        	}
			}
			this.status=STATUS_WRITE_OK;
	        return completed;
		} else {
			//BLog.e("E1","Ensure file path failed");
			return false;
		}
	}

	public boolean WriteToSd() {
		if(EnsurePathAndFile()) {
			boolean completed=false;
			BufferedWriter out =null;
			FileWriter writer=null;
	        try {
	        	writer=new FileWriter(path+ File.separator+filename);
	            out = new BufferedWriter(new FileWriter(path+ File.separator+filename));
	            out.write(Encrypt.encodeForEncription(writeContent));
	            out.close();
	            writer.close();
	            completed=true;
	        } catch (IOException e) {
	        	if(writer!=null) {
	        		try {
	        			writer.close();
	        		} catch(Exception ex) {}
	        	}
	        	if(out!=null) {
	        		try {
	        			out.close();
	        		} catch(Exception ex) {}
	        	}
	        }
	        this.status=STATUS_WRITE_OK;
	        return completed;
		} else {
			return false;
		}
	}
	private boolean EnsurePathAndFile() {
		boolean ok=true;
		File filedir = new File(path);
		if(!filedir.exists())
			ok=filedir.mkdirs();
		if(ok) {
			File file = new File(path+ File.separator+filename);
			try {
			if(!file.exists())
				file.createNewFile();
			} catch(IOException e) {
				BLog.add("EnsurePathAndFile", e);
			}
			
			ok=(file.exists() && file.canWrite());
			if(!ok) {
				this.status=STATUS_WRITE_FAILED;
				this.statusMessage="Write disabled, ensure the device is not mounted";
			}
		} else {
			this.status=STATUS_WRITE_FAILED;
			this.statusMessage="No storage card found, unable to access data";
		}
		return ok;
	}
}

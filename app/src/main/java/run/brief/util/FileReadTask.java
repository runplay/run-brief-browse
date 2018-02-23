package run.brief.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import run.brief.secure.Encrypt;
import run.brief.secure.HomeFarm;
import run.brief.util.log.BLog;

public class FileReadTask {
	
	public static final int STATUS_NOT_STARTED=0;
	public static final int STATUS_READ_OK=1;
	public static final int STATUS_READ_FAILED=2;
	

	private String path;
	private String filename;
	private long fileSize=-1;
	private int status;
	private String statusMessage="Nothing done";
	private String fileContent;
    private String useKey;
	
	public FileReadTask() throws NoSuchMethodException {
		throw new NoSuchMethodException();
	}
	public FileReadTask(String filepath, String filename) {
		this.path=filepath;
		this.filename=filename;

	}
    public FileReadTask(String useKey, String filepath, String filename) {
        this.path=filepath;
        this.filename=filename;
        this.useKey=useKey;
    }
	public long getFilesize() {
		if(fileSize==-1) {
			File f = new File(path+ File.separator+filename);
			if(f.exists())
				fileSize=f.length();
			else
				fileSize=0;
		}
		return fileSize;
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
	public String getFileContent() {
		return fileContent;
	}
	public static byte[] readBytesFromDisk(String filePath) {
		File file = new File(filePath);
	    int size = (int) file.length();
	    byte[] bytes = new byte[size];
	    try {
	        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
	        buf.read(bytes, 0, bytes.length);
	        buf.close();
	        return bytes;
	    } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    return null;
	}
	public synchronized boolean ReadSecureFromSd() {
		if(EnsurePathAndFile()) {
			StringBuffer contents = new StringBuffer();
			
			BufferedReader input = null;
			InputStream in=null;
			//String read=null;
            String readStr=null;
            //BLog.e("Er1",path+File.separator+filename);
			try {
				Encrypt enc = new Encrypt(useKey!=null?useKey:HomeFarm.getPemKey());
				in = enc.decryptStream(new FileInputStream(path+ File.separator+filename));

                byte[] bytes = new byte[1000];

                StringBuilder x = new StringBuilder();

                int numRead = 0;
                while ((numRead = in.read(bytes)) >= 0) {
                    x.append(new String(bytes, 0, numRead));
                }

                readStr=Encrypt.decodeFromEncription(x.toString());
                in.close();
                in=null;

			} catch(Exception e) {
				//BLog.e("FILES5",e.getClass()+" - "+e.getMessage()+" - "+path+File.separator+filename);
			} finally {
				try {
					if (in!= null) {
				      //flush and close both "input" and its underlying FileReader
						in.close();
				    }
				}
				catch (IOException ex) {
					//ex.printStackTrace();
				}
			}

			this.fileContent= readStr!=null?readStr:"";
			this.status=STATUS_READ_OK;
			return true;
		} else {
			return false;
		}
	}
	static public String getFileContent(String file) {

		StringBuffer contents = new StringBuffer();

		//declared here only to make visible to finally clause
		BufferedReader input = null;
		try {
			input = new BufferedReader( new FileReader(file) );
			String line = null;
			while (( line = input.readLine()) != null){
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (IOException ex){
			ex.printStackTrace();
		}
		finally {
			try {
				if (input!= null) {
					//flush and close both "input" and its underlying FileReader
					input.close();
				}
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return contents.toString();
	}
	public synchronized boolean ReadFromSd() {
		if(EnsurePathAndFile()) {
			StringBuilder contents = new StringBuilder();
			BufferedReader input = null;
			FileInputStream in=null;
			try {
				File file = new File(path+ File.separator+filename);
				in= new FileInputStream(file);
				input = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while (( line = input.readLine()) != null){
                    if(contents.length()>0)
                        contents.append(System.getProperty("line.separator"));
					contents.append(line);

				}
                in.close();
				input.close();

			}
			catch (FileNotFoundException e) {
				BLog.e("FILES6", e.getMessage());
			}
			catch (IOException e){
				BLog.e("FILES7", e.getMessage());
			}
			this.fileContent= Encrypt.decodeFromEncription(contents.toString());
			this.status=STATUS_READ_OK;
			return true;
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
			
			ok=(file.exists() && file.canRead());
			if(!ok) {
				this.status=STATUS_READ_FAILED;
				this.statusMessage="Read disabled, ensure the device is not mounted";
			}
		} else {
			this.status=STATUS_READ_FAILED;
			this.statusMessage="No storage card found, unable to access data";
		}
		return ok;
	}
}

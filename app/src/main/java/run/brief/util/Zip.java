package run.brief.util;

import android.app.Activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import run.brief.util.explore.FileItem;

public class Zip {

    public static FileItem getSuggestedZipFile(List<FileItem> files) {

        String sn="untitled";
        String smallestfolder=null;
        for(FileItem f:files) {
            if(smallestfolder==null || f.getParentFile().getAbsolutePath().length()<smallestfolder.length()) {
                smallestfolder=f.getParentFile().getAbsolutePath();
            }
        }
        if(smallestfolder!=null) {
            File f = new File(smallestfolder);
            sn=f.getName();
        } else {
            smallestfolder=Files.HOME_PATH_ZIP_FILES;
            Files.ensurePath(smallestfolder);
        }
        // ensurefile not exists
        for(int i=0; i<100; i++) {
            String testname=sn+(i==0?"":i+"");
            File f = new File(smallestfolder+File.separator+testname+".zip");
            if(!f.exists()) {
                sn=testname;
                break;
            }
        }
        return new FileItem(smallestfolder+File.separator+sn+".zip");
    }

    public static List<FileItem> getStopZipFiles(List<FileItem> files) {
        List<FileItem> stopfiles=new ArrayList<FileItem>();

        HashMap<String,Boolean> uniqueFilenames=new HashMap<String,Boolean>();
        for(FileItem f: files) {
            if(uniqueFilenames.get(f.getName())==null) {
                uniqueFilenames.put(f.getName(),Boolean.TRUE);
            } else {
                stopfiles.add(f);
            }
        }
        // check ok folders

        return stopfiles;

    }

    public static void compress(String zipfolder, String zipFilename, List<FileItem> files) {


        final int BUFFER = 1024;
        //final String ps=pipe;
        BufferedInputStream origin = null;

        File zipFolder = new File(zipfolder);

        File zipFile = new File(zipfolder+"/"+zipFilename);
        try {

                if(!zipFile.exists())  {
                        zipFile.createNewFile();
                }
                //BLog.e("ZIPDIR",zipFile.getAbsolutePath());

                FileOutputStream dest = new FileOutputStream(zipFile.getAbsolutePath());
                ZipOutputStream zout = new ZipOutputStream(new BufferedOutputStream(dest));
                //out.setMethod(ZipOutputStream.DEFLATED);

                //byte data[] = new byte[BUFFER];
                for(FileItem item: files) {
                    addFileItem(zout,item);
                }


                zout.close();

                //FileManager.refresh(activity);
        } catch(Exception e) {
                //BLog.add("ZIPDIR:E:"+zipFile.getAbsolutePath(),e);
        }
    }

    private static void addFileItem(ZipOutputStream zout, FileItem file) {
        try
        {
            //System.out.println("Adding file " + files[i].getName());

            //create byte buffer
            byte[] buffer = new byte[1024];

            //create object of FileInputStream
            File usefile=file.getAbsoluteFile();
            FileInputStream fin = new FileInputStream(usefile.getAbsoluteFile());

            zout.putNextEntry(new ZipEntry(usefile.getName()));

                        /*
                         * After creating entry in the zip file, actually
                         * write the file.
                         */
            int length;

            while((length = fin.read(buffer)) > 0)
            {
                zout.write(buffer, 0, length);
            }

                        /*
                         * After writing the file to ZipOutputStream, use
                         *
                         * void closeEntry() method of ZipOutputStream class to
                         * close the current entry and position the stream to
                         * write the next entry.
                         */

            zout.closeEntry();

            //close the InputStream
            fin.close();

        }
        catch(IOException ioe)
        {
            //BLog.add("ZIP IOException :" + ioe);
        }
    }


	public static void compress(Activity activity,String folderPath) {
		
		
		   //final int BUFFER = 1024;
		   //final String ps=pipe;
		         BufferedInputStream origin = null;
		         
		         File zipFolder = new File(folderPath);
		         
		         File zipFile = new File(zipFolder.getAbsolutePath()+"/../"+zipFolder.getName()+".zip");
		      try {

		         if(!zipFile.exists())  {
		             zipFile.createNewFile();
		         }
		         //BLog.e("ZIPDIR",zipFile.getAbsolutePath());
		         
		         FileOutputStream dest = new FileOutputStream(zipFile.getAbsolutePath());
		         ZipOutputStream zout = new ZipOutputStream(new BufferedOutputStream(dest));
		         //out.setMethod(ZipOutputStream.DEFLATED);
		         
		         //byte data[] = new byte[BUFFER];

		         addDirectory(zout,zipFolder);
		         
		         zout.close();
		         
		         //FileManager.refresh(activity);
		      } catch(Exception e) {
		    	  //BLog.add("ZIPDIR:E:"+zipFile.getAbsolutePath(),e);
		      }
	}
	
	private static void addDirectory(ZipOutputStream zout, File fileSource) {
	       
        //get sub-folder/files list
        File[] files = fileSource.listFiles();
       
        //System.out.println("Adding directory " + fileSource.getName());
       
        for(int i=0; i < files.length; i++)
        {
                //if the file is directory, call the function recursively
                if(files[i].isDirectory())
                {
                        addDirectory(zout, files[i]);
                        continue;
                }
               
                /*
                 * we are here means, its file and not directory, so
                 * add it to the zip file
                 */
               
                try
                {
                        //System.out.println("Adding file " + files[i].getName());
                       
                        //create byte buffer
                        byte[] buffer = new byte[1024];
                       
                        //create object of FileInputStream
                        FileInputStream fin = new FileInputStream(files[i]);
                       
                        zout.putNextEntry(new ZipEntry(files[i].getName()));
                 
                        /*
                         * After creating entry in the zip file, actually
                         * write the file.
                         */
                        int length;
                 
                        while((length = fin.read(buffer)) > 0)
                        {
                           zout.write(buffer, 0, length);
                        }
                 
                        /*
                         * After writing the file to ZipOutputStream, use
                         *
                         * void closeEntry() method of ZipOutputStream class to
                         * close the current entry and position the stream to
                         * write the next entry.
                         */
                 
                         zout.closeEntry();
                 
                         //close the InputStream
                         fin.close();
               
                }
                catch(IOException ioe)
                {
                        //BLog.add("ZIP IOException :" + ioe);
                }
        }
       
	}
}

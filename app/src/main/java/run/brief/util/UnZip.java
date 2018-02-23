package run.brief.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import run.brief.util.log.BLog;

public class UnZip
{
    //List<String> fileList;
    //private static final String INPUT_ZIP_FILE = "C:\\MyFile.zip";
    //private static final String OUTPUT_FOLDER = "C:\\outputzip";

    public static void extract(String zipFile, String outputFolder, HashMap<String,Boolean> unzippedfiles){
        try {
            extract(new FileInputStream(zipFile), outputFolder, unzippedfiles);
        } catch(Exception e) {
            BLog.e(e.getMessage());
        }
    }

    public static void extract(InputStream zipInput, String outputFolder, HashMap<String,Boolean> unzippedfiles){

        byte[] buffer = new byte[1024];

        try{

            //create output directory is not exists
            File folder = new File(outputFolder);
            if(!folder.exists()){
                folder.mkdir();
            }

            //get the zip file content
            ZipInputStream zis =
                    new ZipInputStream(zipInput);
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();


            while(ze!=null){

                String fileName = ze.getName();




                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                if(ze.isDirectory()) {
                    //File newFile = new File(outputFolder + File.separator + fileName);
                    //newFile.mkdir();
                    //appendFolder=newFile.getName();
                } else {

                    File newFile =new File(outputFolder + File.separator + fileName);

                    new File(newFile.getParent()).mkdirs();

                    FileOutputStream fos = new FileOutputStream(newFile);

                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                    fos.close();

                    if (unzippedfiles != null)
                        unzippedfiles.put(fileName, Boolean.TRUE);

                    //BLog.e("file unzip : " + newFile.getAbsoluteFile());
                }
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            //System.out.println("Done");

        }catch(IOException ex){
            BLog.e(ex.getMessage());
        }
    }
}
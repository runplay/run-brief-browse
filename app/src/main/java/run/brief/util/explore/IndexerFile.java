package run.brief.util.explore;

import java.io.File;

import run.brief.beans.BJSONBean;
import run.brief.util.json.JSONObject;

/**
 * Created by coops on 27/07/15.
 */
public class IndexerFile extends BJSONBean {

    public static final String LONG_ID="id";
    public static final String STRING_FILENAME ="fn";
    public static final String STRING_FILEPATH ="fp";
    public static final String LONG_FILESIZE="size";
    public static final String INT_ICONTYPE="type";
    public static final String INT_CATEGORY="cat";
    public static final String INT_BOOL_ISFOLDER ="isf";

    public static final String LONG_MODIFIED="modified";


    public int rating=1;



    public IndexerFile(String filename,String filePath) {
        bean = new JSONObject();
        bean.put(STRING_FILENAME,filename);
        bean.put(STRING_FILEPATH,filePath);
    }

    public IndexerFile(long id,String filename, String filePath, int category, long size, int icontype, long modified) {
        bean = new JSONObject();
        bean.put(LONG_ID,id);
        bean.put(STRING_FILENAME,filename);
        bean.put(STRING_FILEPATH,filePath);
        bean.put(INT_CATEGORY,category);
        bean.put(LONG_FILESIZE,size);
        bean.put(INT_ICONTYPE,icontype);
        bean.put(LONG_MODIFIED,modified);

    }
    public IndexerFile(long id,String filename, String filePath, int category, long size, int icontype, long modified, int isFolder) {
        bean = new JSONObject();
        bean.put(LONG_ID,id);
        bean.put(STRING_FILENAME,filename);
        bean.put(STRING_FILEPATH,filePath);
        bean.put(INT_CATEGORY,category);
        bean.put(LONG_FILESIZE,size);
        bean.put(INT_ICONTYPE,icontype);
        bean.put(LONG_MODIFIED,modified);
        bean.put(INT_BOOL_ISFOLDER,isFolder);

    }
    public String absoluteFile() {
        return getString(STRING_FILEPATH)+File.separator+getString(STRING_FILENAME);
    }
    public FileItem getAsFileItem() {
        //if(getInt(INT_ICONTYPE)==0)
            return new FileItem(getString(STRING_FILEPATH)+File.separator+getString(STRING_FILENAME));
        //else
            //return new FileItem(getString(STRING_FILEPATH)+File.separator+getString(STRING_FILENAME),getInt(INT_ICONTYPE));
    }

}

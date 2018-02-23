package run.brief.util.explore;

/**
 * Created by coops on 05/08/15.
 */
public class FileItemZip extends FileItem {

    public String subpath="/";


    public FileItemZip(String zipfilename, long zipFileSize) {
        super("/"+zipfilename);
        this.PARENT_TYPE_=PARENT_TYPE_ZIP;
        this.zipFileSize=zipFileSize;
    }

}

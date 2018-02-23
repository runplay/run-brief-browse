package run.brief.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by coops on 25/08/15.
 */
public class BitmapFunctions {
    private static BitmapFunctions bmf=new BitmapFunctions();
    public static Bitmap getBitmap(Context context, File image) {

        Bitmap bm = getThumbnail(context, image.getPath());
        if(bm==null)
            bm = BitmapFactory.decodeFile(image.getPath(), null);


        return bm;

    }
    public static Bitmap getPreview(File image, int toSize) {
        //File image = new File(uri);

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getPath(), bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
            return null;

        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
                : bounds.outWidth;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        if(originalSize>300) {
            opts.inSampleSize = originalSize / toSize;
        }
        Bitmap bm = BitmapFactory.decodeFile(image.getPath(), opts);


        return bm;

    }
    public static Bitmap getPreview(File image) {
        //File image = new File(uri);
        return getPreview(image,300);

    }
    public static Drawable getApkIcon(Context context, String APKFilePath) {
        //String APKFilePath = "mnt/sdcard/myapkfile.apk"; //For example...
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo(APKFilePath, 0);

        // the secret are these two lines....
        pi.applicationInfo.sourceDir       = APKFilePath;
        pi.applicationInfo.publicSourceDir = APKFilePath;
        //

        Drawable APKicon = pi.applicationInfo.loadIcon(pm);
        //String   AppName = (String)pi.applicationInfo.loadLabel(pm);
        return APKicon;
    }
    public static Bitmap getThumbnail(Context context, String path) {
        //ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), MediaStore.Images.Thumbnails.MINI_KIND);
        synchronized (bmf) {
            ContentResolver cr = context.getContentResolver();
            Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.MediaColumns._ID}, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
            if (ca != null && ca.moveToFirst()) {
                int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
                ca.close();
                return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
            }
            if (ca != null)
                ca.close();
        }
        return null;

    }

}

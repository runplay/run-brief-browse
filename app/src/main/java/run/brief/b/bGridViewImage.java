package run.brief.b;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import run.brief.util.BitmapFunctions;
import run.brief.util.ImageCache;
import run.brief.util.explore.FileImagesAdapter;

//import sherif.caching.R;

/**
 *
 * @author Sherif
 * thanks Blundell
 *
 */
public class bGridViewImage extends LinearLayout {

    private static final int COMPLETE = 0;
    private static final int FAILED = 1;

    //private bGridViewImage thisFragment;
    private Context mContext;
    private Drawable mDrawable;
    private Bitmap mBitmap;
    private ProgressBar mSpinner;
    private ImageView mImage;
    private File usefile;
    private int usewidth;
    private int useheight;
    private Map<String,Bitmap> cachedBitmaps= new HashMap<String,Bitmap>();
    private FileImagesAdapter adapter;

    public void setAdapter(FileImagesAdapter ad) {
        this.adapter=ad;
    }
    /**
     * This is used when creating the view in XML
     * To have an image load in XML use the tag 'image="http://developer.android.com/images/dialog_buttons.png"'
     * Replacing the url with your desired image
     * Once you have instantiated the XML view you can call
     * setImageDrawable(url) to change the image
     * @param context
     * @param attrSet
     */
    public bGridViewImage(final Context context, final AttributeSet attrSet) {
            super(context, attrSet);
            //final String url = attrSet.getAttributeValue(null, "image");

            instantiate(context);
    }

    /**
     * This is used when creating the view programatically
     * Once you have instantiated the view you can call
     * setImageDrawable(url) to change the image
     * @param context the Activity context

     */
    //USE THIS TO ADD IMAGEVIEWS
    public bGridViewImage(final Context context) {
            super(context);
            instantiate(context);         
    }

    /**
     *  First time loading of the LoaderImageView
     *  Sets up the LayoutParams of the view, you can change these to
     *  get the required effects you want
     */
    //public void instantiate(final Context context, final String imageUrl) {
    public void instantiate(final Context context) {
    	//thisFragment=this;
        //this.setClickable(true);
            mContext = context;

            mImage = new ImageView(mContext);
            mImage.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            mSpinner = new ProgressBar(mContext);
            LayoutParams lp =new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.gravity= Gravity.CENTER;
            mSpinner.setLayoutParams(lp);


            mSpinner.setIndeterminate(true);
            mSpinner.setAlpha(0.2F);

            addView(mSpinner);
            addView(mImage);


            /*
            if(imageUrl != null){
                    setImageDrawable(imageUrl);
            }
            */
    }


    public void setImageDrawable(final Drawable image) {
        mDrawable = image;
        mBitmap=null;
        mSpinner.setVisibility(View.GONE);
        mImage.setVisibility(View.VISIBLE);
        mImage.setImageDrawable(mDrawable);

    }
    private LoadImageTask loadImage;
    public void setImageDrawable(final File f, final int width, int height) {
    	//final int THUMBSIZE = 200;
    	usefile=f;
        usewidth=width;
        useheight=height;
    	mDrawable=null;
        ImageCache.CacheBitmap cb=ImageCache.get(f.getPath());
        if(cb==null) {

            cb = ImageCache.getNewCacheBitmap();
            cb.status = ImageCache.CACHE_B_LOADING;
            ImageCache.put(f.getPath(), cb);
            loadImage = new LoadImageTask();
            loadImage.setData(f, width, height);
            loadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);
        } else if(cb.status==ImageCache.CACHE_B_LOADING) {
            //mImage.setImageBitmap(cb.bitmap);
            mImage.setVisibility(View.VISIBLE);
            //mImage.setCa(null);
            //mImage.setAnimation(alphaAnim);
            mSpinner.setVisibility(View.GONE);
        } else if(cb.status==ImageCache.CACHE_B_LOADED) {
            mBitmap=cb.bitmap;
            mImage.setImageBitmap(cb.bitmap);
            mImage.setVisibility(View.VISIBLE);
            //mImage.setCa(null);
            //mImage.setAnimation(alphaAnim);
            mSpinner.setVisibility(View.GONE);
//this.invalidate();
        }

    }
    /*
    public void setImageLayoutParams(int width, int height) {
        mImage.setLayoutParams(new GridView.LayoutParams(width,height));
    }
    */
    public void setImageBitmap(final Bitmap b) {
        //final int THUMBSIZE = 200;

        mDrawable=null;
        mBitmap = b;
        //mDrawable = image;
        mSpinner.setVisibility(View.GONE);
        mImage.setVisibility(View.VISIBLE);
        mImage.setImageBitmap(mBitmap);
        //imageDrawableLoadedHandler.sendEmptyMessage(COMPLETE);

    }
    public void setScaleType(ImageView.ScaleType type) {
        mImage.setScaleType(type);
    }
    //private load
    /**
     * Callback that is received once the image has been downloaded
     */
    private static AlphaAnimation alphaAnim = new AlphaAnimation(0.0f,1.0f);
    static {
        alphaAnim.setDuration(300);
    }
    private int counttry=0;

    Runnable callme = new Runnable() {
        @Override
        public void run() {


                mImage.setImageBitmap(null);
                mImage.setImageBitmap(mBitmap);
                mImage.setVisibility(View.VISIBLE);

                mImage.setAnimation(alphaAnim);
                mSpinner.setVisibility(View.GONE);
            if(adapter!=null) {
                adapter.notifyDataSetChanged();
            }
                //this.notify();
                //mImage.refreshDrawableState();

        }
    };

    Handler.Callback callback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            if(mBitmap!=null) {
                mImage.setImageBitmap(mBitmap);
                mImage.setVisibility(View.VISIBLE);
                mImage.setAnimation(alphaAnim);
                mSpinner.setVisibility(View.GONE);

            } else if(mDrawable!=null) {

                mImage.setImageDrawable(mDrawable);
                mImage.setVisibility(View.VISIBLE);

                mImage.setAnimation(alphaAnim);
                mSpinner.setVisibility(View.GONE);
            } else {

                mImage.setVisibility(View.VISIBLE);
                mSpinner.setVisibility(View.VISIBLE);

            }


            return true;
        }
    };


    private final Handler imageLoadedHandler = new Handler();
    private final Handler imageDrawableLoadedHandler = new Handler(callback);
    //private boolean stopAction=false;
    private class LoadImageTask extends AsyncTask<Boolean, Void, Boolean> {
        private File image;
        private int width;
        private int height;
        private ImageCache.CacheBitmap cb;

        public void setData(File image, int width, int height) {
            this.image=image;
            this.width=width;
            this.height=height;
        }
        @Override
        protected Boolean doInBackground(Boolean... params) {
            //boolean stopAction=false;
//synchronized ("img.sync."+image.getPath()) {
            cb = ImageCache.get(image.getPath());
            //if (cb == null) {


            cb.bitmap = ThumbnailUtils.extractThumbnail(BitmapFunctions.getBitmap(mContext,image), width, height);

            cb.status = ImageCache.CACHE_B_LOADED;
            ImageCache.putFinal(image.getPath(), cb);

            //} else {
            //    stopAction=true;

            //}

            return true;

        }
        @Override
        protected void onPostExecute(Boolean result) {
            //list
            if(result && cb.bitmap!=null) {
                mBitmap = cb.bitmap;

                mImage.setPadding(0, 0, 0, 0);
                //mImage.setLayoutParams(new GridView.LayoutParams(width, height));
                mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageLoadedHandler.postDelayed(callme,10);
            }

        }

    }
    private static bGridViewImage staticimage;
    public static void preLoadImage(Activity activity, File f, int width, int height) {
        if(staticimage==null)
            staticimage=new bGridViewImage(activity);
        PreloadImageTask pmt = staticimage.new PreloadImageTask();
        pmt.setData(f,width ,height);
        pmt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);

    }
    public void setImageAlpha(int alpha255) {
        mImage.setImageAlpha(alpha255);
    }
    private class PreloadImageTask extends AsyncTask<Boolean, Void, Boolean> {
        private File image;
        private int width;
        private int height;
        private ImageCache.CacheBitmap cb;
        public void setData(File image, int width, int height) {
            this.image=image;
            this.width=width;
            this.height=height;
        }
        @Override
        protected Boolean doInBackground(Boolean... params) {
            //BLog.e("POINT", "1");

            cb = ImageCache.get(image.getPath());
            if(cb==null) {
                cb=ImageCache.getNewCacheBitmap();
                cb.status=ImageCache.CACHE_B_LOADING;
                ImageCache.put(image.getPath(), cb);

                //cb.bitmap= ThumbnailUtils.extractThumbnail(BitmapFunctions.getPreview(image,width), width, height);
                cb.bitmap = ThumbnailUtils.extractThumbnail(BitmapFunctions.getBitmap(mContext,image), width, height);
                cb.status=ImageCache.CACHE_B_LOADED;
                ImageCache.putFinal(image.getPath(), cb);

            }
            return Boolean.TRUE;

        }
        @Override
        protected void onPostExecute(Boolean result) {

        }

    }


}
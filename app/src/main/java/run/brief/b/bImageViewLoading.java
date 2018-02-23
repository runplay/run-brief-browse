package run.brief.b;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import run.brief.browse.R;

//import sherif.caching.R;

/**
 *
 * @author Sherif
 * thanks Blundell
 *
 */
public class bImageViewLoading extends LinearLayout {

    private static final int COMPLETE = 0;
    private static final int FAILED = 1;

    private bImageViewLoading thisFragment;
    private Context mContext;
    private Drawable mDrawable;
    private Bitmap mBitmap;
    private ProgressBar mSpinner;
    private ImageView mImage;
    private Map<String,Bitmap> cachedBitmaps= new HashMap<String,Bitmap>();

    /**
     * This is used when creating the view in XML
     * To have an image load in XML use the tag 'image="http://developer.android.com/images/dialog_buttons.png"'
     * Replacing the url with your desired image
     * Once you have instantiated the XML view you can call
     * setImageDrawable(url) to change the image
     * @param context
     * @param attrSet
     */
    public bImageViewLoading(final Context context, final AttributeSet attrSet) {
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
    public bImageViewLoading(final Context context) {
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
    	thisFragment=this;
            mContext = context;

            mImage = new ImageView(mContext);
            mImage.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            mSpinner = new ProgressBar(mContext);
            mSpinner.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            mSpinner.setIndeterminate(true);

            addView(mSpinner);
            addView(mImage);
            /*
            if(imageUrl != null){
                    setImageDrawable(imageUrl);
            }
            */
    }

    /**
     * Set's the view's drawable, this uses the internet to retrieve the image
     * don't forget to add the correct permissions to your manifest
     * @param imageUrl the url of the image you wish to load
     */
    public void setImageDrawable(final String imageUrl) {
            mDrawable = null;
            mSpinner.setVisibility(View.VISIBLE);
            mImage.setVisibility(View.GONE);
            new Thread(){
                    public void run() {
                            try {
                                    mDrawable = getDrawableFromUrl(imageUrl);
                                    imageLoadedHandler.sendEmptyMessage(COMPLETE);
                            } catch (MalformedURLException e) {
                                    imageLoadedHandler.sendEmptyMessage(FAILED);
                            } catch (IOException e) {
                                    imageLoadedHandler.sendEmptyMessage(FAILED);
                            }
                    };
            }.start();
    }
    public void setImageDrawable(final Drawable image) {
        mDrawable = image;
        mBitmap=null;
        mSpinner.setVisibility(View.GONE);
        mImage.setVisibility(View.VISIBLE);
        imageLoadedHandler.sendEmptyMessage(COMPLETE);

    }
    public void setImageDrawable(final File f) {
    	final int THUMBSIZE = 200;
    	
    	mDrawable=null;
    	mBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(f.getAbsolutePath()),
                THUMBSIZE, THUMBSIZE);
        //mDrawable = image;
        mSpinner.setVisibility(View.GONE);
        mImage.setVisibility(View.VISIBLE);
        imageLoadedHandler.sendEmptyMessage(COMPLETE);

    }
    public void setImageBitmap(final Bitmap b) {
        final int THUMBSIZE = 200;

        mDrawable=null;
        mBitmap = b;
        //mDrawable = image;
        mSpinner.setVisibility(View.GONE);
        mImage.setVisibility(View.VISIBLE);
        imageLoadedHandler.sendEmptyMessage(COMPLETE);

    }
    public void setScaleType(ImageView.ScaleType type) {
        mImage.setScaleType(type);
    }
    //private load
    /**
     * Callback that is received once the image has been downloaded
     */
    private final Handler imageLoadedHandler = new Handler(new Callback() {
            public boolean handleMessage(Message msg) {
                    switch (msg.what) {
                    case COMPLETE:
                    		if(mBitmap!=null) {
                    			mImage.setImageBitmap(mBitmap);
                    		} else {
                    			mImage.setImageDrawable(mDrawable);
                    		}
                            mImage.setVisibility(View.VISIBLE);
                            mSpinner.setVisibility(View.GONE);
                            break;
                    case FAILED:
                    default:
                            mImage.setImageResource(R.drawable.action_help);
                            mImage.setVisibility(View.VISIBLE);
                            mSpinner.setVisibility(View.GONE);
                            // Could change image here to a 'failed' image
                            // otherwise will just keep on spinning
                            break;
                    }
                    return true;
            }               
    });

    /**
     * Pass in an image url to get a drawable object
     * @return a drawable object
     * @throws IOException
     * @throws MalformedURLException
     */
    private static Drawable getDrawableFromUrl(final String url) throws IOException, MalformedURLException {
            return Drawable.createFromStream(((java.io.InputStream) new java.net.URL(url).getContent()), "name");

        /*
        if (profile != null) {
    Bitmap mIcon1 =
        BitmapFactory.decodeStream(url_value.openConnection().getInputStream());
    profile.setImageBitmap(mIcon1);
}

         */
    }

}
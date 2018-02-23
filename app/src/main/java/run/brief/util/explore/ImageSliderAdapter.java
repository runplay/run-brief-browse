package run.brief.util.explore;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import run.brief.browse.R;

public class ImageSliderAdapter extends PagerAdapter {

    private Activity _activity;
    private List<FileItem> _imagePaths;
    private LayoutInflater inflater;

    private CustomZoomableImageView imgDisplay;

    private Matrix matrix;

    private Handler loadFullSize;
    private int currentPosition;
    Bitmap bitmap;
    //private boolean firstloaded=false;



    // constructor
    public ImageSliderAdapter(Activity activity, List<FileItem> imagePaths) {
        this._activity = activity;
        this._imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return this._imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        //Button btnClose;
        //if(loadFullSize!=null)
        //    loadFullSize.removeCallbacks(lfs);

        currentPosition=position;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.images_slider_item, container,false);



        //btnClose = (Button) viewLayout.findViewById(R.id.btnClose);


        //options.inSampleSize=6;
        //if(!firstloaded) {
        imgDisplay = (CustomZoomableImageView) viewLayout.findViewById(R.id.imgDisplay);
//BLog.e("Image file size: "+_imagePaths.get(position).length());
        if(_imagePaths.get(position).length()>1000000) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize=4;
            bitmap = BitmapFactory.decodeFile(_imagePaths.get(position).getAbsolutePath(), options);
        } else if(_imagePaths.get(position).length()>500000) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize=2;
            bitmap = BitmapFactory.decodeFile(_imagePaths.get(position).getAbsolutePath(), options);
        } else  {
            bitmap = BitmapFactory.decodeFile(_imagePaths.get(position).getAbsolutePath(), null);
        }

        imgDisplay.setImageBitmap(bitmap);
            //firstloaded=true;
        //}

        viewLayout.setTag(_imagePaths.get(position).getAbsolutePath());

        ((ViewPager) container).addView(viewLayout);
        //Log.e("LOAD", "SHOW IMAGE w: " + bitmap.getWidth() + " - h: " + bitmap.getHeight());
        return viewLayout;
    }
    /*
    private Runnable lfs = new Runnable() {
        @Override
        public void run() {

            //BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            //options.inSampleSize=1;

            bitmap = BitmapFactory.decodeFile(_imagePaths.get(currentPosition).getAbsolutePath(), null);
            imgDisplay.setImageBitmap(bitmap);
            imgDisplay.refreshDrawableState();
            imgDisplay.requestFocus();


            Log.e("RELOAD", "UPGRADING TO FULL SIZE IMAGE w: "+bitmap.getWidth()+" - h: "+bitmap.getHeight());
            System.gc();

        }
    };
*/
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
        System.gc();
    }

}
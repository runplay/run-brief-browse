package run.brief.util.explore;

import android.app.Activity;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import run.brief.b.bGridViewImage;
import run.brief.browse.R;
import run.brief.util.Files;
import run.brief.util.Sf;
import run.brief.util.explore.fm.FileManager;


public class FileImagesAdapter extends BaseAdapter {
    private Activity activity;
    //private SQLiteDatabase db;

    public boolean isFastScroll=false;



    //private int imageViewId = 13232;
    //private int textRelViewId = 13233;

    private CursorLoader thisCursorLoader;
    private final Uri sourceUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private final Uri thumbUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
    private final String thumb_DATA = MediaStore.Images.Thumbnails.DATA;
    private final String thumb_IMAGE_ID = MediaStore.Images.Thumbnails.IMAGE_ID;
    //private final Uri thumb_IMAGE_URI = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
    //private int lastypos;
    private int lastvis;

    private LayoutParams param;// = new LayoutParams(LayoutParams.MATCH_PARENT,240);
    private LayoutParams txtparams;// = new LayoutParams(LayoutParams.MATCH_PARENT,240);
    private LayoutParams txtppod;
    private RelativeLayout.LayoutParams laycheck;
    //private int lastVisibilePos;
    private GridView parent;

    private Animation animation1;
    private Animation animation2;
    //private int checkedCount = 0;
    //public static ActionMode mMode;
    //public static boolean isActionModeShowing;
    private ImageView ivFlip;

    private FilesActionModeSelecter fileSelectedActionSelecter;
    private FileManager fm;

    public void setFileSelectedActionSelecter(FilesActionModeSelecter adapter) {
        this.fileSelectedActionSelecter=adapter;
    }


    public FileImagesAdapter(Activity c, GridView parent, FileManager fileManager) {
        this.activity = c;
        this.parent=parent;
        fm= fileManager;//fm.init(activity);
        param = new LayoutParams(LayoutParams.MATCH_PARENT,300);
        txtparams = new LayoutParams(LayoutParams.MATCH_PARENT,40);
        txtppod = new LayoutParams(LayoutParams.MATCH_PARENT,300);
        animation1 = AnimationUtils.loadAnimation(activity, R.anim.to_middle);
        animation2 = AnimationUtils.loadAnimation(activity, R.anim.from_middle);

        laycheck = new RelativeLayout.LayoutParams(65,60);
        laycheck.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        laycheck.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        //isActionModeShowing = false;
        //txtparams.
        //cache = new HashMap<String,Bitmap>();
    }

    public int getCount() {
        return fm.getCurrentCount();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
    	bGridViewImage imageView;
        ImageView chkImg;
    	RelativeLayout lay;
        if (convertView == null) {  
        	lay=new RelativeLayout(activity);
        	
            
            lay.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            lay.setLayoutParams(param);


        } else {
        	lay=(RelativeLayout) convertView;
        	lay.removeAllViews();
        }
        FileItem f= fm.getDirectoryItem(position);
        imageView=new bGridViewImage(activity);
        imageView.setAdapter(this);
        /*
        imageView = GridMapImageCache.get(f.getAbsolutePath());
        if(imageView==null) {
            imageView=new bGridViewImage(activity);
            GridMapImageCache.put(f.getAbsolutePath(),imageView);
        }
        */
        imageView.setId(R.id.file_images_image_view_id);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //imageView = new ImageView(activity);

        chkImg = new ImageView(activity);
        chkImg.setId(R.id.file_images_image_check_id);
        chkImg.setLayoutParams(laycheck);
        chkImg.setPadding(10, 10, 15, 10);
        //chkImg.
        


        if(f!=null) {
            chkImg.setVisibility(View.VISIBLE);
            if(f.getAbsoluteFile().isDirectory())
                chkImg.setVisibility(View.GONE);

            chkImg.setTag("" + position);
            lay.setTag("" + position);
            RelativeLayout rel=new RelativeLayout(activity);
            rel.setLayoutParams(txtppod);
            rel.setPadding(0, 260, 0, 0);

            TextView txt = new TextView(activity);
            txt.setLayoutParams(txtparams);
            txt.setText(Sf.shortenText(f.getName(), 26));

            txt.setBackgroundColor(activity.getResources().getColor(R.color.black_alpha));
            txt.setTextColor(activity.getResources().getColor(R.color.grey));
            txt.setPadding(8, 2, 4, 8);
            //txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);



            rel.setId(R.id.file_images_text_view_id);
            rel.addView(txt);

            //imageView.setImageBitmap(null);
            //imageView.setImageDrawable(null);

            if(Files.isImage(Files.removeBriefFileExtension(f.getName()))) {
                //if(!isFastScroll)
                    imageView.setImageDrawable(f,380,300);

            } else if(Files.isVideo(Files.removeBriefFileExtension(f.getName()))) {
                //ImageCache.CacheBitmap bMap = ImageCache.get(f.getPath());

/*
                if(bMap==null && !isFastScroll) {
                    bMap=ImageCache.getNewCacheBitmap();
                    //ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(f.getAbsolutePath()), width, height);
                    Bitmap bitmap= ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                    bMap.bitmap=bitmap;
                    bMap.status=1;
                    ImageCache.putFinal(f.getPath(),bMap);
                    //FileItem fi= fm.getDirectoryItem(position);
                    imageView.setImageBitmap(bMap.bitmap);
                    imageView.setLayoutParams(new RelativeLayout.LayoutParams(380, 300));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setPadding(0,0,0,0);
                } else {
*/
                    //FileItem fi= fm.getDirectoryItem(position);
                    imageView.setImageDrawable(activity.getResources().getDrawable(f.icon));
                    imageView.setLayoutParams(new RelativeLayout.LayoutParams(100, 100));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setPadding(0,0,0,0);
                //}
            }else {


                //FileItem fi= fm.getDirectoryItem(position);
                imageView.setImageDrawable(activity.getResources().getDrawable(f.icon));
                imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(0,0,0,0);
            }

            chkImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FileItem f = fm.getDirectoryItem(Integer.valueOf(v.getTag().toString()).intValue());
                    if (f != null && !f.getAbsoluteFile().isDirectory()) {
                        if (fm.getSelectedFiles().get(f.getAbsolutePath()) != null) {
                            //BLog.e("REM", f.getAbsolutePath());
                            fm.removeSelectedFile(f);
                            setAnimListners(f);
                            notifyDataSetChanged();
                            if (fm.getSelectedFiles().isEmpty()) {
                                if (fileSelectedActionSelecter.mMode != null) {
                                    fileSelectedActionSelecter.mMode.finish();

                                }
                            } else {
                                if (fileSelectedActionSelecter.mMode != null)
                                    fileSelectedActionSelecter.mMode.setTitle(fm.getSelectedFiles().size() + " " + activity.getResources().getString(R.string.label_files));
                            }


                        } else {
                            fm.addSelectedFile(f);
                            if (!fm.getSelectedFiles().isEmpty()) {
                                if (!fileSelectedActionSelecter.isActionModeShowing) {
                                    fileSelectedActionSelecter.mMode = ((AppCompatActivity) activity).startSupportActionMode(fileSelectedActionSelecter);
                                    fileSelectedActionSelecter.isActionModeShowing = true;
                                }
                            } else if (fileSelectedActionSelecter.mMode != null) {
                                fileSelectedActionSelecter.mMode.finish();
                                fileSelectedActionSelecter.isActionModeShowing = false;
                            }

                            // Set action mode title
                            if (fileSelectedActionSelecter.mMode != null)
                                fileSelectedActionSelecter.mMode.setTitle(fm.getSelectedFiles().size() + " " + activity.getResources().getString(R.string.label_files));

                            notifyDataSetChanged();

                        }
                    }

                }

            });



            lay.addView(imageView);
            lay.addView(chkImg);
            lay.addView(rel);
            if (fm.getSelectedFiles().get(f.getAbsolutePath())!=null) {
                //holder.selectBox.setImageResource(R.drawable.cb_checked);
                imageView.setImageAlpha(30);
                chkImg.setImageDrawable(activity.getResources().getDrawable(R.drawable.s_checkon));

            } else {
                imageView.setImageAlpha(255);
                chkImg.setImageDrawable(activity.getResources().getDrawable(R.drawable.s_checkoff));

            }
        }

        if(fm.getSelectedFiles().isEmpty() && fileSelectedActionSelecter.isActionModeShowing==true) {
            fileSelectedActionSelecter.mMode.finish();
            fileSelectedActionSelecter.isActionModeShowing = false;
        }
        return lay;
    }



    private void setAnimListners(final FileItem curMail) {
        Animation.AnimationListener animListner;
        animListner = new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                if (animation == animation1) {
//
                    ivFlip.clearAnimation();
                    ivFlip.setAnimation(animation2);
                    ivFlip.startAnimation(animation2);
                } else {
                    //curMail.setChecked(!curMail.isChecked());
                    //setCount();
                    setActionMode();
                }
            }
            // Show/Hide action mode
            private void setActionMode() {

                if (!fm.getSelectedFiles().isEmpty()) {
                    if (!fileSelectedActionSelecter.isActionModeShowing) {
                        fileSelectedActionSelecter.mMode = ((AppCompatActivity)activity).getSupportActionBar().startActionMode(fileSelectedActionSelecter);
                        fileSelectedActionSelecter.isActionModeShowing = true;
                    }
                } else if (fileSelectedActionSelecter.mMode != null) {
                    fileSelectedActionSelecter.mMode.finish();
                    fileSelectedActionSelecter.isActionModeShowing = false;
                }

                // Set action mode title
                if (fileSelectedActionSelecter.mMode != null)
                    fileSelectedActionSelecter.mMode.setTitle(fm.getSelectedFiles().size()+" "+activity.getResources().getString(R.string.label_files));

                notifyDataSetChanged();

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                // TODO Auto-generated method stub

            }
        };

        animation1.setAnimationListener(animListner);
        animation2.setAnimationListener(animListner);

    }

}
package run.brief.search;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import run.brief.browse.R;
import run.brief.util.BitmapFunctions;
import run.brief.util.Cal;
import run.brief.util.Files;
import run.brief.util.ImageCache;
import run.brief.util.Num;
import run.brief.util.explore.FileItem;
import run.brief.util.explore.IndexerFile;
import run.brief.util.explore.InfoClicker;

public class SearchShortcutAdapter extends BaseAdapter {

    private Activity activity;
    //private JSONArray data;
    private static LayoutInflater inflater=null;
    List<IndexerFile> files;
    private ListView parent;
    public boolean isFastScroll=false;

    public SearchShortcutAdapter(Activity a, ListView parent) {
        this.parent=parent;
        activity = a;
        this.files=files;
        //this.data=data;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return Searcher.size();
    }

    public Object getItem(int position) {
        return Searcher.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        //view = inflater.inflate(android.R.layout.simple_list_item_1, null);
        //LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = inflater.inflate(R.layout.file_explore_list_item, null);
            //convertView = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.explore_item_head);
            holder.selectBox = (ImageView) view.findViewById(R.id.explore_file_type);
            holder.data = (TextView) view.findViewById(R.id.explore_item_size);
            holder.date = (TextView) view.findViewById(R.id.explore_item_date);
            holder.check=(ImageView) view.findViewById(R.id.file_explore_check);
            view.setTag(holder);

        }

        holder = (ViewHolder) view.getTag();
        // put the image on the text view
        IndexerFile iitem= Searcher.get(position);
        FileItem item = iitem.getAsFileItem();

        holder.check.setVisibility(View.VISIBLE);
        if(item.getAbsoluteFile().isDirectory())
            holder.check.setVisibility(View.GONE);

        //holder.title.setCompoundDrawablesWithIntrinsicBounds(item.icon, 0, 0, 0);
        holder.title.setText(item.file);
        //holder.selectBox.setTag(chkhold);
        String size = Num.btyesToFileSizeString(item.length());
        if(item.PARENT_TYPE_==FileItem.PARENT_TYPE_ZIP)
            size=Num.btyesToFileSizeString(item.zipFileSize);
        holder.data.setText(size);
        holder.date.setText(Cal.getCal(item.lastModified()).getDatabaseDate());

        holder.selectBox.setVisibility(View.VISIBLE);


        if(item.exists() && Files.isImage(item.getName())) {
            ImageCache.CacheBitmap cb = ImageCache.get(item.getPath());
            if(cb!=null) {
                holder.selectBox.setImageBitmap(cb.bitmap);
            } else {
                LoadImageTask loadImage = new LoadImageTask();
                loadImage.setData(item,position,holder.selectBox);
                loadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);
                holder.selectBox.setImageDrawable(activity.getResources().getDrawable(item.icon));
            }

        } else {
            holder.selectBox.setImageDrawable(activity.getResources().getDrawable(item.icon));
        }

        ImageView showInfo = (ImageView) view.findViewById(R.id.file_info);
        InfoClicker info = new InfoClicker(activity,item);
        //info.pos=position;
        showInfo.setOnClickListener(info);

        return view;
    }
    private static class ViewHolder {
        TextView title;
        ImageView selectBox;
        TextView data;
        TextView date;
        ImageView check;
    }
    private synchronized void refreshIfVisible(File image, int pos) {

        if(parent!=null) {
            int first=parent.getFirstVisiblePosition();
            int last=parent.getLastVisiblePosition();
            LinearLayout view = (LinearLayout) parent.getChildAt(pos);
            if(view!=null) {
                //BLog.e("REFR", "refresh "+image.getPath());
                view=(LinearLayout)this.getView(pos, view, parent);

                parent.invalidate();

            }
        }

    }
    private int lastvis;
    private boolean activeCachechange=false;
    public void promptCacheChange(int ypos, ListView list) {
        if(!activeCachechange) {
            activeCachechange=true;
            synchronized (this) {
                int firstpos = list.getFirstVisiblePosition();
                int lastpos = list.getLastVisiblePosition();

                if (lastpos > 0 && lastvis != lastpos) {

                    if (lastvis < lastpos) {
                        // going down
                        //Log.e("SCR","GPOING DOWN");
                        for (int i = lastpos; i <= lastpos + 10; i++) {
                            File f = Searcher.get(i).getAsFileItem();
                            if (f != null) {
                                //if(f.getName().endsWith("383.jpg")){BLog.e("PREL1", f.getPath());}
                                ImageCache.CacheBitmap cb = ImageCache.get(f.getPath());

                                if (cb == null) {
                                    if(Files.isImage(Files.removeBriefFileExtension(f.getName()))) {
                                        //BLog.e("PREL", f.getPath());
                                        LoadImageTask lt = new LoadImageTask();
                                        lt.setData(f, i, null);
                                        lt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);

                                    } else if(Files.isVideo(Files.removeBriefFileExtension(f.getName()))) {
                                        ImageCache.CacheBitmap bMap=ImageCache.getNewCacheBitmap();
                                        Bitmap bitmap= ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                                        bMap.bitmap=bitmap;
                                        bMap.status=1;
                                        ImageCache.putFinal(f.getPath(),bMap);
                                    }
                                }

                            } else {
                                //BLog.e("BREAK1: "+ i);
                                break;
                            }
                        }
                    } else {
                        // going up
                        //Log.e("SCR","GPOING up");
                        for (int i = firstpos; i >= 0 && i >= firstpos - 10; i--) {
                            File f = Searcher.get(i).getAsFileItem();
                            if (f != null) {

                                ImageCache.CacheBitmap cb = ImageCache.get(f.getPath());
                                if (cb == null) {
                                    if(Files.isImage(Files.removeBriefFileExtension(f.getName()))) {
                                        //BLog.e("PREL", f.getPath());
                                        LoadImageTask lt = new LoadImageTask();
                                        lt.setData(f, i, null);
                                        lt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);

                                    } else if(Files.isVideo(Files.removeBriefFileExtension(f.getName()))) {
                                        ImageCache.CacheBitmap bMap=ImageCache.getNewCacheBitmap();
                                        Bitmap bitmap= ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                                        bMap.bitmap=bitmap;
                                        bMap.status=1;
                                        ImageCache.putFinal(f.getPath(),bMap);
                                    }
                                }
                            } else {
                                //BLog.e("BREAK2: "+i);
                                break;
                            }
                        }
                    }

                    //lastypos=ypos;
                    lastvis = lastpos;
                }
            }
            activeCachechange=false;
        }
        //list.refreshDrawableState();
    }
    private class LoadImageTask extends AsyncTask<Boolean, Void, Boolean> {
        private File image;
        private int pos;
        private ImageCache.CacheBitmap cb;
        private ImageView forview;

        public void setData(File image, int position, ImageView forview) {
            this.image = image;
            pos = position;
            this.forview=forview;
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            //BLog.e("POINT", "1");
            cb = ImageCache.get(image.getPath());
            if (cb == null) {
                cb = ImageCache.getNewCacheBitmap();
                cb.status = ImageCache.CACHE_B_LOADING;
                ImageCache.put(image.getPath(), cb);
                Bitmap bitmap = BitmapFunctions.getPreview(image,80);



                cb.bitmap = bitmap;
                cb.status = ImageCache.CACHE_B_LOADED;
                ImageCache.putFinal(image.getPath(), cb);

            }
            return Boolean.TRUE;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(forview!=null) {

                if(cb.bitmap==null) {

                    // LoadImageTask loadImage = new LoadImageTask();
                    // loadImage.setData(image,pos,forview);
                    // loadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);
                } else {
                    forview.setId(R.id.file_images_image_view_id);
                    forview.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    forview.setLayoutParams(new RelativeLayout.LayoutParams(380, 300));

                    forview.setPadding(0, 0, 0, 0);
                    forview.setImageBitmap(cb.bitmap);
//parent.getChildAt(pos).invalidate();
                    //notifyDataSetChanged();
//parent.refreshDrawableState();
                    /*
                    forview.setId(R.id.file_images_image_view_id);
                    forview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    forview.setImageBitmap(cb.bitmap);
                    forview.setLayoutParams(new RelativeLayout.LayoutParams(380, 300));

                    forview.setPadding(0, 0, 0, 0);
                    */

                }


            }
            //refreshIfVisible(image, pos);
        }
    }

}

package run.brief.util.explore;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import run.brief.browse.R;
import run.brief.util.Num;
import run.brief.util.explore.fm.FileManagerZip;

public class FileExploreAdapterZip extends BaseAdapter {

	    private Activity activity;



	    //private int checkedCount = 0;

	    private FileManagerZip fm;
		private List<FileItemZip> files=new ArrayList<FileItemZip>();
	    //private JSONArray data;
	    private static LayoutInflater inflater=null;



	 	@Override
		public void notifyDataSetInvalidated() {
			buildList();
			super.notifyDataSetInvalidated();
		}
		private void buildList() {
			files.clear();
			for(int i=0; i<fm.getCurrentCount(); i++) {
				files.add((FileItemZip)fm.getDirectoryItem(i));
			}
		}
	    public FileExploreAdapterZip(Activity a, FileManagerZip filemanager) {
	        activity = a;

			this.fm=filemanager;
			buildList();
	        //this.data=data;
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


	    }


	 
		@Override
		public int getCount() {

			return files.size();
		}
	 
		@Override
	    public Object getItem(int position) {
	        return position;
	    }
	 
		@Override
	    public long getItemId(int position) {
	        return position;
	    }
	    @Override
	    public View getView(final int position, View view, ViewGroup parent) {
	        ViewHolder holder;
	    	//view = inflater.inflate(android.R.layout.simple_list_item_1, null);
	        //LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        if (view == null) {
	        	view = inflater.inflate(R.layout.file_explore_list_item_zip, null);

	            //convertView = inflater.inflate(R.layout.list_item, null);
	            holder = new ViewHolder();
                holder.view=view;
	            holder.title = (TextView) view.findViewById(R.id.explore_item_head);
	            holder.selectBox = (ImageView) view.findViewById(R.id.explore_file_type);
	            holder.data = (TextView) view.findViewById(R.id.explore_item_size);
                //holder.date = (TextView) view.findViewById(R.id.explore_item_date);

                holder.subpath=(TextView) view.findViewById(R.id.zip_subpath);
	            view.setTag(holder);

	        }
	 
	        holder = (ViewHolder) view.getTag();

			FileItemZip item= files.get(position);

            if(item.subpath.length()>1)
                holder.view.setPadding(40,0,0,0);
            else
                holder.view.setPadding(0,0,0,0);
            CheckHolder chkhold= new CheckHolder();
            chkhold.position= Integer.valueOf(position);


            holder.subpath.setText(item.subpath);

	        holder.title.setText(item.file);
	        holder.selectBox.setTag(chkhold);
			//Log.e("SIZE",item.getAbsolutePath()+" = "+item.length());
			String size = Num.btyesToFileSizeString(item.length());
			if(item.PARENT_TYPE_==FileItem.PARENT_TYPE_ZIP)
				size=Num.btyesToFileSizeString(item.zipFileSize);
	        holder.data.setText(size);
            //holder.date.setText(Cal.getCal(item.lastModified()).getDatabaseDate());



	        //} else {
			holder.selectBox.setImageDrawable(activity.getResources().getDrawable(item.icon));
	       // }

			view.setBackgroundColor(activity.getResources().getColor(R.color.transparent));


	        return view;
	 
	    }
	 

	    private static class ViewHolder {
            View view;
	        TextView title;
	        ImageView selectBox;
	        TextView data;
            //TextView date;
            TextView subpath;

	    }
        private static class CheckHolder {
            Integer position;
            ImageView check;
        }

	}

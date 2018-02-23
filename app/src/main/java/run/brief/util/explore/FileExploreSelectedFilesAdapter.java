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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import run.brief.browse.R;
import run.brief.util.Cal;
import run.brief.util.Num;
import run.brief.util.explore.fm.FileManagerDisk;

public class FileExploreSelectedFilesAdapter extends BaseAdapter {

	 
	
	    private Activity activity;

	    //private JSONArray data;
	    private LayoutInflater inflater=null;
	    private ArrayList<FileItem> selectedFiles=new ArrayList<FileItem>();
	 	private FileManagerDisk fm;
		HashMap<String,FileItem> stopfileshash=new HashMap<String,FileItem>();

		public FileExploreSelectedFilesAdapter(Activity a, FileManagerDisk filemanager, List<FileItem> stopfiles) {
			activity = a;
			fm=filemanager;
			for(FileItem fi: stopfiles) {
				stopfileshash.put(fi.getAbsolutePath(),fi);
			}
			//this.data=data;
			inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			Iterator<String> it= fm.getSelectedFiles().keySet().iterator();
			while(it.hasNext()) {
				FileItem fi= fm.getSelectedFiles().get(it.next());
				if(fi!=null)
					selectedFiles.add(fi);
			}
		}

	    public FileExploreSelectedFilesAdapter(Activity a, FileManagerDisk filemanager) {
	        activity = a;
			fm=filemanager;
	        //this.data=data;
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        
	        Iterator<String> it= fm.getSelectedFiles().keySet().iterator();
	        while(it.hasNext()) {
	        	FileItem fi= fm.getSelectedFiles().get(it.next());
	        	if(fi!=null)
	        		selectedFiles.add(fi);
	        }
	    }
	 
	    public ArrayList<FileItem> getSelectedFiles() {
	    	return selectedFiles;
	    }
		@Override
		public int getCount() {
			return selectedFiles.size();

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
	        if (view == null) {
	        	view = inflater.inflate(R.layout.file_explore_list_item, null);

	        }
	        //BLog.e("FESFA","called getViw()");
	        
	        
            TextView title = (TextView) view.findViewById(R.id.explore_item_head);
            ImageView selectBox = (ImageView) view.findViewById(R.id.explore_file_type);
            TextView data = (TextView) view.findViewById(R.id.explore_item_size);
            TextView date = (TextView) view.findViewById(R.id.explore_item_date);
			//TextView textView = (TextView) view.findViewById(R.id.explore_item_head);
			//ImageView img = (ImageView) view.findViewById(R.id.explore_file_type);

			// put the image on the text view
			FileItem item=selectedFiles.get(position);
			//selectBox.setVisibility(View.GONE);
			if(item!=null) {
                selectBox.setImageDrawable(activity.getResources().getDrawable(item.icon));
				//title.setCompoundDrawablesWithIntrinsicBounds(item.icon, 0, 0, 0);
				title.setText(item.file);
                data.setText(Num.btyesToFileSizeString(item.length()));
                date.setText(Cal.getCal(item.lastModified()).getDatabaseDate());
				if(stopfileshash.get(item.getAbsolutePath())!=null) {
					view.setBackgroundColor(activity.getResources().getColor(R.color.black_alpha));
				} else {
					view.setBackgroundColor(activity.getResources().getColor(R.color.transparent));
				}
			}


	        return view;
	 
	    }



	}

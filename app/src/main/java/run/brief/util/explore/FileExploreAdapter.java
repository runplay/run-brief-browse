package run.brief.util.explore;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import run.brief.browse.R;
import run.brief.util.Cal;
import run.brief.util.Num;
import run.brief.util.explore.fm.FileManager;

public class FileExploreAdapter extends BaseAdapter {

	    private FilesActionModeSelecter fileSelectedActionSelecter;
	
	    private Activity activity;
	    private ImageView ivFlip;
	    private Animation animation1;
	    private Animation animation2;

		private boolean isMultiSelect=true;
	    //private int checkedCount = 0;

	    private FileManager fm;
		private List<FileItem> files=new ArrayList<FileItem>();
	    //private JSONArray data;
	    private static LayoutInflater inflater=null;

        public void setFileSelectedActionSelecter(FilesActionModeSelecter adapter) {
            fileSelectedActionSelecter=adapter;
            //fileSelectedActionSelecter.isActionModeShowing = false;
        }

	 	@Override
		public void notifyDataSetInvalidated() {
			buildList();
			super.notifyDataSetInvalidated();
		}
		private void buildList() {
			files.clear();
			for(int i=0; i<fm.getCurrentCount(); i++) {
				files.add(fm.getDirectoryItem(i));
			}
		}
	    public FileExploreAdapter(Activity a, FileManager filemanager) {
	        activity = a;

			this.fm=filemanager;
			buildList();
	        //this.data=data;
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			animation1 = AnimationUtils.loadAnimation(activity, R.anim.to_middle);
			animation2 = AnimationUtils.loadAnimation(activity, R.anim.from_middle);


	    }
	public FileExploreAdapter(Activity a, FileManager filemanager,boolean isMultiSelect) {
		activity = a;
		this.isMultiSelect=isMultiSelect;
		this.fm=filemanager;
		buildList();
		//this.data=data;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		animation1 = AnimationUtils.loadAnimation(activity, R.anim.to_middle);
		animation2 = AnimationUtils.loadAnimation(activity, R.anim.from_middle);


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



			//TextView textView = (TextView) view.findViewById(R.id.explore_item_head);
			//ImageView img = (ImageView) view.findViewById(R.id.explore_file_type);

			// put the image on the text view
			FileItem item= files.get(position);

			if(item!=null) {
				CheckHolder chkhold = new CheckHolder();
				chkhold.position = Integer.valueOf(position);
				chkhold.check = holder.check;

				//holder.title.setCompoundDrawablesWithIntrinsicBounds(item.icon, 0, 0, 0);
				holder.title.setText(item.file);
				holder.selectBox.setTag(chkhold);
				//Log.e("SIZE",item.getAbsolutePath()+" = "+item.length());
				String size = Num.btyesToFileSizeString(item.length());
				if (item.PARENT_TYPE_ == FileItem.PARENT_TYPE_ZIP)
					size = Num.btyesToFileSizeString(item.zipFileSize);
				holder.data.setText(size);
				holder.date.setText(Cal.getCal(item.lastModified()).getDatabaseDate());
				//if(item.icon==Files.F_DIR) {
				//	holder.selectBox.setVisibility(View.GONE);
				//} else {
				if (isMultiSelect) {
					holder.selectBox.setVisibility(View.VISIBLE);
					holder.selectBox.setClickable(true);
					if (item.getAbsoluteFile() == null || item.getAbsoluteFile().isDirectory())
						holder.check.setVisibility(View.GONE);
					else
						holder.check.setVisibility(View.VISIBLE);
					holder.selectBox.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							CheckHolder chk = (CheckHolder) v.getTag();
							FileItem f = fm.getDirectoryItem(chk.position.intValue());
							//ImageView img = (ImageView) v.getTag();
							//BLog.e("SEL", "view tag: " + v.getTag().toString() +"--"+Integer.valueOf(v.getTag().toString()).intValue()+ "--" + f.getName());
							if (f != null && !f.getAbsoluteFile().isDirectory()) {
								if (fm.getSelectedFiles().get(f.getAbsolutePath()) != null) {


									chk.check.setImageDrawable(activity.getResources().getDrawable(R.drawable.s_checkon));
									//BLog.e("REM",f.getAbsolutePath());
									fm.removeSelectedFile(f);
									setAnimListners(f);
									notifyDataSetChanged();
									if (fm.getSelectedFiles().isEmpty()) {
										if (fileSelectedActionSelecter.mMode != null)
											fileSelectedActionSelecter.mMode.finish();
									} else {
										if (fileSelectedActionSelecter.mMode != null)
											fileSelectedActionSelecter.mMode.setTitle(fm.getSelectedFiles().size() + "");
									}


								} else {
									fm.addSelectedFile(f);
									//File fi=new File(f.file);
									//if(fi.exists() && !fi.isDirectory()) {
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
										fileSelectedActionSelecter.mMode.setTitle(fm.getSelectedFiles().size() + "");

									notifyDataSetChanged();
								}
							}
						}

					});
				} else {
					holder.check.setVisibility(View.GONE);
				}

				//holder.selectBox.setImageDrawable(activity.getResources().getDrawable(item.icon));

				//if(item.file.endsWith(".jpg")) {
				//	holder.selectBox.setImageDrawable(item);
				//} else {
				holder.selectBox.setImageDrawable(activity.getResources().getDrawable(item.icon));
				// }
				//BLog.e("DISPO",item.getAbsolutePath());
				if (fm.getSelectedFiles().get(item.getAbsolutePath()) != null) {
					//holder.selectBox.setImageResource(R.drawable.cb_checked);
					view.setBackgroundColor(activity.getResources().getColor(R.color.browse_brand_alpha50));
					holder.check.setImageDrawable(activity.getResources().getDrawable(R.drawable.s_checkon));
					//holder.selectBox.setBackgroundResource(R.drawable.navigation_accept);

				} else {
					//holder.selectBox.setImageResource(R.drawable.cb_unchecked);
					view.setBackgroundColor(activity.getResources().getColor(R.color.transparent));
					holder.check.setImageDrawable(activity.getResources().getDrawable(R.drawable.s_checkoff));
					//holder.selectBox.setBackgroundResource(R.drawable.navigation_cancel);

				}
				ImageView showInfo = (ImageView) view.findViewById(R.id.file_info);
				InfoClicker info = new InfoClicker(activity, item);
				//info.pos=position;
				showInfo.setOnClickListener(info);
			}
	        return view;
	 
	    }


	    private void setAnimListners(final FileItem curMail) {
	        AnimationListener animListner;
	        animListner = new AnimationListener() {
	 
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
	    private static class ViewHolder {
	        TextView title;
	        ImageView selectBox;
	        TextView data;
            TextView date;
            ImageView check;
	    }
        private static class CheckHolder {
            Integer position;
            ImageView check;
        }

	}

package run.brief.util.explore;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import run.brief.b.BFragment;
import run.brief.b.BRefreshable;
import run.brief.b.Bgo;
import run.brief.b.BrowseService;
import run.brief.b.Device;
import run.brief.b.State;
import run.brief.b.StateObject;
import run.brief.b.fab.Fab;
import run.brief.b.fab.FloatingActionButton;
import run.brief.browse.R;
import run.brief.search.SearchFragment;
import run.brief.util.ActionBarManager;
import run.brief.util.Cal;
import run.brief.util.Files;
import run.brief.util.ImageCache;
import run.brief.util.explore.fm.FileManagerDisk;
import run.brief.util.explore.fm.FileManagerList;
import run.brief.util.explore.fm.FileManagerZip;

public class FileExploreFragment extends BFragment implements BRefreshable,OnScrollListener {
	private View view;
	
	//private LinearLayout options;
	private ViewGroup container;
	private LayoutInflater inflater;
	
	
	private AppCompatActivity activity=null;
	private FileExploreAdapter adapter;
	private FileImagesAdapter adapterImages;
	private ListView list;
	private GridView listImages;
    private Menu menu;

	private TextView title;
	private TextView path;
	private TextView info;
	private TextView files;
	private View loading;

	//private ImageButton upBtn;
    private ExploreDialog popupMenu;
    private static FileExploreFragment thisFragment;

    private FilesActionModeSelecter fileSelectedActionSelecter; //new FilesActionModeSelecter(activity)
	private FloatingActionButton fab;
	private FileManagerDisk fm;
    private DisplayFolderAsync bigFolderTask;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        //Log.e("RESUME", "FileExploreFrAGMENT CREATE: "+this.getClass().getCanonicalName());
        //Validator.logCaller();
		this.container=container;
		this.inflater=inflater;
		this.activity=(AppCompatActivity) getActivity();

		fm = (FileManagerDisk) State.getCachedFileManager(FileManagerDisk.class);
		if(fm==null) {
			fm=new FileManagerDisk();
			//fm.init(activity);
		}

		//State.clearStateAllObjects();

        thisFragment=this;
        fileSelectedActionSelecter= new FilesActionModeSelecter(activity,fm);

		view=inflater.inflate(R.layout.file_explore,container, false);
		
		//fileExplorerHandler.postDelayed(fileExplorerRunner, 10);
		return view;
		

	}
	@Override
	public void onPause() {
		super.onPause();

		State.addToState(State.SECTION_FILE_EXPLORE, new StateObject(StateObject.INT_LISTVIEW_FIRST_VIEWABLE, list.getFirstVisiblePosition()));
		if(adapterImages!=null) {

			ImageCache.clearCache();
            adapterImages=null;

		}
		State.addToState(State.SECTION_FILE_EXPLORE,new StateObject(StateObject.STRING_FILE_PATH,fm.getCurrentDirectory().getAbsolutePath()));
		processMessageHandler.removeCallbacks(processMessagerunnable);
        State.setStateLastKnownPosition(State.SECTION_FILE_EXPLORE,list);
	}
	@Override
	public void onResume() {
        //Log.e("RESUME", "FileExploreFrAGMENT: " + this.getClass().getCanonicalName());
        //Validator.calldata();


		super.onResume();
		State.sectionsClearBackstack();
		State.setCurrentSection(State.SECTION_FILE_EXPLORE);
		//fm.init(activity);
		path = (TextView) view.findViewById(R.id.files_directory_path);
		info = (TextView) view.findViewById(R.id.files_directory_info);
		
		files = (TextView) view.findViewById(R.id.files_directory_info_files);
		title = (TextView) view.findViewById(R.id.files_directory_title);
		
		list=(ListView) view.findViewById(R.id.files_list);
        list.setAdapter(null);
		listImages=(GridView)  view.findViewById(R.id.files_list_grid);
        listImages.setAdapter(null);

		loading = activity.findViewById(R.id.loading);


		Fab.set(activity, null, null, upDirectoryListener);

		refresh();





	}




	public void refresh() {
		String sob = State.getStateObjectString(State.SECTION_FILE_EXPLORE, StateObject.STRING_FILE_PATH);
		if(sob!=null) {
			//BLog.e("file manager refresh called: " + sob);
			fm.setCurrentDirectory(activity, sob);

		}



        refreshData();

	}
    public void refreshData() {

        showHideMessage();
        if(fm.getCurrentDirectory().getAbsolutePath().equals(File.separator)) {
            Fab.hide();
        } else {
            Fab.show();
        }


        int dcount=fm.loadDirectory();
        if(dcount>200) {
            loading.setVisibility(View.VISIBLE);
            loading.bringToFront();
            //BLog.e("call async load folder");
            bigFolderTask =new DisplayFolderAsync();
            bigFolderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);

        } else {
            loading.setVisibility(View.GONE);
            fm.refresh(activity);
            displayFolder();
        }

    }
	private void displayFolder() {
		//BLog.e("displayFolder");
		loadFileList();
		if(fm.getSelectedFiles().isEmpty()) {
			if(State.getFileExploreState()==State.FILE_EXPLORE_STATE_STANDALONE) {
				ActionBarManager.setActionBarBackOnlyWithLogo(activity,R.drawable.icon, activity.getResources().getString(R.string.label_files),R.menu.file_explore,R.color.actionbar_basic);
			} else {
				ActionBarManager.setActionBarBackOnlyWithLogo(activity,R.drawable.icon, activity.getResources().getString(R.string.label_files),R.menu.basic,R.color.actionbar_basic);
			}
			fileSelectedActionSelecter.mMode=null;
			fileSelectedActionSelecter.isActionModeShowing = false;
		} else {
			if(State.getFileExploreState()==State.FILE_EXPLORE_STATE_STANDALONE) {
				ActionBarManager.setActionBarBackOnlyWithLogo(activity,R.drawable.icon, activity.getResources().getString(R.string.label_files),R.menu.file_explore,R.color.actionbar_basic);
			} else {
				ActionBarManager.setActionBarBackOnlyWithLogo(activity,R.drawable.icon, activity.getResources().getString(R.string.label_files),R.menu.basic,R.color.actionbar_basic);
			}
			if(fileSelectedActionSelecter.mMode==null) {
				fileSelectedActionSelecter.mMode = ((AppCompatActivity)activity).startSupportActionMode(fileSelectedActionSelecter);
				//Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
				//toolbar.startActionMode(fileSelectedActionSelecter)
			}
			fileSelectedActionSelecter.updateTitle();
			fileSelectedActionSelecter.isActionModeShowing = true;
		}



		//apadpter
		fileSelectedActionSelecter.setWipeonfinish(true);

		File f= fm.getCurrentDirectory();
		title.setText(f.getName());
		path.setText(f.getAbsolutePath());
		info.setText(""+ fm.getCurrentDirectoryCount());
		files.setText("" + fm.getCurrentFileCount());

        int gopos=0;
        if(State.hasStateObject(State.SECTION_FILE_EXPLORE,StateObject.INT_LISTVIEW_FIRST_VIEWABLE)) {
            gopos=State.getStateObjectInt(State.SECTION_FILE_EXPLORE, StateObject.INT_LISTVIEW_FIRST_VIEWABLE);

            State.setFolderPosition(fm.getCurrentDirectory().getAbsolutePath(), gopos);
        } else {
            gopos=State.getFolderPosition(fm.getCurrentDirectory().getAbsolutePath());
        }
        if(fm.isCurrentDirImages()) {
            listImages.setSelection(gopos);
        } else {
            list.setSelection(gopos);
        }
        State.clearStateObjects(State.SECTION_FILE_EXPLORE);
	}
	private void loadFileList() {

		//BLog.e("loadFileList1");


		File cdir= fm.getCurrentDirectory();
		title.setText(cdir.getName());

		//BLog.e("isimages: " + fm.isCurrentDirImages());
		if(fm.isCurrentDirImages()) {
			view.setBackgroundColor(activity.getResources().getColor(R.color.black));

			list.setVisibility(View.GONE);
			listImages.setVisibility(View.VISIBLE);


			listImages.setClickable(true);
			listImages.setOnItemClickListener(openListener);
			listImages.setOnItemLongClickListener(openLongListener);
			adapterImages=new FileImagesAdapter(getActivity(),listImages,fm);
			adapterImages.setFileSelectedActionSelecter(fileSelectedActionSelecter);
			listImages.setAdapter(adapterImages);
			listImages.setEmptyView(activity.getLayoutInflater().inflate(R.layout.file_explore_empty, null));
			listImages.setOnScrollListener(this);
			//BLog.e("loadFileList.images");
			//BLog.e("CDIR", "is pictures");

		} else {
			//BLog.e("CDIR", "is files");
			view.setBackgroundColor(activity.getResources().getColor(R.color.transparent));


			list.setVisibility(View.VISIBLE);
			listImages.setVisibility(View.GONE);

			list.setClickable(true);
			list.setOnItemClickListener(openListener);
			list.setOnItemLongClickListener(openLongListener);
			adapter=new FileExploreAdapter(getActivity(),fm);
			adapter.setFileSelectedActionSelecter(fileSelectedActionSelecter);

			list.setAdapter(adapter);
			list.setEmptyView(activity.getLayoutInflater().inflate(R.layout.file_explore_empty, null));

		}
		State.updateLastKnownPosition(State.SECTION_FILE_EXPLORE,list);
		setColors();




	}
	private class DisplayFolderAsync extends AsyncTask<Boolean, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Boolean... params) {
			//BLog.e("call refresh");
            fm.refresh(activity);
			//BLog.e("finish call refresh");
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			loading.setVisibility(View.GONE);
            if(android.os.Build.VERSION.SDK_INT>= 17) {
                if (!activity.isDestroyed())
                    displayFolder();
            } else {
				displayFolder();
			}
		}
	};
	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        //BLog.e("FileExplore oncreateoptionsmenu");
        this.menu=menu;
		MenuInflater minflater = activity.getMenuInflater();
		minflater.inflate(R.menu.file_explore, menu);

        HashMap<String,FileItem> copyfiles=fm.getClipboardCopyFiles();
        //Log.e("COPY","copy files: "+copyfiles.size());
        MenuItem item = menu.findItem(R.id.action_paste);

        if(copyfiles!=null && !copyfiles.isEmpty()) {

            item.setVisible(true);
        } else {
            item.setVisible(false);
        }
        if(fm!=null) {
            if (fm.getCurrentDirectory().getAbsolutePath().equals(File.separator)) {
                Fab.hide();
            } else {
                Fab.show();
            }
        }
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//BLog.e("onCreateOptionsMenu at home");
        boolean callrefresh=true;
        boolean intercept=false;
		switch(item.getItemId()) {
			case R.id.action_search:
				State.addCachedFileManager(fm);
                callrefresh=false;
				Bgo.openFragmentBackStack(activity,new SearchFragment());
                intercept=true;
				break;
            /*
            case R.id.orderby_name_asc:
                //g.setEmailServiceInstance(emailService);
                fm.setOrderBy(activity, fm.ORDER_ALPHA_ASC);
                intercept=true;
                break;
            case R.id.orderby_name_desc:
                //g.setEmailServiceInstance(emailService);
                fm.setOrderBy(activity, fm.ORDER_ALPHA_DESC);
                intercept=true;
                break;
            case R.id.orderby_date_asc:
                //g.setEmailServiceInstance(emailService);
                fm.setOrderBy(activity, fm.ORDER_DATE_ASC);
                intercept=true;
                break;
            case R.id.orderby_date_desc:
                //g.setEmailServiceInstance(emailService);
                fm.setOrderBy(activity, fm.ORDER_DATE_DESC);
                intercept=true;
                break;
            case R.id.showhide_system_files:
                //g.setEmailServiceInstance(emailService);
                //if(fm.getShowSystemFiles())
                    //fm.setOrderBy(fm.ORDER_DATE_DESC);
                fm.setShowSystemFiles(activity, !fm.getShowSystemFiles());
                intercept=true;
                //refreshData();
                break;
            */
            case R.id.action_paste:
                //g.setEmailServiceInstance(emailService);

				if(fm.isCutPasteFilesOnClipboard) {
					Toast.makeText(activity,getString(R.string.action_paste),Toast.LENGTH_SHORT);
					BrowseService.MoveFiles(activity, fm.getClipboardCopyFilesAsList(), fm.getPath());
				} else {
					Toast.makeText(activity,getString(R.string.action_paste),Toast.LENGTH_SHORT);
					BrowseService.PasteFiles(activity,fm.getClipboardCopyFilesAsList(),fm.getPath());
				}
				fm.clearSelectedClipboardCopyFiles();
                //fm.setOrderBy(activity, fm.ORDER_DATE_DESC);
                callrefresh=true;
                intercept=true;
                break;

		}
        if(callrefresh)
            refreshData();
		return intercept;
	}

	private Handler processMessageHandler =new Handler();
	private Runnable processMessagerunnable = new Runnable() {
		@Override
		public void run() {
			showHideMessage();
		}
	};
	private void showHideMessage() {
		TextView pop = (TextView) activity.findViewById(R.id.main_pop_message);
		if(pop!=null) {
			if(BrowseService.isArchiving()) {
				if(fm.isCurrentDirImages()) {
					pop.setBackgroundColor(activity.getResources().getColor(R.color.white_alpha));
					pop.setTextColor(activity.getResources().getColor(R.color.black));
				} else {
					pop.setBackgroundColor(activity.getResources().getColor(R.color.black_alpha));
					pop.setTextColor(activity.getResources().getColor(R.color.white));
				}
				pop.setText(BrowseService.getCurrentArchiveCount() + "/" + BrowseService.getCurrentArchiveTotal() + " - "+getString(R.string.creating_archive)+": " + BrowseService.getCurrentArhiveFileItem().getName());
				pop.setVisibility(View.VISIBLE);
				pop.bringToFront();
				if(fab!=null)
					fab.bringToFront();
				processMessageHandler.postDelayed(processMessagerunnable, 500);
			} else 	if(BrowseService.isMoving()) {

				if(fm.isCurrentDirImages()) {
					pop.setBackgroundColor(activity.getResources().getColor(R.color.white_alpha));
					pop.setTextColor(activity.getResources().getColor(R.color.black));
				} else {
					pop.setBackgroundColor(activity.getResources().getColor(R.color.black_alpha));
					pop.setTextColor(activity.getResources().getColor(R.color.white));
				}
				pop.setText(getString(R.string.label_moving));
				pop.setVisibility(View.VISIBLE);
				pop.bringToFront();
				if(fab!=null)
					fab.bringToFront();
				processMessageHandler.postDelayed(processMessagerunnable, 500);
			}  else if(BrowseService.isPasting()) {

				if(fm.isCurrentDirImages()) {
					pop.setBackgroundColor(activity.getResources().getColor(R.color.white_alpha));
					pop.setTextColor(activity.getResources().getColor(R.color.black));
				} else {
					pop.setBackgroundColor(activity.getResources().getColor(R.color.black_alpha));
					pop.setTextColor(activity.getResources().getColor(R.color.white));
				}
				pop.setText(getString(R.string.label_paste_files));
				pop.setVisibility(View.VISIBLE);
				pop.bringToFront();
				if(fab!=null)
					fab.bringToFront();
				processMessageHandler.postDelayed(processMessagerunnable, 500);
			} else {
				pop.setVisibility(View.GONE);
				pop.setText("");
			}
		}
	}




	private void setColors() {
		int set=activity.getResources().getColor(R.color.black);
		if(fm.isCurrentDirImages()) {
			set=activity.getResources().getColor(R.color.white);
		} else {
            TypedValue outValue = new TypedValue();
            activity.getTheme().resolveAttribute(R.attr.themeName, outValue, true);
            if ("dark".equals(outValue.string)) {
                set=activity.getResources().getColor(R.color.white);
            }

        }
		path.setTextColor(set);
		info.setTextColor(set);
		title.setTextColor(set);
		files.setTextColor(set);
	}
	public OnItemClickListener openListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {


			File f = fm.getDirectoryItemAsFile(position);
            //BLog.e("OPEN FILES !!!!: "+f.getName());
			if(f!=null) {
				State.setFolderPosition(fm.getCurrentDirectory().getAbsolutePath(), list.getFirstVisiblePosition());
				if(f.isDirectory()) {

					boolean ok=fm.setCurrentDirectory(activity, f.getAbsolutePath());
					if(ok) {
						refreshData();
						title.setText(f.getName());
						path.setText(f.getAbsolutePath());
						info.setText("" + fm.getCurrentDirectoryCount());
						files.setText("" + fm.getCurrentFileCount());
						setColors();
					} else {
						Toast.makeText(activity,"Cannot read directory, insufficient permissions",Toast.LENGTH_SHORT);
					}
					//adapter.notifyDataSetChanged();
					
				} else if(f.isFile()) {
					String fname=Files.removeBriefFileExtension(f.getName());
					if(Files.isImage(fname)) {

						//FileManager fm = new FileManager();
						//fm.setCurrentDirectory(activity,f.getParentFile().getAbsolutePath());
						//fm.readDirectory(activity);
						int usepos=position;
                        List<FileItem> useitems = new ArrayList<FileItem>();

							for(int i=0; i<fm.getDirectory(activity).size(); i++) {

								if(!Files.isImage(Files.removeBriefFileExtension(fm.getDirectoryItem(i).getName()))) {
                                    if(i<position)
                                        usepos--;
                                } else
                                    useitems.add(fm.getDirectoryItem(i));
							}


                        FileManagerList fml = new FileManagerList(useitems);
                        fml.setStartAtPosition(usepos);
                        State.addCachedFileManager(fml);

						Bgo.openFragmentBackStack(activity,new ImagesSliderFragment());

					} else if(Files.isTextFile(f.getName())) {

						State.addCachedFileManager(fm);
                        State.addToState(State.SECTION_TEXT_FILE_VIEW,new StateObject(StateObject.STRING_FILE_PATH,fm.getDirectoryItem(position).getAbsolutePath()));
						Bgo.openFragmentBackStack(activity,new TextFileFragment());

					} else {
						//openOptions(f.getAbsolutePath());
						if(fname.endsWith(".zip")) {

							FileManagerZip fmz = new FileManagerZip(f.getAbsolutePath());
							State.addCachedFileManager(fmz);
							Bgo.openFragmentBackStack(activity, new ZipExploreFragment());

						} else {
							Device.openAndroidFile(activity, f);
						}
					}
				}
			}
			
			
		}
	};
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {


	}
    private long lastscroll;
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
        long now=Cal.getUnixTime();
        if(now-lastscroll<12) {
            //BLog.e("FASAT SCROLL");
            adapterImages.isFastScroll=true;
        } else {
            adapterImages.isFastScroll=false;
        }

        lastscroll=now;

	}
	public OnItemLongClickListener openLongListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {

			File f = fm.getDirectoryItemAsFile(position);
			if(f!=null) {
				
				if(State.getFileExploreState()==State.FILE_EXPLORE_STATE_STANDALONE && !f.isDirectory()) {
					//openOptions(f.getAbsolutePath());
                    popupMenu = new ExploreDialog(getActivity(),f,thisFragment,fm);

                    popupMenu.show();
                    popupMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface intf) {
                            //BLog.e("DISMISS", "Called");
                            if(ExploreDialog.shouldRefresh)
                                refreshData();
                        }
                    });
				}

			}
			return true;
			
			
		}
	};
	public OnClickListener upDirectoryListener = new OnClickListener() {
		@Override
		public void onClick(View arg1) {
            if(fm.isCurrentDirImages()) {
                State.setFolderPosition(fm.getCurrentDirectory().getAbsolutePath(), listImages.getFirstVisiblePosition());
            } else {
                State.setFolderPosition(fm.getCurrentDirectory().getAbsolutePath(), list.getFirstVisiblePosition());
            }

			fm.goUpDirectory(activity);
            refreshData();
		}
	};


	
}
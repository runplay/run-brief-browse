package run.brief.util.explore;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

import run.brief.b.BFragment;
import run.brief.b.BRefreshable;
import run.brief.b.Bgo;
import run.brief.b.BrowseService;
import run.brief.b.State;
import run.brief.b.StateObject;
import run.brief.b.fab.Fab;
import run.brief.b.fab.FloatingActionButton;
import run.brief.browse.R;
import run.brief.util.ActionBarManager;
import run.brief.util.explore.fm.FileManagerZip;

public class ZipExploreFragment extends BFragment implements BRefreshable {
	private View view;

	//private LinearLayout options;
	private ViewGroup container;
	private LayoutInflater inflater;


	private AppCompatActivity activity=null;
	private static FileExploreAdapterZip adapter;
	//private static FileImagesAdapter adapterImages;
	private static ListView list;
	//private GridView listImages;

	private View headpod;
	private View extractpod;
	//private static HashMap<String,FileItem> selected=new HashMap<String,FileItem>();
	private TextView title;
	private TextView path;
	private TextView info;
	private TextView files;
    private TextView zipFolder;
    private TextView zipFolderPath;
    private ImageView chooseFolder;
	private Button extractNow;

    private String extractToFolder;

    private ExploreDialog popupMenu;
    private static ZipExploreFragment thisFragment;

	private FloatingActionButton fab;
	private FileManagerZip fm;

	private int mode;

    //private Handler unzipHandler;



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.container=container;
		this.inflater=inflater;
		this.activity=(AppCompatActivity)getActivity();



		fm= (FileManagerZip) State.getCachedFileManager(FileManagerZip.class);
		if(fm==null) {
			//Log.e("FS","Zip from state: "+State.getStateObjectString(State.SECTION_FILE_EXPLORE_ARCHIVE, StateObject.STRING_FILE_PATH));
			fm=new FileManagerZip(State.getStateObjectString(State.SECTION_FILE_EXPLORE_ARCHIVE, StateObject.STRING_FILE_PATH));
			fm.refresh(activity);
		}


        thisFragment=this;

		view=inflater.inflate(R.layout.file_explore_archive_view_extract,container, false);

		return view;


	}
	@Override
	public void onPause() {
		super.onPause();

		recheckArchingstatus.removeCallbacks(runCheckingArchive);
		//BLog.e("SAVE: " + fm.getCurrentDirectory().getAbsolutePath());
		State.addToState(State.SECTION_FILE_EXPLORE_ARCHIVE, new StateObject(StateObject.STRING_FILE_PATH, fm.getCurrentDirectory().getAbsolutePath()));
        State.addToState(State.SECTION_FILE_EXPLORE_ARCHIVE, new StateObject(StateObject.INT_MODE,mode));
        if(amb!=null)
			amb.done();
        //amb.mMode.finish();


	}
	@Override
	public void onResume() {
		//Log.e("RESUME", "ZipFileExplore");
		super.onResume();
		State.setCurrentSection(State.SECTION_FILE_EXPLORE_ARCHIVE);

		//fm.init(activity);
		headpod =  view.findViewById(R.id.files_header);
		extractpod =  view.findViewById(R.id.extract_files_header);
		path = (TextView) view.findViewById(R.id.files_directory_path);
		info = (TextView) view.findViewById(R.id.files_directory_info);

		files = (TextView) view.findViewById(R.id.files_directory_info_files);
		title = (TextView) view.findViewById(R.id.files_directory_title);

		list=(ListView) view.findViewById(R.id.files_list);
		//listImages=(GridView)  view.findViewById(R.id.files_list_grid);
        chooseFolder=(ImageView) view.findViewById(R.id.file_explore_choose_folder);
        chooseFolder.setClickable(true);
        chooseFolder.setOnClickListener(changeFolderListner);

        zipFolder=(TextView) view.findViewById(R.id.zip_folder);
        zipFolderPath=(TextView) view.findViewById(R.id.zip_folder_path);

		extractNow=(Button) view.findViewById(R.id.file_explore_extract_now);
		extractNow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!BrowseService.isArchiving()) {
                    started=true;
                    recheckArchingstatus.postDelayed(runCheckingArchive, 100);
                    BrowseService.unArchiveFiles(fm.getCurrentDirectory().getAbsolutePath(),extractToFolder);

				}
			}
		});

		Fab.hide();
		//header=(LinearLayout) view.findViewById(R.id.files_header);
		refresh();

	}

	public void refreshData() {
        refresh();

	}
	public void refresh() {

		if(State.hasStateObject(State.SECTION_FILE_EXPLORE_ARCHIVE, StateObject.INT_MODE)) {
			mode = State.getStateObjectInt(State.SECTION_FILE_EXPLORE_ARCHIVE, StateObject.INT_MODE);
		}
        int useMenu=R.menu.archive;
        if (mode > 0) {
            useMenu=R.menu.basic;
            headpod.setVisibility(View.GONE);
            extractpod.setVisibility(View.VISIBLE);
        } else {
            headpod.setVisibility(View.VISIBLE);
            extractpod.setVisibility(View.GONE);
        }

		State.clearStateObjects(State.SECTION_FILE_EXPLORE_ARCHIVE);
		fm.refresh(activity);
		loadFileList();
		amb= new ActionModeBack(activity, activity.getResources().getString(R.string.label_archive)
				,useMenu
				, new ActionModeCallback() {
			@Override
			public void onActionMenuItem(ActionMode mode, MenuItem item) {
				onOptionsItemSelected(item);
			}
		});
		if(android.os.Build.VERSION.SDK_INT>= 19) {

			ActionBarManager.setActionBarBackV19(activity, amb);
			//setActionBarBackV19();
		} else {
			ActionBarManager.setActionBarBackOnly(activity, activity.getResources().getString(R.string.label_archive), useMenu,amb);
		}


		//ActionBarManager.setActionBarBackOnly(activity, activity.getResources().getString(R.string.label_archive), R.menu.archive, R.color.actionbar_basic);


		File f= fm.getCurrentDirectory();
		title.setText(f.getName());
		path.setText(f.getAbsolutePath());
		info.setText(""+ fm.getCurrentDirectoryCount());
		files.setText("" + fm.getCurrentFileCount());



        File p=f.getParentFile();
        if(p.exists()) {
            zipFolder.setText(p.getName());
            zipFolderPath.setText(p.getParent());
        } else {
            zipFolder.setText("");
            zipFolderPath.setText("/");
        }

        extractToFolder=p.getAbsolutePath();



		adapter.notifyDataSetChanged();


	}
	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
		//BLog.e("OPTIONS", "onCreateOptionsMenu at new emai home");

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//BLog.e("onOptionsItemSelected");
		switch(item.getItemId()) {

            case R.id.file_explore_archive_extract:
				mode=1;
				amb.done();
				refresh();
				return true;
		}
		return false;
	}

	private Handler recheckArchingstatus=new Handler();
	private Runnable runCheckingArchive = new Runnable() {
		@Override
		public void run() {
			showHideMessage();
		}
	};
    private boolean started=false;
	private void showHideMessage() {
		TextView pop = (TextView) activity.findViewById(R.id.main_pop_message);
		if(pop!=null) {
			if(BrowseService.isUnArchiving()) {

				if(fm.isCurrentDirImages()) {
					pop.setBackgroundColor(activity.getResources().getColor(R.color.white_alpha));
				} else {
					pop.setBackgroundColor(activity.getResources().getColor(R.color.black_alpha));
                }
                pop.setText(BrowseService.getCurrentUnArchiveCount() + "/" + BrowseService.getCurrentUnArchiveTotal() + " - Extracting archive ");
				pop.setVisibility(View.VISIBLE);
				pop.bringToFront();
				//fab.bringToFront();
				recheckArchingstatus.postDelayed(runCheckingArchive,100);
			} else {
				pop.setVisibility(View.GONE);
                pop.setText("");
                State.addToState(State.SECTION_FILE_EXPLORE, new StateObject(StateObject.STRING_FILE_PATH, extractToFolder));
                if(started) {
                    amb.done();
                    Bgo.goPreviousFragment(activity);
                }
			}
		}
	}



	private void loadFileList() {


		File cdir= fm.getCurrentDirectory();
		title.setText(cdir.getName());


		list.setVisibility(View.VISIBLE);
		//listImages.setVisibility(View.GONE);

		list.setClickable(true);
		//list.setOnItemClickListener(openListener);
		//list.setOnItemLongClickListener(openLongListener);
		adapter=new FileExploreAdapterZip(getActivity(),fm);
		list.setAdapter(adapter);
		list.setEmptyView(activity.getLayoutInflater().inflate(R.layout.file_explore_empty, null));
			

		setColors();


        

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
    public OnClickListener changeFolderListner = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //Log.e("FOLDER","open popchoosefolder");
            amb.done();
            State.addToState(State.SECTION_POP_FOLDER_CHOOSER, new StateObject(StateObject.STRING_FILE_PATH, fm.getCurrentDirectory().getAbsolutePath()));
            Bgo.openFragmentBackStack(activity, new FolderChooseFragment());
        }
    };
/*
	public OnItemClickListener openListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {

			File f = fm.getDirectoryItem(position);
			if(f!=null) {
				if(f.isDirectory()) {
					//fm.setCurrentDirectory(activity, f.getAbsolutePath());
					refresh();
					title.setText(f.getName());
					path.setText(f.getAbsolutePath());
					info.setText(""+ fm.getCurrentDirectoryCount());
					files.setText(""+ fm.getCurrentFileCount());
					setColors();
					//adapter.notifyDataSetChanged();

				} else if(f.isFile()) {
					Bgo.openFile(activity,fm,f);
				}
			}


		}
	};
	

	public OnItemLongClickListener openLongListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {

			File f = fm.getDirectoryItem(position);
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
                                refresh();
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

			fm.goUpDirectory(activity);
            refreshData();
		}
	};
*/

	
}
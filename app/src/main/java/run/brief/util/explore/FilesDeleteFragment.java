package run.brief.util.explore;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import run.brief.b.BFragment;
import run.brief.b.BRefreshable;
import run.brief.b.State;
import run.brief.b.fab.Fab;
import run.brief.browse.R;
import run.brief.util.ActionBarManager;
import run.brief.util.explore.fm.FileManagerDisk;

public class FilesDeleteFragment extends BFragment implements BRefreshable {
	private View view;
	private LinearLayout options;
	private ViewGroup container;
	private LayoutInflater inflater;
	
	private AppCompatActivity activity=null;
	private FileExploreSelectedFilesAdapter adapter;
	private ListView list;
	private RelativeLayout head;
	//private View upgrade;
	private LinearLayout deleteing;
	private TextView progressText;
	private LinearLayout completed;
	private Runnable completedp;

    private Button deletenow;
    //bButton cancel = (bButton) view.findViewById(R.id.file_explore_delete_cancel);
    //bButton safeDelete;

	private FileManagerDisk fm =null;
	
	private Handler deleteHandler = new Handler();
	//private ActionModeBack amb;
	//private static final int OPTIONS_WITH=150;

	
	//private File path = new File(Environment.getExternalStorageDirectory() + "");


	@Override
	public void onPause() {
		super.onPause();

		if(amb!=null)
			amb.done();


	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.container=container;
		this.inflater=inflater;
		this.activity=(AppCompatActivity) getActivity();
		
		fm=(FileManagerDisk) State.getCachedFileManager(FileManagerDisk.class);
		
		view=inflater.inflate(R.layout.file_explore_delete,container, false);
		//fileExplorerHandler.postDelayed(fileExplorerRunner, 10);
		return view;
		

	}
	@Override
	public void onResume() {
		super.onResume();
		State.setCurrentSection(State.SECTION_FILE_EXPLORE_DELETE);


        head = (RelativeLayout) view.findViewById(R.id.files_header);
        //upgrade = PlusMember.getPlusMemberUpgradeView(activity,view,cancelUpgradeListener); //(RelativeLayout) view.findViewById(R.id.file_explore_delete_upgrade_message);
        deleteing = (LinearLayout) view.findViewById(R.id.file_explore_deleteing);
        completed = (LinearLayout) view.findViewById(R.id.file_explore_deleteing_completed);
        progressText = (TextView) view.findViewById(R.id.file_explore_progress_text);

        deletenow = (Button) view.findViewById(R.id.file_explore_delete_now);
        //bButton cancel = (bButton) view.findViewById(R.id.file_explore_delete_cancel);


        //upgradeCancel = (bButton) view.findViewById(R.id.upgrade_cancel);
        //upgradeNow = (bButton) view.findViewById(R.id.upgrade_now);


		refresh();
	}
	public void refresh() {

		amb= new ActionModeBack(activity, activity.getResources().getString(R.string.label_delete)
				,R.menu.basic
				, new ActionModeCallback() {
			@Override
			public void onActionMenuItem(ActionMode mode, MenuItem item) {
				//Log.e("AMB", "menuitem actionmodeback: " + mode);

			}
		});
		if(android.os.Build.VERSION.SDK_INT>= 19) {

			ActionBarManager.setActionBarBackV19(activity, amb);
			//setActionBarBackV19();
		} else {
			ActionBarManager.setActionBarBackOnly(activity, activity.getResources().getString(R.string.label_archive), R.menu.archive,amb);
		}
		refreshData();
		Fab.hide();
	}
	public void refreshData() {
		

        deletenow.setOnClickListener(deleteNowListener);


		list=(ListView) view.findViewById(R.id.delete_files_list);
		//list.setClickable(true);
		//list.setOnItemClickListener(openListener);
		//list.setOnItemLongClickListener(openLongListener); 
		adapter=new FileExploreSelectedFilesAdapter(getActivity(),fm);
		
        list.setAdapter(adapter);
        
        //BLog.e("DEL", adapter.getCount()+" files");

        //list.setEmptyView((View) activity.findViewById(R.layout.file_explore_empty));

		head.setVisibility(View.VISIBLE);
		list.setVisibility(View.VISIBLE);
		//upgrade.setVisibility(View.GONE);
		deleteing.setVisibility(View.GONE);
		completed.setVisibility(View.GONE);
	}
	


	public OnClickListener deleteNowListener = new OnClickListener() {
		@Override
		public void onClick(View view) {

			ArrayList<FileItem> fileitems = adapter.getSelectedFiles();
			if(fileitems!=null && !fileitems.isEmpty()) {
				for(FileItem f: fileitems) {
					((File) f).delete();
				}
			}
			deleteHandler.postDelayed(completedReturnPrevious, 1000);
		}
	};


	public OnClickListener cancelUpgradeListener = new OnClickListener() {
		@Override
		public void onClick(View view) {

			head.setVisibility(View.VISIBLE);
			list.setVisibility(View.VISIBLE);
			//upgrade.setVisibility(View.GONE);
			deleteing.setVisibility(View.GONE);
			completed.setVisibility(View.GONE);
		}
	};
	public OnClickListener returnPreviousListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			goPreviousNow();
		}
	};
	private void goPreviousNow() {
		head.setVisibility(View.GONE);
		list.setVisibility(View.GONE);
		//upgrade.setVisibility(View.GONE);
		deleteing.setVisibility(View.GONE);
		completed.setVisibility(View.VISIBLE);
		fm.getSelectedFiles().clear();
		amb.finish();
		//Bgo.goPreviousFragment(activity);
	}
 	private Runnable completedReturnPrevious = new Runnable() {
		public void run() {
			goPreviousNow();
		}
	};

}
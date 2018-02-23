package run.brief.util.explore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

import run.brief.b.BFragment;
import run.brief.b.BRefreshable;
import run.brief.b.State;
import run.brief.b.StateObject;
import run.brief.b.fab.Fab;
import run.brief.browse.R;
import run.brief.util.ActionBarManager;
import run.brief.util.explore.fm.FileManagerFolders;

public class FolderChooseFragment extends BFragment implements BRefreshable {
	private View view;
	
	//private LinearLayout options;
	private ViewGroup container;
	private LayoutInflater inflater;
	
	
	private AppCompatActivity activity=null;
	private FolderChooseAdapter adapter;

	private ListView list;

    private Menu menu;

	private TextView title;
	private TextView path;
	private View loading;
	//private ImageButton upBtn;
    private ExploreDialog popupMenu;
    private static FolderChooseFragment thisFragment;

 	private FileManagerFolders fm;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        //Log.e("RESUME", "FileExploreFrAGMENT CREATE: "+this.getClass().getCanonicalName());
        //Validator.logCaller();
		this.container=container;
		this.inflater=inflater;
		this.activity=(AppCompatActivity) getActivity();

		fm= new FileManagerFolders();
			//fm.init(activity);


		thisFragment=this;
		view=inflater.inflate(R.layout.pop_folder_choose,container, false);

		//fileExplorerHandler.postDelayed(fileExplorerRunner, 10);
		return view;


	}
	@Override
	public void onPause() {
		super.onPause();

	}
	@Override
	public void onResume() {
       // Log.e("RESUME", "PopFolderChoose - Fragment: " + this.getClass().getCanonicalName());
		super.onResume();
		State.setCurrentSection(State.SECTION_POP_FOLDER_CHOOSER);
		amb = new ActionModeBack(activity, activity.getResources().getString(R.string.choose_folder)
				,R.menu.search
				, new ActionModeCallback() {
			@Override
			public void onActionMenuItem(ActionMode mode, MenuItem item) {

			}
		});
		if(android.os.Build.VERSION.SDK_INT>= 19) {

			ActionBarManager.setActionBarBackV19(activity, amb);
			//setActionBarBackV19();
		} else {
			ActionBarManager.setActionBarBackOnly(activity, activity.getResources().getString(R.string.choose_folder), R.menu.image_slider,amb);
		}

		String startFolder = State.getStateObjectString(State.SECTION_POP_FOLDER_CHOOSER, StateObject.STRING_FILE_PATH);
		if(startFolder!=null)
			fm.setCurrentDirectory(activity,startFolder);


		path = (TextView) view.findViewById(R.id.files_directory_path);

		title = (TextView) view.findViewById(R.id.files_directory_title);

		list=(ListView) view.findViewById(R.id.pop_files_list);
        list.setAdapter(null);
		loading = activity.findViewById(R.id.loading);



		refresh();

        Fab.set(activity, null, null, upDirectoryListener);



	}

	public void refreshData() {


        if(fm.getCurrentDirectory().getAbsolutePath().equals(File.separator)) {
            Fab.hide();
        } else {
            Fab.show();
        }


        //fm.readDirectory(activity);

		loading.setVisibility(View.GONE);
		fm.refresh(activity);
		displayFolder();


	}
	public void refresh() {


        refreshData();
        //Log.e("IIII","FileExploreFrag, finished render");


	}
	private void displayFolder() {
		loadFileList();

//		ActionBarManager.setActionBarBackOnly(activity, activity.getResources().getString(R.string.choose_folder), R.menu.basic);


		File f= fm.getCurrentDirectory();
		title.setText(f.getName());
		path.setText(f.getAbsolutePath());

	}

	@Override
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        //Log.e("FE","FileExplore oncreateoptionsmenu");
        this.menu=menu;


	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Log.e("OPTIONS", "onCreateOptionsMenu at new emai home");
        return true;
	}




	private void loadFileList() {

		File cdir= fm.getCurrentDirectory();
		title.setText(cdir.getName());
		//BLog.e("CDIR", "is files");
		view.setBackgroundColor(activity.getResources().getColor(R.color.transparent));


		list.setVisibility(View.VISIBLE);

		list.setClickable(true);
		list.setOnItemClickListener(openListener);
		adapter=new FolderChooseAdapter(getActivity(),fm);


		list.setAdapter(adapter);
		list.setEmptyView(activity.getLayoutInflater().inflate(R.layout.file_explore_empty,null));





	}

	public OnItemClickListener openListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {

			File f = fm.getDirectoryItemAsFile(position);
			if(f!=null) {
				if(f.isDirectory()) {
					fm.setCurrentDirectory(activity, f.getAbsolutePath());
					refreshData();
					title.setText(f.getName());
					path.setText(f.getAbsolutePath());

					//adapter.notifyDataSetChanged();
					
				}
			}
			
			
		}
	};
	


	public OnClickListener upDirectoryListener = new OnClickListener() {
		@Override
		public void onClick(View arg1) {

			fm.goUpDirectory(activity);
            refreshData();
		}
	};


	
}
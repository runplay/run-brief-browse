package run.brief.util.explore;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

import run.brief.b.BFragment;
import run.brief.b.BRefreshable;
import run.brief.b.Bgo;
import run.brief.b.State;
import run.brief.b.StateObject;
import run.brief.b.fab.Fab;
import run.brief.browse.R;
import run.brief.util.ActionBarManager;
import run.brief.util.BriefActivityManager;
import run.brief.util.Files;
import run.brief.util.explore.fm.FileManagerList;

public class ImagesSliderFragment extends BFragment implements BRefreshable {
	private View view;

	private ViewGroup container;
	private LayoutInflater inflater;
	
	private AppCompatActivity activity;
	private FileManagerList fm;
	private ImageSliderPager pager;
	private Toolbar toolbar;
    private List<FileItem> imgfiles;

    private ImageView btnBack;
    private ImageView btnShare;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.container=container;
		this.inflater=inflater;
		this.activity=(AppCompatActivity)getActivity();

		
		view=inflater.inflate(R.layout.images_slider,container, false);

		toolbar = (Toolbar) activity.findViewById(R.id.toolbar);

		//fileExplorerHandler.postDelayed(fileExplorerRunner, 10);
		return view;
		

	}
	@Override
	public void onResume() {
		super.onResume();
		Fab.hide();

		fm=(FileManagerList)State.getCachedFileManager(FileManagerList.class);
		if(fm==null) {
			//fm = new FileManagerDisk(State.getStateObjectString(State.SECTION_IMAGES_SLIDER, StateObject.STRING_FILE_PATH));
			fm.setStartAtPosition(State.getStateObjectInt(State.SECTION_IMAGES_SLIDER, StateObject.INT_VALUE));
		}
		State.setCurrentSection(State.SECTION_IMAGES_SLIDER);

		imgfiles=fm.getDirectory(activity);
		for(int i=imgfiles.size()-1; i>=0; i--) {
			FileItem imfile= imgfiles.get(i);
			if(!Files.isImage(Files.removeBriefFileExtension(imfile.getName())))
				imgfiles.remove(i);
		}
		WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);



		pager = (ImageSliderPager) view.findViewById(R.id.pager);

		pager.setViewWidth(size.x);
		pager.setOffscreenPageLimit(0);
		pager.setAdapter(new ImageSliderAdapter(activity, imgfiles));

        pager.setCurrentItem(fm.getStartAtPosition());

        btnBack=(ImageView) view.findViewById(R.id.slider_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bgo.goPreviousFragment(activity);
            }
        });

        btnShare=(ImageView) view.findViewById(R.id.slider_share);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = pager.getCurrentItem();
                File fi = fm.getDirectoryItemAsFile(pos);
                if (fi != null) {
                    //this.done();
                    BriefActivityManager.shareFile(activity, fi.getAbsolutePath());
                }
            }
        });

		refresh();
	}
	public void onPause() {
		super.onPause();
//		State.addToState(State.SECTION_IMAGES_SLIDER,new StateObject(StateObject.STRING_FILE_PATH,fm.getCurrentDirectory().getAbsolutePath()));
		State.addToState(State.SECTION_IMAGES_SLIDER,new StateObject(StateObject.INT_VALUE,fm.getStartAtPosition()));
        if(amb!=null)
            amb.done();
        pager.removeAllViews();
        pager=null;

	}
	public void refresh() {
        /*
		amb = new ActionModeBack(activity
				, activity.getResources().getString(R.string.label_images)
				,R.menu.image_slider
				, new ActionModeCallback() {
			@Override
			public void onActionMenuItem(ActionMode mode, MenuItem item) {
				//Log.e("OPTIONS", "ACTIONMODE CALL BACK !!!!!!!!!!!!!!!!!!!");
				switch (item.getItemId()) {


					case R.id.menu_share:
						int pos = pager.getCurrentItem();
						File fi = fm.getDirectoryItemAsFile(pos);
						if (fi != null) {
							//this.done();
							BriefActivityManager.shareFile(activity, fi.getAbsolutePath());
						}
						break;
				}
			}
		});
		if(android.os.Build.VERSION.SDK_INT>= 19) {

			ActionBarManager.setActionBarBackV19(activity, amb);
			//setActionBarBackV19();
		} else {
			ActionBarManager.setActionBarBackOnly(activity, activity.getResources().getString(R.string.label_images), R.menu.image_slider,amb);
		}
        */
		ActionBarManager.hide(activity);

		refreshData();

	}


	public void refreshData() {


	}



}
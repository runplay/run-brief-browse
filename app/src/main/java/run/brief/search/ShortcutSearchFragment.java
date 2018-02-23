package run.brief.search;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import run.brief.b.BFragment;
import run.brief.b.BRefreshable;
import run.brief.b.Bgo;
import run.brief.b.Device;
import run.brief.b.State;
import run.brief.b.StateObject;
import run.brief.b.fab.Fab;
import run.brief.browse.R;
import run.brief.util.ActionBarManager;
import run.brief.util.Cal;
import run.brief.util.Files;
import run.brief.util.Functions;
import run.brief.util.Sf;
import run.brief.util.explore.ActionModeBack;
import run.brief.util.explore.ActionModeCallback;
import run.brief.util.explore.ImagesSliderFragment;
import run.brief.util.explore.IndexerDb;
import run.brief.util.explore.IndexerFile;
import run.brief.util.explore.TextFileFragment;
import run.brief.util.explore.ZipExploreFragment;
import run.brief.util.explore.fm.FileManagerList;
import run.brief.util.explore.fm.FileManagerZip;
import run.brief.util.json.JSONObject;
import run.brief.util.log.BLog;


public class ShortcutSearchFragment extends BFragment implements BRefreshable,AbsListView.OnScrollListener {
	private View view;

	private AppCompatActivity activity=null;
	//private EditText searchText;
	private static SearchShortcutAdapter adapter;
	private ListView list;
	private View updating;
	private View start;
	//private View searchHeader;
	private SearchPacket packet;
	private View loading;
	private LinearLayout foldersView;
	private List<IndexerFile> folders;
    private Animation animation;

    @Override
    public void onPause() {
		Searcher.clear();
		amb.done();
        amb.finish();
        super.onPause();
		//State.setStateLastKnownPosition(State.SECTION_FILE_EXPLORE, list);

    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity=(AppCompatActivity) getActivity();
		view=inflater.inflate(R.layout.search_shortcut,container, false);
        animation= AnimationUtils.loadAnimation(activity, R.anim.slide_in_from_bottom);
		return view;
	}


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        //if(scrollState==2) {
        //	adapterImages.isFastScroll=true;
        //} else {

        if (list != null &&  adapter!=null) {
            //adapter.promptStateChange(view.getScrollY(), list);
            adapter.promptCacheChange(view.getScrollY(), list);
            adapter.notifyDataSetChanged();
        }
        //}

    }
    private long lastscroll;
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        long now= Cal.getUnixTime();

        if(now-lastscroll>100) {
            if(list!=null && adapter!=null) {
                //adapterImages.promptCacheChange(view.getScrollY(),listImages);
                //adapter.promptStateChange(view.getScrollY(), listImages);
            }

        }

        if(now-lastscroll<15) {
            adapter.isFastScroll=true;
        } else {
            adapter.isFastScroll=false;
        }
        //BLog.e("SCR SPEED: " + (now-lastscroll));
        lastscroll=now;

    }

	@Override
	public void onResume() {
		super.onResume();

		State.setCurrentSection(State.SECTION_SEARCH_SHORTCUT);

		String sob = State.getStateObjectString(State.SECTION_SEARCH_SHORTCUT,StateObject.STRING_BJSON_OBJECT);
		//Log.e("SOB","sob: "+sob );
		if(sob!=null) {
			packet = new SearchPacket(new JSONObject(sob));

		}
        list=(ListView) view.findViewById(R.id.search_list);
		foldersView = (LinearLayout) view.findViewById(R.id.search_list_folders);

		start = (View) view.findViewById(R.id.search_start);
		updating = (View) view.findViewById(R.id.search_updating);
		adapter=new SearchShortcutAdapter(activity,list);


		amb = new ActionModeBack(activity, activity.getResources().getString(R.string.label_search)
				,R.menu.search_shortcut
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
            ActionBarManager.setActionBarBackOnly(activity, activity.getResources().getString(R.string.label_search), R.menu.search_shortcut,amb);
        }
		//searchHeader=activity.findViewById(R.id.search_tools);
		//searchHeader.setVisibility(View.GONE);
		loading = activity.findViewById(R.id.loading);
		if(packet!=null) {

			loading.setVisibility(View.VISIBLE);
			openSearchShort=new OpenSearchShort();
			openSearchShort.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);
		} else {
			refresh();
		}

		Fab.hide();


	}

	private OpenSearchShort openSearchShort;

	private class OpenSearchShort extends AsyncTask<Boolean, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Boolean... params) {

            if(packet.getInt(SearchPacket.INT_TYPE)==Files.CAT_ANY) {
                Searcher.doSearchFolderByCat(activity, packet.getInt(SearchPacket.INT_TYPE), null);
            } else {

                Searcher.doSearchShortcut(activity, packet.getInt(SearchPacket.INT_TYPE), null);

                folders = IndexerDb.getDb().getFoldersByCategory(packet.getInt(SearchPacket.INT_TYPE),0,30);
            }


			//BLog.e("FOLDERS GOT: "+folders.size());
			return Boolean.TRUE;

		}
		@Override
		protected void onPostExecute(Boolean result) {
			//ImageView searchShortImg = (ImageView) searchHeader.findViewById(R.id.search_short_img);
			//TextView searchShortText = (TextView) searchHeader.findViewById(R.id.search_short_text);
			//searchShortText.setText(packet.getString(SearchPacket.STRING_TERM));
			//searchShortImg.setImageDrawable(getResources().getDrawable(packet.getInt(SearchPacket.INT_ICON)));

			updating.setVisibility(View.GONE);
			loading.setVisibility(View.GONE);
			refresh();
		}

	}

	public void refreshData() {
		
	}
	public void refresh() {

		//BriefMenu.hideMenu();
		//ActionBarManager.setActionBarBackOnly(getActivity(), activity.getResources().getString(R.string.label_search), R.menu.accounts);
		adapter.notifyDataSetInvalidated();

        list.setAdapter(adapter);
        list.setOnItemClickListener(openListener);
		//State.setStateLastKnownPosition(State.SECTION_FILE_EXPLORE, list);
        list.setSelection(State.getFolderPosition("searcher"));
        amb.mMode.setTitle(packet.getString(SearchPacket.STRING_TERM));


        foldersView.removeAllViews();
        if(folders!=null && !folders.isEmpty()) {
            LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int size = Functions.dpToPx(40, activity);
            int tsize = Functions.dpToPx(90, activity);
            LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(tsize, LinearLayout.LayoutParams.WRAP_CONTENT);
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(size, size);
            for (final IndexerFile ifile : folders) {
                LinearLayout rlay = new LinearLayout(activity);
                rlay.setOrientation(LinearLayout.VERTICAL);
                rlay.setGravity(Gravity.CENTER_HORIZONTAL);
                rlay.setLayoutParams(rlp);

                ImageView img = new ImageView(activity);
                img.setLayoutParams(lp);
                img.setImageDrawable(getResources().getDrawable(Files.getFoldersRIcon(ifile.getInt(IndexerFile.INT_CATEGORY))));

                TextView txtx = new TextView(activity);
                txtx.setPadding(3, 0, 3, 0);
                txtx.setLayoutParams(tlp);
                txtx.setTextSize(11f);
                txtx.setGravity(Gravity.CENTER_HORIZONTAL);
                txtx.setText(Sf.restrictLength(ifile.getString(IndexerFile.STRING_FILENAME), 12));
                rlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //amb.done();
                        //State.sectionsClearBackstack();
                        BLog.e(ifile.absoluteFile() + " --- " + ifile.getString(IndexerFile.STRING_FILENAME));
                        State.addToState(State.SECTION_FILE_EXPLORE, new StateObject(StateObject.STRING_FILE_PATH, ifile.absoluteFile()));
                        //Bgo.openFragmentBackStack(activity,new FileExploreFragment());
                        Bgo.goPreviousFragment(activity);
                    }
                });

                rlay.addView(img);
                rlay.addView(txtx);
                foldersView.addView(rlay);
            }
            foldersView.setVisibility(View.VISIBLE);
            foldersView.setAnimation(animation);
            foldersView.startAnimation(animation);
        } else {
            foldersView.setVisibility(View.GONE);
        }
		//Device.setKeyboard(activity,searchText, true);
		//view.requestFocus();
	}





    public OnItemClickListener openListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {
            IndexerFile ind =Searcher.getResults().get(position);

            if(ind!=null) {
                State.setFolderPosition("searcher", list.getFirstVisiblePosition());
                File f = ind.getAsFileItem().getAbsoluteFile();
                if(f.isFile()) {
                    if(Files.isImage(Files.removeBriefFileExtension(f.getName()))) {
                        FileManagerList fml = new FileManagerList(Searcher.getResultsFileItems());
                        fml.setStartAtPosition(position);
                        State.addCachedFileManager(fml);
                        Bgo.openFragmentBackStack(activity, new ImagesSliderFragment());

                    } else if(Files.isTextFile(f.getName())) {

                        //State.addCachedFileManager(fm);
                        State.addToState(State.SECTION_TEXT_FILE_VIEW,new StateObject(StateObject.STRING_FILE_PATH,ind.getAsFileItem().getAbsolutePath()));
                        Bgo.openFragmentBackStack(activity,new TextFileFragment());

                    } else {
                        //openOptions(f.getAbsolutePath());
                        if(Files.removeBriefFileExtension(f.getName()).endsWith(".zip")) {

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
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {

		MenuInflater minflater = activity.getMenuInflater();
		minflater.inflate(R.menu.basic, menu);

		//searchView.setSearchableInfo(
		//		searchManager.getSearchableInfo(activity.getComponentName()));


	}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean intercept=false;
        switch(item.getItemId()) {
            case R.id.action_search:
                //State.sectionsClearBackstack();

                Bgo.openFragmentBackStack(activity,new SearchFragment());
                intercept=true;
                break;


        }
        return intercept;
    }
}

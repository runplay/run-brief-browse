package run.brief.browse;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import run.brief.b.Bgo;
import run.brief.b.Device;
import run.brief.b.State;
import run.brief.b.StateObject;
import run.brief.b.bImageViewLoading;
import run.brief.b.fab.Fab;
import run.brief.settings.SettingsHomeTabbedFragment;
import run.brief.util.Cal;
import run.brief.util.Files;
import run.brief.util.Functions;
import run.brief.util.ImageCache;
import run.brief.util.Sf;
import run.brief.util.explore.FileItem;
import run.brief.util.explore.ImagesSliderFragment;
import run.brief.util.explore.IndexerDb;
import run.brief.util.explore.IndexerFile;
import run.brief.util.explore.TextFileFragment;
import run.brief.util.explore.ZipExploreFragment;
import run.brief.util.explore.fm.FileManagerDisk;
import run.brief.util.explore.fm.FileManagerList;
import run.brief.util.explore.fm.FileManagerZip;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragmentOld extends Fragment implements OnScrollListener  {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    public ActionBarDrawerToggle mDrawerToggle;
    private Activity activity;

    public DrawerLayout mDrawerLayout;
    private View mDrawerView;
    private GridView mDrawerListView;

    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private List<IndexerFile> lastFiles=new ArrayList<IndexerFile>();

    public static final List<Holder> items = new ArrayList<Holder>();

    private GridView lastfiles;
    private LastFilesAdapter lastfilesadapter;




    public NavigationDrawerFragmentOld() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity=getActivity();
        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
        items.clear();
        items.add(new Holder(R.drawable.nav_btn_txt,activity.getString(R.string.text_files)));
        items.add(new Holder(R.drawable.nav_btn_videos,activity.getString(R.string.nav_videos)));
        items.add(new Holder(R.drawable.nav_btn_images,activity.getString(R.string.nav_images)));
        items.add(new Holder(R.drawable.nav_btn_download,activity.getString(R.string.nav_downloads)));
        items.add(new Holder(R.drawable.nav_btn_documents,activity.getString(R.string.nav_documents)));
        items.add(new Holder(R.drawable.nav_btn_music, activity.getString(R.string.nav_music)));


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDrawerView = inflater.inflate(R.layout.navigation_drawer, container, false);
        mDrawerListView=(GridView)mDrawerView.findViewById(R.id.nav_list);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mDrawerListView.setAdapter(new ItemsAdapter(getActivity()));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        //mDrawerView.setFitsSystemWindows(true);

        lastfiles = (GridView) mDrawerView.findViewById(R.id.nav_shortcuts);

        if(Device.isMediaMounted()) {
            IndexerDb.init(getActivity());

            new BuildLastFilesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);
            showDiskSizeDetails();
        }

        ImageView settings = (ImageView) mDrawerView.findViewById(R.id.btn_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout != null) {
                    mDrawerLayout.closeDrawer(mFragmentContainerView);
                }
                Bgo.openFragmentBackStack(activity, new SettingsHomeTabbedFragment());
            }
        });
        ImageView hdd = (ImageView) mDrawerView.findViewById(R.id.btn_nav_hdd);
        hdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout != null) {
                    mDrawerLayout.closeDrawer(mFragmentContainerView);
                }
                State.addToState(State.SECTION_FILE_EXPLORE,new StateObject(StateObject.STRING_FILE_PATH,"/"));
                Bgo.refreshCurrentFragment(activity);
            }
        });
        ImageView sdcard = (ImageView) mDrawerView.findViewById(R.id.btn_nav_sdcard);
        sdcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout != null) {
                    mDrawerLayout.closeDrawer(mFragmentContainerView);
                }
                State.addToState(State.SECTION_FILE_EXPLORE,new StateObject(StateObject.STRING_FILE_PATH,Files.getSDCardFilePath()));
                Bgo.refreshCurrentFragment(activity);
            }
        });
        //ImageView settings = (ImageView) mDrawerView.findViewById(R.id.btn_settings);



        return mDrawerView;
    }

    public void closeDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }
    private void showDiskSizeDetails() {

        double totalspace= Device.getSdSize();

        if(totalspace!=0) {
            int pctleft = Double.valueOf(100-((100/totalspace)*Device.getSdAvailable())).intValue();
            RelativeLayout rel = (RelativeLayout) mDrawerView.findViewById(R.id.nav_show_disk_stats);

            ProgressBar progressBar = (ProgressBar) rel.findViewById(R.id.nav_show_disk_progress);
            progressBar.setProgress(pctleft);

            TextView pct = (TextView) rel.findViewById(R.id.nav_show_disk_stats_total);
            pct.setText(pctleft+" %");
        }

    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;


        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //Fab.showHideNavClose();
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Fab.hide();
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }
                //mDrawerView.setVisibility(View.GONE);
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()

                reloadLastItems();
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            //mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            Log.e("Callback","callback navigation");
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            Log.e("Callback","callback navigation - attached");
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            //inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Log.e("TOOLBAR", "onOptionsItemSelected called");

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if(mDrawerLayout!=null)
            mDrawerLayout.bringToFront();
        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        //ActionBar actionBar = getActionBar();
        //actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        //actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {

        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }


    public class Holder {
        public Holder(int Rid,String txt) {this.Rid=Rid; this.txt=txt;}
        public int Rid;
        public String txt;

    }

    public class ItemsAdapter extends BaseAdapter {

        private Activity activity;

        private LayoutInflater inflater;



        public ItemsAdapter(Activity activity) {
            //activity = a;
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.activity=activity;



            //inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return items.size();
        }
        /*
           public static void setSelectedPersons(HashMap<String,Person> tos) {
               selectedPersons=tos;
           }
           */
        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view=convertView;

            if(view==null) {
                view = inflater.inflate(R.layout.navigation_drawer_item,parent,false);

            }


            TextView text=(TextView) view.findViewById(R.id.btn_nav_item_txt);
            ImageView img = (ImageView) view.findViewById(R.id.btn_nav_item_img);

            Holder it = items.get(position);
            if(it!=null) {
                text.setText(it.txt);
                img.setImageDrawable(activity.getResources().getDrawable(it.Rid));

                text.bringToFront();
            }

            return view;


        }

    }


    private class BuildLastFilesTask extends AsyncTask<Boolean, Void, Boolean> {
        List<IndexerFile> items;
        public void setData(File image, int position) {

        }
        @Override
        protected Boolean doInBackground(Boolean... params) {
            IndexerDb.init(getActivity());
            items = IndexerDb.getDb().getImageVideoItems(0, 10);
//Log.e("ITEMSSIZE","ITEMS SIZE : "+items.size());

            return Boolean.TRUE;

        }
        @Override
        protected void onPostExecute(Boolean result) {
            GridView lastfiles = (GridView) getActivity().findViewById(R.id.nav_shortcuts);
            lastfilesadapter = new LastFilesAdapter(getActivity(),lastfiles,items);
            lastfiles.setAdapter(lastfilesadapter);
            lastfiles.refreshDrawableState();
        }

    }

    private void reloadLastItems() {
        List<IndexerFile> items = IndexerDb.getDb().getImageVideoItems(0, 10);
        GridView lastfiles = (GridView) getActivity().findViewById(R.id.nav_shortcuts);

        lastfiles.setAdapter(new LastFilesAdapter(getActivity(),lastfiles,items));
        lastfiles.refreshDrawableState();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        lastfilesadapter.notifyDataSetChanged();

    }
    private long lastscroll;
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        long now= Cal.getUnixTime();

        //lastfilesadapter.notifyDataSetChanged();

        //BLog.e("SCR SPEED: " + (now-lastscroll));
        lastscroll=now;

    }

    public class LastFilesAdapter extends BaseAdapter {
        private Activity activity;


        //private final Uri thumb_IMAGE_URI = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        private int ypos;
        private int lastvis;

        private AbsListView.LayoutParams param;// = new LayoutParams(LayoutParams.MATCH_PARENT,240);
        private AbsListView.LayoutParams txtparams;// = new LayoutParams(LayoutParams.MATCH_PARENT,240);
        private AbsListView.LayoutParams txtppod;
        //private RelativeLayout.LayoutParams laycheck;
        //private int lastVisibilePos;
        private GridView parent;
        private List<IndexerFile> items;
        private int txtItemPadTop;

        private int imgWidth;
        private int imgHeight;
;        //public void setFirstLastVisPos(int first, int last) {
        //	firstVisibilePos=first;
        //	lastVisibilePos=last;
        //}
        public LastFilesAdapter(Activity c, GridView parent, List<IndexerFile> items) {
            this.activity = c;
            this.parent=parent;
            this.items=items;


            imgWidth=Functions.dpToPx(120,c);
            imgHeight=Functions.dpToPx(90,c);
            txtItemPadTop = Functions.dpToPx(70,c);

            param = new AbsListView.LayoutParams(imgWidth,imgHeight);
            txtparams = new AbsListView.LayoutParams(imgWidth,Functions.dpToPx(20,c));
            txtppod = new AbsListView.LayoutParams(imgWidth,imgHeight);

            //laycheck = new RelativeLayout.LayoutParams(65,60);
            //laycheck.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            //laycheck.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        }

        public int getCount() {
            return items.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            bImageViewLoading imageView;
            //ImageView chkImg;
            RelativeLayout lay;
            if (convertView == null) {
                lay=new RelativeLayout(activity);


                lay.setGravity(Gravity.CENTER_HORIZONTAL| Gravity.CENTER_VERTICAL);
                lay.setLayoutParams(param);


            } else {
                lay=(RelativeLayout) convertView;
                lay.removeAllViews();
            }

            imageView = new bImageViewLoading(activity);
            imageView.setId(R.id.file_images_image_view_id);
            //imageView = new ImageView(activity);

            //chkImg = new ImageView(activity);
            //chkImg.setId(R.id.file_images_image_check_id);
            //chkImg.setLayoutParams(laycheck);
            //chkImg.setPadding(10,10,15,10);
            //chkImg.

            FileItem f= items.get(position).getAsFileItem();


            if(f!=null) {
                //chkImg.setVisibility(View.VISIBLE);
                //if(f.getAbsoluteFile().isDirectory())
                //    chkImg.setVisibility(View.GONE);
                //imageView.setTag(f.getPath());
                //chkImg.setTag(""+position);
                lay.setTag("" + position);
                RelativeLayout rel=new RelativeLayout(activity);
                rel.setLayoutParams(txtppod);
                rel.setPadding(0, txtItemPadTop, 0, 0);

                TextView txt = new TextView(activity);
                txt.setLayoutParams(txtparams);
                txt.setText(Sf.shortenText(f.getName(), 26));

                txt.setBackgroundColor(activity.getResources().getColor(R.color.black_alpha_strong));
                txt.setTextColor(activity.getResources().getColor(R.color.grey));
                txt.setPadding(8, 2, 4, 8);
                //txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);



                rel.setId(R.id.file_images_text_view_id);
                rel.addView(txt);

                //imageView.setImageBitmap(null);
                //imageView.setImageDrawable(null);
                if(Files.isImage(f.getName())) {

                    ImageCache.CacheBitmap cb = ImageCache.get(f.getPath());

                    if(cb==null) {
                        LoadImageTask loadImage = new LoadImageTask();
                        loadImage.setData(f, position);
                        loadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);
                        imageView.setImageDrawable(activity.getResources().getDrawable(f.icon));
                        imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setPadding(8, 8, 8, 8);
                    } else {
                        imageView.setImageBitmap(cb.bitmap);
                        imageView.setLayoutParams(new GridView.LayoutParams(imgWidth, imgHeight));
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setPadding(0, 0, 0, 0);
                    }
                } else {
                    //FileItem fi= fm.getDirectoryItem(position);
                    imageView.setImageDrawable(activity.getResources().getDrawable(f.icon));
                    imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setPadding(8, 8, 8, 8);
                }

                lay.addView(imageView);
                //lay.addView(chkImg);
                lay.addView(rel);

                View.OnClickListener lastItemListner = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = Sf.toInt(view.getTag().toString());
                        IndexerFile item = items.get(pos);
                        FileManagerDisk fm = new FileManagerDisk();
                        fm.setCurrentDirectory(activity, item.getString(IndexerFile.STRING_FILEPATH));
                        fm.readDirectory(activity);
                        File f =  item.getAsFileItem();
                        if(f.exists()) {
                            if (Files.isImage(item.getString(IndexerFile.STRING_FILENAME))) {

                                List<FileItem> its = new ArrayList<FileItem>();
                                its.add(item.getAsFileItem());
                                FileManagerList fml = new FileManagerList(its);
                                fml.setStartAtPosition(0);
                                State.addCachedFileManager(fml);
                                Bgo.openFragmentBackStack(activity, new ImagesSliderFragment());

                            } else if (Files.isTextFile(item.getString(IndexerFile.STRING_FILENAME))) {
                                State.addCachedFileManager(fm);
                                State.addToState(State.SECTION_TEXT_FILE_VIEW, new StateObject(StateObject.STRING_FILE_PATH, f.getAbsolutePath()));
                                Bgo.openFragmentBackStack(activity, new TextFileFragment());
                            } else {
                                if (Files.removeBriefFileExtension(f.getName()).endsWith(".zip")) {

                                    FileManagerZip fmz = new FileManagerZip(f.getAbsolutePath());
                                    State.addCachedFileManager(fmz);
                                    Bgo.openFragmentBackStack(activity, new ZipExploreFragment());

                                } else {
                                    Device.openAndroidFile(activity, f);
                                }
                            }
                        }

                        if (mDrawerLayout != null) {
                            mDrawerLayout.closeDrawer(mFragmentContainerView);
                        }
                    }
                };
                lay.setOnClickListener(lastItemListner);

            }

            return lay;
        }

        private synchronized void refreshIfVisible(File image, int pos) {

            if(parent!=null) {
                RelativeLayout view = (RelativeLayout) parent.getChildAt(pos);
                if(view!=null) {
                    parent.childDrawableStateChanged(view);
                    //BLog.e("REFR", "refresh "+image.getPath());
                    //view=(RelativeLayout)this.getView(pos, view, parent);

                    //parent.invalidate();

                }

            }

        }
        private synchronized void refreshIfVisibleOld(File image, int pos) {

            if(parent!=null) {
                //int first=parent.getFirstVisiblePosition();
                //int last=parent.getLastVisiblePosition();
                RelativeLayout view = (RelativeLayout) parent.getChildAt(pos);
                if(view!=null) {
                    //BLog.e("REFR", "refresh "+image.getPath());
                    view=(RelativeLayout)this.getView(pos, view, parent);
                    view.refreshDrawableState();
                    //parent.invalidate();
                }
            }

        }

        private class LoadImageTask extends AsyncTask<Boolean, Void, Boolean> {
            private File image;
            private int pos;
            public void setData(File image, int position) {
                this.image=image;
                pos=position;
            }
            @Override
            protected Boolean doInBackground(Boolean... params) {
                //BLog.e("POINT", "1");
                ImageCache.CacheBitmap cb = ImageCache.get(image.getPath());
                if(cb==null) {
                    cb=ImageCache.getNewCacheBitmap();
                    cb.status=ImageCache.CACHE_B_LOADING;
                    ImageCache.put(image.getPath(), cb);
                    Bitmap bitmap=getThumbnail(image.getPath());
                    if(bitmap==null) {
                        if(image.isDirectory()) {
                            // TODO
                        } else {
                            bitmap = getPreview(image);
                        }

                    }
                    cb.bitmap=bitmap;
                    cb.status=ImageCache.CACHE_B_LOADED;
                    ImageCache.putFinal(image.getPath(), cb);

                }
                return Boolean.TRUE;

            }
            @Override
            protected void onPostExecute(Boolean result) {
                //list
                lastfiles.refreshDrawableState();
                //lastfilesadapter.notifyDataSetChanged();
                //refreshIfVisible(image, pos);
            }

        }

        private Bitmap getPreview(File image) {
            //File image = new File(uri);

            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(image.getPath(), bounds);
            if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
                return null;

            int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
                    : bounds.outWidth;

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = originalSize / 150;

            Bitmap bm = BitmapFactory.decodeFile(image.getPath(), opts);


            return bm;

        }
        public Bitmap getThumbnail(String path) {
            ContentResolver cr= activity.getContentResolver();
            Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?", new String[] {path}, null);
            if (ca != null && ca.moveToFirst()) {
                int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
                ca.close();
                return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null );
            }
            if(ca!=null)
                ca.close();
            return null;

        }


    }
}

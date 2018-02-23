package run.brief.browse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import run.brief.b.Bgo;
import run.brief.b.BrowseService;
import run.brief.b.Device;
import run.brief.b.State;
import run.brief.b.StateObject;
import run.brief.beans.BriefSettings;
import run.brief.search.SearchFragment;
import run.brief.search.SearchPacket;
import run.brief.search.ShortcutSearchFragment;
import run.brief.secure.HomeFarm;
import run.brief.settings.SettingsDb;
import run.brief.util.ActionBarManager;
import run.brief.util.Cal;
import run.brief.util.Files;
import run.brief.util.explore.Indexer;
import run.brief.util.log.BLog;


public class Main extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    public static final String INTENT_DATE_STACKTRACE="HASCRASH";

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    private     AppCompatActivity activity;
    private boolean isCreateStart=true;
    private boolean isRestart=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Thread.setDefaultUncaughtExceptionHandler(new CatchCrash(this));
        ensureStartups(this);
        BriefSettings settings = State.getSettings();
        if(settings!=null && settings.getBoolean(BriefSettings.BOOL_STYLE_DARK)==Boolean.FALSE) {
            setTheme(R.style.AppThemeLight);
            //Log.e("THEME","Theme is LIGHT");
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        activity = this;

        //getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        State.loadState(savedInstanceState);

        Log.e("DEVICE", "SDK :" + android.os.Build.VERSION.SDK_INT);
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }


        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.icon);
        ab.setDisplayHomeAsUpEnabled(true);

        ActionBarManager.restart(this);

        checkSdCard();


        Device.hideKeyboard(this);

        Bgo.clearBackStack(activity);

        if (!BrowseService.isBrowseServiceRunning(activity)) {
            //Log.e("SERV", "Starting Browse service");
            Intent service = new Intent(activity, BrowseService.class);
            activity.startService(service);

        }
        BrowseService.setIsAppStarted(true);
    }

    public static void ensureStartups(Context context) {
        HomeFarm.init(context);
        Files.setAppHomePath(context);
        SettingsDb.init();
        State.setSettings(SettingsDb.getSettings());
        Device.init(context);
    }
    @Override
    public void onRestart() {
        //BLog.e("SAVE", "RESTART instance state");
        super.onRestart();
        isRestart=true;

    }
    @Override
    public void onStop() {
        super.onStop();
        BrowseService.setIsAppStarted(false);
    }
    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        //Uri data = intent.getData();
        Bundle data=intent.getBundleExtra(INTENT_DATE_STACKTRACE);
        if (data != null) {
            //BLog.e("COMPOSE", " -- " + data.toString());
            //String launchUri = data.toString();
            Toast.makeText(this,"App Caught crash",Toast.LENGTH_LONG);

        }





        String locale = getResources().getConfiguration().locale.getDisplayCountry();
        //Log.e("LOCALE", "Main.onResume() : " + locale);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        //this.getActionBar().getCustomView().setFitsSystemWindows(true);
        if(isCreateStart) {
            //Log.e("MAIN", "CREATE START tested as TRUE !!");
            if(Device.isMediaMounted()) {
                Bgo.openCurrentState(this);
            } else {
                checkSdCard();
            }

        } else if(isRestart) {
            //Log.e("MAIN", "RESTART tested as TRUE !!");
            //throw new NullPointerException("Manually created exception");
            //Bgo.openCurrentState(this);
        }
        isCreateStart=false;
        isRestart=false;


    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        //BLog.e("SAVE", "RESTORE instance state");
        isCreateStart=true;
        Bgo.clearBackStack(activity);
        State.sectionsClearBackstack();
        State.loadState(savedInstanceState);
        //isRestart=true;
        Device.hideKeyboard(this);
        //Device.updateRotation(this);


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //BLog.e("SAVE", "SAVE instance state");
        outState.clear();
        State.saveState(outState);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        SearchPacket packet = null;
        String gofilepath=null;

        //ActionModeBack.isActionModeShowing=false;
        //Log.e("Nav", "Nav drawer selection : " + position);
        if(!NavigationDrawerFragment.items.isEmpty()) {

            NavigationDrawerFragment.Holder navitem = NavigationDrawerFragment.items.get(position);
            //Log.e("Nav","Nav drawer selection : "+navitem.Rid);
            switch (navitem.Rid) {
                case R.drawable.nav_btn_txt:
                    packet = new SearchPacket(Files.CAT_TEXTFILE, R.drawable.nav_btn_txt, activity.getString(R.string.text_files));
                    State.addToState(State.SECTION_SEARCH_SHORTCUT, new StateObject(StateObject.STRING_BJSON_OBJECT, packet.toString()));
                    //BLog.e("gofilepath: " + gofilepath);
                    break;
                case R.drawable.nav_btn_videos:
                    packet = new SearchPacket(Files.CAT_VIDEO, R.drawable.nav_btn_videos, activity.getString(R.string.nav_videos));
                    State.addToState(State.SECTION_SEARCH_SHORTCUT, new StateObject(StateObject.STRING_BJSON_OBJECT, packet.toString()));
                    break;
                case R.drawable.nav_btn_images:
                    packet = new SearchPacket(Files.CAT_IMAGE, R.drawable.nav_btn_images, activity.getString(R.string.nav_images));
                    State.addToState(State.SECTION_SEARCH_SHORTCUT, new StateObject(StateObject.STRING_BJSON_OBJECT, packet.toString()));
                    break;
                case R.drawable.nav_btn_download:
                    gofilepath =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                    if(gofilepath==null) {
                        File f= new File(Files.SDCARD_PATH+File.separator+ Indexer.downloadFolder);
                        if(f.exists()) {
                            gofilepath=f.getAbsolutePath();
                        } else {
                            //BLog.e("gopacket !!!!.......................... ");
                            packet = new SearchPacket(Files.CAT_ANY, R.drawable.nav_btn_download, activity.getString(R.string.useful_folders));
                            State.addToState(State.SECTION_SEARCH_SHORTCUT, new StateObject(StateObject.STRING_BJSON_OBJECT, packet.toString()));
                        }
                    }

                    BLog.e("gofilepath: " + gofilepath);
                    //packet =  new SearchPacket(Files.,"");
                    //State.addToState(State.SECTION_SEARCH_SHORTCUT,new StateObject(StateObject.STRING_BJSON_OBJECT,packet.toString()));
                    break;
                case R.drawable.nav_btn_documents:
                    packet = new SearchPacket(Files.CAT_DOCUMENT, R.drawable.nav_btn_documents, activity.getString(R.string.nav_documents));
                    State.addToState(State.SECTION_SEARCH_SHORTCUT, new StateObject(StateObject.STRING_BJSON_OBJECT, packet.toString()));
                    break;
                case R.drawable.nav_btn_music:
                    packet = new SearchPacket(Files.CAT_SOUND, R.drawable.nav_btn_music, activity.getString(R.string.nav_music));
                    State.addToState(State.SECTION_SEARCH_SHORTCUT, new StateObject(StateObject.STRING_BJSON_OBJECT, packet.toString()));
                    break;
            }
        }
        if(packet!=null)
            Bgo.openFragmentBackStack(activity, new ShortcutSearchFragment());
        if(gofilepath!=null) {
            mNavigationDrawerFragment.closeDrawer();
            //State.clearStateObjects(State.SECTION_FILE_EXPLORE);
            State.addToState(State.SECTION_FILE_EXPLORE, new StateObject(StateObject.STRING_FILE_PATH, gofilepath));
            Bgo.refreshCurrentFragment(activity);
            //Bgo.openFragment(activity, FileExploreFragment.class);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Log.e("SELECT","on create options called");
        if (!mNavigationDrawerFragment.isDrawerOpen()) {

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //BLog.e("onOptionsItemSelected called: "+item.getItemId());
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case R.id.action_search:
                Bgo.clearBackStack(activity);
                Bgo.openFragmentBackStack(activity,new SearchFragment());
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private long closebackpressed;
    private Handler closehandler;
    private Runnable closerun = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(activity, "Press back again to exit\nOr click the arrow to go up folder", Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    public void onBackPressed() {
        ActionBarManager.setStopOptions(true);
        //Log.e("BACK PRESS", "item: " + State.getSectionsSize() + " -- " + (State.getCurrentSection() != State.SECTION_FILE_EXPLORE));
        if(State.getSectionsSize()<2) {
            //if(State.getCurrentSection()!=State.SECTION_FILE_EXPLORE)
            //    Bgo.openFragment(this, new FileExploreFragment());
            //else {
            if(Cal.getUnixTime()-closebackpressed<700) {

                Bgo.clearBackStack(this);

                State.clearStateAllObjects();
                if(closehandler!=null)
                    closehandler.removeCallbacks(closerun);
                //BLog.e("EXIT", "APP");
                //this.get
                super.onBackPressed();
            } else{
                closebackpressed=Cal.getUnixTime();
                closehandler=new Handler();
                closehandler.postDelayed(closerun, 750);

            }
            //}
        } else {
            Bgo.goPreviousFragment(this);
        }

    }

    private static Handler checkMediaMountedHandler;
    private void checkSdCard() {
        View v = activity.findViewById(R.id.main_no_sd_card);
        v.setVisibility(View.GONE);
        //v.setX(2000F);
        v.findViewById(R.id.main_no_sd_card_text).setVisibility(View.GONE);
        //if(v!=null) {
        if(Device.isMediaMounted()) {

            //BriefMenu.showActionBar();
        } else {
            //BLog.e("SDCARD IS DIABLED !!!!!!!!!!!!!");
            v.setVisibility(View.VISIBLE);
            //BriefMenu.hideActionBar();
            v.bringToFront();
            v.findViewById(R.id.main_no_sd_card_text).setVisibility(View.VISIBLE);
        }

        //}
        if(checkMediaMountedHandler==null)
            checkMediaMountedHandler=new Handler();
        checkMediaMountedHandler.removeCallbacks(runSdCard);
        checkMediaMountedHandler.postDelayed(runSdCard,3000);
    }
    private Runnable runSdCard = new Runnable() {
        @Override
        public void run() {
            checkSdCard();
        }
    };

}

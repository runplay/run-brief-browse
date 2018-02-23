package run.brief.settings;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

import java.util.HashMap;

import run.brief.b.BFragment;
import run.brief.b.BRefreshable;
import run.brief.b.Bgo;
import run.brief.b.State;
import run.brief.b.StateObject;
import run.brief.b.fab.Fab;
import run.brief.browse.R;
import run.brief.util.ActionBarManager;
import run.brief.util.explore.ActionModeBack;
import run.brief.util.explore.ActionModeCallback;


public class SettingsHomeTabbedFragment extends BFragment implements BRefreshable, TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
 
    private TabHost mTabHost;
    private ViewPager mViewPager;
    private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabInfo>();
    private SettingsHomeTabbedAdapter sMessageAdapter;

    //Bundle savedInstanceState;
    private FragmentActivity activity;

    private View view;
    boolean isInit=false;


    private class TabInfo {
         private String tag;
         private Class<?> clss;
         private Bundle args;
         private Fragment fragment;
         TabInfo(String tag, Class<?> clazz,Bundle args) {
             this.tag = tag;
             this.clss = clazz;
             this.args = args;
         }

    }

    class TabFactory implements TabContentFactory {

        private final Context mContext;

        /**
         * @param context
         */
        public TabFactory(SettingsHomeTabbedFragment context) {
            mContext = context.getActivity();
        }
        /** (non-Javadoc)
         * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
         */
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }

    }
	public void refreshData() {

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		//frag=this;
		this.activity=(FragmentActivity) getActivity();
		//this.savedInstanceState=savedInstanceState;
		view=inflater.inflate(R.layout.settings_tabbed_frame,container, false);
		//startHandler.postDelayed(initialise, 50);
		return view;
	}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //BLog.e("OPTIONS", "onCreateOptionsMenu at new emai home");
        switch(item.getItemId()) {

        }
        return true;
    }
	@Override
	public void onResume() {
		super.onResume();
        //BLog.e("RESUME", "Settings resume");
		State.setCurrentSection(State.SECTION_SETTINGS);
        Fab.hide();
        refresh();

	}
    public void refresh() {
        amb= new ActionModeBack(activity, activity.getResources().getString(R.string.action_settings)
                ,R.menu.settings
                , new ActionModeCallback() {
            @Override
            public void onActionMenuItem(ActionMode mode, MenuItem item) {
                //Log.e("AMB", "menuitem actionmodeback: " + mode);
                Bgo.goPreviousFragment(activity);
            }
        });
        if(android.os.Build.VERSION.SDK_INT>= 19) {

            ActionBarManager.setActionBarBackV19((AppCompatActivity) activity, amb);
            //setActionBarBackV19();
        } else {
            ActionBarManager.setActionBarBackOnly(activity, activity.getResources().getString(R.string.action_settings), R.menu.archive,amb);
        }
    	//BriefManager.clearController(activity);
		//ActionBarManager.setActionBarBackOnly(activity, activity.getResources().getString(R.string.action_settings),R.menu.settings);
        //BLog.e("RESUME", "Settings refresh");
        initialiseTabHost();
        intialiseViewPager();
        if (State.hasStateObject(State.SECTION_SETTINGS, StateObject.STRING_VALUE)) {
            //BLog.e("SETTAG",""+State.getStateObjectString(State.SECTION_SETTINGS,StateObject.STRING_VALUE));
            mTabHost.setCurrentTabByTag(State.getStateObjectString(State.SECTION_SETTINGS, StateObject.STRING_VALUE));
        }
        //if (savedInstanceState != null) {
        //    mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        //}

    }

	@Override
	public void onPause() {
		super.onPause();
        //BLog.e("SET","pause called");


        //Bgo.removeFragmentFromFragmentManager(activity);
        State.addToState(State.SECTION_SETTINGS, new StateObject(StateObject.STRING_VALUE, mTabHost.getCurrentTabTag()));
        mTabHost.setOnTabChangedListener(null);
        mTabHost.getTabWidget().removeAllViews();
        mTabHost.clearAllTabs();
        mTabHost.setEnabled(false);
        mTabHost=null;

        mViewPager.removeAllViews();
        mViewPager.setOnPageChangeListener(null);
        mViewPager=null;

        sMessageAdapter.notifyDataSetChanged();
        sMessageAdapter=null;

        //tabInfo=null;
        mapTabInfo = new HashMap<String, TabInfo>();
        if(amb!=null)
            amb.done();
        //Bgo.removeFragmentFromFragmentManager(activity,SettingsGeneralFragment.class.getName());
        //Bgo.removeFragmentFromFragmentManager(activity,SettingsHomeTabbedFragment.class.getName());
        //outState.putString("tab", mTabHost.getCurrentTabTag());
	}
	@Override
	public void onStop() {
		super.onStop();

	}



    private void intialiseViewPager() {

        sMessageAdapter  = new SettingsHomeTabbedAdapter(activity.getSupportFragmentManager());
        //
        mViewPager = (ViewPager)view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(sMessageAdapter);
        mViewPager.setOnPageChangeListener(this);
    }

    /**
     * Initialise the Tab Host
     */
    private void initialiseTabHost() {
        //BLog.e("SETTAG","INIT TAB HOST");
        mTabHost = (TabHost)view.findViewById(android.R.id.tabhost);

        mTabHost.setup();
        mTabHost.clearAllTabs();
        mTabHost.setEnabled(true);
        TabInfo tabInfo = null;
        //BriefMessage.
//        SpannableString briefStr= B.getStyledWithTypeFaceName(activity, activity.getResources().getString(R.string.title_brief), State.getSettings().getString(BriefSettings.STRING_THEME),1f);
        SettingsHomeTabbedFragment.AddTab(this, this.mTabHost, mTabHost.newTabSpec(activity.getResources().getString(R.string.settings_tab1)).setIndicator(activity.getResources().getString(R.string.settings_tab1)), (tabInfo = new TabInfo(activity.getResources().getString(R.string.settings_tab1), SettingsAboutFragment.class, new Bundle())));
        mapTabInfo.put(tabInfo.tag, tabInfo);
        SettingsHomeTabbedFragment.AddTab(this, this.mTabHost, mTabHost.newTabSpec(activity.getResources().getString(R.string.settings_tab2)).setIndicator(activity.getResources().getString(R.string.settings_tab2)), (tabInfo = new TabInfo(activity.getResources().getString(R.string.settings_tab2), SettingsGeneralFragment.class, new Bundle())));
        mapTabInfo.put(tabInfo.tag, tabInfo);
        SettingsHomeTabbedFragment.AddTab(this, this.mTabHost, mTabHost.newTabSpec(activity.getResources().getString(R.string.settings_tab3)).setIndicator(activity.getResources().getString(R.string.settings_tab3)), (tabInfo = new TabInfo(activity.getResources().getString(R.string.settings_tab3), SettingsAboutFragment.class, new Bundle())));
        mapTabInfo.put(tabInfo.tag, tabInfo);

        mTabHost.getTabWidget().getChildAt(0).setBackground(activity.getResources().getDrawable(R.drawable.tab_bg_selector));
        mTabHost.getTabWidget().getChildAt(1).setBackground(activity.getResources().getDrawable(R.drawable.tab_bg_selector));
        mTabHost.getTabWidget().getChildAt(2).setBackground(activity.getResources().getDrawable(R.drawable.tab_bg_selector));
        mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_bg_divider);

        //TextView x2 = (TextView) mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        //TextView x3 = (TextView) mTabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);

        mTabHost.setOnTabChangedListener(this);
    }


    private static void AddTab(SettingsHomeTabbedFragment activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
        // Attach a Tab view factory to the spec
        tabSpec.setContent(activity.new TabFactory(activity));
        tabHost.addTab(tabSpec);
    }

    /** (non-Javadoc)
     * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
     */
    public void onTabChanged(String tag) {
        int position = mTabHost.getCurrentTab();
        mViewPager.setCurrentItem(position);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
            int positionOffsetPixels) {
        // TODO Auto-generated method stub
 
    }

    @Override
    public void onPageSelected(int position) {
        // TODO Auto-generated method stub
    	synchronized(this) {
    		mTabHost.setCurrentTab(position);

    	}
    }
 
    /* (non-Javadoc)
     * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged(int)
     */
    @Override
    public void onPageScrollStateChanged(int state) {
        // TODO Auto-generated method stub
 
    }
}
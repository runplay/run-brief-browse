package run.brief.util;


import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import run.brief.browse.R;
import run.brief.util.explore.ActionModeBack;

//import android.view.ActionMode;


public class ActionBarManager {
	private static final ActionBarManager ACT=new ActionBarManager();
	private static List<bMenuItem> MENU_ITEMS=new ArrayList<bMenuItem>();
	private static Menu menuTabs;

    private ColorDrawable backgroundAlpha50;
    private ColorDrawable backgroundAlpha100;

    private View actionbarTitle;
    private TextView actionbarTitleText;
    Activity activity;
    private boolean stopOtions=false;

    public static boolean stopOptions() {
        if(ACT.stopOtions) {
            ACT.stopOtions=false;
            return true;
        }
        return false;
    }
    public static void setStopOptions(boolean stop) {
        ACT.stopOtions=stop;
    }
	//private static ActionMode mode;

    public static void restart(Activity activity) {
        ACT.activity=activity;
        ACT.actionbarTitle=null;
        ACT.actionbarTitleText=null;
    }
    public static ColorDrawable getAlphaBackground100(Activity activity) {
        if(ACT.backgroundAlpha100 ==null) {
            ACT.backgroundAlpha100 = new ColorDrawable(activity.getResources().getColor(R.color.browse_brand));
            ACT.backgroundAlpha100.setAlpha(0);
        }
        return ACT.backgroundAlpha100;
    }
    public static ColorDrawable getAlphaBackground50(Activity activity) {
        if(ACT.backgroundAlpha50 ==null) {
            ACT.backgroundAlpha50 = new ColorDrawable(activity.getResources().getColor(R.color.browse_brand));
            ACT.backgroundAlpha50.setAlpha(80);
        }
        return ACT.backgroundAlpha50;
    }
    private static ColorDrawable getBackground(Activity activity,int Rcolor) {
        ColorDrawable cd= new ColorDrawable(activity.getResources().getColor(Rcolor));
        return cd;

    }



	public static Menu getMenu() {
		return menuTabs;
	}
	
	public class bMenuItem {
		int rMenu;
		String title;
		int STATE_SECTION_;
		Drawable img;
		String subtext;
		public bMenuItem(int rMenu, int STATE_SECTION_, String title, Drawable img, String subtext) {
			this.rMenu=rMenu; this.title=title; this.STATE_SECTION_=STATE_SECTION_; this.img=img; this.subtext=subtext;
		}
	}
	public bMenuItem makeMenu(int rMenu, int STATE_SECTION_, String title, Drawable img, String subtext) {
		return new bMenuItem(rMenu,STATE_SECTION_,title, img, subtext);
	}

	
	private int CURRENT=-1;
	
	public static int getCurrent() {
		return ACT.CURRENT;
	}


	public static void setActionBarBackOnly(Activity activity, String title, int R_MENU_ ,ActionModeBack amb) {
		setActionBarBackOnly(activity, title, R_MENU_, R.color.actionbar_basic,amb);
	}

    public static void setActionBarBackOnlyTransparent(Activity activity, String title, int R_MENU_,ActionModeBack amb) {
        setActionBarBackOnly(activity, title, R_MENU_, getAlphaBackground100(activity),amb);
        //showActionBarUnderlayer(activity);
    }
    public static void setActionBarBackOnlyTransparentNoUnderlay(Activity activity, String title, int R_MENU_,ActionModeBack amb) {
        setActionBarBackOnly(activity, title, R_MENU_, getAlphaBackground100(activity),amb);
        //hideActionBarUnderlayer(activity);
    }
    public static void setActionBarBackOnlyTransparent50NoUnderlay(Activity activity, String title, int R_MENU_,ActionModeBack amb) {
        setActionBarBackOnly(activity, title, R_MENU_, getAlphaBackground50(activity),amb);
        //hideActionBarUnderlayer(activity);
    }

    public static void setActionBarBackOnly(Activity activity, String title, int R_MENU_, int Rcolor,ActionModeBack amb) {
        setActionBarBackOnly(activity, title, R_MENU_,getBackground(activity, Rcolor),amb);
        //showActionBarUnderlayer(activity);
    }

    public static void hide(AppCompatActivity activity) {
        ActionBar ab = activity.getSupportActionBar();
        ab.hide();
    }
    public static void show(AppCompatActivity activity) {
        ActionBar ab = activity.getSupportActionBar();
        ab.show();
    }
    public static void setActionBarBackV19(AppCompatActivity activity,ActionModeBack amb) {
        ActionBar ab = activity.getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(false);
        ab.show();
    }


	private static void setActionBarBackOnly(Activity activity, String title, int R_MENU_, ColorDrawable color,ActionModeBack amb ) {
		//ACT.CURRENT= R_MENU_;
        ActionBar ab = ((AppCompatActivity) activity).getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(false);
        ab.show();

	}



	public static void setActionBarBackOnlyWithLogo(final Activity activity,int Rdrawable, String title, int R_MENU_, int Rcolor) {
		ACT.CURRENT= R_MENU_;
		final AppCompatActivity apact = (AppCompatActivity) activity;

		ActionBar actionBar = apact.getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        actionBar.setDisplayShowCustomEnabled(false);//.setDisplayShowTitleEnabled(true);

        actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(title);
        actionBar.setLogo(null);
        apact.supportInvalidateOptionsMenu();
        actionBar.invalidateOptionsMenu();
		actionBar.show();

	}









}

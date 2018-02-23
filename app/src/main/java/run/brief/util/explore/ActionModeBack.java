package run.brief.util.explore;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import run.brief.b.Bgo;

/**
 * Created by coops on 16/12/14.
 */
public class ActionModeBack implements ActionMode.Callback {

    private Activity activity;
    private boolean wipeonfinish=true;
    public ActionMode mMode;
    public boolean isActionModeShowing=false;

    //private List<MenuItem> menuitems = new ArrayList<MenuItem>();
    private Menu menu;
    private String title;
    private int useRmenu;
    ActionModeCallback callback;

    //private boolean mBackWasPressedInActionMode = false;

    public ActionModeBack(Activity activity, String title,int Rmenu, ActionModeCallback callback) {
        //BLog.e("ACTION MODE CALL NEW INSTANCE !!!!!!!");
        this.activity = activity;
        this.title=title;
        this.callback=callback;
        this.useRmenu=Rmenu;
        isActionModeShowing=true;
        mMode = ((AppCompatActivity)activity).startSupportActionMode(this);
        mMode.setTitle(title);

    }
    public void updateTitle() {

        mMode.setTitle(title);
    }

    public void done() {
        isActionModeShowing=false;

        //this.mMode.finish();
    }
    public void finish() {
        mMode.finish();
    }
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        //BLog.e("ACTION MODE CREATE !!!!!!!");
        if(this.menu==null) {
            this.menu = menu;
            MenuInflater mf = activity.getMenuInflater();
            mf.inflate(useRmenu, menu);
        }
        //isActionModeShowing=true;
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        //BLog.e("ACTION MODE PREPARE !!!!!!!");
        isActionModeShowing=true;
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        callback.onActionMenuItem(mode,item);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        //BLog.e("ACTION MODE onDestroyActionMode");
        if(isActionModeShowing) {
            //BLog.e("ACTION MODE BACK CALLING PREVIOUS FRAGMENT");
            Bgo.goPreviousFragment(activity);
        }

    }
}

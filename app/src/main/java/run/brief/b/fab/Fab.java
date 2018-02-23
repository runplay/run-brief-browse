package run.brief.b.fab;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;

import run.brief.browse.R;

/**
 * Created by coops on 08/08/15.
 */
public class Fab {

    private static FloatingActionButton fab;
    private static ArrayList<Boolean> lastKnownState=new ArrayList<Boolean>();
    public static void show() {
        //Validator.calldata();
        if(fab!=null) {
            addTrimLastKnownState(Boolean.TRUE);
            fab.show(true);

        }
    }
    public static void showHideNavClose() {
        if(fab!=null) {
            if (!lastKnownState.isEmpty() && lastKnownState.get(0) == Boolean.TRUE)
                show();
            else
                hide();
        }
    }
    public static void set(Activity activity, ListView list, ScrollDirectionListener scroll, View.OnClickListener click) {

        fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.hide(true);
        if(list==null) {

        } else if(scroll==null) {
            fab.attachToListView(list);
        } else {
            fab.attachToListView(list, new ScrollDirectionListener() {
                @Override
                public void onScrollDown() {
                    Log.d("ListViewFragment", "onScrollDown()");
                }

                @Override
                public void onScrollUp() {
                    Log.d("ListViewFragment", "onScrollUp()");
                }
            }, new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    Log.d("ListViewFragment", "onScrollStateChanged()");
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    Log.d("ListViewFragment", "onScroll()");
                }
            });
        }


        //fab.onsc
        fab.setOnClickListener(click);
        fab.bringToFront();
        //fab.setAnimation(new AnimatorSet());
        //fab.setVisibility(View.VISIBLE);
        //fab.show(true);
    }
    public static void hide() {
        if(fab!=null) {
            addTrimLastKnownState(Boolean.FALSE);
            fab.hide(true);
        }
    }
    public static void addTrimLastKnownState(Boolean bool) {
        lastKnownState.add(bool);
        if(lastKnownState.size()>2)
            lastKnownState.remove(0);
    }
}



/*

		fab = (FloatingActionButton) view.findViewById(R.id.fab);
		fab.attachToListView(list, new ScrollDirectionListener() {
			@Override
			public void onScrollDown() {
				Log.d("ListViewFragment", "onScrollDown()");
			}

			@Override
			public void onScrollUp() {
				Log.d("ListViewFragment", "onScrollUp()");
			}
		}, new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//Log.d("ListViewFragment", "onScrollStateChanged()");
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				//Log.d("ListViewFragment", "onScroll()");
			}
		});
		//fab.onsc
		fab.setOnClickListener(upDirectoryListener);
		fab.bringToFront();
 */

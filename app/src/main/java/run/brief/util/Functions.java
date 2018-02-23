package run.brief.util;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import run.brief.browse.R;

public class Functions {
	
	
	
    public static int dpToPx(int dp, Context ctx) {
    Resources r = ctx.getResources();
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
    
    public static void copyToClipFlashView(Activity activity, final View view) {
    	if(view!=null) {
    		Integer colorFrom = activity.getResources().getColor(R.color.copy_to_clip);
    		Integer colorTo = activity.getResources().getColor(R.color.transparent);
    		ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
    		colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

    		    @Override
    		    public void onAnimationUpdate(final ValueAnimator animator) {
    		        view.setBackgroundColor((Integer)animator.getAnimatedValue());
    		    }

    		});
    		colorAnimation.start();
    	}
    }
    
    
//originally: http://stackoverflow.com/questions/5418510/disable-the-touch-events-for-all-the-views
//modified for the needs here
	public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
	    int childCount = viewGroup.getChildCount();
	    for (int i = 0; i < childCount; i++) {
	        View view = viewGroup.getChildAt(i);
	        if(view.isFocusable())
	            view.setEnabled(enabled);
	        if (view instanceof ViewGroup) {
	            enableDisableViewGroup((ViewGroup) view, enabled);
	        } else if (view instanceof ListView) {
                if(view.isFocusable())
                    view.setEnabled(enabled);
                ListView listView = (ListView) view;
                int listChildCount = listView.getChildCount();
                for (int j = 0; j < listChildCount; j++) {
                    if(view.isFocusable())
                        listView.getChildAt(j).setEnabled(enabled);
                }
            }
        }
    }
	

}

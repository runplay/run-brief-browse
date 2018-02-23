package run.brief.util.explore;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by coops on 25/08/15.
 */
public class ImageSliderPager  extends ViewPager {

        private static boolean enabled;
    private int viewWidth;

        public ImageSliderPager(Context context, AttributeSet attrs) {
            super(context, attrs);


            enabled = true;
        }
        public void setViewWidth( int viewWidth) {
            this.viewWidth=viewWidth;
        }
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (enabled) {
                return super.onTouchEvent(event);
            }

            return false;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
            float x=event.getX();
            if(x>(viewWidth/4) && x< viewWidth-(viewWidth/4))
                ImageSliderPager.setPagingEnabled(false);
            else
                ImageSliderPager.setPagingEnabled(true);


            if (enabled) {
                return super.onInterceptTouchEvent(event);
            }

            return false;
        }

        private static void setPagingEnabled(boolean enabled) {
            enabled = enabled;
        }
}
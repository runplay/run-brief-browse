
package run.brief.b;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import java.lang.reflect.Method;


public final class B {

    public static final B THEME = new B();

    public static final int ACTIVITY_RESULT_SHOW_IMAGE_SLIDER=324452;

    public static final boolean LIVE_MODE = true;
    public static final boolean DEBUG = true;

    public static final String NAME="Brief";
    
    private static final FontSizes fontSizes = new FontSizes();

    public static double FONT_MEDIUM=1.5D;
    public static double FONT_LARGE=1.4D;
    public static double FONT_XLARGE=1.3D;
    public static double FONT_SMALL=2.3D;

    private Typeface typeface;
    private Typeface typefaceBold;
    private Bitmap resizedBitmap;
    private float defaultTextSize;

    public static void resetDefaultTextSize() {
        THEME.defaultTextSize=0;
    }


    public static final int APP_STAGE_FIRST_TIME=0;
    public static final int APP_STAGE_UNREGISTERED=1;
    public static final int APP_STAGE_REGISTERED=2;

    public static int getAppStage() {

        //File pemfile = new File(Files.HOME_PATH_APP+File.separator+)

        return APP_STAGE_FIRST_TIME;
    }
    public static AlphaAnimation animateAlphaFlash() {
        AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);
        animation1.setDuration(200);
        animation1.setStartOffset(50);
        animation1.setFillAfter(true);
        return animation1;
    }


    public static Method getMethod(Class<?> classObject, String methodName) {
        try {
            return classObject.getMethod(methodName, boolean.class);
        } catch (NoSuchMethodException e) {
            //Log.i(B.LOG_TAG, "Can't get method " +
            //      classObject.toString() + "." + methodName);
        } catch (Exception e) {
            //BLog.add("B() Error while using reflection to get method " + classObject.toString() + "." + methodName, e);
        }
        return null;
    }

    public static FontSizes getFontSizes() {
        return fontSizes;
    }
    public static void fixDrawableLevels(TextView textview) {
    	// Fix level of existing drawables
    	Drawable[] drawables = textview.getCompoundDrawables();
    	for (Drawable d : drawables) if (d != null && d instanceof ScaleDrawable) d.setLevel(1);
    	textview.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

}

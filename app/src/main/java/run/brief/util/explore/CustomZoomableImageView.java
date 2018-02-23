package run.brief.util.explore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.WindowManager;
import android.widget.ImageView;

import run.brief.util.Cal;

/**
 * Created by coops on 20/08/15.
 */
public class CustomZoomableImageView  extends ImageView {
    private Paint borderPaint = null;
    private Paint backgroundPaint = null;

    private static final float minScale=0.1F;
    private static final float maxScale=6F;

    private float mPosX = 0f;
    private float mPosY = 0f;
    private float mPosXstart = 0f;
    private float mPosYstart = 0f;

    float pivotPointX = 0f;
    float pivotPointY = 0f;
    float pivotPointXstart = 0f;
    float pivotPointYstart = 0f;
    float scaleStart=0f;
    private long lastclick;


    private float mLastTouchX;
    private float mLastTouchY;
    private static final int INVALID_POINTER_ID = -1;
    private static final String LOG_TAG = "TouchImageView";

    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;

    private float viewWidth;
    private float viewHeight;
    private float bmpWidth;
    private float bmpHeight;
    private boolean isPortrait=true;
    private boolean isPortraitImage=true;
    private final int borderWidth=0;

    public CustomZoomableImageView(Context context) {
        this(context, null, 0);
    }

    public CustomZoomableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    Matrix matrix=new Matrix();

    // Existing code ...
    public CustomZoomableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // Create our ScaleGestureDetector
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        borderPaint = new Paint();
        borderPaint.setARGB(255, 255, 128, 0);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4);

        backgroundPaint = new Paint();
        backgroundPaint.setARGB(32, 255, 255, 255);
        backgroundPaint.setStyle(Paint.Style.FILL);

    }

    private void revertStartValues() {
        float startMid = scaleStart*1.4f;
        float startMax = scaleStart*1.8f;
        if(mScaleFactor>=startMax) {
            mScaleFactor=scaleStart;
        } else if(mScaleFactor>=startMid) {
            mScaleFactor=startMax;
        } else if(mScaleFactor==scaleStart) {
            mScaleFactor=startMid;
        } else {
            mScaleFactor=scaleStart;
        }
        //int borderWidth = (int) borderPaint.getStrokeWidth();
        pivotPointX = ((viewWidth - borderWidth) - (int) (bmpWidth * mScaleFactor)) / 2;
        pivotPointY = ((viewHeight - borderWidth) - (int) (bmpHeight * mScaleFactor)) / 2;
        //pivotPointY=-pivotPointY;
        mPosX=mPosXstart;//pivotPointX;
        mPosY=mPosYstart;//-(pivotPointY/2);
        //if((isPortraitImage&&isPortrait)||(!isPortraitImage&&!isPortrait))
        //    mPosY=-((pivotPointY/2)+(pivotPointY/4));
        //pivotPointY=pivotPointYstart;
        //pivotPointX=pivotPointXstart;
        //mPosY=mPosYstart;
        //mPosX=mPosXstart;
        //mScaleFactor=scaleStart;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                long cl = Cal.getUnixTime();
                if((cl-lastclick)<600) {
                    revertStartValues();
                    invalidate();

                }
                lastclick=cl;

                final float x = ev.getX();
                final float y = ev.getY();

                mLastTouchX = x;
                mLastTouchY = y;

                mActivePointerId = ev.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);


                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress()) {
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    mPosX += dx;
                    mPosY += dy;

                    invalidate();
                }

                mLastTouchX = x;
                mLastTouchY = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#draw(android.graphics.Canvas)
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //canvas.drawRect(0, 0, getWidth() - 1, getHeight() - 1, borderPaint);
        //canvas.translate(mPosX, mPosY);
        //BLog.e("STA55555555555555555RT --- SF: " + mPosX + ", PPX: " + mPosY);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //canvas.drawRect(0, 0, getWidth() - 1, getHeight() - 1, backgroundPaint);
        if (this.getDrawable() != null) {
            //canvas.save();
            canvas.translate(mPosX, mPosY);

            matrix.reset();



            matrix.postScale(mScaleFactor, mScaleFactor, pivotPointX, pivotPointY);
            fixTrans();
            //matrix.postScale(mScaleFactor, mScaleFactor, getWidth() / mScaleFactor, getHeight() / mScaleFactor);
            // canvas.setMatrix(matrix);
            //BLog.e("START --- SF: " + mScaleFactor + ", PPX: " + pivotPointX + ", PPY: " + pivotPointY);
            try{
                canvas.drawBitmap(
                        ((BitmapDrawable) this.getDrawable()).getBitmap(), matrix,  null);
            } catch (Exception e) {}
            // this.getDrawable().draw(canvas);
            //canvas.restore();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        if(bitmap!=null) {
            bmpWidth = bitmap.getWidth();
            bmpHeight = bitmap.getHeight();
            mLastTouchX = mPosX = 0;
            mLastTouchY = mPosY = 0;
        }
        //setWindowSize();
        super.setImageBitmap(bitmap);
    }



    @Override
    public void setImageDrawable(Drawable drawable) {
        // Constrain to given size but keep aspect ratio
        bmpWidth = drawable.getIntrinsicWidth();
        bmpHeight = drawable.getIntrinsicHeight();
        mLastTouchX = mPosX = 0;
        mLastTouchY = mPosY = 0;
        setWindowSize();

        super.setImageDrawable(drawable);
    }


    private void setWindowSize() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        viewWidth = size.x;
        viewHeight = size.y;


        if(viewWidth>viewHeight)
            isPortrait=false;
        else
            isPortrait=true;

        if(bmpWidth>bmpHeight)
            isPortraitImage=false;
        else
            isPortraitImage=true;

        //borderWidth = (int) borderPaint.getStrokeWidth();
        mScaleFactor = Math.min((viewWidth - borderWidth)
                / bmpWidth, (viewHeight - borderWidth)
                / bmpHeight);

        mScaleFactor = Math.max(minScale, mScaleFactor);
        mScaleFactor = Math.min(maxScale, mScaleFactor);

        if(isPortrait && !isPortraitImage) {
            mScaleFactor=mScaleFactor*1.4f;
        } else if(!isPortrait && isPortraitImage) {
            mScaleFactor=mScaleFactor*1.4f;
        }
        //BLog.e("firsttime scalefactor: : " + mScaleFactor);

        pivotPointX = ((viewWidth - borderWidth) - (int) (bmpWidth * mScaleFactor)) / 2;
        pivotPointY = ((viewHeight - borderWidth) - (int) (bmpHeight * mScaleFactor)) / 2;

        fixTrans();
        //pivotPointY=-pivotPointY;
        mPosX=pivotPointX;
        mPosY=-(pivotPointY/4);
        //if((isPortraitImage&&isPortrait)||(!isPortraitImage&&!isPortrait))
        //    mPosY=-((pivotPointY/2)+(pivotPointY/4));

        //float scaleX = viewWidth / (float) bmpWidth;
        //float scaleY = viewHeight / (float) bmpHeight;

        //float scale = Math.min(scaleX, scaleY);





        scaleStart=mScaleFactor;
        pivotPointYstart=pivotPointY;
        pivotPointXstart=pivotPointX;
        mPosYstart=mPosY;
        mPosXstart=mPosX;

        //BLog.e("BEGIN --- SF: " + mPosX + ", PPX: " + mPosY + ", PPY: " + pivotPointY);
        //fixTrans();
        //matrix.setScale(scale, scale);
        //BLog.e("START --- SF: " + mScaleFactor + ", PPX: " + pivotPointX + ", PPY: " + pivotPointY);
        //matrix.setScale(mScaleFactor, mScaleFactor);
        //fixTrans();

    }






    float[] m = new float[9];
    void fixTrans() {
        matrix.getValues(m);
        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];



        float fixTransX = getFixTrans(transX, viewWidth, bmpWidth * mScaleFactor);
        float fixTransY = getFixTrans(transY, viewHeight, bmpHeight * mScaleFactor);

        if (fixTransX != 0 || fixTransY != 0)
            matrix.postTranslate(fixTransX, fixTransY);
    }
    float getFixTrans(float trans, float viewSize, float contentSize) {
        float minTrans, maxTrans;

        if (contentSize <= viewSize) {
            minTrans = Double.valueOf((viewSize-contentSize)/2).intValue();
            maxTrans = viewSize - contentSize;
        } else {
            minTrans = viewSize - contentSize;
            maxTrans = 0;
        }

        if (trans < minTrans)
            return -trans + minTrans;
        if (trans > maxTrans)
            return -trans + maxTrans;
        return 0;
    }



    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            pivotPointX = detector.getFocusX();
            pivotPointY = detector.getFocusY();

            //Log.e(LOG_TAG, "mScaleFactor " + mScaleFactor);
            //Log.e(LOG_TAG, "pivotPointY " + pivotPointY + ", pivotPointX= " + pivotPointX);
            mScaleFactor = Math.max(minScale, mScaleFactor);
            mScaleFactor = Math.min(maxScale, mScaleFactor);

            invalidate();
            return true;
        }
    }
}

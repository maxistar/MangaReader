package com.maxistar.mangabrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class ImagePager extends View implements Animator.Animable {
    Bitmap bitmap = null; // current bitmap

    private final Semaphore drawLock = new Semaphore(0);
    private Animator animator;
    VolumeActivity owner;

    SimpleAnimation zoomAnimation;
    SimpleAnimation nextPageAnimation;
    SimpleAnimation currentPageAnimation;

    ArrayList<File> files = new ArrayList<File>();
    VolumeItem item = null;

    Matrix matrix = null;
    Paint paint = null;
    Paint paint2 = null;

    ScaleGestureDetector mScaleDetector;
    GestureDetector mDetector;

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    static final int SCROLLING_PREV = 3;
    static final int SCROLLING_NEXT = 4;

    int mode = NONE;
    float minScale = 1f;
    float maxScale = 5f; // maximum scale

    float baseScale = 1f; // the scale to make image fit on screen
    float saveScale = 1f; // the additional scale for image

    Bitmap next_bitmap = null; // next bitmap to be shown here
    Matrix next_matrix;
    float next_scale;
    float next_offset;
    float next_last_diff; // base on this we will decide where to animate

    int viewWidth, viewHeight;

    float zoomDestination;
    float zoomStep = 0.1f;
    float zoomDirection = 1;
    float zoomX;
    float zoomY;

    float lastZoomIn = 3f; //the last zoom position to save

    Handler handler = new Handler();
    /**
     * Class constructor taking only a context. Use this constructor to create
     * objects from your own code.
     *
     * @param context
     */
    public ImagePager(Context context) {
        super(context);
        init(context);
    }

    /**
     * Class constructor taking a context and an attribute set. This constructor
     * is used by the layout engine to construct a from a set
     * of XML attributes.
     *
     * @param context
     * @param attrs
     *            An attribute set which can contain attributes from
     *            as well as attributes inherited from {@link android.view.View}
     *            .
     */
    public ImagePager(Context context, AttributeSet attrs) {
        super(context, attrs);

        // attrs contains the raw values for the XML attributes
        // that were specified in the layout, which don't include
        // attributes set by styles or themes, and which may have
        // unresolved references. Call obtainStyledAttributes()
        // to get the final values for each attribute.
        //
        // This call uses R.styleable.PieChart, which is an array of
        // the custom attributes that were declared in attrs.xml.
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ViewPager,
                0,
                0
        );

        try {
            // Retrieve the values from the TypedArray and store into
            // fields of this class.
            //
            // The R.styleable.PieChart_* constants represent the index for
            // each custom attribute in the R.styleable.PieChart array.
            /*
             * mShowText = a.getBoolean(R.styleable.PieChart_showText, false);
             * mTextY = a.getDimension(R.styleable.PieChart_labelY, 0.0f);
             * mTextWidth = a.getDimension(R.styleable.PieChart_labelWidth,
             * 0.0f); mTextHeight =
             * a.getDimension(R.styleable.PieChart_labelHeight, 0.0f); mTextPos
             * = a.getInteger(R.styleable.PieChart_labelPosition, 0); mTextColor
             * = a.getColor(R.styleable.PieChart_labelColor, 0xff000000);
             * mHighlightStrength =
             * a.getFloat(R.styleable.PieChart_highlightStrength, 1.0f);
             * mPieRotation = a.getInt(R.styleable.PieChart_pieRotation, 0);
             * mPointerRadius =
             * a.getDimension(R.styleable.PieChart_pointerRadius, 2.0f);
             * mAutoCenterInSlice =
             * a.getBoolean(R.styleable.PieChart_autoCenterPointerInSlice,
             * false);
             */
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

        init(context);
    }

    void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        matrix = new Matrix();
        next_matrix = new Matrix();
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        GestureListener moveListener = new GestureListener();
        mDetector = new GestureDetector(context, moveListener);
        mDetector.setOnDoubleTapListener(moveListener);

        zoomAnimation = new SimpleAnimation();

        zoomAnimation.setZoomAnimationListener(new SimpleAnimationListener() {
            @Override
            public void onZoom(float scale, float x, float y) {

                if (saveScale == zoomDestination) {
                    animationStop();
                }

                float mScaleFactor = 1 + zoomDirection * zoomStep;
                float origScale = saveScale;
                saveScale *= mScaleFactor;
                if (saveScale > zoomDestination && zoomDirection == 1) {
                    saveScale = zoomDestination;
                    mScaleFactor = zoomDestination / origScale;

                } else if (saveScale < zoomDestination && zoomDirection == -1) {
                    saveScale = minScale;
                    mScaleFactor = zoomDestination / origScale;
                }

                matrix.postScale(mScaleFactor, mScaleFactor, zoomX, zoomY);

                fixPosition();
            }

            @Override
            public void onComplete() {
                //handler.postDelayed(new Runnable(){
                //    public void run() {
                //    	owner.onPageShown(item.page_num);
                //    }                   
                //}, 100);
            }
        });


        nextPageAnimation = new SimpleAnimation();
        nextPageAnimation.setZoomAnimationListener(new SimpleAnimationListener() {
            @Override
            public void onZoom(float scale, float x, float y) {
                float step_diff = 10f;

                if (mode == ImagePager.SCROLLING_NEXT) {
                    if (next_offset-step_diff<=0){
                        animationStop();
                        item.page_num++;
                        handler.postDelayed(new Runnable(){
                            public void run() {
                                owner.onPageShown(item.page_num);
                                owner.saveCache();
                            }
                        }, 100);

                        baseScale = next_scale;
                        matrix.setScale(next_scale, next_scale);
                        bitmap = next_bitmap;
                        next_bitmap = null;
                        mode = NONE;
                    }
                    else { //do step
                        matrix.postTranslate(-step_diff, 0);
                        next_matrix.postTranslate(-step_diff, 0);
                        next_offset -= step_diff;
                    }
                } else {

                    if (next_offset + step_diff >= viewWidth * 2) {
                        animationStop();
                        item.page_num--;
                        handler.postDelayed(new Runnable(){
                            public void run() {
                                owner.onPageShown(item.page_num);
                                owner.saveCache();
                            }
                        }, 100);
                        baseScale = next_scale;
                        matrix.setScale(next_scale, next_scale);
                        bitmap = next_bitmap;
                        next_bitmap = null;
                        mode = NONE;
                    }
                    else { //do step
                        matrix.postTranslate(step_diff, 0);
                        next_matrix.postTranslate(step_diff, 0);
                        next_offset += step_diff;
                    }
                }
            }

            @Override
            public void onComplete() {
            }
        });


        currentPageAnimation = new SimpleAnimation();
        currentPageAnimation.setZoomAnimationListener(new SimpleAnimationListener() {
            @Override
            public void onZoom(float scale, float x, float y) {
                float step_diff = 10f;

                if (mode == ImagePager.SCROLLING_NEXT) {
                    if (next_offset + step_diff >= viewWidth) {
                        animationStop();
                        next_bitmap = null;
                        mode = NONE;
                    } else { //do step
                        matrix.postTranslate(step_diff, 0);
                        next_matrix.postTranslate(step_diff, 0);
                        next_offset += step_diff;
                    }
                } else {
                    if (next_offset - step_diff <= viewWidth){
                        animationStop();
                        next_bitmap = null;
                        mode = NONE;
                    }
                    else { //do step
                        matrix.postTranslate(-step_diff, 0);
                        next_matrix.postTranslate(-step_diff, 0);
                        next_offset -= step_diff;
                    }
                }
            }

            @Override
            public void onComplete() {
            }
        });

    }

    @Override
    protected void onAttachedToWindow() {
        animator = new Animator(this, MStrings.IMAGE_PAGER_ANIMATOR);
        animator.start();

        super.onAttachedToWindow();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // boolean is_scale =
        mScaleDetector.onTouchEvent(ev);
        mDetector.onTouchEvent(ev);

        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:

            break;

        case MotionEvent.ACTION_MOVE:
            break;

        case MotionEvent.ACTION_UP:

            handleUpEvent();
            break;

        case MotionEvent.ACTION_POINTER_UP:

            handleUpEvent();
            break;
        }

        invalidate();

        return true;
    }

    void handleUpEvent(){
        if (mode == ImagePager.ZOOM) {
            if (saveScale!=1f){
                lastZoomIn = saveScale;
            }
            mode = NONE;
        }
        else if (mode == ImagePager.SCROLLING_NEXT) {

            if (next_last_diff > 0) {
                nextPageAnimation.reset();
                animationStart(nextPageAnimation);
                // startScrollToNextPage();
            } else {
                nextPageAnimation.reset();
                animationStart(currentPageAnimation);
                // startScrollToCurrentPage();
            }
        } else if (mode == ImagePager.SCROLLING_PREV) {

            if (next_last_diff < 0) {
                nextPageAnimation.reset();
                animationStart(nextPageAnimation);
                // startScrollToNextPage();
            } else {
                nextPageAnimation.reset();
                animationStart(currentPageAnimation);
                // startScrollToCurrentPage();
            }
        } else {
            mode = NONE;
        }
    }

    float getScale() {
        return saveScale;
    }

    void setFiles(ArrayList<File> files, VolumeItem item, VolumeActivity owner){
        this.files = files;
        this.item = item;
        this.owner = owner;

        Bitmap bitmap = getBitmap(item.page_num);
        this.bitmap = bitmap;
        this.invalidate();
    }

    Bitmap getBitmap(int index) {
        Bitmap bitmap;
        try {
            bitmap =  BitmapFactory.decodeFile(files.get(index).getAbsolutePath());
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.error_image);
            }
        }
        catch (OutOfMemoryError e) {
            owner.showToast(owner.l(R.string.ImageTooLarge));
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.error_image);
        }
        catch(Exception e){
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.error_image);
        }
        return bitmap;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (next_bitmap != null) {
            canvas.drawBitmap(next_bitmap, next_matrix, paint2);
        }

        if (bitmap != null) {
            canvas.drawBitmap(bitmap, matrix, paint);
        }

        // Draw the margin drawable if needed.
        // if (mPageMargin > 0 && mMarginDrawable != null) {
        // final int scrollX = getScrollX();
        // final int width = getWidth();
        // final int offset = scrollX % (width + mPageMargin);
        // if (offset != 0) {
        // Pages fit completely when settled; we only need to draw when in
        // between
        // final int left = scrollX - offset + width;
        // mMarginDrawable.setBounds(left, 0, left + mPageMargin, getHeight());
        // mMarginDrawable.draw(canvas);
        // }
        // }

        if (drawLock.availablePermits() <= 0) {
            drawLock.release();
        }

    }

    //
    // Measurement functions. This example uses a simple heuristic: it assumes
    // that
    // the pie chart should be at least as wide as its label.
    //
    // @Override
    // protected int getSuggestedMinimumWidth() {
    // return (int) mTextWidth * 2;
    // }

    // @Override
    // protected int getSuggestedMinimumHeight() {
    // return (int) mTextWidth;
    // }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        // int minw = getPaddingLeft() + getPaddingRight() +
        // getSuggestedMinimumWidth();

        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        // origWidth = viewWidth;
        // origHeight = viewHeight;

        // do not know what is this - todo get the information
        setMeasuredDimension(viewWidth, viewHeight);

        /* if (firstcall) */{
            // Fit to screen.
            // float scale;
            // Drawable drawable = getDrawable();
            // if (drawable == null || drawable.getIntrinsicWidth() == 0 ||
            // drawable.getIntrinsicHeight() == 0)
            // return;

            if (this.bitmap == null)
                return;

            int bmWidth = this.bitmap.getWidth();

            //int bmHeight = this.bitmap.getHeight();


            float scaleX = (float) viewWidth / (float) bmWidth;
            // float scaleY = (float) viewHeight / (float) bmHeight;
            baseScale = scaleX;// Math.min(scaleX, scaleY);
            matrix.setScale(baseScale, baseScale);

            // Center the image by horizontal
            // top should stay on top of screen
            float offsetX = ((float) viewWidth - (baseScale * (float) bmWidth)) / 2f;
            float offsetY = 0;// ((float) viewHeight - (baseScale * (float)
                                // bmHeight)) / 2f;

            matrix.postTranslate(offsetX, offsetY);

            // origWidth = viewWidth - 2 * redundantXSpace;
            // origHeight = viewHeight - 2 * redundantYSpace;

            // setImageMatrix(matrix);

        }
    }

    void scaleNextBitmap() {
        int bmWidth = this.next_bitmap.getWidth();

        next_scale = (float) viewWidth / (float) bmWidth;

        next_matrix.setScale(next_scale, next_scale);
        next_matrix.postTranslate(viewWidth, 0);
    }

    void scalePrevBitmap() {
        int bmWidth = this.next_bitmap.getWidth();

        next_scale = (float) viewWidth / (float) bmWidth;

        next_matrix.setScale(next_scale, next_scale);
        next_matrix.postTranslate(-viewWidth, 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mode = ZOOM;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (mode != ZOOM)
                return false;


            float mScaleFactor = detector.getScaleFactor();
            float origScale = saveScale;
            saveScale *= mScaleFactor;

            if (saveScale > maxScale) {
                saveScale = maxScale;
                mScaleFactor = maxScale / origScale;
            } else if (saveScale < minScale) {
                saveScale = minScale;
                mScaleFactor = minScale / origScale;
            }

            matrix.postScale(
                    mScaleFactor,
                    mScaleFactor,
                    detector.getFocusX(),
                    detector.getFocusY()
            );

            fixPosition();

            return true;
        }
    }

    /**
     * Extends {@link GestureDetector.SimpleOnGestureListener} to provide custom
     * gesture processing.
     */
    private class GestureListener extends
            GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {

            if (mode == NONE) {
                if (saveScale == 1 && Math.abs(distanceX) > Math.abs(distanceY)) { // original
                                                                                    // size
                    if (item.page_num > 0 && distanceX < 0) {
                        mode = SCROLLING_PREV;
                        next_offset = viewWidth;
                        next_bitmap = getBitmap(item.page_num - 1);
                        scalePrevBitmap();
                    }
                    if (item.page_num < files.size() - 1 && distanceX > 0) {
                        mode = SCROLLING_NEXT;
                        next_offset = viewWidth;
                        next_bitmap = getBitmap(item.page_num + 1);
                        scaleNextBitmap();
                    }
                } else {
                    mode = DRAG;
                }
            }
            if (mode == DRAG) {
                matrix.postTranslate(-distanceX, -distanceY);
                fixPosition();
            }
            if (mode == SCROLLING_NEXT) {
                matrix.postTranslate(-distanceX, 0);
                next_matrix.postTranslate(-distanceX, 0);
                next_last_diff = distanceX;
                next_offset -= distanceX;


                float[] m = new float[9];
                matrix.getValues(m);

                float transX = m[Matrix.MTRANS_X];
                float fixTransX = getFixPosition(transX, viewWidth,
                        bitmap.getWidth() * baseScale * saveScale);

                if (fixTransX < 0) {
                    matrix.postTranslate(fixTransX, 0);
                    next_matrix.postTranslate(fixTransX, 0);
                }

            }
            if (mode == SCROLLING_PREV) {
                matrix.postTranslate(-distanceX, 0);
                next_matrix.postTranslate(-distanceX, 0);
                next_last_diff = distanceX;
                next_offset -= distanceX;



                float[] m = new float[9];
                matrix.getValues(m);

                float transX = m[Matrix.MTRANS_X];
                float fixTransX = getFixPosition(transX, viewWidth,
                        bitmap.getWidth() * baseScale * saveScale);

                if (fixTransX > 0) {
                    matrix.postTranslate(fixTransX, 0);
                    next_matrix.postTranslate(fixTransX, 0);
                }
            }

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            zoomAnimation.reset();
            if (saveScale == 1f) {
                zoomDestination = lastZoomIn;
                zoomDirection = 1;
                zoomX = e.getX();
                zoomY = e.getY();
                // zoomAnimation.setZoom(3f);
                // zoomAnimation.setTouchX(e.getX());
                // zoomAnimation.setTouchY(e.getY());
                animationStart(zoomAnimation);
            } else {
                zoomDestination = 1f;
                zoomDirection = -1;
                zoomX = e.getX();
                zoomY = e.getY();

                // zoomAnimation.setZoom(1f);
                // zoomAnimation.setTouchX(e.getX());
                // zoomAnimation.setTouchY(e.getY());
                animationStart(zoomAnimation);
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e){
            //ImagePager.this.getParent().

        }
    }

    /**
     * fixes position if after transformation image got beyound the viewport
     */
    void fixPosition() {
        float[] m = new float[9];

        matrix.getValues(m);

        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];

        float fixTransX = getFixPosition(transX, viewWidth, bitmap.getWidth()
                * baseScale * saveScale);
        float fixTransY = getFixPosition(transY, viewHeight, bitmap.getHeight()
                * baseScale * saveScale);

        if (fixTransX != 0 || fixTransY != 0)
            matrix.postTranslate(fixTransX, fixTransY);
    }

    float getFixPosition(float trans, float viewSize, float contentSize) {
        float minTrans, maxTrans;

        if (contentSize <= viewSize) {
            minTrans = 0;
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

    /**
     * Waits for a draw
     *
     * @param timeout
     *            time to wait for draw (ms)
     * @throws InterruptedException
     */
    public boolean waitForDraw(long timeout) throws InterruptedException {
        return drawLock.tryAcquire(timeout, TimeUnit.MILLISECONDS);
    }

    public void redraw() {
        postInvalidate();
    }

    public void animationStart(Animation animation) {
        if (animator != null) {
            animator.play(animation);
        }
    }

    public void animationStop() {
        if (animator != null) {
            animator.cancel();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if(animator != null) {
            animator.finish();
        }
        super.onDetachedFromWindow();
    }


}

package com.example.niujun.njbv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static android.widget.LinearLayout.HORIZONTAL;

/**
 * Created by niujun on 2017/3/10.
 */

public class BannerScrollView2 extends HorizontalScrollView {

    private LinearLayout mContainer;
    private Context mContext;
    private LinearLayout.LayoutParams imgParams;

    private Paint mStrokePaint, mFillPaint;


    private int CIRCLE_RADIUS = 3;
    private int minCount = 3;

    private int mCurrentPage;


    private static AutoPlayRunnable mAuroPlayRunnable;
    private static int AUTO_PLAY_DUATION = 3000;

    private int[] imgIds = new int[]{};

    private VelocityTracker mVelocityTracker;
    private RecycleBin mRecycleBin;


    public BannerScrollView2(Context context) {
        super(context);
        init(context);
    }

    public BannerScrollView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        initContainer();
        initRecycleBin();
        mAuroPlayRunnable = new AutoPlayRunnable(this);
        run();
    }


    public void setMinCount(int minCount) {
        this.minCount = minCount;
    }

    private void initRecycleBin() {
        mRecycleBin = new RecycleBin();
    }

    public void run() {
        mAuroPlayRunnable.run();
    }

    private void initContainer() {
        mContainer = new LinearLayout(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.setLayoutParams(params);
        mContainer.setOrientation(HORIZONTAL);
        this.addView(mContainer);
        setSmoothScrollingEnabled(true);//平滑
        setHorizontalScrollBarEnabled(false);//不显示滑动条
    }

    private void initImgRes() {
        imgParams = new LinearLayout.LayoutParams(getWidth(), getHeight());
        int size = imgIds.length;
        if (size > 1) {
            //放最后一张图片
            mContainer.addView(makeImgView(imgIds[size - 1]));
        }
        for (int resID : imgIds) {
            mContainer.addView(makeImgView(resID));
        }
        if (size > 1) {
            //放第一张图片
            mContainer.addView(makeImgView(imgIds[0]));
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    private ImageView makeImgView(int resID) {
//        ImageView imageView = new ImageView(mContext);
//        imageView.setLayoutParams(imgParams);
//        imageView.setImageResource(resID);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        return imageView;
        if (mRecycleBin.hasMoreViews()) {
            ImageView imageView = mRecycleBin.getFromScrapViews();
            imageView.setImageResource(resID);
        } else {
            for (int i = 0; i < minCount; i++) {
            }
        }
        return null;
    }




    public void setImgIds(int[] imgIds) {
        this.imgIds = imgIds;
        if (imgIds != null && imgIds.length > 0) {
            initImgRes();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        imgParams.width = getMeasuredWidth();
        imgParams.height = getMeasuredHeight();
    }

    /**
     * 滑动到第几页
     *
     * @param page
     * @param isSmooth
     */
    public void scrollToPage(int page, boolean isSmooth) {
        int totalSize = imgIds.length;
        if (page < 0) {
            page = totalSize - 1;
        } else if (page >= totalSize) {
            page = 0;
        }
        mCurrentPage = page;
        //第一张前面增加了一张图片，所以默认 page+1
        if (isSmooth) {
            this.smoothScrollTo(getWidth() * (page + 1), 0);
        } else {
            this.scrollTo(getWidth() * (page + 1), 0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //滑动到第一张图片
        scrollToPage(0, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    private void initPaint() {

        mStrokePaint = new Paint();
        mStrokePaint.setAntiAlias(true); //抗锯齿
        mStrokePaint.setColor(Color.WHITE);//颜色
        mStrokePaint.setStrokeWidth(1);//空心线宽
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mFillPaint = new Paint();
        mFillPaint.setColor(Color.WHITE);
        mFillPaint.setAntiAlias(true);
        mFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        initPaint();
        int width = getWidth();//获取当前view的宽度，即BannerScrollView的宽度
        float density = getResources().getDisplayMetrics().density;
        int radiusPix = (int) (CIRCLE_RADIUS * density);//圆圈半径（由密度转化为像素）
        int margin = radiusPix;//两个圆圈之间的距离
        int diameterPix = 2 * radiusPix;//圆圈直径
        int totleWidth = imgIds.length * diameterPix + margin * (imgIds.length - 1); //圆圈的总宽度

        //第一个圆圈的位置
        int offsetX = getScrollX() + width / 2 - totleWidth / 2 + radiusPix;  //横坐标偏移
        int offsetY = getHeight() - (int) (10 * density);       //纵坐标偏移

        //画剩下的圆圈
        for (int i = 0; i < imgIds.length; i++) {
            if (i == mCurrentPage) {
                //画实心圆圈
                canvas.drawCircle(offsetX, offsetY, radiusPix, mFillPaint);
            } else {
                //画空心圆
                canvas.drawCircle(offsetX, offsetY, radiusPix, mStrokePaint);
            }
            offsetX += diameterPix + margin;//下一个圆圈的横坐标偏移
        }
    }


    public int getCurrentPage() {
        return mCurrentPage;
    }

    //拦截down事件 ， 则完全由viewGroup处理
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //终止自动轮播
                removeCallbacks(mAuroPlayRunnable);
                break;
        }
        return super.onInterceptHoverEvent(event);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        mVelocityTracker.computeCurrentVelocity(1000, 2000);
        int xVelocity = (int) mVelocityTracker.getXVelocity();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                int width = getWidth();
                int page;
                if (Math.abs(xVelocity) > 1000) {
                    //移动速度够大
                    page = scrollX / width;
                    if (xVelocity > 0) {
                        page -= 1;
                    }
                } else {
                    //缓慢移动，按当前哪张图展示多就显示哪张
                    page = (int) Math.round(scrollX * 1.0 / width) - 1;
                }
                scrollToPage(page, true);
                //开启自动播放
                postDelayed(mAuroPlayRunnable, AUTO_PLAY_DUATION);
                return true;    //直接返回，不让ScrollView处理事件
        }
        return super.onTouchEvent(ev);
    }


    public static class AutoPlayRunnable implements Runnable {

        private WeakReference<BannerScrollView2> reference = null;

        public AutoPlayRunnable(BannerScrollView2 scrollView) {
            this.reference = new WeakReference<>(scrollView);
        }

        @Override
        public void run() {
            BannerScrollView2 bannerView = reference.get();
            if (bannerView != null) {
                int page = bannerView.getCurrentPage();
                bannerView.scrollToPage(page + 1, true);
                bannerView.postDelayed(mAuroPlayRunnable, AUTO_PLAY_DUATION);
            }
        }
    }


    class RecycleBin {
        private ImageView[] mActiveViews;
        private ArrayList<ImageView> mScrapViews = new ArrayList<>();


        RecycleBin() {
            initActiveViews(minCount);
        }

        // 初始化有效views
        private void initActiveViews(int minCount) {

            if (mActiveViews.length < minCount) {
                if (mActiveViews == null) {
                    mActiveViews = new ImageView[minCount];
                }
                for (int i = 0; i < minCount; i++) {
                    if (mActiveViews[i] == null) {
                        ImageView imageView = new ImageView(mContext);
                        imageView.setLayoutParams(imgParams);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        mActiveViews[i] = imageView;
                    }
                }

            }
        }

        public void setDataToActiveViews(int imgids) {


        }


        //缓存是否有多余的view
        private boolean hasMoreViews() {
            return mScrapViews.size() > 0;
        }

        //将废弃的view加入缓存
        void putScrapViews(ImageView imagview) {
            mScrapViews.add(imagview);
        }

        //从缓存views随意取出一个view
        ImageView getFromScrapViews() {
            if (hasMoreViews()) {
                for (ImageView imagview : mScrapViews) {
                    if (imagview != null) {
                        return imagview;
                    }
                }
            }
            return null;
        }

    }

}

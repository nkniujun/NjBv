package com.example.niujun.njbv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static android.widget.LinearLayout.HORIZONTAL;

/**
 * Created by niujun on 2017/3/10.
 *  * 将isEnd()和isStart()注销掉就可以轮播
 */

public class BannerScrollView extends HorizontalScrollView {

    private LinearLayout mContainer;
    private Context mContext;
    private LinearLayout.LayoutParams imgParams;

    private Paint mStrokePaint, mFillPaint;

    private int CIRCLE_RADIUS = 3; //圆圈半径
    private int mCurrentPage;

    private int[] imgIds = new int[]{};
    private int downScrollX = 0;

    private int imgIndex;
    private int imgCount;
    private int nextImgIndex;
    private int preImgIndex;

    private boolean isRight;
    private boolean isLeft;

    private ImageView mPreImageView;
    private ImageView mCurrentImageView;
    private ImageView mNextImageView;

    private boolean up;

    private int circlePos;

    private VelocityTracker mVelocityTracker;


    private onStartOrEndListener mListener;


    public BannerScrollView(Context context) {
        super(context);
        init(context);
    }

    public BannerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        initContainer();
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
        for (int i = 0; i < 3; i++) {
            mContainer.addView(makeImgView());
        }
        mPreImageView = (ImageView) mContainer.getChildAt(0);
        mCurrentImageView = (ImageView) mContainer.getChildAt(1);
        mNextImageView = (ImageView) mContainer.getChildAt(2);
    }


    private ImageView makeImgView() {
        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(imgParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }


    public void setData(int[] imgIds) {
        this.imgIds = imgIds;
        imgCount = imgIds.length;
        imgIndex = 0;
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
        //第一张前面增加了一张图片，所以默认 page+1
        int i = Math.abs(page) % 3;
        int j = resetImgIndex(imgIndex);
        ImageView imageView = (ImageView) mContainer.getChildAt(i);
        imageView.setImageResource(imgIds[j]);

        if (isSmooth) {
            this.smoothScrollTo(getWidth() * (page), 0);
        } else {
            this.scrollTo(getWidth() * (page), 0);
        }
    }


    /**
     * 进行偏移
     *
     * @param l
     */
    private void updataScroll(int l) {
        if ((l == 2 * getWidth() || l == 0) && up) {
            setScrollX(1 * getWidth());
            mCurrentImageView.setImageResource(imgIds[resetImgIndex(imgIndex)]);
        }

    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        updataScroll(l);
    }


    private int resetImgIndex(int i) {
        if (i < 0) {
            i = i + imgCount;
        }
        return i % imgCount;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //滑动到第一张图片
        scrollToPage(1, false);
        preLoad();
        nextLoad();
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

    /**
     * 小圆圈绘制
     *
     * @param canvas
     */
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
        drawCircle(canvas, radiusPix, margin, diameterPix, offsetX, offsetY);
    }


    /**
     * 画下面指示圆点
     *
     * @param canvas
     * @param radiusPix
     * @param margin
     * @param diameterPix
     * @param offsetX
     * @param offsetY
     */
    private void drawCircle(Canvas canvas, int radiusPix, int margin, int diameterPix, int offsetX, int offsetY) {
        int cicleIndex = resetImgIndex(imgIndex);
        int currentIndex = 0;
        for (int i = 0; i < imgCount; i++) {
            if (i == cicleIndex) {
                circlePos = i;
                Log.i("Nj", "" + circlePos);
                canvas.drawCircle(offsetX, offsetY, radiusPix, mFillPaint);
                currentIndex = i;
            } else {
                canvas.drawCircle(offsetX, offsetY, radiusPix, mStrokePaint);
            }
            offsetX += diameterPix + margin;//下一个圆圈的横坐标偏移
        }

        if (currentIndex == 0) {
            mListener.doStart();
        } else if (currentIndex == imgCount - 1) {
            mListener.doFinish();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {


        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();//速度追踪
        }
        mVelocityTracker.addMovement(ev);
        mVelocityTracker.computeCurrentVelocity(1000, 2000);
        int xVelocity = (int) mVelocityTracker.getXVelocity();//水平速度
        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                up = false;
                downScrollX = getScrollX();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentPage = (int) Math.round((double) getScrollX() / (double) getWidth());//当前页
                int nextPage = (int) Math.ceil((double) getScrollX() / (double) getWidth());//下一页
                int prePage = (int) Math.floor((double) getScrollX() / (double) getWidth());//上一页

                if (getScrollX() < downScrollX) {
                    //向右滑动
                    if (isStart()) {
                        mListener.doStart();
                        return true;
                    }

                    if (Math.abs(xVelocity) > 1000 || mCurrentPage == prePage) {
                        isRight = true;
                    }

                } else if (getScrollX() > downScrollX) {
                    //向左滑动

                    if (isEnd()) {
                        return true;
                    }

                    if (Math.abs(xVelocity) > 1000 || mCurrentPage == nextPage) {
                        isLeft = true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                up = true;
                if (isLeft) {
                    //真正滑动到下一张
                    imgIndex++;

                } else if (isRight) {
                    imgIndex--;

                }
                imgIndex = resetImgIndex(imgIndex);
                scrollToPage(mCurrentPage, true);
                isLeft = false;
                isRight = false;
                updataScroll(getScrollX());
                return true;    //直接返回，不让ScrollView处理事件
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 预加载右侧图片
     */
    private void nextLoad() {
        nextImgIndex = (imgIndex + 1) % imgCount;
        mNextImageView.setImageResource(imgIds[nextImgIndex]);
    }

    /**
     * 预加载左侧图片
     */
    private void preLoad() {
        preImgIndex = (imgIndex - 1 + imgCount) % imgCount;
        mPreImageView.setImageResource(imgIds[preImgIndex]);
    }

    //判断是否滑到最后一张view
    private boolean isEnd() {
        return circlePos == imgCount - 1;
    }

    //判断是否是第一张
    private boolean isStart() {
        return circlePos == 0;
    }


    private void show(String str) {
        Log.i("Nj", str);
    }

    public interface onStartOrEndListener {
        void doFinish();

        void doStart();
    }

    public void setListener(onStartOrEndListener listener) {
        mListener = listener;
    }

}

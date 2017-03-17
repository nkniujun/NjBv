package com.example.niujun.njbv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static android.widget.LinearLayout.HORIZONTAL;

/**
 * Created by niujun on 2017/3/10.
 */

public class BannerScrollView extends HorizontalScrollView {

    private LinearLayout mContainer;
    private Context mContext;
    private LinearLayout.LayoutParams imgParams;

    private Paint mStrokePaint, mFillPaint;

    private int CIRCLE_RADIUS = 3;
    private int mCurrentPage;

    private int[] imgIds = new int[]{};
    private int downScrollX = 0;

    private int imgIndex;
    private int imgCount;
    private int nextImgIndex;
    private int preImgIndex;

    private boolean isRight;
    private boolean isLeft;

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
    }


    private ImageView makeImgView(int resID) {
        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(imgParams);
        imageView.setImageResource(resID);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
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
        int j = Math.abs(imgIndex + imgCount) % imgCount;
        ImageView imageView = (ImageView) mContainer.getChildAt(i);
        imageView.setImageResource(imgIds[j]);

        if (isSmooth) {
            this.smoothScrollTo(getWidth() * (page), 0);
        } else {
            this.scrollTo(getWidth() * (page), 0);
        }
        show("平滑完毕");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //滑动到第一张图片
        scrollToPage(1, false);
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

        drawCircle(canvas, radiusPix, margin, diameterPix, offsetX, offsetY);

        show("小圆点");
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
        show("cicleIndex= " + imgIndex % imgIds.length);
        int cicleIndex = imgIndex % imgCount;
//        if (mCurrentPage % imgIds.length == 0) {
        //第3个圆实心
//            for (int i = 0; i < imgIds.length; i++) {
//                if (i == (imgIndex%imgIds.length)) {
//                    canvas.drawCircle(offsetX, offsetY, radiusPix, mFillPaint);
//                } else {
//                    canvas.drawCircle(offsetX, offsetY, radiusPix, mStrokePaint);
//                }
//                offsetX += diameterPix + margin;//下一个圆圈的横坐标偏移
//            }
//        }

        for (int i = 0; i < imgCount; i++) {
            if (i == cicleIndex) {
                canvas.drawCircle(offsetX, offsetY, radiusPix, mFillPaint);
            } else {
                canvas.drawCircle(offsetX, offsetY, radiusPix, mStrokePaint);
            }
            offsetX += diameterPix + margin;//下一个圆圈的横坐标偏移
        }

//        if (mCurrentPage % imgIds.length == 1) {
//            //第1个圆实心
//            for (int i = 1; i <= imgIds.length; i++) {
//                if (i == 1) {
//                    canvas.drawCircle(offsetX, offsetY, radiusPix, mFillPaint);
//                } else {
//                    canvas.drawCircle(offsetX, offsetY, radiusPix, mStrokePaint);
//                }
//                offsetX += diameterPix + margin;//下一个圆圈的横坐标偏移
//            }
//        }

//        if (mCurrentPage % imgIds.length == 2) {
//            //第1个圆实心
//            for (int i = 1; i <= imgIds.length; i++) {
//                if (i == 2) {
//                    canvas.drawCircle(offsetX, offsetY, radiusPix, mFillPaint);
//                } else {
//                    canvas.drawCircle(offsetX, offsetY, radiusPix, mStrokePaint);
//                }
//                offsetX += diameterPix + margin;//下一个圆圈的横坐标偏移
//            }
//        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {


        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                downScrollX = getScrollX();
                break;
            case MotionEvent.ACTION_MOVE:


                mCurrentPage = (int) Math.round((double) getScrollX() / (double) getWidth());//当前页
                int nextPage = (int) Math.ceil((double) getScrollX() / (double) getWidth());//下一页
                int prePage = (int) Math.floor((double) getScrollX() / (double) getWidth());//上一页


                if (getScrollX() < downScrollX) {
                    //向右滑动

                    preImgIndex = (imgIndex - 1 + imgCount) % imgCount;
                    ImageView imageView = (ImageView) mContainer.getChildAt(0);
                    imageView.setImageResource(imgIds[preImgIndex]);

                    if (mCurrentPage == prePage) {
                        isRight = true;
                    } else {
                        isRight = false;
                    }
                } else if (getScrollX() > downScrollX) {
                    //向左滑动
                    nextImgIndex = (imgIndex + 1) % imgCount;
                    ImageView imageView = (ImageView) mContainer.getChildAt(2);
                    imageView.setImageResource(imgIds[nextImgIndex]);
                    if (mCurrentPage == nextPage) {
                        isLeft = true;
                    } else {
                        isLeft = false;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                show("抬起手指");
                if (isLeft) {
                    //真正滑动到下一张
                    imgIndex++;
                }
                if (isRight) {
                    imgIndex--;
                    if (imgIndex < 0) {
                        imgIndex = imgIndex + imgCount;
                    }
                }
                scrollToPage(mCurrentPage, true);
                isLeft = false;
                isRight = false;

                return true;    //直接返回，不让ScrollView处理事件
        }
        return super.onTouchEvent(ev);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        if (l == 2 * getWidth()) {
            setScrollX(1 * getWidth());
            ImageView imageView = (ImageView) mContainer.getChildAt(1);
            int j = imgIndex % imgCount;
            imageView.setImageResource(imgIds[j]);
            next(j);

            show("最右边");
        } else if (l == 0) {
            setScrollX(1 * getWidth());
            ImageView imageView = (ImageView) mContainer.getChildAt(1);
            int j = (imgIndex + imgCount) % imgCount;
            imageView.setImageResource(imgIds[j]);
            pre(j);

            show("最左边");

        }
    }

    private void next(int i) {

        int index = (i + 1) % imgCount;
        ImageView imageView = (ImageView) mContainer.getChildAt(2);
        imageView.setImageResource(imgIds[index]);
    }

    private void pre(int i) {
        if (i < 0) {
            i = i + imgCount;
        }
        int index = (i - 1 + imgCount) % imgCount;
        ImageView imageView = (ImageView) mContainer.getChildAt(0);
        imageView.setImageResource(imgIds[index]);

    }

    private void show(String str) {
        Log.i("Nj", str);
    }

}

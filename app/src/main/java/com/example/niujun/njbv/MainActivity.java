package com.example.niujun.njbv;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;

public class MainActivity extends Activity {

    private BannerScrollView mScrollView;
    private int[] imgIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
//        MLog.init(true);
        initView();
        initData();
        initAction();
    }

    private void initView() {
        mScrollView = (BannerScrollView) findViewById(R.id.scrollView);

    }

    private void initData() {
        imgIds = new int[]{R.drawable.p1, R.drawable.p2, R.drawable.p3, R.drawable.p4, R.drawable.p5, R.drawable.p6, R.drawable.p7, R.drawable.p8};
        mScrollView.setData(imgIds);
    }

    private void initAction() {

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}

package com.example.niujun.njbv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    private BannerScrollView mScrollView;
    private Button mButton;
    private int[] imgIds;
    private Toast toast;

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
        mButton = (Button) findViewById(R.id.btnStatrt);

    }

    private void initData() {
//        imgIds = new int[]{R.drawable.p1, R.drawable.p2, R.drawable.p3, R.drawable.p4, R.drawable.p5, R.drawable.p6, R.drawable.p7, R.drawable.p8};
        imgIds = new int[]{R.drawable.guide1, R.drawable.guide2, R.drawable.guide3};
        mScrollView.setData(imgIds);
    }

    private void initAction() {

        mScrollView.setListener(new BannerScrollView.onStartOrEndListener() {
            @Override
            public void doFinish() {
                showMsg("结束");
//                toOtherActivity();
                if (!mButton.isShown()) {
                    showMsg("结束--------------");
                    mButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void doStart() {

                showMsg("开始");

            }
        });
    }

    private void toOtherActivity() {
        startActivity(new Intent(this, Main2Activity.class));
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private void showMsg(String msg) {
//        toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.show();
        Log.i("Nj", msg);
    }

}

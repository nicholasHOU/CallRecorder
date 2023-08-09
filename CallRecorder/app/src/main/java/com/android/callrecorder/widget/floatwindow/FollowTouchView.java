package com.android.callrecorder.widget.floatwindow;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.android.callrecorder.R;

import zuo.biao.library.util.ScreenUtil;

/**
 * Create by sun on 2020/7/31 5:16 PM
 */
public class FollowTouchView extends AbsFloatBase {

    private final int mScaledTouchSlop;

    public FollowTouchView(Context context) {
        super(context);

        mViewMode = WRAP_CONTENT_TOUCHABLE;
        mGravity = Gravity.START | Gravity.TOP;
        mAddX = ScreenUtil.getScreenWidth(context);
        mAddY = ScreenUtil.getScreenHeight(context) * 3 / 4;
        inflate(R.layout.floatview_touch);
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mInflate.setOnTouchListener(new View.OnTouchListener() {

            private float mLastY;
            private float mLastX;
            private float mDownY;
            private float mDownX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getRawX();
                float y = event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mDownX = x;
                        mDownY = y;
                        mLastX = x;
                        mLastY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:

                        float moveX = x - mLastX;
                        float moveY = y - mLastY;

                        Log.e("TAG", moveX + " " + moveY);

                        mLayoutParams.x += moveX;
                        mLayoutParams.y += moveY;

                        mWindowManager.updateViewLayout(mInflate, mLayoutParams);

                        mLastX = x;
                        mLastY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        float disX = x - mDownX;
                        float disY = y - mDownY;
                        double sqrt = Math.sqrt(Math.pow(disX, 2) + Math.pow(disY, 2));
                        if (sqrt < mScaledTouchSlop) {
                        }
                        break;
                }

                return false;
            }
        });
    }

    private Handler mImHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mImHandler.sendEmptyMessageDelayed(0, 5 * 1000);
        }
    };

    @Override
    public synchronized void show() {
        super.show();
        mImHandler.sendEmptyMessageDelayed(0, 5 * 1000);
    }

    @Override
    public void remove() {
        if (mHandler != null) {
            mImHandler.removeCallbacksAndMessages(null);
        }
        super.remove();
    }

    @Override
    protected void onAddWindowFailed(Exception e) {
    }


}

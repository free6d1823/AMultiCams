package com.example.allcams;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

public class OsdView extends SurfaceView implements SurfaceHolder.Callback, Runnable, View.OnTouchListener {

    static String TAG = "FocusView";
    private final Paint mPaint;
    private final Paint mPaintBorder;
    private final Paint mPaintFocused;
    private final Paint mPaintBackground;
    private final SurfaceHolder mHolder;

    public OsdView(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.setFormat(PixelFormat.TRANSPARENT);
        mHolder.addCallback(this);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setTextSize(20);

        mPaintBorder = new Paint();
        mPaintBorder.setColor(Color.YELLOW);
        mPaintBorder.setStyle(Paint.Style.STROKE);
        mPaintBorder.setStrokeWidth(2);
        mPaintFocused = new Paint();
        mPaintFocused.setColor(Color.RED);
        mPaintFocused.setStyle(Paint.Style.STROKE);
        mPaintFocused.setStrokeWidth(2);

        mPaintBackground = new Paint();
        mPaintBackground.setColor(Color.BLUE);
        setFocusable(true);
        setZOrderOnTop(true);
        setFocusableInTouchMode(true);
        setVisibility(VISIBLE);
        Log.d(TAG, "FocusView created.");
        setOnTouchListener(this);


    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        new Handler().post(this);
        Log.d(TAG, "FocusView surfaceCreated ");
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "FocusView surfaceChanged "+ width +"x"+height);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
         if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //invalidate();
            if (mHolder.getSurface().isValid()) {
                mState = STATE_FOCUSED;
                final Canvas canvas = mHolder.lockCanvas();
                if (canvas != null) {
                    int w = canvas.getWidth()-1;
                    int h = canvas.getHeight()-1;
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    canvas.drawColor(Color.TRANSPARENT);
                    canvas.drawRect(new Rect(1,1,w,h), mPaintFocused);
                    canvas.drawCircle(event.getX(), event.getY(), 20, mPaint);
                    mHolder.unlockCanvasAndPost(canvas);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mState = STATE_NORMAL;
                            draw();
                        }
                    }, 1000);

                }
                //mHolder.unlockCanvasAndPost(canvas);
            }
        }

        return false;
    }
    private void draw() {
        Canvas canvas = mHolder.lockCanvas();
        if (canvas != null ) {
            int w = canvas.getWidth() - 1;
            int h = canvas.getHeight() - 1;
            if (canvas != null) {
                switch (mState) {
                    case STATE_OFF:
                        Log.d(TAG, "draw OFF BLUE ");
                        canvas.drawRect(new Rect(1, 1, w, h), mPaintBackground);
                        break;
                    case STATE_NORMAL:
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        canvas.drawRect(new Rect(1, 1, w, h), mPaintBorder);
                        break;
                    case STATE_FOCUSED:
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        canvas.drawRect(new Rect(1, 1, w, h), mPaintFocused);
                        break;
                    default:
                        break;
                }
                if (!mTitle.isEmpty()) {
                    canvas.drawText(mTitle, 10, 26, mPaint);
                }
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
    final static int STATE_OFF = 0;
    final static int STATE_NORMAL = 1;
    final static int STATE_FOCUSED = 2;
    int mState = 0;
    @Override
    public void run() {
        draw();

    }
    String mTitle;
    public void setTitle(String title)
    {
        mTitle = title;
        new Handler().post(this);
    }
    public void setState(int state)
    {
        mState = state;
        new Handler().post(this);
    }
}
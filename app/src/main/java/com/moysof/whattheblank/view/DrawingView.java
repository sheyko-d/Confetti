package com.moysof.whattheblank.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.moysof.whattheblank.R;
import com.moysof.whattheblank.util.Util;

public class DrawingView extends View {
    private Context mContext;
    private Paint paint = new Paint();
    private Path path = new Path();
    private boolean mClear = false;
    private boolean mIsEmpty = true;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mIsEmpty = true;
        setupPaint();
    }

    // Setup paint with color and stroke styles
    private void setupPaint() {
        paint.setAntiAlias(true);
        paint.setStrokeWidth(Util.convertDpToPixel(8));
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

    }

    public void clear() {
        mClear = true;
        mIsEmpty = true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mClear) {
            canvas.drawColor(mContext.getResources().getColor(R.color.draw_bg),
                    PorterDuff.Mode.SRC);
            mClear = false;
            path.reset();
        }
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the coordinates of the touch event.
        float eventX = event.getX();
        float eventY = event.getY();


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(eventX, eventY);

                // Draw a dot
                eventX++;
                path.lineTo(eventX, eventY);

                mIsEmpty = false;

                return true;
            case MotionEvent.ACTION_MOVE:

                // Connect the points
                path.lineTo(eventX, eventY);

                break;
            default:
                return false;
        }

        // Makes our view repaint and call onDraw
        invalidate();
        return true;
    }

    public Boolean isEmpty(){
        return mIsEmpty;
    }
}
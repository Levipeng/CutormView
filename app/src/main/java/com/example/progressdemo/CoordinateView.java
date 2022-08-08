package com.example.progressdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @Description 坐标轴柱状图
 * @Author pt
 * @Date 2022/8/5 14:24
 */
public class CoordinateView extends View {
    //坐标轴画笔
    private Paint mXYPaint;
    //坐标轴距离左边的间距
    private float mLeftWidth;
    //坐标轴Y的高度
    private float mYHeight;

    public CoordinateView(Context context) {
        this(context,null);
    }

    public CoordinateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CoordinateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initPaint();
        mLeftWidth=dp2px(getContext(),50);
        mYHeight=dp2px(getContext(),200);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        //坐标轴画笔
        mXYPaint= new Paint();
        mXYPaint.setAntiAlias(true);
        mXYPaint.setStyle(Paint.Style.FILL);
        mXYPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制坐标系
        canvas.drawLine(mLeftWidth,0,mLeftWidth,mYHeight,mXYPaint);
        canvas.drawLine(mLeftWidth,mYHeight,getWidth(),mYHeight,mXYPaint);

    }

    public int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.getResources().getDisplayMetrics());
    }

}

package com.example.progressdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * 扇形图
 */
public class ProgressCircle extends View {
    private float mRingBias = 0.15f;
    protected float mRadius;
    //格子数 总进度
    protected int mMaxProgress;
    //当前进度
    protected int mProgress;

    protected float mCenterX;
    protected float mCenterY;

    private Paint mPaint;
    private int mColor1;
    private int mColor2;
    private int mInactiveColor;
    private int mBackgroundColor;
    private float angleDu; //


    private Paint mPaint1;

    private Paint mPaint2;

    private Paint textPaint;

    private Paint textAllPaint;

    private Paint textCountPaint;
    private int maxNumber = 500;//设置最大分数，默认500
    private int currentNumber = 400;//设置当前数字
    private float scale;
    private float realDU;


    public ProgressCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initAttributes(context, attrs);
    }

    public ProgressCircle(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAttributes(context, attrs);
    }

    public ProgressCircle(Context context) {
        super(context);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        mMaxProgress = 17;
        mProgress = 14;
    }

    private void updateDimensions(int width, int height) {

        mCenterX = width / 2.0f;
        mCenterY = height / 2.0f;

        int diameter = Math.min(width, height);

        float outerRadius = diameter / 2;
        float sectionHeight = (float) (2.5 * outerRadius * mRingBias);
        mRadius = outerRadius - sectionHeight / 2;
        angleDu = (float) (Math.PI / 180f);
        scale = width / 1080f;
        translation = translation * scale;

        mBackgroundColor=Color.parseColor("#E6F1EF");
        initPaint();
    }
    private String endColor="#3BD298";
    private String startColor="#DCF5EC";
    private void initPaint() {
        mMaxProgress = 17;
        mProgress = 0;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        //渐变开始颜色
        mColor1 = Color.parseColor(startColor);
        //渐变结束颜色
        mColor2 = Color.parseColor(endColor);
        mInactiveColor = Color.parseColor("#E2E6EA");
        mPaint.setColor(mColor1);
        //格子画笔
        mPaint1 = new Paint();
        mPaint1.setAntiAlias(true);
        mPaint1.setStyle(Paint.Style.FILL);
        mPaint1.setColor(mColor1);

        //外圈画笔
        mPaint2 = new Paint();
        mPaint2.setAntiAlias(true);
        mPaint2.setStrokeWidth(10);
        mPaint2.setStyle(Paint.Style.STROKE);
        mPaint2.setColor(mInactiveColor);

        //左下脚右下角文本画笔
        textPaint = new Paint();
        textPaint.setTextSize(dp2px(getContext(), 20 * scale)); // 文字大小
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setColor(mInactiveColor);

        //中间总分画笔
        textAllPaint = new Paint();
        textAllPaint.setTextSize(dp2px(getContext(), 30 * scale)); // 文字大小
        textAllPaint.setAntiAlias(true);
        textAllPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textAllPaint.setColor(Color.parseColor("#666666"));

        //中间分数画笔
        textCountPaint = new Paint();
        textCountPaint.setTextSize(dp2px(getContext(), 60 * scale)); // 文字大小
        textCountPaint.setAntiAlias(true);
        textCountPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textCountPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textCountPaint.setColor(mColor2);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateDimensions(w, h);
    }

    private float translation = 110;

    @Override
    protected void onDraw(Canvas canvas) {


        float arcLeft = mCenterX - mRadius;
        float arcTop = mCenterY - mRadius;
        float arcRight = mCenterX + mRadius;
        float arcBottom = mCenterY + mRadius;
        RectF arcRF0 = new RectF(arcLeft, arcTop, arcRight, arcBottom);
        //外圈
        RectF arcRF1 = new RectF(arcLeft - 30, arcTop - 30, arcRight + 30, arcBottom + 30);
        Paint PaintArc = new Paint();
        PaintArc.setAntiAlias(true);

        float Percentage = 0.0f;
        float CurrPer = -225.0f;
        float wPer = 3.0f;//间隔度数
        //绘制内部白色扇形
        canvas.drawArc(arcRF1, CurrPer, 270, false, mPaint2);
        for (int i = 0; i < mMaxProgress; i++) {
            //每个格子所占的度数
            Percentage = (270 - (mMaxProgress - 1) * wPer) / mMaxProgress;

            if (i < mProgress) {
                float bias = (float) i / (float) (mMaxProgress);
                int color = interpolateColor(mColor1, mColor2, bias);
                mPaint.setColor(color);
                canvas.drawArc(arcRF0, CurrPer, Percentage, true, mPaint);
            } else {
                canvas.scale(1.0f, 1.0f);
                float v =Math.abs(realDU - (CurrPer + 225.0f)) ;
                if (v<Percentage && i==mProgress){
                    //增加的一部分
                    float bias = (float) i / (float) (mMaxProgress);
                    int color = interpolateColor(mColor1, mColor2, bias);
                    mPaint.setColor(color);
                    canvas.drawArc(arcRF0, CurrPer, v, true, mPaint);
                    CurrPer = CurrPer + v;
                    mPaint.setColor(mInactiveColor);
                    canvas.drawArc(arcRF0,CurrPer,Percentage-v,true,mPaint);
                    CurrPer = CurrPer -v;

                }else{
                    mPaint.setColor(mInactiveColor);
                    canvas.drawArc(arcRF0, CurrPer, Percentage, true, mPaint);
                }
            }


            CurrPer = CurrPer + Percentage + wPer;
        }

        PaintArc.setColor(mBackgroundColor);
        canvas.drawCircle(mCenterX, mCenterY, (int) (mRadius / 1.2), PaintArc);
        //绘制梯形
        //计算三个点的坐标
        //根据勾股定理求出xy的坐标，绘制小圆

        float cx = (float) Math.cos(135 * angleDu) * (mRadius) + mCenterX;
        float cy = (float) Math.sin(135 * angleDu) * (mRadius) + mCenterY + translation;
        float cx1 = (float) Math.cos(45 * angleDu) * (mRadius) + mCenterX;
        float cy1 = (float) Math.sin(45 * angleDu) * (mRadius) + mCenterY + translation;

        //为Paint设置渐变器
        Shader mShasder = new LinearGradient(mCenterX, mCenterY, mCenterX, mCenterY + mRadius + 80, new int[]{mBackgroundColor,mBackgroundColor,mColor2, mColor2}, new float[]{0f, 0.55f,0.8f, 1f}, Shader.TileMode.CLAMP);
        Path path = new Path();
        //translation代表三角形向下位移距离
        path.moveTo(mCenterX, mCenterY + translation);
        path.lineTo(cx, cy);
        path.lineTo(cx1, cy1);
        path.close();
        mPaint1.setShader(mShasder);
        canvas.drawPath(path, mPaint1);

        //绘制文本
        float txtX = (float) Math.cos(135 * angleDu) * (mRadius + 30) + mCenterX;
        float txty = (float) Math.sin(135 * angleDu) * (mRadius + 30) + mCenterY;
        float textWidth = textPaint.measureText("0");
        //左边文本
        canvas.drawText("0", txtX - (textWidth / 2f), txty - textPaint.getFontMetrics().top, textPaint);
        //右边文本
        float txtRightX = (float) Math.cos(45 * angleDu) * (mRadius + 30) + mCenterX;
        float txtRightY = (float) Math.sin(45 * angleDu) * (mRadius + 30) + mCenterY;
        float textRightWidth = textPaint.measureText(maxNumber+"");
        //左边文本
        canvas.drawText(String.valueOf(maxNumber), txtRightX - (textRightWidth / 2f), txtRightY - textPaint.getFontMetrics().top, textPaint);
        //绘制总分,位于三角顶点的下方
        float allWidth = textAllPaint.measureText("总分");
        canvas.drawText("总分", mCenterX - allWidth / 2, mCenterY - textAllPaint.getFontMetrics().top + dp2px(getContext(), 15 * scale), textAllPaint);
        //绘制分数
        float measureTextWidth = textCountPaint.measureText(currentNumber + "");
        canvas.drawText(String.valueOf(currentNumber), mCenterX - measureTextWidth / 2, mCenterY, textCountPaint);
        super.onDraw(canvas);
    }

    private float interpolate(float a, float b, float bias) {
        return (a + ((b - a) * bias));
    }

    private int interpolateColor(int colorA, int colorB, float bias) {
        float[] hsvColorA = new float[3];
        Color.colorToHSV(colorA, hsvColorA);

        float[] hsvColorB = new float[3];
        Color.colorToHSV(colorB, hsvColorB);

        hsvColorB[0] = interpolate(hsvColorA[0], hsvColorB[0], bias);
        hsvColorB[1] = interpolate(hsvColorA[1], hsvColorB[1], bias);
        hsvColorB[2] = interpolate(hsvColorA[2], hsvColorB[2], bias);

        if (isInEditMode())
            return colorA;

        return Color.HSVToColor(hsvColorB);
    }

    /**
     * 设置最大数字
     *
     * @param maxNumber     最大分数
     * @param currentNumber 当前分数
     * @param startColor 开始颜色
     * @param endColor 结束颜色
     * @param backColor 背景色
     */
    public void setMaxNumber(int maxNumber, int currentNumber,String startColor,String endColor,String backColor) {
        this.mBackgroundColor=Color.parseColor(backColor);
        this.startColor=startColor;
        this.endColor=endColor;
        this.maxNumber = maxNumber;
        this.currentNumber = currentNumber;
        initPaint();
        //根据分数计算当前mProgress
        float v = ((float) currentNumber / maxNumber);
        //换算成°
        realDU = v * 270;
        mProgress = (int) (v * mMaxProgress);
        //刻度采用动画
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, mProgress);
        valueAnimator.setDuration(500);
        //线性变换插值器
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (int) animation.getAnimatedValue();
                Log.d("mProgress", mProgress + "");
                postInvalidate();
            }
        });
        valueAnimator.start();
        //分数采用动画
        ValueAnimator valueAnimator1 = ValueAnimator.ofInt(0, currentNumber);
        valueAnimator1.setDuration(500);
        //线性变换插值器
        valueAnimator1.setInterpolator(new LinearInterpolator());
        valueAnimator1.setRepeatCount(0);
        valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ProgressCircle.this.currentNumber = (int) animation.getAnimatedValue();
                Log.d("currentNumber", currentNumber + "");
                postInvalidate();
            }
        });
        valueAnimator1.start();
        initPaint();
    }


    public int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.getResources().getDisplayMetrics());
    }

}

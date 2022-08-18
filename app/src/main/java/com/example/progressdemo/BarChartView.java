package com.example.progressdemo;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;




import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * 柱状图
 */
public class BarChartView extends View {
    
    /**
     * 柱状图垂直叠加
     */
    public static final int TYPE_VERTICAL = 0;
    
    /**
     * 柱状图并列
     */
    public static final int TYPE_HORIZONTAL = 1;
    
    /**
     * 柱状图合并方式，垂直叠加或者并列
     */
    private int mBarType;
    
    
    private Context mContext;
    /**
     * 视图的宽和高  刻度区域的最大值
     */
    private int mTotalWidth, mTotalHeight, mMaxHeight;
    private int mPaddingRight, mPaddingBottom, mPaddingTop;
    /**
     * 柱形图的颜色集合
     */
    private int[] mBarColors;
    private int[] mBarEndColors;
    private String[] mBarLevelLabel;
    /**
     * 底部缩进
     */
    private int mBottomMargin;
    /**
     * 顶部缩进
     */
    private int mTopMargin;
    /**
     * 右边缩进
     */
    private int mRightMargin;
    /**
     * 左边缩进
     */
    private int mLeftMargin;
    /**
     * 画笔 轴 刻度 柱子 点击后的柱子 单位
     */
    private Paint mAxisPaint, mTextPaint, mBarPaint, mBorderPaint;
    private List<BarChartEntity> mData = new ArrayList<>();
    /**
     * item中的Y轴最大值
     */
    private float mMaxYValue;
    /**
     * Y轴最大的刻度值
     */
    private float mMaxYDivisionValue;
    /**
     * 柱子的矩形
     */
    private RectF mBarRect, mBarRectClick;
    /**
     * 绘制的区域
     */
    private RectF mDrawArea;
    /**
     * 每一个bar的宽度
     */
    private int mBarWidth;
    /**
     * 每个bar之间的距离
     */
    private int mBarSpace;
    /**
     * 向右边滑动的距离
     */
    private float mLeftMoving;
    /**
     * 左后一次的x坐标
     */
    private float mLastPointX;
    /**
     * 当前移动的距离
     */
    private float mMovingThisTime = 0.0f;
    /**
     * 右边的最大和最小值
     */
    private int mMaxRight, mMinRight;
    /**
     * 下面两个相当于图表的原点
     */
    private float mStartX;
    private int mStartY;
    /**
     * 柱形图左边的x轴坐标 和右边的x轴坐标
     */
    private final List<Integer> mBarLeftXPoints = new ArrayList<>();
    private final List<Integer> mBarRightXPoints = new ArrayList<>();
    
    /**
     * 是否隐藏Y坐标
     */
    private boolean isHideAxisY;
    /**
     * 是否隐藏X坐标
     */
    private boolean isHideAxisX;
    
    /**
     * 用户点击到了无效位置
     */
    public static final int INVALID_POSITION = -1;
    private OnItemBarClickListener mOnItemBarClickListener;
    private GestureDetector mGestureListener;
    /**
     * 是否绘制点击效果
     */
    private boolean isDrawBorder;
    /**
     * 点击的地方
     */
    private int mClickPosition;
    
    //滑动速度相关
    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;
    /**
     * fling最大速度
     */
    private int mMaxVelocity;
    /**
     * 单位标题X坐标
     */
    private String mUnitTiltleX;
    private String mUnitNameX;
    /**
     * 单位标题Y坐标
     */
    private String mUnitTitleY;
    /**
     * 单位名称
     */
    private String mUnitNameY;
    
    /**
     * 柱状顶部显示个数
     */
    private boolean isShowBarValue;
    
    public void setOnItemBarClickListener(OnItemBarClickListener onRangeBarClickListener) {
        this.mOnItemBarClickListener = onRangeBarClickListener;
    }
    
    public interface OnItemBarClickListener {
        void onClick(int position,float x,float y);
    }
    
    public BarChartView(Context context) {
        super(context);
        init(context);
    }
    
    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public BarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    public int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.getResources().getDisplayMetrics());
    }

    private void init(Context context) {
        mContext = context;
        mBarWidth = dp2px(mContext,20);
        mBarSpace =dp2px(mContext,20);
        mTopMargin =dp2px(mContext,20);
        mBottomMargin = dp2px(mContext,10);
        mRightMargin = 0;
        mLeftMargin = 0;
        mBarType = TYPE_VERTICAL;
        mUnitNameY = "";
        mUnitNameX = "";
        mMaxYValue = 100;
        
        mScroller = new Scroller(context);
        mMaxVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mGestureListener = new GestureDetector(context, new RangeBarOnGestureListener());
        
        mAxisPaint = new Paint();
        mAxisPaint.setColor(Color.parseColor("#999999"));
        mAxisPaint.setStrokeWidth(dp2px(mContext,0.5f));
        
        mTextPaint = new Paint();
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(dp2px(mContext,10));
        
        mBarPaint = new Paint();
        mBarPaint.setAntiAlias(true);
        mBarPaint.setColor(mBarColors != null && mBarColors.length > 0 ? mBarColors[0] : Color.parseColor("#6FC5F4"));
        
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.FILL);
        mBorderPaint.setColor(Color.rgb(0, 0, 0));
        mBorderPaint.setAlpha(120);
        
        mBarRect = new RectF(0, 0, 0, 0);
        mBarRectClick = new RectF(0, 0, 0, 0);
        mDrawArea = new RectF(0, 0, 0, 0);
        
        setData(mData, 100, 0);
    }
    
    
    public void initChart(int[] colors, String[] barLevelLabel, String mUnitX, String mUnitY) {
        this.mBarColors = colors;
        this.mBarLevelLabel = barLevelLabel;
        this.mUnitTiltleX = mUnitX;
        this.mUnitTitleY = mUnitY;
        
        mBarEndColors = new int[mBarColors.length];
        for (int i = 0; i < mBarColors.length; i++) {
            mBarEndColors[i] = mBarColors[i] & 0x00FFFFFF;
            mBarEndColors[i] = mBarEndColors[i] | 0x66000000;
        }
    }
    
    public void initChart(int[] colors, int[] endColors, String[] barLevelLabel, String mUnitX, String mUnitY) {
        this.mBarColors = colors;
        this.mBarEndColors = endColors;
        this.mBarLevelLabel = barLevelLabel;
        this.mUnitTiltleX = mUnitX;
        this.mUnitTitleY = mUnitY;
    }
    
    public void setBarWidth(int barWidth) {
        mBarWidth = barWidth;
    }
    
    public void setHideAxisY(boolean hideAxisY) {
        isHideAxisY = hideAxisY;
    }
    
    public void setBarSpace(int barSpace) {
        mBarSpace = barSpace;
    }
    
    public void setShowBarValue(boolean showBarValue) {
        isShowBarValue = showBarValue;
    }
    
    public void setUnitNameX(String unitNameX) {
        mUnitNameX = unitNameX;
    }
    
    public void setUnitNameY(String unitNameY) {
        mUnitNameY = unitNameY;
    }
    
    public void setBarType(int barType) {
        mBarType = barType;
    }
    
    /**
     * @param list
     * @param maxValue Y坐最大值,0的话不限制
     * @param minValue Y坐标最小值
     */
    public void setData(List<BarChartEntity> list, int maxValue, int minValue) {
        this.mData = list;
        if (list != null && list.size() > 0) {
            mMaxYValue = calculateMaxValueY(list);
        }
        if (maxValue > 0 && mMaxYValue > maxValue) {
            mMaxYValue = maxValue;
        }
        if (minValue > 0 && mMaxYValue < minValue) {
            mMaxYValue = minValue;
        }
        getRange(mMaxYValue);
        calculateOffsetY();
        
        if (!TextUtils.isEmpty(mUnitTiltleX)) {
            mRightMargin = (int) (mTextPaint.measureText(mUnitTiltleX) +dp2px(mContext,5));
        } else {
            mRightMargin = dp2px(mContext,5);
        }
        
    }
    
    /**
     * 对颜色进行透明值0x66
     *
     * @param color
     * @return
     */
    private int calculateAlphaColor(int color) {
        int newColor = color & 0x00FFFFFF;
        
        newColor = newColor | 0x66000000;
        return newColor;
    }
    
    /**
     * 计算出Y轴最大值
     *
     * @return
     */
    private float calculateMaxValueY(List<BarChartEntity> list) {
        float start = list.get(0).getSum();
        for (BarChartEntity entity : list) {
            if (entity.getSum() > start) {
                start = entity.getSum();
            }
        }
        return start;
    }
    
    /**
     * 得到柱状图的最大和最小的分度值
     */
    private void getRange(float maxYValue) {
        int scale = CalculateUtil.getScale(maxYValue);//获取这个最大数 数总共有几位
        float value = (float) Math.pow(10, scale);
        float unScaleValue = (float) (maxYValue / value);//最大值除以位数之后剩下的值  比如1200/1000 后剩下1.2
        mMaxYDivisionValue = (float) (CalculateUtil.getRangeTop(unScaleValue) * value);//获取Y轴的最大的分度值
        BigDecimal b = new BigDecimal(mMaxYDivisionValue);
        mMaxYDivisionValue = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        if (mMaxYDivisionValue > maxYValue) {
            mMaxYDivisionValue = maxYValue;
        }
        mStartX = CalculateUtil.getDivisionTextMaxWidth(mMaxYDivisionValue, mUnitNameY);
        
        if (!TextUtils.isEmpty(mUnitTitleY) && mStartX < mTextPaint.measureText(mUnitTitleY)) {
            mStartX = mTextPaint.measureText(mUnitTitleY) + dp2px(mContext,10);;
        } else {
            mStartX = mStartX +dp2px(mContext,10);;
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;
        mMaxHeight = h - getPaddingTop() - getPaddingBottom() - mBottomMargin - mTopMargin - (int) mYOffset;
        
        if (mBarLevelLabel != null) {
            mMaxHeight = (int) (mMaxHeight * 0.8);
        } else {
            mMaxHeight = (int) (mMaxHeight * 0.8);
        }
        mPaddingBottom = getPaddingBottom();
        mPaddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();
        
    }
    
    //获取滑动范围和指定区域
    private void getArea() {
        if (mBarType == TYPE_VERTICAL) {
            mMaxRight = (int) (mStartX + (mBarSpace + mBarWidth) * mData.size()) + mBarSpace;
        } else {
            if (mBarColors.length == 2) {
                mMaxRight = (int) (mStartX + (mBarSpace + mBarWidth + mBarWidth) * mData.size()) + mBarSpace;
            } else {
                mMaxRight = (int) (mStartX + (mBarSpace + mBarWidth) * mData.size()) + mBarSpace;
            }
        }
        mMinRight = mTotalWidth - mLeftMargin - mRightMargin;
        mStartY = mTotalHeight - mBottomMargin - mPaddingBottom - (int) mYOffset;
        mDrawArea = new RectF(mStartX, mPaddingTop, mTotalWidth - mPaddingRight - mRightMargin, mTotalHeight - mPaddingBottom);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        if (mData == null || mData.isEmpty()) return;
        if (mData == null) return;
        getArea();
        checkTheLeftMoving();
        //绘制刻度线 和 刻度
        drawYAxisAndText(canvas);
        //绘制单位
        drawUnit(canvas);
        //颜色类型标签
        drawBarTypeLabel(canvas);
        //调用clipRect()方法后，只会显示被裁剪的区域
        canvas.clipRect(mDrawArea.left, mDrawArea.top, mDrawArea.right, mDrawArea.bottom + mDrawArea.height());
        //绘制柱子
        drawBar(canvas);
        //绘制X轴的text
        drawXAxisAndText(canvas);
    }
    
    private void drawBarTypeLabel(Canvas canvas) {
        if (mBarLevelLabel == null) {
            return;
        }
        mTextPaint.setAntiAlias(true);
//        Typeface typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mTextPaint.setTextSize(dp2px(mContext,10));
        int circleWidth =dp2px(mContext,4);;
        float startX = mDrawArea.right;
        for (int i = 0; i < mBarLevelLabel.length; i++) {
            mTextPaint.setColor(Color.parseColor("#333333"));
            startX = startX - mTextPaint.measureText(mBarLevelLabel[i]);
            canvas.drawText(mBarLevelLabel[i], startX, mTopMargin + mPaddingTop, mTextPaint);
            startX = (float) (startX - circleWidth * 1.2);
            if (mBarType == TYPE_HORIZONTAL) {
                mTextPaint.setColor(mBarColors[mBarLevelLabel.length - i - 1]);
            } else {
                mTextPaint.setColor(mBarColors[i]);
                
            }
            canvas.drawCircle(startX, mTopMargin + mPaddingTop - circleWidth, circleWidth, mTextPaint);
            startX -=dp2px(mContext,20);;
        }
        
    }
    
    protected Rect mRect = new Rect();
    
    private void drawUnit(Canvas canvas) {
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.parseColor("#999999"));
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mTextPaint.setTextSize(dp2px(mContext,8));
        
        if (!TextUtils.isEmpty(mUnitTitleY)) {
            canvas.drawText(mUnitTitleY, dp2px(mContext,8), mStartY - mMaxHeight - dp2px(mContext,215),
                    mTextPaint);
        }
        
        if (!TextUtils.isEmpty(mUnitTiltleX)) {
            mTextPaint.getTextBounds(mUnitTiltleX, 0, mUnitTiltleX.length(), mRect);
            //画X坐标单位标签
            canvas.drawText(mUnitTiltleX, mMinRight + 5, mStartY + (mRect.bottom - mRect.top) / 2, mTextPaint);
        }
    }
    
    /**
     * 检查向左滑动的距离 确保没有画出屏幕
     */
    private void checkTheLeftMoving() {
        if (mLeftMoving > (mMaxRight - mMinRight)) {
            mLeftMoving = mMaxRight - mMinRight;
        }
        if (mLeftMoving < 0) {
            mLeftMoving = 0;
        }
    }

//    private void drawXAxisText(Canvas canvas) {
//        //这里设置 x 轴的字一条最多显示3个，大于三个就换行
//        for (int i = 0; i < mData.size(); i++) {
//            String text = mData.get(i).getxLabel();
//            if (text.length() <= 3) {
//                canvas.drawText(text, mBarLeftXPoints.get(i) - (mTextPaint.measureText(text) - mBarWidth) / 2, mTotalHeight - mBottomMargin * 2 / 3
//                        , mTextPaint);
//            } else {
//                String text1 = text.substring(0, 3);
//                String text2 = text.substring(3, text.length());
//                canvas.drawText(text1, mBarLeftXPoints.get(i) - (mTextPaint.measureText(text1) - mBarWidth) / 2,
//                        mTotalHeight - mBottomMargin * 2 / 3, mTextPaint);
//                canvas.drawText(text2, mBarLeftXPoints.get(i) - (mTextPaint.measureText(text2) - mBarWidth) / 2, mTotalHeight - mBottomMargin / 3,
//                        mTextPaint);
//            }
//        }
//    }
    
    /**
     * 计算Y坐偏移
     */
    private void calculateOffsetY() {
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mTextPaint.setTextSize(dp2px(mContext,10));
        if (mData != null && !mData.isEmpty()) {
            float yOffset = mTextPaint.measureText(mData.get(0).getxLabel());
            for (int i = 1; i < mData.size(); i++) {
                float curLength = mTextPaint.measureText(mData.get(i).getxLabel());
                if (curLength > mYOffset) {
                    yOffset = curLength;
                }
            }
            mYOffset = (float) (yOffset * Math.cos(Math.PI * mTextAngle / 180));
            float xOffset = (float) (yOffset * Math.sin(Math.PI * mTextAngle / 180));
            if (xOffset > mBarSpace) {
                mXOffset = xOffset - mBarSpace;
            }
        }
    }
    
    private final Path mTextPath = new Path();
    private float mYOffset;
    private float mXOffset;
    /**
     * X左边标签旋转度数
     */
    private float mTextAngle = 35;
    
    private void drawXAxisAndText(Canvas canvas) {
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.parseColor("#999999"));
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mTextPaint.setTextSize(dp2px(mContext,8));
//        mTextPaint.setTextSize(Dimens.dpToPx(10));
//        mTextPaint.setColor(Color.parseColor("#333333"));
//        if (mTextType == TEXT_TYPE_HORIZONTAL) {
//            //这里设置 x 轴的字一条最多显示3个，大于三个就换行
//            for (int i = 0; i < mData.size(); i++) {
//                String text = mData.get(i).getxLabel();
//                if (text.length() <= 3) {
//                    canvas.drawText(text, mBarLeftXPoints.get(i) - (mTextPaint.measureText(text) - mBarWidth) / 2, mTotalHeight - mBottomMargin *
//                    2 / 3, mTextPaint);
//                } else {
//                    String text1 = text.substring(0, 3);
//                    String text2 = text.substring(3, text.length());
//                    canvas.drawText(text1, mBarLeftXPoints.get(i) - (mTextPaint.measureText(text1) - mBarWidth) / 2, mTotalHeight - mBottomMargin
//                    * 2 / 3, mTextPaint);
//                    canvas.drawText(text2, mBarLeftXPoints.get(i) - (mTextPaint.measureText(text2) - mBarWidth) / 2, mTotalHeight - mBottomMargin
//                    / 3, mTextPaint);
//                }
//            }
//        } else {
        
        for (int i = 0; i < mData.size(); i++) {
            mTextPath.reset();
            String text = mData.get(i).getxLabel();
            float textLength = mTextPaint.measureText(text);
            float xOffset = (float) (textLength * Math.sin(Math.PI * mTextAngle / 180));
            float yOffset = (float) (textLength * Math.cos(Math.PI * mTextAngle / 180));
            if (TYPE_HORIZONTAL == mBarType && mBarColors.length == 2) {
                
                mTextPath.moveTo((float) (mBarLeftXPoints.get(i) + mBarWidth * 1.5 - xOffset), mTotalHeight - mBottomMargin / 2 - mYOffset + yOffset);
                mTextPath.lineTo((float) (mBarLeftXPoints.get(i) + mBarWidth * 1.5), mTotalHeight - mBottomMargin / 2 - mYOffset);
            } else {
                mTextPath.moveTo(mBarLeftXPoints.get(i) + mBarWidth / 2 - xOffset, mTotalHeight - mBottomMargin / 2 - mYOffset + yOffset);
                mTextPath.lineTo(mBarLeftXPoints.get(i) + mBarWidth / 2, mTotalHeight - mBottomMargin / 2 - mYOffset);
            }
            canvas.drawTextOnPath(text, mTextPath, 0, 0, mTextPaint);
        }
//        }
    
    }
    
    private float percent = 1f;
    private final TimeInterpolator pointInterpolator = new DecelerateInterpolator();
    
    public void startAnimation() {
        ValueAnimator mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(600);
        mAnimator.setInterpolator(pointInterpolator);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                percent = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.start();
    }
    
    private void drawBar(Canvas canvas) {
        mBarLeftXPoints.clear();
        mBarRightXPoints.clear();
        mBarRect.bottom = mStartY;
        int radiusWidth = dp2px(mContext,4);
        BarChartEntity barChartEntity;
        
        mTextPaint.setTextSize(dp2px(mContext,8));
        float barLabelWidth;
        String barLabel;
        for (int index = 0; index < mData.size(); index++) {
            barChartEntity = mData.get(index);
            if (mBarType == TYPE_VERTICAL) {
                if (mBarColors.length == 1) {
                    
                    mBarRect.left = (int) (mStartX + mBarWidth * index + mBarSpace * (index + 1) - mLeftMoving) + (int) mXOffset;
                    mBarRect.top = mStartY - (int) ((mMaxHeight * (barChartEntity.getyValue()[0] / mMaxYDivisionValue)) * percent);
                    mBarRect.right = mBarRect.left + mBarWidth;
                    
                    if (barChartEntity.getSelectColor() != 0) {
                        LinearGradient linearGradient = new LinearGradient(mBarRect.left, mBarRect.top, mBarRect.left, mBarRect.bottom,
                                barChartEntity.getSelectColor(), calculateAlphaColor(barChartEntity.getSelectColor()),
                                Shader.TileMode.MIRROR);
                        //设置垂直向下渐变
                        mBarPaint.setShader(linearGradient);
                        canvas.drawRoundRect(mBarRect, mBarWidth >> 1, mBarWidth >> 1, mBarPaint);
                    } else {
                        LinearGradient linearGradient = new LinearGradient(mBarRect.left, mBarRect.top, mBarRect.left, mBarRect.bottom,
                                mBarColors[0], mBarEndColors[0],
                                Shader.TileMode.MIRROR);
                        //设置垂直向下渐变
                        mBarPaint.setShader(linearGradient);
                        canvas.drawRoundRect(mBarRect, mBarWidth >> 1, mBarWidth >> 1, mBarPaint);
                    }
                    
                    
                    if (isShowBarValue && percent == 1) {
                        barLabel = String.format("%d%s", barChartEntity.getyValue()[0].intValue(), mUnitNameY);
                        barLabelWidth = mTextPaint.measureText(barLabel);
                        float startX = mBarRect.left;
                        float startY = mBarRect.top;
                        if (barChartEntity.getImgResId() > 0) {
                            Bitmap bitmap =createBitmap(mContext,(int) (mBarWidth * 1.2), (int) (mBarWidth * 1.5), barChartEntity.getImgResId());
                            canvas.drawBitmap(bitmap, startX + mBarWidth / 2 - 5, startY - bitmap.getHeight(), mTextPaint);
                            startY = startY - bitmap.getHeight();
                        }
                        
                        canvas.drawText(barLabel, startX + (mBarWidth - barLabelWidth) / 2, startY - 5, mTextPaint);
                    }
                } else {
                    int eachHeight = 0;//每一块的高度
                    mBarRect.left = (int) (mStartX + mBarWidth * index + mBarSpace * (index + 1) - mLeftMoving) + (int) mXOffset;
                    mBarRect.right = mBarRect.left + mBarWidth;
                    for (int j = 0; j < mBarColors.length; j++) {
                        mBarPaint.setColor(mBarColors[j]);
                        mBarRect.bottom = (int) (mStartY - eachHeight * percent);
                        
                        mBarRect.top = (int) (mBarRect.bottom - ((mMaxHeight * (barChartEntity.getyValue()[j] / mMaxYDivisionValue))) * percent);
//                        if (j == 0) {
////                        canvas.drawRoundRect(mBarRect,   Dimens.dpToPx( 2),   Dimens.dpToPx( 2), mBarPaint);
////                        mBarRect.bottom = mBarRect.top + (((int) (mBarRect.bottom - mBarRect.top)) >> 1);
//                            canvas.drawRect(mBarRect, mBarPaint);
//                        } else if (j == (mBarColors.length - 1)) {
//                            //画圆角
//                            canvas.drawRoundRect(mBarRect, radiusWidth, radiusWidth, mBarPaint);
//                            mBarRect.top = mBarRect.bottom - (((int) (mBarRect.bottom - mBarRect.top)) >> 1);
//                            canvas.drawRect(mBarRect, mBarPaint);
//                        } else {
                        canvas.drawRect(mBarRect, mBarPaint);
//                        }
                        
                        eachHeight += (int) ((mMaxHeight * (barChartEntity.getyValue()[j] / mMaxYDivisionValue)));
                    }
                }
                mBarLeftXPoints.add((int) mBarRect.left);
                mBarRightXPoints.add((int) mBarRect.right);
            } else {
                
                float width = 0;
                if (isDrawBorder && mClickPosition == index) {
                    mBarPaint.setAlpha(255);
                    width = 20;
                } else {
                    mBarPaint.setAlpha(200);
                    width = 0;
                }
                if (mBarColors.length == 2) {
                    mBarRect.left = (int) (mStartX + mBarWidth * index * 2 + mBarSpace * (index + 1) - mLeftMoving);
                    mBarRect.top = mStartY - (int) ((mMaxHeight * (barChartEntity.getyValue()[0] / mMaxYDivisionValue))) * percent;
                    mBarRect.right = mBarRect.left + mBarWidth;
                    
                    mBarLeftXPoints.add((int) mBarRect.left);
                    LinearGradient linearGradient =
                            new LinearGradient(mBarRect.left, mBarRect.top, mBarRect.left, mBarRect.bottom, mBarColors[0], mBarEndColors[0],
                                    Shader.TileMode.MIRROR);
                    
                    mBarPaint.setShader(linearGradient);
                    canvas.drawRoundRect(mBarRect, radiusWidth, radiusWidth, mBarPaint);
                    mBarRect.top = mBarRect.bottom - (((int) (mBarRect.bottom - mBarRect.top)) >> 1);
//                    canvas.drawRect(mBarRect, mBarPaint);
                    
                    mBarRect.left = mBarRect.right;
                    mBarRect.top = mStartY - (int) ((mMaxHeight * (barChartEntity.getyValue()[1] / mMaxYDivisionValue))) * percent;
                    mBarRect.right = mBarRect.left + mBarWidth;
                    
                    
                    linearGradient =
                            new LinearGradient(mBarRect.left, mBarRect.top, mBarRect.left, mBarRect.bottom, mBarColors[1], mBarEndColors[1],
                                    Shader.TileMode.MIRROR);
                    mBarPaint.setShader(linearGradient);
                    canvas.drawRoundRect(mBarRect, radiusWidth, radiusWidth, mBarPaint);
//                    mBarRect.top = mBarRect.bottom - (((int) (mBarRect.bottom - mBarRect.top)) >> 1);
//                    canvas.drawRect(mBarRect, mBarPaint);
                    
                    mBarRightXPoints.add((int) mBarRect.right);
                }
            }
            
            
        }
        //画点击效果
//        if (isDrawBorder) {
//            drawBorder(canvas, mClickPosition);
//        }
    }
    
    private void drawBorder(Canvas canvas, int position) {
        mBarRectClick.left = (int) (mStartX + mBarWidth * position + mBarSpace * (position + 1) - mLeftMoving);
        mBarRectClick.right = mBarRectClick.left + mBarWidth;
        mBarRectClick.bottom = mStartY;
        mBarRectClick.top = mStartY - (int) (mMaxHeight * (mData.get(position).getSum() / mMaxYDivisionValue));
        canvas.drawRect(mBarRectClick, mBorderPaint);
    }
    
    /**
     * Y轴上的text (1)当最大值大于1 的时候 将其分成5份 计算每个部分的高度  分成几份可以自己定
     * （2）当最大值大于0小于1的时候  也是将最大值分成5份
     * （3）当为0的时候使用默认的值
     */
    private void drawYAxisAndText(Canvas canvas) {
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.parseColor("#999999"));
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mTextPaint.setTextSize(dp2px(mContext,8));
        
        float eachHeight = (mMaxHeight / 5f);
        float textValue = 0;
        String text = null;
        if (mMaxYValue > 1) {
            for (int i = 0; i <= 5; i++) {
                float startY = mStartY - eachHeight * i;
                BigDecimal maxValue = new BigDecimal(mMaxYDivisionValue);
                BigDecimal fen = new BigDecimal(0.2 * i);
                //因为图表分了5条线，如果能除不进，需要显示小数点不然数据不准确
                if (mMaxYDivisionValue % 5 != 0) {
                    text = maxValue.multiply(fen).floatValue() + mUnitNameY;
                } else {
                    if (maxValue.multiply(fen).longValue() == 0) {
                        text = String.valueOf(maxValue.multiply(fen).longValue());
                    } else {
                        text = maxValue.multiply(fen).longValue() + mUnitNameY;
                    }
                }
                if (!isHideAxisY) {
                    //画Y坐标刻度
                    canvas.drawText(text, mStartX - mTextPaint.measureText(text) - 10, startY + mTextPaint.measureText("0") / 2, mTextPaint);
                }
                if (i == 0) {
                    if (!isHideAxisX) {
                        //画X坐标横线
                        canvas.drawLine(mStartX, startY, mTotalWidth - mPaddingRight - mRightMargin, startY, mAxisPaint);
                    }
                    if (!isHideAxisY) {
                        //画Y坐标横线
                        canvas.drawLine(mStartX, startY, mStartX, startY - mMaxHeight - 10, mAxisPaint);
                    }
                }
            }
        } else if (mMaxYValue > 0 && mMaxYValue <= 1) {
            for (int i = 0; i <= 5; i++) {
                float startY = mStartY - eachHeight * i;
                textValue = CalculateUtil.numMathMul(mMaxYDivisionValue, (float) (0.2 * i));
                text = String.valueOf(textValue);
                if (!isHideAxisY) {
                    
                    canvas.drawText(text, mStartX - mTextPaint.measureText(text) - 5, startY + mTextPaint.measureText("0") / 2, mTextPaint);
                    canvas.drawLine(mStartX, startY, mTotalWidth - mPaddingRight - mRightMargin, startY, mAxisPaint);
                }
            }
        } else {
            for (int i = 0; i <= 5; i++) {
                float startY = mStartY - eachHeight * i;
                text = String.valueOf(10 * i);
                if (!isHideAxisY) {
                    canvas.drawText(text, mStartX - mTextPaint.measureText(text) - 5, startY + mTextPaint.measureText("0") / 2, mTextPaint);
                    canvas.drawLine(mStartX, startY, mTotalWidth - mPaddingRight - mRightMargin, startY, mAxisPaint);
                }
            }
        }
    }
    
    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }
    
    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
    
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mMovingThisTime = (mScroller.getCurrX() - mLastPointX);
            mLeftMoving = mLeftMoving + mMovingThisTime;
            mLastPointX = mScroller.getCurrX();
            postInvalidate();
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("onSingleTapUp:","onTouchEvent--x:"+event.getX()+"   y:"+event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastPointX = event.getX();
                mScroller.abortAnimation();//终止动画
                initOrResetVelocityTracker();
                mVelocityTracker.addMovement(event);//将用户的移动添加到跟踪器中。
                break;
            case MotionEvent.ACTION_MOVE:
                float movex = event.getX();
                mMovingThisTime = mLastPointX - movex;
                mLeftMoving = mLeftMoving + mMovingThisTime;
                mLastPointX = movex;
                invalidate();
                mVelocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                int initialVelocity = (int) mVelocityTracker.getXVelocity();
                mVelocityTracker.clear();
                mScroller.fling((int) event.getX(), (int) event.getY(), -initialVelocity / 2,
                        0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
                invalidate();
                mLastPointX = event.getX();
                break;
            case MotionEvent.ACTION_CANCEL:
                recycleVelocityTracker();
                break;
            default:
                return super.onTouchEvent(event);
        }
        if (mGestureListener != null) {
            mGestureListener.onTouchEvent(event);
        }
        return true;
    }
    
    /**
     * 点击
     */
    private class RangeBarOnGestureListener implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        
        @Override
        public void onShowPress(MotionEvent e) {
        }
        
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int position = identifyWhichItemClick(e.getX(), e.getY());
            Log.d("onSingleTapUp","x:"+e.getX()+"   y:"+e.getY());
            if (position != INVALID_POSITION && mOnItemBarClickListener != null) {
                mOnItemBarClickListener.onClick(position,e.getX(),e.getY());
                setClicked(position);
                invalidate();


            }
            return true;
        }
        
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }
        
        @Override
        public void onLongPress(MotionEvent e) {
        }
        
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }
    
    /**
     * 设置选中的位置
     *
     * @param position
     */
    public void setClicked(int position) {
        isDrawBorder = true;
        mClickPosition = position;
    }
    
    private int mLastXIntercept;
    private int mLastYIntercept;
    
    /**
     * 事件分发，在这里将子view不需要的事件交给父容器处理
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //禁用父布局拦截事件，从而失去后续Action(即失去Move，UP等)
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                int a1 = x - mLastXIntercept;
                int a2 = y - mLastYIntercept;
                //上下滑动时候不拦截事件滑动事件，允许父View拦截事件
                if ((Math.abs(a1) < Math.abs(a2)) || (a2 > 0 && (mLeftMoving == (mMaxRight - mMinRight))) || (a2 < 0 && mLeftMoving == 0)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        mLastXIntercept = x;
        mLastYIntercept = y;
        return super.dispatchTouchEvent(ev);
    }
    
    /**
     * 根据点击的手势位置识别是第几个柱图被点击
     *
     * @param x
     * @param y
     * @return -1时表示点击的是无效位置
     */
    private int identifyWhichItemClick(float x, float y) {
        float leftx = 0;
        float rightx = 0;
        if (mData != null) {
            for (int i = 0; i < mData.size(); i++) {
                leftx = mBarLeftXPoints.get(i);
                rightx = mBarRightXPoints.get(i);
                if (x < leftx) {
                    break;
                }
                if (leftx <= x && x <= rightx) {
                    return i;
                }
            }
        }
        return INVALID_POSITION;
    }

    public  Bitmap createBitmap(Context context,int width, int height, int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        return zoomImg(bitmap,width,height);

    }

    public  Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        Bitmap newbm = null;
        if (bm != null) {
            // 获得图片的宽高
            int width = bm.getWidth();
            int height = bm.getHeight();
            // 计算缩放比例
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 得到新的图片
            newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        }
        return newbm;
    }
    
    public void setTextAngle(float textAngle) {
        mTextAngle = textAngle;
    }
    
    public void setBottomMargin(int bottomMargin) {
        mBottomMargin = bottomMargin;
    }
    
    public void setTopMargin(int topMargin) {
        mTopMargin = topMargin;
    }
    
    public void setRightMargin(int rightMargin) {
        mRightMargin = rightMargin;
    }
    
    public void setLeftMargin(int leftMargin) {
        mLeftMargin = leftMargin;
    }
}

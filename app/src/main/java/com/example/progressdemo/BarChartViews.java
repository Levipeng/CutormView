package com.example.progressdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * 柱状图
 */
public class BarChartViews extends View {

    private Paint axisPaint;//画L线
    private int axisWidth= dip2px(getContext(),2);//线的宽度
    private int axisColor = Color.LTGRAY;//线的颜色

    private int paddingLeft = dip2px(getContext(),20);
    private int paddingTop = dip2px(getContext(),20);
    private int paddingRight = dip2px(getContext(),20);
    private int paddingBottom = dip2px(getContext(),20);
    private int xTextHeight = dip2px(getContext(),40);//X轴底部文字高度


    private TextPaint axisTextPaint;//画坐标轴的文字
    private int axisTextSize= dip2px(getContext(),8);//文字大小
    private int axisTextColor = Color.BLACK;

    private TextPaint axisXTextPaint;//画坐标轴的文字
    private int axisXTextSize= dip2px(getContext(),8);//文字大小
    private int axisXTextColor = Color.BLACK;

    private TextPaint axisYTextPaint;//画坐标轴的文字
    private int axisyTextSize= dip2px(getContext(),8);//文字大小
    private int axisyTextColor = Color.WHITE;

    private TextPaint barTextPaint;//画坐标轴的文字
    private int barTextSize= dip2px(getContext(),8);//文字大小
    private int barTextColor = Color.BLACK;
    private int barTextHeight = dip2px(getContext(),15);

    public void setBarTextColor(int barTextColor) {
        this.barTextColor = barTextColor;
    }

    private int barWidth = dip2px(getContext(),40);//柱子宽度
    private int barSpace = dip2px(getContext(),15);//柱子间距

    //设置柱子宽度
    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }

    private Paint barPaint,barPaint2,barPaint3,barPaint4,barPaint5;//画柱子
    private List<Paint> mPaint=new ArrayList<>();//画笔集合
    private int barColor = Color.RED;//柱子颜色
    //设置柱子颜色
    public void setBarColor(int barColor) {
        this.barColor = barColor;
    }

    private Paint LegendPaint;
    private TextPaint legendTextPaint;
    private int legendTextColor = Color.BLACK;
    private int legendTextSize = dip2px(getContext(),10);


    private List<List<Integer>> yList;//y轴数据
    private List<String> xList;

    private int maxOffset;
    private float lastX;

    private VelocityTracker tracker;
    private Scroller scroller;

    private boolean isLegend;
    private ArrayList<String> legendText;

    //设置数据
    public void setChartData(List<String> xList,List<List<Integer>> yList,boolean isLegend,ArrayList<String> legendText){
        this.yList = yList;
        this.xList = xList;
        this.isLegend = isLegend;
        this.legendText = legendText;
        invalidate();
    }

    //得到Y轴最大值
    private float maxYData(List<Float> lists){
        HashSet<Float> hashSet = new HashSet<>(lists);
        List<Float> list = new ArrayList<>(hashSet);
        Collections.sort(list);//升序
        return list.get(list.size()-1);
    }

    public BarChartViews(Context context) {
        this(context,null);
    }

    public BarChartViews(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BarChartViews(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        axisPaint = new Paint();
        axisPaint.setStrokeWidth(axisWidth);
        axisPaint.setColor(axisColor);
        axisPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setAntiAlias(true);

        axisTextPaint = new TextPaint();
        axisTextPaint.setAntiAlias(true);
        axisTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        axisTextPaint.setTextSize(axisTextSize);
        axisTextPaint.setColor(axisTextColor);

        axisXTextPaint = new TextPaint();
        axisXTextPaint.setAntiAlias(true);
        axisXTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        axisXTextPaint.setTextSize(axisXTextSize);
        axisXTextPaint.setColor(axisXTextColor);

        axisYTextPaint = new TextPaint();
        axisYTextPaint.setAntiAlias(true);
        axisYTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        axisYTextPaint.setTextSize(axisyTextSize);
        axisYTextPaint.setColor(axisyTextColor);

        barTextPaint = new TextPaint();
        barTextPaint.setAntiAlias(true);
        barTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        barTextPaint.setTextSize(barTextSize);
        barTextPaint.setColor(barTextColor);

        barPaint = new Paint();
        barPaint.setColor(Color.parseColor("#F0836C"));
        barPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        barPaint.setAntiAlias(true);
        barPaint.setTextSize(legendTextSize);
        barPaint.setStrokeCap(Paint.Cap.ROUND);

        barPaint2 = new Paint();
        barPaint2.setColor(Color.parseColor("#F4CD59"));
        barPaint2.setFlags(Paint.ANTI_ALIAS_FLAG);
        barPaint2.setTextSize(legendTextSize);
        barPaint2.setAntiAlias(true);


        barPaint3 = new Paint();
        barPaint3.setColor(Color.parseColor("#F6D35A"));
        barPaint3.setFlags(Paint.ANTI_ALIAS_FLAG);
        barPaint3.setTextSize(legendTextSize);
        barPaint3.setAntiAlias(true);


        barPaint4 = new Paint();
        barPaint4.setColor(Color.parseColor("#9DDA53"));
        barPaint4.setFlags(Paint.ANTI_ALIAS_FLAG);
        barPaint4.setTextSize(legendTextSize);
        barPaint4.setAntiAlias(true);


        barPaint5 = new Paint();
        barPaint5.setColor(Color.parseColor("#3AD7A0"));
        barPaint5.setFlags(Paint.ANTI_ALIAS_FLAG);
        barPaint5.setTextSize(legendTextSize);
        barPaint5.setAntiAlias(true);

        mPaint.add(barPaint);
        mPaint.add(barPaint2);
        mPaint.add(barPaint3);
        mPaint.add(barPaint4);
        mPaint.add(barPaint5);

        LegendPaint = new Paint();
        LegendPaint.setColor(barColor);
        LegendPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        LegendPaint.setAntiAlias(true);

        legendTextPaint = new TextPaint();
        legendTextPaint.setAntiAlias(true);
        legendTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        legendTextPaint.setTextSize(legendTextSize);
        legendTextPaint.setColor(legendTextColor);
        scroller = new Scroller(getContext());

    }
    private float ageY;//平均每等分是多少


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (yList==null || yList.size()==0)return;
        int maxTextWidth = (int) axisTextPaint.measureText(100+"0");
        int left = paddingLeft+axisWidth/2+maxTextWidth;//paddingLeft+线的宽度/2+Y轴值最大宽度
        int right = width - paddingRight;
        int top = paddingTop;
        int bottom = height-paddingBottom-axisWidth/2;//预留X轴文字高度
        canvas.save();
        canvas.translate(getScrollX(),0);
        //画Legend
        if (isLegend){
            for(int i=0;i<legendText.size();i++){
                Rect rect = new Rect();
                rect.left = (width-left-paddingRight)/2+i*100;
                rect.top= top;
                rect.right=rect.left+20;
                rect.bottom= top+20;
                canvas.drawRect(rect,mPaint.get(i));
                Paint.FontMetricsInt LegendmetricsInt = mPaint.get(i).getFontMetricsInt();
                int dyLegend = (LegendmetricsInt.bottom-LegendmetricsInt.top)/2-LegendmetricsInt.bottom;
                float ydyLegend = dyLegend+rect.top+10;
                canvas.drawText(legendText.get(i),rect.right,ydyLegend,mPaint.get(i));
            }

        }
        //画Y轴值
        int maxdata = 100;//最大值
        int agedata = maxdata/5;//平均每份
        ageY = (height-paddingBottom-paddingTop-xTextHeight)/5;//线的每等分

        for (int i=0;i<6;i++){
            if (i==0){
                canvas.drawText("0",paddingLeft,height-paddingBottom-xTextHeight,axisTextPaint);//画Y轴刻度
                Log.d("x","画y轴="+(height-paddingBottom-xTextHeight));
            }else {
                Paint.FontMetricsInt metricsInt = axisTextPaint.getFontMetricsInt();
                int dy = (metricsInt.bottom-metricsInt.top)/2-metricsInt.bottom;
                float y = dy+(height-paddingBottom)-ageY*i-xTextHeight;
                canvas.drawText(""+i*agedata,paddingLeft,y,axisTextPaint); //画Y轴刻度
                Log.d("x","画y轴="+(y));
            }

        }
        canvas.drawLine(left,top,left,bottom-xTextHeight,axisPaint);//画Y轴
        canvas.drawLine(left,bottom-xTextHeight,right,bottom-xTextHeight,axisPaint);//画X轴
        Log.d("x","画X轴="+(bottom-xTextHeight));
        for (int i=0;i<yList.size();i++){
            int x0 = left+(barSpace+barWidth)*i+barSpace+getScrollX();
            int x1 = x0+barWidth;
            if (x1<=left || x0>=right){
                continue;
            }

            float mtop=0;
            float lastTop= 0;
            //     x=1077     y=1320
            //     x=825     y =1077
            for(int j = 0;j<yList.get(i).size();j++){
                String ytext = String.valueOf(yList.get(i).get(j));
                float ytextwidth = barTextPaint.measureText(ytext);
                if(j==0){
                    mtop = (float)((yList.get(i).get(j)*ageY/(agedata)));
                    canvas.clipRect(left,top,right,bottom);//剪切柱状图区域
                    Log.d("x"," top0="+(bottom-mtop-xTextHeight)+" bottom="+(bottom-xTextHeight)+ "j"+j);
                    lastTop = bottom-mtop-xTextHeight;
                    //
                    RectF rectF2 = new RectF(x0,bottom-barWidth-xTextHeight,x1, bottom-xTextHeight);
                    canvas.drawOval(rectF2,mPaint.get(j));
                    canvas.drawRect(x0,bottom-mtop-xTextHeight,x1, bottom-xTextHeight-(barWidth/2),mPaint.get(j));//画柱状图
                    canvas.drawText(ytext,x0+barWidth/2-ytextwidth/2,((bottom-mtop-xTextHeight)+(bottom-xTextHeight))/2,axisYTextPaint);
                }else {
                    if(j==yList.get(i).size()-1){
                        mtop = (float)((yList.get(i).get(j)*ageY/(agedata)));
                        canvas.clipRect(left,top,right,bottom);//剪切柱状图区域
                        Log.d("x"," top0="+(lastTop-mtop)+" bottom="+(lastTop)+ "j"+j);
                        RectF rectF2 = new RectF(x0,lastTop-mtop,x1, lastTop-mtop+barWidth);
                        canvas.drawArc(rectF2,0,-180,true,mPaint.get(j));
                        float ovalheight = (lastTop-mtop+barWidth/2)-(lastTop-mtop);
                        Log.d("x"," ovalheight="+ovalheight);
                        canvas.drawRect(x0,lastTop-mtop+(barWidth/2)-ovalheight/2+20,x1, lastTop,mPaint.get(j));
                        canvas.drawText(ytext,x0+barWidth/2-ytextwidth/2,((lastTop-mtop)+(lastTop))/2,axisYTextPaint);
                        lastTop = lastTop-mtop;
                    }else{
                        mtop = (float)((yList.get(i).get(j)*ageY/(agedata)));
                        canvas.clipRect(left,top,right,bottom);//剪切柱状图区域
                        Log.d("x"," top0="+(lastTop-mtop)+" bottom="+(lastTop)+ "j"+j);
                        canvas.drawRect(x0,lastTop-mtop,x1, lastTop,mPaint.get(j));
                        canvas.drawText(ytext,x0+barWidth/2-ytextwidth/2,((lastTop-mtop)+(lastTop))/2,axisYTextPaint);
                        lastTop = lastTop-mtop;
                    }
                }
            }

            //底部X轴文字
            String xtext = xList.get(i);
            float xtextwidth = axisXTextPaint.measureText(xtext);//X文字宽度
            Paint.FontMetricsInt metricsInt = axisXTextPaint.getFontMetricsInt();
            int dy = (metricsInt.bottom-metricsInt.top)/2-metricsInt.bottom;
            float y = height-xTextHeight-dy;
            //柱状图上加文字
            drawText(canvas,xtext,barTextSize,-45,x0+barWidth/2+barWidth/4,y,Color.BLACK);
        }

        maxOffset = (yList.size() * (barWidth+barSpace)-(getMeasuredWidth()-paddingRight-paddingLeft-maxTextWidth));//计算可滑动距离
        if (maxOffset<0){
            maxOffset=0;
        }
        canvas.restore();
    }

    private void drawText(Canvas canvas,String str,int size,float degrees,float startX,float
            startY,int color){
        canvas.save();
        Paint paint = new Paint();
        paint.setAntiAlias(true);//抗锯齿
        paint.setColor(color);
        paint.setTypeface(Typeface.DEFAULT_BOLD);//字体
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);//字体风格
        paint.setTypeface(font);
        paint.setTextSize(size);
        //Paint设置水平居中
        paint.setTextAlign(Paint.Align.CENTER);
        float length = paint.measureText(str);//获取字体的长度
        //获取文字的高度
        //参考网址:http://www.voidcn.com/article/p-zqzznoyl-ce.html
        //1.获取文字高度
        Paint.FontMetrics fm = paint.getFontMetrics();
        float fFontHeight = (float)Math.ceil(fm.descent - fm.ascent);
        //2.获取文字高度---1和2获取到的高度不同
        //参考网址:https://blog.csdn.net/u010661782/article/details/52805939
        //Rect rect = new Rect();
        //textPaint.getTextBounds(str,0,str.length(), rect);
        //fFontHeight = rect.height();
        float ncenterx = startX + (size - length) / 2;
        if (degrees!=0){
            //以指定坐标点旋转指定角度
            canvas.rotate(degrees, ncenterx, startY);
        }
        canvas.drawText(str, ncenterx,startY,paint);
        canvas.restore();
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (tracker!=null){
                    tracker.clear();
                }
                lastX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (tracker==null){
                    tracker = VelocityTracker.obtain();
                }
                if (tracker!=null){
                    tracker.addMovement(event);
                }
                int sX= getScrollX();
                sX += event.getX()-lastX;
                sX = Math.max(Math.min(0,sX),-maxOffset);
                scrollTo(sX,0);
                lastX=event.getX();
                break;
            case MotionEvent.ACTION_UP:
                setTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                setTracker();
                break;
        }
        invalidate();
        return true;
    }

    private void setTracker(){
        if (tracker!=null){
            tracker.computeCurrentVelocity(1000);
            scroller.forceFinished(true);
            scroller.fling(getScrollX(),0,(int) (0.5*tracker.getXVelocity()),0,-maxOffset,0,0,0);
            tracker.recycle();
        }
        tracker=null;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()){
            scrollTo(scroller.getCurrX(),0);
            postInvalidate();
        }
    }

    public  int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
package com.example.progressdemo;

/**
 * 柱状图实体类
 */
public class BarChartEntity {
    private String xLabel;
    private Float[] yValue;
    private float sum;
    
    private int selectColor;
    private int imgResId;

    public BarChartEntity(String xLabel, Float[] yValue) {
        this.xLabel = xLabel;
        this.yValue = yValue;
        for (float y : yValue) {
            sum+=y;
        }
    }
    
    public void setSelectColor(int selectColor) {
        this.selectColor = selectColor;
    }
    
    public int getSelectColor() {
        return selectColor;
    }
    
    public int getImgResId() {
        return imgResId;
    }
    
    public void setImgResId(int imgResId) {
        this.imgResId = imgResId;
    }
    
    public String getxLabel() {
        return xLabel;
    }

    public void setxLabel(String xLabel) {
        this.xLabel = xLabel;
    }

    public BarChartEntity(Float[] yValue) {
        this.yValue = yValue;
    }

    public Float[] getyValue() {
        return yValue;
    }

    public void setyValue(Float[] yValue) {
        this.yValue = yValue;
        for (float y : yValue) {
            sum+=y;
        }
    }

    public float getSum() {
        return sum;
    }
}

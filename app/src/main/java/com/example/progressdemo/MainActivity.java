package com.example.progressdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupAnimation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressCircle progressView;
    private ArrayList<BarChartEntity> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BarChartView barChartViews = (BarChartView) findViewById(R.id.bar_view);
//        List<String> str=new ArrayList<>();
//        str.add("abcde");
//        str.add("abcde");
//        str.add("abcde");
//        str.add("abcde");
//        str.add("abcde");
//        str.add("abcde");
//        str.add("abcde");
//        str.add("abcde");
//        List<List<Integer>> inList=new ArrayList<>();
//        List intList=new ArrayList();
//        intList.add(10);
//        intList.add(50);
//        intList.add(40);
//        inList.add(intList);
//        inList.add(intList);
//        inList.add(intList);
//        inList.add(intList);
//        inList.add(intList);
//        inList.add(intList);
//        inList.add(intList);
//        inList.add(intList);
//        ArrayList<String> str1=new ArrayList<>();
//        str1.add("a");
//        str1.add("a");
//        str1.add("a");
//        str1.add("a");
//
        // barChartViews.setChartData(str,inList,true,str1);
        barChartViews.initChart(new int[]{Color.parseColor("#F0836C")
                        , Color.parseColor("#F4A359")
                        , Color.parseColor("#F6D35A")
                        , Color.parseColor("#9DDA53")
                        , Color.parseColor("#3AD7A0")},
                new String[]{"E", "D", "C", "B", "A"}, null, "人数");


        List<BarChartEntity> datas = new ArrayList<>();
        Float[] valueArr;
        float count = 0;
        for (int i = 0; i < 10; i++) {
            count = 0;
            valueArr = new Float[5];
            valueArr[0] = (float) (Math.random() * 50);
            count += valueArr[0];
            valueArr[1] = (float) (Math.random() * 50);
            count += valueArr[1];
            if ((100 - count) < 50) {
                valueArr[2] = (float) (Math.random() * (100 - count));
                count += valueArr[2];
            } else {
                valueArr[2] = (float) (Math.random() * 50);
                count += valueArr[2];
            }
            if ((100 - count) < 50) {
                valueArr[3] = (float) (Math.random() * (100 - count));
                count += valueArr[3];
            } else {
                valueArr[3] = (float) (Math.random() * 50);
                count += valueArr[3];
            }
            valueArr[4] = (100 - count);
//            datas.add(new BarChartEntity("三年" + String.valueOf(i) + "班", new Float[]{6f,6f,9f,3f,0f}));
            datas.add(new BarChartEntity("三年" + String.valueOf(i) + "班", valueArr));
        }
        List<BarChartEntity> finalDatas1 = datas;
        final XPopup.Builder builder = new XPopup.Builder(MainActivity.this)
//                .isCenterHorizontal(true)
                .watchView(barChartViews);

        barChartViews.setOnItemBarClickListener(new BarChartView.OnItemBarClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(int position, float x, float y) {
//            Toast.makeText(getContext(), String.format("及格人数：%f\n不及格人数：%f", finalDatas1.get(position).getyValue()[0], finalDatas1.get(position).getyValue()[1]), Toast.LENGTH_SHORT).show();
//                Toast.makeText(MainActivity.this,"点击了"+position,Toast.LENGTH_SHORT).show();
                builder.hasShadowBg(false)
                        .asCustom(new DialogXpopu(MainActivity.this))
                        .show();
//                new XPopup.Builder(MainActivity.this)
//                        .popupAnimation(PopupAnimation.NoAnimation)
//                        .offsetX((int)x)
//                        .offsetY((int)y)
//                        .asCustom()
//                        .show();
            }
        });


        barChartViews.setData(datas, 0, 40);
        barChartViews.startAnimation();
//
//         progressView = (ProgressCircle)findViewById(R.id.progress_circular);
//         progressView.setMaxNumber(500,250,"#DCF5EC","#3BD298","#E6F1EF");
    }

    public void onClick(View view) {
        progressView.setMaxNumber(500, 250, "#FFffff", "#FFFACD", "#E6F1EF");
    }

    public void onClick1(View view) {
        progressView.setMaxNumber(500, 323, "#FFffff", "#FFE4E1", "#E6F1EF");
    }

    public void onClick2(View view) {
        progressView.setMaxNumber(500, 340, "#FFffff", "#228B22", "#E6F1EF");
    }
}
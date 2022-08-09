package com.example.progressdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressCircle progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BarChartViews barChartViews = (BarChartViews) findViewById(R.id.bar_view);
        List<String> str=new ArrayList<>();
        str.add("abcde");
        str.add("abcde");
        str.add("abcde");
        str.add("abcde");
        str.add("abcde");
        str.add("abcde");
        str.add("abcde");
        str.add("abcde");
        List<List<Integer>> inList=new ArrayList<>();
        List intList=new ArrayList();
        intList.add(10);
        intList.add(50);
        intList.add(40);
        inList.add(intList);
        inList.add(intList);
        inList.add(intList);
        inList.add(intList);
        inList.add(intList);
        inList.add(intList);
        inList.add(intList);
        inList.add(intList);
        ArrayList<String> str1=new ArrayList<>();
        str1.add("a");
        str1.add("a");
        str1.add("a");
        str1.add("a");

        barChartViews.setChartData(str,inList,true,str1);
//         progressView = (ProgressCircle)findViewById(R.id.progress_circular);
//         progressView.setMaxNumber(500,250,"#DCF5EC","#3BD298","#E6F1EF");
    }

    public void onClick(View view) {
        progressView.setMaxNumber(500,250,"#FFffff","#FFFACD","#E6F1EF");
    }

    public void onClick1(View view) {
        progressView.setMaxNumber(500,323,"#FFffff","#FFE4E1","#E6F1EF");
    }

    public void onClick2(View view) {
        progressView.setMaxNumber(500,340,"#FFffff","#228B22","#E6F1EF");
    }
}
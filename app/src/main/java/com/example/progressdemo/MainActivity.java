package com.example.progressdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private ProgressCircle progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         progressView = (ProgressCircle)findViewById(R.id.progress_circular);
         progressView.setMaxNumber(500,150,"#FFC0CB","#FFD700");
    }
}
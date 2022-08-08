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
         progressView.setMaxNumber(500,250,"#DCF5EC","#3BD298","#E6F1EF");
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
package com.example.opencv_motiondetect;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(OpenCVLoader.initDebug()) {
            Log.d("OPENCVLOAD", "success");
        } else {
            Log.d("OPENCVLOAD", "error");
        }
    }
}
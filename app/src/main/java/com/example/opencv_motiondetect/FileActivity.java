package com.example.opencv_motiondetect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;

public class FileActivity extends AppCompatActivity {

    VideoCapture videoCapture;
    File sample;
    TextView textView;

    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(FileActivity.this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == BaseLoaderCallback.SUCCESS) {
                videoCapture = new VideoCapture();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        textView = findViewById(R.id.textView);



        testCVLoad();
    }

    @Override
    protected void onResume() {
        super.onResume();

        videoCapture = new VideoCapture();

        Intent intent = getIntent();
        String path = intent.getStringExtra("PATH");


        videoCapture.open(path, Videoio.CAP_ANDROID);
        if (!videoCapture.isOpened()) {
            textView.setText("Video failed to open");
            Log.d("VIDEOCAPTURE", "Video failed to open");
        } else {
            textView.setText("Video opened OK");
            Log.d("VIDEOCAPTURE", "Video opened OK");
        }
    }

    public void testCVLoad() {
        if (OpenCVLoader.initDebug()) {
            Log.d("OPENCVLOAD", "successful OpenCV load");
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        } else {
            Log.d("OPENCVLOAD", "error loading OpenCV");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, baseLoaderCallback);
        }
    }
}
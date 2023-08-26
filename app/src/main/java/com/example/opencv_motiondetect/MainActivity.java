package com.example.opencv_motiondetect;

import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.Collections;
import java.util.List;

public class MainActivity extends CameraActivity {

    CameraBridgeViewBase cameraBridgeViewBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();

        cameraBridgeViewBase = findViewById(R.id.cameraView);
        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {

            }

            @Override
            public void onCameraViewStopped() {

            }

            // When a frame is captured by android camera
            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                // cast input frame to Mat
                return inputFrame.rgba();
            }
        });

        if (OpenCVLoader.initDebug()) {
            Log.d("OPENCVLOAD", "success");
        } else {
            Log.d("OPENCVLOAD", "error");
        }
        cameraBridgeViewBase.enableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraBridgeViewBase.enableView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraBridgeViewBase.disableView();
    }

    // В современных версиях Android нет нужды проверять, он сам проверяет
    void getPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 3);
        }
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            getPermission();
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(cameraBridgeViewBase);
    }
}
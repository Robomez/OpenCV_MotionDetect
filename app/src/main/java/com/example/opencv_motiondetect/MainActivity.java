package com.example.opencv_motiondetect;

import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import java.util.List;

public class MainActivity extends CameraActivity {

    CameraBridgeViewBase cameraBridgeViewBase;
    // Matrices for current frame(gray sc), previous frame(gray sc),
    // difference between and rgb image to print noise to
    Mat current, previous, difference, rgb;
    boolean isInit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();

        isInit = false;

        cameraBridgeViewBase = findViewById(R.id.cameraView);
        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {
                // Initialise matrices
                current = new Mat();
                previous = new Mat();
                rgb = new Mat();
                difference = new Mat();
            }

            @Override
            public void onCameraViewStopped() {

            }

            // When a frame is captured by android camera
            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                // First frame set previous
                if (!isInit) {
                    previous = inputFrame.gray();
                    isInit = true;
                    return previous;
                }

                rgb = inputFrame.rgba();
                current = inputFrame.gray();

                // Detect noises
                Core.absdiff(current, previous, difference);
                // Divide pixel values to 0 or 1. If value > 100 -> 1, else 0
                Imgproc.threshold(
                        difference,
                        difference,
                        100,
                        255,
                        Imgproc.THRESH_BINARY);

                // Set current frame as previous
                previous = current.clone();

                // cast input frame to Mat
                return difference;
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
package com.example.opencv_motiondetect;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorKNN;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends CameraActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    BackgroundSubtractorMOG2 backSub;
    BackgroundSubtractorKNN backSubKNN;
    JavaCameraView javaCameraView;
    Mat frame, subMask, frameResult;
    List<MatOfPoint> contours;

    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(MainActivity.this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == BaseLoaderCallback.SUCCESS) {
                javaCameraView.enableView();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // getPermission();

        javaCameraView = findViewById(R.id.camera_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(MainActivity.this);
        javaCameraView.setCameraPermissionGranted();
    }

    @Override
    protected void onResume() {
        super.onResume();
        testCVLoad();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
        frame.release();
        subMask.release();
        backSub.clear();
        backSubKNN.clear();
    }

    // В современных версиях Android нет нужды проверять, он сам проверяет
 /*   void getPermission() {
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
    }*/

    @Override
    public void onCameraViewStarted(int width, int height) {
        frame = new Mat();
        frameResult = new Mat();
        subMask = new Mat();
        backSub = Video.createBackgroundSubtractorMOG2();
        backSubKNN = Video.createBackgroundSubtractorKNN();
        contours = new ArrayList<>();
    }

    @Override
    public void onCameraViewStopped() {
        frame.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        frame = inputFrame.gray();
        frameResult = inputFrame.rgba();

        /*
        // При вертикальной ориентации
        Core.transpose(frame, frame);
        Core.flip(frame, frame, 1);
        */

        Imgproc.GaussianBlur(frame, frame, new Size(3,3), 0, 0);
        // backSub.apply(frame, subMask);
        backSubKNN.apply(frame, subMask);

        Imgproc.erode(subMask,
                subMask,
                Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3))
        );
        Imgproc.dilate(
                subMask,
                subMask,
                Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3))
        );
        Imgproc.findContours(
                subMask,
                contours,
                new Mat(),
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE
        );

        for (MatOfPoint m : contours) {
            Rect rectangle = Imgproc.boundingRect(m);
            Imgproc.rectangle(frameResult, rectangle, new Scalar(255, 255, 0), 4);
        }
        contours.clear();

        // return frame;
        return frameResult;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
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
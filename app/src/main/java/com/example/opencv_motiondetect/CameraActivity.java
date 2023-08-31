package com.example.opencv_motiondetect;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
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

public class CameraActivity extends org.opencv.android.CameraActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    JavaCameraView javaCameraView;
    BackgroundSubtractorMOG2 backSub;
    BackgroundSubtractorKNN backSubKNN;
    Mat frame, subMask, frameResult;
    List<MatOfPoint> contours;

    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(CameraActivity.this) {
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
        setContentView(R.layout.activity_camera);

        javaCameraView = findViewById(R.id.camera_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(CameraActivity.this);
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

        frameResult = inputFrame.rgba();

        // При вертикальной ориентации
        Core.transpose(frameResult, frameResult);
        Core.flip(frameResult, frameResult, 1);

        // Получить gray матрицу
        Imgproc.cvtColor(frameResult, frame, Imgproc.COLOR_RGB2GRAY);

        Imgproc.GaussianBlur(frame, frame, new Size(5, 5), 0, 0);
        // backSub.apply(frame, subMask);
        // KNN метод даёт меньше ложных срабатываний
        backSubKNN.apply(frame, subMask);

        Imgproc.erode(subMask,
                subMask,
                Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3))
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
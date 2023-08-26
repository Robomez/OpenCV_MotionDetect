package com.example.opencv_motiondetect;

import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class MainActivity extends CameraActivity {

    CameraBridgeViewBase cameraBridgeViewBase;
    // Cascade classifier to detect faces
    CascadeClassifier cascadeClassifier;
    // Matrices for transformation
    Mat rgb, gray;
    MatOfRect faces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();

        cameraBridgeViewBase = findViewById(R.id.cameraView);
        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {
                // Initialise matrices when camera is initiated
                rgb = new Mat();
                gray = new Mat();
                faces = new MatOfRect();
            }

            @Override
            public void onCameraViewStopped() {
                // release resources
                rgb.release();
                gray.release();
                faces.release();
            }

            // When a frame is captured by android camera
            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                // Get matrices of the frame
                rgb = inputFrame.rgba();
                gray = inputFrame.gray();

                // Classifier needs grayscale and writes them to faces rectangles matrix
                cascadeClassifier.detectMultiScale(gray, faces, 1.1, 3);
                // Process these rectangles one by one
                for (Rect rect : faces.toList()) {

                    // Draw the rectangle
                    Imgproc.rectangle(rgb, rect, new Scalar(255, 255, 0), 10);

                    // Blur faces
                    Mat submat = rgb.submat(rect);
                    Imgproc.blur(submat, submat, new Size(20, 20));
                    submat.release();
                }

                // cast input frame to Mat
                return rgb;
            }
        });

        if (OpenCVLoader.initDebug()) {
            cameraBridgeViewBase.enableView();

            // Get faces data
            try {
                InputStream inputStream = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                // Write to the file inside directory
                File file = new File(getDir("cascade", MODE_PRIVATE), "anyname.xml");
                FileOutputStream fileOutputStream = new FileOutputStream((file));
                byte[] data = new byte[4096];
                int read_bytes;

                // read bytes from input stream
                while((read_bytes = inputStream.read(data)) != -1 ) {
                    // write them to file output stream
                    fileOutputStream.write(data, 0, read_bytes);
                }

                // Classifier needs a path to an output file
                cascadeClassifier = new CascadeClassifier(file.getAbsolutePath());
                if (cascadeClassifier.empty()) {
                    cascadeClassifier = null;
                }

                // Clear everything
                inputStream.close();
                fileOutputStream.close();
                if (!file.delete()) {
                    Log.e("FILEDELETE", "FILE NOT DELETED");
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            Log.d("OPENCVLOAD", "success");
        } else {
            Log.d("OPENCVLOAD", "error");
        }

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
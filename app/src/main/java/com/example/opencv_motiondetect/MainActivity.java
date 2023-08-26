package com.example.opencv_motiondetect;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int SELECT_CODE = 1, CAMERA_CODE = 2;
    Button select, camera;
    ImageView imageView;
    Bitmap bitmap;
    Mat mat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (OpenCVLoader.initDebug()) {
            Log.d("OPENCVLOAD", "success");
        } else {
            Log.d("OPENCVLOAD", "error");
        }

        camera = findViewById(R.id.camera);
        select = findViewById(R.id.select);
        imageView = findViewById(R.id.imageView);

        select.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_CODE);
        });

        camera.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_CODE && data != null) {
            try {
                // Получили bitmap
                bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(this.getContentResolver(), data.getData());
                // Установили содержимое imageView этот bitmap
                imageView.setImageBitmap(bitmap);
                // В матрицу openCV загрузили bitmap
                mat = new Mat();
                Utils.bitmapToMat(bitmap, mat);

                // Convert to grayscale
                // Функция openCV для конвертации в gc
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
                // Так же из матрицы конвертированных значений перевести в bitmap
                Utils.matToBitmap(mat, bitmap);
                // Установить в imageView
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (requestCode == CAMERA_CODE && data != null) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            mat = new Mat();
            Utils.bitmapToMat(bitmap, mat);

            // Convert to grayscale
            // Функция openCV для конвертации в gc
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2BGR);
            // Так же из матрицы конвертированных значений перевести в bitmap
            Utils.matToBitmap(mat, bitmap);
            // Установить в imageView
            imageView.setImageBitmap(bitmap);
        }
    }
}
package com.example.opencv_motiondetect;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.ReturnCode;

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

        //videoCapture = new VideoCapture();

        /* Чтобы использовать VideoCapture, нужно конвертировать файл видео в mjpeg
        в контейнере avi. Иначе OpenCV на андроиде его не поймёт.
        Для этого используется FFmpeg библиотека. Её можно взять отдельно уже собранным модулем
        или собрать OpenCV с поддержкой FFmpeg. Тут взято отдельно библиотека mobile_ffmpeg.
         */

        String fileName = "/storage/emulated/0/Download/sample.mp4";
        String mjpegFileName = "/storage/emulated/0/Download/outputMjpeg.mjpeg";
        String aviFileName = "/storage/emulated/0/Download/outputAvi.avi";

        FFmpegSession session = FFmpegKit.execute("-i" + fileName + " -vcodec mjpeg " + mjpegFileName);
        //session = FFmpegKit.execute("-i " + mjpegFileName + " -vcodec " + aviFileName);

        if(ReturnCode.isSuccess(session.getReturnCode())) {
            Log.i("FFMPEG", "Command execution completed successfully");
        } else if (ReturnCode.isCancel(session.getReturnCode())) {
            Log.i("FFMPEG", "Command execution cancelled by user");
        } else {
            Log.i("FFMPEG", String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

            videoCapture.open(mjpegFileName);

            if (videoCapture.isOpened()) {
                Log.d("VIDEOCAPTURE", "Video opened OK");
            } else {
                Log.d("VIDEOCAPTURE", "Video open FAILED");
            }
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
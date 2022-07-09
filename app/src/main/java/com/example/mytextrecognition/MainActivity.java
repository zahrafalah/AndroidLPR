package com.example.mytextrecognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.dnn.*;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
//    SurfaceView cameraView;
//    TextView textView;
//    CameraSource cameraSource;
//    final int RequestCameraPermissionId = 1001;

    TextView textView;
    private static final String TAG = "MyActivity";
    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    int counter = 0;
    boolean startCanny = false;
    boolean startYolo = false;
    boolean firstTimeYolo = false;
    Net tinyYolo;

    public void Canny(View Button){

//        Log.i(TAG, String.valueOf(Environment.getExternalStorageDirectory()));
        if (startCanny == false){
            startCanny = true;
        }
        else{
            startCanny = false;
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case RequestCameraPermissionId:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        return;
//                    }
//                    try {
//                        cameraSource.start(cameraView.getHolder());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//
//                    }
//                }
//        }
//    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.CameraView);
//        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
//        cameraBridgeViewBase.setCvCameraViewListener(this);
//
//        cameraView = (SurfaceView) findViewById(R.id.surface_view);
//        textView = (TextView) findViewById(R.id.text_view);
//        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
//        if (!textRecognizer.isOperational()) {
//            Log.w("MainActivity", "Detector Dependencies are not yet available");
//        }else {
//            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer).setFacing(CameraSource.CAMERA_FACING_BACK).setRequestedPreviewSize(1280, 1024).setRequestedFps(2.0f).setAutoFocusEnabled(true).build();
//            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
//                @Override
//                public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
//                    try {
//                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},RequestCameraPermissionId);
//                            return;
//                        }
//                        cameraSource.start(cameraView.getHolder());
//                    }catch(IOException e){
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
//                    cameraSource.stop();
//                }
//            });
//            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
//                @Override
//                public void release() {
//
//                }
//
//                @Override
//                public void receiveDetections(Detector.Detections<TextBlock> detections) {
//                    final SparseArray<TextBlock> items = detections.getDetectedItems();
//                    if(items.size() != 0){
//                        textView.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                StringBuilder stringBuilder = new StringBuilder();
//                                for(int i = 0; i< items.size(); i++){
//                                    TextBlock item = items.valueAt(i);
//                                    stringBuilder.append(item.getValue());
//                                    stringBuilder.append("/n");
//                                }
//                                textView.setText(stringBuilder.toString());
//                            }
//                        });
//                    }
//                }
//            });
//
//            }
//
//    }
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE},PackageManager.PERMISSION_GRANTED);

    try{
        String tinyYoloCfg = Environment.getExternalStorageDirectory() + "Downloads/yolov3-tiny.cfg" ;
        String tinyYoloWeights =  Environment.getExternalStorageDirectory() + "Downloads/yolov3-tiny.weights";
//        tinyYolo = Dnn.readNetFromDarknet(tinyYoloCfg, tinyYoloWeights);

    }catch(Exception e){
        e.printStackTrace();
    }

    cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.CameraView);
    cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
    cameraBridgeViewBase.setCvCameraViewListener(this);

    //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);

            switch(status){

                case BaseLoaderCallback.SUCCESS:
                    cameraBridgeViewBase.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }


        }

    };



}



    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        Log.println(Environment.getExternalStorageDirectory());
        Mat frame = inputFrame.rgba();

        // gray scale and
//        if (counter % 2 == 0){
//            Core.flip(frame, frame, 1);
//            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2GRAY);
//        }
//        counter = counter + 1;

//          Canny
        if (startCanny == true) {
//            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2GRAY);
//            Imgproc.Canny(frame, frame, 100, 80);

            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2GRAY);
            Mat imageBlob = Dnn.blobFromImage(frame, 0.00392, new Size(416,416),new Scalar(0,0,0), false,false);
//            tinyYolo.setInput(imageBlob);
//            tinyYolo.forward();
        }
        return frame;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"There's a problem, yo!", Toast.LENGTH_SHORT).show();
        }

        else
        {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

}
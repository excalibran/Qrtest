package com.example.williamwinters.qrtest2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    String qrData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SurfaceView cameraView = (SurfaceView)findViewById(R.id.camera_view);
        final TextView barcodeInfo = (TextView)findViewById(R.id.code_info);

        /*
        *
        *
        * */

        final Button bConfirm = (Button) findViewById(R.id.button_confirm);
        bConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "confirm works", Toast.LENGTH_LONG).show();
            }
        });
        bConfirm.setVisibility(View.INVISIBLE);

        final LinearLayout gCodeEnterPrompt = (LinearLayout) findViewById(R.id.ask_if_problem);

        final Button bEnterCodeInstead = (Button) findViewById(R.id.button_string_code);
        bEnterCodeInstead.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "opting to enter code instead", Toast.LENGTH_LONG).show();
            }
        });

        Button bBack = (Button) findViewById(R.id.button_back);
        bConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "going back", Toast.LENGTH_LONG).show();
            }
        });

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

        CameraSource cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();

        if(cameraSource != null){
            final CameraSource finalCameraSource = cameraSource;

            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        finalCameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.e("CAMERA SOURCE", ie.getMessage());
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    finalCameraSource.stop();
                }
            });
        }
        else{
            Toast.makeText(MainActivity.this, "no camera source", Toast.LENGTH_LONG).show();
        }

        if(barcodeDetector != null){
            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {

                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                    if (barcodes.size() != 0) {
                        barcodeInfo.post(new Runnable() {    // Use the post method of the TextView
                            public void run() {
                                barcodeInfo.setText(barcodes.valueAt(0).displayValue);
                            }
                        });

                        bConfirm.post(new Runnable(){
                            public void run(){
                                bConfirm.setVisibility(View.VISIBLE);
                            }
                        });
                        gCodeEnterPrompt.post(new Runnable(){
                            public void run(){
                                gCodeEnterPrompt.setVisibility(View.INVISIBLE);
                            }
                        });

                        qrData = barcodeInfo.getText().toString();
                    }
                }
            });
        }
        else{
            Toast.makeText(MainActivity.this, "barcode reader failed to start", Toast.LENGTH_LONG).show();
        }
    }
}

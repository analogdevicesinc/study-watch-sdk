package com.analog.androidsamples;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.analog.study_watch_sdk.StudyWatch;
import com.analog.study_watch_sdk.application.BCMApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.packets.stream.BCMDataPacket;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

public class BCMExample extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = BCMExample.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        final Button button = findViewById(R.id.button);
        button.setEnabled(false);
        // connect to study watch with its mac address.
        StudyWatch.connectBLE("C5:05:CA:F1:67:D5", getApplicationContext(), new StudyWatchCallback() {
            @Override
            public void onSuccess(SDK sdk) {
                Log.d(TAG, "onSuccess: SDK Ready");
                watchSdk = sdk; // store this sdk reference to be used for creating applications
                runOnUiThread(() -> {
                    button.setEnabled(true);
                });
            }

            @Override
            public void onFailure(String message, int state) {
                Log.e(TAG, "onError: " + message);
            }
        });

        button.setOnClickListener(v -> {
            // Get applications from SDK
            BCMApplication bcmApp = watchSdk.getBCMApplication();

            bcmApp.setCallback(bcmDataPacket -> {
                for (BCMDataPacket.Payload.StreamData streamData : bcmDataPacket.payload.getStreamData()) {
                    long bcmReal = streamData.getRealData();
                    long bcmImaginary = streamData.getImaginaryData();
                    if (bcmReal == 0)
                        bcmReal = 1;
                    if (bcmImaginary == 0)
                        bcmImaginary = 1;
                    double impedanceImg = bcmImaginary;
                    double impedanceReal = bcmReal;
                    double realAndImg = (double) (impedanceReal * impedanceReal + impedanceImg * impedanceImg);
                    double impedanceMagnitude = Math.sqrt(realAndImg);
                    double impedancePhase = Math.atan2(impedanceImg, impedanceReal);
                    Log.d(TAG, " BCM (Timestamp, sequence number, impedanceMagnitude, impedancePhase) :: " +
                            streamData.getTimestamp() + " , " + bcmDataPacket.payload.getSequenceNumber() + " , " +
                            impedanceMagnitude + " , " + impedancePhase);
                }
            });
            //config
            bcmApp.writeLibraryConfiguration(new long[][]{{0x0, 0x4}});
            // start sensor
            bcmApp.startSensor();
            bcmApp.subscribeStream();
            // sleep
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // stop sensor
            bcmApp.unsubscribeStream();
            bcmApp.stopSensor();

        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
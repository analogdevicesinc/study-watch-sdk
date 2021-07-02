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
import com.analog.study_watch_sdk.application.EDAApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.packets.stream.EDADataPacket;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

public class EDAExample extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = EDAExample.class.getSimpleName();

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
            EDAApplication edaApp = watchSdk.getEDAApplication();

            edaApp.setCallback(edaDataPacket -> {
                for (EDADataPacket.Payload.StreamData streamData : edaDataPacket.payload.getStreamData()) {
                    long edaReal = streamData.getRealData();
                    long edaImaginary = streamData.getImaginaryData();
                    if (edaReal == 0)
                        edaReal = 1;
                    double impedanceImg = edaImaginary * 1000.0;
                    double impedanceReal = edaReal * 1000.0;
                    double realAndImg = (impedanceReal * impedanceReal + impedanceImg * impedanceImg);
                    double impedanceMagnitude = Math.sqrt(realAndImg);
                    double impedancePhase = Math.atan2(impedanceImg, impedanceReal);
                    Log.d(TAG, " EDA (Timestamp, sequence number, impedanceMagnitude, impedancePhase) :: " +
                            streamData.getTimestamp() + " , " + edaDataPacket.payload.getSequenceNumber() + " , " +
                            impedanceMagnitude + " , " + impedancePhase);
                }
            });
            //config
            edaApp.writeLibraryConfiguration(new long[][]{{0x0, 0x4}});
            // start sensor
            edaApp.startSensor();
            edaApp.subscribeStream();
            // sleep
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // stop sensor
            edaApp.unsubscribeStream();
            edaApp.stopSensor();

        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
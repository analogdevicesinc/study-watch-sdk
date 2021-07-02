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
import com.analog.study_watch_sdk.application.ADPDApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.enums.ADPDLed;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

public class ADPDExample extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = ADPDExample.class.getSimpleName();

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
            ADPDApplication adpdApp = watchSdk.getADPDApplication();

            adpdApp.setCallback(adpdDataPacket -> {
                for (long signalData : adpdDataPacket.payload.getSignalData()) {
                    Log.d(TAG, "Stream Data (Timestamp, adpdData, channel num) :: " + adpdDataPacket.payload.getTimestamp()
                            + " , " + signalData + " , " + adpdDataPacket.payload.getChannelNum());
                }
            }, adpdApp.STREAM_ADPD6);

            // config
            adpdApp.loadConfiguration(adpdApp.DEVICE_GREEN);
            // if DVT2 watch then adpdApp.CLOCK_1M
            adpdApp.calibrateClock(adpdApp.CLOCK_1M_AND_32M);
            adpdApp.enableAgc(new ADPDLed[]{adpdApp.LED_GREEN});

            // start sensor
            adpdApp.startSensor();
            adpdApp.subscribeStream(adpdApp.STREAM_ADPD6);
            // sleep
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // stop sensor
            adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD6);
            adpdApp.stopSensor();

        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
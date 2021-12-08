package com.analog.androidsamples;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.analog.study_watch_sdk.StudyWatch;
import com.analog.study_watch_sdk.application.ADPDApplication;
import com.analog.study_watch_sdk.application.TemperatureApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

/**
 * Quickstart for Temperature stream.
 */
public class TemperatureExample extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = TemperatureExample.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
            }
        }

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        final Button button = findViewById(R.id.button);
        button.setEnabled(false);
        // connect to study watch with its mac address.
        StudyWatch.connectBLE("D5:67:F1:CA:05:C5", getApplicationContext(), new StudyWatchCallback() {
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
            TemperatureApplication tempApp = watchSdk.getTemperatureApplication();
            ADPDApplication adpdApp = watchSdk.getADPDApplication();
            adpdApp.deleteDeviceConfigurationBlock();
            tempApp.deleteDeviceConfigurationBlock();
            adpdApp.loadConfiguration(adpdApp.DEVICE_GREEN);
            tempApp.setCallback(temperatureDataPacket -> {
                Log.d(TAG, "TEMP: " + temperatureDataPacket);
            }, tempApp.STREAM_TEMPERATURE4);
            tempApp.startSensor();
            tempApp.subscribeStream(tempApp.STREAM_TEMPERATURE4);

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tempApp.unsubscribeStream(tempApp.STREAM_TEMPERATURE4);
            tempApp.stopSensor();

            Log.d(TAG, " Total Packet lost During streaming :: " +
                    tempApp.getPacketLostCount(tempApp.STREAM_TEMPERATURE4));

        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
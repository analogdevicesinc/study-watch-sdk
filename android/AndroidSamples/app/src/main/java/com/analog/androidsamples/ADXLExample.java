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
import com.analog.study_watch_sdk.application.ADXLApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.packets.stream.ADXLDataPacket;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

import java.sql.Timestamp;

/**
 * Quickstart for ADXL stream.
 */
public class ADXLExample extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = ADXLExample.class.getSimpleName();

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
            ADXLApplication adxlApp = watchSdk.getADXLApplication();

            adxlApp.setCallback(adxlDataPacket -> {
                for (ADXLDataPacket.Payload.StreamData streamData : adxlDataPacket.payload.streamData) {
                    Timestamp obj = new Timestamp((long) streamData.getTimestamp());
                    Log.d(TAG, "Stream Data (Timestamp, X, Y, Z) :: " + obj.toString() + ", " +
                            streamData.getX() + ", " + streamData.getY() + ", " + streamData.getZ());
                }
            });
            // start sensor
            adxlApp.startSensor();
            // 50Hz
            adxlApp.writeRegister(new int[][]{{0x2c, 0x9A}});
            adxlApp.subscribeStream();
            // sleep
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // stop sensor
            adxlApp.unsubscribeStream();
            adxlApp.stopSensor();

        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
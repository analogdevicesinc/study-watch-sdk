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
import com.analog.study_watch_sdk.application.SQIApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.enums.adpd.ADPDLed;
import com.analog.study_watch_sdk.interfaces.ADPDCallback;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Quickstart for SQI stream.
 */
public class SQIExample extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = SQIExample.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 2);
            }
        }

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
        final Button button3 = findViewById(R.id.button3);
        button.setEnabled(false);
        button3.setEnabled(false);
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
            button.setEnabled(false);
            button3.setEnabled(true);
            SQIApplication sqiApp = watchSdk.getSQIApplication();
            ADPDApplication adpdApp = watchSdk.getADPDApplication();

            sqiApp.setCallback(sqiDataPacket -> Log.d(TAG, "SQI: " + sqiDataPacket));
            adpdApp.setCallback((ADPDCallback) adpdDataPacket -> Log.d(TAG, "ADPD6: " + adpdDataPacket), adpdApp.STREAM_ADPD6);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                adpdApp.loadConfiguration(adpdApp.DEVICE_GREEN);
                adpdApp.writeRegister(new int[][]{{0x0D, 0x2710}});
                // if DVT2 watch then adpdApp.CLOCK_1M
                adpdApp.calibrateClock(adpdApp.CLOCK_1M_AND_32M);
                sqiApp.setSlot(sqiApp.SLOT_F);
                sqiApp.startSensor();
                sqiApp.subscribeStream();
                adpdApp.enableAgc(new ADPDLed[]{adpdApp.LED_GREEN});
                adpdApp.startSensor();
                adpdApp.subscribeStream(adpdApp.STREAM_ADPD6);
            });
        });

        button3.setOnClickListener(v -> {
            button.setEnabled(true);
            button3.setEnabled(false);
            SQIApplication sqiApp = watchSdk.getSQIApplication();
            ADPDApplication adpdApp = watchSdk.getADPDApplication();

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                sqiApp.unsubscribeStream();
                adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD6);
                sqiApp.stopSensor();
                adpdApp.stopSensor();
            });
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
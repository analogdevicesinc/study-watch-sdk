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
import com.analog.study_watch_sdk.application.SQIApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.enums.adpd.ADPDLed;
import com.analog.study_watch_sdk.interfaces.ADPDCallback;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

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
            SQIApplication sqiApp = watchSdk.getSQIApplication();
            ADPDApplication adpdApp = watchSdk.getADPDApplication();

            sqiApp.setCallback(sqiDataPacket -> Log.d(TAG, "SQI: " + sqiDataPacket));
            adpdApp.setCallback((ADPDCallback) adpdDataPacket -> Log.d(TAG, "ADPD6: " + adpdDataPacket), adpdApp.STREAM_ADPD6);

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

            // sleep
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sqiApp.unsubscribeStream();
            adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD6);

            sqiApp.stopSensor();
            adpdApp.stopSensor();

        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
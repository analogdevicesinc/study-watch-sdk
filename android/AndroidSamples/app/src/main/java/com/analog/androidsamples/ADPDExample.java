package com.analog.androidsamples;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.analog.study_watch_sdk.StudyWatch;
import com.analog.study_watch_sdk.application.ADPDApplication;
import com.analog.study_watch_sdk.application.PMApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.enums.adpd.ADPDLed;
import com.analog.study_watch_sdk.interfaces.ADPDCallback;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

import java.io.File;

/**
 * Quickstart for ADPD stream.
 */
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                Use the code below to ask for ALL files permission.
//                Intent intent = new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
//                startActivity(intent);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);
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
            ADPDApplication adpdApp = watchSdk.getADPDApplication();
            PMApplication pmAPP = watchSdk.getPMApplication();

            adpdApp.setCallback((ADPDCallback) adpdDataPacket -> {
                for (long signalData : adpdDataPacket.payload.getSignalData()) {
                    Log.d(TAG, "Stream Data (Timestamp, adpdData, channel num) :: " + adpdDataPacket.payload.getTimestamp()
                            + " , " + signalData + " , " + adpdDataPacket.payload.getChannelNum());
                }
            }, adpdApp.STREAM_ADPD6);

            // config
            adpdApp.loadConfiguration(adpdApp.DEVICE_GREEN);
            if (pmAPP.getChipID(pmAPP.CHIP_ADPD4K).payload.getChipID() == 0xc0)
                adpdApp.calibrateClock(adpdApp.CLOCK_1M_AND_32M);
            else
                adpdApp.calibrateClock(adpdApp.CLOCK_1M);
            adpdApp.enableAgc(new ADPDLed[]{adpdApp.LED_GREEN});

            // start sensor
            adpdApp.startSensor();
            File file = new File(Environment.getExternalStorageDirectory(), "Test/adpd.csv");
            adpdApp.enableCSVLogging(file, adpdApp.STREAM_ADPD6);
            adpdApp.subscribeStream(adpdApp.STREAM_ADPD6);
            // sleep
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // stop sensor
            adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD6);
            adpdApp.disableCSVLogging(adpdApp.STREAM_ADPD6);
            adpdApp.stopSensor();

            Log.d(TAG, " Total Packet lost During streaming :: " +
                    adpdApp.getPacketLostCount(adpdApp.STREAM_ADPD6));

        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
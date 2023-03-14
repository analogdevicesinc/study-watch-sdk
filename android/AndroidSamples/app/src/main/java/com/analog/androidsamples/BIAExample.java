package com.analog.androidsamples;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.analog.study_watch_sdk.StudyWatch;
import com.analog.study_watch_sdk.application.BIAApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.packets.stream.BIADataPacket;
import com.analog.study_watch_sdk.interfaces.BCMCallback;
import com.analog.study_watch_sdk.interfaces.BIACallback;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Quickstart for BIA stream.
 */
public class BIAExample extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = BIAExample.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            statusCheck(); // checking for location permission.
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
            // Get applications from SDK
            BIAApplication biaApp = watchSdk.getBIAApplication();
            biaApp.setCallback((BCMCallback) bcmDataPacket -> Log.d(TAG, "BCM : " + bcmDataPacket), biaApp.STREAM_BCM);
            biaApp.setCallback((BIACallback) biaDataPacket -> {
                for (BIADataPacket.Payload.StreamData streamData : biaDataPacket.payload.getStreamData()) {
                    long bcmReal = streamData.getReal();
                    long bcmImaginary = streamData.getImaginary();
                    if (bcmReal == 0)
                        bcmReal = 1;
                    if (bcmImaginary == 0)
                        bcmImaginary = 1;
                    double impedanceImg = bcmImaginary;
                    double impedanceReal = bcmReal;
                    double realAndImg = impedanceReal * impedanceReal + impedanceImg * impedanceImg;
                    double impedanceMagnitude = Math.sqrt(realAndImg);
                    double impedancePhase = Math.atan2(impedanceImg, impedanceReal);
                    Log.d(TAG, " BIA (Timestamp, sequence number, impedanceMagnitude, impedancePhase) :: " +
                            streamData.getTimestamp() + " , " + biaDataPacket.payload.getSequenceNumber() + " , " +
                            impedanceMagnitude + " , " + impedancePhase);
                }
            }, biaApp.STREAM_BIA);
            File file = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/bia.csv");
            File bcmFile = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/bcm.csv");

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                //config
                biaApp.writeLibraryConfiguration(new long[][]{{0x6, 1}});
                // start sensor
                biaApp.enableCSVLogging(file, biaApp.STREAM_BIA);
                biaApp.enableCSVLogging(bcmFile, biaApp.STREAM_BCM);
                biaApp.startSensor();
                biaApp.subscribeStream(biaApp.STREAM_BIA);
                biaApp.subscribeStream(biaApp.STREAM_BCM);
            });
        });

        button3.setOnClickListener(v -> {
            button.setEnabled(true);
            button3.setEnabled(false);
            BIAApplication biaApp = watchSdk.getBIAApplication();
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                // stop sensor
                biaApp.unsubscribeStream(biaApp.STREAM_BIA);
                biaApp.unsubscribeStream(biaApp.STREAM_BCM);
                biaApp.stopSensor();
                biaApp.disableCSVLogging(biaApp.STREAM_BIA);
                biaApp.disableCSVLogging(biaApp.STREAM_BCM);
            });
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
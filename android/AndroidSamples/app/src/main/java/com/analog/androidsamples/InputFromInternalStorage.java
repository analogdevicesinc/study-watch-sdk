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
import com.analog.study_watch_sdk.application.ADXLApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.packets.DCBPacket;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

import java.io.File;
import java.io.IOException;

public class InputFromInternalStorage extends AppCompatActivity {

    private static final String TAG = EDAExample.class.getSimpleName();
    SDK watchSdk; // sdk reference variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Requires 2 extra permission in AndroidManifest:
        //      <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
        //      <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"
        //      tools:ignore="ScopedStorage" />

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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

        // Using button click to write device configuration for adxl.
        button.setOnClickListener(v -> {
            // Get ADXL application from SDK
            ADXLApplication adxl = watchSdk.getADXLApplication();
            // file to read.
            File file = new File(Environment.getExternalStorageDirectory(), "Test/adxl.dcfg");
            Log.d(TAG, "File Exist :: " + file.exists());
            Log.d(TAG, "File read permission :: " + file.canRead());
            try {
                DCBPacket packet = adxl.writeDeviceConfigurationBlockFromFile(file);
                Log.d(TAG, String.valueOf(packet));

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
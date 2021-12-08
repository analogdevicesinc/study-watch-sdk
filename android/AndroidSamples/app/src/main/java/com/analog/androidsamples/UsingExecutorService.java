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
import com.analog.study_watch_sdk.application.ADXLApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.packets.common.StreamStatusPacket;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Example show how to run SDK API in executorService, We recommend to use API calls in separate
 * thread so it doesn't cause any issue with main UI thread.
 */
public class UsingExecutorService extends AppCompatActivity {

    private static final String TAG = UsingExecutorService.class.getSimpleName();
    SDK watchSdk; // sdk reference variable

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

        // Using button click to run adxl get sensor status in executorService.
        button.setOnClickListener(v -> {
            // Get ADXL application from SDK
            ADXLApplication adxl = watchSdk.getADXLApplication();
            // executor Service
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                StreamStatusPacket packet = adxl.getSensorStatus();
                Log.d(TAG, String.valueOf(packet));
            });

        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
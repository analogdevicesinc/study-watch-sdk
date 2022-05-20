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
import com.analog.study_watch_sdk.application.EDAApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.packets.stream.EDADataPacket;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

/**
 * Quickstart for EDA stream.
 */
public class WatchOnWristAlgoExample extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = WatchOnWristAlgoExample.class.getSimpleName();

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
            EDAApplication edaApp = watchSdk.getEDAApplication();

            edaApp.setCallback(edaDataPacket -> {
                int[] realValue = new int[6]; // number of packets
                int i = 0;
                for (EDADataPacket.Payload.StreamData streamData : edaDataPacket.payload.getStreamData()) {
                    realValue[i++] = streamData.getReal();
                }
                // 50.0 is the threshold value for algo, if standard deviation in EDA real data
                // is less than 50 then it will return true else false.
                boolean watchOnWrist = isWatchOnWrist(realValue, 50.0);
                Log.d(TAG, "Watch on wrist status :: " + watchOnWrist);

            });
            //config
            edaApp.writeLibraryConfiguration(new int[][]{{0x0, 0x4}});
            // start sensor
            edaApp.startSensor();
            edaApp.subscribeStream();
            // sleep
            try {
                Thread.sleep(200000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // stop sensor
            edaApp.unsubscribeStream();
            edaApp.stopSensor();

        });


    }

    /**
     * This is one of the sample approach to detect watch on wrist.
     * This algorithm calculates the standard deviation of the given array and check if it is
     * in the threshold range.
     *
     * @param arr       Array of EDA real data
     * @param threshold threshold value for the standard deviation.
     * @return boolean indicating watch on wrist.
     */
    private boolean isWatchOnWrist(int[] arr, double threshold) {
        int n = arr.length;
        if (n == 0) return false;
        double sum = 0;
        double mean;
        double sd = 0.0;

        for (int value : arr) {
            sum = sum + value;
        }
        mean = sum / n;

        for (int value : arr) {
            sd = sd + Math.pow((value - mean), 2);
        }
        sd = sd / n;
        double res = Math.sqrt(sd);

        Log.d(TAG, "WatchOnWrist SD: " + res);
        return res < threshold;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
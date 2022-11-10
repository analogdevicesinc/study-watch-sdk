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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * On Wrist Algorithm.
 */
public class WatchOnWristAlgoExample extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = WatchOnWristAlgoExample.class.getSimpleName();

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
        StudyWatch.connectBLE("CE:7B:4B:3D:A6:F9", getApplicationContext(), new StudyWatchCallback() {
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
            button.setEnabled(false);
            button3.setEnabled(true);
            EDAApplication edaApp = watchSdk.getEDAApplication();
            edaApp.setCallback(edaDataPacket -> {
                int[] realValue = new int[6]; // number of packets
                int i = 0;
                for (EDADataPacket.Payload.StreamData streamData : edaDataPacket.payload.getStreamData()) {
                    realValue[i++] = streamData.getReal();
                }
                // 50.0 is the threshold value for algo, if standard deviation in EDA real data
                // is less than 50 then it will return true else false.
                WatchOnWristAlgoResult watchOnWrist = isWatchOnWrist(realValue, 50.0, 1.5);
                Log.d(TAG, "Watch on wrist status :: " + Arrays.toString(realValue) + " :: " + watchOnWrist);

            });
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                //config
                edaApp.writeLibraryConfiguration(new long[][]{{0x0, 0x4}});
                // start sensor
                edaApp.startSensor();
                edaApp.subscribeStream();

            });
        });

        button3.setOnClickListener(v -> {
            button.setEnabled(true);
            button3.setEnabled(false);
            EDAApplication edaApp = watchSdk.getEDAApplication();
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                // stop sensor
                edaApp.unsubscribeStream();
                edaApp.stopSensor();
            });
        });

    }

    /**
     * This is one of the sample approach to detect watch on wrist.
     * This algorithm calculates the standard deviation of the given array and check if it is
     * in the threshold range.
     *
     * @param arr       Array of EDA real data.
     * @param threshold threshold value for the standard deviation.
     * @param spread    spread for the SD to clean EDA real data.
     * @return WatchOnWristAlgoResult indicating watch on wrist with confidence.
     */
    private WatchOnWristAlgoResult isWatchOnWrist(int[] arr, double threshold, double spread) {
        int n = arr.length;
        if (n == 0) return new WatchOnWristAlgoResult(false, 0.0);
        double sum = 0;
        double mean;
        for (int value : arr)
            sum = sum + value;
        mean = sum / n;

        double sd = getStandardDeviation(arr, n);
        Log.d(TAG, "WatchOnWrist SD: (before cleanup) " + sd);
        // In this step we remove the values which are deviating more than 1.5 times of SD.
        ArrayList<Integer> cleanArr = new ArrayList<>();
        for (int value : arr) {
            if (value >= (mean - (spread * sd)) && value <= (mean + (spread * sd))) {
                cleanArr.add(value);
            }
        }
        int[] intArr = new int[cleanArr.size()];
        for (int i = 0; i < intArr.length; i++)
            intArr[i] = cleanArr.get(i);

        double newSD = getStandardDeviation(intArr, intArr.length);
        Log.d(TAG, "WatchOnWrist SD: (after cleanup) " + newSD);
        boolean status = false;
        // confidence is determined based on the elements count in the new Array after cleanup.
        double confidence = ((intArr.length * 1.0) / n) * 100.0;
        if (newSD < threshold)
            status = true;

        return new WatchOnWristAlgoResult(status, confidence);
    }

    /**
     * returns Standard Deviation of the given array.
     */
    private double getStandardDeviation(int[] arr, int n) {
        double sd = 0.0;
        double sum = 0;
        double mean;
        for (int value : arr)
            sum = sum + value;
        mean = sum / n;
        for (int value : arr)
            sd = sd + Math.pow((value - mean), 2);
        sd = sd / n;
        return Math.sqrt(sd);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}

/**
 * Watch on Wrist Algorithm Result structure.
 */
class WatchOnWristAlgoResult {
    boolean watchOnWristStatus;
    double watchOnWristConfidence;

    WatchOnWristAlgoResult(boolean watchOnWristStatus, double watchOnWristConfidence) {
        this.watchOnWristStatus = watchOnWristStatus;
        this.watchOnWristConfidence = watchOnWristConfidence;
    }

    @Override
    public String toString() {
        return "WatchOnWristAlgoResult{" +
                "watchOnWristStatus=" + watchOnWristStatus +
                ", watchOnWristConfidence=" + watchOnWristConfidence +
                '}';
    }
}
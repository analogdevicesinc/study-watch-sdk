package com.analog.androidsamples;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.analog.study_watch_sdk.StudyWatch;
import com.analog.study_watch_sdk.application.EDAApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

public class EDAExample extends AppCompatActivity {

    private static final String TAG = EDAExample.class.getSimpleName();
    SDK watchSdk; // sdk reference variable

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

        // Using button click to send EDA sensor start and stop after 30 sec.
        button.setOnClickListener(v -> {
            // Get EDA application from SDK
            EDAApplication eda = watchSdk.getEDAApplication();
            // callback to receive data packets from EDA stream
            eda.setCallback(edaDataPacket -> {
                Log.d(TAG, "" + edaDataPacket);
                // Sample Output(Class structure) ::  Header: {source=<Stream.EDA: [0xc4, 0x2]>, destination=<Application.APP_BLE: [0xc8, 0x8]>, length=0x3d, checksum=0x0}, Payload{command=<CommonCommand.STREAM_DATA: [0x28]>, status=<CommonStatus.OK: [0x0]>, sequenceNumber=4, dataType=0, streamData=[StreamData{timestamp=1758129180, realData=58137, imaginaryData=-172621824}, StreamData{timestamp=1758136945, realData=62922, imaginaryData=1090715648}, StreamData{timestamp=1758144708, realData=16663, imaginaryData=1064697856}, StreamData{timestamp=1758152472, realData=16265, imaginaryData=-1062404096}, StreamData{timestamp=1758160237, realData=49345, imaginaryData=-1062404096}, StreamData{timestamp=1758168001, realData=49345, imaginaryData=-1526726656}]}
            });
            // Setting up library config before starting EDA sensor
            eda.writeLibraryConfiguration(new long[][]{{0x0, 0x4}});
            // Starting and Subscribing to callback provided above
            eda.startSensor();
            eda.subscribeStream();
            // Waiting for 30 Sec
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Stopping and Un-subscribing
            eda.stopSensor();
            eda.unsubscribeStream();
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
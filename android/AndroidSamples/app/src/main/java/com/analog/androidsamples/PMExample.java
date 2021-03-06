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
import com.analog.study_watch_sdk.application.PMApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

import java.util.Calendar;

public class PMExample extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = PMExample.class.getSimpleName();

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
        StudyWatch.connectBLE("C5:05:CA:F1:67:D5", getApplicationContext(), new StudyWatchCallback() {
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
            PMApplication pmApp = watchSdk.getPMApplication();

            Log.d(TAG, "packet: " + pmApp.writeDeviceConfigurationBlock(new long[][]{{0x1, 0x1}}));
            Log.d(TAG, "packet: " + pmApp.readDeviceConfigurationBlock());


            Log.d(TAG, "packet: " + pmApp.getVersion());
            Log.d(TAG, "packet: " + pmApp.getMcuVersion());
            Log.d(TAG, "packet: " + pmApp.getSystemInfo());
            Log.d(TAG, "packet: " + pmApp.getBatteryInfo());
            Log.d(TAG, "packet: " + pmApp.getDatetime());
            Calendar cal = Calendar.getInstance();
            Log.d(TAG, "packet: " + pmApp.setDatetime(cal));
//            Log.d(TAG, "packet: "+ pmApp.systemReset());


        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
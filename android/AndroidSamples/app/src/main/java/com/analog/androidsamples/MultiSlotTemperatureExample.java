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
import com.analog.study_watch_sdk.application.TemperatureApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

import java.io.File;
import java.io.IOException;

/**
 * Temperature application support 12 slot streaming, but currently only
 * STREAM_TEMPERATURE3, STREAM_TEMPERATURE4, STREAM_TEMPERATURE10, STREAM_TEMPERATURE11
 * STREAM_TEMPERATURE12 are supported.
 * <p>
 * STREAM_TEMPERATURE4 is same as previous STREAM_TEMPERATURE.
 */
public class MultiSlotTemperatureExample extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = MultiSlotTemperatureExample.class.getSimpleName();

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
            TemperatureApplication tempApp = watchSdk.getTemperatureApplication();
            ADPDApplication adpd = watchSdk.getADPDApplication();
            PMApplication pmAPP = watchSdk.getPMApplication();

            adpd.deleteDeviceConfigurationBlock();
            File fileDVT1 = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/temperature_adpd_dcb_DVT1.dcfg");
            File fileDVT2 = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/temperature_adpd_dcb_DVT2.dcfg");
            adpd.deleteDeviceConfigurationBlock();

            // config
            if (pmAPP.getChipID(pmAPP.CHIP_ADPD4K).payload.getChipID() == 0xc0) {
                try {
                    adpd.writeDeviceConfigurationBlockFromFile(fileDVT1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    adpd.writeDeviceConfigurationBlockFromFile(fileDVT2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            adpd.loadConfiguration(adpd.DEVICE_GREEN);

            File file_temp = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/temperature_lcfg_dcb.lcfg");
            tempApp.deleteDeviceConfigurationBlock();
            try {
                tempApp.writeDeviceConfigurationBlockFromFile(file_temp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tempApp.writeDCBToLCFG();
            tempApp.setCallback(temperatureDataPacket -> Log.d(TAG, "TEMP: " + temperatureDataPacket), tempApp.STREAM_TEMPERATURE3);
            tempApp.setCallback(temperatureDataPacket -> Log.d(TAG, "TEMP: " + temperatureDataPacket), tempApp.STREAM_TEMPERATURE4);
            tempApp.setCallback(temperatureDataPacket -> Log.d(TAG, "TEMP: " + temperatureDataPacket), tempApp.STREAM_TEMPERATURE10);
            tempApp.setCallback(temperatureDataPacket -> Log.d(TAG, "TEMP: " + temperatureDataPacket), tempApp.STREAM_TEMPERATURE11);
            tempApp.setCallback(temperatureDataPacket -> Log.d(TAG, "TEMP: " + temperatureDataPacket), tempApp.STREAM_TEMPERATURE12);
            tempApp.startSensor();
            tempApp.subscribeStream(tempApp.STREAM_TEMPERATURE3);
            tempApp.subscribeStream(tempApp.STREAM_TEMPERATURE4);
            tempApp.subscribeStream(tempApp.STREAM_TEMPERATURE10);
            tempApp.subscribeStream(tempApp.STREAM_TEMPERATURE11);
            tempApp.subscribeStream(tempApp.STREAM_TEMPERATURE12);
            File file12 = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/temp12.csv");

            tempApp.enableCSVLogging(file12, tempApp.STREAM_TEMPERATURE12);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tempApp.unsubscribeStream(tempApp.STREAM_TEMPERATURE3);
            tempApp.unsubscribeStream(tempApp.STREAM_TEMPERATURE4);
            tempApp.unsubscribeStream(tempApp.STREAM_TEMPERATURE10);
            tempApp.unsubscribeStream(tempApp.STREAM_TEMPERATURE11);
            tempApp.unsubscribeStream(tempApp.STREAM_TEMPERATURE12);
            tempApp.stopSensor();
            tempApp.disableCSVLogging(tempApp.STREAM_TEMPERATURE12);

        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
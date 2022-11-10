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
import com.analog.study_watch_sdk.application.ADXLApplication;
import com.analog.study_watch_sdk.application.ECGApplication;
import com.analog.study_watch_sdk.application.FSApplication;
import com.analog.study_watch_sdk.application.PMApplication;
import com.analog.study_watch_sdk.application.TemperatureApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Example show how to log MultiStreamExample1 streams into firmware filesystem.
 */

public class MultiStreamWithFSLoggingExample extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = MultiStreamWithFSLoggingExample.class.getSimpleName();


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
            ECGApplication ecgApp = watchSdk.getECGApplication();
            ADPDApplication adpdApp = watchSdk.getADPDApplication();
            ADXLApplication adxlApp = watchSdk.getADXLApplication();
            TemperatureApplication tempApp = watchSdk.getTemperatureApplication();
            PMApplication pmAPP = watchSdk.getPMApplication();
            FSApplication fsAPP = watchSdk.getFSApplication();

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                //setting ECG ODR to 250Hz
                ecgApp.writeLibraryConfiguration(new int[][]{{0x0, 0xFA}});

                // adpd config
                File dvt1DCB = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/DVT1_TEMP+4LED.dcfg");
                File dvt2DCB = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/DVT2_TEMP+4LED.dcfg");

                // checking for DVT2 board
                if (pmAPP.getChipID(pmAPP.CHIP_ADPD4K).payload.getChipID() == 0xc0) {
                    try {
                        adpdApp.writeDeviceConfigurationBlockFromFile(dvt1DCB);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    adpdApp.calibrateClock(adpdApp.CLOCK_1M_AND_32M);
                } else {
                    try {
                        adpdApp.writeDeviceConfigurationBlockFromFile(dvt2DCB);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    adpdApp.calibrateClock(adpdApp.CLOCK_1M);
                }
                adpdApp.loadConfiguration(adpdApp.DEVICE_GREEN);
                adpdApp.writeRegister(new int[][]{{0xD, 0x2710}});
                // senors
                ecgApp.startSensor();
                adpdApp.startSensor();
                adxlApp.startSensor();
                tempApp.startSensor();

                //setting ADXL ODR to 50Hz -- ADXL Loads DCB when Start Sensor is done, if no DCB it will load Default Config
                adxlApp.writeRegister(new int[][]{{0x2c, 0x9A}});

                // subs
                fsAPP.subscribeStream(fsAPP.STREAM_ECG);
                fsAPP.subscribeStream(fsAPP.STREAM_ADPD6);
                fsAPP.subscribeStream(fsAPP.STREAM_ADPD7);
                fsAPP.subscribeStream(fsAPP.STREAM_ADPD8);
                fsAPP.subscribeStream(fsAPP.STREAM_ADPD9);
                fsAPP.subscribeStream(fsAPP.STREAM_ADXL);
                fsAPP.subscribeStream(fsAPP.STREAM_TEMPERATURE4);

                // logging started
                fsAPP.startLogging();
            });
        });

        button3.setOnClickListener(v -> {
            button.setEnabled(true);
            button3.setEnabled(false);
            // Get applications from SDK
            ECGApplication ecgApp = watchSdk.getECGApplication();
            ADPDApplication adpdApp = watchSdk.getADPDApplication();
            ADXLApplication adxlApp = watchSdk.getADXLApplication();
            TemperatureApplication tempApp = watchSdk.getTemperatureApplication();
            FSApplication fsAPP = watchSdk.getFSApplication();

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                fsAPP.unsubscribeStream(fsAPP.STREAM_ECG);
                fsAPP.unsubscribeStream(fsAPP.STREAM_ADPD6);
                fsAPP.unsubscribeStream(fsAPP.STREAM_ADPD7);
                fsAPP.unsubscribeStream(fsAPP.STREAM_ADPD8);
                fsAPP.unsubscribeStream(fsAPP.STREAM_ADPD9);
                fsAPP.unsubscribeStream(fsAPP.STREAM_ADXL);
                fsAPP.unsubscribeStream(fsAPP.STREAM_TEMPERATURE4);

                ecgApp.stopSensor();
                adpdApp.stopSensor();
                adxlApp.stopSensor();
                tempApp.stopSensor();

                // logging stopped
                fsAPP.stopLogging();
            });
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
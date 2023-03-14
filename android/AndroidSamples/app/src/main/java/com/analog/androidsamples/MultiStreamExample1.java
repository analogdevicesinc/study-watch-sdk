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
import com.analog.study_watch_sdk.application.ADPDApplication;
import com.analog.study_watch_sdk.application.ADXLApplication;
import com.analog.study_watch_sdk.application.ECGApplication;
import com.analog.study_watch_sdk.application.PMApplication;
import com.analog.study_watch_sdk.application.TemperatureApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.interfaces.ADPDCallback;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Example show how to run ECG + every wavelength of the optical sensor (F,G,H,I) + ADXL + Temperature.
 */

public class MultiStreamExample1 extends AppCompatActivity {
    SDK watchSdk;
    private static final String TAG = MultiStreamExample1.class.getSimpleName();

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
        StudyWatch.connectBLE("CE:7B:4B:3D:A6:F9", getApplicationContext(), new StudyWatchCallback() {
            @Override
            public void onSuccess(SDK sdk) {
                Log.d(TAG, "onSuccess: SDK Ready");
                watchSdk = sdk; // store this sdk reference to be used for creating applications
                runOnUiThread(() -> button.setEnabled(true));
            }

            @Override
            public void onFailure(String message, int state) {
                Log.e(TAG, "onError: " + message);
            }
        });

        button.setOnClickListener(v -> {
            button.setEnabled(false);
            button3.setEnabled(true);
            ECGApplication ecgApp = watchSdk.getECGApplication();
            ADPDApplication adpdApp = watchSdk.getADPDApplication();
            ADXLApplication adxlApp = watchSdk.getADXLApplication();
            TemperatureApplication tempApp = watchSdk.getTemperatureApplication();
            PMApplication pmAPP = watchSdk.getPMApplication();

            // callbacks
            ecgApp.setCallback(ecgDataPacket -> {
                Log.d(TAG, "ECG: " + ecgDataPacket);
            });
            adpdApp.setCallback((ADPDCallback) adpdDataPacket -> {
                Log.d(TAG, "ADPD6: " + adpdDataPacket);
            }, adpdApp.STREAM_ADPD6);
            adpdApp.setCallback((ADPDCallback) adpdDataPacket -> {
                Log.d(TAG, "ADPD7: " + adpdDataPacket);
            }, adpdApp.STREAM_ADPD7);
            adpdApp.setCallback((ADPDCallback) adpdDataPacket -> {
                Log.d(TAG, "ADPD8: " + adpdDataPacket);
            }, adpdApp.STREAM_ADPD8);
            adpdApp.setCallback((ADPDCallback) adpdDataPacket -> {
                Log.d(TAG, "ADPD9: " + adpdDataPacket);
            }, adpdApp.STREAM_ADPD9);
            adxlApp.setCallback(adxlDataPacket -> {
                Log.d(TAG, "ADXL: " + adxlDataPacket);
            });
            tempApp.setCallback(temperatureDataPacket -> {
                Log.d(TAG, "TEMP: " + temperatureDataPacket);
            }, tempApp.STREAM_TEMPERATURE4);

            File dvt1DCB = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/DVT1_TEMP+4LED.dcfg");
            File dvt2DCB = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/DVT2_TEMP+4LED.dcfg");

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                //setting ECG ODR to 250Hz
                ecgApp.writeLibraryConfiguration(new int[][]{{0x0, 0xFA}});
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

//                // senors
                ecgApp.startSensor();
                adpdApp.startSensor();
                adxlApp.startSensor();
                tempApp.startSensor();

                //setting ADXL ODR to 50Hz -- ADXL Loads DCB when Start Sensor is done, if no DCB it will load Default Config
                adxlApp.writeRegister(new int[][]{{0x2c, 0x9A}});

                // subs
                Log.d(TAG, "onCreate: " + ecgApp.subscribeStream());
                adpdApp.subscribeStream(adpdApp.STREAM_ADPD6);
                adpdApp.subscribeStream(adpdApp.STREAM_ADPD7);
                adpdApp.subscribeStream(adpdApp.STREAM_ADPD8);
                adpdApp.subscribeStream(adpdApp.STREAM_ADPD9);
                adxlApp.subscribeStream();
                tempApp.subscribeStream(tempApp.STREAM_TEMPERATURE4);
            });
        });


        button3.setOnClickListener(v -> {
            button.setEnabled(true);
            button3.setEnabled(false);
            ECGApplication ecgApp = watchSdk.getECGApplication();
            ADPDApplication adpdApp = watchSdk.getADPDApplication();
            ADXLApplication adxlApp = watchSdk.getADXLApplication();
            TemperatureApplication tempApp = watchSdk.getTemperatureApplication();

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                Log.d(TAG, "onCreate: " + ecgApp.unsubscribeStream());
                Log.d(TAG, "onCreate: " + adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD6));
                Log.d(TAG, "onCreate: " + adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD7));
                Log.d(TAG, "onCreate: " + adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD8));
                Log.d(TAG, "onCreate: " + adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD9));
                Log.d(TAG, "onCreate: " + adxlApp.unsubscribeStream());
                Log.d(TAG, "onCreate: " + tempApp.unsubscribeStream(tempApp.STREAM_TEMPERATURE4));

                Log.d(TAG, "onCreate: " + ecgApp.stopSensor());
                Log.d(TAG, "onCreate: " + adpdApp.stopSensor());
                Log.d(TAG, "onCreate: " + adxlApp.stopSensor());
                Log.d(TAG, "onCreate: " + tempApp.stopSensor());

                Log.d(TAG, "Packet Loss ADPD6: " + adpdApp.getPacketLostCount(adpdApp.STREAM_ADPD6));
                Log.d(TAG, "Packet Loss ADPD7: " + adpdApp.getPacketLostCount(adpdApp.STREAM_ADPD7));
                Log.d(TAG, "Packet Loss ADPD8: " + adpdApp.getPacketLostCount(adpdApp.STREAM_ADPD8));
                Log.d(TAG, "Packet Loss ADPD9: " + adpdApp.getPacketLostCount(adpdApp.STREAM_ADPD9));
                Log.d(TAG, "Packet Loss ADXL: " + adxlApp.getPacketLostCount());
                Log.d(TAG, "Packet Loss TEMP4: " + tempApp.getPacketLostCount(tempApp.STREAM_TEMPERATURE4));
                Log.d(TAG, "Packet Loss ECG: " + ecgApp.getPacketLostCount());
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
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
import com.analog.study_watch_sdk.application.ADP5360Application;
import com.analog.study_watch_sdk.application.ADPDApplication;
import com.analog.study_watch_sdk.application.ADXLApplication;
import com.analog.study_watch_sdk.application.EDAApplication;
import com.analog.study_watch_sdk.application.PMApplication;
import com.analog.study_watch_sdk.application.TemperatureApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.interfaces.ADPDCallback;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Use-case C
 * Application	Data rate (bytes/sec)
 * ADPD@100Hz, 4 slots, 2channels	5867
 * ADXL@50Hz	550
 * EDA@30Hz	305
 * Temp@1Hz	  20
 * Total 6742
 **/
public class UseCaseCWithBatteryStream extends AppCompatActivity {

    SDK watchSdk;
    HashMap<String, Integer> packetCount = new HashMap<>();
    private static final String TAG = UseCaseCWithBatteryStream.class.getSimpleName();

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
            button.setEnabled(false);
            button3.setEnabled(true);
            // Get applications from SDK
            ADXLApplication adxlApp = watchSdk.getADXLApplication();
            ADPDApplication adpdApp = watchSdk.getADPDApplication();
            EDAApplication edaApp = watchSdk.getEDAApplication();
            TemperatureApplication tempApp = watchSdk.getTemperatureApplication();
            PMApplication pmAPP = watchSdk.getPMApplication();
            ADP5360Application adp5360Application = watchSdk.getADP5360Application();

            packetCount.put("adxl", 0);
            packetCount.put("adpd6", 0);
            packetCount.put("adpd7", 0);
            packetCount.put("adpd8", 0);
            packetCount.put("adpd9", 0);
            packetCount.put("eda", 0);
            packetCount.put("temp", 0);

            adp5360Application.setCallback(batteryInfoPacket -> {
                Log.d(TAG, "onCreate: " + batteryInfoPacket);
            });

            adxlApp.setCallback(adxlDataPacket -> {
                packetCount.put("adxl", packetCount.get("adxl") + 1);
            });

            adpdApp.setCallback((ADPDCallback) adpdDataPacket -> {
                packetCount.put("adpd6", packetCount.get("adpd6") + 1);
            }, adpdApp.STREAM_ADPD6);

            adpdApp.setCallback((ADPDCallback) adpdDataPacket -> {
                packetCount.put("adpd7", packetCount.get("adpd7") + 1);
            }, adpdApp.STREAM_ADPD7);

            adpdApp.setCallback((ADPDCallback) adpdDataPacket -> {
                packetCount.put("adpd8", packetCount.get("adpd8") + 1);
            }, adpdApp.STREAM_ADPD8);

            adpdApp.setCallback((ADPDCallback) adpdDataPacket -> {
                packetCount.put("adpd9", packetCount.get("adpd9") + 1);
            }, adpdApp.STREAM_ADPD9);

            edaApp.setCallback(edaDataPacket -> {
                packetCount.put("eda", packetCount.get("eda") + 1);
            });

            tempApp.setCallback(temperatureDataPacket -> {
//                Log.d(TAG, "onCreate: " + temperatureDataPacket);
                packetCount.put("temp", packetCount.get("temp") + 1);
            }, tempApp.STREAM_TEMPERATURE4);

            File dvt1DCB = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/DVT1_TEMP+4LED.dcfg");
            File csv = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/temp1.csv");
            File dvt2DCB = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/DVT2_TEMP+4LED.dcfg");

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {

                Log.d(TAG, "onCreate: " + pmAPP.getVersion());
                adpdApp.deleteDeviceConfigurationBlock();
                adxlApp.deleteDeviceConfigurationBlock();
                tempApp.deleteDeviceConfigurationBlock();
                edaApp.deleteDeviceConfigurationBlock(edaApp.EDA_DCFG_BLOCK);

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

                // ADPD 100HZ
                adpdApp.writeRegister(new int[][]{{0xD, 0x2710}});
                //setting EDA ODR to 30HZ
                edaApp.writeLibraryConfiguration(new long[][]{{0x0, 0x1E}});
                edaApp.writeLibraryConfiguration(new long[][]{{0x2, 0x1}});

//             Subscribe
                adpdApp.subscribeStream(adpdApp.STREAM_ADPD6);
                adpdApp.subscribeStream(adpdApp.STREAM_ADPD7);
                adpdApp.subscribeStream(adpdApp.STREAM_ADPD8);
                adpdApp.subscribeStream(adpdApp.STREAM_ADPD9);
                edaApp.subscribeStream();
                tempApp.subscribeStream(tempApp.STREAM_TEMPERATURE4);
                tempApp.enableCSVLogging(csv, tempApp.STREAM_TEMPERATURE4);
                adxlApp.subscribeStream();
                adp5360Application.subscribeStreamData(adp5360Application.STREAM_BATTERY);
//
//             start sensor
                edaApp.startSensor();
                adpdApp.startSensor();
                tempApp.startSensor();
                adxlApp.startSensor();
                adxlApp.writeRegister(new int[][]{{0x2c, 0x9A}}); // 50Hz
            });
        });

        button3.setOnClickListener(v -> {
            button.setEnabled(true);
            button3.setEnabled(false);
            // Get applications from SDK
            ADXLApplication adxlApp = watchSdk.getADXLApplication();
            ADPDApplication adpdApp = watchSdk.getADPDApplication();
            EDAApplication edaApp = watchSdk.getEDAApplication();
            TemperatureApplication tempApp = watchSdk.getTemperatureApplication();
            PMApplication pmAPP = watchSdk.getPMApplication();
            ADP5360Application adp5360Application = watchSdk.getADP5360Application();

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                // unSubscribe
                adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD6);
                adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD7);
                adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD8);
                adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD9);
                edaApp.unsubscribeStream();
                adxlApp.unsubscribeStream();
                tempApp.unsubscribeStream(tempApp.STREAM_TEMPERATURE4);
                tempApp.disableCSVLogging(tempApp.STREAM_TEMPERATURE4);
                adp5360Application.unsubscribeStream(adp5360Application.STREAM_BATTERY);

                // stop sensor
                edaApp.stopSensor();
                adpdApp.stopSensor();
                tempApp.stopSensor();
                adxlApp.stopSensor();

                Log.d(TAG, "Packet Count:" + packetCount);

                Log.d(TAG, "ADPD6 Packet Lost:" + adpdApp.getPacketLostCount(adpdApp.STREAM_ADPD6));
                Log.d(TAG, "ADPD7 Packet Lost:" + adpdApp.getPacketLostCount(adpdApp.STREAM_ADPD7));
                Log.d(TAG, "ADPD8 Packet Lost:" + adpdApp.getPacketLostCount(adpdApp.STREAM_ADPD8));
                Log.d(TAG, "ADPD8 Packet Lost:" + adpdApp.getPacketLostCount(adpdApp.STREAM_ADPD9));
                Log.d(TAG, "ADXL Packet Lost:" + adxlApp.getPacketLostCount());
                Log.d(TAG, "EDA Packet Lost:" + edaApp.getPacketLostCount());
                Log.d(TAG, "Temperature Packet Lost:" + tempApp.getPacketLostCount(tempApp.STREAM_TEMPERATURE4));
            });
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
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
import com.analog.study_watch_sdk.application.EDAApplication;
import com.analog.study_watch_sdk.application.PMApplication;
import com.analog.study_watch_sdk.application.PPGApplication;
import com.analog.study_watch_sdk.application.TemperatureApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.enums.adpd.ADPDLed;
import com.analog.study_watch_sdk.interfaces.ADPDCallback;
import com.analog.study_watch_sdk.interfaces.PPGCallback;
import com.analog.study_watch_sdk.interfaces.SYNCPPGCallback;
import com.analog.study_watch_sdk.interfaces.SlotApp;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Use-case D
 * Application
 * ADPD
 * ADXL
 * EDA
 * PPG
 * Temp
 **/
public class UseCaseD extends AppCompatActivity {

    SDK watchSdk;
    HashMap<String, Integer> packetCount = new HashMap<>();
    private static final String TAG = UseCaseD.class.getSimpleName();

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
        StudyWatch.connectBLE("D3:EC:DA:54:95:A6", getApplicationContext(), new StudyWatchCallback() {
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
            PPGApplication ppgApp = watchSdk.getPPGApplication();
            EDAApplication edaApp = watchSdk.getEDAApplication();
            PMApplication pmApp = watchSdk.getPMApplication();
            TemperatureApplication tempApp = watchSdk.getTemperatureApplication();

            packetCount.put("adpd6", 0);
            packetCount.put("adpd7", 0);
            packetCount.put("adpd8", 0);
            packetCount.put("eda", 0);
            packetCount.put("temp", 0);
            packetCount.put("ppg", 0);
            packetCount.put("adxl", 0);

            adpdApp.setCallback((ADPDCallback) adpdDataPacket -> {
                packetCount.put("adpd6", packetCount.get("adpd6") + 1);
            }, adpdApp.STREAM_ADPD6);

            adpdApp.setCallback((ADPDCallback) adpdDataPacket -> {
                packetCount.put("adpd7", packetCount.get("adpd7") + 1);
            }, adpdApp.STREAM_ADPD7);

            adpdApp.setCallback((ADPDCallback) adpdDataPacket -> {
                packetCount.put("adpd8", packetCount.get("adpd8") + 1);
            }, adpdApp.STREAM_ADPD8);

            edaApp.setCallback(edaDataPacket -> {
                packetCount.put("eda", packetCount.get("eda") + 1);
            });

            adxlApp.setCallback(adxlDataPacket -> {
                packetCount.put("adxl", packetCount.get("adxl") + 1);
            });

            tempApp.setCallback(temperatureDataPacket -> {
                packetCount.put("temp", packetCount.get("temp") + 1);
            }, tempApp.STREAM_TEMPERATURE4);

            ppgApp.setCallback((PPGCallback) ppgDataPacket -> {
                packetCount.put("ppg", packetCount.get("ppg") + 1);
            }, ppgApp.STREAM_PPG);


            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                Log.d(TAG, "onCreate: " + pmApp.getVersion());
                adpdApp.deleteDeviceConfigurationBlock();
                tempApp.deleteDeviceConfigurationBlock();
                ppgApp.deleteDeviceConfigurationBlock();
                edaApp.deleteDeviceConfigurationBlock(edaApp.EDA_DCFG_BLOCK);
                ppgApp.deleteDeviceConfigurationBlock();

                SlotApp[][] configBuilder = new SlotApp[][]{
                        {adpdApp.SLOT_D, adpdApp.APP_TEMPERATURE_THERMISTOR},
                        {adpdApp.SLOT_E, adpdApp.APP_TEMPERATURE_RESISTOR},
                        {adpdApp.SLOT_F, adpdApp.APP_ADPD_GREEN},
                        {adpdApp.SLOT_G, adpdApp.APP_ADPD_RED},
                        {adpdApp.SLOT_H, adpdApp.APP_ADPD_INFRARED}
                };
                adpdApp.createDeviceConfiguration(configBuilder);
                adpdApp.loadConfiguration(adpdApp.DEVICE_GREEN);
                // checking for DVT2 board
                if (pmApp.getChipID(pmApp.CHIP_ADPD4K).payload.getChipID() == 0xc0)
                    adpdApp.calibrateClock(adpdApp.CLOCK_1M_AND_32M);
                else
                    adpdApp.calibrateClock(adpdApp.CLOCK_1M);
                adpdApp.writeRegister(new int[][]{{0xD, 0x2710}});
                adpdApp.enableUCHR(adpdApp.SLOT_F);

                ppgApp.setLibraryConfiguration(ppgApp.LCFG_ID_ADPD4000);
                adpdApp.enableAgc(new ADPDLed[]{adpdApp.LED_GREEN, adpdApp.LED_IR, adpdApp.LED_RED});
                edaApp.writeLibraryConfiguration(new long[][]{{0x0, 0x1E}});


//             Subscribe
                edaApp.subscribeStream();
                ppgApp.subscribeStream(ppgApp.STREAM_PPG);
                adpdApp.subscribeStream(adpdApp.STREAM_ADPD6);
                adpdApp.subscribeStream(adpdApp.STREAM_ADPD7);
                adpdApp.subscribeStream(adpdApp.STREAM_ADPD8);
                adxlApp.subscribeStream();
                tempApp.subscribeStream(tempApp.STREAM_TEMPERATURE4);
//
//             start sensor
                edaApp.startSensor();
                adpdApp.startSensor();
                adxlApp.startSensor();
                tempApp.startSensor();
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
            PPGApplication ppgApp = watchSdk.getPPGApplication();


            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                // unSubscribe
                tempApp.unsubscribeStream(tempApp.STREAM_TEMPERATURE4);
                adxlApp.unsubscribeStream();
                adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD6);
                adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD7);
                adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD8);
                ppgApp.unsubscribeStream(ppgApp.STREAM_PPG);
                edaApp.unsubscribeStream();


                // stop sensor
                tempApp.stopSensor();
                adxlApp.stopSensor();
                adpdApp.stopSensor();
                edaApp.stopSensor();

                Log.d(TAG, "Packet Count:" + packetCount);

                Log.d(TAG, "ADPD6 Packet Lost:" + adpdApp.getPacketLostCount(adpdApp.STREAM_ADPD6));
                Log.d(TAG, "ADPD7 Packet Lost:" + adpdApp.getPacketLostCount(adpdApp.STREAM_ADPD7));
                Log.d(TAG, "ADPD8 Packet Lost:" + adpdApp.getPacketLostCount(adpdApp.STREAM_ADPD8));
                Log.d(TAG, "EDA Packet Lost:" + edaApp.getPacketLostCount());
                Log.d(TAG, "Temperature Packet Lost:" + tempApp.getPacketLostCount(tempApp.STREAM_TEMPERATURE4));
                Log.d(TAG, "PPG Packet Lost:" + ppgApp.getPacketLostCount(ppgApp.STREAM_PPG));
                Log.d(TAG, "ADXL Packet Lost:" + adxlApp.getPacketLostCount());
            });
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
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
import com.analog.study_watch_sdk.application.PedometerApplication;
import com.analog.study_watch_sdk.application.TemperatureApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.interfaces.ADPDCallback;
import com.analog.study_watch_sdk.interfaces.AGCCallback;
import com.analog.study_watch_sdk.interfaces.PPGCallback;
import com.analog.study_watch_sdk.interfaces.SYNCPPGCallback;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

import java.io.File;
import java.io.IOException;


/**
 * Use-case A
 * Application	Data rate (bytes/sec)
 * ADPD@100Hz, 3 slots, 2channels	4400
 * ADXL@100Hz	1100
 * EDA@30Hz	305
 * PPG@1Hz	  46
 * Temp@1Hz	  20
 * Pedometer@1Hz	  23
 * AGC@1Hz	48
 * Total	5942
 **/
public class UseCaseA extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = UseCaseA.class.getSimpleName();

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
            ADXLApplication adxlApp = watchSdk.getADXLApplication();
            ADPDApplication adpdApp = watchSdk.getADPDApplication();
            EDAApplication edaApp = watchSdk.getEDAApplication();
            PPGApplication ppgApp = watchSdk.getPPGApplication();
            TemperatureApplication tempApp = watchSdk.getTemperatureApplication();
            PedometerApplication pedApp = watchSdk.getPedometerApplication();
            PMApplication pmAPP = watchSdk.getPMApplication();

            adpdApp.deleteDeviceConfigurationBlock();
            ppgApp.deleteDeviceConfigurationBlock();
            adxlApp.setCallback(adxlDataPacket -> {

            });

            adpdApp.setCallback((ADPDCallback) adpdDataPacket -> {

            }, adpdApp.STREAM_ADPD6);

            adpdApp.setCallback((ADPDCallback) adpdDataPacket -> {

            }, adpdApp.STREAM_ADPD7);

            adpdApp.setCallback((ADPDCallback) adpdDataPacket -> {

            }, adpdApp.STREAM_ADPD8);

            adpdApp.setCallback((AGCCallback) agcDataPacket -> {

            }, adpdApp.STREAM_STATIC_AGC);

            ppgApp.setCallback((AGCCallback) agcDataPacket -> {

            }, ppgApp.STREAM_DYNAMIC_AGC);

            edaApp.setCallback(edaDataPacket -> {

            });

            ppgApp.setCallback((PPGCallback) ppgDataPacket -> {

            }, ppgApp.STREAM_PPG);

            ppgApp.setCallback((SYNCPPGCallback) syncppgDataPacket -> {

            }, ppgApp.STREAM_SYNC_PPG);

            tempApp.setCallback(temperatureDataPacket -> {
            }, tempApp.STREAM_TEMPERATURE4);
            pedApp.setCallback(pedometerDataPacket -> {

            });

            File dvt1DCB = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/DVT1_TEMP+3LED.dcfg");
            File dvt2DCB = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/DVT2_TEMP+3LED.dcfg");

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

            adpdApp.enableUCHR(adpdApp.SLOT_F);
            // 100HZ
            adpdApp.writeRegister(new int[][]{{0xD, 0x2710}});
            ppgApp.setLibraryConfiguration(ppgApp.LCFG_ID_ADPD4000);
            //setting EDA ODR to 30HZ
            edaApp.writeLibraryConfiguration(new int[][]{{0x0, 0x1E}});
            edaApp.writeLibraryConfiguration(new int[][]{{0x2, 0x1}});

            // Subscribe
            adpdApp.subscribeStream(adpdApp.STREAM_ADPD6);
            adpdApp.subscribeStream(adpdApp.STREAM_ADPD7);
            adpdApp.subscribeStream(adpdApp.STREAM_ADPD8);
            adpdApp.subscribeStream(adpdApp.STREAM_STATIC_AGC);
            edaApp.subscribeStream();
            ppgApp.subscribeStream(ppgApp.STREAM_PPG);
            ppgApp.subscribeStream(ppgApp.STREAM_DYNAMIC_AGC);
            pedApp.subscribeStream();
            tempApp.subscribeStream(tempApp.STREAM_TEMPERATURE4);
            adxlApp.subscribeStream();

            // start sensor
            edaApp.startSensor();
            adpdApp.startSensor();
            pedApp.startSensor();
            tempApp.startSensor();
            adxlApp.startSensor();
            adxlApp.writeRegister(new int[][]{{0x2c, 0x9B}}); // 100Hz

            // sleep
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // unSubscribe
            adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD6);
            adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD7);
            adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD8);
            adpdApp.unsubscribeStream(adpdApp.STREAM_STATIC_AGC);
            edaApp.unsubscribeStream();
            ppgApp.unsubscribeStream(ppgApp.STREAM_PPG);
            ppgApp.unsubscribeStream(ppgApp.STREAM_DYNAMIC_AGC);
            pedApp.unsubscribeStream();
            adxlApp.unsubscribeStream();
            tempApp.unsubscribeStream(tempApp.STREAM_TEMPERATURE4);

            // stop sensor
            edaApp.stopSensor();
            adpdApp.stopSensor();
            pedApp.stopSensor();
            tempApp.stopSensor();
            adxlApp.stopSensor();

            Log.d(TAG, "ADPD6 Packet Lost:" + adpdApp.getPacketLostCount(adpdApp.STREAM_ADPD6));
            Log.d(TAG, "ADPD7 Packet Lost:" + adpdApp.getPacketLostCount(adpdApp.STREAM_ADPD7));
            Log.d(TAG, "ADPD8 Packet Lost:" + adpdApp.getPacketLostCount(adpdApp.STREAM_ADPD8));
            Log.d(TAG, "STATIC_AGC Packet Lost: " + adpdApp.getPacketLostCount(adpdApp.STREAM_STATIC_AGC));
            Log.d(TAG, "EDA Packet Lost:" + edaApp.getPacketLostCount());
            Log.d(TAG, "PPG Packet Lost:" + ppgApp.getPacketLostCount(ppgApp.STREAM_PPG));
            Log.d(TAG, "DYNAMIC_AGC Packet Lost:" + ppgApp.getPacketLostCount(ppgApp.STREAM_DYNAMIC_AGC));
            Log.d(TAG, "Pedometer Packet Lost:" + pedApp.getPacketLostCount());
            Log.d(TAG, "Temperature Packet Lost:" + tempApp.getPacketLostCount(tempApp.STREAM_TEMPERATURE4));
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
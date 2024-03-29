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
import android.util.Log;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.analog.study_watch_sdk.StudyWatch;
import com.analog.study_watch_sdk.application.ADPDApplication;
import com.analog.study_watch_sdk.application.EDAApplication;
import com.analog.study_watch_sdk.application.PPGApplication;
import com.analog.study_watch_sdk.application.PedometerApplication;
import com.analog.study_watch_sdk.application.TemperatureApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.interfaces.AGCCallback;
import com.analog.study_watch_sdk.interfaces.PPGCallback;
import com.analog.study_watch_sdk.interfaces.SYNCPPGCallback;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Use-case B
 * Application	Data rate (bytes/sec)
 * Syncppg (ADPD and ADXL @50Hz)	900
 * EDA@30Hz	305
 * PPG@1Hz	  46
 * Temp@1Hz	  20
 * Pedometer@1Hz	  23
 * AGC@1Hz	48
 * Total	1342
 **/
public class UseCaseB extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = UseCaseA.class.getSimpleName();

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
            ADPDApplication adpdApp = watchSdk.getADPDApplication();
            EDAApplication edaApp = watchSdk.getEDAApplication();
            PPGApplication ppgApp = watchSdk.getPPGApplication();
            TemperatureApplication tempApp = watchSdk.getTemperatureApplication();
            PedometerApplication pedApp = watchSdk.getPedometerApplication();

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

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                ppgApp.setLibraryConfiguration(ppgApp.LCFG_ID_ADPD4000);
                //setting EDA ODR to 30HZ
                edaApp.writeLibraryConfiguration(new long[][]{{0x0, 0x1E}});
                edaApp.writeLibraryConfiguration(new long[][]{{0x2, 0x1}});

                // start sensor
                edaApp.startSensor();
                ppgApp.startSensor();
                pedApp.startSensor();
                tempApp.startSensor();

                // Subscribe
                adpdApp.subscribeStream(adpdApp.STREAM_STATIC_AGC);
                edaApp.subscribeStream();
                ppgApp.subscribeStream(ppgApp.STREAM_PPG);
                ppgApp.subscribeStream(ppgApp.STREAM_SYNC_PPG);
                ppgApp.subscribeStream(ppgApp.STREAM_DYNAMIC_AGC);
                pedApp.subscribeStream();
                tempApp.subscribeStream(tempApp.STREAM_TEMPERATURE4);
            });
        });

        button3.setOnClickListener(v -> {
            button.setEnabled(true);
            button3.setEnabled(false);
            // Get applications from SDK
            ADPDApplication adpdApp = watchSdk.getADPDApplication();
            EDAApplication edaApp = watchSdk.getEDAApplication();
            PPGApplication ppgApp = watchSdk.getPPGApplication();
            TemperatureApplication tempApp = watchSdk.getTemperatureApplication();
            PedometerApplication pedApp = watchSdk.getPedometerApplication();

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                // unSubscribe
                adpdApp.unsubscribeStream(adpdApp.STREAM_STATIC_AGC);
                edaApp.unsubscribeStream();
                ppgApp.unsubscribeStream(ppgApp.STREAM_PPG);
                ppgApp.unsubscribeStream(ppgApp.STREAM_SYNC_PPG);
                ppgApp.unsubscribeStream(ppgApp.STREAM_DYNAMIC_AGC);
                pedApp.unsubscribeStream();
                tempApp.unsubscribeStream(tempApp.STREAM_TEMPERATURE4);

                // stop sensor
                edaApp.stopSensor();
                ppgApp.stopSensor();
                pedApp.stopSensor();
                tempApp.stopSensor();

                Log.d(TAG, "STATIC_AGC Packet Lost: " + adpdApp.getPacketLostCount(adpdApp.STREAM_STATIC_AGC));
                Log.d(TAG, "EDA Packet Lost:" + edaApp.getPacketLostCount());
                Log.d(TAG, "PPG Packet Lost:" + ppgApp.getPacketLostCount(ppgApp.STREAM_PPG));
                Log.d(TAG, "SYNC_PPG Packet Lost:" + ppgApp.getPacketLostCount(ppgApp.STREAM_SYNC_PPG));
                Log.d(TAG, "DYNAMIC_AGC Packet Lost:" + ppgApp.getPacketLostCount(ppgApp.STREAM_DYNAMIC_AGC));
                Log.d(TAG, "Pedometer Packet Lost:" + pedApp.getPacketLostCount());
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
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
import com.analog.study_watch_sdk.application.ADPDApplication;
import com.analog.study_watch_sdk.application.PMApplication;
import com.analog.study_watch_sdk.application.PPGApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.enums.adpd.ADPDLed;
import com.analog.study_watch_sdk.interfaces.AGCCallback;
import com.analog.study_watch_sdk.interfaces.HRVCallback;
import com.analog.study_watch_sdk.interfaces.PPGCallback;
import com.analog.study_watch_sdk.interfaces.SYNCPPGCallback;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

/**
 * Quickstart for PPG stream.
 */
public class PPGExample extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = PPGExample.class.getSimpleName();

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

        button.setOnClickListener(v -> {
            // Get applications from SDK
            PPGApplication ppgApp = watchSdk.getPPGApplication();
            ADPDApplication adpdApp = watchSdk.getADPDApplication();
            PMApplication pmAPP = watchSdk.getPMApplication();

            adpdApp.deleteDeviceConfigurationBlock();
            ppgApp.deleteDeviceConfigurationBlock();
            ppgApp.setCallback((PPGCallback) ppgDataPacket -> {
                float hr = (float) (ppgDataPacket.payload.getHR() / 16.0);
                float confidence = (float) (100.0 * (ppgDataPacket.payload.getConfidence() / 1024.0));
                Log.d(TAG, " PPG (Timestamp, HR, Confidence, HR Type) :: " +
                        ppgDataPacket.payload.getTimestamp() + " , " + hr + " , " + confidence +
                        " , " + ppgDataPacket.payload.getHRType());
            }, ppgApp.STREAM_PPG);
            ppgApp.setCallback((SYNCPPGCallback) syncppgDataPacket -> Log.d(TAG, "SYNC_PPG: " + syncppgDataPacket), ppgApp.STREAM_SYNC_PPG);
            ppgApp.setCallback((HRVCallback) hrvDataPacket -> Log.d(TAG, "HRV: " + hrvDataPacket), ppgApp.STREAM_HRV);
            ppgApp.setCallback((AGCCallback) agcDataPacket -> Log.d(TAG, "DYNAMIC_AGC: " + agcDataPacket), ppgApp.STREAM_DYNAMIC_AGC);

            adpdApp.loadConfiguration(adpdApp.DEVICE_GREEN);
            adpdApp.enableAgc(new ADPDLed[]{adpdApp.LED_GREEN});
            // checking for DVT2 board
            if (pmAPP.getChipID(pmAPP.CHIP_ADPD4K).payload.getChipID() == 0xc0)
                adpdApp.calibrateClock(adpdApp.CLOCK_1M_AND_32M);
            else
                adpdApp.calibrateClock(adpdApp.CLOCK_1M);
            ppgApp.setLibraryConfiguration(ppgApp.LCFG_ID_ADPD4000);
            ppgApp.writeDeviceConfigurationBlock(new long[][]{{0x4, 0x1210}});

            ppgApp.startSensor();
            ppgApp.subscribeStream(ppgApp.STREAM_PPG);
            ppgApp.subscribeStream(ppgApp.STREAM_SYNC_PPG);
            ppgApp.subscribeStream(ppgApp.STREAM_HRV);
            ppgApp.subscribeStream(ppgApp.STREAM_DYNAMIC_AGC);

            // sleep
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ppgApp.unsubscribeStream(ppgApp.STREAM_PPG);
            ppgApp.unsubscribeStream(ppgApp.STREAM_SYNC_PPG);
            ppgApp.unsubscribeStream(ppgApp.STREAM_HRV);
            ppgApp.unsubscribeStream(ppgApp.STREAM_DYNAMIC_AGC);
            ppgApp.stopSensor();

        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
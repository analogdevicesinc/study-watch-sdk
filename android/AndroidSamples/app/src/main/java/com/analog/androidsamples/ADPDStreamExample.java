package com.analog.androidsamples;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.analog.study_watch_sdk.StudyWatch;
import com.analog.study_watch_sdk.application.ADPDApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.enums.ADPDLed;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

public class ADPDStreamExample extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = ADPDStreamExample.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.button);
        button.setEnabled(false);

        // Connecting to watch with MAC address.
        StudyWatch.connectBLE("C5:05:CA:F1:67:D5", getApplicationContext(), new StudyWatchCallback() {
            @Override
            public void onSuccess(SDK sdk) {
                Log.d(TAG, "onSuccess: SDK Ready");
                watchSdk = sdk;
                runOnUiThread(() -> {
                    button.setEnabled(true);
                });
            }

            @Override
            public void onFailure(String message, int state) {
                Log.d(TAG, "onError: " + message);
            }
        });

        // Using button click to send ADPD sensor start and stop after 10 sec.
        button.setOnClickListener(v -> {
            // Get ADPD application from SDK
            ADPDApplication adpd = watchSdk.getADPDApplication();
            // callback to receive data packets from ADPD stream
            adpd.setCallback(adpdDataPacket -> {
                Log.d(TAG, "STREAM_ADPD6 :: " + adpdDataPacket);
//                ADPD data parsed from stream
                long[] adpdData = adpdDataPacket.payload.getAdpdData();
//                D/ADPDStreamExample: STREAM_ADPD6 :: Header: {source=<Stream.ADPD6: [0xC2, 0x16]>, destination=<Application.APP_BLE: [0xC8, 0x08]>, length=0x2C, checksum=0x00}, Payload{command=<CommonCommand.STREAM_DATA: [0x28]>, status=<CommonStatus.OK: [0x00]>, sequenceNumber=504, dataType=4, adpdData=[408961, 408958, 409004, 409011, 409051, 409049]}
            }, adpd.STREAM_ADPD6);
            adpd.setCallback(adpdDataPacket -> {
                Log.d(TAG, "STREAM_ADPD7 ::" + adpdDataPacket);
//                D/ADPDStreamExample: STREAM_ADPD7 ::Header: {source=<Stream.ADPD7: [0xC2, 0x17]>, destination=<Application.APP_BLE: [0xC8, 0x08]>, length=0x2C, checksum=0x00}, Payload{command=<CommonCommand.STREAM_DATA: [0x28]>, status=<CommonStatus.OK: [0x00]>, sequenceNumber=504, dataType=4, adpdData=[388266, 388227, 388290, 388298, 388308, 388268]}
            }, adpd.STREAM_ADPD7);
            adpd.setCallback(adpdDataPacket -> {
                Log.d(TAG, "STREAM_ADPD8 :: " + adpdDataPacket);
//                D/ADPDStreamExample: STREAM_ADPD8 :: Header: {source=<Stream.ADPD8: [0xC2, 0x18]>, destination=<Application.APP_BLE: [0xC8, 0x08]>, length=0x2C, checksum=0x00}, Payload{command=<CommonCommand.STREAM_DATA: [0x28]>, status=<CommonStatus.OK: [0x00]>, sequenceNumber=503, dataType=4, adpdData=[387466, 387433, 387405, 387452, 387421, 387422]}
            }, adpd.STREAM_ADPD8);
            adpd.setCallback(adpdDataPacket -> {
                Log.d(TAG, "STREAM_ADPD9 :: " + adpdDataPacket);
//                D/ADPDStreamExample: STREAM_ADPD9 :: Header: {source=<Stream.ADPD9: [0xC2, 0x19]>, destination=<Application.APP_BLE: [0xC8, 0x08]>, length=0x2C, checksum=0x00}, Payload{command=<CommonCommand.STREAM_DATA: [0x28]>, status=<CommonStatus.OK: [0x00]>, sequenceNumber=504, dataType=4, adpdData=[252590, 252563, 252594, 252608, 252561, 252601]}
            }, adpd.STREAM_ADPD9);

            adpd.loadConfiguration(adpd.DEVICE_G_R_IR_B);
            adpd.calibrateClock(adpd.CLOCK_1M_AND_32M);
            adpd.enableAgc(new ADPDLed[]{adpd.LED_GREEN, adpd.LED_BLUE, adpd.LED_IR, adpd.LED_RED});
            // setting ADPD ODR to 100Hz
            adpd.writeRegister(new long[][]{{0xD, 0x2710}});

            adpd.startSensor();
            adpd.subscribeStream(adpd.STREAM_ADPD6);
            adpd.subscribeStream(adpd.STREAM_ADPD7);
            adpd.subscribeStream(adpd.STREAM_ADPD8);
            adpd.subscribeStream(adpd.STREAM_ADPD9);
            // Waiting for 10 Sec
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Stopping and Un-subscribing
            adpd.unsubscribeStream(adpd.STREAM_ADPD6);
            adpd.unsubscribeStream(adpd.STREAM_ADPD7);
            adpd.unsubscribeStream(adpd.STREAM_ADPD8);
            adpd.unsubscribeStream(adpd.STREAM_ADPD9);
            adpd.stopSensor();

        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
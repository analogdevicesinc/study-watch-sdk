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
import com.analog.study_watch_sdk.application.ADXLApplication;
import com.analog.study_watch_sdk.application.EDAApplication;
import com.analog.study_watch_sdk.application.PMApplication;
import com.analog.study_watch_sdk.application.TemperatureApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.packets.stream.EDADataPacket;
import com.analog.study_watch_sdk.interfaces.SlotApp;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

public class MultiStreamExample2 extends AppCompatActivity {
    // Impedance + every wavelength of the optical sensor (F,G,H,I) + Acc + Temp.
    SDK watchSdk;
    private static final String TAG = MultiStreamExample2.class.getSimpleName();

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
            EDAApplication edaApp = watchSdk.getEDAApplication();
            ADPDApplication adpdApp = watchSdk.getADPDApplication();
            ADXLApplication adxlApp = watchSdk.getADXLApplication();
            TemperatureApplication tempApp = watchSdk.getTemperatureApplication();
            PMApplication pmAPP = watchSdk.getPMApplication();

            // callbacks
            edaApp.setCallback(edaDataPacket -> {
                for (EDADataPacket.Payload.StreamData streamData : edaDataPacket.payload.getStreamData()) {
                    long edaReal = streamData.getRealData();
                    long edaImaginary = streamData.getImaginaryData();
                    if (edaReal == 0)
                        edaReal = 1;
                    double impedanceImg = edaImaginary * 1000.0;
                    double impedanceReal = edaReal * 1000.0;
                    double realAndImg = (impedanceReal * impedanceReal + impedanceImg * impedanceImg);
                    double impedanceMagnitude = Math.sqrt(realAndImg);
                    double impedancePhase = Math.atan2(impedanceImg, impedanceReal);
                    Log.d(TAG, " BCM (Timestamp, sequence number, impedanceMagnitude, impedancePhase) :: " +
                            streamData.getTimestamp() + " , " + edaDataPacket.payload.getSequenceNumber() + " , " +
                            impedanceMagnitude + " , " + impedancePhase);
                }
            });
            adpdApp.setCallback(adpdDataPacket -> Log.d(TAG, "ADPD6: " + adpdDataPacket), adpdApp.STREAM_ADPD6);
            adpdApp.setCallback(adpdDataPacket -> Log.d(TAG, "ADPD7: " + adpdDataPacket), adpdApp.STREAM_ADPD7);
            adpdApp.setCallback(adpdDataPacket -> Log.d(TAG, "ADPD8: " + adpdDataPacket), adpdApp.STREAM_ADPD8);
            adpdApp.setCallback(adpdDataPacket -> Log.d(TAG, "ADPD9: " + adpdDataPacket), adpdApp.STREAM_ADPD9);
            adxlApp.setCallback(adxlDataPacket -> Log.d(TAG, "ADXL: " + adxlDataPacket));
            tempApp.setCallback(temperatureDataPacket -> Log.d(TAG, "TEMP: " + temperatureDataPacket));

            //setting EDA ODR to 30HZ
            edaApp.writeLibraryConfiguration(new long[][]{{0x0, 0x1E}});

            // adpd config
            adpdApp.createDeviceConfiguration(new SlotApp[][]{
                    {adpdApp.SLOT_D, adpdApp.APP_TEMPERATURE_THERMISTOR},
                    {adpdApp.SLOT_E, adpdApp.APP_TEMPERATURE_RESISTOR},
                    {adpdApp.SLOT_F, adpdApp.APP_ADPD_GREEN},
                    {adpdApp.SLOT_G, adpdApp.APP_ADPD_RED},
                    {adpdApp.SLOT_H, adpdApp.APP_ADPD_INFRARED},
                    {adpdApp.SLOT_I, adpdApp.APP_ADPD_BLUE},
            });
            adpdApp.loadConfiguration(adpdApp.DEVICE_GREEN);
            // checking for DVT2 board
            if (pmAPP.getChipID(pmAPP.CHIP_ADPD4K).payload.getChipID() == 0xc0)
                adpdApp.calibrateClock(adpdApp.CLOCK_1M_AND_32M);
            else
                adpdApp.calibrateClock(adpdApp.CLOCK_1M);

            adpdApp.writeRegister(new long[][]{{0xD, 0x2710}});

            // senors
            edaApp.startSensor();
            adpdApp.startSensor();
            adxlApp.startSensor();
            tempApp.startSensor();

            //setting ADXL ODR to 50Hz -- ADXL Loads DCB when Start Sensor is done, if no DCB it will load Default Config
            adxlApp.writeRegister(new long[][]{{0x2c, 0x9A}});

            // subs
            edaApp.subscribeStream();
            adpdApp.subscribeStream(adpdApp.STREAM_ADPD6);
            adpdApp.subscribeStream(adpdApp.STREAM_ADPD7);
            adpdApp.subscribeStream(adpdApp.STREAM_ADPD8);
            adpdApp.subscribeStream(adpdApp.STREAM_ADPD9);
            adxlApp.subscribeStream();
            tempApp.subscribeStream();

            // wait
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            edaApp.unsubscribeStream();
            adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD6);
            adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD7);
            adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD8);
            adpdApp.unsubscribeStream(adpdApp.STREAM_ADPD9);
            adxlApp.unsubscribeStream();
            tempApp.unsubscribeStream();

            edaApp.stopSensor();
            adpdApp.stopSensor();
            adxlApp.stopSensor();
            tempApp.stopSensor();

        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
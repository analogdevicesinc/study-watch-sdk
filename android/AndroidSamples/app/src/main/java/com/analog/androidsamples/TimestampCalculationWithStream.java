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
import com.analog.study_watch_sdk.application.ADXLApplication;
import com.analog.study_watch_sdk.application.PMApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.packets.DateTimePacket;
import com.analog.study_watch_sdk.core.packets.stream.ADXLDataPacket;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import static java.lang.Math.abs;

public class TimestampCalculationWithStream extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = TimestampCalculationWithStream.class.getSimpleName();
    double refTime;
    double lastTimestamp = 0;

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
            PMApplication pmApp = watchSdk.getPMApplication();
            ADXLApplication adxlApp = watchSdk.getADXLApplication();

            adxlApp.setCallback(adxlDataPacket -> {
                for (ADXLDataPacket.Payload.StreamData streamData : adxlDataPacket.payload.streamData) {
                    // obtaining the change in timestamp
                    double timestamp = streamData.getTimestamp();
                    double change = abs(timestamp - lastTimestamp);
                    change = change / 32768.0;
                    if (lastTimestamp != 0) {
                        refTime += (change * 1000); // adding the change with reftime
                    }
                    lastTimestamp = timestamp;
                    Timestamp obj = new Timestamp((long) refTime);
                    Log.d(TAG, "Stream Data (Timestamp, X, Y, Z) :: " + obj.toString() + ", " +
                            streamData.getX() + ", " + streamData.getY() + ", " + streamData.getZ());
                }
            });

            // converting datetime to timestamp
            pmApp.setDatetime(Calendar.getInstance()); // setting datetime so that system time and watch time are in sync
            DateTimePacket packet = pmApp.getDatetime();
            Log.d(TAG, "datetime :: " + packet);
            Calendar date = Calendar.getInstance();
            date.set(Calendar.HOUR_OF_DAY, packet.payload.getHour());
            date.set(Calendar.MINUTE, packet.payload.getMinute());
            date.set(Calendar.SECOND, packet.payload.getSecond());
            date.set(Calendar.YEAR, packet.payload.getYear());
            date.set(Calendar.MONTH, packet.payload.getMonth() - 1); // from 1 index to 0 index base.
            date.set(Calendar.DAY_OF_MONTH, packet.payload.getDay());
            TimeZone tz = TimeZone.getDefault();
            tz.setRawOffset((int) (packet.payload.getTzSec() * 1000));
            date.setTimeZone(tz);
            // reference time for the watch, to be obtained before starting the stream
            refTime = date.getTimeInMillis();
            Log.d(TAG, "TIME IN MS :: " + (long) refTime);

            // start sensor
            adxlApp.startSensor();
            // 0x9B - 100Hz, 0x9A - 50Hz
            adxlApp.writeRegister(new long[][]{{0x2C, 0x9B}});
            adxlApp.subscribeStream();
            // sleep
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // stop sensor
            adxlApp.unsubscribeStream();
            adxlApp.stopSensor();

        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
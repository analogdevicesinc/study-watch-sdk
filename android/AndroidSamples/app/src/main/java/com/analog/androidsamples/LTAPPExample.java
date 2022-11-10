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
import com.analog.study_watch_sdk.application.LTApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.packets.low_touch.LTDCBCommandPacket;
import com.analog.study_watch_sdk.core.packets.low_touch.LTDCBPacket;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * LT APP examples.
 */
public class LTAPPExample extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = LTAPPExample.class.getSimpleName();

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);
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
//            button3.setEnabled(true);
            // Get applications from SDK
            LTApplication ltAPP = watchSdk.getLTApplication();

            File genFile = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/gen_blk_dcb.lcfg");
            File lcfgFile = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/lt_app_dcb.lcfg");

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                LTDCBCommandPacket packet1 = ltAPP.deleteDeviceConfigurationBlock(ltAPP.GENERAL_BLOCK);
                Log.d(TAG, "onCreate: " + packet1);
                try {
                    LTDCBCommandPacket[] packet = ltAPP.writeDeviceConfigurationBlockFromFile(genFile, ltAPP.GENERAL_BLOCK);
                    Log.d(TAG, "onCreate: " + Arrays.toString(packet));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                LTDCBPacket[] packet2 = ltAPP.readDeviceConfigurationBlock(ltAPP.GENERAL_BLOCK);
                Log.d(TAG, "onCreate: " + Arrays.deepToString(packet2));
                Log.d(TAG, "onCreate: " + Arrays.deepToString(packet2[0].payload.getData()));

                LTDCBCommandPacket packet3 = ltAPP.deleteDeviceConfigurationBlock(ltAPP.LT_APP_LCFG_BLOCK);
                Log.d(TAG, "onCreate: " + packet3);
                try {
                    LTDCBCommandPacket[] packet = ltAPP.writeDeviceConfigurationBlockFromFile(lcfgFile, ltAPP.LT_APP_LCFG_BLOCK);
                    Log.d(TAG, "onCreate: " + Arrays.toString(packet));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                LTDCBPacket[] packet4 = ltAPP.readDeviceConfigurationBlock(ltAPP.LT_APP_LCFG_BLOCK);
                Log.d(TAG, "onCreate: " + Arrays.deepToString(packet4));
                Log.d(TAG, "onCreate: " + Arrays.deepToString(packet4[0].payload.getData()));

                Log.d(TAG, "onCreate: " + ltAPP.writeLibraryConfiguration(new int[][]{{0x0, 0x1}}));
                Log.d(TAG, "onCreate: " + ltAPP.readLibraryConfiguration(new int[]{0x0}));
                Log.d(TAG, "onCreate: " + ltAPP.readCH2Cap());
            });
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
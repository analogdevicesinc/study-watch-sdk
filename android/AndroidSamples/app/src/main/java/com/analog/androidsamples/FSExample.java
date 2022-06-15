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
import com.analog.study_watch_sdk.application.FSApplication;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.packets.CommandPacket;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

import java.io.File;
import java.util.Arrays;

/**
 * FS example to download file and how to run general commands.
 */
public class FSExample extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = FSExample.class.getSimpleName();

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                Use the code below to ask for ALL files permission.
//                Intent intent = new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
//                startActivity(intent);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);
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
            FSApplication fsAPP = watchSdk.getFSApplication();

            // file download with callback
            File downloadFile = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/B1320595.LOG");
            try {
                fsAPP.downloadFile("B1320595.LOG", downloadFile, (event, totalSize, currentSize) -> {
                    // Don't hold the process here, keep this callback lightweight
                    // Don't perform any large computation here.
                    // Just store values in some variable or queue and release the callback
                    // EVENTS :
                    // fsAPP.DOWNLOAD_EVENT
                    // fsAPP.CRC_CHECK_EVENT
                    // fsAPP.SEQUENCE_CHECK_EVENT
                    // fsAPP.JOIN_FILE_EVENT
                    Log.d(TAG, "callback: " + event + ", " + totalSize + ", " + currentSize);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            // file download without callback
            File downloadFile1 = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/B1320595.LOG");
            try {
                fsAPP.downloadFile("B1320595.LOG ", downloadFile1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // writing config File.
            File file = new File(Environment.getExternalStorageDirectory(), "Test/gen_blk_dcb.lcfg");
            try {
                fsAPP.deleteConfigFile();
                fsAPP.enableConfigLog();
                CommandPacket[] packets = fsAPP.writeConfigFile(file);
                Log.d(TAG, "onCreate: " + Arrays.toString(packets));
                fsAPP.disableConfigLog();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //general commands
            Log.d(TAG, "onCreate: " + fsAPP.stopLogging());
            Log.d(TAG, "onCreate: " + fsAPP.enableConfigLog());
            Log.d(TAG, "onCreate: " + fsAPP.disableConfigLog());

        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
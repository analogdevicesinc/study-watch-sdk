package com.analog.androidsamples;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.analog.study_watch_sdk.core.SDK;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Example show how convert .LOG file CSVs.
 */
public class LogConvertExample extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = LogConvertExample.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.button);
        final Button button3 = findViewById(R.id.button3);
        button.setEnabled(true);
        button3.setEnabled(false);
        button.setOnClickListener(v -> {
            File downloadFile = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/C062793A.LOG");
            File outputFolder = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/C062793A");

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                // Get applications from SDK
                try {
                    SDK.convertLogToCsv(downloadFile, outputFolder, (event, totalSize, currentSize) -> {
                        // Don't hold the process here, keep this callback lightweight
                        // Don't perform any large computation here.
                        // Just store values in some variable or queue and release the callback
                        Log.d(TAG, "onCreate: " + event + " " + totalSize + " " + currentSize);
                    });
                } catch (Exception e) {
                    Log.e(TAG, "onCreate: ", e);
                }
                Log.d(TAG, "onCreate: DONE");
            });
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
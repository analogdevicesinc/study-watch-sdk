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
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.analog.study_watch_sdk.StudyWatch;
import com.analog.study_watch_sdk.application.AD7156Application;
import com.analog.study_watch_sdk.application.ADP5360Application;
import com.analog.study_watch_sdk.application.ADPDApplication;
import com.analog.study_watch_sdk.application.ADXLApplication;
import com.analog.study_watch_sdk.application.BIAApplication;
import com.analog.study_watch_sdk.application.ECGApplication;
import com.analog.study_watch_sdk.application.EDAApplication;
import com.analog.study_watch_sdk.application.LTApplication;
import com.analog.study_watch_sdk.application.PMApplication;
import com.analog.study_watch_sdk.application.PPGApplication;
import com.analog.study_watch_sdk.application.TemperatureApplication;
import com.analog.study_watch_sdk.application.User0Application;
import com.analog.study_watch_sdk.core.SDK;
import com.analog.study_watch_sdk.core.packets.bia.BIADCBCommandPacket;
import com.analog.study_watch_sdk.core.packets.bia.BIADCBPacket;
import com.analog.study_watch_sdk.interfaces.StudyWatchCallback;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Quickstart for ADXL stream.
 */
public class DCBExample extends AppCompatActivity {

    SDK watchSdk;
    private static final String TAG = DCBExample.class.getSimpleName();

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
        StudyWatch.connectBLE("CE:7B:4B:3D:A6:F9", getApplicationContext(), new StudyWatchCallback() {
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
            AD7156Application ad7156App = watchSdk.getAD7156Application();
            ADPDApplication adpdApp = watchSdk.getADPDApplication();
            ADXLApplication adxlApp = watchSdk.getADXLApplication();
            BIAApplication biaApp = watchSdk.getBIAApplication();
            ECGApplication ecgApp = watchSdk.getECGApplication();
            EDAApplication edaAPP = watchSdk.getEDAApplication();
            LTApplication ltApp = watchSdk.getLTApplication();
            PMApplication pmApp = watchSdk.getPMApplication();
            ADP5360Application adp5360Application = watchSdk.getADP5360Application();
            PPGApplication ppgApp = watchSdk.getPPGApplication();
            TemperatureApplication tempApp = watchSdk.getTemperatureApplication();
            User0Application user0App = watchSdk.getUser0Application();
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                //AD7156
                File fileAD7156 = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/ad7156_dcb.dcfg");

                try {
                    ad7156App.writeDeviceConfigurationBlockFromFile(fileAD7156);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ad7156App.readDeviceConfigurationBlock();
                ad7156App.deleteDeviceConfigurationBlock();

                //adpd
                File fileADPD = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/adpd4000_dcb.dcfg");

                try {
                    adpdApp.writeDeviceConfigurationBlockFromFile(fileADPD);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                adpdApp.readDeviceConfigurationBlock();
                adpdApp.deleteDeviceConfigurationBlock();

                //adxl
                File fileADXL = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/adxl_dcb.dcfg");

                try {
                    adxlApp.writeDeviceConfigurationBlockFromFile(fileADXL);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                adxlApp.readDeviceConfigurationBlock();
                adxlApp.deleteDeviceConfigurationBlock();

                //bia LCFG
                File fileBIALcfg = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/bia_dcb.lcfg");

                try {
                    BIADCBCommandPacket pkt = biaApp.writeDeviceConfigurationBlockFromFile(fileBIALcfg, biaApp.BIA_LCFG_BLOCK);
                    Log.d(TAG, "onCreate: " + pkt);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BIADCBPacket pk1 = biaApp.readDeviceConfigurationBlock(biaApp.BIA_LCFG_BLOCK);
                Log.d(TAG, "onCreate111: " + pk1);
                biaApp.deleteDeviceConfigurationBlock(biaApp.BIA_LCFG_BLOCK);

                //bia DCFG
                File fileBIADcfg = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/bia_dcb.dcfg");

                try {
                    biaApp.writeDeviceConfigurationBlockFromFile(fileBIADcfg, biaApp.BIA_DCFG_BLOCK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                biaApp.readDeviceConfigurationBlock(biaApp.BIA_DCFG_BLOCK);
                biaApp.deleteDeviceConfigurationBlock(biaApp.BIA_DCFG_BLOCK);

                //ecg
                File fileECG = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/ecg_dcb.lcfg");

                try {
                    ecgApp.writeDeviceConfigurationBlockFromFile(fileECG);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ecgApp.readDeviceConfigurationBlock();
                ecgApp.deleteDeviceConfigurationBlock();

                //eda LCFG
                File fileEDALcfg = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/eda_dcb.lcfg");

                try {
                    edaAPP.writeDeviceConfigurationBlockFromFile(fileEDALcfg, edaAPP.EDA_LCFG_BLOCK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                edaAPP.readDeviceConfigurationBlock(edaAPP.EDA_LCFG_BLOCK);
                edaAPP.deleteDeviceConfigurationBlock(edaAPP.EDA_LCFG_BLOCK);

                //eda DCFG
                File fileEDADcfg = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/eda_dcb.dcfg");

                try {
                    edaAPP.writeDeviceConfigurationBlockFromFile(fileEDADcfg, edaAPP.EDA_DCFG_BLOCK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                edaAPP.readDeviceConfigurationBlock(edaAPP.EDA_DCFG_BLOCK);
                edaAPP.deleteDeviceConfigurationBlock(edaAPP.EDA_DCFG_BLOCK);

                //lt
                File genFile = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/gen_blk_dcb.lcfg");
                File lcfgFile = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/lt_app_dcb.lcfg");

                try {
                    ltApp.writeDeviceConfigurationBlockFromFile(genFile, ltApp.GENERAL_BLOCK);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ltApp.readDeviceConfigurationBlock(ltApp.GENERAL_BLOCK);
                ltApp.deleteDeviceConfigurationBlock(ltApp.GENERAL_BLOCK);

                try {
                    ltApp.writeDeviceConfigurationBlockFromFile(lcfgFile, ltApp.LT_APP_LCFG_BLOCK);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ltApp.readDeviceConfigurationBlock(ltApp.LT_APP_LCFG_BLOCK);
                ltApp.deleteDeviceConfigurationBlock(ltApp.LT_APP_LCFG_BLOCK);


                //pm
                File filePM = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/lt_app_dcb.lcfg");

                try {
                    adp5360Application.writeDeviceConfigurationBlockFromFile(filePM);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                adp5360Application.readDeviceConfigurationBlock();
                adp5360Application.deleteDeviceConfigurationBlock();

                //ppg
                File filePPG = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/ppg_dcb.lcfg");

                try {
                    ppgApp.writeDeviceConfigurationBlockFromFile(filePPG);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ppgApp.readDeviceConfigurationBlock();
                ppgApp.deleteDeviceConfigurationBlock();

                //temperature
                File fileTemp = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/temperature_lcfg_dcb.lcfg");

                try {
                    tempApp.writeDeviceConfigurationBlockFromFile(fileTemp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tempApp.readDeviceConfigurationBlock();
                tempApp.deleteDeviceConfigurationBlock();

                //user0
                File fileUser0 = new File(Environment.getExternalStorageDirectory(), "dcb_cfg/user0_blk_dcb.lcfg");

                try {
                    user0App.writeDeviceConfigurationBlockFromFile(fileUser0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                user0App.readDeviceConfigurationBlock();
                user0App.deleteDeviceConfigurationBlock();

            });
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        watchSdk.disconnect();
    }
}
# Study Watch Android SDK

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.analogdevicesinc/study_watch_sdk/badge.svg)](https://search.maven.org/artifact/com.github.analogdevicesinc/study_watch_sdk/4.1.0/aar)

### `minSdkVersion 19`

Add sdk in app `build.gradle`

```java
implementation 'com.github.analogdevicesinc:study_watch_sdk:4.1.0'
```

Permissions required to enable BLE, add the permission below in `AndroidManifest.xml`

```java
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
```

**ACCESS_BACKGROUND_LOCATION** is required for android 11 and above.

Location service needs to be enabled for android 11 and
above [Example](https://github.com/analogdevicesinc/study-watch-sdk/blob/main/android/AndroidSamples/app/src/main/java/com/analog/androidsamples/ADXLExample.java)
.

## Getting started with sdk

Step 1 : Connecting to BLE and obtaining the study watch sdk instance.

```java
// connect to study watch with its mac address.
StudyWatch.connectBLE("C5:05:CA:F1:67:D5", getApplicationContext(), new StudyWatchCallback() {
    @Override
    public void onSuccess(SDK sdk) {
        Log.d(TAG, "onSuccess: SDK Ready");
        // store this sdk reference to be used for creating applications
        watchSdk = sdk; 
    }

    @Override
    public void onFailure(String message, int state) {
        Log.e(TAG, "onError: " + message);
    }
});
```

Step 2 : Once you are connected to the study and obtained the sdk instance you can now start streaming data.

Here we will show how to start EDA sensor, steam data and close it after 30 sec.

```java
button.setOnClickListener(v -> {
    // Get EDA application from SDK
    EDAApplication eda = watchSdk.getEDAApplication();

    // callback to receive data packets from EDA stream
    eda.setCallback(edaDataPacket -> {
        Log.d(TAG, "DATA :: " + edaDataPacket);
        // Sample Output(Class structure) ::  Header: {source=<Stream.EDA: [0xc4, 0x2]>, destination=<Application.APP_BLE: [0xc8, 0x8]>, length=0x3d, checksum=0x0}, Payload{command=<CommonCommand.STREAM_DATA: [0x28]>, status=<CommonStatus.OK: [0x0]>, sequenceNumber=4, dataType=0, streamData=[StreamData{timestamp=1758129180, realData=58137, imaginaryData=-172621824}, StreamData{timestamp=1758136945, realData=62922, imaginaryData=1090715648}, StreamData{timestamp=1758144708, realData=16663, imaginaryData=1064697856}, StreamData{timestamp=1758152472, realData=16265, imaginaryData=-1062404096}, StreamData{timestamp=1758160237, realData=49345, imaginaryData=-1062404096}, StreamData{timestamp=1758168001, realData=49345, imaginaryData=-1526726656}]}
    });

    // Setting up library config before starting EDA sensor
    eda.writeLibraryConfiguration(new long[][]{{0x0, 0x4}});

    // Starting and Subscribing to callback provided above
    eda.startSensor();
    eda.subscribeStream();

    // Waiting for 30 Sec
    try {
        Thread.sleep(30000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    // Stopping and Un-subscribing
    eda.stopSensor();
    eda.unsubscribeStream();
});
```

### Error Messages:

###### 1 - Bluetooth is not enabled.

You can resolve above error by enabling bluetooth.

```java
BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
if (!mBluetoothAdapter.isEnabled()) {
    mBluetoothAdapter.enable();
}
```

###### 2 - Location permission not granted.

You can resolve above error by granting location
permission. [Read here : https://developer.android.com/guide/topics/connectivity/bluetooth]

```java
if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { 
    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
}
```

###### 3 - Location permission in Android 11 and above. [ADXLExample](https://github.com/analogdevicesinc/study-watch-sdk/blob/main/android/AndroidSamples/app/src/main/java/com/analog/androidsamples/ADXLExample.java)

**Examples :**
https://github.com/analogdevicesinc/study-watch-sdk/blob/main/android/AndroidSamples/app/src/main/java/com/analog/androidsamples/

**Documentation :**
https://analogdevicesinc.github.io/study-watch-sdk/android

**License :**
https://github.com/analogdevicesinc/study-watch-sdk/blob/main/LICENSE

**Changelog**
https://github.com/analogdevicesinc/study-watch-sdk/blob/main/android/CHANGELOG.md

## Firmware Setup

To set up firmware for the study watch follow the
pdf (https://github.com/analogdevicesinc/study-watch-sdk/blob/main/firmware/Study_Watch_Firmware_Upgrade.pdf)







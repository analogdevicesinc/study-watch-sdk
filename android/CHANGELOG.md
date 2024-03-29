# Changelog

All notable changes to Android SDK will be documented in this file.

## [v5.21.0] - Jul 13, 2023

- Support added for 5.21.0 firmware.

## [v5.20.0] - Jun 20, 2023

- Support added for firmware 5.20.0


## [v5.19.1] - May 18, 2023

- Timestamp issue resolved for csv and filesystem logging.

## [v5.19.0] - May 10, 2023

- BatteryInfoPacket structure modified.
- systemHardwareReset api enabled.
- CSV logging issue resolved for ADP5360 app.
- Support added for firmware 5.19.0

## [v5.17.0] - Mar 14, 2023

- Fixed issue related to BLE permission for Android 11 and below.
- systemHardwareReset API temporary disabled.
- Support added for 5.17.0 firmware.
- SDK version changed to match the firmware version.
- New alarms Enum added for ADPD and PM.
- Alarms removed for LT and USER0

## [v4.3.0] - Nov 10, 2022

#### Updates:
- New ADP5360 Application (Added). 

#### AD7156
- AD7156DataPacket stream packet updated.

#### PM
- fdsErase (Added).

#### BIA
- BCM stream Added.

#### ADP5360
- setBatteryThreshold (New).
- getBatteryThreshold (New).
- getBatteryInfo (New).
- readRegister (New).
- writeRegister (New).
- deleteDeviceConfigurationBlock (New).
- readDeviceConfigurationBlock (New).
- writeDeviceConfigurationBlock (New).
- writeDeviceConfigurationBlockFromFile (New).
- subscribeStream (New).
- unsubscribeStream (New).
- enableCSVLogging (New).
- disableCSVLogging (New).

#### PM 
- setBatteryThreshold (Moved to ADP5360).
- getBatteryThreshold (Moved to ADP5360).
- getBatteryInfo (Moved to ADP5360).
- readRegister (Moved to ADP5360).
- writeRegister (Moved to ADP5360).
- deleteDeviceConfigurationBlock (Moved to ADP5360).
- readDeviceConfigurationBlock (Moved to ADP5360).
- writeDeviceConfigurationBlock (Moved to ADP5360).
- writeDeviceConfigurationBlockFromFile (Moved to ADP5360).
- subscribeStream (Moved to ADP5360).
- unsubscribeStream (Moved to ADP5360).
- enableCSVLogging (Moved to ADP5360).
- disableCSVLogging (Moved to ADP5360).

## [v4.2.1] - Jun 15, 2022

### New features

#### Updates:

- Alarms callback API (Added).
- freq_index column added in BIA csv logging.

#### PM

- loadConfiguration (Added)
- getTopTouchStatus (Added)
- disableTopTouch (Added)
- enableTopTouch (Added)
- stopSyncTimer (Added)
- startSyncTimer (Added)
- disableSyncTimer (Added)
- enableSyncTimer (Added)
- writeUICRCustomerRegisters (Added)
- readUICRCustomerRegisters (Added)
- deviceConfigurationBlockStatus (Updated)

#### ADPD

- enableSaturationCheck (Added)
- disableSaturationCheck (Added)

#### BIA

- writeLibraryConfiguration (Updated)
- writeDeviceConfigurationBlockFromFile (updated)
- readDeviceConfigurationBlock (Updated)
- deleteDeviceConfigurationBlock (Updated)
- getDeviceConfiguration (Added)
- setDeviceConfiguration (Added)
- loadDeviceConfiguration (Added)

#### EDA

- writeDeviceConfigurationBlockFromFile (updated)
- readDeviceConfigurationBlock (Updated)
- deleteDeviceConfigurationBlock (Updated)
- getRamRTIACalibrationTable (Added)
- getFdsRTIACalibrationTable (Added)
- deleteRTIACalibrationTable (Added)
- getDeviceConfiguration (Added)
- setDeviceConfiguration (Added)
- loadDeviceConfiguration (Added)


#### Packet Structure Changes

- DeviceConfigurationBlock for EDA and BIA now accepts **DCBConfigBlockIndex**.
- EDA writeLibraryConfiguration now accepts **long** instead of **int**.


## [v4.1.0] - Dec 7, 2021

### New features

- downloadFile API
  added. ([Example](https://github.com/analogdevicesinc/study-watch-sdk/blob/main/android/AndroidSamples/app/src/main/java/com/analog/androidsamples/FSExample.java))
- convertLogToCsv API
  added. ([Example](https://github.com/analogdevicesinc/study-watch-sdk/blob/main/android/AndroidSamples/app/src/main/java/com/analog/androidsamples/LogConvertExample.java))
- BCM APP renamed to
  BIA. ([Example](https://github.com/analogdevicesinc/study-watch-sdk/blob/main/android/AndroidSamples/app/src/main/java/com/analog/androidsamples/BIAExample.java))
- Static (General ADPD AGC data) and Dynamic AGC (HR AGC data)
  added. ([Example1](https://github.com/analogdevicesinc/study-watch-sdk/blob/main/android/AndroidSamples/app/src/main/java/com/analog/androidsamples/UseCaseA.java)) ([Example2](https://github.com/analogdevicesinc/study-watch-sdk/blob/main/android/AndroidSamples/app/src/main/java/com/analog/androidsamples/PPGExample.java))

### Temperature APP changes

- Multi slot streaming
  added. ([Example](https://github.com/analogdevicesinc/study-watch-sdk/blob/main/android/AndroidSamples/app/src/main/java/com/analog/androidsamples/MultiSlotTemperatureExample.java))
- STREAM_TEMPERATURE enum replaced with
  STREAM_TEMPERATURE4 ([Example](https://github.com/analogdevicesinc/study-watch-sdk/blob/main/android/AndroidSamples/app/src/main/java/com/analog/androidsamples/TemperatureExample.java))

### General Changes

- Callbacks updated in ADPD and
  PPG ([ADPDExample](https://github.com/analogdevicesinc/study-watch-sdk/blob/main/android/AndroidSamples/app/src/main/java/com/analog/androidsamples/ADPDExample.java)) ([PPGExample](https://github.com/analogdevicesinc/study-watch-sdk/blob/main/android/AndroidSamples/app/src/main/java/com/analog/androidsamples/PPGExample.java))
- getPacketLostCount API
  added. ([Example](https://github.com/analogdevicesinc/study-watch-sdk/blob/main/android/AndroidSamples/app/src/main/java/com/analog/androidsamples/ADXLCSVExample.java))
- Streaming added for AD7156
  sensor ([Example](https://github.com/analogdevicesinc/study-watch-sdk/blob/main/android/AndroidSamples/app/src/main/java/com/analog/androidsamples/AD7156Example.java))
- Inbuilt CSV logging added for all
  applications ([Example](https://github.com/analogdevicesinc/study-watch-sdk/blob/main/android/AndroidSamples/app/src/main/java/com/analog/androidsamples/ADXLCSVExample.java))
- HRV, AGC stream
  added. ([Example](https://github.com/analogdevicesinc/study-watch-sdk/blob/main/android/AndroidSamples/app/src/main/java/com/analog/androidsamples/PPGExample.java))

### Packets updated:

- DCBPacket - split into application respective packets ADPDDCBPacket, ADXLDCBPacket, BIADCBPacket, PPGDCBPacket etc.
- DCFGPacket - split into application respective packets ADXLDCFGPacket, ADPDDCFGPacket etc. 
- RegisterPacket - split into - application respective packets ADPDRegisterReadPacket, ADPDRegisterWritePacket, ADXLRegisterReadPacket, ADXLRegisterWritePacket etc. 
- LibraryConfigReadWritePacket - split into application respective packets BIALibraryConfigPacket, ADPDLibraryConfigPacket, PPGLibraryConfigPacket etc.
- DFTPacket - split into application respective packets EDADFTPacket, BIADFTPacket etc.

### Datatype change:

- Data Type for all packets are made similar to firmware C data types to reduce SDK memory footprint.

### known Issues:

- ADPD Pause and Resume returns No Response.

## [v4.0.8] - Jul 2, 2021

### New application LT (Low Touch) added.

### All Streams

Timestamp in all data streams is changed from ticks to epoch.

### ADPD

- readDeviceConfigurationBlock return type changed to DCBPacket[] from DCBPacket.
- writeDeviceConfigurationBlock return type changed to DCBPacket[] from DCBPacket.
- getSensorStatus now takes ADPD stream as parameter.
- In ADPDDataPacket payload adpdData variable is removed and two new variables are added singnalData and darkdata.
- getAgcStatus method removed.

### Temperature

- In TemperatureDataPacket payload temperature1 and temperature2 are renamed to skinTemperature and impedance
  respectively.

### FS

- new method writeConfigFile






# Changelog

All notable changes to Android SDK will be documented in this file.
## [v4.2.1] - Jun 15, 2022

### New features


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






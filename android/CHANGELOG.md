# Changelog

All notable changes to Android SDK will be documented in this file.

## [v4.0.8] - Jul 2, 2021

### New application LT (Low Touch) added.

### All Streams

Timestamp in all data streams is changed from ticks to epoch.

### ADPD

- readDeviceConfigurationBlock return type changed to DCBPacket[] from DCBPacket.
- writeDeviceConfigurationBlock return type changed to DCBPacket[] from DCBPacket.
- getSensorStatus now takes ADPD stream as parameter.
- In ADPDDataPacket payload adpdData variable is removed and two new variables  are added singnalData and darkdata. 
- getAgcStatus method removed.

### Temperature

- In TemperatureDataPacket payload temperature1 and temperature2 are renamed to skinTemperature and impedance respectively.

### FS

- new method writeConfigFile






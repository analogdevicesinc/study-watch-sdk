# Changelog

All notable changes to Android SDK will be documented in this file.

## [v4.1.0] - Dec 7, 2021
# Temperature Application
# STREAM_TEMPERATURE enum replaced with STREAM_TEMPERATURE4
# multi slot temp (Now temperature application has 12 slots for temperature. Example)
callbacks updated
file_download
log_convert
get_packet lost count
ad7156 sensor, sub, plot, csv_log, fs_sub
inbuilt CSV logging
HRV, AGC and ADP stream added.

Packets updated:
DCBPacket - split into application respective packets ADPDDCBPacket, ADXLDCBPacket, BIADCBPacket, PPGDCBPacket etc.
DCFGPacket - split into application respective packets ADXLDCFGPacket, ADPDDCFGPacket etc.
RegisterPacket - split into application respective packets ADPDRegisterReadPacket, ADPDRegisterWritePacket, ADXLRegisterReadPacket, ADXLRegisterWritePacket etc.
LibraryConfigReadWritePacket - split into application respective packets BIALibraryConfigPacket, ADPDLibraryConfigPacket, PPGLibraryConfigPacket etc.
DFTPacket - split into application respective packets EDADFTPacket, ADPDLibraryConfigPacket, PPGLibraryConfigPacket etc.

Datatype change:
Data Type for all packets are made similar to firmware C data types to reduce SDK memory footprint.

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






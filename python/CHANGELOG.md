# Changelog

All notable changes to python SDK will be documented in this file.

## [v4.0.8] - Aug 10, 2021

- All timestamps in every data stream are changed from ticks to unix epoc.

#### PPG, SQI, PED, ECG
- get_algo_version method added.
- get_version method added.

#### TEMP
- Change in temperature stream: renamed temperature1 to skin_temperature and temperature2 to impedance.

#### FS
- write_config_file method added.

#### ADPD
- get_agc_status method removed.
- Change in ADPD stream data: renamed adpdData to singnalData and Darkdata.
- get_sensor_status requires stream as parameter.
- read_device_configuration_block returns array.


#### EDA
- dynamic_scaling scales updated.

#### SDK
- connect and disconnect method added.
- convert_log_to_csv method added.
- join_csv method added.

#### PM
- device_configuration_block_status packet update: renamed wrist_detect_block to low_touch_block and added bcm_block.
- set_datetime bug fix for negative timezone issue.
- DCB read/write/delete methods added.

#### BCM
- DCB read/write/delete methods added.

#### LT_APP
- enable_command_logging  method added.
- disable_command_logging  method added.


## [v4.0.7] - Apr 29, 2021

### Modified methods signature

#### PM
- set_datetime takes no default value.

## [v4.0.6] - Apr 08, 2021

### New methods

#### FS
- stream_file

## [v4.0.5] - Mar 26, 2021

### New methods

#### AD7156
- delete_device_configuration_block
- load_configuration
- read_device_configuration_block
- read_register
- set_timeout
- write_device_configuration_block
- write_device_configuration_block_from_file
- write_register

#### ADPD
- write_library_configuration
- pause
- resume
- get_supported_app_id
- get_supported_clocks
- get_supported_devices
- get_supported_slots
- get_supported_streams
- get_agc_status


#### ADXL
- get_supported_devices


#### BCM
- get_supported_dft_windows
- get_supported_hs_resistor_tia_ids

#### EDA
- calibrate_resistor_tia
- get_supported_scales
- get_supported_dft_windows

#### PM
- device_configuration_block_status
- enable_touch_sensor
- disable_touch_sensor
- enable_user_config_logs
- disable_user_config_logs
- disable_user_config_logs
- get_low_touch_status
- get_supported_chips
- read_register
- write_register

#### PPG
- get_supported_lcfg_ids

#### SQI
- get_supported_slots
- set_slot
- get_sensor_status
- set_callback
- set_timeout
- start_and_subscribe_stream
- start_sensor
- stop_and_unsubscribe_stream
- stop_sensor
- subscribe_stream
- unsubscribe_stream

#### Temperature
- get_device_configuration

#### FS
- get_supported_streams
- abort_logging
- start_logging
- stop_logging


### Modified methods names

#### ADPD
- create_dcfg -> create_device_configuration
- dcb_delete_config -> delete_device_configuration_block
- agc_ctrl -> disable_agc, enable_agc
- get_com_mode -> get_communication_mode
- get_dcfg -> get_device_configuration
- get_slot_active -> get_slot_status
- get_stream_decimation_factor -> get_decimation_factor
- get_stream_status -> get_sensor_status
- load_cfg -> load_configuration
- read_dcb_config -> read_device_configuration_block
- register_read -> read_register
- register_write -> write_register
- set_adpd4k_fs -> set_sampling_frequency
- set_slot_active -> enable_slot, disable_slot
- set_stream_callback -> set_callback
- set_stream_decimation_factor -> set_decimation_factor
- write_dcb_config -> write_device_configuration_block
- write_dcb_config_file -> write_device_configuration_block_from_file

#### ADXL
- load_cfg -> load_configuration
- get_stream_status -> get_sensor_status
- get_dcfg -> get_device_configuration
- register_read -> read_register
- register_write -> write_register
- read_dcb_config -> read_device_configuration_block
- dcb_delete_config -> delete_device_configuration_block
- set_stream_callback -> set_callback
- set_stream_decimation_factor -> set_decimation_factor
- get_stream_decimation_factor -> get_decimation_factor
- write_dcb_config -> write_device_configuration_block
- write_dcb_config_file -> write_device_configuration_block_from_file

#### BCM
- set_dft_num -> set_discrete_fourier_transformation
- set_hsrtia_cal -> calibrate_hs_resistor_tia
- lcfg_read -> read_library_configuration
- lcfg_write -> write_library_configuration
- get_stream_status -> get_sensor_status
- set_stream_callback -> set_callback
- set_stream_decimation_factor -> set_decimation_factor
- get_stream_decimation_factor -> get_decimation_factor

#### ECG
- lcfg_read -> read_library_configuration
- lcfg_write -> write_library_configuration
- get_stream_status -> get_sensor_status
- set_stream_callback -> set_callback
- set_stream_decimation_factor -> set_decimation_factor
- get_stream_decimation_factor -> get_decimation_factor
- read_dcb_config -> read_device_configuration_block
- dcb_delete_config -> delete_device_configuration_block
- write_dcb_config -> write_device_configuration_block
- write_dcb_config_file -> write_device_configuration_block_from_file
- dcb_set_lcfg -> write_dcb_to_lcfg
- get_algo_vendor_version -> get_version

#### EDA
- lcfg_read -> read_library_configuration
- lcfg_write -> write_library_configuration
- get_stream_status -> get_sensor_status
- set_stream_callback -> set_callback
- set_stream_decimation_factor -> set_decimation_factor
- get_stream_decimation_factor -> get_decimation_factor
- read_dcb_config -> read_device_configuration_block
- dcb_delete_config -> delete_device_configuration_block
- write_dcb_config -> write_device_configuration_block
- write_dcb_config_file -> write_device_configuration_block_from_file
- dcb_set_lcfg -> write_dcb_to_lcfg
- set_dft_num -> set_discrete_fourier_transformation
- dynamic_scaling -> enable_dynamic_scaling, disable_dynamic_scaling

#### Pedometer
- get_stream_status -> get_sensor_status
- set_stream_callback -> set_callback

#### PM
- enter_bootloader -> enter_boot_loader_mode
- get_date_time -> get_datetime
- set_date_time -> set_datetime
- set_stream_callback -> set_callback

#### PPG
- lcfg_read -> read_library_configuration
- lcfg_write -> write_library_configuration
- get_stream_status -> get_sensor_status
- set_stream_callback -> set_callback
- set_stream_decimation_factor -> set_decimation_factor
- get_stream_decimation_factor -> get_decimation_factor
- read_dcb_config -> read_device_configuration_block
- dcb_delete_config -> delete_device_configuration_block
- write_dcb_config -> write_device_configuration_block
- write_dcb_config_file -> write_device_configuration_block_from_file
- get_algo_vendor_version -> get_version
- get_lcfg -> get_library_configuration
- set_ppg_stream_callback -> set_ppg_callback
- set_syncppg_stream_callback -> set_syncppg_callback

#### Temperature
- get_stream_status -> get_sensor_status
- set_stream_callback -> set_callback
- set_stream_decimation_factor -> set_decimation_factor
- get_stream_decimation_factor -> get_decimation_factor

#### FS
- fs_status -> get_status
- fs_subscribe -> subscribe_stream
- fs_unsubscribe -> unsubscribe_stream
- fs_format -> format
- fs_volume_info -> volume_info
- fs_ls -> ls
- fs_get -> download_file
- fs_get_stream_chunk -> download_file_chunk
- fs_debug_info -> get_debug_info
- fs_stream_subscription_status -> get_stream_status
- fs_key_value_pair -> inject_key_value_pair
- fs_get_file_count -> get_file_count
- fs_config_log -> enable_config_log, disable_config_log
- fs_mount -> mount


### Removed methods names

#### ADPD
- clear_stream_impedance_list_buffer
- get_stream_address
- stream_dispatch
- agc_info  # moved to test application

#### FS
- fs_config_write
- fs_refhr
- stream_dispatch
- fs_get_number_of_bad_blocks # moved to test application

#### Pedometer
- clear_stream_impedance_list_buffer
- stream_dispatch

#### PM
- clear_stream_impedance_list_buffer
- set_usb_power
- enable_or_disable_thermistor_state_change
- dg502_software_control
- stream_dispatch
- set_power_state # moved to test application
- ldo_control # moved to test application
- cap_sense_test  # moved to test application
- ping  # moved to test application

#### PPG
- clear_stream_impedance_list_buffer
- stream_dispatch
- get_last_state # moved to test application

#### Temperature
- clear_stream_impedance_list_buffer
- stream_dispatch
- get_version

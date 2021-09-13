# Study watch cheatsheet.

### ADPD

| Register Name | Address| Values  | Example|
| ------------- |:-------------:| :-----|-----:|
| ODR     | `0xD` |`0x4E20` -- 50Hz <br> `0x2710` -- 100Hz<br> `0x0FA0` -- 250Hz <br> `0x07D0` -- 500Hz | [Python](https://analogdevicesinc.github.io/study-watch-sdk/python/_rst/adi_study_watch.application.html#adi_study_watch.application.adpd_application.ADPDApplication.write_register) <br> [Android](https://analogdevicesinc.github.io/study-watch-sdk/android/com/analog/study_watch_sdk/application/ADPDApplication.html#writeRegister-long:A:A-)|

### ADXL

| Register Name | Address| Values  | Example|
| ------------- |:-------------:| :-----|-----:|
| ODR     | `0x2C` | `0x98` -- 12.5Hz <br> `0x99` -- 25Hz <br> `0x9A` -- 50Hz <br> `0x9B` -- 100Hz <br> `0x9C` -- 200Hz <br> `0x9F` -- 400Hz| [Python](https://analogdevicesinc.github.io/study-watch-sdk/python/_rst/adi_study_watch.application.html#adi_study_watch.application.adxl_application.ADXLApplication.write_register) <br> [Android](https://analogdevicesinc.github.io/study-watch-sdk/android/com/analog/study_watch_sdk/application/ADXLApplication.html#writeRegister-long:A:A-)|

Note: send ODR write register command only after adxl sensor start.

### EDA

| Register Name | Address| Values  | Example|
| ------------- |:-------------:| :-----|-----:|
| ODR     | `0x0` | `0x04` -- 4Hz <br>  `0x0A` -- 10Hz<br> `0x0F` -- 15Hz<br>`0x14` -- 20Hz<br> `0x19` -- 25Hz<br> `0x1E` -- 30Hz|  [Python](https://analogdevicesinc.github.io/study-watch-sdk/python/_rst/adi_study_watch.application.html#adi_study_watch.application.eda_application.EDAApplication.write_library_configuration) <br> [Android](https://analogdevicesinc.github.io/study-watch-sdk/android/com/analog/study_watch_sdk/application/EDAApplication.html#writeLibraryConfiguration-long:A:A-)|

### ECG

| Register Name | Address| Values  | Example|
| ------------- |:-------------:| :-----|-----:|
| ODR     | `0x0` | `0x0C` -- 12Hz<br> `0x19` -- 25Hz<br> `0x32` -- 50Hz<br>`0x64` -- 100Hz<br>`0xC8` -- 200Hz<br> `0xFA` -- 250Hz<br>`0x12C` -- 300Hz<br> `0x190` -- 400Hz<br> `0x1F4` -- 500Hz<br> `0x258` -- 600Hz<br> `0x2BC` -- 700Hz<br> `0x320` -- 800Hz<br> `0x384` -- 900Hz<br> `0x3E8` -- 1000Hz |  [Python](https://analogdevicesinc.github.io/study-watch-sdk/python/_rst/adi_study_watch.application.html#adi_study_watch.application.ecg_application.ECGApplication.write_library_configuration) <br> [Android](https://analogdevicesinc.github.io/study-watch-sdk/android/com/analog/study_watch_sdk/application/ECGApplication.html#writeLibraryConfiguration-long:A:A-)|

### BCM

| Register Name | Address| Values  | Example|
| ------------- |:-------------:| :-----|-----:|
| ODR     | `0x0` | `0x04` -- 4Hz<br> `0x0A` -- 10Hz<br> `0x0F` -- 15Hz<br> `0x14` -- 20Hz<br> `0x16` -- 22Hz |  [Python](https://analogdevicesinc.github.io/study-watch-sdk/python/_rst/adi_study_watch.application.html#adi_study_watch.application.bcm_application.BCMApplication.write_library_configuration) <br> [Android](https://analogdevicesinc.github.io/study-watch-sdk/android/com/analog/study_watch_sdk/application/BCMApplication.html#writeLibraryConfiguration-long:A:A-)|

### Sequence for Python CSV logging

- sensor start
- enable csv_logging
- subscribe to stream
- wait
- unsubscribe to stream
- disable csv_logging
- sensor stop

### Preferred sequence for starting sensors

- EDA
- ADPD
- ADXL
- PPG
- SQI
- ECG
- Temperature

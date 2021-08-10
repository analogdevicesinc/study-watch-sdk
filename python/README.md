# Study Watch Python SDK

The adi-study-watch provides an object-oriented interface for interacting with ADI's VSM study watch platform.

**Installation**

```python
pip install adi-study-watch
```
**Description**

A user application can use the SDK to receive complete packets of bytes over a physical interface (USB or BLE) and
decode it. The functionality is organized into applications, some of which own sensors, some own system-level
functionality (i.e. file system), and while others own algorithms. The hierarchy of objects within the SDK mirrors the
applications present on the device. Each application has its own object within the SDK hierarchy, which is used to
interact with that application. A brief guide on using the SDK and few examples have been added below.

**Firmware Setup**

https://github.com/analogdevicesinc/study-watch-sdk/blob/main/firmware/Study_Watch_Firmware_Upgrade.pdf

**Getting started with SDK**

Import the adi-study-watch module into your application code
```python
from adi_study_watch import SDK
```
Instantiate the SDK object by passing the com port number
```python
sdk = SDK('COM28')
```
The application objects can be instantiated from the sdk object. In order to instantiate an application object, we'll
have to pass a call-back function as an input argument which can be used to retrieve the data from the application
object. Define a callback function as displayed below.
```python
def adxl_cb(data):
    print(data)
```
Once the call-back function is defined, you can instantiate the application object as shown below.
```python
adxl_app = sdk.get_adxl_application()
adxl_app.set_callback(adxl_cb)
```
Each application object has various methods that can be called by referring to the application. An example of retrieving
the sensor status is shown below. Almost all method in an application returns result in a dict.

```python
packet = adxl_app.get_sensor_status() # returns dict
print(packet)
```

**Basic Example:**

```python
import time
from datetime import datetime
from adi_study_watch import SDK

# callback function to receive adxl data
def callback_data(data):
    sequence_number = data["payload"]["sequence_number"]
    for stream_data in data["payload"]["stream_data"]:
        dt_object = datetime.fromtimestamp(stream_data['timestamp'] / 1000)  # convert timestamp from ms to sec.
        print(f"seq :{sequence_number} timestamp: {dt_object} x,y,z :: ({stream_data['x']}, "
              f"{stream_data['y']}, {stream_data['z']})")


if __name__ == "__main__":
    sdk = SDK("COM4")
    application = sdk.get_adxl_application()
    application.set_callback(callback_data)

    # quickstart adxl stream
    application.start_sensor()
    application.enable_csv_logging("adxl.csv") # logging adxl data to csv file
    application.subscribe_stream()
    time.sleep(10)
    application.unsubscribe_stream()
    application.disable_csv_logging()
    application.stop_sensor()
```

**All streams packet structure :**
https://analogdevicesinc.github.io/study-watch-sdk/python/_rst/adi_study_watch.core.packets.html#module-adi_study_watch.core.packets.stream_data_packets

**Documentation :**
https://analogdevicesinc.github.io/study-watch-sdk/python

**Examples :**
https://github.com/analogdevicesinc/study-watch-sdk/tree/main/python/samples

**License :**
https://github.com/analogdevicesinc/study-watch-sdk/blob/main/LICENSE

**Changelog**
https://github.com/analogdevicesinc/study-watch-sdk/blob/main/python/CHANGELOG.md


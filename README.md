# ADI Study Watch

The adi-study-watch provides an object-oriented interface for interacting with ADI's VSM study watch platform. 


**Installation**

    pip install adi-study-watch
    

**Description**

A user application can use the SDK to receive complete packets of bytes over a physical interface (USB or BLE) and decode it.
The functionality is organized into applications, some of which own sensors, some own system-level functionality (i.e. file system), and while others own algorithms. The hierarchy of objects within the SDK mirrors the applications present on the device. Each application has its own object within the SDK hierarchy, which is used to interact with that application.  A brief guide on using the SDK and few examples have been added below.


**Firmware Setup**

https://github.com/analogdevicesinc/study-watch-sdk/blob/main/bin_Firmware/Study_Watch_Firmware_Upgrade_using_Android_App.docx


**Getting started with SDK**
 
Import the adi-study-watch module into your application code

    from adi_study_watch import SDK

Instantiate the SDK object by passing the com port number

    sdk = SDK('COM28')

The application objects can be instantiated from the sdk object. In order to instantiate an application object, we'll have to pass a call-back function as an input argument which can be used to retrieve the data from the application object. Define a callback function as displayed below.
    
    def adpd_cb(seq, data):
        for d in data:
            print(d)

Once the call-back function is defined, you can instantiate the application object as shown below.

    adpd_app = sdk.get_adpd4000_application(adpd_cb)

Each application object has various methods that can be called by referring to the application. An example of retrieving the version is shown below.
    
    adpd_app.get_version()



**Basic Example:**

    import time
    from adi_study_watch import SDK
    
    # callback function to receive adxl data
    def callback_adxl(seq, data):
        for d in data:
            print("t:{} x:{} y:{} z:{}".format(d.timestamp, d.x, d.y, d.z))


    sdk = SDK("COM4")
    adxl_application = sdk.get_adxl_application(callback_adxl)
    adxl_application.start_default_stream()
    time.sleep(10)
    adxl_application.stop_default_stream()
    
    
**Examples**
https://github.com/analogdevicesinc/study-watch-sdk/tree/main/samples

**Documentation:**
https://analogdevicesinc.github.io/study-watch-sdk/

**License**
https://github.com/analogdevicesinc/study-watch-sdk/blob/main/LICENSE.pdf



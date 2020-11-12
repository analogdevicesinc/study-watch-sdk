# Getting started with study watch python api

**Install**

    pip install adi_study_watch-4.0.4-py3-none-any.whl

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


**Github Repo:**
https://github.com/analogdevicesinc/study-watch-sdk

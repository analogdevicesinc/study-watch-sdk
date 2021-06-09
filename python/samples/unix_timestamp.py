# This example converts sensor timestamp to UNIX timestamp.
import time

from adi_study_watch import SDK
from datetime import datetime

last_timestamp = 0
ref_timestamp = 0


def callback_data(data):
    global last_timestamp
    global ref_timestamp
    sequence_number = data["payload"]["sequence_number"]
    for stream_data in data["payload"]["stream_data"]:
        timestamp = stream_data["timestamp"]
        # difference between two timestamp
        change = timestamp - last_timestamp
        # should come close to 0.009s
        change = change / 32768.0
        # ignoring first packet
        if not last_timestamp == 0:
            ref_timestamp += change
        last_timestamp = timestamp
        dt_object = datetime.fromtimestamp(ref_timestamp)
        print(f"seq :{sequence_number} tick: {stream_data['timestamp']} timestamp : {ref_timestamp} "
              f"ch_time: {change} datetime: {dt_object}  "
              f"x,y,z :: ({stream_data['x']}, {stream_data['y']}, {stream_data['z']})")


if __name__ == '__main__':
    sdk = SDK("COM4")
    pm_app = sdk.get_pm_application()
    application = sdk.get_adxl_application()
    application.set_callback(callback_data)
    # setting current date time, this step can be ignored
    pm_app.set_datetime(datetime.now())
    # reference datetime
    x = pm_app.get_datetime()
    dt = datetime(x['payload']['year'], x['payload']['month'], x['payload']['day'], x['payload']['hour'],
                  x['payload']['minute'], x['payload']['second'])
    print(f"start time {dt}")
    # convert datetime to timestamp
    ref_timestamp = datetime.timestamp(dt)
    print(f"System TS {ref_timestamp}")
    application.start_sensor()
    # ODR to 100Hz so 100 data points in 1 sec
    application.write_register([[0x2c, 0x9B]])
    application.subscribe_stream()
    time.sleep(10)
    application.unsubscribe_stream()
    application.stop_sensor()
    # end datetime to compare from data time limit
    x = pm_app.get_datetime()
    dt = datetime(x['payload']['year'], x['payload']['month'], x['payload']['day'], x['payload']['hour'],
                  x['payload']['minute'], x['payload']['second'])
    print(f"end time {dt}")

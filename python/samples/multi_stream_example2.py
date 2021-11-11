# ******************************************************************************
# Copyright (c) 2019 Analog Devices, Inc.  All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
# - Redistributions of source code must retain the above copyright notice, this
#  list of conditions and the following disclaimer.
# - Redistributions in binary form must reproduce the above copyright notice,
#  this list of conditions and the following disclaimer in the documentation
#  and/or other materials provided with the distribution.
# - Modified versions of the software must be conspicuously marked as such.
# - This software is licensed solely and exclusively for use with
#  processors/products manufactured by or for Analog Devices, Inc.
# - This software may not be combined or merged with other code in any manner
#  that would cause the software to become subject to terms and conditions
#  which differ from those listed here.
# - Neither the name of Analog Devices, Inc. nor the names of its contributors
#  may be used to endorse or promote products derived from this software
#  without specific prior written permission.
# - The use of this software may or may not infringe the patent rights of one
#  or more patent holders.  This license does not release you from the
#  requirement that you obtain separate licenses from these patent holders to
#  use this software.
#
# THIS SOFTWARE IS PROVIDED BY ANALOG DEVICES, INC. AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
# NONINFRINGEMENT, TITLE, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL ANALOG DEVICES, INC. OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, DAMAGES ARISING OUT OF
# CLAIMS OF INTELLECTUAL PROPERTY RIGHTS INFRINGEMENT; PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
# ******************************************************************************

# Impedance + every wavelength of the optical sensor (F,G,H,I) + Acc + Temp.

import math
import time

from adi_study_watch import SDK


def callback_eda_data(data):
    for value in data["payload"]["stream_data"]:
        eda_real = value["real"]
        eda_imaginary = value["imaginary"]
        if eda_real == 0:
            eda_real = 1
        impedance_real = eda_real * 1000
        impedance_img = eda_imaginary * 1000
        real_and_img = float(impedance_real * impedance_real + impedance_img * impedance_img)
        impedance_magnitude = math.sqrt(real_and_img)
        impedance_phase = math.atan2(impedance_img, impedance_real)
        admittance_real = float(impedance_real / real_and_img)
        admittance_img = -float(impedance_img / real_and_img)
        admittance_magnitude = 1 / impedance_magnitude
        admittance_phase = math.atan2(admittance_img, admittance_real)
        print(f"{value['timestamp']}, {impedance_real}, {impedance_img}, {impedance_magnitude}, {impedance_phase},"
              f"{admittance_real}, {admittance_img}, {admittance_magnitude}, {admittance_phase}")


def callback_slot_f_data(data):
    print(f"SLOT F:: {data}")


def callback_slot_g_data(data):
    print(f"SLOT G:: {data}")


def callback_slot_h_data(data):
    print(f"SLOT H:: {data}")


def callback_slot_i_data(data):
    print(f"SLOT I:: {data}")


def callback_adxl_data(data):
    print(f"ADXL:: {data}")


def callback_temp_data(data):
    print(f"TEMP:: {data}")


if __name__ == "__main__":
    sdk = SDK("COM4")
    # sdk = SDK("COM6", is_ble=True) # for BLE
    # getting all required application from SDK
    eda_application = sdk.get_eda_application()
    adpd_application = sdk.get_adpd_application()
    adxl_application = sdk.get_adxl_application()
    temp_application = sdk.get_temperature_application()
    pm_application = sdk.get_pm_application()

    # assigning callbacks
    eda_application.set_callback(callback_eda_data)
    adpd_application.set_callback(callback_slot_f_data, stream=adpd_application.STREAM_ADPD6)
    adpd_application.set_callback(callback_slot_g_data, stream=adpd_application.STREAM_ADPD7)
    adpd_application.set_callback(callback_slot_h_data, stream=adpd_application.STREAM_ADPD8)
    adpd_application.set_callback(callback_slot_i_data, stream=adpd_application.STREAM_ADPD9)
    adxl_application.set_callback(callback_adxl_data)
    temp_application.set_callback(callback_temp_data)

    # setting EDA ODR to 30Hz
    eda_application.write_library_configuration([[0x0, 0x1E]])

    # checking for DVT2 board
    if pm_application.get_chip_id(pm_application.CHIP_ADPD4K)["payload"]["chip_id"] == 0xc0:
        adpd_application.write_device_configuration_block_from_file("dcb_cfg/DVT1_TEMP+4LED.dcfg")
        adpd_application.calibrate_clock(adpd_application.CLOCK_1M_AND_32M)
    else:
        adpd_application.write_device_configuration_block_from_file("dcb_cfg/DVT2_TEMP+4LED.dcfg")
        adpd_application.calibrate_clock(adpd_application.CLOCK_1M)
    adpd_application.load_configuration(adpd_application.DEVICE_GREEN)

    adpd_application.enable_agc([adpd_application.LED_GREEN, adpd_application.LED_RED,
                                 adpd_application.LED_IR, adpd_application.LED_BLUE])

    # setting ADPD ODR to 100Hz
    adpd_application.write_register([[0xD, 0x2710]])

    # starting sensors
    eda_application.start_sensor()
    adpd_application.start_sensor()
    adxl_application.start_sensor()
    temp_application.start_sensor()

    # setting ADXL ODR to 100Hz -- ADXL Loads DCB when Start Sensor is done, if no DCB it will load Default Config
    adxl_application.write_register([[0x2C, 0x9B]])

    # subscribing streams
    eda_application.subscribe_stream()
    adpd_application.enable_csv_logging("adpd6.csv", stream=adpd_application.STREAM_ADPD6)
    adpd_application.enable_csv_logging("adpd7.csv", stream=adpd_application.STREAM_ADPD7)
    adpd_application.enable_csv_logging("adpd8.csv", stream=adpd_application.STREAM_ADPD8)
    adpd_application.enable_csv_logging("adpd9.csv", stream=adpd_application.STREAM_ADPD9)

    adpd_application.subscribe_stream(adpd_application.STREAM_ADPD6)
    adpd_application.subscribe_stream(adpd_application.STREAM_ADPD7)
    adpd_application.subscribe_stream(adpd_application.STREAM_ADPD8)
    adpd_application.subscribe_stream(adpd_application.STREAM_ADPD9)
    adxl_application.subscribe_stream()
    temp_application.subscribe_stream()

    # sleep for 10 sec
    time.sleep(10)

    # unsubscribing streams
    eda_application.unsubscribe_stream()
    adpd_application.unsubscribe_stream(adpd_application.STREAM_ADPD6)
    adpd_application.unsubscribe_stream(adpd_application.STREAM_ADPD7)
    adpd_application.unsubscribe_stream(adpd_application.STREAM_ADPD8)
    adpd_application.unsubscribe_stream(adpd_application.STREAM_ADPD9)
    adxl_application.unsubscribe_stream()
    temp_application.unsubscribe_stream()

    adpd_application.disable_csv_logging(stream=adpd_application.STREAM_ADPD6)
    adpd_application.disable_csv_logging(stream=adpd_application.STREAM_ADPD7)
    adpd_application.disable_csv_logging(stream=adpd_application.STREAM_ADPD8)
    adpd_application.disable_csv_logging(stream=adpd_application.STREAM_ADPD9)

    # stop sensors
    eda_application.stop_sensor()
    adpd_application.stop_sensor()
    adxl_application.stop_sensor()
    temp_application.stop_sensor()

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

import time

from adi_study_watch import SDK


def callback_ecg_data(data):
    print(f"ECG:: {data}")


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
    ecg_application = sdk.get_ecg_application()
    adpd_application = sdk.get_adpd_application()
    adxl_application = sdk.get_adxl_application()
    temp_application = sdk.get_temperature_application()

    # assigning callbacks
    ecg_application.set_callback(callback_ecg_data)
    adpd_application.set_callback(callback_slot_f_data, stream=adpd_application.STREAM_ADPD6)
    adpd_application.set_callback(callback_slot_g_data, stream=adpd_application.STREAM_ADPD7)
    adpd_application.set_callback(callback_slot_h_data, stream=adpd_application.STREAM_ADPD8)
    adpd_application.set_callback(callback_slot_i_data, stream=adpd_application.STREAM_ADPD9)
    adxl_application.set_callback(callback_adxl_data)
    temp_application.set_callback(callback_temp_data)

    # setting ECG ODR to 250Hz
    ecg_application.write_library_configuration([[0x0, 0xFA]])

    # adpd config:
    adpd_application.load_configuration(adpd_application.DEVICE_G_R_IR_B)
    adpd_application.calibrate_clock(adpd_application.CLOCK_1M_AND_32M)
    adpd_application.enable_agc([adpd_application.LED_GREEN, adpd_application.LED_RED, adpd_application.LED_IR,
                                 adpd_application.LED_BLUE])

    # setting ADPD ODR to 100Hz
    adpd_application.write_register([[0xD, 0x2710]])

    # starting sensors
    ecg_application.start_sensor()
    adpd_application.start_sensor()
    adxl_application.start_sensor()
    temp_application.start_sensor()

    # setting ADXL ODR to 50Hz -- ADXL Loads DCB when Start Sensor is done, if no DCB it will load Default Config
    adxl_application.write_register([[0x2C, 0x9A]])

    # subscribing streams
    ecg_application.subscribe_stream()
    adpd_application.subscribe_stream(adpd_application.STREAM_ADPD6)
    adpd_application.subscribe_stream(adpd_application.STREAM_ADPD7)
    adpd_application.subscribe_stream(adpd_application.STREAM_ADPD8)
    adpd_application.subscribe_stream(adpd_application.STREAM_ADPD9)
    adxl_application.subscribe_stream()
    temp_application.subscribe_stream()

    # sleep for 10 sec
    time.sleep(10)

    # unsubscribing streams
    ecg_application.unsubscribe_stream()
    adpd_application.unsubscribe_stream(adpd_application.STREAM_ADPD6)
    adpd_application.unsubscribe_stream(adpd_application.STREAM_ADPD7)
    adpd_application.unsubscribe_stream(adpd_application.STREAM_ADPD8)
    adpd_application.unsubscribe_stream(adpd_application.STREAM_ADPD9)
    adxl_application.unsubscribe_stream()
    temp_application.unsubscribe_stream()

    # stop sensors
    ecg_application.stop_sensor()
    adpd_application.stop_sensor()
    adxl_application.stop_sensor()
    temp_application.stop_sensor()

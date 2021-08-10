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

if __name__ == "__main__":
    sdk = SDK("COM4")
    # sdk = SDK("COM6", is_ble=True) # for BLE
    # getting all required application from SDK
    ecg_application = sdk.get_ecg_application()
    adpd_application = sdk.get_adpd_application()
    adxl_application = sdk.get_adxl_application()
    temp_application = sdk.get_temperature_application()
    fs_application = sdk.get_fs_application()
    pm_application = sdk.get_pm_application()

    # setting ECG ODR to 250Hz
    ecg_application.write_library_configuration([[0x0, 0xFA]])

    # adpd config:
    adpd_application.create_device_configuration([
        [adpd_application.SLOT_D, adpd_application.APP_TEMPERATURE_THERMISTOR],
        [adpd_application.SLOT_E, adpd_application.APP_TEMPERATURE_RESISTOR],
        [adpd_application.SLOT_F, adpd_application.APP_ADPD_GREEN],
        [adpd_application.SLOT_G, adpd_application.APP_ADPD_RED],
        [adpd_application.SLOT_H, adpd_application.APP_ADPD_INFRARED],
        [adpd_application.SLOT_I, adpd_application.APP_ADPD_BLUE],
    ])
    adpd_application.load_configuration(adpd_application.DEVICE_GREEN)
    # checking for DVT2 board
    if pm_application.get_chip_id(pm_application.CHIP_ADPD4K)["payload"]["chip_id"] == 0xc0:
        adpd_application.calibrate_clock(adpd_application.CLOCK_1M_AND_32M)
    else:
        adpd_application.calibrate_clock(adpd_application.CLOCK_1M)
    adpd_application.enable_agc([adpd_application.LED_GREEN, adpd_application.LED_RED,
                                 adpd_application.LED_IR, adpd_application.LED_BLUE])

    # setting ADPD ODR to 100Hz
    adpd_application.write_register([[0xD, 0x2710]])

    # subscribing streams
    fs_application.subscribe_stream(fs_application.STREAM_ECG)
    fs_application.subscribe_stream(fs_application.STREAM_ADPD6)
    fs_application.subscribe_stream(fs_application.STREAM_ADPD7)
    fs_application.subscribe_stream(fs_application.STREAM_ADPD8)
    fs_application.subscribe_stream(fs_application.STREAM_ADPD9)
    fs_application.subscribe_stream(fs_application.STREAM_ADXL)
    fs_application.subscribe_stream(fs_application.STREAM_TEMPERATURE)

    # starting sensors
    ecg_application.start_sensor()
    adpd_application.start_sensor()
    adxl_application.start_sensor()
    temp_application.start_sensor()

    # setting ADXL ODR to 50Hz -- ADXL Loads DCB when Start Sensor is done, if no DCB it will load Default Config
    adxl_application.write_register([[0x2C, 0x9A]])

    # logging started
    fs_application.start_logging()

    # sleep for 10 sec
    time.sleep(10)

    # stop sensors
    ecg_application.stop_sensor()
    adpd_application.stop_sensor()
    adxl_application.stop_sensor()
    temp_application.stop_sensor()

    # unsubscribing streams
    fs_application.unsubscribe_stream(fs_application.STREAM_ECG)
    fs_application.unsubscribe_stream(fs_application.STREAM_ADPD6)
    fs_application.unsubscribe_stream(fs_application.STREAM_ADPD7)
    fs_application.unsubscribe_stream(fs_application.STREAM_ADPD8)
    fs_application.unsubscribe_stream(fs_application.STREAM_ADPD9)
    fs_application.unsubscribe_stream(fs_application.STREAM_ADXL)
    fs_application.unsubscribe_stream(fs_application.STREAM_TEMPERATURE)

    # stop logging
    fs_application.stop_logging()

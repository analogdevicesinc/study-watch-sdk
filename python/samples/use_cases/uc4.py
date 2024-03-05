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

# temp_freq_hz = 1
# ppg_freq_hz = 50
# ecg_freq_hz = 1000

import time

from adi_study_watch import SDK


def ppg_callback(data):
    print(data)


def sync_ppg_callback(data):
    print(data)


def temp_callback(data):
    print(data)


def ecg_callback(data):
    print(data)


if __name__ == "__main__":
    sdk = SDK("COM4")
    # getting all required application from SDK
    adxl_app = sdk.get_adxl_application()
    adpd_app = sdk.get_adpd_application()
    ecg_app = sdk.get_ecg_application()
    ppg_app = sdk.get_ppg_application()
    temp_app = sdk.get_temperature_application()
    pm_application = sdk.get_pm_application()

    adpd_dcfg = None
    adxl_dcfg = None

    if pm_application.get_chip_id(pm_application.CHIP_ADPD4K)["payload"]["chip_id"] == 0xc0:
        adpd_dcfg = "dcb_cfg/DVT1_MV_UC4_ADPD_dcb.dcfg"
        adxl_dcfg = "dcb_cfg/DVT1_MV_UC4_ADXL_dcb.dcfg"
    else:
        adpd_dcfg = "dcb_cfg/DVT2_MV_UC4_ADPD_dcb.dcfg"
        adxl_dcfg = "dcb_cfg/DVT2_MV_UC4_ADXL_dcb.dcfg"

    temp_app.set_callback(temp_callback)
    ecg_app.set_callback(ecg_callback)
    ppg_app.set_callback(ppg_callback, stream=ppg_app.STREAM_PPG)
    ppg_app.set_callback(sync_ppg_callback, stream=ppg_app.STREAM_SYNC_PPG)

    adpd_app.delete_device_configuration_block()
    # loading dcb
    adpd_app.write_device_configuration_block_from_file(adpd_dcfg)
    adpd_app.load_configuration(adpd_app.DEVICE_GREEN)
    ppg_app.set_library_configuration(ppg_app.LCFG_ID_ADPD4000)

    adxl_app.delete_device_configuration_block()
    adxl_app.write_device_configuration_block_from_file(adxl_dcfg)

    ecg_app.delete_device_configuration_block()
    ecg_app.write_library_configuration([[0x0, 0x3E8]])  # 1000 ODR

    # starting sensors
    ppg_app.start_sensor()
    temp_app.start_sensor()
    ecg_app.start_sensor()

    # subscribing streams
    ppg_app.subscribe_stream()
    temp_app.subscribe_stream()
    ecg_app.subscribe_stream()

    time.sleep(20)

    # unsubscribing streams
    ppg_app.unsubscribe_stream()
    temp_app.unsubscribe_stream()
    ecg_app.unsubscribe_stream()

    # stop sensors
    ppg_app.stop_sensor()
    temp_app.stop_sensor()
    ecg_app.stop_sensor()

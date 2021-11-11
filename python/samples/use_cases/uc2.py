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

# adpd_freq_hz = 100
# adxl_freq_hz = 50
# eda_freq_hz = 30
# temp_freq_hz = 1

import time

from adi_study_watch import SDK


def adxl_callback(data):
    print(data)


def adpd_callback(data):
    print(data)


def temp_callback(data):
    print(data)


def eda_callback(data):
    print(data)


if __name__ == "__main__":
    sdk = SDK("COM4")
    # getting all required application from SDK
    adxl_app = sdk.get_adxl_application()
    adpd_app = sdk.get_adpd_application()
    eda_app = sdk.get_eda_application()
    temp_app = sdk.get_temperature_application()
    pm_application = sdk.get_pm_application()

    adpd_dcfg = None
    adxl_dcfg = None

    if pm_application.get_chip_id(pm_application.CHIP_ADPD4K)["payload"]["chip_id"] == 0xc0:
        adpd_dcfg = "dcb_cfg/DVT1_MV_UC2_ADPD_dcb.dcfg"
        adxl_dcfg = "dcb_cfg/DVT1_MV_UC2_ADXL_dcb.dcfg"
    else:
        adpd_dcfg = "dcb_cfg/DVT2_MV_UC2_ADPD_dcb.dcfg"
        adxl_dcfg = "dcb_cfg/DVT2_MV_UC2_ADXL_dcb.dcfg"

    adxl_app.set_callback(adxl_callback)
    adpd_app.set_callback(adpd_callback)
    temp_app.set_callback(temp_callback)
    eda_app.set_callback(eda_callback)

    adpd_app.delete_device_configuration_block()
    # loading dcb
    adpd_app.write_device_configuration_block_from_file(adpd_dcfg)
    adpd_app.load_configuration(adpd_app.DEVICE_GREEN)

    adxl_app.delete_device_configuration_block()
    adxl_app.write_device_configuration_block_from_file(adxl_dcfg)

    eda_app.delete_device_configuration_block()
    eda_app.write_library_configuration([[0x0, 0x1E]])  # 30 ODR

    # starting sensors
    adxl_app.start_sensor()
    adpd_app.start_sensor()
    temp_app.start_sensor()
    eda_app.start_sensor()

    # subscribing streams
    adxl_app.subscribe_stream()
    adpd_app.subscribe_stream()
    temp_app.subscribe_stream()
    eda_app.subscribe_stream()

    time.sleep(20)

    # unsubscribing streams
    adxl_app.unsubscribe_stream()
    adpd_app.unsubscribe_stream()
    temp_app.unsubscribe_stream()
    eda_app.unsubscribe_stream()

    # stop sensors
    adxl_app.stop_sensor()
    adpd_app.stop_sensor()
    temp_app.stop_sensor()
    eda_app.stop_sensor()

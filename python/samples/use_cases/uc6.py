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


def adxl_callback(data):
    print(data)


def adpd6_callback(data):
    print(data)


def bia_callback(data):
    print(data)


def bcm_callback(data):
    print(data)


def temp_callback(data):
    print(data)


def ppg_callback(data):
    print(data)


def sync_ppg_callback(data):
    print(data)


if __name__ == "__main__":
    # For UC6 you need to wear the watch and touch ECG electrodes (both top electrodes).
    sdk = SDK("COM10", mac_address="D3-EC-DA-54-95-A6")
    # getting all required application from SDK
    adxl_app = sdk.get_adxl_application()
    adpd_app = sdk.get_adpd_application()
    bia_app = sdk.get_bia_application()
    temp_app = sdk.get_temperature_application()
    pm_application = sdk.get_pm_application()
    ppg_app = sdk.get_ppg_application()

    adxl_app.set_callback(adxl_callback)
    adpd_app.set_callback(adpd6_callback, stream=adpd_app.STREAM_ADPD6)
    bia_app.set_callback(bia_callback, stream=bia_app.STREAM_BIA)
    bia_app.set_callback(bcm_callback, stream=bia_app.STREAM_BCM)
    temp_app.set_callback(temp_callback)
    ppg_app.set_callback(ppg_callback, stream=ppg_app.STREAM_PPG)
    ppg_app.set_callback(sync_ppg_callback, stream=ppg_app.STREAM_SYNC_PPG)

    adpd_app.delete_device_configuration_block()
    adpd_app.write_device_configuration_block_from_file("dcb_cfg/adpd_g_dcb_100hz.dcfg")
    adpd_app.enable_uc_hr(adpd_app.SLOT_F)
    adpd_app.load_configuration(adpd_app.DEVICE_GREEN)

    adxl_app.delete_device_configuration_block()
    adxl_app.write_device_configuration_block_from_file("dcb_cfg/adxl_dcb.dcfg")

    bia_app.delete_device_configuration_block(bia_app.BIA_LCFG_BLOCK)
    bia_app.delete_device_configuration_block(bia_app.BIA_DCFG_BLOCK)

    # modify index 07 08 and 09 for height weight and age.
    bia_app.write_device_configuration_block_from_file("dcb_cfg/bia_dcb.lcfg", bia_app.BIA_LCFG_BLOCK)
    bia_app.write_dcb_to_lcfg()

    adxl_app.write_register([[0x2C, 0x9A]])
    adpd_app.write_register([[0xD, 0x2710]])  # 100 ODR

    bia_app.subscribe_stream(stream=bia_app.STREAM_BIA)
    bia_app.subscribe_stream(stream=bia_app.STREAM_BCM)
    adpd_app.subscribe_stream(adpd_app.STREAM_ADPD6)
    adxl_app.subscribe_stream()
    ppg_app.subscribe_stream(stream=ppg_app.STREAM_PPG)
    temp_app.subscribe_stream()

    bia_app.start_sensor()
    adpd_app.start_sensor()
    adxl_app.start_sensor()
    temp_app.start_sensor()

    time.sleep(60)

    temp_app.stop_sensor()
    adpd_app.stop_sensor()
    adxl_app.stop_sensor()
    bia_app.stop_sensor()

    temp_app.unsubscribe_stream()
    ppg_app.unsubscribe_stream(stream=ppg_app.STREAM_PPG)
    adpd_app.unsubscribe_stream(adpd_app.STREAM_ADPD6)
    adxl_app.unsubscribe_stream()
    bia_app.unsubscribe_stream(stream=bia_app.STREAM_BIA)
    bia_app.unsubscribe_stream(stream=bia_app.STREAM_BCM)
    adpd_app.disable_uc_hr(adpd_app.SLOT_F)

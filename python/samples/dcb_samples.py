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

from adi_study_watch import SDK


def callback_data(data):
    print(data)


if __name__ == "__main__":
    sdk = SDK("COM4")
    ad7156_app = sdk.get_ad7156_application()
    adpd_app = sdk.get_adpd_application()
    adxl_app = sdk.get_adxl_application()
    bia_app = sdk.get_bia_application()
    ecg_app = sdk.get_ecg_application()
    eda_app = sdk.get_eda_application()
    lt_app = sdk.get_low_touch_application()
    pm_app = sdk.get_pm_application()
    ppg_app = sdk.get_ppg_application()
    temp_app = sdk.get_temperature_application()
    user0_app = sdk.get_user0_application()
    adp5360_app = sdk.get_adp5360_application()

    print("ad7156")
    packet = ad7156_app.write_device_configuration_block_from_file("dcb_cfg/ad7156_dcb.dcfg")
    print(packet)
    packet = ad7156_app.read_device_configuration_block()
    print(packet)
    packet = ad7156_app.delete_device_configuration_block()
    print(packet)

    print("adpd_app")
    packet = adpd_app.write_device_configuration_block_from_file("dcb_cfg/adpd4000_dcb.dcfg")
    print(packet)
    packet = adpd_app.read_device_configuration_block()
    print(packet)
    packet = adpd_app.delete_device_configuration_block()
    print(packet)

    print("adxl_app")
    packet = adxl_app.write_device_configuration_block_from_file("dcb_cfg/adxl_dcb.dcfg")
    print(packet)
    packet = adxl_app.read_device_configuration_block()
    print(packet)
    packet = adxl_app.delete_device_configuration_block()
    print(packet)

    print("bia_app LCFG")
    packet = bia_app.write_device_configuration_block_from_file("dcb_cfg/bia_dcb.lcfg", bia_app.BIA_LCFG_BLOCK)
    print(packet)
    packet = bia_app.read_device_configuration_block(bia_app.BIA_LCFG_BLOCK)
    print(packet)
    packet = bia_app.delete_device_configuration_block(bia_app.BIA_LCFG_BLOCK)
    print(packet)

    print("bia_app DCFG")
    packet = bia_app.write_device_configuration_block_from_file("dcb_cfg/bia_dcb.dcfg", bia_app.BIA_DCFG_BLOCK)
    print(packet)
    packet = bia_app.read_device_configuration_block(bia_app.BIA_DCFG_BLOCK)
    print(packet)
    packet = bia_app.delete_device_configuration_block(bia_app.BIA_DCFG_BLOCK)
    print(packet)

    print("ecg_app")
    packet = ecg_app.write_device_configuration_block_from_file("dcb_cfg/ecg_dcb.lcfg")
    print(packet)
    packet = ecg_app.read_device_configuration_block()
    print(packet)
    packet = ecg_app.delete_device_configuration_block()
    print(packet)

    print("eda_app LCFG")
    packet = eda_app.write_device_configuration_block_from_file("dcb_cfg/eda_dcb.lcfg", eda_app.EDA_LCFG_BLOCK)
    print(packet)
    packet = eda_app.read_device_configuration_block(eda_app.EDA_LCFG_BLOCK)
    print(packet)
    packet = eda_app.delete_device_configuration_block(eda_app.EDA_LCFG_BLOCK)
    print(packet)

    print("eda_app DCFG")
    packet = eda_app.write_device_configuration_block_from_file("dcb_cfg/eda_dcb.dcfg", eda_app.EDA_DCFG_BLOCK)
    print(packet)
    packet = eda_app.read_device_configuration_block(eda_app.EDA_DCFG_BLOCK)
    print(packet)
    packet = eda_app.delete_device_configuration_block(eda_app.EDA_DCFG_BLOCK)
    print(packet)

    print("lt_app")
    packet = lt_app.write_device_configuration_block_from_file("dcb_cfg/gen_blk_dcb.lcfg", lt_app.GENERAL_BLOCK)
    print(packet)
    packet = lt_app.read_device_configuration_block(lt_app.GENERAL_BLOCK)
    print(packet)
    packet = lt_app.delete_device_configuration_block(lt_app.GENERAL_BLOCK)
    print(packet)

    print("adp5360_app")
    packet = adp5360_app.write_device_configuration_block_from_file("dcb_cfg/lt_app_dcb.lcfg")
    print(packet)
    packet = adp5360_app.read_device_configuration_block()
    print(packet)
    packet = adp5360_app.delete_device_configuration_block()
    print(packet)

    print("ppg_app")
    packet = ppg_app.write_device_configuration_block_from_file("dcb_cfg/ppg_dcb.lcfg")
    print(packet)
    packet = ppg_app.read_device_configuration_block()
    print(packet)
    packet = ppg_app.delete_device_configuration_block()
    print(packet)

    print("temp_app")
    packet = temp_app.write_device_configuration_block_from_file("dcb_cfg/temperature_lcfg_dcb.lcfg")
    print(packet)
    packet = temp_app.read_device_configuration_block()
    print(packet)
    packet = temp_app.delete_device_configuration_block()
    print(packet)

    print("user0_app")
    packet = user0_app.write_device_configuration_block_from_file("dcb_cfg/user0_blk_dcb.lcfg")
    print(packet)
    packet = user0_app.read_device_configuration_block()
    print(packet)
    packet = user0_app.delete_device_configuration_block()
    print(packet)

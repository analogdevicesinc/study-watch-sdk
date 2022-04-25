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


def callback_data3(data):
    print(data)


def callback_data4(data):
    print(data)


def callback_data10(data):
    print(data)


def callback_data11(data):
    print(data)


def callback_data12(data):
    print(data)


if __name__ == "__main__":
    sdk = SDK("COM4")
    temp_application = sdk.get_temperature_application()
    temp_application.set_callback(callback_data3, stream=temp_application.STREAM_TEMPERATURE3)
    temp_application.set_callback(callback_data4, stream=temp_application.STREAM_TEMPERATURE4)
    temp_application.set_callback(callback_data10, stream=temp_application.STREAM_TEMPERATURE10)
    temp_application.set_callback(callback_data11, stream=temp_application.STREAM_TEMPERATURE11)
    temp_application.set_callback(callback_data12, stream=temp_application.STREAM_TEMPERATURE12)
    adpd_application = sdk.get_adpd_application()

    temp_application.write_device_configuration_block_from_file("dcb_cfg/temperature_lcfg_dcb.lcfg")
    adpd_application.write_device_configuration_block_from_file("dcb_cfg/temperature_adpd_dcb.dcfg")

    # start and stop stream
    adpd_application.load_configuration(adpd_application.DEVICE_GREEN)
    temp_application.start_sensor()
    temp_application.subscribe_stream()

    temp_application.subscribe_stream(temp_application.STREAM_TEMPERATURE4)
    temp_application.subscribe_stream(temp_application.STREAM_TEMPERATURE10)
    temp_application.subscribe_stream(temp_application.STREAM_TEMPERATURE11)
    temp_application.subscribe_stream(temp_application.STREAM_TEMPERATURE12)
    temp_application.enable_csv_logging("temp3.csv", stream=temp_application.STREAM_TEMPERATURE3)
    temp_application.enable_csv_logging("temp.csv", stream=temp_application.STREAM_TEMPERATURE4)
    temp_application.enable_csv_logging("temp10.csv", stream=temp_application.STREAM_TEMPERATURE10)
    temp_application.enable_csv_logging("temp11.csv", stream=temp_application.STREAM_TEMPERATURE11)
    temp_application.enable_csv_logging("temp12.csv", stream=temp_application.STREAM_TEMPERATURE12)
    time.sleep(10)
    temp_application.unsubscribe_stream(temp_application.STREAM_TEMPERATURE3)
    temp_application.unsubscribe_stream(temp_application.STREAM_TEMPERATURE4)
    temp_application.unsubscribe_stream(temp_application.STREAM_TEMPERATURE10)
    temp_application.unsubscribe_stream(temp_application.STREAM_TEMPERATURE11)
    temp_application.unsubscribe_stream(temp_application.STREAM_TEMPERATURE12)
    temp_application.disable_csv_logging(temp_application.STREAM_TEMPERATURE3)
    temp_application.disable_csv_logging(temp_application.STREAM_TEMPERATURE4)
    temp_application.disable_csv_logging(temp_application.STREAM_TEMPERATURE10)
    temp_application.disable_csv_logging(temp_application.STREAM_TEMPERATURE11)
    temp_application.disable_csv_logging(temp_application.STREAM_TEMPERATURE12)
    temp_application.stop_sensor()

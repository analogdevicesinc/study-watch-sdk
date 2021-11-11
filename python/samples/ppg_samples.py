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


def callback_ppg(data):
    print(data)


def callback_syncppg(data):
    print(data)


def callback_d_agc(data):
    print(data)


def callback_hrv(data):
    print(data)


if __name__ == "__main__":
    sdk = SDK("COM4")
    application = sdk.get_ppg_application()
    application.set_callback(callback_ppg, stream=application.STREAM_PPG)
    application.set_callback(callback_syncppg, stream=application.STREAM_SYNC_PPG)
    application.set_callback(callback_d_agc, stream=application.STREAM_DYNAMIC_AGC)
    application.set_callback(callback_hrv, stream=application.STREAM_HRV)
    adpd_application = sdk.get_adpd_application()

    # quickstart ppg
    adpd_application.load_configuration(adpd_application.DEVICE_GREEN)
    adpd_application.enable_agc([adpd_application.LED_GREEN])
    adpd_application.calibrate_clock(adpd_application.CLOCK_1M_AND_32M)
    application.set_library_configuration(application.LCFG_ID_ADPD4000)
    application.write_library_configuration([[0x4, 0x1210]])
    application.start_sensor()
    application.enable_csv_logging("ppg.csv", stream=application.STREAM_PPG)
    application.enable_csv_logging("syncppg.csv", stream=application.STREAM_SYNC_PPG)
    application.enable_csv_logging("dynamic_agc.csv", stream=application.STREAM_DYNAMIC_AGC)
    application.enable_csv_logging("hrv.csv", stream=application.STREAM_HRV)
    application.subscribe_stream(stream=application.STREAM_PPG)
    application.subscribe_stream(stream=application.STREAM_SYNC_PPG)
    application.subscribe_stream(stream=application.STREAM_DYNAMIC_AGC)
    application.subscribe_stream(stream=application.STREAM_HRV)
    time.sleep(10)
    application.unsubscribe_stream(stream=application.STREAM_PPG)
    application.unsubscribe_stream(stream=application.STREAM_SYNC_PPG)
    application.unsubscribe_stream(stream=application.STREAM_DYNAMIC_AGC)
    application.unsubscribe_stream(stream=application.STREAM_HRV)
    application.disable_csv_logging(stream=application.STREAM_PPG)
    application.disable_csv_logging(stream=application.STREAM_SYNC_PPG)
    application.disable_csv_logging(stream=application.STREAM_DYNAMIC_AGC)
    application.disable_csv_logging(stream=application.STREAM_HRV)
    application.stop_sensor()

    # get sensor status
    packet = application.get_sensor_status()
    print(packet)

    # read lcfg
    packet = application.read_library_configuration([0x20, 0x21])
    print(packet)

    # write lcfg
    packet = application.write_library_configuration([[0x20, 0x1], [0x21, 0x2]])
    print(packet)

    # read dcb
    packet = application.read_device_configuration_block()
    print(packet)

    # write dcb
    packet = application.write_device_configuration_block_from_file("dcb_cfg/ppg_dcb.lcfg")
    print(packet)

    # delete dcb
    packet = application.delete_device_configuration_block()
    print(packet)

    # get lcfg
    packet = application.get_library_configuration()
    print(packet)

    # get supported lcfg ids
    packet = application.get_supported_lcfg_ids()
    print(packet)

    # set lcfg
    packet = application.set_library_configuration(application.LCFG_ID_ADPD4000)
    print(packet)

    # algo version
    packet = application.get_algo_version()
    print(packet)

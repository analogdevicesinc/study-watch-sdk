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


def callback_data(data):
    print(data)


if __name__ == "__main__":
    sdk = SDK("COM4")
    application = sdk.get_adpd_application()
    application.set_callback(callback_data, stream=application.STREAM_ADPD6)

    # quickstart stream
    application.load_configuration(application.DEVICE_GREEN)
    application.calibrate_clock(application.CLOCK_1M_AND_32M)
    application.enable_agc([application.LED_GREEN])
    application.start_sensor()
    application.enable_csv_logging("adpd6.csv", stream=application.STREAM_ADPD6)
    application.subscribe_stream(application.STREAM_ADPD6)
    time.sleep(10)
    application.unsubscribe_stream(application.STREAM_ADPD6)
    application.disable_csv_logging(stream=application.STREAM_ADPD6)
    application.stop_sensor()

    # get version
    packet = application.get_version()
    print(packet)

    # get supported clocks
    packet = application.get_supported_clocks()
    print(packet)

    # get supported devices
    y = application.get_supported_devices()
    print(y)

    # get supported slots
    packet = application.get_supported_slots()
    print(packet)

    # get supported app_ids
    packet = application.get_supported_app_id()
    print(packet)

    # create dcfg
    packet = application.create_device_configuration([[application.SLOT_A, application.APP_ECG],
                                                      [application.SLOT_B, application.APP_ADPD_GREEN]])
    print(packet)

    # get dcfg
    packet = application.get_device_configuration()
    print(packet)

    # load cfg
    packet = application.load_configuration(application.DEVICE_GREEN)
    print(packet)

    # calibrate clock
    packet = application.calibrate_clock(application.CLOCK_1M_AND_32M)
    print(packet)

    # disable agc
    packet = application.disable_agc([application.LED_MWL, application.LED_GREEN])
    print(packet)

    # enable agc
    packet = application.enable_agc([application.LED_GREEN])
    print(packet)

    # get slot
    packet = application.get_slot(application.SLOT_F)
    print(packet)

    # set slot
    packet = application.set_slot(application.SLOT_F, True, 1, 1)
    print(packet)

    # get slot status
    packet = application.get_slot_status(application.SLOT_F)
    print(packet)

    # enable slot
    packet = application.enable_slot(application.SLOT_F)
    print(packet)

    # disable slot
    packet = application.disable_slot(application.SLOT_F)
    print(packet)

    # set decimation factor
    packet = application.set_decimation_factor(2, application.STREAM_ADPD6)
    print(packet)

    # get decimation factor
    packet = application.get_decimation_factor(application.STREAM_ADPD6)
    print(packet)
    # pause stream
    packet = application.pause()
    print(packet)

    # resume stream
    packet = application.resume()
    print(packet)

    # get communication mode
    packet = application.get_communication_mode()
    print(packet)

    # sensor status
    packet = application.get_sensor_status()
    print(packet)

    # read register
    packet = application.read_register([0x20, 0x21, 0x22])
    print(packet)

    # write register
    packet = application.write_register([[0x20, 0x1], [0x21, 0x2]])
    print(packet)

    # sensor status
    packet = application.get_sensor_status()
    print(packet)

    # read dcb
    packet = application.read_device_configuration_block()
    print(packet)

    # write dcb from file
    packet = application.write_device_configuration_block_from_file("dcb_cfg/adpd4000_dcb.dcfg")
    print(packet)

    # delete dcb
    packet = application.delete_device_configuration_block()
    print(packet)

    # set sampling frequency
    packet = application.set_sampling_frequency(100)
    print(packet)

    # write lcfg
    packet = application.write_library_configuration([[0x00, 1]])
    print(packet)

    # read lcfg
    packet = application.read_library_configuration([0x00])
    print(packet)

    # get supported streams
    packet = application.get_supported_streams()
    print(packet)

    # set external stream sampling frequency
    packet = application.set_external_stream_sampling_frequency(50)
    print(packet)

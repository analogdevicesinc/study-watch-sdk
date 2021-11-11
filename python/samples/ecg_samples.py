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
    application = sdk.get_ecg_application()
    application.set_callback(callback_data)

    # quickstart ECG stream
    application.write_library_configuration([[0x0, 1000]])
    application.write_library_configuration([[0x3, 0]])
    application.start_sensor()
    application.enable_csv_logging("ecg.csv")
    application.subscribe_stream()
    time.sleep(20)
    application.unsubscribe_stream()
    application.disable_csv_logging()
    application.stop_sensor()

    # get decimation factor
    packet = application.get_decimation_factor()
    print(packet)

    # set decimation factor
    packet = application.set_decimation_factor(1)
    print(packet)

    # get sensor status
    packet = application.get_sensor_status()
    print(packet)

    # read dcb
    packet = application.read_device_configuration_block()
    print(packet)

    # write dcb
    packet = application.write_device_configuration_block_from_file("dcb_cfg/ecg_dcb.lcfg")
    print(packet)

    # delete dcb
    packet = application.delete_device_configuration_block()
    print(packet)

    # read lcfg
    packet = application.read_library_configuration([0x0, 0x1])
    print(packet)

    # write dcb to lcfg
    packet = application.write_dcb_to_lcfg()
    print(packet)

    # write lcfg
    packet = application.write_library_configuration([[0x0, 1], [0x1, 2]])
    print(packet)

    # get version
    packet = application.get_version()
    print(packet)

    # algo version
    packet = application.get_algo_version()
    print(packet)

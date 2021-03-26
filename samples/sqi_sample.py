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


def adpd_callback_data(data):
    print(data)


def sqi_callback_data(data):
    print(data)


if __name__ == "__main__":
    sdk = SDK("COM4")
    sqi_application = sdk.get_sqi_application(sqi_callback_data)
    adpd_application = sdk.get_adpd_application()
    adpd_application.set_callback(adpd_callback_data, stream=adpd_application.STREAM_ADPD6)

    # Quick start SQI stream
    adpd_application.load_configuration(adpd_application.DEVICE_GREEN)
    adpd_application.write_register([[0x0D, 0x2710]])
    adpd_application.calibrate_clock(adpd_application.CLOCK_1M_AND_32M)

    sqi_application.set_slot(sqi_application.SLOT_F)
    sqi_application.start_sensor()
    sqi_application.subscribe_stream()

    adpd_application.enable_agc([adpd_application.LED_GREEN])
    adpd_application.start_sensor()
    adpd_application.subscribe_stream(stream=adpd_application.STREAM_ADPD6)
    time.sleep(20)
    sqi_application.stop_sensor()
    sqi_application.unsubscribe_stream()
    adpd_application.stop_sensor()
    adpd_application.unsubscribe_stream(stream=adpd_application.STREAM_ADPD6)

    # get sensor status
    packet = sqi_application.get_sensor_status()
    print(packet)

    # get supported slots
    print(sqi_application.get_supported_slots())

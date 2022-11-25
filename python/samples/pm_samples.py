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
import datetime

from adi_study_watch import SDK


def battery_callback(data):
    print(data)


if __name__ == "__main__":
    sdk = SDK("COM8")
    application = sdk.get_pm_application()

    # set datetime
    packet = application.set_datetime(datetime.datetime.now())
    print(packet)

    # get datetime
    packet = application.get_datetime()
    print(packet)

    # system reset
    # packet = application.system_reset()
    # print(packet)

    # hardware reset
    # packet = application.system_hardware_reset()
    # print(packet)

    # dcb status
    packet = application.device_configuration_block_status()
    print(packet)

    # disable touch sensor
    packet = application.disable_touch_sensor()
    print(packet)

    # get chip id
    packet = application.get_chip_id(application.CHIP_ADXL362)
    print(packet)

    # enter boot loader mode
    # packet = application.enter_boot_loader_mode()
    # print(packet)

    packet = application.write_uicr_customer_registers("2022-02-15")
    print(packet)

    packet = application.read_uicr_customer_registers()
    print(packet)

    packet = application.enable_sync_timer()
    print(packet)

    packet = application.start_sync_timer()
    print(packet)

    packet = application.stop_sync_timer()
    print(packet)

    packet = application.disable_sync_timer()
    print(packet)

    packet = application.get_hibernate_mode()
    print(packet)

    packet = application.enable_hibernate_mode(100)
    print(packet)

    packet = application.get_hibernate_mode()
    print(packet)

    packet = application.disable_hibernate_mode()
    print(packet)

    packet = application.get_hibernate_mode()
    print(packet)

    packet = application.get_existing_connection()
    print(packet)

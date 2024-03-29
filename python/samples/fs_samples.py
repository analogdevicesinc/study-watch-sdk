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


def stream_file(data, total_size, progress):
    print(data)
    print(total_size, progress)


def stream_progress_file(event_type, total_size, progress):
    print(event_type, total_size, progress)


if __name__ == "__main__":
    sdk = SDK("COM4", debug=True)
    application = sdk.get_fs_application()
    application1 = sdk.get_low_touch_application()

    # get supported streams
    packet = application.get_supported_streams()
    print(packet)

    # download file without progress callback
    application.download_file("03124671.LOG", download_to_file=True, display_progress=True)

    # download file with progress callback
    application.download_file("B1320595.LOG", download_to_file=True,
                              display_progress=True, progress_callback=stream_progress_file)

    application.download_file("514199C5.LOG", download_to_file=True, display_progress=True,
                              continue_download="514199C5.LOG_RAW")

    # convert log file to csv
    SDK.convert_log_to_csv("512190CB.LOG")

    # stream file
    application.stream_file("80A1BE7E.LOG", stream_file)
    # time.sleep(20)  # wait for stream to finish

    # Write config file for LT application
    # enable config log
    packet = application.enable_config_log()
    print(packet)

    # write cfg
    pkt = application.write_config_file("dcb_cfg/gen_blk_dcb.lcfg")
    print(pkt)

    # disable config log
    packet = application.disable_config_log()
    print(packet)

    # delete config file
    packet = application.delete_config_file()
    print(packet)
    # LT config file commands end.

    # format
    packet = application.format()
    print(packet)

    # ls - list all files
    packet = application.ls()
    print(packet)

    # get volume info
    packet = application.volume_info()
    print(packet)

    # mount
    packet = application.mount()
    print(packet)

    # start logging
    packet = application.start_logging()
    print(packet)

    # stop logging
    packet = application.stop_logging()
    print(packet)

    # subscribe stream
    packet = application.subscribe_stream(application.STREAM_ADXL)
    print(packet)

    # unsubscribe stream
    packet = application.unsubscribe_stream(application.STREAM_ADXL)
    print(packet)

    # download file chunk
    packet = application.download_file_chunk("12104AD0.LOG", 0, 0, 0, 1)
    print(packet)

    # get file count
    packet = application.get_file_count()
    print(packet)

    # get status
    packet = application.get_status()
    print(packet)

    # get stream status
    packet = application.get_stream_status(application.STREAM_ADXL)
    print(packet)

    # inject key value
    packet = application.inject_key_value_pair("4562")
    print(packet)

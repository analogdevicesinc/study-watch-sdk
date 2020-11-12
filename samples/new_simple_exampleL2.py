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


def callback_adxl(seq, data):
    for d in data:
        print("t:{} x:{} y:{} z:{}".format(d.timestamp, d.x, d.y, d.z))


def callback_adpd400(seq, data):
    for d in data:
        print("source: {}\nDark: {}\nSignal: {}\nImpulse: {}\nChannel Number: {}\nTimestamp: {}\n".format(
            d.adpd_stream, d.dark, d.signal, d.impulse, d.channel_num, d.timestamp))


def callback_temperature(seq, data):
    for d in data:
        print("t: {} Skin temp: {} Ambient temp: {}\n".format(d.timestamp, d.temp_skin, d.temp_ambient))


def callback_pedometer(seq, data):
    for d in data:
        print("TimeStamp: {} NumSteps:{} AlgoStatus: {}\n".format(d.TimeStamp, d.NumSteps, d.AlgoStatus))


def callback_ppg(seq, data):
    for d in data:
        print("t:{} adpdlibstate: {} hr: {} confidence:{} hr_type: {} rr interval: {}\n".format(d.timestamp,
                                                                                                d.adpdlibstate,
                                                                                                d.hr, d.confidence,
                                                                                                d.hr_type,
                                                                                                d.rr_interval))


def callback_syncppg(seq, data):
    for d in data:
        print("t:{} PPG: {} X: {} Y:{} Z: {}\n".format(d.timestamp, d.ppg, d.x, d.y, d.z))


def callback_ecg(seq, data):
    for d in data:
        print("t:{} data:{} HR:{} leads_off:{} data_type:{}\n".format(d.timestamp, d.ecg_data, d.HR, d.leadsoff,
                                                                      d.datatype))


def callback_eda(seq, data):
    for d in data:
        print(
            "t:{} admittance_real: {} admittance_img: {} impedance_real:{} impedance_img: {} admittance_magnitude: {} admittance_phase: {} impedance_magnitude: {} impedance_phase: {}\n".format(
                d.timestamp, d.admittance_real, d.admittance_img, d.impedance_real, d.impedance_img,
                d.admittance_magnitude, d.admittance_phase, d.impedance_magnitude, d.impedance_phase))


def callback_bcm(seq, data):
    for d in data:
        print(
            "t:{} impedance_real:{} impedance_imgdata:{} is_finger_on_leads:{} signal_stability:{} impedance_module:{} impedance_phase:{} admittance_module:{} admittance_phase:{} \n".format(
                d.timestamp, d.impedance_real, d.impedance_img, d.is_finger_on_leads, d.signal_stability,
                d.impedance_magnitude, d.impedance_phase, d.admittance_magnitude, d.admittance_phase))


def callback_battery(data):
    for d in data:
        print("timestamp: {} bat_chrg_stat: {} bat_lvl:{} bat_mv: {} bat_temp: {}\n".format(d.timestamp,
                                                                                            d.bat_chrg_stat,
                                                                                            d.bat_lvl, d.bat_mv,
                                                                                            d.bat_temp))


global fs_application


# fs_ls_callback
def callback_fs_ls(data):
    for d in data:
        if d.status != fs_application.FILE_SYS_END_OF_DIR:
            if d.filetype == fs_application.FS_SYS_IS_FILE:
                file = "FILE"
            else:
                file = "DIRECTORY"

            print("full_file_name: {}\n"
                  "filetype      : {}\n"
                  "filesize      : {}\n".format(d.filename, file, d.filesize))
        else:
            print("END OF DIRECTORY")


# fs_stream_callback
def callback_fs(data):
    print(data)
    for d in data:
        byte_list = []
        for x in range(0, d.byte_Stream.size()):
            byte_list.append((d.byte_Stream[x]))
        print(byte_list)


if __name__ == "__main__":
    # sdk = SDK("COM6", is_ble=True)
    sdk = SDK("COM4")

    # adxl application
    adxl_application = sdk.get_adxl_application(callback_adxl)
    # print(adxl_application.write_dcb_config_file("./dcb_files/adxl_dcb.dcfg"))
    # print(adxl_application.dcb_delete_config())
    adxl_application.start_default_stream()
    time.sleep(3)
    adxl_application.stop_default_stream()

    # adpd4000 application
    adpd4000_application = sdk.get_adpd4000_application(callback_adpd400)
    # print(adpd4000_application.write_dcb_config_file("./dcb_files/adpd4000_dcb.dcfg"))
    print(adpd4000_application.dcb_delete_config())
    print(adpd4000_application.set_adpd4k_fs(50))
    adpd4000_application.start_default_stream()
    time.sleep(2)
    adpd4000_application.stop_default_stream()
    # print(adpd4000_application.get_stream_status(adpd4000_application.ADPD_STREAM_G))
    # adpd4000_application.start_default_stream_g()
    # time.sleep(2)
    # adpd4000_application.stop_default_stream_g()
    # adpd4000_application.start_default_stream_r()
    # time.sleep(2)
    # adpd4000_application.stop_default_stream_r()
    # adpd4000_application.start_default_stream_ir()
    # time.sleep(2)
    # adpd4000_application.stop_default_stream_ir()
    # adpd4000_application.start_default_stream_b()
    # time.sleep(2)
    # adpd4000_application.stop_default_stream_b()

    # Temperature application
    temperature_application = sdk.get_temperature_application(callback_temperature)
    temperature_application.start_default_stream()
    time.sleep(3)
    temperature_application.stop_default_stream()

    # Pedometer Application
    pedometer_application = sdk.get_pedometer_application(callback_pedometer)
    pedometer_application.start_default_stream()
    time.sleep(10)
    pedometer_application.stop_default_stream()

    # ppg application
    ppg_application = sdk.get_ppg_application(callback_ppg, callback_syncppg)
    ppg_application.start_default_stream()
    time.sleep(10)
    ppg_application.stop_default_stream()

    # ecg Application
    ecg_application = sdk.get_ecg_application(callback_ecg)
    ecg_application.start_default_stream()
    time.sleep(10)
    ecg_application.stop_default_stream()

    # eda application
    eda_application = sdk.get_eda_application(callback_eda)
    eda_application.start_default_stream()
    time.sleep(10)
    eda_application.stop_default_stream()

    # bcm Application
    bcm_application = sdk.get_bcm_application(callback_bcm)
    bcm_application.start_default_stream()
    time.sleep(10)
    bcm_application.stop_default_stream()

    # pm application
    pm_application = sdk.get_pm_application(callback_battery)
    pm_application.start_default_stream()
    time.sleep(10)
    pm_application.stop_default_stream()

    # fs application
    fs_application = sdk.get_fs_application()
    fs_application.start_default_stream(fs_application.ADXL_STREAM)
    time.sleep(10)
    fs_application.stop_default_stream(fs_application.ADXL_STREAM)

    fs_application.fs_ls(callback_fs_ls)
    # 102123F2.LOG convert it to 102123F2_chunk.LOG
    fs_application.fs_get("102123F2_chunk.LOG", callback_fs)

    sdk.quit()

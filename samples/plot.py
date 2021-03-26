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

from matplotlib import pyplot
from matplotlib.animation import FuncAnimation


# plotting adxl using matplotlib
def callback_data(data, adxl_time, adxl_x, adxl_y, adxl_z):
    print(data)
    # plotting only 200 points and removing prev data points.
    if len(adxl_time) > 200:
        del adxl_time[:5]
        del x_data[:5]
        del y_data[:5]
        del z_data[:5]

    for value in data["payload"]["stream_data"]:
        adxl_time.append(value["timestamp"])
        adxl_x.append(value["x"])
        adxl_y.append(value["y"])
        adxl_z.append(value["z"])


# init
x_data, y_data, z_data, time = [], [], [], []
figure = pyplot.figure()
x_line, = pyplot.plot(time, x_data, '-')
y_line, = pyplot.plot(time, x_data, '-')
z_line, = pyplot.plot(time, x_data, '-')


# refreshing plot
def update(frame, plot_time, plot_x_data, plot_y_data, plot_z_data):
    x_line.set_data(plot_time, plot_x_data)
    y_line.set_data(plot_time, plot_y_data)
    z_line.set_data(plot_time, plot_z_data)
    figure.gca().relim()
    figure.gca().autoscale_view()
    return x_line, y_line, z_line


sdk = SDK("COM4")

application = sdk.get_adxl_application()
# setting callback with some reference list to plot from
application.set_callback(callback_data, args=(time, x_data, y_data, z_data))

# start sensor
application.start_sensor()
application.subscribe_stream()

# plot started
animation = FuncAnimation(figure, update, interval=10, fargs=(time, x_data, y_data, z_data))
pyplot.show()

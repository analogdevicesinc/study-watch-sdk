@REM Install Applications wavetool and place "dfuse_fw_update.bat" file in the below installation directory and execute it
@REM Installation Dir (Default): "C:\Analog Devices\ADI_ApplicationsWaveTool-RelX.X.X)\etc\Firmware"

@ECHO OFF
set mypath=%cd%
@echo %mypath%
PowerShell.exe -Command "& '%mypath%/DfuseCommand/DfuSeCommand.exe' -c --de 0 -d --v --fn '%mypath%/Adpd_M4_uC.dfu'"
PAUSE
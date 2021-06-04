# Log Converter Application
This application can be used for converting log files downloaded from VSM Watch's NAND Flash. The application supports functioning either from the application's GUI or command line.

Unzip the contents of the LogConverter.zip file into your computer and follow the instructions given below for converting log files.

## Log Converter GUI
1) From the directory extracted from LogConverter.zip, run LogConverter.exe
2) Brose the *.LOG file using the Browse button
3) Tick the check-boxes 'M2M2 to JSON' and 'JSON to CSV'
4) Click Convert button
5) The converted log files will be placed in a new folder created with the name of *.LOG file in the same direcotry as the input *.LOG file.

## Log Converter CLI
1) From the directory extracted from LogConverter.zip, run the below command
   `LogConverter.exe <log_file_path.LOG>`
2) The converted log files will be placed in a new folder created with the name of *.LOG file in the same direcotry as the input *.LOG file
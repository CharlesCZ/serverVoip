# *Voip P2P Application*

Overview
- 
The project of a Voip(Voice over Internet Protocol) with usage of Wireless LAN, SIP(Session Initiation Protocol), SQLite, Java8, JavaFx 

How to run on Windows10 & 7
-
1. Firstly you have to install version of Java8, better between 1.8.0_181 and jdk1.8.0_211 from official Oracle site
2. Add environmental variable of your JDK or JRE path( for example C:\Program Files\Java\jdk1.8.0_181\bin)
2. Open CMD and wrtie java -version to check if environmental variable is set
4. Download SQLite easiest way is to use A bundle of command-line tools for managing SQLite database files, including the command-line shell program, the sqldiff.exe program, and the sqlite3_analyzer.exe program.
                  from official site https://www.sqlite.org/download.html
5. Copy of path of unzipped SQLite database path and set as an environmental variable
6. Open CMD and write sqlite3
 to close database write .exit
More on https://sqlite.org/cli.html
7. Turn off all network adapters except one
8. If everything is set then go to package with application
9. If your double clicking on icon is not working. You can open it via CMD and write: 
cd _path of your voip application folder_ 
after it write
java -jar voip.jar
10. You can also search for regedit on your system and then go to HKEY_CLASSES_ROOT\jarfile\shell\open\command and check if there path is "C:\Program Files\Java\jdk1.8.0_181\bin\javaw.exe" -jar "%1" %*
you should have jdk1.8.0_181 or higher but up to java8

License
-
MIT

Credits
-
* Cezary Czekalski


The project was an assignment as a part of studies course. It was conducted during IP Telephony course held by the
Institute of Control, Robotics and Information Engineering, Poznan University of Technology.

Supervisor: Michał Apollinarski
@echo off
rem
rem Copyright 2011, 2012, Oracle and/or its affiliates. All rights reserved.
rem Oracle and Java are registered trademarks of Oracle and/or its 
rem affiliates. Other names may be trademarks of their respective owners.
rem UNIX is a registered trademark of The Open Group.
rem 
rem This software and related documentation are provided under a license 
rem agreement containing restrictions on use and disclosure and are 
rem protected by intellectual property laws. Except as expressly permitted 
rem in your license agreement or allowed by law, you may not use, copy, 
rem reproduce, translate, broadcast, modify, license, transmit, distribute, 
rem exhibit, perform, publish, or display any part, in any form, or by any 
rem means. Reverse engineering, disassembly, or decompilation of this 
rem software, unless required by law for interoperability, is prohibited.
rem The information contained herein is subject to change without notice 
rem and is not warranted to be error-free. If you find any errors, please 
rem report them to us in writing.
rem U.S. GOVERNMENT END USERS: Oracle programs, including any operating 
rem system, integrated software, any programs installed on the hardware, 
rem and/or documentation, delivered to U.S. Government end users are 
rem "commercial computer software" pursuant to the applicable Federal 
rem Acquisition Regulation and agency-specific supplemental regulations. 
rem As such, use, duplication, disclosure, modification, and adaptation 
rem of the programs, including any operating system, integrated software, 
rem any programs installed on the hardware, and/or documentation, shall be 
rem subject to license terms and license restrictions applicable to the 
rem programs. No other rights are granted to the U.S. Government.
rem This software or hardware is developed for general use in a variety 
rem of information management applications. It is not developed or 
rem intended for use in any inherently dangerous applications, including 
rem applications that may create a risk of personal injury. If you use 
rem this software or hardware in dangerous applications, then you shall 
rem be responsible to take all appropriate fail-safe, backup, redundancy, 
rem and other measures to ensure its safe use. Oracle Corporation and its 
rem affiliates disclaim any liability for any damages caused by use of this 
rem software or hardware in dangerous applications.
rem This software or hardware and documentation may provide access to or 
rem information on content, products, and services from third parties. 
rem Oracle Corporation and its affiliates are not responsible for and 
rem expressly disclaim all warranties of any kind with respect to 
rem third-party content, products, and services. Oracle Corporation and 
rem its affiliates will not be responsible for any loss, costs, or damages 
rem incurred due to your access to or use of third-party content, products, 
rem or services.

setlocal

call %~dp0..\config\script\set_environment.bat

if not defined ENDECA_ROOT (
  echo ERROR: ENDECA_ROOT is not set.
  exit /b 1
)

set JAVA=java
if exist %ENDECA_ROOT%\j2sdk\bin\java.exe (
  set JAVA=%ENDECA_ROOT%\j2sdk\bin\java.exe
)

set APP_CONFIG_XML=%~dp0..\config\script\AppConfig.xml
if not exist %APP_CONFIG_XML% (
  echo ERROR: Cannot find AppConfig.xml at %APP_CONFIG_XML%
  exit /b 1
)

rem Add jars for EAC client
rem Do not include orawsdl.jar from ENDECA_ROOT, as it is not compatible with CXF.  
rem We use instead the version in RS_JAVA_LIB_DIR.
set CLASSPATH=%ENDECA_ROOT%\lib\java\eacclient.jar
set CLASSPATH=%CLASSPATH%;%ENDECA_ROOT%\lib\java\jaxrpc.jar
set CLASSPATH=%CLASSPATH%;%ENDECA_ROOT%\lib\java\mail.jar
set CLASSPATH=%CLASSPATH%;%ENDECA_ROOT%\lib\java\saaj.jar
set CLASSPATH=%CLASSPATH%;%ENDECA_ROOT%\lib\java\axis.jar
set CLASSPATH=%CLASSPATH%;%ENDECA_ROOT%\lib\java\commons-discovery-0.2.jar
set CLASSPATH=%CLASSPATH%;%ENDECA_ROOT%\lib\java\commons-logging-1.0.4.jar

rem Add the script folder of the application to the classpath
set CLASSPATH=%CLASSPATH%;%~dp0\..\config\script

rem Add all zip and jar files that are in the ..\config\lib\java directory
for %%i in ("%~dp0\..\config\lib\java\*.jar") do call set CLASSPATH=%%CLASSPATH%%;%%i%%

rem support for recovering of failed partial update runs
for %%i in (%RS_JAVA_LIB_DIR%\*.jar) do call set CLASSPATH=%%CLASSPATH%%;%%i%%

set JAVA_ARGS=%JAVA_ARGS% "-Djava.util.logging.config.file=%~dp0..\config\script\logging.properties"

set CONTROLLER_ARGS=--app-config AppConfig.xml

set OVERRIDE_PROPERTIES=%~dp0..\config\script\environment.properties
if exist %OVERRIDE_PROPERTIES% (
  set OVERRIDE_ARG=--config-override environment.properties
)
set CONTROLLER_ARGS=%CONTROLLER_ARGS% %OVERRIDE_ARG%

"%JAVA%" %JAVA_ARGS% -cp "%CLASSPATH%" com.endeca.soleng.eac.toolkit.Controller %CONTROLLER_ARGS% %*

if not %ERRORLEVEL%==0 goto :FAILURE
endlocal
exit /b 0

:FAILURE
endlocal
exit /b 1




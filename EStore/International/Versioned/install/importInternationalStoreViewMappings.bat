@echo off
setlocal

REM Check for DYNAMO_HOME
if "%DYNAMO_HOME%" == "" goto :dynamohomeerror
if NOT EXIST %DYNAMO_HOME%\nul goto :dynamohomedirerror

REM 
REM Convert forward slashes to backslashes in DYNAMO_HOME before trying to
REM cd there, just in case the user is running Cygwin or something like it 
REM and using Unix-style paths on a Windows machine.
REM 

set DYNAMO_HOME=%DYNAMO_HOME:/=\%
if NOT EXIST %DYNAMO_HOME%\META-INF\MANIFEST.MF goto :dynamohomeerror
goto :homedone

:dynamohomeerror
echo ** Invalid setting of DYNAMO_HOME: %DYNAMO_HOME%
echo **
goto :pauseexit

:dynamohomedirerror
echo ** Invalid setting of DYNAMO_HOME: %DYNAMO_HOME% does not exist
echo **
goto :pauseexit

:homedone
  cd /d %DYNAMO_HOME%

  echo.
  echo ************************************
  echo Importing International viewmappings
  echo ************************************

  echo.
  echo Importing Store.EStore.International.Versioned/install/data/viewmapping.xml into /atg/web/viewmapping/ViewMappingRepository

  call %DYNAMO_HOME%\bin\startSQLRepository -m DCS-UI -m Store.EStore.International.Versioned -repository /atg/web/viewmapping/ViewMappingRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/EStore/International/Versioned/install/data/viewmapping.xml -workspace initialVwMapping -comment initialVwMapping -user publishing

  goto :endofscript

:pauseexit
pause

:endofscript
if exist endofscript rmdir endofscript
  
endlocal

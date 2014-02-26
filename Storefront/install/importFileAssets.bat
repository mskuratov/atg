@echo off
 setlocal
 

 REM Check for DYNAMO_HOME
 if "%DYNAMO_HOME%" == "" goto :dynamohomeerror
 if NOT EXIST %DYNAMO_HOME%\nul goto :dynamohomedirerror

 REM Check for JAVA_HOME
 if "%JAVA_HOME%" == "" goto :javahomeerror
 if NOT EXIST %JAVA_HOME%\nul goto :javahomedirerror

 set i18n=
 
 IF [%1] == [-i18n] set i18n=true

 REM 
 REM Convert forward slashes to backslashes in DYNAMO_HOME before trying to
 REM cd there, just in case the user is running Cygwin or something like it 
 REM and using Unix-style paths on a Windows machine.
 REM 

 set DYNAMO_HOME=%DYNAMO_HOME:\=\%
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

:javahomeerror
 echo ** Invalid setting of JAVA_HOME: %JAVA_HOME%
 echo **
 goto :pauseexit

:javahomedirerror
 echo ** Invalid setting of JAVA_HOME: %JAVA_HOME% does not exist
 echo **
 goto :pauseexit

:homedone
  cd %DYNAMO_HOME%

  echo *************************
  echo importing targeters
  echo *************************

  echo.
  echo Importing targeters into \atg\epub\file\PublishingFileRepository
 
  
  copy %DYNAMO_HOME%\..\Publishing\base\bin\startRepositoryLoader.bat %DYNAMO_HOME%\bin 
 
  cd %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront
  rmdir /S /Q tempconfig
  mkdir tempconfig
  cd tempconfig
  %JAVA_HOME%\bin\jar xf %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\NoPublishing\config\config.jar
  
  cd %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront
  rmdir /S /Q temp
  mkdir temp
  mkdir temp\atg
  mkdir temp\atg\registry
  mkdir temp\atg\registry\RepositoryTargeters
  mkdir temp\atg\registry\RepositoryTargeters\ProductCatalog

  cd temp\atg\registry\
  
  copy %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\tempconfig\atg\registry\RepositoryTargeters\ProductCatalog\*.properties RepositoryTargeters\ProductCatalog

 

  cd %DYNAMO_HOME%


  call bin\startRepositoryLoader.bat -m Publishing.base  -initialService  /atg/dynamo/service/loader/RLInitial -dir %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp -update -filemapping /atg/epub/file/typemappers/TargeterTypeMapping -foldermapping /atg/epub/file/typemappers/FileFolderTypeMapping
  
  
  rmdir /S/Q  %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp

 echo.

  echo *************************
  echo importing content groups
  echo *************************

  echo.

  cd %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront
    
 rmdir /S/Q temp
 mkdir temp
 mkdir temp\atg
 mkdir temp\atg\registry
 mkdir  %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp\atg\registry\RepositoryGroups

 copy  %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\tempconfig\atg\registry\RepositoryGroups\*.properties  %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp\atg\registry\RepositoryGroups

 cd %DYNAMO_HOME%
 
  call bin\startRepositoryLoader -m Publishing.base -initialService /atg/dynamo/service/loader/RLInitial -dir %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp -update -filemapping /atg/epub/file/typemappers/ContentGroupTypeMapping -foldermapping /atg/epub/file/typemappers/FileFolderTypeMapping


 cd %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront
 rmdir /S/Q temp


 echo.

  echo *************************
  echo importing segments
  echo *************************

  echo.

  cd %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront
    
 rmdir /S/Q temp
 mkdir temp
 mkdir temp\atg
 mkdir temp\atg\registry
 mkdir  %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp\atg\registry\RepositoryGroups
 mkdir %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp\atg\registry\RepositoryGroups\UserProfiles

 copy  %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\tempconfig\atg\registry\RepositoryGroups\UserProfiles\*.properties  %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp\atg\registry\RepositoryGroups\UserProfiles

 cd %DYNAMO_HOME%
 
  call bin\startRepositoryLoader -m Publishing.base -initialService /atg/dynamo/service/loader/RLInitial -dir %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp -update -filemapping /atg/epub/file/typemappers/SegmentTypeMapping -foldermapping /atg/epub/file/typemappers/FileFolderTypeMapping


 cd %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront
 rmdir /S/Q temp

echo.

  echo *************************
  echo importing Slots
  echo *************************

  echo.

  cd %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront
    
 rmdir /S/Q temp
 mkdir temp
 mkdir temp\atg
 mkdir temp\atg\registry
 mkdir temp\atg\registry\Slots

 copy  tempconfig\atg\registry\Slots\*.properties temp\atg\registry\Slots

 cd %DYNAMO_HOME%
 
  call bin\startRepositoryLoader -m Publishing.base -initialService /atg/dynamo/service/loader/RLInitial -dir %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp -update -filemapping /atg/epub/file/typemappers/SlotTypeMapping -foldermapping /atg/epub/file/typemappers/FileFolderTypeMapping


 cd %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront
 rmdir /S/Q temp

echo.

echo *************************
echo importing Scenario
echo *************************

echo.

cd %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront

mkdir temp
mkdir temp\atg
mkdir temp\atg\registry
mkdir temp\atg\registry\data
mkdir temp\atg\registry\data\scenarios
mkdir temp\atg\registry\data\scenarios\store
mkdir temp\atg\registry\data\scenarios\store\abandonedorders
mkdir temp\atg\registry\data\scenarios\store\global
mkdir temp\atg\registry\data\scenarios\store\homepage
mkdir temp\atg\registry\data\scenarios\store\orders

copy  %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\tempconfig\atg\registry\data\scenarios\store\abandonedorders\*.sdl %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp\atg\registry\data\scenarios\store\abandonedorders
copy  %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\tempconfig\atg\registry\data\scenarios\store\global\*.sdl %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp\atg\registry\data\scenarios\store\global
copy  %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\tempconfig\atg\registry\data\scenarios\store\homepage\*.sdl %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp\atg\registry\data\scenarios\store\homepage
copy  %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\tempconfig\atg\registry\data\scenarios\store\orders\*.sdl %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp\atg\registry\data\scenarios\store\orders

cd %DYNAMO_HOME%

call bin\startRepositoryLoader -m Publishing.base -initialService /atg/dynamo/service/loader/RLInitial -dir %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp -update -filemapping /atg/epub/file/typemappers/ScenarioTypeMapping -foldermapping /atg/epub/file/typemappers/FileFolderTypeMapping


cd %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront
rmdir /S/Q temp
rmdir /S/Q tempconfig

del %DYNAMO_HOME%\bin\startRepositoryLoader.bat


:-i18n
  IF not [%i18n%] == [true] goto :endofscript 

  cd %DYNAMO_HOME%

  echo *************************
  echo importing international targeters
  echo *************************

  echo.
  echo Importing targeters into \atg\epub\file\PublishingFileRepository
 
  
  copy %DYNAMO_HOME%\..\Publishing\base\bin\startRepositoryLoader.bat %DYNAMO_HOME%\bin 
 
  cd %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront
  rmdir /S /Q tempconfig
  mkdir tempconfig
  cd tempconfig
  %JAVA_HOME%\bin\jar xf %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\NoPublishing\International\config\config.jar
  
  cd %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront
  rmdir /S /Q temp
  mkdir temp
  mkdir temp\atg
  mkdir temp\atg\registry
  mkdir temp\atg\registry\RepositoryTargeters
  mkdir temp\atg\registry\RepositoryTargeters\ProductCatalog

  cd temp\atg\registry\
  
  copy %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\tempconfig\atg\registry\RepositoryTargeters\ProductCatalog\*.properties RepositoryTargeters\ProductCatalog

 

  cd %DYNAMO_HOME%


  call bin\startRepositoryLoader.bat -m Publishing.base  -initialService  /atg/dynamo/service/loader/RLInitial -dir %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp -update -filemapping /atg/epub/file/typemappers/TargeterTypeMapping -foldermapping /atg/epub/file/typemappers/FileFolderTypeMapping
  
  
  rmdir /S/Q  %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp

  echo.

  echo *************************
  echo importing international segments
  echo *************************

  echo.

  cd %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront
    
  rmdir /S/Q temp
  mkdir temp
  mkdir temp\atg
  mkdir temp\atg\registry
  mkdir  %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp\atg\registry\RepositoryGroups
  mkdir %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp\atg\registry\RepositoryGroups\UserProfiles

  copy  %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\tempconfig\atg\registry\RepositoryGroups\UserProfiles\*.properties  %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp\atg\registry\RepositoryGroups\UserProfiles

  cd %DYNAMO_HOME%
 
  call bin\startRepositoryLoader -m Publishing.base -initialService /atg/dynamo/service/loader/RLInitial -dir %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront\temp -update -filemapping /atg/epub/file/typemappers/SegmentTypeMapping -foldermapping /atg/epub/file/typemappers/FileFolderTypeMapping


  cd %DYNAMO_HOME%\..\CommerceReferenceStore\Store\Storefront
  rmdir /S/Q temp

  rmdir /S/Q tempconfig

  del %DYNAMO_HOME%\bin\startRepositoryLoader.bat

goto :endofscript

:pauseexit
 pause

:endofscript
if exist endofscript rmdir endofscript

 endlocal

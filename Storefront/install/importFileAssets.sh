#!/bin/sh

# Check for DYNAMO_HOME
if [ "x${DYNAMO_HOME}" != "x" ] ; then
  if [ -r ${DYNAMO_HOME}/META-INF/MANIFEST.MF ] ; then
    cd ${DYNAMO_HOME}
  else
    echo "Invalid setting of DYNAMO_HOME: ${DYNAMO_HOME}"
    exit 1
  fi
else
  echo "DYNAMO_HOME must be set prior to running this script"
  exit 1
fi

# Check for JAVA_HOME
if [ "x${JAVA_HOME}" = "x" ] ; then
  echo "JAVA_HOME must be set prior to running this script"

  exit 1
elif [ ! -d ${JAVA_HOME} ] ; then
  echo "Invalid setting of JAVA_HOME. Directory ${JAVA_HOME} does not exist."
  exit 1
fi

i18n=false

if [ $param = "-i18n" ]; then
    i18n=true
fi



 echo "Importing targeters into /atg/epub/file/PublishingFileRepository"
 
  echo "*************************"
  echo  "importing targeters"
  echo "*************************"
  
 cp ${DYNAMO_HOME}/../Publishing/base/bin/startRepositoryLoader ${DYNAMO_HOME}/bin
 chmod +x ${DYNAMO_HOME}/bin/startRepositoryLoader
  

cd ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/
rm -rf tempconfig
mkdir tempconfig
cd tempconfig
${JAVA_HOME}/bin/jar xf ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/NoPublishing/config/config.jar

cd ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront
rm -rf temp
mkdir temp
mkdir temp/atg
mkdir temp/atg/registry
mkdir temp/atg/registry/RepositoryTargeters
mkdir temp/atg/registry/RepositoryTargeters/ProductCatalog

cd temp/atg/registry/
  
cp ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/tempconfig/atg/registry/RepositoryTargeters/ProductCatalog/*.properties RepositoryTargeters/ProductCatalog

cd ${DYNAMO_HOME}

bin/startRepositoryLoader -m Publishing.base  -initialService /atg/dynamo/service/loader/RLInitial -dir ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp -update -filemapping /atg/epub/file/typemappers/TargeterTypeMapping -foldermapping /atg/epub/file/typemappers/FileFolderTypeMapping

rm -rf ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp

echo "*************************"
echo "importing content groups"
echo "*************************"

cd ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront
mkdir temp
mkdir temp/atg
mkdir temp/atg/registry
mkdir  ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp/atg/registry/RepositoryGroups

cp  ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/tempconfig/atg/registry/RepositoryGroups/*.properties  ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp/atg/registry/RepositoryGroups

cd ${DYNAMO_HOME}

bin/startRepositoryLoader -m Publishing.base  -initialService /atg/dynamo/service/loader/RLInitial -dir ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp -update -filemapping /atg/epub/file/typemappers/ContentGroupTypeMapping -foldermapping /atg/epub/file/typemappers/FileFolderTypeMapping

rm -rf  ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp

echo "*************************"
echo "importing segments"
echo "*************************"

cd ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront
mkdir temp
mkdir temp/atg
mkdir temp/atg/registry
mkdir  ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp/atg/registry/RepositoryGroups
mkdir ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp/atg/registry/RepositoryGroups/UserProfiles


cp  ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/tempconfig/atg/registry/RepositoryGroups/UserProfiles/*.properties  ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp/atg/registry/RepositoryGroups/UserProfiles

cd ${DYNAMO_HOME}

bin/startRepositoryLoader -m Publishing.base  -initialService /atg/dynamo/service/loader/RLInitial -dir ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp -update -filemapping /atg/epub/file/typemappers/SegmentTypeMapping -foldermapping /atg/epub/file/typemappers/FileFolderTypeMapping

rm -rf  ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp

echo "*************************"
echo "importing Slots"
echo "*************************"

cd ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront
mkdir temp
mkdir temp/atg
mkdir temp/atg/registry
mkdir temp/atg/registry/Slots

cp  tempconfig/atg/registry/Slots/*.properties temp/atg/registry/Slots

cd ${DYNAMO_HOME}

bin/startRepositoryLoader -m Publishing.base  -initialService /atg/dynamo/service/loader/RLInitial -dir ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp -update -filemapping /atg/epub/file/typemappers/SlotTypeMapping -foldermapping /atg/epub/file/typemappers/FileFolderTypeMapping

rm -rf  ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp

echo "*************************"
echo "importing Scenario"
echo "*************************"

cd ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront

mkdir temp
mkdir temp/atg
mkdir temp/atg/registry
mkdir temp/atg/registry/data
mkdir temp/atg/registry/data/scenarios
mkdir temp/atg/registry/data/scenarios/store
mkdir temp/atg/registry/data/scenarios/store/abandonedorders
mkdir temp/atg/registry/data/scenarios/store/global
mkdir temp/atg/registry/data/scenarios/store/homepage
mkdir temp/atg/registry/data/scenarios/store/orders

cp ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/tempconfig/atg/registry/data/scenarios/store/abandonedorders/*.sdl ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp/atg/registry/data/scenarios/store/abandonedorders
cp ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/tempconfig/atg/registry/data/scenarios/store/global/*.sdl ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp/atg/registry/data/scenarios/store/global
cp ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/tempconfig/atg/registry/data/scenarios/store/homepage/*.sdl ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp/atg/registry/data/scenarios/store/homepage
cp ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/tempconfig/atg/registry/data/scenarios/store/orders/*.sdl ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp/atg/registry/data/scenarios/store/orders

cd ${DYNAMO_HOME}

bin/startRepositoryLoader -m Publishing.base -initialService /atg/dynamo/service/loader/RLInitial -dir ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp -update -filemapping /atg/epub/file/typemappers/ScenarioTypeMapping -foldermapping /atg/epub/file/typemappers/FileFolderTypeMapping

rm -rf  ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp
rm -rf  ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/tempconfig

rm -f ${DYNAMO_HOME}/bin/startRepositoryLoader


if [ $i18n = "true" ]; then

 echo "Importing targeters into /atg/epub/file/PublishingFileRepository"
 
 echo "*************************"
 echo  "importing international targeters"
 echo "*************************"
  
 cp ${DYNAMO_HOME}/../Publishing/base/bin/startRepositoryLoader ${DYNAMO_HOME}/bin
 chmod +x ${DYNAMO_HOME}/bin/startRepositoryLoader
  

 cd ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/
 rm -rf tempconfig
 mkdir tempconfig
 cd tempconfig
 ${JAVA_HOME}/bin/jar xf ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/NoPublishing/International/config/config.jar

 cd ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront
 rm -rf temp
 mkdir temp
 mkdir temp/atg
 mkdir temp/atg/registry
 mkdir temp/atg/registry/RepositoryTargeters
 mkdir temp/atg/registry/RepositoryTargeters/ProductCatalog

 cd temp/atg/registry/
  
 cp ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/tempconfig/atg/registry/RepositoryTargeters/ProductCatalog/*.properties RepositoryTargeters/ProductCatalog

 cd ${DYNAMO_HOME}

 bin/startRepositoryLoader -m Publishing.base  -initialService /atg/dynamo/service/loader/RLInitial -dir ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp -update -filemapping /atg/epub/file/typemappers/TargeterTypeMapping -foldermapping /atg/epub/file/typemappers/FileFolderTypeMapping

 rm -rf ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp

 echo "*************************"
 echo "importing international segments"
 echo "*************************"

 cd ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront
 mkdir temp
 mkdir temp/atg
 mkdir temp/atg/registry
 mkdir  ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp/atg/registry/RepositoryGroups
 mkdir ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp/atg/registry/RepositoryGroups/UserProfiles


 cp  ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/tempconfig/atg/registry/RepositoryGroups/UserProfiles/*.properties  ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp/atg/registry/RepositoryGroups/UserProfiles

 cd ${DYNAMO_HOME}

 bin/startRepositoryLoader -m Publishing.base  -initialService /atg/dynamo/service/loader/RLInitial -dir ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp -update -filemapping /atg/epub/file/typemappers/SegmentTypeMapping -foldermapping /atg/epub/file/typemappers/FileFolderTypeMapping

 rm -rf  ${DYNAMO_HOME}/../CommerceReferenceStore/Store/Storefront/temp

 rm -f ${DYNAMO_HOME}/bin/startRepositoryLoader
 
 
fi


exit 0
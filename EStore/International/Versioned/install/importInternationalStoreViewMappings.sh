#!/bin/sh

# Check for DYNAMO_HOME
if [ "x${DYNAMO_HOME}" != "x" ] ; then
  if [ -r ${DYNAMO_HOME}/META-INF/MANIFEST.MF ] ; then
    cd ${DYNAMO_HOME}
  else
    echo Invalid setting of DYNAMO_HOME: ${DYNAMO_HOME}
    exit 1
  fi
else
  echo DYNAMO_HOME must be set prior to running this script
  exit 1
fi

echo
echo "************************************"
echo "Importing International viewmappings"
echo "************************************"

echo
echo "Importing Store.EStore.International.Versioned/install/data/viewmapping.xml into /atg/web/viewmapping/ViewMappingRepository"

${DYNAMO_HOME}/bin/startSQLRepository -m DCS-UI -m Store.EStore.International.Versioned -repository /atg/web/viewmapping/ViewMappingRepository -import ${DYNAMO_HOME}/../CommerceReferenceStore/Store/EStore/International/Versioned/install/data/viewmapping.xml -workspace initialVwMapping -comment initialVwMapping -user publishing

exit 0

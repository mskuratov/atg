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
echo "*************************"
echo "Importing Preview URL data"
echo "*************************"

echo
echo "Importing store.estore.Versioned/install/data/viewmapping.xml into /atg/web/viewmapping/ViewMappingRepository"

bin/startSQLRepository -m BIZUI -repository /atg/web/viewmapping/ViewMappingRepository -import "${DYNAMO_HOME}/../CommerceReferenceStore/store/estore/Versioned/install/data/viewmapping.xml"
echo " Data CleanUp"
${DYNAMO_HOME}/../Publishing/base/bin/executeSQL -m Publishing.base -f ${DYNAMO_HOME}/../Publishing/base/install/install-cleanup.sql

exit 0

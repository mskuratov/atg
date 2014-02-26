#!/bin/sh

# Set DCS-UI version 
version=9.1

# This is a script to import data for Publishing.
# To run this script there is no need to set the following variables.

core=true
store=true
switching=false
search=false
i18n=false
template=false

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

for param in $*
do
  echo "Argument value is: $param"
  if [ $param = "-all" ]; then
    i18n=true
    search=true 
  
  elif [ $param = "-store" ]; then
    core=false

  elif [ $param = "-i18n" ]; then
    i18n=true

  elif [ $param = "-search" ]; then
    search=true

  elif [ $param = "-template" ]; then
    template=true

  elif [ $param = "-help" ]; then
    echo "  -all               Import Data for Store, International Store and Search Integration"
    echo "  -i18n            Import Data for International Store without Search Integration"
    echo "  -search        Import Data for Search Integration without International Store"
    echo "  -template     Import only template data for store"
    exit 2

  else
    echo "illegal argument $param"
    exit 2
  fi
done

if [ $template = "true" ]; then
  $DYNAMO_HOME/bin/startSQLRepository -m Store.Storefront -repository /atg/commerce/catalog/ProductCatalog -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/catalog-template.xml
  $DYNAMO_HOME/bin/startSQLRepository -m Store.Storefront -repository /atg/commerce/pricing/priceLists/PriceLists -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/pricelists-template.xml
  exit 4
fi

if [ $core = "true" ]; then
  # Check for version variable is defined
  if [ "x$version" != "x" ] ; then
    if [ -r ${DYNAMO_HOME}/../DCS-UI$version/META-INF/MANIFEST.MF ] ; then
      cd ${DYNAMO_HOME}
    else
      echo echo ** *Invalid version defined of DCS-UI ${DYNAMO_HOME}/../DCS-UI$version
      exit 5
    fi
  else
    echo DCS-UI version not define for this script please open the script and set version variable. 
    exit 6
  fi
  $DYNAMO_HOME/../Publishing/base/install/importPublishing.sh
  $DYNAMO_HOME/../BIZUI/install/importBizui.sh
  $DYNAMO_HOME/../AssetUI/install/importAssetUI.sh
  $DYNAMO_HOME/../DPS-UI/install/importDPSUI.sh
  $DYNAMO_HOME/../DPS-UI/install/importDPSUIExamples.sh
  $DYNAMO_HOME/../DCS-UI$version/DCS-UI/install/importDCSUIManagement.sh
  $DYNAMO_HOME/../DCS-UI$version/DCS-UI/install/importDCSUIManagementExamples.sh
fi

if [ $store = "true" ]; then
  if [ $i18n = "true" ]; then
    echo "***Import Data for Version Store International"
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore.Versioned -m Store.EStore.International -repository /atg/commerce/catalog/ProductCatalog -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/catalog-i18n-versioned.xml -workspace initiali18nCatalog -comment initiali18nCtlg -user publishing
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore.Versioned -repository /atg/commerce/pricing/priceLists/PriceLists -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/pricelists.xml -workspace initialPriceList -comment initialPriceList -user publishing
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore.Versioned -repository /atg/commerce/locations/LocationRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/stores.xml -workspace initialpStore -comment initialStore -user publishing
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore.Versioned -repository /atg/store/stores/StoreTextRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/storetext-i18n.xml -workspace initialpStoreText -comment initialStoreText -user publishing
    $DYNAMO_HOME/bin/startSQLRepository -m DCS-UI -m Store.EStore.Versioned -repository /atg/web/viewmapping/ViewMappingRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/EStore/Versioned/install/data/viewmapping.xml -workspace initialVwMapping -comment initialVwMapping -user publishing
    $DYNAMO_HOME/bin/startSQLRepository -m DAS.Versioned -m Store.EStore.Versioned -repository /atg/seo/SEORepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/seotags.xml -workspace initialSEOTags -comment initialSEOTags -user publishing
    $DYNAMO_HOME/../CommerceReferenceStore/Store/EStore/install/importFileAssets.sh
    $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/install/importFileAssets.sh -i18n
  else 
   echo "***Import Data for Version Store Non International"
   $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore.Versioned -repository /atg/commerce/catalog/ProductCatalog -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/catalog-versioned.xml -workspace initialPrdctCtlg -comment initialPrdctCtlg -user publishing
   $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore.Versioned -repository /atg/commerce/pricing/priceLists/PriceLists -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/pricelists.xml -workspace initialPriceList -comment initialPriceList -user publishing
   $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore.Versioned -repository /atg/commerce/locations/LocationRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/stores.xml -workspace initialStore -comment initialStore -user publishing
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore.Versioned -repository /atg/store/stores/StoreTextRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/storetext.xml -workspace initialpStoreText -comment initialStoreText -user publishing
   $DYNAMO_HOME/bin/startSQLRepository -m DCS-UI -m Store.EStore.Versioned -repository /atg/web/viewmapping/ViewMappingRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/EStore/Versioned/install/data/viewmapping.xml -workspace initialVwMapping -comment initialVwMapping -user publishing
   $DYNAMO_HOME/bin/startSQLRepository -m DAS.Versioned -m Store.EStore.Versioned -repository /atg/seo/SEORepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/seotags.xml -workspace initialSEOTags -comment initialSEOTags -user publishing
   $DYNAMO_HOME/../CommerceReferenceStore/Store/EStore/install/importFileAssets.sh
   $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/install/importFileAssets.sh
  fi
fi


if [ $search = "true" ]; then
  # Check for version variable is defined
  if [ "x$version" != "x" ] ; then
    if [ -r ${DYNAMO_HOME}/../DCS-UI$version/META-INF/MANIFEST.MF ] ; then
      cd ${DYNAMO_HOME}
    else
      echo echo ** *Invalid version defined of DCS-UI ${DYNAMO_HOME}/../DCS-UI$version
      exit 7
    fi
  else
    echo DCS-UI version not define for this script please open the script and set version variable. 
    exit 8
  fi

  $DYNAMO_HOME/../DCS-UI$version/DCS-UI/Search/install/importDCSUISearchCustomCatalogs.sh
  $DYNAMO_HOME/../AssetUI/Search/install/importAssetUISearch.sh
  $DYNAMO_HOME/../DCS-UI$version/DCS-UI/Search/install/importSearchTesting.sh
  if [ $i18n = "true" ]; then
   echo ***Import Data For Version Search Non Internationalizaation
   $DYNAMO_HOME/bin/startSQLRepository -m DCS-UI -m DCS.Search.CustomCatalogs.Versioned -m Store.EStore.Versioned -m Store.EStore.International -repository  /atg/search/repository/RefinementRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/facets-i18n.xml -workspace  initiali18nSrchFcts -comment initiali18nSrchFcts1 -user publishing
   $DYNAMO_HOME/bin/startSQLRepository -m DCS-UI -m DCS.Search.CustomCatalogs.Versioned -m Store.EStore.Versioned -m Store.EStore.International   -repository  /atg/commerce/catalog/ProductCatalog -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/out-international-category-facets.xml -workspace initiali18nCtgry -comment initiali18nCtgry1 -user publishing
  else 
   $DYNAMO_HOME/bin/startSQLRepository -m DCS-UI -m DCS.Search.CustomCatalogs.Versioned -m Store.EStore.Versioned -repository  /atg/search/repository/RefinementRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/facets.xml -workspace initialSrchFcts -comment initialSrchFcts -user publishing
   $DYNAMO_HOME/bin/startSQLRepository -m DCS-UI -m DCS.Search.CustomCatalogs.Versioned -m Store.EStore.Versioned -repository  /atg/commerce/catalog/ProductCatalog -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/out-category-facets.xml -workspace initialCtgry -comment initialCtgry -user publishing
  fi
fi

echo "Data CleanUp"
${DYNAMO_HOME}/../Publishing/base/bin/executeSQL -m Publishing.base -f ${DYNAMO_HOME}/../Publishing/base/install/install-cleanup.sql

exit 9


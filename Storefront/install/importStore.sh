#!/bin/sh

# This is a script to import data for Production.
# To run this script there is no need to set the following variables.

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
  
  elif [ $param = "-switching" ]; then
    switching=true

  elif [ $param = "-i18n" ]; then
    i18n=true

  elif [ $param = "-search" ]; then
    search=true

  elif [ $param = "-template" ]; then
    template=true

  elif [ $param = "-help" ]; then
    echo "This script is required to run separately for importing data into core and each of switching DB instances"
    echo "-all               Import Demo Data for Store, International Store and Search Integration."
    echo "-switching    Import Demo Data for Switching DataSource"
    echo  "                   Must be specified for importing data into Switching Datasources"
    echo  "                   When specified it should be the first option. E.g. importStore -switching -i18n"
    echo "-i18n            Import Demo Data for International Store."
    echo "-search        Import Demo Data for Search Integration."
    echo "-template     Import only template data for Store. "
    echo "                    -switching not required for importing template data in switching DB."
    exit 2

  else
    echo "illegal argument $param"
    exit 2
  fi
done

if [ $template = "true" ]; then
  $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/commerce/catalog/ProductCatalog -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/catalog-template.xml
  $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/commerce/pricing/priceLists/PriceLists -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/pricelists-template.xml
  exit 4
fi

if [ $switching = "true" ]; then
  if [ $i18n = "true" ]; then
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -m Store.EStore.International -repository /atg/commerce/catalog/ProductCatalog -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/catalog-i18n.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/commerce/pricing/priceLists/PriceLists -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/pricelists.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/commerce/locations/LocationRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/stores.xml
  else 
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/commerce/catalog/ProductCatalog -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/catalog.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/commerce/pricing/priceLists/PriceLists -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/pricelists.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/commerce/locations/LocationRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/stores.xml
  fi
fi

if [ $store = "true" ] && [ $switching != "true" ]; then
  if [ $i18n = "true" ]; then
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -m Store.EStore.International -repository /atg/commerce/catalog/ProductCatalog -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/catalog-i18n.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/commerce/pricing/priceLists/PriceLists -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/pricelists.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/commerce/inventory/InventoryRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/inventory.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/userprofiling/ProfileAdapterRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/users.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/commerce/locations/LocationRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/stores.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/store/stores/StoreTextRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/storetext-i18n.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/commerce/claimable/ClaimableRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/claimable-i18n.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/seo/SEORepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/seotags.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/commerce/order/OrderRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/orders.xml
  else 
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/commerce/catalog/ProductCatalog -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/catalog.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/commerce/pricing/priceLists/PriceLists -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/pricelists.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/commerce/inventory/InventoryRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/inventory.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/userprofiling/ProfileAdapterRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/users.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/commerce/locations/LocationRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/stores.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/store/stores/StoreTextRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/storetext.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/commerce/claimable/ClaimableRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/claimable.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/seo/SEORepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/seotags.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -repository /atg/commerce/order/OrderRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/orders.xml
  fi
fi


if [ $search = "true" ]; then
  if [ $i18n = "true" ]; then
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -m Store.EStore.International -m DCS.Search.CustomCatalogs -repository /atg/search/repository/RefinementRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/facets-i18n.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -m Store.EStore.International -m DCS.Search.CustomCatalogs -repository /atg/commerce/catalog/ProductCatalog -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/out-international-category-facets.xml
  else 
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -m DCS.Search.CustomCatalogs -repository /atg/search/repository/RefinementRepository -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/facets.xml
    $DYNAMO_HOME/bin/startSQLRepository -m Store.EStore -m DCS.Search.CustomCatalogs -repository /atg/commerce/catalog/ProductCatalog -import $DYNAMO_HOME/../CommerceReferenceStore/Store/Storefront/data/out-category-facets.xml
  fi
fi

if [ $switching != "true" ]; then
  echo "**Data CleanUp"
  ${DYNAMO_HOME}/../Publishing/base/bin/executeSQL -m Store.EStore -f ${DYNAMO_HOME}/../CommerceReferenceStore/Store/EStore/install/install-cleanup.sql
fi

exit 5


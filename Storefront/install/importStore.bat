echo off
REM To run this script there is no need to set the following variables.

set search=
set i18n=
set switching=

REM Check for DYNAMO_HOME

IF [%DYNAMO_HOME%] == [] goto :dynamohomeerror

IF NOT EXIST %DYNAMO_HOME%\nul goto :dynamohomedirerror

:Loop

  echo  Here your batch file handles %1

  IF [%1] == [] GOTO -store

  IF [%1] == [-help] GOTO -help

  IF [%1] == [-template] set template=true

  IF [%1] == [-all] GOTO -all

  IF [%1] == [-i18n] set i18n=true

  IF [%1] == [-search] set search=true

  IF [%1] == [-switching] set switching=true

  SHIFT
GOTO Loop

  :-all
    IF [%template%] == [true] goto -template
    set i18n=true
    set search=true
    goto -store

  :-store
    IF [%template%] == [true] goto -template
    IF [%switching%] == [true] goto -switching
    IF [%i18n%] == [true] goto -i18n
    echo ***Import Data for  -store
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/commerce/catalog/ProductCatalog -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/catalog.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/commerce/pricing/priceLists/PriceLists -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/pricelists.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/commerce/inventory/InventoryRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/inventory.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/userprofiling/ProfileAdapterRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/users.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/commerce/locations/LocationRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/stores.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/store/stores/StoreTextRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/storetext.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/commerce/claimable/ClaimableRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/claimable-i18n.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/seo/SEORepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/seotags.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/commerce/order/OrderRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/orders.xml
    goto -search  

  :-i18n
    IF not [%i18n%] == [true] goto -search
    echo ***Import Data for Store Internationalization
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -m Store.EStore.International -repository /atg/commerce/catalog/ProductCatalog -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/catalog-i18n.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/commerce/pricing/priceLists/PriceLists -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/pricelists.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/commerce/inventory/InventoryRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/inventory.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/userprofiling/ProfileAdapterRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/users.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/commerce/locations/LocationRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/stores.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/store/stores/StoreTextRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/storetext-i18n.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/commerce/claimable/ClaimableRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/claimable.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/seo/SEORepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/seotags.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/commerce/order/OrderRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/orders.xml
    goto -search  

  :-search
    IF not [%search%] == [true] goto endofscript
    IF [%i18n%] == [true] goto -i18nsearch
    echo ***Import Data for -search
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -m DCS.Search.CustomCatalogs -repository /atg/search/repository/RefinementRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/facets.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -m DCS.Search.CustomCatalogs -repository /atg/commerce/catalog/ProductCatalog -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/out-category-facets.xml
    goto cleanUp

  :-i18nsearch
    echo **** import Data for search Internationalization
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -m Store.EStore.International -m DCS.Search.CustomCatalogs -repository /atg/search/repository/RefinementRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/facets-i18n.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -m Store.EStore.International -m DCS.Search.CustomCatalogs -repository /atg/commerce/catalog/ProductCatalog -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/out-international-category-facets.xml
    goto cleanUp

  :-switching
    IF [%i18n%] == [true] goto -i18nswitching 
    echo **** import Data for Switching
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/commerce/catalog/ProductCatalog -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/catalog.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/commerce/pricing/priceLists/PriceLists -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/pricelists.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/commerce/locations/LocationRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/stores.xml
    IF [%search%] == [true] goto -search
    goto endofscript

  :-i18nswitching
    echo **** Import Data for Switching Internationalization
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -m Store.EStore.International -repository /atg/commerce/catalog/ProductCatalog -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/catalog-i18n.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/commerce/pricing/priceLists/PriceLists -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/pricelists.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/commerce/locations/LocationRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/stores.xml
    IF [%search%] == [true] goto -i18nsearch
    goto endofscript

  :-template
    echo **** Import Data For Version Template
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/commerce/catalog/ProductCatalog -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/catalog-template.xml
    Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore -repository /atg/commerce/pricing/priceLists/PriceLists -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/pricelists-template.xml
    goto cleanUp

  :dynamohomeerror
    echo ** Invalid setting of DYNAMO_HOME: %DYNAMO_HOME%
    echo **
    goto endofscript


  :dynamohomedirerror
    echo ** Invalid setting of DYNAMO_HOME: %DYNAMO_HOME% does not exist
    echo **
    goto endofscript

  :-help
    echo  This script is required to run separately for importing data into core and each of switching DB instances
    echo  -all               Import Demo Data for Store, International Store and Search Integration.
    echo -switching          Import Demo Data for Switching DataSource
    echo                     Must be specified for importing demo data into Switching Datasources
    echo                     When specified it should be the first option. E.g. importStore -switching -i18n
    echo -i18n               Import Demo Data for International Store.
    echo -search             Import Demo Data for Search Integration.
    echo -template           Import only template data for Store.
    echo                           -switching not required for importing template data in switching DB.
    goto endofscript

  :cleanUp
    IF [%switching%] == [true] goto endofscript
    echo  Data CleanUp
    call %DYNAMO_HOME%\..\Publishing\base\bin\executeSQL.bat -m Store.EStore -f %DYNAMO_HOME%\..\CommerceReferenceStore\Store\EStore\install\install-cleanup.sql
    goto endofscript

  :endofscript
    set search=
    set i18n=
    set switching=
    echo ** End of importStore
    pause


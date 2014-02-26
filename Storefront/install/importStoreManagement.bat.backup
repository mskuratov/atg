echo off

REM Set Version for DCS-UI

set version=9.1

REM To run this script there is no need to set the following variables.
set core=
set search=
set i18n=

REM Check for DYNAMO_HOME

IF [%DYNAMO_HOME%] == [] goto :dynamohomeerror

IF NOT EXIST %DYNAMO_HOME%\nul goto :dynamohomedirerror

:Loop

  echo  Here your batch file handles %1

  IF [%1] == [] GOTO -core

  IF [%1] == [-help] GOTO -help

  IF [%1] == [-template] GOTO -template

  IF [%1] == [-all] GOTO -all

  IF [%1] == [-i18n] set i18n=true

  IF [%1] == [-store] set core=false

  IF [%1] == [-search] set search=true

  SHIFT
GOTO Loop

 :-all
    set i18n=true
    set search=true
    goto -core

 :-core
    REM Check for DCS-UI
    IF [%version%] == [] goto :dcsuiversionerror
    IF NOT EXIST %DYNAMO_HOME%\..\DCS-UI%version%\nul goto :dcsuidirerror
    IF [%core%] == [false] goto -store
    Call %DYNAMO_HOME%/../Publishing/base/install/importPublishing.bat
    Call %DYNAMO_HOME%/../BIZUI/install/importBizui.bat
    Call %DYNAMO_HOME%/../AssetUI/install/importAssetUI.bat
    Call %DYNAMO_HOME%/../DPS-UI/install/importDPSUI.bat
    Call %DYNAMO_HOME%/../DPS-UI/install/importDPSUIExamples.bat
    Call %DYNAMO_HOME%/../DCS-UI%version%/DCS-UI/install/importDCSUIManagement.bat
    Call %DYNAMO_HOME%/../DCS-UI%version%/DCS-UI/install/importDCSUIManagementExamples.bat
    goto -store  
  
 :-store
   IF [%i18n%] == [true] goto -i18n
   echo ***Import Data for Version Store Non Internationalizaation
   Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore.Versioned -repository /atg/commerce/catalog/ProductCatalog -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/catalog-versioned.xml -workspace initialPrdctCtlg -comment initialPrdctCtlg -user publishing
   Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore.Versioned -repository /atg/commerce/pricing/priceLists/PriceLists -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/pricelists.xml -workspace initialPriceList -comment initialPriceList -user publishing
   Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore.Versioned -repository /atg/commerce/locations/LocationRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/stores.xml -workspace initialStore -comment initialStore -user publishing
   Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore.Versioned -repository /atg/store/stores/StoreTextRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/storetext.xml -workspace initialStoreText -comment initialStoreText -user publishing
   Call %DYNAMO_HOME%\bin\startSQLRepository -m DCS-UI -m Store.EStore.Versioned -repository /atg/web/viewmapping/ViewMappingRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/EStore/Versioned/install/data/viewmapping.xml -workspace initialVwMapping -comment initialVwMapping -user publishing
   Call %DYNAMO_HOME%\bin\startSQLRepository -m DAS.Versioned -m Store.EStore.Versioned -repository /atg/seo/SEORepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/seotags.xml -workspace initialSEOTags -comment initialSEOTags -user publishing
   Call %DYNAMO_HOME%/../CommerceReferenceStore/Store/EStore/install/importFileAssets.bat
   Call %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/install/importFileAssets.bat
   goto -search

 :-i18n
   IF not [%i18n%] == [true] goto -search
   echo ***Import Data for Version Store Internationalization
   Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore.Versioned -m Store.EStore.International -repository /atg/commerce/catalog/ProductCatalog -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/catalog-i18n-versioned.xml -workspace initiali18nCatalog -comment initiali18nCtlg -user publishing
   Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore.Versioned -repository /atg/commerce/pricing/priceLists/PriceLists -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/pricelists.xml -workspace initialPriceList -comment initialPriceList -user publishing
   Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore.Versioned -repository /atg/commerce/locations/LocationRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/stores.xml -workspace initialpStore -comment initialStore -user publishing
   Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore.Versioned -repository /atg/store/stores/StoreTextRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/storetext-i18n.xml -workspace initialStoreText -comment initialStoreText -user publishing
   Call %DYNAMO_HOME%\bin\startSQLRepository -m DCS-UI -m Store.EStore.Versioned -repository /atg/web/viewmapping/ViewMappingRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/EStore/Versioned/install/data/viewmapping.xml -workspace initialVwMapping -comment initialVwMapping -user publishing
   Call %DYNAMO_HOME%\bin\startSQLRepository -m DCS-UI -m Store.EStore.International.Versioned -repository /atg/web/viewmapping/ViewMappingRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/EStore/International/Versioned/install/data/viewmapping.xml -workspace initialVwMapping -comment initialVwMapping -user publishing
   Call %DYNAMO_HOME%\bin\startSQLRepository -m DAS.Versioned -m Store.EStore.Versioned -repository /atg/seo/SEORepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/seotags.xml -workspace initialSEOTags -comment initialSEOTags -user publishing
   Call %DYNAMO_HOME%/../CommerceReferenceStore/Store/EStore/install/importFileAssets.bat
   Call %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/install/importFileAssets.bat -i18n
   goto -search  

 :-search
   IF not [%search%] == [true] goto endofscript
   REM Check for DCS-UI
   IF [%version%] == [] goto :dcsuiversionerror
   IF NOT EXIST %DYNAMO_HOME%\..\DCS-UI%version%\nul goto :dcsuidirerror
   call %DYNAMO_HOME%\..\DCS-UI%version%\DCS-UI\Search\install\importDCSUISearchCustomCatalogs.bat
   call %DYNAMO_HOME%\..\AssetUI\Search\install\importAssetUISearch.bat
   call %DYNAMO_HOME%\..\DCS-UI%version%\DCS-UI\Search\install\importSearchTesting.bat
   IF [%i18n%] == [true] goto -i18nsearch
   echo ***Import Data For Version Search Non Internationalizaation
   Call %DYNAMO_HOME%\bin\startSQLRepository -m DCS-UI -m DCS.Search.CustomCatalogs.Versioned -m Store.EStore.Versioned -repository  /atg/search/repository/RefinementRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/facets.xml -workspace initialSrchFcts -comment initialSrchFcts -user publishing
   Call %DYNAMO_HOME%\bin\startSQLRepository -m DCS-UI -m DCS.Search.CustomCatalogs.Versioned -m Store.EStore.Versioned -repository  /atg/commerce/catalog/ProductCatalog -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/out-category-facets.xml -workspace initialCtgry -comment initialCtgry -user publishing
   goto endofscript

 :-i18nsearch
   echo ~~~ Import Data For Version Search Internationalization
   Call %DYNAMO_HOME%\bin\startSQLRepository -m DCS-UI -m DCS.Search.CustomCatalogs.Versioned -m Store.EStore.Versioned -m Store.EStore.International -repository /atg/search/repository/RefinementRepository -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/facets-i18n.xml -workspace  initiali18nSrchFcts -comment initiali18nSrchFcts -user publishing
   Call %DYNAMO_HOME%\bin\startSQLRepository -m DCS-UI -m DCS.Search.CustomCatalogs.Versioned -m Store.EStore.Versioned -m Store.EStore.International -repository /atg/commerce/catalog/ProductCatalog -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/out-international-category-facets.xml -workspace initiali18nCtgry -comment initiali18nCtgry -user publishing
   goto endofscript

 :-template
   echo ~~~ Import Data For Version Template
   Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore.Versioned -repository /atg/commerce/catalog/ProductCatalog -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/catalog-template.xml -workspace initialCtlgTmplt -comment initialCtlgTmplt -user publishing
   Call %DYNAMO_HOME%\bin\startSQLRepository -m Store.EStore.Versioned -repository /atg/commerce/pricing/priceLists/PriceLists -import %DYNAMO_HOME%/../CommerceReferenceStore/Store/Storefront/data/pricelists-template.xml -workspace initialPrcTmplt -comment initialPrcTmplt -user publishing
   goto endofscript

 :dcsuiversionerror
   echo ** *DCS-UI version not define for this script please open the script and set version variable. 
   echo ***
   goto endofscript


 :dcsuidirerror
   echo ** *Invalid version defined of DCS-UI %DYNAMO_HOME%/../DCS-UI%version%
   echo ***
   goto endofscript

 :dynamohomeerror
   echo ** *Invalid setting of DYNAMO_HOME: %DYNAMO_HOME%
   echo ***
   goto endofscript

 :dynamohomedirerror
   echo ** *Invalid setting of DYNAMO_HOME: %DYNAMO_HOME% does not exist
   echo ***
   goto endofscript

 :-help
    echo -all         Import Data for Store, International Store and Search Integration
    echo -i18n        Import Data for International Store without Search Integration
    echo -search      Import Data for Search Integration
    echo -template    Import only template data for store
    goto endofscript

 :endofscript
   echo ** Installed Data CleanUp
   call %DYNAMO_HOME%\..\Publishing\base\bin\executeSQL.bat -m Publishing.base -f %DYNAMO_HOME%\..\Publishing\base\install\install-cleanup.sql
   echo *** End of importData
   pause

 



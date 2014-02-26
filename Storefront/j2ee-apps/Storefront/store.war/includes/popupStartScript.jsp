<%--
  Include all Javascript files that need to be loaded for the page. 
  All <script> blocks should be included on this page to perform any page initialization.
  
  Required Parameters:
    None
    
  Optional Parameters:
    None
--%>
  
<dsp:page>
  <dsp:getvalueof var="storeConfig" bean="/atg/store/StoreConfiguration"/>
  <fmt:message key="common.button.pleaseWaitText" var="pleaseWaitMessage"/>
  
  <%-- 
    Include dojo from WebUI module. 
    Context root for dojo version 1.6.1 is configured in WebUI module as '/dojo-1-6-1' 
  --%>
  <script type="text/javascript">
    <%-- 
      Dojo Configuration.

      Enable/Disable Dojo debugging - This will log any dojo.debug calls to the Firebug console if installed. 
      Enable debugging by setting component /atg/store/StoreConfiguration.dojoDebug=true 
    --%>
    var djConfig={
      disableFlashStorage: true,
      parseOnLoad: true,
      isDebug: ${storeConfig.dojoDebug}
    };
  </script>
   
  <script type="text/javascript" src="/dojo-1-6-1/dojo/dojo.js.uncompressed.js"></script>

  <%-- 
    Include all Javascript files that need to be loaded for the page. 
    All <script> blocks should be included on this page to perform any page initialization. 
  --%>
  <c:set var="javascriptRoot" value="${pageContext.request.contextPath}/javascript"/>

  <%-- Include other required external Javascript files --%>
  <script type="text/javascript" src="${javascriptRoot}/store.js"></script>  
  <script type="text/javascript" charset="utf-8">
    var formIdArray = ["atg_store_preRegisterForm","atg_store_registerLoginForm","atg_store_checkoutLoginForm","searchForm","simpleSearch"];
    dojo.addOnLoad(function(){
      atg.store.util.addTextAreaCounter();
      atg.store.util.addNumericValidation();
      atg.store.util.addReturnHandling();
      atg.store.util.popupCloseHandler();
    });
  </script>  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/includes/popupStartScript.jsp#1 $$Change: 735822 $--%>

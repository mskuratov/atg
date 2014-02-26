<%--
  Renders "Email Me" form errors, if any.

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/inventory/BackInStockFormHandler"/>

  <dsp:getvalueof var="formExceptions" bean="BackInStockFormHandler.formExceptions"/>
  <json:array name="formExceptions" var="formException" items="${formExceptions}">
    <json:object>
        <json:property name="propertyName" value="${formException.propertyName}"/>
        <json:property name="errorCode" value="${formException.errorCode}"/>
    </json:object>
  </json:array>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/gadgets/emailMeFormErrors.jsp#2 $$Change: 742374 $ --%>

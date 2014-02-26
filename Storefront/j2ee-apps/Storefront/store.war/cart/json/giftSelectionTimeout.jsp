<%@ page contentType="application/json; charset=UTF-8" %>

<%--
  This page sets the gift selection session expiration values as json properties.
  
  Required Parameters:
    None.
--%>

<dsp:page>
  <json:object>
    <json:property name="timeout" value="true"/>
    <%-- The real error URL that the server would have redirected to if the request wasn't AJAX --%>
    <json:property name="timeoutUrl" escapeXml="false">
      <dsp:valueof bean="/atg/commerce/promotion/GiftWithPurchaseFormHandler.sessionExpirationURL" valueishtml="true"/>
    </json:property>
  </json:object>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/json/giftSelectionTimeout.jsp#1 $$Change: 735822 $--%>

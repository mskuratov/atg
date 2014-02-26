<%@ page contentType="application/json; charset=UTF-8"%>

<%--
  This JSON notifies the GWP pop over that an item was successfully added to the cart.
--%>

<dsp:page>
  <json:object name="giftSelectionStatus">
    <%-- The statusCode is not a localized resource but a flag for the JavaScript GWP pop over --%>
    <json:property name="statusCode">
      SUCCESS
    </json:property>
  </json:object>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/json/giftSelectionStatus.jsp#2 $$Change: 742374 $--%>

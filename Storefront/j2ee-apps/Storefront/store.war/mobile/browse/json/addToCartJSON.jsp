<%--
  Returns JSON for adding item to the "Shopping Cart".

  Optional Parameters:
    errorAddToCart
      If present, signifies that an error is occurred
--%>
<%@page contentType="application/json"%>
<%@page trimDirectiveWhitespaces="true"%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>

  <json:object>
    <c:choose>
      <c:when test="${param.errorAddToCart}">
        <dsp:getvalueof var="formExceptions" bean="CartModifierFormHandler.formExceptions"/>
        <json:property name="addToCartError" value="${formExceptions}"/>
      </c:when>
      <c:otherwise>
        <%@include file="/mobile/cart/gadgets/cartItemCount.jspf"%>
        <json:property name="cartItemCount" value="${itemsQuantity}"/>
      </c:otherwise>
    </c:choose>
  </json:object>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/json/addToCartJSON.jsp#4 $$Change: 788278 $--%>

<%--
  Renders billing page during checkout process where user can select saved credit card or create new one.

  Page includes:
    /mobile/checkout/selectCreditCard.jsp - List of saved credit cards
    /mobile/checkout/newCreditCard.jsp - New credit card page
    /mobile/checkout/noCreditCardNeeded.jsp - Explanatory page of why a credit card is not needed

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupDroplet"/>
  <dsp:importbean bean="/atg/store/droplet/EnsureCreditCard"/>
  <dsp:importbean bean="/atg/store/mobile/order/purchase/MobileBillingFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <%--
    If there are errors during form submission (we tried to select credit card without billing address)
    the current order is invalidated and doesn't contain priceInfo.
    Will reprice whole order in this case.
  --%>
  <dsp:getvalueof var="formExceptions" bean="MobileBillingFormHandler.formExceptions"/>
  <c:if test="${not empty formExceptions}">
    <dsp:droplet name="RepriceOrderDroplet">
      <dsp:param name="pricingOp" value="ORDER_TOTAL"/>
    </dsp:droplet>
  </c:if>

  <%-- INITALIZE COMMERCE SHIPPING OBJECTS --%>
  <dsp:droplet name="ShippingGroupDroplet">
    <dsp:param name="createOneInfoPerUnit" value="true"/>
    <dsp:param name="clearShippingInfos" value="true"/>
    <dsp:param name="shippingGroupTypes" value="hardgoodShippingGroup"/>
    <dsp:param name="initShippingGroups" value="true"/>
    <dsp:param name="initBasedOnOrder" value="true"/>
    <dsp:oparam name="output"/>
  </dsp:droplet>

  <%-- Apply available store credits to the order --%>
  <dsp:setvalue bean="MobileBillingFormHandler.applyStoreCreditsToOrder" value=""/>

  <dsp:getvalueof var="order" vartype="atg.commerce.Order" bean="ShoppingCart.current"/>

  <%-- Don't offer enter credit card's info if order's total is covered by store credits --%>
  <c:choose>
    <c:when test="${order.priceInfo.total > order.storeCreditsAppliedTotal}">
      <%-- Store credits are not enough. Render billing form --%>
      <dsp:droplet name="EnsureCreditCard">
        <dsp:param name="order" bean="ShoppingCart.current"/>
      </dsp:droplet>
      <dsp:getvalueof var="creditCards" bean="Profile.creditCards"/>
      <c:choose>
        <c:when test="${not empty creditCards}">
          <%-- List of credit cards --%>
          <dsp:include page="selectCreditCard.jsp"/>
        </c:when>
        <c:otherwise>
          <%-- "New credit card" form --%>
          <dsp:include page="newCreditCard.jsp"/>
        </c:otherwise>
      </c:choose>
    </c:when>
    <c:otherwise>
      <%-- Order is paid by store credits. Display its total and 'Continue' button --%>
      <dsp:include page="noCreditCardNeeded.jsp"/>
    </c:otherwise>
  </c:choose>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/checkout/billing.jsp#2 $$Change: 742374 $--%>

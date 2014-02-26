<%--
  This page renders content of not empty "Shopping Cart".

  Page includes:
    /mobile/global/gadgets/errorMessage.jsp - Displays all errors collected from FormHandler
    /mobile/cart/gadgets/cartItems.jsp - Renderer of cart items
    /mobile/global/gadgets/pricingSummary.jsp - Renderer of order summary (payment info, shipping address, etc)

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>

  <dsp:getvalueof var="mobileStorePrefix" bean="/atg/store/StoreConfiguration.mobileStorePrefix"/>

  <div class="cartContainer">
    <%-- ========== Form ========== --%>
    <dsp:form action="${siteContextPath}/cart/cart.jsp" method="post" name="cartContent" formid="cartContent">
      <%-- ========== Redirection URLs ========== --%>
      <dsp:input type="hidden" bean="CartModifierFormHandler.moveToPurchaseInfoSuccessURL" value="${siteContextPath}/checkout/shipping.jsp"/>
      <dsp:input type="hidden" bean="CartModifierFormHandler.moveToPurchaseInfoErrorURL" value="${pageContext.request.requestURI}"/>
      <dsp:input type="hidden" bean="CartModifierFormHandler.updateSuccessURL" value="${pageContext.request.requestURI}"/>
      <dsp:input type="hidden" bean="CartModifierFormHandler.updateErrorURL" value="${pageContext.request.requestURI}"/>
      <dsp:input type="hidden" bean="CartModifierFormHandler.giftMessageUrl" value="${siteContextPath}/checkout/giftMessage.jsp"/>
      <dsp:input type="hidden" bean="CartModifierFormHandler.shippingInfoURL" value="${siteContextPath}/checkout/shipping.jsp"/>
      <dsp:input type="hidden" bean="CartModifierFormHandler.loginDuringCheckoutURL" value="${siteContextPath}/checkout/login.jsp"/>

      <dsp:input type="hidden" id="removeItemFromOrder" bean="CartModifierFormHandler.removeItemFromOrder" value=""/>
      <dsp:input type="hidden" bean="CartModifierFormHandler.removeItemFromOrderSuccessURL" value="${pageContext.request.requestURI}"/>
      <dsp:input type="hidden" bean="CartModifierFormHandler.removeItemFromOrderErrorURL" value="${pageContext.request.requestURI}"/>

      <div class="cartData">
        <%-- Prepare formHandlers map --%>
        <dsp:getvalueof var="handlerBean1" bean="CartModifierFormHandler"/>
        <dsp:getvalueof var="handlerBean2" bean="CouponFormHandler"/>
        
        <jsp:useBean id="formHandlers" class="java.util.HashMap"/>
        <c:set target="${formHandlers}" property="CartModifierFormHandler" value="${handlerBean1}"/>
        <c:set target="${formHandlers}" property="CouponFormHandler" value="${handlerBean2}"/>
        
        <%-- Display "CartModifierFormHandler" error messages, which were specified in "formException.message" --%>        
        <dsp:include page="${mobileStorePrefix}/global/gadgets/errorMessage.jsp">
          <dsp:param name="formHandlers" value="${formHandlers}"/>          
        </dsp:include>

        <%-- Shopping cart content --%>
        <div>
          <dsp:include page="gadgets/cartItems.jsp"/>
        </div>

        <%-- Order summary --%>
        <dsp:include page="${mobileStorePrefix}/global/gadgets/pricingSummary.jsp">
          <dsp:param name="order" bean="ShoppingCart.current"/>
          <dsp:param name="isCheckout" value="true"/>
          <dsp:param name="isOrderReview" value="false"/>
        </dsp:include>
      </div>

      <%-- "Submit" button --%>
      <div class="cartCheckout">
        <fmt:message var="checkoutText" key="mobile.cart.checkout"/>
        <dsp:input class="mainActionButton" type="submit" bean="CartModifierFormHandler.checkout" value="${checkoutText}"/>
      </div>
    </dsp:form>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cart/cartContent.jsp#4 $$Change: 794114 $ --%>

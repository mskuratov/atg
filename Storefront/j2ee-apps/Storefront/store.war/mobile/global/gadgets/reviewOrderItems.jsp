<%-- 
  Render order items with order summary box.

  Page includes:
    /mobile/global/gadgets/errorMessage.jsp - Displays all errors collected from FormHandler
    /mobile/global/gadgets/orderItemsRenderer.jsp - Order items renderer
    /mobile/global/gadgets/pricingSummary.jsp - Checkout summary

  Required parameters:
    order
      Order which items should be displayed
    isCheckout
      Indicator if this order is about to checkout or has already been placed.
      Possible values:
        'true' = is about to checkout
        'false' = placed order
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>

  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="isCheckout" param="isCheckout"/>
  <dsp:getvalueof var="commerceItems" param="order.commerceItems"/>

  <div class="cartData">
    <c:if test="${not empty commerceItems}">
      <div class="cartItems">
        <c:if test="${isCheckout}">
          <%-- Display "CouponFormHandler" error messages, which were specified in "formException.message" --%>
          <dsp:include page="errorMessage.jsp">
            <dsp:param name="formHandler" bean="CouponFormHandler"/>
          </dsp:include>
        </c:if>
        <c:forEach var="currentItem" items="${commerceItems}">
          <dsp:include page="orderItemsRenderer.jsp">
            <dsp:param name="currentItem" value="${currentItem}"/>
            <dsp:param name="isCheckout" param="isCheckout"/>
          </dsp:include>
        </c:forEach>
      </div>
    </c:if>

    <dsp:include page="pricingSummary.jsp">
      <dsp:param name="order" param="order"/>
      <dsp:param name="isCheckout" param="isCheckout"/>
      <dsp:param name="isOrderReview" value="true"/>
    </dsp:include>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/global/gadgets/reviewOrderItems.jsp#1 $$Change: 742374 $--%>

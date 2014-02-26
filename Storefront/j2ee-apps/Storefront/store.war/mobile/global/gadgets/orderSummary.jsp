<%-- 
  Renders "Order summary".

  Page includes:
    /mobile/global/gadgets/reviewOrderItems.jsp - Display order items
    /mobile/global/gadgets/reviewPayment.jsp - Renderer of payment info
    /mobile/global/gadgets/reviewShippingAddress.jsp - Renderer of shipping address
    /mobile/global/gadgets/reviewShippingMethod.jsp - Renderer of shipping method

  Required parameters:
    order
      Order item (as for view submitted order as for review order for checkout)
    hardgoodShippingGroup
      Correct single hardgood shipping group without giftWrapItems
    isCheckout
      Indicator if this order is about to checkout or has already been placed.
      Possible values:
        'true' = is about to checkout
        'false' = placed order
--%>
<dsp:page>
  <%-- Render order items with order summary box --%>
  <dsp:include page="reviewOrderItems.jsp">
    <dsp:param name="order" param="order"/>
    <dsp:param name="isCheckout" param="isCheckout"/>
  </dsp:include>
  <%-- Pay With (Payment Method and Billing Address) --%>
  <dsp:include page="reviewPayment.jsp">
    <dsp:param name="order" param="order"/>
    <dsp:param name="isCheckout" param="isCheckout"/>
  </dsp:include>
  <%-- Ship To --%>
  <dsp:include page="reviewShippingAddress.jsp">
    <dsp:param name="order" param="order"/>
    <dsp:param name="address" param="hardgoodShippingGroup.shippingAddress"/>
    <dsp:param name="isCheckout" param="isCheckout"/>
  </dsp:include>
  <%-- Shipping method --%>
  <dsp:include page="reviewShippingMethod.jsp">
    <dsp:param name="order" param="order"/>
    <dsp:param name="isCheckout" param="isCheckout"/>
  </dsp:include>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/global/gadgets/orderSummary.jsp#2 $$Change: 742374 $--%>

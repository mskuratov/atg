<%--
  Hidden form for adding items to the cart.

  Required Parameters:
    productId
      ID of the product to add to the cart

  Optional Parameters:
    selectedSkuId
      SKU being added to shopping cart
    selectedQty
      Number of items being added
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>

  <%-- ========== Form ========== --%>
  <dsp:form id="addToCartForm" formid="addToCartForm" action="${pageContext.request.requestURI}" method="post">
    <dsp:input bean="CartModifierFormHandler.addItemCount" type="hidden" value="1"/>

    <dsp:input id="addToCart_skuId" bean="CartModifierFormHandler.items[0].catalogRefId" type="hidden" paramvalue="selectedSkuId"/>
    <dsp:input id="addToCart_qty" bean="CartModifierFormHandler.items[0].quantity" type="hidden" paramvalue="selectedQty"/>

    <dsp:input bean="CartModifierFormHandler.items[0].productId" type="hidden" paramvalue="productId"/>
    <%-- Set "originOfOrder" to "mobile" on the shopping cart, qualifying the user for mobile promotions on "UIOnly" --%>
    <dsp:input bean="CartModifierFormHandler.originOfOrder" type="hidden" value="mobile"/>

    <dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL" type="hidden"
               value="${siteContextPath}/browse/json/addToCartJSON.jsp"/>
    <dsp:input bean="CartModifierFormHandler.addItemToOrderErrorURL" type="hidden"
               value="${siteContextPath}/browse/json/addToCartJSON.jsp?errorAddToCart=true"/>
    <dsp:input bean="CartModifierFormHandler.addItemToOrder" type="hidden" value="Add to cart"/>
  </dsp:form>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/gadgets/addToCartForm.jsp#3 $$Change: 788278 $--%>

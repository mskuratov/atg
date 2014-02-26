<%--
  This page renders form for back to/update "Shopping Cart".

  Required parameters:
    productId
      ID of the product to add to the cart
    selectedSku
      SKU being added to shopping cart
    selectedQty
      Number of items being added
    ciId
      ID of commerce item being edited

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>

  <dsp:getvalueof var="errorURL" vartype="java.lang.String" value=""/>
  <dsp:form id="updateCart" formid="updateCartForm" name="updateCartForm"
            action="${pageContext.request.requestURI}" method="post">
    <dsp:input type="hidden" bean="CartModifierFormHandler.addItemToOrderErrorURL" value="${errorURL}"/>
    <dsp:input type="hidden" bean="CartModifierFormHandler.addItemCount" value="1"/>
    <dsp:input type="hidden" bean="CartModifierFormHandler.removeAndAddItemToOrderSuccessURL" value="${siteContextPath}/cart/cart.jsp"/>
    <dsp:input type="hidden" id="addToCart_skuId" bean="CartModifierFormHandler.items[0].catalogRefId" paramvalue="selectedSku.repositoryId"/>
    <dsp:input type="hidden" bean="CartModifierFormHandler.items[0].productId" paramvalue="productId"/>
    <dsp:input type="hidden" id="addToCart_qty" bean="CartModifierFormHandler.items[0].quantity" paramvalue="selectedQty"/>
    <dsp:input type="hidden" bean="CartModifierFormHandler.removalCommerceIds" paramvalue="ciId"/>
    <dsp:input type="hidden" bean="CartModifierFormHandler.removeAndAddItemToOrder" value="Update Cart"/>
  </dsp:form>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/gadgets/updateCartForm.jsp#3 $$Change: 788278 $--%>

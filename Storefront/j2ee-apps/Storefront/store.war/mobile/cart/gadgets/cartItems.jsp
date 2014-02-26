<%--
  Iterates over the list of products and renders each product according to its type.

  Form Condition:
    This gadget must be contained inside of a form.
    "CartModifierFormHandler" must be invoked from a submit button in this form for fields in this page to be processed.

  Page includes:
    /mobile/cart/gadgets/cartItem.jsp - Display cart item
    /mobile/cart/gadgets/editBox.jsp - Renderer of cart item edit block

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/commerce/promotion/GiftWithPurchaseSelectionsDroplet"/>

  <%-- Get the items in the shopping cart --%>
  <dsp:getvalueof var="items" bean="ShoppingCart.current.commerceItems"/>

  <c:forEach var="currentItem" items="${items}">
    <dsp:param name="currentItem" value="${currentItem}"/>
    <dsp:getvalueof var="commerceItemClassType" param="currentItem.commerceItemClassType"/>
    <c:choose>
      <c:when test="${commerceItemClassType == 'giftWrapCommerceItem'}">
        <%-- Filter out the giftWrapCommerceItem, but add as hidden input so it doesn't --%>
        <%-- get removed from the cart --%>
        <input type="hidden" name="<dsp:valueof param='currentItem.id'/>" value="<dsp:valueof param='currentItem.quantity'/>">
      </c:when>
      <c:otherwise>
        <dsp:droplet name="GiftWithPurchaseSelectionsDroplet">
          <dsp:param name="order" bean="ShoppingCart.current"/>
          <dsp:param name="item" param="currentItem"/>

          <%-- This commerce item have been added by GWP --%>
          <dsp:oparam name="output">
            <dsp:getvalueof var="commerceItemQty" param="currentItem.quantity"/>
            <dsp:getvalueof var="selections" param="selections"/>
            <c:forEach var="selection" items="${selections}">
              <dsp:param name="selection" value="${selection}"/>
              <%--
                Even in case this item has been added as a gift, there is also a possibility
                the Shopper added the same item(s). Check selection quantity vs commerce item quantity.
                If commerce item quantity is greater, that means we have gift items and commerce items
              --%>
              <dsp:getvalueof var="automaticQuantity" param="selection.automaticQuantity"/>
              <dsp:getvalueof var="targetedQuantity" param="selection.targetedQuantity"/>
              <dsp:getvalueof var="selectedQuantity" param="selection.selectedQuantity"/>
              <dsp:getvalueof var="selectedQty" value="${automaticQuantity + targetedQuantity + selectedQuantity}"/>
              <c:if test="${commerceItemQty > selectedQty}">
                <dsp:getvalueof var="missedQty" value="${commerceItemQty - selectedQty}"/>
                <input type="hidden" name="<dsp:valueof param='currentItem.id'/>" value="${missedQty}"/>
              </c:if>
              <dsp:include page="cartItem.jsp">
                <dsp:param name="currentItem" param="currentItem"/>
              </dsp:include>
              <dsp:include page="editBox.jsp">
                <dsp:param name="currentItem" param="currentItem"/>
              </dsp:include>
            </c:forEach>
          </dsp:oparam>

          <%-- This is regular commerce item --%>
          <dsp:oparam name="empty">
            <input type="hidden" name="<dsp:valueof param='currentItem.id'/>" value="<dsp:valueof param='currentItem.quantity'/>"/>
            <dsp:include page="cartItem.jsp">
              <dsp:param name="currentItem" param="currentItem"/>
            </dsp:include>
            <dsp:include page="editBox.jsp">
              <dsp:param name="currentItem" param="currentItem"/>
            </dsp:include>
          </dsp:oparam>
        </dsp:droplet>
      </c:otherwise>
    </c:choose>
  </c:forEach>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cart/gadgets/cartItems.jsp#3 $$Change: 788278 $--%>

<%--
  Renders commerce item information.

  Page includes:
    /mobile/global/gadgets/productLinkGenerator.jsp - Product link generator
    /mobile/cart/gadgets/cartItemImg.jsp - Item image renderer
    /mobile/global/util/displaySkuProperties.jsp - Display list of SKU properties
    /mobile/checkout/gadgets/confirmDetailedItemPrice.jsp - Display price details of the item

  Required parameters:
    currentItem
      Item to be displayed
    isCheckout
      Indicator if this order is about to checkout or has already been placed.
      Possible values:
        'true' = is about to checkout
        'false' = placed order
--%>
<dsp:page>
  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="currentItem" vartype="atg.commerce.order.CommerceItem" param="currentItem"/>
  <dsp:getvalueof var="navigable" vartype="java.lang.Boolean" param="currentItem.auxiliaryData.productRef.NavigableProducts"/>
  <dsp:getvalueof var="isCheckout" param="isCheckout"/>

  <dsp:getvalueof var="mobileStorePrefix" bean="/atg/store/StoreConfiguration.mobileStorePrefix"/>

  <div class="cartItem">
    <div class="itemImage">
      <dsp:include page="${mobileStorePrefix}/cart/gadgets/cartItemImg.jsp">
        <dsp:param name="commerceItem" param="currentItem"/>
      </dsp:include>
    </div>
    <div class="itemDescription">
      <p class="name">
        <%-- Link to the product detail page --%>
        <dsp:include page="${mobileStorePrefix}/global/gadgets/productLinkGenerator.jsp">
          <dsp:param name="product" param="currentItem.auxiliaryData.productRef"/>
          <dsp:param name="siteId" param="currentItem.auxiliaryData.siteId"/>
        </dsp:include>
        <c:choose>
          <c:when test="${not empty productUrl && not isCheckout}">
            <%-- We do not need to read any parameters as order is already been placed --%>
            <dsp:a href="${fn:escapeXml(productUrl)}">
              <dsp:valueof param="currentItem.auxiliaryData.productRef.displayName">
                <fmt:message key="mobile.common.noDisplayName"/>
              </dsp:valueof>
            </dsp:a>
          </c:when>
          <c:otherwise>
            <dsp:valueof param="currentItem.auxiliaryData.productRef.displayName">
              <fmt:message key="mobile.common.noDisplayName"/>
            </dsp:valueof>
          </c:otherwise>
        </c:choose>
      </p>

      <p class="properties">
        <dsp:include page="${mobileStorePrefix}/global/util/displaySkuProperties.jsp">
          <dsp:param name="product" param="currentItem.auxiliaryData.productRef"/>
          <dsp:param name="sku" param="currentItem.auxiliaryData.catalogRef"/>
        </dsp:include>
      </p>
    </div>
    <div class="price">
      <div class="priceContent">
        <dsp:include page="${mobileStorePrefix}/checkout/gadgets/confirmDetailedItemPrice.jsp">
          <dsp:param name="commerceItem" value="${currentItem}"/>
        </dsp:include>
      </div>
    </div>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/global/gadgets/orderItemsRenderer.jsp#2 $$Change: 742374 $--%>

<%--
  Generates item edit block.

  Page includes:
    /mobile/cart/gadgets/cartItemImg.jsp - Display product image

  Required parameters:
    currentItem
      The commerce item to render

  Optional parameters:
    None
--%>
<dsp:page>
  <div class="cartItemEdit">
    <%-- Product image --%>
    <div class="image">
      <dsp:include page="cartItemImg.jsp">
        <dsp:param name="commerceItem" param="currentItem"/>
      </dsp:include>
    </div>

    <div class="cover"></div>

    <%-- Product name --%>
    <p class="name"><dsp:valueof param="currentItem.auxiliaryData.productRef.displayName"/></p>

    <%-- Show product detail page link and the share button only if there is a URL --%>
    <c:if test="${not empty productUrl}">
      <dsp:a href="javascript:void(0);" iclass="productDetailPageLink">
        <%-- SKU properties - TO POPULATE --%>
        <span class="properties"></span>
        <%-- Product quantity --%>
        <dsp:getvalueof var="qty" param="currentItem.quantity"/>
        <span class="qty">
          <fmt:message key="mobile.common.quantity" var="quantityAbbrExpansion"/>
          <abbr title="${quantityAbbrExpansion}"><fmt:message key="mobile.cart.qty"/></abbr>
        </span>
        <span class="qty icon-ArrowLeft">
          <c:choose>
            <c:when test="${not empty qty}">${qty}</c:when>
            <c:otherwise>1</c:otherwise>
          </c:choose>
        </span>
      </dsp:a>

      <fmt:message key="mobile.cart.share" var="shareTitle"/>
      <a href="javascript:void(0);" title="${shareTitle}" class="shareLink icon-Share"></a>
    </c:if>

    <%-- Show the delete button even if there is no URL. This will enable users
         to delete free gifts that are part of the order --%>
    <fmt:message key="mobile.common.delete" var="removeTitle"/>
    <a href="javascript:void(0);" title="${removeTitle}" class="moveLink icon-Remove"></a>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cart/gadgets/editBox.jsp#3 $$Change: 788278 $--%>

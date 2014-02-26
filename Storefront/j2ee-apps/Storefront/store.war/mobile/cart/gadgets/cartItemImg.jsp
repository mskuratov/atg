<%--
  Show the image of the product, if you have the SKU image let us show it instead.
  If no SKU image then select the best product for display of the image.

  Page includes:
    None

  Required parameters:
    commerceItem
      The commerce item whose image we should display

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/Cache"/>

  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="imageUrl" param="commerceItem.auxiliaryData.catalogRef.smallImage"/>
  <dsp:getvalueof var="productPromoImageUrl" param="commerceItem.auxiliaryData.productRef.smallImage.url"/>
  <dsp:getvalueof var="productName" vartype="java.lang.String" param="commerceItem.auxiliaryData.productRef.displayName"/>
  <dsp:getvalueof var="productId" vartype="java.lang.String" param="commerceItem.auxiliaryData.productRef.repositoryId"/>
  <dsp:getvalueof var="commerceItemId" vartype="java.lang.String" param="commerceItem.id"/>

  <dsp:getvalueof var="cache_key" value="bp_fpti_cart_${commerceItemId}_${productId}_${imageUrl}_${productPromoImageUrl}"/>

  <dsp:droplet name="Cache">
    <dsp:param name="key" value="${cache_key}"/>
    <dsp:oparam name="output">
      <c:choose>
        <c:when test="${not empty imageUrl}">
          <%-- Show SKU thumbnail image --%>
          <img src="${imageUrl}" alt="${productName}"/>
        </c:when>
        <c:otherwise>
          <%-- SKU thumbnail image is unavailable --%>
          <c:choose>
            <c:when test="${not empty productPromoImageUrl}">
              <%-- Show product thumbnail image --%>
              <img src="${productPromoImageUrl}" alt="${productName}"/>
            </c:when>
            <c:otherwise>
              <%-- Image unavailable --%>
              <img src="/crsdocroot/content/images/products/small/MissingProduct_small.jpg" alt="${productName}"/>
            </c:otherwise>
          </c:choose>
        </c:otherwise>
      </c:choose>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cart/gadgets/cartItemImg.jsp#2 $$Change: 742374 $--%>

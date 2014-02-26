<%--
  This page renders view block for each item of the "Shopping Cart".

  Form Condition:
    This gadget must be contained inside of a form.
    "CartModifierFormHandler" must be invoked from a submit
    button in this form for fields in this page to be processed.

  Page includes:
    /mobile/global/gadgets/productLinkGenerator.jsp - Product link generator
    /mobile/cart/gadgets/cartItemImg.jsp - Display product image
    /mobile/cart/gadgets/detailedItemPrice.jsp - Display price details

  Required parameters:
    currentItem
      The commerce item to render

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:getvalueof var="skuType" vartype="java.lang.String" param="currentItem.auxiliaryData.catalogRef.type"/>
  <dsp:getvalueof var="mobileStorePrefix" bean="/atg/store/StoreConfiguration.mobileStorePrefix"/>
  <dsp:getvalueof var="productDisplayName" vartype="java.lang.String" param="currentItem.auxiliaryData.productRef.displayName"/>
  <c:if test="${empty productDisplayName}">
    <fmt:message var="productDisplayName" key="mobile.common.noDisplayName"/>
  </c:if>

  <%-- Dataset attribute: Product detail page URL - for "Edit" --%>
  <c:set var="productpageurl4edit" value=""/>
  <%-- Dataset attribute: Product detail page URL - for "Share" --%>
  <c:set var="productpageurl4share" value=""/>
  <%-- Dataset attribute: "Color" (for "clothing" SKUs) or "WoodFinish" (for "furniture" SKUs) --%>
  <c:set var="skuProperty1" value=""/>
  <%-- Dataset attribute: "Size" (for "clothing" SKUs) --%>
  <c:set var="skuProperty2" value=""/>

  <%-- Full CRS link to the product detail page --%>
  <dsp:include page="${mobileStorePrefix}/global/gadgets/productLinkGenerator.jsp">
    <dsp:param name="product" param="currentItem.auxiliaryData.productRef"/>
    <dsp:param name="siteId" param="currentItem.auxiliaryData.siteId"/>
  </dsp:include>

  <c:if test="${not empty productUrl}">
    <%--
      Read "selectedColor", "selectedSize" parameters as they will NOT be added
      by "productLinkGenerator.jsp", if "ATG Search" is not enabled
    --%>
    <c:choose>
      <c:when test="${skuType == 'clothing-sku'}">
        <dsp:getvalueof var="skuProperty1" vartype="java.lang.String" param="currentItem.auxiliaryData.catalogRef.color"/>
        <dsp:getvalueof var="skuProperty2" vartype="java.lang.String" param="currentItem.auxiliaryData.catalogRef.size"/>
        <c:url var="pageurl" value="${productUrl}" context="/">
          <c:param name="selectedSize"><dsp:valueof param="currentItem.auxiliaryData.catalogRef.size"/></c:param>
          <c:param name="selectedColor"><dsp:valueof param="currentItem.auxiliaryData.catalogRef.color"/></c:param>
        </c:url>
      </c:when>
      <c:when test="${skuType == 'furniture-sku'}">
        <dsp:getvalueof var="skuProperty1" vartype="java.lang.String" param="currentItem.auxiliaryData.catalogRef.woodFinish"/>
        <c:url var="pageurl" value="${productUrl}" context="/">
          <c:param name="selectedColor"><dsp:valueof param="currentItem.auxiliaryData.catalogRef.woodFinish"/></c:param>
        </c:url>
      </c:when>
      <c:otherwise>
        <%-- If none above, then single SKU or multi-SKU ? --%>
        <dsp:getvalueof var="childSKUs" param="currentItem.auxiliaryData.productRef.childSKUs"/>
        <c:choose>
          <c:when test="${fn:length(childSKUs) > 1}">
            <c:url var="pageurl" value="${productUrl}" context="/">
              <%-- Selected feature for multi-SKU items is item selected SKU itself --%>
              <c:param name="selectedFeature"><dsp:valueof param="currentItem.auxiliaryData.catalogRef.id"/></c:param>
            </c:url>
          </c:when>
          <c:otherwise>
            <c:url var="pageurl" value="${productUrl}" context="/"/>
          </c:otherwise>
        </c:choose>
      </c:otherwise>
    </c:choose>

    <%-- Link to product page - for "Edit" --%>
    <c:url var="productpageurl4edit" value="${pageurl}" context="/">
      <c:param name="selectedQty"><dsp:valueof param="currentItem.quantity"/></c:param>
      <c:param name="ciId"><dsp:valueof param="currentItem.id"/></c:param>
    </c:url>

    <%-- Link to product page - for "Share" --%>
    <%-- "Subject" --%>
    <fmt:message var="emailSubject" key="mobile.emailtemplates_emailAFriend.subject">
      <fmt:param><dsp:valueof bean="/atg/multisite/Site.name"/></fmt:param>
    </fmt:message>
    <%-- "Body" --%>
    <dsp:getvalueof var="serverName" bean="/atg/store/StoreConfiguration.siteHttpServerName"/>
    <dsp:getvalueof var="serverPort" bean="/atg/store/StoreConfiguration.siteHttpServerPort"/>
    <dsp:getvalueof var="httpServer" value="http://${serverName}:${serverPort}"/>
    <fmt:message var="emailBody" key="mobile.emailtemplates_emailAFriend.message">
      <fmt:param value="${productDisplayName} (${httpServer}${productUrl})"/>
      <fmt:param><dsp:valueof bean="/atg/multisite/Site.name"/></fmt:param>
    </fmt:message>
    <c:set var="emailBody" value='${fn:replace(emailBody, "&", "%26")}'/>
    <c:set var="productpageurl4share" value="mailto:?subject=${emailSubject}&body=${emailBody}"/>
  </c:if>

  <%-- Item view block --%>
  <div class="cartItem" role="link" id="cartItem_<dsp:valueof param='currentItem.id'/>"
      ${'onclick="CRSMA.cart.showCartItemEditBox(this)"'}
      data-productpageurl4edit="${fn:escapeXml(productpageurl4edit)}"
      data-productpageurl4share="${fn:escapeXml(productpageurl4share)}"
      data-skuproperty1="${skuProperty1}" data-skuproperty2="${skuProperty2}">
    <div class="itemImage">
      <dsp:include page="cartItemImg.jsp">
        <dsp:param name="commerceItem" param="currentItem"/>
      </dsp:include>
    </div>

    <div class="itemDescription">
      <p class="name">
        <c:choose>
          <c:when test="${not empty productUrl}">
            <a href="${productUrl}">
              <dsp:valueof value="${productDisplayName}"/>
            </a>
          </c:when>
          <c:otherwise>
            <dsp:valueof value="${productDisplayName}"/>
          </c:otherwise>
        </c:choose>
      </p>

      <%--
        SKU properties.
        For the "clothing-sku" type display the following:
          1. size
          2. color
        For the "furniture-sku" type display the following:
          1. woodFinish
      --%>
      <p class="properties">
        <c:if test="${not empty skuProperty1}">
          <c:out value="${skuProperty1}"/><c:if test="${not empty skuProperty2}"><c:out value=","/></c:if>
        </c:if>
        <c:if test="${not empty skuProperty2}">
          <c:out value="${skuProperty2}"/>
        </c:if>
      </p>
    </div>

    <div class="priceContent">
      <dsp:include page="detailedItemPrice.jsp">
        <dsp:param name="commerceItem" param="currentItem"/>
      </dsp:include>
    </div>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cart/gadgets/cartItem.jsp#3 $$Change: 788278 $--%>

<%--
  Renders product details to the page.

  Page includes:
    /mobile/browse/gadgets/productPrice.jsp - Display product price
    /mobile/browse/gadgets/quantityPickerList.jsp - Renders a list of quantity selections
    /mobile/global/gadgets/productsHorizontalList.jsp - Renders the "Related Items" slider
    /mobile/global/gadgets/recentlyViewed.jsp - Renders the "Recently Viewed Items" slider

  Required parameters:
    picker
      Path to the JSP that will render buttons for this product
    product
      Product repository item whose details are being displayed

  Optional parameters:
    selectedSku
      Currently selected SKU. If present, signifies that this is a single-SKU product
    selectedQty
      Currently selected quantity
    selectedSize
      Currently selected size
    selectedColor
      Currently selected color
    availableSizes
      Available sizes for this product
    availableColors
      Available colors for this product
    oneSize
      If true, this product only has one size
    oneColor
      If true, this product only has one color
    isUpdateCart
      If true, changes default button text
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/droplet/ItemSiteGroupFilterDroplet"/>
  <dsp:importbean bean="/atg/store/droplet/SkuAvailabilityLookup"/>

  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="selectedQty" param="selectedQty"/>
  <dsp:getvalueof var="selectedSku" param="selectedSku"/>
  <dsp:getvalueof var="displayName" param="product.displayName"/>

  <ul class="productDetails">
    <li class="productTitle">
      <a href="javascript:history.back(1)" class="icon-ArrowLeftBefore"><c:out value="${displayName}"/></a>
    </li>
    <li class="itemPickers ${empty selectedSku ? '' : 'expanded'}">
      <div class="imageContainer">
        <dsp:getvalueof var="url" param="product.largeImage.url" idtype="java.lang.String"/>
        <c:if test="${empty url}">
          <c:set var="url" value="/crsdocroot/content/images/products/large/MissingProduct_large.jpg"/>
        </c:if>
        <a href="javascript:CRSMA.product.toggleProductView()">
          <img id="productImage" src="${url}" alt="${displayName}"/>
        </a>
      </div>
      <div onclick="CRSMA.product.expandPickers();" class="pickersContent">
        <dsp:include page="gadgets/productPrice.jsp">
          <dsp:param name="product" param="product"/>
          <dsp:param name="selectedSku" param="selectedSku"/>
        </dsp:include>
        <ul>
          <dsp:getvalueof var="picker" param="picker"/>
          <c:if test="${not empty picker}">
            <dsp:include page="${picker}">
              <dsp:param name="product" param="product"/>
              <dsp:param name="selectedSku" param="selectedSku"/>
              <dsp:param name="selectedSize" param="selectedSize"/>
              <dsp:param name="selectedColor" param="selectedColor"/>
              <dsp:param name="availableSizes" param="availableSizes"/>
              <dsp:param name="availableColors" param="availableColors"/>
              <dsp:param name="oneSize" param="oneSize"/>
              <dsp:param name="oneColor" param="oneColor"/>
            </dsp:include>
          </c:if>
          <li id="qtyContainer">
            <dsp:include page="gadgets/quantityPickerList.jsp">
              <dsp:param name="selectedValue" param="selectedQty"/>
              <dsp:param name="id" value="qtySelect"/>
            </dsp:include>
          </li>
        </ul>
        <div id="addToCartButton" class="centralButton">
          <%-- Determine default button text --%>
          <dsp:getvalueof var="isUpdateCart" param="isUpdateCart"/>

          <%-- Default values --%>
          <fmt:message var="buttonLabel">
            <c:choose>
              <c:when test="${empty isUpdateCart || !isUpdateCart}">mobile.common.button.addToCartText</c:when>
              <c:otherwise>mobile.productDetails.backtocart</c:otherwise>
            </c:choose>
          </fmt:message>

          <c:set var="buttonText" value=""/>

          <%-- Customization --%>
          <c:if test="${not empty selectedSku}">
            <dsp:droplet name="SkuAvailabilityLookup">
              <dsp:param name="product" param="product"/>
              <dsp:param name="skuId" value="${selectedSku.repositoryId}"/>
              <dsp:oparam name="preorderable">
                <fmt:message key="mobile.productDetails.preorderText" var="buttonText"/>

                <c:if test="${empty isUpdateCart || !isUpdateCart}">
                  <fmt:message key="mobile.productDetails.preorderLabel" var="buttonLabel"/>
                </c:if>
              </dsp:oparam>
              <dsp:oparam name="backorderable">
                <fmt:message key="mobile.productDetails.backorderText" var="buttonText"/>
              </dsp:oparam>
              <dsp:oparam name="unavailable">
                <fmt:message key="mobile.productDetails.emailMeText" var="buttonLabel"/>
                <fmt:message key="mobile.common.temporarilyOutOfStock" var="buttonText"/>
              </dsp:oparam>
            </dsp:droplet>
          </c:if>

          <span id="buttonText">
            <c:out value="${buttonText}"/>
          </span>
          <button class="mainActionButton" onclick="CRSMA.product.actionHandler(event);" ${empty selectedSku ? 'disabled' : ''}>
            <c:out value="${buttonLabel}"/>
          </button>
        </div>
      </div>
    </li>

    <li class="itemDetails">
      <p class="detailsTitle"><fmt:message key="mobile.productDetails.details"/></p>

      <p class="detailsContent">
        <dsp:getvalueof var="LongDescription" param="product.longDescription"/>
        <c:choose>
          <c:when test="${not empty LongDescription}">
            <c:out value="${LongDescription}"></c:out>
          </c:when>
          <c:otherwise>
            <dsp:valueof param="product.description"/>
          </c:otherwise>
        </c:choose>
      </p>

      <%--
        "ItemSiteGroupFilterDroplet" droplet filters out items that don't belong to the same cart sharing site group
        as the current site.

        Input parameters:
          collection
            Collection of products to filter based on the site group

        Output parameters:
          filteredCollection
            Filtered collection
      --%>
      <dsp:droplet name="ItemSiteGroupFilterDroplet">
        <dsp:param name="collection" param="product.relatedProducts"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="relatedProducts" param="filteredCollection"/>
        </dsp:oparam>
      </dsp:droplet>
    </li>

    <%-- Display "Related Items" slider panel, if any --%>
    <c:if test="${not empty relatedProducts}">
      <div class="sliderTitle">
        <fmt:message key="mobile.productDetails.related" />
      </div>
      <dsp:include page="${mobileStorePrefix}/global/gadgets/productsHorizontalList.jsp">
        <dsp:param name="products" value="${relatedProducts}" />
        <dsp:param name="index" value="1" />
      </dsp:include>
    </c:if>

    <%-- Display "Recently Viewed Items" slider panel, if any --%>
    <dsp:include page="${mobileStorePrefix}/global/gadgets/recentlyViewed.jsp">
      <dsp:param name="exclude" param="product.id"/>
      <dsp:param name="index" value="2" />
    </dsp:include>
  </ul>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/productDetailsContainer.jsp#4 $$Change: 790558 $--%>

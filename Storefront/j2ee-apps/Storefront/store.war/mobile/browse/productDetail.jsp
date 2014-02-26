<%--
  Renders product details content and hidden forms.
  
  Page includes:
    /mobile/browse/productDetailsContainer.jsp - Product details renderer
    /mobile/browse/gadgets/addToCartForm.jsp - Hidden form for "Add to cart"
    /mobile/browse/gadgets/updateCartForm.jsp - Hidden form for "Update cart"
    /mobile/browse/gadgets/emailMeForm.jsp - Email notification form
    /mobile/browse/json/getSkuJSON.jsp - JSON renderer for a product SKU

  Required parameters:
    product
      Product to display
    picker
      Path to the JSP that will render buttons for this product

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
    ciId 
      Id of commerce item being edited. This parameter is only present when editing the cart
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/ProductBrowsed"/>
  <dsp:importbean bean="/atg/commerce/order/processor/SetProductRefs"/>

  <fmt:message var="titleString" key="mobile.productDetails.pageTitle"/>
  <crs:mobilePageContainer titleString="${titleString}" displayModal="true">
    <jsp:attribute name="modalContent">
      <dsp:include page="gadgets/emailMeForm.jsp">
        <dsp:param name="productId" param="product.repositoryId"/>
        <dsp:param name="selectedSkuId" param="selectedSku.repositoryId"/>
      </dsp:include>
      <%-- "Loading..." message box --%>
      <dsp:include page="${mobileStorePrefix}/global/gadgets/loadingWindow.jsp"/>
    </jsp:attribute>

    <jsp:body>
      <c:set var="isMissingProduct" value="${true}"/>
      <dsp:getvalueof var="missingProductId" vartype="java.lang.String" bean="SetProductRefs.substituteDeletedProductId"/>
      <c:if test="${missingProductId != param.product.repositoryId}">
        <c:set var="isMissingProduct" value="${false}"/>
        <%--
          Notify anyone who cares that the current product has been viewed.
          This droplet sends a special "ViewItemEvent" JMS message.
          This event is required in order for the productId to be automatically
          placed in the page body by the auto-tagging code for "Recommendations".
          The "ProductTrackingCodeProcessor" takes care of including the productId in the page body.
        --%>
        <dsp:droplet name="ProductBrowsed">
          <dsp:param name="eventobject" param="product"/>
        </dsp:droplet>

        <%-- Quantity defaults to 1 --%>
        <c:if test="${empty param.selectedQty}">
          <dsp:setvalue param="selectedQty" value="1"/>
        </c:if>

        <dsp:getvalueof var="ciId" param="ciId"/>

        <dsp:include page="json/getSkuJSON.jsp">
          <dsp:param name="product" param="product"/>
        </dsp:include>

        <div>
          <dsp:include page="productDetailsContainer.jsp">
            <dsp:param name="product" param="product"/>
            <dsp:param name="picker" param="picker"/>
            <dsp:param name="selectedQty" param="selectedQty"/>
            <dsp:param name="selectedSize" param="selectedSize"/>
            <dsp:param name="selectedColor" param="selectedColor"/>
            <dsp:param name="selectedSku" param="selectedSku"/>
            <dsp:param name="availableSizes" param="availableSizes"/>
            <dsp:param name="availableColors" param="availableColors"/>
            <dsp:param name="oneSize" param="oneSize"/>
            <dsp:param name="oneColor" param="oneColor"/>
            <dsp:param name="isUpdateCart" value="${not empty ciId}"/>
          </dsp:include>
        </div>

        <c:choose>
          <c:when test="${empty ciId}">
            <%-- "Add to cart" --%>
            <dsp:include page="gadgets/addToCartForm.jsp">
              <dsp:param name="productId" param="product.repositoryId"/>
              <dsp:param name="selectedQty" param="selectedQty"/>
              <dsp:param name="selectedSkuId" param="selectedSku.repositoryId"/>
            </dsp:include>
          </c:when>
          <c:otherwise>
            <%-- "Update cart" --%>
            <dsp:include page="gadgets/updateCartForm.jsp">
              <dsp:param name="productId" param="product.repositoryId"/>
              <dsp:param name="selectedQty" param="selectedQty"/>
              <dsp:param name="selectedSku" param="selectedSku"/>
              <dsp:param name="ciId" value="${ciId}"/>
            </dsp:include>
          </c:otherwise>
        </c:choose>
      </c:if>

      <script>
        $(document).ready(function() {
          <c:if test="${!isMissingProduct}">
            CRSMA.product.init("${siteContextPath}/");
          </c:if>
          CRSMA.global.hideLoadingWindow();
        });
      </script>
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/productDetail.jsp#3 $$Change: 788278 $--%>

<%--
  This page is a template for products with color, size and quantity pickers.

  Page includes:
    /mobile/browse/productDetail.jsp - Renders product details

  Required Parameters:
    productId
      Product ID whose details are being displayed

  Optional Parameters:
    selectedColor
      Selected color name
    selectedSize
      Selected size name
    selectedQty
      Selected quantity
    ciId 
      ID of commerce item being edited. This parameter is only present when editing the cart
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/store/droplet/CatalogItemFilterDroplet"/>
  <dsp:importbean bean="/atg/store/droplet/ColorSizeDroplet"/>

  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" param="productId"/>
    <dsp:oparam name="output">
      <dsp:droplet name="CatalogItemFilterDroplet">
        <dsp:param name="collection" param="element.childSKUs"/>
        <dsp:oparam name="output">
          <dsp:droplet name="ColorSizeDroplet">
            <dsp:param name="skus" param="filteredCollection"/>
            <dsp:param name="product" param="element"/>
            <dsp:param name="selectedColor" param="selectedColor"/>
            <dsp:param name="selectedSize" param="selectedSize"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="availableColors" param="availableColors"/>
              <dsp:getvalueof var="availableSizes" param="availableSizes"/>

              <dsp:getvalueof var="selectedSku" param="selectedSku"/>

              <c:set var="oneColor" value="${fn:length(availableColors) == 1}"/>
              <c:set var="oneSize" value="${fn:length(availableSizes) == 1}"/>

              <c:if test="${oneColor}">
                <dsp:setvalue param="selectedColor" paramvalue="element.childSKUs[0].color"/>
              </c:if>

              <c:if test="${oneSize}">
                <dsp:setvalue param="selectedSize" paramvalue="element.childSKUs[0].size"/>
              </c:if>

              <dsp:include page="productDetail.jsp">
                <dsp:param name="product" param="element"/>
                <dsp:param name="picker" value="gadgets/colorSizePickers.jsp"/>
                <dsp:param name="selectedSku" value="${selectedSku}"/>
                <dsp:param name="selectedSize" param="selectedSize"/>
                <dsp:param name="selectedColor" param="selectedColor"/>
                <dsp:param name="selectedQty" param="selectedQty"/>
                <dsp:param name="availableSizes" value="${availableSizes}"/>
                <dsp:param name="availableColors" value="${availableColors}"/>
                <dsp:param name="oneSize" value="${oneSize}"/>
                <dsp:param name="oneColor" value="${oneColor}"/>
                <dsp:param name="ciId" param="ciId"/>
              </dsp:include>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/productDetailColorSizePicker.jsp#2 $$Change: 742374 $--%>

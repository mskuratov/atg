<%--
  This page is a template for products with wood finishes.

  Page includes:
    /mobile/browse/productDetail.jsp - Renders product details

  Required parameters:
    productId
      Product ID whose details are being displayed

  Optional parameters:
    selectedColor
      Selected color name
    selectedQty
      Selected quantity
    ciId
      ID of commerce item being edited
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/store/droplet/CatalogItemFilterDroplet"/>
  <dsp:importbean bean="/atg/store/droplet/WoodFinishDroplet"/>

  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" param="productId"/>
    <dsp:oparam name="output">
      <dsp:droplet name="CatalogItemFilterDroplet">
        <dsp:param name="collection" param="element.childSKUs"/>
        <dsp:oparam name="output">
          <dsp:droplet name="WoodFinishDroplet">
            <dsp:param name="skus" param="filteredCollection"/>
            <dsp:param name="selectedColor" param="selectedColor"/>
            <dsp:param name="product" param="element"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="availableColors" param="availableColors"/>

              <c:set var="oneColor" value="${fn:length(availableColors) == 1}"/>

              <dsp:getvalueof var="selectedSku" param="selectedSku"/>

              <c:if test="${oneColor}">
                <dsp:setvalue param="selectedColor" paramvalue="element.childSKUs[0].woodFinish"/>
              </c:if>

              <dsp:include page="productDetail.jsp">
                <dsp:param name="product" param="element"/>
                <dsp:param name="picker" value="gadgets/woodFinishPicker.jsp"/>
                <dsp:param name="selectedSku" value="${selectedSku}"/>
                <dsp:param name="selectedColor" param="selectedColor"/>
                <dsp:param name="selectedQty" param="selectedQty"/>
                <dsp:param name="availableColors" value="${availableColors}"/>
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
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/productDetailWoodFinishPicker.jsp#2 $$Change: 742374 $--%>

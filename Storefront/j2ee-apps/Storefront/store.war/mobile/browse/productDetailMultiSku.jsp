<%--
  This page is a template for products with multiple distinct SKUs.

  Page includes:
    /mobile/browse/productDetail.jsp - Renders product details

  Required parameters:
    productId
      Product ID whose details being displayed

  Optional parameters:
    selectedFeature
      Selected feature for multi-SKU items (selected SKU identifier)
    selectedQty
      Selected quantity
    ciId
      ID of commerce item being edited
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
  <dsp:importbean bean="/atg/store/droplet/CatalogItemFilterDroplet"/>

  <%-- Find SKU item by ID --%>
  <dsp:droplet name="SKULookup">
    <dsp:param name="id" param="selectedFeature"/>
    <dsp:param name="filterByCatalog" value="false"/>
    <dsp:param name="filterBySite" value="false"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="selectedSku" param="element"/>
    </dsp:oparam>
  </dsp:droplet>

  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" param="productId"/>
    <dsp:oparam name="output">
      <dsp:droplet name="CatalogItemFilterDroplet">
        <dsp:param name="collection" param="element.childSKUs"/>
        <dsp:oparam name="output">
          <dsp:include page="productDetail.jsp">
            <dsp:param name="product" param="element"/>
            <dsp:param name="picker" value="gadgets/multiSkuPicker.jsp"/>
            <dsp:param name="selectedSku" value="${selectedSku}"/>
            <dsp:param name="selectedQty" param="selectedQty"/>
            <dsp:param name="ciId" param="ciId"/>
          </dsp:include>
        </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/productDetailMultiSku.jsp#2 $$Change: 742374 $--%>

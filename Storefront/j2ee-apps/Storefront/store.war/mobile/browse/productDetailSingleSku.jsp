<%--
  This page is a template for single-SKU products.

  Page includes:
    /mobile/browse/productDetail.jsp - Renders product details

  Required parameters:
    productId
      Product ID whose details are being displayed

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>

  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" param="productId"/>
    <dsp:oparam name="output">
      <dsp:include page="productDetail.jsp">
        <dsp:param name="product" param="element"/>
        <dsp:param name="selectedQty" param="selectedQty"/>
        <dsp:param name="selectedSku" param="element.childSKUs[0]"/>
        <dsp:param name="ciId" param="ciId"/>
      </dsp:include>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/productDetailSingleSku.jsp#2 $$Change: 742374 $--%>

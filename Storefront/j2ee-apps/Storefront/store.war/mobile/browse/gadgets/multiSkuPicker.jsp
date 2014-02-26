<%--
  Renders pickers for a multi-SKU product.

  Page includes:
    /mobile/browse/gadgets/pickerList.jsp - Renders a list of SKU picker selections

  Required parameters:
    product
      Current product
    selectedSku
      Currently-selected SKU

  Unused parameters:
    availableSizes
    availableColors
    selectedSize
    selectedColor
    oneSize
    oneColor
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/droplet/CatalogItemFilterDroplet"/>

  <fmt:message key="mobile.facet.label.Feature" var="featureLabel"/>

  <%--
    By this point, "CatalogItemFilterDroplet" has already been used in "productDetailMultiSku.jsp".
    However, passing the resulting "filteredCollection" would mean adding another parameter to the
    growing list. Instead, we opt to use CatalogItemFilterDroplet again
  --%>
  <dsp:droplet name="CatalogItemFilterDroplet">
    <dsp:param name="collection" param="product.childSKUs"/>
    <dsp:oparam name="output">
      <li>
        <dsp:include page="pickerList.jsp">
          <dsp:param name="id" value="featureSelect"/>
          <dsp:param name="collection" param="filteredCollection"/>
          <dsp:param name="type" value="feature"/>
          <dsp:param name="selectedValue" param="selectedSku.repositoryId"/>
          <dsp:param name="valueProperty" value="repositoryId"/>
          <dsp:param name="labelProperty" value="displayName"/>
          <dsp:param name="defaultLabel" value="${featureLabel}"/>
        </dsp:include>
      </li>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/gadgets/multiSkuPicker.jsp#3 $$Change: 768606 $--%>

<%--
  Renders pickers for a color/size SKU.

  Page includes:
    /mobile/browse/gadgets/pickerList.jsp - Renders a list of SKU picker selections

  Required parameters:
    availableSizes
      Available sizes for this product
    availableColors
      Available colors for this product

  Optional parameters:
    selectedSize
      Currently selected size
    selectedColor
      Currently selected color
    oneSize
      If true, this product only has one size
    oneColor
      If true, this product only has one color

  Unused parameters:
    product
    selectedSku
--%>
<dsp:page>
  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="availableColors" param="availableColors"/>
  <dsp:getvalueof var="availableSizes" param="availableSizes"/>

  <c:if test="${fn:length(availableColors) > 0}">
    <li>
      <fmt:message key="mobile.common.color" var="colorLabel"/>
      <dsp:include page="pickerList.jsp">
        <dsp:param name="id" value="colorSelect"/>
        <dsp:param name="collection" param="availableColors"/>
        <dsp:param name="type" value="color"/>
        <dsp:param name="selectedValue" param="selectedColor"/>
        <dsp:param name="disabled" param="oneColor"/>
        <dsp:param name="defaultLabel" value="${colorLabel}"/>
        <dsp:param name="valueProperty" value="name"/>
      </dsp:include>
    </li>
  </c:if>
  <c:if test="${fn:length(availableSizes) > 0}">
    <li>
      <fmt:message key="mobile.common.size" var="sizeLabel"/>
      <dsp:include page="pickerList.jsp">
        <dsp:param name="id" value="sizeSelect"/>
        <dsp:param name="collection" param="availableSizes"/>
        <dsp:param name="type" value="size"/>
        <dsp:param name="selectedValue" param="selectedSize"/>
        <dsp:param name="disabled" param="oneSize"/>
        <dsp:param name="defaultLabel" value="${sizeLabel}"/>
        <dsp:param name="valueProperty" value="name"/>
      </dsp:include>
    </li>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/gadgets/colorSizePickers.jsp#3 $$Change: 768606 $--%>

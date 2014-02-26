<%--
  Renders pickers for a furniture product.

  Page includes:
    /mobile/browse/gadgets/pickerList.jsp - Renders a list of SKU picker selections

  Required parameters:
    availableColors
      Available colors for this product

  Optional parameters:
    selectedColor
      Currently selected color
    oneColor
      If true, this product only has one color

  Unused parameters:
    product
    selectedSku
    availableSizes
    selectedSize
    oneSize
--%>
<dsp:page>
  <li>
    <fmt:message key="mobile.common.woodFinish" var="finishLabel"/>
    <dsp:include page="pickerList.jsp">
      <dsp:param name="id" value="woodFinishSelect"/>
      <dsp:param name="collection" param="availableColors"/>
      <dsp:param name="type" value="woodFinish"/>
      <dsp:param name="selectedValue" param="selectedColor"/>
      <dsp:param name="disabled" param="oneColor"/>
      <dsp:param name="defaultLabel" value="${finishLabel}"/>
      <dsp:param name="valueProperty" value="name"/>
    </dsp:include>
  </li>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/gadgets/woodFinishPicker.jsp#3 $$Change: 768606 $--%>

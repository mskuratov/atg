<%--
  Renders a list of SKU picker selections.

  Required parameters:
    collection
      Collection of values to render
    valueProperty
      Name of the property used for the item value
    type
      'type' of value being selected. This must correspond exactly to the one of the
      values passed to "pickerSelectFunction" in "product.js"
    id
      Value for the 'id' attribute of the rendered picker

  Optional parameters:
    selectedValue
      Currently selected value
    disabled
      If present, causes the picker to be disabled
    defaultLabel
      Label to display when there is no selection
    labelProperty
      Name of the property used for the item label. Defaults to "valueProperty"
--%>
<dsp:page>
  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="collection" param="collection"/>
  <dsp:getvalueof var="type" param="type"/>
  <dsp:getvalueof var="selectedValue" param="selectedValue"/>
  <dsp:getvalueof var="defaultLabel" param="defaultLabel"/>
  <dsp:getvalueof var="disabled" param="disabled"/>
  <dsp:getvalueof var="id" param="id"/>
  <dsp:getvalueof var="valueProperty" param="valueProperty"/>
  <dsp:getvalueof var="labelProperty" param="labelProperty"/>

  <%-- Check if some item will be marked as selected in dropdown --%>
  <%-- It is used for Chrome browser workaround, see setting of 'selected'-attribute in the defaultLabel option (Feature, Color, Size) below--%>
  <c:set var="isItemSelected" value="false"/>
  <c:forEach var="item" items="${collection}">
    <c:set var="itemValue" value="${item[valueProperty]}"/>
    <c:if test="${itemValue == selectedValue}">
      <c:set var="isItemSelected" value="true"/>
    </c:if>
  </c:forEach>

  <%-- Label included for accessibility --%>
  <label for="${id}" class="noText">
    <select title="${empty selectedValue ? "" : defaultLabel}" id="${id}" name="${type}" onchange="CRSMA.product.pickerSelect(event);"
            onclick="event.stopPropagation(); CRSMA.product.expandPickers(true);"
            ontouchend="event.stopPropagation(); CRSMA.product.expandPickers(true);"
            class="${empty selectedValue ? '' : 'selected'}"
            ${(not empty disabled && disabled) ? 'disabled' : ''}>

      <c:if test="${not empty defaultLabel}">
        <%--
          Default option is always disabled.
          Note, that newer versions of Chrome browser (it was firstly detected in '18.0.1025.142') don't select 'disabled' 
          option by default if it wasn't marked as 'selected' also.

          As a result, browser selects first picker item instead of picker's label (Feature, Color, Size etc.) if there are no selected items.
          To make 'label' selected in this situation, we first check if there are no any selected items in dropdown
          and explicitly set 'selected' for the label-option(Feature, Color, Size etc) --%>
        <option value="" label="${defaultLabel}" disabled="disabled" ${!isItemSelected ? 'selected' : ''}/>
      </c:if>
      <c:forEach var="item" items="${collection}">
        <c:set var="itemValue" value="${item[valueProperty]}"/>
        <c:set var="itemLabel" value="${(empty labelProperty) ? itemValue : item[labelProperty]}"/>
        <option value="${itemValue}" label="${itemLabel}" ${itemValue == selectedValue ? 'selected' : ''}/>
      </c:forEach>
    </select>
  </label>

  <%--
    Script registers picker type (color, size, feature, woodFinish).
    It's used in "CRSMA.product.getSelectedSku" js-function to get SKU from predefined map.
  --%>
  <script>
    CRSMA.product.registerPickerType("${type}");
  </script>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/gadgets/pickerList.jsp#5 $$Change: 793006 $--%>

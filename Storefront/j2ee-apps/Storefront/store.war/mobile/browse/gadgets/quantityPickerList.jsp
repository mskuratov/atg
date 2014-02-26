<%--
  Renders a list of quantity selections. Quantity "1" is selected by default.

  Required parameters:
    id
      Value for the 'id' attribute of the rendered picker

  Optional parameters:
    selectedValue
      Currently selected value
    max
      Maximum quantity to display in list. Defaults to 10

  Unused parameters:
    disabled
      If present, causes the picker to be disabled
--%>
<dsp:page>
  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="id" param="id"/>
  <dsp:getvalueof var="selectedValue" param="selectedValue"/>
  <dsp:getvalueof var="max" param="max"/>

  <c:if test="${empty selectedValue}">
    <c:set var="selectedValue" value="1"/>
  </c:if>

  <label for="${id}" class="noText" onclick="">
    <span class="qtyLabelText"><fmt:message key="mobile.common.quantity"/><fmt:message key="mobile.common.labelSeparator"/></span>
    <select id="${id}" name="qty" onchange="CRSMA.product.quantitySelect(event);"
            onclick="event.stopPropagation(); CRSMA.product.expandPickers(true);" class="qtySelect selected">
      <c:forEach var="itemValue" begin="1" end="${empty max ? 10 : max}" step="1">
        <option value="${itemValue}" label="${itemValue}" ${(itemValue == selectedValue) ? 'selected' : ''}/>
      </c:forEach>
    </select>
  </label>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/gadgets/quantityPickerList.jsp#3 $$Change: 768606 $--%>

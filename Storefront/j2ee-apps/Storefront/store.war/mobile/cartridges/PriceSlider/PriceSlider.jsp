<%--
  "PriceSlider" cartridge renderer.
  Mobile version.

  Page includes:
    /mobile/global/util/currencyCode.jsp - Returns ISO 4217 currency code/symbol corresponding to "Price List Locale"

  Required Parameters:
    contentItem
      The "PriceSlider" content item to render.
--%>
<dsp:page>
  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/>

  <c:if test="${contentItem.enabled}">
    <%-- The code below sets the low/high price prefix and suffix depending on the ISO 4217 currency code --%>
    <dsp:include page="${mobileStorePrefix}/global/util/currencyCode.jsp"/>
    <dsp:getvalueof var="currencyCode" param="currencyCode"/>
    <dsp:getvalueof var="currencySymbol" param="currencySymbol"/>
    <c:choose>
      <c:when test="${currencyCode == 'USD'}">
        <c:set var="pricePrefix" value="${currencySymbol}"/>
        <c:set var="priceSuffix" value=""/>
      </c:when>
      <c:otherwise>
        <c:set var="pricePrefix" value=""/>
        <c:set var="priceSuffix" value="${currencySymbol}"/>
      </c:otherwise>
    </c:choose>

    <div class="refinementFacetGroupContainer">
      <span class="refinementFacetGroupName"><fmt:message key="mobile.priceslider.title"/></span>
      <ul class="refinementDataList">
        <li>
          <div class="content" id="price-range-filter">
            <span id="labelLow" style="display:none"><fmt:message key="mobile.priceslider.lowbound"/></span>
            <span id="labelHigh" style="display:none"><fmt:message key="mobile.priceslider.highbound"/></span>
            ${pricePrefix}<span class="price" id="minPrice"></span>${priceSuffix}
            <span class="priceDelimeter">-</span>
            ${pricePrefix}<span class="price" id="maxPrice"></span>${priceSuffix}
            <div class="slider-wrapper">
              <div class="range-pin range-min-value" style="left:50px;" role="slider" aria-labelledby="labelLow" aria-controls="minPrice"></div>
              <div class="range-pin range-max-value" style="right:100px;" role="slider" aria-labelledby="labelHigh" aria-controls="maxPrice"></div>
              <div class="active-range" style="left:50px;"></div>
            </div>
          </div>
        </li>
      </ul>
    </div>

    <script>
      $(function() {
        CRSMA.search.initRangeFilter($('#price-range-filter'),
          {min: parseFloat('${contentItem.sliderMin}'), max: parseFloat('${contentItem.sliderMax}')},
          {min: parseFloat('${contentItem.filterCrumb.lowerBound}'), max: parseFloat('${contentItem.filterCrumb.upperBound}')},
          "${contentItem.priceProperty}");
      });
    </script>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cartridges/PriceSlider/PriceSlider.jsp#4 $$Change: 793440 $--%>

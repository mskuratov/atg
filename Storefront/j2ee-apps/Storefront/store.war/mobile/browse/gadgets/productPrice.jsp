<%--
  This page will display product price or out of stock message if selected SKU is out of stock.

  Page includes:
    /mobile/browse/gadgets/priceLookup.jsp - Looks for SKU price (sale/regular price list), then renders the result
    /mobile/browse/gadgets/priceRange.jsp - Looks for lowest/highest prices (sale/regular price list), then renders the result

  Required parameters:
    product
      Product repository item whose details being displayed

  Optional parameters:
    selectedSku
      Currently selected SKU
--%>
<dsp:page>
  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="selectedSku" param="selectedSku"/>

  <div id="pickerPrice">
    <c:choose>
      <c:when test="${not empty selectedSku}">
        <dsp:include page="priceLookup.jsp">
          <dsp:param name="sku" param="selectedSku"/>
          <dsp:param name="product" param="product"/>
        </dsp:include>
      </c:when>
      <c:otherwise>
        <dsp:include page="priceRange.jsp">
          <dsp:param name="product" param="product"/>
        </dsp:include>
      </c:otherwise>
    </c:choose>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/gadgets/productPrice.jsp#2 $$Change: 742374 $--%>

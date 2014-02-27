<%--
  This gadget renders the list and sale prices defined for the product/SKU pair specified.
  Only list price will be displayed to the user if a sales price is not found.

  Required parameters:
    product
      Specifies a product to find the price for.
    sku
      Specifies a SKU to find the price for.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/com/commerce/CustomDroplet"/>

  <%--
    At first, look up for the list price for the product/SKU specified.

    This droplet searches for a price defined in the price list specified for the product/SKU pair.

    Input parameters:
      product
        Search the price for this product.
      sku
        Search the price for this SKU.
      priceList
        Search the price in this price list.

    Output parameters:
      price
        Price bean with price info found.

    Open parameters:
      output
        Rendered if price is found in the price list specified.
      empty
        Rendered otherwise.
  --%>
  <dsp:droplet name="PriceDroplet">
    <dsp:param name="product" param="product"/>
    <dsp:param name="sku" param="sku"/>
    <dsp:oparam name="output">
      <dsp:setvalue param="theListPrice" paramvalue="price"/>
      <%-- Search for the sale price, if sale price list is defined for the current user. --%>
      <dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
      <c:choose>
        <c:when test="${not empty profileSalePriceList}">
          <%-- Lookup the sale price. --%>
          <dsp:droplet name="PriceDroplet">
            <dsp:param name="priceList" bean="Profile.salePriceList"/>
            <dsp:oparam name="output">
              <%-- Sale price found, display both list and sale prices. --%>
                <dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
                <dsp:include page="/global/gadgets/formattedPrice.jsp">
                  <dsp:param name="price" value="1"/>
                </dsp:include>
                
              <span class="atg_store_oldPrice">
                <fmt:message key="common.price.old"/>
                <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
                <del>
                  <dsp:include page="/global/gadgets/formattedPrice.jsp">
                    <dsp:param name="price" value="1"/>
                  </dsp:include>
                </del>
              </span>
            </dsp:oparam>
            <dsp:oparam name="empty">
              <%-- Can't find sale price, display list price only. --%>
              <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
              <dsp:include page="/global/gadgets/formattedPrice.jsp">
                <dsp:param name="price" value="1"/>
              </dsp:include>
            </dsp:oparam>
          </dsp:droplet><%-- End price droplet on sale price --%>
        </c:when>
        <c:otherwise>
          <%-- No sale price list defined for the current user, display list price only. --%>
          <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="1"/>
          </dsp:include>
        </c:otherwise>
      </c:choose><%-- End Is Empty Check --%>
    </dsp:oparam>
  </dsp:droplet><%-- End Price Droplet --%>
    <dsp:droplet name="CustomDroplet">
        <dsp:oparam name="output">
            <dsp:getvalueof var="text" vartype="java.lang.String" param="text"/>
            <h1>
                <c:out value="${text}"/>
            </h1>
        </dsp:oparam>
    </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/priceLookup.jsp#1 $$Change: 735822 $--%>

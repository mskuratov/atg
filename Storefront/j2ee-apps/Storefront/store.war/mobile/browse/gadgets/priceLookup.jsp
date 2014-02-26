<%--
  This page, given a product and SKU, and using the "Profile" bean, looks for that SKU price
  in the sale price list (if it exists) and the regular price list.

  Page includes:
    /global/gadgets/formattedPrice.jsp - Price formatter

  Required parameters:
    product
      Product repository item whose price we wish to display
    sku
      SKU repository item for the product whose price we wish to display

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <%--
    The 1-st call to price droplet is going to get the price from the profile list price
    or the default price list
  --%>
  <dsp:droplet name="PriceDroplet">
    <dsp:param name="product" param="product"/>
    <dsp:param name="sku" param="sku"/>
    <dsp:oparam name="output">
      <dsp:setvalue param="theListPrice" paramvalue="price"/>

      <%-- Is there a sale price? --%>
      <dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
      <c:choose>
        <c:when test="${not empty profileSalePriceList}">
          <dsp:droplet name="PriceDroplet">
            <dsp:param name="priceList" bean="Profile.salePriceList"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
              <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
              <p>
                <strong>
                  <dsp:include page="/global/gadgets/formattedPrice.jsp">
                    <dsp:param name="price" value="${listPrice}"/>
                  </dsp:include>
                </strong>
                <br/><fmt:message key="mobile.price.old"/>&nbsp;
                <span class="crossed">
                  <dsp:include page="/global/gadgets/formattedPrice.jsp">
                    <dsp:param name="price" value="${price}"/>
                  </dsp:include>
                </span>
              </p>
            </dsp:oparam>
            <dsp:oparam name="empty">
              <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
              <p>
                <strong>
                  <dsp:include page="/global/gadgets/formattedPrice.jsp">
                    <dsp:param name="price" value="${price}"/>
                  </dsp:include>
                </strong>
              </p>
            </dsp:oparam>
          </dsp:droplet><%-- End price droplet on sale price --%>
        </c:when>
        <c:otherwise>
          <p>
            <strong>
              <dsp:include page="/global/gadgets/formattedPrice.jsp">
                <dsp:param name="price" value="${price}"/>
              </dsp:include>
            </strong>
          </p>
        </c:otherwise>
      </c:choose><%-- End Is Empty Check --%>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/gadgets/priceLookup.jsp#2 $$Change: 742374 $--%>

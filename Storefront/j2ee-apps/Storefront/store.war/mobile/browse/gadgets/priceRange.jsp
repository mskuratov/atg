<%--
  This page, given a product and using the Profile bean, looks for lowest and highest prices
  in the sale price list (if exists) and the regular price list.

  Page includes:
    /global/gadgets/formattedPrice.jsp - Price formatter

  Required parameters:
    product
      Product repository item whose price we wish to display

  Optional parameters:
    None
--%>
<%@page trimDirectiveWhitespaces="true"%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  <dsp:importbean bean="/atg/commerce/pricing/PriceRangeDroplet"/>

  <dsp:getvalueof var="showPriceLabel" param="showPriceLabel"/>

  <dsp:droplet name="PriceRangeDroplet">
    <dsp:param name="productId" param="product.repositoryId"/>
    <dsp:param name="salePriceList" bean="Profile.priceList"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="highestListPrice" vartype="java.lang.Double" param="highestPrice"/>
      <dsp:getvalueof var="lowestListPrice" vartype="java.lang.Double" param="lowestPrice"/>
    </dsp:oparam>
  </dsp:droplet>

  <dsp:droplet name="PriceRangeDroplet">
    <dsp:param name="productId" param="product.repositoryId"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="highestPrice" vartype="java.lang.Double" param="highestPrice"/>
      <dsp:getvalueof var="lowestPrice" vartype="java.lang.Double" param="lowestPrice"/>
      <c:if test="${highestListPrice != highestPrice || lowestListPrice != lowestPrice}">
        <c:set var="showSalePrice" value="true"/>
      </c:if>
      <p>
        <dsp:droplet name="Compare">
          <dsp:param name="obj1" param="lowestPrice" converter="number"/>
          <dsp:param name="obj2" param="highestPrice" converter="number"/>
          <dsp:oparam name="equal">
            <strong>
              <dsp:include page="/global/gadgets/formattedPrice.jsp">
                <dsp:param name="price" value="${lowestPrice}"/>
              </dsp:include>
            </strong>
          </dsp:oparam>
          <dsp:oparam name="default">
            <fmt:message key="mobile.price.from"/><br/><strong>
              <dsp:include page="/global/gadgets/formattedPrice.jsp">
                <dsp:param name="price" value="${lowestPrice}"/>
              </dsp:include>
            </strong><br/><fmt:message key="mobile.price.to"/>
            <br/><strong>
              <dsp:include page="/global/gadgets/formattedPrice.jsp">
                <dsp:param name="price" value="${highestPrice}"/>
              </dsp:include>
            </strong>
          </dsp:oparam>
        </dsp:droplet>
        <c:if test="${showSalePrice}">
          <br/><fmt:message key="mobile.price.old"/>
          <dsp:droplet name="Compare">
            <dsp:param name="obj1" value="${lowestListPrice}" converter="number"/>
            <dsp:param name="obj2" value="${highestListPrice}" converter="number"/>
            <dsp:oparam name="equal">
              <span>
                <dsp:include page="/global/gadgets/formattedPrice.jsp">
                  <dsp:param name="price" value="${lowestListPrice}"/>
                </dsp:include>
              </span>
            </dsp:oparam>
            <dsp:oparam name="default">
              <span>
                <dsp:include page="/global/gadgets/formattedPrice.jsp">
                  <dsp:param name="price" value="${lowestListPrice}"/>
                </dsp:include>
              </span>
              <fmt:message key="mobile.common.textSeparator"/>
              <span>
                <dsp:include page="/global/gadgets/formattedPrice.jsp">
                  <dsp:param name="price" value="${highestListPrice}"/>
                </dsp:include>
              </span>
            </dsp:oparam>
          </dsp:droplet>
        </c:if>
      </p>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/gadgets/priceRange.jsp#2 $$Change: 742374 $--%>

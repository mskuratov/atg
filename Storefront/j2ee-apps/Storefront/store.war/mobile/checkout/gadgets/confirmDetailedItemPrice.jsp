<%--
  This gadget displays price details for a commerce item specified.

  Page includes:
    /mobile/checkout/gadgets/confirmItemPrice.jsp - Price details renderer

  Required parameters:
    currentItem
      Specifies a commerce item, whose price details should be displayed

  Optional parameters:
    priceBeans
      Price beans to be used when displaying applied discounts.
    priceBeansQuantity
      Quantity to be displayed
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/pricing/UnitPriceDetailDroplet"/>
    
  <%-- First check to see if the item was discounted --%>
  <dsp:getvalueof var="rawPrice" param="currentItem.priceInfo.rawTotalPrice"/>
  <dsp:getvalueof var="actualPrice" param="currentItem.priceInfo.amount"/>
  <dsp:getvalueof var="listPrice" param="currentItem.priceInfo.listPrice"/>
  <dsp:getvalueof var="priceBeans" vartype="java.util.Collection" param="priceBeans"/>

  <c:choose>
    <c:when test="${rawPrice == actualPrice}">
      <%-- They match, no discounts --%>
        <c:choose>
          <%-- Price beans already found with "StorePriceBeansDroplet", use their quantities --%>
          <c:when test="${not empty priceBeans}">
            <dsp:include page="confirmItemPrice.jsp">
              <dsp:param name="quantity" param="priceBeansQuantity"/>
              <dsp:param name="price" param="currentItem.priceInfo.listPrice"/>
            </dsp:include>
          </c:when>
          <c:otherwise>
            <dsp:include page="confirmItemPrice.jsp">
              <dsp:param name="quantity" param="currentItem.quantity"/>
              <dsp:param name="price" param="currentItem.priceInfo.listPrice"/>
            </dsp:include>
          </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
      <%-- There's some discounting going on --%>
      <dsp:droplet name="UnitPriceDetailDroplet">
        <dsp:param name="item" param="currentItem"/>
        <dsp:oparam name="output">
          <%-- Always use price beans got from outer droplet, if any. Otherwise use price beans generated for commerce item --%>
          <c:set var="unitPriceBeans" value="${priceBeans}"/>
          <c:if test="${empty unitPriceBeans}">
            <dsp:getvalueof var="unitPriceBeans" param="unitPriceBeans"/>
          </c:if>
          <c:set var="priceBeansNumber" value="${fn:length(unitPriceBeans)}"/>
          <c:forEach var="unitPriceBean" items="${unitPriceBeans}">
            <c:set var="unitPrice" value="${unitPriceBean.unitPrice}"/>
            <dsp:include page="confirmItemPrice.jsp">
              <dsp:param name="quantity" value="${unitPriceBean.quantity}"/>
              <dsp:param name="price" value="${unitPriceBean.unitPrice}"/>
              <dsp:param name="oldPrice" value="${unitPrice == listPrice ? '' : listPrice}"/>
            </dsp:include>
          </c:forEach>
        </dsp:oparam>
      </dsp:droplet>
    </c:otherwise>
  </c:choose>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/checkout/gadgets/confirmDetailedItemPrice.jsp#2 $$Change: 742374 $--%>

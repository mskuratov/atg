<%--
  Display price detail for each "Shopping Cart" item.

  Page includes:
    /global/gadgets/formattedPrice.jsp - Price formatter

  Required parameters:
    commerceItem
      The commerce item whose datailed price to show

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/pricing/UnitPriceDetailDroplet"/>

  <dsp:getvalueof var="listPrice" param="commerceItem.priceInfo.listPrice"/>

  <dsp:droplet name="UnitPriceDetailDroplet">
    <dsp:param name="item" param="commerceItem"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="unitPriceBeans" param="unitPriceBeans"/>
      <c:set var="priceBeansNumber" value="${fn:length(unitPriceBeans)}"/>
      <c:forEach var="unitPriceBean" items="${unitPriceBeans}">
        <%-- Display commerce item quantity --%>
        <c:choose>
          <%-- First check to see if the item was discounted --%>
          <c:when test="${listPrice != unitPriceBean.unitPrice}">
            <%-- There's some discounting going on --%>
            <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <dsp:param name="price" value="${unitPriceBean.unitPrice}"/>
              <dsp:param name="saveFormattedPrice" value="true"/>
            </dsp:include>
            <span ${fn:length(formattedPrice) > 6 ? 'class="longPrice"' : ''}>
              <span class="quantity">
                <fmt:formatNumber value="${unitPriceBean.quantity}" type="number"/>
              </span>
              <span>
                <fmt:message key="mobile.price.atRateOf"/>
              </span>
              <span class="newPrice">
                ${formattedPrice}
              </span>
              <br/>
              <span class="oldPrice">
                <fmt:message key="mobile.price.old"/>
                <del>
                  <dsp:include page="/global/gadgets/formattedPrice.jsp">
                    <dsp:param name="price" value="${listPrice}"/>
                  </dsp:include>
                </del>
              </span>
            </span>
          </c:when>
          <c:otherwise>
            <%-- They match, no discounts --%>
            <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <dsp:param name="price" value="${listPrice}"/>
              <dsp:param name="saveFormattedPrice" value="true"/>
            </dsp:include>
            <span class="${fn:length(formattedPrice) > 7 ? 'longPrice' : ''}">
              <span class="quantity">
                <fmt:formatNumber value="${unitPriceBean.quantity}" type="number"/>
              </span>
              <span>
                <fmt:message key="mobile.price.atRateOf"/>
              </span>
              <span class="newPrice">
                ${formattedPrice}
              </span>                          
            </span>
          </c:otherwise>
        </c:choose>
        <br/>
      </c:forEach>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cart/gadgets/detailedItemPrice.jsp#2 $$Change: 742374 $--%>

<%-- 
  This page calculates and displays an item's price. It will also be formatted according 
  to the appropriate price list locale.
  
  Required Parameters:
    displayQuantity
      Whether to display the item quantity or not.
    quantity
      The actual quantity of a particular item.
    price
      The price of an item.
  
  Optional parameters:
    oldPrice
      What the item used to cost.
--%>

<dsp:page>

  <dsp:getvalueof var="displayQuantity" param="displayQuantity"/>
  <dsp:getvalueof var="quantity" param="quantity"/>
  <dsp:getvalueof var="price" param="price"/>
  <dsp:getvalueof var="oldPrice" param="oldPrice"/>

  <p class="price">
    <c:if test="${displayQuantity}">
      <fmt:formatNumber value="${quantity}" type="number"/>
      <fmt:message key="common.atRateOf"/>
    </c:if>
    
    <c:if test="${not empty price }"> 
      <span>
        <dsp:include page="/global/gadgets/formattedPrice.jsp">
          <dsp:param name="price" value="${price}"/>
        </dsp:include>
      </span>
    </c:if>
     
     
    <c:if test="${not empty oldPrice}">
      <%-- Display the item's old price --%>
      <span class="atg_store_oldPrice">
        <fmt:message key="common.price.old"/>
        <del>
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${oldPrice}"/>
          </dsp:include>
        </del>
      </span> 
    </c:if>
  </p>

</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/gadgets/displayItemPrice.jsp#3 $$Change: 788432 $--%>
<%--
  This gadget displays price details for a commerce item specified. It displays 
  price per quantity with discounts applied.

  Required parameters:
    currentItem
      Specifies a commerce item, whose price details should be displayed.
    priceBeans
      Price beans to be used when displaying applied discounts (obtained through StorePriceBeansDroplet
      invocation in the parent page and passed to this gadget).

  Optional parameters:
    includeGifts
      The boolean indicating whether free gifts should be included into pricing details default is false).      
--%>

<dsp:page>
  
  <dsp:getvalueof var="rawPrice" param="currentItem.priceInfo.rawTotalPrice"/>
  <dsp:getvalueof var="listPrice" param="currentItem.priceInfo.listPrice"/>
  <dsp:getvalueof var="priceBeans" vartype="java.util.Collection" param="priceBeans"/>
  <dsp:getvalueof var="includeGifts" param="includeGifts"/>
      
  <c:forEach var="priceBean" items="${priceBeans}">
    
    <%-- 
      Check if the given price bean contains GWP model. If yes,
      just skip it - no need to display 0.0 price, it will be
      handled in another item line.
     --%>
    
    <c:if test="${includeGifts || !priceBean.giftWithPurchase}">
      
      <dsp:include page="confirmItemPrice.jsp">
        <dsp:param name="currentItem" param="currentItem"/>
        <dsp:param name="unitPriceBean" value="${priceBean}"/>
        <dsp:param name="quantity" value="${priceBean.quantity}"/>
        <dsp:param name="price" value="${priceBean.unitPrice}"/>
        <dsp:param name="oldPrice" value="${priceBean.unitPrice == listPrice ? '' : listPrice}"/>
      </dsp:include>
    </c:if>
 </c:forEach>
    
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/confirmDetailedItemPrice.jsp#2 $$Change: 788278 $--%>

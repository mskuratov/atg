<%-- 
  If available, display a price bean's discount according to its pricing model.
  
  Required Parameters:
    currentItem
      The current item to be processed.
    unitPriceBean
      The bean containing price information about the item.
          
--%>
<dsp:page>
  
  <%-- This will determine whether to indicate that an item is on sale or not --%>
  <dsp:getvalueof var="currentItemOnSale" param="currentItem.priceInfo.onSale"/>
  <%-- List of pricing models for the item --%>
  <dsp:getvalueof var="pricingModels" vartype="java.lang.Object" param="unitPriceBean.pricingModels"/>
  
  <dsp:getvalueof var="isGift" param="unitPriceBean.giftWithPurchase"/>
  
  <c:if test="${currentItemOnSale or not empty pricingModels}">
    <span style="display:block;font-size:12px;font-family:Tahoma,Arial,sans-serif;">
      <%-- Do not display 'On Sale' message if the item is gift. --%>
      <c:if test="${currentItemOnSale && !isGift}">
        <fmt:message key="cart_detailedItemPrice.salePriceB"/>
      </c:if>
      
      <c:forEach var="pricingModel" items="${pricingModels}" varStatus="status">
        
        <span>
          <dsp:param name="pricingModel" value="${pricingModel}"/>
          <dsp:getvalueof var="modelId" param="pricingModel.id"/>
            
          <c:if test="${(currentItemOnSale && !isGift) or not status.first}">
            <fmt:message key="common.and"/>
          </c:if>
          
          <dsp:valueof param="pricingModel.displayName" valueishtml="true">
            <fmt:message key="common.promotionDescriptionDefault"/>
          </dsp:valueof>
            
        </span>
        
      </c:forEach><%-- End for each promotion used to create the unit price --%>
    </span>
  </c:if>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/emailDisplayItemPricePromotions.jsp#1 $$Change: 788278 $--%>

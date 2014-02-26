<%-- 
  If available, display a price bean's discount according to its pricing model.
  
  Required Parameters:
    currentItem
      The current item to be processed.
    unitPriceBean
      The bean containing price information about the item.
    excludeModelId
      The promotion ID which should be excluded from prices information. This parameter is used to
      exclude the item's quantity and corresponding price produced with the use of specified promotion ID
      from prices information. This is needed to display regular
      item's quantity and free gift quantity of the same commerce item as separate line items.
      
  Optional Parameters:
    isGift
      The boolean indicating whether the given item is a gift.        
--%>

<dsp:page>

  <dsp:getvalueof var="excludeModelId" param="excludeModelId"/>
  
  <%-- This will determine whether to indicate that an item is on sale or not --%>
  <dsp:getvalueof var="currentItemOnSale" param="currentItem.priceInfo.onSale"/>
  <%-- List of pricing models for the item --%>
  <dsp:getvalueof var="pricingModels" vartype="java.lang.Object" param="unitPriceBean.pricingModels"/>
  
  <dsp:getvalueof var="isGift" param="isGift"/>
  
  <c:if test="${currentItemOnSale or not empty pricingModels}">
    <span class="atg_store_discountNote">
      <%-- Do not display 'On Sale' message if the item is gift. --%>
      <c:if test="${currentItemOnSale && !isGift}">
        <fmt:message key="cart_detailedItemPrice.salePriceB"/>
      </c:if>
      
      <c:forEach var="pricingModel" items="${pricingModels}" varStatus="status">
        <preview:repositoryItem item="${pricingModel}">
          <span>
            <dsp:param name="pricingModel" value="${pricingModel}"/>
            <dsp:getvalueof var="modelId" param="pricingModel.id"/>
            <c:if test="${excludeModelId != modelId}">
              <c:if test="${(currentItemOnSale && !isGift) or not status.first}">
                <fmt:message key="common.and"/>
              </c:if>
          
              <dsp:valueof param="pricingModel.displayName" valueishtml="true">
               <fmt:message key="common.promotionDescriptionDefault"/>
              </dsp:valueof>
            </c:if>
          </span>
        </preview:repositoryItem>
      </c:forEach><%-- End for each promotion used to create the unit price --%>
    </span>
  </c:if>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/gadgets/displayItemPricePromotions.jsp#2 $$Change: 788278 $--%>
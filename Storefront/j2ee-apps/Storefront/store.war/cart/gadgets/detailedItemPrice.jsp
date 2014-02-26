<%-- 
  This page displays price details for an item that has been passed in as a parameter. Price details
  include unit price and if available, promotion/sale prices.
  
  Required Parameters:
    currentItem
      The item we want price details of.
 
  Optional Parameters:  
    displayDiscountFirst
      Whether to display the item's discount before or after it's item price.
    displayQuantity
      Whether to display the specified quantity of items or not.
    excludeModelId
      The promotion ID which should be excluded from prices information. This parameter is used to
      exclude the item's quantity and corresponding price produced with the use of specified promotion ID
      from prices information. This is needed to display regular item's quantity and free gift quantity of 
      the same commerce item as separate line items.  
--%>

<dsp:page>

  <dsp:importbean bean="/atg/commerce/pricing/UnitPriceDetailDroplet"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>

  <dsp:getvalueof var="displayDiscountFirst" param="displayDiscountFirst"/>
  <dsp:getvalueof var="excludeModelId" param="excludeModelId"/>
  
  <%-- The list price of the current item that will be processed by the UnitPriceDetailDroplet --%>
  <dsp:getvalueof var="listPrice" param="currentItem.priceInfo.listPrice"/>

  <%-- 
    Given a CommerceItem, UnitPriceDetailDroplet will return its unit price details in the
    format of a UnitPriceBean.
    
    Input Parameters:
      item 
        The commerce item to be processed.
    
    Open Parameters:
      output
        This is always rendered.
    
    Output Parameters:
      unitPriceBeans
        A list of UnitPriceBeans.
  --%>
  <dsp:droplet name="UnitPriceDetailDroplet">
    <dsp:param name="item" param="currentItem"/>
    <dsp:oparam name="output">

      <dsp:getvalueof var="unitPriceBeans" vartype="java.lang.Object" param="unitPriceBeans"/>
      <c:set var="priceBeansNumber" value="${fn:length(unitPriceBeans)}"/>
      
      <c:forEach var="unitPriceBean" items="${unitPriceBeans}" varStatus="unitPriceBeanStatus">
      
        <c:set var="displayPrice" value="true"/>
        
        <c:if test="${displayDiscountFirst}">
          <%-- promotions info --%>
          <dsp:include page="displayItemPricePromotions.jsp">
            <dsp:param name="excludeModelId" param="excludeModelId"/>
            <dsp:param name="currentItem" param="currentItem"/>
            <dsp:param name="unitPriceBean" value="${unitPriceBean}"/>
          </dsp:include>
        </c:if>
        
        <%-- 
          Check if the given price bean contains GWP model. If yes, just skip it - no need to 
          display 0.0 price, it will be handled in another item line.
         --%>
         <dsp:getvalueof var="pricingModels" vartype="java.lang.Object" value="${unitPriceBean.pricingModels}"/>

         <c:if test="${not empty pricingModels}">
           <c:forEach var="pricingModel" items="${pricingModels}" varStatus="status">
          
             <dsp:param name="pricingModel" value="${pricingModel}"/>
             <dsp:getvalueof var="modelId" param="pricingModel.id"/>
            
             <c:if test="${excludeModelId == modelId}">
               <c:set var="displayPrice" value="false"/>
             </c:if>
            
           </c:forEach>
         </c:if>
   
        <c:if test="${displayPrice}">   
          <dsp:include page="displayItemPrice.jsp">
            <dsp:param name="quantity" value="${unitPriceBean.quantity}"/>
            <dsp:param name="displayQuantity" value="${priceBeansNumber > 1}"/>
            <dsp:param name="price" value="${unitPriceBean.unitPrice}"/>
            <dsp:param name="oldPrice" value=""/>
          </dsp:include>
        </c:if>
        
        <c:if test="${not displayDiscountFirst}">  
          <dsp:include page="displayItemPricePromotions.jsp">
            <dsp:param name="excludeModelId" param="excludeModelId"/>
            <dsp:param name="currentItem" param="currentItem"/>
            <dsp:param name="unitPriceBean" value="${unitPriceBean}"/>
          </dsp:include>
        </c:if>

        <%--
          Note that we never displayed old price for a price bean before. This is done to 
          display prices in the following format:
            - Current price.
            - All applied promotions.
            - Old price. 
         --%>        
        <c:if test="${listPrice != unitPriceBean.unitPrice && displayPrice}">
          <dsp:include page="displayItemPrice.jsp">
            <dsp:param name="displayQuantity" value="false"/>
            <dsp:param name="oldPrice" value="${listPrice}"/>        
          </dsp:include>
        </c:if>
        
      </c:forEach>
    </dsp:oparam>
  </dsp:droplet><%-- End for unit price detail droplet --%>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/gadgets/detailedItemPrice.jsp#2 $$Change: 788278 $--%>
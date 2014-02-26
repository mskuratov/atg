<%-- 
  Template that is used to render each cart item. 
  
  Required Parameters:
    currentItem
      The item that is currently being processed.
      
  Optional Parameters:
    isGift
      true if the current item is gift    
    itemQty
      quantity we'd like to display instead of commerceItem.quantity 
      (primary to display quantities of regular items and gifts belongs to
      single commerce item)  
    excludeModelId
      The promotion ID which should be excluded from prices information. This parameter is used to
      exclude the item's quantity and corresponding price produced with the use of specified promotion ID
      from prices information. This is needed to display regular
      item's quantity and free gift quantity of the same commerce item as separate line items.  
--%>

<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/commerce/pricing/UnitPriceDetailDroplet"/>
  
  <dsp:getvalueof var="isGift" param="isGift"/>
  <dsp:getvalueof var="itemQty" param="itemQty"/>
  <dsp:getvalueof var="excludeModelId" param="excludeModelId"/>
  
  <c:if test="${empty itemQty}">
    <dsp:getvalueof var="itemQty" param="currentItem.quantity"/>
  </c:if>
  
  <c:if test="${empty isGift}">
    <c:set var="isGift" value="false"/>
  </c:if>
  
  <json:object>

    <%-- Core properties for the item --%>
    <json:property name="name">
      
      <dsp:getvalueof var="displayName" scope="page" param="currentItem.auxiliaryData.catalogRef.displayName"/>
      
      <c:if test="${empty displayName}">
        <dsp:getvalueof var="displayName" scope="page" param="currentItem.auxiliaryData.productRef.displayName"/>
      </c:if>
      
      <c:out value="${displayName}" escapeXml="false"/>
    </json:property>
    
    <json:property name="url" escapeXml="false">
      
      <dsp:getvalueof var="productId" vartype="java.lang.String" param="currentItem.auxiliaryData.productId"/>
      
      <dsp:include page="/global/gadgets/crossSiteLinkGenerator.jsp">
        <dsp:param name="product" param="currentItem.auxiliaryData.productRef"/>
        <dsp:param name="siteId" param="item.auxiliaryData.siteId"/>
        <dsp:param name="queryParams" value="productId=${productId}"/>
      </dsp:include>
      
      <dsp:getvalueof var="siteLinkUrl" param="siteLinkUrl"/>
      
      <c:out value="${siteLinkUrl}"/>
    </json:property>
    
    <json:property name="imageUrl">
      <dsp:getvalueof param="currentItem.auxiliaryData.catalogRef.smallImage.url" var="imageUrl"/>
                
      <c:if test="${empty imageUrl}">
        <%-- No alternate image, so use product image --%>
        <dsp:getvalueof param="currentItem.auxiliaryData.productRef.smallImage.url" var="imageUrl"/>
      </c:if>
      
      <%-- Render the image URL, with a default if still empty --%>
      <c:out value="${imageUrl}" default="/crsdocroot/content/images/products/small/MissingProduct_small.jpg" 
             escapeXml="false"/>         
    </json:property>
    
    <%-- 
      'modified' flag to indicate if this item was just modified. This should be true
       if the item has just been added, or the quantity has just been changed.
       The rich cart will highlight and scroll into view the modified item 
     --%>
    <dsp:getvalueof bean="/atg/commerce/order/purchase/CartModifierFormHandler.items" var="itemsJustAdded"/>
    <c:set var="itemModified" value="${false}"/>
  
    <%-- Items have been added to the cart on this request. Check to see if this is it. --%>  
    <c:forEach items="${itemsJustAdded}" var="itemToCheck">
      <dsp:setvalue param="formHandlerItem" value="${itemToCheck}" /> 
      
      <%-- 
        The Compare droplet renders one of its open parameters based on
        the relative values of the obj1 and obj2 input parameters.
        
        Input Parameters:
          obj1
            The first object to be compared (e.g obj1 > obj2).
          obj2
            The second object to be compared.
          
        Open Parameters:
          equal
            Rendered if obj1 == obj2.
      --%>
      <dsp:droplet name="Compare">
        <dsp:param name="obj1" param="formHandlerItem.catalogRefId" />
        <dsp:param name="obj2" param="currentItem.catalogRefId" />
        <dsp:oparam name="equal">
          <%-- 
            Item in the cart matches one in the formhandler. Check that the quantity just added
            is non zero 
          --%>
          <dsp:getvalueof param="formHandlerItem.quantity" var="qtyAdded"/>
          
          <c:if test="${qtyAdded > 0}">
            <%-- Item has just been added to the cart, so set the flag --%>
            <c:set var="itemModified" value="${true}"/>
          </c:if>
        </dsp:oparam>  
      </dsp:droplet>   
    </c:forEach> 
    <json:property name="modified" value="${itemModified}"/>
    
    <%-- Link Item - should the item's image and title be rendered as a clickable link? --%>
    <dsp:getvalueof var="itemType" param="currentItem.commerceItemClassType" />
    
    <%-- Determine if this product is navigable, i.e. if we can build a link to the product details page --%>
    <dsp:getvalueof var="isNavigable" param="currentItem.auxiliaryData.productRef.NavigableProducts"/>
    
    <json:property name="linkItem" value="${itemType != 'giftWrapCommerceItem' && isNavigable}"/>
    
    <%--
      If the current item is a gift, display FREE instead of 0.0 price 
     --%>
    <c:choose>
      <c:when test="${isGift}">
        <json:array name="prices">
          <json:object>
            <json:property name="quantity"><dsp:valueof value="${itemQty}"/></json:property>
            <json:property name="price"><fmt:message key="common.FREE"/></json:property>
          </json:object>
         </json:array>
      </c:when>
      <c:otherwise>
        <%-- Pricing properties --%>  
        <dsp:tomap param="currentItem.priceInfo" var="priceInfo"/>
      
        <%-- First check to see if the item was discounted --%>
        <dsp:getvalueof var="rawPrice" param="currentItem.priceInfo.rawTotalPrice"/>
        <dsp:getvalueof var="actualPrice" param="currentItem.priceInfo.amount"/>
        
        <json:array name="prices">
          <c:choose>
            <c:when test="${rawPrice == actualPrice}">
              <%-- They match, no discounts --%>
              <json:object>
                <json:property name="quantity"><dsp:valueof value="${itemQty}"/></json:property>
                <json:property name="price">
                  <dsp:include page="/global/gadgets/formattedPrice.jsp">
                    <dsp:param name="price" param="currentItem.priceInfo.listPrice"/>
                  </dsp:include>
                </json:property>
              </json:object>
            </c:when>
            <c:otherwise>
              <%--  
                Given a CommerceItem, UnitPriceDetailDroplet will return its unit price details in the
                format of a UnitPriceBean.
                
                Input Parameters:
                  item
                    The commerce item.
    
                Open Parameters:
                  output
                    This is always rendered.
    
                Output Parameters:
                  unitPriceBeans
                    List of UnitPriceBeans.
                --%>
              <dsp:droplet name="UnitPriceDetailDroplet">
                <dsp:param name="item" param="currentItem"/>
                <dsp:oparam name="output">
                  
                  <dsp:getvalueof var="unitPriceBeans" vartype="java.lang.Object" param="unitPriceBeans"/>
                  
                  <c:forEach var="unitPriceBean" items="${unitPriceBeans}">
                    <c:set var="displayPrice" value="true"/>
                    <dsp:param name="unitPriceBean" value="${unitPriceBean}"/>
                    
                    <%-- 
                      Check if the given price bean contains GWP model. If yes,
                      just skip it - no need to display 0.0 price, it will be
                      handled in another item line.
                     --%>
                     <dsp:getvalueof var="pricingModels" vartype="java.lang.Object" 
                                     value="${unitPriceBean.pricingModels}"/>
            
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
                      <json:object>
                        <json:property name="quantity"><dsp:valueof param="unitPriceBean.quantity"/></json:property>
                        <json:property name="price">
                          <dsp:include page="/global/gadgets/formattedPrice.jsp">
                            <dsp:param name="price" param="unitPriceBean.unitPrice"/>
                          </dsp:include>
                        </json:property>
                      </json:object>
                    </c:if>
                      
                  </c:forEach>
                </dsp:oparam>
              </dsp:droplet><%-- End for unit price detail droplet --%>
            </c:otherwise>
          </c:choose>
        </json:array>  
      </c:otherwise>
    </c:choose>

    <%-- 
      Extra item properties that will be listed in the rich cart item in the order 
      they are listed here. Each object should contain 'name' and 'value' elements. 
    --%>
    <dsp:getvalueof var="skuType" vartype="java.lang.String" param="currentItem.auxiliaryData.catalogRef.type"/>
    <json:array name="properties">
      <c:choose>
        <c:when test="${skuType == 'clothing-sku'}">
          <%-- Size --%>
          <dsp:include page="cartItemProperty.jsp">
            <dsp:param name="propertyValue" param="currentItem.auxiliaryData.catalogRef.size"/>
            <dsp:param name="propertyNameKey" value="common.size"/>
          </dsp:include>
          
          <%-- Color/Wood Finish --%>
          <dsp:include page="cartItemProperty.jsp">
            <dsp:param name="propertyValue" param="currentItem.auxiliaryData.catalogRef.color"/>
            <dsp:param name="propertyNameKey" value="common.color"/>
          </dsp:include>
        </c:when>
        <c:when test="${skuType == 'furniture-sku'}">
          <dsp:include page="cartItemProperty.jsp">
            <dsp:param name="propertyValue" param="currentItem.auxiliaryData.catalogRef.woodFinish"/>
            <dsp:param name="propertyNameKey" value="common.woodFinish"/>
          </dsp:include>
        </c:when>
      </c:choose>
      
      <%-- Description --%>
      <dsp:include page="cartItemProperty.jsp">
        <dsp:param name="propertyValue" param="currentItem.auxiliaryData.catalogRef.description"/>
        <dsp:param name="propertyNameKey" value="common.description"/>
      </dsp:include>
      
    </json:array>
    
    <%-- 
      Availability Message - if an availability message should be shown in the 
      Rich Cart, then set a JSON 'availability' property. The cart will display 
      this if set.  
    --%>         
    <dsp:include page="/global/gadgets/skuAvailabilityLookup.jsp">
      <dsp:param name="product" param="currentItem.auxiliaryData.productRef"/>
      <dsp:param name="skuId" param="currentItem.auxiliaryData.catalogRef.repositoryId"/>
    </dsp:include>
    <c:if test="${!empty availabilityMessage}">
      <json:property name="availability" escapeXml="false">
        ${availabilityMessage}
      </json:property>
    </c:if>
      
    <%-- Display site information only for items from sites other than given. --%>
    <dsp:getvalueof var="currentSiteId" bean="Site.id"/>
    <dsp:getvalueof var="itemSiteId" param="currentItem.auxiliaryData.siteId"/>
    
    <c:if test="${currentSiteId != itemSiteId}">
      <json:property name="siteName" escapeXml="false">
        <dsp:include page="/global/gadgets/siteIndicator.jsp">
          <dsp:param name="mode" value="name"/>              
          <dsp:param name="siteId" param="currentItem.auxiliaryData.siteId"/>
          <dsp:param name="product" param="currentItem.auxiliaryData.productRef"/>
        </dsp:include>
      </json:property>
    </c:if>
  </json:object>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/json/cartItem.jsp#3 $$Change: 788278 $--%>

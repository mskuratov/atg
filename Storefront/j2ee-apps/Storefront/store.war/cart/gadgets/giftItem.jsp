<%-- 
  This gadget renders the row for the gift item, i.e. commerce item
  that has been added as a gift or re-priced as free.
  
  This gadget must be contained inside of a form.

  Required parameters:
    currentItem
      commerce item that have been added as a gift
    selection
      GiftWithPurchaseSelection bean associated with that commerce item
    currentSiteSharesCart
      true if current site shares shopping cart and we need to display 
      additional column with site indicator
    count
      commerce item count       

  Optional parameters:
    gwpCount
      The current number of the rendered line for the GWP commerce item. The commerce item with GWP
      markers can be rendered on several lines, as we render non-GWP quantity of commerce item and every
      1 quantity of gift item on separate lines.
--%>

<dsp:page>

  <dsp:importbean bean="/atg/commerce/promotion/PromotionLookup"/>

  <dsp:getvalueof var="selection" param="selection"/>
  <dsp:getvalueof var="currentItem" param="currentItem"/>
  
  <dsp:getvalueof var="count" param="count"/>
  <dsp:getvalueof var="currentSiteSharesCart" param="currentSiteSharesCart"/>

  <tr>
    <%-- Display site indicator only if current site shares the cart with some other site. --%>
    <c:if test="${currentSiteSharesCart == true}">
      
      <%-- Visual indication of the site that the product belongs to. --%>
      <td class="site">
        <dsp:include page="/global/gadgets/siteIndicator.jsp">
          <dsp:param name="mode" value="icon"/>
          <dsp:param name="siteId" param="currentItem.auxiliaryData.siteId"/>
          <dsp:param name="product" param="currentItem.auxiliaryData.productRef"/>
        </dsp:include>
      </td>
      
    </c:if>
                        
    <td class="image">
      <dsp:include page="cartItemImage.jsp">
        <dsp:param name="commerceItem" param="currentItem"/>
        <dsp:param name="displayAsLink" param="currentItem.auxiliaryData.productRef.NavigableProducts"/>
      </dsp:include>
    </td>
  
    <dsp:getvalueof var="rowTitle" param="currentItem.auxiliaryData.productRef.displayName"/>
    
    <c:if test="${empty rowTitle}">
      <fmt:message var="rowTitle" key="common.noDisplayName" />
    </c:if>
                          
    <td class="item giftItemName" scope="row" abbr="${rowTitle}">
    
      <%-- Link back to the product detail page. --%>
      <dsp:getvalueof var="url" vartype="java.lang.Object" param="currentItem.auxiliaryData.productRef.template.url"/>
      <dsp:getvalueof var="navigable" param="currentItem.auxiliaryData.productRef.NavigableProducts"/>
      
      <c:choose>
        <c:when test="${not empty url and navigable}">
          <dsp:include page="/global/gadgets/crossSiteLink.jsp">
            <dsp:param name="item" param="currentItem"/>
          </dsp:include>
        </c:when>
        <c:otherwise>
          <span class="itemName"> 
            <dsp:valueof param="currentItem.auxiliaryData.productRef.displayName">
              <fmt:message key="common.noDisplayName"/>
            </dsp:valueof>
          </span>  
        </c:otherwise>
      </c:choose>
  
      <%-- Render all SKU-specific properties for the current item. --%>
      <dsp:include page="/global/util/displaySkuProperties.jsp">
        <dsp:param name="product" param="currentItem.auxiliaryData.productRef"/>
        <dsp:param name="sku" param="currentItem.auxiliaryData.catalogRef"/>
        <dsp:param name="displayAvailabilityMessage" value="true"/>
      </dsp:include>
      
      <%-- 
        We offer to change gift only if the gift type is not a SKU
        and the gift type is a product with more than one SKU.
      --%>
      <dsp:getvalueof var="skus" param="currentItem.auxiliaryData.productRef.childSKUs" />
      <dsp:getvalueof var="skuLength" value="${fn:length(skus)}" />
      
      <c:if test="${((selection.giftType != 'sku') && 
                    ((selection.giftType == 'product' && skuLength > 1) || 
                      selection.giftType == 'category')||
                      (selection.giftType == 'skuContentGroup'))}">
        <noscript>
        
          <dsp:a href="noJsGiftSelection.jsp" iclass="atg_store_basicButton">
            <dsp:param name="giftType" param="selection.giftType" />
            <dsp:param name="giftDetail" param="selection.giftDetail" />
            <dsp:param name="giftHashCode" param="selection.giftHashCode" />
            <dsp:param name="promotionId" param="selection.promotionId" />
            <dsp:param name="selectedSkuId" param="currentItem.auxiliaryData.catalogRef.repositoryId"/>
            <dsp:param name="selectedProductId" param="currentItem.auxiliaryData.productRef.repositoryId"/>
            <dsp:param name="commerceItemId" param="currentItem.id"/>
    
            <span>
              <fmt:message key="common.button.changeText" />
            </span>
            
          </dsp:a>
          
        </noscript>
        
        <%-- Construct an URL with gift selector information and pass selected SKU info. --%>
        <dsp:getvalueof var="skuType" param="currentItem.auxiliaryData.catalogRef.type"/>
        
        <c:choose>
          <c:when test="${skuType == 'clothing-sku'}">
            <dsp:getvalueof var="selectedColor" param="currentItem.auxiliaryData.catalogRef.color"/>
            <dsp:getvalueof var="selectedSize" param="currentItem.auxiliaryData.catalogRef.size"/>
          </c:when>
          <c:otherwise>
            <dsp:getvalueof var="selectedColor" param="currentItem.auxiliaryData.catalogRef.woodFinish"/>
          </c:otherwise> 
        </c:choose>
        
        <c:url var="selectionUrl" value="gadgets/giftSelection.jsp">
          <c:param name="giftType" value="${selection.giftType}"/>
          <c:param name="giftDetail" value="${selection.giftDetail}"/>
          <c:param name="giftHashCode" value="${selection.giftHashCode}"/>
          <c:param name="promotionId" value="${selection.promotionId}"/>
          <c:param name="selectedSkuId" value="${currentItem.auxiliaryData.catalogRef.repositoryId}"/>
          <c:param name="selectedProductId" value="${currentItem.auxiliaryData.productRef.repositoryId}"/>
          <c:param name="commerceItemId" value="${currentItem.id}"/>
          <c:param name="selectedColor" value="${selectedColor}"/>
          <c:param name="selectedSize" value="${selectedSize}"/>
        </c:url>
        
        <%--
          Link to the JS-based pop over dialog.
          We need to initialize HREF with proper link onclick first.
        --%>
        <a href="#" class="atg_store_basicButton atg_store_gwpSelectButton" 
           onclick="dijit.byId('giftSelectorDialog').href='${selectionUrl}'; dijit.byId('giftSelectorDialog').refresh(); dijit.byId('giftSelectorDialog').show();"> 
          <span>
            <fmt:message key="common.button.changeText" />
          </span>
        </a>
        
      </c:if>
    </td>
  
    <%-- Display all necessary buttons for the current item. --%>
    <td class="cartActions">
      <ul>
        
        <dsp:include page="itemListingButtons.jsp">
          <%-- Count is used to construct input names. Don't use dynamic names. --%>
          <dsp:param name="count" value="${count}"/>
          
          <%-- GWP line count for commerce items with gwp to construct unique input names. --%>
          <dsp:param name="gwpCount" param="gwpCount"/>
          
          <%-- We cannot add gift item to the gift list, wish list, or comparison list here --%>
          <dsp:param name="navigable" value="false"/>
          
          <%-- Pass selection so we can perform gift-specific operations --%>
          <dsp:param name="selection" value="${selection}" />
        </dsp:include>
        
      </ul>
    </td>
  
    <td class="price">
      
      <fmt:message key="common.FREE"/>
      
      <%-- 
        Gift place holder doesn't provide any promotion notes but provide promotion ID instead.
        We will retrieve promotion repository item using PromotionLookup and first and then
        get promotion's display name.
        
        Input Parameters
          id
            promotion ID
          
        Output Parameters
          element
            promotion repository item  
      --%>
      <dsp:droplet name="PromotionLookup">
        <dsp:param name="id" param="selection.promotionId"/>
        <dsp:getvalueof var="promotion" param="element"/>
        <preview:repositoryItem item="${promotion}">
        
          <dsp:oparam name="output">
            <span class="atg_store_discountNote">
              <dsp:valueof param="element.displayName" valueishtml="true"/>
            </span>  
          </dsp:oparam>
        </preview:repositoryItem>
      </dsp:droplet>
    </td>
  
    <td class="quantity">
      <%-- Display '1' as item quantity but pass real commerce item quantity via hidden field. --%>
      1
    </td>
  
    <td class="total">
      <fmt:message key="common.FREE"/>
    </td>

  </tr>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/gadgets/giftItem.jsp#3 $$Change: 788278 $--%>
<%-- 
  This page renders a row entry for the Wish List item 
  
  Required parameters:
    giftlistId
      The wish list ID 
    giftItem 
      repository item representing a SKU added to the Wish List 
    product
      repository item of the product whose SKU is added to the Wish List
      
  Optional parameters:
    None    
--%>

<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>

  <%-- 
    Search for SKU by the 'giftItem.catalogRefId'.
    
    Input parameters:
      id
        SKU id associated with the giftItem
      filterByCatalog
        set to false to display items from another catalog
      filterBySite
        set to false to display items from another sites
      
    Output parameters:
      element
        SKU item for the given id          
  --%>
  <dsp:droplet name="SKULookup">
    <dsp:param name="id" param="giftItem.catalogRefId"/>
    <dsp:param name="filterByCatalog" value="false"/>
    <dsp:param name="filterBySite" value="false"/>

    <dsp:oparam name="output">
    
      <%-- Set giftSku parameter for further usage --%>
      <dsp:setvalue param="giftSku" paramvalue="element"/>
      
      <dsp:getvalueof var="giftItemId" vartype="java.lang.String" param="giftItem.repositoryId"/>
      <dsp:getvalueof var="messageFormId" vartype="java.lang.String" value="formid_${giftItemId}"/>
      
      <%--
        Display site indicator only for 
        sites that share atg.ShoppingCart shareable,
        otherwise don't display site column. 
      --%>
	  <%--
        CartSharingSitesDroplet returns a collection of sites that share the shopping
	    cart shareable (atg.ShoppingCart) with the current site.
	    You may optionally exclude the current site from the result.

        Input Parameters:
          excludeInputSite - Should the returned sites include the current
   
        Open Parameters:
          output - This parameter is rendered once, if a collection of sites
                   is found.
   
        Output Parameters:
          sites - The list of sharing sites.
      --%>
      <dsp:droplet name="/atg/dynamo/droplet/multisite/CartSharingSitesDroplet">
        <dsp:param name="excludeInputSite" value="true"/>
        <dsp:oparam name="output">
          <td class="site">
            <%-- display site information for the given product as icon --%>
            <dsp:include page="/global/gadgets/siteIndicator.jsp">
              <dsp:param name="mode" value="icon"/>              
              <dsp:param name="siteId" param="giftItem.siteId"/>
              <dsp:param name="product" param="product"/>
            </dsp:include>
          </td>
        </dsp:oparam>
      </dsp:droplet>
      
      <%-- Product image --%>
      <td class="image">
        <dsp:include page="/browse/gadgets/productImg.jsp">
          <dsp:param name="product" param="product"/>
          <dsp:param name="image" param="product.smallImage"/>
          <dsp:param name="siteId" param="giftItem.siteId"/>          
          <dsp:param name="defaultImageSize" value="small"/>    
        </dsp:include>
        <dsp:getvalueof var="pageurl" value="${siteLinkUrl}"/>
      </td>
    
      <%--
        Display name and additional info 
       --%>
      <dsp:getvalueof var="productDisplayName" param="giftSku.displayName"/> 
      <c:if test="${empty productDisplayName}">
        <dsp:getvalueof var="productDisplayName" param="product.displayName"/>
        <c:if test="${empty productDisplayName}">
          <fmt:message var="productDisplayName" key="common.noDisplayName" />
        </c:if>  
      </c:if> 
       
      <td class="item" scope="row" abbr="${productDisplayName}">     
        <%-- Get this products template --%>
        <dsp:getvalueof var="pageurl" vartype="java.lang.String" param="product.template.url"/>
        <c:choose>
          <%-- If the product has a template generate a link --%>
          <c:when test="${not empty pageurl}">
            <p class="name">
              <dsp:include page="/global/gadgets/crossSiteLink.jsp">
                <dsp:param name="siteId" param="giftItem.siteId"/>
                <dsp:param name="product" param="product"/>
                <dsp:param name="skuDisplayName" param="giftSku.displayName"/>
              </dsp:include>
            </p>
           
            <dsp:getvalueof var="description" vartype="java.lang.String" param="giftSku.description"/>
            <c:if test="${empty size && empty color && !empty description}">
              <p><c:out value="${description}"/></p>
            </c:if>   
          </c:when>
          <%-- Otherwise just display the displayname --%>
          <c:otherwise>
            <%-- Product Template not set --%>
            <p class="brand"><ftm:message key="common.brandName"/></p>
            <c:out value="${productDisplayName}"/>
              
            <!-- end repeat-->
            
          </c:otherwise>
        </c:choose>
    
        <%--
          SKU information: size, color, availability status
         --%>
        <dsp:include page="/global/util/displaySkuProperties.jsp">
          <dsp:param name="product" param="product"/>
          <dsp:param name="sku" param="giftSku"/>
          <dsp:param name="displayAvailabilityMessage" value="true"/>
        </dsp:include>
      </td>
      
      <%-- SKU price --%>
      <td class="price">
        <dsp:include page="/global/gadgets/priceLookup.jsp">
          <dsp:param name="product" param="product"/>
          <dsp:param name="sku" param="giftSku"/>
        </dsp:include>
      </td>
          
      <%-- The quantity and wishlist actions button --%>
      <td class="quantity_fav_actions quantity">
        
        <dsp:form action="${originatingRequest.requestURI}" 
                  method="post" name="${messageFormId}" 
                  id="${messageFormId}" formid="${messageFormId}">
          
          <fmt:message var="qtyTitle" key="common.qty"/>
          <dsp:input bean="CartModifierFormHandler.quantity" iclass="qty atg_store_numericInput" 
                   type="text" paramvalue="giftItem.quantityRemaining" title="${qtyTitle}"/>
    
          <%-- SKU availability lookup --%>
          <dsp:include page="/global/gadgets/skuAvailabilityLookup.jsp">
            <dsp:param name="product" param="product"/>
            <dsp:param name="skuId" param="giftSku.Id"/>
          </dsp:include>
            
          <div class="wishList_actions">
            <%-- The Action Button --%>
            <c:choose>
              <c:when test="${not empty addButtonText}">
                <%-- Hidden params in case the form errors --%>
                <dsp:getvalueof var="currentCategoryId" vartype="java.lang.String" param="categoryId"/>
                <dsp:getvalueof var="currentProductId" vartype="java.lang.String" param="productId"/>
                <input type="hidden" name="categoryId" value="<dsp:valueof value='${currentCategoryId}'/>"/>
                <input type="hidden" name="productId" value="<dsp:valueof value='${currentProductId}'/>"/>
    
                <%-- Set the successURL --%>
                <dsp:getvalueof var="contextroot" bean="/OriginatingRequest.contextPath"/>
                <dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL" type="hidden" 
                           value="${contextroot}/cart/cart.jsp"/>
    
                <%-- Set the errorURL --%>
                <dsp:input bean="CartModifierFormHandler.addItemToOrderErrorURL" type="hidden" 
                           value="${contextroot}/myaccount/myWishList.jsp"/>
                
                <%-- Set the sessionExpirationURL --%>
                <dsp:input bean="CartModifierFormHandler.sessionExpirationURL" type="hidden" 
                           value="${contextroot}/global/sessionExpired.jsp"/>
    
                <%-- URLs for the RichCart AJAX response. Renders cart contents as JSON --%>
                <dsp:input bean="CartModifierFormHandler.ajaxAddItemToOrderSuccessUrl" type="hidden"
                           value="${contextroot}/cart/json/cartContents.jsp"/>
                <dsp:input bean="CartModifierFormHandler.ajaxAddItemToOrderErrorUrl" type="hidden"
                           value="${contextroot}/cart/json/errors.jsp"/>
    
                <dsp:input bean="CartModifierFormHandler.productId" paramvalue="product.repositoryId" type="hidden"/>
                <dsp:input bean="CartModifierFormHandler.catalogRefIds" paramvalue="giftSku.repositoryId" type="hidden"/>
                <dsp:input bean="CartModifierFormHandler.siteId" paramvalue="giftItem.siteId" type="hidden"/>
                <dsp:input bean="CartModifierFormHandler.giftlistId" paramvalue="giftlistId" type="hidden"/>
                <dsp:input bean="CartModifierFormHandler.giftlistItemId" value="${giftItemId}" type="hidden"/>
    
                <%-- The only submit we got --%>
                <span class="atg_store_basicButton add_to_cart_link">
                  <dsp:input bean="CartModifierFormHandler.addItemToOrder" 
                             type="submit" value="${addButtonText}"
                             title="${addButtonTitle}" 
                             iclass="atg_behavior_addItemToCart" 
                             id="atg_store_wishListAdd"/>
                </span>
              </c:when>
              
              <%--
                If item is not available,
                display 'Email me when in stock' link 
               --%>
              <c:otherwise>
                <%-- Unavailable --%>
                <dsp:getvalueof var="contextroot" bean="/OriginatingRequest.contextPath"/>
                <dsp:getvalueof var="sid" param="giftSku.repositoryId"/>
                <dsp:getvalueof var="pid" param="product.repositoryId"/>
                <fmt:message var="emailMeInStockText" key="common.button.emailMeInStockText"/>
                <fmt:message var="emailMeInStockTitle" key="common.button.emailMeInStockTitle"/>
                
                <%-- Build Notify Me popup URL --%>
                <c:url var="notifyMePopupUrl" value="/browse/notifyMeRequestPopup.jsp">
                  <c:param name="skuId"><dsp:valueof param="giftSku.repositoryId" /></c:param>
                  <c:param name="productId"><dsp:valueof param="product.repositoryId"/></c:param>
                </c:url>
    
                <a href="${notifyMePopupUrl}" 
                   title="${emailMeInStockTitle}" 
                   class="atg_store_basicButton atg_store_emailMe" 
                   target="popup">
                  <span><c:out value="${emailMeInStockText}"/></span>
                </a>
                
                <div class="atg_store_emailMeMessage">
                  <fmt:message key="common.whenAvailable"/>
                </div>
              </c:otherwise>
            </c:choose>
    
            <%-- Delete item from wish list --%>
            <div class="atg_store_wishListDelete">
              <dsp:getvalueof var="giftId" vartype="java.lang.String" param="giftItem.id"/>
              <dsp:input type="hidden" bean="GiftlistFormHandler.removeItemsFromGiftlistSuccessURL"
                         value="${originatingRequest.contextPath}/myaccount/myWishList.jsp"/>
              <dsp:input type="hidden" bean="GiftlistFormHandler.removeItemsFromGiftlistErrorURL"
                         value="${originatingRequest.contextPath}/myaccount/myWishList.jsp"/>
              <dsp:input type="hidden" bean="GiftlistFormHandler.giftlistId" paramvalue="giftlistId"/>
              <dsp:input type="hidden" bean="GiftlistFormHandler.removeGiftitemIds" paramvalue="giftItem.id"/>
    
              <fmt:message var="removeText" key="common.button.removeText"/>
          
              <dsp:input type="submit" bean="GiftlistFormHandler.removeItemsFromGiftlist" 
                         value="${removeText}" id="atg_store_deleteFromWishList" 
                         iclass="atg_store_textButton"/>
            </div>
          </div>
        </dsp:form>
      </td>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/wishListRow.jsp#3 $$Change: 788278 $--%>

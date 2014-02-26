<%-- 
  This gadget renders the "Delete", "Add to Comparisons" and "Add To WishList" buttons for each
  item in the cart.

  Required Parameters:
    currentItem
      The current item we're trying to add/delete.
    count
      The current number for the rendered commerce item.

  Optional Parameters:
    navigable
      boolean value that defines whether an item can be added to the wish list or not.
    selection
      gift selection bean associated with the currentItem    
    missedQty
      quantity of commerce items except gift items (if the current item has associated selection bean)
    gwpCount
      The current number of the rendered line for the GWP commerce item. The commerce item with GWP
      markers can be rendered on several lines, as we render non-GWP quantity of commerce item and every
      1 quantity of gift item on separate lines.    
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/store/profile/SessionBean"/>
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListContains"/>
  <dsp:importbean bean="/atg/commerce/catalog/comparison/ProductList"/>
  <dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
  <dsp:importbean bean="/atg/commerce/collections/filter/droplet/GiftlistSiteFilterDroplet"/>
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>
  
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="navigable" param="navigable"/>
  <dsp:getvalueof id="productId" param="currentItem.auxiliaryData.productId"/>
  <dsp:getvalueof id="currentItemId" param="currentItem.id"/>
  <dsp:getvalueof id="nameSuffix" param="count"/>
  <dsp:getvalueof id="gwpCount" param="gwpCount"/>
  <dsp:getvalueof id="currentItem" param="currentItem"/>
  <dsp:getvalueof id="missedQty" param="missedQty"/>
  <dsp:getvalueof var="auxiliaryDataType" vartype="java.lang.String" 
                  param="currentItem.auxiliaryData.catalogRef.type"/>
  <dsp:getvalueof var="selection" param="selection" />
  
  <fmt:message var="deleteButtonTitle" key="common.button.removeText"/>
  
  <c:if test="${not empty gwpCount}">
    <c:set var="nameSuffix" value="${nameSuffix}_${gwpCount}"/>
  </c:if>

  <c:choose>
    <c:when test='${auxiliaryDataType == "sampleSku"}'>
      <li>
       <dsp:input iclass="atg_store_textButton atg_store_actionDelete" type="submit" name="remove_ci_${nameSuffix}"
                   bean="CartModifierFormHandler.removeItemFromOrder" value="${deleteButtonTitle}" 
                   submitvalue="${currentItemId}"/>
      </li>
    </c:when>
    <c:when test='${auxiliaryDataType == "webhostSku"}'>
      <li>
        <dsp:input iclass="atg_store_textButton atg_store_actionDelete" type="submit" name="remove_ci_${nameSuffix}"
                   bean="CartModifierFormHandler.removeItemFromOrder" value="${deleteButtonTitle}" 
                   submitvalue="${currentItemId}"/>
      </li>
    </c:when>
    <c:otherwise>
      <li>
      
        <c:choose>
         
          <%--
            A gift item. 
          --%>
          <c:when test="${not empty selection && empty missedQty}">
            <dsp:input iclass="atg_store_textButton atg_store_actionDelete" type="submit" name="remove_gwp_${nameSuffix}" 
                       bean="CartModifierFormHandler.removeItemGwp" value="${deleteButtonTitle}" 
                       submitvalue="${currentItemId}"/>
          </c:when>
          
          <%--
            Not a gift item. But there is the same item in order which is gift so we need
            to update the quantity rather than remove the item completely.
          --%>
          <c:when test="${not empty missedQty}">
            <dsp:input type="hidden" converter="map"
                      bean="CartModifierFormHandler.itemNonGwpQuantities" value="${currentItemId}=${missedQty}"/>
            <dsp:input iclass="atg_store_textButton atg_store_actionDelete" type="submit" name="remove_non_gwp_${nameSuffix}"
                       bean="CartModifierFormHandler.removeItemNonGwp" value="${deleteButtonTitle}"
                       submitvalue="${currentItemId}"/>
          </c:when>
          
          <%--
            Not a gift item and there is no the same item in order which is gift.
          --%>
          <c:otherwise>
            <%--
              You should never use unique IDs in form element names or a memory leak will result. 
              We are using 'count' for names to so DropletEventServlet can reuse entries instead of 
              creating new ones (which is happen when name contains ${commerceItem.id} which is unique
              and cannot be reused).
            --%>
            <dsp:input iclass="atg_store_textButton atg_store_actionDelete" type="submit" name="remove_ci_${nameSuffix}"
                       bean="CartModifierFormHandler[${count}].removeItemFromOrder" value="${deleteButtonTitle}" 
                       submitvalue="${currentItemId}"/>
                       
          </c:otherwise>
        </c:choose>
      </li>
  
      <%-- Non navigable items can't be added to wish list --%>
      <dsp:getvalueof var="shippingGroupRelationships" vartype="java.lang.Object" 
                      param="currentItem.shippingGroupRelationships"/>
                      
      <c:if test="${not empty shippingGroupRelationships}">
        <c:forEach var="sgRel" items="${shippingGroupRelationships}" varStatus="status">
  
          <dsp:param name="sgRel" value="${sgRel}"/>
          
          <%-- We only care about the first shipping group --%>
          <c:if test='${status.index == "0"}'>
            
            <dsp:getvalueof var="shippingGroupClassType" param="sgRel.shippingGroup.shippingGroupClassType"/>
  
            <c:if test="${navigable}">
              
              <fmt:message var="moveToWishListTitle" key="common.button.moveToWishListTitle"/>
              <%-- The items Sku id --%>
              
              <dsp:getvalueof param="currentItem.catalogRefId" var="skuId" vartype="java.lang.String"/>
              <%-- The items Site id --%>
              <dsp:getvalueof param="currentItem.auxiliaryData.siteId" var="skuSite" vartype="java.lang.String"/>
              
              <%-- Determine which wishlist icon to display --%>
              <c:set var="viewWishListButtonStyle" value=""/>
              
              <%--
                This GiftlistSiteFilterDroplet is used to execute the GiftlistSiteFilter collection filter.
  
                The GiftlistSiteFilter filters gift list and gift list items in the collection depending on
                whether they belong to the specified site/shareable or not.
  
                Input Parameters:
                  collection
                    The unfiltered collection (required).
  
                Open Parameters:
                  output
                    This parameter is rendered once upon successful completion of the filter.
                  empty
                    This parameter is rendered if the filtered collection is null or contains no objects.
                  error
                    This parameter is rendered if an error occurs.
  
                Output Parameters:
                  filteredCollection
                    This output parameter references the filtered collection.
                  errorMsg
                    This output parameter contains an error message when processing errors occur.
              --%>
              <dsp:droplet name="GiftlistSiteFilterDroplet">
                <dsp:param name="collection" bean="Profile.wishlist.giftlistItems"/>
                <dsp:oparam name="output">
                  
                  <dsp:getvalueof param="filteredCollection" var="items" vartype="java.util.Collection"/>
                  
                  <c:forEach items="${items}" var="item">
                   
                    <dsp:param name="item" value="${item}"/>
                    <dsp:getvalueof param="item.catalogRefId" var="currentSkuId" vartype="java.lang.String"/>
  
                    <%-- Check if this item exists in the WishList --%>
                    <c:if test="${skuId == currentSkuId}">
                      <c:set var="viewWishListButtonStyle" value="atg_store_viewWishlist"/>                   
                    </c:if>
  
                  </c:forEach>
                </dsp:oparam>
              </dsp:droplet>
  
              <%-- WishList --%>
              <li>
                <c:choose>
                  <%-- Move to WishList --%>
                  <c:when test="${empty viewWishListButtonStyle}">
                    
                    <dsp:a page="/cart/cart.jsp" title="${moveToWishListTitle}"
                           iclass="atg_store_actionAddToWishList" id="${itemId}">
                      
                      <fmt:message key="common.button.moveToWishListText"/>
  
                      <dsp:property bean="GiftlistFormHandler.giftlistId" beanvalue="Profile.wishlist.id"/>
                      <dsp:property bean="GiftlistFormHandler.itemIds" paramvalue="currentItem.id"/>
                      <dsp:property bean="GiftlistFormHandler.quantity" value="1"/>
                      <dsp:property bean="GiftlistFormHandler.moveItemsFromCartSuccessURL"
                                    value="${pageContext.request.requestURI}"/>
                      <dsp:property bean="GiftlistFormHandler.moveItemsFromCartErrorURL"
                                    value="${pageContext.request.requestURI}"/>
                      <dsp:property bean="GiftlistFormHandler.moveItemsFromCartLoginURL"
                                    value="${contextPath}/global/util/loginRedirect.jsp"/>
                      <dsp:property bean="GiftlistFormHandler.moveItemsFromCart" value="true"/>
                    </dsp:a>
                    
                  </c:when>
  
                  <%-- View WishList --%>
                  <c:otherwise>
                    
                    <c:url value="/myaccount/myWishList.jsp" var="wishListUrl" scope="page"/>
                    
                    <dsp:a href="${wishListUrl}" iclass="${viewWishListButtonStyle}">
                      <fmt:message key="common.button.viewWishListText"/>
                    </dsp:a>
                    
                  </c:otherwise>
                </c:choose>
              </li>
            </c:if>
          </c:if>
        </c:forEach>
      </c:if>
    </c:otherwise>
  </c:choose><%-- End Check to see what kind of sku we had --%>
  
  <li>
    <%-- 
      The ProductListContains droplet queries whether a ProductComparisonList contains
      a specified product, category, and sku.
  
      Input Parameters:
        productList
          Specifies the ProductComaprisonList object to examine.  This can also be
          specified as a property of the droplet but only if the product comparison 
          list and the droplet have compatible scopes. A globally scoped droplet should 
          not try to refer to a session scoped comparison list in its property-based 
          configuration.
        productID
          Specifies the repository id of the product to look for in productList (required).
        siteID
          Specifies the repository id of the product to look for in productList.
  
      Output Parameters:
        true
          If the product comparison list contains the specific product, category, and sku.
        false
          If the product comparison list does not contain the specified product, category, and sku.
    --%>
    <dsp:droplet name="ProductListContains">
      <dsp:param name="productList" bean="ProductList"/>
      <dsp:param name="productID" value="${productId}"/>
      <dsp:param name="siteID" param="currentItem.auxiliaryData.siteId"/>
  
      <dsp:oparam name="false">
        <c:if test="${navigable}">
          
          <fmt:message var="addToComparisonsSubmitTitle" key="browse_productAction.addToComparisonsSubmit"/>
          
          <dsp:a page="/cart/cart.jsp"
                 title="${addToComparisonsTitle}"
                 iclass="atg_store_actionAddToComparisons">
            
            <fmt:message key="browse_productAction.addToComparisonsSubmit"/>
           
            <%-- load the values into the form handler bean --%>
            <dsp:property bean="ProductListHandler.productID" paramvalue="currentItem.auxiliaryData.productId" 
                          name="pID" />
            <dsp:property bean="ProductListHandler.addProductSuccessURL" beanvalue="/OriginatingRequest.requestURI" 
                          name="or_rURI" />
            <dsp:property bean="ProductListHandler.siteID" paramvalue="currentItem.auxiliaryData.siteId"/>
  
            <%-- Now call the method on the form handler bean --%>
            <dsp:property bean="ProductListHandler.addProduct" value="" />
            
          </dsp:a>
        </c:if>
      </dsp:oparam>
  
      <dsp:oparam name="true">
        
        <fmt:message var="linkTitle" key="browse_productAction.viewComparisonsTitle"/>
        
        <dsp:a page="/comparisons/productComparisons.jsp" title="${linkTitle}" iclass="atg_store_viewComparisons">        
          <fmt:message key="browse_productAction.viewComparisonsLink"/>
        </dsp:a>
      
      </dsp:oparam>
    </dsp:droplet>
  </li>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/gadgets/itemListingButtons.jsp#3 $$Change: 788278 $--%>

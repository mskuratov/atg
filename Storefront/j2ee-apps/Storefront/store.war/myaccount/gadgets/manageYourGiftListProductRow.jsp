<%--
  This gadget renders the table row with gift item details. The following information is rendered for
  each gift item:
    site icon (if current site is in the group with other sites that share shopping cart),
    SKU or product thumbnail image,
    SKU display name,
    SKU attributes (color, size, etc),
    SKU description,
    SKU price,
    number of gift items requested by owner,
    SKU availability message,
    quantity input field to update the number of desired items,
    action button that corresponds current SKU availability status,
    remove item button.
    
  Required parameters:
    giftlistItem
      Gift list item to display.
    giftlist
      Gift list to which item belongs. 
    product 
      Gift item's product repository item.
    count
      A 1-based index of current gift item, used to construct correct style for a table row depending
      on item position in the list.
    size
      Total number of gift list items in the list.
      
  Optional parameters:
    displaySiteIndicator
      If 'true' site icon will be displayed for gift item ('false' by default).
    start
      The pagination parameter: the index (1 based) of first element to show on the page.
      It is used to build remove item link with the same pagination settings as the current page.
    viewAll
      The pagination parameter: it is set to true if all items should be displayed on the page.
      It is used to build remove item link with the same pagination settings as the current page.
    howMany
      The pagination parameter: specifies how many items should be displayed per page. It is used
      to build remove item link with the same pagination settings as the current page.
    
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>

  <dsp:getvalueof var="count" idtype="int" param="count"/>
  <dsp:getvalueof var="size" idtype="int" param="size"/>
  <dsp:getvalueof var="displaySiteIndicator" param="displaySiteIndicator"/>
  <dsp:getvalueof var="viewAll" param="viewAll"/>
  <dsp:getvalueof var="howMany" param="howMany"/>
  <dsp:getvalueof var="start" param="start"/>
  <%-- Get number of items displayed on the page --%>
  <dsp:getvalueof var="pageSize" vartype="java.lang.Object" 
                  bean="/atg/multisite/SiteContext.site.defaultPageSize"/>  
  
  <%-- Build the URL for Remove Item action. --%>
  <c:if test="${start == size && (start - pageSize > 0)}">
    <%-- 
      This the the last item on the page so after removing this item the previous page
      should be displayed. --%>
    <c:set var="start" value="${start - pageSize}" />
  </c:if>
  <c:url var="removeItemLink" value="/myaccount/giftListEdit.jsp">    
    <c:if test="${not empty start}">
      <c:param name="start" value="${start}" />
    </c:if>
    <c:if test="${not empty viewAll}">
      <c:param name="viewAll" value="${viewAll}" />
      <c:param name="howMany" value="${howMany}" />
    </c:if>
  </c:url>
    
  <%--
    Lookup for a SKU item that corresponds to a given gift list item.
    SKULookup droplet renders in output parameter only items that belong to current site and
    catalog. As in the gift list there are items not from the current site and catalog the 
    'filterBySite' and 'filterByCatalog' parameters are set to 'false'.
    
    Input parameters:
      id
        SKU ID to lookup for
        
    Output parameters:
      element
        SKU repository item
        
    Open parameters:
      output
        Rendered if the item that belongs current site and catalog was found in the repository
   --%>  
  <dsp:droplet name="SKULookup">
    <dsp:param name="id" param="giftlistItem.catalogRefId"/>
    <dsp:param name="filterBySite" value="false"/>
    <dsp:param name="filterByCatalog" value="false"/>
    <dsp:oparam name="output">
      <dsp:param name="sku" param="element"/>
    
      <tr class="<crs:listClass count='${count}' size='${size}' selected="false" />">
        <%-- Display site icon if displaySiteIndicator is 'true'. --%>
        <c:if test="${displaySiteIndicator}">
          <td>
            <dsp:include page="/global/gadgets/siteIndicator.jsp">
              <dsp:param name="mode" value="icon"/>              
              <dsp:param name="siteId" param="giftlistItem.siteId"/>
              <dsp:param name="product" param="product"/>
            </dsp:include>
          </td>
        </c:if>
    
        <%-- 
          SKU or product thumbnail image.
          At first check if SKU contains thumbnail image and if not 
          display product's thumbnail image.
          --%>
        <dsp:getvalueof var="smallImageUrl" param="sku.smallImage.url"/>
        <c:choose>
          <c:when test="${not empty smallImageUrl}">
            <td class="image">
              <dsp:include page="/browse/gadgets/productImg.jsp">
                <dsp:param name="product" param="product"/>
                <dsp:param name="image" param="product.smallImage"/>
                <dsp:param name="alternateImage" param="sku.smallImage"/>
                <dsp:param name="siteId" param="giftlistItem.siteId"/>                
                <dsp:param name="defaultImageSize" value="small"/>    
              </dsp:include>
            </td>
          </c:when>
          <c:otherwise>
            <td class="image">
              <dsp:include page="/browse/gadgets/productImg.jsp">
                <dsp:param name="product" param="product"/>
                <dsp:param name="image" param="product.smallImage"/>
                <dsp:param name="siteId" param="giftlistItem.siteId"/>                
                <dsp:param name="defaultImageSize" value="small"/>    
              </dsp:include>
            </td>
          </c:otherwise>
        </c:choose>
                      
        <dsp:getvalueof var="productDisplayName" param="sku.displayName"/>
        <c:if  test="${empty productDisplayName}">
          <dsp:getvalueof var="productDisplayName" param="product.displayName"/>
          <c:if test="${empty productDisplayName}">
            <fmt:message var="productDisplayName" key="common.noDisplayName" />
          </c:if> 
        </c:if>
         
        <td class="item" scope="row" abbr="${productDisplayName}">
          <%-- Get the product's template --%>
          <dsp:getvalueof var="pageurl" vartype="java.lang.String" param="product.template.url"/>
          <c:choose>
            <%-- If the product has a template generate a link --%>
            <c:when test="${not empty pageurl}">
              <p class="name">
                <dsp:include page="/global/gadgets/crossSiteLink.jsp">
                  <dsp:param name="siteId" param="giftlistItem.siteId"/>
                  <dsp:param name="product" param="product"/>
                  <dsp:param name="skuDisplayName" param="sku.displayName"/>
                </dsp:include>
              </p>
            </c:when>
            <%-- Otherwise just display the product's  display name. --%>
            <c:otherwise>
              <dsp:valueof value="${productDisplayName}"/>
            </c:otherwise>
          </c:choose>
    
          <%-- SKU attributes, e.g., color, size, etc. --%>
          <dsp:include page="/global/util/displaySkuProperties.jsp">
            <dsp:param name="product" param="product"/>
            <dsp:param name="sku" param="sku"/>
            <dsp:param name="displayAvailabilityMessage" value="true"/>
          </dsp:include>
        </td>
        
        <%-- SKU price --%>
        <td class="numerical price atg_store_productPrice">
          <dsp:include page="/global/gadgets/priceLookup.jsp">
            <dsp:param name="product" param="product"/>
            <dsp:param name="sku" param="sku"/>
          </dsp:include>
        </td>
        
        <%-- Quantity input fields to update desired quantity for the gift item. --%>
        <td class="requstd quantity">
            <dsp:getvalueof var="quantityDesired" vartype="java.lang.String" 
                            param="giftlistItem.quantityDesired"/>
          <input name="<dsp:valueof param="giftlistItem.id"/>" size="2" value="<fmt:formatNumber value="${quantityDesired}" type="number"/>" class="text qty atg_store_numericInput" title="<fmt:message key="common.qty" />"/>
        </td>
        
        <%-- Remaining quantity --%>
        <td class="remain">
          <dsp:getvalueof var="quantityRemaining" vartype="java.lang.String" 
                          param="giftlistItem.quantityRemaining"/>
          <fmt:formatNumber value="${quantityRemaining}" type="number"/>
        </td>
        
        <td class="atg_store_actionItems">
          <div class="atg_store_giftListActions">
          
            <%-- Display 'Add to cart' action if item is still needed. --%>
            <dsp:getvalueof var="quantityRemaining" param="giftlistItem.quantityRemaining"/>
            <c:choose>
              <c:when test="${quantityRemaining != '0'}">
                <%-- Display Add to Cart button with text message corresponding to SKU's availability status. --%>
                <dsp:include page="giftListAddToCart.jsp">
                  <dsp:param name="giftlistItem" param="giftlistItem"/>
                  <dsp:param name="giftlist" param="giftlist"/>
                  <dsp:param name="product" param="product"/>
                  <dsp:param name="skuId" param="sku.repositoryId"/>
                </dsp:include>
              </c:when>
            </c:choose>
            
            <%-- Delete action --%>
            <div class="atg_store_GiftListItemDelete">
              <fmt:message var="deleteTitle" key="myaccount_manageYourGiftlistProductRow.deleteItem"/>
              <dsp:a title="${deleteTitle}" href="${removeItemLink}" iclass="atg_store_giftListRemove">
                <dsp:param name="giftId" param="giftlistItem.id"/>
                <dsp:param name="giftlistId" param="giftlist.repositoryId"/>
                <span>
                  <fmt:message key="common.button.removeText"/>
                </span>
              </dsp:a>
            </div>
          </div>
        </td>
      </tr>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/manageYourGiftListProductRow.jsp#1 $$Change: 735822 $--%>

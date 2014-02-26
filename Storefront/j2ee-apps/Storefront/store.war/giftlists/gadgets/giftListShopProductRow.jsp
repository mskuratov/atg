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
    number of gift items that are still needed,
    SKU availability message,
    quantity input field to specify how many items to add to cart (if SKU is not out of stock),
    action button that corresponds current SKU availability status.
    
  Required parameters:
    giftlistItem
      Gift list item to display.
    giftlist
      Gift list to which item belongs. 
    product 
      Gift item's product repository item.    
    errorPath
      URL to which user will be redirected if error occurred during adding item to cart.
    count
      A 1-based index of current gift item, used to construct correct style for a table row depending
      on item position in the list 
    size
      Total number of gift list items in the list.
      
  Optional parameters:
    displaySiteIndicator
      If 'true' site icon will be displayed for gift item ('false' by default).
 --%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
  
  <dsp:getvalueof var="contextroot" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="originatingRequest" bean="/OriginatingRequest"/>
  
  <dsp:getvalueof id="count" param="count"/>
  <dsp:getvalueof id="size" param="size"/>
  <dsp:getvalueof id="displaySiteIndicator" param="displaySiteIndicator"/>
  <dsp:getvalueof id="errorPath" param="errorPath"/>
  
  <%--
    Lookup for a SKU item that corresponds to a given gift list item.
    SKULookup droplet renders in output parameter only items that belong to current site and
    catalog. As in gift list there are items from not current site and catalog the 'filterBySite'
    and 'filterByCatalog' parameter are false.
    
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
      <tr class="<crs:listClass count="${count}" size="${size}" selected="false"/>">
        <%-- Display site icon if displaySiteIndicator is 'true'. --%>
        <c:if test="${displaySiteIndicator}">
          <td class="site">
            <dsp:include page="/global/gadgets/siteIndicator.jsp">
              <dsp:param name="mode" value="icon"/>              
              <dsp:param name="siteId" param="giftlistItem.siteId"/>
              <dsp:param name="product" param="product"/>
            </dsp:include>
          </td>
        </c:if>
    
        <%--
          SKU or product thumbnail image.
          At first check if SKU contains small image and if not display product's small image.
        --%>  
        <td class="image">
          <dsp:getvalueof var="skuThumbnailImageUrl" param="sku.smallImage.url"/>
          <c:choose>
            <c:when test="${empty skuThumbnailImageUrl}">
              <dsp:include page="/browse/gadgets/productImg.jsp">
                <dsp:param name="product" param="product"/>
                <dsp:param name="image" param="product.smallImage"/>
                <dsp:param name="siteId" param="giftlistItem.siteId"/>
              </dsp:include>
            </c:when>
            <c:otherwise>
              <dsp:include page="/browse/gadgets/productImg.jsp">
                <dsp:param name="product" param="product"/>
                <dsp:param name="image" param="product.smallImage"/>
                <dsp:param name="siteId" param="giftlistItem.siteId"/>
                <dsp:param name="alternateImage" param="sku.smallImage"/>
              </dsp:include>
            </c:otherwise>
          </c:choose>
        </td>

        <%--
          Display name and additional info 
         --%>
        <dsp:getvalueof var="productDisplayName" param="sku.displayName"/> 
        <c:if test="${empty productDisplayName}">>
          <dsp:getvalueof var="productDisplayName" param="product.displayName"/>
          <c:if test="${empty productDisplayName}">
            <fmt:message var="productDisplayName" key="common.noDisplayName" />
          </c:if>  
        </c:if>   

        <td class="item" scope="row" abbr="${productDisplayName}">
          <%-- Get the product's template URL --%>
          <dsp:getvalueof var="pageurl" vartype="java.lang.String" param="product.template.url"/>
          <c:choose>
            <%-- If the product has a template generate a link for the product. --%>
            <c:when test="${not empty pageurl}">
              <p class="name">
                <dsp:include page="/global/gadgets/crossSiteLink.jsp">
                  <dsp:param name="product" param="product"/>
                  <dsp:param name="siteId" param="giftlistItem.siteId"/>
                  <dsp:param name="skuDisplayName" param="sku.displayName"/>
                </dsp:include>
              </p>
            </c:when>
            <%-- Otherwise just display the product's  display name. --%>
            <c:otherwise>
              <c:out value="${productDisplayName}"/>
            </c:otherwise>
          </c:choose>

          <%-- SKU attributes, e.g., color, size, etc. --%>
          <dsp:include page="/global/util/displaySkuProperties.jsp">
            <dsp:param name="product" param="product"/>
            <dsp:param name="sku" param="sku"/>
            <dsp:param name="displayAvailabilityMessage" value="true"/>
          </dsp:include>

          <%-- SKU description if not empty --%>
          <dsp:getvalueof var="skuDescription" param="sku.description"/>
          <c:if test="${not empty skuDescription}">                             
            <dsp:valueof param="sku.description"/>
            <br />
          </c:if>
        </td>
    
        <%-- SKU price --%>
        <td class="atg_store_productPrice price">
          <dl>
            <dsp:include page="/global/gadgets/priceLookup.jsp">
              <dsp:param name="product" param="product"/>
              <dsp:param name="sku" param="sku"/>
            </dsp:include>
          </dl>    
        </td>
    
        <%-- Total number of items requested by owner. --%>
        <td class="requstd">
          <dsp:getvalueof var="quantityDesired" vartype="java.lang.Double" param="giftlistItem.quantityDesired"/>
          <fmt:formatNumber value="${quantityDesired}" type="number"/>
        </td>
    
        <%-- Number of items that is still needed. --%>
        <td class="remain">
          <dsp:getvalueof var="quantity" vartype="java.lang.Double" param="giftlistItem.quantityRemaining"/>
          <fmt:formatNumber value="${quantity}" type="number"/>
        </td>
    
        <%--
          'Add to cart' form with quantity input field that allows to specify a number of items that should
          be added to cart and an action button that corresponds to gift item's SKU availability status.
        --%>
        <td class="quantity">
        
          <%-- 
            Check inventory status. The skuAvailabilityLookup.jsp sets the following variables
            that describe inventory status of the SKU:
              availabilityMessage
                Prefix for an availability message.
              availabilityType
                An inventory availability type, can be either 'backorderable, 'preoderable'
                or 'available'.
              addButtonText
                The text that should be displayed on submit button, corresponds to the availability
                status of item.
              addButtonTitle
                The title for submit button, corresponds to the availability status of item.
          --%>
          <dsp:include page="/global/gadgets/skuAvailabilityLookup.jsp">
            <dsp:param name="product" param="product"/>
            <dsp:param name="skuId" param="sku.repositoryId"/>
          </dsp:include>

          <%--
            Check whether 'Email me when back in stock' button should displayed. It is displayed
            when item is currently out of stock.
          --%>
          <c:choose>
            <c:when test="${availabilityType == 'unavailable'}">
              <%--
                Yes, display "Email me when back in stock' button, no quantity input fields is displayed in this
                case.
              --%>
              <div class="atg_store_productAvailability">
                
                <fmt:message var="linkTitle" key="common.button.emailMeInStockTitle"/>
                
                <%-- Build URL for Email me notification popup --%>
                <c:url var="notifyPopupUrl" value="/browse/notifyMeRequestPopup.jsp">
                  <c:param name="skuId"><dsp:valueof param="sku.repositoryId" /></c:param>
                  <c:param name="productId"><dsp:valueof param="product.repositoryId" /></c:param>
                </c:url>
                
                <%-- 'Email me' notification link --%>
                <dsp:a target="popup" href="${notifyPopupUrl}"
                       title="${linkTitle}" iclass="atg_store_basicButton atg_store_emailMe" >
                  <span><fmt:message key="common.button.emailMeInStockText"/></span>
                </dsp:a>
                <div class="atg_store_emailMeMessage">
                  <fmt:message key="common.whenAvailable"/>
                </div>
              </div>
            </c:when>
            <c:otherwise>
              <dsp:form id="addToCart" formid="addToCart"
                        action="${originatingRequest.requestURI}" method="post"
                        name="addToCart">
                
                <%--
                  Set the items siteId so that commerce item in shopping cart will have the same site ID
                  as the current gift item.
                --%>
                <dsp:input bean="CartModifierFormHandler.siteId" paramvalue="giftlistItem.siteId" type="hidden"/>

                <%--
                  Item is not out of stock, display quantity input field and submit button with text that
                  corresponds to SKU availability status: it can be either 'add to cart', 'preorder or 
                  'backorder'. 
                  The 'atg_store_numericInput' class of quantity input field doesn't allow to enter 
                  non-numeric symbols into the field.
                --%>
                
                <fmt:message var="atg_store_qtyInputTitle" key="common.qty"/>
                <%-- Quantity input field --%>
                <dsp:input bean="CartModifierFormHandler.quantity" type="text" value="0" size="2"
                           iclass="qty atg_store_numericInput" id="atg_store_quantity1" title="${atg_store_qtyInputTitle}"/>

                <%-- Hidden params for redirects --%>         
                <dsp:input bean="CartModifierFormHandler.addItemToOrderErrorURL" type="hidden" 
                           paramvalue="errorPath"/>
                <dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL" type="hidden"
                           value="${contextroot}/cart/cart.jsp"/>
            
                <%-- URLs for the RichCart AJAX response. Renders cart contents as JSON --%>
                <dsp:input bean="CartModifierFormHandler.ajaxAddItemToOrderSuccessUrl" type="hidden" 
                           value="${contextroot}/cart/json/cartContents.jsp"/>
                <dsp:input bean="CartModifierFormHandler.ajaxAddItemToOrderErrorUrl" type="hidden" 
                           value="${contextroot}/cart/json/errors.jsp"/>
                       
                <dsp:input bean="CartModifierFormHandler.sessionExpirationURL" type="hidden"
                           value="${contextroot}/global/sessionExpired.jsp"/>
            
                <dsp:input bean="CartModifierFormHandler.productId" paramvalue="product.repositoryId" 
                           type="hidden"/>
                <dsp:input bean="CartModifierFormHandler.catalogRefIds" paramvalue="sku.repositoryId" 
                           type="hidden"/>                   
                <dsp:input bean="CartModifierFormHandler.giftlistItemId" paramvalue="giftlistItem.id" 
                           type="hidden"/>
                <dsp:input bean="CartModifierFormHandler.giftlistId" paramvalue="giftlist.id" 
                           type="hidden"/>
            
                <%--
                  Display action buttons with text and title that are determined by skuAvailabilityLookup.jsp
                  gadget. Also depending on availability type different styles are applyed to submit button.
                --%>
                <c:choose>
                  <c:when test="${availabilityType == 'preorderable'}">
                    <span class="atg_store_basicButton add_to_cart_link_preorder">
                      <dsp:input bean="CartModifierFormHandler.addItemToOrder" type="submit"
                                 value="${addButtonText}" title="${addButtonTitle}" 
                                 iclass="atg_behavior_addItemToCart"/>
                    </span>
                  </c:when>

                  <c:when test="${availabilityType == 'backorderable'}">
                    <span class="atg_store_basicButton add_to_cart_link_backorder">
                      <dsp:input bean="CartModifierFormHandler.addItemToOrder" type="submit"
                                 value="${addButtonText}" title="${addButtonTitle}" 
                                 iclass="atg_behavior_addItemToCart"/>
                    </span>
                  </c:when>
                  <c:otherwise>
                    <span class="atg_store_basicButton add_to_cart_link">
                      <dsp:input bean="CartModifierFormHandler.addItemToOrder" type="submit" 
                                 value="${addButtonText}" title="${addButtonTitle}"
                                 alt="${addButtonTitle}" iclass="atg_behavior_addItemToCart" />                  
                    </span>
                  </c:otherwise>
                </c:choose>
              </dsp:form>
            </c:otherwise>
          </c:choose>
          
        </td>
      </tr>      
    </dsp:oparam>    
  </dsp:droplet> 
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/giftlists/gadgets/giftListShopProductRow.jsp#3 $$Change: 788278 $--%>

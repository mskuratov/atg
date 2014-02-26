<%-- 
  This page renders the correct button style depending on the current inventory
  state for a particular product. It may display an 'Email Me' button when the 
  item is out of stock, it may display a 'Preorder' button when the item is
  preorderable, it may display a 'Backorder' button when the item is backorderable
  and it may display an 'Add To Cart' button when the item is in stock.
  
  Required Parameters:
    product
      The product repository item to display
    
    categoryId
      The repository ID of the product's category
    
    sku
      The sku repository item to add to cart
    
  Optional Parameters: 
    displayAvailability
      Boolean flag indicating whether the availability message should be displayed.
      Defaults to true.     
    siteId
      The products site id
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>  

  <dsp:getvalueof  var="displayAvailability" param="displayAvailability"/>
  
  <%--
    Get Product/SKU availability message and button labels. The following request scoped
    page variables are set:  
    - addButtonText - The name that will be displayed on the "Add To Cart" button 
      ("Add To Cart", "Preorder", etc.)
    - addButtonTitle - The title that will be display with the "Add To Cart" button
      ("Add To Cart", "Preorder", etc.)
    - availabilityMessage - The prefix for an availability message 
      (i.e., "Preorderable until")
    - availabilityType - The oparam name returned from the SkuAvailabilityLookup droplet
  --%>
  <dsp:include page="/global/gadgets/skuAvailabilityLookup.jsp">
    <dsp:param name="product" param="product"/>
    <dsp:param name="skuId" param="sku.repositoryId"/>
    <dsp:param name="showUnavailable" value="true"/>
  </dsp:include>
  
  <span>
    <dsp:getvalueof var="formId" vartype="java.lang.String" param="sku.repositoryId"/>
    
    <%--
      Add to Cart button. Adds the sku referenced in the form handlers catalogRefIds property to
      the cart when the form is submitted. The form id is set to the skus repository id, this
      ensures its unique.
    --%>
    <dsp:form id="${formId}" action="${pageContext.request.requestURI}" method="post">
      <%-- Redirect URLs --%>
      <c:url var="errorUrl" value="${pageContext.request.requestURI}">
        <c:param name="productId">
          <dsp:valueof param="product.repositoryId"/>
        </c:param>
        <c:param name="categoryId">
          <dsp:valueof param="categoryId"/>
        </c:param>
      </c:url>
      <c:url var="successUrl" value="/cart/cart.jsp"/>
      <c:url var="sessionExpiredUrl" value="/global/sessionExpired.jsp"/>
      <c:url var="jsonSuccessUrl" value="/cart/json/cartContents.jsp"/>
      <c:url var="jsonErrorUrl" value="/cart/json/errors.jsp"/>
      
      <dsp:input bean="CartModifierFormHandler.addItemToOrderErrorURL" type="hidden" 
                 value="${errorUrl}"/>        
      <dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL" type="hidden" 
                 value="${successUrl}"/>
      <dsp:input bean="CartModifierFormHandler.sessionExpirationURL" type="hidden" 
                 value="${sessionExpiredUrl}"/>
      <dsp:input bean="CartModifierFormHandler.ajaxAddItemToOrderSuccessUrl" type="hidden" 
                 value="${jsonSuccessUrl}"/>
      <dsp:input bean="CartModifierFormHandler.ajaxAddItemToOrderErrorUrl" type="hidden" 
                 value="${jsonErrorUrl}"/>
      <dsp:input bean="CartModifierFormHandler.productId" paramvalue="product.repositoryId" type="hidden"/>
      <dsp:input bean="CartModifierFormHandler.siteId" paramvalue="siteId" type="hidden"/>
      <dsp:input bean="CartModifierFormHandler.quantity" type="hidden" value="1"/>
      <dsp:input bean="CartModifierFormHandler.catalogRefIds" paramvalue="sku.repositoryId" type="hidden"/>
      
      <%-- Display availability message, if needed --%>
      <c:if test="${(displayAvailability or empty displayAvailability) and !empty availabilityMessage}">
        <div class="atg_store_availability">
          <span>
            <c:out value="${availabilityMessage}"/>
          </span>
        </div>
      </c:if>
      
      <%-- Determine which button to display --%>
      <c:choose>
        <%-- Unavailable? Then display an 'Email Me' link --%>
        <c:when test="${availabilityType == 'unavailable'}">
          <c:url var="notifyMeUrl" value="/browse/notifyMeRequestPopup.jsp">
            <c:param name="skuId">
              <dsp:valueof param="sku.repositoryId"/>
            </c:param>
            <c:param name="productId">
              <dsp:valueof param="product.repositoryId"/>
            </c:param>
          </c:url>
          <a href="${notifyMeUrl}" class="atg_store_basicButton atg_store_emailMe" target="popup"
             title="${addButtonTitle}">
            <span>
              <c:out value="${addButtonText}"/>
            </span>
          </a>
          <div class="atg_store_emailMeMessage">
            <fmt:message key="common.whenAvailable"/>
          </div>
        </c:when>
        <%-- Otherwise display an 'Add to Cart' button --%>
        <c:otherwise>
          <c:choose>
            <c:when test="${availabilityType == 'preorderable'}">
              <c:set var="buttonStyle" value="add_to_cart_link_preorder"/>
            </c:when>
            <c:when test="${availabilityType == 'backorderable'}">
              <c:set var="buttonStyle" value="add_to_cart_link_backorder"/>
            </c:when>
            <c:otherwise>
              <c:set var="buttonStyle" value="add_to_cart_link"/>
            </c:otherwise>
          </c:choose>
          <span class="atg_store_basicButton ${buttonStyle} atg_store_comparison_inventoryStateButton">
            <dsp:input bean="CartModifierFormHandler.addItemToOrder" type="submit" value="${addButtonText}"
                       title="${addButtonTitle}" iclass="atg_behavior_addItemToCart"/>
          </span>
        </c:otherwise>
      </c:choose>

    </dsp:form>
  </span>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/comparisons/gadgets/productAddToCart.jsp#3 $$Change: 788278 $--%>

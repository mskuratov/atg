<%--
  This page renders Add to Cart button for gift list item based on its availability status.
  
  Required parameters:
    giftlistItem
      The gift list item to display Add to Cart button for.
    giftlist
      The gift list to which gift list item belongs.
    product
      The product repository item for gift item
    skuId
      The SKU ID for gift item
      
  Optional parameters:
    None      
--%>
<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>

  <dsp:getvalueof var="contextroot" bean="/OriginatingRequest.contextPath"/>

   <%-- 
     Check inventory status. The skuAvailabilityLookup.jsp sets the following variables
     that describe inventory status of the SKU:
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
    <dsp:param name="skuId" param="skuId"/>
    <dsp:param name="showUnavailable" value="true"/>
  </dsp:include>

  <c:if test="${!empty addButtonText}">
    <c:choose>
      <%-- If item is out of stock display 'Email me' button. --%>
      <c:when test="${availabilityType == 'unavailable'}">
        <%-- Build URL for Email me notification popup --%>
        <c:url var="notifyPopupUrl" value="/browse/notifyMeRequestPopup.jsp">
          <c:param name="skuId"><dsp:valueof param="skuId" /></c:param>
          <c:param name="productId"><dsp:valueof param="product.repositoryId" /></c:param>
        </c:url>
        <a href="${notifyPopupUrl}" title="${addButtonTitle}"
           class="atg_store_basicButton" target="popup">
          <span>${addButtonText}</span>
        </a>
      </c:when>
      <c:otherwise>
        <%-- 
          Item is either in stock or preoderable/backoderable. Display add to cart button with
          corresponding message. 
          --%>
        <dsp:a page="/cart/cart.jsp" title="${addButtonTitle}" 
               iclass="add_to_cart_link atg_behavior_addItemToCart atg_store_basicButton"> 
     
          <%-- Pass values as request parameters --%>
          <dsp:property bean="CartModifierFormHandler.giftlistId" paramvalue="giftlist.repositoryId" name="gl_rId" />
          <dsp:property bean="CartModifierFormHandler.giftlistItemId" paramvalue="giftlistItem.repositoryId" name="gli_rId" />
          <dsp:property bean="CartModifierFormHandler.productId" paramvalue="giftlistItem.productId" name="gli_pId" />
          <dsp:property bean="CartModifierFormHandler.siteId" paramvalue="giftlistItem.siteId"/>
          <dsp:property bean="CartModifierFormHandler.catalogRefIds" paramvalue="giftlistItem.catalogRefId" name="gli_cRId" />
          <%-- URLs for the RichCart AJAX response to render cart contents as JSON --%>
          <dsp:property bean="CartModifierFormHandler.ajaxAddItemToOrderSuccessUrl" value="${contextroot}/cart/json/cartContents.jsp" name="successUrl" />
          <dsp:property bean="CartModifierFormHandler.ajaxAddItemToOrderErrorUrl" value="${contextroot}/cart/json/errors.jsp" name="errorUrl" />
          <%-- Assume a quantity of the remaining value since we are not allowing users to select quantity from here --%>
          <dsp:property bean="CartModifierFormHandler.quantity" paramvalue="giftlistItem.quantityRemaining" name="gli_qR" />
          <%-- Call the form handler addItemToOrder method--%>
          <dsp:property bean="CartModifierFormHandler.addItemToOrder" value="" />
          
          <span>  ${addButtonText}</span>
        </dsp:a>
      </c:otherwise>
    </c:choose>

  </c:if>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/giftListAddToCart.jsp#2 $$Change: 788278 $--%>
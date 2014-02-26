<%-- 
  This page determines which message to render when the shopping cart is empty, depending on 
  whether a user is anonymous or not. When there are items in the cart, a number of CartModifierFormHandler 
  success/error and update URLs are populated through hidden inputs as well as displaying the cart's
  items and user operations relevant to those items.
  
  Required Parameters:
    None.
  
  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/userprofiling/PropertyManager"/>
  <dsp:importbean bean="/atg/commerce/promotion/GiftWithPurchaseSelectionsDroplet"/>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  
  <%-- 
    The number of items currently in the shopping cart. This is used to determine
    which shopping cart message to display to the user when the cart is empty.
  --%>
  <dsp:getvalueof var="commerceItemCount" bean="ShoppingCart.current.CommerceItemCount"/>
  
  <c:choose>
    <c:when test='${commerceItemCount == 0}'>
      <dsp:include page="shoppingCartMessage.jsp"/>
    </c:when>
    <c:otherwise>
    
      <%--
        This droplet is used to retrieve all of the gift with purchase selections on an 
        order or a commerce item. If the commerce item is supplied then the selections 
        only apply to that item.
        
        In this case, check if the shopping cart contains unselected gifts. If yes, redirect to the 
        intermediate page where we offer to select gifts.
        
        Input Parameters:
         order
           The Order to be inspected. This is a required parameter.
         onlyOutputAvailableSelections
           Boolean flag. For order inspections, if true then only selections where 
           quantityAvaiableForSelection is > 0 will be output. Defaults to false.

        Open Parameters:
          output
            If there are selections returned, this open parameter will be rendered.
          empty
            This will be rendered if no selections exist in the current order
          error
            This will be rendered if errors occur during processing.
   
        Output Parameters:
          selections
            A collection of GiftWithPurchaseSelection beans.
          errorMsg
            If there is an error in the processing, this parameter will hold the error message string.
       --%>
      <dsp:droplet name="GiftWithPurchaseSelectionsDroplet">
        <dsp:param name="order" bean="ShoppingCart.current"/>
        <dsp:param name="onlyOutputAvailableSelections" value="true" />
    
        <dsp:oparam name="output">
          <dsp:input type="hidden" bean="CartModifierFormHandler.moveToPurchaseInfoSuccessURL" 
                     value="../cart/giftNotSelected.jsp"/>
        </dsp:oparam>
          
        <dsp:oparam name="empty">
          <dsp:input type="hidden" bean="CartModifierFormHandler.moveToPurchaseInfoSuccessURL" 
                     value="../checkout/shipping.jsp"/>
        </dsp:oparam>
      </dsp:droplet>
            
      <dsp:input type="hidden" bean="CartModifierFormHandler.moveToPurchaseInfoErrorURL"
                 value="${originatingRequest.requestURI}"/>
      <dsp:input type="hidden" bean="CartModifierFormHandler.updateSuccessURL"
                 value="${originatingRequest.requestURI}"/>
      <dsp:input type="hidden" bean="CartModifierFormHandler.updateErrorURL"
                 value="${originatingRequest.requestURI}"/>
      <dsp:input type="hidden" bean="CartModifierFormHandler.expressCheckoutErrorURL"
                 value="${originatingRequest.requestURI}"/>
      <dsp:input type="hidden" bean="CartModifierFormHandler.removeItemFromOrderSuccessURL" 
                 value="${originatingRequest.requestURI}"/>
      <dsp:input type="hidden" bean="CartModifierFormHandler.removeItemFromOrderErrorURL" 
                 value="${originatingRequest.requestURI}"/>

      <%-- Possible Success/Error URLs for Checkout/ExpressCheckout with/without GiftMessage --%>
      <dsp:input bean="CartModifierFormHandler.giftMessageUrl" type="hidden"
                 value="${originatingRequest.contextPath}/checkout/giftMessage.jsp"/>
                 
      <%--
       Check if the shopping cart contains unselected gifts. If yes, redirect to the intermediate page 
       where we offer to select gifts.
       
       Pass 'expressCheckout' parameter for Express Checkout handling and 'loginDuringCheckout' if user 
       is not logged in and need to be authorized before checkout.
       
       Input Parameters:
         order
           The Order to be inspected.  This is a required parameter.
         onlyOutputAvailableSelections
           Boolean flag. For order inspections, if <code>true</code> then only selections where 
           quantityAvaiableForSelection is > 0 will be output. Defaults to false.

        Open Parameters:
          output
            If there are selections returned, this open parameter will be rendered.
          empty
            This will be rendered if no selections exist in the current order
          error
            This will be rendered if errors occur during processing.
   
        Output Parameters:
          selections
            A collection of GiftWithPurchaseSelection beans.
          errorMsg 
            If there is an error in the processing, this parameter will hold the error message string.
      --%>
      <dsp:droplet name="GiftWithPurchaseSelectionsDroplet">
        <dsp:param name="order" bean="ShoppingCart.current"/>
        <dsp:param name="onlyOutputAvailableSelections" value="true" />
      
        <dsp:oparam name="output">
          <dsp:input type="hidden" bean="CartModifierFormHandler.shippingInfoURL" 
                     value="${originatingRequest.contextPath}/cart/giftNotSelected.jsp"/>
          <dsp:input bean="CartModifierFormHandler.confirmationURL" type="hidden"
                     value="${originatingRequest.contextPath}/cart/giftNotSelected.jsp?expressCheckout=true&express=true"/>
          <dsp:input bean="CartModifierFormHandler.loginDuringCheckoutURL" type="hidden"
                     value="${originatingRequest.contextPath}/cart/giftNotSelected.jsp?loginDuringCheckout=true"/>
        </dsp:oparam>
        
        <dsp:oparam name="empty">           
          <dsp:input bean="CartModifierFormHandler.shippingInfoURL" type="hidden"
                     value="${originatingRequest.contextPath}/checkout/shipping.jsp"/>
          <dsp:input bean="CartModifierFormHandler.confirmationURL" type="hidden"
                     value="${originatingRequest.contextPath}/checkout/confirm.jsp?expressCheckout=true&express=true"/>
          <dsp:input bean="CartModifierFormHandler.loginDuringCheckoutURL" type="hidden"
                     value="${originatingRequest.contextPath}/checkout/login.jsp"/>
                            
        </dsp:oparam>
      </dsp:droplet>             
                     
      <%-- URLs for the RichCart AJAX response. Renders cart contents as JSON --%>
      <dsp:input bean="CartModifierFormHandler.ajaxAddItemToOrderSuccessUrl" type="hidden"
                 value="${originatingRequest.contextPath}/cart/json/cartContents.jsp"/>
      <dsp:input bean="CartModifierFormHandler.ajaxAddItemToOrderErrorUrl" type="hidden"
                 value="${originatingRequest.contextPath}/cart/json/errors.jsp"/>
      <%-- 
        This gadget is used to display cartItems information and relative operations such as delete etc. 
      --%>
      <dsp:include page="cartItems.jsp"/>
     
      <div class="order_details">
        <dsp:include page="giftWrap.jsp"/>
      </div>
    </c:otherwise>
  </c:choose>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/gadgets/cartContents.jsp#2 $$Change: 788278 $--%>
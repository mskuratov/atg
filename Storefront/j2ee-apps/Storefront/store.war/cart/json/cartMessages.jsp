<%-- 
  This page renders JSON data when there are messages in the cart.
  
  Required Parameters:
    None.

  Optional parameters:
    None.
--%>
<dsp:page>
  
  <dsp:importbean bean="/atg/targeting/TargetingArray"/>
  <dsp:importbean bean="/atg/store/droplet/CartMessages"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  <dsp:importbean bean="/atg/store/profile/RequestBean"/>
  <dsp:importbean bean="/atg/store/collections/filter/PromotionMessagesFilter"/>
  <dsp:importbean bean="/atg/commerce/pricing/NoCleanBeforePricingSlot"/>
  
  <%-- 
    Determine if the item has been added just now.
    If yes, we'll display GWP message.
   --%>
  <dsp:getvalueof bean="/atg/commerce/order/purchase/CartModifierFormHandler.items" var="itemsJustAdded"/>
  <c:set var="itemModified" value="${false}"/>

  <%-- Items have been added to the cart on this request. Check to see if this is it. --%>  
  <c:forEach items="${itemsJustAdded}" var="itemToCheck">
    <dsp:setvalue param="formHandlerItem" value="${itemToCheck}" /> 
    <dsp:getvalueof var="cartItems" bean="ShoppingCart.current.commerceItems"/>
    
    <c:forEach items="${cartItems}" var="currentItem">
      <dsp:param name="currentItem" value="${currentItem}"/>
      
      <%-- 
        Check current item with the just added one
      --%>
      <dsp:droplet name="Compare">
        <dsp:param name="obj1" param="formHandlerItem.catalogRefId" />
        <dsp:param name="obj2" param="currentItem.catalogRefId" />
        <dsp:oparam name="equal">
          <%-- 
            Item in the cart matches one in the formhandler. 
            Check that the quantity just added is non zero 
          --%>
          <dsp:getvalueof param="formHandlerItem.quantity" var="qtyAdded"/>
          <c:if test="${qtyAdded > 0}">
            <%-- Item has just been added to the cart, so set the flag --%>
            <c:set var="itemModified" value="${true}"/>
          </c:if>
        </dsp:oparam>  
      </dsp:droplet>
    </c:forEach>   
  </c:forEach>
  
  <json:object name="messagesContainer">
    <json:array name="messages">

      <%-- 
        Display GWP messages only if item has been added via JavaScript,
        check isAjaxRequest flag for it. We do this because JSON data 
        created every time independently of JS is on or off. That's a problem
        because we invoke Targeting droplet on the rich cart and shopping cart
        and can lose GWP messages (Slot destruct messages by default) when JS is
        turned off and shopping cart is opened.
        --%>
      <dsp:getvalueof var="isAjaxRequest" bean="RequestBean.values.isAjaxRequest"/>
      
      <c:if test="${itemModified and isAjaxRequest}">
      
        <%--
          Retrieve all GWP messages first.
          
          Input Parameters
            targeter
              Slot with GWP messages.
            filter
              PromotionMessagesFilter will be used to filter duplicates
              and remove 'success' messages if we have 'failure' messages
              associated with the same promotion.     
              
          Output Parameters
            element
              Message to display.
          --%>
        <dsp:droplet name="TargetingArray">
          <dsp:param name="targeter" bean="NoCleanBeforePricingSlot"/>
          <dsp:param name="filter" bean="PromotionMessagesFilter"/>
     
          <dsp:oparam name="output">
          
            <dsp:getvalueof var="elements" param="elements"/>
            <c:forEach var="element" items="${elements}">
              <dsp:setvalue param="element" value="${element}"/>
          
              <%-- Check for message summary and skip messages with empty one --%>
              <dsp:getvalueof var="messageSummary" param="element.summary"/>
              
              <c:if test="${not empty messageSummary}">
                <dsp:include page="cartMessage.jsp">
                  <dsp:param name="currentMessage" param="element" />
                </dsp:include>   
              </c:if>
            </c:forEach>
            
          </dsp:oparam>
        </dsp:droplet>
      </c:if>
            
        <%--
          Now retrieve non-GWP promotion message
          
          Output Parameters
            message
              Message to display    
         --%>
        <dsp:droplet name="CartMessages">
     
          <dsp:oparam name="output">
            <dsp:include page="cartMessage.jsp">
              <dsp:param name="currentMessage" param="message" />
            </dsp:include>
          </dsp:oparam>
        </dsp:droplet>
    </json:array>
  </json:object>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/json/cartMessages.jsp#2 $$Change: 788278 $--%>
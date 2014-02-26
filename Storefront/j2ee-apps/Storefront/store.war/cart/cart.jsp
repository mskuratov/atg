<%-- 
  Top level cart page that displays a summary of the products added to a user's cart. 
  It provides functions such as updating quantity, pricing adjustments and displaying 
  related products along with a number of options that allow the user to proceed with 
  their order or continue shopping.
  
  Required Parameters:
    None.
    
  Optional parameters:
    None.
--%>
<dsp:page>
  <%-- 
    Set a request scoped var to indicate that the rich cart shouldn't do any form
    hijacking. Any 'Add To Cart' links/buttons clicked on the cart page need the entire
    page to refresh to reflect the changes.
  --%>
  <c:set var="noRichCartFormHijacking" value="${true}" scope="request"/>

  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
  
  <%--
    This droplet executes 'repriceAndUpdateOrder' chain. It uses the current cart as input 
    order parameter for the chain in question.
  
    Output parameters:
      pipelineResult
        Result returned from the pipeline, if execution is successful.
      exception
        Exception thrown by the pipeline, if any.

    Open parameters:
      failure
        Rendered, if the pipeline has thrown an exception.
      successWithErrors
        Rendered, if pipeline successfully finished, but returned error.
      success
        Rendered, if pipeline successfully finished without errors returned.

    Input parameters:
      pricingOp
        Pricing operation to be performed on the current order.
  --%>
  <dsp:droplet name="RepriceOrderDroplet">
    <dsp:param name="pricingOp" value="ORDER_SUBTOTAL"/>
  </dsp:droplet> 
    
  <%-- 
    The number of items currently in the shopping cart. This will determine whether
    to include an order summary or display a message to the user informing them that
    their cart is empty.
  --%>    
  <dsp:getvalueof var="commerceItemCount" bean="ShoppingCart.current.CommerceItemCount"/>  
  
  <%-- Determine CSS class for shopping cart page container --%>
  <c:set var="cartPageClass" value="atg_store_pageCart atg_store_rightCol" />
  
  <c:if test='${commerceItemCount == 0}'>
    <c:set var="cartPageClass" value="atg_store_pageEmptyCart"/>
  </c:if>
  
  <crs:pageContainer divId="" titleKey="" index="false" follow="false">
    
    <jsp:attribute name="bodyClass">
      <c:out value="${cartPageClass}"/>
    </jsp:attribute>   
    
    <jsp:attribute name="formErrorsRenderer">
      <%-- Display error messages if any above the accessibility navigation --%>
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="CartModifierFormHandler"/>
      </dsp:include>   
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="CartModifierFormHandler.storeExpressCheckoutFormHandler"/>
      </dsp:include>
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="CouponFormHandler"/>
      </dsp:include>
    </jsp:attribute>       
    
    <jsp:body>
         
      <dsp:form action="${pageContext.request.requestURI}" method="post" name="cartform" formid="cartform">
        
        <div id="atg_store_contentHeader">
          <h2 class="title">
            <fmt:message  key="common.cart.shoppingCart"/>
          </h2>
          <dsp:include page="/global/gadgets/closenessQualifiers.jsp"/>
        </div>
        
        <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
          <dsp:param name="formHandler" bean="CartModifierFormHandler"/>
        </dsp:include>   
        <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
          <dsp:param name="formHandler" bean="CartModifierFormHandler.storeExpressCheckoutFormHandler"/>
        </dsp:include>
        <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
          <dsp:param name="formHandler" bean="CouponFormHandler"/>
        </dsp:include>
        
        <%-- Display messages even if there is no items in the cart --%>
        <dsp:include page="/cart/gadgets/cartMessages.jsp"/>            
         
        <div id="atg_store_shoppingCart" class="atg_store_main"> 
          <dsp:include page="gadgets/cartContents.jsp" />
          <dsp:include page="gadgets/orderRelatedProducts.jsp"/>
        </div>
        
        <%-- Order summary and action buttons --%> 
        <c:if test='${commerceItemCount != 0}'>
          <dsp:include page="/checkout/gadgets/checkoutOrderSummary.jsp">
            <dsp:param name="skipCouponFormDeclaration" value="true"/>
            <dsp:param name="order" bean="ShoppingCart.current"/>
            <dsp:param name="isShoppingCart" value="true"/>
          </dsp:include>
        </c:if>
        
      </dsp:form>
    </jsp:body>
  </crs:pageContainer>
  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/cart.jsp#2 $$Change: 788278 $--%>

<%-- 
  This page displays all necessary elements to make proper billing for the cart the user is 
  checking out. First, this page applies all available store credits to the current order. If 
  there are no enough store credits to pay for the whole order, this page would include gadgets 
  to render a form with necessary input fields to select or create a credit card. Or it will just 
  display 'Move to Confirm' button otherwise.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/ShoppingCart" />
  <dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler" />
  <dsp:importbean bean="/atg/store/order/purchase/BillingFormHandler" />
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  
  <c:set var="stage" value="billing"/>
  
  <%-- Set title for submit button --%>
  <fmt:message var="submitFieldText" key="common.button.continueText"/>

  <crs:pageContainer divId="atg_store_cart" index="false" follow="false" 
                     levelNeeded="BILLING" redirectURL="../cart/cart.jsp"
                     bodyClass="atg_store_pageBilling atg_store_checkout atg_store_rightCol">
                     
    <jsp:attribute name="formErrorsRenderer">
      <%-- 
        Display error messages from Billing and Coupon form handlers 
        above the accessibility navigation  
      --%>     
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="BillingFormHandler"/>
        <dsp:param name="submitFieldText" value="${submitFieldText}"/>
      </dsp:include>
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="CouponFormHandler"/>
        <dsp:param name="submitFieldText" value="${submitFieldText}"/>
      </dsp:include>  
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="ProfileFormHandler"/>
      </dsp:include> 
    </jsp:attribute>
          
    <jsp:body>
   
      <%-- 
        If this parameter is 'true', a promotion has expired in the users cart before they have  
        placed the order. The user has then tried to place the order on the confirmation page, 
        where the promotion pipeline validation has subsequently invalidated the order. This means 
        that the order must be re-priced to refresh the cart.
      --%>
      <dsp:getvalueof var="expiredPromotion" vartype="java.lang.String" param="expiredPromotion"/> 

      <c:if test='${expiredPromotion == "true"}'>
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
          <dsp:param name="pricingOp" value="ORDER_TOTAL"/>
        </dsp:droplet>
      </c:if> 
      
      <%-- Apply available store credits to the order  --%>
      <dsp:setvalue bean="BillingFormHandler.applyStoreCreditsToOrder" value=""/>

      <%-- Reprice the order, we should now its exact cost to render the checkout summary area. --%>
      <dsp:getvalueof var="formError" vartype="java.lang.String" bean="BillingFormHandler.formError"/>
      
      <%-- Check, if there was an error in the component specified. --%>
      <c:if test='${formError == "true"}'>
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
          <dsp:param name="pricingOp" value="ORDER_TOTAL"/>
        </dsp:droplet>
      </c:if>

      <dsp:form id="atg_store_checkoutBilling" formid="atg_store_checkoutBilling"
                action="${pageContext.request.requestURI}" method="post">
        
        <dsp:param name="skipCouponFormDeclaration" value="true"/>
        <fmt:message key="checkout_title.checkout" var="title"/>
        
        <crs:checkoutContainer currentStage="${stage}" title="${title}">
          
          <jsp:attribute name="formErrorsRenderer">
            
            <%-- Display error messages from Billing and Coupon form handlers. --%>            
            <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
              <dsp:param name="formHandler" bean="BillingFormHandler"/>
              <dsp:param name="submitFieldText" value="${submitFieldText}"/>
            </dsp:include>
            <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
              <dsp:param name="formHandler" bean="CouponFormHandler"/>
              <dsp:param name="submitFieldText" value="${submitFieldText}"/>
            </dsp:include>   
            <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
              <dsp:param name="formHandler" bean="ProfileFormHandler"/>
            </dsp:include>        
            <dsp:include page="gadgets/checkoutErrorMessages.jsp">
              <dsp:param name="formHandler" bean="CommitOrderFormHandler"/>
            </dsp:include>
          </jsp:attribute>
          <jsp:body>
            <div id="atg_store_checkout" class="atg_store_main">
              
              <dsp:getvalueof var="order" vartype="atg.commerce.Order" bean="ShoppingCart.current"/>
              
              <%-- Don't offer enter credit card's info if order's total is covered by store credits. --%>
              <c:choose>
                <c:when test="${order.priceInfo.total > order.storeCreditsAppliedTotal}">
                  <%-- Store credits are not enough, render billing form. --%>
                  <dsp:include page="gadgets/billingForm.jsp"/>
                </c:when>
                <c:otherwise>
                  
                  <%-- Order is paid by store credits, display its total and 'Continue' button. --%>
                  <crs:messageContainer id="atg_store_zeroBalance">                     
                    
                    <h3>
                      <%-- Display 'Order's total is 0' message. --%>
                      <fmt:message key="checkout_billing.yourOrderTotal"/>
                      <dsp:include page="/global/gadgets/formattedPrice.jsp">
                        <dsp:param name="price" value="0"/>
                      </dsp:include>
                    </h3>
                    
                    <p><fmt:message key="checkout_billing.yourOrderTotalMessage"/></p>
                    <dsp:input bean="BillingFormHandler.moveToConfirmSuccessURL" type="hidden" value="confirm.jsp"/>
                    
                    <%-- Go to Confirm button. --%>
                    <span class="atg_store_basicButton">
                      <fmt:message var="caption" key="common.button.continueText"/>
                      <dsp:input bean="BillingFormHandler.billingWithStoreCredit" type="submit" value="${caption}"/>
                    </span>                    
                  </crs:messageContainer>     
                </c:otherwise>
              </c:choose>
            </div>
          </jsp:body>
        </crs:checkoutContainer>
        
        <%-- Order Summary --%>
        <dsp:include page="/checkout/gadgets/checkoutOrderSummary.jsp">
          <dsp:param name="order" bean="ShoppingCart.current"/>
          <dsp:param name="currentStage" value="${stage}"/>
        </dsp:include>
        
      </dsp:form>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/billing.jsp#3 $$Change: 796495 $--%>

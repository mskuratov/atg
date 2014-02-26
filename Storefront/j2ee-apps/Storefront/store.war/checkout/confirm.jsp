<%-- 
  This is the last checkout step page. It displays all cart contents, shipping and billing information.
  It also displays buttons to confirm or discard current order.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
  
  <c:set var="stage" value="confirm"/>

  <crs:pageContainer divId="atg_store_cart" 
                     index="false" 
                     follow="false"
                     levelNeeded="CONFIRM"
                     redirectURL="../cart/cart.jsp"
                     bodyClass="atg_store_orderConfirmation atg_store_checkout atg_store_rightCol">
    
    <jsp:attribute name="formErrorsRenderer">
      <%-- Display all page-related errors above accessibility navigation. --%>
      <dsp:include page="gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="CommitOrderFormHandler"/>
      </dsp:include>
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="CouponFormHandler"/>
      </dsp:include>
    </jsp:attribute>
        
    <jsp:body>
      
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

      <fmt:message key="checkout_title.checkout" var="title"/>

      <%-- Start a form spanning through the whole page to link all page inputs. --%>
      <dsp:form formid="confirmgadgetsform" action="${originatingRequest.requestURI}"  method="post">
        
        <%-- 
          This hidden input sets the URL that will be used to redirect to when an expired promotion 
          form exception is detected.
        --%>
        <dsp:input type="hidden" bean="CommitOrderFormHandler.expiredPromotionErrorURL" 
                   value="billing.jsp?preFillValues=true&expiredPromotion=true"/>
                          
        <%-- Do not display 'Enter Coupon Code' input in a separate form. --%>
        <dsp:param name="skipCouponFormDeclaration" value="true"/>
        
        <crs:checkoutContainer currentStage="${stage}" title="${title}">
          
          <jsp:attribute name="formErrorsRenderer">  
            <%-- Display all page-related errors. --%>
            <dsp:include page="gadgets/checkoutErrorMessages.jsp">
              <dsp:param name="formHandler" bean="CommitOrderFormHandler"/>
            </dsp:include>
            <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
              <dsp:param name="formHandler" bean="CouponFormHandler"/>
            </dsp:include>
          </jsp:attribute>
          
          <jsp:body>
            <div id="atg_store_checkout" class="atg_store_main">
              
              <%--
                CartSharingSitesDroplet returns a collection of sites that share the shopping
                cart shareable (atg.ShoppingCart) with the current site. You may optionally 
                exclude the current site from the result.

                Input Parameters:
                  excludeInputSite
                    Should the returned sites include the current.
   
                Open Parameters:
                  output
                    This parameter is rendered once, if a collection of sites is found.
   
                Output Parameters:
                  sites - The list of sharing sites.
              --%>
              <dsp:droplet name="/atg/dynamo/droplet/multisite/CartSharingSitesDroplet">
                <dsp:param name="excludeInputSite" value="true"/>
                <dsp:oparam name="output">
                  <%-- There are more than one sites in current group, display site indicator on the page. --%>
                  <c:set var="hideSiteIndicator" value="false"/>
                </dsp:oparam>
                <dsp:oparam name="empty">
                  <%-- There is only one site in current group, do not display site indicator on the page. --%>
                  <c:set var="hideSiteIndicator" value="true"/>
                </dsp:oparam>
              </dsp:droplet>
              
              <%-- Display cart contents, shipping and billing information. --%>
              <dsp:include page="/global/gadgets/orderSummary.jsp">
                <dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
                <dsp:param name="isCurrent" value="true"/>
                <dsp:param name="hideSiteIndicator" value="${hideSiteIndicator}"/>
              </dsp:include>
             
              <%-- Display 'Modify Cart' link. --%>
              <div class="atg_store_confirmFooterLink">
                <dsp:a page="/cart/cart.jsp" iclass="atg_store_modifyCart" title="">
                  <fmt:message key="global_orderShippingItems.modifyCart"/>
                </dsp:a>
              </div>
              
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
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/confirm.jsp#4 $$Change: 796495 $--%>

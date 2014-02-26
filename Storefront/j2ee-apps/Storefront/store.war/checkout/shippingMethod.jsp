<%--
  This page allows the user to select shipping method for single shipping 
  (i.e. all products are shipped to the same address).

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
    
  <c:set var="stage" value="shipping"/>

  <crs:pageContainer divId="atg_store_cart"
                     index="false" 
                     follow="false"
                     levelNeeded="SHIPPING"
                     redirectURL="../cart/cart.jsp">
                     
    <jsp:attribute name="bodyClass">atg_store_pageShipping atg_store_checkout atg_store_rightCol</jsp:attribute>
    
    <jsp:attribute name="formErrorsRenderer">
      <%-- 
        Display error messages from Shipping and Coupon form handlers above
        accessibility navigation. 
      --%>
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="ShippingGroupFormHandler"/>
      </dsp:include>
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="CouponFormHandler"/>
      </dsp:include>
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="ProfileFormHandler"/>
      </dsp:include>
    </jsp:attribute>
          
    <jsp:body>
      
      <dsp:form id="atg_store_checkoutShippingMethod" 
                iclass="atg_store_checkoutOption" 
                formid="atg_store_checkoutShippingAddress"
                action="${pageContext.request.requestURI}" method="post">
                
        <dsp:param name="skipCouponFormDeclaration" value="true"/>
        <fmt:message key="checkout_title.checkout" var="title"/>
        
        <crs:checkoutContainer currentStage="${stage}" title="${title}">
          
          <jsp:attribute name="formErrorsRenderer">
            <%-- Display error messages from Shipping and Coupon form handlers. --%>
            <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
              <dsp:param name="formHandler" bean="ShippingGroupFormHandler"/>
            </dsp:include>
            <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
              <dsp:param name="formHandler" bean="CouponFormHandler"/>
            </dsp:include>
          </jsp:attribute>
          
          <jsp:body> 
            <div id="atg_store_checkout" class="atg_store_main">
              
              <%-- Check if there are any not gift shipping groups --%>
              <dsp:getvalueof var="anyHardgoodShippingGroups" vartype="java.lang.String"
                              bean="ShippingGroupFormHandler.anyHardgoodShippingGroups"/>
                              
              <c:if test='${anyHardgoodShippingGroups == "true"}'>

                <%-- Include available shipping methods --%> 
                <dsp:include page="gadgets/shippingOptions.jsp"/>
                
                <%-- 
                  If user is anonymous and the session has expired, the cart looses its contents,
                  so the page gets redirected to the home page else it will be redirected to the
                  checkout login page.
                  
                  Open parameters:
                    loggedIn
                      User is already logged in with login/password.
                    default
                      User is anonymous or logged in from cookie.
                --%>
                <dsp:droplet name="ProfileSecurityStatus">
                  <dsp:oparam name="anonymous">
                    <%-- User is anonymous --%>
                    <dsp:input type="hidden" 
                               bean="ShippingGroupFormHandler.sessionExpirationURL" 
                               value="${originatingRequest.contextPath}/index.jsp"/>
                  </dsp:oparam>
                  <dsp:oparam name="default">
                    <dsp:input type="hidden" 
                               bean="ShippingGroupFormHandler.sessionExpirationURL" 
                               value="${originatingRequest.contextPath}/checkout/login.jsp"/>
                  </dsp:oparam>
                </dsp:droplet>
                
                <dsp:input type="hidden" bean="ShippingGroupFormHandler.updateShippingMethodSuccessURL" 
                           value="billing.jsp"/>
                <dsp:input type="hidden" bean="ShippingGroupFormHandler.updateShippingMethodErrorURL" 
                           value="shippingMethod.jsp"/>

                <%-- Submit button --%>
                <fmt:message var="continueButtonText" key="common.button.continueText"/>
                
                <div class="atg_store_formActions">
                  <span class="atg_store_basicButton">
                    <dsp:input type="submit"  bean="ShippingGroupFormHandler.updateShippingMethod" 
                               value="${continueButtonText}"/>
                  </span>
                </div>
                
              </c:if>
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
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/shippingMethod.jsp#2 $$Change: 788278 $--%>

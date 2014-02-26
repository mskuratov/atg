<%--
  This page renders a form that allows the user to edit saved billing address.

  Required parameters:
    nickName
      Nickname of saved address to be edited.

  Optional parameters:
    successURL
      The user will be redirected there after the address is successfully changed.
--%>

<dsp:page>

  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>
  <dsp:importbean bean="/atg/store/order/purchase/BillingFormHandler"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  
  <c:set var="stage" value="billing"/>
  
  <crs:pageContainer divId="atg_store_cart" 
                     index="false" 
                     follow="false"
                     bodyClass="atg_store_checkout atg_store_editBillingAddress atg_store_rightCol">
    
    <jsp:attribute name="formErrorsRenderer">
      <%-- Display all page-related errors above accessibility navigation. --%>
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
       <dsp:param name="formhandler" bean="BillingFormHandler"/>
      </dsp:include>

      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="CouponFormHandler"/>
      </dsp:include>
    </jsp:attribute>
    
    <jsp:body>
      <fmt:message key="checkout_title.checkout" var="title"/>
      <crs:checkoutContainer currentStage="${stage}" title="${title}">
                             
        <jsp:attribute name="formErrorsRenderer">
          <%-- Display all page-related errors. --%>
          <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
            <dsp:param name="formhandler" bean="BillingFormHandler"/>
          </dsp:include>

          <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
            <dsp:param name="formHandler" bean="CouponFormHandler"/>
          </dsp:include>
        </jsp:attribute>
                       
        <jsp:body>
          <%-- Use existing gadget to edit saved address. --%>
          <div id="atg_store_checkout" class="atg_store_main">
            <dsp:include page="gadgets/shippingAddressEdit.jsp"/>
          </div>
        </jsp:body>
        
      </crs:checkoutContainer>
      
      <%-- Order Summary --%>
      <dsp:include page="/checkout/gadgets/checkoutOrderSummary.jsp">
        <dsp:param name="order" bean="ShoppingCart.current"/>
        <dsp:param name="currentStage" value="${stage}"/>
      </dsp:include>
      
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/billingAddressEdit.jsp#1 $$Change: 735822 $--%>

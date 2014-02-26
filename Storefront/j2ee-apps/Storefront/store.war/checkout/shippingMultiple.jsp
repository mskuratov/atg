<%--
  This page includes gadgets for the shipping page for multiple shipping groups.
  (That is, items may be shipped to different shipping addresses).

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  
  <c:set var="stage" value="shipping"/>
  
  <fmt:message  var="submitText" key="checkout_shippingAddresses.button.shipToThisAddress"/>
  
  <crs:pageContainer divId="atg_store_cart" index="false" follow="false" levelNeeded="SHIPPING"
                     redirectURL="../cart/cart.jsp"
                     bodyClass="atg_store_shippingMultiple atg_store_checkout atg_store_rightCol">
                     
    <jsp:attribute name="formErrorsRenderer">
      <%-- Display all related errors above accessibility navigation. --%>
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="ShippingGroupFormHandler"/>
        <dsp:param name="submitFieldText" value="${submitText}"/>
      </dsp:include>
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="CouponFormHandler"/>
        <dsp:param name="submitFieldText" value="${submitText}"/>
      </dsp:include>
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="ProfileFormHandler"/>
      </dsp:include>
    </jsp:attribute>
          
    <jsp:body>
    
      <%-- Multiple shipping address form. --%>
      <dsp:form id="atg_store_checkoutMultiShippingAddress" formid="atg_store_checkoutMultiShippingAddress"
                action="${pageContext.request.requestURI}" method="post">
                
        <dsp:param name="skipCouponFormDeclaration" value="true"/>
        <fmt:message key="checkout_title.checkout" var="title"/>
        
        <crs:checkoutContainer currentStage="${stage}" title="${title}">
        
          <jsp:attribute name="formErrorsRenderer">
            <%-- Display all related errors. We're using Shipping and Coupon form handlers here. --%>
            <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
              <dsp:param name="formHandler" bean="ShippingGroupFormHandler"/>
              <dsp:param name="submitFieldText" value="${submitText}"/>
            </dsp:include>
            <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
              <dsp:param name="formHandler" bean="CouponFormHandler"/>
              <dsp:param name="submitFieldText" value="${submitText}"/>
            </dsp:include>
            <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
              <dsp:param name="formHandler" bean="ProfileFormHandler"/>
            </dsp:include>
          </jsp:attribute>

          <jsp:body>
            <%-- Include the form content. --%>
            <dsp:include page="gadgets/shippingMultipleForm.jsp"/>
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
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/shippingMultiple.jsp#2 $$Change: 788278 $--%>

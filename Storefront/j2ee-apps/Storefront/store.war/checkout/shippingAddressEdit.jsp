<%--
  This page displays all necessary input fields to enable the user to edit already created shipping address.

  Required parameters:
    successURL
      The user will be redirected to this URL after he successfully changed address.
    nickName
      Nickname of address to be edited.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>
  
  <fmt:message  var="submitFieldText" key="common.button.saveAddressText"/>
  
  <c:set var="stage" value="shipping"/>
  
  <crs:pageContainer divId="atg_store_cart"
                     index="false" 
                     follow="false"
                     bodyClass="atg_store_shippingAddressAddEdit atg_store_checkout atg_store_rightCol">
    
    <jsp:attribute name="formErrorsRenderer">
      <%-- Display page errors above accessibility navigation. --%>           
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="ShippingGroupFormHandler"/>
        <dsp:param name="submitFieldText" value="${submitFieldText}"/>
      </dsp:include> 
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="CouponFormHandler"/>
        <dsp:param name="submitFieldText" value="${submitFieldText}"/>
      </dsp:include>
    </jsp:attribute>
        
    
    <jsp:body>
      
      <fmt:message key="checkout_title.checkout" var="title"/>
      
      <crs:checkoutContainer currentStage="${stage}" title="${title}">
        
        <jsp:attribute name="formErrorsRenderer">
          <%-- Display page errors. We're using Coupon and Shipping form handlers on the page. --%>
          <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
            <dsp:param name="formHandler" bean="ShippingGroupFormHandler"/>
            <dsp:param name="submitFieldText" value="${submitFieldText}"/>
          </dsp:include> 
          <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
            <dsp:param name="formHandler" bean="CouponFormHandler"/>
            <dsp:param name="submitFieldText" value="${submitFieldText}"/>
          </dsp:include>
        </jsp:attribute>
        
        <jsp:body>
          <%-- Include main part of the page. --%>
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
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/shippingAddressEdit.jsp#2 $$Change: 788278 $--%>

<%--
  This page displays all necessary input fields to create a new shipping address.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  
  <c:set var="stage" value="shipping"/>
  
  <fmt:message  var="submitText" key="common.button.saveText"/>
  
  <crs:pageContainer divId="atg_store_cart" titleKey="checkout_shippingAddAddress.title"  follow="false"
                     bodyClass="atg_store_shippingAddressAddEdit atg_store_checkout atg_store_rightCol">
    
    <jsp:attribute name="formErrorsRenderer">
      <%-- Display page errors above accessibility navigation. --%>      
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="ShippingGroupFormHandler"/>
        <dsp:param name="submitFieldText" value="${submitText}"/>
      </dsp:include>
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="CouponFormHandler"/>
        <dsp:param name="submitFieldText" value="${submitText}"/>
      </dsp:include>
    </jsp:attribute>
        
    <jsp:body>
      
      <fmt:message key="checkout_title.checkout" var="title"/>
      
      <crs:checkoutContainer currentStage="${stage}" title="${title}">
        
        <jsp:attribute name="formErrorsRenderer">
          <%-- Display page errors. We're using Shipping and Coupon form handlers on the page. --%>
          <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
            <dsp:param name="formHandler" bean="ShippingGroupFormHandler"/>
            <dsp:param name="submitFieldText" value="${submitText}"/>
          </dsp:include>
          <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
            <dsp:param name="formHandler" bean="CouponFormHandler"/>
            <dsp:param name="submitFieldText" value="${submitText}"/>
          </dsp:include>
        </jsp:attribute>
        
        <jsp:body>
          
          <%-- Main page content. --%>
          <div id="atg_store_checkout" class="atg_store_main">
            <div class="atg_store_checkoutOption">
              
              <dsp:form action="${pageContext.request.requestURI}" method="post" id="atg_store_checkoutAddAddress"
                        formid="shippingaddressaddform">
                
                <fieldset class="atg_store_checkoutAddAddressFormFields">
                  
                  <%-- Success/error/cancel URLs, this gadget is used for multiple shipping only. --%>
                  <dsp:input type="hidden" bean="ShippingGroupFormHandler.addShippingAddressSuccessURL" 
                             value="shippingMultiple.jsp"/>
                  <dsp:input type="hidden" bean="ShippingGroupFormHandler.addShippingAddressErrorURL"
                             value="shippingAddressAdd.jsp?preFillValues=true"/>
                  <dsp:input type="hidden" bean="ShippingGroupFormHandler.cancelURL" 
                             value="shippingMultiple.jsp"/>
                  <dsp:input type="hidden" bean="ShippingGroupFormHandler.address.email" 
                             beanvalue="Profile.email"/>

                  <%-- Include address-related input fields. --%>
                  <div id="atg_store_shippingInformation">
                    <h3><fmt:message key="myaccount_addressEdit.newAddress"/></h3>
                    <dsp:include page="gadgets/shippingAddressAdd.jsp"/>
                  </div>
                </fieldset>

                <%-- Display Save and Cancel buttons. --%>
                <fieldset>
                
                  <div class="atg_store_formFooter">
                    
                    <fmt:message var="cancelButtonText" key="common.button.cancelText"/>
                    <fmt:message var="saveButtonText" key="common.button.saveText"/>
                    
                    <div class="atg_store_formActions">
                      <div class="atg_store_formActionItem">
                        <span class="atg_store_basicButton">
                          <dsp:input type="submit" bean="ShippingGroupFormHandler.addShippingAddress" 
                                     value="${saveButtonText}"/>
                        </span>
                      </div>
                      
                      <div class="atg_store_formActionItem">
                        <span class="atg_store_basicButton secondary">
                          <dsp:input type="submit" bean="ShippingGroupFormHandler.cancel" 
                                     value="${cancelButtonText}"/>
                        </span>
                      </div>
                    </div>
                  </div>
                  
                </fieldset>
              </dsp:form>
            </div>
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
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/shippingAddressAdd.jsp#2 $$Change: 788278 $--%>

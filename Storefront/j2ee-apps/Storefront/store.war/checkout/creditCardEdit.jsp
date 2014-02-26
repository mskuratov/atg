<%--
  This page renders the form that allows a shopper to edit a saved credit card.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  
  <fmt:message var="submitText" key="common.button.saveChanges"/>
  
  <c:set var="stage" value="billing"/>
  
  <crs:pageContainer divId="atg_store_cart" 
                     index="false" 
                     follow="false"
                     bodyClass="atg_store_checkout atg_store_editCreditCard atg_store_rightCol">
    <jsp:attribute name="formErrorsRenderer">
     <%-- 
       Render page errors above accessibility navigation, 
       this page operates with ProfileFormHandler only. 
     --%>
     <div id="atg_store_formValidationError">
       <dsp:include page="/myaccount/gadgets/myAccountErrorMessage.jsp">
         <dsp:param name="formHandler" bean="ProfileFormHandler"/>
         <dsp:param name="submitFieldText" value="${submitText}"/>
         <dsp:param name="errorMessageClass" value="errorMessage"/>
       </dsp:include>
     </div>
          
     <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
       <dsp:param name="formHandler" bean="CouponFormHandler"/>
     </dsp:include>          
   </jsp:attribute>
        
    <jsp:body>
      <fmt:message key="checkout_title.checkout" var="title"/>
      <crs:checkoutContainer currentStage="${stage}" title="${title}">
        <jsp:attribute name="formErrorsRenderer">
          <%-- Render page errors, this page operates with ProfileFormHandler only. --%>
          <div id="atg_store_formValidationError">
            <dsp:include page="/myaccount/gadgets/myAccountErrorMessage.jsp">
              <dsp:param name="formHandler" bean="ProfileFormHandler"/>
              <dsp:param name="submitFieldText" value="${submitText}"/>
              <dsp:param name="errorMessageClass" value="errorMessage"/>
            </dsp:include>
          </div>
          
          <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
            <dsp:param name="formHandler" bean="CouponFormHandler"/>
          </dsp:include>
          
        </jsp:attribute>
        <jsp:body>
          <div id="atg_store_checkout" class="atg_store_main">
            <div class="atg_store_checkoutOption">
              <fieldset>
                  <%-- Include the form itself, with all proper input fields and buttons. --%>
                  <dsp:include page="/myaccount/gadgets/creditCardEdit.jsp">
                    <dsp:param name="successURL" value="billing.jsp"/>
                    <dsp:param name="cancelURL" value="billing.jsp"/>
                    <dsp:param name="checkout" value="true"/>
                  </dsp:include>
              </fieldset>
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
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/creditCardEdit.jsp#2 $$Change: 788278 $--%>

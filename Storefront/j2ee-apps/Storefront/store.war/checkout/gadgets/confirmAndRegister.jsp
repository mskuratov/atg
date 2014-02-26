<%--
  This gadget displays a 'Thank you' message at the end of the checkout progress.
  It also renders a form that allows the user to register.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/store/profile/RegistrationFormHandler"/>
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath"/>

  <%-- Retrieve the submit button text here in case we need to use it in an error message --%>
  <fmt:message key="common.button.continueText" var="submitText"/>

  <div id="atg_store_confirmAndRegister"> 
    <%-- 
      The submit button text is used in some error messages, so ensure 
      it is set as a param to be utilized in the error jsp. 
    --%>
    <dsp:param name="submitFieldText" value="${submitText}" />
    
    <%-- Show page errors. We're using Billing and Coupon form handlers here. --%>
    <dsp:include page="checkoutErrorMessages.jsp" >
      <dsp:param name="formHandler" bean="RegistrationFormHandler"/>
    </dsp:include>
    <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
      <dsp:param name="formHandler" bean="CouponFormHandler"/>
    </dsp:include>

    <%-- 'Thank you' message area. --%>
    <div id="atg_store_confirmResponse">
      <h3><fmt:message key="checkout_confirmResponse.successTitle"/></h3>
      
      <p>
        <fmt:message key="checkout_confirmResponse.omsOrderId">
          <fmt:param>
            <span><dsp:valueof bean="ShoppingCart.last.omsOrderId"/></span>
          </fmt:param>
        </fmt:message>
      </p>

      <dsp:getvalueof var="confirmationEmail" bean="Profile.email"/>
      
      <c:if test="${not empty confirmationEmail}">
        <p>
          <fmt:message key="checkout_confirmResponse.emailText"/>
          <span><dsp:valueof value="${confirmationEmail}"/></span>
        </p>
      </c:if>

      <%-- Display link to just placed order. --%>
      <p>
        <fmt:message key="checkout_confirmResponse.printOrderText"/>
        <dsp:a page="/myaccount/printOrder.jsp">
          <dsp:param name="orderId" bean="ShoppingCart.last.id"/>
          <fmt:message key="common.here"/>
        </dsp:a>
      </p>
    </div>

    <%-- Registration area. --%>
    <dsp:form method="post" action="${originatingRequest.requestURI}"
              id="atg_store_registration" formid="atg_store_registerForm">
      <h3><fmt:message key="checkout_confirmResponse.registerTitle"/></h3>
      
      <dsp:setvalue bean="RegistrationFormHandler.extractDefaultValuesFromProfile" value="false"/>
      
      <%-- 
        Don't assign a new repository id during registration - keep transient one:
        This is critical because we store this ID in the ownerId field of the
        addresses (see below). 
        --%>
      <dsp:input bean="RegistrationFormHandler.createNewUser" type="hidden" value="false"/>

      <%-- If registration succeeds, go to my profile page and welcome new customer there --%>
      <dsp:input bean="RegistrationFormHandler.createSuccessURL" type="hidden" value="${contextPath}/myaccount/profile.jsp"/>
      <%-- If registration fails, redisplay this page with errors shown --%>
      <dsp:input bean="RegistrationFormHandler.createErrorURL" type="hidden" value="${originatingRequest.requestURI}"/>

      <%-- User must confirm password when registering --%>
      <dsp:input bean="RegistrationFormHandler.confirmPassword" type="hidden" value="true"/>

      <%-- A registered customer is a member --%>
      <dsp:input bean="RegistrationFormHandler.value.member" type="hidden" value="true"/>

      <%-- Set the sourceCode value in case user signs up to receive email --%>
      <dsp:input bean="RegistrationFormHandler.sourceCode" type="hidden" value="promo From registration"/>

      <%-- Set the flag for auto-login with cookies --%>
      <dsp:input bean="RegistrationFormHandler.value.autoLogin" type="hidden" value="true"/>

      <%-- Registration form --%>
      <dsp:include page="/myaccount/gadgets/registrationForm.jsp">
        <dsp:param name="formHandler" value="RegistrationFormHandler"/>
        <dsp:param name="email" value="${confirmationEmail}"/>
      </dsp:include>

      <div class="atg_store_formActions">
        <span class="atg_store_basicButton">
          <dsp:input bean="RegistrationFormHandler.create" type="submit" alt="${submitText}" value="${submitText}" />
        </span>
      </div>
    </dsp:form>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/confirmAndRegister.jsp#2 $$Change: 788278 $--%>

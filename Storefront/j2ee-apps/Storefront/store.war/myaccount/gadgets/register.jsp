<%-- 
  This page renders registration form for new customers
  
  Required parameters:
    None
  
  Optional parameters:
    checkout
      if this parameter is true, then register page was included
      at the checkout confirmation stage. In this case, we redirect
      new user to the shipping page after success registration.   
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/profile/SessionBean"/>
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath"/>

  <dsp:importbean bean="/atg/userprofiling/ProfileAdapterRepository"/>
  <dsp:importbean bean="/atg/store/profile/RegistrationFormHandler"/>
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>
 
  <div class="atg_store_registerForm atg_store_main" id="atg_store_newCustomerLogin">
    
    <%-- 
      Check to see if the user is unknown (do not show anything if the user is known)
    --%>
    <dsp:droplet name="ProfileSecurityStatus">
      <dsp:oparam name="anonymous">
        
        <%-- Get the text used on the submit link --%>
        <fmt:message  var="submitText" key="myaccount_registration.submit"/>
        <div class="atg_store_register">
    
          <dsp:setvalue bean="RegistrationFormHandler.extractDefaultValuesFromProfile" value="false"/>
    
          <dsp:form method="post" action="${originatingRequest.requestURI}"
                    id="atg_store_registration" 
                    formid="atg_store_registerForm">
            <dsp:getvalueof param="checkout" var="checkout" vartype="java.lang.String"/>
            <c:choose>
              <c:when test="${(empty checkout) || (checkout == false)}">
                <c:set var="successUrl" value="profile.jsp"/>
                <dsp:getvalueof bean="RegistrationFormHandler.value.email" var="email" vartype="java.lang.String"/>
                <c:set var="cancelUrl" value="${contextPath}/myaccount/login.jsp"/>
              </c:when>
              <c:otherwise>
                <c:set var="successUrl" value="shipping.jsp"/>
                <dsp:getvalueof param="newEmailAddress" var="email" vartype="java.lang.String"/>
                <c:url var="errorUrl" value="${pageContext.request.requestURI}" context="/">
                  <c:param name="newEmailAddress" value="${email}"/>
                  <c:param name="checkout" value="true"/>
                </c:url>
                <%--
                  Assign checkout error URL with lower priority, this will redirect to a login.jsp page with all proper parameters
                  set on the request. These parameters should be set for proper page displaying.
                --%>
                <dsp:input bean="RegistrationFormHandler.createErrorURL" type="hidden" value="${errorUrl}" priority="-5"/>
                <c:set var="cancelUrl" value="${contextPath}/checkout/login.jsp"/>
              </c:otherwise>
            </c:choose>
    
            <%-- 
              Don't assign a new repository id during registration - keep transient one:
              This is critical because we store this ID in the ownerId field of the
                 addresses (see below). 
              --%>
            <dsp:input bean="RegistrationFormHandler.createNewUser" type="hidden" value="false"/>
    
            <%-- If registration succeeds, go to my profile page and welcome new customer there --%>
            <dsp:input bean="RegistrationFormHandler.createSuccessURL" type="hidden" value="${successUrl}"/>
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
    
            <%-- Registration form fields --%>
            <dsp:include page="registrationForm.jsp">
              <dsp:param name="formHandler" value="RegistrationFormHandler"/>
              <dsp:param name="email" value="${email}"/>
            </dsp:include>
    
            <div class="atg_store_formFooter">
              <div class="atg_store_formKey">
                <span class="required">* <fmt:message key="common.requiredFields" /> </span>
              </div>
              <div class="atg_store_formActions">
                
                <%-- 'Save' button --%>
                <div class="atg_store_formActionItem">
                  <span class="atg_store_basicButton">
                    <dsp:input bean="RegistrationFormHandler.create" type="submit"
                               alt="${submitText}" value="${submitText}" 
                               id="atg_store_createMyAccount"/></span>
                </div>
                
                <%-- 'Cancel' button --%>
                <div class="atg_store_formActionItem">
                <dsp:a href="${cancelUrl}" iclass="atg_store_basicButton secondary">
                  <span><fmt:message key="common.button.cancelText"/></span>
                </dsp:a>
                </div>
              </div>
    
            </div>
    
            <dsp:input bean="RegistrationFormHandler.create" type="hidden" value="Submit"/>
          </dsp:form>
        </div>
      </dsp:oparam>
    </dsp:droplet>
  
  </div><%-- atg_store_registration  loginBox_blue--%>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/register.jsp#1 $$Change: 735822 $--%>

<%--
  This page checks, if the user is already logged in. If this is the case, it displays whether 
  Shipping or Confirmation page, depending on the 'express' parameter passed. Otherwise, this 
  page will display the Login form.

  Required parameters:
    None.

  Optional parameters:
    express
      This flag specifies, if express checkout option has been selected. If true, the user will be 
      redirected to the Confirmation page; otherwise the user will be redirected to the Shipping page.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/store/profile/CheckoutProfileFormHandler"/>
  
  <crs:pageContainer index="false" follow="false" bodyClass="atg_store_pageLogin atg_store_checkout">
    
    <jsp:attribute name="formErrorsRenderer">
      <%-- Show form errors above accessibility navigation. --%>
      <dsp:include page="/myaccount/gadgets/myAccountErrorMessage.jsp">
        <dsp:param name="formHandler" bean="CheckoutProfileFormHandler"/>
        <dsp:param name="errorMessageClass" value="errorMessage"/>
      </dsp:include>
    </jsp:attribute>
    
    <jsp:body>
      <fmt:message key="checkout_title.checkout" var="title"/>
      <crs:checkoutContainer currentStage="login" title="${title}">
        <jsp:body>          
          <dsp:importbean bean="/atg/userprofiling/Profile"/>
          
          <%-- show form errors --%>
          <dsp:include page="/myaccount/gadgets/myAccountErrorMessage.jsp">
            <dsp:param name="formHandler" bean="CheckoutProfileFormHandler"/>
            <dsp:param name="errorMessageClass" value="errorMessage"/>
          </dsp:include>

          <div id="atg_store_checkoutlogin">
            
            <%-- Existing customer tab --%>
            <div class="atg_store_checkoutLogin atg_store_loginMethod" id="atg_store_returningCustomerLogin">
              
              <h2>
                <span><fmt:message key="login.returningCustomer"/></span>
              </h2>
              
              <div class="atg_store_register">
                
                <dsp:form id="atg_store_checkoutLoginForm" formid="checkoutloginregistered" 
                          action="${pageContext.request.requestURI}" method="post">
                  
                  <%-- 
                    Returning customers should be redirected to Shipping or Confirmation page, depending 
                    on the 'express' parameter. 
                  --%>
                  <dsp:input bean="CheckoutProfileFormHandler.loginSuccessURL" type="hidden"
                             value="${param['express'] ? 'confirm.jsp?expressCheckout=true' : 'shipping.jsp'}"/>
                  <dsp:input bean="CheckoutProfileFormHandler.loginErrorURL" type="hidden" 
                             value="${pageContext.request.requestURI}"/>
                  
                  <fieldset class="enter_info atg_store_havePassword">
                    <div class="hid">
                      
                      <ul class="atg_store_basicForm">
                        
                        <%-- E-mail field. --%>
                        <li>
                          <label for="atg_store_emailInput">
                            <fmt:message key="common.email"/>
                            <span class="required">*</span>
                          </label>
                          
                          <dsp:getvalueof var="anonymousStatus" vartype="java.lang.Integer" 
                                          bean="/atg/userprofiling/PropertyManager.securityStatusAnonymous"/>
                          <dsp:getvalueof var="currentStatus" vartype="java.lang.Integer" 
                                          bean="Profile.securityStatus"/>
                          <dsp:getvalueof var="currentEmail" vartype="java.lang.String" 
                                          bean="Profile.email"/>
                          
                          <%-- Prepopulate the e-mail field for auto-logged in users only. --%>
                          <dsp:input type="text" bean="CheckoutProfileFormHandler.emailAddress" 
                                     name="email" value="${currentStatus > anonymousStatus ? currentEmail : ''}" 
                                     id="atg_store_emailInput" maxlength="255"/>
                        </li>
                        
                        <%-- Password Field. --%>
                        <li>
                         
                          <label for="atg_store_passwordInput">
                            <fmt:message key="common.loginPassword"/>
                            <span class="required">*</span>
                          </label>
                          
                          <dsp:input bean="CheckoutProfileFormHandler.value.password" type="password" 
                                     name="password" id="atg_store_passwordInput" value="" />
                          
                          <fmt:message var="forgotPasswordTitle" key="checkout_checkoutLogin.forgotPasswordTitle"/>
                          
                          <dsp:a page="/myaccount/passwordReset.jsp" title="${forgotPasswordTitle}" 
                                 iclass="info_link atg_store_forgetPassword">
                            <fmt:message key="common.button.passwordResetText"/>
                          </dsp:a>

                        </li>
                      </ul>
                      
                    </div>
                    <%-- Login button. --%>
                    <div class="atg_store_formFooter">
                    
                      <div class="atg_store_formActions">
                        <span class="atg_store_basicButton">
                          
                          <fmt:message key="myaccount_login.submit" var="loginCaption"/>
                          
                          <dsp:input bean="CheckoutProfileFormHandler.returningCustomer" 
                                     id="atg_store_checkoutLoginButton" type="submit" value="${loginCaption}"/>
                        </span>
                      </div>
                      
                    </div>
                  </fieldset>
                </dsp:form>
              </div>
            </div>

            <%-- New customer tab --%>
            <div class="atg_store_checkoutLogin atg_store_loginMethod" id="atg_store_newCustomerLogin">
              
              <h2>
                <span class="open"><fmt:message key="login.newCustomer"/></span>
              </h2>
              
              <div class="atg_store_register">
                <dsp:form id="atg_store_checkoutLoginForm" formid="checkoutloginnewuser" 
                          action="${pageContext.request.requestURI}" method="post">
                          
                  <!--[if IE]><input type="text" style="display: none;" disabled="disabled" size="1" /><![endif]-->
                  
                  <%-- 
                    New customers should be redirected to the Registration page. They will be redirected 
                    to Shipping later. 
                  --%>
                  <dsp:input bean="CheckoutProfileFormHandler.loginSuccessURL" type="hidden" value="registration.jsp"/>
                  <dsp:input bean="CheckoutProfileFormHandler.loginErrorURL" type="hidden" 
                             value="${pageContext.request.requestURI}"/>
                             
                  <fieldset class="enter_info atg_store_noPassword">
                    
                    <div class="hid">
                      <ul class="atg_store_basicForm">
                        <%-- E-mail field. --%>
                        <li>
                          
                          <label for="atg_store_emailInputRegister">
                            <fmt:message key="common.email"/>
                            <span class="required">*</span>
                          </label>
                          
                          <dsp:input type="text" bean="CheckoutProfileFormHandler.emailAddress" 
                                     id="atg_store_emailInputRegister" maxlength="255"/>

                          <a class="info_link" href="javascript:void(0)" 
                             onclick="atg.store.util.openwindow('../company/privacyPolicyPopup.jsp', 'sizeChart', 500, 500)">
                            <fmt:message key="common.button.privacyPolicyText"/>
                          </a>
                        </li>
                      </ul>
                    </div>
                    
                  </fieldset>
                  <%-- Register button. --%>
                  <div class="atg_store_formFooter">
                    <div class="atg_store_formActions">
                      <span class="atg_store_basicButton">
                        <fmt:message key="myaccount_registration.submit" var="createAccountCaption"/>
                        <dsp:input bean="CheckoutProfileFormHandler.newCustomer" id="atg_store_createMyAccountButton" 
                                   type="submit" value="${createAccountCaption}"/>
                      </span>
                    </div>
                  </div>
                </dsp:form>
              </div>
            </div>

            <%-- Anonymous customer tab --%>
            <div class="atg_store_checkoutLogin atg_store_loginMethod" id="atg_store_anonCustomerLogin">
              
              <h2>
                <span class="open"><fmt:message key="checkout_login.button.anonymous"/></span>
              </h2>
              
              <div class="atg_store_register">
                <dsp:form id="atg_store_checkoutLoginForm" formid="checkoutloginanonymous" 
                          action="${pageContext.request.requestURI}" method="post">
                          
                  <%-- Anonymous customers should be redirected directly to the Shipping page. --%>
                  <dsp:input bean="CheckoutProfileFormHandler.loginSuccessURL" type="hidden" 
                             value="shipping.jsp"/>
                  <dsp:input bean="CheckoutProfileFormHandler.loginErrorURL" type="hidden" 
                             value="${pageContext.request.requestURI}"/>
                             
                  <fieldset>
                    <ul class="atg_store_basicForm">
                      <li>
                        <p><fmt:message key="checkout_login.description.anonymous"/></p>
                      </li>
                    </ul>
                  </fieldset>
                  
                  <%-- Anonymous button. --%>
                  <div class="atg_store_formFooter">
                    <div class="atg_store_formActions">
                      <span class="atg_store_basicButton">
                        <fmt:message key="checkout_login.button.anonymous" var="anonymousCaption"/>
                        <dsp:input bean="CheckoutProfileFormHandler.anonymousCustomer" type="submit" 
                                   value="${anonymousCaption}"/>
                      </span>
                    </div>
                  </div>
                </dsp:form>
              </div>
            </div>
          </div>
        </jsp:body>
      </crs:checkoutContainer>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/login.jsp#2 $$Change: 788278 $--%>
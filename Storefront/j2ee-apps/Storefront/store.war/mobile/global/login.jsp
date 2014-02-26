<%--
  This page renders a form with three different possible options:
    - Login
    - Signup
    - Skip Registration

  Each different form is contained in its own row, and clicking on that row causes a child row to expand containing the form.

  Page includes:
    /mobile/global/gadgets/loginPage.jsp - Login form
    /mobile/global/gadgets/registration.jsp - Registration form for new customers

  Required parameters:
    checkoutLogin
      If true, sets the FormHandler, error/success URLs, and proper form
      inputs to the "CheckoutProfileFormHandler". Otherwise, "ProfileFormHandler" is used
    registrationFormHandler
      FormHandler, used for user registration, one from below:
        /atg/store/profile/RegistrationFormHandler
        /atg/store/mobile/order/purchase/MobileBillingFormHandler
    registrationSuccessUrl
      Redirection path, used when registration succeeds

  Optional parameters:
    passwordSent
      Tells us whether the customer has had a temporary password sent to their email
    loginErrors
      If true, we know there are errors on the login form, and will show it by default
    registrationErrors
      If true, we know there are errors on the registration form, and will show it by default
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="passwordSent" param="passwordSent"/>
  <dsp:getvalueof var="checkoutLogin" param="checkoutLogin"/>
  <dsp:getvalueof var="loginErrors" param="loginErrors"/>
  <dsp:getvalueof var="registrationErrors" param="registrationErrors"/>

  <dsp:getvalueof var="currentLocale" vartype="java.lang.String" bean="/atg/dynamo/servlet/RequestLocale.localeString"/>

  <fmt:message var="pageTitle" key="mobile.common.login"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <div class="dataContainer">
      <br/>
      <ul class="dataList">
        <%-- Login row --%>
        <%-- Update the arrow if the login row will open by default --%>
        <li class="icon-ArrowDown ${loginErrors == 'true' ? 'turn' : ''}" onclick="CRSMA.global.loginPageClick('loginRow', this);">
          <div class="content">
            <a href="javascript:void(0);" onclick="">
              <span class="parentSpan">
                <span class="title"><fmt:message key="mobile.common.login"/></span>
                <%-- Get the first name if the user is auto logged in --%>
                <dsp:droplet name="ProfileSecurityStatus">
                  <dsp:oparam name="autoLoggedIn">
                    <span class="vcard">
                      <dsp:valueof bean="Profile.firstName"/>
                      <c:set var="logoutShow" value="true"/>
                    </span>
                  </dsp:oparam>
                </dsp:droplet>
                <span class="rightContentContainer">
                  <span class="username">
                    <div class="arrow"></div>
                  </span>
                </span>
              </span>
            </a>
          </div>
        </li>
        <%-- Login Form row --%>
        <%-- If we don't have errors in the login row, hide by default --%>
        <li id="loginRow" class="${!(loginErrors == 'true' || passwordSent == 'true' || logoutShow == 'true') ? 'hidden ' : ''} expandable">
          <div class="content">
            <dsp:include page="gadgets/loginPage.jsp">
              <c:if test="${checkoutLogin == 'true'}">
                <dsp:param name="checkoutLogin" value="true"/>
                <dsp:param name="passwordSent" value="${passwordSent}"/>
              </c:if>
            </dsp:include>
          </div>
        </li>
        <%-- If logged in, display a logout row, otherwise display a registration row --%>
        <c:choose>
          <c:when test="${logoutShow == 'true'}">
            <li>
              <div class="content">
                <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>

                <dsp:a page="/">
                  <span>
                    <c:url value="${pageContext.request.requestURI}" var="successURL" context="/">
                      <c:if test="${checkoutLogin == 'true'}">
                        <c:param name="checkoutLogin" value="true"/>
                      </c:if>
                      <c:param name="userLocale" value="${currentLocale}"/>
                    </c:url>
                    <dsp:property bean="ProfileFormHandler.logoutSuccessURL" value="${successURL}"/>
                    <dsp:property bean="ProfileFormHandler.logout" value="true"/>

                    <span class="title"><fmt:message key="mobile.common.logout"/></span>
                  </span>
                </dsp:a>
              </div>
            </li>
          </c:when>
          <c:otherwise>
            <%-- Registration row --%>
            <%-- Update the arrow if the login row will open by default --%>
            <li class="icon-ArrowDown ${registrationErrors == 'true' ? 'turn' : ''}" onclick="CRSMA.global.loginPageClick('regRow', this);">
              <div class="content">
                <a href="javascript:void(0);" onclick="">
                  <span class="parentSpan">
                    <span class="title">
                      <fmt:message key="mobile.myaccount_login.button.create"/>
                    </span>
                    <span class="rightContentContainer">
                      <div class="arrow"></div>
                    </span>
                  </span>
                </a>
              </div>
            </li>
            <%-- Registration form row --%>
            <li id="regRow" class="${registrationErrors != 'true' ? 'hidden ' : ''}expandable">
              <dsp:include page="gadgets/registration.jsp">
                <dsp:param name="formHandler" param="registrationFormHandler"/>
                <dsp:param name="successUrl" param="registrationSuccessUrl"/>
              </dsp:include>
            </li>
          </c:otherwise>
        </c:choose>
        <%-- Display the "Skip Login" row if using "Checkout" workflow --%>
        <c:if test="${checkoutLogin == 'true'}">
          <li onclick="document.anonymousForm.submit();" class="icon-ArrowRight">
            <div class="content">
              <a href="javascript:void(0);" onclick="">
                <dsp:importbean bean="/atg/store/profile/CheckoutProfileFormHandler"/>
                <%-- ========== Form ========== --%>
                <dsp:form action="${pageContext.request.requestURI}" method="post" name="anonymousForm" formid="anonymousForm">
                  <c:url value="../checkout/shipping.jsp" var="successURL">
                    <c:param name="locale" value="${currentLocale}"/>
                  </c:url>
                  <dsp:input bean="CheckoutProfileFormHandler.loginSuccessURL" type="hidden" value="${successURL}"/>
                  <fmt:message key="mobile.myaccount_login.button.anonymous" var="anonymousCaption"/>
                  <dsp:input bean="CheckoutProfileFormHandler.anonymousCustomer" type="hidden" value="${anonymousCaption}"/>
                </dsp:form>
                <span class="parentSpan">
                  <span class="title">
                    <fmt:message key="mobile.myaccount_login.button.anonymous"/>
                  </span>
                  <span class="rightContentContainer">
                    <div class="arrowRight"></div>
                  </span>
                </span>
              </a>
            </div>
          </li>
        </c:if>
      </ul>
      <br/>
    </div>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/global/login.jsp#3 $$Change: 788278 $--%>

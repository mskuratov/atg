<%--
  Login form.

  Page includes:
    None

  Required parameters:
    passwordSent
      Whether the customer has had a temporary password sent to their email
    checkoutLogin
      If true, sets the FormHandler, error/success URLs, and proper form
      inputs to the "CheckoutProfileFormHandler". Otherwise, "ProfileFormHandler" FormHandler is used.

  Optional parameters:
    email
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>
  <dsp:importbean bean="/atg/store/profile/SessionBean" var="sessionbean"/>

  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="checkoutLogin" param="checkoutLogin"/>
  <dsp:getvalueof var="passwordSent" param="passwordSent"/>

  <dsp:getvalueof var="mobileStorePrefix" bean="/atg/store/StoreConfiguration.mobileStorePrefix"/>

  <%-- See which FormHandler we are using, and set all necessary values --%>
  <c:choose>
    <c:when test="${checkoutLogin == 'true'}">
      <dsp:importbean bean="/atg/store/profile/CheckoutProfileFormHandler"/>
      <c:set var="handler" value="CheckoutProfileFormHandler"/>
      <c:set var="emailField" value="${handler}.emailAddress"/>
      <dsp:getvalueof var="emailValue" bean="CheckoutProfileFormHandler.emailAddress"/>
      <c:if test="${empty emailValue}">
        <dsp:getvalueof var="emailValue" bean="/atg/userprofiling/Profile.email"/>
      </c:if>
      <c:set var="loginField" value="${handler}.returningCustomer"/>
    </c:when>
    <c:otherwise>
      <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
      <c:set var="handler" value="ProfileFormHandler"/>
      <c:set var="emailField" value="${handler}.value.login"/>
      <dsp:getvalueof var="emailValue" bean="ProfileFormHandler.value.login"/>
      <c:set var="loginField" value="${handler}.login"/>
    </c:otherwise>
  </c:choose>

  <c:if test="${empty emailValue}">
    <dsp:getvalueof var="paramEmailValue" param="email"/>
    <c:if test="${not empty paramEmailValue}">
      <c:set var="emailValue" value="${paramEmailValue}"/>
    </c:if>
  </c:if>

  <%-- ========== Handle form exceptions ========== --%>
  <dsp:getvalueof var="formExceptions" bean="${handler}.formExceptions"/>
  <jsp:useBean id="errorMap" class="java.util.HashMap"/>
  <c:if test="${not empty formExceptions}">
    <c:forEach var="formException" items="${formExceptions}">
      <c:set var="errorCode" value="${formException.errorCode}"/>
      <c:choose>
        <c:when test="${errorCode == 'missingEmail'}">
          <c:set target="${errorMap}" property="email" value="missing"/>
        </c:when>
        <c:when test="${errorCode == 'missingRequiredValue'}">
          <c:set var="propertyName" value="${formException.propertyName}"/>
          <c:set target="${errorMap}" property="${propertyName}" value="missing"/>
        </c:when>
        <c:when test="${errorCode == 'invalidLogin' || errorCode == 'invalidEmailAddress'}">
          <c:set target="${errorMap}" property="email" value="invalid"/>
        </c:when>
        <c:when test="${errorCode == 'invalidPassword'}">
          <c:set target="${errorMap}" property="badPassword" value="invalid"/>
        </c:when>
      </c:choose>
    </c:forEach>
  </c:if>

  <%--
    Check Profile security status. If user is logged in from cookie,
    display default values, i.e. profile's email address in this case, otherwise
    do not populate form handler with profile values
  --%>
  <dsp:droplet name="ProfileSecurityStatus">
    <dsp:oparam name="anonymous">
      <dsp:setvalue bean="${handler}.extractDefaultValuesFromProfile" value="false"/>
    </dsp:oparam>
    <dsp:oparam name="autoLoggedIn">
      <dsp:setvalue bean="${handler}.extractDefaultValuesFromProfile" value="true"/>
    </dsp:oparam>
  </dsp:droplet>

  <%-- ========== Form ========== --%>
  <dsp:form action="${pageContext.request.requestURI}" method="post" formid="loginPage">
    <%-- "Remember me on this device" --%>
    <dsp:input bean="${handler}.value.autoLogin" type="hidden" value="true"/>
    <%-- ========== Redirection URLs ========== --%>
    <dsp:getvalueof var="currentLocale" vartype="java.lang.String" bean="/atg/dynamo/servlet/RequestLocale.localeString"/>
    <%-- SuccessURL --%>
    <c:set var="successURI">
      <c:choose>
        <c:when test="${checkoutLogin == 'true'}">../checkout/shipping.jsp</c:when>
        <c:otherwise>../myaccount/profile.jsp</c:otherwise>
      </c:choose>
    </c:set>
    <c:url value="${successURI}" var="successURL">
      <c:param name="locale" value="${currentLocale}"/>
    </c:url>
    <dsp:input bean="${handler}.loginSuccessURL" type="hidden" value="${successURL}"/>
    <%-- ErrorURL --%>
    <c:url value="${pageContext.request.requestURI}" var="errorURL" context="/">
      <c:if test="${checkoutLogin == 'true'}">
        <c:param name="checkoutLogin" value="true"/>
      </c:if>
      <c:param name="loginErrors" value="true"/>
    </c:url>
    <dsp:input bean="${handler}.loginErrorURL" type="hidden" value="${errorURL}"/>

    <div class="dataContainer">
      <ul class="dataList" role="presentation">
        <c:if test="${not empty errorMap['email'] || not empty errorMap['badPassword']}">
          <li class="errorStateLine">
            <div class="content">
              <span class="errorMessageLine"><fmt:message key="mobile.form.validation.invalidLoginCred"/></span>
            </div>
          </li>
        </c:if>
        <c:if test="${passwordSent == 'true'}">
          <li class="errorState">
            <span class="errorMessageLine"><fmt:message key="mobile.myaccount_login.passwordSent"/></span>
          </li>
        </c:if>
        <%-- "Email" --%>
        <c:set var="liClassEmail">
          <c:choose>
            <c:when test="${not empty errorMap['login'] || not empty errorMap['emailAddress']}">errorState</c:when>
            <c:when test="${not empty errorMap['email'] || not empty errorMap['badPassword'] || passwordSent == 'true'}">errorState fullWidth</c:when>
          <c:otherwise></c:otherwise>
          </c:choose>
        </c:set>
        <li class="${liClassEmail}">
            <div class="content">
              <c:set var="populatedEmail" value="${(not empty emailValue) ? emailValue : sessionbean.values['rememberedEmail']}"/>
              <fmt:message var="emailPlaceholder" key="mobile.common.email"/>
              <dsp:input bean="${emailField}" required="true" type="email" value="${populatedEmail}"
                         placeholder="${emailPlaceholder}" aria-label="${emailPlaceholder}" autocapitalize="off" maxlength="40"/>
            </div>
            <c:if test="${not empty errorMap['login'] || not empty errorMap['emailAddress']}">
              <span class="errorMessage">
                <fmt:message key="mobile.form.validation.missing"/>
              </span>
            </c:if>
        </li>
        <%-- "Password" --%>
        <li ${not empty errorMap['email'] || not empty errorMap['badPassword'] || not empty errorMap['password'] || passwordSent == 'true' ? 'class="errorState"' : ''}>
            <div class="content">
              <fmt:message var="password" key="mobile.common.password"/>
              <dsp:input bean="${handler}.value.password" type="password" required="true" value="" maxlength="35"
                         placeholder="${password}" aria-label="${password}"/>
            </div>
            <c:choose>
              <c:when test="${passwordSent == 'true'}">
                <span class="errorMessage normalWidth">
                  <p><fmt:message key="mobile.passwordReset.paste"/></p>
                </span>
              </c:when>
              <c:when test="${not empty errorMap['password']}">
                <span class="errorMessage">
                  <fmt:message key="mobile.form.validation.missing"/>
                </span>
              </c:when>
            </c:choose>
        </li>
        <c:if test="${not empty errorMap['email'] || not empty errorMap['badPassword']}">
          <li class="errorStateLine icon-ArrowForget">
            <dsp:a page="${mobileStorePrefix}/myaccount/passwordReset.jsp?email=${emailValue}&checkoutLogin=${checkoutLogin}" title="${forgotPasswordTitle}">
              <span class="content">
                <span class="errorMessageLine"><fmt:message key="mobile.login.passwordResetText"/></span>
              </span>
            </dsp:a>
          </li>
        </c:if>
      </ul>
    </div>

    <%-- "Submit" button --%>
    <div class="centralButton">
      <fmt:message key="mobile.common.login" var="submitBtnValue"/>
      <dsp:input bean="${loginField}" class="mainActionButton" type="submit" value="${submitBtnValue}"/>
    </div>
  </dsp:form>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/global/gadgets/loginPage.jsp#3 $$Change: 788278 $--%>

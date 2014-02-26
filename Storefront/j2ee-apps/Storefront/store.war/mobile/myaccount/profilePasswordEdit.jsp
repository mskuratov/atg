<%--
  "Change My Password" page.

  Page includes:
    /mobile/myaccount/gadgets/subheaderAccounts.jsp - Display subheader items

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>

  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="resultSuccess" param="resultSuccess"/>

  <%-- ========== Handle form exceptions ========== --%>
  <dsp:getvalueof var="formExceptions" bean="ProfileFormHandler.formExceptions"/>
  <jsp:useBean id="errorMap" class="java.util.HashMap"/>
  <c:if test="${not empty formExceptions}">
    <c:forEach var="formException" items="${formExceptions}">
      <c:set var="errorCode" value="${formException.errorCode}"/>
      <c:choose>
        <c:when test="${errorCode == 'missingRequiredValue'}">
          <c:set var="propertyName" value="${formException.propertyName}"/>
          <c:set target="${errorMap}" property="${propertyName}" value="missing"/>
        </c:when>
        <c:when test="${errorCode == 'passwordsDoNotMatch'}">
          <c:set target="${errorMap}" property="notMatching" value="invalid"/>
        </c:when>
        <c:when test="${errorCode == 'passwordEqualsOldPassword'}">
          <c:set target="${errorMap}" property="allEqual" value="invalid"/>
        </c:when>
        <%-- Old password doesn't match --%>
        <c:when test="${errorCode == 'permissionDefinedPasswordChange'}">
          <c:set target="${errorMap}" property="oldInvalid" value="invalid"/>
        </c:when>
        <%-- Password length too short --%>
        <c:when test="${errorCode == 'atg.droplet.DropletException'}">
          <c:set target="${errorMap}" property="requirements" value="invalid"/>
        </c:when>
      </c:choose>
    </c:forEach>
  </c:if>

  <fmt:message var="pageTitle" key="mobile.myaccount_profileMyInfoEditLinks.button.changePasswordText"/>
  <crs:mobilePageContainer titleString="${pageTitle}" displayModal="${empty resultSuccess ? false : true}">
    <jsp:attribute name="modalContent">
      <%-- Visible by default when resultSuccess is true --%>
      <div id="modalMessageBox" ${empty resultSuccess ? '' : 'style="display:block"'}>
        <div>
          <ul class="dataList">
            <li>
              <div class="content">
                <fmt:message key="mobile.emailtemplates_changePassword.title"/>
              </div>
            </li>
            <li>
              <div class="content">
                <dsp:a href="profile.jsp" class="icon-ArrowRight">
                  <fmt:message var="myaccountTitle" key="mobile.myaccount_myAccountMenu.myAccount"/>
                  <fmt:message key="mobile.common.returnTo"><fmt:param value="${myaccountTitle}"/></fmt:message>
                </dsp:a>
               </div>
            </li>
          </ul>
        </div>
      </div>
    </jsp:attribute>

    <jsp:body>
      <%-- ========== Subheader ========== --%>
      <fmt:message var="centerText" key="mobile.myaccount_changePassword.title"/>
      <dsp:include page="gadgets/subheaderAccounts.jsp">
        <dsp:param name="rightText" value="${centerText}"/>
        <dsp:param name="highlight" value="right"/>
      </dsp:include>

      <div class="dataContainer">
        <%-- ========== Form ========== --%>
        <dsp:form action="${pageContext.request.requestURI}" method="post" formid="profilePasswordEdit">
          <%-- Prevent prepopulation of input fields --%>
          <dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>

          <%-- ========== Redirection URLs ========== --%>
          <dsp:input bean="ProfileFormHandler.changePasswordSuccessURL" type="hidden"
                     value="${pageContext.request.requestURI}?resultSuccess=true"/>
          <dsp:input bean="ProfileFormHandler.changePasswordErrorURL" type="hidden"
                     value="${pageContext.request.requestURI}"/>

          <%-- Require that both of the new passwords entered match --%>
          <dsp:input bean="ProfileFormHandler.confirmPassword" type="hidden" value="true"/>

          <ul class="dataList" role="presentation">
            <%-- Grab commonly used missing error text --%>
            <fmt:message var="missing" key="mobile.form.validation.missing"/>

            <%-- "Old Password" --%>
            <li ${not empty errorMap['oldpassword'] || not empty errorMap['oldInvalid'] ? 'class="errorState"' : ''}>
              <div class="content">
                <fmt:message var="oldPasswordPlaceholder" key="mobile.myaccount_profilePasswordEdit.oldPassword"/>
                <dsp:input id="oldPassword" maxlength="35" bean="ProfileFormHandler.value.oldpassword"
                           type="password" required="true" value="" placeholder="${oldPasswordPlaceholder}" aria-label="${oldPasswordPlaceholder}" />
                <label for="oldPassword"/>
              </div>
              <c:if test="${not empty errorMap['oldpassword']}">
                <span class="errorMessage">${missing}</span>
              </c:if>
              <c:if test="${not empty errorMap['oldInvalid']}">
                <span class="errorMessage"><fmt:message key="mobile.form.validation.retry"/></span>
              </c:if>
            </li>

            <%-- "New Password" --%>
            <li ${not empty errorMap['password'] || not empty errorMap['allEqual'] || not empty errorMap['requirements'] ? 'class="errorState"' : ''}>
              <div class="content">
                <fmt:message var="newPasswordPlaceholder" key="mobile.myaccount_profilePasswordEdit.newPassword"/>
                <dsp:input maxlength="35" bean="ProfileFormHandler.value.password"
                           type="password" required="true" value="" placeholder="${newPasswordPlaceholder}" aria-label="${newPasswordPlaceholder}" />
              </div>
              <c:if test="${not empty errorMap['password']}">
                <span class="errorMessage">${missing}</span>
              </c:if>
              <c:if test="${not empty errorMap['allEqual']}">
                <span class="errorMessage"><fmt:message key="mobile.form.validation.reusePassword"/></span>
              </c:if>
              <c:if test="${not empty errorMap['requirements']}">
                <span class="errorMessage normalWidth"><p><fmt:message key="mobile.form.validation.requirements"/></p></span>
              </c:if>
            </li>

            <%-- "Confirm New Password" --%>
            <li ${not empty errorMap['confirmpassword'] || not empty errorMap['notMatching'] ? 'class="errorState"' : ''}>
              <div class="content">
                <fmt:message var="confirmPasswordPlaceholder" key="mobile.common.confirmPassword"/>
                <dsp:input maxlength="35" bean="ProfileFormHandler.value.confirmpassword"
                           type="password" required="true" value="" placeholder="${confirmPasswordPlaceholder}" aria-label="${confirmPasswordPlaceholder}" />
              </div>
              <c:if test="${not empty errorMap['confirmpassword']}">
                <span class="errorMessage">${missing}</span>
              </c:if>
              <c:if test="${not empty errorMap['notMatching']}">
                <span class="errorMessage">
                  <fmt:message key="mobile.form.validation.mismatch"/>
                </span>
              </c:if>
            </li>
          </ul>

          <%-- "Submit" button --%>
          <div class="centralButton">
            <fmt:message var="updateButton" key="mobile.common.button.saveChanges"/>
            <dsp:input bean="ProfileFormHandler.changePassword" class="mainActionButton" type="submit" value="${updateButton}"/>
          </div>
        </dsp:form>
      </div>
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/myaccount/profilePasswordEdit.jsp#3 $$Change: 788278 $--%>

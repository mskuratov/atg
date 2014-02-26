<%--
  This page displays the "Personal Information" details with "edit" functionality.

  Page includes:
    /mobile/myaccount/gadgets/subheaderAccounts.jsp - Display subheader items

  Required parameters:
    none

  Optional parameters:
    none
--%>
<dsp:page>
  <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>
  <dsp:importbean bean="/atg/dynamo/droplet/PossibleValues"/>
  <dsp:importbean bean="/atg/store/droplet/IsEmailRecipient"/>
  <dsp:importbean bean="/atg/store/profile/RegistrationFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileAdapterRepository"/>

  <%-- ========== Handle form exceptions ========== --%>
  <dsp:getvalueof var="formExceptions" bean="RegistrationFormHandler.formExceptions"/>
  <jsp:useBean id="errorMap" class="java.util.HashMap"/>
  <c:if test="${not empty formExceptions}">
    <c:forEach var="formException" items="${formExceptions}">
      <c:set var="errorCode" value="${formException.errorCode}"/>
      <c:choose>
        <c:when test="${errorCode == 'missingRequiredValue'}">
          <c:set var="propertyName" value="${formException.propertyName}"/>
          <c:set target="${errorMap}" property="${propertyName}" value="missing"/>
        </c:when>
        <c:when test="${errorCode == 'postalCode'}">
          <c:set target="${errorMap}" property="postalCode" value="invalid"/>
        </c:when>
        <c:when test="${errorCode == 'userAlreadyExists'}">
          <c:set target="${errorMap}" property="email" value="inUse"/>
        </c:when>
        <c:when test="${errorCode == 'invalidEmailAddress'}">
          <c:set target="${errorMap}" property="email" value="invalid"/>
        </c:when>
      </c:choose>
    </c:forEach>
  </c:if>

  <fmt:message var="pageTitle" key="mobile.myaccount_profileMyInfo.myInformation"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <jsp:body>
      <%-- Set the Date format --%>
      <dsp:getvalueof var="dateFormat" bean="LocaleTools.userFormattingLocaleHelper.datePatterns.shortWith4DigitYear"/>
      <dsp:setvalue bean="RegistrationFormHandler.dateFormat" value="${dateFormat}"/>
      <dsp:setvalue bean="RegistrationFormHandler.extractDefaultValuesFromProfile" value="true"/>

      <%-- ========== Subheader ========== --%>
      <fmt:message var="editProf" key="mobile.myaccount_personalInformation.editProfile"/>
      <dsp:include page="gadgets/subheaderAccounts.jsp">
        <dsp:param name="rightText" value="${editProf}"/>
        <dsp:param name="highlight" value="right"/>
      </dsp:include>

      <div class="dataContainer">
        <%-- ========== Form ========== --%>
        <dsp:form formid="accountProfileEdit" action="${pageContext.request.requestURI}" method="post">
          <%-- ========== Redirection URLs ========== --%>
          <dsp:input bean="RegistrationFormHandler.updateSuccessURL" type="hidden" value="profile.jsp"/>
          <dsp:input bean="RegistrationFormHandler.updateErrorURL" type="hidden" value="${pageContext.request.requestURI}"/>

          <ul class="dataList" role="presentation">
            <%-- "First Name" --%>
            <li ${not empty errorMap['firstName'] ? 'class="errorState"' : ''}>
              <fmt:message var="firstNamePlace" key="mobile.profile.firstName"/>
              <div class="content">
                <dsp:input maxlength="40" bean="RegistrationFormHandler.value.firstName" type="text"
                            placeholder="${firstNamePlace}" aria-label="${firstNamePlace}" required="true"/> 
              </div>
              <c:if test="${not empty errorMap['firstName']}">
                <span class="errorMessage">
                  <fmt:message key="mobile.form.validation.${errorMap['firstName']}"/>
                </span>
              </c:if>
            </li>
            <%-- "Last Name" --%>
            <li ${not empty errorMap['lastName'] ? 'class="errorState"' : ''}>
              <fmt:message var="lastNamePlace" key="mobile.profile.lastName"/>
              <div class="content">
                <dsp:input maxlength="40" bean="RegistrationFormHandler.value.lastName" type="text"
                           placeholder="${lastNamePlace}" aria-label="${lastNamePlace}" required="true"/>
              </div>
              <c:if test="${not empty errorMap['lastName']}">
                <span class="errorMessage">
                  <fmt:message key="mobile.form.validation.${errorMap['lastName']}"/>
                </span>
              </c:if>
            </li>
            <%-- "Email" --%>
            <li ${not empty errorMap['email'] ? 'class="errorState"' : ''}>
              <fmt:message var="emailPlace" key="mobile.common.email"/>
              <div class="content">
                <dsp:input maxlength="40" bean="RegistrationFormHandler.value.email"
                           placeholder="${emailPlace}" aria-label="${emailPlace}" type="email" required="true" autocapitalize="off"/>
              </div>
              <c:if test="${not empty errorMap['email']}">
                <span class="errorMessage">
                  <fmt:message key="mobile.form.validation.${errorMap['email']}"/>
                </span>
              </c:if>
            </li>
            <li>
              <fmt:message var="postalCodePlace" key="mobile.common.postalCode"/>
              <%-- "Postal Code" --%>
              <div class="left ${not empty errorMap['postalCode'] ? 'errorState bottom' : ''}">
                <div class="content">
                  <dsp:input maxlength="10" size="10" bean="RegistrationFormHandler.value.homeAddress.postalCode"
                             placeholder="${postalCodePlace}" aria-label="${postalCodePlace}" type="text" required="false"/>
                </div>
                <c:if test="${not empty errorMap['postalCode']}">
                  <span class="errorMessage">
                    <fmt:message key="mobile.form.validation.${errorMap['postalCode']}"/>
                  </span>
                </c:if>
              </div>
              <div class="right">
                <%-- "Phone" --%>
                <fmt:message var="phonePlace" key="mobile.common.phone"/>
                <div class="content icon-ArrowLeft">
                  <dsp:input maxlength="15" size="10" bean="RegistrationFormHandler.value.homeAddress.phoneNumber"
                             type="tel" required="false" placeholder="${phonePlace}" aria-label="${phonePlace}" />
                </div>
              </div>
            </li>
            <li>
              <%-- "Gender" --%>
              <div class="content icon-ArrowLeft">
                <dsp:getvalueof var="selectedGender" bean="RegistrationFormHandler.value.gender"/>
                <dsp:getvalueof var="selectStyle" value="${(selectedGender == 'unknown') ? ' default' : ''}"/>
                <%-- No permanent styles should be applied to this select using a class --%>
                <dsp:select onchange="CRSMA.myaccount.changeDropdown(event)" name="genderSelect"
                            bean="RegistrationFormHandler.value.gender" class="select ${selectStyle}">
                  <%--
                    Get genders from "PossibleValues" droplet need to pass parameter "itemDescriptorName" as "user"
                    and "propertyName" as "gender".
                  --%>
                  <dsp:droplet name="PossibleValues">
                    <dsp:param name="itemDescriptorName" value="user"/>
                    <dsp:param name="propertyName" value="gender"/>
                    <dsp:param name="repository" bean="ProfileAdapterRepository"/>
                    <dsp:param name="returnValueObjects" value="true"/>
                    <dsp:param name="comparator" bean="/atg/userprofiling/ProfileEnumOptionsComparator"/>
                    <dsp:param name="bundle" bean="/atg/multisite/Site.resourceBundle"/>
                    <dsp:oparam name="output">
                      <%-- Get the output parameter of "PossibleValues" Droplet and put it into the forEach --%>
                      <dsp:getvalueof var="values" param="values"/>
                      <c:forEach var="gender" items="${values}">
                        <c:choose>
                          <%-- If value is unknown, show title --%>
                          <c:when test="${gender.settableValue == 'unknown'}">
                            <dsp:option iclass="default" value="${gender.settableValue}">
                              <fmt:message key="mobile.profile.selectGender"/>
                            </dsp:option>
                          </c:when>
                          <%-- Otherwise get possible values from repository --%>
                          <c:otherwise>
                            <dsp:option value="${gender.settableValue}">
                              <c:out value="${gender.localizedLabel}"/>
                            </dsp:option>
                          </c:otherwise>
                        </c:choose>
                      </c:forEach>
                    </dsp:oparam>
                  </dsp:droplet>
                </dsp:select>
              </div>
            </li>
            <li>
              <%-- "Date of Birth" --%>
              <div class="left40">
                <div class="content">
                  <label class="hint left"><fmt:message key="mobile.profile.DOB"/></label>
                </div>
              </div>
              <div class="right60">
                <div class="content">
                  <jsp:useBean id="currDate" class="java.util.Date"/>
                  <fmt:formatDate var="currYear" value="${currDate}" type="DATE" pattern="yyyy"/>
                  <dsp:getvalueof var="DOB_day" bean="RegistrationFormHandler.date"/>
                  <dsp:getvalueof var="DOB_month" bean="RegistrationFormHandler.month"/>
                  <dsp:getvalueof var="DOB_year" bean="RegistrationFormHandler.year"/>
                  <div class="day">
                    <dsp:select id="DOB_DaySelect" bean="RegistrationFormHandler.date" required="false"
                                iclass="${empty DOB_day ? 'default' : ''}" onchange="CRSMA.myaccount.changeDropdown(event)">
                      <dsp:option value=""><fmt:message key="mobile.profile.DOB.day"/></dsp:option>
                      <%-- Set "end day" for days cycle, respecting leap year --%>
                      <c:set var="endDay">
                        <c:choose>
                          <c:when test="${not empty DOB_month && DOB_month == 2}">
                            <c:choose>
                              <c:when test="${not empty DOB_year && (DOB_year % 4 == 0 && (!(DOB_year % 100 == 0) || DOB_year % 400 == 0))}">29</c:when>
                              <c:otherwise>28</c:otherwise>
                            </c:choose>
                          </c:when>
                          <c:when test="${not empty DOB_month && (DOB_month == 4 || DOB_month == 6 || DOB_month == 9 || DOB_month == 11)}">30</c:when>
                          <c:otherwise>31</c:otherwise>
                        </c:choose>
                      </c:set>
                      <%-- Display days [1..31] --%>
                      <c:forEach var="count" begin="1" end="${endDay}" step="1">
                        <c:choose>
                          <c:when test="${not empty DOB_day && DOB_day == count}">
                            <dsp:option selected="true" value="${count}">${count}</dsp:option>
                          </c:when>
                          <c:otherwise>
                            <dsp:option value="${count}">${count}</dsp:option>
                          </c:otherwise>
                        </c:choose>
                      </c:forEach>
                    </dsp:select>
                    <label for="DOB_DaySelect"/> <%-- This makes VoiceOver to pausing before select element reading --%>
                  </div>
                  <div class="dateDelimiter"><label class="hint">/&nbsp;</label></div>
                  <div class="month">
                    <dsp:select id="DOB_MonthSelect" bean="RegistrationFormHandler.month" required="false"
                                iclass="${empty DOB_month ? 'default' : ''}" onchange="CRSMA.myaccount.changeDOBDropdown(event)">
                      <dsp:option value=""><fmt:message key="mobile.common.month"/></dsp:option>
                      <%-- Display months --%>
                      <c:forEach var="count" begin="1" end="12" step="1">
                        <c:choose>
                          <c:when test="${not empty DOB_month && DOB_month == count}">
                            <dsp:option selected="true" value="${count}"><fmt:message key="mobile.common.month${count}"/></dsp:option>
                          </c:when>
                          <c:otherwise>
                            <dsp:option value="${count}"><fmt:message key="mobile.common.month${count}"/></dsp:option>
                          </c:otherwise>
                        </c:choose>
                      </c:forEach>
                    </dsp:select>
                    <label for="DOB_MonthSelect"/> <%-- This makes VoiceOver to pausing before select element reading --%>
                  </div>
                  <div class="dateDelimiter"><label class="hint">/&nbsp;</label></div>
                  <div class="year">
                    <c:if  test="${empty DOB_year}">
                      <c:set var="class" value="default"/>
                    </c:if>
                    <crs:yearList numberOfYears="100" bean="/atg/store/profile/RegistrationFormHandler.year" id="DOB_YearSelect"
                      selectRequired="false" yearString="true" onchange="CRSMA.myaccount.changeDOBDropdown(event)"
                      startYear="${currYear - 100}" iclass="${class}"/>
                    <label for="DOB_YearSelect"/> <%-- This makes VoiceOver to pausing before select element reading --%>
                  </div>
                  <dsp:input type="hidden" bean="RegistrationFormHandler.dateFormat" value="${dateFormat}"/>
                </div>
              </div>
            </li>
            <li>
              <div class="content">
                <div class="checkbox">
                  <%-- "Receive Emails" checkbox --%>
                  <dsp:droplet name="IsEmailRecipient">
                    <dsp:param name="email" bean="RegistrationFormHandler.value.email"/>
                    <dsp:oparam name="true">
                      <dsp:input bean="RegistrationFormHandler.previousOptInStatus" type="hidden" value="true"/>
                      <c:choose>
                        <c:when test="${empty formExceptions}">
                          <dsp:input id="receive_emails" type="checkbox" bean="RegistrationFormHandler.emailOptIn" checked="true"/>
                        </c:when>
                        <c:otherwise>
                          <dsp:input id="receive_emails" type="checkbox" bean="RegistrationFormHandler.emailOptIn"/>
                        </c:otherwise>
                      </c:choose>
                    </dsp:oparam>
                    <dsp:oparam name="false">
                      <dsp:input bean="RegistrationFormHandler.previousOptInStatus" type="hidden" value="false"/>
                      <c:choose>
                        <c:when test="${empty formExceptions}">
                          <dsp:input id="receive_emails" type="checkbox" bean="RegistrationFormHandler.emailOptIn" checked="false"/>
                        </c:when>
                        <c:otherwise>
                          <dsp:input id="receive_emails" type="checkbox" bean="RegistrationFormHandler.emailOptIn"/>
                        </c:otherwise>
                      </c:choose>
                    </dsp:oparam>
                  </dsp:droplet>
                  <span>
                    <fmt:message var="privacyTitle" key="mobile.company_privacyAndTerms.pageTitle"/>
                    <dsp:a page="${mobileStorePrefix}/company/terms.jsp" title="${privacyTitle}" class="icon-help"/>
                  </span>
                  <label for="receive_emails" onclick=""><fmt:message key="mobile.common.receiveEmails"/></label>
                </div>
              </div>
            </li>
          </ul>

          <%-- "Submit" button --%>
          <div class="centralButton">
            <fmt:message var="done" key="mobile.common.done"/>
            <dsp:input bean="RegistrationFormHandler.update" class="mainActionButton" type="submit" value="${done}"/>
          </div>
        </dsp:form>
      </div>
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @updated $DateTime: 2013/02/26 15:06:20 $$Author: shafez $ --%>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/myaccount/accountProfileEdit.jsp#4 $$Change: 793233 $--%>

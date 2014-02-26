<%--
  Renders content of credit card form, "Add Credit Card".

  Page includes:
    None

  Required parameters:
    formHandler
      Full path to form handler that will handle creating request
    cardParamsMap
      Form handler's map property that contains credit card properties

  Optional parameters:
    nicknameProperty
      Indicates where credit card "Nickname" property is saved
    showSaveCardOption
      Flag to show "Save Card to Profile" option
    showDefaultCardOption
      Flag to show "Default Card" option
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/StoreConfiguration"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="formHandler" param="formHandler"/>
  <dsp:getvalueof var="cardParamsMap" param="cardParamsMap"/>
  <dsp:getvalueof var="nicknameProperty" param="nicknameProperty"/>
  <dsp:getvalueof var="showSaveCardOption" param="showSaveCardOption"/>
  <dsp:getvalueof var="showDefaultCardOption" param="showDefaultCardOption"/>

  <%-- ========== Handle form exceptions ========== --%>
  <dsp:getvalueof var="formExceptions" bean="${formHandler}.formExceptions"/>
  <jsp:useBean id="errorMap" class="java.util.HashMap"/>
  <c:if test="${not empty formExceptions}">
    <c:forEach var="formException" items="${formExceptions}">
      <c:set var="errorCode" value="${formException.errorCode}"/>
      <c:set var="propertyName" value="${formException.propertyName}"/>
      <c:choose>
        <c:when test="${errorCode == 'missingRequiredValue'}">
          <c:set target="${errorMap}" property="${propertyName}" value="missing"/>
        </c:when>
        <c:when test="${errorCode == 'errorInvalidCreditCard'}">
          <c:set target="${errorMap}" property="${propertyName}" value="invalid"/>
        </c:when>
        <c:when test="${errorCode == 'errorDuplicateCCNickname'}">
          <c:set target="${errorMap}" property="${propertyName}" value="duplicate"/>
        </c:when>
      </c:choose>
    </c:forEach>
  </c:if>

  <div class="dataContainer">
    <ul class="dataList">
      <%-- "Nickname" --%>
      <li ${not empty errorMap['creditCardNickname'] ? 'class="errorState"' : ''}>
        <div class="content">
          <fmt:message var="nicknameLabel" key="mobile.common.nickName"/>
          <c:set var="nicknameTarget">
            <c:choose>
              <c:when test="${nicknameProperty == 'map'}">${cardParamsMap}</c:when>
              <c:otherwise>${formHandler}</c:otherwise>
            </c:choose>
          </c:set>
          <label for="newCardNickname"/> <%-- This makes VoiceOver to pausing before select element reading --%>
          <dsp:input bean="${cardParamsMap}.creditCardNickname" class="text" type="text" maxlength="42"
                     id="newCardNickname" required="true" placeholder="${nicknameLabel}" aria-label="${nicknameLabel}"/>
        </div>
        <c:if test="${not empty errorMap['creditCardNickname']}">
          <span class="errorMessage">
            <fmt:message key="mobile.form.validation.${errorMap['creditCardNickname']}"/>
          </span>
        </c:if>
      </li>

      <%-- "Type" --%>
      <dsp:getvalueof var="type" bean="${cardParamsMap}.creditCardType"/>
      <%-- Retrieve the list of supported credit card types from StoreConfiguration component --%>
      <dsp:getvalueof var="supportedCreditCardTypes" bean="StoreConfiguration.supportedCreditCardTypes"/>
      <li class="icon-ArrowLeft ${not empty errorMap['creditCardType'] ? 'errorState' : ''}">
        <div class="content">
          <label for="paymentInfoAddNewCardCardType"></label> <%-- This makes VoiceOver to pausing before select element reading --%>
          <dsp:select id="paymentInfoAddNewCardCardType" bean="${cardParamsMap}.creditCardType"
                      required="true" iclass="${empty type ? 'default' : ''}" onchange="CRSMA.myaccount.changeDropdown(event)" role="listbox">
            <dsp:option value="" iclass="default" role="option"><fmt:message key="mobile.card.type"/></dsp:option>

            <%-- Display supported credit card types as options --%>
            <c:forEach var="creditCardType" items="${supportedCreditCardTypes}">
              <dsp:option value="${creditCardType}" role="option"><fmt:message key="mobile.card.${creditCardType}"/></dsp:option>
            </c:forEach>
          </dsp:select>
        </div>
        <c:if test="${not empty errorMap['creditCardType']}">
          <span class="errorMessage">
            <fmt:message key="mobile.form.validation.${errorMap['creditCardType']}"/>
          </span>
        </c:if>
      </li>

      <%-- "Number" --%>
      <li ${not empty errorMap['creditCardNumber'] ? 'class="errorState"' : ''}>
        <div class="content">
          <fmt:message var="cardNumberLabel" key="mobile.card.cardNumber"/>
          <dsp:input type="tel" bean="${cardParamsMap}.creditCardNumber" autocomplete="off" required="true"
                     maxlength="19" placeholder="${cardNumberLabel}" aria-label="${cardNumberLabel}"/>
        </div>
        <c:if test="${not empty errorMap['creditCardNumber']}">
          <span class="errorMessage">
            <fmt:message key="mobile.form.validation.${errorMap['creditCardNumber']}"/>
          </span>
        </c:if>
      </li>

      <%-- "Expiration Month/Year" --%>
      <dsp:getvalueof var="month" bean="${cardParamsMap}.expirationMonth"/>
      <dsp:getvalueof var="year" bean="${cardParamsMap}.expirationYear"/>
      <li ${not empty errorMap['expirationMonth'] || not empty errorMap['expirationYear'] ? 'class="errorState"' : ''}>
        <div class="left40">
          <div class="content">
            <label class="hint left"><fmt:message key="mobile.common.expirationDate"/></label>
          </div>
        </div>
        <div class="right60">
          <div class="content">
            <div class="month">
              <dsp:select id="cardExpirationDateMonthSelect" bean="${cardParamsMap}.expirationMonth" required="true"
                          iclass="${empty month ? 'default' : ''}" onchange="CRSMA.myaccount.changeDropdown(event)">
                <dsp:option value="">
                  <fmt:message key="mobile.common.month"/>
                </dsp:option>
                <%-- Display months --%>
                <c:forEach var="count" begin="1" end="12" step="1">
                  <dsp:option value="${count}"><fmt:message key="mobile.common.month${count}"/></dsp:option>
                </c:forEach>
              </dsp:select>
              <label for="cardExpirationDateMonthSelect"/> <%-- This makes VoiceOver to pausing before select element reading --%>
            </div>

            <div class="dateDelimiter"><label class="hint">/&nbsp;</label></div>

            <div class="year">
              <crs:yearList numberOfYears="16" bean="${cardParamsMap}.expirationYear" id="cardExpirationDateYearSelect"
                           selectRequired="true" yearString="true" onchange="CRSMA.myaccount.changeDropdown(event)"
                           iclass="${empty year ? 'default' : ''}"/>
              <label for="cardExpirationDateYearSelect"/> <%-- This makes VoiceOver to pausing before select element reading --%>
            </div>
          </div>
          <c:choose>
            <c:when test="${not empty errorMap['expirationMonth']}">
              <span class="errorMessage"><fmt:message key="mobile.form.validation.${errorMap['expirationMonth']}"/></span>
            </c:when>
            <c:when test="${not empty errorMap['expirationYear']}">
              <span class="errorMessage"><fmt:message key="mobile.form.validation.${errorMap['expirationYear']}"/></span>
            </c:when>
          </c:choose>
        </div>
      </li>

      <%-- If the shopper is a guest shopper, we don't offer to save the card --%>
      <c:if test="${isLoggedIn}">
        <c:if test="${showSaveCardOption == 'true'}">
          <%-- "Save created credit card to profile" --%>
          <li class="clear">
            <div class="content">
              <dsp:input type="checkbox" bean="${cardParamsMap}.saveCreditCard" checked="true" id="saveCreditCard"/>
              <label for="saveCreditCard" onclick=""><fmt:message key="mobile.checkout_billing.savePaymentInfor"/></label>
            </div>
          </li>
        </c:if>
        <c:if test="${showDefaultCardOption == 'true'}">
          <%-- "Default card" --%>
          <li class="clear">
            <div class="content">
              <%-- Check if "Default card" checkbox should initially be checked --%>
              <dsp:getvalueof var="creditCards" bean="Profile.creditCards"/>
              <dsp:getvalueof var="targetCardKey" vartype="java.lang.String" bean="${formHandler}.editCard"/>
              <dsp:getvalueof var="userCards" vartype="java.util.Map" bean="Profile.creditCards"/>
              <dsp:getvalueof var="defaultCardId" vartype="java.lang.String" bean="Profile.defaultCreditCard.id"/>
              <dsp:input type="checkbox" bean="${cardParamsMap}.newCreditCard" id="defaultCreditCard"
                         checked="${empty creditCards or (defaultCardId == userCards[targetCardKey].repositoryId)}"/>
              <label for="defaultCreditCard" onclick=""><fmt:message key="mobile.myaccount_paymentInfoCardAddEdit.defaultCard"/></label>
            </div>
          </li>
        </c:if>
      </c:if>
    </ul>

    <%-- "Submit" button --%>
    <div class="centralButton">
      <fmt:message var="submitLabel" key="mobile.common.button.continueText"/>
      <dsp:input bean="${formHandler}.storeNewCreditCardDataWithoutAddress" type="submit" class="mainActionButton" value="${submitLabel}"/>
    </div>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/creditcard/gadgets/creditCardAddForm.jsp#3 $$Change: 794655 $--%>

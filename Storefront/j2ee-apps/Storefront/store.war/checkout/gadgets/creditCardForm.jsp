<%--
  This gadget renders input fields, necessary to create new credit card. It should 
  be used to render the billing page.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/store/StoreConfiguration"/>
  <dsp:importbean bean="/atg/store/order/purchase/BillingFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <%-- Credit Card Nickname Input --%>
  <ul class="atg_store_basicForm atg_store_addNewCreditCard">
    <li>
      <label for="atg_store_nickNameInput" class="atg_store_cardNickName">
        <fmt:message key="common.nicknameThisCard"/>
      </label>

      <dsp:input bean="BillingFormHandler.creditCardNickname" type="text" 
                 maxlength="42" id="atg_store_nickNameInput"/>
    </li>

    <%-- Credit Card Type Input --%>
    <li class="atg_store_cardType">
      <label for="atg_store_cardTypeSelect">
        <fmt:message key="common.cardType"/><span class="required">*</span>
      </label>
      <dsp:select bean="BillingFormHandler.creditCardType" id="atg_store_cardTypeSelect"
                  title="${creditCardTypeTitle}">
        
        <%-- Retrieve the list of supported credit card types from StoreConfiguration component. --%>
        <dsp:getvalueof var="supportedCreditCardTypes" bean="StoreConfiguration.supportedCreditCardTypes" />
        
        <dsp:option value=""><fmt:message key="common.chooseCardType"/></dsp:option>
        
        <%-- Display supported credit card types as options --%>
        <c:forEach var="creditCardType" items="${supportedCreditCardTypes}">
          <dsp:option value="${creditCardType}"><fmt:message key="common.${creditCardType}"/></dsp:option>
        </c:forEach>
        
      </dsp:select>
    </li>

    <%--  Credit Card Number Input --%>
    <li class="atg_store_ccNumber">
      <label for="atg_store_cardNumberInput">
        <fmt:message key="common.cardNumber"/><span class="required">*</span>
      </label>
      <dsp:input bean="BillingFormHandler.creditCardNumber" type="text"
                 maxlength="16" id="atg_store_cardNumberInput" autocomplete="off"/>
    </li>

    <%--  Expiration Date Select --%>
    <li class="atg_store_expiration">
      
      <label for="atg_store_expirationDateMonthSelect" class="required">
        <fmt:message key="common.expirationDate"/><span class="required">*</span>
      </label>
      
      <div class="atg_store_ccExpiration">
        
        <%-- Expiration month. --%>
        <fmt:message var="expirationMonthTitle" key="checkout_creditCardForm.expirationMonthTitle"/>
        
        <dsp:select bean="BillingFormHandler.creditCardExpirationMonth" id="atg_store_expirationDateMonthSelect"
                    title="${expirationMonthTitle}">
          <dsp:option>
            <fmt:message key="common.month"/>
          </dsp:option>

          <%-- Display months --%>
          <c:forEach var="count" begin="1" end="12" step="1" varStatus="status">
            <dsp:option value="${count}">
              <fmt:message key="common.month${count}"/>
            </dsp:option>
          </c:forEach>
        </dsp:select>

        <%-- Expiration year. --%>
        <fmt:message var="expirationYearTitle" key="checkout_creditCardForm.expirationYearTitle"/>
        
        <crs:yearList numberOfYears="16" bean="/atg/store/order/purchase/BillingFormHandler.creditCardExpirationYear"
                      id="atg_store_expirationDateYearSelect" title="${expirationYearTitle}"/>
      </div>
    </li>

    <%-- CVC/CCV verification number input field. --%>
    <li class="atg_store_ccCsvCode">
      
      <dsp:getvalueof var="requireCreditCardVerification" vartype="java.lang.Boolean" 
                      bean="StoreConfiguration.requireCreditCardVerification"/>
      
      <c:if test="${requireCreditCardVerification}">
        
        <label for="atg_store_verificationNumberInput">
          <fmt:message key="checkout_billing.securityCode"/><span class="required">*</span>
        </label>
        
        <dsp:input bean="BillingFormHandler.newCreditCardVerificationNumber" type="text"
                   name="atg_store_verificationNumberInput" id="atg_store_verificationNumberInput" 
                   autocomplete="off" value=""/>

        <fmt:message var="whatisThisTitle" key="checkout_billing.whatIsThis"/>
        
        <a href="${pageContext.request.contextPath}/checkout/whatsThisPopup.jsp" 
           title="${whatisThisTitle}" class="atg_store_help" target="popup">
          ${whatisThisTitle}
        </a>
      
      </c:if>
    </li>

    <%-- If the shopper is transient (a guest shopper) we don't offer to save the card --%>
    <dsp:getvalueof var="transient" bean="Profile.transient"/>
    
    <%-- Save this card checkbox. Hide if the profile is transient --%>
    <li class="atg_store_saveCC option" style="${transient ? 'display: none;' : ''}">
     
      <dsp:input type="checkbox" bean="BillingFormHandler.saveCreditCard" name="addCard" 
                 iclass="checkbox" checked="${!transient}" id="atg_store_ccAddSaveccInput"/> 
      
      <label for="atg_store_ccAddSaveccInput"><fmt:message key="checkout_billing.savePaymentInfor"/></label>
    
    </li>
  </ul>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/creditCardForm.jsp#4 $$Change: 788842 $--%>

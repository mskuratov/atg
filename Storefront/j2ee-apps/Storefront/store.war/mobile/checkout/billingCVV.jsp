<%--
  This page fragment renders user's payment methods for selections.

  Page includes:
    None

  Required parameters:
    dispatchCSV
      Specifies card operation that was used before CSV page appearance.
      This parameter should also be posted with CSV-code to help CRS-M billing-handler
      choose appropriate method of CRS-billing handler for finalize billing. Possible values:
        'newCardSelectedAddress' = new credit card with address, selected from the list
        'newCardNewAddress'      = new credit card with the new billable address 
        'selectCard'             = credit card was selected from the list

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/mobile/order/purchase/MobileBillingFormHandler"/>
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>

  <%-- Apply available store credits to the order --%>
  <dsp:setvalue bean="MobileBillingFormHandler.applyStoreCreditsToOrder" value=""/>

  <%--
    If there are errors during form submition (we entered invalid CVV) the current order is invalidated
    and doesn't contain priceInfo. Will reprice whole order in this case
  --%>
  <dsp:getvalueof var="formExceptions" bean="MobileBillingFormHandler.formExceptions"/>
  <jsp:useBean id="errorMap" class="java.util.HashMap"/>
  <%-- Prepares credit card number and type in handler properties --%>
  <dsp:setvalue bean="MobileBillingFormHandler.prepareCreditCardNumberAndType" value=""/>
  
  <c:if test="${not empty formExceptions}">
    <c:forEach var="formException" items="${formExceptions}">
      <c:set var="errorCode" value="${formException.errorCode}"/>
      <c:choose>
        <c:when test="${errorCode == 'missingRequiredValue' || errorCode == 'invalidCreditCardVerificationNumber'}">
          <c:set target="${errorMap}" property="cvvCodeField" value="invalid"/>
        </c:when>
      </c:choose>
    </c:forEach>

    <dsp:droplet name="RepriceOrderDroplet">
      <dsp:param name="pricingOp" value="ORDER_TOTAL"/>
    </dsp:droplet>
  </c:if>

  <fmt:message var="pageTitle" key="mobile.checkout_billing.billingInformation"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <jsp:body>
      <div class="storeCheckout">
        <h2>
          <dsp:getvalueof var="creditCardType" bean="MobileBillingFormHandler.creditCardType"/>
          <c:out value="${creditCardType}"/>
          <fmt:message key="mobile.common.ellipsis"/>
          <dsp:getvalueof var="creditCardNumber" bean="MobileBillingFormHandler.creditCardNumber"/>
          <c:set var="creditCardNumberLength" value="${fn:length(creditCardNumber)}"/>
          <c:out value="${fn:substring(creditCardNumber,creditCardNumberLength-4,creditCardNumberLength)}"/>
        </h2>

        <%-- ========== Form ========== --%>
        <dsp:form formid="billingCVV" action="${siteContextPath}/checkout/billingCVV.jsp" method="post">
          <%-- Set dispatchCSV parameter to the billing handler --%>
          <dsp:input type="hidden" bean="MobileBillingFormHandler.dispatchCSV" paramvalue="dispatchCSV"/>

          <%-- Set "storedCreditCardName" (used only when existing card was selected --%>
          <dsp:input type="hidden" bean="MobileBillingFormHandler.storedCreditCardName"/>
          
          <%-- Set "storedAddressSelection" (used only when existing address was selected while creating of new credit card --%>
          <dsp:input type="hidden" bean="MobileBillingFormHandler.storedAddressSelection"/>

          <div class="cvvCode">
            <div class="${creditCardType}">
              <%-- Include hidden form params --%>
              <dsp:getvalueof var="dispatchCSV" param="dispatchCSV"/>
              <dsp:input type="hidden" value="confirm.jsp" bean="MobileBillingFormHandler.moveToConfirmSuccessURL"/>
              <dsp:input type="hidden" value="billingCVV.jsp?dispatchCSV=${dispatchCSV}" bean="MobileBillingFormHandler.moveToConfirmErrorURL"/>
              <dsp:input type="hidden" value="login.jsp" bean="MobileBillingFormHandler.sessionExpirationURL"/>
              <dsp:input type="hidden" value="billingWithSavedCard" bean="MobileBillingFormHandler.moveToConfirm" name="submit" priority="-10"/>

              <%-- "Coupon code" --%>
              <dsp:getvalueof var="couponCode" bean="CouponFormHandler.couponCode"/>
              <dsp:input bean="CouponFormHandler.couponCode" priority="10" type="hidden" value="${couponCode}"/>

              <fmt:message key="mobile.checkout_billing.securityCode" var="securityCodeText"/>
              <dsp:input class="cvvCodeField" type="tel" value="" maxlength="4" placeholder="${securityCodeText}" aria-label="${securityCodeText}"
                         bean="MobileBillingFormHandler.newCreditCardVerificationNumber"/>
              <c:if test="${not empty errorMap['cvvCodeField']}">
                <span id="formValidationError">
                  <fmt:message key="mobile.form.validation.${errorMap['cvvCodeField']}"/>
                </span>
              </c:if>
              <p>
                <c:choose>
                  <c:when test="${creditCardType == 'visa'}">
                    <fmt:message key="mobile.checkout_whatsThisPopup.instructions.visa"/>
                  </c:when>
                  <c:otherwise>
                    <fmt:message key="mobile.checkout_whatsThisPopup.instructions.americanExpress"/>
                  </c:otherwise>
                </c:choose>
              </p>

              <%-- "Submit" button --%>
              <br/>
              <div class="centralButton">
                <fmt:message var="submitBtnValue" key="mobile.common.button.continueText"/>
                <dsp:input bean="MobileBillingFormHandler.moveToConfirm" type="submit" class="mainActionButton" value="${submitBtnValue}"/>
              </div>
            </div>
          </div>
        </dsp:form>
      </div>

      <script>
        $(document).ready(function(event) {
          var $continueBtn = $(".mainActionButton");
          $continueBtn.hide();
          var cvvCodeLastValue = "";
          var $cvvCode = $(".cvvCodeField");
          $cvvCode.keyup(function(e) {
            var text = $.trim($cvvCode.val());
            if (text != cvvCodeLastValue) {
              if (text.length > 0) {
                $continueBtn.show();
              } else {
                $continueBtn.hide();
              }
              cvvCodeLastValue = text;
            }
          });
        });
      </script>
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/checkout/billingCVV.jsp#5 $$Change: 794114 $--%>

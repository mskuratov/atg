<%--
  This page fragment renders list of credit cards to select ("Pay With" step of "Checkout").

  Page includes:
    /mobile/global/gadgets/errorMessage.jsp - Displays all errors collected from FormHandler
    /mobile/creditcard/gadgets/savedCreditCards.jsp - List of saved credit cards

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>
  <dsp:importbean bean="/atg/store/mobile/order/purchase/MobileBillingFormHandler"/>
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>

  <fmt:message var="pageTitle" key="mobile.checkout_billing.billingInformation"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <jsp:body>
      <div class="dataContainer">
        <h2><span><fmt:message key="mobile.checkout_confirmPaymentOptions.payment"/>:</span></h2>

        <%-- Display "MobileBillingFormHandler" error messages, which were specified in "formException.message" --%>
        <dsp:include page="${mobileStorePrefix}/global/gadgets/errorMessage.jsp">
          <dsp:param name="formHandler" bean="MobileBillingFormHandler"/>
        </dsp:include>

        <%-- ========== Form ========== --%>
        <dsp:form formid="selectCreditCard" action="${siteContextPath}/checkout/billingCVV.jsp?dispatchCSV=selectCard" method="post">
          <%-- ========== Redirection URLs ========== --%>
          <dsp:droplet name="ProfileSecurityStatus">
            <dsp:oparam name="anonymous">
              <dsp:input type="hidden" name="sessionExpirationURL" bean="MobileBillingFormHandler.sessionExpirationURL"
                         value="${siteContextPath}"/>
            </dsp:oparam>
            <dsp:oparam name="default">
              <dsp:input type="hidden" name="sessionExpirationURL" bean="MobileBillingFormHandler.sessionExpirationURL"
                         value="${siteContextPath}/checkout/login.jsp"/>
            </dsp:oparam>
          </dsp:droplet>

          <dsp:include page="${mobileStorePrefix}/creditcard/gadgets/savedCreditCards.jsp">
            <dsp:param name="page" value="checkout"/>
            <dsp:param name="selectable" value="true"/>
            <dsp:param name="displayDefaultLabeled" value="false"/>
            <dsp:param name="selectProperty" value="MobileBillingFormHandler.storedCreditCardName"/>
          </dsp:include>

          <%-- "Coupon code" --%>
          <dsp:getvalueof var="couponCode" bean="CouponFormHandler.currentCouponCode"/>
          <dsp:input bean="CouponFormHandler.couponCode" priority="10" type="hidden" value="${couponCode}"/>
        </dsp:form>
      </div>

      <script>
        $(document).ready(function() {
          CRSMA.global.delayedSubmitSetup("#creditCardList");
        });
      </script>
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/checkout/selectCreditCard.jsp#4 $$Change: 794114 $--%>

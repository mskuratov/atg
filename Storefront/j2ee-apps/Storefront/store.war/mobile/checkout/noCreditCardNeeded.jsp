<%--
  Displays a message telling the user that their order was entirely paid for by
  store credit and therefore no credit card is needed.

  Page includes:
    None

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/mobile/order/purchase/MobileBillingFormHandler"/>

  <fmt:message var="pageTitle" key="mobile.checkout_billing.billingInformation"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <jsp:body>
      <div class="storeCheckout">
        <dsp:form formid="billingCVV" action="${siteContextPath}/checkout/billingCVV.jsp" method="post">
          <dsp:getvalueof var="dispatchCSV" param="dispatchCSV"/>
          <br/>
          <h4 align="center">
            <%-- Display 'Order's total is 0' message --%>
            <fmt:message key="checkout_billing.yourOrderTotal"/>
            <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <dsp:param name="price" value="0"/>
            </dsp:include>
          </h4>
          <br/>
          <p align="center">
            <fmt:message key="checkout_billing.yourOrderTotalMessage"/>
          </p>
          <br/>
          <div class="centralButton">
            <dsp:input type="hidden" value="${siteContextPath}/checkout/confirm.jsp" bean="MobileBillingFormHandler.moveToConfirmSuccessURL"/>
            <fmt:message var="submitBtnValue" key="mobile.common.button.continueText"/>
            <dsp:input bean="MobileBillingFormHandler.moveToConfirm" type="submit" class="mainActionButton" value="${submitBtnValue}"/>
          </div>
        </dsp:form>
      </div>
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/checkout/noCreditCardNeeded.jsp#3 $$Change: 794114 $--%>

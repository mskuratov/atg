<%--
  Renders all stored addresses to select as Billing one for created credit card.
  This page is the next step after "newCreditCard.jsp".

  Page includes:
    /mobile/myaccount/gadgets/subheaderAccounts.jsp - Display subheader items
    /mobile/myaccount/gadgets/billingAddressesList.jsp - List of available Billing addresses to select

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/MobileProfileFormHandler"/>

  <fmt:message var="pageTitle" key="mobile.myaccount_payment_subHeader.addCard"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <jsp:body>
      <%-- ========== Subheader ========== --%>
      <fmt:message var="subheader_CenterText" key="mobile.myaccount_myAccountMenu.paymentInfo"/>
      <dsp:include page="gadgets/subheaderAccounts.jsp">
        <dsp:param name="centerText" value="${subheader_CenterText}"/>
        <dsp:param name="centerURL" value="${mobileStorePrefix}/myaccount/selectCreditCard.jsp"/>
        <dsp:param name="rightText" value="${pageTitle}"/>
        <dsp:param name="highlight" value="right"/>
      </dsp:include>

      <div class="dataContainer">
        <%-- ========== Form ========== --%>
        <dsp:form action="${pageContext.request.requestURI}" method="post">
          <%-- ========== Redirection URLs ========== --%>
          <dsp:input type="hidden" bean="MobileProfileFormHandler.createCardSuccessURL" value="selectCreditCard.jsp"/>
          <dsp:input type="hidden" bean="MobileProfileFormHandler.createCardErrorURL" value="newCreditCard.jsp"/>

          <dsp:include page="gadgets/billingAddressesList.jsp"/>

          <dsp:input type="hidden" bean="MobileProfileFormHandler.createNewCreditCard" value="createNewCreditCard" priority="-10"/>
        </dsp:form>

        <script>
          $(document).ready(function() {
            CRSMA.global.delayedSubmitSetup("#savedBillingAddresses");
          });
        </script>
      </div>
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/myaccount/creditCardAddressSelect.jsp#3 $$Change: 788278 $--%>

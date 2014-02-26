<%--
  Renders "Create new credit card" page.

  Page includes:
    /mobile/myaccount/gadgets/subheaderAccounts.jsp - Display subheader items
    /mobile/creditcard/gadgets/creditCardAddForm.jsp - Renderer of credit card add form

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/MobileProfileFormHandler"/>

  <fmt:message key="mobile.myaccount_payment_subHeader.addCard" var="pageTitle"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <jsp:body>
      <%-- ========== Subheader ========== --%>
      <fmt:message var="paymentInfoTitle" key="mobile.myaccount_myAccountMenu.paymentInfo"/>
      <dsp:include page="gadgets/subheaderAccounts.jsp">
        <dsp:param name="centerText" value="${paymentInfoTitle}"/>
        <dsp:param name="centerURL" value="${mobileStorePrefix}/myaccount/selectCreditCard.jsp"/>
        <dsp:param name="rightText" value="${pageTitle}"/>
        <dsp:param name="highlight" value="right"/>
      </dsp:include>

      <div>
        <%-- ========== Form ========== --%>
        <dsp:form formid="newCreditCard" action="${pageContext.request.requestURI}" method="post">
          <%-- ========== Redirection URLs ========== --%>
          <dsp:input type="hidden" bean="MobileProfileFormHandler.createCardSuccessURL" value="creditCardAddressSelect.jsp"/>
          <dsp:input type="hidden" bean="MobileProfileFormHandler.createCardErrorURL" value="newCreditCard.jsp"/>

          <%-- Include "creditCardAddForm.jsp" to render credit card properties --%>
          <dsp:include page="../creditcard/gadgets/creditCardAddForm.jsp">
            <dsp:param name="formHandler" value="/atg/userprofiling/MobileProfileFormHandler"/>
            <dsp:param name="cardParamsMap" value="/atg/userprofiling/MobileProfileFormHandler.editValue"/>
            <dsp:param name="nicknameProperty" value="map"/>
            <dsp:param name="showDefaultCardOption" value="true"/>
          </dsp:include>
        </dsp:form>
      </div>
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/myaccount/newCreditCard.jsp#3 $$Change: 788278 $--%>

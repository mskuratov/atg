<%--
  Credit card detail page: Edit mode.

  Page includes:
    /mobile/myaccount/gadgets/subheaderAccounts.jsp - Display subheader items
    /mobile/creditcard/gadgets/creditCardEditForm.jsp credit - Card edit form

  Required parameters:
    page
      'myaccount' - This page is called in "My Account" context
      'checkout' - This page is called in "Checkout" context

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/MobileProfileFormHandler"/>

  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="page" param="page"/>

  <fmt:message var="pageTitle" key="mobile.myaccount_payment_subHeader.editCard"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <jsp:attribute name="modalContent">
      <dsp:getvalueof var="cardNickname" bean="MobileProfileFormHandler.editValue.nickname"/>
      <div class="moveDialog">
        <div class="moveItems">
          <ul class="dataList">
            <li class="remove">
              <fmt:message key="mobile.common.delete" var="removeText"/>
              <fmt:message key="mobile.myaccount_storedCreditCards.removeCard" var="removeCard"/>
              <c:set var="removeCardSuccessUrl" value="${siteContextPath}/${page}/selectCreditCard.jsp"/>
              <dsp:a title="${removeCard}" bean="MobileProfileFormHandler.removeCard" value="${cardNickname}"
                     id="removeLink" iclass="icon-Remove" href="${removeCardSuccessUrl}">${removeText}</dsp:a>
            </li>
          </ul>
        </div>
      </div>
    </jsp:attribute>

    <jsp:body>
      <%-- ========== Subheader ========== --%>
      <c:if test="${page == 'myaccount'}">
        <fmt:message var="paymentInfoTitle" key="mobile.myaccount_myAccountMenu.paymentInfo"/>
        <dsp:include page="${mobileStorePrefix}/myaccount/gadgets/subheaderAccounts.jsp">
          <dsp:param name="centerText" value="${paymentInfoTitle}"/>
          <dsp:param name="centerURL" value="${mobileStorePrefix}/myaccount/selectCreditCard.jsp"/>
          <dsp:param name="rightText" value="${pageTitle}"/>
          <dsp:param name="highlight" value="right"/>
        </dsp:include>
      </c:if>

      <div class="dataContainer">
        <%-- ========== Form ========== --%>
        <dsp:form formid="editCreditCard" action="${pageContext.request.requestURI}" method="post">
          <%-- ========== Redirection URLs ========== --%>
          <c:set var="successURL">
            <c:if test="${page == 'myaccount'}">${siteContextPath}/myaccount/selectCreditCard.jsp</c:if>
            <c:if test="${page == 'checkout'}">${siteContextPath}/checkout/billing.jsp</c:if>
          </c:set>
          <c:url value="${pageContext.request.requestURI}" var="errorURL" context="/">
            <c:param name="page" value="${page}"/>
          </c:url>
          <dsp:input type="hidden" bean="MobileProfileFormHandler.updateCardSuccessURL" value="${successURL}"/>
          <dsp:input type="hidden" bean="MobileProfileFormHandler.updateCardErrorURL" value="${errorURL}"/>

          <%-- Include "creditCardEditForm.jsp" to render credit card properties --%>
          <dsp:include page="gadgets/creditCardEditForm.jsp">
            <dsp:param name="page" value="${page}"/>
          </dsp:include>
        </dsp:form>
      </div>
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/creditcard/editCreditCard.jsp#2 $$Change: 788278 $--%>

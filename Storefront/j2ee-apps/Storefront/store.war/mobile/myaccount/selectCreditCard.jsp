<%--
  This page renders user's credit cards list ("My Account / Credit Cards").

  Page includes:
    /mobile/myaccount/gadgets/subheaderAccounts.jsp - Display subheader items
    /mobile/global/gadgets/savedCreditCards.jsp - List of saved credit cards

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <fmt:message var="pageTitle" key="mobile.myaccount_myAccountMenu.paymentInfo"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <jsp:body>
      <%-- ========== Subheader ========== --%>
      <dsp:include page="gadgets/subheaderAccounts.jsp">
        <dsp:param name="centerText" value="${pageTitle}"/>
        <dsp:param name="highlight" value="center"/>
      </dsp:include>

      <div class="dataContainer">
        <dsp:include page="${mobileStorePrefix}/creditcard/gadgets/savedCreditCards.jsp">
          <dsp:param name="page" value="myaccount"/>
          <dsp:param name="selectable" value="false"/>
          <dsp:param name="displayDefaultLabeled" value="true"/>
        </dsp:include>
      </div>
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/myaccount/selectCreditCard.jsp#1 $$Change: 742374 $--%>

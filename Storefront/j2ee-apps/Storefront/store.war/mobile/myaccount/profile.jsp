<%--
  This page displays an overview of the logged in user "Profile information".
  It includes links to edit "Personal Information", "Change Password", "View Orders", "View Addresses",
  "View Credit Cards".

  Page includes:
    /mobile/myaccount/gadgets/subheaderAccounts.jsp - Display subheader items

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
  <dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst"/>
  <dsp:importbean bean="/atg/dynamo/servlet/RequestLocale"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <fmt:message var="pageTitle" key="mobile.myaccount_accountInformation.title"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <%-- ========== Subheader ========== --%>
    <dsp:include page="gadgets/subheaderAccounts.jsp"/>

    <div class="dataContainer">
      <ul class="dataList" role="presentation">
        <li>
          <div class="left30">
            <%-- "Logout" --%>
            <fmt:message var="logout" key="mobile.common.logout"/>
            <dsp:getvalueof id="userLocale" vartype="java.lang.String" bean="RequestLocale.locale"/>
            <dsp:a page="/" title="${logout}">
              <span class="content">
                <dsp:property bean="ProfileFormHandler.logoutSuccessURL" value="mobile/myaccount/login.jsp?locale=${userLocale}"/>
                <dsp:property bean="ProfileFormHandler.logout" value="true"/>
                <c:out value="${logout}"/>
              </span>
            </dsp:a>
          </div>
          <div class="right70">
            <%-- "Change Password" --%>
            <dsp:a page="${mobileStorePrefix}/myaccount/profilePasswordEdit.jsp" class="icon-ArrowRight">
              <span class="content"><fmt:message key="mobile.myaccount_changePassword.text"/></span>
            </dsp:a>
          </div>
        </li>
        <li>
          <%-- "Personal Information" --%>
          <dsp:a page="${mobileStorePrefix}/myaccount/accountProfileEdit.jsp" class="icon-ArrowRight">
            <span class="content">
              <fmt:message key="mobile.myaccount_profileMyInfo.myInformation"/>
              <span class="formFieldText">
                <dsp:valueof bean="Profile.firstName"/>
                <dsp:valueof bean="Profile.lastName"/><br/>
                <dsp:valueof bean="Profile.email"/><br/>
                <dsp:getvalueof var="postalCode" bean="Profile.homeAddress.postalCode"/>
                <c:if test="${not empty postalCode}">
                  ${postalCode}
                </c:if>
              </span>
            </span>
          </dsp:a>
        </li>
        <li>
          <%-- "Orders" --%>
          <dsp:a page="${mobileStorePrefix}/myaccount/myOrders.jsp" class="icon-ArrowRight">
            <span class="content">
              <fmt:message key="mobile.myaccount_myAccountMenu.myOrders"/><fmt:message key="mobile.common.labelSeparator"/>
              <span class="formFieldMajorText">
                <%-- Total number of orders for the user --%>
                <dsp:droplet name="OrderLookup">
                  <dsp:param name="userId" bean="Profile.id"/>
                  <dsp:param name="state" value="closed"/>
                  <dsp:param name="queryTotalOnly" value="true"/>
                  <dsp:param name="numOrders" value="-1"/>
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="counterOrder" param="totalCount"/>
                    <fmt:message key="mobile.myaccount_accountInformation.numberOrdersSaved">
                      <fmt:param value="${counterOrder}"/>
                    </fmt:message>
                  </dsp:oparam>
                </dsp:droplet>
              </span>
            </span>
          </dsp:a>
        </li>
        <li>
          <%-- "Addresses" --%>
          <dsp:a page="${mobileStorePrefix}/address/addressBook.jsp" class="icon-ArrowRight">
            <span class="content">
              <fmt:message key="mobile.myaccount_accountInformation.addresses"/>
              <span class="formFieldMajorText">
                <%-- Total number of the user's addresses --%>
                <dsp:droplet name="MapToArrayDefaultFirst">
                  <dsp:param name="defaultId" bean="Profile.shippingAddress.repositoryId"/>
                  <dsp:param name="map" bean="Profile.secondaryAddresses"/>
                  <dsp:oparam name="empty">
                    <fmt:message key="mobile.myaccount_accountInformation.numberAddressesSaved">
                      <fmt:param value="0"/>
                    </fmt:message>
                  </dsp:oparam>
                  <dsp:oparam name="output">
                    <c:set var="counterAddress" value="0"/>
                    <dsp:getvalueof var="sortedArray" param="sortedArray"/>
                    <c:forEach var="shippingAddress" items="${sortedArray}">
                      <dsp:setvalue param="shippingAddress" value="${shippingAddress}"/>
                      <c:if test="${not empty shippingAddress}">
                        <c:set var="counterAddress" value="${counterAddress + 1}"/>
                      </c:if>
                    </c:forEach>
                    <%-- Make text dynamic --%>
                    <fmt:message key="mobile.myaccount_accountInformation.numberAddressesSaved">
                      <fmt:param value="${counterAddress}"/>
                    </fmt:message>
                  </dsp:oparam>
                </dsp:droplet>
              </span>
            </span>
          </dsp:a>
        </li>
        <li>
          <%-- "Credit Cards" --%>
          <dsp:a page="${mobileStorePrefix}/myaccount/selectCreditCard.jsp" class="icon-ArrowRight">
            <span class="content">
              <fmt:message key="mobile.myaccount_accountInformation.cc"/>
              <span class="formFieldMajorText">
                <%-- Get the total number of Credit Cards for the user --%>
                <dsp:droplet name="MapToArrayDefaultFirst">
                  <dsp:param name="defaultId" bean="Profile.defaultCreditCard.repositoryId"/>
                  <dsp:param name="map" bean="Profile.creditCards"/>
                  <dsp:oparam name="empty">
                    <fmt:message key="mobile.myaccount_accountInformation.numberCreditCardsSaved">
                      <fmt:param value="0"/>
                    </fmt:message>
                  </dsp:oparam>
                  <dsp:oparam name="output">
                    <c:set var="counterCard" value="0"/>
                    <dsp:getvalueof var="sortedArray" param="sortedArray"/>
                    <c:forEach var="creditCard" items="${sortedArray}">
                      <dsp:setvalue param="creditCard" value="${creditCard}"/>
                      <c:if test="${not empty creditCard}">
                        <c:set var="counterCard" value="${counterCard + 1}"/>
                      </c:if>
                    </c:forEach>
                    <fmt:message key="mobile.myaccount_accountInformation.numberCreditCardsSaved">
                      <fmt:param value="${counterCard}"/>
                    </fmt:message>
                  </dsp:oparam>
                </dsp:droplet>
              </span>
            </span>
          </dsp:a>
        </li>
      </ul>
    </div>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @updated $DateTime: 2013/02/18 05:52:29 $$Author: dvolakh $ --%>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/myaccount/profile.jsp#4 $$Change: 791011 $--%>

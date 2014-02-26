<%--
  This page serves adding/editing of Shipping/Billing addresses for "My Account".

  Page includes:
    /mobile/myaccount/gadgets/subheaderAccounts.jsp - Display subheader items
    /mobile/address/gadgets/addressAddEdit.jsp - Renders address form (except "Nickname" field)
    /mobile/includes/crsRedirect.jspf - Renderer of the redirect prompt to the full CRS site

  Required parameters:
    addrOper
      'edit' = edit address
      'add' = add address
    addrType
      'shipping' = add/edit Shipping address
      'billing' = add/edit Billing address

  Optional parameters:
    cardOper
      'edit' = edit credit card
      'add' = add credit card
      NOTE: this parameter is only valid for "Edit Billing Address"
    editAddrNickname
      "Nickname" of the address that is being edited
    deletable
      Flag to mark address item as deletable.
      NOTE: this parameter is only valid for "Edit Shipping Address"
--%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/MobileProfileFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="editAddrNickname" param="editAddrNickname"/>
  <dsp:getvalueof var="addrType" param="addrType"/>
  <dsp:getvalueof var="addrOper" param="addrOper"/>
  <dsp:getvalueof var="cardOper" param="cardOper"/>
  <dsp:getvalueof var="deletable" param="deletable"/>

  <%-- FormHandler "address" property name --%>
  <c:set var="addrPropertyName">
    <c:choose>
      <c:when test="${cardOper == 'add' && addrOper == 'add'}">/atg/userprofiling/MobileProfileFormHandler.billAddrValue</c:when>
      <c:otherwise>/atg/userprofiling/MobileProfileFormHandler.editValue</c:otherwise>
    </c:choose>
  </c:set>

  <%-- ========== Handle form exceptions ========== --%>
  <dsp:getvalueof var="formExceptions" bean="MobileProfileFormHandler.formExceptions"/>
  <jsp:useBean id="errorMap" class="java.util.HashMap"/>
  <c:if test="${not empty formExceptions}">
    <c:forEach var="formException" items="${formExceptions}">
      <c:set var="errorCode" value="${formException.errorCode}"/>
      <c:choose>
        <c:when test="${errorCode == 'errorDuplicateNickname'}">
          <c:set target="${errorMap}" property="nickname" value="inUse"/>
        </c:when>
        <c:when test="${errorCode == 'stateIsIncorrect'}">
          <c:set target="${errorMap}" property="state" value="invalid"/>
          <c:set target="${errorMap}" property="country" value="invalid"/>
        </c:when>
        <c:when test="${errorCode == 'missingRequiredValue'}">
          <c:set var="propertyName" value="${formException.propertyName}"/>
          <%--
            "Nickname" may have different names: "nickname", "newNickname"
          --%>
          <c:if test="${propertyName == 'newNickname'}">
            <c:set var="propertyName" value="nickname"/>
          </c:if>
          <c:set target="${errorMap}" property="${propertyName}" value="missing"/>
        </c:when>
      </c:choose>
    </c:forEach>
  </c:if>

  <fmt:message var="pageTitle" key="mobile.myaccount_accountAddressEdit.title"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <jsp:attribute name="modalContent">
      <c:if test="${addrType == 'shipping' && addrOper == 'edit'}">
        <c:choose>
          <c:when test="${deletable == 'true'}">
            <%-- "Remove address" modal dialog template --%>
            <div class="moveDialog">
              <div class="moveItems">
                <ul class="dataList">
                  <li class="remove">
                    <fmt:message key="mobile.checkout_shippingAddressEdit.deleteText" var="deleteText"/>
                    <dsp:a title="${deleteText}" bean="MobileProfileFormHandler.removeAddress" value="${editAddrNickname}"
                           href="addressBook.jsp" id="removeLink" iclass="icon-Remove">
                      <fmt:message key="mobile.common.delete"/>
                    </dsp:a>
                  </li>
                </ul>
              </div>
            </div>
          </c:when>
          <c:otherwise>
            <%-- Modal dialog when address can't be deleted due to gift list redirect for Full CRS --%>
            <div id="modalMessageBox">
              <div id="modalInclude">
                <c:set var="successURL" value="addressBook.jsp"/>
                <fmt:message var="topString" key="mobile.myaccount_addresses.redirectTopString"/>
                <fmt:message var="bottomString" key="mobile.myaccount_addresses.redirectBottomString"/>
                <%@include file="../includes/crsRedirect.jspf"%>
              </div>
            </div>
          </c:otherwise>
        </c:choose>
      </c:if>
    </jsp:attribute>

    <jsp:body>
      <%-- ========== Subheader ========== --%>
      <fmt:message var="subheader_RightText">
        <c:if test="${addrType == 'shipping' && addrOper == 'edit'}">mobile.myaccount_accountAddressEdit.title</c:if>
        <c:if test="${addrType == 'shipping' && addrOper == 'add'}">mobile.myAccount.checkoutDefaults.addAddress</c:if>
        <c:if test="${addrType == 'billing' && cardOper == 'edit'}">mobile.myaccount_payment_subHeader.editCard</c:if>
        <c:if test="${addrType == 'billing' && cardOper == 'add'}">mobile.myaccount_payment_subHeader.addCard</c:if>
      </fmt:message>
      <fmt:message var="subheader_CenterText">
        <c:if test="${addrType == 'shipping'}">mobile.myaccount_addressBook.title</c:if>
        <c:if test="${addrType == 'billing'}">mobile.myaccount_myAccountMenu.paymentInfo</c:if>
      </fmt:message>
      <c:set var="subheader_CenterURL">
        <c:if test="${addrType == 'shipping'}">${mobileStorePrefix}/address/addressBook.jsp</c:if>
        <c:if test="${addrType == 'billing'}">${mobileStorePrefix}/myaccount/selectCreditCard.jsp</c:if>
      </c:set>
      <dsp:include page="${mobileStorePrefix}/myaccount/gadgets/subheaderAccounts.jsp">
        <dsp:param name="centerText" value="${subheader_CenterText}"/>
        <dsp:param name="centerURL" value="${subheader_CenterURL}"/>
        <dsp:param name="rightText" value="${subheader_RightText}"/>
        <dsp:param name="highlight" value="right"/>
      </dsp:include>

      <div class="dataContainer">
        <%-- ========== Form ========== --%>
        <dsp:form action="${pageContext.request.requestURI}" method="post">
          <%-- ========== Redirection URLs ========== --%>
          <c:url value="${pageContext.request.requestURI}" var="errorURL" context="/">
            <c:param name="addrOper" value="${addrOper}"/>
            <c:param name="addrType" value="${addrType}"/>
            <c:param name="cardOper" value="${cardOper}"/>
            <c:if test="${addrOper == 'edit'}">
              <c:param name="editAddrNickname" value="${editAddrNickname}"/>
              <c:param name="deletable" value="${deletable}"/>
            </c:if>
          </c:url>
          <c:set var="successURL">
            <c:choose>
              <c:when test="${addrType == 'billing'}">
                <c:if test="${cardOper == 'add'}">
                  <c:if test="${addrOper == 'edit'}">${siteContextPath}/myaccount/creditCardAddressSelect.jsp</c:if>
                  <c:if test="${addrOper == 'add'}">${siteContextPath}/myaccount/selectCreditCard.jsp</c:if>
                </c:if>
              </c:when>
              <c:otherwise>addressBook.jsp</c:otherwise> <%-- Shipping address --%>
            </c:choose>
          </c:set>
          <dsp:input type="hidden" bean="MobileProfileFormHandler.successURL" value="${successURL}"/>
          <dsp:input type="hidden" bean="MobileProfileFormHandler.errorURL" value="${errorURL}"/>

          <ul class="dataList">
            <%-- Display "Nickname" for specific contexts only --%>
            <c:if test="${!(addrType == 'billing' && addrOper == 'add' && cardOper == 'edit')}">
              <%-- "Nickname" --%>
              <li ${not empty errorMap['nickname'] ? 'class="errorState"' : ''}>
                <div class="content">
                  <fmt:message var="nickPlace" key="mobile.common.addressNickname"/>
                  <c:if test="${addrOper == 'edit'}">
                    <dsp:input type="hidden" bean="${addrPropertyName}.nickname"/>
                    <dsp:input type="text" bean="${addrPropertyName}.newNickname" maxlength="42" required="true"
                               placeholder="${nickPlace}" aria-label="${nickPlace}"/>
                  </c:if>
                  <c:if test="${addrOper == 'add' && addrType == 'shipping'}">
                    <dsp:input type="text" bean="${addrPropertyName}.nickname" maxlength="42" required="true"
                               placeholder="${nickPlace}" aria-label="${nickPlace}"/>
                  </c:if>
                  <c:if test="${addrOper == 'add' && addrType == 'billing'}">
                    <dsp:input type="text" bean="${addrPropertyName}.shippingAddrNickname" maxlength="42" required="true"
                               placeholder="${nickPlace}" aria-label="${nickPlace}"/>
                  </c:if>
                </div>
                <c:if test="${not empty errorMap['nickname']}">
                  <span class="errorMessage">
                    <fmt:message key="mobile.form.validation.${errorMap['nickname']}"/>
                  </span>
                </c:if>
              </li>
            </c:if>

            <%-- Include "addressAddEdit.jsp" to render address properties --%>
            <dsp:include page="gadgets/addressAddEdit.jsp">
              <dsp:param name="formHandlerComponent" value="${addrPropertyName}"/>
              <dsp:param name="restrictionDroplet" value="/atg/store/droplet/ShippingRestrictionsDroplet"/>
              <dsp:param name="errorMap" value="${errorMap}"/>
            </dsp:include>

            <%-- Checkbox: "Make this my default address" --%>
            <c:if test="${addrType == 'shipping'}">
              <li>
                <dsp:getvalueof var="defaultAddressId" bean="Profile.shippingAddress.repositoryId"/>
                <dsp:getvalueof var="currentAddressId" bean="${addrPropertyName}.addressId"/>
                <div class="content">
                  <div class="checkBox">
                    <dsp:input type="checkbox" name="useShippingAddressAsDefault" id="addresses_useShippingAddressAsDefault"
                               bean="MobileProfileFormHandler.useShippingAddressAsDefault"
                               checked="${defaultAddressId == currentAddressId}"/>
                    <label for="addresses_useShippingAddressAsDefault" onclick="">
                      <fmt:message key="mobile.myaccount_addresses.default"/>
                    </label>
                  </div>
                </div>
              </li>
            </c:if>

            <%-- "Delete" button --%>
            <c:if test="${addrType == 'shipping' && addrOper == 'edit'}">
              <li>
                <div class="deleteContainer">
                  <fmt:message var="deleteText" key="mobile.checkout_shippingAddressEdit.deleteText"/>
                  <div onclick="${deletable ? 'CRSMA.global.removeItemDialog($(this).parent())' : 'CRSMA.global.toggleCantDeleteAddressDialog()'}" title="${deleteText}" class="icon-Remove">
                    ${deleteText}
                  </div>
                </div>
              </li>
            </c:if>
          </ul>

          <%-- Hidden input: "Make this my default address" --%>
          <c:if test="${addrType == 'billing' && addrOper == 'edit' && cardOper == 'edit'}">
            <%-- "Edit Credit Card / Edit Billing Address" --%>
            <dsp:getvalueof var="defaultAddressId" bean="Profile.shippingAddress.repositoryId"/>
            <dsp:getvalueof var="currentAddressId" bean="${addrPropertyName}.addressId"/>
            <dsp:input type="hidden" bean="MobileProfileFormHandler.useShippingAddressAsDefault"
                       value="${defaultAddressId == currentAddressId}"/>
          </c:if>

          <%-- "Submit" button --%>
          <div class="centralButton">
            <c:set var="submitBtnHandleMethod">
              <c:if test="${addrType == 'shipping' && addrOper == 'edit'}">MobileProfileFormHandler.updateAddress</c:if>
              <c:if test="${addrType == 'shipping' && addrOper == 'add'}">MobileProfileFormHandler.newAddress</c:if>
              <c:if test="${addrType == 'billing'  && cardOper == 'add'  && addrOper == 'add'}">MobileProfileFormHandler.createNewCreditCardAndAddress</c:if>
              <c:if test="${addrType == 'billing'  && cardOper == 'add'  && addrOper == 'edit'}">MobileProfileFormHandler.updateAddress</c:if>
              <c:if test="${addrType == 'billing'  && cardOper == 'edit' && addrOper == 'add'}">MobileProfileFormHandler.newAddress</c:if>
              <c:if test="${addrType == 'billing'  && cardOper == 'edit' && addrOper == 'edit'}">MobileProfileFormHandler.updateAddress</c:if>
            </c:set>
            <fmt:message var="submitBtnValue" key="mobile.common.button.saveAddressText"/>
            <dsp:input bean="${submitBtnHandleMethod}" class="mainActionButton" type="submit" value="${submitBtnValue}"/>
          </div>
        </dsp:form>
      </div>
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/address/myaccountAddressDetail.jsp#3 $$Change: 788278 $--%>

<%--
  Context: "Checkout / New Credit Card / Select Billing Address".
  This page is the next step after "newCreditCard.jsp".

  Page includes:
    /mobile/checkout/gadgets/billingAddressesList.jsp - List of saved Billing addresses
    /mobile/address/gadgets/displayAddress.jsp - Renderer of address info

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupContainerService"/>
  <dsp:importbean bean="/atg/store/droplet/AvailableBillingAddresses"/>
  <dsp:importbean bean="/atg/store/mobile/order/purchase/MobileBillingFormHandler"/>
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <%-- Get Profile "Default Shipping address" --%>
  <dsp:getvalueof var="defaultAddr" bean="Profile.shippingAddress"/>

  <fmt:message var="pageTitle" key="mobile.myAccount.checkoutDefaults.addCreditCard"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <jsp:body>
      <div class="dataContainer">
        <h2><fmt:message key="mobile.myaccount_paymentInfoCardAddEdit.billingAddress"/></h2>

        <%-- ========== Form ========== --%>
         <dsp:form action="${siteContextPath}/checkout/billingCVV.jsp?dispatchCSV=newCardSelectedAddress" method="post">
          <%-- "Coupon code" --%>
          <dsp:getvalueof var="couponCode" bean="CouponFormHandler.currentCouponCode"/>
          <dsp:input bean="CouponFormHandler.couponCode" priority="10" type="hidden" value="${couponCode}"/>

          <%-- ========== Render Billing addresses list ========== --%>
          <dsp:getvalueof var="shippingGroupMap" bean="ShippingGroupContainerService.shippingGroupMap"/>
          <c:if test="${not empty shippingGroupMap}">
            <dsp:droplet name="AvailableBillingAddresses">
              <dsp:param name="map" value="${shippingGroupMap}"/>
              <c:if test="${not empty defaultAddr}"> <%-- "Default Shipping address" might NOT be set --%>
                <dsp:param name="defaultId" value="${defaultAddr.repositoryId}"/>
              </c:if>
              <dsp:param name="sortByKeys" value="true"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="permittedAddresses" param="permittedAddresses"/>
              </dsp:oparam>
            </dsp:droplet>
          </c:if>
          <c:if test="${not empty permittedAddresses}">
            <ul id="savedBillingAddresses" class="dataList">
              <c:forEach var="billingAddress" items="${permittedAddresses}">
                <li onclick="">
                  <dsp:getvalueof var="shippingGroupAddress" value="${billingAddress.value.shippingAddress}"/>
                  <dsp:getvalueof var="addrNickname" value="${billingAddress.key}"/>
                  <div class="content">
                    <dsp:input type="radio" name="storedAddressSelection" value="${addrNickname}"
                               id="${shippingGroupAddress.repositoryItem.repositoryId}"
                               checked="false" bean="MobileBillingFormHandler.storedAddressSelection"/>
                    <label for="${shippingGroupAddress.repositoryItem.repositoryId}">
                      <dsp:include page="${mobileStorePrefix}/address/gadgets/displayAddress.jsp">
                        <dsp:param name="address" value="${shippingGroupAddress}"/>
                        <dsp:param name="isPrivate" value="false"/>
                      </dsp:include>
                    </label>
                  </div>

                  <%-- Link to "Edit Billing Address" --%>
                  <span class="storedAddressActions">
                    <fmt:message var="addrEditTitle" key="mobile.myaccount_accountAddressEdit.title"/>
                    <c:url value="${mobileStorePrefix}/address/checkoutAddressDetail.jsp" var="editURL">
                      <c:param name="editAddrNickname" value="${addrNickname}"/>
                      <c:param name="addrOper" value="edit"/>
                      <c:param name="cardOper" value="add"/>
                      <c:param name="addrType" value="billing"/>
                    </c:url>
                    <dsp:a href="${editURL}" title="${addrEditTitle}" class="icon-BlueArrow"/>
                  </span>
                </li>
              </c:forEach>

              <%-- Link to "Add Billing Address" --%>
              <li id="newItemLI">
                <dsp:a page="../address/checkoutNewBillingAddressDetail.jsp?cardOper=add" class="icon-ArrowRight">
                  <span class="content"><fmt:message key="mobile.common.newBillingAddress"/></span>
                </dsp:a>
              </li>
            </ul>
          </c:if>
        </dsp:form>
      </div>

      <script>
        $(document).ready(function() {
          CRSMA.global.delayedSubmitSetup("#savedBillingAddresses");
        });
      </script>
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/checkout/creditCardAddressSelect.jsp#4 $$Change: 794114 $--%>

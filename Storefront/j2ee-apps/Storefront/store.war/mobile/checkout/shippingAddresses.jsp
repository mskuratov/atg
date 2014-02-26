<%--
  Renders list of Shipping addresses for selection.

  Page includes:
    /mobile/address/gadgets/displayAddress.jsp - Address info renderer
    /mobile/global/gadgets/errorMessage.jsp - Displays all errors collected from FormHandler

  Required parameters:
    permittedAddresses
      List of shipping permitted addresses

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <fmt:message var="pageTitle" key="mobile.common.shipping"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <jsp:body>
      <div class="dataContainer">
        <h2><fmt:message key="mobile.checkout_shipping.chooseShippingAddress"/></h2>

        <%-- Display "ShippingGroupFormHandler" error messages, which were specified in "formException.message" --%>
        <dsp:include page="${mobileStorePrefix}/global/gadgets/errorMessage.jsp">
          <dsp:param name="formHandler" bean="ShippingGroupFormHandler"/>
        </dsp:include>

        <%-- ========== Form ========== --%>
        <dsp:form formid="shippingAddresses" action="${pageContext.request.requestURI}" method="post">
          <%-- ========== Redirection URLs ========== --%>
          <%--
            If user is "anonymous" and the session has expired: the cart looses its contents.
            Then the page gets redirected to the home page.
            Otherwise, it will be redirected to the "Checkout" login page.
          --%>
          <dsp:droplet name="ProfileSecurityStatus">
            <dsp:oparam name="anonymous">
              <dsp:input type="hidden" bean="ShippingGroupFormHandler.sessionExpirationURL" value="${siteContextPath}"/>
            </dsp:oparam>
            <dsp:oparam name="default">
              <dsp:input type="hidden" bean="ShippingGroupFormHandler.sessionExpirationURL" value="${siteContextPath}/checkout/login.jsp"/>
            </dsp:oparam>
          </dsp:droplet>

          <dsp:input type="hidden" bean="ShippingGroupFormHandler.address.email" beanvalue="Profile.email"/>

          <dsp:input type="hidden" bean="ShippingGroupFormHandler.shipToExistingAddressSuccessURL" value="shippingMethod.jsp"/>
          <dsp:input type="hidden" bean="ShippingGroupFormHandler.shipToExistingAddressErrorURL" value="shipping.jsp"/>

          <%-- "Coupon code" --%>
          <dsp:getvalueof var="couponCode" bean="CouponFormHandler.currentCouponCode"/>
          <dsp:input bean="CouponFormHandler.couponCode" priority="10" type="hidden" value="${couponCode}"/>

          <ul id="savedAddresses" class="dataList">
            <c:forEach var="shippingGroupEntry" items="${permittedAddresses}" varStatus="status">
              <li>
                <dsp:getvalueof var="shippingAddr" value="${shippingGroupEntry.value.shippingAddress}"/>
                <dsp:getvalueof var="shippingAddrKey" value="${shippingGroupEntry.key}"/>
                <dsp:getvalueof var="shippingAddrId" value="${shippingAddr.repositoryItem.repositoryId}"/>
                <div class="content">
                  <dsp:input type="radio" name="address" value="${shippingAddrKey}" id="${shippingAddrId}" checked="false"
                             bean="ShippingGroupFormHandler.shipToAddressName"/>
                  <label for="${shippingAddrId}" onclick="">
                    <dsp:include page="${mobileStorePrefix}/address/gadgets/displayAddress.jsp">
                      <dsp:param name="address" value="${shippingAddr}"/>
                      <dsp:param name="isPrivate" value="false"/>
                    </dsp:include>
                  </label>

                  <%-- If this shipping group is gift shipping group? --%>
                  <c:set var="description" value="${shippingGroupEntry.value.description}"/>
                  <dsp:getvalueof var="giftPrefix" bean="/atg/commerce/gifts/GiftlistManager.giftShippingGroupDescriptionPrefix"/>
                  <c:if test="${!(fn:startsWith(description, giftPrefix))}">
                    <%-- Link to "Edit Shipping Address" --%>
                    <dsp:getvalueof var="shipToAddressName" bean="ShippingGroupFormHandler.shipToAddressName"/>
                    <c:url value="${mobileStorePrefix}/address/checkoutAddressDetail.jsp" var="editURL">
                      <c:param name="addrOper" value="edit"/>
                      <c:param name="addrType" value="shipping"/>
                      <c:param name="editAddrNickname" value="${shippingAddrKey}"/>
                      <c:param name="shipToAddressName" value="${shipToAddressName}"/>
                    </c:url>
                    <fmt:message var="editText" key="mobile.checkout_shipping.edit"/>
                    <dsp:a href="${editURL}" title="${editText}" class="icon-BlueArrow"/>
                  </c:if>
                </div>
              </li>
            </c:forEach>

            <%-- Link to "Add Shipping Address" --%>
            <li id="newItemLI">
              <dsp:a page="${mobileStorePrefix}/address/checkoutAddressDetail.jsp" class="icon-ArrowRight">
                <dsp:param name="addrOper" value="add"/>
                <dsp:param name="addrType" value="shipping"/>
                <span class="content"><fmt:message key="mobile.checkout_shippingAddresses.createShippingAddress"/></span>
              </dsp:a>
            </li>
          </ul>

          <%-- Submit fires handleShipToExistingAddress() --%>
          <dsp:input type="hidden" bean="ShippingGroupFormHandler.shipToExistingAddress" value="moveToBilling"/>
        </dsp:form>

        <script>
          $(document).ready(function() {
            CRSMA.global.delayedSubmitSetup("#savedAddresses");
          });
        </script>
      </div>
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/checkout/shippingAddresses.jsp#3 $$Change: 788278 $--%>

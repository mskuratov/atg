<%--
  Renders list of billing addresses in "My Account" contexts.

  Page includes:
    /mobile/address/gadgets/displayAddress.jsp - Renderer of address info

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/MobileProfileFormHandler"/>
  <dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <dsp:getvalueof var="mobileStorePrefix" bean="/atg/store/StoreConfiguration.mobileStorePrefix"/>

  <%-- Get Profile "Default Shipping address" --%>
  <dsp:getvalueof var="defaultAddr" bean="Profile.shippingAddress"/>

  <ul id="savedBillingAddresses" class="dataList">
    <%--
      Iterate through all the user's secondary addresses, sorting the array so that default address is first.

      Input parameters:
        defaultId
          Repository Id of item that will be the first in the array
        map
          Map of repository items that will be converted into array
        sortByKeys
          Returning array will be sorted by keys (address "Nickname")

      Output parameters:
        sortedArray
          Array of sorted profile secondary addresses
    --%>
    <dsp:droplet name="MapToArrayDefaultFirst">
      <dsp:param name="map" bean="Profile.secondaryAddresses"/>
      <c:if test="${not empty defaultAddr}"> <%-- "Default Shipping address" might NOT be set --%>
        <dsp:param name="defaultId" value="${defaultAddr.repositoryId}"/>
      </c:if>
      <dsp:oparam name="output">
        <dsp:getvalueof var="sortedArray" param="sortedArray"/>
        <c:if test="${not empty sortedArray}">
          <c:forEach var="shippingAddress" items="${sortedArray}">
            <dsp:param name="shippingAddress" value="${shippingAddress}"/>
            <c:if test="${not empty shippingAddress}">
              <li onclick="">
                <div class="content">
                  <dsp:getvalueof var="selectorValue" param="shippingAddress.key"/>
                  <dsp:droplet name="/atg/store/droplet/BillingRestrictionsDroplet">
                    <dsp:param name="countryCode" param="shippingAddress.value.country"/>
                    <dsp:oparam name="true">
                      <dsp:input type="radio" value="${selectorValue}" disabled="true"
                                 id="${shippingAddress.value.repositoryId}"
                                 bean="MobileProfileFormHandler.selectedBillingAddress"/>
                    </dsp:oparam>
                    <dsp:oparam name="false">
                      <dsp:input type="radio" value="${selectorValue}"
                                 id="${shippingAddress.value.repositoryId}"
                                 checked="false"
                                 bean="MobileProfileFormHandler.selectedBillingAddress"/>
                    </dsp:oparam>
                  </dsp:droplet>

                  <label for="${shippingAddress.value.repositoryId}" onclick="">
                    <dsp:include page="${mobileStorePrefix}/address/gadgets/displayAddress.jsp">
                      <dsp:param name="address" param="shippingAddress.value"/>
                      <dsp:param name="isPrivate" value="false"/>
                    </dsp:include>
                  </label>
                </div>

                <%-- Link to "Edit Billing Address" --%>
                <span class="storedAddressActions">
                  <fmt:message var="addrEditTitle" key="mobile.myaccount_accountAddressEdit.title"/>
                  <c:url value="${mobileStorePrefix}/address/myaccountAddressDetail.jsp" var="editURL">
                    <c:param name="editAddrNickname" value="${shippingAddress.key}"/>
                    <c:param name="addrOper" value="edit"/>
                    <c:param name="cardOper" value="add"/>
                    <c:param name="addrType" value="billing"/>
                  </c:url>
                  <dsp:a href="${editURL}" title="${addrEditTitle}" class="icon-BlueArrow"
                         bean="MobileProfileFormHandler.editAddress" paramvalue="shippingAddress.key"/>
                </span>
              </li>
            </c:if>
          </c:forEach>
        </c:if>
      </dsp:oparam>
    </dsp:droplet>

    <%-- Link to "Add Billing Address" --%>
    <li id="newItemLI">
      <c:url value="${mobileStorePrefix}/address/myaccountAddressDetail.jsp" var="newURL">
        <c:param name="addrOper" value="add"/>
        <c:param name="cardOper" value="add"/>
        <c:param name="addrType" value="billing"/>
      </c:url>
      <dsp:a href="${newURL}" class="icon-ArrowRight">
        <span class="content"><fmt:message key="mobile.common.newBillingAddress"/></span>
      </dsp:a>
    </li>
  </ul>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/myaccount/gadgets/billingAddressesList.jsp#3 $$Change: 788278 $--%>

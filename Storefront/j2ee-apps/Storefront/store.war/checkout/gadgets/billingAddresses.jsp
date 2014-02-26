<%--
  This gadget lists the user's saved billing addresses as options when adding a 
  new credit card during the checkout process.

  Required parameters:
    availableBillingAddresses
      An array of permitted nickname=HardgoodShippingGroup pairs (Map.Entrys) taken 
      from the ShippingGroupContainerService.shippingGroupMap.

  Optional parameters:
    None.
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/order/purchase/BillingFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupContainerService"/>  
  
  <dsp:getvalueof var="availableBillingAddresses" param="availableBillingAddresses"/>
  <dsp:getvalueof var="storedAddressSelection" vartype="java.lang.String" bean="BillingFormHandler.storedAddressSelection"/>

  <fmt:message var="savedAddressesOption" key="checkout_billing.savedAddresses"/>
  
  <%-- Render the addresses we may choose to bill to --%>
  <div id="atg_store_savedAddresses">
    <c:forEach var="billingAddress" items="${availableBillingAddresses}">
      
      <%-- The address details from the shippingGroup entry and the nickname --%>
      <dsp:getvalueof var="shippingGroupAddress" value="${billingAddress.value.shippingAddress}"/>
      <dsp:getvalueof var="shippingGroupNickname" value="${billingAddress.key}"/>
          
      <div class="atg_store_addressGroup">
        <dl class="atg_store_billingAddresses atg_store_savedAddresses">
          <dt>
            <%-- Remember previously selected address. --%>
            <dsp:input type="radio" name="address" value="${shippingGroupNickname}" 
                       id="${shippingGroupAddress.repositoryItem.repositoryId}"
                       checked="${shippingGroupNickname == storedAddressSelection}" 
                       bean="BillingFormHandler.storedAddressSelection"/>
                
            <%-- Address nickname --%>
            <label for="${shippingGroupAddress.repositoryItem.repositoryId}">
              <c:out value="${shippingGroupNickname}"/>
            </label>
          </dt>
              
          <%-- Address info --%>
          <dd class="atg_store_addressSelect">
            <dsp:include page="/global/util/displayAddress.jsp">
              <dsp:param name="address" value="${shippingGroupAddress}"/>
              <dsp:param name="private" value="false"/>
            </dsp:include>
          </dd>
              
          <%-- 
            Display 'Edit' and 'Remove' buttons. We dont need to filter out gift addresses here as 
            they are filtered out by the AvailableBillingAddresses droplet which generates our
            availableBillingAddresses
          --%>
          <dd class="atg_store_storedAddressActions">
            <ul>
              <li>
                <dsp:getvalueof var="successURL" vartype="java.lang.String" 
                                value="${pageContext.request.contextPath}/checkout/billing.jsp"/>
                
                <dsp:a bean="ProfileFormHandler.editAddress" page="../billingAddressEdit.jsp" 
                       value="${shippingGroupNickname}">
                  <dsp:param name="nickName" value="${shippingGroupNickname}"/>
                  <dsp:param name="successURL" value="${successURL}"/>
                  <fmt:message key="checkout_billing.Edit"/>
                </dsp:a>
              </li>
                  
              <li class="last">
                <fmt:message var="removeAddressTitle" key="common.button.deleteTitle"/>
                
                <dsp:a title="${removeAddressTitle}" bean="ProfileFormHandler.removeAddress"
                       href="${pageContext.request.requestURI}" value="${shippingGroupNickname}">
                  <span>
                    <fmt:message key="common.button.deleteText"/>
                  </span>
                </dsp:a>
              </li>
                  
            </ul>
          </dd>
        </dl>
      </div>
    </c:forEach>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/billingAddresses.jsp#2 $$Change: 788278 $--%>

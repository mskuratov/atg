<%-- 
  This gadget renders the user's saved shipping addresses list for selection.

  Required parameters:
    permittedAddresses - An array of nickname=HardgoodShippingGroup pairs (Map.Entrys) taken
                         from the ShippingGroupContainerService.shippingGroupMap

  Optional parameters:
    None.
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  
  <dsp:getvalueof var="permittedAddresses" vartype="java.lang.Object" param="permittedAddresses"/>
  <dsp:getvalueof var="shipToAddressName" vartype="java.lang.String" bean="ShippingGroupFormHandler.shipToAddressName"/>
  
  <c:if test="${not empty permittedAddresses}">
    <fieldset class="atg_store_chooseShippingAddresses">    
      <h3>
        <fmt:message key="checkout_shippingAddresses.selectShippingAddress"/>
      </h3>
    
      <div id="atg_store_savedAddresses">
      
        <%--
          For each entry in the permittedAddresses array display the nickname and contact info.
          An entry in the permittedAddresses array is a Map.Entry where the key is the address 
          nickname and the value is the address information.
        --%>
        <c:forEach var="shippingGroupEntry" items="${permittedAddresses}">
          <%-- Get the shipping group and its nickname from the map entry --%>
          <dsp:getvalueof var="shippingGroup" value="${shippingGroupEntry.value}"/>
          <dsp:getvalueof var="shippingGroupNickname" value="${shippingGroupEntry.key}"/>
          
          <div class="atg_store_addressGroup">                 
         
            <dl class="atg_store_savedAddresses">
              <dt>
                <dsp:input type="radio" name="address" value="${shippingGroupNickname}"
                           id="${shippingGroup.shippingAddress.repositoryItem.repositoryId}"
                           checked="${shippingGroupNickname == shipToAddressName}"
                           bean="ShippingGroupFormHandler.shipToAddressName"/>
                           
                <%-- Display Address Name --%>
                <label for="${shippingGroup.shippingAddress.repositoryItem.repositoryId}">
                  <dsp:valueof value="${shippingGroupNickname}"/>
                </label>
              </dt>
          
              <%-- Display address details. --%>
              <dd class="atg_store_addressSelect">
                <dsp:include page="/global/util/displayAddress.jsp">
                  <dsp:param name="address" value="${shippingGroup.shippingAddress}"/>
                  <dsp:param name="private" value="false"/>
                </dsp:include>
              </dd>
                        
              <%-- If its from the giftlist dont display the 'Edit' or 'Remove' buttons --%>
              <c:set var="description" value="${shippingGroup.description}"/>
              <dsp:getvalueof var="giftPrefix" bean="/atg/commerce/gifts/GiftlistManager.giftShippingGroupDescriptionPrefix"/>
              <c:if test="${!(fn:startsWith(description, giftPrefix))}">
                <dd class="atg_store_storedAddressActions">
                  <ul>
                    <fmt:message var="editAddressTitle" key="common.button.editAddressTitle"/>
                    <li class="<crs:listClass count="1" size="2" selected="false"/>">
                      <dsp:a page="/checkout/shippingAddressEdit.jsp" value="${shippingGroupNickname}">
                        <dsp:param name="nickName" value="${shippingGroupNickname}"/>
                        <dsp:param name="selectedAddress" bean="ShippingGroupFormHandler.shipToAddressName"/>
                        <dsp:param name="successURL" value="shipping.jsp"/>
                        <fmt:message key="checkout_shipping.edit"/>
                      </dsp:a>
                    </li>
                    
                    <fmt:message var="removeAddressTitle" key="common.button.deleteTitle"/>
                    <dsp:getvalueof id="requestURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
                    <li class='<crs:listClass count="2" size="2" selected="false"/>'>
                      <dsp:a title="${removeAddressTitle}" bean="ProfileFormHandler.removeAddress"
                             href="${requestURL}" value="${shippingGroupNickname}">
                        <span>
                          <fmt:message key="common.button.deleteText"/>
                        </span>
                      </dsp:a>
                    </li>
                  </ul>
                </dd>
              </c:if>
              
            </dl>                
          </div>
        </c:forEach>
      </div>
      
      <%-- Ship to this address button --%>
      <fmt:message var="shipToButtonText" key="checkout_shippingAddresses.button.shipToThisAddress"/>
      
      <%-- 
        Check if user is anonymous or the session has expired. If so set the sessionExpirationURL
    
        Open parameters:
          anonymous - user is not logged in
          default - user has been recognized
      --%>
      <dsp:droplet name="ProfileSecurityStatus">
        <dsp:oparam name="anonymous">
          <dsp:input type="hidden" bean="ShippingGroupFormHandler.sessionExpirationURL" 
                     value="${originatingRequest.contextPath}/index.jsp"/>
        </dsp:oparam>
    
        <dsp:oparam name="default">
          <dsp:input type="hidden" bean="ShippingGroupFormHandler.sessionExpirationURL" 
                     value="${originatingRequest.contextPath}/checkout/login.jsp"/>
        </dsp:oparam>
      </dsp:droplet>
        
      <dsp:input type="hidden" bean="ShippingGroupFormHandler.address.email" beanvalue="Profile.email"/>
      <dsp:input type="hidden" bean="ShippingGroupFormHandler.shipToExistingAddressSuccessURL" value="shippingMethod.jsp"/>
      <dsp:input type="hidden" bean="ShippingGroupFormHandler.shipToExistingAddressErrorURL" value="shipping.jsp"/>
    
      <div class="atg_store_saveSelectAddress atg_store_formActions">
        <span class="atg_store_basicButton">
          <dsp:input type="submit"  bean="ShippingGroupFormHandler.shipToExistingAddress" value="${shipToButtonText}"/>
        </span>
      </div>

    </fieldset>    
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/shippingAddresses.jsp#2 $$Change: 788278 $--%>

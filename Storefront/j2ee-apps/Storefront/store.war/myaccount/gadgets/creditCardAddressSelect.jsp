<%--
  This page renders all stored addresses for a registered user
  which could be selected to use with credit card
  
  Form Condition:
    this gadget must be contained inside of a form.
    
    ProfileFormHandler must be invoked from a submit
    button in this form for fields in this page to be processed
    
  
  Required parameters:
    None
    
  Optional parameters:
    None     
--%>
<dsp:page>

  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst"/>
  <dsp:importbean bean="/atg/store/droplet/BillingRestrictionsDroplet"/>

  <%-- Get Profile's default address --%>
  <dsp:getvalueof var="defaultAddress" bean="Profile.shippingAddress"/>

  <%-- 
    Iterate through all this user's shipping addresses, sorting the array so that the
    default shipping address is first.
    
    Input parameters:
      defaultId
        repository Id of item that will be the first in the array
      map
        map of repository items that will be converted into array
      sortByKeys
        returning array will be sorted by keys (address nicknames)
        
    Output parameters:
      sortedArray
        array of sorted profile addresses 
  --%>
  <dsp:droplet name="MapToArrayDefaultFirst">
    <dsp:param name="map" bean="Profile.secondaryAddresses"/>
    <dsp:param name="defaultId" value="${defaultAddress.repositoryId}"/>
    
    <dsp:oparam name="output">
      <dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/>
  
      <div id="atg_store_selectAddress"> 
        <fieldset class="atg_store_chooseBillingAddresses">
          <h3>
            <fmt:message key="common.useSavedAddressAsBillingAddress"/>
          </h3>
        
          <div id="atg_store_savedAddresses">
            
            <c:forEach var="shippingAddress" items="${sortedArray}" varStatus="status">
                       
              <dsp:param name="shippingAddress" value="${shippingAddress}"/>
              <%-- 
                BillingRestrictionsDroplet is used below to disable radio buttons
                for the case where shippable addresses are restricted for billing
              --%>
              <div class="atg_store_addressGroup">
                <dl class="atg_store_savedAddresses">
                  <dt>
                    <dsp:getvalueof var="addressKey" vartype="java.util.String" param="shippingAddress.key"/>
                    
                    <%--
                      Check for shipping restrictions
                      
                      Input parameters:
                        countryCode
                          address' country code
                      
                      Output parameters:
                        'true' oparam rendered if this address could be used
                        to ship to, otherwise false
                     --%>
                    <dsp:droplet name="BillingRestrictionsDroplet">
                      <dsp:param name="countryCode" param="shippingAddress.value.country"/>
                      <dsp:oparam name="true">
                        <dsp:input type="radio" name="address" paramvalue="shippingAddress.key"
                                   id="${shippingAddress.value.repositoryId}" disabled="true"
                                   bean="ProfileFormHandler.billAddrValue.newNickname"/>
                      </dsp:oparam>
                      <dsp:oparam name="false">
                        
                        <%-- Automatically check the first address --%>
                        <dsp:input type="radio" name="address" paramvalue="shippingAddress.key"
                                   id="${shippingAddress.value.repositoryId}" 
                                   checked="${status.count == 1}"
                                   bean="ProfileFormHandler.billAddrValue.newNickname"/>
                      </dsp:oparam>
                    </dsp:droplet>

                    <%-- Display address nickname --%>
                    <label for="${shippingAddress.value.repositoryId}">
                      <dsp:valueof param="shippingAddress.key"/>
                    </label>
                  </dt>
                  
                  <%-- Display address details --%>
                  <dd class="atg_store_addressSelect">                
                    <dsp:include page="/global/util/displayAddress.jsp">
                      <dsp:param name="address" param="shippingAddress.value"/>
                      <dsp:param name="private" value="false"/>
                    </dsp:include>
                  </dd>
                  
                  <%-- Display Edit/Remove Links --%>
                  <dd class="atg_store_storedAddressActions">
                    <ul>
                      <fmt:message var="editAddressTitle" key="common.button.editAddressTitle"/>
                      
                      <%-- 'Edit Address' link --%>
                      <li class="<crs:listClass count="1" size="2" selected="false"/>">
                        <dsp:a title="${editAddressTitle}"
                               bean="ProfileFormHandler.editAddress"
                               page="/myaccount/accountAddressEdit.jsp" paramvalue="shippingAddress.key">
                          <dsp:param name="successURL" bean="/OriginatingRequest.requestURI"/>
                          <dsp:param name="addEditMode" value="edit"/>
                          <span><fmt:message key="common.button.editAddressText"/></span>
                        </dsp:a>
                      </li>
                      
                      <fmt:message var="removeAddressTitle" key="myaccount_addressBookDefault.button.removeAddressTitle"/>
                      <dsp:getvalueof id="requestURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
                      
                      <%-- 'Remove Address' link --%>
                      <li class="<crs:listClass count="2" size="2" selected="false"/>">
                        <dsp:a title="${removeAddressTitle}"
                               bean="ProfileFormHandler.removeAddress"
                               href="${requestURL}" paramvalue="shippingAddress.key">
                         <span><fmt:message key="common.button.deleteText"/></span>
                        </dsp:a>
                      </li>
                    </ul>
                  </dd> 
                </dl>
              </div>
            </c:forEach>
          </div>
           <div class="atg_store_saveSelectAddress atg_store_formActions">
              <fmt:message var="submitText" key="common.button.saveCardText"/>

              <%-- 'Create New Card' button --%>
              <span class="atg_store_basicButton">
                <dsp:input type="submit" value="${submitText}" 
                           id="atg_store_paymentInfoAddNewCardSubmit"
                           bean="ProfileFormHandler.createNewCreditCard"/>
              </span>

              <p>
                <fmt:message key="checkout_billing.usingSavedAddress" />
              </p>
            </div>
        </fieldset>
        
       
      </div>
    </dsp:oparam>
  </dsp:droplet>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/creditCardAddressSelect.jsp#2 $$Change: 788278 $--%>

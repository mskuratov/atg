<%--
  Page for managing a user's saved addresses (their address book).
  Displays available addresses for the current profile.
  
  Required parameters:
    None
    
  Optional parameters:
    None 
--%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst"/>

  <crs:pageContainer divId="atg_store_accountEditProfileIntro" 
                     index="false" follow="false" 
                     bodyClass="atg_store_myAccountPage atg_store_leftCol">    
    <jsp:body>
      
      <%-- Page title --%>
      <div id="atg_store_contentHeader">
        <h2 class="title">
          <fmt:message key="myaccount_addressBook.title"/>
        </h2>
      </div>
      
      <%-- Left-hand menu with highlighted 'Address Book' menu item --%>
      <dsp:include page="gadgets/myAccountMenu.jsp">
        <dsp:param name="selpage" value="ADDRESS BOOK" />
      </dsp:include>
  
      <div class="atg_store_myAccount atg_store_main">
      
        <%-- Display errors if any --%>
        <div id="atg_store_formValidationError">        
          <dsp:include page="/global/gadgets/errorMessage.jsp">
            <dsp:param name="formHandler" bean="ProfileFormHandler"/>
            <dsp:param name="divid" value="errorMessage"/>
          </dsp:include>
        </div>

        <!-- Begin saved shipping addresses -->
        <div id="atg_store_addressBookDefault" class="atg_store_savedAddresses">
          <dsp:getvalueof id="requestURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
        
          <%-- Get Profile's default address --%>
          <dsp:getvalueof var="defaultAddress" bean="Profile.shippingAddress"/>
          
          <%-- 
            Iterate through all this user's shipping addresses, sorting the array so that the
            default shipping address is first.
            
            Input parameters:
              defaultId
                repository Id of item that will be the first in the array
              map
                Map of repository items that will be converted into array
              sortByKeys
                returning array will be sorted by keys (address nicknames)
                
            Output parameters:
              sortedArray
                array of sorted profile addresses    
          --%>
          <dsp:droplet name="MapToArrayDefaultFirst">
            <dsp:param name="defaultId" value="${defaultAddress.repositoryId}"/>
            <dsp:param name="map" bean="Profile.secondaryAddresses"/>
            <dsp:param name="sortByKeys" value="true"/>
            
            <dsp:oparam name="empty">
              <%--
                If no saved addresses, offer to create a new address
               --%>
              <crs:messageContainer titleKey="myaccount_addressBookDefault.noShippingAddress">
                <jsp:body>
                  <div class="atg_store_formActions">
                    <!-- Link to Add a New Address page -->
                    <dsp:a page="/myaccount/accountAddressEdit.jsp" iclass="atg_store_basicButton">
                      <dsp:param name="successURL" bean="/OriginatingRequest.requestURI"/>
                      <dsp:param name="firstLastRequired" value="true"/>
                      <dsp:param name="addEditMode" value="add"/>
                      <dsp:param name="restrictionDroplet" value="/atg/store/droplet/ShippingRestrictionsDroplet"/>
                      <span><fmt:message key="myaccount_addressEdit.newAddress"/></span>
                    </dsp:a>
                  </div>
                </jsp:body>
              </crs:messageContainer>
            </dsp:oparam>
            
            <dsp:oparam name="output">
              <dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/>
          
              <%-- 
                Iterate over the array of addresses and display address information
                and edit/remove links 
               --%>
              <c:forEach var="shippingAddress" items="${sortedArray}" varStatus="status">
                <dsp:setvalue param="shippingAddress" value="${shippingAddress}"/>
                
                <%-- Display address details --%>
                
                
                <%-- 
                  If default shipping address is defined, it will be always the first
                --%>
                <c:choose>
                  <c:when test="${status.count == 1 and not empty defaultAddress}">
                    <div class="atg_store_addressGroup atg_store_addressGroupDefault">
                      <dl>
                        <%-- 
                          Display address nickname and 'Default Shipping' link 
                          if it is the default value
                          --%>
                        <dt class="atg_store_defaultShippingAddress">
                          <dsp:valueof param="shippingAddress.key"/>
                          <fmt:message var="defaultAddressTitle" key="common.defaultShipping"/>
                          
                          <%-- link to profile defaults page --%>                          
                          <dsp:a page="/myaccount/profileDefaults.jsp" 
                                 title="${defaultAddressTitle}">
                            <span>${defaultAddressTitle}</span>
                          </dsp:a>
                        </dt>    
                  </c:when>
                  <c:otherwise>
                    <div class="atg_store_addressGroup">
                      <dl>
                        <%-- Display static link --%>
                        <dt><dsp:valueof param="shippingAddress.key"/></dt>
                  </c:otherwise>
                </c:choose>
                
                  <dd>
                    <%-- Address information --%>
                    <dsp:include page="/global/util/displayAddress.jsp">
                      <dsp:param name="address" param="shippingAddress.value"/>
                      <dsp:param name="private" value="false"/>
                    </dsp:include>
                  </dd>
                </dl>

                  <%-- Display Edit/Remove links --%>
                  <ul class="atg_store_storedAddressActions">
                    
                    <%-- 'Edit' link --%>
                    <fmt:message var="editAddressTitle" key="common.button.editAddressTitle"/>
                    <li class="<crs:listClass count="1" size="2" selected="false"/>">
                      <dsp:a title="${editAddressTitle}"
                             iclass="atg_store_addressBookDefaultEdit" page="/myaccount/accountAddressEdit.jsp">
                        <dsp:param name="successURL" bean="/OriginatingRequest.requestURI"/>
                        <dsp:param name="addEditMode" value="edit"/>
                        <dsp:param name="nickName" value="${shippingAddress.key}"/>
                        <span><fmt:message key="common.button.editAddressText"/></span>
                      </dsp:a>
                    </li>

                    <%-- 'Remove' link --%>
                    <fmt:message var="removeAddressTitle" key="myaccount_addressBookDefault.button.removeAddressTitle"/>
                    <li class="<crs:listClass count="2" size="2" selected="false"/>">
                      <dsp:a title="${removeAddressTitle}"
                             iclass="atg_store_addressBookDefaultRemove"
                             bean="ProfileFormHandler.removeAddress"
                             href="${requestURL}" paramvalue="shippingAddress.key">
                       <span><fmt:message key="myaccount_addressBookDefault.button.removeAddressText"/></span>
                      </dsp:a>
                    </li>

                  </ul>
                </div>
              </c:forEach>
              
             <div class="atg_store_formActions">

                <!-- Link to Add a New Address page -->
                <dsp:a page="/myaccount/accountAddressEdit.jsp" iclass="atg_store_basicButton">
                  <dsp:param name="successURL" bean="/OriginatingRequest.requestURI"/>
                  <dsp:param name="firstLastRequired" value="true"/>
                  <dsp:param name="addEditMode" value="add"/>
                  <dsp:param name="restrictionDroplet" value="/atg/store/droplet/ShippingRestrictionsDroplet"/>
                  <span><fmt:message key="myaccount_addressEdit.newAddress"/></span>
                </dsp:a>
              </div>
            </dsp:oparam>
          </dsp:droplet> <%-- MapToArrayDefaultFirst (sort saved addresses) --%>
      
        <!-- End saved shipping addresses -->
        </div>
      </div> 
    </jsp:body>
  </crs:pageContainer>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/addressBook.jsp#2 $$Change: 788278 $--%>

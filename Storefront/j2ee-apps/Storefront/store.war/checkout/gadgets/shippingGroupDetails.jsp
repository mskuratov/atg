<%--
  This gadget displays shipping group's details - its name and address.
  It also displays Edit and Remove buttons, the former opens an 'Edit Shipping Address' 
  page and the latter removes a shipping address with name equal to group's name.

  Required parameters:
    shippingGroup
      Shipping group to be displayed. It should be hardgood shipping group only.
    shippingAddressNickname
      Address with this nickname will be removed when the user clicks on the 'Remove' button.
    editShippingAddressSuccessURL
      The user will be redirected here after address is successfully edited.
    removeShippingAddressSuccessURL
      The user will be redirected here after address is successfully removed.
    isDefault
      Flags, if currently passed address is default.

    Optional parameters:
      None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>

  <dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
  <dsp:getvalueof var="shippingAddressNickname" param="shippingAddressNickname"/>
  <dsp:getvalueof var="editShippingAddressSuccessURL" param="editShippingAddressSuccessURL"/>
  <dsp:getvalueof var="removeShippingAddressSuccessURL" param="removeShippingAddressSuccessURL"/>
  <dsp:getvalueof var="isDefault" param="isDefault"/>
  <dsp:getvalueof var="shippingGroupClassType" vartype="java.lang.String" 
                  param="shippingGroup.shippingGroupClassType"/>

  <%-- This gadget supports hardgood shipping groups only. Check this. --%>
  <c:if test='${shippingGroupClassType == "hardgoodShippingGroup"}'>
    
    <dsp:getvalueof var="shippingAddress" param="shippingGroup.shippingAddress"/>

    <%-- Proper shipping group must possess a shipping address. --%>
    <c:if test="${not empty shippingAddress}">
      
      <%-- Display Address Details --%>
      <div class="atg_store_addressGroup${isDefault ? ' atg_store_addressGroupDefault' : ''}">
        <dl>
          <c:choose>
            <c:when test="${isDefault}">
              
              <%-- Show Default Label if it is the default value --%>
              <dt class="atg_store_defaultShippingAddress">
                
                <c:out value="${shippingAddressNickname}"/>
                
                <fmt:message var="defaultAddressTitle" key="common.defaultShipping"/>

                <dsp:a page="/myaccount/profileDefaults.jsp" title="${defaultAddressTitle}">
                  <span>${defaultAddressTitle}</span>
                </dsp:a>
                
              </dt>             
            </c:when>
            <c:otherwise>
              <%-- Just display address nickname otherwise. --%>
              <dt>
                <c:out value="${shippingAddressNickname}"/>
              </dt>
            </c:otherwise>
          </c:choose> 

          <%-- Display address details. --%>
          <dd>
            <dsp:include page="/global/util/displayAddress.jsp">
              <dsp:param name="address" value="${shippingAddress}"/>
              <dsp:param name="private" value="false"/>
            </dsp:include>
          </dd>
        </dl>

        <%-- Do not display Edit/Remove buttons for gift shipping groups. --%>
        <c:set var="description" value="${shippingGroup.description}"/>
        <dsp:getvalueof var="giftPrefix" 
                        bean="/atg/commerce/gifts/GiftlistManager.giftShippingGroupDescriptionPrefix"/>
        
        <c:if test="${!(fn:startsWith(description, giftPrefix))}">
          
          <%-- It's not a gift shipping group. --%>
          <ul class="atg_store_storedAddressActions">
            
            <%-- Display Edit Link --%>
            <fmt:message var="editAddressTitle" key="common.button.editAddressTitle"/>
            <c:set var="count" value="1"/>
            
            <li class="<crs:listClass count="${count}" size="2" selected="false"/>">
              <dsp:a title="${editAddressTitle}"
                     iclass="atg_store_addressBookDefaultEdit"
                     page="/checkout/shippingAddressEdit.jsp">
                <dsp:param name="nickName" value="${shippingAddressNickname}"/>
                <dsp:param name="successURL" 
                           value="${pageContext.request.contextPath}${editShippingAddressSuccessURL}"/>
                <span><fmt:message key="common.button.editAddressText"/></span>
              </dsp:a>
            </li>

            <%-- Display Remove Link --%>
            <fmt:message var="removeAddressTitle" key="myaccount_addressBookDefault.button.removeAddressTitle"/>
            <c:set var="count" value="${count + 1}"/>
            
            <li class="<crs:listClass count="${count}" size="2" selected="false"/>">
              <dsp:a title="${removeAddressTitle}"
                     iclass="atg_store_addressBookDefaultRemove"
                     page="${removeShippingAddressSuccessURL}">
                <dsp:property bean="ShippingGroupFormHandler.removeShippingAddressNickName" 
                              value="${shippingAddressNickname}"/>
                <dsp:property bean="ShippingGroupFormHandler.removeShippingAddress" 
                              value="${shippingAddressNickname}"/>
                <span><fmt:message key="myaccount_addressBookDefault.button.removeAddressText"/></span>
              </dsp:a>
            </li>
          </ul>
        </c:if>
      </div>
    </c:if><%-- check for empty shipping address --%>
  </c:if><%-- check for hard good shipping --%>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/shippingGroupDetails.jsp#2 $$Change: 788278 $--%>
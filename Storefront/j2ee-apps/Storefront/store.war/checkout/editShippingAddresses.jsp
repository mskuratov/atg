<%--
  This page displays a list of available shipping addresses. For each non-gift shipping address, 
  there will be displayed an 'Edit/Remove' buttons.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupContainerService"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  
  <c:set var="stage" value="shipping"/>

  <crs:pageContainer divId="atg_store_cart"
                     index="false" 
                     follow="false"
                     bodyClass="atg_store_checkoutEditAddresses atg_store_checkout atg_store_rightCol">
    <jsp:body>
      
      <fmt:message key="checkout_title.checkout" var="title"/>
      
      <crs:checkoutContainer currentStage="${stage}" title="${title}">
        <jsp:body>
          <div id="atg_store_checkout" class="atg_store_main">
            
            <dsp:getvalueof var="shippingGroupMap" vartype="java.lang.Object" 
                            bean="ShippingGroupContainerService.shippingGroupMap"/>
            <dsp:getvalueof var="defaultShippingNickname" vartype="java.lang.String"
                            bean="ShippingGroupContainerService.defaultShippingGroupName"/>
            
            <c:choose>
              <c:when test="${empty shippingGroupMap}">
                
                <%-- We don't have shipping addresses, display a message instead of addresses list. --%>
                <crs:messageContainer
                  titleKey="myaccount_addressBookDefault.noShippingAddress">
                  
                  <jsp:body>
                    <div class="atg_store_formActions">
                      <%-- Display button to return back. --%>
                      <dsp:a page="/checkout/shippingMultiple.jsp" iclass="atg_store_basicButton secondary">
                        <span><fmt:message key="common.button.cancelText"/></span>
                      </dsp:a>
                    </div>
                  </jsp:body>
                  
                </crs:messageContainer>
              </c:when>
              <c:otherwise>
                <%-- Display default shipping address first. --%>
                <div id="atg_store_shippingAddresses" class="atg_store_savedAddresses">
                  
                  <h3><fmt:message key="checkout_shippingMultipleDestinations.editAddresses"/></h3>
                  
                  <div id="atg_store_storedAddresses">
                    
                    <%-- This gadget will display address details and proper buttons. --%>
                    <dsp:include page="gadgets/shippingGroupDetails.jsp">
                      <dsp:param name="shippingGroup" value="${shippingGroupMap[defaultShippingNickname]}"/>
                      <dsp:param name="shippingAddressNickname" value="${defaultShippingNickname}"/>
                      <dsp:param name="isDefault" value="true"/>
                      <dsp:param name="editShippingAddressSuccessURL" value="/checkout/shippingMultiple.jsp"/>
                      <dsp:param name="removeShippingAddressSuccessURL" value="/checkout/shippingMultiple.jsp?init=true"/>
                    </dsp:include>

                    <%-- Then display remaining non-default shipping addresses. --%>
                    <c:forEach var="shippingGroupMapEntry" items="${shippingGroupMap}">
                      
                      <dsp:getvalueof var="shippingAddressNickname" value="${shippingGroupMapEntry.key}"/>
                      
                      <%-- Do not display default address. --%>
                      <c:if test='${shippingAddressNickname != defaultShippingNickname}'>
                        <dsp:include page="gadgets/shippingGroupDetails.jsp">
                          <dsp:param name="shippingGroup" value="${shippingGroupMapEntry.value}"/>
                          <dsp:param name="shippingAddressNickname" value="${shippingAddressNickname}"/>
                          <dsp:param name="isDefault" value="false"/>
                          <dsp:param name="editShippingAddressSuccessURL" value="/checkout/shippingMultiple.jsp"/>
                          <dsp:param name="removeShippingAddressSuccessURL" value="/checkout/shippingMultiple.jsp?init=true"/>
                        </dsp:include>
                      </c:if><%-- check for non-default shipping address --%>
                    </c:forEach><%-- end loop through shipping addresses --%>
                  </div>
                </div>
                
                <div class="atg_store_formActions">
                  <%-- Display button to return back to shipping page. --%>
                  <dsp:a page="/checkout/shippingMultiple.jsp" iclass="atg_store_basicButton secondary">
                    <span><fmt:message key="common.button.cancelText"/></span>
                  </dsp:a>
                </div>
                
              </c:otherwise>
            </c:choose>
          </div>
        </jsp:body>
      </crs:checkoutContainer>
      
      <%-- Order Summary --%>
      <dsp:include page="/checkout/gadgets/checkoutOrderSummary.jsp">
        <dsp:param name="order" bean="ShoppingCart.current"/>
        <dsp:param name="currentStage" value="${stage}"/>
      </dsp:include>
      
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/editShippingAddresses.jsp#2 $$Change: 788278 $--%>

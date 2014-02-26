<%-- 
  This container page renders the shipping-single form contents. It renders necessary 
  hidden fields and includes shippingAddresses.jsp and shippingForm.jsp pages.

  Required parameters:
    None.

  Optional parameters:
    init
      When set to true shipping related commerce objects are initialized.
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/store/droplet/AvailableShippingGroups"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupContainerService"/>  
                                             
  <dsp:getvalueof var="init" param="init"/>
  
  <div id="atg_store_checkout" class="atg_store_main"> 
    
    <%-- INITALIZE COMMERCE SHIPPING OBJECTS --%>
    <c:if test='${init == "true"}'>
      
      <dsp:getvalueof var="transient" bean="Profile.transient"/>

      <%--
        Used to initialize ShippingGroups and CommerceItemShippingInfo objects. We don't want to 
        clear shipping groups if the user is transient, if they re-enter the checkout previously
        entered addresses will appear. 
      
        Input Parameters:
          createOneInfoPerUnit
            Creates one CommerceItemShippingInfo for each individual unit contained by a commerce item.
          clear
            Clear both the CommerceItemShippingInfoContainer and the ShippingGroupMapContainer.
          shippingGroupTypes
            Comma separated list of ShippingGroup types used to determine which ShippingGroupInitializer 
            components are executed.
          initShippingGroups
            ShippingGroup types will be initialized.
          initBasedOnOrder
            create a CommerceItemShippingInfo for each ShippingGroupCommerceItemRelationships in the order.
        
        Open Parameters:
          output
      --%>
      <dsp:droplet name="ShippingGroupDroplet">
        <dsp:param name="createOneInfoPerUnit" value="false"/>
        <dsp:param name="clearShippingInfos" param="init"/>
        <dsp:param name="clearShippingGroups" value="${not transient}"/>
        <dsp:param name="shippingGroupTypes" value="hardgoodShippingGroup"/>
        <dsp:param name="initShippingGroups" param="init"/>
        <dsp:param name="initBasedOnOrder" param="init"/>
        <dsp:oparam name="output"/>
      </dsp:droplet>
    </c:if>
    
    <%-- INITALIZE SINGLE SHIPPING SPECIFIC OBJECTS --%>
    <dsp:setvalue bean="ShippingGroupFormHandler.initSingleShippingForm" value=""/>

    <%-- 
      shippingGroupMap contains address information from the profile and the order we only want to
      check if we have permitted shipping addresses if we have any addresses
    --%>
    <dsp:getvalueof var="shippingGroupMap" vartype="java.lang.Object" 
                    bean="ShippingGroupContainerService.shippingGroupMapForDisplay"/> 
    
    <c:if test="${not empty shippingGroupMap}">
      
      <%-- 
        Sorts shipping addresses so that the default address is first and returns only permitted
        shipping addresses. (E.g Do we ship to this country?)
                   
        Input parameters:
          defaultKey
            The parameter that defines the map key of the default item that should be
            placed in the beginning of the array.   
          sortByKeys
            Boolean that specifies whether to sort map entries by keys or not.
          map
            The parameter that defines the map of items to convert to the sorted array.          
                   
        Open parameters:
          output
            Rendered for permitted shipping address list, permittedAddresses parameter
            contains the permitted shipping addresses.
          empty
            Rendered if there are no shipping addresses or there are no permitted shipping addresses.  
              
        Output parameters:
          permittedAddresses
            contains the permitted shipping addresses.
      --%>
      <dsp:droplet name="AvailableShippingGroups">
        <dsp:param name="map" value="${shippingGroupMap}"/>
        <dsp:param name="defaultKey"  bean="ShippingGroupFormHandler.shipToAddressName" />
        <dsp:param name="sortByKeys" value="true"/>
        
        <dsp:oparam name="output">
          <dsp:getvalueof var="permittedAddresses" vartype="java.lang.Object" param="permittedAddresses"/>
        </dsp:oparam>
      </dsp:droplet>
    </c:if>
      
    <div class="${!empty permittedAddresses ?'atg_store_existingAddresses ':''}atg_store_checkoutOption" 
         id="atg_store_checkoutOptionArea"> 
          
      <%-- Saved addresses area. --%>
      <div id="atg_store_selectAddress">
        <dsp:include page="shippingAddresses.jsp">
          <dsp:param name="permittedAddresses" value="${permittedAddresses}"/>
        </dsp:include>
      </div>
      
      <%-- New address area. --%>
      <div id="atg_store_createNewShippingAddress">
        
        <dsp:input type="hidden" bean="ShippingGroupFormHandler.address.ownerId" beanvalue="Profile.id"/>
        
        <%-- Address-specific fields for user input. --%>
        <fieldset class="atg_store_createNewShippingAddress">
          <h3>
            <fmt:message key="checkout_shippingAddresses.createShippingAddress"/>
          </h3>
          
          <dsp:include page="shippingAddressAdd.jsp"/>
          
          <%-- Ship to new address button. --%>
          <fmt:message var="shipToButtonText" key="checkout_shippingAddresses.button.shipToThisAddress"/>
          
          <%-- 
            Check if user is anonymous or the session has expired. If so set the sessionExpirationURL.
    
            Open parameters:
              anonymous
                User is not logged in.
              default
                User has been recognized.
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
                                  
          <dsp:input type="hidden" bean="ShippingGroupFormHandler.address.email" 
                     beanvalue="Profile.email"/>
          <dsp:input type="hidden" bean="ShippingGroupFormHandler.shipToNewAddressSuccessURL" 
                     value="shippingMethod.jsp"/>
          <dsp:input type="hidden" bean="ShippingGroupFormHandler.shipToNewAddressErrorURL" 
                     value="shipping.jsp"/>
            
          <div class="atg_store_saveNewBillingAddress atg_store_formActions">
            <span class="atg_store_basicButton">
              <dsp:input type="submit"  bean="ShippingGroupFormHandler.shipToNewAddress" value="${shipToButtonText}"/>
            </span>
          </div>
          
        </fieldset>
      </div>
    
      <%--
        Ship to Multiple Addresses button should be displayed only if there are is more than one
        non giftwrap commerce (cart) item.
      --%>
      <dsp:getvalueof var="isMultipleNonGiftWrapItems" bean="ShippingGroupFormHandler.multipleNonGiftWrapItems"/>
      
      <c:if test="${isMultipleNonGiftWrapItems}">
        <div class="atg_store_shipToMultipleLink atg_store_formActions">
          
          <fmt:message var="shippingMultipleLinkText" key="checkout_shippingOptions.button.shipToMultipleText"/>
          
          <c:url var="successUrl" value="/checkout/shippingMultiple.jsp">
            <c:param name="init" value="true"/>
          </c:url>
          
          <dsp:input type="hidden"  bean="ShippingGroupFormHandler.addAddressAndMoveToMultipleShippingSuccessURL"
                     value="${successUrl}"/>
          <dsp:input type="hidden"  bean="ShippingGroupFormHandler.addAddressAndMoveToMultipleShippingErrorURL"
                     beanvalue="/OriginatingRequest.requestURI"/>
    
          <span class="atg_store_basicButton">
            <dsp:input type="submit" bean="ShippingGroupFormHandler.addAddressAndMoveToMultipleShipping" 
                       value="${shippingMultipleLinkText}"/>
          </span>
        </div>
      </c:if>
      
    </div>
  </div>  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/shippingSingleForm.jsp#2 $$Change: 788278 $--%>

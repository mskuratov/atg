<%--
  This gadget renders the fields that allow the shopper to add a new shipping address.
  
  Form Condition:
    
    - This gadget must be contained inside of a form.
    - ShippingGroupFormHandler must be invoked on a form submit.

  Required parameters:
    None.
    
  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>

  <%-- Nickname input field. --%>
  <ul class="atg_store_basicForm atg_store_addNewAddress">
    <li class="nickname">
      <label for="atg_store_nickNameInput">
        <fmt:message key="common.nicknameThisAddress"/>
      </label>
      <dsp:input type="text" name="atg_store_nickNameInput" id="atg_store_nickNameInput" maxlength="42"
                 bean="ShippingGroupFormHandler.newShipToAddressName"/>
    </li>

    <%-- Display all address-related input fields. --%>
    <dsp:include page="/global/gadgets/addressAddEdit.jsp">
      <dsp:param name="formHandlerComponent" value="/atg/commerce/order/purchase/ShippingGroupFormHandler.address"/>
      <dsp:param name="checkForRequiredFields" value="false"/>
      <dsp:param name="preFillValues" value="true"/>
      <dsp:param name="restrictionDroplet" value="/atg/store/droplet/ShippingRestrictionsDroplet"/>
    </dsp:include>
    
    <%-- 
      If the shopper is transient (a guest shopper) we don't offer to save the address.
      
      Open parameters:
        anonymous
          user is not logged in (transient)
        default
          user is logged in    
      --%>
    <dsp:droplet name="ProfileSecurityStatus">
      <dsp:oparam name="anonymous">
        <%-- User is anonymous, don't save address --%>
      </dsp:oparam>
      <dsp:oparam name="default">
        
        <%-- Checkbox with 'save this address' option --%>
        <li class="last option">
          
          <label for="atg_store_addressAddSaveAddressInput">
            <fmt:message key="checkout_addressAdd.saveAddress"/>
          </label>
          
          <dsp:input type="checkbox" name="atg_store_addressAddSaveAddressInput"
                     id="atg_store_addressAddSaveAddressInput" checked="true"
                     bean="ShippingGroupFormHandler.saveShippingAddress"/>
        </li>
      </dsp:oparam>
    </dsp:droplet>
  </ul>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/shippingAddressAdd.jsp#2 $$Change: 788278 $--%>

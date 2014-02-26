<%--
  This gadget renders an 'Edit address' form with all necessary input fields and buttons.

  Required parameters:
    successURL
      The user will be redirected to this URL after he successfully changed address.
    nickName
      Nickname of address to be edited.

  Optional parameters:
    selectedAddress
      Nickname of address to be edited.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>

  <%-- First, initialize form handler with address to be edited. --%>
  <dsp:setvalue bean="ShippingGroupFormHandler.editShippingAddressNickName" paramvalue="nickName"/>
  <dsp:setvalue bean="ShippingGroupFormHandler.shippingAddressNewNickName" paramvalue="nickName"/>
  <dsp:setvalue bean="ShippingGroupFormHandler.initEditAddressForm" value=""/>
  
  <dsp:getvalueof var="contextRoot" vartype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof id="submitFieldText" param="submitFieldText"/>

  <div class="atg_store_checkoutOption" id="atg_store_shippingAddressEdit">
    <h3><fmt:message key="checkout_shippingAddressEdit.title"/></h3>

    <dsp:form action="${pageContext.request.requestURI}" method="post" formid="shippingaddresseditform">
     
      <%-- Update success/cancel URLs from page parameter. --%>
      <dsp:input type="hidden" bean="ShippingGroupFormHandler.editShippingAddressSuccessURL" paramvalue="successURL"/>
      <dsp:input type="hidden" bean="ShippingGroupFormHandler.cancelURL" paramvalue="successURL"/>

      <dsp:getvalueof var="successURL" vartype="java.lang.String" param="successURL"/>
      <dsp:getvalueof var="nickName" vartype="java.lang.String" param="nickName"/>
      <dsp:getvalueof var="selectedAddress" vartype="java.lang.String" param="selectedAddress"/>
      <dsp:getvalueof id="submitFieldText" param="submitFieldText"/>

      <%-- Choose proper 'Save' button caption. --%>
      <c:choose>
        <c:when test="${successURL eq 'checkout/shippingEdit.jsp'}"> 
          
          <c:set var="errorPage" value="checkout/shippingAddressEdit.jsp" />
          
          <fmt:message  var="submitFieldText" 
                        key="checkout_shippingAddresses.button.shipToThisAddress"/> 
        </c:when>   
        <c:otherwise>       
          
          <c:set var="errorPage" value="checkout/shippingAddressEdit.jsp" />
          
          <fmt:message  var="submitFieldText" 
                        key="common.button.saveAddressText"/> 
        </c:otherwise>
      </c:choose>

      <%-- Essential hidden inputs, specify address to be edited and redirect URLs. --%>
      <dsp:input type="hidden" bean="ShippingGroupFormHandler.editShippingAddressErrorURL"
                 value="${contextRoot}/${errorPage}?successURL=${successURL}&nickName=${nickName}&selectedAddress=${selectedAddress}"/>
      
      <dsp:input type="hidden" bean="ShippingGroupFormHandler.shipToAddressName" paramvalue="selectedAddress"/>
      
      <dsp:input bean="ShippingGroupFormHandler.editShippingAddressNickName" type="hidden" name="nickname" 
                 paramvalue="nickName"/>
      
      <dsp:input bean="ShippingGroupFormHandler.editAddress.country" type="hidden" name="country" 
                 beanvalue="ShippingGroupFormHandler.editAddress.country"/> 
      
      <dsp:input bean="ShippingGroupFormHandler.editAddress.email" type="hidden" name="country"
                 beanvalue="ShippingGroupFormHandler.editAddress.email"/> 

      <%-- Nickname input field. --%>
      <ul class="atg_store_basicForm">
        <li class="first">
          
          <label for="atg_store_editAddressNickname" class="required">
            <fmt:message key="common.nicknameThisAddress"/>
          </label>
          
          <dsp:input type="text" bean="ShippingGroupFormHandler.shippingAddressNewNickName"
                     id="atg_store_editAddressNickname" maxlength="42" required="true" iclass="required"/>
        </li>

        <%-- All other address-related input fields. --%>
        <dsp:include page="/global/gadgets/addressAddEdit.jsp">
          <dsp:param name="formHandlerComponent" value="ShippingGroupFormHandler.editAddress"/>
          <dsp:param name="checkForRequiredFields" value="true"/>
        </dsp:include>
      
      </ul>
    </fieldset>

    <%-- Now display Save and Cancel buttons. --%>
    <div class="atg_store_formFooter">
      
      <div class="atg_store_formKey">
        <span class="required"><fmt:message key="common.requiredFields"/>*</span>
      </div>
      
      <div class="atg_store_formActions">
        
        <fmt:message var="cancelButtonText" key="common.button.cancelText"/>
        <fmt:message var="saveButtonText" key="common.button.saveAddressText"/>
        
        <div class="atg_store_formActionItem">
          <span class="atg_store_basicButton">
            <dsp:input type="submit" bean="ShippingGroupFormHandler.editShippingAddress" value="${saveButtonText}"/>
          </span>
        </div>
        
        <div class="atg_store_formActionItem">
          <span class="atg_store_basicButton secondary">
            <dsp:input type="submit" bean="ShippingGroupFormHandler.cancel" value="${cancelButtonText}"/>
          </span>
        </div>

      </div>
    </div>
  </div>
</dsp:form>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/shippingAddressEdit.jsp#2 $$Change: 788278 $--%>

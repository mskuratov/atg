<%--
  This page for the account specific payment information details. 

  Required parameters:
    successURL
     to redirect to, during the success of updation or creation of new Credit Card Details.
    cancelURL
     to redirect to, during the failure of updation or creation of new Credit Card Details.
    
  Optional parameters:
    checkout
      if this flag is true, do not display error messages from ProfileFormHandler   
--%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/userprofiling/PropertyManager"/>
  
  <%-- Set title for submit button --%>
  <fmt:message var="submitText" key="common.button.saveChanges"/>

  <div id="atg_store_paymentInfoAddNewCard">    

    <dsp:form formid="atg_store_paymentInfoAddNewCardForm"   
              name="atg_store_paymentInfoAddNewCardForm" 
              action="${pageContext.request.requestURI}" method="post">
      
      <dsp:getvalueof id="originatingRequestURL" bean="/OriginatingRequest.requestURI"/>
      <dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
      
      <fieldset class="atg_store_editCreditCard">
              
        <%-- show form errors for non-checkout pages only, checkout pages display their errors themselves --%>
        <dsp:getvalueof var="isCheckout" vartype="java.lang.Boolean" param="checkout"/>
        <c:if test="${!isCheckout}">
          <div id="atg_store_formValidationError">        
            <dsp:include page="myAccountErrorMessage.jsp">
              <dsp:param name="formHandler" bean="ProfileFormHandler"/>
              <dsp:param name="submitFieldText" value="${submitText}"/>
              <dsp:param name="errorMessageClass" value="errorMessage"/>
            </dsp:include>
          </div>
        </c:if>

        <%-- Get edited card nickname --%>
        <dsp:getvalueof var="cardNickName" bean="ProfileFormHandler.editValue.nickname" 
                        vartype="java.lang.String"/>
      
        <%-- Set success/error url's  --%>
        <dsp:getvalueof var="cancelURL" param="cancelURL" vartype="java.lang.String"/>
        <dsp:getvalueof var="successURL" param="successURL" vartype="java.lang.String"/>   

        <dsp:input type="hidden" bean="ProfileFormHandler.updateCardSuccessURL" paramvalue="successURL"/>
        <dsp:input type="hidden" bean="ProfileFormHandler.cancelURL" 
                   value="${cancelURL}?preFillValues=false"/>
        <dsp:input type="hidden" bean="ProfileFormHandler.updateCardErrorURL" 
                   value="${originatingRequestURL}?preFillValues=true&cancelURL=${cancelURL}&successURL=${successURL}"/> 
        
        <dsp:getvalueof var="preFillValuesVar" value="true" vartype="java.lang.Boolean"/>

        <%-- Only used in the 'edit' mode of this gadget --%>
        <dsp:getvalueof var="creditCardNumber" 
                        bean="ProfileFormHandler.editValue.creditCardNumber" 
                        vartype="java.lang.String"/>
        <dsp:getvalueof var="creditCardType" 
                        bean="ProfileFormHandler.editValue.creditCardType" 
                        vartype="java.lang.String"/>
  
        <ul class="atg_store_basicForm">
          <%-- New nickname --%>          
          <li>
            <label for="atg_store_paymentInfoAddNewCardCreateNickname" class="required atg_store_cardNickName">
              <fmt:message key="common.nicknameThisCard"/>
              <span class="require">*</span>
            </label>
            <dsp:input bean="ProfileFormHandler.editValue.nickname" type="hidden"/>
            <dsp:input type="text" bean="ProfileFormHandler.editValue.newNickname"
                       id="atg_store_paymentInfoAddNewCardCreateNickname"
                       maxlength="42" iclass="required" required="true"/>
          </li>
          <%-- Credit card number --%>
          <li>
            <label>
              <fmt:message key="common.cardNumber"/>
            </label>
            <span class="atg_store_creditCardNumber">
              <fmt:message key="global_displayCreditCard.endingIn"> 
                <fmt:param>
                  <fmt:message key="common.${creditCardType}"/>
                </fmt:param>
                <fmt:param>
                  <crs:trim message="${creditCardNumber}" length="4" fromEnd="true"/>
                </fmt:param>
              </fmt:message>             
            </span>
          </li>          
    
          <%-- Expiration date --%>
          <li class="atg_store_expiration">
            <label for="atg_store_paymentInfoAddNewCardMonth" class="required">
              <fmt:message key="common.expirationDate"/>
              <span class="require">*</span>
            </label>
            <div class="atg_store_ccExpiration">
              <dsp:getvalueof var="selectedMonth" bean="ProfileFormHandler.editValue.expirationMonth"/>
              <fmt:message var="expirationMonthTitle" key="checkout_creditCardForm.expirationMonthTitle"/>
              <dsp:select id="atg_store_paymentInfoAddNewCardMonth"
                        bean="ProfileFormHandler.editValue.expirationMonth" 
                        required="true" iclass="number"
                        title="${expirationMonthTitle}">
                <dsp:option>
                  <fmt:message key="common.month"/>
                </dsp:option>

                <%-- Display months --%>
                <c:forEach var="count" begin="1" end="12" step="1" varStatus="status">
                  <dsp:option value="${count}" selected="${not empty selectedMonth && (selectedMonth eq count)}">
                    <fmt:message key="common.month${count}"/>
                  </dsp:option>
                </c:forEach>            
              </dsp:select>
                 
              <%-- Expiration year. --%>
              <fmt:message var="expirationYearTitle" key="checkout_creditCardForm.expirationYearTitle"/>
              <crs:yearList numberOfYears="16" 
                            bean="/atg/userprofiling/ProfileFormHandler.editValue.expirationYear"
                            id="atg_store_paymentInfoAddNewCardYear"
                            selectRequired="true"
                            nodefault="false" iclass="number"
                            title="${expirationYearTitle}"/>
            </div>
          </li>
      
          <%-- Default cart checkbox --%>
          <li class="option">
           <dsp:getvalueof var="creditCards" vartype="java.lang.Object" bean="Profile.creditCards"/>
              <dsp:getvalueof var="targetCardKey" vartype="java.lang.String" bean="ProfileFormHandler.editCard"/>
              <dsp:getvalueof var="userCards" vartype="java.util.Map" bean="Profile.creditCards"/>
              <dsp:getvalueof var="defaultCardId" vartype="java.lang.String" bean="Profile.defaultCreditCard.id"/>
              <dsp:input type="checkbox" checked="${empty creditCards or (defaultCardId == userCards[targetCardKey].repositoryId)}"
                  id="atg_store_paymentInfoAddNewCardCheckbox" bean="ProfileFormHandler.editValue.newCreditCard" />
          
            <label for="atg_store_paymentInfoAddNewCardCheckbox"><fmt:message key="myaccount_paymentInfoCardAddEdit.defaultCard"/></label>
          </li>
        </ul>
        
        <h3>
          <fmt:message key="myaccount_paymentInfoCardAddEdit.billingAddress"/>
        </h3>
        
        <ul class="atg_store_basicForm">      
          <dsp:getvalueof id="chkForRequired" value="true"/>
  
          <dsp:include page="/global/gadgets/addressAddEdit.jsp">
            <dsp:param name="formHandlerComponent" value="/atg/userprofiling/ProfileFormHandler.billAddrValue"/>
            <dsp:param name="checkForRequiredFields" value="${chkForRequired}"/>
            <dsp:param name="restrictionDroplet" value="/atg/store/droplet/BillingRestrictionsDroplet"/>
            <dsp:param name="hideNameFields" value="false"/>
            <dsp:param name="preFillValues" value="${preFillValuesVar}"/>
          </dsp:include>
        </ul>
      </fieldset>
      
      <div class="atg_store_formActions">
        <div class="atg_store_formKey">
          <span class="required">* <fmt:message key="common.requiredFields"/></span>
        </div>
  
        <fmt:message var="cancelButtonText" key="common.button.cancelText" />
        <fmt:message var="cancelButtonTitle" key="common.button.cancelTitle" />
            
        <%-- 'Update' button --%>          
        <div class="atg_store_formActionItem">
          <span class="atg_store_basicButton">
            <dsp:input type="submit" value="${submitText}" 
                       id="atg_store_editAddressSubmit"
                       bean="ProfileFormHandler.updateCard"/>
          </span>
        </div>
        
        <%-- 'Cancel' button --%>
        <div class="atg_store_formActionItem">
          <span class="atg_store_basicButton secondary">
            <dsp:input type="submit"
                       value="${cancelButtonText}"
                       id="atg_store_paymentInfoAddNewCardCancel" 
                       bean="ProfileFormHandler.cancel"/>
          </span>
        </div>
      </div>
      
    </dsp:form>
  </div><%-- atg_store_paymentInfoAddNewCard --%>  
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/creditCardEdit.jsp#3 $$Change: 788842 $--%>
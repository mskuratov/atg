<%-- 
  Outlay page for rendering the new credit card page
  
  Required parameters:
    None
          
  Optional parameters:
    None  
--%>
<dsp:page>

  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/userprofiling/PropertyManager"/>
  <dsp:importbean bean="/atg/store/StoreConfiguration"/>
  
  <fmt:message var="submitText" key="common.button.saveCardText"/>
      
  <crs:pageContainer divId="atg_store_paymentInfoIntro" 
                     index="false" follow="false"
                     bodyClass="atg_store_myAccountPage atg_store_leftCol">    

    <jsp:attribute name="formErrorsRenderer">
      <%-- Display error messages if any above the accessibility navigation--%> 
      <dsp:include page="gadgets/myAccountErrorMessage.jsp">
        <dsp:param name="formHandler" bean="ProfileFormHandler"/>
        <dsp:param name="submitFieldText" value="${submitText}"/>
        <dsp:param name="errorMessageClass" value="errorMessage"/>
      </dsp:include>
    </jsp:attribute>
    
    <jsp:body>
      
      <%-- Page title --%>
      <div id="atg_store_contentHeader">
        <h2 class="title">
          <fmt:message key="myaccount_newCreditCard.title"/>
        </h2>
      </div>
      
      <%-- Left-hand menu --%>
      <dsp:include page="gadgets/myAccountMenu.jsp">
        <dsp:param name="selpage" value="PAYMENT INFO" />
      </dsp:include>
     
      <%-- Include 'create card' form --%>
      <div class="atg_store_main atg_store_myAccount">
        <%-- Show form errors if any --%>
        <div id="atg_store_formValidationError">
          <dsp:include page="gadgets/myAccountErrorMessage.jsp">
            <dsp:param name="formHandler" bean="ProfileFormHandler"/>
            <dsp:param name="submitFieldText" value="${submitText}"/>
            <dsp:param name="errorMessageClass" value="errorMessage"/>
          </dsp:include>
        </div>

        <dsp:getvalueof var="savedAddresses" bean="Profile.secondaryAddresses"></dsp:getvalueof>
      
        <div id="atg_store_paymentInfoAddNewCard">    
      
          <dsp:form formid="atg_store_paymentInfoAddNewCardForm"   
                    name="atg_store_paymentInfoAddNewCardForm" 
                    action="${pageContext.request.requestURI}" method="post">
            
            <dsp:getvalueof id="originatingRequestURL" bean="/OriginatingRequest.requestURI"/>
            <dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
            
            <div class="atg_store_addCardInfo">
            <fieldset class="atg_store_creditCardForm">
              
         
                        
                <%-- Set success/error url's --%>
                <dsp:input bean="ProfileFormHandler.createCardSuccessURL" type="hidden" value="${contextPath}/myaccount/paymentInfo.jsp"/>
                <dsp:input bean="ProfileFormHandler.createCardErrorURL" type="hidden" 
                           value="${originatingRequestURL}?preFillValues=true&cancelURL=${cancelURL}"/>
                <dsp:input bean="ProfileFormHandler.removeCardSuccessURL" type="hidden" value="${contextPath}/myaccount/paymentInfo.jsp"/>
                <dsp:input bean="ProfileFormHandler.removeCardErrorURL" type="hidden"   
                           beanvalue="/OriginatingRequest.requestURI"/>
      
                <c:choose>
                  <c:when test="${empty param.preFillValues}">
                    <dsp:getvalueof var="preFillValuesVar" value="false" vartype="java.lang.Boolean"/>
                  </c:when>
                  <c:otherwise>
                    <dsp:getvalueof var="preFillValuesVar" value="${param.preFillValues}" vartype="java.lang.Boolean"/>
                  </c:otherwise>
                </c:choose>
              
                <%-- Only used in the 'create' mode of this gadget --%>
                <ul class="atg_store_basicForm atg_store_addNewCreditCard">
                  <%-- Credit card nickname --%>
                  <li>
                    <label for="atg_store_paymentInfoAddNewCardCreateNickname" class="required atg_store_cardNickName">
                      <fmt:message key="common.nicknameThisCard"/>
                      <span class="require">*</span>
                    </label>
                    <dsp:input type="text" id="atg_store_paymentInfoAddNewCardCreateNickname"   
                               bean="ProfileFormHandler.editValue.creditCardNickname" 
                               maxlength="42" iclass="required" required="true"/>
                  </li>
                  
                  <%-- Credit card type --%>
                  <li class="atg_store_cardType">
                    <label for="atg_store_paymentInfoAddNewCardCardType" class="required">
                      <fmt:message key="common.cardType"/>
                      <span class="require">*</span>
                    </label>
      
                    <dsp:select id="atg_store_paymentInfoAddNewCardCardType" 
                                bean="ProfileFormHandler.editValue.creditCardType" 
                                required="true" iclass="custom_select">
                                
                      <%-- Retrieve the list of supported credit card types from StoreConfiguration component. --%>
                      <dsp:getvalueof var="supportedCreditCardTypes" bean="StoreConfiguration.supportedCreditCardTypes" />
                      
                      <dsp:option value="">
                        <%-- Use an empty value to trip required error handling --%>
                        <fmt:message key="common.chooseCardType"/>
                      </dsp:option>
                      
                      <%-- Display supported credit card types as options --%>
                      <c:forEach var="creditCardType" items="${supportedCreditCardTypes}">
                        <dsp:option value="${creditCardType}">
                          <fmt:message key="common.${creditCardType}"/>
                        </dsp:option>
                      </c:forEach>
                      
                    </dsp:select>
                  </li>
                  
                  <%-- Credit card number --%>
                  <li class="atg_store_ccNumber">
                    <label for="atg_store_paymentInfoAddNewCardCardNumber" class="required">
                      <fmt:message key="common.cardNumber"/>
                      <span class="require">*</span>
                    </label>
                    <dsp:input type="text" iclass="required"
                               id="atg_store_paymentInfoAddNewCardCardNumber" 
                               bean="ProfileFormHandler.editValue.creditCardNumber" maxlength="16" required="true" autocomplete="off"/>
                  </li>
            
                  <%-- Expiration date --%>
                  <li class="atg_store_expiration">
                    <label for="atg_store_expirationDate" class="required">
                      <fmt:message key="common.expirationDate"/>
                      <span class="require">*</span>
                    </label>
                    <div class="atg_store_ccExpiration">
                     
                      <%-- Expiration month. --%>
                     <fmt:message var="expirationMonthTitle" key="checkout_creditCardForm.expirationMonthTitle"/>
        
                      <dsp:select id="atg_store_paymentInfoAddNewCardMonth"
                                bean="ProfileFormHandler.editValue.expirationMonth" 
                                required="true" iclass="number" title="${expirationMonthTitle}">
                        <dsp:option>
                          <fmt:message key="common.month"/>
                        </dsp:option>
                        
                        <%-- Display months --%>
                        <c:forEach var="count" begin="1" end="12" step="1" varStatus="status">
                          <dsp:option value="${count}">
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
                                    iclass="number"
                                    title="${expirationYearTitle}"/>
                    </div>
                  </li>
                    
                  <%-- Default cart checkbox --%>
                  <li class="option">
                    <label for="atg_store_paymentInfoAddNewCardCheckbox"><fmt:message key="myaccount_paymentInfoCardAddEdit.defaultCard"/></label>
                    <dsp:getvalueof var="creditCards" vartype="java.lang.Object" bean="Profile.creditCards"/>
                      <dsp:getvalueof var="targetCardKey" vartype="java.lang.String" bean="ProfileFormHandler.editCard"/>
                      <dsp:getvalueof var="userCards" vartype="java.util.Map" bean="Profile.creditCards"/>
                      <dsp:getvalueof var="defaultCardId" vartype="java.lang.String" bean="Profile.defaultCreditCard.id"/>
                      <dsp:input type="checkbox" checked="${empty creditCards or (defaultCardId == userCards[targetCardKey].repositoryId)}"
                          id="atg_store_paymentInfoAddNewCardCheckbox" bean="ProfileFormHandler.editValue.newCreditCard" />
                  </li>
                </ul>
              </fieldset>
      </div>
              <div id="atg_store_chooseCardAddress" class="${!empty savedAddresses?'atg_store_existingAddresses':''}"> 
        
                <%-- Prompt the shopper to select a saved address as Billing Address --%>
                <dsp:include page="gadgets/creditCardAddressSelect.jsp"/>
    
                <%-- 
                  Alternatively, prompt the shopper to provide a new address as billing address. 
                  If user is logged in, then display radio button for them to choose a new address
                  rather than using an address-book address. 
                  --%>
                <div id="atg_store_enterNewBillingAddress">
                  <fieldset>
                
                    <h3>
                      <fmt:message key="common.newBillingAddress"/>
                    </h3>
      
                    <ul class="atg_store_basicForm atg_store_addNewAddress">
                      <%-- Billing address nickname --%>
                      <li>
                        <label for="atg_store_paymentInfoAddNewCardAddressNickname" class="required">
                          <fmt:message key="common.nicknameThisAddress"/>
                        </label>
                        <dsp:input type="text" id="atg_store_paymentInfoAddNewCardAddressNickname" 
                                   maxlength="42" iclass="text" 
                                   bean="ProfileFormHandler.billAddrValue.shippingAddrNickname" />
                      </li>
                      <dsp:getvalueof id="chkForRequired" value="false"/>
    
                      <dsp:include page="/global/gadgets/addressAddEdit.jsp">
                        <dsp:param name="formHandlerComponent" value="/atg/userprofiling/ProfileFormHandler.billAddrValue"/>
                        <dsp:param name="checkForRequiredFields" value="${chkForRequired}"/>
                        <dsp:param name="restrictionDroplet" value="/atg/store/droplet/BillingRestrictionsDroplet"/>
                        <dsp:param name="hideNameFields" value="false"/>
                        <dsp:param name="preFillValues" value="${preFillValuesVar}"/>
                      </dsp:include>
                    </ul>
                    <div class="atg_store_saveNewBillingAddress atg_store_formActions">
                      <span class="atg_store_basicButton">
                        <dsp:input type="submit" value="${submitText}" 
                                   id="atg_store_paymentInfoAddNewCardAndAddressSubmit"
                                   bean="ProfileFormHandler.createNewCreditCardAndAddress"/>
                      </span>   
                      <p>
                        <fmt:message key="checkout_billing.usingNewAddress" />
                      </p>
                    </div>
                  </fieldset>
        
                  
                </div><%--atg_store_enterNewBillingAddress --%>
          
                <div class="atg_store_formActions atg_store_cancel">
                  <div class="atg_store_formKey">
                    <span class="required">* <fmt:message key="common.requiredFields"/></span>
                  </div>
          
                  <fmt:message var="cancelButtonText" key="common.button.cancelText" />
                  <fmt:message var="cancelButtonTitle" key="common.button.cancelTitle" />
            
                  <%-- Create New Card Cancel button --%>
                  <dsp:a href="${contextPath}/myaccount/paymentInfo.jsp" 
                         iclass="atg_store_basicButton secondary">
                    <span><fmt:message key="common.button.cancelText"/></span>
                  </dsp:a>              
                </div>
              </div><%-- atg_store_chooseCardAddress --%>
          </dsp:form>
        </div><%-- atg_store_paymentInfoAddNewCard --%>  
      </div>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/newCreditCard.jsp#3 $$Change: 788842 $--%>

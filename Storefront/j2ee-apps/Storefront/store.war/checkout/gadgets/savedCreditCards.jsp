<%--
  This gadget lists the user's saved credit cards as radio buttons on the billing page.
  It displays default credit card first.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <div id="atg_store_storedCreditCards">
    <dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
    <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
    <dsp:importbean bean="/atg/store/order/purchase/BillingFormHandler"/>

    <%-- Check if the profile has credit cards and show them. --%>
    <dsp:getvalueof var="creditCards" vartype="java.lang.Object" bean="Profile.creditCards"/>
    
    <c:if test="${not empty creditCards}">
      
      <%-- 
        This droplet is used to convert a map of items to a sorted array of map entries
        with the default item in the beginning of the array. The default item is specified
        either through defaultId parameter (only for repository items) or through
        defaultKey parameter that is the map key that corresponds to the default item.
        The droplet is primarily intended to sort shipping addresses or credit cards 
        so that the default address or card is displayed as the first item on the JSP page.
      
        In this case get all saved credit cards, display default card first, then sort other 
        cards by title.
        
        Required parameters:
          defaultId
            The parameter that defines the repositoryId of the default item.
            Can be used only for repository item, for other types of objects
            like ShippingGroup the defaultKey parameter should be used.
          sortByKeys
            Boolean that specifies whether to sort map entries by keys or not.
          map
            The parameter that defines the map of items to convert to the sorted array.
        
        Open parameters:
          output
            Rendered on successful sort.
          empty
            Rendered if input map is empty or null
       
       Output parameters:
          sortedArray
            Array of RepositoryItem objects with the default item first.
          sortedArraySize
            Size of returned array.
      --%>
      <dsp:droplet name="MapToArrayDefaultFirst">
        <dsp:param name="map" bean="Profile.creditCards"/>
        <dsp:param name="defaultId" bean="Profile.defaultCreditCard.repositoryId"/>
        <dsp:param name="sortByKeys" value="true"/>
        <dsp:oparam name="output">
          
          <dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/>
          <dsp:getvalueof var="defaultCardId" vartype="java.lang.String" 
                          bean="Profile.defaultCreditCard.repositoryId"/>
          
          <%-- Display all saved credit cards. --%>
          <c:forEach var="userCard" items="${sortedArray}" varStatus="status">
            <div class="atg_store_paymentInfoGroup">
              <dl>
                <%-- Display card name. --%>
                <dt class="${userCard.value.repositoryId == defaultCardId ? 'atg_store_defaultCreditCard' : ''}">
                  <dsp:input type="radio" value="${userCard.key}" id="atg_store_savedCreditCard${status.count}"
                             bean="BillingFormHandler.storedCreditCardName"/>
                  
                  <label for="atg_store_savedCreditCard${status.count}"><c:out value="${userCard.key}"/></label>
                  
                  <c:if test="${userCard.value.repositoryId == defaultCardId}">
                    <span><fmt:message key="common.default"/></span>
                  </c:if>
                
                </dt>
                
                <%-- Render credit card details here. --%>
                <dsp:param name="creditCard" value="${userCard.value}"/>
                <dsp:getvalueof var="cardNumber" vartype="java.lang.String" param="creditCard.creditCardNumber"/>
                
                <dd class="vcard">
                  <div>
                  
                    <dsp:getvalueof var="creditCardType" param="creditCard.creditCardType"/>
      
                    <fmt:message key="global_displayCreditCard.endingIn">
                      <fmt:param>
                        <fmt:message key="common.${creditCardType}"/>
                      </fmt:param>
                      <fmt:param>
                        <crs:trim message="${cardNumber}" length="4" fromEnd="true"/>
                      </fmt:param>
                    </fmt:message>
                  </div>
                  
                  <div>
                    <fmt:message key="checkout_creditCards.expiration" />:&nbsp;
                    
                    <dsp:getvalueof var="var_expirationMonth" vartype="java.lang.String" 
                                    param="creditCard.expirationMonth"/>
                    <dsp:getvalueof var="var_expirationYear" vartype="java.lang.String" 
                                    param="creditCard.expirationYear"/>
                    
                    <%-- Month should be displayed in 2 digits --%>
                    <fmt:formatNumber minIntegerDigits="2" value="${var_expirationMonth}" var="formattedMonthValue"/>    
                    
                    <fmt:message key="myaccount.creditCardExpShortDate">
                      <fmt:param value="${formattedMonthValue}"/>
                      <fmt:param value="${var_expirationYear}"/>
                    </fmt:message>
                  </div>
                  
                  <div class="fn">
                    <dsp:valueof param="creditCard.billingAddress.firstName"/>
                    <dsp:valueof param="creditCard.billingAddress.lastName"/>
                  </div>
                  
                  <div class="adr">
                    <fmt:message key="checkout_confirmPaymentOptions.billTo"/>:&nbsp;
                    <dsp:valueof param="creditCard.billingAddress.address1"/>
                  </div>
                </dd>
                <%-- Display 'Edit' link for this card. --%>
                <ul class="atg_store_storedCreditCardsActions">
                  <li class="last">
                    
                    <fmt:message var="editCardTitle" key="common.button.editCardTitle" />
                    
                    <dsp:a bean="ProfileFormHandler.editCard" page="../creditCardEdit.jsp" 
                           value="${userCard.key}" title="${editCardTitle}">
                      <fmt:message key="checkout_billing.Edit" />
                    </dsp:a>
                  
                  </li>
                </ul>
              </dl>
            </div>
          </c:forEach>
        </div>

        <div class="atg_store_billingEnterCardCSV">
          
          <%-- Credit card CVC/CCV verification number. --%>
          <ul class="atg_store_basicForm">
            <li class="atg_store_ccCsvCode">
              
              <label for="atg_store_existingVerificationNum">
                <fmt:message key="checkout_billing.securityCode"/>
              </label>

              <dsp:input bean="BillingFormHandler.creditCardVerificationNumber" value="" type="text"
                        iclass="required" id="atg_store_existingVerificationNum" autocomplete="off" 
                        dojoType="atg.store.widget.enterSubmit" targetButton="atg_store_continueButton" />
              
              <fmt:message var="whatisThisTitle" key="checkout_billing.whatIsThis"/>
              
              <a href="${pageContext.request.contextPath}/checkout/whatsThisPopup.jsp" 
                 title="${whatisThisTitle}" class="atg_store_help" target="popup">${whatisThisTitle}</a>
            </li>
          </ul>
          
          <%-- Link to info about this verification number. --%>
   
          <%-- Display 'Billing with Saved Card' button. --%>
          <div class="atg_store_formActions">
            
            <span class="atg_store_basicButton tertiary">
              <fmt:message key="common.button.continueText" var="submitText"/>
              <dsp:input type="submit" value="${submitText}" id="atg_store_continueButton" 
                         bean="BillingFormHandler.billingWithSavedCard" />
            </span>
            
            <span class="atg_store_buttonMessage"><fmt:message key="checkout_billing.usingCreditCard" /></span>
            
          </div>
        </div>
      </dsp:oparam>
    </dsp:droplet> <%-- MapToArrayDefaultFirst (sort saved credit cards) --%>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/savedCreditCards.jsp#3 $$Change: 788842 $--%>

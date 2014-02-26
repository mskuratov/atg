<%--
  This page is used to display the details of a credit card. 
  These details include:-
    
    - The last 4 numbers of the credit card.
    - The credit card's billing address first name.
    - The credit card's billing address last name.
    - The credit card's expiration date. 
    
  Required parameters:
    creditCard
      a creditCard repository item to display.
      
  Optional parameters:
    displayCardHolder
      flag indicating whether to display card holder name.
--%>
<dsp:page>

  <dsp:getvalueof var="displayCardHolder" param="displayCardHolder"/>
  
  <dd class="atg_store_creditCardProvider">
    <dsp:getvalueof var="creditCard" param="creditCard.creditCardNumber" />
    <dsp:getvalueof var="creditCardType" param="creditCard.creditCardType"/>
        
    <fmt:message key="global_displayCreditCard.endingIn">
      <fmt:param>
        <fmt:message key="common.${creditCardType}"/>
      </fmt:param>
      <fmt:param>
        <crs:trim message="${creditCard}" length="4" fromEnd="true"/>
      </fmt:param>
    </fmt:message>
  </dd>
  
  <c:if test="${not empty displayCardHolder}">
    <dd class="atg_store_creditCardHolderName">
      <dsp:valueof param="creditCard.billingAddress.firstName"/> <dsp:valueof param="creditCard.billingAddress.lastName"/>
    </dd>
  </c:if>
  
  <dd class="atg_store_expirationDate">
    <fmt:message key="checkout_creditCards.expiration"/><fmt:message key="common.labelSeparator"/>
    
    <dsp:getvalueof var="var_expirationMonth" vartype="java.lang.String" param="creditCard.expirationMonth"/>
    <dsp:getvalueof var="var_expirationYear" vartype="java.lang.String" param="creditCard.expirationYear"/>
    
    <%-- Month should be displayed in 2 digits --%>
    <fmt:formatNumber minIntegerDigits="2" value="${var_expirationMonth}" var="formattedMonthValue"/>
    
    <fmt:message key="myaccount.creditCardExpShortDate">
      <fmt:param value="${formattedMonthValue}"/>
      <fmt:param value="${var_expirationYear}"/>
    </fmt:message>
  
  </dd>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/util/displayCreditCard.jsp#2 $$Change: 788842 $--%>

<%--
  This page renders Profile's checkout preferences: 
  default shipping method, default credit card, and default shipping address.
  
  Required parameters:
    None
    
  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/store/profile/ProfileCheckoutPreferences"/>
  
    <div id="atg_store_checkoutPrefs">
      <h3 class="atg_store_subHeadCustom">
        <fmt:message key="common.expressCheckoutPreferences"/>
      </h3>
    
      <ul class="atg_store_infoList atg_store_curentPref">
        <%-- Default shipping method --%>
        <li>
        <span class="atg_store_curentPrefLabel">
          <fmt:message key="common.defaultShippingMethod"/>
          <dsp:getvalueof var="defaultCarrier" vartype="java.lang.String" bean="Profile.defaultCarrier"/>
        </span>
        <span class="atg_store_curentPrefItem">  
          <c:choose>
            <c:when test="${not empty defaultCarrier}">
              <fmt:message key="checkout_shipping.delivery${fn:replace(defaultCarrier, ' ', '')}"/>
            </c:when>
            
            <%-- Display message if default shipping method is not defined --%>
            <c:otherwise>
              <fmt:message key="common.notSpecified"/>
            </c:otherwise>
          </c:choose>
        </span>
        </li>
        <%-- Default shipping address --%>
        <li>
          <span class="atg_store_curentPrefLabel">
          <fmt:message key="common.defaultShippingAddress"/>
          <%-- Display Default Address Nickname--%>
          <dsp:getvalueof var="profileShippingAddress" bean="Profile.shippingAddress"/>
        </span>
        <span class="atg_store_curentPrefItem">
          <c:choose>
            <c:when test="${not empty profileShippingAddress}">
              <dsp:getvalueof var="shippingAddress" bean="ProfileCheckoutPreferences.defaultShippingAddressNickname"/>
              <%-- Trim long nicknames to 26 symbols --%>
              <crs:trim message="${shippingAddress}" length="26"/>
            </c:when>

            <%-- Display message if default shipping address is not defined --%>
            <c:otherwise>
              <fmt:message key="common.notSpecified"/>
            </c:otherwise>
          </c:choose>
        </span>
        </li>
        
        <%-- Default credit card --%>
        <li>
        <span class="atg_store_curentPrefLabel">
          <fmt:message key="common.defaultCreditCard"/>
          <%-- Display Default Credit Card Nickname--%>
          <dsp:getvalueof var="profileDefaultCreditCard" bean="Profile.defaultCreditCard"/>
        </span>
        <span class="atg_store_curentPrefItem">
          <c:choose>
            <c:when test="${not empty profileDefaultCreditCard}">
              <dsp:valueof bean="ProfileCheckoutPreferences.defaultCreditCardNickname"/>
              <fmt:message key="common.textSeparator"/>
              <dsp:getvalueof var="creditCardNumber" bean="Profile.defaultCreditCard.creditCardNumber" />
              
              <%-- Display only last 4 digits of the credit card --%>
              <crs:trim message="${creditCardNumber}" length="4" fromEnd="true"/>
            </c:when>
            
            <%-- display message if default credit card is not defined --%>
            <c:otherwise>
              <fmt:message key="common.notSpecified"/>
            </c:otherwise>
          </c:choose>
        </span>
        </li>
      </ul>
      
      <%-- Link to 'profile defaults' page --%>
      <div class="atg_store_formActions">
        <a href="profileDefaults.jsp" class="atg_store_basicButton atg_store_prefEdit">
         <span><fmt:message key="common.button.editText"/></span>
        </a>
      </div>   
    </div>
  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/profileCheckOutPrefs.jsp#1 $$Change: 735822 $--%>

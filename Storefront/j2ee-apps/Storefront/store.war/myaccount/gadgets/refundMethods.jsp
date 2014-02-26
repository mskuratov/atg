<%--
  This page displays refund payment methods (credit cart or store credit). For each payment method
  the details are displayed and the amount applied to the refund method.
  
  Required parameters:
    returnRequest
      The ReturnReqeust object to display refund summary for.
      
  Optional parameters:
    priceListLocale
      Specifies a locale in which to format the price (as string).
      If not specified, locale will be taken from profile price list (Profile.priceList.locale).  
--%>
<dsp:page>

  <dsp:getvalueof var="returnRequest" param="returnRequest"/>
  
  <div id="atg_store_refundMethods" class="atg_store_returnSummaryPart">
  
    <%-- Refund Methods title. --%>
    <h4>
      <fmt:message key="myaccount_refundMethods_title"/><fmt:message key="common.labelSeparator"/>
    </h4>
    
    <ul>
      <c:forEach var="refundMethod" items="${returnRequest.refundMethodList}">
        <c:if test="${refundMethod.amount > 0 }">
          <c:choose>
            <c:when test="${refundMethod.refundType eq 'creditCard'}">
            
              <%--
                For credit card refund method display credit card's type and 4 last digits of
                credit card number.
              --%>
              <li id="atg_store_refundCreditCardNumber">
                <%-- Amount applied to the credit card refund method. --%>
                <span id="refundAmountOn">
                  <span id="refundAmount">
                    <dsp:include page="/global/gadgets/formattedPrice.jsp">
                      <dsp:param name="price" value="${refundMethod.amount}"/>
                      <dsp:param name="priceListLocale" param="priceListLocale" />
                    </dsp:include>
                  </span>
                  
                  <fmt:message key="myaccount_refundMethods_amountOnCreditCard.on"/>
                </span>
                
                <span id="creditCardNumber">
                  <dsp:getvalueof var="creditCardType" value="${refundMethod.creditCard.creditCardType}"/>
                  <fmt:message key="myaccount_refundMethods_creditCardTypeNumber_format">
                    <fmt:param>
                      <fmt:message key="common.${creditCardType}"/>
                    </fmt:param>
                    <fmt:param>
                      <crs:trim message='${refundMethod.creditCard.creditCardNumber}' length='4' fromEnd='true'/>
                    </fmt:param>
                  </fmt:message>
                </span>
                        
              </li>
              
              <%-- Expiration date. --%>
              <li class="atg_store_refundCreditCardExpDate">
                <div id="credCardExpDate">
                  <span id="credCardExpDateLabel"><fmt:message key="global_displayCreditCard.expDate"/>:</span>
                  
                  <fmt:formatNumber var="expirationMonth" minIntegerDigits="2" value="${refundMethod.creditCard.expirationMonth}" />
                  <c:set var="expirationYear" value="${refundMethod.creditCard.expirationYear}"/>
                        
                  <fmt:message key="myaccount.creditCardExpShortDate">
                    <fmt:param value="${expirationMonth}"/>
                    <fmt:param value="${expirationYear}"/>
                  </fmt:message>
                </div>
              </li>
              
              <%-- Also display billing address for the credit card.  --%>
              <li id="atg_store_creditCardBillingAddress">
                <dsp:include page="/global/util/displayAddress.jsp">
                  <dsp:param name="address" value="${refundMethod.creditCard.billingAddress}"/>
                  <dsp:param name="displayName" value="true"/>
                </dsp:include>
              </li>
            
            </c:when>
            <c:when test="${refundMethod.refundType eq 'storeCredit' }">
            
              <%-- Store credit refund method. --%>
              <li id="atg_store_refundStoreCredit">
                
                <%-- Amount applied to the store credit refund method. --%>
                <span id="refundAmountOn">
                  <span id="refundAmount">
                    <dsp:include page="/global/gadgets/formattedPrice.jsp">
                      <dsp:param name="price" value="${refundMethod.amount}"/>
                      <dsp:param name="priceListLocale" param="priceListLocale" />
                    </dsp:include>
                  </span>
                
                  <fmt:message key="myaccount_refundMethods_amountOnCreditCard.on"/>
                </span>
                
                <span id="storeCreditTitle">
                  <fmt:message key="common.storeCredit"/><fmt:message key="common.labelSeparator"/>
                </span>            
              </li>
              <li id="atg_store_refundStoreCreditNote">
                <div class="creditNoteMessage"><fmt:message key="myaccount_refundMethods_storeCreditNote"/></div>
              </li>
            </c:when>
            
          </c:choose>
        
        </c:if>
      
      </c:forEach>
    </ul>
  </div>
  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/refundMethods.jsp#4 $$Change: 791295 $--%>
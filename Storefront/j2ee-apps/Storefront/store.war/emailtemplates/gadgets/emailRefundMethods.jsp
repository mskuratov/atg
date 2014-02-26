<%--
  This page displays refund payment methods (credit cart or store credit). For each payment method
  the details are displayed and the amount applied to the refund method. The gadget is intended
  to be used inside email templates and has inline styles.
  
  Required parameters:
    returnRequest
      The ReturnReqeust object to display refund summary for.
      
  Optional parameters:
    priceListLocale
      The locale to use for price formatting  
--%>
<dsp:page>

  <dsp:getvalueof var="returnRequest" param="returnRequest"/>
  
  <%-- Refund Methods title. --%>
  <table style="color:#000;font-family:Tahoma,Arial,sans-serif;font-size:12px;">
  <tr>
  <td>
  <span style="font-size:18px"><fmt:message key="emailtemplates_refundMethods_title"/><fmt:message key="common.labelSeparator"/></span>
      
  <br /><br />

  </td>
  </tr>
  
  <%-- Iterate through refund method list --%>
  <c:forEach var="refundMethod" items="${returnRequest.refundMethodList}">
    <c:choose>
      <c:when test="${refundMethod.refundType eq 'creditCard'}">
          
        <%--
          For credit card refund method display credit card's type and 4 last digits of
          credit card number.
        --%>
        
        <tr>
          <td style="font-size:13px;font-weight:bold">
        <%-- Amount applied to the credit card refund method. --%>
        <span>
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${refundMethod.amount}"/>
            <dsp:param name="priceListLocale" param="priceListLocale"/>
          </dsp:include>
              
          <span style="font-weight:normal;color:#666"><fmt:message key="emailtemplates_refundMethods_amountOnCreditCard.on"/></span>

        </td>
        <td style="font-weight:bold">
              
          <span>
          
            <dsp:getvalueof var="creditCardType" value="${refundMethod.creditCard.creditCardType}"/>
            <fmt:message key="emailtemplates_refundMethods_creditCardTypeNumber_format">
              <fmt:param>
                <fmt:message key="common.${creditCardType}"/>
              </fmt:param>
              <fmt:param>
                <crs:trim message='${refundMethod.creditCard.creditCardNumber}' length='4' fromEnd='true'/>
              </fmt:param>
            </fmt:message>
          </span>     
        </span>
      </td>
    </tr>
    
    <tr>
      <td>&nbsp;</td>
      <td>
        <strong><fmt:message key="global_displayCreditCard.expDate"/>:</strong>
        
        <fmt:formatNumber var="expirationMonth" minIntegerDigits="2" value="${refundMethod.creditCard.expirationMonth}" />
        <c:set var="expirationYear" value="${refundMethod.creditCard.expirationYear}"/>        
      
        <fmt:message key="myaccount.creditCardExpShortDate">
          <fmt:param value="${expirationMonth}"/>
          <fmt:param value="${expirationYear}"/>
        </fmt:message>
      </td>
    </tr>
    
    <tr>
      <td>&nbsp;</td>
      <td>

        <br/>
            
        <%-- Also display billing address for the credit card.  --%>
        <span style="color:#666">
        <dsp:include page="/global/util/displayAddress.jsp">
          <dsp:param name="address" value="${refundMethod.creditCard.billingAddress}"/>
          <dsp:param name="displayName" value="true"/>
        </dsp:include>
        <span>

        <br/>

      </td>
    </tr>
          
      </c:when>

      <c:when test="${refundMethod.refundType eq 'storeCredit' }">
          
        <%-- Store credit refund method. --%>
        <tr>
        <td style="font-size:13px;font-weight:bold">
          <%-- Amount applied to the store credit refund method. --%>
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${refundMethod.amount}"/>
            <dsp:param name="priceListLocale" param="priceListLocale"/>
          </dsp:include>

          <span style="font-weight:normal;color:#666"><fmt:message key="emailtemplates_refundMethods_amountOnCreditCard.on"/></span>
        </td>
        <td style="font-weight:bold">
            <fmt:message key="common.storeCredit"/><fmt:message key="common.labelSeparator"/>
        </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td>
            <br />
            <span style="color:#666">
            <fmt:message key="emailtemplates_refundMethods_storeCreditNote"/>
            </span>
            <br /><br />
          </td>
        </tr>
        
      </c:when>
          
    </c:choose>
      
  </c:forEach>

</table>
    
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/emailRefundMethods.jsp#3 $$Change: 788844 $--%>
<%--
  This gadget renders payment info from a payment group. Payment group passed should be a credit card.

  Required parameters:
    paymentGroup
      Payment group to be displayed.

  Optional parameters:
    isCurrent
      Flags, if payment group specified belongs to current shopping cart, or not.
    isExpressCheckout
      Flags, if this gadget is a part of express checkout Confirmation page.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>

  <dsp:getvalueof var="contextroot" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="isCurrent" param="isCurrent"/>
  <dsp:getvalueof var="isExpressCheckout" param="isExpressCheckout"/>

  <dl class="atg_store_groupBillingAddress">
    <dt>
      <fmt:message key="checkout_confirmPaymentOptions.billTo"/><fmt:message key="common.labelSeparator"/>
    </dt>
    <dd>
      
      <%-- First, display billing address for the credit card specified. --%>
      <dsp:include page="/global/util/displayAddress.jsp">
        <dsp:param name="address" param="paymentGroup.billingAddress"/>
      </dsp:include>
      
      <%-- If it's a Confirmation page, display link to the 'Change Billing Details' page. --%>
      <c:if test="${isCurrent}">
        <dsp:a page="/checkout/billing.jsp" title="">
          <span><fmt:message key="common.button.editText"/></span>
        </dsp:a>
      </c:if>
    
    </dd>
  </dl>

  <%-- Now display credit-card-specific parameters. --%>
  <dl class="atg_store_groupPayment">
    <dt>
      <fmt:message key="checkout_confirmPaymentOptions.payment"/><fmt:message key="common.labelSeparator"/>
    </dt>
    <%-- Card type. --%>
    <dd class="atg_store_groupPaymentCardType">
      
      <dsp:getvalueof var="creditCard" param="paymentGroup.creditCardNumber" />
      
      <dsp:getvalueof var="creditCardType" param="paymentGroup.creditCardType"/>
      
      <fmt:message key="global_displayCreditCard.endingIn">
        <fmt:param>
          <fmt:message key="common.${creditCardType}"/>
        </fmt:param>
        <fmt:param>
          <crs:trim message='${creditCard}' length='4' fromEnd='true'/>
        </fmt:param>
      </fmt:message>
      
    </dd>
    <%-- Expiration date. --%>
    <dd class="atg_store_groupPaymentCardExp">
      
      <strong><fmt:message key="global_displayCreditCard.expDate"/>:</strong>
      
      <dsp:getvalueof var="var_expirationMonth" vartype="java.lang.String" param="paymentGroup.expirationMonth"/>
      <dsp:getvalueof var="var_expirationYear" vartype="java.lang.String" param="paymentGroup.expirationYear"/>
      
      <%-- 
        So that the expiration date doesn't show "null/null" when the payment group 
        values haven't been defined, display integer values.
      --%>
      <c:if test="${empty var_expirationMonth}">
        <c:set var="var_expirationMonth" value="00"/>
      </c:if>
      <c:if test="${empty var_expirationYear}">
        <c:set var="var_expirationYear" value="0000"/>
      </c:if>

      <fmt:formatNumber minIntegerDigits="2" value="${var_expirationMonth}" var="formattedMonthValue"/>
      
      <fmt:message key="myaccount.creditCardExpShortDate">
        <fmt:param value="${formattedMonthValue}"/>
        <fmt:param value="${var_expirationYear}"/>
      </fmt:message>
      
      <c:if test="${isCurrent}">
        <dsp:a page="/checkout/billing.jsp" title="">
          <span><fmt:message key="common.button.editText" /></span>
        </dsp:a>
      </c:if>

    </dd>

    <%-- If it's express checkout Confirmation page, display an input field for CVC/CCV security code. --%>
    <c:if test="${isCurrent && isExpressCheckout}">
      <dd class="atg_store_groupPaymentCardCSV">
        
        <label for="atg_store_verificationNumberInput"><fmt:message key="checkout_billing.securityCode"/></label>
        
        <dsp:input bean="CommitOrderFormHandler.creditCardVerificationNumber" value="" type="text"
                   iclass="required" id="atg_store_verificationNumberInput" autocomplete="off" 
                   dojoType="atg.store.widget.enterSubmit" targetButton="atg_store_placeMyOrderButton" />
        
        <fmt:message var="whatisThisTitle" key="checkout_billing.whatIsThis"/>
        
        <a href="${contextroot}/checkout/whatsThisPopup.jsp" title="${whatisThisTitle}" 
           class="atg_store_help" target="popup">
          ${whatisThisTitle}
        </a>
      </dd>
    </c:if>
  </dl>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/paymentGroupRenderer.jsp#3 $$Change: 788842 $--%>
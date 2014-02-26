<%-- 
  This gadget renders the payment groups of a type specified by the paymentGroupType parameter.
  The payment types can be either 'creditCard' or 'storeCredit'. 
  
  Required parameters: 
    order
      The order which payment groups should be rendered
    paymentGroupType
      The payment group type to render ('creditCard' or 'storeCredit' are expected).
      
  Optional parameters:
    priceListLocale
      The locale to use for prices formatting.
--%>
<dsp:page>
  
  <dsp:getvalueof var="requestedPaymentGroupType" vartype="java.lang.String" param="paymentGroupType"/>
  
  <%-- Get all payment group relationships from order. --%>
  <dsp:getvalueof var="paymentGroupRelationships" vartype="java.lang.Object" param="order.paymentGroupRelationships"/>
  
  <%--
    Loop through all payment group relationships to find payment groups
    of the specified type.
   --%>
  <c:forEach var="paymentGroupRelationship" items="${paymentGroupRelationships}">
    <dsp:param name="rel" value="${paymentGroupRelationship}"/>
    <%-- Get payment group from relationship --%>
    <dsp:setvalue param="paymentGroup" paramvalue="rel.paymentGroup"/>

    <dsp:getvalueof var="paymentGroupType" param="paymentGroup.paymentGroupClassType"/>
    <c:if test="${paymentGroupType == requestedPaymentGroupType}">
      <%--
        The payment group has the specified type, render its details.
        To know how to display payment group details we need
        to know with which exactly payment type we are dealing.
       --%>
      <c:choose>
        <c:when test="${paymentGroupType == 'creditCard'}">
          <%-- Credit card payment type --%>
          <dsp:getvalueof var="creditCard" param="paymentGroup.creditCardNumber" />
          <dsp:getvalueof var="creditCardType" param="paymentGroup.creditCardType"/>           
          <fmt:message key="global_displayCreditCard.endingIn">
            <fmt:param>
              <fmt:message key="common.${creditCardType}"/>
            </fmt:param>
            <fmt:param>
              <crs:trim message="${creditCard}" length="4" fromEnd="true"/>
            </fmt:param>
          </fmt:message>

          <br />
          
          <%-- Card's expiration date --%>
          <span style="font-weight:bold;color:#000000;">
             <fmt:message key="emailtemplates_orderConfirmation.exp"/>
          </span>    
          
         <dsp:getvalueof var="var_expirationMonth" vartype="java.lang.String" param="paymentGroup.expirationMonth"/>
         <dsp:getvalueof var="var_expirationYear" vartype="java.lang.String" param="paymentGroup.expirationYear"/>
         <fmt:formatNumber minIntegerDigits="2" value="${var_expirationMonth}" var="formattedMonthValue"/>
         <fmt:message key="myaccount.creditCardExpShortDate">
           <fmt:param value="${formattedMonthValue}"/>
           <fmt:param value="${var_expirationYear}"/>
         </fmt:message>
        </c:when>

        <c:when test="${paymentGroupType == 'storeCredit'}">
          <%-- Store credit payment type --%>
          <div style="margin-bottom: 10px;">
            <span style="font-weight: bold;">
              <fmt:message key="common.storeCredit"/><fmt:message key="common.labelSeparator"/>
            </span>
            <%-- Store credit's number --%> 
            <dsp:valueof param="paymentGroup.storeCreditNumber"/>
            <br />
            <%-- Store credit's amount used. --%>
            <span style="font-weight: bold;">
              <fmt:message key="common.amount"/><fmt:message key="common.labelSeparator"/>
            </span>
            <%-- Format store credit's amount using the price list's locale --%>
            <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <dsp:param name="price" param="paymentGroup.amount"/>
              <dsp:param name="priceListLocale" param="priceListLocale"/>
            </dsp:include>
          </div>
        </c:when>
      </c:choose>
    </c:if>
  </c:forEach>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/emailOrderPaymentRenderer.jsp#2 $$Change: 788842 $--%>
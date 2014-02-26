<%--
  Order confirmation email template. Contains an order details including shipping, billing
  information and order contents.
  
  Required parameters:
    message
      The message set by scenario that is used to retrieve order (either message or order parameter are
      required)
    order
      The order submitted (either message or order parameter are required). Used by Template Email Tester
    
  Optional parameters:
    locale
      Locale that specifies in which language email should be rendered.    
 --%>
<dsp:page>
  
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <%-- Get sender email address from site configuration --%>
  <dsp:getvalueof var="orderConfirmationFromAddress" bean="Site.orderConfirmationFromAddress" />
  
  <%--
    When order confirmation is sent through Email Template Tester order is passed to template
    directly through order parameter, when email is sent through scenario order should be
    retrieved from the message parameter.
   --%>
  <dsp:getvalueof var="message" param="message"/>
  <%--
    Check if message is not empty so that not to override order parameter
    when template is used by Email Template Tester.
   --%>
  <c:if test="${not empty message }">
    <dsp:setvalue param="order" paramvalue="message.order"/>
  </c:if>
  
  <%-- 
    Determine order ID, at first get OMS order ID, if it's not specified 
    get order's repository ID.
   --%>
  <dsp:getvalueof var="orderId" param="order.omsOrderId"/>
  <c:if test="${empty orderId}">
    <dsp:getvalueof var="orderId" param="order.id"/>
  </c:if>

  <%-- Email subject --%>
  <fmt:message var="emailSubject" key="emailtemplates_orderConfirmation.subject">
    <fmt:param>
      <dsp:valueof bean="Site.name" />
    </fmt:param>
    <fmt:param>${orderId}</fmt:param>
  </fmt:message>
  
  <%-- Email title --%>
  <fmt:message var="emailTitle" key="emailtemplates_orderConfirmation.title">
    <fmt:param>
      <dsp:valueof bean="Site.name" />
    </fmt:param>
  </fmt:message>
  
  <crs:emailPageContainer divId="atg_store_orderConfirmationIntro" 
                          titleString="${emailTitle}" 
                          messageSubjectString="${emailSubject}"
                          messageFromAddressString="${orderConfirmationFromAddress}">
    <jsp:body>
      <table border="0" cellpadding="0" cellspacing="0" width="100%" summary="" role="presentation"
             style="margin-bottom:4px;color:#666;font-family:Tahoma,Arial,sans-serif;font-size:14px; border-collapse: collapse;">
        <tr>
          <td colspan="5" 
              style="color:#666;font-family:Tahoma,Arial,sans-serif;font-size:14px;padding-bottom:20px">
          
            <%-- Display link to order details only for registered users --%>
            <dsp:getvalueof var="isTransient" bean="Profile.transient"/>
            <c:if test="${!isTransient}">
              <fmt:message key="emailtemplates_orderShipped.track">
                <fmt:param>
                  <fmt:message var="linkText" key="emailtemplates_orderConfirmation.orderDetails"/>
                  <dsp:include page="/emailtemplates/gadgets/emailSiteLinkDisplay.jsp">
                    <dsp:param name="path" value="/myaccount/gadgets/loginOrderDetail.jsp"/>
                    <dsp:param name="queryParams" value="orderId=${orderId}"/>
                    <dsp:param name="locale" param="locale"/>
                    <dsp:param name="httpServer" param="httpServer"/>
                    <dsp:param name="linkText" value="${linkText}"/>
                  </dsp:include>
                </fmt:param>
              </fmt:message>
            </c:if>
          </td>
        </tr>
        
        <%-- Order Number --%>
        <tr>
          <td style="color:#666;font-family:Tahoma,Arial,sans-serif;font-size:16px;font-weight:bold;padding-bottom:10px;">
            <fmt:message key="emailtemplates_orderConfirmation.orderNumber"/>
          </td>
          <td style="color:#000;font-family:Tahoma,Arial,sans-serif;font-size:12px;font-weight:bold;padding-bottom:10px;">
            ${orderId}
          </td>
        </tr>
        
        <%-- Order's submission date. --%>
        <tr>
          <td style="color:#666;font-family:Tahoma,Arial,sans-serif;font-size:16px;font-weight:bold;padding-bottom:10px;padding-right:5px;white-space:nowrap;">
            <fmt:message key="emailtemplates_orderConfirmation.placedOn"/>
          </td>
          <td style="color:#000;font-family:Tahoma,Arial,sans-serif;font-size:12px;font-weight:bold;padding-bottom:10px;white-space:nowrap;">
            <dsp:getvalueof var="submittedDate" vartype="java.util.Date" param="order.submittedDate"/>
            <fmt:formatDate value="${submittedDate}" type="both" dateStyle="long"/>
          </td>
        </tr>
        
        <%-- Order's current state --%>
        <tr>
          <td style="color:#666;font-family:Tahoma,Arial,sans-serif;font-size:16px;font-weight:bold;padding-bottom:10px;">
            <fmt:message key="emailtemplates_orderConfirmation.status"/>
          </td>
          <td style="color:#000;font-family:Tahoma,Arial,sans-serif;font-size:12px;font-weight:bold;padding-bottom:10px;">
            <dsp:include page="/global/util/orderState.jsp">
              <dsp:param name="order" param="order"/>
            </dsp:include>
          </td>
        </tr>
      
        <%-- Billing Info --%>
        
        <%-- 
          Retrieve the price lists's locale used for order. It will be used to
          format prices correctly. 
          We can't use Profile's price list here as it will not work in CSC where orders can be
          submitted by agent profile. So extract price list's locale from commerce item's price info.
        --%>
        <dsp:getvalueof var="commerceItems" vartype="java.lang.Object" param="order.commerceItems"/>     
        <c:if test="${not empty commerceItems}">
          <dsp:tomap var="priceListMap" param="order.commerceItems[0].priceInfo.priceList" />
          <dsp:getvalueof var="priceListLocale" vartype="java.lang.String" value="${priceListMap.locale}"/>
        </c:if>
        <tr>
          <td valign="top" style="color:#666;font-family:Tahoma,Arial,sans-serif;font-size:16px;font-weight:bold;padding-bottom:20px;">
            <fmt:message key="emailtemplates_orderConfirmation.billTo"/>
          </td>
          <td style="color:#000;font-family:Tahoma,Arial,sans-serif;font-size:12px;padding-bottom:20px;">
  
            <%-- Credit Card Info --%>
            <dsp:include page="/emailtemplates/gadgets/emailOrderPaymentRenderer.jsp">
              <dsp:param name="order" param="order"/>
              <dsp:param name="paymentGroupType" value="creditCard"/>
              <dsp:param name="priceListLocale" value="${priceListLocale}"/>
            </dsp:include>
  
            <%-- Store Credit Info --%>
            <dsp:include page="/emailtemplates/gadgets/emailOrderPaymentRenderer.jsp">
              <dsp:param name="order" param="order"/>
              <dsp:param name="paymentGroupType" value="storeCredit"/>
              <dsp:param name="priceListLocale" value="${priceListLocale}"/>
            </dsp:include>
          </td>
        </tr> 
        <tr>
          <td colspan="5">
            <hr size="1">
          </td>
        </tr>
                  
        <%-- Shipping Info & Order Contents --%>
        <dsp:include page="/emailtemplates/gadgets/emailOrderContents.jsp">
          <dsp:param name="priceListLocale" value="${priceListLocale}"/>
          <dsp:param name="httpServer" param="httpServer"/>
        </dsp:include>
              
      </table>
      <br />
    </jsp:body>
    
  </crs:emailPageContainer>

</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/orderConfirmation.jsp#1 $$Change: 735822 $--%>

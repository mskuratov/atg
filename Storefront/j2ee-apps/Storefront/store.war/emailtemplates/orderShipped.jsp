<%--
  Order shipped email template. Contains an shipping group's details including shipping address,
  commerce items shipped to this shipping address and shipping subtotal, tax and shipping charges.
  
  Required parameters:
    message
      The message that is set by scenario and used to retrieve order and shipping group (either message 
      or order parameter are required)
    order
      The order submitted (either message or order parameter are required). Used by Email Template Tester
    shippingGroup
      The shipping group to display details for (either message or shippingGroup parameter is required). Used
      by Email Template Tester.    
    
  Optional parameters:    
    locale
      Locale that specifies in which language email should be rendered.
 --%>
<dsp:page>

  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>

  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <%-- Get sender email address from site configuration --%>
  <dsp:getvalueof var="orderShippedFromAddress" bean="Site.orderShippedFromAddress" />
  
  <%--
    When order shipped email is sent through Email Template Tester order and shipping group
    are passed to template directly through order and shippingGroup parameters, when email 
    is sent through scenario order/shipping group should be retrieved from the message parameter.
   --%>
  
  <dsp:getvalueof var="message" param="message"/>
  
  <%--
    Check if message is not empty so that not to override order parameter
    when template is used by Email Template Tester.
   --%>
  <c:if test="${not empty message }">
    <dsp:setvalue param="order" paramvalue="message.order"/>
    <dsp:setvalue param="shippingGroup" paramvalue="message.shippingGroup"/>
  </c:if>
  
  <%-- 
    Retrieve the price lists's locale used for order. It will be used to
    format prices correctly. 
    We can't use Profile's price list here as it will not work in CSC where orders can be
    submitted by agent profile. So extract price list's locale from commerce item's price info.
   --%>
  <dsp:getvalueof var="commerceItems" vartype="java.lang.Object" param="message.order.commerceItems"/>     
  <c:if test="${not empty commerceItems}">
    <dsp:getvalueof var="priceListLocale" vartype="java.lang.String" param="message.order.commerceItems[0].priceInfo.priceList.locale"/>
  </c:if>

  <%-- Email subject --%>
  <fmt:message var="emailSubject" key="emailtemplates_orderShipped.subject">
    <fmt:param>
      <dsp:valueof bean="Site.name" />
    </fmt:param>
    <fmt:param>
      <dsp:valueof param="order.omsOrderId"/>
    </fmt:param>
  </fmt:message>
  
  <crs:emailPageContainer divId="atg_store_orderConfirmationIntro" 
                          titleKey="emailtemplates_orderShipped.title" 
                          messageSubjectString="${emailSubject}"
                          messageFromAddressString="${orderShippedFromAddress}">
    <jsp:body>
      <table border="0" cellpadding="0" cellspacing="0" width="100%" style="margin-top:20px;margin-bottom:4px;color:#666;font-family:Tahoma,Arial,sans-serif;font-size:14px; border-collapse: collapse;"
             summary="" role="presentation">
        <tr>
          <td colspan="5" 
              style="color:#666;font-family:Tahoma,Arial,sans-serif;font-size:14px;padding-bottom:20px">
  
            <%-- Display link to order details only for registered users --%>
            <dsp:getvalueof var="isTransient" bean="Profile.transient"/>
            <c:if test="${!isTransient}">
              <fmt:message key="emailtemplates_orderShipped.track">
                <fmt:param>
                  <fmt:message var="linkText" key="emailtemplates_orderConfirmation.orderDetails"/>
                  <dsp:getvalueof var="orderId" param="order.omsOrderId"/>
                  <dsp:include page="/emailtemplates/gadgets/emailSiteLinkDisplay.jsp">
                    <dsp:param name="path" value="/myaccount/gadgets/loginOrderDetail.jsp"/>
                    <dsp:param name="httpServer" param="httpServer"/>
                    <dsp:param name="locale" param="locale"/>
                    <dsp:param name="queryParams" value="orderId=${orderId}"/>              
                    <dsp:param name="linkText" value="${linkText}"/>
                  </dsp:include>
                </fmt:param>
              </fmt:message>
            </c:if>
          </td>
        </tr>
        
        <%-- Order ID --%>
        <tr>
          <td style="color:#666;font-family:Tahoma,Arial,sans-serif;font-size:16px;font-weight:bold;padding-bottom:10px; white-space: nowrap;">
            <fmt:message key="emailtemplates_orderShipped.orderNumber"/>
          </td>
          <td colspan="4" 
              style="color:#000;font-family:Tahoma,Arial,sans-serif;font-size:12px;font-weight:bold;padding-bottom:10px;">
            <dsp:valueof param="order.omsOrderId"/>
          </td>
        </tr>
        
        <%-- Order's submission date --%>
        <tr>
          <td style="color:#666;font-family:Tahoma,Arial,sans-serif;font-size:16px;font-weight:bold;padding-bottom:10px; white-space: nowrap;">
            <fmt:message key="emailtemplates_orderShipped.placedOn"/>
          </td>
          <td colspan="4" 
              style="color:#000;font-family:Tahoma,Arial,sans-serif;font-size:12px;font-weight:bold;padding-bottom:10px;">
            <dsp:getvalueof var="submittedDate" vartype="java.util.Date" param="order.submittedDate"/>
            <fmt:formatDate value="${submittedDate}" type="both" dateStyle="long"/>
          </td>
        </tr>
        
        <%-- Order's state --%>
        <tr>
          <td style="color:#666;font-family:Tahoma,Arial,sans-serif;font-size:16px;font-weight:bold;padding-bottom:10px; white-space: nowrap;">
            <fmt:message key="emailtemplates_orderShipped.status"/>
          </td>
          <td colspan="4" 
              style="color:#000;font-family:Tahoma,Arial,sans-serif;font-size:12px;font-weight:bold;padding-bottom:10px;">
            <dsp:include page="/global/util/orderState.jsp">
              <dsp:param name="order" param="order"/>
            </dsp:include>
          </td>
        </tr>
  
        <tr style="margin-top:20px;padding-bottom:10px">
          <td colspan="5">
            <hr size="1">
          </td>
        </tr> 
                  
        <%-- Shipping Info & Order Contents --%>
        <dsp:include page="/emailtemplates/gadgets/shippingGroupRenderer.jsp">
          <dsp:param name="shippingGroup" param="shippingGroup"/>
          <dsp:param name="locale" param="locale"/>
          <dsp:param name="httpServer" param="httpServer"/>
          <dsp:param name="excludeGiftWrap" value="false"/>
        </dsp:include>
        
        <%-- Render shipping group's pricing information --%>
        <dsp:include page="/emailtemplates/gadgets/emailOrderSubtotalRenderer.jsp">
          <dsp:param name="order" param="order"/>
          <dsp:param name="shippingGroup" param="shippingGroup"/>
          <dsp:param name="priceListLocale" value="${priceListLocale}"/>
        </dsp:include>          

      </table>
      <br />
    </jsp:body>
    
  </crs:emailPageContainer>

</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/orderShipped.jsp#1 $$Change: 735822 $--%>


<%--
  Return received email template. Contains return details including items returned, quantity,
  return reason, per item refund amounts and refund summary.
  
  Required parameters:
    message
      The message set by scenario that is used to retrieve returnRequest (either message or returnRequest and returnItemToQuantityReceived
      parameters are
      required)
    returnRequest
      The return submitted (either message or returnRequest parameter are required). Used by Template Email Tester
    returnItemToQuantityReceived
      The map where the keys are return Item IDs and values are received quantity of the item.  Used by Template Email Tester
    
  Optional parameters:
    locale
      Locale that specifies in which language email should be rendered.
--%>
<dsp:page>
  
  <dsp:importbean bean="/atg/multisite/Site"/>

  <%-- Get sender email address from site configuration --%>
  <dsp:getvalueof var="returnConfirmationFromAddress" bean="Site.orderConfirmationFromAddress" />
  
  <%--
    When return confirmation is sent through Email Template Tester ReturnRequest is passed to template
    directly through returnRequest parameter, when email is sent through scenario returnRequest should be
    retrieved from the message parameter.
  --%>
  <dsp:getvalueof var="message" param="message"/>
  
  <%--
    Check if message is not empty so that not to override returnRequest parameter
    when template is used by Email Template Tester.
  --%>
  <c:if test="${not empty message }">
    <dsp:setvalue param="returnRequest" paramvalue="message.returnRequest"/>
    <dsp:setvalue param="returnItemToQuantityReceived" paramvalue="message.returnItemToQuantityReceived"/>
  </c:if>
  
  <%-- 
    Determine returnRequest's ID.
  --%>
  <dsp:getvalueof var="returnRequestId" param="returnRequest.requestId"/>

  <%-- Email subject --%>
  <fmt:message var="emailSubject" key="emailtemplates_returnReceived.subject">
    <fmt:param>
      <dsp:valueof bean="Site.name" />
    </fmt:param>
    <fmt:param>${returnRequestId}</fmt:param>
  </fmt:message>
  
  <%-- Email title --%>
  <fmt:message var="emailTitle" key="emailtemplates_returnReceived.title">
    <fmt:param>
      <dsp:valueof bean="Site.name" />
    </fmt:param>
  </fmt:message>
  
  <crs:emailPageContainer divId="atg_store_orderConfirmationIntro" 
                          titleString="${emailTitle}" 
                          messageSubjectString="${emailSubject}"
                          messageFromAddressString="${returnConfirmationFromAddress}">
                          

    <jsp:body>
      <dsp:include page="/emailtemplates/gadgets/emailReturnDetails.jsp">
        <dsp:param name="returnRequest" param="returnRequest"/>
        <dsp:param name="httpServer" param="httpServer"/>
        <dsp:param name="locale" param="locale"/>
        <dsp:param name="returnItemToQuantityReceived" param="returnItemToQuantityReceived"/>
      </dsp:include>
     
      
    </jsp:body>
                          
  </crs:emailPageContainer>
    
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/returnReceived.jsp#1 $$Change: 788278 $--%>
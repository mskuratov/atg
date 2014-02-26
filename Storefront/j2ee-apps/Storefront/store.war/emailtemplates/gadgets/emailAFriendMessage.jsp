<%--
  This page display information about who is sending this email for recipient as well as
  the message mailed by him/her.
  
  Required parameters:
    product
      The product repository item to be featured in the greeting message
    recipientName
      Name of the Shopper who is receiving the Email
    senderName
      Name of the Shopper who is sending the Email
      
  Optional parameters:
    message
      Optional Message to be delivered as part of the Email
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/multisite/Site"/>

  <dsp:getvalueof var="recipientName" param="recipientName"/>
  <dsp:getvalueof var="message" param="message"/>
  <dsp:getvalueof var="productName" param="product.displayName"/>

  <%-- Display greeting message. --%>
  <div style="font-size:14px;color:#666;font-family:Tahoma,Arial,sans-serif;">
    <fmt:message key="emailtemplates_emailAFriend.greeting">
      <fmt:param value="${recipientName}"/>
    </fmt:message>
  </div>
  <br />

  <%-- Message about the product that is recommended by the sender. --%>
  <div style="font-size:14px;color:#666;font-family:Tahoma,Arial,sans-serif;">
    <fmt:message key="emailtemplates_emailAFriend.message">
      <fmt:param value="${productName}"/>
      <fmt:param>
        <dsp:valueof bean="/atg/multisite/Site.name"/>
      </fmt:param>
    </fmt:message>
  </div>
  <br />

  <%-- The Sender's message to the friend. --%>
  <c:if test="${not empty message}">
    <div style="font-size:14px;color:#666;font-family:Tahoma,Arial,sans-serif;">
      <dsp:valueof value="${message}"/>
    </div>
    <br />
  </c:if>
  
  <%-- Sender's name --%>
  <div style="font-size:14px;color:#666;font-family:Tahoma,Arial,sans-serif;">
    <dsp:valueof param="senderName"/>
  </div>
  
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/emailAFriendMessage.jsp#1 $$Change: 735822 $--%>

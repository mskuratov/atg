<%-- 
  Template that is used to render each cart message. 
  
  Required Parameters:
    currentMessage
      The message that is currently being processed.
--%>
<dsp:page>
  <json:object>
    <json:property name="title">
      <%-- Use message identifier as a title key --%>
      <dsp:getvalueof var="title" param="currentMessage.identifier"/>
      <fmt:message key="${title}"/>
    </json:property>
    
    <json:property name="text">
      <dsp:getvalueof var="text" param="currentMessage.summary"/>
      <c:out value="${text}" escapeXml="false"/>
    </json:property>
  </json:object>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/json/cartMessage.jsp#1 $$Change: 735822 $--%>
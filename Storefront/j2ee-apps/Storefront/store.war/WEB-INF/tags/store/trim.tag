<%--
  This tag trims long strings using the following logic:
  if 'message' is longer 'length' then return substring of 
  'message' with given 'length'. If 'fromEnd' is true, 
  then return substring from the end of 'message', otherwise
  return substring from the start.   
  
  Required parameters:
    message
      message to trim
    length
      how many symbols we want to leave  
    
  Optional parameters:
    fromEnd
      if true, tag trims 'message' from end, otherwise from start
  
--%>
<%@ include file="/includes/taglibs.jspf" %>
<%@ include file="/includes/context.jspf" %>

<%@attribute name="message" required="true"%>
<%@attribute name="length" required="true"%>
<%@attribute name="fromEnd"%>

<dsp:page>
  <c:choose>
    <c:when test="${fn:length(message) > length}">
      <c:choose>
        <c:when test="${not empty fromEnd and fromEnd == 'true' }">
          <c:out value="${fn:substring(message,fn:length(message)-length,fn:length(message))}"/>
        </c:when>
        <c:otherwise>
          <c:out value="${fn:substring(message,0,length)}..."/>
        </c:otherwise>
      </c:choose>
    </c:when>
    <c:otherwise>
      <c:out value="${message}"/>
    </c:otherwise>
  </c:choose>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/WEB-INF/tags/store/trim.tag#1 $$Change: 735822 $--%>
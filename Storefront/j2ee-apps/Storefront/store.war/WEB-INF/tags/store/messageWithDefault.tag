<%-- 
  Tag that looks up a message in a resource bundle based on a key value. 
  If the key is valid and matches a message resource it is returned in 'messageText', otherwise the 
  the key is returned in 'messageText' prefixed and suffixed with '???'.
  If no key was specified the default string is returned in 'messageText'. 

  Note message format specifiers are not supported for the resource bundle message entry.

  Required attributes:
    None.

  Optional attributes:
    key
      The key value to lookup the resource properties file.
    string
      The default text string if no match on the key is found.
--%>

<%@include file="/includes/taglibs.jspf"%>
<%@include file="/includes/context.jspf"%>

<%@ tag body-content="empty" %>

<%@ attribute name="key" required="false" %>
<%@ attribute name="string" required="false" %>

<%@ variable name-given="messageText" variable-class="java.lang.String" scope="AT_END" %>

<c:set var="messageText" value=""/>

<%--
  If the key was specified, check for the key value in the resource bundle.
  If there is no resource match on the key value, set the message text as the key prefixed and suffixed with '???'
--%>
<c:if test="${!empty key}">
  <c:set var="missingKeyResult" value="???${key}???"/>
  <fmt:message var="keyValue" key="${key}"/>
  <c:if test="${keyValue != missingKeyResult}">
    <c:set var="messageText" value="${keyValue}"/>
  </c:if>
</c:if>

<%--
  No key was specified so just return the default text string.
--%>
<c:if test="${empty messageText}">
  <c:set var="messageText" value="${string}"/>
</c:if>
<%-- @version $Id$$Change$--%>

<%--
  This tag renders a "message" box with message title, message text, and additional information.
  
  Required attributes:
    id 
      id for the atg_store_generalMessageContainer div.

  Optional attributes:
    titleKey 
      Resource bundle key for the message title.
    titleText 
      Message title
    messageKey 
      Resource bundle key for the message.
    messageText 
      The message text.
    optionalClass 
      Additional class for message container.
--%>

<%@ include file="/includes/taglibs.jspf" %>
<%@ include file="/includes/context.jspf" %>

<%@attribute name="id"%>
<%@attribute name="titleKey"%>
<%@attribute name="titleText"%>
<%@attribute name="messageKey"%>
<%@attribute name="messageText"%>
<%@attribute name="optionalClass"%>

<dsp:page>
  <div id="${id}" class="${optionalClass} atg_store_generalMessage">
      
      <%-- Message title --%>
      <c:choose>
        <c:when test="${not empty titleKey}">
          <h3>
            <fmt:message key="${titleKey}"/>
          </h3>
        </c:when>
        <c:when test="${not empty titleText and empty titleKey}">
          <h3>
            <c:out value="${titleText}" escapeXml="true"/>
          </h3>
        </c:when>
      </c:choose>
      
      <%-- Message text --%>
      <c:choose>
        <c:when test="${not empty messageKey}">
          <p>
            <fmt:message key="${messageKey}"/>
          </p>
        </c:when>
        <c:when test="${not empty messageText and empty messageKey}">
          <p>
            <c:out value="${messageText}" escapeXml="true"/>
          </p>
        </c:when>
      </c:choose>

      <%-- Additional content will be render here --%>
      <jsp:doBody/>
  </div>  
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/WEB-INF/tags/store/messageContainer.tag#1 $$Change: 735822 $--%>
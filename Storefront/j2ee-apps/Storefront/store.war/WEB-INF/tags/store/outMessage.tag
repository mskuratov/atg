<%--
  Tag using the StoreText droplet to perform a look up for a localized text resource message
  based on a supplied key attribute writing the resulting message to the page.

  Required attributes:
  key         
    The key code to use when looking up the message.

   Optional attributes:
     arg name  
       An arbitrary number of tag attributes used when populating any embedded
       format specifiers in the message text. The actual 'arg name' attribute
       is not defined; any attribute passed to the tag which is not tied to an
       explicitly named attribute is added to the dynamic 'argMap' map object
       with the attribute name/value pairs becoming the map key/entry values.
       The map is then passed to the StoreText droplet as the 'args' parameter.

  Usage:
    <crs:outMessage key={key} [{arg name}={arg value}]/>

  Example:
    <crs:getMessage key="common.storeName" var="storeName"/>
    <crs:outMessage key="company_aboutUs.aboutUs" storeName="${storeName}"/>
--%>

<%@include file="/includes/taglibs.jspf"%>
<%@include file="/includes/context.jspf"%>


<%@attribute name="key" required="true"%>
<%@attribute name="enablePreview" type="java.lang.Boolean"%>

<%@tag dynamic-attributes="argMap"%>


<dsp:importbean bean="/atg/store/droplet/StoreText"/>


<%--
  Obtain the localized resource text message matching the 'key' value. 
  
  Input parameters:
   key 
     The key code to use when looking up the resource message text in the repository.

   args (optional)
     A map object consisting of a number of entries to use when populating the format pattern specifiers embedded
     in the message template text. The format pattern specifiers in the message template are used to match on
     the map key values and the corresponding map entry value is substituted for the format pattern in the resulting
     message text.

   arg name (optional)
     An arbitrary number of parameters to use when populating the format pattern specifiers embedded in the message
     template text. The actual 'arg' name is not defined; the format pattern specifiers in the message template are
     used as possible parameter names and the parameter value is substituted for the format pattern in the resulting
     message text.

   defaultText (optional)
     The default text to output when a message for the given key could not be found.

  Output parameters:
    output
      message - the localized resource message text

    error
      message - error message if a problem occurred processing the request
--%>
<dsp:droplet name="StoreText">
  <dsp:param name="key" value="${key}"/>
  <dsp:param name="args" value="${argMap}"/>

  <dsp:oparam name="output">
    <dsp:getvalueof var="message" param="message"/>
    <c:choose>
      <c:when test="${!empty enablePreview and enablePreview}">
        <dsp:getvalueof var="item" param="item"/>
        <preview:repositoryItem item="${item}">
          <span><c:out value="${message}" escapeXml="false"/></span>
        </preview:repositoryItem>
      </c:when>
      <c:otherwise>
        <c:out value="${message}" escapeXml="false"/>
      </c:otherwise>
    </c:choose>
  </dsp:oparam>
  <dsp:oparam name="error">
    <dsp:getvalueof var="message" param="message"/>
    <c:out value="${message}" escapeXml="false"/>
  </dsp:oparam>
</dsp:droplet>
<%-- @version $Id$$Change$--%>

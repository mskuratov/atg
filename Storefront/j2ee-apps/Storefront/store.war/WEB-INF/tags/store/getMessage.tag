<%--
  Tag using the StoreText droplet to perform a look up for a localized text resource message
  matching on the supplied key attribute, storing the message in a page scope variable.

  Required attributes:
    key 
      The key code to use when looking up the message.
    var 
      The page parameter name to hold the message text result.

  Optional attributes:
    arg name    
      An arbitrary number of tag attributes used when populating any embedded format specifiers 
      in the message text. The actual 'arg name' attribute is not defined; any attribute passed 
      to the tag which is not tied to an explicitly named attribute is added to the dynamic 'argMap' 
      map object with the attribute name/value pairs becoming the map key/entry values.
      The map is then passed to the StoreText droplet as the 'args' parameter.

  Usage:
    <crs:getMessage key={key} var={var} [{arg name}={arg value}]/>

  Example:
    <crs:getMessage key="common.storeName" var="storeName"/>
    <crs:outMessage key="company_aboutUs.aboutUs" storeName="${storeName}"/>
--%>

<%@include file="/includes/taglibs.jspf"%>
<%@include file="/includes/context.jspf"%>


<%@attribute name="key" required="true"%>

<%@attribute name="var" required="true" rtexprvalue="false"%>
<%@variable name-from-attribute="var" alias="varAlias" scope="AT_END"%>

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
    <c:set var="varAlias" value="${message}"/>
  </dsp:oparam>
  <dsp:oparam name="error">
    <dsp:getvalueof var="message" param="message"/>
    <c:set var="varAlias" value="${message}"/>
  </dsp:oparam>
</dsp:droplet>
<%-- @version $Id$$Change$--%>

<%--
  This tag returns a CSS class name based on the specified string size. 
  It could be used to assign different font sizes to strings of different length.

  Page scope variables set:
    atg_string_medium
      For strings of medium size (default values are from 26 to 40 characters)
    atg_string_long
      For strings of long size (default values are from 41 characters)

  Required attributes:
    string 
      The string on which size should be examined.

  Optional attributes:
    mediumBeginIndex
      The starting point from which the string is considered to be of medium size; Defaults to 26.
    longBeginIndex 
      The starting point from which the string is considered to be of long size. Defaults to 41.        
    additionalClasses 
      Static CSS classes that will get returned as part of the CSS class.

  Example:
    <h2 class="title <crs:stringSizeClass string='${productName}' mediumBeginIndex='26' longBeginIndex='41'/>">

    Which, for a given header element, may end up getting parsed into HTML like:

    <tr class="title atg_string_long">
--%>

<%@include file="/includes/taglibs.jspf"%>
<%@include file="/includes/context.jspf"%>

<%@ tag body-content="empty" %>

<%@ attribute name="string" required="true" %>
<%@ attribute name="mediumBeginIndex" required="false" %>
<%@ attribute name="longBeginIndex" required="false" %>
<%@ attribute name="additionalClasses" required="false" %>

<dsp:page>
  <c:if test="${empty mediumBeginIndex}">
    <c:set var="mediumBeginIndex" value="26"/>
  </c:if>

  <c:if test="${empty longBeginIndex}">
    <c:set var="longBeginIndex" value="41"/>
  </c:if>

  ${(fn:length(string)) >= longBeginIndex ? 'atg_string_long' : ((fn:length(string)) >= mediumBeginIndex ? 'atg_string_medium' : '')}${empty additionalClasses ? '' : ' '+ additionalClasses}  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/WEB-INF/tags/store/stringSizeClass.tag#1 $$Change: 735822 $--%>

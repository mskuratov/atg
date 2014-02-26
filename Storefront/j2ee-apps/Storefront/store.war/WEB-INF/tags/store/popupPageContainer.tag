<%-- 
  This tag that acts as a container for all popup pages, it includes all relevant header, footer and nav elements.

  The body of this tag should include any required gadgets.

  If any of the divId, titleKey or textKey attributes are set, then the 'popupPageIntro' gadget will be included.
  If none of these attributes are specified, then the 'popPageIntro' gadget will not be included.

  Required attributes:
    None.

  Optional attributes: 
    pageTitle
      String to be displayed as the '<title>' for the page.
    divId
      Id for the containing div, passed to 'popupPageIntro' gadget.
    titleKey
      Resource bundle key for the title/heading of the page, passed to 'popupPageIntro' gadget.
    textKey
      Resource bundle key for the intro text, passed to 'popupPageIntro' gadget.
    titleString
      String returned in message text when title key not found. Title string that will be passed to the 'popupPageIntro' gadget.
    textString
      Intro text string that will be passed to the 'popupPageIntro' gadget.
--%>

<%@ include file="/includes/taglibs.jspf" %>
<%@ tag language="java" %>
<%@ attribute name="pageTitle" %>
<%@ attribute name="divId" %>
<%@ attribute name="titleKey" %>
<%@ attribute name="textKey" %>
<%@ attribute name="titleString" %>
<%@ attribute name="textString" %>
<%@ attribute name="useCloseButton" %>
<%@ attribute name="customCssFile" %>
<%@ attribute name="formErrorsRenderer" fragment="true" %>

<%-- Use the 'titleKey'/'titleString' for the page title if no 'pageTitle' is provided --%>
<c:if test="${empty pageTitle && (!empty titleKey || !empty titleString)}">
  <crs:messageWithDefault key="${titleKey}" string="${titleString}"/>
  <c:set var="pageTitle" value="${messageText}"/>
</c:if>

<%-- Setup the page e.g CSS, JS, Page Icon etc --%>
<dsp:include page="/includes/popupStart.jsp">
  <dsp:param name="index" value="${index}"/>
  <dsp:param name="follow" value="${follow}"/>
  <dsp:param name="pageTitle" value="${pageTitle}"/>
  <dsp:param name="customCssFile" value="${customCssFile}"/>
</dsp:include>

<c:if test="${!empty divId and (!empty titleKey or !empty textKey or !empty titleString or !empty textString)}">
  <dsp:include page="/global/gadgets/popupPageIntro.jsp">
    <dsp:param name="divId" value="${divId}" />
    <dsp:param name="titleKey" value="${titleKey}" />
    <dsp:param name="textKey" value="${textKey}" />
    <dsp:param name="titleString" value="${titleString}"/>
    <dsp:param name="textString" value="${textString}"/>
    <dsp:param name="useCloseButton" value="${useCloseButton}"/>
  </dsp:include>
</c:if>

<%-- Display any error messages. --%>
<div style="display:none">
  <jsp:invoke fragment="formErrorsRenderer"/>
</div>

<jsp:doBody/>

<dsp:include page="/includes/popupEnd.jsp" />
<%-- @version $Id$$Change$--%>

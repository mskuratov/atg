<%--
  This tag acts as a container for top-level pages for all email templates, including all relevant header, footer 
  elements wherever required.

  If any of the divId, titleKey or textKey attributes are set, then the emailPageIntro gadget will be included. 
  If none of these attributes are specified, then the pageIntro gadget will not be included.

  The body of this tag should include any required gadgets.
  
  The tag sets 'httpServer' and 'imageRoot' parameters that can be used in the tag's body to build
  absolute URLs to site pages and images correspondingly. 

  Required attributes:
    None.
  Optional attributes:
    divId  
      The id for the containing div. Will be passed to the pageIntro gadget.
    titleKey 
      resource bundle key for the title. Will be passed to the pageIntro gadget.
    textKey  
      Resource bundle key for the intro text. Will be passed to the pageIntro gadget.
    titleString 
      Title text that will be passed to the pageIntro gadget.
    textString 
      Intro text string that will be passed to the pageIntro gadget.
    displayHeader  
      Boolean flag to render/not-render the common email header in the email content; Defaults to true.
    displayFooter  
      Boolean flag to render/not-render the common email footer in the email content; Defaults to true.
    messageSubjectKey  
      Resource Key for the string to be used as Subject of the email.
    messageSubjectString  
      Text string to be used as Subject of the email.
    messageFromAddressString  
      Email address to be set as Sender's address for the email.
    displayProfileLink  
      Boolean flag indicating if display the 'My Account' link in the header.

  The tag accepts the following fragments:
    subNavigation 
      Defines a fragment that will be contain sub navigation gadgets.
      For Example:
        <jsp:attribute name="subNavigation">
         ::
        </jsp:attribute>
--%>

<%@include file="/includes/taglibs.jspf" %>
<%@include file="/includes/context.jspf"%>
<%@ tag language="java" %>

<%@ attribute name="divId" %>
<%@ attribute name="titleKey" %>
<%@ attribute name="textKey" %>
<%@ attribute name="titleString" %>
<%@ attribute name="textString" %>
<%@ attribute name="displayHeader" type="java.lang.Boolean" %>
<%@ attribute name="displayFooter" type="java.lang.Boolean" %>
<%@ attribute name="messageSubjectKey" %>
<%@ attribute name="messageSubjectString" %>
<%@ attribute name="messageFromAddressString" %>
<%@ attribute name="subNavigation" fragment="true" %>
<%@ attribute name="displayProfileLink" type="java.lang.Boolean" %>

<dsp:page>
   
  <dsp:importbean var="storeConfig" bean="/atg/store/StoreConfiguration"/>
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
  
  <%--
    ComponentExists droplet conditionally renders one of its output parameters
    depending on whether or not a specified Nucleus path currently refers to a
    non-null object.  It it used to query whether a particular component has been
    instantiated, in this case StoreVersioned. If the StoreVersioned component has 
    not been instantiated we can display the 404.
      
    Input Parameters:
      path - The path to a component
       
    Open Parameters:
      true
        Rendered if the component 'path' has been instantiated.
      false
        Rendered if the component 'path' has not been instantiated. 
  --%>
  <dsp:droplet name="/atg/dynamo/droplet/ComponentExists">
    <dsp:param name="path" value="/atg/store/recommendations/StoreRecommendationsConfiguration"/>
    <dsp:oparam name="true">    
      <%--
        Specify that no automatic tagging should be apply to emails.
      --%> 
      <dsp:droplet name="/atg/adc/droplet/NoTag">
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet> 
  
  <%--
    Build URL prefix with protocol, server name and port. It will be used to get fully-qualified URLs
    to site pages.
   --%>
  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${storeConfig.siteHttpServerName}"/>
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${storeConfig.siteHttpServerPort}"/>
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}"/>
  <%-- Put the URL prefix into parameter so that it can be used in the tag's body --%>
  <dsp:setvalue param="httpServer" value="${httpServer}"/>
  <%-- Build URL prefix for images and put it into parameter to use in the tag's body. --%>
  <dsp:setvalue param="imageRoot" value="${httpServer}/crsdocroot/"/>  
  
  <%-- Show the header if requested. --%>
  <c:if test="${empty displayHeader || displayHeader}">
    <dsp:include page="/emailtemplates/gadgets/header.jsp">
      <dsp:param name="httpServer" value="${httpServer}"/>
      <dsp:param name="displayProfileLink" value="${displayProfileLink}"/>
    </dsp:include>
  </c:if>

  <%-- Set TemplateInfo parameters if required --%>
  <crs:messageWithDefault key="${messageSubjectKey}" string="${messageSubjectString}"/>
  <c:if test="${!empty messageText}">
    <dsp:setvalue param="messageSubject" value="${messageText}"/>
  </c:if>

  <c:if test="${!empty messageFromAddressString}">
    <dsp:setvalue param="messageFrom" value="${messageFromAddressString}"/>
  </c:if>

  <jsp:invoke fragment="subNavigation"/>

  <c:if test="${!empty divId and (!empty titleKey or !empty textKey or !empty titleString or !empty textString)}">
    <dsp:include page="/emailtemplates/gadgets/emailPageIntro.jsp">
      <dsp:param name="divId" value="${divId}"/>
      <dsp:param name="titleKey" value="${titleKey}"/>
      <dsp:param name="textKey" value="${textKey}"/>
      <dsp:param name="titleString" value="${titleString}"/>
      <dsp:param name="textString" value="${textString}"/>
    </dsp:include>
  </c:if>

  <jsp:doBody/>

  <%-- Show the footer if requested. --%>
  <c:if test="${empty displayFooter || displayFooter}">
    <dsp:include page="/emailtemplates/gadgets/footer.jsp">
      <dsp:param name="httpServer" value="${httpServer}"/>
    </dsp:include>
  </c:if>

</dsp:page>
<%-- @version $Id$$Change$--%>

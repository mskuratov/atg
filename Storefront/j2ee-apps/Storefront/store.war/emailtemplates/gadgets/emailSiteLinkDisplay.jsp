<%--
  This gadget renders link with fully-qualified site URL and displays it as text link or image link
  depending on input parameters: if imageUrl is provided then the image link will be displayed otherwise
  the text link will be displayed.
  
  Required parameters:
    httpServer
      URL prefix that includes protocol, host and port
    path
      Base URL to wrap into site details.
    linkStyle
      CSS style for anchor tag
    linkTitle
      link title
    
  Optional parameters:
    queryParams
      optional query parameters to include into URL
    imageUrl
      image URL for a link. Either imageUrl or linkText are required for this gadget.
    imageTitle
      image title
    imageAltText
      image alt text
    linkText
      Text to display for link. Either imageUrl or linkText are required for this gadget.
    locale
      Locale to append to the URL as URL parameter so that the pages to which user navigates using the URL
      will be rendered in the same language as the email.      
--%>
<dsp:page>


   
  <dsp:getvalueof var="linkStyle" param="linkStyle"/>
  <dsp:getvalueof var="linkText" param="linkText"/>
  <dsp:getvalueof var="linkTitle" param="linkTitle"/>
  <dsp:getvalueof var="imageUrl" param="imageUrl"/>
  <dsp:getvalueof var="imageTitle" param="imageTitle"/>
  <dsp:getvalueof var="imageAltText" param="imageAltText"/>
  
  <%-- This gadget generate fully-qualified site-specific URL and stores it into 'siteLinkUrl' variable. --%>
  <dsp:include page="/emailtemplates/gadgets/emailSiteLink.jsp">
    <dsp:param name="path" param="path"/>
    <dsp:param name="locale" param="locale"/>
    <dsp:param name="httpServer" param="httpServer"/>
    <dsp:param name="queryParams" param="queryParams"/>
  </dsp:include>
  
    <c:choose>
      <c:when test="${not empty imageUrl}">
        <dsp:a href="${siteLinkUrl}" style="${linkStyle}">
          <img src="${imageUrl}" border="0" title="${imageTitle}" alt="${imageAltText}" style="max-width:171px;max-height:68px;"/>
          <c:if test="${not empty linkText}">
            <span>${linkText}</span>
          </c:if>
        </dsp:a>
      </c:when>
      <c:otherwise>
        <dsp:a href="${siteLinkUrl}" style="${linkStyle}"><c:out value="${linkText}" escapeXml="false"/></dsp:a>
      </c:otherwise>
    </c:choose>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/emailSiteLinkDisplay.jsp#2 $$Change: 788278 $--%>

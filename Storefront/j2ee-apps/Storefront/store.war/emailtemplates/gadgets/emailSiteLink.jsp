<%--
  This gadget generates fully-qualified site link and puts it into request scope parameter 'siteLinkUrl'
  
  Required parameters:
    httpServer
      The URL prefix that includes protocol, host and port
    path
      The base URL to wrap into site details
      
  Optional parameters:
    queryParams
      optional query parameters to include into URL
    locale
      Locale to append to the URL as URL parameter so that the pages to which user navigates using the URL
      will be rendered in the same language as the email.
--%>
<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/SiteLinkDroplet"/>
  <dsp:importbean bean="/atg/multisite/Site"/>

  <dsp:getvalueof var="httpServer" param="httpServer"/>
  <dsp:getvalueof var="locale" param="locale"/>

  <dsp:include page="/global/gadgets/crossSiteLinkGenerator.jsp">
    <dsp:param name="siteId" bean="Site.id"/>
    <dsp:param name="customUrl" param="path"/>
    <dsp:param name="queryParams" param="queryParams"/>
    <dsp:param name="forceFullSite" value="${true}"/>
  </dsp:include>

  <%-- Build fully-qualified site-specific URL with locale parameter included --%>
  <c:url var="siteLinkUrl" value="${httpServer}${siteLinkUrl}" scope="request">
    <c:if test="${not empty locale}">
      <c:param name="locale">${locale}</c:param>
    </c:if>
  </c:url>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/emailSiteLink.jsp#2 $$Change: 742374 $--%>

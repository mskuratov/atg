<%--
  This is a container tag for all top-level mobile pages.
  It introduces the following request-scoped variables:
    mobileStorePrefix
      "/atg/store/StoreConfiguration.mobileStorePrefix" bean property value.
    siteContextPath
      Is a context path ("/atg/multisite/Site.productionURL" or "pageContext.request.contextPath")
      plus mobile store prefix ("/atg/store/StoreConfiguration.mobileStorePrefix" bean property value).
    siteBaseURL
      The same as "siteContextPath", but WITHOUT the "/mobile" suffix.
    isLoggedIn
      Whether a user is logged in (i.e. if a user Profile is not transient).
    userGender
      User gender: male, female or "unknown" (if user is not logged in).
    navigationActionPath
      Endeca Experience Manager default navigation action path.

  Includes:
    /mobile/includes/mobilePageContainerTagBody.jspf - Body part generation of the "mobilePageContainer" custom tag
    /mobile/includes/pageDirectives.jsp - Specific JSP directives which are not allowed in custom tags
    /mobile/global/util/getSiteContextPath.jsp - Defines "siteContextPath", "siteBaseURL" request-scoped variables

  Required Parameters:
    titleString
      The title of the current page

  Optional Parameters:
    displayModal
      If present, modal dialog will be visible on page load

  Page Fragments:
    modalContent (optional) - renders modal dialog / popup content for the page

  NOTES:
    1) The "endecaUserAgent" request-scoped variable (request attribute), which is used here,
      is set by the "MobileDetectionInterceptor": it's set, when the "Endeca Preview" is enabled
      (AssemblerSettings.previewEnabled = true) and the "Endeca Preview User-Agent" request parameter
      is present in the request (even if it's an empty string).
    2) The "rootContentItem" request-scoped variable (request attribute), which is used here,
      is set by the "AssemblerPipelineServlet".
--%>
<%@ include file="/includes/taglibs.jspf" %>
<%@ include file="/includes/context.jspf" %>

<%@ tag language="java" %>

<%@ attribute name="titleString" required="true" %>
<%@ attribute name="modalContent" fragment="true" %>
<%@ attribute name="displayModal" required="false" %>

<dsp:include page="/mobile/includes/pageDirectives.jsp"/>

<dsp:importbean bean="/atg/endeca/assembler/cartridge/manager/AssemblerSettings"/>
<dsp:importbean bean="/atg/endeca/assembler/cartridge/manager/DefaultActionPathProvider"/>
<dsp:importbean bean="/atg/multisite/Site" var="currentSite"/>
<dsp:importbean bean="/atg/store/StoreConfiguration"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:getvalueof var="mobileStorePrefix" bean="StoreConfiguration.mobileStorePrefix" scope="request"/>
<dsp:getvalueof var="navigationActionPath" bean="DefaultActionPathProvider.defaultExperienceManagerNavigationActionPath" scope="request"/>

<dsp:include page="${mobileStorePrefix}/global/util/getSiteContextPath.jsp"/>
<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
<c:set var="isLoggedIn" scope="request" value="${isTransient ? false : true}"/>
<dsp:getvalueof var="userGender" bean="Profile.gender" scope="request"/>

<dsp:getvalueof var="language" bean="/OriginatingRequest.requestLocale.locale.language"/>
<c:if test="${empty language}">
  <dsp:getvalueof var="language" bean="/OriginatingRequest.locale.language"/>
</c:if>

<!DOCTYPE HTML>
<html lang="${language}">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="format-detection" content="telephone=no"/>
    <link rel="stylesheet" href="${siteContextPath}/css/mobile.css" type="text/css" media="screen"/>
    <%-- Load the site specific CSS --%>
    <dsp:getvalueof var="siteCssFile" value="${currentSite.cssFile}"/>
    <c:if test="${not empty siteCssFile}">
      <link rel="stylesheet" href="${siteContextPath}${siteCssFile}.css" type="text/css" media="screen" charset="utf-8"/>
    </c:if>
    <dsp:getvalueof var="faviconUrl" vartype="java.lang.String" value="${currentSite.favicon}"/>
    <link rel="shortcut icon" href="${faviconUrl}"/>
    <link rel="apple-touch-icon" href="/crsdocroot/content/mobile/images/apple-touch-icon.png"/>
    <%-- Endeca Preview scripts: adding Preview resources (JavaScript, CSS) --%>
    <c:if test="${endecaUserAgent != null && not empty rootContentItem}">
      <%-- This includes pathes to the Endeca preview JavaScript and CSS files --%>
      <endeca:pageHead rootContentItem="${rootContentItem}"/>
    </c:if>
    <script type="text/javascript" src="${siteContextPath}/js/jquery/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="${siteContextPath}/js/atg/mobile.js"></script>
    <script type="text/javascript" src="${siteContextPath}/js/resources/resources_${language}.js"></script>
    <title><crs:outMessage key="common.storeName"/><fmt:message key="mobile.common.textSeparator"/> <c:out value="${titleString}"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0"/>
  </head>
  <body>
    <c:choose>
      <c:when test="${endecaUserAgent != null && not empty rootContentItem}">
        <%-- Endeca Preview scripts: enabling auditing --%>
        <endeca:pageBody rootContentItem="${rootContentItem}">
          <%@ include file="/mobile/includes/mobilePageContainerTagBody.jspf" %>
        </endeca:pageBody>
      </c:when>
      <c:otherwise>
        <%@ include file="/mobile/includes/mobilePageContainerTagBody.jspf" %>
      </c:otherwise>
    </c:choose>
  </body>
</html>

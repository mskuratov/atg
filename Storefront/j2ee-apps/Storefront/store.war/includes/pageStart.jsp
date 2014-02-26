<%--
  This page is used for other pages starting together with pageEnd.jsp. 

  Required Parameters:
    None
    
  Optional Parameters:
    None
--%>

<%@ page contentType="text/html; charset=UTF-8" %>

<%-- 
  JSP 2.1 parameter. With trimDirectiveWhitespaces enabled,
  template text containing only blank lines, or white space,
  is removed from the response output.
  
  trimDirectiveWhitespaces doesn't remove all white spaces in a
  HTML page, it is only supposed to remove the blank lines left behind
  by JSP directives (as described here 
  http://java.sun.com/developer/technicalArticles/J2EE/jsp_21/ ) 
  when the HTML is rendered. 
 --%>
 
<%@page trimDirectiveWhitespaces="true"%>

<dsp:page>
  <dsp:importbean bean="/atg/multisite/Site" var="currentSite"/>
  <dsp:importbean bean="/atg/endeca/assembler/cartridge/StoreCartridgeTools" var="StoreCartridgeTools"/>
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean bean="/atg/store/StoreConfiguration" var="storeConfiguration"/>
  <dsp:importbean bean="/atg/endeca/assembler/cartridge/manager/AssemblerSettings"/>
  
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="language" bean="/OriginatingRequest.requestLocale.locale.language"/>
  
  <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
                        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
  <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
 
      <%--
        Puts ie8 into "Compatibility" mode so it will render just like ie7.
        http://blogs.msdn.com/ie/archive/2008/08/27/introducing-compatibility-view.aspx
        http://blogs.msdn.com/ie/archive/2009/03/12/site-compatibility-and-ie8.aspx
      --%>
      <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7; IE=EmulateIE9" />

      
      <%-- Base style sheet --%>
      
      <link rel="stylesheet" href="${contextPath}/css/base.css"
            type="text/css" media="screen" charset="utf-8" />
      
      <%-- Grid style sheet --%>
      <link rel="stylesheet" href="${contextPath}/css/grid.css"
            type="text/css" media="screen" charset="utf-8" />

      <%-- According to the conditional comment this is Internet Explorer load the IE CSS --%>
      
      <!--[if lte IE 8]>
          <link rel="stylesheet" href="${contextPath}/css/ie.css"
        type="text/css" media="screen" title="ie.css" charset="utf-8" />
      <![endif]-->

      <%-- Load the site specific CSS --%>
      <dsp:getvalueof var="siteCssFile" value="${currentSite.cssFile}" />

      <c:choose>
        <c:when test="${empty siteCssFile}">
          <dsp:getvalueof var="siteCssFile" value="${storeConfiguration.defaultCssFile}" />
        </c:when>
      </c:choose>

      <!--[if lte IE 8]>
        <link rel="stylesheet" href="${contextPath}${siteCssFile}_old_ie.css"
              type="text/css" media="screen" title="${siteCssFile}_old_ie.css" charset="utf-8" />
      <![endif]-->

      <c:if test="${not empty siteCssFile}">
        <link rel="stylesheet" href="${contextPath}${siteCssFile}.css"
              type="text/css" media="screen" charset="utf-8" />
          

        <%-- Load language specific CSS if this is a language which requires additional styling --%>
        <c:if test="${language eq 'de'}">
          <link rel="stylesheet" href="${contextPath}${siteCssFile}_de.css"
                type="text/css" media="screen" charset="utf-8" />
          <!--[if IE]>
            <link rel="stylesheet" href="${contextPath}${siteCssFile}_de_ie.css"
                  type="text/css" media="screen" title="no title" charset="utf-8" />
          <![endif]-->
        </c:if>
      
        <c:if test="${language eq 'es'}">
          <link rel="stylesheet" href="${contextPath}${siteCssFile}_es.css"
                type="text/css" media="screen" charset="utf-8" />
          <!--[if IE]>
            <link rel="stylesheet" href="${contextPath}${siteCssFile}_es_ie.css"
                  type="text/css" media="screen" title="${siteCssFile}_es_ie.css" charset="utf-8" />
          <![endif]-->
        </c:if>
      </c:if>


      <%-- Possible alternative print style sheet modify the css style file --%>
      <link rel="stylesheet" href="${contextPath}/css/common_print.css"
            type="text/css" media="print" title="common_print.css" charset="utf-8" />
                
      <script type="text/javascript" charset="utf-8">
        <%-- Javascript on css style --%>
        document.write('<link rel="stylesheet" href="${contextPath}/css/javascript.css" type="text/css" charset="utf-8" />');
        <%-- Get rid of the IE rollover flicker. --%>
        try {
          document.execCommand('BackgroundImageCache', false, true);
        } catch(e) {}
      </script>

      <%-- Robots meta tag --%>
      <dsp:getvalueof var="index" param="index"/>
      <dsp:getvalueof var="follow" param="follow"/>
              
      <c:set var="indexValue" value="${(index eq 'false') ? 'noindex' : 'index'}"/>
      <c:set var="followValue" value="${(follow eq 'false') ? 'nofollow' : 'follow'}"/>
      
      <meta name="robots" content="${indexValue},${followValue}"/>
    
      <%-- Include content from SEO tag renderer --%>
      <dsp:getvalueof var="SEOTagRendererContent" param="SEOTagRendererContent"/>
      <c:choose>
        <c:when test="${not empty SEOTagRendererContent }">
          <c:out value="${SEOTagRendererContent}" escapeXml="false"/>
        </c:when>
        <c:otherwise>
          
          <dsp:getvalueof var="currentCategoryId" value="${StoreCartridgeTools.currentCategoryId}"/>
          <c:choose>
            <c:when test="${not empty currentCategoryId}">
              <dsp:droplet name="CategoryLookup">
                <dsp:param name="id" value="${currentCategoryId}"/>      
                <dsp:oparam name="output">                 
                   <dsp:include page="/global/gadgets/metaDetails.jsp">
                     <dsp:param name="catalogItem" param="element"/>
                   </dsp:include>
                </dsp:oparam>
              </dsp:droplet>
            </c:when>
            <c:otherwise>
             <%-- Use default SEO tag renderer --%>          
             <dsp:include page="/global/gadgets/metaDetails.jsp"/>
            </c:otherwise>
          </c:choose>          
            
        </c:otherwise>
      </c:choose>

      <%-- Renders canonical links --%>
      <dsp:include page="/global/gadgets/documentLinks.jsp">
        <dsp:param name="categoryId" param="categoryId"/>
        <dsp:param name="productId" param="productId"/>
      </dsp:include>
    
      <dsp:getvalueof var="faviconUrl" vartype="java.lang.String" value="${currentSite.favicon}"/>
      <link rel="icon" type="image/png" href="${faviconUrl}"/>
    
      <!--[if IE 7]>
        <link rel="shortcut icon" type="image/vnd.microsoft.icon" href="${fn:replace(faviconUrl, '.png', '.ico')}"/>
      <![endif]-->

      <dsp:getvalueof var="previewEnabled" bean="AssemblerSettings.previewEnabled"/>
      <c:if test="${previewEnabled and not empty rootContentItem}">
        <%-- This includes paths to the Endeca preview JavaScript and CSS files. --%>
        <endeca:pageHead rootContentItem="${rootContentItem}"/>
      </c:if>
      
      <dsp:include page="/includes/pageStartScript.jsp"/>
    </head>

    <dsp:getvalueof var="bodyClass" param="bodyClass"/>
  <body class="${bodyClass}">
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/includes/pageStart.jsp#3 $$Change: 788949 $--%>

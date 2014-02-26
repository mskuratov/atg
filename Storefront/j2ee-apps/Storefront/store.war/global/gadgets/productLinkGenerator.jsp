<%-- 
  Calculates a valid URL to be displayed for the specified product. Calculated URL is stored 
  within 'productUrl' request-scoped variable.

  Required parameters:
    product
      The product repository item to build URL for.

  Optional parameters:
    categoryId
      The product parent category ID.
    navLinkAction
      Type of breadcrumb navigation to use for product detail links.
      Valid values are 'push', 'pop', or 'jump'. Default is 'jump'.
    categoryNavIds
      The colon-separated list representing the category navigation trail.
    categoryNav
      Determines if breadcrumbs are updated to reflect category navigation trail on click through.
    searchClickId
      Search click ID parameter appended to the URL to notify reporting service. This parameter
      tells reporting service which search query returned the product. It is usually appended to
      product URLs on search results page.
    siteId
      The site ID to generate link for. If not specified, current site ID will be used.
    forceFullSite
      If this parameter is specified (no matter what the value is), then a "full CRS" site ID
      must be used, irrespective of the actual site. This is needed to force a "full CRS" site link
      generation regardless of the actual site (which is identified by "siteId" parameter).
      For example, when the "CRS mobile" module is installed, the "siteId", the current site might be
      a mobile one and in some cases we need a non-mobile (full CRS) site links to be generated.
      One of such cases: generation of emails with links to products and sites, where links
      should point to "full CRS" site and then the "MobileDetectionInterceptor" component
      will handle redirection to mobile site, when a request is originated by mobile user agent.
      To force get a "full CRS" site when the "CRS mobile" module is installed, we use site groups
      with "crs.MobileSitePairs" shareable type introduced by mobile version of "sites.xml" files:
      each site group with the "crs.MobileSitePairs" shareable type joins together a "full CRS"
      and corresponding mobile site (in other words, we get a "non-mobile - mobile" site pairs).
      More technical details are available in "PairedSiteDroplet" component.
      Also NOTE a site is identified as "full CRS" when it has the "channel" property equal to "desktop"
      (the "channel" property is defined in "sites.xml" files).
      Also NOTE the "forceFullSite" parameter is only used by the "crossSiteLinkGenerator.jsp" page.
--%>
<dsp:page>  
  <dsp:getvalueof var="searchClickId" param="searchClickId"/>
  <dsp:getvalueof var="templateUrl" param="product.template.url"/>
  <dsp:getvalueof var="categoryId" param="categoryId"/>

  <%-- 
    If category ID is not specified and among product.parentCategory.siteIds
    is the siteId for wich the link is generated, then take the product 
    default parent category 
  --%>  
  <c:if test="${empty categoryId}">
  
    <dsp:getvalueof var="siteId" param="siteId"/>
    <dsp:getvalueof var="productCategorySiteIds" param="product.parentCategory.siteIds"/>
    <%-- 
      Check that among category sites is the site for that the link
      should be generated. 
    --%>
    <c:if test="${fn:contains(productCategorySiteIds,siteId)}">
      <dsp:param name="categoryId" param="product.parentCategory.repositoryId"/>
    </c:if>
    
  </c:if>

  <c:choose>
    <c:when test="${empty templateUrl}">
      <c:set var="productUrl" scope="request" value=""/>
    </c:when>
    <c:otherwise>
      <%--
        Determine if the generated URL is indirect URL for search spiders by 
        checking the browser type.
      --%>
      <dsp:droplet name="/atg/repository/seo/BrowserTyperDroplet">
        <dsp:oparam name="output">
          <dsp:getvalueof var="browserType" param="browserType"/>
          <c:set var="isIndirectUrl" value="${browserType eq 'robot'}"/>
        </dsp:oparam>
      </dsp:droplet>

      <c:choose> 
        <c:when test="${not empty categoryId or not isIndirectUrl}">            
          <%--
            "ProductLookupItemLink" is used to generate a browser-specific URL for a repository item.
            Here we generate the link to the product item  passed into the droplet. "categoryId"
            parameter is not used directly by droplet, it is specified in URL template so putting it into request.

            Input Parameters:
              item
                The repository item to generate URL for.

            Output Parameters:
              url
                The URL generated for a repository item.

            Open Parameters:
              output
                Serviced when no errors occur.
          --%>
          <dsp:droplet name="/atg/repository/seo/ProductLookupItemLink">
            <dsp:param name="item" param="product"/>
            <dsp:param name="categoryId" param="categoryId"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="pageurl" vartype="java.lang.String" param="url"/>
            </dsp:oparam>
          </dsp:droplet>
        </c:when>
        <c:otherwise>
          <%--
            "ProductItemLink" is used to generate a browser-specific URL for a repository item.
            Here we generate the link to the product item passed into the droplet.
            "categoryId" parameter is not specified in URL template.

            Input Parameters:
              item
                The repository item to generate URL for.

            Output Parameters:
              url
                The URL generated for a repository item.

            Open Parameters:
              output
                Serviced when no errors occur.
           --%>
          <dsp:droplet name="/atg/repository/seo/CatalogItemLink">
            <dsp:param name="item" param="product"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="pageurl" vartype="java.lang.String" param="url"/>
            </dsp:oparam>
          </dsp:droplet>
        </c:otherwise>
      </c:choose>

      <%-- Generate the link passing base URL and site ID to crossSiteLinkGenerator.jsp --%>
      <dsp:include page="/global/gadgets/crossSiteLinkGenerator.jsp">
        <dsp:param name="product" param="product"/>
        <dsp:param name="customUrl" value="${pageurl}"/>
        <dsp:param name="siteId" param="siteId"/>
        <dsp:param name="forceFullSite" param="forceFullSite"/>
      </dsp:include>

      <%--
        Do not add additional parameters if the URL for search spiders as this
        will turn the static URL back to dynamic one.
       --%>
      <c:url var="pageurl" value="${siteLinkUrl}" context="/">
        <c:if test="${not isIndirectUrl}">
          <dsp:getvalueof var="categoryNavIds" param="categoryNavIds"/>
          <c:if test="${!empty categoryNavIds}">
            <c:param name="categoryNavIds" value="${categoryNavIds}"/>
          </c:if>

          <dsp:getvalueof var="categoryNav" param="categoryNav"/>
          <c:if test="${!empty categoryNav}">
            <c:param name="categoryNav" value="${categoryNav}"/>
          </c:if>

          <dsp:getvalueof var="navLinkAction" param="navLinkAction"/>
          <c:if test="${empty navLinkAction}">
            <c:set var="navLinkAction" value="jump"/>
          </c:if>

          <dsp:getvalueof var="navCount" bean="/atg/commerce/catalog/CatalogNavHistory.navCount"/>
          <c:param name="navAction" value="${navLinkAction}"/>
          <c:param name="navCount" value="${navCount}"/>
        </c:if>
      </c:url>

      <%-- Put generated product URL into request-scoped variable --%>
      <c:set var="productUrl" scope="request" value="${pageurl}"/>
    </c:otherwise>
  </c:choose>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/productLinkGenerator.jsp#3 $$Change: 788278 $--%>

<%-- 
  Calculates a valid URL to be displayed for the specified product.
  Calculated URL is stored within 'productUrl' request-scoped variable.

  Page includes:
    /global/gadgets/crossSiteLinkGenerator.jsp - Generator of cross-site links

  Required parameters:
    product
      The product repository item to build URL for

  Optional parameters:
    categoryId
      The product parent category ID
    navLinkAction
      Type of breadcrumb navigation to use for product detail links.
      Valid values are:
      'push'
      'pop'
      'jump' (the default)
    categoryNavIds
      The colon-separated list representing the category navigation trail
    categoryNav
      Determines if breadcrumbs are updated to reflect category navigation trail on click through
    siteId
      The site ID to generate link for. If not specified, current site ID will be used
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/multisite/SiteIdForCatalogItem"/>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/GetSiteDroplet"/>

  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="templateUrl" param="product.template.url"/>
  <dsp:getvalueof var="categoryId" param="categoryId"/>

  <%-- If category ID is not specified take the product default parent category --%>
  <c:if test="${empty categoryId}">
    <dsp:param name="categoryId" param="product.parentCategory.repositoryId"/>
  </c:if>

  <c:choose>
    <c:when test="${empty templateUrl}">
      <c:set var="productUrl" scope="request" value=""/>
    </c:when>
    <c:otherwise>
      <%--
        Determine if the generated URL is indirect URL for search spiders by
        checking the browser type
      --%>
      <dsp:droplet name="/atg/repository/seo/BrowserTyperDroplet">
        <dsp:oparam name="output">
          <dsp:getvalueof var="browserType" param="browserType"/>
          <c:set var="isIndirectUrl" value="${browserType == 'robot'}"/>
        </dsp:oparam>
      </dsp:droplet>

      <c:choose>
        <c:when test="${not empty categoryId || not isIndirectUrl}">
          <%--
            "ProductLookupItemLink" is used to generate a browser-specific URL for a repository item.
            Here we generate the link to the product item passed into the droplet.
            "categoryId" parameter is not used directly by droplet,
            it is specified in URL template so putting it into request.

            Input Parameters:
              item
                The repository item to generate URL for
            Output Parameters:
              url
                The URL generated for a repository item
            Open Parameters:
              output
                Serviced when no errors occur
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
            "CatalogItemLink" is used to generate a browser-specific URL for a repository item.
            Here we generate the link to the product item passed into the droplet.
            "categoryId" parameter is NOT specified in URL template.

            Input Parameters:
              item
                The repository item to generate URL for
            Output Parameters:
              url
                The URL generated for a repository item
            Open Parameters:
              output
                Serviced when no errors occur
          --%>
          <dsp:droplet name="/atg/repository/seo/CatalogItemLink">
            <dsp:param name="item" param="product"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="pageurl" vartype="java.lang.String" param="url"/>
            </dsp:oparam>
          </dsp:droplet>
        </c:otherwise>
      </c:choose>

      <%-- If no site ID specified for given product - use "SiteIdForCatalogItem" --%>
      <dsp:getvalueof var="siteId" param="siteId"/>
      <dsp:getvalueof var="product" param="product"/>
      <c:if test="${empty siteId && !empty product}">
        <%--
          "SiteIdForCatalogItem" droplet returns the most appropriate site id for a
          given repository item.

          Input Parameters:
            item
              The item that the site selection is going to be based on

          Open Parameters:
            output
              Rendered if no errors occur

          Output Parameters:
            siteId
              The calculated siteId, which represents the best match for the given item
        --%>
        <dsp:droplet name="SiteIdForCatalogItem" item="${product}" var="siteIdForCatalogItem">
          <dsp:param name="currentSiteFirst" value="true"/>
          <%-- Search through the mobile (!) sites which belong to "crs.MobileSite" --%>
          <dsp:param name="shareableTypeId" value="crs.MobileSite"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="siteId" value="${siteIdForCatalogItem.siteId}"/>
          </dsp:oparam>
        </dsp:droplet>
      </c:if>

      <%-- Generate the link passing base URL and site ID to "crossSiteLinkGenerator.jsp" --%>
      <c:set var="prefixForMobileSite" value=""/>
      <c:if test="${not empty siteId}">
        <dsp:droplet name="GetSiteDroplet">
          <dsp:param name="siteId" value="${siteId}"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="site" param="site"/>
            <c:if test="${site.channel == 'mobile'}">
              <c:set var="prefixForMobileSite" value="${mobileStorePrefix}"/>
            </c:if>
          </dsp:oparam>
        </dsp:droplet>
      </c:if>
      <dsp:include page="/global/gadgets/crossSiteLinkGenerator.jsp">
        <dsp:param name="product" param="product"/>
        <dsp:param name="customUrl" value="${prefixForMobileSite}${pageurl}"/>
        <c:if test="${not empty siteId}">
          <dsp:param name="siteId" value="${siteId}"/>
        </c:if>
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
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/global/gadgets/productLinkGenerator.jsp#3 $$Change: 768606 $--%>

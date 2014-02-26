<%--
  This gadget generates <dsp:a> tag with cross site link.
  
  Required parameters:
    item
      commerce item object to display cross-site link for.
      Either item or product parameters is required for this gadget.
    product
      The product item to display cross-site link for. Either item or product 
      parameters is required for this gadget.
      
  Optional parameters:
    imgUrl
      Image URL to display link as image.
    httpServer
      The URL prefix with protocol, server name and port to prepend URLs with.
      This prefix is usually passed from email templates to get fully-qualified URLs.
    includeTitle
      Specify if title attribute should be included in image tag. By default is false. 
    skuDisplayName
      If skuDisplayName not empty, it will be use instead of product display name.
--%>
<dsp:page>

  <dsp:importbean bean="/atg/dynamo/droplet/multisite/SiteLinkDroplet"/>
  <dsp:importbean bean="/atg/commerce/multisite/SiteIdForCatalogItem"/>
  <dsp:getvalueof var="missingProductSkuId" vartype="java.lang.String" bean="/atg/commerce/order/processor/SetCatalogRefs.substituteDeletedSkuId"/>

  <dsp:getvalueof var="imgUrl" param="imgUrl"/>
  <dsp:getvalueof var="item" param="item"/>
  <dsp:getvalueof var="includeTitle" param="includeTitle"/>

  <%-- Check wheter commerce item or product is passed to the gadget --%>
  <c:choose>
    <c:when test="${not empty item}">
      <%-- Commerce item is passed, retrieve product and site ID from it. --%>
     
      <%-- 
        If sku.displayName is not empty and sku is not deleted, use it displayName 
        as product display name 
      --%>
      <c:if test="${missingProductSkuId != item.auxiliaryData.catalogRef.repositoryId}">  
        <dsp:getvalueof var="displayName" param="item.auxiliaryData.catalogRef.displayName"/>
      </c:if> 
      <c:if test="${empty displayName}">
        <dsp:getvalueof var="displayName" param="item.auxiliaryData.productRef.displayName"/>
      </c:if>
      
      <dsp:getvalueof var="productId" param="item.auxiliaryData.productId"/>
      <dsp:getvalueof var="siteId" param="item.auxiliaryData.siteId"/>

      <dsp:include page="/global/gadgets/crossSiteLinkGenerator.jsp">
        <dsp:param name="product" param="item.auxiliaryData.productRef"/>
        <dsp:param name="siteId" value="${siteId}"/>
      </dsp:include>
    </c:when>
    <c:otherwise>
      <%-- Product is passed. --%>
      <dsp:getvalueof var="displayName" param="skuDisplayName"/>
      <c:if test="${empty displayName}">
        <dsp:getvalueof var="displayName" param="product.displayName"/>
      </c:if>              
      <dsp:getvalueof var="productId" param="product.repositoryId"/>

      <dsp:include page="/global/gadgets/crossSiteLinkGenerator.jsp">
          <dsp:param name="product" param="product"/>
      </dsp:include>
    </c:otherwise>
  </c:choose>

  <dsp:getvalueof var="siteLinkUrl" param="siteLinkUrl"/>
  <dsp:getvalueof var="httpServer" param='httpServer'/>
  <c:choose>
    <c:when test="${empty imgUrl}">
      <%-- Image URL is not specified, just display link --%>
      <dsp:a href="${httpServer}${siteLinkUrl}" title="${displayName}" iclass="atg_store_productTitle">
        <dsp:valueof value="${displayName}">
          <fmt:message key="common.noDisplayName"/>
        </dsp:valueof>
        <dsp:param name="productId" value="${productId}"/>
      </dsp:a>
    </c:when>
    <c:otherwise>
      <%-- Image URL is specified, display link as image --%>
      <dsp:a href="${httpServer}${siteLinkUrl}" title="${displayName}">      
        <c:choose>
          <c:when test="${includeTitle}">
            <img src="${imgUrl}" alt="${displayName}" title="${displayName}"/>
          </c:when>
          <c:otherwise>
            <img src="${imgUrl}" alt="${displayName}"/>
          </c:otherwise>
        </c:choose>       
        <dsp:param name="productId" value="${productId}"/>
      </dsp:a>
    </c:otherwise>
  </c:choose>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/crossSiteLink.jsp#1 $$Change: 735822 $--%>

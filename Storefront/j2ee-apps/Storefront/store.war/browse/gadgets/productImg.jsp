<%-- 
  This page renders product's image. If 'showAsLink' boolean is 'true' then image 
  is displayed as link to product page.
   
  Required parameters:
    product
      The product repository item whose image we display
    image
      image for the given product, could be 'full',
      'large', 'medium', 'small, or 'thumbnail'.  
      
  Optional parameters:
    showAsLink
      Specifies if product thumbnail image should be displayed as link 
      (if possible), 'true' by default
    siteId
      The site ID that should be used to generate product link
    linkImage
      The boolean indicating if the image should be a link (defaults to true)
    httpServer
      The URL prefix with protocol, server name and port to prepend to all image URLs 
      and image links. This parameter is usually specified in email templates
      as they need to render images with fully qualified URLs.
    categoryNavIds
      The colon-separated list representing the category navigation trail
    categoryNav
      Determines if breadcrumbs are updated to reflect category navigation trail on click through
    navLinkAction
      The type of breadcrumb navigation to use for product detail links. 
      Valid values are push, pop, or jump. Default is jump.
    defaultImageSize
      Size of default image to use if image.url isn't set
--%>
<dsp:page>

  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
 
  <%-- get product's display name and escape special characters in it --%> 
  <dsp:getvalueof var="displayName" vartype="java.lang.String" param="product.displayName"/>
  <c:set var="displayName"><c:out value="${displayName}" escapeXml="true"/></c:set>
  
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" param="httpServer"/>
  <dsp:getvalueof var="linkImage" vartype="java.lang.String" param="linkImage"/>
  <dsp:getvalueof var="showAsLink" vartype="java.lang.String" param="showAsLink"/>
  <dsp:getvalueof var="imageUrl" vartype="java.lang.String" param="image.url"/>
  <dsp:getvalueof var="defaultImageSize" vartype="java.lang.String" param="defaultImageSize"/>
  
  <%-- Check if image URL is found --%>
  <c:choose>
    <c:when test="${empty imageUrl}">
      <%-- Image URL is empty, display 'unavailable' image --%>
      <c:choose>
        <c:when test="${defaultImageSize eq 'small'}">
          <img src="${httpServer}/crsdocroot/content/images/products/small/MissingProduct_small.jpg" alt="${displayName}"/>
        </c:when>
        <c:when test="${defaultImageSize eq 'large'}">
          <img src="${httpServer}/crsdocroot/content/images/products/large/MissingProduct_large.jpg" alt="${displayName}"/>
        </c:when>
        <c:when test="${defaultImageSize eq 'medium'}">
          <img src="${httpServer}/crsdocroot/content/images/products/medium/MissingProduct_medium.jpg" alt="${displayName}"/>
        </c:when>
        <c:when test="${defaultImageSize eq 'full'}">
          <img src="${httpServer}/crsdocroot/content/images/products/full/MissingProduct_full.jpg" alt="${displayName}"/>
        </c:when>
        <c:otherwise>
          <img src="${httpServer}/crsdocroot/content/images/products/thumb/MissingProduct_thumb.jpg" alt="${displayName}"/>
        </c:otherwise>
      </c:choose>
      
    </c:when>
    <c:otherwise>
      <c:choose>
        <c:when test="${(showAsLink == 'false') or (linkImage == 'false')}">
          <%-- 
            The showAsLink is false so don't display image as link, no product
            URL should be generated.
           --%>
          <c:set var="productUrl" value=""/>
        </c:when>
        <c:otherwise>
          <%-- The showAsLink is true so generate URL to product page. --%>
          <dsp:include page="/global/gadgets/productLinkGenerator.jsp">
            <dsp:param name="product" param="product"/>
            <dsp:param name="navLinkAction" param="navLinkAction"/>
            <dsp:param name="categoryNavIds" param="categoryNavIds"/>
            <dsp:param name="categoryNav" param="categoryNav"/>
            <dsp:param name="siteId" param="siteId"/>
          </dsp:include>
        </c:otherwise>
      </c:choose>
      
      <c:choose>
        <c:when test="${empty productUrl}">
          <%-- There is no product URL, just display image --%>
          <img src="${httpServer}${imageUrl}" border="0" alt="${displayName}"/>
        </c:when>
        <c:otherwise>
          <%-- Display image as link to product page. --%>
          <a href="${httpServer}${productUrl}" title="${displayName}">
            <img src="${httpServer}${imageUrl}" border="0" alt="${displayName}"/>
          </a>
        </c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/productImg.jsp#1 $$Change: 735822 $--%>

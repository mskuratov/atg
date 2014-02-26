<%-- 
  This JSP renders a product items thumbnail image as a link which can be
  clicked on, the user will be directed to the product detail page.
  
  Required Parameters:
    product
      The product repository item
    
    commerceItem
      A commerce item object 
      
  Optional Parameters:
    alternateImage
      An alternate image to display instead of the product thumbnail.
    
    linkImage
      Boolean indicating if the image should link (defaults to true)
    
    httpServer
      Prepended to all images and image links
      
    siteId
      The current site id
--%>
<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/Cache"/>
 
  <dsp:getvalueof var="productId" vartype="java.lang.String" param="product.repositoryId"/>
  <dsp:getvalueof var="alternateImageId" vartype="java.lang.String" 
                  param="alternateImage.repositoryId"/>
  <dsp:getvalueof var="pageurl" vartype="java.lang.String" param="product.template.url"/>
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" param="httpServer"/>
  <dsp:getvalueof var="linkImage" vartype="java.lang.String" param="linkImage"/>
  <dsp:getvalueof var="commerceItemId" vartype="java.lang.String" param="commerceItem.id"/>
  <dsp:getvalueof var="productName" vartype="java.lang.String" param="product.displayName"/>
  <dsp:getvalueof var="siteId" vartype="java.lang.String" param="siteId"/>

  <%-- If the productName on the product is empty use the commerceItem --%>  
  <c:if test="${empty productName}">
    <c:set var="productName">
      <dsp:valueof param="commerceItem.productRef.displayName"/>
    </c:set>
  </c:if>
  
  <%-- 
    Generate cache keys, the cache key will be the same for all users when the placeholders in the
    key are the same (for example if its the same product, with the same alternate image, on the 
    same site..etc). The cache is a global component so all users will be able to use the cached
    value even if they werent responsible for it being placed in the cache.  
  --%>
  <dsp:getvalueof var="cache_key" 
                  value="bp_fpti_cart_${productId}_${alternateImageId}_${linkImage}_${httpServer}_${siteId}"/>

  <c:if test="${not empty commerceItemId}">
    <dsp:getvalueof var="cache_key" 
                    value="bp_fpti_cart_${commerceItemId}_${productId}_${alternateImageId}_${linkImage}_${httpServer}_${siteId}"/>
  </c:if>
  
  <%--
    Cache is used to cache the contents of its open parameter "output". It
    improves performance for pages that generate dynamic content which is
    the same for all users.
         
    Input Parameters:
      key - Contains a value that uniquely identifies the content
         
    Open Parameters:
      output - The serviced value is cached for use next time    
  --%> 
  <dsp:droplet name="Cache">
    <dsp:param name="key" value="${cache_key}"/>
    <dsp:oparam name="output">  
      <dsp:getvalueof var="alternateImageUrl" param="alternateImage.url"/>
      
      <c:choose>
        <%-- No alternate image passed, use the products image --%>
        <c:when test="${empty alternateImageUrl}">
          <dsp:getvalueof var="productThumbnailImageUrl" param="product.smallImage.url"/>
          <c:choose>
            <%-- The products smallImage is set --%>
            <c:when test="${not empty productThumbnailImageUrl}">
              <%-- Determine if we are to link the image --%>
              <c:choose>
                <%-- If linkImage is false only display the image  --%>
                <c:when test="${linkImage == false}">
                  <img  src="<dsp:valueof param='httpServer'/><dsp:valueof param='product.smallImage.url'/>"
                        alt="${productName}"/>
                </c:when>
                <%-- If linkImage is true link the image to the product details page --%>
                <c:otherwise> 
                  <dsp:getvalueof var="productTemplateUrl" param="product.template.url"/>
                  
                  <%-- Make sure we have a template --%>
                  <c:choose>
                    <%-- The product has a template URL --%>
                    <c:when test="${not empty productTemplateUrl}">
                      <dsp:getvalueof var="url_part1" param="httpServer"/>
                      <dsp:getvalueof var="url_part2" param="product.smallImage.url"/>
                      
                      <%-- Build the URL --%>
                      <c:set var="imgUrl" value="${url_part1}${url_part2}"/>
                      <c:if test="${empty url_part1}">
                        <c:set var="imgUrl" value="${url_part2}"/>
                      </c:if> 

                      <%-- Generate the link --%>
                      <dsp:include page="/global/gadgets/crossSiteLink.jsp">
                        <dsp:param name="item" param="commerceItem"/>
                        <dsp:param name="imgUrl" value="${imgUrl}"/>
                      </dsp:include>
                    </c:when>
                    <%-- The product does not have a template URL just display the image --%>
                    <c:otherwise>
                      <%-- The product template.url is not set --%>
                      <img src="<dsp:valueof param='httpServer'/><dsp:valueof param='product.smallImage.url'/>"
                           alt="${productName}"/>
                    </c:otherwise>
                  </c:choose>
                </c:otherwise>
              </c:choose>
            </c:when>
            <%-- The products smallImage is not set --%>
            <c:otherwise>
              <img  height="105" width="105" src="<dsp:valueof param='httpServer'/>/crsdocroot/content/images/products/small/MissingProduct_small.jpg"
                    width="85" height="85" alt="${productName}">
            </c:otherwise>
          </c:choose>
        </c:when>
        
        <%-- We have an alternate image to display --%>
        <c:otherwise>
          <c:choose>
            <%-- Just display the alternate image without linking it --%>
            <c:when test="${linkImage == 'false'}">
              <img src="<dsp:valueof param='httpServer'/><dsp:valueof param='alternateImage.url'/>"
                   alt="${productName}"/>
            </c:when>
            <%-- Link the alternate image to the product detail page--%>
            <c:otherwise> 
              <dsp:getvalueof var="templateUrl" param="product.template.url"/>
              <c:choose>
                <%-- Product template is set --%>
                <c:when test="${not empty templateUrl}">
                  <dsp:getvalueof var="alternateImageUrl" vartype="java.lang.String" 
                                  param="alternateImage.url"/>
          
                  <%-- Make sure the httpServer is set before making the image url --%>
                  <c:choose>
                    <c:when test="${empty httpServer}">
                      <dsp:getvalueof var="imageurl" vartype="java.lang.String" 
                                      value="${alternateImageUrl}"/>
                    </c:when>
                    <c:otherwise>
                      <dsp:getvalueof var="imageurl" vartype="java.lang.String" 
                                      value="${httpServer}${alternateImageUrl}"/>
                    </c:otherwise>
                  </c:choose>
                  
                  <%-- Generate the link --%>
                  <dsp:include page="/global/gadgets/crossSiteLink.jsp">
                    <dsp:param name="item" param="commerceItem"/>
                    <dsp:param name="imgUrl" value="${imageurl}"/>
                  </dsp:include>
                </c:when>
                <%-- Product template not set --%>
                <c:otherwise>
                  <img src="<dsp:valueof param='httpServer'/><dsp:valueof param='alternateImage.url'/>" 
                       alt="${productName}"/>
                </c:otherwise>
              </c:choose>
            
            </c:otherwise>
          </c:choose>
        </c:otherwise>
      </c:choose>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/productImgCart.jsp#1 $$Change: 735822 $--%>

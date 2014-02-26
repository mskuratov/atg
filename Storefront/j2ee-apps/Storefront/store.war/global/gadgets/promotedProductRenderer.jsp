<%--
  Page renders promotional item. When the rendered product is clicked the user
  will be brought to the product details page for that particular product.
    
  Required Parameters:
    product
      The product repository item to display

  Optional Parameters:   
    imagesize
      The size of image to render (thumbnail, promo or medium) default is promo.
--%>
<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  <dsp:importbean bean="/atg/store/droplet/CategoryBySiteDroplet"/>
  <dsp:importbean bean="/atg/store/catalog/CatalogNavigation"/>

  <dsp:getvalueof var="categoryId" bean="CatalogNavigation.currentCategory" />  
  
  <c:if test="${empty categoryId}">
    <dsp:getvalueof var="parentCategories" param="product.parentCategories"/>
      
    <c:choose>
      <c:when test="${fn:length(parentCategories) > 1}">
        <%-- 
          This droplet retrieves the parent categories from a passed in product
          item. It then iterates through each category checking if the siteIds
          property contains the passed in siteId. If the siteId is matched, a
          'categoryId' output parameter is set with the category id of the matched
          category.
        
          Input Parameters:
            siteId
              The siteId of the site we want to get the corresponding category of.
            product
              The product RepositoryItem that we want to retrieve the parentCategories of.

          Output Parameters:
            categoryId
              The category id of the category that is relevant to the passed in site id.
        
          Open Parameters:
            output
              Serviced when no errors occur.
        --%>
        <dsp:droplet name="CategoryBySiteDroplet">
          <dsp:param name="siteId" param="siteId"/>
          <dsp:param name="product" param="product"/>
        
          <dsp:oparam name="output">
            <dsp:getvalueof var="categoryId" param="categoryId"/>
          </dsp:oparam>
        </dsp:droplet>
      </c:when>
      <c:otherwise>
        <dsp:getvalueof var="categoryId" param="product.parentCategory.id"/>
      </c:otherwise>
    </c:choose>
  </c:if>
     
  <%-- Generate the siteLinkURL link to the product --%>
  <dsp:include page="/global/gadgets/productLinkGenerator.jsp">
    <dsp:param name="product" param="product"/>
    <dsp:param name="categoryId" value="${categoryId}"/>
  </dsp:include>
   
  <dsp:getvalueof var="pageUrl" value="${siteLinkUrl}"/>
  <dsp:getvalueof var="siteId" param="siteId"/>
  
  <dsp:getvalueof var="item" param="product"/> 
  <preview:repositoryItem item="${item}">
    <%-- Select image to display --%>
    <dsp:a href="${pageUrl}">
        <span class="atg_store_productImage">      
        <dsp:getvalueof var="imagesize" param="imagesize"/>
        <c:choose>
          <c:when test="${imagesize == 'thumbnail'}">
            <dsp:include page="/browse/gadgets/productImg.jsp">
              <dsp:param name="image" param="product.smallImage"/>
              <dsp:param name="product" param="product"/>
              <dsp:param name="showAsLink" value="false"/>
              <dsp:param name="defaultImageSize" value="small"/>     
            </dsp:include>
          </c:when>
          <c:when test="${imagesize == 'medium'}">
            <dsp:include page="/browse/gadgets/productImg.jsp">
              <dsp:param name="product" param="product"/>
              <dsp:param name="image" param="product.mediumImage"/>
              <dsp:param name="showAsLink" value="false"/>
              <dsp:param name="defaultImageSize" value="medium"/>     
            </dsp:include>
          </c:when>
        </c:choose>    
      </span>
 
      <%-- Display name --%>
      <span class="atg_store_productTitle">      
        <dsp:include page="/browse/gadgets/productName.jsp">
          <dsp:param name="product" param="product"/>
          <dsp:param name="categoryId" value="${categoryId }"/>
          <dsp:param name="showAsLink" value="false"/>
        </dsp:include>      
      </span>
  
      <%-- Check the size of the sku array to see how we handle things --%>
      <dsp:getvalueof var="childSKUs" param="product.childSKUs"/>
      <c:set var="totalSKUs" value="${fn:length(childSKUs)}"/>
       
      <%-- 
        Droplet for testing if the product has a single SKU
      
        Input parameters:
          obj1
            number of SKUs for the product
          obj2
            1 - minimal number of SKU for the product
          
        Open parameters:
          equal
            product has the only one SKU
          default
            product has more than one SKU           
        --%>
      <dsp:droplet name="Compare">
        <dsp:param name="obj1" value="${totalSKUs}" converter="number"/>
        <dsp:param name="obj2" value="1" converter="number"/>
        <dsp:oparam name="equal">  
          <%-- Display Price --%>
          <span class="atg_store_productPrice">            
            <dsp:include page="/global/gadgets/priceLookup.jsp">
              <dsp:param name="product" param="product"/>
              <dsp:param name="sku" param="product.childSKUs[0]"/>
            </dsp:include>            
          </span>                         
        </dsp:oparam>            
        <dsp:oparam name="default">          
          <%-- Display Price Range (and Get Details link)--%>          
          <span class="atg_store_productPrice">            
            <dsp:include page="/global/gadgets/priceRange.jsp">
              <dsp:param name="product" param="product"/>
            </dsp:include>            
          </span>            
        </dsp:oparam>
      </dsp:droplet> 
                   
      <dsp:include page="/global/gadgets/siteIndicator.jsp">
        <dsp:param name="mode" value="name"/>              
        <dsp:param name="product" param="product"/>
        <dsp:param name="displayCurrentSite" value="false"/>
        <dsp:param name="siteId" value="${siteId}"/>
      </dsp:include>
    </dsp:a>
  </preview:repositoryItem> 
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/promotedProductRenderer.jsp#2 $$Change: 788278 $--%>

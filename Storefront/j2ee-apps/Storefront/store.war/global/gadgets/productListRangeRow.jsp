<%-- 
  Renders a product entry in a row of product listings.
  
  Required Parameters: 
    product
      Product repository item to display
    categoryNav
      Determines if breadcrumbs are updated to reflect category navigation on click through
  Optional Parameters:
    displaySiteIndicator
      Indicates whether or not to display a site indicator for cross site products
    mode
      Indicates whether to display the "icon" or "name" site indicators
    searchClickId
      Search click ID parameter appended to the URL to notify reporting service. This parameter
      tells reporting service which search query returned the product. It is usually appended to
      product URLs on search results page.
--%>
<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  <dsp:importbean bean="/atg/dynamo/servlet/RequestLocale" var="requestLocale"/>
  <dsp:importbean bean="/atg/repository/seo/BrowserTyperDroplet"/>  
  
  <dsp:getvalueof var="displaySiteIndicator" param="displaySiteIndicator"/>
  <dsp:getvalueof var="productId" vartype="java.lang.String" param="product.repositoryId"/>
  <dsp:getvalueof var="categoryNav" param="categoryNav"/>
  <dsp:getvalueof var="mode" param="mode"/>  

  <%-- 
    Generates a URL for the product, the URL is stored in the productUrl request scoped variable 
  --%>
  <dsp:include page="productLinkGenerator.jsp">
    <dsp:param name="product" param="product"/>
    <dsp:param name="navLinkAction" param="navLinkAction"/>
    <dsp:param name="categoryNavIds" param="categoryNavIds"/>
    <dsp:param name="categoryNav" param="categoryNav"/>
    <dsp:param name="searchClickId" param="searchClickId"/>
  </dsp:include>
  
  <dsp:getvalueof var="item" param="product"/>
  <preview:repositoryItem item="${item}">
    <a href="${productUrl}">
      <%-- Listing image --%>
      <span class="atg_store_productImage">     
        <dsp:include page="/browse/gadgets/productImg.jsp">
          <dsp:param name="product" param="product" />
          <dsp:param name="image" param="product.mediumImage" />
          <dsp:param name="categoryNav" value="${categoryNav}" />
          <dsp:param name="showAsLink" value="false"/>
          <dsp:param name="defaultImageSize" value="medium"/>     
        </dsp:include>
      </span>
  
      <%-- Product name --%>
      <span class="atg_store_productTitle">
        <dsp:include  page="/browse/gadgets/productName.jsp">
          <dsp:param name="product" param="product" />
          <dsp:param name="categoryNav" value="${categoryNav}" />
          <dsp:param name="showAsLink" value="false"/>
        </dsp:include>
      </span>  
  
      <%-- 
        Check the size of the sku array to see how we handle price, e.g a range or a single price
      --%>
      <dsp:getvalueof var="childSKUs" param="product.childSKUs"/>
      <c:set var="totalSKUs" value="${fn:length(childSKUs)}"/>
    
      <%--
        The Compare droplet renders one of its open parameters based on
        the relative values of the obj1 and obj2 input parameters. Here we
        use it to determine if we have a price range or a single price, if
        we have a range the prices are different, if its a single price the
        prices are the same.
              
        Input Parameters:
          obj1 - The first object to be compared (e.g obj1 > obj2)
                    
          obj2 - The second object to be compared
                     
        Open Parameters:               
          default - Rendered If either obj1 or obj2 is null or if no other
                    oparam is rendered
                
          equal - Render If obj1 is equal to obj2
      --%> 
      <dsp:droplet name="Compare">
        <dsp:param name="obj1" value="${totalSKUs}" converter="number" />
        <dsp:param name="obj2" value="1" converter="number"/>
        <dsp:oparam name="equal">
          <%-- Size is one, display Price --%>
          <dsp:param name="sku" param="product.childSKUs[0]"/>
          <span class="atg_store_productPrice">         
            <%@ include file="priceLookup.jsp" %>
          </span>
          
        <%-- end alt. content --%>
        </dsp:oparam>
        <dsp:oparam name="default">
          <%-- Size is not one, display Price Range --%>
          <span class="atg_store_productPrice">
            <%@ include file="priceRange.jsp" %>
          </span>
        </dsp:oparam>
      </dsp:droplet> 
    
      <%-- Site Icon --%>
      <c:if test="${displaySiteIndicator}">
        <c:if test="${empty mode}">
          <dsp:getvalueof var="mode" value="icon"/>
        </c:if>

        <dsp:include page="/global/gadgets/siteIndicator.jsp">
          <dsp:param name="mode" value="${mode}"/>              
          <dsp:param name="product" param="product"/>
        </dsp:include>
      </c:if>
    </a>
  
    <dsp:getvalueof var="productTemplateUrl" param="product.template.url"/>
    <c:if test="${not empty productTemplateUrl}">
      <%-- Product Template is set --%>
      <dsp:getvalueof var="pageurl" vartype="java.lang.String" param="product.template.url"/>

      <%--
        CatalogItemLink is used to generate a URL string for a repository 
        item. Here we generate the link to the product item passed into
        the droplet.
           
        Input Parameters:
          item - The repository item or the ID of the repository item to generate the URL for
                 
        Open Parameters:
          output - Serviced when no errors occur
              
        Output Parameters:
          url - The URL generated by the droplet
      ---%>
      <dsp:droplet name="/atg/repository/seo/CatalogItemLink">
        <dsp:param name="item" param="product"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="pageurl" vartype="java.lang.String" param="url"/>
        </dsp:oparam>
      </dsp:droplet>
    </c:if> 
  </preview:repositoryItem>
</dsp:page>                              

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/productListRangeRow.jsp#1 $$Change: 735822 $--%>

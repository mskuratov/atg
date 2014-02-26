<%--
  Page renders related products for the currently viewed product. The relatedProducts are filtered
  based on the cart sharing site group to which the current site belongs.
  
  This page expects the following parameters
  
  Required Parameters:
    product
      the product repository item for which related products should be displayed.
  
  Optional Parameters:
    None
--%>
<dsp:page>

  <dsp:importbean bean="/atg/store/droplet/ItemSiteGroupFilterDroplet"/>
  <dsp:importbean bean="/atg/store/droplet/CatalogItemFilterDroplet"/>

  <dsp:getvalueof var="product" param="product" />
  
  <%-- Check that product parameter is not empty. --%>
  <c:if test="${not empty product}">
  
    <%--
      Filters out items that don't belong to the same cart sharing site group
      as the current site.
       
      Input parameters:
        collection
          collection of products to filter based on the site group
       
      Output parameters:
        filteredCollection
          filtered collection
    --%>
    <dsp:droplet name="ItemSiteGroupFilterDroplet">
      <dsp:param name="collection" param="product.relatedProducts"/>
       
      <dsp:oparam name="output">
          
        <%--
          This droplet filters out items with invalid dates.

          Input parameters:
            collection
              Collection of items to be filtered. 

          Output parameters:
            filteredCollection
              Resulting filtered collection.

          Open parameters:
            output
              Always rendered.
        --%>
        <dsp:droplet name="CatalogItemFilterDroplet">
          <dsp:param name="collection" param="filteredCollection"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="filteredRelatedItems" param="filteredCollection"/>
          </dsp:oparam>
          <dsp:oparam name="empty">
            <dsp:getvalueof var="filteredRelatedItems" value=""/>
          </dsp:oparam>
        </dsp:droplet> 
          
        <div id="atg_store_recommendedProducts" class="atg_store_relatedProducts">
          <h3>
            <fmt:message key="browse_recommendations.weAlsoSuggest" />
          </h3>
          <ul>
            <dsp:getvalueof var="size" value="${fn:length(filteredRelatedItems)}"/>                
            <%-- Only show six of them. --%>
            <c:forEach var="relatedProduct" items="${filteredRelatedItems}" varStatus="status" begin="0" end="5">
              <preview:repositoryItem item="${relatedProduct}">
                <dsp:param name="relatedProduct" value="${relatedProduct}"/>
                <dsp:getvalueof var="imageUrl" param="relatedProduct.thumbnailImage.url"/>
               
                <%-- If related product has thumbnail image url set display it. --%>
                <c:if test="${not empty imageUrl}">
                  <li class="<crs:listClass count="${status.count}" size="${ size < 5 ? size : 5}" selected="false"/>">
                    <%-- Generate cross site link for current related product. --%> 
                    <dsp:include page="/global/gadgets/crossSiteLinkGenerator.jsp">
                      <dsp:param name="product" param="relatedProduct"/>
                    </dsp:include>
                         
                    <dsp:getvalueof var="siteLinkUrl" param="siteLinkUrl"/>
                    <dsp:getvalueof var="productId" param="relatedProduct.repositoryId"/>
                    <dsp:getvalueof var="displayName" param="relatedProduct.displayName"/>                 
                   
                    <%-- Show thumbnail image as link for related product. --%>
                    <dsp:a href="${siteLinkUrl}" title="${displayName}">      
                      <img src="${imageUrl}" alt="${displayName}" title="${displayName}"/> 
                      <dsp:param name="productId" value="${productId}"/>
                    
                      
                      <%-- Show cross site indicator. --%>      
                      <dsp:include page="/global/gadgets/siteIndicator.jsp">
                        <dsp:param name="mode" value="name"/>              
                        <dsp:param name="product" param="relatedProduct"/>
                        <dsp:param name="displayCurrentSite" value="false"/>
                      </dsp:include>
                    </dsp:a>
                  </li>
                </c:if>
              </preview:repositoryItem>
            </c:forEach><%-- End For Each related product --%>
          </ul>
        </div>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>  
       
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/relatedProducts.jsp#2 $$Change: 788278 $--%>


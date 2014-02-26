<%--
  This gadget renders recommendations container DIV element for product detail pages. The recommendations
  container DIV is inserted into the page in the place where the generated recommendations should appear.
  The recommendations container DIV will provide information for the recommendations service and
  custom renderers / builders that are responsible for rendering recommendations.  
  
  Here is the list of parameters that will be configured within the recommendations 
  DIV container on this page and passed to recommendations service or used by recommendations
  renderer / builder:
    headerText
      Header text to display above recommendations slot
    numRecs
      Number of recommendations that should be returned by recommendation service
    renderer
      Custom Recommendations Renderer to use for recommendations rendering.
    -rec-builder
      Custom Recommendations Builder to render each particular recommendation
    dataItems
      Additional catalog properties to be included into recommendation's data object, by default
      only image, product URL and display name are included. The following parameter will be included
      for product recommendations slot:
      - title_{language} - localized display name,
      - thumb_image_link - product's thumbnail image URL,
      - availableStores - the list of site IDs the product is available on.
    -alternativeStoreIds
      The list of site IDs (except of current site ID)in order of preference that should be
      used to lookup product's price and product link.
    -language
      Language from the current request's locale, that should be used to lookup localized display name
      in recommendations data object.
    -baseURL_{siteId}
      The site-specific base URL (e.g. '/crs/storeus/'). The custom builder will use it to prepend
      product relative URLs returned in recommendation's data.
    -{siteId}_name
      The site's display names that will be used in site indicators.
    -siteIndicatorText
      The localized site indicator text.
    recommendationCategories
      The list of categories paths to pass to Recommendations engine in order to use in custom campaign.
    includeRandom
      The manually specified list of products that should be included into generated recommendations set.
      For the product detail page the list will contain merchandiser defined cross sells specified in the 
      products's relatedProducts property. Before passing merchandiser defined cross sells to ATG Recommendations 
      service they are filtered based on the cart sharing site group to which the current site belongs.
  
  Required parameters:
    product
      Product repository item for which the recommendations slot will be generated.
      
  Optional parameters:
    None.
 --%>
<dsp:page>
  
   <%--
    ComponentExists droplet conditionally renders one of its output parameters
    depending on whether or not a specified Nucleus path currently refers to a
    non-null object.  It it used to query whether a particular component has been
    instantiated, in this case StoreRecommendationsConfiguration. If the StoreRecommendationsConfiguration
    component has not been instantiated recommendations container will not be included.
      
    Input Parameters:
      path - The path to a component
       
    Open Parameters:
      true
        Rendered if the component 'path' has been instantiated.
      false
        Rendered if the component 'path' has not been instantiated. 
  --%>
  <dsp:droplet name="/atg/dynamo/droplet/ComponentExists">
  
    <dsp:param name="path" value="/atg/store/recommendations/StoreRecommendationsConfiguration"/>
    <dsp:oparam name="true">
      <dsp:importbean bean="/atg/dynamo/droplet/multisite/SiteLinkDroplet"/>
      <dsp:importbean bean="/atg/store/recommendations/droplet/RecommendationAlternativeSiteIds"/>
      <dsp:importbean bean="/atg/store/recommendations/droplet/RecommendationCategoriesForProduct"/>      
      <dsp:importbean bean="/atg/store/droplet/ItemSiteGroupFilterDroplet"/>
      <dsp:importbean bean="/atg/store/droplet/CatalogItemFilterDroplet"/>
    
      <dsp:getvalueof var="currentSiteId" bean="/atg/multisite/Site.id"/>
      <dsp:getvalueof var="language" bean="/atg/dynamo/servlet/RequestLocale.locale.language" />
      <dsp:getvalueof var="sites" bean="/atg/multisite/SiteManager.activeSites"/>
     
      <%-- Recommendations DIV container element --%>
      <div id="cs-recslot-product" class="cs-slot">
        <dl class="cs-cfg">
        
          <%-- Header text that will be display above the recommendations slot --%>
          <dt>headerText</dt>
          <dd><fmt:message key="browse_recommendations.weAlsoSuggest"/></dd>
        
          <%--
            The number of recommendations that should be returned by the recommendations service
            for this slot.
           --%>
          <dt>numRecs</dt>
          <dd>6</dd>
          
          <%-- Custom renderer that is used to renderer recommendations slot layout.--%>
          <dt>renderer</dt>
          <dd>crs</dd>
          
          <%-- Custom recommendation builder that is used to render each particular recommendation --%>          
          <dt>-rec-builder</dt>
          <dd>Product_Recommendation_Builder</dd>
          
          <%--
            Additional catalog properties to include into recommendation data object that will be
            used by custom renderer / builder.
           --%>
          <dt>dataItems</dt>
          <dd>
            <dl> 
              
              <%-- Include localized title for the current request's locale language. --%>
              <dt>title_${language}</dt>
              
              <%-- Include product's thumbnail image URL that will be used in recommendation display. --%>                            
              <dt>thumb_image_link</dt>
              
              <%--
                Include the list of store IDs on which the recommended product is available on.
                Will be used by custom builder to determine the best matching store ID to use
                to get product link, price and display site indicator.
              --%>
              <dt>availableStores</dt>
            </dl>
          </dd>
         
          <%-- 
            The list of site IDs (except of current site ID)in order of preference that should be
            used to lookup product's price and product link. The custom builder will first check
            if the recommended product is available on the current store, if not will check the 
            store IDs from the alternativeStoreIds list. At first include site IDs from the
            cart sharing group to which current site belongs. 
           --%>
          <dt>-alternativeStoreIds</dt>
          <dd>
            <dl>
              <%--
                This droplet generates the list of site IDs in the order of preference
                that should be used by custom builder to determine best matching site
                specific product URL, price, etc.
            
                Input parameters:
                  includeActiveSites - whether to include only active sites into the result.
                  excludeInputSite - whether current site should be excluded from the result.
                  
                Output parameters:
                  siteIds - the list of site IDs in order of preference to use
                            to determine best matching site ID for the product.
              
                Open Parameters:
                  output - rendered if no errors occur
           --%>
              <dsp:droplet name="RecommendationAlternativeSiteIds">
                <dsp:param name="includeActiveSites" value="true"/>
                <dsp:param name="excludeInputSite" value="true"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="siteIds" param="siteIds"/>
                  <c:forEach var="siteId" items="${siteIds}">
                    <dt>${siteId}</dt>
                  </c:forEach>
                </dsp:oparam>
              </dsp:droplet>
              
            </dl>
          </dd>
          
          <%-- 
            Specify the current request's locale language that will be used by custom builder
            to lookup product's localized display name.
          --%>
          <dt>-language</dt>
          <dd>${language}</dd>          
          
          <c:forEach var="site" items="${sites}">
            <%-- Configure site-specific base URL that will prepend product's URLs. --%>
            <dt>-baseURL_${site.repositoryId}</dt>
            <dd>
            
              <%--
                SiteLinkDroplet gets the URL for a particular site.
    
                Input Parameters:
                  siteId - The site to render the site link for
       
                  path - An optional path string that will be included in the
                  returned url.
     
                  queryParams- Optional url parameters
       
                Open Parameters:
                  output - rendered if no errors occur
       
                Output Parameters:
                  url - The url for the site
              --%>
              <dsp:droplet name="SiteLinkDroplet">
                <dsp:param name="siteId" value="${site.repositoryId}"/>
                <dsp:param name="path" value="/"/>
                <dsp:oparam name="output">
                  <dsp:valueof param="url"/>
                </dsp:oparam>
              </dsp:droplet>
            </dd>
            
            <%--
              Specify site name for the site ID that will be used by custom builder to display
              site indicator if the product is not available on the current site.
            --%>
            <c:if test="${site.repositoryId != currentSiteId}">
              <dsp:param name="site" value="${site}"/>
              <dt>-${site.repositoryId}_name</dt><dd><dsp:valueof param="site.name"/></dd>
            </c:if>
            
          </c:forEach>
          
          <%-- The localized site indicator text. --%>
          <dt>-siteIndicatorText</dt>
          <dd><fmt:message key="siteindicator.prefix" /></dd>
          
          <%--
            This droplet determines recommendations categories for the given product
            that should be used by custom recommendations campaign for generating
            recommendation.
            
            Input parameters:
              product - product repository item for which recommendation categories
                        should be generated.
                        
            Output parameters:
              recommendationCategories - the list of recommendations categories generated.
              
            Open Parameters:
              output - rendered if no errors occur
           --%>
          <dsp:droplet name="RecommendationCategoriesForProduct">
            <dsp:param name="product" param="product"/>
            <dsp:oparam name="output">
              <%--
                Include recommendation Categories that will be used by the custom campaign 
                to generate recommendations.
               --%>
              <dsp:getvalueof var="recommendationCategories" param="recommendationCategories"/>
              <c:if test="${not empty recommendationCategories}">
                <dt>recommendationcategories</dt>
                <dd>
                  <dl>
                    <c:forEach var="category" items="${recommendationCategories}">
                      <dt>${category}</dt>
                    </c:forEach>
                  </dl>
                </dd>   
              </c:if>
            </dsp:oparam>
          </dsp:droplet>
          
          <%-- Filters collection of items using CatalogItemFilter --%>
          <dsp:droplet name="CatalogItemFilterDroplet">
            <dsp:param name="collection" param="product.relatedProducts" />
            <dsp:oparam name="output">
          
              <dsp:getvalueof var="filteredRelatedProducts" param="filteredCollection"/>
          
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
                <dsp:param name="collection" value="${filteredRelatedProducts}"/>
        
                <dsp:oparam name="output">
                  <dsp:getvalueof var="filteredRelatedItems" param="filteredCollection"/>
                  <dt>includeRandom</dt>
                  <dd>
                    <dl>
                      <c:forEach var="relatedProduct" items="${filteredRelatedItems}">
                        <dt>${relatedProduct.repositoryId}</dt>
                      </c:forEach>
                    </dl>
                  </dd>   
                </dsp:oparam>
              </dsp:droplet><%-- End of ItemSiteGroupFilterDroplet --%>
          
            </dsp:oparam>
          </dsp:droplet><%-- End of CatalogItemFilterDroplet --%>
         
        </dl>
     </dsp:oparam>
  </dsp:droplet>
        
  <%--
    Specify failover content here. It will contain merchandiser defined cross sells
    specified in products relatedProducts property.
  --%>
  <dsp:include page="relatedProducts.jsp">
    <dsp:param name="product" param="product"/>
  </dsp:include>
  
  <%--
    ComponentExists droplet conditionally renders one of its output parameters
    depending on whether or not a specified Nucleus path currently refers to a
    non-null object.  It it used to query whether a particular component has been
    instantiated, in this case StoreRecommendationsConfiguration. If the StoreRecommendationsConfiguration
    component has not been instantiated recommendations container will not be included.
      
    Input Parameters:
      path - The path to a component
       
    Open Parameters:
      true
        Rendered if the component 'path' has been instantiated.
      false
        Rendered if the component 'path' has not been instantiated. 
  --%>      
  <dsp:droplet name="/atg/dynamo/droplet/ComponentExists">
  
    <dsp:param name="path" value="/atg/store/recommendations/StoreRecommendationsConfiguration"/>
    <dsp:oparam name="true">
      </div>
      <%-- End of Recommendations DIV container element --%>
    </dsp:oparam>
  </dsp:droplet>
        
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/productRecommendationsContainer.jsp#3 $$Change: 788278 $--%>

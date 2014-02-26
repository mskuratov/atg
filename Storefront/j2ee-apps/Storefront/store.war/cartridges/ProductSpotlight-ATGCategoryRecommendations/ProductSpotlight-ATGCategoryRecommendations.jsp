<%--
  This gadget renders recommendations container DIV element for category pages. The recommendations
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
      - listPrice_{siteId} - the site-specific list price,
      - highestPrice_{siteId} - the site-specific highest product's price,
      - highestListPrice_{siteId} - the site-specific highest product's list price,
      - title_{language} - localized display name.
    -language
      Language from the current request's locale, that should be used to lookup localized display name
      in recommendations data object.
    -baseURL_{siteId}
      The site-specific base URL (e.g. '/crs/storeus/'). The custom builder will use it to prepend
      product relative URLs returned in recommendation's data.
    -category_page
      The boolean marker that we are on the category page that will be used by custom renderer to determine
      page's specific markup / styles.
    -oldPriceText
      The localized text to display for old prices when product is on sale.
     exclude
       Contains the list of related products that should be excluded form generated recommendations.
       
  Required parameters:
    None.
      
  Optional parameters;
    None.
 --%>
<dsp:page>  
  
  <dsp:importbean bean="/atg/endeca/assembler/cartridge/CrossCartridgeItemsLookup"/>
  <dsp:importbean bean="/atg/endeca/assembler/cartridge/CrossCartridgeItemsLookupDroplet"/>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  
  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/>  
  <c:set var="categoryId" value="${contentItem.categoryId}"/>
  
  <%-- Check whether user is viewing the category page. --%>
  <c:if test="${not empty categoryId }">
  
     <%--
      ComponentExists droplet conditionally renders one of its output parameters
      depending on whether or not a specified Nucleus path currently refers to a
      non-null object.  It it used to query whether a particular component has been
      instantiated, in this case StoreVersioned. If the StoreVersioned component has 
      not been instantiated we can display the 404.
        
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
      
        <dsp:getvalueof var="currentSiteId" bean="/atg/multisite/Site.id"/>
        <dsp:getvalueof var="language" bean="/atg/dynamo/servlet/RequestLocale.locale.language" />  
            
        <%-- Recommendations DIV container element --%>
        <div id="cs-recslot-category" class="cs-slot">
          <dl class="cs-cfg">
          
            <%-- Header text that will be display above the recommendations slot --%>
            <dt>headerText</dt>
            <dd><crs:outMessage key="product_spotlight_categoryRecommendedProductsTitle" /></dd>
            
            <%--
              The number of recommendations that should be returned by the recommendations service
              for this slot.
             --%>
            <dt>numRecs</dt>
            <dd>${contentItem.numberOfRecords}</dd>
            
            <%-- Custom renderer that is used to renderer recommendations slot layout.--%>
            <dt>renderer</dt>
            <dd>crs</dd>
            
            <%-- Custom recommendation builder that is used to render each particular recommendation --%>
            <dt>-rec-builder</dt>
            <dd>Category_Recommendation_Builder</dd>
            
            <%--
              Additional catalog properties to include into recommendation data object that will be
              used by custom renderer / builder.
             --%>
            <dt>dataItems</dt>
            <dd>
              <dl>
                <dt>listPrice_${currentSiteId}</dt>
                <dt>highestPrice_${currentSiteId}</dt>
                <dt>highestListPrice_${currentSiteId}</dt>
                <dt>title_${language}</dt>              
              </dl>
            </dd>
            
            <%-- 
              Specify the current request's locale language that will be used by custom builder
              to lookup product's localized display name.
            --%>
            <dt>-language</dt>
            <dd>${language}</dd>
            
            <%--
              Add the marker that we are on category page, so that custom renderer can
              assign slot specific styles to recommendations container. 
             --%>
            <dt>-category_page</dt>
            <dd>true</dd>
            
            <%-- Configure site-specific base URL that will prepend product's URLs  --%>
            <dt>-baseURL_${currentSiteId}</dt>
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
                <dsp:param name="path" value="/"/>
                <dsp:oparam name="output">
                  <dsp:valueof param="url"/>
                </dsp:oparam>
              </dsp:droplet>
            </dd>
            
            <%-- The localized text to display for old list prices --%>
            <dt>-oldPriceText</dt>
            <dd><fmt:message key="common.price.old"/></dd>
            
            <%--
              Exclude category related products from recommendations as they will appear
              in other part of the page.
            --%>
            <dsp:droplet name="CrossCartridgeItemsLookupDroplet">
              <dsp:oparam name="output">
              <dsp:getvalueof var="productsToExclude" param="items" />
              </dsp:oparam>
            </dsp:droplet>
            
            <dsp:getvalueof var="childProductsContentItem"
                            bean="/atg/endeca/assembler/cartridge/CrossCartridgeItemsLookup.contentItemsCache.ProductList-ATGCategoryChildren"/>
                            
            <c:if test="${not empty productsToExclude or not empty childProductsContentItem}">
              <dt>exclude</dt>
              <dd>
                <dl>
            </c:if>
            
            <c:if test="${not empty productsToExclude}">
              <c:forEach var="product" items="${productsToExclude}">
                <dt>${product}</dt>
              </c:forEach>
            </c:if>
            
            <c:if test="${not empty childProductsContentItem }">
                  
              <%-- Additionally exclude category child products displayed in category products grid cartridge. --%>
              <dsp:include page="/browse/gadgets/categoryChildProductsLookup.jsp">
                <dsp:param name="contentItem" value="${childProductsContentItem[0]}"/>
                <dsp:param name="productsRenderer" value="/browse/gadgets/recommendationsExcludeProductsRenderer.jsp"/>
                <dsp:param name="shareableTypeId" param="shareableTypeId"/>
                <dsp:param name="sort" param="sort"/>
                <dsp:param name="p" param="p"/>
                <dsp:param name="viewAll" param="viewAll"/>
              </dsp:include>
            </c:if>
              
            <c:if test="${not empty productsToExclude or not empty childProductsContentItem}">
                  </dl>
                </dd>
            </c:if>
            
          </dl>
        </div>
        <%-- End of Recommendations DIV container element --%>
        
      </dsp:oparam>
    </dsp:droplet>
  </c:if>
        
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cartridges/ProductSpotlight-ATGCategoryRecommendations/ProductSpotlight-ATGCategoryRecommendations.jsp#1 $$Change: 742374 $--%>
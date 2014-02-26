<%--
  This page renders links to the top level categories and their subcategories.
  The categories are endeca driven pages and therefore link to endeca urls. 
  The output is displayed across the top banner to facilitate a user navigating
  the site catalog. Its responsible for rendering the selected category in orange
  high-light and also rendering featured products for every category.
  
  Required Parameters:
    None
    
  Optional Parameters:
    product
      product id of the currently viewed product
    
    category
      category id of the currently viewed category/subcategory
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/endeca/cache/DimensionValueCacheDroplet"/>
  <dsp:importbean bean="/atg/store/StoreConfiguration"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/store/droplet/CatalogItemFilterDroplet"/>
  <dsp:importbean bean="/atg/store/catalog/CatalogNavigation"/>
  <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory"/>
  
  <%-- 
    Attempt to resolve selected top category using navigation history to allow correct styling of
    the catalog menu top level category after the shopper has navigated to a category or product page.
    When shopper browses a category/subcategory page the CatalogNavigation component will contain
    the current category, when shopper browses product detail page a product parameter exists.
    If we're rendering the catalog menu after the shopper has performed an alternative navigation
    these should not exist and the category menu option should not highlight as selected.
  --%>
  <dsp:getvalueof var="product" param="product"/>
  <c:if test="${empty product}">
    <dsp:getvalueof var="category" bean="CatalogNavigation.currentCategory"/>
  </c:if>
  
  <c:if test="${not empty product || not empty category}">
    <dsp:getvalueof var="selectedCategory" bean="CatalogNavHistory.topLevelCategory"/>
    <c:if test="${not empty selectedCategory}">
      <dsp:getvalueof var="selectedCategoryId" value="${selectedCategory.repositoryId}"/>
    </c:if>
  </c:if>
   
  <%-- Retrieve list of top level categories from the root category --%>
  <dsp:getvalueof var="topLevelCategories" 
                  bean="Profile.catalog.rootNavigationCategory.childCategories" />

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
        Rendered when the filtered collection is not empty.
  --%>
  <dsp:droplet name="CatalogItemFilterDroplet">
    <dsp:param name="collection" value="${topLevelCategories}"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="topLevelCategories" param="filteredCollection"/>
    </dsp:oparam>
  </dsp:droplet>
   
  <%-- 
    Should we render categories we cant navigate to? Default is only in a 
    preview environment. 
  --%>  
  <dsp:getvalueof var="showUnindexedCategories" bean="StoreConfiguration.showUnindexedCategories" />

  <%--
    Iterate over the list of top level categories to perform the operations
    detailed in the description (rendering top level categories, subcategories,
    featured products, high-lighting).
  --%>
  <c:forEach var="topLevelCategory" items="${topLevelCategories}" varStatus="status">
    <dsp:getvalueof var="topLevelCategoryId" value="${topLevelCategory.repositoryId}" />
    <dsp:getvalueof var="topLevelDisplayName" value="${topLevelCategory.displayName}" />
    <dsp:getvalueof var="subcategories" value="${topLevelCategory.childCategories}" />
    <dsp:getvalueof var="topLevelRelatedProducts" value="${topLevelCategory.relatedProducts}" />

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
          Rendered when the filtered collection is not empty.
    --%>
    <dsp:droplet name="CatalogItemFilterDroplet">
      <dsp:param name="collection" value="${subcategories}"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="subcategories" param="filteredCollection"/>
      </dsp:oparam>
    </dsp:droplet>
            
    <%--
      Look in a cache of ATG repository Id to Endeca navigationStates for the 
      URL that will be used to navigate to when an Endeca driven category page
      is clicked. If the ancestors input parameter is blank we assume a top level
      category.
            
      Input Parameters:
        repositoryId - The repository id of the value we want to retrieve from
                       the cache.
        
      Open Parameters:
        output - Serviced when no errors occur.
       
      Output Parameters:
        dimensionValueCacheEntry - The entry in the cache for this particular
                                   repository id and ancestors.
    --%>
    <c:set var="topLevelCategoryCacheEntry" value=""/>
    <dsp:droplet name="DimensionValueCacheDroplet">
      <dsp:param name="repositoryId" value="${topLevelCategoryId}"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="topLevelCategoryCacheEntry" param="dimensionValueCacheEntry" />
      </dsp:oparam>
    </dsp:droplet>

    <%-- 
      The categoryNavIds parameter indicates the categories navigated to get to the current 
      category/subcategory.So for a root level category we set the categoryNavIds to the current top
      level category
    --%>
    <c:set var="categoryNavIds" value="${topLevelCategoryId}" />
    
    <%-- 
      CATEGORY
      Highlight selected top level category by setting the 'currentCat' class
      if the categoryId we are currently iterating over matches the categoryId
      of the second entry in the navigation stack (a top level category).
    --%>
    <c:if test="${(not empty topLevelCategoryCacheEntry 
                && not empty topLevelCategoryCacheEntry.url) 
                || showUnindexedCategories}">
                
      <li class="${topLevelCategoryId==selectedCategoryId?' currentCat atg_store_dropDownParent':'atg_store_dropDownParent'}">
        <preview:repositoryItem item="${topLevelCategory}">

          <c:choose>
            <%-- Unindexed category? Render the categoryId --%>
            <c:when test="${(empty topLevelCategoryCacheEntry
                          || empty topLevelCategoryCacheEntry.url)}">
              
              <dsp:getvalueof var="actionPath" 
                bean="/atg/endeca/assembler/cartridge/manager/DefaultActionPathProvider.defaultExperienceManagerNavigationActionPath"/>
              
              <dsp:a page="${actionPath}">
                <dsp:param name="categoryId" value="${topLevelCategory}"/>
                <dsp:valueof value="${topLevelDisplayName}">
                  <fmt:message key="common.categoryNameDefault" />
                </dsp:valueof>
              </dsp:a>
            </c:when>
            <%-- Render a link to the category --%>
            <c:otherwise>
              <dsp:a page="${topLevelCategoryCacheEntry.url}">
                <dsp:valueof value="${topLevelDisplayName}">
                  <fmt:message key="common.categoryNameDefault" />
                </dsp:valueof>
              </dsp:a> 
            </c:otherwise>
            </c:choose>

        </preview:repositoryItem> 
      
        <%--
          SUBCATEGORY
          Render the subcategories allowing style and Javascript to handle
          displaying in place versus a flyout.
        --%>
        <div class="atg_store_catSubNv atg_store_dropDownChild">
          <dsp:getvalueof id="size" value="${fn:length(subcategories)}" />

          <ul class="sub_category">
            <%-- Render every child category in our top level category --%>
            <c:forEach var="subcategory" items="${subcategories}" varStatus="status">
              <dsp:getvalueof var="subcategoryId" value="${subcategory.repositoryId}" />
              <dsp:getvalueof var="subcategoryDisplayName" value="${subcategory.displayName}" />
            
              <%--
                Look in a cache of ATG repository Id to Endeca navigationStates for the 
                URL that will be used to navigate to when an Endeca driven category page
                is clicked.
            
                Input Parameters:
                  repositoryId - The repository id of the value we want to retrieve from
                                 the cache.
                
                  ancestors - A colon delimited string of ancestors for this particular
                              category.
        
                Open Parameters:
                  output - Serviced when no errors occur.
       
                Output Parameters:
                  dimensionValueCacheEntry - The entry in the cache for this particular
                                             repository id and ancestors. 
              --%>
              <c:set var="subCategoryCacheEntry"/>
              <dsp:droplet name="DimensionValueCacheDroplet">
                <dsp:param name="repositoryId" value="${subcategoryId}"/>
                <dsp:param name="ancestors" value="${topLevelCategoryId}"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="subCategoryCacheEntry" param="dimensionValueCacheEntry" />
                </dsp:oparam>
              </dsp:droplet>
              
              <c:if test="${(not empty subCategoryCacheEntry 
                          && not empty subCategoryCacheEntry.url)
                          || showUnindexedCategories}">

                <preview:repositoryItem item="${subcategory}">
                  <dsp:getvalueof id="count" value="${status.count}" />
                  
                  <c:choose>
                    <%-- Unindexed category? Render the categoryId --%>
                    <c:when test="${empty subCategoryCacheEntry 
                                 || empty subCategoryCacheEntry.url}">
                       
                      <dsp:getvalueof var="actionPath" 
                        bean="/atg/endeca/assembler/cartridge/manager/DefaultActionPathProvider.defaultExperienceManagerNavigationActionPath"/>

                      <li class="<crs:listClass count="${count}" size="${size}" selected="false"/>">
                        <dsp:a page="${actionPath}">
                          <dsp:param name="categoryId" value="${subcategoryId}"/>
                          <dsp:valueof value="${subcategoryDisplayName}"/>
                        </dsp:a>
                      </li>
                    </c:when>
                    <%-- Render a link to the subcategory --%>
                    <c:otherwise>
                      <li class="<crs:listClass count="${count}" size="${size}" selected="false"/>">
                        <dsp:a page="${subCategoryCacheEntry.url}">
                          <dsp:valueof value="${subcategoryDisplayName}"/>
                        </dsp:a>  
                      </li>
                    </c:otherwise>
                  </c:choose>
                  
                </preview:repositoryItem>
              </c:if>
            </c:forEach>
          </ul>

          <%-- 
            FEATURED PRODUCTS
            Renders featured products menu items for the currently iterated category
          --%>
          <%-- Make sure we have related products for this category --%>
          <c:if  test="${not empty topLevelRelatedProducts}">
            <dsp:param name="categorynavIds" value="${categoryNavIds}"/>
            <ul class="atg_store_featureProducts">
            
              <%--
                CatalogItemFilterDroplet is used to filter a collection of catalog
                items. Here we use this droplet to validate the input collection using 
                the StartEndDateValidator.
              
                Input Parameters:
                  collection - The unfiltered collection
               
                Open Parameters:
                  output - Serviced when no errors occur
            
                Output Parameters:
                  filteredCollection - the filtered collection
              --%>
              <dsp:droplet name="CatalogItemFilterDroplet">
                <dsp:param name="collection" value="${topLevelRelatedProducts}" />
                <dsp:oparam name="output">
                  <li class="atg_store_featureProductsTitle">
                    <fmt:message key="navigation.featuredProducts" />
                  </li>

                  <dsp:getvalueof var="filteredCollection" param="filteredCollection"/>
                
                  <%-- Display only five featured products --%>
                  <c:forEach var="featuredProduct" items="${filteredCollection}" begin="0" end="4">
                    <preview:repositoryItem item="${featuredProduct}">
                      <li>
                        <dsp:include page="/browse/gadgets/productName.jsp">
                          <dsp:param name="product" value="${featuredProduct}" />
                          <dsp:param name="categoryNavIds" value="${categoryNavIds}" />
                          <dsp:param name="categoryId" value="${topLevelCategoryId}" />
                          <dsp:param name="categoryNav" value="true" />
                          <dsp:param name="navLinkAction" value="push" />
                        </dsp:include>
                      </li>
                    </preview:repositoryItem>
                  </c:forEach>
                </dsp:oparam>
              </dsp:droplet>
            </ul>
          </c:if>
        </div>
      </li>
    </c:if>
  </c:forEach>
  
  <%--
    WHATS NEW CATEGORY
    Display the New Items link which is not part of the catalog roots sub
    categories.
  --%>
  <li>
    <dsp:a page="/browse/newItems.jsp">
      <fmt:message key="navigation_category.newItems" />
    </dsp:a>
  </li>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/navigation/gadgets/catalog.jsp#3 $$Change: 788432 $ --%>

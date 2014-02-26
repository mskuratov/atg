<%-- 
  Generates breadcrumbs below the title on the category/subcategory/product
  pages. A user can navigate to previously visited categories by clicking on a
  breadcrumb. This JSP snippet is responsible for maintaining and displaying
  navigation history.
  
  Required Parameters:   
    element
      A top level category
      
  Optional Parameters:
    None   
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory" />
  <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistoryCollector" />
  <dsp:importbean bean="/atg/repository/seo/CatalogItemLink" />
  <dsp:importbean bean="/atg/commerce/endeca/cache/DimensionValueCacheDroplet"/>

  <%--
    CatalogNavHistoryCollector constructs the navigation history, here we pass
    in the current category so its added to the navigation stack. The jump parameter
    that is passed indicates the current navigation history will reset and a new
    navigation history will be generated in the hierarchical category tree from the
    root node to the "item" category. 
    
    Input Parameters:
      navAction - The action to take, either "pop", "push" or "jump". 
        jump indicates jumping to an unrelated area of the site, as if by a global navigation 
        or some other link
        pop pops the item off the top of the navigation stack.
        push pushes an item onto the top of the navigation stack.
      
      item - The item to add to the breadcrumb stack.
  --%>
  <dsp:droplet name="CatalogNavHistoryCollector">
    <dsp:param name="navAction" value="jump" />
    <dsp:param name="item" param="element" />
  </dsp:droplet>

  <%--
    Get the value of CatalogNavHistory.navHistory which contains our navigation history. 
    For each history item render the link to the category. The first item is the catalog root category.
  --%>
  <dsp:getvalueof var="navHistory" bean="CatalogNavHistory.navHistory" />

  <%--
    To obtain the correct URL for the Endeca driven category page we need to query the category id <-> dimension value cache 
    with the category id and the navigation category history. The ancestor categories is the navigation path from the current 
    category to the root category in the catalog but does not include the current category or the root category.    
  --%>
  <c:set var="parentCategoryId" value="" />
  <c:set var="ancestorCategoryIds" value="" />

  <c:forEach var="navItem" items="${navHistory}" varStatus="status">
    <dsp:param name="navItem" value="${navItem}" />

    <%-- Put a separator between breadcrumbs --%>
    <c:if test="${status.count > 1}">
      <fmt:message key="common.breadcrumbSeparator" />
    </c:if>

    <%-- 
      For the first item in the history we want to render a link to the home page. 
      Otherwise we will render a link to that category.
    --%>
    <c:choose>
      <c:when test="${status.first}">
        <fmt:message var="itemLabel" key="common.home" />
        <dsp:a page="/index.jsp" iclass="atg_store_navLogo">
          <c:out value="${itemLabel}"/>
        </dsp:a>
      </c:when>
      <c:otherwise>
        <%--
          Obtain the category id for the parent of the current category and add this to the ':' delimited list of ancestor categories.
        --%>
        <c:set var="parentCategoryId" value="${(status.index == 1) ? '' : navHistory[status.index - 1].id}"/>
        <c:choose>
          <c:when test="${empty ancestorCategoryIds}">
            <c:set var="ancestorCategoryIds" value="${parentCategoryId}"/>
          </c:when>
          <c:otherwise>
            <c:set var="ancestorCategoryIds" value="${ancestorCategoryIds}:${parentCategoryId}"/>
          </c:otherwise>
        </c:choose>

        <%--
          Look in a cache of ATG repository Id to Endeca navigationStates for the URL
          that will be used to navigate to when an Endeca driven category page is clicked.
            
          Input Parameters:
            repositoryId - The repository id of the value we want to retrieve from the cache.
            ancestors    - Colon ':' delimted string of ancestor category ids representing the 
                           catalog navigation path to this category.
        
          Open Parameters:
            output - Serviced when no errors occur.
       
          Output Parameters:
            dimensionValueCacheEntry - The entry in the cache for this particular
                                       repository id and ancestors. 
        --%>
        <dsp:droplet name="DimensionValueCacheDroplet">
          <dsp:param name="repositoryId" value="${navItem.id}"/>
          <dsp:param name="ancestors" value="${ancestorCategoryIds}"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="categoryCacheEntry" param="dimensionValueCacheEntry" />
          </dsp:oparam>
        </dsp:droplet>

        <c:if test="${not empty categoryCacheEntry.url}">
          <preview:repositoryItem item="${navItem}">
            <dsp:a page="${categoryCacheEntry.url}">
              <dsp:valueof param="navItem.displayName" />
            </dsp:a>
          </preview:repositoryItem>
        </c:if>        
      </c:otherwise>
    </c:choose>
  </c:forEach>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/navigation/gadgets/breadcrumbs.jsp#3 $$Change: 788278 $--%>

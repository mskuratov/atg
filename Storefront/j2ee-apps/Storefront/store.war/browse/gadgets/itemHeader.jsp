<%--
  itemHeader.jsp is responsible for rending the horizontal header bar which appears on 
  category/product pages, this includes the category/product display name, the hero image belonging
  to the top level category, the promotion belonging to the top level category and the bread crumbs. 
  
  Required Parameters:
    displayName 
      display name of category or product item
    
    category
      current category
    
  Optional Parameters:
    categoryNavIds
      string of category IDs separated with ':'
 --%>
<dsp:page>

  <%-- Import Required Beans --%>
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory" />

  <%-- Extract input parameters --%>
  <dsp:getvalueof var="categoryNavIds" param="categoryNavIds"/>
  <dsp:getvalueof var="displayName" param="displayName"/>
  
  <%-- Determine top-level category and background image --%>
  <c:choose>
    <%-- If we have categoryNavIds use top level category from there --%>
    <c:when test="${not empty categoryNavIds}">
      <dsp:getvalueof var="categoryIds" value="${fn:split(categoryNavIds, ':')}"/>
      
      <%--
        CategoryLookup droplet looks for a RepositoryItem by its id from within a
        Repository.  If the item is found, then it will check whether the item
        belongs to the user's catalog in his current Profile and if the item
        belongs to the current site. If it does output is rendered.
        
        Input Parameters:
          id - The id of the item to lookup 
          
        Open Parameters:
          output - Rendered if the item was found in the repository
        
        Output Parameters:
          element - The RepositoryItem corresponding to the id supplied
      --%>
      <dsp:droplet name="CategoryLookup">
        <dsp:param name="id" value="${categoryIds[0]}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="categoryHeroImageUrl" param="element.heroImage.url"/>
          <dsp:getvalueof var="topCategory" param="element"/>
        </dsp:oparam>
      </dsp:droplet>
    </c:when>
    <%-- Otherwise use CatalogNavHistory.navHistory --%>
    <c:otherwise>
      <dsp:getvalueof var="navHistory" vartype="java.util.Collection" bean="CatalogNavHistory.navHistory"/>
      <c:if test="${fn:length(navHistory) > 1}">
        <dsp:getvalueof var="topCategory" bean="CatalogNavHistory.navHistory[1]" />
        <dsp:getvalueof var="categoryHeroImageUrl" bean="CatalogNavHistory.navHistory[1].heroImage.url"/>
      </c:if>
    </c:otherwise>
  </c:choose>
  
  <%-- Set top level category to the CatalogNavigation bean for further user in targeters--%>
  <dsp:setvalue bean="/atg/store/catalog/CatalogNavigation.topLevelCategory" value="${topCategory.repositoryId}"/>
  
  <%-- 
    Render the hero image, we need to check if we have a url, if so set the
    style accordingly, otherwise just set it blank
  --%>
  <c:set var="style" value="background:url(${categoryHeroImageUrl}) bottom left no-repeat"/>
  <div id="atg_store_contentHeader" style="${not empty categoryHeroImageUrl ? style : ' '}">
    <%-- Render the Promotion Image that appears on the hero image --%>
    <div id="atg_store_contentHeadPromo"> 
      <dsp:include page="/browse/gadgets/categoryPromotions.jsp">
        <dsp:param name="category" value="${topCategory}" />
      </dsp:include>
    </div>    
    
    <%-- Render the category display name --%>
    <dsp:getvalueof var="displayName" param="displayName"/>
    <h2 class="title <crs:stringSizeClass string='${displayName}'/>">
      ${displayName}
    </h2>
  
    <%-- Display the breadcrumbs along the bottom of the top-level category image --%>
    <div id="atg_store_breadcrumbs">
      <dsp:include page="/navigation/gadgets/breadcrumbs.jsp">
        <%-- The parameter element represents a top level category --%>
        <dsp:param name="element" param="category" />
      </dsp:include>
    </div>
    
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/itemHeader.jsp#1 $$Change: 735822 $--%>

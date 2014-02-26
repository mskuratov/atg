<%-- 
  This page lays out the elements that make up the search results page.
    
  Required Parameters:
    contentItem
      The content item - results list type 
   
  Optional Parameters:

--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/SharingSitesDroplet"/>
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>

  <dsp:getvalueof var="shareableTypeId" param="shareableTypeId"/>
  <dsp:getvalueof var="filterByCatalog" param="filterByCatalog"/>

  <c:if test="${not empty shareableTypeId}">
  
    <%--
      Get a list of sites associated with the supplied shareable type.
      
      Input Parameters:
        shareableTypeId
          The shareable type that the list of sites returned will belong to.
  
      Open Parameters:
        output
          Serviced when no errors occur.
    
      Output Parameters:
        sites
          List of sites associated with the shareableTypeid.
    --%>
    <dsp:droplet name="SharingSitesDroplet">
      <dsp:param name="shareableTypeId" value="${shareableTypeId}"/>
      
      <dsp:oparam name="output">
        <dsp:getvalueof var="sites" param="sites"/>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>

  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/>
  <c:set var="recsPerPage" value="${contentItem.recsPerPage}"/>
  <c:set var="totalRecs" value="${contentItem.totalNumRecs}"/>
  <c:set var="initialPortionCount" value="${recsPerPage}"/>
  <c:if test="${recsPerPage > totalRecs}">
    <c:set var="initialPortionCount" value="${totalRecs}"/>
  </c:if> 
  
  <dsp:getvalueof var="p" param="p"/>
  <c:if test="${empty p}">
    <c:set var="p" value="1" />
  </c:if>
  
  <c:if test="${(not empty contentItem.categoryId)}">
  
    <div class="searchResults" data-results-list-count="${contentItem.totalNumRecs}">
      <div class="searchSectionHeader" style="display:none">
        <span class="searchSectionHeaderCaption">
          <span id="paginationInfo">
            <fmt:message key="mobile.search_searchResults.count">
              <fmt:param value="${initialPortionCount}"/>
              <fmt:param value="${totalRecs}"/>
            </fmt:message>
          </span>
        </span>
      </div>
      <ul class="searchResults">
        <%--
          Get the category repository object according to the id

          Input Parameters:
            id - The ID of the category we want to look up

          Open Parameters:
            output - Serviced when no errors occur
            error - Serviced when an error was encountered when looking up the category

          Output Parameters:
            element - The category whose ID matches the 'id' input parameter  
        --%>
        
        <dsp:droplet name="CategoryLookup">
          <dsp:param name="id" value="${contentItem.categoryId}"/>
          <dsp:param name="sites" value="${sites}"/>
          <dsp:param name="filterByCatalog" value="${not empty filterByCatalog ? filterByCatalog : 'true'}"/>
          <dsp:param name="filterBySite" value="true"/>

          <dsp:oparam name="output">
            <dsp:getvalueof var="productList" param="element.childProducts"/>
            <c:set var="productListSize" value="${fn:length(productList)}"/>

            <c:if test="${productListSize > 0}">
              <script>$('div.searchSectionHeader').show();</script>
            </c:if>

            <dsp:include page="${mobileStorePrefix}/browse/gadgets/categoryChildProductsRenderer.jsp">
              <dsp:param name="contentItem" value="${contentItem}" />
              <dsp:param name="productList" value="${productList}" />
              <dsp:param name="p" value="${p}" />
              <dsp:param name="sortSelection" value="${originatingRequest.sort}" />
            </dsp:include>

          </dsp:oparam>
        </dsp:droplet>
      </ul>
    </div>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cartridges/ProductList-ATGCategoryChildren/ProductList-ATGCategoryChildren.jsp#3 $$Change: 788278 $--%>

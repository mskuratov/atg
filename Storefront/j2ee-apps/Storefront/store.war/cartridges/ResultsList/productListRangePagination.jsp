<%--  
  Renders page numbers and view all link

  Required Parameters:
    arraySplitSize
      Number of items to be displayed per page.
    start
      Start index of the item to be rendered on this page.
    size
      Total number of items to be displayed
    p
      Current page number.

  Optional Parameters:
    top
      Where to render the pagination links on the page. Set to true for top set of links,
      false for the bottom set.
    viewAll
      Set to true if 'view all' has been requested.
    sort
      How the results should be sorted sorted.
    facetTrail  
      Records the currently supplied faceting on the search results.
    addFacet
      Facet to be added to the facet trail
    categoryId
      The currently browsed category
--%>
<dsp:page>

  <dsp:importbean bean="/atg/store/droplet/ArraySubsetHelper"/>
  
  <dsp:getvalueof var="viewAll" param="viewAll"/>
  <dsp:getvalueof var="top" param="top"/>
  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" param="contentItem"/>

  <c:set var="size" value="${contentItem.totalNumRecs}" />
  <c:set var="arraySplitSize" value="${contentItem.recsPerPage}"/>
  <c:set var="start" value="${contentItem.firstRecNum}"/>
  
  <c:set var="viewAllLinkClass" value="atg_store_actionViewAll"/>
  
  <c:if test="${size > arraySplitSize}">

    <crs:pagination size="${size}" arraySplitSize="${arraySplitSize}" start="${start}" viewAll="${viewAll}" top="${top}">
  
      <jsp:attribute name="pageLinkRenderer"> 
        <%-- RENDER PAGE LINKS --%>
        <c:set var="pageTemplate" value="${contentItem.pagingActionTemplate.navigationState}"/>       
        <c:set var="linkAction" value="${fn:replace(fn:replace(pageTemplate, '%7Boffset%7D', startValue - 1), '%7BrecordsPerPage%7D',  arraySplitSize)}" />
        <c:url var="url" value="${linkAction}"></c:url>
        <c:choose>
          <c:when test="${selected && !viewAll}">
            <a title="${linkTitle}" class="${linkClass}">${linkText}</a>
          </c:when>
          <c:otherwise>
            <a href="${url}" title="${linkTitle}" class="${linkClass}">${linkText}</a>
          </c:otherwise>
        </c:choose>
      </jsp:attribute>
      
 
      
    </crs:pagination>
  </c:if>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cartridges/ResultsList/productListRangePagination.jsp#1 $$Change: 742374 $ --%>
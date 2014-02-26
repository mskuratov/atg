<%-- 
  This gadget displays the list of category products. Displayed products are sorted according to the sort options passed
  to the gadget.

  Required parameters:
    contentItem
      The category child products content item
    productList
      The list of child products to display
    sortSelection
      The property name to sort products by
    p
      The page number
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/sort/RangeSortDroplet"/>
  <dsp:importbean bean="atg/projects/store/mobile/droplet/URLProcessor"/>

  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="contentItem" param="contentItem"/>
  <dsp:getvalueof var="productList" param="productList"/>
  <dsp:getvalueof var="sortSelection" param="sortSelection"/>
  <dsp:getvalueof var="p" param="p"/>

  <c:set var="recsPerPage" value="${contentItem.recsPerPage}"/>

  <dsp:droplet name="RangeSortDroplet">
    <dsp:param name="array" value="${productList}"/>
    <dsp:param name="sortSelection" param="sort"/>
    <dsp:param name="howMany" value="${recsPerPage}"/>
    <dsp:param name="start" value="${(p - 1) * recsPerPage + 1}"/>

    <%-- Rendered once before the main "output" --%>
    <dsp:oparam name="outputStart">
      <c:set var="productListSize" value="${fn:length(productList)}"/>
      <c:if test="${productListSize > 1}">
        <%-- Determine the right labels based on the sort status and determine the selected button --%>
        <fmt:message var="topPicksLabel" key="mobile.sort.topPicks" />
        <fmt:message var="nameSortLabel" key="mobile.sort.name" />
        <fmt:message var="priceSortLabel" key="mobile.sort.price" />
        
        <c:set var="topPicksSelected" value="" />
        <c:set var="nameSelected" value="" />
        <c:set var="priceSelected" value="" />
        
        <c:choose>
          <c:when test="${sortSelection == 'displayName:ascending'}">
            <fmt:message var="nameSortLabel" key="mobile.sort.name.asc" />
            <c:set var="nameSelected" value="selected" />
          </c:when>
          <c:when test="${sortSelection == 'displayName:descending'}">
            <fmt:message var="nameSortLabel" key="mobile.sort.name.desc" />
            <c:set var="nameSelected" value="selected" />
          </c:when>
          <c:when test="${sortSelection == 'price:ascending'}">
            <fmt:message var="priceSortLabel" key="mobile.sort.price.asc" />
            <c:set var="priceSelected" value="selected" />
          </c:when>
          <c:when test="${sortSelection == 'price:descending'}">
            <fmt:message var="priceSortLabel" key="mobile.sort.price.desc" />
            <c:set var="priceSelected" value="selected" />
          </c:when>
          <c:otherwise>
            <c:set var="topPicksSelected" value="selected" />
          </c:otherwise>
        </c:choose>

        <%-- Draw the sorting "buttons" --%>
        <table class="sortToolbar">
          <tr>
            <td id="topPicksSort" class="${topPicksSelected}">${topPicksLabel}</td>
            <td class="divider"><img src="/crsdocroot/content/mobile/images/menu_divider.png" /></td>
            <td id="nameSort" class="${nameSelected}">${nameSortLabel}</td>
            <td class="divider"><img src="/crsdocroot/content/mobile/images/menu_divider.png" /></td>
            <td id="priceSort" class="${priceSelected}">${priceSortLabel}</td>
          </tr>
        </table>
        
        <%--The sorting forms --%>
        <dsp:form id="sortByForm" action="${siteContextPath}${navigationActionPath}/${contentItem.categoryAction}">
          <input type="hidden" id="sort" name="sort" value="${sortSelection}" />
          <input type="hidden" name="N" value="${contentItem.categoryDimensionId}" />
          <input type="hidden" name="p" value="1" />
        </dsp:form>
      </c:if>
    </dsp:oparam>

    <%-- Product repository item is output --%>
    <dsp:oparam name="output">
      <dsp:setvalue param="product" paramvalue="element"/>
      <dsp:getvalueof var="item" param="product"/>
      <preview:repositoryItem item="${item}">
        <dsp:include page="${mobileStorePrefix}/browse/gadgets/productListRow.jsp">
          <dsp:param name="product" param="element"/>
        </dsp:include>
      </preview:repositoryItem>
    </dsp:oparam>

    <%-- Increase p-parameter value in request URL --%>
    <dsp:droplet name="URLProcessor">
      <dsp:param name="url" value="${pageContext.request.queryString}"/>
      <dsp:param name="parameter" value="p"/>
      <dsp:param name="parameterValue" value="${p + 1}"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="element" param="element"/>
        <c:set var="queryString" value="${element}"/>
      </dsp:oparam>
    </dsp:droplet>

    <%-- Delete "nav=true" if it exists --%>
    <c:set var="queryString" value="${fn:replace(queryString, '&nav=true', '')}"/>

    <%-- Construct the URL --%>
    <c:set var="url" value="${siteContextPath}${navigationActionPath}?${queryString}"/>
    
    <c:set var="lastRecordIndex" value="${recsPerPage * p}" />
    <c:if test="${lastRecordIndex > productListSize}">
      <c:set var="lastRecordIndex" value="${productListSize}" />
    </c:if>

    <%-- Rendered once after all products have been output --%>
    <dsp:oparam name="outputEnd">
      <%-- Show pagination links --%>
      <dsp:include page="${mobileStorePrefix}/browse/gadgets/productListRangePagination.jsp">
        <dsp:param name="lastRecordIndex" value="${lastRecordIndex}"/>
        <dsp:param name="totalNumRecords" value="${contentItem.totalNumRecs}"/>
        <dsp:param name="recordsPerPage" value="${recsPerPage}"/>
        <dsp:param name="url" value="${url}"/>
      </dsp:include>
    </dsp:oparam>
  </dsp:droplet>

  <script>
    $(document).ready(function() {
      CRSMA.categorysort.initSortingActions();
    });
  </script>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/gadgets/categoryChildProductsRenderer.jsp#2 $$Change: 791340 $--%>

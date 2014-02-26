<%--
  "ResultsList" cartridge renderer.
  Mobile version.

  Includes:
    /mobile/browse/gadgets/productListRow.jsp - Renderer of product row
    /mobile/browse/gadgets/sortToolbar.jsp - Renders the sort toolbar

  Required parameters:
    contentItem
      The "ResultsList" content item to render.
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="atg/projects/store/mobile/droplet/URLProcessor"/>
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>

  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/>
  <dsp:getvalueof var="totalNumRecords" value="${contentItem.totalNumRecs}"/>
  <%-- Store "recordsPerPage" in a session-scoped variable "recordsPerPage"
       because "contentItem.recsPerPage" is changed after each ResultsList fetch --%>
  <c:if test="${empty recordsPerPage}">
    <dsp:getvalueof var="recordsPerPage" value="${contentItem.recsPerPage}" scope="session"/>
  </c:if>
  <dsp:getvalueof var="lastRecordIndex" value="${contentItem.lastRecNum}"/>

  <div class="searchResults" data-results-list-count="${totalNumRecords}">
    <ul class="searchResults">
      <c:choose>
        <c:when test="${empty totalNumRecords || totalNumRecords == 0}">
          <%-- No search results --%>
          <li></li>
          <li><h2><fmt:message key="mobile.search.noResults"/></h2></li>
          <li></li>
        </c:when>
        <c:otherwise>
          <div class="searchSectionHeader">
            <span class="searchSectionHeaderCaption">
              <span id="paginationInfo">
                <fmt:message key="mobile.search_searchResults.count">
                  <fmt:param value="${lastRecordIndex}"/>
                  <fmt:param value="${totalNumRecords}"/>
                </fmt:message>
              </span>
            </span>
          </div>

          <%-- Sort Toolbar --%>
          <dsp:include page="${mobileStorePrefix}/browse/gadgets/sortToolbar.jsp">
            <dsp:param name="contentItem" value="${contentItem}"/>
          </dsp:include>

          <%-- Search results page --%>
          <c:forEach var="record" items="${contentItem.records}">
            <dsp:getvalueof var="productId" value="${record.attributes['product.repositoryId']}"/>
            <dsp:droplet name="ProductLookup">
              <dsp:param name="id" value="${productId}"/>
              <dsp:param name="filterByCatalog" value="false"/>
              <dsp:param name="filterBySite" value="false"/>
              <dsp:oparam name="output">
                <dsp:include page="${mobileStorePrefix}/browse/gadgets/productListRow.jsp">
                  <dsp:param name="product" param="element"/>
                </dsp:include>
              </dsp:oparam>
            </dsp:droplet>
          </c:forEach>

          <%-- Show pagination links --%>
          <c:set var="pagingAction" value="${fn:replace(contentItem.pagingActionTemplate.navigationState, '%7Boffset%7D', 0)}"/>
          <dsp:droplet name="URLProcessor">
            <dsp:param name="url" value="${pagingAction}"/>
            <dsp:param name="parameter" value="No"/>
            <dsp:param name="parameterValue" value="${lastRecordIndex}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="element" param="element"/>
              <c:set var="pagingAction" value="${element}"/>
            </dsp:oparam>
          </dsp:droplet>

          <%-- Switch to "Results List" mode (actual in case if we were in "Refinement" mode) --%>
          <c:set var="pagingAction" value="${fn:replace(pagingAction, '&nav=true', '')}"/>
          <c:set var="url" value="${siteBaseURL}${contentItem.pagingActionTemplate.contentPath}${pagingAction}"/>

          <dsp:include page="${mobileStorePrefix}/browse/gadgets/productListRangePagination.jsp">
            <dsp:param name="lastRecordIndex" value="${lastRecordIndex}"/>
            <dsp:param name="totalNumRecords" value="${totalNumRecords}"/>
            <dsp:param name="recordsPerPage" value="${recordsPerPage}"/>
            <dsp:param name="url" value="${url}"/>
          </dsp:include>
        </c:otherwise>
      </c:choose>
    </ul>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cartridges/ResultsList/ResultsList.jsp#5 $$Change: 790561 $--%>

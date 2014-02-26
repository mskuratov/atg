<%--
  Draws "Show More" link for retrieving the following items.

  Required parameters:
    recordsPerPage
      Max count of items that is retrieved at once.
    lastRecordIndex
      Index of the last retrieved item (1..N).
    totalNumRecords
      Total number of items according to selected refinements.
    url
      URL for "Show more" link.
--%>
<dsp:page>
  <dsp:getvalueof var="recordsPerPage" idtype="java.lang.Integer" param="recordsPerPage"/>
  <dsp:getvalueof var="lastRecordIndex" param="lastRecordIndex"/>
  <dsp:getvalueof var="totalNumRecords" param="totalNumRecords"/>
  <dsp:getvalueof var="url" param="url"/>

  <c:if test="${lastRecordIndex < totalNumRecords}">
    <c:set var="remainingResults" value="${recordsPerPage > (totalNumRecords - lastRecordIndex) ? (totalNumRecords - lastRecordIndex) : recordsPerPage}"/>
    <li class="moreResults" onclick="CRSMA.search.getItems('${url}');">
      <span role="link">
        <fmt:message key="mobile.search.showMore"><fmt:param value="${remainingResults}"/></fmt:message>
      </span>
    </li>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/gadgets/productListRangePagination.jsp#3 $$Change: 788278 $--%>

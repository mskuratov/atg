<%--
  "HorizontalResultsList" cartridge renderer.
  Mobile version.
  
  Includes:
    /mobile/global/gadgets/productsHorizontalList.jsp - Renders the slider

  Required parameters:
    contentItem
      The "ResultsList" content item to render.
--%>
<dsp:page>
  <dsp:importbean bean="atg/projects/store/mobile/droplet/URLProcessor" />
  <dsp:importbean bean="/atg/multisite/Site" />
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest" />

  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}" />
  <dsp:getvalueof var="totalNumRecords" value="${contentItem.totalNumRecs}" />
  <dsp:getvalueof var="recordsPerPage" value="${contentItem.recsPerPage}"  />
  <dsp:getvalueof var="firstRecordNum" value="${contentItem.firstRecNum}" />
  <dsp:getvalueof var="lastRecordNum" value="${contentItem.lastRecNum}" />

  <table class="horizontalResultsList">
    <tr>
      <%-- Display the left arrow to go to the previous page, if the user is not on the first page --%>
      <c:if test="${not empty firstRecordNum && firstRecordNum != 1}">
        <td class="navigation">
          <%-- Determine the pagination link --%> 
          <c:set var="pagingAction" value="${contentItem.pagingActionTemplate.navigationState}" />
          <dsp:droplet name="URLProcessor">
            <dsp:param name="url" value="${pagingAction}" />
            <dsp:param name="parameter" value="No" />
            <dsp:param name="parameterValue" value="${firstRecordNum - recordsPerPage - 1}" />
            <dsp:oparam name="output">
              <dsp:getvalueof var="element" param="element" />
              <c:set var="pagingAction" value="${element}" />
            </dsp:oparam>
          </dsp:droplet> 
          <c:set var="url" value="${siteBaseURL}${contentItem.pagingActionTemplate.contentPath}${pagingAction}" />

          <a href="${url}" role="button" title="<fmt:message key='mobile.horizontalResultList.prev'/>"><img src="/crsdocroot/content/mobile/images/icon-blp-resultsList-previousArrow.png"/></a>
        </td>
      </c:if>
      
      <%-- Display the slider--%>
      <td class="slider">
        <dsp:include page="${mobileStorePrefix}/global/gadgets/productsHorizontalList.jsp">
          <dsp:param name="products" value="${contentItem.records}" />
        </dsp:include>
      </td>
      
      <%-- Display the right arrow to go to the next page, if the user is not on the last page --%>
      <c:if test="${lastRecordNum < totalNumRecords}">
        <td class="navigation">
          <%-- Determine the pagination link --%> 
          <c:set var="pagingAction" value="${contentItem.pagingActionTemplate.navigationState}" />
          <dsp:droplet name="URLProcessor">
            <dsp:param name="url" value="${pagingAction}" />
            <dsp:param name="parameter" value="No" />
            <dsp:param name="parameterValue" value="${lastRecordNum}" />
            <dsp:oparam name="output">
              <dsp:getvalueof var="element" param="element" />
              <c:set var="pagingAction" value="${element}" />
            </dsp:oparam>
          </dsp:droplet> 
          <%-- Switch to "Results List" mode as opposed to showing the "Refinement" mode --%>
          <c:set var="pagingAction" value="${fn:replace(pagingAction, '&nav=true', '')}"/>
          <c:set var="url" value="${siteBaseURL}${contentItem.pagingActionTemplate.contentPath}${pagingAction}" />

          <a href="${url}" role="button" title="<fmt:message key='mobile.horizontalResultList.next'/>"><img src="/crsdocroot/content/mobile/images/icon-blp-resultsList-nextArrow.png"/></a>
        </td>
      </c:if>
    </tr>
  </table>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cartridges/HorizontalResultsList/HorizontalResultsList.jsp#6 $$Change: 795154 $--%>

<%--
  "Search Adjustments" cartridge renderer.
  Mobile version.

  Required Parameters:
    contentItem
      The "SearchAdjustments" content item to render.
--%>
<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/>

  <c:if test="${not empty contentItem.adjustedSearches || not empty contentItem.suggestedSearches}">
    <div class="SearchAdjustments">
      <%-- Search adjustments --%>
      <c:forEach var="originalTerm" items="${contentItem.originalTerms}" varStatus="status">
        <c:if test="${not empty contentItem.adjustedSearches[originalTerm]}">
          <fmt:message key="mobile.search.adjust.description"><fmt:param><span>${originalTerm}</span></fmt:param></fmt:message>
          <c:forEach var="adjustment" items="${contentItem.adjustedSearches[originalTerm]}" varStatus="status">
            <span class="autoCorrect">${adjustment.adjustedTerms}</span>
            <c:if test="${!status.last}">, </c:if>
          </c:forEach>
        </c:if>

        <%-- "Did You Mean?" --%>
        <c:if test="${not empty contentItem.suggestedSearches[originalTerm]}">
          <div class="DYM">
            <fmt:message key="mobile.search.adjust.didYouMean">
              <fmt:param>
                <c:forEach var="suggestion" items="${contentItem.suggestedSearches[originalTerm]}" varStatus="status">
                  <a href="${siteBaseURL}${suggestion.contentPath}${suggestion.navigationState}">${suggestion.label}</a>
                  <c:if test="${!status.last}">, </c:if>
                </c:forEach>
              </fmt:param>
            </fmt:message>
          </div>
        </c:if>
      </c:forEach>
    </div>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cartridges/SearchAdjustments/SearchAdjustments.jsp#3 $$Change: 791340 $--%>

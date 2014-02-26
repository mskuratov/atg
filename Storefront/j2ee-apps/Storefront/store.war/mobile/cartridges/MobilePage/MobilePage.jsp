<%--
  "MobilePage" cartridge renderer.

  Includes:
    /mobile/global/gadgets/noSearchResults.jsp - "No Search Results" pseudo-modal popup renderer

  Required Parameters:
    contentItem
      The "MobilePage" content item to render.

  Optional parameters:
    nav
      nav=true - The page and all the subpages are in "Refinement" mode.
                 Otherwise, the mode is considered "Results list".

  NOTE:
    The "endecaUserAgent" request-scoped variable (request attribute), which is used here,
    is set by the "MobileDetectionInterceptor": it's set, when the "Endeca Preview" is enabled
    (AssemblerSettings.previewEnabled = true) and the "Endeca Preview User-Agent" request parameter
    is present in the request (even if it's an empty string).
--%>
<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/>

  <fmt:message var="pageTitle" key="mobile.search_searchResults.title"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <jsp:attribute name="modalContent">
      <%-- "No Search Results" pseudo-modal popup --%>
      <dsp:include page="${mobileStorePrefix}/global/gadgets/noSearchResults.jsp"/>
    </jsp:attribute>

    <jsp:body>
      <div id="switchBar"></div>

      <div id="main" style="display:none">
        <c:choose>
          <c:when test="${endecaUserAgent != null}">
            <endeca:includeSlot contentItem="${contentItem}">
              <c:forEach var="element" items="${contentItem.MainContent}">
                <dsp:renderContentItem contentItem="${element}"/>
              </c:forEach>
            </endeca:includeSlot>
          </c:when>
          <c:otherwise>
            <c:forEach var="element" items="${contentItem.MainContent}">
              <dsp:renderContentItem contentItem="${element}"/>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </div>

      <div id="secondary" style="display:none">
        <c:choose>
          <c:when test="${endecaUserAgent != null}">
            <endeca:includeSlot contentItem="${contentItem}">
              <c:forEach var="element" items="${contentItem.SecondaryContent}">
                <dsp:renderContentItem contentItem="${element}"/>
              </c:forEach>
            </endeca:includeSlot>
          </c:when>
          <c:otherwise>
            <c:forEach var="element" items="${contentItem.SecondaryContent}">
              <dsp:renderContentItem contentItem="${element}"/>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </div>

      <script>
        $(document).ready(function() {
          CRSMA.global.initMobilePage();
        });
      </script>
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cartridges/MobilePage/MobilePage.jsp#4 $$Change: 792662 $--%>

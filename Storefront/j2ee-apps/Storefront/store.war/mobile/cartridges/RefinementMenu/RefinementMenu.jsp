<%--
  "RefinementMenu" cartridge renderer.
  Mobile version.

  Includes:
    /mobile/global/util/getNavLink.jsp - Endeca-specific navigation link generator

  Required Parameters:
    contentItem
      The "RefinementMenu" content item to render.
--%>
<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/>

  <c:if test="${not empty contentItem.refinements}">
    <div class="refinementFacetGroupContainer" data-is-category="${contentItem.dimensionName == 'product.category'}">
      <%-- Facet group name --%>
      <span class="refinementFacetGroupName">
        <fmt:message>mobile.${not empty contentItem.dimensionName ? contentItem.dimensionName : contentItem.name}</fmt:message>
      </span>

      <ul class="refinementDataList" id="ul_${fn:replace(contentItem.name, '.', '_')}">
        <%-- Facet rows --%>
        <c:forEach var="refinement" items="${contentItem.refinements}">
          <dsp:include page="${mobileStorePrefix}/global/util/getNavLink.jsp">
            <dsp:param name="navAction" value="${refinement}"/>
          </dsp:include>
          <li onclick="CRSMA.search.applyFacet(this, '${navLink}')" role="link" aria-describedby="addId">
            <div class="refinementContent">
              ${refinement.label} <span class="refinementCount">${refinement.count}</span>
            </div>
          </li>
        </c:forEach>
      </ul>

      <%-- "Show More" item --%>
      <c:set var="hasMoreLink" value="${not empty contentItem.moreLink.navigationState}"/>
      <c:if test="${hasMoreLink}">
        <dsp:include page="${mobileStorePrefix}/global/util/getNavLink.jsp">
          <dsp:param name="navAction" value="${contentItem.moreLink}"/>
        </dsp:include>
        <span class="refinementShowMoreLess">
          <a href="javascript:void(0);" onclick="document.location = CRSMA.search.setNavState('${navLink}');">
            <fmt:message key="mobile.search.refine.showMore"/>
          </a>
        </span>
      </c:if>

      <%-- "Show Less" item --%>
      <c:set var="hasLessLink" value="${not empty contentItem.lessLink.navigationState}"/>
      <c:if test="${hasLessLink}">
        <dsp:include page="${mobileStorePrefix}/global/util/getNavLink.jsp">
          <dsp:param name="navAction" value="${contentItem.lessLink}"/>
        </dsp:include>
        <span class="refinementShowMoreLess">
          <a href="javascript:void(0);" onclick="document.location = CRSMA.search.setNavState('${navLink}');">
            <fmt:message key="mobile.search.refine.showLess"/>
          </a>
        </span>
      </c:if>
    </div>

    <div id="addId" style="display:none">
      <fmt:message>mobile.a11y.addMessage</fmt:message>
    </div>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cartridges/RefinementMenu/RefinementMenu.jsp#4 $$Change: 793980 $--%>

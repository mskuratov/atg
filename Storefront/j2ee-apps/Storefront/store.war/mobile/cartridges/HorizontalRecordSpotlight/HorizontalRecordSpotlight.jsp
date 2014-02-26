<%-- 
  This page lays out the elements that make up the Endeca driven horizontal spotlight.
  
  Includes:
    /mobile/global/gadgets/productsHorizontalList.jsp - Renders the slider

  Required Parameters:
    None

  Optional Parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>

  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/>

  <c:if test="${not empty contentItem.records}">
    <div class="searchSectionHeader">
      <span class="searchSectionHeaderCaption">
        <crs:outMessage key="horizontal_record_spotlightTitle"/>
      </span>
    </div>
    <dsp:include page="${mobileStorePrefix}/global/gadgets/productsHorizontalList.jsp">
      <dsp:param name="products" value="${contentItem.records}" />
    </dsp:include>
  </c:if>

  <c:if test="${not empty contentItem.seeAllLink}">
    <div id="mobileHorizontalRecordSpotlightSeeAllLink">
      <c:url var="seeAllAction" value="${contentItem.seeAllLink.navigationState}"/>
      <%--
        Modify the URL to remove the nav=true part. Removing this parameter will take the user
        directly to the list view as opposed to landing them on the filter view
      --%>
      <c:set var="seeAllAction" value="${fn:replace(seeAllAction, '&nav=true', '')}"/>
      <a href="${seeAllAction}"><crs:outMessage key="horizontal_record_spotlightSeeAllText"/></a>
    </div>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cartridges/HorizontalRecordSpotlight/HorizontalRecordSpotlight.jsp#3 $$Change: 790558 $ --%>

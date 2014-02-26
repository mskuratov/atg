<%--
  "GuidedNavigation" cartridge renderer.
  Mobile version.

  Required Parameters:
    contentItem
      The "GuidedNavigation" content item to render.
--%>
<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/>

  <%-- Calculate total count of enabled children components --%>
  <%-- Note, that "enabled" property doesn't belongs to the BasicContentItem --%>
  <%-- So we check this property only for those, who has it --%>
  <c:set var="childrenCount" value="0"/>
  <c:forEach var="child" items="${contentItem.navigation}">
    <c:if test="${empty child.enabled || child.enabled}">
      <c:set var="childrenCount" value="${childrenCount+1}"/>
    </c:if>
  </c:forEach>
  
  <div class="dataContainer guidedNavigation" data-navigation-groups-count="${childrenCount}">
    <c:forEach var="element" items="${contentItem.navigation}">
      <dsp:renderContentItem contentItem="${element}"/>
    </c:forEach>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cartridges/GuidedNavigation/GuidedNavigation.jsp#2 $$Change: 768606 $--%>

<%--
  ~ Copyright 2001, 2012, Oracle and/or its affiliates. All rights reserved.
  ~ Oracle and Java are registered trademarks of Oracle and/or its
  ~ affiliates. Other names may be trademarks of their respective owners.
  ~ UNIX is a registered trademark of The Open Group.

  This renderer calls the renderContentItem for it's contents.

  Required Parameters:
    contentItem
      The page slot content item to render.

  Optional Parameters:
    None

  NOTE:
    The "endecaUserAgent" request-scoped variable (request attribute), which is used here,
    is set by the "MobileDetectionInterceptor": it's set, when the "Endeca Preview" is enabled
    (AssemblerSettings.previewEnabled = true) and the "Endeca Preview User-Agent" request parameter
    is present in the request (even if it's an empty string).
--%>
<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/>

  <c:choose>
    <c:when test="${endecaUserAgent != null}">
      <endeca:includeSlot contentItem="${contentItem}">
        <c:forEach var="element" items="${contentItem.contents}">
          <dsp:renderContentItem contentItem="${element}"/>
        </c:forEach>
      </endeca:includeSlot>
    </c:when>
    <c:otherwise>
      <c:forEach var="element" items="${contentItem.contents}">
        <dsp:renderContentItem contentItem="${element}"/>
      </c:forEach>
    </c:otherwise>
  </c:choose>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cartridges/ContentSlot-Secondary/ContentSlot-Secondary.jsp#5 $$Change: 792662 $--%>

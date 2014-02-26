<%--
  Draws MediaBanner on page.
  "Media image" and "Click Action URL" are taken from values defined in XMgr for MediaBanner cartridge.

  Required parameters:
    none
--%>
<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/>

  <c:if test="${not empty contentItem.link}">
    <dsp:include page="${mobileStorePrefix}/global/util/getNavLink.jsp">
      <dsp:param name="navAction" value="${contentItem.link}"/>
    </dsp:include>
  </c:if>

  <div class="mediaBannerContainer">
    <c:if test="${not empty navLink}"><a href="${navLink}"></c:if>
      <img src="${contentItem.imageURL}" alt="${contentItem.name}"/>
    <c:if test="${not empty navLink}"></a></c:if>
  </div>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cartridges/MediaBanner/MediaBanner.jsp#4 $$Change: 794212 $--%>

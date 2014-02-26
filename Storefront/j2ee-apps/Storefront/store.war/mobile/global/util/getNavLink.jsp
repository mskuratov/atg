<%--
  Returns Endeca-specific navigation links based on given NavigationAction (navAction).
  The result is returned in the "navLink" request-scoped variable.

  Required parameters:
    navAction
      Endeca "NavigationAction" or "UrlAction" or "LinkBuilder" instance.
--%>
<dsp:page>
  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="navActionObject" param="navAction"/>

  <c:choose>
    <c:when test="${navActionObject.class.name == 'com.endeca.infront.cartridge.model.UrlAction'}">
      <c:set var="navLink" value="${navActionObject.url}" scope="request"/>
    </c:when>
    <c:when test="${navActionObject.class.name == 'com.endeca.infront.cartridge.model.LinkBuilder'}">
      <c:choose>
        <c:when test="${not empty navActionObject.queryString}">
          <c:set var="navLink" value="${navActionObject.path}?${navActionObject.queryString}" scope="request"/>
        </c:when>
        <c:otherwise>
          <c:set var="navLink" value="${navActionObject.path}" scope="request"/>
        </c:otherwise>
      </c:choose>
    </c:when>
    <c:otherwise>
      <c:set var="navLink" scope="request">
        <c:choose>
          <c:when test="${not empty navActionObject.contentPath}">${siteBaseURL}${navActionObject.contentPath}${navActionObject.navigationState}</c:when>
          <c:otherwise>${siteBaseURL}${navActionObject.navigationState}</c:otherwise>
        </c:choose>
      </c:set>
    </c:otherwise>
  </c:choose>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/global/util/getNavLink.jsp#3 $$Change: 788278 $--%>

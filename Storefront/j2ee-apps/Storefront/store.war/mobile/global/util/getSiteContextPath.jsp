<%--
  This gadget returns mobile site-specific context path, which also includes "/mobile" suffix at the end.
  The results are returned in the following request-scoped variables:
    siteContextPath
      Mobile site-specific context path. Has "/mobile" suffix at the end.
    siteBaseURL
      The same as "siteContextPath", but WITHOUT the "/mobile" suffix.
--%>
<dsp:page>
  <dsp:importbean bean="/atg/multisite/Site" var="currentSite"/>

  <dsp:getvalueof var="mobileStorePrefix" bean="/atg/store/StoreConfiguration.mobileStorePrefix"/>

  <c:set var="siteBaseURL" value="${currentSite.productionURL}" scope="request"/>
  <c:if test="${empty siteBaseURL}">
    <c:set var="siteBaseURL" value="${pageContext.request.contextPath}" scope="request"/>
  </c:if>
  <c:set var="siteBaseURLLength" value="${fn:length(siteBaseURL)}"/>
  <c:set var="siteBaseURLLastChar" value="${fn:substring(siteBaseURL, siteBaseURLLength-1, siteBaseURLLength)}"/>
  <c:if test="${siteBaseURLLastChar == '/'}">
    <c:set var="siteBaseURL" value="${fn:substring(siteBaseURL, 0, siteBaseURLLength - 1)}" scope="request"/>
  </c:if>

  <%-- Return the result --%>
  <dsp:getvalueof var="siteContextPath" value="${siteBaseURL}${mobileStorePrefix}" scope="request"/>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/global/util/getSiteContextPath.jsp#2 $$Change: 768606 $--%>

<%--
  This page displays a list of available sites for switching, current one is ticked.

  Page includes:
    /global/gadgets/crossSiteLinkGenerator.jsp - Generator of cross-site links

  Required Parameters:
    None

  Optional Parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/CartSharingSitesDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/GetSiteDroplet"/>

  <%-- Get the site ID for the current site. The current site should not be rendered as a link in the Store site picker --%> 
  <dsp:getvalueof var="currentSiteId" bean="Site.id"/>

  <fmt:message var="pageTitle" key="mobile.company_sites.pageTitle"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <jsp:body>
      <dsp:droplet name="CartSharingSitesDroplet">
        <dsp:param name="siteId" value="${currentSiteId}"/>
        <dsp:param name="shareableTypeId" value="crs.MobileSite"/>
        <dsp:getvalueof var="sites" param="sites"/>
        <dsp:oparam name="output">
          <%-- Ensure we have more than 1 site --%>
          <c:if test="${fn:length(sites) > 0}">
            <div class="dataContainer">
              <div class="basicText">
                <fmt:message key="mobile.company_sites.pageHeader"/>
              </div>
              <ul id="sites" class="dataList">
                <%-- Iterate through the sites in the Shopping Cart sharing group --%> 
                <dsp:droplet name="ForEach">
                  <dsp:param name="array" param="sites"/>
                  <dsp:param name="sortProperties" value="-name"/>
                  <dsp:oparam name="output">
                    <dsp:setvalue param="site" paramvalue="element"/>
                    <dsp:getvalueof var="siteIdx" param="index"/>
                    <dsp:getvalueof var="siteName" param="site.name"/>
                    <dsp:getvalueof var="siteId" param="site.id"/>
                    <li>
                      <c:set var="isChecked" value=""/>
                      <c:if test="${siteId == currentSiteId}">
                        <c:set var="isChecked" value="checked"/>
                      </c:if>
                      <c:set var="prefixForMobileSite" value="/"/>
                      <dsp:droplet name="GetSiteDroplet">
                        <dsp:param name="siteId" value="${siteId}"/>
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="siteChannel" param="site.channel"/>
                          <c:if test="${siteChannel == 'mobile'}">
                            <c:set var="prefixForMobileSite" value="${mobileStorePrefix}"/>
                          </c:if>
                        </dsp:oparam>
                      </dsp:droplet>
                      <dsp:include page="/global/gadgets/crossSiteLinkGenerator.jsp">
                        <dsp:param name="siteId" value="${siteId}"/>
                        <dsp:param name="customUrl" value="${prefixForMobileSite}"/>
                      </dsp:include>

                      <div class="content">
                        <input type="radio" id="curSite${siteIdx}" name="site" ${isChecked}
                               onclick="setTimeout(function() {CRSMA.global.gotoURL('${siteLinkUrl}')}, 300);"/>
                        <label for="curSite${siteIdx}" onclick="">${siteName}</label>
                      </div>
                    </li>
                  </dsp:oparam>
                </dsp:droplet>
              </ul>
            </div>
          </c:if> 
        </dsp:oparam> 
      </dsp:droplet> 
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/company/sites.jsp#2 $$Change: 768606 $--%>

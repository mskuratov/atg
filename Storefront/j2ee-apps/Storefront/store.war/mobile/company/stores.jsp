<%--
  This page is the company "Store Locator" page.
  Displays store list for current site.

  Page includes:
    /mobile/company/gadgets/storeMapLink.jsp - Link generator for device-native Google Maps application

  Required Parameters:
    None

  Optional Parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/store/droplet/StoreLookupDroplet"/>
  <dsp:importbean bean="/atg/store/droplet/StoreSiteFilterDroplet"/>

  <fmt:message var="pageTitle" key="mobile.company_storeLocations.pageTitle"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <%-- Lookup stores from repository --%>
    <dsp:droplet name="StoreLookupDroplet">
      <dsp:oparam name="output">
        <%-- Filter stores for current site --%>
        <dsp:droplet name="StoreSiteFilterDroplet">
          <dsp:param name="collection" param="items"/>
          <dsp:oparam name="output">
            <%-- Iterate collection of stores --%>
            <dsp:droplet name="ForEach">
              <dsp:param name="array" param="filteredCollection"/>
              <dsp:oparam name="outputStart">
                <div class="dataContainer">
                  <ul id="store_locations" class="dataList">
              </dsp:oparam>
              <dsp:oparam name="outputEnd">
                  </ul>
                </div>
              </dsp:oparam>
              <dsp:oparam name="output">
                <dsp:setvalue param="storeItem" paramvalue="element"/>
                <li class="icon-ArrowLeft" onclick="CRSMA.global.storeLocationClick(this);">
                  <p class="storeName"><dsp:valueof param="storeItem.name"/></p>
                  <p>
                    <dsp:valueof param="storeItem.address1"/>
                    <dsp:getvalueof var="storeAddress2" param="storeItem.address2"/>
                    <c:if test="${not empty storeAddress2}">
                      ${storeAddress2}
                    </c:if>
                    <dsp:getvalueof var="storeAddress3" param="storeItem.address3"/>
                    <c:if test="${not empty storeAddress3}">
                      ${storeAddress3}
                    </c:if>
                    <dsp:getvalueof var="storeCity" param="storeItem.city"/>
                    <c:if test="${not empty storeCity}">
                      <br/>
                      ${storeCity},
                    </c:if>
                    <dsp:getvalueof var="storeStateAddress" param="storeItem.stateAddress"/>
                    <c:if test="${not empty storeStateAddress}">
                      ${storeStateAddress},
                    </c:if>
                    <dsp:getvalueof var="storePostalCode" param="storeItem.postalCode"/>
                    <c:if test="${not empty storePostalCode}">
                      ${storePostalCode}
                    </c:if>
                    <dsp:getvalueof var="storeCountry" param="storeItem.country"/>
                    <c:if test="${not empty storeCountry}">
                      ${storeCountry}
                    </c:if>
                  </p>
                  <%-- This element used for Screen Reader (VoiceOver) --%>
                  <div id="storeExt" style="display:none">
                    <div class="hidden">
                      <dsp:include page="gadgets/storeMapLink.jsp">
                        <dsp:param name="store" param="element"/>
                      </dsp:include>
                      <a href="tel:<dsp:valueof param='storeItem.phoneNumber'/>"><fmt:message key="mobile.common.phone"/></a>
                      <dsp:getvalueof var="email" vartype="java.lang.String" param="storeItem.email"/>
                      <c:if test="${not empty email}">
                        <a href="mailto:${email}"><fmt:message key="mobile.common.email"/></a>
                      </c:if>
                    </div>
                  </div>
                </li>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
      </dsp:oparam>
      <dsp:oparam name="empty">
        <fmt:message key="mobile.company_stores.noStoresFound"/>
      </dsp:oparam>
    </dsp:droplet>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/company/stores.jsp#3 $$Change: 788278 $ --%>

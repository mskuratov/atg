<%--
  This page is the company's "Store Locator" page.
  Displays store list for current site.

  Required Parameters:
    None
    
  Optional Parameters:
    None

--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/droplet/StoreLookupDroplet"/>
  <dsp:importbean bean="/atg/store/droplet/StoreSiteFilterDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

  <crs:pageContainer divId="atg_store_company"
                     bodyClass="atg_store_stores atg_store_leftCol atg_store_company">
    <div id="atg_store_contentHeader">
      <h2 class="title">
        <fmt:message key="company_stores.title"/>
      </h2>
    </div>
    <div class="atg_store_main">
      <ul class="atg_store_locator">
        <%-- Lookup stores from repository --%>      
        <dsp:droplet name="StoreLookupDroplet">
          <dsp:oparam name="output">
            <%-- Filter stores for current site --%>
            <dsp:droplet name="StoreSiteFilterDroplet">
              <dsp:param name="collection" param="items" />
              <dsp:oparam name="output">
                <%-- Iterate collection of stores --%>
                <dsp:droplet name="ForEach">
                  <dsp:param name="array" param="filteredCollection" />
                  <dsp:oparam name="output">
                    <dsp:setvalue param="storeItem" paramvalue="element"/> 
                    <li>
                      <div class="vcard">
                        <div class="org"><dsp:valueof param="storeItem.name"/></div>
                        <div class="adr">
                          <div class="street-address"><dsp:valueof param="storeItem.address1"/></div>

                          <dsp:getvalueof var="storeAddress2" param="storeItem.address2"/>
                          <c:if test="${not empty storeAddress2}">
                            <div class="street-address">${storeAddress2}</div>
                          </c:if>

                          <dsp:getvalueof var="storeAddress3" param="storeItem.address3"/>
                          <c:if test="${not empty storeAddress3}">
                            <div class="street-address">${storeAddress3}</div>
                          </c:if>

                          <dsp:getvalueof var="storeCity" param="storeItem.city"/>
                          <c:if test="${not empty storeCity}">
                            <span class="locality">${storeCity}</span>,
                          </c:if>
                          
                          <dsp:getvalueof var="storeCounty" param="storeItem.county"/>
                          <c:if test="${not empty storeCounty}">
                            <span class="county-name">${storeCounty}</span>,
                          </c:if>

                          <dsp:getvalueof var="storeStateAddress" param="storeItem.stateAddress"/>
                          <c:if test="${not empty storeStateAddress}">
                            <span class="region">${storeStateAddress}</span>,
                          </c:if>

                          <dsp:getvalueof var="storePostalCode" param="storeItem.postalCode"/>
                          <c:if test="${not empty storePostalCode}">
                            <span class="postal-code">${storePostalCode}</span>
                          </c:if>

                          <dsp:getvalueof var="storeCountry" param="storeItem.country"/>
                          <c:if test="${not empty storeCountry}">
                            <span class="country-name">${storeCountry}</span>
                          </c:if>
                        </div>

                        <div class="tel">
                          <span class="type"><fmt:message key="common.phone"/></span><fmt:message key="common.labelSeparator"/>
                          <span class="value"><dsp:valueof param="storeItem.phoneNumber"/></span>
                        </div>

                        <dsp:getvalueof var="faxNumber" param="storeItem.faxNumber"/>
                        <c:if test="${not empty faxNumber}">
                          <div class="tel">
                            <span class="type"><fmt:message key="common.fax"/></span><fmt:message key="common.labelSeparator"/>
                            <span class="value"><c:out value="${faxNumber}"/></span>
                          </div>
                        </c:if>

                        <dsp:getvalueof var="email" vartype="java.lang.String" param="storeItem.email"/>
                        <c:if test="${not empty email}">
                          <a class="email" href="mailto:${email}">
                            ${email}
                          </a>
                        </c:if>
                      </div>
                    </li>
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
          <dsp:oparam name="empty">
            <fmt:message key="company_stores.noStoresFound"/>
          </dsp:oparam>
        </dsp:droplet>
      </ul>
    </div>

    <%-- 
      This page renders navigation links to ancillary pages related to
      company policies, help and contact information 
    --%>
    <div class="atg_store_companyNavigation aside">
      <dsp:include page="/company/gadgets/navigationPanel.jsp">
        <dsp:param name="selpage" value="stores"/>
      </dsp:include>
    </div>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/company/stores.jsp#2 $$Change: 788278 $--%>

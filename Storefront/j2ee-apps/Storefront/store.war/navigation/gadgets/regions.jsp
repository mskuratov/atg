<%--
  This page renders the available regions for a particular site. A user is able
  to click on a region and will be directed to the site for that particular
  region.
  
  Required Parameters:
    None
  
  Optional Parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/ComponentExists"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/SharingSitesDroplet" />

  <dsp:importbean bean="/atg/multisite/Site" var="currentSite"/>
  <dsp:importbean bean="/atg/store/droplet/DisplayCountryDroplet"/>
  
  <%--
    ComponentExists droplet conditionally renders one of its output parameters
    depending on whether or not a specified Nucleus path currently refers to a
    non-null object.  It it used to query whether a particular component has been
    instantiated, in this case the InternationalStore. If the InternationalStore
    component has been instantiated then we render the alternate regions.
    
    Input Parameters:
      path - The path to a component
      
    Open Parameters:
      true - Rendered if the component 'path' has been instantiated.      
  --%>
  <dsp:droplet name="ComponentExists" path="/atg/modules/InternationalStore">
    <dsp:oparam name="true">
      <%--
        SharingSitesDroplet returns all the sharing sites within the given site
        group. If the siteId is not provided as an input parameter, it will be 
        retrieved from the SiteContext. Similarly, the ShareableTypeId may be 
        provided as an optional input parameter. The droplet is used here to 
        return all the sites which have the crs.RelatedRegionalStores sharable
        type with the current site, these are the regional stores. 
     
        Input Parameters:
          shareableTypeId - The shareable type's id.
       
        Open Parameters:
         output - This parameter is rendered once, if a collection of sites
                   is found.
       
        Output Parameters:
          sites - The list of sharing sites.
      --%>
      <dsp:droplet name="SharingSitesDroplet" shareableTypeId="crs.RelatedRegionalStores"
                                              var="sharingSites">
        <dsp:oparam name="output">
          <dsp:getvalueof var="sites" param="sites"/>
      
          <%-- Ensure we have stores in different regions --%>
          <c:if test="${fn:length(sharingSites.sites) > 1}">
            <dl id="atg_store_regions">
              <%-- Display the Country text --%>
              <dt>
                <fmt:message key="navigation_internationalStores.RegionsTitle"/>
                <fmt:message key="common.labelSeparator"/>
              </dt>
              
              <%--
                ForEach droplet renders the open parameter output for each 
                element in its array input parameter. For every regional site render
                a link to it.
                  
                Input Parameters:
                  array - The parameter that defines the list of items to output.
                    
                Open Parameters:
                  output - Rendered for each element in 'array'
                  
                Output Parameters:
                  size - Set to the size of the array
                    
                  count - Set to the index of the current element of the array
              --%>
              <dsp:droplet name="ForEach" array="${sharingSites.sites}"
                                            var="current">
                <dsp:oparam name="output">
                  <dsp:setvalue param="site" value="${current.element}"/>
 
                  <dsp:getvalueof var="siteName" param="site.name"/>
                  <dsp:getvalueof var="siteId" param="site.id"/>

                  <dsp:getvalueof var="siteDefaultCountry" param="site.defaultCountry"/>
                  <dsp:getvalueof var="siteDefaultLanguage" param="site.defaultLanguage"/>

                  <%-- 
                    DisplayCountryDroplet takes a locale language key and country code and 
                    returns the corresponding country display name in the user's locale.
                    
                    Input Paramaters:
                      language - Language code for a particular site, e.g - en, de, es...
           
                      countryCode - The country code e.g - US, DE, ES...
                              
                    Open Parameters:
                      output - Serviced when there are no errors
                              
                    Output Parameters:  
                      displayCountry - A country display name based on the language and country code
                                       input parameters.
                  --%>
                  <dsp:droplet name="DisplayCountryDroplet" 
                               countryCode="${siteDefaultCountry}" 
                               language="${siteDefaultLanguage}" 
                               var="siteCountry">
                               
                    <dsp:oparam name="output">
                      <c:set var="countryName" value="${siteCountry.displayCountryName}"/>
                    </dsp:oparam>      
                         
                  </dsp:droplet>
                    
                  <%-- Display a link to the related regional store --%>
                  <dd class="<crs:listClass count="${current.count}" size="${current.size}" 
                                            selected="${siteId == currentSite.id}" />">
                    <c:choose>
                      <%-- Display the current region name as text --%>
                      <c:when test="${siteId == currentSite.id}">
                        <dsp:valueof value="${countryName}" />
                      </c:when>
                      <%-- Otherwise generate a link to the related store --%>
                      <c:otherwise>
                        <dsp:include page="/global/gadgets/crossSiteLinkGenerator.jsp">
                          <dsp:param name="siteId" value="${siteId}"/>
                          <dsp:param name="customUrl" value="/"/>
                        </dsp:include>
                        <dsp:a href="${siteLinkUrl}" title="${countryName}">
                          <c:out value="${countryName}"/>
                        </dsp:a>
                      </c:otherwise>
                    </c:choose>
                  </dd>    
                </dsp:oparam>
              </dsp:droplet>
            </dl>
          </c:if>
        </dsp:oparam>
      </dsp:droplet>    
    </dsp:oparam>
  </dsp:droplet>  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/navigation/gadgets/regions.jsp#1 $$Change: 735822 $--%>
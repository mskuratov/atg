<%--
  This gadget renders available sites (stores). A user can click on a store
  link and will be directed to the desired store.
  
  Required Parameters:
    None
    
  Optional Parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/CartSharingSitesDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

  <dsp:importbean bean="/atg/multisite/Site" var="currentSite"/>

  <%--
    CartSharingSitesDroplet returns a collection of sites that share the shopping
	cart shareable (atg.ShoppingCart) with the current site.
	You may optionally exclude the current site from the result.

    Input Parameters:
      excludeInputSite - Should the returned sites include the current
   
    Open Parameters:
      output - This parameter is rendered once, if a collection of sites
               is found.
   
    Output Parameters:
      sites - The list of sharing sites.
  --%>
  <dsp:droplet name="CartSharingSitesDroplet" var="sharingSites">
    <dsp:oparam name="output">
      <%-- Ensure we have more than 1 site --%>
      <c:if test="${fn:length(sharingSites.sites) > 1}">
        
        <dl id="atg_store_sites">
          <dt>
            <%-- Display the Sites text --%>
            <fmt:message key="navigation_internationalStores.internationalStoresTitle"/>
            <fmt:message key="common.labelSeparator"/>
          </dt>

          <%--
            ForEach droplet renders the open parameter output for each 
            element in its array input parameter. For each site found render
            a link to the site.
            
            Input Parameters:
              array - The parameter that defines the list of items to output.
              
              sortProperties - A string that specifies how to sort the list of
                               items
              
            Open Parameters:
              output - Rendered for each element in 'array'
            
            Output Parameters:
              size - Set to the size of the array
              
              count - Set to the index of the current element of the array
          --%>
          <dsp:droplet name="ForEach" array="${sharingSites.sites}"
                                      sortProperties="-name"
                                      var="current">
            <dsp:oparam name="output">
              <dsp:setvalue param="site" value="${current.element}"/>

              <dsp:getvalueof var="siteName" param="site.name"/>
              <dsp:getvalueof var="siteId" param="site.id"/>

              <%-- Display a link to the other sites --%>
              <dd class="<crs:listClass count="${current.count}" size="${current.size}" 
                                        selected="${siteId == currentSite.id}" />">
                <c:choose>
                  <%-- Display the current site name as text --%>
                  <c:when test="${siteId == currentSite.id}">
                    <dsp:valueof value="${siteName}" />
                  </c:when>
                  <%-- Otherwise generate a link to the site --%>
                  <c:otherwise>
                    <dsp:include page="/global/gadgets/crossSiteLinkGenerator.jsp">
                      <dsp:param name="siteId" value="${siteId}"/>
                      <dsp:param name="customUrl" value="/"/>
                    </dsp:include>
                    <dsp:a href="${siteLinkUrl}" title="${siteName}">
                      <c:out value="${siteName}"/>
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
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/navigation/gadgets/sites.jsp#1 $$Change: 735822 $ --%>

<%--
  This page rendering the product articles description.
  
  Required Parameters:
    None

  Optional Parameters:
    None
--%>

<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/store/droplet/AsSeenInRQL"/>
  <dsp:importbean bean="/atg/store/droplet/CatalogItemFilterDroplet"/>
  
  <crs:pageContainer divId="atg_store_company" titleKey=""
                     bodyClass="atg_store_asSeenIn atg_store_leftCol atg_store_company">
    <div id="atg_store_contentHeader">
      <h2 class="title">
        <fmt:message key="browse_asSeenIn.title"/>
      </h2>
    </div> 
                    
    <dsp:getvalueof var="contextRoot" vartype="java.lang.String"
                    bean="/OriginatingRequest.contextPath"/>
    <div class="atg_store_main">
    
      <%--
        This droplet is used in favor of the ATG RQLQueryForEach droplet. There are
        two reasons for this. The first is that this droplet must be configured
        from outside the JSP to prevent the setting of an RQL query string in the
        JSP template. The second is that this droplet returns the result set (array
        of repository items) which allows other droplets to loop through them
        appropriately (Range for example). It also wraps the results in a
        transaction, something RQLQueryForEach does not do.         
      --%>
      <dsp:droplet name="AsSeenInRQL">
        <dsp:oparam name="output">
        
          <%-- Filters collection of items using CatalogItemFilter --%>
          <dsp:droplet name="CatalogItemFilterDroplet">
            <dsp:param name="collection" param="items" />
            <dsp:oparam name="output">
              <div id="atg_store_asSeenIn">
                <ul class="atg_store_asSeenInList atg_store_product">
                
                  <%-- 
                    Displays filtered collection
                    
                    Input Parameters:
                      collection - The parameter that defines the list of items to output.                     
              
                    Open Parameters:
                      output - Rendered for each element in 'collection' 
                  --%>
                  <dsp:droplet name="ForEach">
                    <dsp:param name="array" param="filteredCollection" />
                    <dsp:param name="sortProperties" value="-asSeenIn.date"/>
                    <dsp:oparam name="output">
                      <dsp:setvalue param="product" paramvalue="element"/>                     
                      <dsp:getvalueof var="productId" param="product.repositoryId" />
                      <li id="${productId}">
                        <%-- Link to Product --%>
                        <dsp:getvalueof var="templateUrl" param="product.template.url" />
                        <c:choose>
                        
                          <c:when test="${not empty templateUrl}">
                          
                            <%-- 
                              Product Template is set 
                              New Implementation for SEO 
                              Renders the links for the featured products on the
                              'As Seen In' quick links page, depending on the userAgent visiting the site 
                            --%>
                            <dsp:droplet name="/atg/repository/seo/CatalogItemLink">
                              <dsp:param name="item" param="product"/>
                              <dsp:oparam name="output">
                                <dsp:getvalueof var="url" vartype="String" param="url"/>
                                <dsp:a href="${contextRoot}${url}" title="${viewProduct}">
                                
                                  <span class="atg_store_productImage">
                                    <%-- Product thumbnail --%>
                                    <dsp:include page="/browse/gadgets/productImg.jsp">
                                      <dsp:param name="product" param="product"/>
                                      <dsp:param name="image" param="product.smallImage"/>
                                      <dsp:param name="showAsLink" value="false"/>
                                      <dsp:param name="defaultImageSize" value="small"/>     
                                    </dsp:include>
                                  </span>
                                  
                                  <dsp:include page="/global/gadgets/crossSiteLinkGenerator.jsp">
                                    <dsp:param name="product" param="product"/>
                                    <dsp:param name="customUrl" value="${contextRoot}${url}"/>
                                  </dsp:include>
                                  
                                  <dsp:getvalueof var="finalUrl" value="${siteLinkUrl}"/>
                                  
                                  <dsp:include page="/browse/gadgets/asSeenInElement.jsp">
                                    <dsp:param name="product" param="product"/>
                                    <dsp:param name="hasTemplate" value="true" />
                                    <dsp:param name="url" value="${finalUrl}" />
                                  </dsp:include> 
                                                             
                                </dsp:a>
                              </dsp:oparam> <%-- End oparam output --%>
                            </dsp:droplet> <%-- End ProductLookupItemLink droplet --%>
                          </c:when>
                          
                          <c:otherwise>                          
                            <%-- Product Template not set --%>                            
                            <dsp:include page="/browse/gadgets/asSeenInElement.jsp">
                              <dsp:param name="product" param="product"/>
                              <dsp:param name="hasTemplate" value="false"/>
                            </dsp:include>                      
                          </c:otherwise>
                          
                        </c:choose> <%-- End is template empty --%>
                      </li>
                    </dsp:oparam>
                  </dsp:droplet> <%-- For Each item --%>
                </ul>
              </div>
            </dsp:oparam>
          </dsp:droplet> <%-- Filtering items not in catalog --%>
        </dsp:oparam>    
        <dsp:oparam name="empty">
          <fmt:message key="browse_asSeenIn.noItemsFound"/>
        </dsp:oparam>
      </dsp:droplet> <%-- As Seen In Query --%>
    </div>
    
    <%-- 
      This page renders navigation links to ancillary pages related to
      company policies, help and contact information 
    --%>
    <div class="atg_store_companyNavigation aside">
      <dsp:include page="/company/gadgets/navigationPanel.jsp">
        <dsp:param name="selpage" value="asSeenIn"/>
      </dsp:include>
    </div>    
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/asSeenIn.jsp#1 $$Change: 735822 $--%>

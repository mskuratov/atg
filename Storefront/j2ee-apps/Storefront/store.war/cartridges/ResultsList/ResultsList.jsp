<%-- 
  This page lays out the elements that make up the search results page.
    
  Required Parameters:
    contentItem
      The content item - results list type 
   
  Optional Parameters:

--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/search/droplet/GetClickThroughId"/>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  
  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/> 

      <%-- Render the search results. --%>
      
      <dsp:getvalueof var="size" value="${contentItem.totalNumRecs}"/>
      <dsp:getvalueof var="page" value="${contentItem.recsPerPage}"/>
      <c:set var="question"><dsp:valueof param="Ntt" valueishtml="true"/></c:set>
      <%-- Display the number of search results if this is a query search --%>

      <div id="atg_store_mainHeader">
        <div class="atg_store_searchResultsCount">
          <c:choose>
            <c:when test="${size == 1}">
              
              <c:choose>
                <c:when test="${not empty question}">
                   <fmt:message var="resultsMessage" key="facet.facetGlossaryContainer.oneResultFor"/>
                </c:when>
                <c:otherwise>
                  <fmt:message var="resultsMessage" key="facet.facetGlossaryContainer.oneResult"/>
                </c:otherwise>
              </c:choose>            
              
            </c:when>
            <c:otherwise>             
              <c:choose>
                <c:when test="${not empty question}">
                  <fmt:message var="resultsMessage" key="facet.facetGlossaryContainer.resultFor"/>
                </c:when>
                <c:otherwise>
                  <fmt:message var="resultsMessage" key="facet.facetGlossaryContainer.results"/>
                </c:otherwise>
              </c:choose>    
                
            </c:otherwise>
          </c:choose>
          <%-- Escape XML specific characters in search term to prevent using it for XSS attacks. --%>
          <span id="resultsCount"><c:out value="${size}"/></span>&nbsp;<c:out value="${resultsMessage}"/>&nbsp;<span class="searchTerm">${fn:escapeXml(question)}</span>
        </div>
      </div>

      <%--Set the number of product columns to render--%>
      <c:set var="columnCount" value="4"/>
  
      <div id="atg_store_catSubProdList">
        <c:choose>
          <%-- No Results --%>
          <c:when test="${empty size || size == 0 }">
            <crs:messageContainer optionalClass="atg_store_noMatchingItem" titleKey="facet_facetSearchResults.noMatchingItem"/>            
          </c:when>
          <%-- Display Results --%>
          <c:otherwise>            
            <%-- Display sort options --%>
            <div class="atg_store_filter">
          
              <dsp:form id="sortByForm" action="/crs/browse">
                <label for="sortBySelect">
                  <fmt:message key="common.sortBy" />:
                </label>
                
                <select  id="sortBySelect" name="sort" onchange="location = this.options[this.selectedIndex].value">
                  <c:forEach var="sortOption" items="${contentItem.sortOptions}">
                    <c:url value="${sortOption.navigationState}" var="sortAction" />
                    <option
                        <c:if test="${sortOption.selected}">
                            selected="true"
                        </c:if>
                        value="${sortAction}"><fmt:message key="${sortOption.label}"/>
                    </option>
                  </c:forEach>
                </select>
                
                <noscript>
                  <span class="atg_store_sortButton">
                    <input value="Sort" type="submit"/>
                  </span>
                </noscript>
                
              </dsp:form>
              
            </div>

            <%--Display top pagination options --%>
            <dsp:include page="productListRangePagination.jsp">
              <dsp:param name="top" value="true" />
              <dsp:param name="contentItem" value="${content}"/>
            </dsp:include>

            <div id="atg_store_prodList">
              <ul class="atg_store_product" id="atg_store_product">
                <%--
                  Loop though each product returned.  Since the search is aware of our desired
                  pagination the results will be the correct results for the current page
                  
                  product.repositoryId
                  <c:out value="${record}"/>
                  <c:out value=" Product id = ${productId}" />
                                    <li>
                      <c:out value="${productId}" />
                  </li>
                --%>
                <c:forEach var="record" items="${contentItem.records}" varStatus="loopStatus">
                  
                  <dsp:getvalueof var="index" value="${loopStatus.index}"/>
                  <dsp:getvalueof var="count" value="${loopStatus.count}"/>
                  <dsp:getvalueof var="productId" value="${record.attributes['product.repositoryId']}" />

                  
                  <%--
                    Generates unique search click identifier for each search result. The generated search click
                    identifier will be appended to product links on search results page as URL parameter and will
                    notify reporting service. This parameter tells reporting service that user navigated to the product
                    page from the search results and which search query returned the product.
                    
                    Input Parameters:
                      result
                        Search result to generate search click identifier for.
                        
                    Open Parameters:
                      output
                        Always serviced.
                        
                    Output Parameters:
                      searchClickId
                        Generated search click identifier. 
                  
                  <dsp:droplet name="GetClickThroughId">
                    <dsp:param name="result" param="element"/>
                    <dsp:oparam name="output">
                    </dsp:oparam>
                  </dsp:droplet>  

                   --%>
                  <dsp:droplet name="GetClickThroughId">
                    <dsp:param name="result" param="element"/>
                    <dsp:oparam name="output">
                      <%--
                        Get the product according to the ID returned from the ATG Search results
                   
                        Input Parameters:
                          id - The ID of the product we want to look up
                          filterBySite - The site to filter by, or false if no filter should be applied
                          filterByCatalog - The catalog to filter by, or false if no filter should be applied
                      
                        Open Parameters:
                          output - Serviced when no errors occur
                          error - Serviced when an error was encountered when looking up the product
                          empty - Serviced when no product is found
                      
                        Output Parameters:
                          element - The product whose ID matches the 'id' input parameter  
                      
                      
                      --%>
                      <dsp:droplet name="ProductLookup">
                        <dsp:param name="id" value="${record.attributes['product.repositoryId']}"/>
                        <dsp:param name="filterBySite" value="false"/>
                        <dsp:param name="filterByCatalog" value="false"/>
                        <dsp:param bean="/OriginatingRequest.requestLocale.locale" name="repositoryKey"/>
                        
                        <dsp:oparam name="output">

                         
                          <dsp:setvalue param="product" paramvalue="element"/>
                          <%-- Get the correct class for the <li> based on this loop's current index --%>
                          <dsp:getvalueof var="additionalClasses" vartype="java.lang.String" 
                                          value="${(count % columnCount) == 1 ? 'prodListBegin' : ((count % columnCount) == 0 ? 'prodListEnd':'')}"/>
                          
                          <li class="<crs:listClass count="${count}" size="${size}" selected="false"/>${empty additionalClasses ? '' : ' '}${additionalClasses}">  
                          
                            <%-- 
                              Figure out if this product belongs to the current site, if not, 
                              display the site indicator in productListRangeRow 
                            

                            --%>
                            <dsp:getvalueof var="productSites" param="product.siteIds" />
                            <dsp:getvalueof var="siteId" bean="Site.id" />
                            <dsp:contains var="productFromCurrentSite" values="${productSites}" object="${siteId}"/> 
                            <dsp:getvalueof var="product" param="element"/>
                            <dsp:include page="/global/gadgets/productListRangeRow.jsp">
                              <dsp:param name="categoryId" param="product.parentCategory.id" />                      
                              <dsp:param name="product" param="product" />
                              <dsp:param name="categoryNav" value="false" />
                              <dsp:param name="displaySiteIndicator" value="${!productFromCurrentSite}" />
                              <dsp:param name="mode" value="name"/>
                              <dsp:param name="searchClickId" param="searchClickId"/>
                            </dsp:include>
                          </li>
                        </dsp:oparam>
                      </dsp:droplet>
                    </dsp:oparam>
                  </dsp:droplet>
                </c:forEach>
              </ul>
            </div>
          
            <%--Display bottom pagination options --%>
            <dsp:include page="productListRangePagination.jsp">
              <dsp:param name="top" value="false" />
              <dsp:param name="component" value="${content}"/>
            </dsp:include>
          </c:otherwise>
        </c:choose>
      </div>
    
    <div name="transparentLayer" id="transparentLayer"></div>
    
    <div name="ajaxSpinner" id="ajaxSpinner"></div>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cartridges/ResultsList/ResultsList.jsp#4 $$Change: 796430 $--%>

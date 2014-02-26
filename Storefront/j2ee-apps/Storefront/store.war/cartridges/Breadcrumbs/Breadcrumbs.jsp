<%--
  Breadcrumbs
  
  Renders refinement that have been selected. Selected refinements can consist
  of search refinements, dimension refinements or range filter refinements.
  
  There are a number of different types of breadcrumb that can be returned
  inside this content item:
    refinementCrumbs - As a result of selecting a dimension
    searchCrumbs - As a result of performing a search
    rangeFilterCrumbs - As a result of applying a range filter
--%>
<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  
  <dsp:getvalueof var="content" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/> 
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}"/>

  <c:if test="${not empty content.refinementCrumbs || not empty content.rangeFilterCrumbs || not empty content.searchCrumbs}">  
  
    <div id="atg_store_dimensionRefinements" class="atg_store_dimensionRefinementsSelected">

      <%-- Show "Your Selections" label--%>
      <h5><fmt:message key="common.yourSelectionsLabel"/></h5>
      
      <%-- Display searched terms if there are any --%>
      <c:if test="${not empty content.searchCrumbs}">
        <fmt:message var="searchTextQuotes" key="common.breadcrumbs.searchTextQuote"/>
        <div class="atg_store_refinements">
          <c:forEach var="searchCrumb" items="${content.searchCrumbs}">              
            <%-- Dimension refinement value e.g "Red" --%>
            <dsp:include page="/global/renderNavLink.jsp">
              <dsp:param name="navAction" value="${searchCrumb.removeAction}"/>
              <dsp:param name="text" value="${searchTextQuotes}${searchCrumb.terms}${searchTextQuotes}"/>
              <dsp:param name="showRemoveLink" value="true"/>
            </dsp:include>
          </c:forEach>
         </div>
       </c:if>      
      

      <%-- Display currently selected refinements if there are any --%>
      <c:forEach var="dimCrumb" items="${content.refinementCrumbs}">
      
        <%-- Get dimension name for refinement --%>
        <c:set var="dimensionName" value="${dimCrumb.dimensionName}" />
         
       <c:if test="${not empty dimCrumb.ancestors}">
      
          <c:set var="showRemoveButton" value="true"/>
          <%-- Ancestors E.g for category --%>
          <script type="text/javascript">
            dojo.ready(function(){

              var divContainer = dojo.byId("atg_store_refinementAncestorsLinks");

              if (divContainer){
                dojo.connect(divContainer, "onclick", function(evt){
                  evt.stopPropagation();
                });
                  
                dojo.connect(divContainer, "onmouseover", function(evt){
                  dojo.addClass(divContainer.parentNode, "noBackgroundImage");
                  dojo.stopEvent(evt);
                });

                dojo.connect(divContainer, "onmouseleave", function(evt){
                  dojo.removeClass(divContainer.parentNode, "noBackgroundImage");

                  dojo.query(evt.currentTarget).children().forEach(function(childItem){
                    dojo.removeClass(childItem, "highlight");
                    dojo.removeClass(childItem, "fade");
                  });

                  dojo.stopEvent(evt);
                });
              }

              var lastAncestorLink = dojo.byId("atg_store_refinementAncestorsLastLink");

              if (lastAncestorLink){
                dojo.connect(lastAncestorLink, "onmouseover", function(evt){

                  dojo.query(evt.currentTarget).siblings().andSelf().forEach(function(childItem){
                      dojo.removeClass(childItem, "highlight");
                      dojo.addClass(childItem, "fade");
                  });

                });
              }

              dojo.query("#atg_store_refinementAncestorsLinks > a").forEach(function(item){
                
                dojo.connect(item, "onmouseover", function(evt){

                  dojo.query(evt.currentTarget).siblings().andSelf().forEach(function(childItem){
                    dojo.removeClass(childItem, "highlight");
                    dojo.removeClass(childItem, "fade");
                  });
                  
                  dojo.query(evt.currentTarget).prevAll().andSelf().forEach(function(linkItem){
                    dojo.addClass(linkItem, "highlight");
                  });

                  dojo.query(evt.currentTarget).nextAll().forEach(function(linkItem){
                    dojo.addClass(linkItem, "fade");
                  });

                });

              });

              function removeAllSelections(){
                location.href="${contextPath}${dimCrumb.removeAction.contentPath}${dimCrumb.removeAction.navigationState}";
              }

              var divContainerParent = divContainer.parentNode;

              dojo.connect(divContainerParent, "click", this, removeAllSelections);

              dojo.connect(divContainerParent, "keypress", this, function(evt){
                if(evt.charOrCode == dojo.keys.ENTER){
                  removeAllSelections();
                }
              });

            });
          </script>
          
         <div id="atg_store_refinementAncestors" tabindex="0">
            <div id="atg_store_refinementAncestorsLinks">
              <c:forEach var="ancestor" items="${dimCrumb.ancestors}">
              
                <%-- Check whether it's product.category dimension and if so enable editing of corresponding category in preview. --%>
                <c:choose>
                  <c:when test="${dimensionName eq 'product.category'}">
                  
                    <c:set var="categoryId" value="${ancestor.properties['category.repositoryId'] }"/>
                    
                    <preview:repositoryItem itemId="${categoryId}" itemType="category"
                                            repositoryName="ProductCatalog">
                  
                      <dsp:include page="/global/renderNavLink.jsp">
                        <dsp:param name="navAction" value="${ancestor}"/>
                        <dsp:param name="text" value="${ancestor.label}"/>
                      </dsp:include>
                      
                    </preview:repositoryItem>
                  </c:when>
                  <c:otherwise>
                
                    <dsp:include page="/global/renderNavLink.jsp">
                      <dsp:param name="navAction" value="${ancestor}"/>
                      <dsp:param name="text" value="${ancestor.label}"/>
                    </dsp:include>
                    
                  </c:otherwise>
                </c:choose>
                
                <span class="divider"><fmt:message key="common.breadcrumbs.categoryDelimiter"/></span>
                                                        
              </c:forEach>
              
              <%--
                Render the last child of hierarchical breadcrumb. It is
                not a link
               --%>        
              <c:if test="${not empty dimCrumb.ancestors}">
              
                <%-- Check whether it's product.category dimension and if so enable editing of corresponding category in preview. --%>
                <c:choose>
                  <c:when test="${dimensionName eq 'product.category'}">
                  
                    <c:set var="categoryId" value="${dimCrumb.properties['category.repositoryId'] }"/>
                    
                    <preview:repositoryItem itemId="${categoryId}" itemType="category"
                                            repositoryName="ProductCatalog">
                  
                      <span id="atg_store_refinementAncestorsLastLink"><dsp:valueof value="${dimCrumb.label}"/></span>
                      
                    </preview:repositoryItem>
                  </c:when>
                  <c:otherwise>
                    <span id="atg_store_refinementAncestorsLastLink"><dsp:valueof value="${dimCrumb.label}"/></span>
                  </c:otherwise>
                </c:choose>
              </c:if>
            </div>            
          </div>
      </c:if>
        
        <div class="atg_store_refinements">    
          <%-- 
            Dimension refinement value e.g "Red". If it is hierarchical breadcrumb 
            it was already rendered in atg_store_refinementAncestors div.
           --%>         
           <c:if test="${empty dimCrumb.ancestors}">
           
            <%-- Check whether it's product.category dimension and if so enable editing of corresponding category in preview. --%>
            <c:choose>
              <c:when test="${dimensionName eq 'product.category'}">
                  
                <c:set var="categoryId" value="${dimCrumb.properties['category.repositoryId'] }"/>
                
                <preview:repositoryItem itemId="${categoryId}" itemType="category"
                                        repositoryName="ProductCatalog">
              
                  <dsp:include page="/global/renderNavLink.jsp">
                    <dsp:param name="navAction" value="${dimCrumb.removeAction}"/>
                    <dsp:param name="text" value="${dimCrumb.label}"/>
                    <dsp:param name="showRemoveLink" value="${showRemoveButton}"/>
                  </dsp:include>
                  
                </preview:repositoryItem>
              </c:when>
              <c:otherwise>
                <dsp:include page="/global/renderNavLink.jsp">
                  <dsp:param name="navAction" value="${dimCrumb.removeAction}"/>
                  <dsp:param name="text" value="${dimCrumb.label}"/>
                  <dsp:param name="showRemoveLink" value="${showRemoveButton}"/>
                </dsp:include>    
              </c:otherwise>
            </c:choose>    
          </c:if>
        </div>
      </c:forEach>
      
      <div class="atg_store_refinements">     
        <%-- Display currently selected Range Filters e.g Price Slider --%>
        <c:forEach var="filterCrumb" items="${content.rangeFilterCrumbs}"> 
        
          <%-- Ensure that start/end date range filters don't appear in the breadcrumbs list --%>
          <c:if test="${filterCrumb.propertyName == 'sku.activePrice'}">
                        
            <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <dsp:param name="price" value="0"/>
              <dsp:param name="saveFormattedPrice" value="true"/>
            </dsp:include>
  
            <%-- Format lower bound: no grouping and fraction digits--%>
            <fmt:formatNumber var="lowerBound" value="${filterCrumb.lowerBound}" maxFractionDigits="0" groupingUsed="false"/>
            <c:choose>
             <c:when test="${fn:contains(formattedPrice, '0.00')}">           
               <dsp:getvalueof var="minPrice" value="${fn:replace(formattedPrice, '0.00', lowerBound)}"/>
             </c:when>
             <c:otherwise>
               <dsp:getvalueof var="minPrice" value="${fn:replace(formattedPrice, '0,00', lowerBound)}"/>
             </c:otherwise>
            </c:choose>
                      
            <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <dsp:param name="price" value="0"/>
              <dsp:param name="saveFormattedPrice" value="true"/>
            </dsp:include>
            
            <%-- Format upper bound: no grouping and fraction digits--%>
            <fmt:formatNumber var="upperBound" value="${filterCrumb.upperBound}" maxFractionDigits="0" groupingUsed="false"/>
            <c:choose>
             <c:when test="${fn:contains(formattedPrice, '0.00')}">           
               <dsp:getvalueof var="maxPrice" value="${fn:replace(formattedPrice, '0.00', upperBound)}"/>
             </c:when>
             <c:otherwise>
               <dsp:getvalueof var="maxPrice" value="${fn:replace(formattedPrice, '0,00', upperBound)}"/>
             </c:otherwise>
            </c:choose>
            
            <dsp:a href="${contextPath}${filterCrumb.removeAction.contentPath}${filterCrumb.removeAction.navigationState}" class="atg_store_actionDelete" 
                   title="${titleText}">
              <span class="atg_store_refinementsPrice"><dsp:valueof value="${minPrice}"/></span> - <span class="atg_store_refinementsPrice"><dsp:valueof value="${maxPrice}"/></span>
            </dsp:a>  
              
          </c:if>   
                      
        </c:forEach>
      </div>
        
    </div>      
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cartridges/Breadcrumbs/Breadcrumbs.jsp#5 $$Change: 796430 $--%>

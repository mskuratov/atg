<%-- 
  This gadget displays the list of category products. Displayed products are sorted according to the sort options passed
  to the gadget. The howMany parameter specifies how many products will be displayed by the gadget. The pagination links are
  also included into the display in the case a number of products is greater than the number of products per page.  
  
  Required Parameters:
    contentItem
      The category child products content item.
    products
      The list of child products to display.
    sortSelection
      The property name to sort products by.
    howMany
      The number of products to display.
    start
      The starting index to display products from.
    p
      The page number.  
   
  Optional Parameters:   
    viewAll
      The boolean indicating whether all products should be displayed on the page ignoring the products per page
      setting.
--%>

<dsp:page>

  <dsp:importbean bean="/atg/store/sort/RangeSortDroplet" />
  <dsp:getvalueof var="originatingRequestURL" bean="/OriginatingRequest.requestURI"/> 
   
  <dsp:getvalueof var="contentItem" param="contentItem"/>

  <dsp:getvalueof var="sortSelection" param="sortSelection"/>
  <%-- Get the total number of products --%>
  <dsp:getvalueof var="products" param="products"/>  
  <c:set var="collectionSize" value="${fn:length(products)}" />
  
  <%-- Set the number of products displayed per row to 4 --%>
  <c:set var="rowSize"  value="4" />

      <dsp:droplet name="RangeSortDroplet">
        <dsp:param name="array" param="products"/>
        <dsp:param name="sortSelection" param="sortSelection"/>
        <dsp:param name="howMany" param="howMany"/>
        <dsp:param name="start" param="start"/>
        
        <%-- Rendered once before the main "output" --%>
        <dsp:oparam name="outputStart">
          
          <%-- Display sort options --%>
          <div class="atg_store_filter">
            
            <dsp:form id="sortByForm" action="${originatingRequestURL}${contentItem.categoryAction}">
              <label for="sortBySelect">
                <fmt:message key="common.sortBy" />:  
              </label>
               <select id="sortBySelect" name="sort" onchange="this.form.submit()" >
                
                <option value="" ${(empty sortSelection) ? 'selected="selected"' : ''}>
                  <fmt:message key="common.topPicks"/>
                </option>
                
                <option value="displayName:ascending" ${sortSelection=='displayName:ascending' ? 'selected="selected"' : ''}>
                  <fmt:message key="sort.nameAZ"/>
                </option>
                
                <option value="displayName:descending" ${sortSelection=='displayName:descending' ? 'selected="selected"' : ''}>
                  <fmt:message key="sort.nameZA"/>
                </option>
                
                <option value="price:ascending" ${sortSelection=='price:ascending' ? 'selected="selected"' : ''}>
                  <fmt:message key="sort.priceLH"/>
                </option>
                
                <option value="price:descending" ${sortSelection=='price:descending' ? 'selected="selected"' : ''}>
                  <fmt:message key="sort.priceHL"/>
                </option>
              </select>
              <noscript>
                <span class="atg_store_sortButton">
                  <input value="Sort" type="submit"/>
                </span>
              </noscript>
              <input type="hidden" value="${contentItem.categoryDimensionId}" name="N" />
              <input type="hidden" value="" name="p"/>
              
            </dsp:form>
          </div>
        
          <%--Top Pagination Links action="/crs/browse"--%>
          <dsp:include  page="/global/gadgets/productListRangePagination.jsp">
            <dsp:param name="size" value="${collectionSize}" />
            <dsp:param name="top" value="true" />
            <dsp:param name="p" param="p" />
            <dsp:param name="arraySplitSize" value="${contentItem.recsPerPage}" />
            <dsp:param name="start" param="start"/>
            <dsp:param name="viewAll" param="viewAll"/>
          </dsp:include>
          
          <div id="atg_store_prodList">
            <ul class="atg_store_product" id="atg_store_product">
        </dsp:oparam>
        
        <%-- Product repository item is output --%>
        <dsp:oparam name="output">
        
          <dsp:setvalue param="product" paramvalue="element"/>
          <dsp:getvalueof var="product" param="product"/>
          <preview:repositoryItem item="${product}">
            <dsp:getvalueof var="count" vartype="java.lang.String" param="count"/>
            <dsp:getvalueof var="size" vartype="java.lang.String" param="size"/>
            <dsp:getvalueof var="additionalClasses" vartype="java.lang.String"
                            value="${(count % rowSize) == 1 ? 'prodListBegin' : ((count % rowSize) == 0 ? 'prodListEnd':'')}"/>
            
            <li class="<crs:listClass count="${count}" size="${size}" selected="false"/>${empty additionalClasses ? '' : ' '}${additionalClasses}">
            
              <%-- 
                Category id is empty if we are using search. Use parent category's id in this case.
              --%>  
              <dsp:getvalueof var="categoryId" param="contentItem.categoryId"/>
              
              <c:set var="categoryNav" value="true" />
              <c:if test="${empty categoryId}">
                <dsp:getvalueof var="categoryId" param="product.parentCategory.id"/>
                <c:set var="categoryNav" value="false" />
              </c:if>
                
              <%-- Render the Product --%>
              <fmt:message var="siteIndicatorPrefix" key="common.from"/>
              <dsp:include page="/global/gadgets/productListRangeRow.jsp">
                <dsp:param name="categoryId" value="${categoryId}"/>
                <dsp:param name="product" param="product" />
                <dsp:param name="displaySiteIndicator" value="true"/>
                <dsp:param name="displayCurrentSite" value="false"/>
                <dsp:param name="noSiteIcon" value="false"/>
                <dsp:param name="sitePrefix" value="${siteIndicatorPrefix}"/>
                <dsp:param name="categoryNav" value="${categoryNav}" />
              </dsp:include>
            </li>
          </preview:repositoryItem>
        </dsp:oparam>
        <%-- Rendered once after all products have been output --%>
        <dsp:oparam name="outputEnd">
            </ul>
          </div>
          
          <%-- Bottom Pagination Links --%>
          <dsp:include  page="/global/gadgets/productListRangePagination.jsp">
            <dsp:param name="size" value="${collectionSize}" />
            <dsp:param name="top" value="true" />
            <dsp:param name="p" param="p" />
            <dsp:param name="arraySplitSize" value="${contentItem.recsPerPage}" />
            <dsp:param name="start" param="start"/>
            <dsp:param name="viewAll" param="viewAll"/>
          </dsp:include>
        </dsp:oparam>        
      </dsp:droplet>

</dsp:page>


<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/categoryChildProductsRenderer.jsp#2 $$Change: 788278 $ --%>
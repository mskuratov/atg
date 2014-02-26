<%-- 
  This page displays the new Product offerings on the Store
  
  Required Parameters:
    None

  Optional Parameters:
    None 
--%>

<dsp:page>

  <dsp:importbean bean="/atg/store/droplet/CatalogItemFilterDroplet"/>
  <dsp:importbean bean="/atg/store/droplet/NewItemsRQL"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  
  <crs:pageContainer divId="atg_store_whatsNewIntro" titleKey="" 
                     bodyClass="atg_store_newItem">
                     
    <div id="atg_store_contentHeader">
      <h2 class="title">
        <fmt:message key="navigation_category.newItems"/>
      </h2>
    </div>
    
    <div class="atg_store_main atg_store_whatsNew">
      <div id="atg_store_newItemList">       
    
        <dsp:getvalueof var="newProductThreshold" vartype="java.util.Integer" 
                        bean="/atg/multisite/SiteContext.site.newProductThresholdDays"/>

        <dsp:getvalueof var="childCategories" bean="Profile.catalog.rootNavigationCategory.childCategories"/>
        
        <%--
          This droplet filters out items with invalid dates.

          Input parameters:
            collection
              Collection of items to be filtered. 

          Output parameters:
            filteredCollection
              Resulting filtered collection.

          Open parameters:
            output
              Always rendered.
        --%>
        <dsp:droplet name="CatalogItemFilterDroplet">
          <dsp:param name="collection" value="${childCategories}"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="childCategories" param="filteredCollection"/>
          </dsp:oparam>
          <dsp:oparam name="empty">
            <dsp:getvalueof var="childCategories" value=""/>
          </dsp:oparam>
        </dsp:droplet>
        
        
        <c:forEach var="cat" items="${childCategories}" varStatus="childCategoriesStatus">
          
          <dsp:param name="cat" value="${cat}"/>      
          <dsp:getvalueof var="catIdVar" param="cat.repositoryId"/>
         
          <%-- 
            Overriding the queryRQL from JSP because the RQLDroplet assumes all query parameters to be 
            of type String whereas the new-ness threshold and daysAvailable property are Number (int).
            This causes problem in executing the comparison query to retrieve the new products. Setting the 
            parameters as below from the JSP solves the issue. 
          --%>
          <dsp:getvalueof var="queryRQLVar" 
                          value='(new = true OR daysAvailable <= ${newProductThreshold}) AND ancestorCategories INCLUDES ITEM (id = "${catIdVar}")'/>
      
          <%-- 
            This droplet is used in favor of the ATG RQLQueryForEach droplet. There are
            two reasons for this. The first is that this droplet must be configured
            from outside the JSP to prevent the setting of an RQL query string in the
            JSP template. The second is that this droplet returns the result set (array
            of repository items) which allows other droplets to loop through them
            appropriately (Range for example). It also wraps the results in a
            transaction, something RQLQueryForEach does not do.
          --%>
          <dsp:droplet name="NewItemsRQL">
            <dsp:param name="queryRQL" value="${queryRQLVar}"/>
        
            <dsp:oparam name="output">
              <dsp:droplet name="CatalogItemFilterDroplet">
                <dsp:param name="collection" param="items"/>
            
                <dsp:oparam name="output">
                  <dsp:getvalueof var="filteredCollection" vartype="java.lang.Object" param="filteredCollection"/>
                  <dsp:getvalueof id="size" value="${fn:length(filteredCollection)}"/>
              
                  <c:if test="${not empty filteredCollection}">
                    <dsp:getvalueof var="repositoryId" vartype="java.lang.String" param="cat.repositoryId" />                    
                    <a name="anchor${repositoryId}"></a>
                                    
                    <%-- Show category Name --%>
                    <h3>
                      <dsp:getvalueof var="displayName" vartype="java.lang.String" param="cat.displayName" />
                      <c:out value="${displayName}">
                        <fmt:message key="common.categoryNameDefault"/>
                      </c:out>
                    </h3>
                
                    <div class="atg_store_whatsNewProducts">
                      <ul class="atg_store_product">
                        <c:forEach var="filteredItem" items="${filteredCollection}" varStatus="filteredItemStatus">
                          <dsp:param name="product" value="${filteredItem}"/>
                          <%-- Retrieve parent category's id --%>
                          <dsp:param name="categoryId" param="product.parentCategory.id"/>
                          <dsp:getvalueof id="count" value="${filteredItemStatus.count}"/>
                          <dsp:getvalueof id="index" value="${filteredItemStatus.index}"/>
                          <li class="<crs:listClass count="${count}" size="${size}" selected="${index == currentSelection}"/>">
                            <dsp:include page="/global/gadgets/productListRangeRow.jsp">
                              <dsp:param name="productTitleClassName" value="atg_store_prodListItem"/>
                              <dsp:param name="productImageClassName" value="atg_store_prodListThumb"/>
                              <dsp:param name="productDescriptionClassName" value="atg_store_prodListDesc"/>
                              <dsp:param name="productPriceClassName" value="atg_store_prodListPrice"/>
                              <dsp:param name="productActionsClassName" value="atg_store_prodListDetLink"/>
                            </dsp:include>
                          </li>
                        </c:forEach><%-- end For Each new item --%>
                      </ul>
                    </div>
                
                  </c:if>
                </dsp:oparam>
            
              </dsp:droplet><%-- End filtering out items that were not new --%>
            </dsp:oparam>
        
          </dsp:droplet><%-- End getting descendant products for this category --%>
        </c:forEach>  
      </div>    
      
    </div>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/newItems.jsp#2 $$Change: 788278 $--%>
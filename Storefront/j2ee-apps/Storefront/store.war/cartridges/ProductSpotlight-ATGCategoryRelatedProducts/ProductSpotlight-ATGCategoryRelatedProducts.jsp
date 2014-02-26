<%-- 
  The renderer page for ProductSpotlight-ATGCategoryRelatedProducts cartridge. It displays featured
  products for the category page.
    
  Required Parameters:
    None.
   
  Optional Parameters:
    None.
--%>
<dsp:page>

  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/>
  
  <%-- Retrieve featured items list form content item. --%>
  <c:set var="relatedProducts" value="${contentItem.relatedProducts}"/>
  
  <c:if test="${not empty relatedProducts}">
  
    <div id="featured_products">
  
      <%-- Display Related Products if you got 'em --%>
      <div id="atg_store_featured_prodList">

        <h3>
          <%-- Display the title for this spotlight. --%>
          <crs:outMessage key="product_spotlight_categoryRelatedProductsTitle" />
        </h3>
          
        <dsp:getvalueof  var="size" value="${fn:length(relatedProducts)}" />
            
        <ul class="atg_store_product">
        
          <%-- Iterate through the featured products and display them. --%>
          <c:forEach var="product" items="${relatedProducts}" varStatus="status">
            <dsp:getvalueof var="index" value="${status.index}"/>
            <dsp:getvalueof var="count" value="${status.count}"/>
            <dsp:getvalueof var="additionalClasses" vartype="java.lang.String" 
                            value="${ (count == 1) ? 'prodListBegin' : count == size ? 'prodListEnd':''}"/>
                                  
            <li class="<crs:listClass count="${count}" size="${size}" selected="false"/>${empty additionalClasses ? '' : ' '}${additionalClasses}">
              <dsp:include page="/global/gadgets/productListRangeRow.jsp">
                <dsp:param name="categoryId" value="${contentItem.categoryId}"/>
                <dsp:param name="product" value="${product}" />
                <dsp:param name="categoryNav" value="false" />
              </dsp:include>
            </li> 
          </c:forEach>
        </ul>                   
      </div>
    </div>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cartridges/ProductSpotlight-ATGCategoryRelatedProducts/ProductSpotlight-ATGCategoryRelatedProducts.jsp#1 $$Change: 742374 $ --%>
<%--
  Used to render a product image, product name and product price.
    
  Required Parameters:
    product
      The product repository item to display    
    categoryId
      The repository ID of the product's category
    
  Optional Parameters:
    None
--%>

<dsp:page>
  
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>  
  <li>
    <%-- Generate the siteLinkURL link to the product --%>
    <dsp:include page="/global/gadgets/productLinkGenerator.jsp">
      <dsp:param name="product" param="product"/>
    </dsp:include>
      
    <dsp:getvalueof var="item" param="product"/>
    <preview:repositoryItem item="${item}">
    
      <dsp:getvalueof var="pageUrl" value="${siteLinkUrl}"/>
      <dsp:param name="productId" param="product.repositoryId"/>
      <dsp:param name="categoryId" param="product.parentCategory.id"/>
  
      <%-- Create a link to the product --%>
      <dsp:a href="${pageUrl}">  
        <%-- Render the Image --%>
        <span class="atg_store_productImage">
          <dsp:include page="/browse/gadgets/productImg.jsp">
            <dsp:param name="product" param="product"/>
            <dsp:param name="image" param="product.thumbnailImage"/>
            <dsp:param name="showAsLink" value="false"/>          
            <dsp:param name="imageSize" value="thumb"/>    
          </dsp:include>
        </span>
    
        <%-- Render the Display Name --%>
        <span class="atg_store_productTitle">      
          <dsp:include page="/browse/gadgets/productName.jsp">
            <dsp:param name="product" param="product"/>
            <dsp:param name="categoryId" param="product.parentCategory.id"/>
            <dsp:param name="showAsLink" value="false"/>
          </dsp:include>      
        </span>
      
        <%-- Check the size of the sku array to see how we handle price --%>
        <dsp:getvalueof var="childSKUs" param="product.childSKUs"/>
        <c:set var="totalSKUs" value="${fn:length(childSKUs)}"/>
      
        <%-- 
          The Compare droplet renders one of its open parameters based on
          the relative values of the obj1 and obj2 input parameters.      

          Input Parameters:
            obj1 - The first object to be compared (e.g obj1 > obj2)        
            obj2 - The second object to be compared       
  
          Open Parameters:
            greaterthan - Rendered obj1 > obj2        
            default - Rendered If either obj1 or obj2 is null
        --%>  
        <dsp:droplet name="Compare">
          <dsp:param name="obj1" value="${totalSKUs}" converter="number"/>
          <dsp:param name="obj2" value="1" converter="number"/>        
          <%-- Size is one --%>
          <dsp:oparam name="equal">
            <%-- Display Price --%>
            <span class="atg_store_productPrice">            
              <dsp:include page="/global/gadgets/priceLookup.jsp">
                <dsp:param name="product" param="product"/>
                <dsp:param name="sku" param="product.childSKUs[0]"/>
              </dsp:include>            
            </span>    
          </dsp:oparam>        
          <%-- Size is not one --%>    
          <dsp:oparam name="default">          
            <%-- Display Price Range (and Get Details link)--%>
            <span class="atg_store_productPrice">            
              <dsp:include page="/global/gadgets/priceRange.jsp">
                <dsp:param name="product" param="product"/>
              </dsp:include>            
            </span>                
          </dsp:oparam>
        </dsp:droplet>
      </dsp:a>
    </preview:repositoryItem>
  </li>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/promo/gadgets/homePromotionalItemRenderer.jsp#1 $$Change: 735822 $--%>

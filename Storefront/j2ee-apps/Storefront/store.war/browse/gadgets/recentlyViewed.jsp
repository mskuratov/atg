<%--
  This page renders a list of recently viewed products associated with a user's
  profile. It will be possible to exclude particular products from the list and
  also limit the number of products that can be displayed.
  
  The information to be displayed for each product in the recently viewed list is:
  
    - Name of product.
    - Product image.
    - Site that the product was viewed on.
    - Product pricing.
 
  Required Parameters:
    None.
  
  Optional Parameters:
    exclude
      This can be the product id, List of product ids or List of
      product RepositoryItems that are to be excluded from the 
      recently viewed list.
    size
      The number of products that are to be displayed in the
      recently viewed list. A default size is defined in
      the RecentlyViewedFilterDroplet filter component.
--%>

<dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
  <dsp:importbean bean="/atg/store/droplet/RecentlyViewedFilterDroplet" />
  <dsp:importbean bean="/atg/userprofiling/Profile" />
  
  <dsp:droplet name="RecentlyViewedFilterDroplet">
    <dsp:param name="collection" bean="Profile.recentlyViewedProducts"/>
    <dsp:param name="exclude" param="exclude"/>
    <dsp:param name="size" param="size"/>
    
    <dsp:oparam name="output">
      <dsp:getvalueof var="recentlyViewedProducts" param="filteredCollection" />
      <%-- 
        Display each item of the filteredCollection.
         
        Input Parameters:
          array:
            The filtered collection we wish to iterate over.
             
        Open Parameters:
          outputStart:
            Anything we want to output on the first iteration goes into this.
              - In this case we will output the title of the recently viewed
                items feature along with an HTML unordered list opening tag. 
          output:
            Rendered for each element in the collection.
              - In this case each product in the filteredCollection will
                displayed.
          outputEnd:
            Anything we want to output on the last iteration goes into this.
              - In this case we will close add an HTML unordered list tag.
      --%>
      <dsp:droplet name="ForEach">
        <dsp:param name="array" value="${recentlyViewedProducts}"/>
        <dsp:oparam name="outputStart">
          <%-- Add atg_store_recentlyViewedProducts css id attribute to replace this id --%>
          <div id="atg_store_recentlyViewedProducts">
            <%-- Display title and open unordered list tag --%>
            <h3>
              <fmt:message key="browse_recentlyViewedProducts.title" />
            </h3>                
            <ul class="atg_store_product">
         </dsp:oparam>
         <dsp:oparam name="output">
           <%-- 
             Get the 'product' item from the recentlyViewedProduct currently
             being iterated over. 
           --%>
           <dsp:param name="recentlyViewedProduct" param="element"/>
           
           <dsp:getvalueof var="product" param="recentlyViewedProduct.product"/>
           <dsp:getvalueof var="siteId" param="recentlyViewedProduct.siteId"/>
           
           <li>
             <%-- Output the 'product' name, image, site context, and pricing --%>
             <dsp:include page="/global/gadgets/promotedProductRenderer.jsp">
               <dsp:param name="product" value="${product}" />
               <dsp:param name="siteId" value="${siteId}" />
               <dsp:param name="imagesize" value="medium"/>
             </dsp:include>
           </li>  
          </dsp:oparam>
          <dsp:oparam name="outputEnd">
            </ul>
          </div> 
        </dsp:oparam>
      <%-- end ForEach droplet --%> 
      </dsp:droplet>
        
    </dsp:oparam>
  <%-- End RecentlyViewedFilterDroplet --%>  
  </dsp:droplet>
 
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/recentlyViewed.jsp#1 $$Change: 735822 $--%>

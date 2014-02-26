<%--
  This gadget searches for a product specified and displays the specified container with product info.
  If unable to find the product, a special 'Product not Available' page will be displayed.

  Required parameters:
    productId
      Specifies the product to be displayed by its ID.
    picker
      Specifies a picker page to be rendered.
    container
      Specifies a product details gadget to be rendered.

  Optional parameters:
    categoryId
      Specifies a category the product is viewed from.
    categoryNavIds
      A colon-separated list of category navigation trail.
--%>
<dsp:page>
  <dsp:getvalueof id="productId" param="productId"/>
  <dsp:getvalueof id="colorSizePicker" param="colorSizePicker"/>
  <dsp:getvalueof id="categoryId" param="categoryId"/>
  <dsp:getvalueof id="tabname" param="tabname"/>
  <dsp:getvalueof id="quantity" param="quantity"/>
  <dsp:getvalueof id="container" param="container"/>
  <dsp:getvalueof var="missingProductId" vartype="java.lang.String" bean="/atg/commerce/order/processor/SetProductRefs.substituteDeletedProductId"/>

  <dsp:importbean bean="/atg/commerce/catalog/ProductBrowsed"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistoryCollector"/>
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/store/droplet/ItemValidatorDroplet" />

  <%--
    This droplet searches for a product in the ProductCatalog repository.

    Input parameters:
      id
        A product's ID to be found.

    Output parameters:
      element
        A product found.

    Open parameters:
      output
        Rendered when product is found.
      empty
        Rendered when can't find the product.
      wrongSite
        Rendered when product is found, but it has wrong site associated.
  --%>
  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" param="productId"/>
    <dsp:oparam name="output">
      
      <dsp:droplet name="ItemValidatorDroplet">
        <dsp:param name="item" param="element"/>
        <dsp:oparam name="true">
          <%-- Display everything, if it's a real product, not missing product substitute. --%>
          <c:choose>
            <c:when test="${missingProductId != productId}">
              <%--
                Notify anyone who cares that the current product has been viewed.
                This droplet sends a special 'Product Browsed' JMS.

                Input parameters:
                  eventobject
                    Specifies a product browsed.

                Output parameters:
                  None.

                Open parameters:
                  None.
               --%>
               <dsp:droplet name="ProductBrowsed">
                 <dsp:param name="eventobject" param="element"/>
               </dsp:droplet>

               <%-- 
                 Add the category specfied by categoryId to the navigation 
                 history collector. 
               --%>
               <dsp:getvalueof var="navCategory" param="categoryId"/>
               <c:choose>
                 <c:when test="${empty navCategory}">
                   <%-- navCategory not specified, get default parent category. --%>
                   <dsp:getvalueof var="navCategory" param="element.parentCategory"/>
                 </c:when>
                 <c:otherwise>
                   <%--
                     This droplet looks up category by its ID in the product catalog.

                     Input parameters:
                       id - Specifies a category to be found.

                     Output parameters:
                       element - Category found.

                     Open parameters:
                       output - Rendered when category is found.
                   --%>
                   <dsp:droplet name="CategoryLookup">
                     <dsp:param name="id" value="${navCategory}"/>
                     <dsp:oparam name="output">
                       <%-- Does navCategory exists on the current site? --%>
                       <dsp:getvalueof var="category" param="element"/>
                       <c:set var="navCategory" value="${category}"/>
                     </dsp:oparam>
                   </dsp:droplet>
                 </c:otherwise>
               </c:choose>
               
               <%-- Update breadcrumbs with path to navCategory. --%>
               <dsp:droplet name="CatalogNavHistoryCollector">
                 <dsp:param name="item" value="${navCategory}" />
                 <dsp:param name="navAction" value="jump" />
               </dsp:droplet>

               <%-- Update last browsed category in profile. --%>
               <dsp:include page="categoryLastBrowsed.jsp"/>

               <%-- Call the container, passing along the product object and the colorSizePicker --%>
               <dsp:include page="${container}">
                 <dsp:param name="product" param="element"/>
                 <dsp:param name="colorSizePicker" param="colorSizePicker"/>
                 <dsp:param name="categoryId" param="categoryId"/>
                 <dsp:param name="navCategory" value="${navCategory}"/>
                 <dsp:param name="tabname" param="tabname"/>
                 <dsp:param name="quantity" param="quantity"/>
                 <dsp:param name="categoryNavIds" param="categoryNavIds"/>
               </dsp:include>
             </c:when>
             <c:otherwise>
               <%-- We're displaying a 'Missing Product' substitute, display a product not available page instead. --%>
               <dsp:include page="/browse/gadgets/productNotAvailable.jsp" />
             </c:otherwise>
           </c:choose>
         </dsp:oparam>
         <dsp:oparam name="false">
           <fmt:message var="errorMsg" key="common.productNotAvailable"/>
           <dsp:include page="/browse/gadgets/productNotAvailable.jsp">
             <dsp:param name="errorMsg" value="${errorMsg}"/>
           </dsp:include>
         </dsp:oparam>
       </dsp:droplet>
    
     
    </dsp:oparam>
    <dsp:oparam name="empty">
      <%-- Can't find the product, display a product not available page. --%>
      <dsp:include page="/browse/gadgets/productNotAvailable.jsp" />
    </dsp:oparam>
    <dsp:oparam name="wrongSite">
      <%-- Can't find the product on the current site, display a product not available page. --%>
      <dsp:include page="/browse/gadgets/productNotAvailable.jsp">
        <dsp:param name="site" bean="Site.name"/>
      </dsp:include>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/productLookupForDisplay.jsp#3 $$Change: 788432 $ --%>

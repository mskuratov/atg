<%--
  This page renders everything the user wants to see on the product detatils page.
  It displays product image, its parameters and description, price and picker to select proper SKU.

  Required parameters:
    product
      The product object to be displayed.
    picker
      Page name with gadget to be rendered.

  Optional parameters:
    categoryId
      ID of category the product is viewed from.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/store/droplet/CatalogItemFilterDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/userprofiling/PropertyManager"/>
  <dsp:importbean bean="/atg/store/profile/SessionBean"/>

  <dsp:getvalueof id="picker" param="picker" />
  <dsp:getvalueof id="navCategory" param="navCategory" />

  <%-- Start the page rendering. --%>
  <crs:pageContainer bodyClass="atg_store_pageProductDetail">
    <jsp:attribute name="SEOTagRenderer">
      <%-- Render SEO for the current product. --%>
      <dsp:include page="/global/gadgets/metaDetails.jsp">
        <dsp:param name="catalogItem" param="product"/>     
      </dsp:include>
    </jsp:attribute>
    
    <jsp:attribute name="formErrorsRenderer">
       <%-- Display error messages if any above the accessibility navigation --%>  
      <dsp:include page="/global/gadgets/displayErrorMessage.jsp">
        <dsp:param name="formHandler" bean="CartModifierFormHandler"/>
      </dsp:include>
      <dsp:include page="/global/gadgets/displayErrorMessage.jsp">
        <dsp:param name="formHandler" bean="GiftlistFormHandler"/>
      </dsp:include>
    </jsp:attribute>
    
    <jsp:body>
    
      <dsp:getvalueof var="item" param="product"/>
      <preview:repositoryItem item="${item}">
        <%-- Display product's header and top-level category promotion. --%>
        <dsp:include page="/browse/gadgets/itemHeader.jsp">
          <dsp:param name="displayName" param="product.displayName"/>
          <dsp:param name="category" param="navCategory"/>
          <dsp:param name="categoryNavIds" param="categoryNavIds"/>
        </dsp:include>

        <%-- Display error messages from form handlers used within this page. --%>
        <dsp:include page="/global/gadgets/displayErrorMessage.jsp">
          <dsp:param name="formHandler" bean="CartModifierFormHandler"/>
        </dsp:include>
        <dsp:include page="/global/gadgets/displayErrorMessage.jsp">
          <dsp:param name="formHandler" bean="GiftlistFormHandler"/>
        </dsp:include>

        <%-- Hidden 'Select SKU before adding to cart (gift/wish list)' error messages. --%>
        <div id="promptSelectDIV" class="promptSelectDIV">
          <fmt:message key="browse_picker.beforeAddingToCartMessage"/>
        </div>
        <div id="promptSelectDIV2" class="promptSelectDIV">
          <fmt:message key="browse_picker.beforeAddingToWishListMessage"/>
        </div>
        <div id="promptSelectDIV3" class="promptSelectDIV">
          <fmt:message key="browse_picker.beforeAddingToGiftListMessage"/>
        </div>

        <div id="atg_store_productCore" class="atg_store_productWithPicker">
          <%-- Product image will not be cached. Cache it and display here. --%>
          <div class="atg_store_productImage">
            <dsp:include page="gadgets/cacheProductDisplay.jsp">
              <dsp:param name="product" param="product"/>
              <dsp:param name="container" value="/browse/gadgets/productImage.jsp"/>
              <dsp:param name="categoryId" param="categoryId"/>
              <dsp:param name="keySuffix" value="image"/>
            </dsp:include>
          </div>
          <div id="productInfoContainer">
            
              <dsp:getvalueof var="contextRoot" vartype="java.lang.String"  bean="/OriginatingRequest.contextPath"/>
              <%--
                Because this page is called directly using ajax, it must look up the product.
                This droplet searches for a product in the product catalog.

                Input parameters:
                  id
                    Specifies a product to be found.

                Output parameters:
                  element
                    Product found.

                Open parameters:
                  output
                    Rendered when product is found.
              --%>
              
             
              <dsp:droplet name="ProductLookup">
                <dsp:param name="id" param="productId"/>
                <dsp:oparam name="output">
            
                  <dsp:setvalue param="product" paramvalue="element"/>

                  <%-- Construct an URL that will be used to redirect user on errors. --%>
                  <dsp:getvalueof var="productTemplateURL" vartype="java.lang.String" param="product.template.url"/>

                  <c:choose>
                    <c:when test="${empty originatingRequest.contextPath}">
                    
                      <dsp:getvalueof var="currentSiteContextRoot" bean="/atg/multisite/Site.contextRoot"/> 
                    
                      <c:url var="errorURL" context="${currentSiteContextRoot}" value="${productTemplateURL}">
                        <c:param name="productId" value="${productId}"/>
                        <c:param name="categoryId" value="${categoryId}"/>
                      </c:url>
                    </c:when>
                    <c:otherwise>
                      <c:url var="errorURL" context="${originatingRequest.contextPath}" value="${productTemplateURL}">
                        <c:param name="productId" value="${productId}"/>
                        <c:param name="categoryId" value="${categoryId}"/>
                      </c:url>
                    </c:otherwise>
                  </c:choose>

                  <dsp:getvalueof var="skus" param="product.childSKUs" />
                  <dsp:getvalueof var="skulength" value="${fn:length(skus)}" />

                  <%--
                    This droplet filters out the skus on base of current catalog.

                    Input parameters:
                      collection
                        Collection of product catalog items (products, SKUs, etc.) to be filtered.

                    Output parameters:
                      filteredCollection
                        Collection of product catalog items located in the current catalog.

                    Open parameters:
                      output
                        Rendered when filtering is done.
                  --%>
                  <dsp:droplet name="CatalogItemFilterDroplet">
                    <dsp:param name="collection" param="product.childSKUs"/>
                    <dsp:oparam name="output">
                      <div class="atg_store_productSummary">
                        <dsp:getvalueof var="filteredSkus" param="filteredCollection" />
                    
                       <%-- Display proper picker to enable the user to select SKU. --%>
                          <dsp:include page="${picker}">
                            <dsp:param name="product" param="product"/>
                            <dsp:param name="categoryId" param="categoryId"/>
                            <dsp:param name="skus" param="filteredCollection"/>
                         </dsp:include>          
                      </div> 
                    </dsp:oparam>
                    <dsp:oparam name="empty">
                      <div id="" class=" atg_store_generalMessage">
                        <div class="errorMessage"> 
                          <fmt:message key="common.productNotAvailable"/>
                        </div>
                      </div>
                    </dsp:oparam>
                  </dsp:droplet><%-- CatalogItemFilterDroplet --%>
             
                </dsp:oparam>
              </dsp:droplet><%-- ProductLookup --%>   
          
  
  
            <%-- As product attributes won't change, cache them and display here. --%>
            <dsp:include page="gadgets/cacheProductDisplay.jsp">
              <dsp:param name="product" param="product"/>
              <dsp:param name="container" value="/browse/gadgets/productAttributes.jsp"/>
              <dsp:param name="categoryId" param="categoryId"/>
              <dsp:param name="keySuffix" value="details"/>
              <dsp:param name="initialQuantity" param="initialQuantity" />
            </dsp:include>
          </div>
      
          <%-- Include recommendations container --%>
          <dsp:include page="gadgets/productRecommendationsContainer.jsp">
            <dsp:param name="productId" param="productId"/>
          </dsp:include>
      
        </div>
    
      </preview:repositoryItem>
    
     <%-- Display recently viewed products --%>
      <dsp:include page="/browse/gadgets/recentlyViewed.jsp">
        <dsp:param name="exclude" param="product.id"/>
      </dsp:include>

    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/productDetailDisplay.jsp#3 $$Change: 788278 $ --%>

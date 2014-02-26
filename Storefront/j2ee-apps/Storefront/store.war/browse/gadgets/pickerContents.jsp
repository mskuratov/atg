<%--
  This gadget renders a JavaScript-enabled version of color/size picker for the product details page.
  It displays a set of buttons to select appropriate color and size (that is appropriate SKU).
  It also displays the 'Add to Cart' button and 'Add to gift/wish List' or 'Email a Friend' buttons.

  Required parameters:
    productId
      Specifies a currently viewed product.

  Optional parameters:
    categoryId
      Specifies a currently viewed category.
--%>

<dsp:page>
  <dsp:getvalueof id="productId" param="productId"/>
  <dsp:getvalueof id="categoryId" param="categoryId"/>

  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/store/droplet/CatalogItemFilterDroplet"/>
  <dsp:importbean bean="/atg/store/droplet/ColorSizeDroplet"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/PropertyManager"/>
  <dsp:importbean bean="/atg/store/profile/SessionBean"/>
  <dsp:importbean bean="/atg/commerce/catalog/comparison/ProductList"/>
  <dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListContains"/>
  <dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>

  <dsp:getvalueof var="contextRoot" vartype="java.lang.String"  bean="/OriginatingRequest.contextPath"/>

  <%-- Because this page is called directly using ajax, it must look up the product. --%>
  <%--
    This droplet searches for a product in the ProductCatalog repository.

    Input parameters:
      id
        A product's ID to be found.
      filterBySite
        The site to filter by, or false if no filter should be applied
      filterByCatalog
        The catalog to filter by, or false if no filter should be applied

    Output parameters:
      element
        The product whose ID matches the 'id' input parameter.

    Open parameters:
      output
        Rendered when product is found.
      empty
        Rendered when can't find the product.
      wrongSite
        Rendered when product is found, but it has wrong site associated.
      error
        Serviced when an error was encountered when looking up the product.
  --%>
  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" param="productId"/>
    <dsp:oparam name="output">
      <dsp:setvalue param="product" paramvalue="element"/>

      <dsp:getvalueof var="productTemplateURL" vartype="java.lang.String" param="product.template.url"/>
      <dsp:getvalueof var="errorURL" vartype="java.lang.String"
                      value="${originatingRequest.contextPath}${productTemplateURL}?productId=${productId}&categoryId=${categoryId}"/>
      <dsp:getvalueof var="skus" param="product.childSKUs" />
      <dsp:getvalueof var="skulength" value="${fn:length(skus)}" />

      <%--
        This droplet filters out child product's SKUs that located in a wrong catalog.

        Input parameters:
          collection
            Collection of SKUs to be filtered.

        Output parameters:
          filteredCollection
            Resulting collection.

        Open parameters:
          output
            Always rendered.
      --%>
      <dsp:droplet name="CatalogItemFilterDroplet">
        <dsp:param name="collection" param="product.childSKUs"/>
        <dsp:oparam name="output">
          <%--
            This droplet calculates available colors and sizes for a collection of SKUs specified.
            It also searches for a SKU specified by its color and size properties.

            Input parameters:
              product
                Specifies a product to be processed.
              skus
                Collection of child SKUs.
              selectedColor
                Currently selected color.
              selectedSize
                Currently selected size.

            Output parameters:
              selectedSku
                Specifies a selected SKU, if both color and size are specified.
              availableColors
                All available colors.
              availableSizes
                All available sizes.
              giftlistsContainingSku
                All the giftlists containing the selected SKU
              wishlistContainsSku
                Flags, if user wishlist already contains the SKU specified. 
              comparisonsContainsProduct
                Flags, if current product is already added to the Comparisons List.   
              giftlists
                Collection of user's giftlists filtered by site.  
              showEmailAFriend
                Flags, if 'Email a Friend' button should be displayed.
              showGiftlists
                Flags, if 'Add to Giftlist' button should be displayed.  
            Open parameters:
              output
                Always rendered.
          --%>
          <dsp:droplet name="ColorSizeDroplet">
            <dsp:param name="skus" param="filteredCollection"/>
            <dsp:param name="selectedColor" param="selectedColor"/>
            <dsp:param name="selectedSize" param="selectedSize"/>
            <dsp:param name="product" param="product"/>
            <dsp:oparam name="output">
              <%-- Signal, that we're displaying a clothing-sku. Will be used later. --%>
              <dsp:param name="skuType" value="clothing"/>
              <div id="picker_contents">
                <dsp:form id="addToCart" formid="addToCart"
                          action="${originatingRequest.requestURI}" method="post"
                          name="addToCart">
                  <div id="atg_store_picker">
                    <div class="atg_store_selectAttributes">
                      <%@ include file="pickerPriceAttribute.jspf" %>
                      <%@ include file="pickerCartFormParams.jspf" %>
                      <div class="atg_store_pickerContainer">
                        <%@ include file="pickerColorPicker.jspf" %>
                        <%@ include file="pickerSizePicker.jspf" %>
                      </div>
                      <div class="atg_store_addQty">
                        <div class="atg_store_quantity">
                          <%@ include file="pickerQuantityAttribute.jspf" %>
                          <%@ include file="pickerItemId.jspf" %>
                        </div>
                        <div class="atg_store_productAvailability">
                          <%@ include file="pickerAvailabilityMessage.jspf" %>
                          <%@ include file="pickerAddToCart.jspf" %>
                        </div>
                      </div>
         
                    </div>
                  </div> 

                  <%-- All picker buttons. --%>
                  <dsp:include page="pickerActions.jsp">
                    <dsp:param name="comparisonsContainsProduct" param="comparisonsContainsProduct"/>
                    <dsp:param name="showEmailAFriend" param="showEmailAFriend"/>
                    <dsp:param name="showGiftlists" param="showGiftlists"/>
                    <dsp:param name="wishlistContainsSku" param="wishlistContainsSku"/>
                    <dsp:param name="giftlists" param="giftlists"/>
                    <dsp:param name="giftlistsContainingSku" param="giftlistsContainingSku"/>
                  </dsp:include>
                </dsp:form>
                <%-- Include invisible form, it's used for refreshing the picker. --%>
                <%@ include file="pickerRefreshForm.jspf" %>
              </div>
            </dsp:oparam>
          </dsp:droplet><%-- ColorSizeDroplet --%>
        </dsp:oparam>
      </dsp:droplet><%-- CatalogItemFilterDroplet --%>
    </dsp:oparam>
  </dsp:droplet><%-- ProductLookup --%>   
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/pickerContents.jsp#2 $$Change: 788278 $ --%>

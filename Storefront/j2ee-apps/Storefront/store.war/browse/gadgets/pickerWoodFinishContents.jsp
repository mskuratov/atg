<%--
  This gadget renders a JavaScript-enabled version of wood finish picker for the product details page.
  It displays a set of buttons to select appropriate color and size (that is appropriate SKU).
  It also displays the 'Add to Cart' button and 'Add to gift/wish List' or 'Email a Friend' buttons.

  Required parameters:
    productId
      Specifies a currently viewed product.

  Optional parameters:
    categoryId
      Specifies a currently viewed category.
    giftlistsContainingSku
      All the giftlists containing the selected SKU
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
  <dsp:importbean bean="/atg/store/droplet/CatalogItemFilterDroplet"/>
  <dsp:importbean bean="/atg/store/droplet/WoodFinishDroplet"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
  
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
      <dsp:getvalueof var="productId" vartype="java.lang.String" param="productId"/>
      <dsp:getvalueof var="categoryId" vartype="java.lang.String" param="categoryId"/>
      <c:url var="errorURL" value="${productTemplateURL}">
        <c:param name="productId" value="${productId}"/>
        <c:param name="categoryId" value="${categoryId}"/>
      </c:url>
    
    <%--
      This droplet is used to filter a collection of catalog
      items. Here we use this droplet to validate the input collection of product's
      SKUs using the ItemDateValidator (filters out outdated SKUs)..

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
          This droplet calculates available wood finishes for a collection of SKUs specified.
          It also searches for a SKU specified by its wood finish.
                
          Input parameters:
            product
              Specifies a product to be processed.
            skus
              Collection of child SKUs.
            selectedColor
              Currently selected wood finish.

          Output parameters:
            selectedSku
              Specifies a selected SKU, if wood finish is specified.
            availableColors
              All available wood finishes.
            giftlistsContainingSku
              All the giftlists containing the selected SKU
            comparisonsContainsProduct
              Flags, if current product is already added to the Comparisons List.
            showEmailAFriend
              Flags, if 'Email a Friend' button should be displayed.
            showGiftlists
              Flags, if 'Add to Giftlist' button should be displayed.
            wishlistContainsSku
              Flags, if user wishlist already contains the SKU specified.
            giftlists
              Collection of user's giftlists filtered by site.  
                  
          Open parameters:
            output
              Always rendered.
        --%>      
        <dsp:droplet name="WoodFinishDroplet">
          <dsp:param name="skus" param="filteredCollection"/>
          <dsp:param name="selectedColor" param="selectedColor"/>
          <dsp:param name="product" param="product"/>
          <dsp:oparam name="output">
            <dsp:param name="skuType" value="furniture"/>
            <div id="picker_contents">
              <dsp:form id="addToCart" formid="addToCart"
                        action="${pageContext.request.requestURI}" method="post"
                        name="addToCart">
                <div id="atg_store_picker">
                  <div class="atg_store_selectAttributes">
                    <%@include file="pickerPriceAttribute.jspf"%>
                    <%@include file="pickerCartFormParams.jspf"%>
                    <div class="atg_store_pickerContainer">
                      <%@include file="pickerColorPicker.jspf" %>
                    </div>
                    <div class="atg_store_addQty">
                      <div class="atg_store_quantity">
                        <%@include file="pickerQuantityAttribute.jspf" %>
                        <%@include file="pickerItemId.jspf" %>
                      </div>
                      <div class="atg_store_productAvailability">
                        <%@include file="pickerAvailabilityMessage.jspf" %>
                        <%@include file="pickerAddToCart.jspf" %>
                      </div>
                    </div>

                  </div>
                </div>

                <div class="atg_store_pickerActions">
                  <dsp:input type="hidden" bean="GiftlistFormHandler.woodfinishPicker" value="true"/>
                  <dsp:include page="pickerActions.jsp">
                    <dsp:param name="comparisonsContainsProduct" param="comparisonsContainsProduct"/>
                    <dsp:param name="showEmailAFriend" param="showEmailAFriend"/>
                    <dsp:param name="showGiftlists" param="showGiftlists"/>
                    <dsp:param name="wishlistContainsSku" param="wishlistContainsSku"/>
                    <dsp:param name="giftlists" param="giftlists"/>
                    <dsp:param name="giftlistsContainingSku" param="giftlistsContainingSku"/>
                  </dsp:include>
                </div>
              </dsp:form>
              <%@include file="pickerRefreshForm.jspf" %>
            </div>
          </dsp:oparam>
        </dsp:droplet>
      </dsp:oparam>
    </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/pickerWoodFinishContents.jsp#2 $$Change: 788278 $--%>

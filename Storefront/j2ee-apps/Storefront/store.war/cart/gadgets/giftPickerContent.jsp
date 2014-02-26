<%--
  This gadget renders a JavaScript-enabled version of color/size picker for the gift item.
  It displays a set of buttons to select appropriate color and size (that is appropriate SKU).
  
  Required parameters:
    productId
      Specifies a currently viewed product.      
    gwpRadioId
      The radio button associated with the color/size picker.
      
  Optional parameters:
    selectedColor
      Currently selected color.
    selectedSize
      Currently selected size.
--%>

<dsp:page>
  <dsp:getvalueof id="productId" param="productId"/>
  <dsp:getvalueof id="gwpRadioId" param="gwpRadioId"/>

  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/store/droplet/CatalogItemFilterDroplet"/>
  <dsp:importbean bean="/atg/store/droplet/ColorSizeDroplet"/>
  <dsp:importbean bean="/atg/store/droplet/WoodFinishDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/userprofiling/PropertyManager"/>
  <dsp:importbean bean="/atg/store/profile/SessionBean"/>

  <dsp:getvalueof var="contextRoot" vartype="java.lang.String"  bean="/OriginatingRequest.contextPath"/>

  <%--
    The ProductLookup droplet looks for a RepositoryItem by its id from within a
    Repository. If the item is found, it will check whether the item belongs 
    to the user's catalog in his current Profile and if the item belongs to the 
    current site.

    Required Parameters:
      id
        The id of the item to lookup.
    Open Parameters:
      output
        If the item is found.
      empty  
        If the item is not found.
    
    Output Parameters:
      element
        Set to the RepositoryItem corresponding to the id supplied.
  --%>
  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" param="productId"/>
    <dsp:param name="filterBySite" value="false"/>
    <dsp:param name="filterByCatalog" value="false"/>
    <dsp:oparam name="output">
      <dsp:setvalue param="product" paramvalue="element"/>

      <dsp:getvalueof var="productTemplateURL" vartype="java.lang.String" param="product.template.url"/>
      <dsp:getvalueof var="errorURL" vartype="java.lang.String"
                      value="${originatingRequest.contextPath}${productTemplateURL}?productId=${productId}&categoryId=${categoryId}"/>
      <dsp:getvalueof var="skus" param="product.childSKUs" />
      <dsp:getvalueof var="skulength" value="${fn:length(skus)}" />
      
      <%-- Determine type of picker to display proper selector --%>
      <dsp:getvalueof var="skuType" param="product.childSKUs[0].type"/>
      
      <c:choose>
      
        <%-- Clothing SKU, display color/size picker --%>
        <c:when test="${skuType == 'clothing-sku'}">
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

            Open parameters:
              output
                Always rendered.
          --%>
          <dsp:droplet name="ColorSizeDroplet">
            <dsp:param name="skus" param="product.childSKUs"/>
            <dsp:param name="selectedColor" param="selectedColor"/>
            <dsp:param name="selectedSize" param="selectedSize"/>
            <dsp:param name="product" param="product"/>
            <dsp:oparam name="output">
            
              <%-- Signal, that we're displaying a clothing-sku. Will be used later. --%>
              <dsp:param name="skuType" value="clothing"/>
              
              <dsp:include page="/cart/gadgets/giftPickerForms.jsp">
                <dsp:param name="productId" value="${productId}"/>
                <dsp:param name="requestURI" value="${originatingRequest.requestURI}"/>
                <dsp:param name="contextPath" value="${pageContext.request.contextPath}"/>
                <dsp:param name="colors" param="availableColors"/>
                <dsp:param name="sizes" param="availableSizes"/>
                <dsp:param name="selectedColor" param="selectedColor"/> 
                <dsp:param name="selectedSize" param="selectedSize"/>
                <dsp:param name="colorIsSelected" param="colorIsSelected"/>
                <dsp:param name="skuType" param="skuType"/>
                <dsp:param name="gwpRadioId" param="gwpRadioId"/>
              </dsp:include>
           </dsp:oparam>
        </dsp:droplet><%-- ColorSizeDroplet --%>
        
        </c:when>
        
        <%-- Furniture SKU, display wood finish picker --%>
        <c:when test="${skuType == 'furniture-sku'}">
        
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
                  
            Open parameters:
              output
                Always rendered.
          --%>
          <dsp:droplet name="WoodFinishDroplet">
            <dsp:param name="skus" param="product.childSKUs"/>
            <dsp:param name="selectedColor" param="selectedColor"/>
            <dsp:param name="product" param="product"/>

            <dsp:oparam name="output">
              <dsp:param name="skuType" value="furniture"/>
                     
              <dsp:include page="/cart/gadgets/giftPickerForms.jsp">
                <dsp:param name="productId" value="${productId}"/>
                <dsp:param name="requestURI" value="${originatingRequest.requestURI}"/>
                <dsp:param name="contextPath" value="${pageContext.request.contextPath}"/>
                <dsp:param name="colors" param="availableColors"/>
                <dsp:param name="sizes" param="availableSizes"/>
                <dsp:param name="selectedColor" param="selectedColor"/>
                <dsp:param name="selectedSize" value=""/>
                <dsp:param name="colorIsSelected" param="colorIsSelected"/>
                <dsp:param name="skuType" param="skuType"/>
                <dsp:param name="gwpRadioId" param="gwpRadioId"/>
              </dsp:include>          
            </dsp:oparam>
          </dsp:droplet>
        </c:when>
        
        <%-- Regular SKU without picker --%>
        <c:otherwise>
          
        </c:otherwise>
      </c:choose> 
    </dsp:oparam>
  </dsp:droplet><%-- ProductLookup --%>   
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/gadgets/giftPickerContent.jsp#2 $$Change: 788278 $ --%>

<%-- 
  This page is a container for products which 
  SKUs are of type wood finish

  Required parameters
    productId
      the product id being displayed

  Optional parameters      
    categoryId
      the id of the category the product is viewed from
--%>
<dsp:page>
  <dsp:getvalueof id="productId" param="productId"/>
  <dsp:getvalueof id="categoryId" param="categoryId"/>

  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/store/droplet/CatalogItemFilterDroplet"/>
  <dsp:importbean bean="/atg/store/droplet/WoodFinishDroplet"/>
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

  <%-- 
    because this page is called directly using ajax, it must look up the product
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

      <%-- Filter out the skus --%>
      <dsp:droplet name="CatalogItemFilterDroplet">
        <dsp:param name="collection" param="product.childSKUs"/>
        <dsp:oparam name="output">

          <%-- 
            in case of non-JS browser we display special
            dropdown which renders list of available SKUs
          --%>
          <dsp:include page="gadgets/noJsWoodFinishPickerLayout.jsp">
            <dsp:param name="product" param="product"/>
            <dsp:param name="categoryId" param="categoryId"/>
            <dsp:param name="skus" param="filteredCollection"/>
          </dsp:include>

          <dsp:include page="gadgets/pickerWoodFinishContents.jsp">
            <dsp:param name="productId" param="product.repositoryId"/>
            <dsp:param name="categoryId" param="categoryId"/>
          </dsp:include>
        </dsp:oparam>
      </dsp:droplet><%-- CatalogItemFilterDroplet --%>
    </dsp:oparam>
  </dsp:droplet><%-- ProductLookup --%>   

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/pickerWoodFinishContainer.jsp#2 $$Change: 788278 $--%>

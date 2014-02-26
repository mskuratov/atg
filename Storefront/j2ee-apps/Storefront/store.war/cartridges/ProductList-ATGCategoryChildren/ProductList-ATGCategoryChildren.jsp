<%-- 
  This page lays out the elements that make up the search results page.
    
  Required Parameters:
    contentItem
      The content item - results list type 
   
  Optional Parameters:
    shareableTypeId
      The Id of site group to use when look up for category products.
    sort
      The property name to sort products by.
    p
      The page number.    
    viewAll
      The boolean indicating whether all products should be displayed on the page ignoring the products per page
      setting.
--%>
<dsp:page>
  
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>

  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/>
  
  <dsp:include page="/browse/gadgets/categoryChildProductsLookup.jsp">
    <dsp:param name="contentItem" value="${contentItem}"/>
    <dsp:param name="productsRenderer" value="/browse/gadgets/categoryChildProductsRenderer.jsp"/>
    <dsp:param name="shareableTypeId" param="shareableTypeId"/>
    <dsp:param name="sort" param="sort"/>
    <dsp:param name="p" param="p"/>
    <dsp:param name="viewAll" param="viewAll"/>
  </dsp:include>
 
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cartridges/ProductList-ATGCategoryChildren/ProductList-ATGCategoryChildren.jsp#2 $$Change: 768606 $--%>

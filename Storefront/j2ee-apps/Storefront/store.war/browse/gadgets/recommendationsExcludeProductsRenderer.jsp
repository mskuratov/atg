<%-- 
  This gadget renders category child products that need to be excluded from recommendations. Each products
  is rendered in the format expected by the recommendations <DIV> container: <dt>prod1</dt>.
  
  Required Parameters:
    
    products
      The list of child products to display.
    sortSelection
      The property name to sort products by.
    howMany
      The number of products to display.
    start
      The starting index to display products from.
   .  
   
  Optional Parameters:
    contentItem
      The category child products content item.   
    viewAll
      The boolean indicating whether all products should be displayed on the page ignoring the products per page
      setting.
    p
      The page number
--%>

<dsp:page>

  <dsp:importbean bean="/atg/store/sort/RangeSortDroplet" />
  <dsp:getvalueof var="originatingRequestURL" bean="/OriginatingRequest.requestURI"/>
  

  <dsp:droplet name="RangeSortDroplet">
    <dsp:param name="array" param="products"/>
    <dsp:param name="sortSelection" param="sortSelection"/>
    <dsp:param name="howMany" param="howMany"/>
    <dsp:param name="start" param="start"/>
    
    <%-- Product repository item is output --%>
    <dsp:oparam name="output">
      <dt><dsp:valueof param="element.repositoryId"/></dt>
    </dsp:oparam>
  </dsp:droplet>
    
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/recommendationsExcludeProductsRenderer.jsp#1 $$Change: 742374 $ --%>
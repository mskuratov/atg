<%--
  This page renders layout for products with single SKU.
  
  Required parameters:
    productId
      Product ID to display.
  
  Optional parameters:
    categoryId
      Category ID from which product is browsed.
    initialQuantity
      The quantity of items to prepopulate quantity input field with.
    categoryNavIds
      The colon separated list representing the category navigation trail
    categoryNav
      Determines if breadcrumbs are updated to reflect category navigation trail on click 
      through.
    navAction
      Type of breadcrumb navigation used to reach this page for tracking.
      Valid values are push, pop, or jump. Default is jump.
    navCount
      Current navigation count used to track for the use of the back button. Default is 0.
 --%>
<dsp:page>
  <dsp:include page="gadgets/productLookupForDisplay.jsp">
    <dsp:param name="productId" param="productId" />
    <dsp:param name="colorSizePicker" param="colorSizePicker" />
    <dsp:param name="categoryId" param="categoryId" />
    <dsp:param name="initialQuantity" param="initialQuantity" />
    <dsp:param name="container" value="/browse/productDetailSingleSkuContainer.jsp" />
    <dsp:param name="categoryNavIds" param="categoryNavIds"/>
    <dsp:param name="categoryNav" param="categoryNav"/>
    <dsp:param name="navAction" param="navAction" />
    <dsp:param name="navCount" param="navCount" />
  </dsp:include>
  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/productDetailSingleSku.jsp#1 $$Change: 735822 $ --%>
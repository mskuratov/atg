<%-- 
  Product detail picker for products with woodFinish SKUs
  
  Required parameters:
    productId
      the id of the product whose details are shown
      
  Optional parameters:      
    categoryId
      the id of the category the product is viewed from
    tabname
      the name of a more details tab to display
    initialQuantity
      sets the initial quantity to preset in the form
    categoryNavIds
      ':' separated list representing the category navigation trail
    categoryNav
      Determines if breadcrumbs are updated to reflect 
      category navigation trail on click through
    navAction
      type of breadcrumb navigation used to reach this page for tracking.
      Valid values are push, pop, or jump. Default is jump.
    navCount
      current navigation count is used to track for the use of the back button
  --%>
<dsp:page>
  <dsp:include page="productDetail.jsp">
    <dsp:param name="productId" param="productId"/>
    <dsp:param name="picker" value="pickerWoodFinishContainer.jsp"/>
    <dsp:param name="categoryId" param="categoryId"/>
    <dsp:param name="tabname" param="tabname"/>
    <dsp:param name="initialQuantity" param="initialQuantity"/>
    <dsp:param name="categoryNavIds" param="categoryNavIds"/>
    <dsp:param name="categoryNav" param="categoryNav"/>
    <dsp:param name="navAction" param="navAction" />
    <dsp:param name="navCount" param="navCount" />
  </dsp:include>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/productDetailWoodFinishPicker.jsp#1 $$Change: 735822 $ --%>

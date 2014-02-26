<%--
  This page displays product details with its image and all its parameters. It also displays a picker to enable the user
  to select appropriate child SKU. The user can add selected SKU to cart or gift/wish list or to comparisons list.

  Required parameters:
    productId
      Specifies the product to be displayed by its ID.
    picker
      Specifies a picker page to be rendered.

  Optional parameters:
    categoryId
      Specifies a category the product is viewed from.
    categoryNavIds
      A colon-separated list of category navigation trail.
    categoryNav
      Flags if breadcrumbs should be updated to reflect category navigation trail.
    navAction
      Type of breadcrumb navigation used to reach this page. Valud values are 'push', 'pop' and 'jump'.
--%>

<dsp:page>
  <%-- Find and display product. --%>
  <dsp:include page="gadgets/productLookupForDisplay.jsp">
    <dsp:param name="productId" param="productId" />
    <dsp:param name="picker" param="picker" />
    <dsp:param name="categoryId" param="categoryId" />
    <dsp:param name="tabname" param="tabname" />
    <dsp:param name="initialQuantity" param="initialQuantity" />
    <%-- Pass page name that will render product details. --%>
    <dsp:param name="container" value="/browse/productDetailDisplay.jsp" />
    <dsp:param name="categoryNavIds" param="categoryNavIds"/>
    <dsp:param name="categoryNav" param="categoryNav"/>
    <dsp:param name="navAction" param="navAction" />
    <dsp:param name="navCount" param="navCount" />
  </dsp:include>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/productDetail.jsp#1 $$Change: 735822 $ --%>

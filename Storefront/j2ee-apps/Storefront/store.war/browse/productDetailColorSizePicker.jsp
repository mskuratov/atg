<%--
  This page displays product details with its image. It also displays a color/size picker to enable the user
  to select appropriate child SKU. The user can add selected SKU to cart or gift/wish list or to comparisons list.

  Required parameters:
    productId
      Specifies the product to be displayed by its ID.

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
  <%-- Include all necessary content. --%>
  <dsp:include page="productDetail.jsp">
    <dsp:param name="productId" param="productId"/>
    <%-- Specify proper picker for this page. --%>
    <dsp:param name="picker" value="pickerColorSizeContainer.jsp"/>
    <dsp:param name="categoryId" param="categoryId"/>
    <dsp:param name="tabname" param="tabname"/>
    <dsp:param name="initialQuantity" param="initialQuantity"/>
    <dsp:param name="categoryNavIds" param="categoryNavIds"/>
    <dsp:param name="categoryNav" param="categoryNav"/>
    <dsp:param name="navAction" param="navAction" />
    <dsp:param name="navCount" param="navCount" />
  </dsp:include>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/productDetailColorSizePicker.jsp#1 $$Change: 735822 $ --%>

<%--
  This gadget renders both JS and non-JS versions of color/size picker.

  Required parameters:
    product
      Currentrly viewed product.
    skus
      Collection of product's SKUs.

  Optional parameters:
    categoryId
      ID of category product is viewed from.
--%>

<dsp:page>
  <%-- Include non-JS picker version. --%>
  <dsp:include page="gadgets/noJsPickerLayout.jsp">
    <dsp:param name="product" param="product"/>
    <dsp:param name="categoryId" param="categoryId"/>
    <dsp:param name="skus" param="filteredCollection"/>
  </dsp:include>

  <%-- Include JS picker version. --%>
  <dsp:include page="gadgets/pickerContents.jsp">
    <dsp:param name="productId" param="product.repositoryId"/>
    <dsp:param name="categoryId" param="categoryId"/>
  </dsp:include>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/pickerColorSizeContainer.jsp#1 $$Change: 735822 $--%>

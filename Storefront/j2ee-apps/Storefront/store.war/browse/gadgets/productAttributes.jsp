<%--
  This gadget displays product attributes (like description) on the product details page.

  Required parameters:
    product
      Specifies a product whose parameters should be displayed.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:getvalueof id="product" param="product"/>
  <dsp:getvalueof id="categoryId" param="categoryId"/>

  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>

  <dsp:getvalueof id="product" param="product" />
  <dsp:getvalueof id="categoryId" param="categoryId" /> 

  <%-- Display 'As seen in' link if necessary. --%>
  <dsp:include page="productAsSeenIn.jsp">
    <dsp:param name="product" param="product"/>
  </dsp:include>

  <%-- Product metadata description. --%>
  <dsp:include page="/browse/gadgets/moreDetails.jsp">
    <dsp:param name="product" param="product"/>
  </dsp:include>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/productAttributes.jsp#1 $$Change: 735822 $--%>

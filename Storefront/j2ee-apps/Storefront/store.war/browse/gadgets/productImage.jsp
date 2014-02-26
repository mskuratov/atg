<%--
  This gadget displays a small product image, if exists. On click it displays large image.

  Required parameters:
    product
      Specifies a product an image should be taken from.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:getvalueof id="product" param="product"/>
  <dsp:getvalueof  id="largeImageURL" idtype="java.lang.String" param="product.largeImage.url" />

  <%-- Display the image only if it exists. --%>
  <c:if test="${!empty largeImageURL}">
    <%-- Collect large image URL and alt text to be displayed. --%>
    <dsp:getvalueof var="altText" vartype="java.lang.String" param="product.displayName" />
    <c:set var="altText"><c:out value="${altText}" escapeXml="true"/></c:set>
    <dsp:getvalueof  id="fullImageURL" idtype="java.lang.String" param="product.fullImage.url" />

    <%-- Image itself. --%>
    <a href="${fullImageURL}" dojoType="dojox.image.Lightbox" title="${altText}">
      <dsp:img src="${largeImageURL}"  alt="${altText}" /> 
    </a>

    <%-- Display large image link. --%>
    <a class="atg_store_largerImage" href="${fullImageURL}" dojoType="dojox.image.Lightbox" title="${altText}">
      <fmt:message key="browse_productAction.largerImageLink"/>
    </a>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/productImage.jsp#1 $$Change: 735822 $ --%>

<%--
  This gadgets is intended to be a universal one for rendering a list of products in a slider. The including
  page will only have to provide a list of records and the page will know how to read them based on their
  class name.

  It is currently used in different pages in the application to render different types of records. For
  example it renders Related Products(GSAItem), Recently Viewed products (GSAItem), Featured Items
  specified by a targeter (Entry), items specified in a HorizontalSpotlight cartridge(Record), and items
  specified in a HorizontalResultList cartridge (Record).

  Required parameters:
    products
      List of products to render.

  Optional parameters:
    index
      The index of the slider. This is only needed if there are more than one slider on the page.
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>

  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="products" param="products"/>

  <dsp:getvalueof var="index" param="index"/>
  <c:if test="${empty index}">
    <c:set var="index" value="1"/>
  </c:if>

  <div class="itemsContainer">
    <div id="horizontalContainer${index}">
      <c:forEach var="record" items="${products}">
        <div class="cell">
          <c:choose>
            <c:when test="${record.class.name == 'atg.adapter.gsa.GSAItem' || record.class.name == 'java.util.HashMap$Entry'}">
              <c:choose>
                <c:when test="${record.class.name == 'java.util.HashMap$Entry'}">
                  <dsp:param name="product" value="${record.value}"/>
                </c:when>
                <c:otherwise>
                  <dsp:param name="product" value="${record}"/>
                  <c:if test="${fn:startsWith(record, 'recentlyViewed')}">
                    <dsp:param name="product" value="${record.product}"/>
                  </c:if>
                </c:otherwise>
              </c:choose>

              <dsp:getvalueof var="productImageUrl" param="product.smallImage.url"/>
              <dsp:getvalueof var="productName" param="product.displayName"/>

              <%-- Generates URL for the product, the URL is stored in the "productUrl" request-scoped variable --%>
              <dsp:include page="${mobileStorePrefix}/global/gadgets/productLinkGenerator.jsp">
                <dsp:param name="product" param="product"/>
              </dsp:include>
            </c:when>
            <c:when test="${record.class.name == 'com.endeca.infront.cartridge.model.Record'}">
              <%-- Save the product's repository id so it can be used later to lookup the product from the database --%>
              <c:set var="productId" value="${record.attributes['product.repositoryId']}"/>

              <%--
                Get the product item using the repository id which we fetched earlier.

                Input Parameters:
                  id
                    The ID of the product we want to look up.

                Open Parameters:
                  output
                    Serviced when no errors occur.

                Output Parameters:
                  element
                    The product whose ID matches the 'id' input parameter.
              --%>
              <dsp:droplet name="ProductLookup">
                <dsp:param name="id" value="${productId}"/>
                <dsp:param name="filterBySite" value="false"/>
                <dsp:param name="filterByCatalog" value="false"/>

                <dsp:oparam name="output">
                  <dsp:setvalue param="product" paramvalue="element"/>
                  <dsp:getvalueof var="templateUrl" param="product.template.url"/>

                  <%-- Display only products with properly configured template --%>
                  <c:if test="${not empty templateUrl}">
                    <dsp:getvalueof var="productImageUrl" param="product.smallImage.url"/>
                    <dsp:getvalueof var="productName" param="product.displayName"/>

                    <%-- Generate the product's URL. The value will be stored in the "productUrl" request-scoped variable --%>
                    <dsp:include page="${mobileStorePrefix}/global/gadgets/productLinkGenerator.jsp">
                      <dsp:param name="product" param="product"/>
                    </dsp:include>
                  </c:if>
                </dsp:oparam>
              </dsp:droplet>
            </c:when>
          </c:choose>

          <%-- Ensure there is an image for the product --%>
          <c:if test="${empty productImageUrl}">
            <c:set var="productImageUrl" value="/crsdocroot/content/images/products/small/MissingProduct_small.jpg"/>
          </c:if>

          <%-- Display the clickable image --%>
          <a href="${fn:escapeXml(productUrl)}">
            <img src="${productImageUrl}" alt="${productName}" class="cellImage" />
          </a>
        </div>
      </c:forEach>
    </div>
  </div>

  <script>
    $(document).ready(function() {
      CRSMA.global.initHorizontalListSliders();
    });
  </script>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/global/gadgets/productsHorizontalList.jsp#3 $$Change: 792961 $--%>

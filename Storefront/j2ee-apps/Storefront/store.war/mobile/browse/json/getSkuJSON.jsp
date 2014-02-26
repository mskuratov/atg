<%--
  Renders appropriate JSON for a product SKUs.

  Page includes:
    /global/gadgets/formattedPrice.jsp - Price formatter

  Required Parameters:
    product
      Product whose SKUs to iterate
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/droplet/SkuAvailabilityLookup"/>
  <dsp:importbean bean="/atg/store/droplet/CatalogItemFilterDroplet"/>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <%-- Sort SKUs into maps based on availability --%>
  <dsp:droplet name="CatalogItemFilterDroplet">
    <dsp:param name="collection" param="product.childSKUs"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="skus" param="filteredCollection"/>

      <c:set var="jsonArray">
        <json:array>
          <c:forEach items="${skus}" var="currentSku">
            <%--
              Construct the key that will be used to identify this SKU by enumerating the known SKU types.
              Order is important for keys based on multiple properties;
              these keys will be derived in by 'checkForSelectedSku' in "product.js",
              so if they are not specified in the same order everywhere then picker selection will not work properly
            --%>
            <c:set var="mapKey">
              <c:choose>
                <c:when test="${currentSku.type == 'clothing-sku'}">
                  <c:if test="${not empty currentSku.color && not empty currentSku.size}">${currentSku.color}:${currentSku.size}</c:if>
                  <c:if test="${not empty currentSku.color &&     empty currentSku.size}">${currentSku.color}</c:if>
                  <c:if test="${    empty currentSku.color && not empty currentSku.size}">${currentSku.size}</c:if>
                </c:when>
                <c:when test="${currentSku.type == 'furniture-sku'}">${currentSku.woodFinish}</c:when>
                <c:otherwise>${currentSku.repositoryId}</c:otherwise>
              </c:choose>
            </c:set>

            <%-- Get the SKU price(s) --%>
            <dsp:droplet name="PriceDroplet">
              <dsp:param name="product" param="product"/>
              <dsp:param name="sku" value="${currentSku}"/>
              <dsp:oparam name="output">
                <dsp:setvalue param="theListPrice" paramvalue="price"/>
                <dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
                <c:choose>
                  <c:when test="${not empty profileSalePriceList}">
                    <dsp:droplet name="PriceDroplet">
                      <dsp:param name="priceList" bean="Profile.salePriceList"/>
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
                        <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
                        <c:set var="productPrice">
                          <dsp:include page="/global/gadgets/formattedPrice.jsp">
                            <dsp:param name="price" value="${listPrice}"/>
                          </dsp:include>
                        </c:set>
                        <c:set var="salePrice">
                          <dsp:include page="/global/gadgets/formattedPrice.jsp">
                            <dsp:param name="price" value="${price}"/>
                          </dsp:include>
                        </c:set>
                      </dsp:oparam>
                      <dsp:oparam name="empty">
                        <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
                        <c:set var="productPrice">
                          <dsp:include page="/global/gadgets/formattedPrice.jsp">
                            <dsp:param name="price" value="${price}"/>
                          </dsp:include>
                        </c:set>
                      </dsp:oparam>
                    </dsp:droplet><%-- End price droplet on sale price --%>
                  </c:when>
                  <c:otherwise>
                    <c:set var="productPrice">
                      <dsp:include page="/global/gadgets/formattedPrice.jsp">
                        <dsp:param name="price" value="${price}"/>
                      </dsp:include>
                    </c:set>
                  </c:otherwise>
                </c:choose><%-- End Is Empty Check --%>
              </dsp:oparam>
            </dsp:droplet>
            
            <%-- Get the SKU status --%>
            <dsp:droplet name="SkuAvailabilityLookup">
              <dsp:param name="product" param="product"/>
              <dsp:param name="skuId" value="${currentSku.repositoryId}"/>
              <dsp:oparam name="available">
                <c:set var="skuStatus" value="available"/>
              </dsp:oparam>
              <dsp:oparam name="preorderable">
                <c:set var="skuStatus" value="preorder"/>
              </dsp:oparam>
              <dsp:oparam name="backorderable">
                <c:set var="skuStatus" value="backorder"/>
              </dsp:oparam>
              <dsp:oparam name="unavailable">
                <c:set var="skuStatus" value="outofstock"/>
              </dsp:oparam>
            </dsp:droplet>

            <%-- JSON body for SKU --%>
            <json:object>
              <json:property name="key">${mapKey}</json:property>
              <json:object name="value">
                <json:property name="salePrice" value="${salePrice}"/>
                <json:property name="productPrice" value="${productPrice}"/>
                <json:property name="skuId" value="${currentSku.repositoryId}"/>
                <json:property name="status" value="${skuStatus}"/>
              </json:object>
            </json:object>
          </c:forEach>
        </json:array>
      </c:set>

      <script>
        CRSMA.product.registerSKUs(${jsonArray});
      </script>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/json/getSkuJSON.jsp#4 $$Change: 788278 $--%>

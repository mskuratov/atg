<%--
  Renders promotional product details JSON, as follows:
  {
    "name" : <Product name>,
    "imageUrl" : <Product image URL>,
    "linkUrl" : <Link to product details>,
    "prices" : {
      "salePrice" : <Product sale price>,
      "listPrice" : <Product list price>
    }
  }

  Page includes:
    /global/gadgets/formattedPrice.jsp - Price formatter
    /global/gadgets/crossSiteLinkGenerator.jsp - Generator of cross-site links

  Required parameters:
    product
      Product to be displayed
    productParams
      Product parameters
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/multisite/SiteIdForCatalogItem"/>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/GetSiteDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="productParams" param="productParams"/>
  <dsp:getvalueof var="product" param="product"/>

  <c:set var="product" value="${productParams.element}"/>
  <dsp:getvalueof var="mobileStorePrefix" bean="/atg/store/StoreConfiguration.mobileStorePrefix"/>

  <json:object>
    <json:property name="name" value="${product.displayName}"/>

    <dsp:getvalueof var="productImageUrl" value="${product.thumbnailImage.url}"/>
    <json:property name="imageUrl">
      <c:choose>
        <c:when test="${not empty productImageUrl}">${productImageUrl}</c:when>
        <c:otherwise>/crsdocroot/content/images/products/thumb/MissingProduct_thumb.jpg</c:otherwise>
      </c:choose>
    </json:property>

    <dsp:droplet name="SiteIdForCatalogItem" item="${product}" var="siteIdForCatalogItem">
      <dsp:param name="currentSiteFirst" value="true"/>
      <%-- Search through the mobile (!) sites which belong to "crs.MobileSite" --%>
      <dsp:param name="shareableTypeId" value="crs.MobileSite"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="siteId" value="${siteIdForCatalogItem.siteId}"/>
      </dsp:oparam>
    </dsp:droplet>
    <c:set var="prefixForMobileSite" value=""/>
    <c:if test="${not empty siteId}">
      <dsp:droplet name="GetSiteDroplet">
        <dsp:param name="siteId" value="${siteId}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="site" param="site"/>
          <c:if test="${site.channel == 'mobile'}">
            <c:set var="prefixForMobileSite" value="${mobileStorePrefix}"/>
          </c:if>
        </dsp:oparam>
      </dsp:droplet>
    </c:if>
    <dsp:include page="/global/gadgets/crossSiteLinkGenerator.jsp">
      <dsp:param name="product" param="product"/>
      <dsp:param name="customUrl" value="${prefixForMobileSite}${product.template.url}"/>
    </dsp:include>
    <json:property name="linkUrl">
      <c:url value="${siteLinkUrl}" context="/">
        <c:param name="productId" value="${product.repositoryId}"/>
      </c:url>
    </json:property>

    <dsp:droplet name="PriceDroplet">
      <dsp:param name="product" param="product"/>
      <dsp:param name="sku" param="product.childSKUs[0]"/>
      <dsp:oparam name="output">
        <dsp:setvalue param="theListPrice" paramvalue="price"/>
        <dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="theListPrice.listPrice"/>
        <dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
        <%-- Search for the sale price, if sale price list is defined for the current user --%>
        <c:choose>
          <c:when test="${not empty profileSalePriceList}">
            <%-- Lookup the sale price --%>
            <dsp:droplet name="PriceDroplet">
              <dsp:param name="priceList" value="${profileSalePriceList}"/>
              <dsp:oparam name="output">
                <%-- Sale price found, display both list and sale prices --%>
                <dsp:getvalueof var="salePrice" vartype="java.lang.Double" param="price.listPrice"/>
                <json:object name="prices">
                  <json:property name="salePrice">
                    <dsp:include page="/global/gadgets/formattedPrice.jsp">
                      <dsp:param name="price" value="${salePrice}"/>
                    </dsp:include>
                  </json:property>
                  <json:property name="listPrice">
                    <dsp:include page="/global/gadgets/formattedPrice.jsp">
                      <dsp:param name="price" value="${listPrice}"/>
                    </dsp:include>
                  </json:property>
                </json:object>
              </dsp:oparam>
              <dsp:oparam name="empty">
                <%-- Can't find sale price, display list price only --%>
                <json:object name="prices">
                  <json:property name="listPrice">
                    <dsp:include page="/global/gadgets/formattedPrice.jsp">
                      <dsp:param name="price" value="${listPrice}"/>
                    </dsp:include>
                  </json:property>
                </json:object>
              </dsp:oparam>
            </dsp:droplet>
          </c:when>
          <c:otherwise>
            <%-- No sale price list defined for the current user, display list price only --%>
            <json:object name="prices">
              <json:property name="listPrice">
                <dsp:include page="/global/gadgets/formattedPrice.jsp">
                  <dsp:param name="price" value="${listPrice}"/>
                </dsp:include>
              </json:property>
            </json:object>
          </c:otherwise>
        </c:choose>
      </dsp:oparam>
    </dsp:droplet>
  </json:object>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/promo/gadgets/promotionalProductsJSON.jsp#2 $$Change: 742374 $--%>

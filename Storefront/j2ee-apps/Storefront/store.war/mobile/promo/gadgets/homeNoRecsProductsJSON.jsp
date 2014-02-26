<%--
  Returns products JSON, if "Recommendations" is NOT installed, as follows:
  {
    "targeterResults" : [{
      "products" : [
        <Featured product #1 JSON object>,
        <Featured product #2 JSON object>,
        <Featured product #3 JSON object>
      ]
    }]
  }

  Page includes:
    /mobile/promo/gadgets/promotionalProductsJSON - Renderer of product details JSON

  Required parameters:
    None

  Optional parameters:
    None
--%>
<%@page contentType="application/json"%>
<%@page trimDirectiveWhitespaces="true"%>

<dsp:page>
  <dsp:importbean bean="/atg/targeting/TargetingRandom"/>

  <dsp:getvalueof var="mobileStorePrefix" bean="/atg/store/StoreConfiguration.mobileStorePrefix" scope="request"/>

  <c:set var="atg.taglib.json.escapeXml" value="${false}" scope="page"/>

  <json:object>
    <json:array name="targeterResults">
      <json:object>
        <json:array name="products">
          <%-- Promotional item/products #1 --%>
          <dsp:droplet name="TargetingRandom" var="myParams">
            <dsp:param bean="/atg/registry/Slots/FeaturedProduct1" name="targeter"/>
            <dsp:param name="fireViewItemEvent" value="false"/>
            <dsp:param name="howMany" value="1"/>
            <dsp:oparam name="output">
              <dsp:include page="${mobileStorePrefix}/promo/gadgets/promotionalProductsJSON.jsp">
                <dsp:param name="product" param="element"/>
                <dsp:param name="productParams" converter="map" value="${myParams}"/>
              </dsp:include>
            </dsp:oparam>
          </dsp:droplet>
          <%-- Promotional item/products #2 --%>
          <dsp:droplet name="TargetingRandom" var="myParams">
            <dsp:param bean="/atg/registry/Slots/FeaturedProduct2" name="targeter"/>
            <dsp:param name="fireViewItemEvent" value="false"/>
            <dsp:param name="howMany" value="1"/>
            <dsp:oparam name="output">
              <dsp:include page="${mobileStorePrefix}/promo/gadgets/promotionalProductsJSON.jsp">
                <dsp:param name="product" param="element"/>
                <dsp:param name="productParams" converter="map" value="${myParams}"/>
              </dsp:include>
            </dsp:oparam>
          </dsp:droplet>
          <%-- Promotional item/products #3 --%>
          <dsp:droplet name="TargetingRandom" var="myParams">
            <dsp:param bean="/atg/registry/Slots/FeaturedProduct3" name="targeter"/>
            <dsp:param name="fireViewItemEvent" value="false"/>
            <dsp:param name="howMany" value="1"/>
            <dsp:oparam name="output">
              <dsp:include page="${mobileStorePrefix}/promo/gadgets/promotionalProductsJSON.jsp">
                <dsp:param name="product" param="element"/>
                <dsp:param name="productParams" converter="map" value="${myParams}"/>
              </dsp:include>
            </dsp:oparam>
          </dsp:droplet>
        </json:array>
      </json:object>
    </json:array>
  </json:object>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/promo/gadgets/homeNoRecsProductsJSON.jsp#2 $$Change: 742374 $--%>

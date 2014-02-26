<%--
  Return promotional products details JSON from the targeter, as follows:
  {
    "targeterResults" : [
      {
        "targeter" : <Targeter #1 path>,
        "products" : [
          <Promotional product #1.1 JSON object>,
          ...
          <Promotional product #1.N JSON object>
        ]
      },
      {
        "targeter" : <Targeter #2 path>,
        "products" : [
          <Promotional product #2.1 JSON object>,
          ...
          <Promotional product #2.N JSON object>
        ]
      },
      {
        "targeter" : <Targeter #3 path>,
        "products" : [
          <Promotional product #3.1 JSON object>,
          ...
          <Promotional product #3.N JSON object>
        ]
      }
    ]
  }

  Page includes:
    /mobile/promo/gadgets/promotionalTargeterJSON.jsp - Renders promotional products JSON for specific targeter

  Required parameters:
    None

  Optional parameters:
    None
--%>
<%@page contentType="application/json"%>
<%@page trimDirectiveWhitespaces="true"%>

<dsp:page>
  <c:set var="atg.taglib.json.escapeXml" value="${false}" scope="page"/>
  <dsp:getvalueof var="mobileStorePrefix" bean="/atg/store/StoreConfiguration.mobileStorePrefix" scope="request"/>

  <json:object>
    <json:array name="targeterResults">
      <dsp:include page="${mobileStorePrefix}/promo/gadgets/promotionalTargeterJSON.jsp">
        <dsp:param name="targeterPath" value="/atg/registry/RepositoryTargeters/ProductCatalog/MobilePromotionChild1"/>
      </dsp:include>
      <dsp:include page="${mobileStorePrefix}/promo/gadgets/promotionalTargeterJSON.jsp">
        <dsp:param name="targeterPath" value="/atg/registry/RepositoryTargeters/ProductCatalog/MobilePromotionChild2"/>
      </dsp:include>
      <dsp:include page="${mobileStorePrefix}/promo/gadgets/promotionalTargeterJSON.jsp">
        <dsp:param name="targeterPath" value="/atg/registry/RepositoryTargeters/ProductCatalog/MobilePromotionChild3"/>
      </dsp:include>
    </json:array>
  </json:object>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/promo/gadgets/homePromotionalProductsJSON.jsp#2 $$Change: 742374 $--%>

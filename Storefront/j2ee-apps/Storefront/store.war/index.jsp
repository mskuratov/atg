<%--
  It's a welcome page for the CRS application.
  The user will see this page when he first comes to the store, or after he's clicked the store icon on the page.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>
<dsp:page>
  <crs:pageContainer bodyClass="atg_store_pageHome">
    <jsp:attribute name="SEOTagRenderer">
      <dsp:include page="/global/gadgets/metaDetails.jsp" />
    </jsp:attribute>
    <jsp:body>
      <%-- Display big promo banner and all site-specific featured products. --%>
      <dsp:include page="/navigation/gadgets/homePagePromotions.jsp"/>
      <dsp:include page="/promo/gadgets/homeFeaturedProducts.jsp"/>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/index.jsp#1 $$Change: 735822 $ --%>
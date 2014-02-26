<%--
  Home page.

  Page includes:
    /mobile/global/gadgets/loadingWindow.jsp - "Loading..." message box
    /mobile/promo/gadgets/homePromotionalItemRenderer.jsp - Renderer of promotional items
    /mobile/global/util/hasRecsInstalled.jsp - Checks if "Recommendations" is installed
    /mobile/global/util/currencyCode.jsp - Returns ISO 4217 currency code/symbol corresponding to "Price List Locale"

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <fmt:message key="mobile.common.home" var="pageTitle"/>
  <crs:mobilePageContainer titleString="${pageTitle}" displayModal="true">
    <jsp:attribute name="modalContent">
      <%-- "Loading..." message box --%>
      <dsp:include page="${mobileStorePrefix}/global/gadgets/loadingWindow.jsp"/>
    </jsp:attribute>

    <jsp:body>
      <%-- "Promotional items" slot --%>
      <div id="homeTopSlot">
        <div class="shadowTopDown"></div>
        <dsp:include page="${mobileStorePrefix}/promo/gadgets/homePromotionalItemRenderer.jsp"/>
      </div>
      <%-- "Promotional products" slot --%>
      <div id="homeBottomSlot">
        <div class="shadowTopDown"></div>
        <div id="homeBottomSlotContent"></div>
        <div id="homeBottomSlotProductDetails"></div>
      </div>

      <dsp:include page="${mobileStorePrefix}/global/util/hasRecsInstalled.jsp"/>
      <dsp:getvalueof var="recsInstalled" param="recsInstalled"/>
      <dsp:include page="${mobileStorePrefix}/global/util/currencyCode.jsp"/>
      <dsp:getvalueof var="currencyCode" param="currencyCode"/>
      <dsp:getvalueof var="currencySymbol" param="currencySymbol"/>
      <dsp:getvalueof var="currentLocale" vartype="java.lang.String" bean="/atg/dynamo/servlet/RequestLocale.localeString"/>

      <script>
        $(document).ready(function() {
          CRSMA.home.initHomePage("${recsInstalled}", "${siteContextPath}/", "${isLoggedIn}", "${userGender}", "${currencyCode}", "${currencySymbol}", "${currentLocale}");
        });
      </script>
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/index.jsp#3 $$Change: 788278 $ --%>

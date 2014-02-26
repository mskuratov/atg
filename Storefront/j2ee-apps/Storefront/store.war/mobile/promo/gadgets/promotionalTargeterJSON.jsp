<%--
  Renders promotional products JSON for specific targeter (targeterPath), as follows:
  {
    "targeter" : <Targeter path>,
    "products" : [
      <Promotional product #1 JSON object>,
      ...
      <Promotional product #N JSON object>
    ]
  }

  Page includes:
    /mobile/promo/gadgets/promotionalProductsJSON.jsp - Renderer of product details JSON

  Required parameters:
    targeterPath
      Used targeter (full name of Nucleus component)

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/targeting/TargetingForEach"/>

  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="targeter" param="targeterPath"/>

  <dsp:getvalueof var="mobileStorePrefix" bean="/atg/store/StoreConfiguration.mobileStorePrefix" scope="request"/>

  <c:if test="${not empty targeter}">
    <json:object>
      <json:property name="targeter" value="${targeter}"/>
      <json:array name="products">
        <dsp:droplet name="TargetingForEach" var="myParams">
          <dsp:param name="targeter" bean="${targeter}"/>
          <dsp:param name="fireViewItemEvent" value="false"/>
          <dsp:oparam name="output">
            <dsp:include page="${mobileStorePrefix}/promo/gadgets/promotionalProductsJSON.jsp">
              <dsp:param name="product" param="element"/>
              <dsp:param name="productParams" converter="map" value="${myParams}"/>
            </dsp:include>
          </dsp:oparam>
        </dsp:droplet>
      </json:array>
    </json:object>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/promo/gadgets/promotionalTargeterJSON.jsp#2 $$Change: 742374 $--%>

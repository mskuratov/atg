<%--
  This gadget checks to see if "Recommendations" module is installed.

  This page also introduces the following request-scoped variables:
    recsInstalled
      "StoreRecommendationsConfiguration.retailerId" - if the "Recommendations" is installed.
      "" (empty string) - otherwise.

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:droplet name="/atg/dynamo/droplet/ComponentExists">
    <dsp:param name="path" value="/atg/store/recommendations/StoreRecommendationsConfiguration"/>
    <dsp:oparam name="true">
      <dsp:getvalueof var="recsInstalled" bean="/atg/store/recommendations/StoreRecommendationsConfiguration.retailerId" scope="request"/>
    </dsp:oparam>
    <dsp:oparam name="false">
      <dsp:getvalueof var="recsInstalled" value="" scope="request"/>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/global/util/hasRecsInstalled.jsp#2 $$Change: 742374 $--%>

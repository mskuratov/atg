<%--
  This page returns the one product of the random targeter.

  This page introduces the following page-scoped variables:
    product
      Product returned by random targeter

  Required Parameters:
    targeter
      The targeter to use

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/mobile/catalog/CurrentSiteItemFilter"/>
  <dsp:importbean bean="/atg/targeting/TargetingRandom"/>

  <c:set var="product" scope="request" value="${null}"/>
  <dsp:droplet name="TargetingRandom">
    <dsp:param name="howMany" value="1"/>
    <dsp:param name="targeter" param="targeter"/>
    <dsp:param name="fireViewItemEvent" value="false"/>
    <dsp:param name="elementName" value="product"/>
    <dsp:param name="filter" bean="CurrentSiteItemFilter"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="product" param="product"/>
      <c:set var="product" scope="request" value="${product}"/>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cart/gadgets/getTargeterProduct.jsp#2 $$Change: 742374 $ --%>

<%--
  This page initializes the data objects needed to organize the shipping.
  This page introduces the following request-scoped variables:
    permittedAddresses
      All permitted shipping addresses

  Page includes:
    None

  Required parameters:
    None

  Optional parameters:
    init
      Flags, if there should be initialized shipping-related data objects.
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupContainerService"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupDroplet"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
  <dsp:importbean bean="/atg/store/droplet/AvailableShippingGroups"/>

  <%-- Re-initialize shipping data objects, if needed --%>
  <dsp:getvalueof var="init" param="init"/>
  <c:if test="${init == 'true'}">
    <dsp:droplet name="ShippingGroupDroplet">
      <dsp:param name="createOneInfoPerUnit" value="false"/>
      <dsp:param name="clearShippingInfos" param="init"/>
      <dsp:param name="clearShippingGroups" param="init"/>
      <dsp:param name="shippingGroupTypes" value="hardgoodShippingGroup"/>
      <dsp:param name="initShippingGroups" param="init"/>
      <dsp:param name="initBasedOnOrder" param="init"/>
      <dsp:oparam name="output"/>
    </dsp:droplet>
  </c:if>

  <%-- Initialize single-shipping-specific objects and parameters --%>
  <dsp:setvalue bean="ShippingGroupFormHandler.initSingleShippingForm" value=""/>

  <dsp:getvalueof var="shippingGroupMap" bean="ShippingGroupContainerService.shippingGroupMapForDisplay"/>
  <c:if test="${not empty shippingGroupMap}">
    <%--
      Sorts shipping addresses alphabetically by "Address Nickname" and the default address is rendered first.
      Returns an array of permitted shipping addresses.

      Input parameters:
        defaultKey
          The parameter that defines the map key of the default item that should be
          placed in the beginning of the array
        sortByKeys
          Boolean that specifies whether to sort map entries by keys or not
        map
          The parameter that defines the map of items to convert to the sorted array

      Open parameters:
        output
          Rendered for permitted shipping address list, "permittedAddresses"
          parameter contains the permitted shipping addresses
        empty
          Rendered if there are no shipping addresses or there are no permitted shipping addresses

      Output parameters:
        permittedAddresses
          contains the permitted shipping addresses
    --%>
    <dsp:droplet name="AvailableShippingGroups">
      <dsp:param name="map" value="${shippingGroupMap}"/>
      <dsp:param name="defaultKey" bean="ShippingGroupFormHandler.shipToAddressName"/>
      <dsp:param name="sortByKeys" value="true"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="permittedAddresses" param="permittedAddresses" scope="request"/>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/checkout/shippingInitialize.jsp#2 $$Change: 742374 $--%>

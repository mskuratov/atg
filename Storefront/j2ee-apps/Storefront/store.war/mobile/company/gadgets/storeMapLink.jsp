<%-- 
  Generates a link that will pass a store location to a map service.

  Page includes:
    None

  Required parameters:
    store
      The store whose location we want to link

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:getvalueof var="street" param="store.address1"/>
  <dsp:getvalueof var="city" param="store.city"/>
  <dsp:getvalueof var="state" param="store.stateAddress"/>
  <dsp:getvalueof var="postal" param="store.postalCode"/>
  <dsp:getvalueof var="country" param="store.country"/>

  <%-- Set the base URL of the map service --%>
  <c:set var="mapUrl" value="http://www.example.com"/>

  <dsp:a href="${mapUrl}">
    <dsp:param name="q" value="${street}, ${city}, ${state} ${postal} ${country}"/>
    <fmt:message key="mobile.company_storeLocations.map"/>
  </dsp:a>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/company/gadgets/storeMapLink.jsp#2 $$Change: 742374 $ --%>

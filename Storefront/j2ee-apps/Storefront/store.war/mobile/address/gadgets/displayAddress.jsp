<%--
  Renders a stored address. "Nickname" of the address is not displayed here-in.
  If required, it must be rendered just before a include to this JSP.

  Page includes:
    None

  Required parameters:
    address
      "ContactInfo" repository item (address data) to display.
    isPrivate
      Indicator if the details of the "address.address1", "address.address2" should be hidden
      Possible values: [true|false]

  Optional parameters:
--%>
<dsp:page>
  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="isPrivate" param="isPrivate"/>

  <%--
    Condition to determine the CountryCode so that Country-specific Address formats can be rendered.
    Currently only default has been included for US Address formats.
    The condition is used as a place holder for future extensibility when multiple countries will be supported.
  --%>
  <dsp:getvalueof var="addressValue" param="address.country"/>
  <c:if test='${addressValue != ""}'>
    <%-- US Address format --%>
    <div class="vcard">
      <div class="fn">
        <span><dsp:valueof param="address.firstName"/></span>
        <span><dsp:valueof param="address.middleName"/></span>
        <span><dsp:valueof param="address.lastName"/></span>
      </div>

      <div>
        <%-- Display private address details --%>
        <c:if test="${isPrivate == 'false'}">
          <div class="street-address">
            <dsp:valueof param="address.address1"/>
          </div>
          <div class="street-address">
            <dsp:getvalueof var="address2" param="address.address2"/>
            <c:if test="${not empty address2}">
              <dsp:valueof param="address.address2"/>
            </c:if>
          </div>
        </c:if>

        <span class="locality"><dsp:valueof param="address.city"/><fmt:message key="mobile.common.comma"/></span>
        <dsp:getvalueof var="state" param="address.state"/>
        <c:if test="${not empty state}">
          <span class="region"><dsp:valueof param="address.state"/></span>
        </c:if>

        <span class="postal-code"><dsp:valueof param="address.postalCode"/></span>
        <div class="country-name">
          <dsp:droplet name="/atg/store/droplet/CountryListDroplet">
            <dsp:param name="userLocale" bean="/atg/dynamo/servlet/RequestLocale.locale"/>
            <dsp:param name="countryCode" param="address.country"/>
              <dsp:oparam name="false">
              <span class="country-name"><dsp:valueof param="countryDetail.displayName"/></span>
            </dsp:oparam>
          </dsp:droplet>
        </div>
      </div>
      <div><dsp:valueof param="address.phoneNumber"/></div>
    </div>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/address/gadgets/displayAddress.jsp#2 $$Change: 768606 $--%>

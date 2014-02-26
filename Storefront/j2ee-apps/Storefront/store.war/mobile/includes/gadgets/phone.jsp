<%--
  Shows a "Phone" link.

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <fmt:message var="phoneLabel" key="mobile.common.phone"/>
  <a class="contact" href="tel:<fmt:message key='mobile.contactUs.phoneNumber'/>" title="${phoneLabel}">
    <img src="/crsdocroot/content/mobile/images/icon-phoneDial.png"/>
    <span>${phoneLabel}</span>
  </a>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/includes/gadgets/phone.jsp#3 $$Change: 788278 $ --%>

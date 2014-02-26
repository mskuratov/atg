<%--
  Shows an "Email" link.

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <fmt:message var="emailLabel" key="mobile.common.email"/>
  <a class="contact" href="mailto:<fmt:message key='mobile.contactUs.emailAddress'/>" title="${emailLabel}">
    <img src="/crsdocroot/content/mobile/images/icon-email.png"/>
    <span>${emailLabel}</span>
  </a>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/includes/gadgets/email.jsp#3 $$Change: 788278 $ --%>

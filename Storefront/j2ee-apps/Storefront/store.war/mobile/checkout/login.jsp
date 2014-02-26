<%--
  This page includes a gadget that will check if the user is already logged in.
  If so, it redirects to the "shipping.jsp" page.

  Page includes:
    /mobile/global/login.jsp - Login form renderer

  Required parameters:
    None

  Optional parameters:
    passwordSent
      If returning from the "passwordReset.jsp", set this variable to true
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>

  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="passwordSent" param="passwordSent"/>

  <dsp:getvalueof var="mobileStorePrefix" bean="/atg/store/StoreConfiguration.mobileStorePrefix"/>

  <dsp:droplet name="ProfileSecurityStatus">
    <dsp:oparam name="loggedIn"> <%-- User is logged in with login/password --%>
      <dsp:include page="shipping.jsp"/>
    </dsp:oparam>
    <dsp:oparam name="default"> <%-- All other cases --%>
      <dsp:include page="${mobileStorePrefix}/global/login.jsp">
        <dsp:param name="checkoutLogin" value="true"/>
        <dsp:param name="registrationFormHandler" value="/atg/store/profile/RegistrationFormHandler"/>
        <dsp:param name="registrationSuccessUrl" value="shipping.jsp"/>
        <c:if test="${passwordSent == 'true'}">
          <dsp:param name="passwordSent" value="true"/>
        </c:if>
      </dsp:include>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/checkout/login.jsp#3 $$Change: 788278 $--%>

<%--
  This page is called by the "AccessControlServlet" to redirect the user to the global login page.

  Page includes:
    /mobile/global/login.jsp - Login form

  Required parameters:
    none

  Optional Params:
    passwordSent
      When returning from the "passwordReset.jsp", set this variable to "true"
--%>
<dsp:page>
  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="passwordSent" param="passwordSent"/>

  <dsp:getvalueof var="mobileStorePrefix" bean="/atg/store/StoreConfiguration.mobileStorePrefix"/>

  <dsp:include page="${mobileStorePrefix}/global/login.jsp">
    <dsp:param name="checkoutLogin" value="false"/>
    <dsp:param name="registrationFormHandler" value="/atg/store/profile/RegistrationFormHandler"/>
    <dsp:param name="registrationSuccessUrl" value="profile.jsp"/>
    <c:if test="${passwordSent == 'true'}">
      <dsp:param name="passwordSent" value="true"/>
    </c:if>
  </dsp:include>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/myaccount/login.jsp#3 $$Change: 788278 $--%>

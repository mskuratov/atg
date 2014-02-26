<%--
  This page is called upon 500 HTTP error - internal server error.

  A 500 HTTP error message will be displayed along with a link to contact
  the system administrator.  
--%>
<dsp:page>

  <crs:pageContainer divId="atg_store_serverErrorIntro" titleKey="" bodyClass="atg_store_internalServerError">
    <div id="atg_store_contentHeader">
      <h2 class="title">
        <fmt:message key="global_serverError.title"/>
      </h2>
    </div>
  
    <crs:messageContainer>
      <jsp:body>
        <p>
          <fmt:message key="global_serverError.serverErrorMsg"/>
          <fmt:message key="global_serverError.notifyAboutErrorMsg"/>
        </p>
        <p><crs:outMessage key="company.gadget.customer_service.byEmail"/></p>
      </jsp:body>
    </crs:messageContainer>
  </crs:pageContainer>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/serverError.jsp#1 $$Change: 735822 $--%>
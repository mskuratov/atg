<%--
  This page displays all necessary fields for the user to be got registered.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/store/profile/RegistrationFormHandler"/>

  <crs:pageContainer index="false" follow="false" 
                     bodyClass="atg_store_pageRegistration atg_store_checkout atg_store_rightCol">
   
    <jsp:attribute name="formErrorsRenderer">
      <%-- Show form errors above accessibility navigation. --%>
      <dsp:include page="./gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="RegistrationFormHandler"/>
      </dsp:include>
    </jsp:attribute>
   
    <jsp:body>
      
      <fmt:message key="checkout_title.checkout" var="title"/>
      
      <crs:checkoutContainer currentStage="register" title="${title}">
        
        <%-- Display form error messages. --%>
        <dsp:include page="./gadgets/checkoutErrorMessages.jsp">
          <dsp:param name="formHandler" bean="RegistrationFormHandler"/>
        </dsp:include>

        <%-- Display registration form. --%>
        <dsp:include page="../myaccount/gadgets/register.jsp" >
          <dsp:param name="restrictionDroplet" value="/atg/store/droplet/CountryListDroplet"/>
          
          <%-- If 'checkout=true', the user will be redirected to Shipping page, when he is registered. --%>
          <dsp:param name="checkout" value="true"/>
          
        </dsp:include>
        
        <dsp:include page="../myaccount/gadgets/benefits.jsp"/>
      </crs:checkoutContainer>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/registration.jsp#2 $$Change: 788278 $--%>

<%--
  This page displays registration form
  and list of benefits for registered customers.

  Required parameters:
    None
    
  Optional parameters:
    None  
--%>

<dsp:page>
  <dsp:importbean bean="/atg/store/profile/RegistrationFormHandler"/>

  <%-- Get the text used on the submit link --%>
  <fmt:message  var="submitText" key="myaccount_registration.submit"/>
        
  <crs:pageContainer index="false" follow="false" 
                     bodyClass="atg_store_pageRegistration atg_store_rightCol">
                       
    <jsp:attribute name="formErrorsRenderer">
      <%-- Display error messages if any above the accessibility navigation--%> 
      <dsp:getvalueof var="regFormExceptions" vartype="java.lang.Object" bean="RegistrationFormHandler.formExceptions"/>
      <c:if test="${not empty regFormExceptions}">
        <dsp:include page="gadgets/myAccountErrorMessage.jsp">
          <dsp:param name="formHandler" bean="RegistrationFormHandler"/>
          <dsp:param name="submitFieldText" value="${submitText}"/>
        </dsp:include>         
      </c:if>
    </jsp:attribute>
      
    <jsp:body>      
      <dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath"/>

      <%-- Registration page title --%>
      <div id="atg_store_contentHeader">
        <dsp:include page="/global/gadgets/pageIntro.jsp">
          <dsp:param name="divId" value="atg_store_registerIntro" />
          <dsp:param name="titleKey" value="myaccount_registration.title" />        
        </dsp:include>
      </div>
      
      <%-- Display form exceptions if any --%>
      <dsp:getvalueof var="regFormExceptions" vartype="java.lang.Object" bean="RegistrationFormHandler.formExceptions"/>
      <c:if test="${not empty regFormExceptions}">
        <dsp:include page="gadgets/myAccountErrorMessage.jsp">
          <dsp:param name="formHandler" bean="RegistrationFormHandler"/>
          <dsp:param name="submitFieldText" value="${submitText}"/>
        </dsp:include>         
      </c:if>
          
      <%-- Registration form itself --%>    
      <dsp:include page="gadgets/register.jsp" >
        <dsp:param name="restrictionDroplet" value="/atg/store/droplet/CountryListDroplet"/>
      </dsp:include>
      
      <%-- List of benefits for registered users --%>    
      <dsp:include page="gadgets/benefits.jsp"/>
      
    </jsp:body>
  </crs:pageContainer>  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/registration.jsp#1 $$Change: 735822 $--%>

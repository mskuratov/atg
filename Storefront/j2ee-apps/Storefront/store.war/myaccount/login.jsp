<%--  
  This login page displays forms to login as new or existing customer.
  
  Page includes:  
    /global/gadgets/pageIntro.jsp introduction message for the page.           
    /myaccount/gadgets/login.jsp login form for returned customers
    /myaccount/gadgets/preRegister.jsp registration form for new customers
  
  Required parameters:
    none
  
  Optional parameters:
    error
      key for error message
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/profile/RegistrationFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>
   
  <%-- Retrieve submit text for further usage --%>
  <fmt:message var="submitText" key="myaccount_registration.preRegister"/>
       
  <crs:pageContainer index="false" follow="false" 
                     bodyClass="atg_store_pageLogin">   
                     
    <jsp:attribute name="formErrorsRenderer">
      <%-- Display error messages if any above the accessibility navigation
      
        Display error that could be passed outside of the page from 
        the global/util/loginRedirect.jsp. This can happen when anonymous
        user tries to add/update giftlist.
        Escape XML specific characters in error message key to prevent
        using it in XSS attacks.
       --%>
      <c:if test="${not empty param['error']}">
        <div class="errorMessage">
          <fmt:message key="${fn:escapeXml(param['error'])}"/>
        </div>
      </c:if>
     
      <%-- 
        Display form exceptions from the RegistrationFormHandler (used on 
        myaccount/gadgets/preRegister.jsp form) and ProfileFormHandler (used on
        myaccount/gadgets/login.jsp form)
       --%>
      <dsp:getvalueof var="regFormExceptions" vartype="java.lang.Object" bean="RegistrationFormHandler.formExceptions"/>
      <c:if test="${not empty regFormExceptions}">
        <dsp:include page="gadgets/myAccountErrorMessage.jsp">
          <dsp:param name="formHandler" bean="RegistrationFormHandler"/>
          <dsp:param name="submitFieldText" value="${submitText}"/>          
        </dsp:include>         
      </c:if>

      <dsp:getvalueof var="b2cFormExceptions" vartype="java.lang.Object" bean="ProfileFormHandler.formExceptions"/>
      <c:if test="${not empty b2cFormExceptions}">
        <dsp:include page="gadgets/myAccountErrorMessage.jsp">
          <dsp:param name="formHandler" bean="ProfileFormHandler"/>                  
        </dsp:include>
      </c:if>
      
      <%--
        Determine status of the current user.
        
        Open parameters:
          autoLoggedIn
            user is logged in from cookie
          loggedIn
            user is logged in with login\password
       --%>
      <dsp:droplet name="ProfileSecurityStatus">
        <dsp:oparam name="autoLoggedIn">
          <div class="errorMessage">
            <fmt:message key="myaccount_login.verifyPassword"/>
          </div>
        </dsp:oparam>
        <dsp:oparam name="loggedIn">
          <%-- 
            If user is logged in, no need to display login form again.
            display notification instead.
           --%>
          <div class="errorMessage">
            <fmt:message key="myaccount_login.currentLogin">
              <fmt:param>
                <dsp:valueof bean="Profile.firstName"/>
              </fmt:param>
            </fmt:message>
            <p>
              <fmt:message var="anotherLoginTitle" key="myaccount_login.anotherLoginTitle"/>
            </p>
          </div>
        </dsp:oparam>
      </dsp:droplet>
    </jsp:attribute>
                      
    <jsp:body>      
      
      <%-- Display page header with title --%>
      <div id="atg_store_contentHeader">
        <dsp:include page="/global/gadgets/pageIntro.jsp">
          <dsp:param name="divId" value="atg_store_registerIntro" />
          <dsp:param name="titleKey" value="myaccount_login.title" />        
        </dsp:include>
      </div>

      <%--
        Display error that could be passed outside of the page from 
        the global/util/loginRedirect.jsp. This can happen when anonymous
        user tries to add/update giftlist.
        Escape XML specific characters in error message key to prevent
        using it in XSS attacks.
       --%>
      <c:if test="${not empty param['error']}">
        <div class="errorMessage">
          <fmt:message key="${fn:escapeXml(param['error'])}"/>
        </div>
      </c:if>
     
      <%-- 
        Display form exceptions from the RegistrationFormHandler (used on 
        myaccount/gadgets/preRegister.jsp form) and ProfileFormHandler (used on
        myaccount/gadgets/login.jsp form)
       --%>
      <dsp:getvalueof var="regFormExceptions" vartype="java.lang.Object" bean="RegistrationFormHandler.formExceptions"/>
      <c:if test="${not empty regFormExceptions}">
        <dsp:include page="gadgets/myAccountErrorMessage.jsp">
          <dsp:param name="formHandler" bean="RegistrationFormHandler"/>
          <dsp:param name="submitFieldText" value="${submitText}"/>
        </dsp:include>         
      </c:if>

      <dsp:getvalueof var="b2cFormExceptions" vartype="java.lang.Object" bean="ProfileFormHandler.formExceptions"/>
      <c:if test="${not empty b2cFormExceptions}">
        <dsp:include page="gadgets/myAccountErrorMessage.jsp">
          <dsp:param name="formHandler" bean="ProfileFormHandler"/>                 
        </dsp:include>
      </c:if>
      
      <%--
        Determine status of the current user.
        
        Open parameters:
          autoLoggedIn
            user is logged in from cookie
          loggedIn
            user is logged in with login\password
       --%>
      <dsp:droplet name="ProfileSecurityStatus">
        <dsp:oparam name="autoLoggedIn">
          <div class="errorMessage">
            <fmt:message key="myaccount_login.verifyPassword"/>
          </div>
        </dsp:oparam>
        <dsp:oparam name="loggedIn">
          <%-- 
            If user is logged in, no need to display login form again.
            display notification instead.
           --%>
          <div class="errorMessage">
            <fmt:message key="myaccount_login.currentLogin">
              <fmt:param>
                <dsp:valueof bean="Profile.firstName"/>
              </fmt:param>
            </fmt:message>
            <p>
              <fmt:message var="anotherLoginTitle" key="myaccount_login.anotherLoginTitle"/>
            </p>
          </div>
        </dsp:oparam>
      </dsp:droplet> 

      <%-- Display login forms --%>
      <div id="atg_store_accountLogin">
        <div id="atg_store_loginOrRegister">  
          
          <%-- Login form for returning customer --%>      
          <dsp:include page="gadgets/login.jsp" />
          
          <%-- Login form for new customer --%>
          <dsp:include page="gadgets/preRegister.jsp" />
                    
        </div>
      </div>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/login.jsp#2 $$Change: 788278 $--%>

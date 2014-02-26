<%-- 
  This page renders login form for returned customers.
  
  Required parameters:
    None
    
  Optional parameters:
    None
--%>
<dsp:page>

  <dsp:importbean bean="/atg/store/profile/SessionBean"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>
  
  <dsp:getvalueof var="currentLocale" vartype="java.lang.String" bean="/atg/dynamo/servlet/RequestLocale.localeString"/>
  
  <div class="atg_store_loginMethod" id="atg_store_returningCustomerLogin">
    
    <%-- Form title --%>
    <h2>
      <fmt:message key="login.returningCustomer" />
    </h2>              
        
    <div class="atg_store_register">
      <fieldset class="atg_store_havePassword">
        
        <%--
          Check Profile's security status. If user is logged in from cookie,
          display default values, i.e. profile's email address in this case, otherwise 
          do not populate form handler with profile's values.
         --%>
        <dsp:droplet name="ProfileSecurityStatus">
          <dsp:oparam name="anonymous">
            <dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" 
                          value="false"/>
          </dsp:oparam>
          <dsp:oparam name="autoLoggedIn">
            <dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" 
                          value="true"/>
          </dsp:oparam>
        </dsp:droplet>  

        <dsp:form action="${originatingRequest.requestURI}" method="post" 
                  id="atg_store_registerLoginForm" formid="atg_store_registerLoginForm">
          
          <%-- Get the loginSuccessURL from SessionBean --%>
          <dsp:getvalueof var="loginSuccessURL" bean="SessionBean.values.loginSuccessURL"/>
          
          <%-- If its not set in the Session bean check for a loginSuccessURL paramater --%>
          <c:if test="${empty loginSuccessURL}">
            <dsp:getvalueof var="loginSuccessURL" param="loginSuccessURL"/>
            <%-- Set the SessionBean in case the page is reloaded --%>
            <dsp:setvalue bean="SessionBean.values.loginSuccessURL" value="${loginSuccessURL}"/>
          </c:if>
          
          <c:choose>
            <c:when test="${not empty loginSuccessURL}">
              
              <c:url value="${loginSuccessURL}" var="successURL" scope="page">                    
                <c:param name="locale" value="${currentLocale}"/>
              </c:url>                

              <dsp:input bean="ProfileFormHandler.loginSuccessURL" type="hidden"
                         value="${successURL}"/>
            </c:when>
            <%-- Default to the home page if there is no loginSuccessURL --%>
            <c:otherwise>
              <dsp:input bean="ProfileFormHandler.loginSuccessURL" 
                         type="hidden" value="../index.jsp?locale=${currentLocale}"/>
            </c:otherwise>
          </c:choose>
    
          <ul class="atg_store_basicForm">
          <li>
            <label for="atg_store_registerLoginEmailAddress"><fmt:message key="common.loginEmailAddress"/></label>
          
            <dsp:input bean="ProfileFormHandler.value.login" iclass="text"
                       type="text" required="true" maxlength="255"
                       name="atg_store_registerLoginEmailAddress"
                       id="atg_store_registerLoginEmailAddress" />
          </li>
          <li>
            <label for="atg_store_registerLoginPassword"><fmt:message key="common.loginPassword"/></label>
            
            <dsp:input bean="ProfileFormHandler.value.password" iclass="text"
                       type="password" required="true" value=""
                       name="atg_store_registerLoginPassword"
                       id="atg_store_registerLoginPassword" />
                      
             <fmt:message var="passwordResetTitle" key="common.button.passwordResetTitle" />
             
             <dsp:a page="../passwordReset.jsp" 
                    title="${passwordResetTitle}" 
                    iclass="info_link atg_store_forgetPassword">
               <fmt:message key="common.button.passwordResetText" />
             </dsp:a>
            
          </li>
          </ul>

          <%-- 'Login' button --%>
            <div class="atg_store_formActions">
              <fmt:message var="submitText" key="myaccount_login.submit"/>
              <span class="atg_store_basicButton atg_store_chevron">
                <dsp:input bean="ProfileFormHandler.login" type="submit"
                           alt="${submitText}" value="${submitText}" 
                           id="atg_store_loginButton"/>
              </span>
            </div> 
        </dsp:form>
    </div><%-- atg_store_register --%>
  </div><%-- atg_store_loginMethod --%> 
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/login.jsp#2 $$Change: 788278 $--%>

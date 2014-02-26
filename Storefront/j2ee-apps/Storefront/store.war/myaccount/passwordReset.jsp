<%--
  Layout page for reset the password.
  
  Required parameters:
    None
    
  Optional parameters:
    None
--%>
<dsp:page>
  <crs:pageContainer divId="atg_store_profilePasswordForgotIntro" 
                     index="false" follow="false"
                     bodyClass="atg_store_forgotPassword">
                     
    <jsp:attribute name="formErrorsRenderer">
      <%-- Show form errors if any above the accessibility navigation--%>
      <dsp:getvalueof var="formExceptions" vartype="java.lang.Object" bean="/atg/userprofiling/ForgotPasswordHandler.formExceptions"/>
      <c:if test="${not empty formExceptions}">
        <div id="atg_store_formValidationError" class="errorMessage">
          <c:forEach var="formException" items="${formExceptions}">
            <dsp:param name="formException" value="${formException}"/>
            <p>
              <%-- Check the error message code to see what we should do --%>
              <dsp:getvalueof var="errorCode" param="formException.errorCode"/>
              <c:choose>
                <%-- 
                  We check error code to display specific error message
                  about email address, not general one.
                --%>
                <c:when test="${errorCode == 'missingRequiredValue'}">
                  <fmt:message key="myaccount_profilePasswordForgot.fillEmailAddress"/>
                </c:when>
                <c:otherwise>
                  <dsp:valueof param="formException.message" valueishtml="true">
                    <fmt:message key="common.errorMessageDefault"/>
                  </dsp:valueof>
                </c:otherwise>
              </c:choose>
            </p>
          </c:forEach>
        </div>
      </c:if>
    </jsp:attribute>
    
    <jsp:body>
    
      <%-- Page title --%>
      <div id="atg_store_contentHeader">
        <h2 class="title"><fmt:message key="myaccount_profilePasswordForgot.title"/></h2>
      </div>
      
      <%-- Show form errors --%>
      <dsp:getvalueof var="formExceptions" vartype="java.lang.Object" bean="/atg/userprofiling/ForgotPasswordHandler.formExceptions"/>
      <c:if test="${not empty formExceptions}">
        <div id="atg_store_formValidationError" class="errorMessage">
          <c:forEach var="formException" items="${formExceptions}">
            <dsp:param name="formException" value="${formException}"/>
            <p>
              <%-- Check the error message code to see what we should do --%>
              <dsp:getvalueof var="errorCode" param="formException.errorCode"/>
              <c:choose>
                <%-- 
                  We check error code to display specific error message
                  about email address, not general one.
                --%>
                <c:when test="${errorCode == 'missingRequiredValue'}">
                  <fmt:message key="myaccount_profilePasswordForgot.fillEmailAddress"/>
                </c:when>
                <c:otherwise>
                  <dsp:valueof param="formException.message" valueishtml="true">
                    <fmt:message key="common.errorMessageDefault"/>
                  </dsp:valueof>
                </c:otherwise>
              </c:choose>
            </p>
          </c:forEach>
        </div>
      </c:if>
      
      <%-- Display password reset message --%>
      <crs:messageContainer titleKey="myaccount_profilePasswordForgot.noProblem"
        messageKey="myaccount_profilePasswordForgot.intro">
        <jsp:body>
          
          <%-- Include page with password forgot form --%>
          <dsp:include page="gadgets/profilePasswordForgot.jsp"/>
          
        </jsp:body>
      </crs:messageContainer>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/passwordReset.jsp#1 $$Change: 735822 $--%>

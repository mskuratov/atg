<%--
  New password email template. Contains a new password requested by user.
  
  Required parameters:
    newpassword
      The new password assigned to user.
     
  Optional parameters:
    locale
      Locale that specifies in which language email should be rendered.
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/userprofiling/Profile"/>  
  <dsp:importbean bean="/atg/multisite/Site"/>
  
  <%-- Get sender email address from site configuration --%>
  <dsp:getvalueof var="newPasswordFromAddress" bean="Site.newPasswordFromAddress" />

  <crs:emailPageContainer divId="atg_store_resetPasswordIntro" 
                          titleKey="emailtemplates_newPassword.title" 
                          messageSubjectKey="emailtemplates_newPassword.subject" 
                          messageFromAddressString="${newPasswordFromAddress}"
                          displayProfileLink="false">
    <jsp:body>
  
      <table border="0" cellpadding="0" cellspacing="0" width="609" 
             style="font-size:14px;margin-top:20px;margin-bottom:30px"
             summary="" role="presentation">
        <tr>
          <td style="color:#666;font-family:Tahoma,Arial,sans-serif;">
            <fmt:message key="emailtemplates_newPassword.greeting">
              <fmt:param>
                <dsp:valueof bean="Profile.firstName"/>
              </fmt:param>
            </fmt:message>
            
            <br /><br />
            
            <%-- Notification that user's password has been changed. --%>
            <fmt:message key="emailtemplates_newPassword.passwordChanged"/>
            <br /><br />
            <fmt:message key="emailtemplates_newPassword.newPassword"/>
            
            <%-- Display new password. --%>
            <span style="color:#000000">
              <dsp:valueof param="newpassword"/>
            </span>
            <br /><br />
            
            <%-- Link to login page. --%>
            <fmt:message key="emailtemplates_newPassword.newPasswordAssignedLogin">
              <fmt:param>
                <fmt:message var="linkText" key="common.login"/>
                <dsp:include page="/emailtemplates/gadgets/emailSiteLinkDisplay.jsp">
                  <dsp:param name="path" value="/myaccount/login.jsp"/>
                  <dsp:param name="queryParams" value="loginSuccessURL=profile.jsp"/>
                  <dsp:param name="locale" param="locale"/>
                  <dsp:param name="httpServer" param="httpServer"/>
                  <dsp:param name="linkStyle" value="font-size:14px;font-family:Tahoma,Arial,sans-serif;color:#004BF9;text-decoration: underline;"/>
                  <dsp:param name="linkText" value="${linkText}"/>
                </dsp:include>
              </fmt:param>
            </fmt:message>
          </td>
        </tr>
      </table>
    </jsp:body>
    
  </crs:emailPageContainer>

</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/newPassword.jsp#2 $$Change: 788278 $--%>

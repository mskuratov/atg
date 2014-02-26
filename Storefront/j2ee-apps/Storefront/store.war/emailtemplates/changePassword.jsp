<%--
  Change password email template. The template contains the confirmation 
  that the password was successfully changed.
  
  Required parameters:
    None.
     
  Optional parameters:
    locale
      Locale that specifies in which language email should be rendered.
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/multisite/Site"/>
  
  <%-- Get sender email address from site configuration --%>
  <dsp:getvalueof var="changePasswordFromAddress" bean="Site.changePasswordFromAddress" />

  <crs:emailPageContainer divId="atg_store_resetPasswordIntro"
                          titleKey="emailtemplates_changePassword.title"                           
                          messageSubjectKey="emailtemplates_changePassword.subject"     
                          messageFromAddressString="${changePasswordFromAddress}"                      
                          displayProfileLink="false">
    <jsp:body>
      <br/><br/>
    </jsp:body>
    
  </crs:emailPageContainer>

</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/changePassword.jsp#1 $$Change: 735822 $--%>

<%--
  Registered User email template. The template contains the confirmation 
  that the successfully registered.
  
  Required parameters:
    None.
     
  Optional parameters:
    locale
      Locale that specifies in which language email should be rendered.
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/userprofiling/Profile"/>  
  <dsp:importbean bean="/atg/multisite/Site"/>
  
  <%-- Get sender email address from site configuration --%>
  <dsp:getvalueof var="registeredUserFromAddress" bean="Site.registeredUserFromAddress" />

  <fmt:message var="emailSubject" key="emailtemplates_registeredUser.subject">
    <fmt:param>
      <dsp:valueof bean="Site.name" />
    </fmt:param>
  </fmt:message> 

  <crs:emailPageContainer divId="atg_store_resetPasswordIntro"  
                          titleKey="emailtemplates_registeredUser.title"
                          messageSubjectString="${emailSubject}"     
                          messageFromAddressString="${registeredUserFromAddress}"                      
                          displayProfileLink="false">
    <jsp:body>
  
      <table border="0" cellpadding="0" cellspacing="0" width="609" 
             style="font-size:14px;margin-top:20px;margin-bottom:30px"
             summary="" role="presentation">
        <tr>
          <td style="color:#666;font-family:Tahoma,Arial,sans-serif;">
                 
            <fmt:message key="emailtemplates_registeredUser.greeting">
              <fmt:param>
                <dsp:valueof bean="Profile.firstName"/>
              </fmt:param>
            </fmt:message>            
            <br/><br/>
            
            <%-- Notification that user's registration is successful. --%>
            <fmt:message key="emailtemplates_registeredUser.registration">
              <fmt:param>
                <dsp:valueof bean="Site.name" />
              </fmt:param>
            </fmt:message>
            <br/>
            <fmt:message key="emailtemplates_registeredUser.validAccount"/>
                        
          </td>
        </tr>
      </table>
    </jsp:body>
    
  </crs:emailPageContainer>

</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/registeredUser.jsp#1 $$Change: 735822 $--%>

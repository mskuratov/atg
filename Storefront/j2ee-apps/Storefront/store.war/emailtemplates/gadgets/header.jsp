<%-- 
  Email template header.
  
  Required parameters:
    httpServer
      The URL prefix with protocol, host and port so we can construct fully-qualified URLs
      
  Optional parameters:
    displayProfileLink
      Boolean indicating whether links to profile should be displayed, 'true' by default
--%>
<dsp:page>
  
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:getvalueof var="httpServer" param="httpServer"/>
    
  
  <html>
    <head>
      <title>
        <fmt:message key="common.storeTitle">
          <fmt:param>
            <dsp:valueof bean="Site.name"/>
          </fmt:param>
        </fmt:message>
      </title>
    </head>
 
    <body style="background-color:#DCE7E9;margin:0px;padding:0px;font-family:Tahoma,Arial,sans-serif;font-size:12px;">
      <div align="center" style="padding:0px;margin:0px">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" style="padding:0px;margin:0px" summary="" role="presentation">
          <tr>
            <td align="center" style="padding-left:0px;padding-right:0px;padding-top:8px;background-color:#DCE7E9">
              <table width="682px" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td style="text-align:left;width:650px;background-color:#FFFFFF;padding-top:16px;padding-left:16px;padding-right:16px;vertical-align:bottom;" 
                      valign="top">
                      
                    <%--Store Logo and My Account Link --%>
                 
                    <table style="width:100%;">
                      <tr>
                        <td>
                          <dsp:getvalueof var="siteIconUrl" bean="Site.largeSiteIcon"/>
                          <dsp:getvalueof var="imageAltText" bean="Site.name"/>
                          
                          <%-- Display site specific URL to home page --%>
                          <dsp:include page="/emailtemplates/gadgets/emailSiteLinkDisplay.jsp">
                            <dsp:param name="path" value="/index.jsp"/>
                            <dsp:param name="httpServer" value="${httpServer}"/>
                            <dsp:param name="imageUrl" value="${httpServer}${siteIconUrl}"/>
                            <dsp:param name="imageAltText" value="${imageAltText}"/>
                          </dsp:include>
                        </td>
                        <td style="vertical-align:bottom;text-align:right">
                    
                        <%-- Display link to account only for registered users --%>
                        <dsp:getvalueof var="isTransient" bean="Profile.transient"/>
                        <c:if test="${!isTransient}">
                          <dsp:getvalueof var="displayProfileLink" vartype="java.lang.String" 
                                          param="displayProfileLink"/>
                          <c:if test="${displayProfileLink == 'true' or empty displayProfileLink}">
                            <fmt:message var="linkText" key="emailtemplates_header.myAccount"/>
                            <dsp:include page="/emailtemplates/gadgets/emailSiteLinkDisplay.jsp">
                              <dsp:param name="path" value="/myaccount/profile.jsp"/>
                              <dsp:param name="httpServer" value="${httpServer}"/>
                              <dsp:param name="linkStyle" 
                                         value="font-size:12px;font-family:Tahoma,Arial,sans-serif;color: #004BF9;text-decoration: none;margin-right: 10px;"/>
                              <dsp:param name="linkText" value="${linkText}"/>
                            </dsp:include>
                           </c:if>
                         </c:if>
                       </td>
                     </tr>
                   </table>
                   <hr size="1" style="color:#7F7F8C;">
                 </td>
               </tr>
              
               <tr>
                 <td align="center" style="padding-left:0px;padding-right:2px;padding-bottom:0px;background-color:#FFFFFF">
                   <table width="681px" border="0" cellspacing="0" cellpadding="0">
                     <tr>
                       <td style="text-align:left;width:650px;background-color:#FFFFFF;padding:16px" 
                           valign="top">

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/header.jsp#1 $$Change: 735822 $--%>

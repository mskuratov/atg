<%-- 
  Email template footer, display links to company information pages.
  
  Required parameters:
    httpServer
      The URL prefix with protocol, host and port so we can construct fully-qualified URLs
      
  Optional parameters:
    messageFrom
      sender's email address
      
--%>
<dsp:page>
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:getvalueof var="httpServer" param="httpServer"/>
  <dsp:getvalueof var="messageFromEmailAddress" param="messageFrom"/>
    
                    <%-- Mail to link for email replies--%>            
                    <div align="center" style="font-family:Tahoma,Arial,sans-serif;font-size:11px;margin-top:8px;margin-bottom:0px;color:#666666">
                      <c:if test="${not empty messageFromEmailAddress}">
                        <fmt:message key="emailtemplates_footer.addToAddressBook">
                          <fmt:param>
                            <a style="color:#004BF9" class="email" target="_blank" href="mailto:${messageFromEmailAddress}">${messageFromEmailAddress}</a>
                          </fmt:param>
                        </fmt:message>  
                      </c:if>
                    </div>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
       
        <%-- Display footer links --%>
        <div align="center">
          <table border="0" cellspacing="0" cellpadding="0" width="100%">
            <tr>
              <td align="center" style="font-family:Verdana,Arial,sans-serif;font-size:12px;color:#4F7BC4;padding-top:16px">
                <%-- Terms & Conditions--%>
                <fmt:message var="linkText" key="company_terms.title"/>
                <dsp:include page="/emailtemplates/gadgets/emailSiteLinkDisplay.jsp">
                  <dsp:param name="path" value="/company/terms.jsp"/>
                  <dsp:param name="httpServer" param="httpServer"/>
                  <dsp:param name="linkStyle" value="margin-left:6px;margin-right:6px;color:#004BF9;text-decoration:none;font-weight:bold"/>
                  <dsp:param name="linkText" value="${linkText}"/>
                </dsp:include> 
                &nbsp;&nbsp;|&nbsp;&nbsp;
                <%-- Privacy Policy --%>
                <fmt:message var="linkText" key="emailtemplates_footer.privacyPolicy"/>
                <dsp:include page="/emailtemplates/gadgets/emailSiteLinkDisplay.jsp">
                  <dsp:param name="path" value="/company/privacy.jsp"/>
                  <dsp:param name="httpServer" param="httpServer"/>
                  <dsp:param name="linkStyle" value="margin-left:6px;margin-right:6px;color:#004BF9;text-decoration:none;font-weight:bold"/>
                  <dsp:param name="linkText" value="${linkText}"/>
                </dsp:include> 
                &nbsp;&nbsp;|&nbsp;&nbsp;
                <%-- Shipping & Handling --%>
                <fmt:message var="linkText" key="emailtemplates_footer.shipping"/>
                <dsp:include page="/emailtemplates/gadgets/emailSiteLinkDisplay.jsp">
                  <dsp:param name="path" value="/company/shipping.jsp"/>
                  <dsp:param name="httpServer" param="httpServer"/>
                  <dsp:param name="linkStyle" value="margin-left:6px;margin-right:6px;color:#004BF9;text-decoration:none;font-weight:bold"/>
                  <dsp:param name="linkText" value="${linkText}"/>
                </dsp:include> 
                &nbsp;&nbsp;|&nbsp;&nbsp;
                <%-- Customer Service --%>
                <fmt:message var="linkText" key="emailtemplates_footer.contactUs"/>
                <dsp:include page="/emailtemplates/gadgets/emailSiteLinkDisplay.jsp">
                  <dsp:param name="path" value="/company/customerService.jsp"/>
                  <dsp:param name="httpServer" param="httpServer"/>
                  <dsp:param name="linkStyle" value="margin-left:6px;margin-right:6px;color:#004BF9;text-decoration:none;font-weight:bold"/>
                  <dsp:param name="linkText" value="${linkText}"/>
                </dsp:include>
              </td>
            </tr>
            <tr>
              <%-- Copyright message --%>
              <td align="center" style="font-family:Tahoma,Arial,sans-serif;font-size:12px;padding-top:16px;color:#666666">
                <dsp:include page="/global/gadgets/copyright.jsp">
                  <dsp:param name="copyrightDivId" value="copyrightText"/>
                </dsp:include>
              </td>
            </tr>
          </table>
        </div>
      </div>

    </body>
  </html>
  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/footer.jsp#1 $$Change: 735822 $--%>

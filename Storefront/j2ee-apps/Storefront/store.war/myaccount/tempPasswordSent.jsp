<%--
  This page displays confirmation that new password
  has been sent on the email address provided by user
  
  Required parameters:
    None
    
  Optional parameters:
    None  
 --%>
<dsp:page>
  <crs:pageContainer divId="atg_store_tempPasswordSentIntro"
                     index="false" follow="false"
                     bodyClass="atg_store_tempPasswordSent">
    <jsp:body>
      
      <%-- Page title --%>
      <div id="atg_store_contentHeader">
        <h2 class="title">
          <fmt:message key="myaccount_tempPasswordSent.title"/>
        </h2>
      </div>
      
      <%-- Display confirmation in the special message container --%>
      <crs:messageContainer messageKey="myaccount_tempPasswordSent.msgConfirmResetPassword">
        <jsp:body>
          <p><fmt:message key="myaccount_tempPasswordSent.msgChangeTemporaryPassword"/></p>
        </jsp:body>
      </crs:messageContainer>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/tempPasswordSent.jsp#1 $$Change: 735822 $--%>

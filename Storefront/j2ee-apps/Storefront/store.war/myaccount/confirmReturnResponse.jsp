<%--
  This is the final page for the return submission process. It displays success message and a link
  to the return details page.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>
<dsp:page>
  <crs:pageContainer divId="atg_store_returnItems" 
                     index="false" follow="false"
                     bodyClass="atg_store_returnItem atg_store_myAccountPage atg_store_leftCol"
                     selpage="MY ORDERS" >
    <jsp:body>

      <dsp:importbean bean="/atg/store/profile/SessionBean"/>
      <dsp:importbean bean="/atg/userprofiling/Profile"/>                       
                       
      <%-- Page title --%>
      <div id="atg_store_contentHeader">
        <div id="atg_store_checkoutProgressContainer">
            
          <h2 class="title"><fmt:message key="myaccount_confirmReturnResponse.header"/></h2>
              
        </div>
      </div>
          
      <%-- Left-hand menu --%>
      <dsp:include page="gadgets/myAccountMenu.jsp">
        <dsp:param name="selpage" value="MY ORDERS" />
      </dsp:include>
      
      <div class="atg_store_main atg_store_myAccount">
      
        <crs:messageContainer titleKey="myaccount_confirmReturnResponse.successTitle">       
          <p>
            <fmt:message var="returnNumberLinkTitle" key="myaccount_confirmReturnResponse.returnNumberLinkTitle"/>
            <fmt:message key="myaccount_confirmReturnResponse.returnNumber">
              <fmt:param>
                <%-- Display link to currently placed order. --%>
                <dsp:a page="/myaccount/returnDetail.jsp" title="${returnNumberLinkTitle}">              
                  <dsp:param name="returnRequestId" bean="SessionBean.values.lastReturnRequest.authorizationNumber"/>
                  <dsp:valueof bean="SessionBean.values.lastReturnRequest.authorizationNumber"/>
                </dsp:a>
              </fmt:param>
            </fmt:message>
          </p>

          <%-- Confirmation e-mail message. --%>
          <p>
            <fmt:message key="myaccount_confirmReturnResponse.emailText">
              <fmt:param>
                <span><dsp:valueof bean="Profile.email"/></span>
              </fmt:param>
            </fmt:message>
          </p>
 
          <%-- Display link to 'All My Orders' page. --%>
          <p>
            <fmt:message key="myaccount_confirmReturnResponse.returnHistoryLinkTitle" var="returnHistoryLinkTitle"/>
            
            <fmt:message key="myaccount_confirmReturnResponse.reviewReturn">
              <fmt:param>
                <dsp:a page="/myaccount/myReturns.jsp" title="${returnHistoryLinkTitle}">              
                  ${returnHistoryLinkTitle}
                </dsp:a>
              </fmt:param>
            </fmt:message>
          </p>
        </crs:messageContainer>
        
      </div>
    </jsp:body>
  </crs:pageContainer> 
  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/confirmReturnResponse.jsp#1 $$Change: 788278 $--%>                       
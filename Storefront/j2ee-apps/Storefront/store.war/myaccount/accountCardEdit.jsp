<%--
  Outlay page for editing user's saved credit card information 

  This page includes paymentInfoCardAdd.jsp for rendering the logic as well as presentation for the account
  specific payment information details. 

  Required parameters:
    successURL
      to redirect to, during the success of updating or creation of new Credit Card Details.
    cancelURL
      to redirect to, during the failure of updating or creation of new Credit Card Details.
    nickName
      the nickName of credit card to edit
      
  Optional parameters:
    None      
--%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  
  <dsp:setvalue bean="ProfileFormHandler.editCard" paramvalue="nickName"/> 
  
  <%-- Set title for submit button --%>
  <fmt:message var="submitText" key="common.button.saveChanges"/>
  
  <crs:pageContainer divId="atg_store_accountEditCardIntro" 
                     index="false" follow="false" 
                     bodyClass="atg_store_myAccountPage atg_store_leftCol">
                     
    <jsp:attribute name="formErrorsRenderer">
      <%-- Display error messages if any above the accessibility navigation--%> 
      <dsp:include page="/myaccount/gadgets/myAccountErrorMessage.jsp">
        <dsp:param name="formHandler" bean="ProfileFormHandler"/>
        <dsp:param name="submitFieldText" value="${submitText}"/>
        <dsp:param name="errorMessageClass" value="errorMessage"/>
      </dsp:include>
    </jsp:attribute>
                     
    <jsp:body>
    
      <%-- Page title --%>
      <div id="atg_store_contentHeader">
        <h2 class="title">
          <fmt:message key="myaccount_accountCardEdit.title"/>
        </h2>
      </div>
      
      <%-- Left-hand menu --%>
      <dsp:include page="gadgets/myAccountMenu.jsp">
        <dsp:param name="selpage" value="PAYMENT INFO" />
      </dsp:include>

      <%-- Credit card edit page --%>
      <div class="atg_store_myAccount atg_store_main">
        <dsp:include page="/myaccount/gadgets/creditCardEdit.jsp">
          <dsp:param name="successURL" param="successURL"/>
          <dsp:param name="cancelURL" param="cancelURL"/>
        </dsp:include>
      </div>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/accountCardEdit.jsp#2 $$Change: 788278 $--%>

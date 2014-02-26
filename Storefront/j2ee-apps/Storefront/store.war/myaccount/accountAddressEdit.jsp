<%--
  Outlay page for editing user saved address. 
  This page includes editAddress.jsp for rendering the logic 
  as well as presentation for the account specific address. 

  Required parameters:
    successURL
      to redirect to, during the success of updation address.
    addEditMode
      set to 'edit' when a current address is being modified, otherwise it is assumed
      a new address is being added.
  
  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  
  <%-- Set title for submit button --%>
  <fmt:message var="submitText" key="common.button.saveAddressText"/>
   
  <crs:pageContainer divId="atg_store_accountEditAddressIntro" 
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
      
      <%-- Display page title based on mode --%>
      <dsp:getvalueof var="addEditMode" param="addEditMode"/>
      
      <div id="atg_store_contentHeader">
        <h2 class="title">
          <c:choose>
            <c:when test="${addEditMode == 'edit'}">
              <fmt:message key="myaccount_accountAddressEdit.title"/>
            </c:when>
            <c:otherwise>
              <fmt:message key="myaccount_addressEdit.newAddress"/>          
            </c:otherwise>
          </c:choose>
        </h2>
      </div>
      
      <%-- Left-hand menu --%>
      <dsp:include page="gadgets/myAccountMenu.jsp">
        <dsp:param name="selpage" value="ADDRESS BOOK" />
      </dsp:include>

      <%-- 'Ccreate Address' form --%>
      <div class="atg_store_main atg_store_myAccount">
        <dsp:include page="/myaccount/gadgets/addressEdit.jsp">
          <dsp:param name="successURL" param="successURL"/>
          <dsp:param name="firstLastRequired" value="true"/>
          <dsp:param name="addEditMode" param="addEditMode"/>
          <dsp:param name="restrictionDroplet" value="/atg/store/droplet/ShippingRestrictionsDroplet"/>
        </dsp:include>  
      </div>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/accountAddressEdit.jsp#2 $$Change: 788278 $--%>



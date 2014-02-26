<%--
  This page displays form for adding new gift list address. Address is stored to profile 
  secondary addresses.
   
  Required parameters:
    successURL
      The URL to where user should be redirected after successful adding of new address.

  Optional parameters:
    None.
 --%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  
  <%-- Set title for submit button --%>
  <fmt:message var="submitText" key="common.button.saveAddressText"/>

  <crs:pageContainer divId="atg_store_editAddressIntro"
                     index="false" follow="false" 
                     bodyClass="atg_store_myAccountPage atg_store_leftCol"
                     selpage="GIFT LISTS" >
                     
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
          <fmt:message key="myaccount_giftAddressAdd.title"/>
        </h2>
      </div>
      <%--
        Include My Account left side menu that provides navigation links between account 
        configuration pages.
       --%>
      <dsp:include page="gadgets/myAccountMenu.jsp">
        <dsp:param name="selpage" value="GIFT LISTS" />
      </dsp:include>
      
      <%-- The URL to where user should be redirected after adding new gift list address. --%>
      <dsp:getvalueof var="successURL" param="successURL"/>
      
      <div class="atg_store_main atg_store_myAccount">
        <%-- Include gadget with add new address form --%>
        <dsp:include page="/myaccount/gadgets/addressEdit.jsp">
          <dsp:param name="successURL" value="${successURL}"/>
          <dsp:param name="cancelURL" value="../${successURL}"/>
          <dsp:param name="firstLastRequired" value="required"/>
          <dsp:param name="addEditMode" value="add"/>
          <dsp:param name="restrictionDroplet" value="/atg/store/droplet/ShippingRestrictionsDroplet"/>
        </dsp:include>
      </div>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/giftAddressAdd.jsp#2 $$Change: 788278 $--%>

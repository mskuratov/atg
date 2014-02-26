<%--  
  This profile page displays general profile information and includes
    
    gadgets/myAccountMenu.jsp for left-hand menu 
    
    gadgets/profileMyInfo.jsp for rendering all general 
    information associated with a profile.

    gadgets/profileMyInfoEditLinks.jsp for rendering the links for editing 
    the profile details
    
    link to page for changing the password associated with a profile.

    gadgets/profileCheckOutPrefs.jsp for rendering all details regarding checkout
    associated with a profile.
    
  Required parameters:
    None
    
  Optional parameters:
    None    
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/profile/RegistrationFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  
  <fmt:message var="changePasswordTitle" key="myaccount_profileMyInfoEditLinks.button.changePasswordTitle"/>
  <fmt:message var="changePasswordText" key="myaccount_profileMyInfoEditLinks.button.changePasswordText"/>
  
  <crs:pageContainer divId="atg_store_profileIntro"
                     index="false" follow="false"
                     bodyClass="atg_store_myAccountPage atg_store_leftCol"
                     selpage="MY PROFILE">    
    <jsp:body>
    
      <%-- Profile page title --%>
      <div id="atg_store_contentHeader">
        <h2 class="title">
          <fmt:message key="myaccount_profile.title" />
        </h2>
      </div>
      
      <%-- 
        Display left-side menu with selected 
      --%>
      <dsp:include page="gadgets/myAccountMenu.jsp">
        <dsp:param name="selpage" value="MY PROFILE" />
      </dsp:include>
      
      <div class="atg_store_main atg_store_myAccount atg_store_myProfile">
        
        <%-- Personal profile information --%>
        <div class="atg_store_myProfileInfo">
          <dsp:include page="/myaccount/gadgets/profileMyInfo.jsp" />
        </div>

        <div class="atg_store_PasswordDefaultsContainer">
          <%-- 'Change password' gadget --%>
          <div class="atg_store_changePassword">
            <div class="atg_store_formActions">
              <a title="${changePasswordTitle}" href="profilePasswordEdit.jsp" 
                 class="atg_store_basicButton">
                <span>${changePasswordText}</span>
              </a>
            </div>
          </div>
          
          <%--
            Checkout defaults: shipping method, default credit card,
            and shipping address
           --%>
          <dsp:include page="/myaccount/gadgets/profileCheckOutPrefs.jsp" />
        </div>
      </div>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/profile.jsp#1 $$Change: 735822 $--%>


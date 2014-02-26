<%--
  Layout page for profile password edit functionality.
  
  Required parameters:
    None
    
  Optional parameters:
    None  
--%>
<dsp:page>

  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>

  <fmt:message  var="saveText" key="common.button.saveText"/>
  
  <crs:pageContainer divId="atg_store_profilePasswordEditIntro" 
                     index="false" follow="false"
                     bodyClass="atg_store_myAccountPage atg_store_leftCol"
                     selpage="MY PROFILE" >
     
    <jsp:attribute name="formErrorsRenderer">
      <%-- Display error messages if any above the accessibility navigation--%> 
      <dsp:include page="/myaccount/gadgets/myAccountErrorMessage.jsp">
        <dsp:param name="formHandler" bean="ProfileFormHandler"/>
        <dsp:param name="submitFieldText" value="${saveText}"/>
      </dsp:include>
    </jsp:attribute>                 
                     
    <jsp:body>

      <%-- Page title --%>
      <div id="atg_store_contentHeader">
        <h2 class="title">
          <fmt:message key="myaccount_profilePasswordEdit.title"/>
        </h2>
      </div>
      
      <%-- Left-hand menu --%>
      <dsp:include page="gadgets/myAccountMenu.jsp">
        <dsp:param name="selpage" value="MY PROFILE" />
      </dsp:include>

      <%-- Password edit form --%>
      <div class="atg_store_main atg_store_myAccount">

        <div id="atg_store_profileMyInfoEdit">
      
          <%-- Show form errors if any --%>
          <dsp:include page="/myaccount/gadgets/myAccountErrorMessage.jsp">
            <dsp:param name="formHandler" bean="ProfileFormHandler"/>
            <dsp:param name="submitFieldText" value="${saveText}"/>
          </dsp:include>
      
          <dsp:form action="${originatingRequest.requestURI}" method="post"
                    id="atg_store_profilePasswordEditForm" 
                    formid="profilepasswordeditform">
                    
            <dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>
            <dsp:input bean="ProfileFormHandler.changePasswordSuccessURL" type="hidden" value="profile.jsp"/>
            <dsp:input bean="ProfileFormHandler.changePasswordErrorURL" type="hidden"
                       beanvalue="/OriginatingRequest.requestURI"/>
            <dsp:input bean="ProfileFormHandler.cancelURL" type="hidden" value="profile.jsp"/>
            
            <%-- 
              Set this form to require that the supplied password value should be the same
              as the confirm password parameter
            --%>
            <dsp:input bean="ProfileFormHandler.confirmPassword" type="hidden" value="true"/>
      
            <fieldset>
              <ul class="atg_store_basicForm">
                <li>
                  <label for="atg_store_profilePasswordEditOldPassword" class="required">
                    <fmt:message key="myaccount_profilePasswordEdit.oldPassword"/>
                    <span class="required">*</span>
                  </label>
                  <dsp:input bean="ProfileFormHandler.value.oldpassword" maxlength="35"
                             type="password" value="" required="true" iclass="required"
                             name="atg_store_profilePasswordEditOldPassword"
                             id="atg_store_profilePasswordEditOldPassword"/>
                </li>
                <li>
                  <label for="atg_store_profilePasswordEditNewPassword" class="required">
                    <fmt:message key="myaccount_profilePasswordEdit.newPassword"/>
                    <span class="required">*</span>
                  </label>
                  <dsp:input bean="ProfileFormHandler.value.password" maxlength="35"
                             type="password" required="true" iclass="required"
                             name="atg_store_profilePasswordEditNewPassword"
                             id="atg_store_profilePasswordEditNewPassword"/>
                  <span class="example"><fmt:message key="common.passwordCharacters"/></span>                         
                </li>
                <li>
                  <label for="atg_store_profilePasswordEditRetypePassword" class="required">
                    <fmt:message key="common.confirmPassword"/>
                    <span class="required">*</span>
                  </label>
                  <dsp:input bean="ProfileFormHandler.value.confirmpassword" maxlength="35"
                             type="password" required="true" iclass="required"
                             name="atg_store_profilePasswordEditRetypePassword"
                             id="atg_store_profilePasswordEditRetypePassword"/>
                </li>
              </ul>
            </fieldset>
      
            <div class="atg_store_formFooter">
              <div class="atg_store_formKey">
                <span class="required">* <fmt:message key="common.requiredFields"/></span>
              </div>
              
              <%-- Form action buttons --%>
              <div class="atg_store_formActions">
                <fmt:message var="updateButton" key="common.button.saveChanges"/>
                <fmt:message var="cancelText" key="common.button.cancelText"/>
                <fmt:message var="cancelTitle" key="common.button.cancelTitle"/>
                
                <%-- Change password --%>
                <div class="atg_store_formActionItem">
                  <span class="atg_store_basicButton">
                    <dsp:input bean="ProfileFormHandler.changePassword"
                               type="submit" value="${updateButton}"
                               name="atg_store_profilePasswordEditSubmit"
                               id="atg_store_profilePasswordEditSubmit"/>
                  </span>
                </div>
                
                <%-- Cancel --%>
                <div class="atg_store_formActionItem">
                  <span class="atg_store_basicButton secondary">
                    <dsp:input type="submit"
                               value="${cancelText}"
                               bean="ProfileFormHandler.cancel"
                               id="atg_store_profileMyInfoEditCancel"/>
                  </span>
                </div>
              </div>
            </div>
      
          </dsp:form>
        </div>
        
      </div>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/profilePasswordEdit.jsp#2 $$Change: 788278 $--%>

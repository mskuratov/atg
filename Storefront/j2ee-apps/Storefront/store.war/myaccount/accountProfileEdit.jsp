<%-- 
  This page displays edit profile form.
  
  Required parameters:
    None
    
  Optional parameters:
    None 
--%>

<dsp:page>

  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>
  <dsp:importbean bean="/atg/store/droplet/IsEmailRecipient"/>
  <dsp:importbean bean="/atg/store/profile/RegistrationFormHandler"/>
  <dsp:importbean bean="/atg/store/StoreConfiguration"/>
  <dsp:importbean bean="/atg/dynamo/droplet/PossibleValues"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileAdapterRepository"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  
  <%-- Set title for submit button --%>
  <fmt:message  var="submitText" key="common.button.saveProfileText"/>
  
  <crs:pageContainer divId="atg_store_accountEditProfileIntro" 
                     index="false" follow="false"
                     bodyClass="atg_store_myAccountPage atg_store_leftCol"
                     selpage="MY PROFILE">  
                     
    <jsp:attribute name="formErrorsRenderer">
      <%-- Display error messages if any above the accessibility navigation--%>  
      <dsp:include page="gadgets/myAccountErrorMessage.jsp">
        <dsp:param name="formHandler" bean="RegistrationFormHandler"/>
        <dsp:param name="submitFieldText" value="${submitText}"/>
        <dsp:param name="errorMessageClass" value="errorMessage"/>                
      </dsp:include>
    </jsp:attribute>
                       
    <jsp:body>
      
      <%-- Page title --%>
      <div id="atg_store_contentHeader">
        <h2 class="title">
          <fmt:message key="myaccount_profileMyInfoEdit.title" />
        </h2>
      </div>
      
      <%-- Left-hand menu --%>
      <dsp:include page="gadgets/myAccountMenu.jsp">
         <dsp:param name="selpage" value="MY PROFILE" />
      </dsp:include>     
      
      <%-- Edit profile page --%>
      <div class="atg_store_main atg_store_myAccount">
      
        <%-- Set the Date format on the basis of locale   --%>        
        <dsp:getvalueof var="dateFormat" 
                        bean="LocaleTools.userFormattingLocaleHelper.datePatterns.shortWith4DigitYear" />
 
        <dsp:setvalue bean="RegistrationFormHandler.dateFormat" value="${dateFormat}"/>
        <dsp:setvalue bean="RegistrationFormHandler.extractDefaultValuesFromProfile" value="true"/>
      
        <div id="atg_store_profileMyInfoEdit">  
          
          <%-- Show form errors if any --%>
          <div id="atg_store_formValidationError">  
            <dsp:include page="gadgets/myAccountErrorMessage.jsp">
              <dsp:param name="formHandler" bean="RegistrationFormHandler"/>
              <dsp:param name="submitFieldText" value="${submitText}"/>
              <dsp:param name="errorMessageClass" value="errorMessage"/>
            </dsp:include>
          </div>  
      
          <%-- 'Edit profile' form --%>
          <dsp:form action="${originatingRequest.requestURI}" method="post" 
                    formid="atg_store_profileMyInfoEditForm">
            
            <%-- Hidden value required by RegistrationFormHandler --%>
            <dsp:input bean="RegistrationFormHandler.updateSuccessURL" type="hidden" value="profile.jsp"/>
            <dsp:input bean="RegistrationFormHandler.updateErrorURL" type="hidden" beanvalue="/OriginatingRequest.requestURI"/>
            <dsp:input bean="RegistrationFormHandler.cancelURL" type="hidden" value="profile.jsp"/>
      
            <dsp:input type="hidden" bean="RegistrationFormHandler.loginEmailAddress"
                       beanvalue="RegistrationFormHandler.value.email"/>
        
            <ul class="atg_store_basicForm">
              <li>
                <label for="atg_store_profileMyInfoEditEmailAddress" class="required">
                  <fmt:message key="common.email"/>
                  <span class="required">*</span>
            
                </label>
                <dsp:input type="text" iclass="required" maxlength="255"
                           id="atg_store_profileMyInfoEditEmailAddress"
                           bean="RegistrationFormHandler.value.email" required="true"/>
              </li>
              <li>
                <label for="atg_store_profileMyInfoEditFirstName" class="required">
                  <fmt:message key="common.firstName"/>
                  <span class="required">*</span>
                </label>
                <dsp:input id="atg_store_profileMyInfoEditFirstName"
                           iclass="required" maxlength="40" bean="RegistrationFormHandler.value.firstName"
                           type="text" required="true"/>
              </li>
              <li>
                <label for="atg_store_profileMyInfoEditLastName" class="required">
                  <fmt:message key="common.lastName"/>
                  <span class="required">*</span>
                </label>
                <dsp:input type="text" iclass="required" maxlength="40"
                           id="atg_store_profileMyInfoEditLastName"
                           bean="RegistrationFormHandler.value.lastName" required="true"/>
              </li>
            </ul>
                
            <h4 class="atg_store_optionalHeader"><fmt:message key="myaccount_optionalInfo.title"/></h4>
                
            <ul class="atg_store_basicForm">
              <li>
                <label for="atg_store_profileMyInfoEditPostalCode" class="required">
                  <fmt:message key="common.zipOrPostalCode"/>
                  <span class="required">*</span>
                </label>
                <dsp:input type="text" iclass="required" maxlength="10" size="36"
                           id="atg_store_profileMyInfoEditPostalCode"
                           bean="RegistrationFormHandler.value.homeAddress.postalCode" />
              </li>
                
              <li>
                <label for="atg_store_profileMyInfoEditGender">
                  <fmt:message key="common.gender"/>
                </label>
                <dsp:select width="50" name="atg_store_profileMyInfoEditGender"
                            id="atg_store_profileMyInfoEditGender"
                            bean="RegistrationFormHandler.value.gender" >
                  <%-- 
                    Get all possible genders for the profile 
                      
                    Input parameters:
                      itemDescriptorName
                        repository item descriptor to be used
                      propertyName
                        name of property where  
                      
                    Output parameters:
                      values
                        collection of possible values for the given property    
                  --%>
                  <dsp:droplet name="PossibleValues">
                    <dsp:param name="itemDescriptorName" value="user"/>
                    <dsp:param name="propertyName" value="gender"/>
                    <dsp:param name="repository" bean="ProfileAdapterRepository"/>
                    <dsp:param name="returnValueObjects" value="true"/>
                    <dsp:param name="comparator" bean="/atg/userprofiling/ProfileEnumOptionsComparator"/>
                    <dsp:param name="bundle" bean="/atg/multisite/Site.resourceBundle"/>
                    <dsp:oparam name="output">
          
                      <%-- 
                        Get the output parameter of PossibleValues droplet and put it
                        into the forEach tag
                      --%>
                      <dsp:getvalueof var="values" vartype="java.lang.Object" param="values"/>
                      <c:forEach var="gender" items="${values}">
                        <c:choose>
                          <%-- If value is unknown, show title --%>
                          <c:when test="${gender.settableValue == 'unknown'}">
                            <dsp:option value="${gender.settableValue}">
                              <fmt:message key="common.selectGender"/>
                            </dsp:option>
                          </c:when>
                          <%-- Otherwise get possible values from repository --%>
                          <c:otherwise>
                            <dsp:option value="${gender.settableValue}">
                              ${gender.localizedLabel}
                            </dsp:option>
                          </c:otherwise>
                        </c:choose>
                      </c:forEach>
                    </dsp:oparam>
                  </dsp:droplet><%-- End Possible Values --%>
                </dsp:select><%-- End of dsp:select --%>
              </li>
              
              <li>
                <label for="atg_store_profileMyInfoEditDateOfBirth">
                  <fmt:message key="common.DOB"/>
                </label>
                
                <!-- Includes a Date  field having a Locale specific day,month,year pattern -->
                <dsp:include page="/myaccount/gadgets/datePicker.jsp">
                  <dsp:param name="formHandlerComponentMonth" value="/atg/store/profile/RegistrationFormHandler.month"/>
                  <dsp:param name="formHandlerComponentDay" value="/atg/store/profile/RegistrationFormHandler.date"/>
                  <dsp:param name="formHandlerComponentYear" value="/atg/store/profile/RegistrationFormHandler.year"/>
                  <dsp:param name="displayDateLables" value="true"/>
                  <dsp:param name="dayTitleKey" value="myaccount_dateOfBirthAdd.dateOfBirthEventDayTitle"/>
                  <dsp:param name="monthTitleKey" value="myaccount_dateOfBirthAdd.dateOfBirthEventMonthTitle"/>
                  <dsp:param name="yearTitleKey" value="myaccount_dateOfBirthAdd.dateOfBirthEventYearTitle"/>
                  <dsp:param name="startYear" value="-150"/>
                  <dsp:param name="numberOfYears" value="150"/>
                </dsp:include>  
              </li>
      
              <li class="atg_store_formElementGroup option">
                <label for="atg_store_emailCheckBox">
                  <fmt:message key="common.productPromotionalInfo" />

                  <%-- Link to privacy policy popup --%>
                  <dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
                  <fmt:message var="privacyPolicyTitle" key="common.button.privacyPolicyTitle"/>
                  <dsp:a href="${contextPath}/company/privacyPolicyPopup.jsp"
                         target="popup" title="${privacyPolicyTitle}">
                    <fmt:message key="common.button.privacyPolicyText"/>
                  </dsp:a>
                </label>
                
                <%--
                  Check if the user is email recipient
                      
                  Input parameters:
                    email
                       The email address of the current Profile
                  
                  Output parameters:
                    true oparam if current Profile has already subscribed 
                    to receive emails, otherwise false
                --%>
                <dsp:droplet name="IsEmailRecipient">
                  <dsp:param name="email" bean="RegistrationFormHandler.value.email"/>
                  <dsp:oparam name="true">
                    <dsp:input bean="RegistrationFormHandler.previousOptInStatus"
                               type="hidden" value="true"/>
              
                    <dsp:getvalueof var="formExceptions" bean="RegistrationFormHandler.formExceptions"/>
                    <c:choose>
                      <c:when test="${empty formExceptions}">
                        <dsp:input type="checkbox" bean="RegistrationFormHandler.emailOptIn"
                                   checked="true" id="atg_store_emailCheckBox" />
                      </c:when>
                      <c:otherwise>
                        <dsp:input type="checkbox" bean="RegistrationFormHandler.emailOptIn" id="atg_store_emailCheckBox"/>
                      </c:otherwise>
                    </c:choose>
                  </dsp:oparam>
              
                  <dsp:oparam name="false">
                    <dsp:input bean="RegistrationFormHandler.previousOptInStatus"
                               type="hidden" value="false"/>
              
                    <dsp:getvalueof var="formExceptions" bean="RegistrationFormHandler.formExceptions"/>
                    <c:choose>
                      <c:when test="${empty formExceptions}">
                        <dsp:input type="checkbox" bean="RegistrationFormHandler.emailOptIn"
                                   checked="false" id="atg_store_emailCheckBox"/>
                      </c:when>
                      <c:otherwise>
                        <dsp:input type="checkbox" bean="RegistrationFormHandler.emailOptIn" id="atg_store_emailCheckBox"/>
                      </c:otherwise>
                    </c:choose>
                  </dsp:oparam>
                </dsp:droplet><%-- End of IsEmailRecipient Droplet--%>   
              </li>
            </ul>
            
            <div class="atg_store_formFooter">
              <div class="atg_store_formKey">
                <span class="required">* <fmt:message key="common.requiredFields"/></span>
              </div>
              
              <div class="atg_store_formActions">
                <fmt:message var="saveProfileText" key="common.button.saveProfileText"/>
                <fmt:message var="saveProfileTitle" key="common.button.saveProfileTitle"/>
                <fmt:message var="cancelText" key="common.button.cancelText"/>
                <fmt:message var="cancelTitle" key="common.button.cancelTitle"/>
      
                <div class="atg_store_formActionItem">
                
                  <%-- Save profile --%>
                  <span class="atg_store_basicButton">
                    <dsp:input type="submit" 
                               value="${saveProfileText}" title="${saveProfileTitle}"
                               bean="RegistrationFormHandler.update"
                               id="atg_store_profileMyInfoEditSubmit"/>
                  </span>
                </div>
                <div class="atg_store_formActionItem">
                
                  <%-- Cancel --%>
                  <span class="atg_store_basicButton secondary">
                    <dsp:input type="submit" 
                               value="${cancelText}"
                               bean="RegistrationFormHandler.cancel"
                               id="atg_store_profileMyInfoEditCancel"/>
                  </span>
                </div>
              </div>
              
            </div>
          </dsp:form><%-- End of form --%>
        </div>    
      </div>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/accountProfileEdit.jsp#2 $$Change: 788278 $--%>

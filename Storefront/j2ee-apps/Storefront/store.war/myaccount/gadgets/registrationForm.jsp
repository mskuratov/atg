<%--
  Registration form gadget, should be inside of the <form> tag.
  
  Required parameters:
    formHandler
      Form handler to use for registration
    email
      new user email
      
  Optional parameters:
    None         
--%>
<dsp:importbean bean="/atg/core/i18n/LocaleTools"/>
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
<dsp:importbean bean="/atg/dynamo/droplet/PossibleValues"/>
<dsp:importbean bean="/atg/store/StoreConfiguration"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileAdapterRepository"/>
<dsp:importbean bean="/atg/userprofiling/PropertyManager"/>

<dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath"/>

<dsp:importbean bean="/atg/store/profile/RegistrationFormHandler"/>
<dsp:importbean bean="/atg/store/order/purchase/BillingFormHandler" />

<dsp:getvalueof var="formHandler" param="formHandler"/>

<dsp:page>
<p class="atg_store_registrationMessage">
  <fmt:message key="myaccount_registerOnce.title"/>
</p>
  <ul class="atg_store_basicForm">
    
    <!-- Register once text -->
    <li class="atg_store_registerEmail">
      <%-- Only populate the email address field when new user --%>
      <dsp:getvalueof var="email" vartype="java.lang.String" param="email"/>
      <c:if test="${empty email}">
        <dsp:getvalueof var="email" vartype="java.lang.String" bean="${formHandler}.value.email"/>
      </c:if>

      <label for="atg_store_registerEmailAddress" class="required">
        <fmt:message key="common.email" />
        <span class="required">*</span>
      </label>

      <dsp:input bean="${formHandler}.value.email" maxlength="255"
                 iclass="text" type="text" required="true"
                 id="atg_store_registerEmailAddress" value="${email}"/>
    </li>

    <li class="atg_store_registerPassword">
      <label for="atg_store_registerPassword" class="required">
        <fmt:message key="common.createPassword" />
        <span class="required">*</span>
      </label>            
      <fmt:message var="defaultMessage" key="common.passwordCharacters" />
      <dsp:input bean="${formHandler}.value.password"
                 type="password" required="true" iclass="text"
                 id="atg_store_registerPassword" value=""/>
     
       <span class="example">${defaultMessage}</span>
    </li>

    <li class="atg_store_registerConfirmPassword">

      <label for="atg_store_registerRetypePassword" class="required">
        <fmt:message key="common.confirmPassword" />
        <span class="required">*</span>
      </label>

      <dsp:input bean="${formHandler}.value.confirmPassword"
                 type="password" required="true" iclass="text"
                 id="atg_store_registerRetypePassword" value=""/>
      
    </li>

    <li class="atg_store_registerFirstName">
      <label for="atg_store_registerFirstName" class="required">
        <fmt:message key="common.firstName"/>
        <span class="required">*</span>
      </label>         
      <dsp:input bean="${formHandler}.value.firstName"
                 type="text" required="true" iclass="text"
                 id="atg_store_registerFirstName"/>
   </li>

   <li class="atg_store_registerLastName">
     <label for="atg_store_registerLastName" class="required">
        <fmt:message key="common.lastName"/>
        <span class="required">*</span>
      </label>
    
      <dsp:input bean="${formHandler}.value.lastName"
                 type="text" required="true" iclass="text"
                 id="atg_store_registerLastName">
             </dsp:input>
   </li>
   </ul>

   <h4 class="atg_store_optionalHeader"><fmt:message key="myaccount_optionalInfo.title"/></h4>

   <ul class="atg_store_basicForm">
   <li class="atg_store_registerPostalCode">          
     <label for="atg_store_registerPostalCode">
       <fmt:message key="common.postalZipCode"/>
     </label>
    
    <c:choose>
      <%--
        We should check postal code here 
        because of changes between value property in 
        BillingFormHandler and RegistrationFormHandler. 
        
        In the first case it is a Map,
        and in the second case it is ProfileFormHashtable
       --%>
      <c:when test="${formHandler == 'BillingFormHandler'}">
        <dsp:input bean="${formHandler}.value.postalCode"
                   type="text" maxlength="10" iclass="text"
                   id="atg_store_registerPostalCode"/>
      </c:when>
      <c:otherwise>
        <dsp:input bean="${formHandler}.value.homeAddress.postalCode"
                   type="text" maxlength="10" iclass="text"
                   id="atg_store_registerPostalCode"/>
      </c:otherwise> 
    </c:choose>    
    </li>
    
    <li class="atg_store_registerGender">
      <label for="atg_store_registerGender">
        <fmt:message key="common.gender"/>
      </label>
    
     <dsp:select bean="${formHandler}.value.gender"
                 id="atg_store_registerGender">
       <dsp:droplet name="PossibleValues">
         <dsp:param name="itemDescriptorName" value="user"/>
         <dsp:param name="propertyName" value="gender"/>
         <dsp:param name="repository" bean="ProfileAdapterRepository"/>
         <dsp:param name="returnValueObjects" value="true"/>
         <dsp:param name="comparator" bean="/atg/userprofiling/ProfileEnumOptionsComparator"/>
         <dsp:param name="bundle" bean="/atg/multisite/Site.resourceBundle"/>
         
         <dsp:oparam name="output">
           <dsp:getvalueof var="selectGender" vartype="java.lang.Object" param="values"/>
           <c:forEach var="element" items="${selectGender}">
             <dsp:param name="element" value="${element}"/>
             <c:choose>
               <c:when test="${element.settableValue == 'unknown'}">
                 <dsp:option paramvalue="element.settableValue">
                   <fmt:message key="common.selectGender"/>
                 </dsp:option>
               </c:when>
               <c:otherwise>
                 <dsp:option paramvalue="element.settableValue">
                   ${element.localizedLabel}
                 </dsp:option>
               </c:otherwise>
             </c:choose>
           </c:forEach>
         </dsp:oparam>
       </dsp:droplet><%-- End Possible Values --%>
     </dsp:select>
   </li>
   
   <li class="atg_store_registerDateofBirth">
      <label for="atg_store_registerDateOfBirth">
        <fmt:message key="common.DOB"/>
      </label>

        <dsp:getvalueof var="formHandlerName" bean="${formHandler}.name"/>

        <c:choose>
          <c:when test='${formHandlerName == "BillingFormHandler"}'>
            <c:set var="formHandlerPath" value="/atg/store/order/purchase/BillingFormHandler"/>
          </c:when>
          <c:otherwise>
            <c:set var="formHandlerPath" value="/atg/store/profile/RegistrationFormHandler"/>          
          </c:otherwise>
        </c:choose>


        
        <!-- Includes a Date field having a Locale specific day,month,year pattern -->
        <dsp:include page="/myaccount/gadgets/datePicker.jsp">
          <dsp:param name="formHandlerComponentMonth" value="${formHandlerPath}.month"/>
          <dsp:param name="formHandlerComponentDay" value="${formHandlerPath}.date"/>
          <dsp:param name="formHandlerComponentYear" value="${formHandlerPath}.year"/>
          <dsp:param name="displayDateLables" value="true"/>
          <dsp:param name="dayTitleKey" value="myaccount_dateOfBirthAdd.dateOfBirthEventDayTitle"/>
          <dsp:param name="monthTitleKey" value="myaccount_dateOfBirthAdd.dateOfBirthEventMonthTitle"/>
          <dsp:param name="yearTitleKey" value="myaccount_dateOfBirthAdd.dateOfBirthEventYearTitle"/>
          <dsp:param name="startYear" value="-150"/>
          <dsp:param name="numberOfYears" value="150"/>
        </dsp:include> 
      
    </li>
   
   
  <li class="atg_store_registerReferer">
    <label for="atg_store_registerReferer">
      <fmt:message key="myaccount_registration.hearAboutUs"/>
    </label>
  
    <dsp:select bean="${formHandler}.value.referralSource"
                id="atg_store_registerReferer">
      <%-- Show all the possible options from the repository --%>
      <dsp:droplet name="PossibleValues">
        <dsp:param name="itemDescriptorName" value="user"/>
        <dsp:param name="propertyName" value="referralSource"/>
        <dsp:param name="repository" bean="ProfileAdapterRepository"/>
        <dsp:param name="returnValueObjects" value="true"/>
        <dsp:param name="comparator" bean="/atg/userprofiling/ProfileEnumOptionsComparator"/>        
        <dsp:oparam name="output">
          <dsp:getvalueof var="hearAboutUs" vartype="java.lang.Object" param="values"/>
          <c:forEach var="element" items="${hearAboutUs}">
            <dsp:param name="element" value="${element}"/>
            <c:choose>
              <c:when test="${element.settableValue == 'Unknown'}">
                <dsp:option paramvalue="element.settableValue">
                  <fmt:message key="myaccount_registration.hearAboutUs"/>
                </dsp:option>
              </c:when>
              <c:otherwise>
                <dsp:option paramvalue="element.settableValue">
                  ${element.localizedLabel}
                </dsp:option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </dsp:oparam>
      </dsp:droplet><%-- End Possible Values --%>
    </dsp:select>
  </li>

  <li class="option">
      <dsp:input type="checkbox" bean="${formHandler}.emailOptIn"
                 checked="false" value="true"
                 id="atg_store_registerEmailOptIn">
        </dsp:input>
    <label for="atg_store_registerEmailOptIn">
      <fmt:message key="common.productPromotionalInfo" />
        <%-- Link to privacy policy popup --%>
        <fmt:message var="privacyPolicyTitle" key="common.button.privacyPolicyTitle"/>
          <dsp:a href="${contextPath}/company/privacyPolicyPopup.jsp"
                 target="popup" title="${privacyPolicyTitle}">
           <fmt:message key="common.button.privacyPolicyText"/>
         </dsp:a>
         </label>
       </li> 
  </ul>  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/registrationForm.jsp#2 $$Change: 788278 $--%>
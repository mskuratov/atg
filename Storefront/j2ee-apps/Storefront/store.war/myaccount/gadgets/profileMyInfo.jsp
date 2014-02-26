<%--
  This page renders all general information associated with a profile.
  
  Required parameters:
    None
    
  Optional parameters:
    None  
--%>
<dsp:page>
  <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/store/profile/RegistrationFormHandler"/>

  <fmt:message var="editProfileTitle" key="myaccount_profileMyInfoEditLinks.button.editProfileTitle"/>
  <fmt:message var="editProfileText" key="myaccount_profileMyInfoEditLinks.button.editProfileText"/>
    
    <h3 class="atg_store_subHeadCustom atg_store_myInfoHeader"><fmt:message key="myaccount_profileMyInfo.myInformation"/></h3>
    
    <dl class="atg_store_infoList">
      <dt><fmt:message key="common.email"/></dt>
      <dd><dsp:valueof bean="Profile.email"/></dd>

      <dt><fmt:message key="common.firstName"/></dt>
      <dd><dsp:valueof bean="Profile.firstName"/>

      <dt><fmt:message key="common.lastName"/></dt>
      <dd><dsp:valueof bean="Profile.lastName"/>

      <dt>
        <fmt:message key="common.zipOrPostalCode"/>
      </dt>
      <dd><dsp:valueof bean="Profile.homeAddress.postalCode"/></dd>

      <dt>
        <fmt:message key="common.gender"/>
      </dt>
      <dd>
        <dsp:getvalueof bean="Profile.gender" var="profileGender"/>
        <fmt:message key="${profileGender}"/>
      </dd>
      
      <dt><fmt:message key="common.birthday"/></dt>
      <dd><dsp:getvalueof var="dateofBirth" vartype="java.util.Date" bean="Profile.dateofBirth"/>
        <c:choose>
          <c:when test="${not empty dateofBirth}">
            <dsp:getvalueof var="dateFormat" 
                            bean="LocaleTools.userFormattingLocaleHelper.datePatterns.shortWith4DigitYear" />
                        
            <fmt:formatDate value="${dateofBirth}" pattern="${dateFormat}"/>
          </c:when>
          <c:otherwise>
            
          </c:otherwise>
        </c:choose>
      </dd>

      <dt><fmt:message key="common.receiveEmails"/></dt>
      <dd>
        <%--
          Check if the user is email recipient
          
          Input parameters:
            email
               The email address of the current Profile
          
          Output parameters:
            true oparam if current Profile has already subscribed 
            to receive emails, otherwise false
         --%>
        <dsp:droplet name="/atg/store/droplet/IsEmailRecipient">
          <dsp:param name="email" bean="Profile.email"/>
          <dsp:oparam name="true"><fmt:message key="common.yes"/></dsp:oparam>
          <dsp:oparam name="false"><fmt:message key="common.no"/></dsp:oparam>
        </dsp:droplet>
      </dd>
    </dl>
    
    <%-- link to 'profile edit' page --%>
    <div class="atg_store_formActions atg_store_myInfoActions">
      <a title="${editProfileTitle}" 
         href="accountProfileEdit.jsp" 
         class="atg_store_basicButton"><span>${editProfileText}</span></a>
    </div>

</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/profileMyInfo.jsp#1 $$Change: 735822 $--%>
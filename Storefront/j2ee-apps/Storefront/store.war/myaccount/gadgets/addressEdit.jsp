<%--
  This page includes the logic as well as presentation for adding a new address by using
  ProfileFormHandler and its various methods.

  Required parameters:
    successURL
      URL to redirect to, after a successful creation of a new address or update
      of an existing address.
    cancelURL
      URL to redirect to, after user clicks on 'Cancel' button.
    firstLastRequired
      boolean to determine if first name and last name fields are required.
    addEditMode
      set to 'edit' when a current address is being modified, otherwise it is assumed
      a new address is being added.
    restrictionDroplet
      passed to addressAddEdit.jsp in order to provide different restriction
      droplets for the country and state picker.
    nickName
      the nickName of address to edit. If new address is created nickName can be omitted.
      
  Optional parameters:      
    preFillValues
      if true, address fields will be prefilled with values,
      otherwise they will be blank.
    
--%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  
  <dsp:getvalueof var="addEditMode" param="addEditMode"/>
  <dsp:getvalueof var="successURL" param="successURL"/>
  
  <c:if test="${not empty successURL}">
    <%-- Value of successURL would be coming from a previous page --%>
    <dsp:setvalue bean="ProfileFormHandler.newAddressSuccessURL" paramvalue="successURL"/>
    <dsp:setvalue bean="ProfileFormHandler.updateAddressSuccessURL" paramvalue="successURL"/>
  </c:if>
 
  <%-- Start of form --%>
  <dsp:form action="${pageContext.request.requestURI}" 
            method="post" formid="atg_store_editAddress">
    
    <dsp:getvalueof id="originatingRequestURL" bean="/OriginatingRequest.requestURI"/>
    <dsp:getvalueof var="successURL" param="successURL" vartype="java.lang.String"/>

    <dsp:input type="hidden" bean="ProfileFormHandler.newAddressSuccessURL"
               beanvalue="ProfileFormHandler.newAddressSuccessURL"/>
    <dsp:input type="hidden" bean="ProfileFormHandler.newAddressErrorURL"
               value="${originatingRequestURL}?preFillValues=true&successURL=${successURL}&addEditMode=add"/>

    <%-- 
      Cancel URL could be pass outside of the page. If not, set 
      URL to /myaccount/addressBook.jsp page
     --%>
    <dsp:getvalueof var="cancelURL" param="cancelURL" vartype="java.lang.String"/>

    <c:if test="${cancelURL eq null || cancelURL eq ''}">
      <dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
      <c:set var="cancelURL" value="${contextPath}/myaccount/addressBook.jsp"/>
    </c:if>
     
    <dsp:input bean="ProfileFormHandler.cancelURL" value="${successURL}" 
               type="hidden"/>

    <dsp:input type="hidden" bean="ProfileFormHandler.updateAddressSuccessURL"
               beanvalue="ProfileFormHandler.updateAddressSuccessURL"/>
    <dsp:input type="hidden" bean="ProfileFormHandler.updateAddressErrorURL" 
               value="${originatingRequestURL}?successURL=${successURL}&addEditMode=edit"/>
               
    <dsp:input bean="ProfileFormHandler.editValue.ownerId" beanvalue="Profile.id"
                 type="hidden"/>

    <%-- Start inclusion of errorPage for handling form errors --%>
    <c:choose>
      <c:when test="${addEditMode == 'edit'}">
        <dsp:getvalueof var="preFillValuesVar" value="true" vartype="java.lang.Boolean"/>
        <dsp:setvalue bean="ProfileFormHandler.editAddress" paramvalue="nickName"/> 
      </c:when>
      <c:otherwise>        
        <c:choose>
          <c:when test="${empty param.preFillValues}">
            <dsp:getvalueof var="preFillValuesVar" value="false" vartype="java.lang.Boolean"/>
          </c:when>
          <c:otherwise>
            <dsp:getvalueof var="preFillValuesVar" value="${param.preFillValues}" vartype="java.lang.Boolean"/>
          </c:otherwise>
        </c:choose>
      </c:otherwise>
    </c:choose>
    
    <%-- Display error messages if any --%>
    <fmt:message var="submitText" key="common.button.saveAddressText"/>
    <div id="atg_store_formValidationError">
      <dsp:include page="myAccountErrorMessage.jsp">
        <dsp:param name="formHandler" bean="ProfileFormHandler"/>
        <dsp:param name="submitFieldText" value="${submitText}"/>
        <dsp:param name="errorMessageClass" value="errorMessage"/>
      </dsp:include>
    </div>
    
    <fieldset>
      <ul class="atg_store_basicForm">
        
        <%-- Start adding nickname field according to the addEdit mode --%>
        <c:choose>
          <c:when test="${addEditMode == 'edit'}">
            <li>
              <dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="true"/>
              <label for="atg_store_editAddressNickname" class="required">
                <fmt:message key="common.nicknameThisAddress"/>
              </label>
              <dsp:input type="hidden" bean="ProfileFormHandler.editValue.nickname"/>     
              <dsp:input type="text" bean="ProfileFormHandler.editValue.newNickname"
                         id="atg_store_editAddressNickname" maxlength="42" 
                         required="true" iclass="required"/>
            </li>
          </c:when>
          <c:otherwise>
          <li>
            <label for="atg_store_editAddressNickname" class="required">
              <fmt:message key="common.nicknameThisAddress"/>
            </label>
            <dsp:getvalueof var="nname" bean="ProfileFormHandler.editValue.nickname"/>
           	<c:choose>
       			<c:when test="${empty nname}">
        			<dsp:input type="text" bean="ProfileFormHandler.editValue.nickname"
                       id="atg_store_editAddressNickname" value="" 
                       iclass="required" maxlength="42">
                    </dsp:input>
       			</c:when>
       			<c:otherwise>
					<dsp:input type="text" bean="ProfileFormHandler.editValue.nickname"
                    		   id="atg_store_editAddressNickname"
                    		   iclass="required" maxlength="42">
            		</dsp:input>
                </c:otherwise>
    		</c:choose>            
          </li>
          </c:otherwise>
        </c:choose>

        <%-- Start checking firstName lastName required --%>
        <dsp:getvalueof var="firstLastRequired" param="firstLastRequired"/>
        <c:choose>
          <c:when test="${firstLastRequired == 'false'}">
            <dsp:getvalueof var="hideFirstLast" value="true"/>
          </c:when>
          <c:when test="${firstLastRequired == 'true'}">
            <dsp:getvalueof var="hideFirstLast" value="false"/>
          </c:when>
        </c:choose>
  
        <%-- Start inclusion of addressAddEdit for rendering other parameters of the form --%>
        <dsp:include page="/global/gadgets/addressAddEdit.jsp">
          <dsp:param name="formHandlerComponent" value="/atg/userprofiling/ProfileFormHandler.editValue"/>
          <dsp:param name="checkForRequiredFields" value="true"/>
          <dsp:param name="restrictionDroplet" param="restrictionDroplet"/>
          <dsp:param name="hideNameFields" value="${hideFirstLast}"/>
          <dsp:param name="preFillValues" value="${preFillValuesVar}"/>
          <dsp:param name="allowDefaultAssignation" value="true"/>        
        </dsp:include>
      </ul>
    </fieldset>
    
    <div class="atg_store_formFooter">
      <div class="atg_store_formKey">
        <span class="required">* <fmt:message key="common.requiredFields"/></span>
      </div>
  
      <%-- Start inclusion of buttons according to the addEdit mode --%>
      <div class="atg_store_formActions">
        <fmt:message var="cancelText" key="common.button.cancelText"/>
        <fmt:message var="cancelTitle" key="common.button.cancelTitle"/>
        <fmt:message var="saveText" key="common.button.saveAddressText"/>
        <fmt:message var="saveTitle" key="common.button.saveAddressTitle"/>
  
        <%-- Start of c:choose. This output shows button base on the parameter addEditMode --%>
        <c:choose>
          <c:when test="${addEditMode == 'edit'}">
            <%-- Edit address button --%>
            <div class="atg_store_formActionItem">
              <span class="atg_store_basicButton">
                <dsp:input bean="ProfileFormHandler.updateAddress" 
                           id="atg_store_editAddressSubmit"
                           type="submit" title="${saveTitle}" value="${saveText}"/>
              </span>
            </div>
            
            <%-- Cancel button --%>
            <div class="atg_store_formActionItem">
              <span class="atg_store_basicButton secondary">
                <dsp:input bean="ProfileFormHandler.cancel" 
                           id="atg_store_editAddressCancel"
                           type="submit" title="${cancelTitle}" value="${cancelText}"/>
              </span>
            </div>
          </c:when>
          <c:otherwise>
            <%-- Save Address button --%>
            <div class="atg_store_formActionItem">
              <span class="atg_store_basicButton">
                <dsp:input bean="ProfileFormHandler.newAddress" id="atg_store_editAddressSubmit"
                           type="submit" title="${saveTitle}" value="${saveText}" />
              </span>
            </div>
  
            <%-- Cancel button --%>                 
            <dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
            <div class="atg_store_formActionItem">
              <dsp:a href="${cancelURL}" iclass="atg_store_basicButton secondary">
                <span><fmt:message key="common.button.cancelText"/></span>
              </dsp:a>
            </div>  
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </dsp:form>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/addressEdit.jsp#2 $$Change: 788278 $ --%>

<%--
  This page displays error messages for the specified form handler. For all 'missing required
  fields' and 'missing required address' errors it displays only a single message.
   
  Required parameters:
    formHandler
      The form handler whose errors we display
      
  Optional parameters:
    submitFieldKey
      The value for the text that is displayed on the form's submit button. Used
      to tell user which button should be pressed to submit form's content again.
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/store/profile/RequestBean"/>
  
  <dsp:getvalueof id="formHandler" param="formHandler"/>
  
  <%-- 
    Reset miss_req_value as it was set to true while rendering errors
    above the page for accessibility.
   --%>
  <dsp:setvalue bean="RequestBean.values.miss_req_value" value="false"/>
  
  <%-- 
    Usually using ErrorMessageForEach droplet is a nice shortcut to get the 
    error messages.  In this case we need to weed out specific error messages 
    because we share a page with another form.
       
    Also we want to display only a single error in cases where one or 
    more required properties have been omitted from the form.  To handle this 
    case we use the RequestBean to store page values useful only for this request.  
  --%> 
  <dsp:getvalueof id="submitFieldKey" param="submitFieldKey"/>

  <dsp:getvalueof var="formExceptions" vartype="java.lang.Object" param="formHandler.formExceptions"/>
  <c:if test="${not empty formExceptions}">
    <div id="atg_store_formValidationError" class="atg_store_formValidationError">
    
      <%-- Iterate through form exceptions --%>
      <c:forEach var="formException" items="${formExceptions}">
        <dsp:param name="formException" value="${formException}"/>
        <%-- Check the error message code to see what we should do --%>
        <dsp:getvalueof var="errorCode" param="formException.errorCode"/>
        <c:choose>

          <c:when test="${errorCode == 'missingRequiredValue'}">
            <%-- Required field is missing --%>
            <dsp:getvalueof var="miss_req_value" bean="RequestBean.values.miss_req_value"/>
            <c:choose>
              <c:when test="${miss_req_value == 'true'}">
                <%-- We've already spoken for missing values --%>
              </c:when>
              <c:otherwise>
                <%-- Show the default message when an error is made --%>
                <fmt:message var="submitFieldText" key="${submitFieldKey}"/>
                <div class="errorMessage">
                  <fmt:message key="common.additionalInfoRequired">
                    <fmt:param value="${submitFieldText}"/>
                  </fmt:message>
                </div>
                <%-- Mark that we've just spoken for missing values --%>
                <dsp:setvalue bean="RequestBean.values.miss_req_value" value="true"/>
              </c:otherwise>
            </c:choose><%-- End c:choose to see what field was not filled in --%>
          </c:when>

          <c:when test="${errorCode == 'missingRequiredAddressProperty'}">
            <%--Missing Required address property --%>
            <dsp:getvalueof var="miss_req_value" bean="RequestBean.values.miss_req_value"/>
            <c:choose>
              <c:when test="${miss_req_value == 'true'}">
                <%-- We've already spoken for missing values --%>
              </c:when>
              <c:otherwise>
                <%-- Show the default message when an error is made --%>
                <fmt:message var="submitFieldText" key="common.button.continueCheckoutText"/>
                <div class="errorMessage">
                  <fmt:message key="common.additionalInfoRequired">
                    <fmt:param value="${submitFieldText}"/>
                  </fmt:message>
                </div>
                <%-- Mark that we've just spoken for missing values --%>
                <dsp:setvalue bean="RequestBean.values.miss_req_value" value="true"/>
              </c:otherwise>
            </c:choose><%-- End c:choose to see what field was not filled in --%>
          </c:when>

          <c:otherwise>
            <div class="errorMessage">
              <dsp:valueof param="formException.message" valueishtml="true">
                <fmt:message key="common.errorMessageDefault"/>
              </dsp:valueof>
            </div>
          </c:otherwise>
        </c:choose>
      </c:forEach>
    </div>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/displayErrorMessage.jsp#1 $$Change: 735822 $--%>

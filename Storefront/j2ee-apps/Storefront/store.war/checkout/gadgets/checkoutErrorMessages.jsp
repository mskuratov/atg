<%--
  This page displays all errors collected by the form handler specified.
  It will display only one 'additional information required' message per page.

  Required parameters:
    formHandler
      The form handler whose errors we will display.

  Optional parameters:
    divid
      A different div id to wrap the error in.
    submitFieldText
      Text on the "commit" field which is added to the message shown for missing required data after the word "click"
      (for example, "click Save").
  --%>

<dsp:page>
  <dsp:importbean bean="/atg/store/profile/RequestBean"/>

  <dsp:getvalueof var="defaultError" vartype="java.lang.String" value="false" scope="request"/>
  <dsp:getvalueof var="formExceptions" vartype="java.lang.Object" param="formHandler.formExceptions"/> 
  <dsp:getvalueof id="divid" param="divid"/>
  <dsp:getvalueof id="submitFieldText" param="submitFieldText"/>
  
  <%-- 
    Reset miss_req_value as it was set to true while rendering errors
    above the page for accessibility.
   --%>
  <dsp:setvalue bean="RequestBean.values.miss_req_value" value="false"/>

  <%-- Define default value for divid. --%>
  <c:if test="${empty divid}">
    <c:set var="divid" value="atg_store_formValidationError"/>
  </c:if>


  <%-- Define default value for submitFieldText. --%>
  <c:if test="${empty submitFieldText}">
    <fmt:message var="submitFieldText" key="common.button.continueCheckoutText"/>
  </c:if>

  <%-- Draw error messages pane only if we have something to display. --%>
  <c:if test="${not empty formExceptions}">
    <div id="${divid}">
      <c:forEach var="formException" items="${formExceptions}">
        <dsp:param name="formException" value="${formException}"/>
        <%-- Check the error message code to see what we should do --%>
        <dsp:getvalueof var="errorCode" vartype="java.lang.String" param="formException.errorCode"/>
  
        <c:choose>
          <c:when test='${errorCode == "missingRequiredValue" || errorCode == "missingRequiredAddressProperty"}'>
            <%--
              We should display only one 'Additional Information Required' error per page (request).
              RequestBean.values.miss_req_value will be set to 'true', if we've displayed this message already.
            --%>
            <dsp:getvalueof var="missReqValue" vartype="java.lang.String" bean="RequestBean.values.miss_req_value"/>
  
            <c:choose>
              <c:when test='${missReqValue == "true"}'>
                <%-- We've already spoken for missing values --%>
              </c:when>
              <c:otherwise>
                <%-- Show the default message when an error is made --%>
                <div class="errorMessage">
                  <fmt:message key="common.additionalInfoRequired">
                    <fmt:param value="${submitFieldText}"/>
                  </fmt:message>
                </div>
                <%-- Mark that we've just spoken for missing values --%>
                <dsp:setvalue bean="RequestBean.values.miss_req_value" value="true"/>
              </c:otherwise>
            </c:choose>
          </c:when>
          <c:otherwise>
            <%-- It's not a 'Missing Required Value' error, just display it to user. --%>
            <dsp:getvalueof var="defaultError" vartype="java.lang.String" value="true" scope="request"/>
            <div class="errorMessage">
              <dsp:valueof param="formException.message" valueishtml="true">
                <fmt:message key="common.errorMessageDefault" />
              </dsp:valueof>
            </div>
          </c:otherwise>
        </c:choose>
      </c:forEach>
    </div>
  </c:if>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/checkoutErrorMessages.jsp#2 $$Change: 788278 $--%>

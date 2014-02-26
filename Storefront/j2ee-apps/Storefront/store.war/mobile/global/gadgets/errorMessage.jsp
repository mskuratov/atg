<%--
  This page displays all error messages (exceptions) collected by the FormHandler(s) specified.
  NOTE:
    1) It displays only the errors (exceptions) with messages.
    2) Errors with code="missingRequiredValue" are NOT displayed.
    3) For the error with code="errorInvalidCreditCard" it sets the request-scoped variable
       "errorCard" with the value from "formException.propertyName".

  Page includes:
    None

  Optional parameters: 
    formHandler
      The FormHandler whose errors are displayed.
    formHandlers
      Collection of form handlers beans.
    NOTE: The "formHandler" and "formHandlers" parameters are mutually exclusive!
--%>
<dsp:page>
  <dsp:getvalueof var="formHandler" param="formHandler"/>
  <dsp:getvalueof var="formHandlers" param="formHandlers"/>
  <jsp:useBean id="mergedFormExceptions" class="java.util.LinkedHashMap"/>

  <%-- If user doesn't specify formHandlers, we create it automatically --%>
  <%-- It will contain single value of form handler with any key --%>
  <c:if test="${empty formHandlers}">
    <jsp:useBean id="formHandlers" class="java.util.HashMap"/>
    <c:set target="${formHandlers}" property="Single handler" value="${formHandler}"/>
  </c:if>

  <%-- Note, that collection of all handlers form exceptions can contain dublicates. --%>
  <%-- To get rid of them, we process all exceptions and put them to the merged collection --%>
  <c:forEach var="handler" items="${formHandlers}">
    <c:forEach var="formException" items="${handler.value.formExceptions}">
      <c:set target="${mergedFormExceptions}" property="${formException.errorCode}" value="${formException}"/>
    </c:forEach>
  </c:forEach>

  <c:if test="${not empty mergedFormExceptions}">
    <c:set var="errorsWithMessageExist" value="${false}"/>
    <c:forEach var="formExceptionEntry" items="${mergedFormExceptions}">
      <c:set var="formException" value="${formExceptionEntry.value}"/>
      <c:set var="msg" value="${formException.message}"/>
      <c:set var="errorCode" value="${formException.errorCode}"/>
      <c:if test="${errorCode == 'errorInvalidCreditCard'}">
        <c:set var="errorCard" value="${formException.propertyName}" scope="request"/>
      </c:if>
      <c:if test="${errorCode != 'missingRequiredValue' && not empty msg && fn:length(msg) > 0}">
        <c:set var="errorsWithMessageExist" value="${true}"/>
      </c:if>
    </c:forEach>
    <c:if test="${errorsWithMessageExist}">
      <div id="formValidationError">
        <c:forEach var="formExceptionEntry" items="${mergedFormExceptions}">
          <c:set var="formException" value="${formExceptionEntry.value}"/>
          <c:set var="msg" value="${formException.message}"/>
          <c:if test="${not empty msg && fn:length(msg) > 0}">
            <div class="errorMessage">
              <dsp:valueof value="${msg}" valueishtml="true"/>
            </div>
          </c:if>
        </c:forEach>
      </div>
    </c:if>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/global/gadgets/errorMessage.jsp#1 $$Change: 742374 $--%>

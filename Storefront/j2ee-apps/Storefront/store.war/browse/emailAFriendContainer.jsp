<%--
  Container page for email a friend.
  
  Required Parameters: 
    product
      Repository item of the Product being browsed.
    productId
      Repository Id of the Product being browsed.
    categoryId  
      Repository Id of the Category to which the Product being browsed belongs.
    templateUrl
      The template to be used as the email content.
--%>

<dsp:page>

  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/store/email/EmailAFriendFormHandler"/>
  
  <dsp:getvalueof var="productId" param="product.id" />
  <dsp:getvalueof var="categoryId" param="categoryId" />
  <dsp:getvalueof var="templateUrl" param="templateUrl" />
  
  <dsp:include page="/browse/gadgets/emailAFriendProductDetails.jsp">
    <dsp:param name="product" param="product" />
  </dsp:include>
  
  <%-- ************************* begin email info form ************************* --%>
  <dsp:form action="${originatingRequest.requestURI}?productId=${param.productId}"
            method="post" name="emailFriend" formid="emailafriendform">
  
    <dsp:getvalueof var="templateUrl" param="templateUrl" />
    <c:if test="${not empty templateUrl}">
      <dsp:input bean="EmailAFriendFormHandler.templateUrl" 
                 value="${originatingRequest.contextPath}${templateUrl}" type="hidden"/>
    </c:if>
  
    <dsp:input bean="EmailAFriendFormHandler.productId" value="${param.productId}" type="hidden"/>
    <fmt:message key="browse_emailAFriend.defaultSubject" var="defaultSubjectText"/>
    <dsp:input bean="EmailAFriendFormHandler.subject" value="${defaultSubjectText}" type="hidden"/>
  
    <c:url var="successUrl" value="/browse/emailAFriendConfirm.jsp">
      <c:param name="productId" value="${param.productId}"/>
      <c:param name="categoryId" value="${param.categoryId}"/>
    </c:url>
    <dsp:input bean="EmailAFriendFormHandler.successURL" type="hidden" value="${successUrl}"/>
    
    <c:url var="errorUrl" value="/browse/emailAFriend.jsp">
      <c:param name="productId" value="${param.productId}"/>
      <c:param name="categoryId" value="${param.categoryId}"/>
    </c:url>
    <dsp:input bean="EmailAFriendFormHandler.errorURL" type="hidden" value="${errorUrl}"/>
  
    <dsp:getvalueof var="url" vartype="java.lang.String" param="product.template.url"/>
    <c:choose>
      <c:when test="${not empty url}">
        <%-- Product template is set --%>
        <c:set var="cancelUrlTarget" value="${originatingRequest.contextPath}${url}"/>
        <dsp:input bean="EmailAFriendFormHandler.cancelURL" type="hidden"
                   value="${cancelUrlTarget}?productId=${param.productId}&categoryId=${param.categoryId}"/>
      </c:when>
      <c:otherwise>
        <%-- Product template not set --%>
        <dsp:input bean="EmailAFriendFormHandler.cancelURL" type="hidden"
                   value="${originatingRequest.requestURI}?productId=${param.productId}&categoryId=${param.categoryId}"/>
      </c:otherwise>
    </c:choose>
    <%-- Show Form Errors, note this fragment already adds a table row --%>
    <dsp:include page="/global/gadgets/displayErrorMessage.jsp">
      <dsp:param name="formHandler" bean="EmailAFriendFormHandler"/>
      <dsp:param name="submitFieldKey" value="common.button.sendText"/>
    </dsp:include>
  
    <p class="atg_store_pageDescription"><fmt:message key="browse_emailAFriendFormInputs.subtitleText"/></p>
  

    <ul class="atg_store_basicForm atg_store_emailAFriend">
    
      <li class="atg_store_recipientName">
        <label for="atg_store_emailAFriendRecipientName" class="required">
          <fmt:message key="browse_emailAFriend.recipientName"/>
          <span class="required">*</span>
        </label>
        <dsp:input bean="EmailAFriendFormHandler.recipientName" required="true" type="text"
                   id="atg_store_emailAFriendRecipientName" maxlength="100"/>
      </li>
      
      <li class="atg_store_recipientEmailAddress">
        <label for="atg_store_emailAFriendRecipientEmailAddress" class="required">
          <fmt:message key="browse_emailAFriend.recipientEmail"/>
          <span class="required">*</span>
        </label>
        <dsp:input bean="EmailAFriendFormHandler.recipientEmail" required="true" type="text"
                   id="atg_store_emailAFriendRecipientEmailAddress" maxlength="255"/>
      </li>
  
      <dsp:getvalueof var="transient" bean="Profile.transient"/>
      <c:choose>
        <c:when test="${transient == 'true'}">
        
          <li class="atg_store_senderName">
            <label for="atg_store_emailAFriendSenderName" class="required">
              <fmt:message key="browse_emailAFriend.senderName"/>
              <span class="required">*</span>
            </label>
            <dsp:input bean="EmailAFriendFormHandler.senderName" required="true" type="text"
                       id="atg_store_emailAFriendSenderName" maxlength="100"/>
          </li>
  
          <li class="atg_store_senderEmailAddress">
            <label for="atg_store_emailAFriendSenderEmailAddress" class="required">
              <fmt:message key="browse_emailAFriend.senderEmail"/>
              <span class="required">*</span>
            </label>
            <dsp:input bean="EmailAFriendFormHandler.senderEmail" required="true" type="text"
                       id="atg_store_emailAFriendSenderEmailAddress" maxlength="255"/>
          </li>
          
        </c:when>
        <c:otherwise>
          <dsp:getvalueof var="formExceptions" bean="EmailAFriendFormHandler.formExceptions"/>
          <c:choose>
            <c:when test="${empty formExceptions}">
              <dsp:getvalueof var="senderFirstName" vartype="java.lang.String" bean="Profile.firstName"/>
              <dsp:getvalueof var="senderLastName" vartype="java.lang.String" bean="Profile.lastName"/>
              
              <c:set var="senderFullName">
                ${fn:trim(senderFirstName)} ${fn:trim(senderLastName)}
              </c:set>
              
              <dsp:getvalueof var="senderEmail" vartype="java.lang.String" bean="Profile.email"/>
  
              <li class="atg_store_senderName">
                <label for="atg_store_emailAFriendSenderName" class="required">
                  <fmt:message key="browse_emailAFriend.senderName"/>
                  <span class="required">*</span>
                </label>
                
                <dsp:input bean="EmailAFriendFormHandler.senderName" required="true"
                           id="atg_store_emailAFriendSenderName"
                           type="text"value="${senderFullName}" maxlength="100"/>
              </li>
  
              <li class="atg_store_senderEmailAddress">
                <label for="atg_store_emailAFriendSenderEmailAddress" class="required">
                  <fmt:message key="browse_emailAFriend.senderEmail"/>
                  <span class="required">*</span>
                  
                </label>
                <dsp:input bean="EmailAFriendFormHandler.senderEmail" required="true"
                           id="atg_store_emailAFriendSenderEmailAddress"
                           type="text" value="${senderEmail}" maxlength="255"/>
              </li>
              
            </c:when>
            <c:otherwise>
              <li class="atg_store_senderName">   
                <label for="atg_store_emailAFriendSenderName" class="required">
                  <fmt:message key="browse_emailAFriend.senderName"/>
                  <span class="required">*</span>
                </label>
                
              <dsp:input bean="EmailAFriendFormHandler.senderName" required="true" type="text"
                         id="atg_store_emailAFriendSenderName"/>
              </li>
              
              <li class="atg_store_senderEmailAddress">
                <label for="atg_store_emailAFriendSenderEmailAddress" class="required">
                  <fmt:message key="browse_emailAFriend.senderEmail"/>
                  <span class="required">*</span>
                </label>
                <dsp:input bean="EmailAFriendFormHandler.senderEmail" required="true" type="text"
                           id="atg_store_emailAFriendSenderEmailAddress" maxlength="255"/>
              </li>
              
            </c:otherwise>
          </c:choose>
        </c:otherwise>
      </c:choose>
  
      <li class="atg_store_emailMessage">
        <label for="atg_store_emailAFriendMessage">
          <fmt:message key="common.message"/>
        </label>
  
        <%-- 
          Use oninput (ff, safari, opera) and onpropertychange (ie) to 
          detect when the content of the text area changes 
        --%>
        <dsp:textarea bean="EmailAFriendFormHandler.message" maxlength="200" iclass="textAreaCount" 
                      id="atg_store_emailAFriendMessage"/>
        <span class="charCounter" id="atg_store_emailAFriendJSCharCounter">
          <fmt:message key="common.charactersUsed">
            <fmt:param>
              <strong>0</strong>
            </fmt:param>
            <fmt:param>
              <strong>200</strong>
            </fmt:param>
          </fmt:message>
        </span>
        <span class="charCounter" id="atg_store_emailAFriendNoJSCharCounter">
          <fmt:message key="browse_emailAFriendConfirm.textLengthCaption"/>
        </span>
      </li>
  
    </ul>
   
    <div class="atg_store_formFooter">
      
      <div class="atg_store_formKey">
        <span class="required">*</span>
        <span class="required"><fmt:message key="common.requiredFields"/></span>
      </div>
  
      <div class="atg_store_formActions">
        <fmt:message var="sendButton" key="browse_emailAFriend.sendEmail"/>
        
        <div class="atg_store_formActionItem">
          <span class="atg_store_basicButton tertiary">
            <dsp:input bean="EmailAFriendFormHandler.send" type="submit" name="EmailAFriendFormHandler.send" 
                       value="${sendButton}"/>
          </span>
        </div>
        
        <div class="atg_store_formActionItem">
          <a class="atg_store_basicButton secondary" href="#" onclick="window.close();">
            <span>
              <fmt:message key="common.button.cancelText"/>
            </span>
          </a>
        </div>
        
      </div>
    </div>
  
  </dsp:form>
  <%-- ************************* end email info form ************************* --%>

</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/emailAFriendContainer.jsp#2 $$Change: 788278 $--%>




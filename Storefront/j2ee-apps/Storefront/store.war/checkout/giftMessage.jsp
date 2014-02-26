<%--
  This page presents a form to the shopper to fill in details of the Gift Note to be sent with the
  Shipped items.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/store/order/purchase/GiftMessageFormHandler"/>
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart" var="shoppingCart"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>
  
  <c:set var="stage" value="shipping"/>
  
  <fmt:message key="common.button.continueText" var="submitButtonCaption"/>  

  <crs:pageContainer index="false"  follow="false" bodyClass="atg_store_giftMessage atg_store_checkout atg_store_rightCol">
    
    <jsp:attribute name="formErrorsRenderer">
      <%-- 
        Display error messages for all form handlers used by this page 
        above accessibility navigation. 
      --%>                
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="GiftMessageFormHandler"/>
        <dsp:param name="submitFieldText" value="${submitButtonCaption}"/>
      </dsp:include>
      <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
        <dsp:param name="formHandler" bean="CouponFormHandler"/>
      </dsp:include>            
    </jsp:attribute>
    
    <jsp:body>
    
      <%--
        This droplet executes 'repriceAndUpdateOrder' chain. It uses current cart as input order parameter
        for the chain in question.
    
        Output parameters:
          pipelineResult
            Result returned from the pipeline, if execution is successful.
          exception
            Exception thrown by the pipeline, if any.
    
        Open parameters:
          failure
            Rendered, if the pipeline has thrown an exception.
          successWithErrors
            Rendered, if pipeline successfully finished, but returned error.
          success
            Rendered, if pipeline successfully finished without errors returned.
            
        Input parameters:
          pricingOp
            Pricing operation to be executed.
      --%>
      <dsp:droplet name="RepriceOrderDroplet">
        <dsp:param name="pricingOp" value="ORDER_SUBTOTAL"/>
      </dsp:droplet>
      
      <dsp:form action="${pageContext.request.requestURI}" name="GiftMessageForm" method="post"
                id="atg_store_giftMessaageForm" formid="giftmessageform">
        <%--
          We've opened a form already, hence the checkoutOrderSummary.jsp page should not open another one.
          Parameter skipCouponFormDeclaration=true prevents the page in question from defining a new form.
        --%>
        <dsp:param name="skipCouponFormDeclaration" value="true"/>
        <fmt:message key="checkout_title.checkout" var="title"/>
        
        <crs:checkoutContainer currentStage="${stage}" title="${title}">
          
          <jsp:attribute name="formErrorsRenderer">
            <%-- Display error messages for all form handlers used by this page. --%>         
            <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
              <dsp:param name="formHandler" bean="GiftMessageFormHandler"/>
              <dsp:param name="submitFieldText" value="${submitButtonCaption}"/>
            </dsp:include>
            <dsp:include page="/checkout/gadgets/checkoutErrorMessages.jsp">
              <dsp:param name="formHandler" bean="CouponFormHandler"/>
            </dsp:include>            
          </jsp:attribute>
          
          <jsp:body>
            <div id="atg_store_checkout" class="atg_store_main">
              
              <%-- Main part of this page starts here. --%>
              <div class="atg_store_enterGiftMessage">
                
                <dsp:input type="hidden" bean="GiftMessageFormHandler.addGiftMessageErrorURL" 
                           value="${pageContext.request.requestURI}"/>

                <h3>
                  <fmt:message key="checkout_giftMessage.title" />
                </h3>

                <ul class="atg_store_basicForm atg_store_addGiftNote">
                  <%-- 'To' input field. --%>
                  <li>
                    
                    <label for="atg_store_messageToInput" class="required">
                      <fmt:message key="common.to"/>
                    </label>
                    
                    <dsp:input type="text" bean="GiftMessageFormHandler.giftMessageTo" name="messageTo"
                               id="atg_store_messageToInput"  iclass="required" maxlength="100"
                               value="${shoppingCart.current.specialInstructions.giftMessageTo}"/>
                  </li>
                  <%-- 'From' input field. --%>
                  <li>
                    
                    <label for="atg_store_messageFromInput" class="required">
                      <fmt:message key="common.from"/>
                    </label>
                    
                    <dsp:input type="text" bean="GiftMessageFormHandler.giftMessageFrom" name="messageFrom"
                               id="atg_store_messageFromInput"  iclass="required" maxlength="100"
                               value="${shoppingCart.current.specialInstructions.giftMessageFrom}"/>
                  </li>
                  <%-- 'Gift Note' input field. --%>
                  <li>
                  
                    <label for="atg_store_messageInput" class="required">
                      <fmt:message key="checkout_giftMessage.note"/>
                    </label>
                    
                    <c:set var="message" value="${shoppingCart.current.specialInstructions.giftMessage}"/>
                    
                    <dsp:textarea bean="GiftMessageFormHandler.giftMessage"  iclass="required" cols="30" 
                                  rows="5" name="giftmessage" default="${message}" id="atg_store_messageInput"/>
                                  
                    <span class="example">
                      <fmt:message key="checkout_giftMessage.noteLengthCaption"/>
                    </span>
                  </li>
                </ul>

                <%-- 
                  Check user's security status and begin 'Add gift message and continue'.
                   
                  Open parameters:
                    loggedIn
                      User is already logged in with login/password.
                    default
                      User is anonymous or logged in from cookie.
                --%>
                <dsp:droplet name="ProfileSecurityStatus">
                  <dsp:oparam name="loggedIn">
                    <%-- User is logged in, figure out if they are express checkout or just editing gift message--%>
                    <c:choose>
                      <c:when test="${param['express']}">
                        <%-- User is in express checkout, head to confirm page --%>
                        <dsp:input type="hidden" bean="GiftMessageFormHandler.addGiftMessageSuccessURL"
                                   value="confirm.jsp?expressCheckout=true"/>
                      </c:when>
                      <c:when test="${param['editMessage']}">
                        <%-- user is simply editing gift message, head to confirm page --%>
                        <dsp:input type="hidden" bean="GiftMessageFormHandler.addGiftMessageSuccessURL"
                                   value="confirm.jsp"/>
                      </c:when>
                      <c:otherwise>
                        <%-- user is not in express checkout, nor editing gift message, go to shipping page --%>
                        <dsp:input type="hidden" bean="GiftMessageFormHandler.addGiftMessageSuccessURL"
                                   value="shipping.jsp"/>
                      </c:otherwise>
                    </c:choose>
                  </dsp:oparam>
                  <dsp:oparam name="default">
                    <%-- User is not logged in, send to login page --%>
                    <dsp:input type="hidden" bean="GiftMessageFormHandler.addGiftMessageSuccessURL" value="login.jsp"/>
                  </dsp:oparam>
                </dsp:droplet>

                <%--
                  If user is anonymous and the session has expired, the cart looses its contents,
                  so the page gets redirected to the home page else it will be redirected to the
                  checkout login page.

                  Open parameters:
                    loggedIn
                      User is already logged in with login/password.
                    default
                      User is anonymous or logged in from cookie.
                --%>
                <dsp:droplet name="ProfileSecurityStatus">
                  <dsp:oparam name="anonymous">
                    <%-- User is anonymous --%>
                    <dsp:input type="hidden" bean="GiftMessageFormHandler.sessionExpirationURL" value="../index.jsp"/>
                  </dsp:oparam>
                  <dsp:oparam name="default">
                    <dsp:input type="hidden" bean="GiftMessageFormHandler.sessionExpirationURL" value="login.jsp"/>
                  </dsp:oparam>
                </dsp:droplet>

                <div class="atg_store_formFooter">
                  <div class="atg_store_formKey">
                    <span class="required">* <fmt:message key="common.requiredFields"/></span>
                  </div>
                  
                  <div class="atg_store_formActions">
                    <span class="atg_store_basicButton">
                      <fmt:message var="addMessageButtonText" key="common.button.continueText"/>
                      <dsp:input type="submit" bean="GiftMessageFormHandler.addGiftMessage" id="atg_store_messageSubmitInput"
                                 value="${addMessageButtonText}"/>
                    </span>
                  </div>
                  
                </div>
              </div>
            </div>
          </jsp:body>
        </crs:checkoutContainer>
        
        <%-- Order Summary --%>
        <dsp:include page="/checkout/gadgets/checkoutOrderSummary.jsp">
          <dsp:param name="order" value="${shoppingCart.current}"/>
          <dsp:param name="currentStage" value="${stage}"/>
        </dsp:include>
        
      </dsp:form>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/giftMessage.jsp#2 $$Change: 788278 $--%>

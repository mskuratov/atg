<%--
  This gadget renders buttons, that should present on the Confirmation page.

  Required parameters:
    None.

  Optional parameters:
    expressCheckout
      Flags, if user has selected an express checkout option.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/CancelOrderFormHandler"/>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>

  <dsp:getvalueof var="expressCheckout" param="expressCheckout"/>

  <fieldset class="atg_store_placeOrder">
    <%--
      If user is anonymous and the session has expired, the cart looses its contents,
      so the page gets redirected to the home page else it will be redirected to the checkout login page.
      
      Input parameters:
        None.
        
      Open parameters:
        anonymous
          User is not logged in.
        default
          Profile's security status is either anonymous or logged in from cookie            
    --%>
    <dsp:droplet name="ProfileSecurityStatus">
      <dsp:oparam name="anonymous">
        <%-- User is anonymous, we will redirect him to the home page. --%>
        <dsp:input type="hidden" bean="CommitOrderFormHandler.sessionExpirationURL" 
                   value="${originatingRequest.contextPath}/index.jsp"/>
      </dsp:oparam>
      <dsp:oparam name="default">
        <%-- Registered user case, we will redirect him to Login page. --%>
        <dsp:input type="hidden" bean="CommitOrderFormHandler.sessionExpirationURL" 
                   value="${originatingRequest.contextPath}/checkout/login.jsp"/>
      </dsp:oparam>
    </dsp:droplet>

    <dsp:getvalueof bean="/OriginatingRequest.requestURI" var="errorUrl"/>
    
    <%-- If it's an express checkout case, add expressCheckout parameter to error URL. --%>
    <c:if test="${expressCheckout}">
      <c:set var="errorUrl" value="${errorUrl}?expressCheckout=true"/>
    </c:if>

    <%-- Success/Error URLs. --%>
    <dsp:input bean="CommitOrderFormHandler.commitOrderSuccessURL" type="hidden" value="confirmResponse.jsp"/>
    <dsp:input bean="CommitOrderFormHandler.commitOrderErrorURL" type="hidden" value="${errorUrl}"/>

    <%-- Display 'Place Order' button. --%>
    <div class="atg_store_formActions">
      <div class="atg_store_actionItems">
        
        <fmt:message var="placeOrderButtonText" key="checkout_confirmPlaceOrder.button.placeOrderText"/>
        
        <span class="atg_store_basicButton">
          <dsp:input type="submit" bean="CommitOrderFormHandler.commitOrder" 
                     id="atg_store_placeMyOrderButton" value="${placeOrderButtonText}"
                     iclass="atg_store_actionSubmit"/>
        </span>
      </div>
    </div>

    <dsp:droplet name="ProfileSecurityStatus">
      <dsp:oparam name="anonymous">
        <%-- Add e-mail input field, if needed. --%>
        <div id="atg_store_confirmEmail">
          <dsp:input bean="CommitOrderFormHandler.confirmEmailAddress" value="" maxlength="255"
                     name="email" type="text" id="atg_store_confirmEmailInput"/>
          <label for="atg_store_confirmEmailInput">
            <fmt:message key="checkout_confirmEmail.provideEmail"/>
          </label>
        </div>
        <%-- User is anonymous, display Cancel link that will remove current order. --%>
        <div id="atg_store_confirmCancel">
          
          <fmt:message var="cancelLinkTitle" key="checkout_confirmAnonCancel.button.cancelText"/>
          
          <dsp:a page="/index.jsp" title="${cancelLinkTitle}">
            <span><fmt:message key="checkout_confirmAnonCancel.button.cancelText"/></span>
            <dsp:property bean="CancelOrderFormHandler.cancelOrderSuccessURL" value="${originatingRequest.contextPath}/index.jsp"/>
            <dsp:property bean="CancelOrderFormHandler.cancelOrderErrorURL" value="${originatingRequest.requestURI}"/>
            <dsp:property bean="CancelOrderFormHandler.cancelCurrentOrder" value="submit"/>
          </dsp:a>
        </div>
      </dsp:oparam>
      <dsp:oparam name="default">
        <%-- User is not anonymous, display 'Cancel' link, that will do nothing. --%>
        <div id="atg_store_confirmCancel">
          
          <fmt:message var="cancelLinkTitle" key="checkout_confirmCancel.button.cancelTitle"/>
          
          <dsp:a page="/cart/orderNotPlaced.jsp" title="${cancelLinkTitle}">
            <span><fmt:message key="checkout_confirmCancel.button.cancelText"/></span>
          </dsp:a>
        </div>
      </dsp:oparam>
    </dsp:droplet>
  </fieldset>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/confirmControls.jsp#2 $$Change: 788278 $--%>

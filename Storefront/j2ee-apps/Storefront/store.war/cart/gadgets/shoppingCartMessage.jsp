<%--
  This gadget informs a user that they have no items in their shopping cart and presents
  the option to continue shopping.
  If the user is anonymous, the system will suggest him to login.
  This gadget must be contained inside of a form.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/store/profile/SessionBean"/>
  <dsp:importbean bean="/atg/userprofiling/Profile" var="profile"/>

  <%--
    This messageContainer tag will render a general application message box with the 
    included content.  
  --%>
  <crs:messageContainer titleKey="cart_shoppingCartMessage.ItemMsg">
    <%-- Anonymous users are always transient. Display login message to them. --%>
    <c:if test="${profile.transient}">
      <p>  
        <fmt:message key="cart_shoppingCartAnonymousMessage.viewMsg">
          <fmt:param>
            <dsp:a page="/global/util/loginRedirect.jsp" bean="SessionBean.values.loginSuccessURL"
                   paramvalue="message">
              <fmt:message key="cart_shoppingCartAnonymousMessage.login"/>
            </dsp:a>
          </fmt:param>
        </fmt:message>
      </p>
      <br />
    </c:if>

    <%-- Display 'Continue Shopping' button to user. --%>
    <crs:continueShopping>
      <dsp:input type="hidden" bean="CartModifierFormHandler.cancelURL" value="${continueShoppingURL}"/>
    </crs:continueShopping>
    
    <fmt:message key="common.button.continueShoppingText" var="continueShopping"/>
    
    <div class="atg_store_formActions">
      <span class="atg_store_basicButton secondary">
        <dsp:input type="submit" bean="CartModifierFormHandler.cancel" value="${continueShopping}"/>
      </span>
    </div>
  
  </crs:messageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/gadgets/shoppingCartMessage.jsp#2 $$Change: 788278 $--%>
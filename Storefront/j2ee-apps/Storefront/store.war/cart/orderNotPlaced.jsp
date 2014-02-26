<%-- 
  This page renders a landing page after a checkout process is cancelled, giving a user
  the option to navigate back to their shopping cart. Several other links to site policies
  and contact information are also defined here.

  Required Parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <crs:pageContainer divId="atg_store_cart" titleKey="cart_orderNotPlaced.title" 
                     index="false" follow="false" bodyClass="atg_store_orderNotPlaced">
    <jsp:body>
      <div id="atg_store_contentHeader">
        <h2 class="title"><fmt:message key="cart_orderNotPlaced.orderNotPlaced" /></h2>  
      </div>
      
      <%--
        This messageContainer tag will render a general application message box with the 
        included content.  
      --%>
      <crs:messageContainer id="atg_store_confirmResponse">
        <jsp:body>
          <p>
            <fmt:message key="cart_orderNotPlaced.tryAgain">
              <fmt:param>
                <dsp:a page="/cart/cart.jsp">
                  <fmt:message key="cart_orderNotPlaced.backToCart" />
                </dsp:a>
              </fmt:param>
            </fmt:message>
          </p>
          <p>
            <fmt:message key="cart_orderNotPlaced.privacyPolicyInfo" />
          </p>
          <ul>
            <li>
              <dsp:a page="/company/returns.jsp">                
                <fmt:message key="common.button.returnPolicyText" />
              </dsp:a>
            </li>
            <li>
              <fmt:message var="privacyPolicyTitle" key="common.button.privacyPolicyTitle" />
              <dsp:a page="/company/privacy.jsp" title="${privacyPolicyTitle}">                
                <fmt:message key="common.button.privacyPolicyText" />
              </dsp:a>
            </li>
            <li class="atg_store_shippingPolicyLink">
              <dsp:a page="/company/shipping.jsp">                 
                 <fmt:message key="cart_orderNotPlaced.shippingPolicy"/>
               </dsp:a>
            </li>
            <li>
              <dsp:a page="/company/customerService.jsp">                
                <fmt:message key="navigation_tertiaryNavigation.contactUs"/>
              </dsp:a>
            </li>
          </ul>
          <div class="atg_store_formActions">
            <dsp:a page="/cart/cart.jsp" iclass="atg_store_basicButton">
              <span>
                <fmt:message key="cart_orderNotPlaced.backToCartButton"/>
              </span>
            </dsp:a>
          </div>
        </jsp:body>
      </crs:messageContainer>
      
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/orderNotPlaced.jsp#2 $$Change: 788278 $--%>

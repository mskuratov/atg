<%--
  Renders a link to the Checkout in the navigation menu. Clicking this checkout
  link will direct a user to the checkout page.
  
  Required Parameters:
    None
    
  Optional Parameters:
    None
--%>
<dsp:page>

  <%-- Get the checkout label --%>
  <fmt:message var="itemLabel" key="navigation_richCart.checkout"/>
  <fmt:message var="itemTitle" key="navigation_personalNavigation.linkTitle">
    <fmt:param value="${itemLabel}"/>
  </fmt:message>

  <%-- Render the checkout link --%>
  <dsp:a page="/cart/cart.jsp" title="${itemTitle}"
         iclass="atg_store_navCart">
    <fmt:message key="navigation_richCart.checkout" />
  </dsp:a>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/navigation/gadgets/shoppingCart.jsp#1 $$Change: 735822 $ --%>

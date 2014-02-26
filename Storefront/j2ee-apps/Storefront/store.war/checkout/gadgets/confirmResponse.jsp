<%--
  This gadget renders the "thank you" message at the end of the checkout process.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupDroplet"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <%--
    This droplet is used to initialize ShippingGroups and CommerceItemShippingInfo objects for 
    use by the ShippingGroupFormHandler. It can be used to remove all objects or create new ones.
    
    In this case we want to clear both the CommerceItemShippingInfoContainer and the 
    ShippingGroupMapContainer.

    Input parameters:
      clear
        Flags, that all shipping-related objects should be removed from all containers.
    
    Open Parameters:
      output
  --%>
  <dsp:droplet name="ShippingGroupDroplet">
    <dsp:param name="clear" value="true"/>
    <dsp:oparam name="output"/>
  </dsp:droplet>

  <%-- Render 'Thank you' message. --%>
  <crs:messageContainer titleKey="checkout_confirmResponse.successTitle">       
    <p>
      <fmt:message var="orderNumberLinkTitle" key="checkout_confirmResponse.orderNumberLinkTitle"/>
      <fmt:message key="checkout_confirmResponse.omsOrderId">
        <fmt:param>
          <%-- Display link to currently placed order. --%>
          <dsp:a page="/myaccount/orderDetail.jsp" title="${orderNumberLinkTitle}">              
            <dsp:param name="orderId" bean="ShoppingCart.last.id"/>
            <dsp:valueof bean="ShoppingCart.last.omsOrderId"/>
          </dsp:a>
        </fmt:param>
      </fmt:message>
    </p>

    <%-- Confirmation e-mail message. --%>
    <p>
      <fmt:message key="checkout_confirmResponse.emailText"/>
      <span><dsp:valueof bean="Profile.email"/></span>
    </p>
 
    <%-- Display link to 'All My Orders' page. --%>
    <p>
      <fmt:message key="checkout_confirmResponse.reviewOrderIdLink" var="ordersMenuTitle"/>
      <fmt:message key="checkout_confirmResponse.reviewOrderId">
        <fmt:param>
          <dsp:a page="/myaccount/myOrders.jsp" title="${ordersMenuTitle}">              
            ${ordersMenuTitle}
          </dsp:a>
        </fmt:param>
      </fmt:message>
    </p>
  </crs:messageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/confirmResponse.jsp#2 $$Change: 788278 $--%>

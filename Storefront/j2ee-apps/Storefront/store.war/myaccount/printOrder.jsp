<%--
  This gadget displays order details for anonymous user so that he was able to print order details.
  We need a separate page for printing order details as orderDetail.jsp page itself is configured to
  redirect user to login page in case when user is anonymous.

  Required parameters:
    orderId
      The ID of the order to display.

  Optional parameters:
    None.
--%>
<dsp:page>
  <dsp:include page="orderDetail.jsp">
    <dsp:param name="orderId" param="orderId"/>
  </dsp:include>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/printOrder.jsp#2 $$Change: 791340 $--%>

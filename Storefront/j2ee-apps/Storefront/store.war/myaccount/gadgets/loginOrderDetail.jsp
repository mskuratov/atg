<%--
  This page redirects user to the login page if profile is anonymous and attempt to
  open order detail page. This check is needed if user received email with 
  order shipping or order confirmation details and click on 'order details' page.
  
  Required parameters:
    None
    
  Optional parameters:
    orderId
      id of the order to be displayed in order details page
 
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/store/profile/SessionBean"/>  
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>

  <%-- 
    Retrieve order Id from the SessionBean first. If not found,
    use input parameter.
   --%>
  <dsp:getvalueof var="orderId" bean="SessionBean.values.orderId"/>
  
  <c:if test="${empty orderId}">
    <dsp:getvalueof var="orderId" param="orderId"/>
  </c:if>

  <%-- 
    Set order detail page as success URL after login.
    Append orderId parameter.
    --%>
  <dsp:getvalueof var="orderDetailURL" value="/myaccount/orderDetail.jsp?orderId=${orderId}"/>
  <dsp:setvalue bean="SessionBean.values.loginSuccessURL" value="${orderDetailURL}"/>
  
  <%--
    For transient (anonymous) profiles we want to display login page
    before order detail page
   --%>
  <dsp:droplet name="ProfileSecurityStatus">
    <dsp:oparam name="anonymous">
      <dsp:include page="/myaccount/login.jsp"/>
      <dsp:setvalue bean="SessionBean.values.orderId" value="${orderId}"/>
    </dsp:oparam>
    <dsp:oparam name="default">
      <dsp:include page="${orderDetailURL}"/>
      <dsp:setvalue bean="SessionBean.values.orderId" value=""/>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/loginOrderDetail.jsp#1 $$Change: 735822 $--%>


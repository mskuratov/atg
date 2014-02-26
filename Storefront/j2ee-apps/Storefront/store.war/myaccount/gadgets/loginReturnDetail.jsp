<%--
  This page redirects user to the login page if user is not explicitly logged in.
  This check is needed if user received email with 
  order return notification and clicked on 'return details' link.
  
  Required parameters:
    returnRequestId
      The ID of the return request.
    
  Optional parameters:
    None.
 
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/store/profile/SessionBean"/>  
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>

  <dsp:getvalueof var="returnRequestId" param="returnRequestId"/>

  <%-- 
    Set return detail page as success URL after login.
    Append returnRequestId parameter.
    --%>
  <dsp:getvalueof var="returnDetailURL" value="/myaccount/returnDetail.jsp?returnRequestId=${returnRequestId}"/>
  <dsp:setvalue bean="SessionBean.values.loginSuccessURL" value="${returnDetailURL}"/>
  
  <%--
    For transient (anonymous) profiles we want to display login page
    before order detail page
   --%>
  <dsp:droplet name="ProfileSecurityStatus">
    <dsp:oparam name="anonymous">
      <dsp:include page="/myaccount/login.jsp"/>
      
    </dsp:oparam>
    <dsp:oparam name="default">
      <dsp:include page="${returnDetailURL}"/>
      
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/loginReturnDetail.jsp#1 $$Change: 788278 $--%>


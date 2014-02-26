<%--
  This gadget renders the specified shipping group address and shipping method.
  
  Required parameters:
    shippingGroup
      The shipping group to be rendered
     
  Optional parameters:
    None.
 --%>  
<dsp:page>

  <%-- 
    If this shipping group is gift shipping group then 
    add title "Gift Shipping Destination" and hide "Edit" link
  --%>
  <c:set var="isGiftShippingGroup" value="false"/>
  
  <%--
    This servlet bean accepts a shipping group as in input parameter and
    checks for gifts (gifthandlinginstructions) in the shipping group.  if
    it contains a gift, the servlet renders true, otherwise it renders false
    parameter.
    
    Input parameters:
      sg
        The shipping group to check.
    
    Open parameters:
      true
        The parameter is rendered if specified group is gift shipping group 
      false
        The parameter is rendered if specified group is gift shipping group
  --%>
  <dsp:droplet name="/atg/commerce/gifts/IsGiftShippingGroup">
    <dsp:param name="sg" param="shippingGroup"/>
    <dsp:oparam name="true">
      <c:set var="isGiftShippingGroup" value="true"/>
    </dsp:oparam>
  </dsp:droplet> 
   
  <tr>
    <td valign="top" 
        style="color:#666;font-family:Tahoma,Arial,sans-serif;font-size:16px;font-weight:bold;">
      <%-- Ship To Label --%>
      <fmt:message key="emailtemplates_orderConfirmation.shipTo"/>
    </td>
    
    <td valign="top" style="color:#000;font-family:Tahoma,Arial,sans-serif;font-size:12px;">
      <%-- Gift Shipping Group --%>
      <c:if test="${isGiftShippingGroup}">
        <span style="font-weight:bold;">
          <fmt:message key="checkout_shippingGifts.giftShippingDestinations"/>
        </span>
      </c:if>
      
      <%-- Display shipping address. --%>
      
      <dsp:getvalueof var="shippingAddress" param="shippingGroup.shippingAddress"/>
      <dsp:getvalueof var="shippingMethod" param="shippingGroup.shippingMethod"/>
      
      <%-- Display shipping address --%>
      <div style="padding-bottom:8px;font-size:20px;">
        <span><dsp:valueof value="${shippingAddress.firstName}"/></span>
        <span><dsp:valueof value="${shippingAddress.middleName}"/></span>
        <span><dsp:valueof value="${shippingAddress.lastName}"/></span>
      </div>
      <dsp:valueof value="${shippingAddress.address1}"/>
      <br />
      <c:if test="${not empty shippingAddress.address2}">
        <dsp:valueof value="${shippingAddress.address2}"/>
        <br />
      </c:if>
      <dsp:valueof value="${shippingAddress.city}"/><fmt:message key="common.comma"/>
      <c:if test="${not empty shippingAddress.state}">
        <dsp:valueof value="${shippingAddress.state}"/><fmt:message key="common.comma"/>
      </c:if>
      <dsp:valueof value="${shippingAddress.postalCode}"/>
      <br />
      
      <%-- This droplet is used here just to get display name for the given country code. --%>  
      <dsp:droplet name="/atg/store/droplet/CountryListDroplet">
        <dsp:param name="userLocale" bean="/atg/dynamo/servlet/RequestLocale.locale" />
        <dsp:param name="countryCode" value="${shippingAddress.country}"/>
        <dsp:oparam name="false">
          <dsp:valueof param="countryDetail.displayName" />
        </dsp:oparam>
      </dsp:droplet>
        
      <br /> 
      <dsp:valueof value="${shippingAddress.phoneNumber}"/>
    </td>
    
    <%-- Shipping method --%>
    <td colspan="3" valign="top" 
        style="color:#666;font-family:Tahoma,Arial,sans-serif;font-size:14px;">
      <%-- Via Label --%>
      <span style="font-size:16px;color:#666;font-weight:bold;">
        <fmt:message key="emailtemplates_orderConfirmation.via"/>
      </span>
      <c:if test="${not empty shippingMethod}">
        <span style="font-size:20px;color:#000;">
          <%--
            To get localized version of shipping method name build 
            resource key from original shipping method name by removing spaces
            and adding 'common.delivery' prefix.
           --%>
          <fmt:message key="common.delivery${fn:replace(shippingMethod, ' ', '')}"/>
        </span>
      </c:if>
    </td>
  </tr>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/emailShippingAddressRenderer.jsp#1 $$Change: 788278 $--%>
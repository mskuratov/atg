<%-- 
  This gadget renders the shipping information along with order items that
  are shipped to the each shipping destination. It also displays order extras
  like gift wraps and order's price information.
  
  The page assumes that it is rendered within the context of an HTML table, thereby 
  requiring a <tr> at the top of the file.
  
  Required parameters:
    order
      The order to be rendered
    httpServer
      The URL prefix with protocol, host and port
     
  Optional parameters:
    priceListLocale
      The locale to use for prices formatting.
    locale
      Locale to append to the URL as URL parameter so that the pages to which user navigates using the URL
      will be rendered in the same language as the email.
--%>
<dsp:page>
  
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
  
  <dsp:getvalueof var="priceListLocale" vartype="java.lang.String" param="priceListLocale"/>
  
  <%-- Get all shipping groups from order --%>
  <dsp:getvalueof var="shippingGroups" vartype="java.lang.Object" param="order.shippingGroups"/>
  
  <c:set var="shippingGroupsSize" value="${fn:length(shippingGroups) }" />
  <%-- Loop through all shipping groups and render their details. --%>
  <c:forEach var="shippingGroup" items="${shippingGroups}" varStatus="shippingGroupStatus">
    <dsp:include page="/emailtemplates/gadgets/shippingGroupRenderer.jsp">
      <dsp:param name="order" param="order"/>      
      <dsp:param name="shippingGroup" value="${shippingGroup}"/>
      <dsp:param name="priceListLocale" value="${priceListLocale}"/>
      <dsp:param name="locale" param="locale"/>
      <dsp:param name="httpServer" param="httpServer"/>
      <dsp:param name="excludeGiftWrap" value="true"/>
    </dsp:include>
  </c:forEach>

  <%-- Check whether order contains gift wraps --%>
  <dsp:getvalueof var="order" vartype="atg.projects.store.order.StoreOrderImpl" param="order"/>
  
  <c:if test="${order.containsGiftWrap or order.containsGiftMessage}">
    <%-- Yes, there are gift wraps in order --%>    
    <tr>
      <td colspan="5" 
          style="color:#666;font-family:Tahoma,Arial,sans-serif;font-size:16px;font-weight:bold;">
        <fmt:message key="checkout_confirmExtras.title"/>
      </td>
    </tr>
    
    <dsp:getvalueof var="giftWrapItem" param="order.giftWrapItem"/>
    
    <c:if test="${not empty giftWrapItem}">
      <%-- This is gift wrap, start to display its details --%>
      <dsp:param name="commerceItem" value="${giftWrapItem}"/>
      <tr>
        <td>            
          <%-- Do not display site-indicator for the Gift Wrap. --%>
        </td>
        <%-- Display name --%>
        <td style="font-family:Tahoma,Arial,sans-serif;font-size:12px;width:170px;">
          <dsp:valueof param="commerceItem.auxiliaryData.productRef.displayName">
            <fmt:message key="common.noDisplayName"/>
          </dsp:valueof>
        </td>
        <%-- Items quantity and price per item --%>
        <td colspan="2" style="font-family:Tahoma,Arial,sans-serif;font-size:12px;color:#666666;width:205px;">
          <table style="border-collapse: collapse; width: 215px;" summary="" role="presentation">
            <tr>
              <td style="width:65px;color:#666666;font-size:12px;font-family:Tahoma,Arial,sans-serif;">
                <fmt:formatNumber value="1" type="number"/>
                <fmt:message key="common.atRateOf"/>
              </td>
              <td style="color:#666666;font-size:12px;font-family:Tahoma,Arial,sans-serif;width:150px;">
                <%-- Format item's price using the specified price list's locale --%>
                <dsp:include page="/global/gadgets/formattedPrice.jsp">
                  <dsp:param name="price" param="commerceItem.priceInfo.listPrice"/>
                  <dsp:param name="priceListLocale" value="${priceListLocale}"/>
                </dsp:include>
              </td>
            </tr>
          </table>
        </td>
         
        <%-- Total items price --%>
        <td align="right" style="font-family:Tahoma,Arial,sans-serif;font-size:12px;color:#000000; white-space:nowrap;">
          =
          <span style="color:#000000">
            <%-- Format price using the specified price list's locale --%>
            <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <dsp:param name="price" param="commerceItem.priceInfo.amount"/>
              <dsp:param name="priceListLocale" value="${priceListLocale}"/>
            </dsp:include>
          </span>
        </td>
      </tr>
        
      <tr >
        <td colspan="5">
          &nbsp;
        </td>
      </tr> 
    </c:if><%-- End of checking upon commerce item class type --%>
      
    <c:if test="${order.containsGiftMessage}">
      <tr>
        <td style="font-family:Tahoma,Arial,sans-serif;font-size:12px;width:60px;height:60px;">
          <%-- Do not display site-indicator for the Gift Note. --%>
        </td>
        <td style="font-family:Tahoma,Arial,sans-serif;font-size:12px;width:170px;">
          <span><fmt:message key="checkout_confirmGiftMessage.reviewGiftMessage"/></span>
          <div style="width:170px;">
            <div style="float:left;" >
              <span style="float:left;width:40px;"><fmt:message key="common.to"/>:</span>
              <span style="display:block;margin-right:10px;margin-top:2px;"><dsp:valueof param="order.specialInstructions.giftMessageTo"/></span>
            </div>
            <div style="float:left;">
              <span style="float:left;width:40px;"><fmt:message key="common.from"/>:</span>
              <span style="display:block;margin-right:10px;margin-top:2px;"><dsp:valueof param="order.specialInstructions.giftMessageFrom"/></span>
            </div>
            <div style="float:left;">
              <span style="float:left;width:40px;"><fmt:message key="common.text"/>:</span>
              <span style="display:block;margin-right:10px;margin-top:2px;"><dsp:valueof param="order.specialInstructions.giftMessage"/></span>
            </div>
          </div>
        </td>
        <td colspan="2" style="font-family:Tahoma,Arial,sans-serif;font-size:12px;color:#666666;width:205px;">
          <table style="border-collapse: collapse; width: 215px;"
                 summary="" role="presentation">
            <tr>
              <td style="width:65px;color:#666666;font-size:12px;font-family:Tahoma,Arial,sans-serif;">
                1 <fmt:message key="common.atRateOf"/>
              </td>
              <td style="color:#666666;font-size:12px;font-family:Tahoma,Arial,sans-serif;width:150px;">
                <fmt:message key="common.FREE"/>
              </td>
            </tr>
          </table>
        </td>
        <td align="right" style="font-family:Tahoma,Arial,sans-serif;font-size:12px;color:#000000; white-space:nowrap;">
          <span style="color:#000000">
            <fmt:message key="common.equals"/> <fmt:message key="common.FREE"/>
          </span>
        </td>
      </tr>
    </c:if>
  </c:if><%-- End of check whether order contains gift wraps. --%>

  <%-- Render order subtotal amount --%>
  <dsp:include page="/emailtemplates/gadgets/emailOrderSubtotalRenderer.jsp">
    <dsp:param name="order" param="order"/>    
    <dsp:param name="priceListLocale" value="${priceListLocale}"/>
  </dsp:include>          

</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/emailOrderContents.jsp#2 $$Change: 788810 $--%>
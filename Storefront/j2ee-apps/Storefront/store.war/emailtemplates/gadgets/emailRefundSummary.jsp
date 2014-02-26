<%--
  This gadget renders refund summary for the specified return request. The gadget is intended
  to be used in email templates.
  
  Required parameters:
    returnRequest
      The ReturnReqeust object to display refund summary for.
      
  Optional parameters:
    priceListLocale
      The locale to use for prices formatting
     
--%>
<dsp:page>

  <dsp:getvalueof var="returnRequest" param="returnRequest"/>

  <table style="border-collapse:collapse">
  
    <%-- Display Refund Summary header. --%>
    <tr>
      <td colspan="2">
        <span style="font-family:Tahoma,Arial,sans-serif;font-size:14px;font-size:18px"><fmt:message key="emailtemplates_refundSummary_title"/><fmt:message key="common.labelSeparator"/></span>
        <br /><br />
      </td>
    </tr>
    
    <%-- Display refund of return items. --%>
    
    <tr>
    
      <td style="font-size:12px;line-height:18px;">
        <fmt:message key="emailtemplates_refundSummary_itemsRefund"/><fmt:message key="common.labelSeparator"/>
      </td>
       
      <td align="right" style="color:#000000;font-weight:bold;font-size:12px;line-height:18px;">
          
        <dsp:include page="/global/gadgets/formattedPrice.jsp">
          <dsp:param name="price" value="${returnRequest.totalReturnItemRefund}"/>
          <dsp:param name="priceListLocale" param="priceListLocale"/>
        </dsp:include>
        
      </td>
    </tr>
      
    <%-- Display total of non-return items adjustments. --%>
            
    <tr>
    
      <td style="font-size:12px;line-height:18px;">
        <fmt:message key="emailtemplates_refundSummary_nonReturnItemsAdjustment"/><fmt:message key="common.labelSeparator"/>
      </td>
       
      <td align="right" style="color:#000000;font-weight:bold;font-size:12px;line-height:18px;">
          
        <dsp:include page="/global/gadgets/formattedPrice.jsp">
          <dsp:param name="price" value="${returnRequest.nonReturnItemSubtotalAdjustment}"/>
          <dsp:param name="priceListLocale" param="priceListLocale"/>
        </dsp:include>
        
      </td>
    </tr>
      
    <%-- Display shipping refund. --%>
    
    <tr>
    
      <td style="font-size:12px;line-height:18px;">
        <fmt:message key="emailtemplates_refundSummary_shippingRefund"/><fmt:message key="common.labelSeparator"/>
      </td>
       
      <td align="right" style="color:#000000;font-weight:bold;font-size:12px;line-height:18px;">
          
        <dsp:include page="/global/gadgets/formattedPrice.jsp">
          <dsp:param name="price" value="${returnRequest.actualShippingRefund}"/>
          <dsp:param name="priceListLocale" param="priceListLocale"/>
        </dsp:include>
        
      </td>
    </tr>
    
    <%-- Display taxes refund. --%>
    <tr>
    
      <td style="font-size:12px;line-height:18px;">
        <fmt:message key="emailtemplates_refundSummary_taxRefund"/><fmt:message key="common.labelSeparator"/>
      </td>
       
      <td align="right" style="color:#000000;font-weight:bold;font-size:12px;line-height:18px;">
          
        <dsp:include page="/global/gadgets/formattedPrice.jsp">
          <dsp:param name="price" value="${returnRequest.actualTaxRefund}"/>
          <dsp:param name="priceListLocale" param="priceListLocale"/>
        </dsp:include>
        
      </td>
    </tr>
      
    <%-- Total refund amount. --%>
      
    <tr>
      <td colspan="5">&nbsp;</td>
    </tr>
    
    <tr style="background-color:#000;color:#fff;font-weight:bold;">
    
      <td style="text-transform:uppercase;font-size:13px;padding:10px 0 10px 2px">
        <fmt:message key="emailtemplates_refundSummary_totalRefund"/><fmt:message key="common.labelSeparator"/>
      </td>
       
      <td align="right" style="font-size:16px;padding:10px 2px 10px 0;white-space:nowrap">
          
        <dsp:include page="/global/gadgets/formattedPrice.jsp">
          <dsp:param name="price" value="${returnRequest.totalRefundAmount}"/>
          <dsp:param name="priceListLocale" param="priceListLocale"/>
        </dsp:include>
        
      </td>
    </tr>
      
  </table>
   
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/emailRefundSummary.jsp#1 $$Change: 788278 $--%>
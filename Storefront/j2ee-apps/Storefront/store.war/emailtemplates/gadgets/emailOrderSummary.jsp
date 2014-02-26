<%--
  This page renders order's price summary including items subtotal, tax, discount, shipping charges and
  total amount for the order.
  
  Required parameters:
    order
      The order to use for display of subtotal, tax, discount and shipping charges of the order.

  Optional parameters:
    priceListLocale
      The locale to use for price formatting
--%>
<dsp:page>

  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  
  <%--
    Determine whether pricing information should be display for a particular shipping group
    or for the whole order.
  --%>
      
  <%-- Get orders's raw subtotal amount --%>
  <dsp:getvalueof var="rawSubtotal" param="order.priceInfo.rawSubtotal"/>
  <%-- Get orders's discount amount --%>
  <dsp:getvalueof var="discountAmount" param="order.priceInfo.discountAmount" />
  <%-- Get shipping amount for the order --%>
  <dsp:getvalueof var="shipping" param="order.priceInfo.shipping" />
  <%-- Get tax amount for the order --%>
  <dsp:getvalueof var="tax" param="order.priceInfo.tax" />
  <%-- Get total amount for the shipping group --%>
  <dsp:getvalueof var="total" param="order.priceInfo.total"/>
  
  <table>  

    <tr>
      <td colspan="2"><fmt:message key="emailtemplates_orderSummary.orderSummary"/><fmt:message key="common.labelSeparator"/></td>
    </tr>
    
    <tr>
      <td style="font-size:12px;line-height:18px;">
        <fmt:message key="common.subTotal" /><fmt:message key="common.labelSeparator"/>
      </td>
      <td align="right" style="color:#000000;font-weight:bold;font-size:12px;line-height:18px;">
        <%--
          Display order's subtotal.
          Format price using the specified locale.
        --%>
        <dsp:include page="/global/gadgets/formattedPrice.jsp">
          <dsp:param name="price" value="${rawSubtotal}"/>
          <dsp:param name="priceListLocale" param="priceListLocale"/>
        </dsp:include>
      </td>
    </tr>
    
    <%-- Check whether order or shipping group has discounts --%>  
    <dsp:droplet name="Compare">
    
      <%-- Only show if there is a discount--%>
      <dsp:param name="obj1" value="${discountAmount}" number="0.0"/>
      <dsp:param name="obj2" value="0" number="0.0" />
      <dsp:oparam name="greaterthan">
        <%-- Display the amount discounted --%>
        <tr>
        
          <td style="font-size:12px;line-height:18px;">
            <fmt:message key="common.discount" /><fmt:message key="common.labelSeparator"/>
          </td>
          <td align="right" style="color:#000000;font-weight:bold;font-size:12px;line-height:18px;">
            <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <dsp:param name="price" value="${-discountAmount}"/>
              <dsp:param name="priceListLocale" param="priceListLocale"/>
            </dsp:include>
          </td>
        </tr>
      </dsp:oparam>
    </dsp:droplet>
  
    <%-- Shipping amount --%>
    <tr>
      <td style="font-size:12px;line-height:18px;">
        <fmt:message key="common.shipping" /><fmt:message key="common.labelSeparator"/>
      </td>
      <td align="right" style="color:#000000;font-weight:bold;font-size:12px;line-height:18px;">
        <dsp:include page="/global/gadgets/formattedPrice.jsp">
          <dsp:param name="price" value="${shipping}"/>
          <dsp:param name="priceListLocale" param="priceListLocale"/>
        </dsp:include>
      </td>
    </tr>
  
    <%-- Tax amount --%>
    <tr>
    
      <td style="font-size:12px;line-height:18px;">
        <fmt:message key="common.tax" /><fmt:message key="common.labelSeparator"/>
      </td>
      <td align="right" style="color:#000000;font-weight:bold;font-size:12px;line-height:18px;">
        <dsp:include page="/global/gadgets/formattedPrice.jsp">
          <dsp:param name="price" value="${tax}"/>
          <dsp:param name="priceListLocale" param="priceListLocale"/>
        </dsp:include>
      </td>
    </tr>

    <%-- Display order's or shipping group's total amount. --%>  
    <tr>
      <td colspan="5">&nbsp;</td>
    </tr>
    <tr>
      <td style="color:#000000;font-weight:bold;">
        <fmt:message key="common.total" /><fmt:message key="common.labelSeparator" />
      </td>
      <td align="right" style="color:#b75a00;font-weight:bold;">
        <strong>        
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${total}"/>
            <dsp:param name="priceListLocale" param="priceListLocale"/>
          </dsp:include>              
        </strong>
      </td>
    </tr>
  
  </table>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/emailOrderSummary.jsp#1 $$Change: 788278 $--%>
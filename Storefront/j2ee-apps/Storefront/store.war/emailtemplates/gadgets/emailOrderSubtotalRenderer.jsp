<%--
  This page renders order's or shipping group's subtotal, tax, discount, shipping charges and
  total amount for the order or shipping group. If shippingGroup parameter is passed the pricing
  information is rendered for shipping group, otherwise order's pricing info is rendred.
    Required parameters:
      order
        The order to use for display of subtotal, tax, discount and shipping charges of the order
        or shipping group.

    Optional parameters:
      shippingGroup
        The shipping group for which to display pricing information. If not specified the
        pricing information for whole order is displayed.        
      
      priceListLocale
        The locale to use for price formatting
--%>
<dsp:page>

  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  
  <dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
  
  <%--
    Determine whether pricing information should be display for a particular shipping group
    or for the whole order.
  --%>
  <c:choose>
    <c:when test="${not empty shippingGroup}">
      <%-- This is the shipping group case --%>
      
      <%-- Get shipping group ID --%>
      <dsp:getvalueof var="shippingGroupId" param="shippingGroup.id"/>
      <%-- Get shipping group item's price info --%>
      <dsp:param name="shippingItemsPriceInfo" param="order.priceInfo.shippingItemsSubtotalPriceInfos.${shippingGroupId}"/>
      <%-- Get shipping group's raw subtotal --%>
      <dsp:getvalueof var="rawSubtotal" param="shippingItemsPriceInfo.rawSubtotal"/>
      <%-- Get shipping group's amount --%>
      <dsp:getvalueof var="amount" param="shippingItemsPriceInfo.amount"/>
      <c:set var="discountAmount" value="${rawSubtotal - amount}"/>
      <%-- Get shipping amount for the shipping group --%>
      <dsp:getvalueof var="shipping" param="shippingGroup.priceInfo.amount"/>
      <%-- Get tax amount for the shipping group --%>
      <dsp:getvalueof var="shippingItemsTaxPriceInfos" param="order.taxPriceInfo.shippingItemsTaxPriceInfos"/>
      <c:choose>
        <c:when test="${not empty shippingItemsTaxPriceInfos}">
          <dsp:getvalueof var="tax" param="order.taxPriceInfo.shippingItemsTaxPriceInfos.${shippingGroupId}.amount"/>  
        </c:when>
        <c:otherwise>
          <c:set var="tax" value="0"/>
        </c:otherwise>
      </c:choose>
      
      <%-- Get total amount for the shipping group --%>
      <dsp:getvalueof var="total" value="${amount + tax + shipping}"/>
    </c:when>
    <c:otherwise>
      <%-- This is the order case --%>
      
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
    </c:otherwise>
  </c:choose>  

  <tr>
    <td colspan="5">&nbsp;</td>
  </tr>
  <tr>
    <td colspan="2"></td>
    <td colspan="2" style="font-size:12px;line-height:18px;">
      <fmt:message key="common.subTotal" />
    </td>
    <td align="right" style="color:#000000;font-weight:bold;font-size:12px;line-height:18px;">
      <%--
        Display order's / shipping group's subtotal.
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
        <td colspan="2"></td>
        <td colspan="2" style="font-size:12px;line-height:18px;">
          <fmt:message key="common.discount" />
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
    <td colspan="2"></td>
    <td colspan="2" style="font-size:12px;line-height:18px;">
      <fmt:message key="common.shipping" />
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
    <td colspan="2"></td>
    <td colspan="2" style="font-size:12px;line-height:18px;">
      <fmt:message key="common.tax" />
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
    <td colspan="2"></td>
    <td colspan="2" style="color:#000000;font-weight:bold;">
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

</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/emailOrderSubtotalRenderer.jsp#2 $$Change: 788278 $--%>
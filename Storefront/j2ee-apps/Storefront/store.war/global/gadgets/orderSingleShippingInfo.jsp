<%--
  This gadget displays shipping address and shipping method for a given shipping group.

  Required parameters:
    shippingGroup
      Shipping group to be displayed.

  Optional parameters:
    isCurrent
      Flags, if shipping group specified belongs to the current shopping cart.
--%>

<dsp:page>
  <dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
  <dsp:param name="shippingAddress" param="shippingGroup.shippingAddress"/>
  <dsp:getvalueof var="isCurrent" param="isCurrent"/>

  <dl class="atg_store_groupShippingAddress">
    <dt>
      <fmt:message key="checkout_confirmPaymentOptions.shipTo"/>: 
    </dt>
    <dd>
      <%-- Display shipping group's shipping address. --%>
      <dsp:include page="/global/util/displayAddress.jsp">
        <dsp:param name="address" param="shippingAddress"/>
      </dsp:include>

      <%-- If it's a Confirmation page, display link to 'Change Shipping Info' page. --%>
      <c:if test="${isCurrent}">
        <dsp:a page="/checkout/shipping.jsp" title=""  iclass="atg_store_editAddress">
          <span><fmt:message key="common.button.editText" /></span>
        </dsp:a>
      </c:if>
    </dd>

    <dt>
      <fmt:message key="checkout_confirmPaymentOptions.viaMethod"/>: 
    </dt>
    <dd class="atg_store_orderDate">
      <%-- Display shipping method. --%>
      <dsp:getvalueof var="shippingMethod" param="shippingGroup.shippingMethod"/>
      <strong><fmt:message key="common.delivery${fn:replace(shippingMethod, ' ', '')}"/></strong>

      <%-- Display link to 'Change Shipping Info' page when rendering a Confirmation page. --%>
      <c:if test="${isCurrent}">
        <dsp:a page="/checkout/shippingMethod.jsp" title="">
          <span><fmt:message key="common.button.editText" /></span>
        </dsp:a>
      </c:if>
    </dd>
  </dl>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/orderSingleShippingInfo.jsp#1 $$Change: 735822 $--%>
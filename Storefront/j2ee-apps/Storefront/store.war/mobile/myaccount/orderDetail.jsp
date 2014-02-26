<%--
  Renders "Order details".

  Page includes:
    /mobile/global/gadgets/obtainHardgoodShippingGroup.jsp - Returns the following request-scoped variables:
      - hardgoodShippingGroups counter
      - hardgoodShippingGroup object
      - giftWrapCommerceItem
    /mobile/myaccount/myOrders.jsp - List of all orders (used to display message if order is multi-shipped)
    /mobile/myaccount/gadgets/subheaderAccounts.jsp - Display subheader items
    /mobile/global/gadgets/orderSummary.jsp - Renderer of order summary

  Required parameters:
    orderId
      Order id
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/OrderLookup"/>

  <dsp:getvalueof var="mobileStorePrefix" bean="/atg/store/StoreConfiguration.mobileStorePrefix"/>

  <dsp:droplet name="OrderLookup">
    <dsp:param name="orderId" param="orderId"/>
    <dsp:oparam name="error">
      <%-- TODO: Error page --%>
      <div>
        <dsp:valueof param="errorMsg"/>
      </div>
    </dsp:oparam>
    <dsp:oparam name="output">
      <%--
        Retrieve the price list locale used for order. It will be used to format prices correctly.
         We can't use Profile's price list here as already submitted order can be priced with price
         list different from current profile price list.
      --%>
      <dsp:getvalueof var="commerceItems" param="result.commerceItems"/>
      <c:if test="${not empty commerceItems}">
        <dsp:getvalueof var="priceListLocale" vartype="java.lang.String" param="result.commerceItems[0].priceInfo.priceList.locale"/>
      </c:if>

      <dsp:include page="${mobileStorePrefix}/global/gadgets/obtainHardgoodShippingGroup.jsp">
        <dsp:param name="order" param="result"/>
      </dsp:include>

      <c:choose>
        <c:when test="${(hardgoodShippingGroups != 1) || not empty giftWrapCommerceItem}">
          <dsp:include page="${mobileStorePrefix}/myaccount/myOrders.jsp">
            <dsp:param name="redirectOrderId" param="orderId"/>
            <dsp:param name="priceListLocale" value="${priceListLocale}"/>
          </dsp:include>
        </c:when>
        <c:otherwise>
          <fmt:message var="myOrders" key="mobile.myaccount_orders.title"/>
          <crs:mobilePageContainer titleString="${myOrders}">
            <jsp:body>
              <%-- ========== Subheader ========== --%>
              <c:set var="orderDetailText">
                <fmt:message key="common.order"/><fmt:message key="mobile.common.labelSeparator"/><dsp:valueof param="orderId"/>
              </c:set>
              <dsp:include page="${mobileStorePrefix}/myaccount/gadgets/subheaderAccounts.jsp">
                <dsp:param name="centerText" value="${myOrders}"/>
                <dsp:param name="centerURL" value="${mobileStorePrefix}/myaccount/myOrders.jsp"/>
                <dsp:param name="rightText" value="${orderDetailText}"/>
                <dsp:param name="highlight" value="right"/>
              </dsp:include>
              <div id="orderDetailContainer">
                <%-- ========== Form ========== --%>
                <form action="#">
                  <dsp:include page="${mobileStorePrefix}/global/gadgets/orderSummary.jsp">
                    <dsp:param name="order" param="result"/>
                    <dsp:param name="hardgoodShippingGroup" value="${requestScope.hardgoodShippingGroup}"/>
                    <dsp:param name="priceListLocale" value="${priceListLocale}"/>
                    <dsp:param name="isCheckout" value="false"/>
                  </dsp:include>
                </form>
              </div>
            </jsp:body>
          </crs:mobilePageContainer>
        </c:otherwise>
      </c:choose>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/myaccount/orderDetail.jsp#2 $$Change: 742374 $--%>

<%--
  This page renders the block with order total information including the applied promotions.

  Form Condition:
    This gadget must be contained inside of a form.
    CartModifierFormHandler must be invoked from a submit button in this form for fields in this page to be processed

  Page includes:
    /mobile/cart/gadgets/promotionCode.jsp - Promotion code box
    /global/gadgets/formattedPrice.jsp - Price formatter

  Required parameters:
    order
      The order to render information of
    isCheckout
      Indicator if this order is about to checkout or has already been placed.
      Possible values:
        'true' = is about to checkout
        'false' = placed order
    isOrderReview
      Indicator if this order is in review step.
      Possible values:
        'true' = is in review step.
        'false' = otherwise

  Optional parameters:
    None
--%>
<dsp:page>
  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="commerceItems" param="order.commerceItems"/>
  <dsp:getvalueof var="order" param="order"/>
  <dsp:getvalueof var="isCheckout" param="isCheckout"/>

  <dsp:getvalueof var="mobileStorePrefix" bean="/atg/store/StoreConfiguration.mobileStorePrefix"/>

  <div class="cartSummary">
    <c:if test="${not empty commerceItems}">
      <div class="discounts">
        <div class="appliedOrderDiscounts">
          <c:if test="${fn:length(order.priceInfo.adjustments) > 1}">
            <ul>
              <%--
                Display all available pricing adjustments except the first one.
                The first adjustment is always order raw subtotal, and hence doesn't contain a discount
              --%>
              <c:forEach var="priceAdjustment" items="${order.priceInfo.adjustments}" begin="1">
                <dsp:tomap var="pricingModel" value="${priceAdjustment.pricingModel}"/>
                <li><c:out value="${pricingModel.description}" escapeXml="false"/></li>
              </c:forEach>
            </ul>
          </c:if>
        </div>

        <%-- "Coupon Code" gadget for "Review your order" checkout step --%>
        <c:if test="${isCheckout}">
          <dsp:include page="${mobileStorePrefix}/cart/gadgets/promotionCode.jsp">
            <dsp:param name="isOrderReview" param="isOrderReview"/>
          </dsp:include>
        </c:if>
      </div>

      <div class="orderInfo">
        <dl>
          <%-- Display Subtotal --%>
          <dt><fmt:message key="mobile.common.items"/><fmt:message key="mobile.common.labelSeparator"/></dt>
          <dd>
            <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <dsp:param name="price" param="order.priceInfo.rawSubtotal"/>
            </dsp:include>
          </dd>

          <%-- Display Discount --%>
          <dsp:getvalueof var="discountAmount" param="order.priceInfo.discountAmount"/>
          <c:if test="${discountAmount > 0}">
            <dt><fmt:message key="mobile.common.discount"/><fmt:message key="mobile.common.labelSeparator"/></dt>
            <dd>
              &minus;
              <dsp:include page="/global/gadgets/formattedPrice.jsp">
                <dsp:param name="price" param="order.priceInfo.discountAmount"/>
              </dsp:include>
            </dd>
          </c:if>

          <%-- Display Shipping --%>
          <dt><fmt:message key="mobile.common.shipping"/><fmt:message key="mobile.common.labelSeparator"/></dt>
          <dd>
            <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <dsp:param name="price" param="order.priceInfo.shipping"/>
            </dsp:include>
          </dd>
          
          <%-- Display Tax --%>
          <dt><fmt:message key="mobile.common.tax"/><fmt:message key="mobile.common.labelSeparator"/></dt>
          <dd>
            <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <dsp:param name="price" param="order.priceInfo.tax"/>
            </dsp:include>
          </dd>

          <%-- Calculated store credits that are available for the user --%>
          <c:set var="storeCreditAmount" value=".0"/>
          <c:choose>
            <c:when test="${empty submittedOrder}">
              <dsp:droplet name="/atg/commerce/claimable/AvailableStoreCredits">
                <dsp:param name="profile" bean="/atg/userprofiling/Profile"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="storeCreditAmount" vartype="java.lang.Double" param="overallAvailableAmount"/>
                </dsp:oparam>
              </dsp:droplet>
            </c:when>
            <c:otherwise>
              <dsp:getvalueof var="storeCreditAmount" vartype="java.lang.Double" param="order.storeCreditsAppliedTotal"/>
            </c:otherwise>
          </c:choose>
          <dsp:getvalueof var="total" vartype="java.lang.Double" param="order.priceInfo.total"/>

          <%-- Display calculated store credits amount only if there are credits to display --%>
          <c:if test="${storeCreditAmount > .0}">
            <dt><fmt:message key="mobile.common.storecredit"/><fmt:message key="mobile.common.labelSeparator"/></dt>
            <dd>
              &minus;
              <dsp:include page="/global/gadgets/formattedPrice.jsp">
                <%--
                  If it's an order details page, display storeCreditAmount (got from order).
                  Otherwise compare order total and storeCreditAmount (available for the current user). If there are enough
                  store credits to pay for order, display order's total. Display store credits otherwise
                --%>
                <dsp:param name="price" value="${storeCreditAmount}"/>
              </dsp:include>
            </dd>
          </c:if>
        </dl>
        <dl>
          <%-- Display Total --%>
          <dt class="totalText"><fmt:message key="mobile.price.total"/><fmt:message key="mobile.common.labelSeparator"/></dt>
          <dd class="totalPrice">
            <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <%-- If there are enough store credits to pay for order, display order's total as 0 --%>
              <dsp:param name="price" value="${total > storeCreditAmount ? total - storeCreditAmount : 0}"/>
            </dsp:include>
          </dd>
        </dl>
      </div>
    </c:if>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/global/gadgets/pricingSummary.jsp#4 $$Change: 795001 $--%>

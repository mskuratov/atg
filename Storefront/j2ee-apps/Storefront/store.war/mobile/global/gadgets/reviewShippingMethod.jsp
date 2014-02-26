<%-- 
  Renders order "Shipping Method".

  Page includes:
    /global/gadgets/formattedPrice.jsp - Price formatter

  Required parameters:
    order
      Order which payment data need to be displayed
    isCheckout
      Indicator if this order is about to checkout or has already been placed.
      Possible values:
        'true' = is about to checkout
        'false' = placed order
--%>
<dsp:page>
  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="isCheckout" param="isCheckout"/>

  <dsp:getvalueof var="shippingMethod" param="hardgoodShippingGroup.shippingMethod"/>
  <c:set var="linkToEditPage" value="../../checkout/shippingMethod.jsp"/>
  <fmt:message var="title" key="mobile.common.defaultShippingMethod"/>

  <div id="shippingMethod">
    <c:choose>
      <c:when test="${isCheckout}">
        <h2><dsp:a page="${linkToEditPage}">${title}</dsp:a></h2>
        <div class="cartData">
          <dsp:a page="${linkToEditPage}" title="${title}">
            <span class="content icon-ArrowRight">
              <span class="shippingMethodTitle"><fmt:message key="common.delivery${fn:replace(shippingMethod, ' ', '')}"/><fmt:message key="mobile.common.labelSeparator"/></span>
              <span class="shippingMethodPrice">
                <dsp:include page="/global/gadgets/formattedPrice.jsp">
                  <dsp:param name="price" param="order.priceInfo.shipping"/>
                </dsp:include>
              </span>
            </span>
          </dsp:a>
        </div>
      </c:when>
      <c:otherwise>
        <h2>${title}</h2>
        <div class="cartData">
          <span class="content">
            <span class="shippingMethodTitle"><fmt:message key="common.delivery${fn:replace(shippingMethod, ' ', '')}"/><fmt:message key="mobile.common.labelSeparator"/></span>
            <span class="shippingMethodPrice">
              <dsp:include page="/global/gadgets/formattedPrice.jsp">
                <dsp:param name="price" param="order.priceInfo.shipping"/>
              </dsp:include>
            </span>
          </span>
        </div>
      </c:otherwise>
    </c:choose>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/global/gadgets/reviewShippingMethod.jsp#1 $$Change: 742374 $--%>

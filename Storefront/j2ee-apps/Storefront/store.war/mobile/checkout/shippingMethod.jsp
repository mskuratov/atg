<%--
  This page includes the gadgets for the select shipping method page for a single shipping group.
  (That is, all items will be shipped to the same shipping address).

  Page includes:
    /global/gadgets/formattedPrice.jsp - Price formatter
    /mobile/global/gadgets/errorMessage.jsp - Displays all errors collected from FormHandler

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
  <dsp:importbean bean="/atg/commerce/pricing/AvailableShippingMethods"/>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>
  <dsp:importbean bean="/atg/store/mobile/droplet/ValidateShippingMethod"/>
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <fmt:message var="pageTitle" key="mobile.common.shipping"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <jsp:body>
      <div class="dataContainer">
        <h2><fmt:message key="mobile.common.defaultShippingMethod"/></h2>

        <%-- Display "ShippingGroupFormHandler" error messages, which were specified in "formException.message" --%>
        <dsp:include page="${mobileStorePrefix}/global/gadgets/errorMessage.jsp">
          <dsp:param name="formHandler" bean="ShippingGroupFormHandler"/>
        </dsp:include>

        <%-- Check if there are any not gift shipping groups --%>
        <dsp:getvalueof var="anyHardgoodShippingGroups" vartype="java.lang.Boolean"
                        bean="ShippingGroupFormHandler.anyHardgoodShippingGroups"/>

        <c:if test="${anyHardgoodShippingGroups}">
          <%-- ========== Form ========== --%>
          <dsp:form formid="shippingMethod" action="${pageContext.request.requestURI}" method="post">
            <%-- ========== Redirection URLs ========== --%>
            <%--
              If user is "anonymous" and the session has expired: the cart looses its contents.
              Then the page gets redirected to the home page.
              Otherwise, it will be redirected to the "Checkout" login page.
            --%>
            <dsp:droplet name="ProfileSecurityStatus">
              <dsp:oparam name="anonymous">
                <dsp:input type="hidden" bean="ShippingGroupFormHandler.sessionExpirationURL" value="${siteContextPath}"/>
              </dsp:oparam>
              <dsp:oparam name="default">
                <dsp:input type="hidden" bean="ShippingGroupFormHandler.sessionExpirationURL" value="${siteContextPath}/checkout/login.jsp"/>
              </dsp:oparam>
            </dsp:droplet>

            <dsp:input type="hidden" bean="ShippingGroupFormHandler.updateShippingMethodSuccessURL" value="billing.jsp"/>
            <dsp:input type="hidden" bean="ShippingGroupFormHandler.updateShippingMethodErrorURL" value="shippingMethod.jsp"/>

            <%-- "Coupon code" --%>
            <dsp:getvalueof var="couponCode" bean="CouponFormHandler.currentCouponCode"/>
            <dsp:input bean="CouponFormHandler.couponCode" priority="10" type="hidden" value="${couponCode}"/>

            <ul id="shippingMethods" class="dataList">
              <%--
                If shipping group is not passed, get first non-gift shipping group with relationships
                or first gift shipping group
              --%>
              <c:if test="${empty shippingGroup}">
                <dsp:getvalueof var="shippingGroup" bean="ShippingGroupFormHandler.firstNonGiftHardgoodShippingGroupWithRels"/>
              </c:if>
              <c:if test="${empty shippingGroup}">
                <dsp:getvalueof var="giftShippingGroups" bean="ShippingGroupFormHandler.giftShippingGroups"/>
                <c:if test="${not empty giftShippingGroups}">
                  <dsp:getvalueof var="shippingGroup" value="${giftShippingGroups[0]}"/>
                </c:if>
              </c:if>

              <%-- Get current shipping method defined in the shipping group --%>
              <dsp:getvalueof value="${shippingGroup.shippingMethod}" var="currentMethod"/>

              <%-- Display available methods --%>
              <dsp:droplet name="AvailableShippingMethods">
                <dsp:param name="shippingGroup" value="${shippingGroup}"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="availableShippingMethods" param="availableShippingMethods"/>

                  <%-- Check if current shipping method defined in the shipping group is the one from available shipping methods --%>
                  <c:if test="${not empty currentMethod}">
                    <c:set var="isCurrentInAvailableMethods" value="false"/>
                    <c:forEach var="method" items="${availableShippingMethods}">
                      <c:if test="${currentMethod == method}">
                        <c:set var="isCurrentInAvailableMethods" value="true"/>
                      </c:if>
                    </c:forEach>
                  </c:if>

                  <c:if test="${empty currentMethod || not isCurrentInAvailableMethods}">
                    <%--
                      Current method in shipping group is either not defined or is not
                      available for this destination. Get default shipping method from
                      user profile
                    --%>
                    <dsp:getvalueof bean="Profile.defaultCarrier" var="currentMethod"/>
                  </c:if>

                  <c:forEach var="method" items="${availableShippingMethods}">
                    <dsp:droplet name="ValidateShippingMethod">
                      <dsp:param name="shippingMethod" value="${method}"/>
                      <dsp:param name="shippingGroup" value="${shippingGroup}"/>
                      <dsp:oparam name="valid">
                        <dsp:param name="method" value="${method}"/>

                        <%-- Determine shipping price for the current shipping method --%>
                        <dsp:droplet name="/atg/store/pricing/PriceShippingMethod">
                          <dsp:param name="shippingGroup" value="${shippingGroup}"/>
                          <dsp:param name="shippingMethod" param="method"/>
                          <dsp:oparam name="output">
                            <dsp:getvalueof var="shippingPrice" param="shippingPrice"/>
                          </dsp:oparam>
                        </dsp:droplet>
                        <c:set var="shippingMethod" value="${fn:replace(method, ' ', '')}"/>
                        <c:set var="shippingMethodResourceKey" value="checkout_shipping.delivery${shippingMethod}"/>
                        <c:set var="shippingMethodContentResourceKey" value="${shippingMethodResourceKey}Content"/>
                        <li>
                          <div class="content icon-ArrowRight">
                            <dsp:input type="radio" class="radio"
                                       bean="ShippingGroupFormHandler.shippingMethod" paramvalue="method"
                                       id="shipping_${shippingMethod}"/>
                            <label for="shipping_${shippingMethod}" onclick="">
                              <%-- Shipping method name --%>
                              <span class="shippingMethodTitle"><fmt:message key="${shippingMethodResourceKey}"/><fmt:message key="mobile.common.labelSeparator"/></span>
                              <span class="shippingMethodPrice">
                                <dsp:include page="/global/gadgets/formattedPrice.jsp">
                                  <dsp:param name="price" value="${shippingPrice}"/>
                                </dsp:include>
                              </span>
                            </label>
                          </div>
                        </li>
                      </dsp:oparam>
                    </dsp:droplet>
                  </c:forEach>
                </dsp:oparam>
              </dsp:droplet>
            </ul>
            <dsp:input type="hidden" bean="ShippingGroupFormHandler.updateShippingMethod" value="updateShippingMethod"/>
          </dsp:form>
        </c:if>
      </div>

      <script>
        $(document).ready(function() {
          CRSMA.global.delayedSubmitSetup("#shippingMethods");
        });
      </script>
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/checkout/shippingMethod.jsp#3 $$Change: 788278 $--%>

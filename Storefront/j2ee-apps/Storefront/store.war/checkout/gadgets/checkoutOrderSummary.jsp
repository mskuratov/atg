<%-- 
  This gadget renders brief order summary for the checkout pages. It will display:
    
    1. order sub-total
    2. applied discounts
    3. shipping price
    4. billing price
    5. 'Apply Coupon' area (or coupon applied, if any)
    6. order total

  Required parameters:
    order
      Specifies an order whose summary should be displayed.

  Optional parameters:
    isShoppingCart
      True when and only when this gadget is included from the Cart page.
    currentStage
      Specifies a checkout stage (login, shipping, billing or confirmation).
    submittedOrder
      True when and only when this gadget is included from the Order Details page.
    skipCouponFormDeclaration
      If ture, 'Apply Coupon Code' gadget will not be wrapped into a form.
--%>
<dsp:page>

  <dsp:importbean bean="/atg/targeting/TargetingArray"/>
  <dsp:importbean bean="/atg/commerce/pricing/CleanBeforePricingSlot"/>
    
  <dsp:getvalueof var="currencyCode" vartype="java.lang.String" param="order.priceInfo.currencyCode" />
  <dsp:getvalueof var="shipping" vartype="java.lang.Double" param="order.priceInfo.shipping" />
  <dsp:getvalueof var="order"  param="order" />
  <dsp:getvalueof var="isShoppingCart" param="isShoppingCart"/>
  <dsp:getvalueof var="currentStage" param="currentStage"/>
  <dsp:getvalueof var="submittedOrder" param="submittedOrder"/>

  <div class="atg_store_orderSummary aside">
    <%-- Display 'Order Details' header. --%>
    <h4>
      <fmt:message key="checkout_orderSummary.orderSummary"/>
    </h4>
    <%-- And render order's details. --%>
    <ul class="atg_store_orderSubTotals">
      <%-- Display order subtotal. --%>
      <li class="subtotal">
        <span class="atg_store_orderSummaryLabel">
          <fmt:message key="common.subTotal"/>
        </span>
        <span class="atg_store_orderSummaryItem">
          
          <dsp:getvalueof var="rawSubtotal" vartype="java.lang.Double" param="order.priceInfo.rawSubtotal"/>
          
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${rawSubtotal}"/>
          </dsp:include>
        
        </span>
      </li>

      <%--
        Display applied discounts amount.
        First, check if there are applied discounts. If this is the case, display applied discounts amount.
      --%>
      <dsp:getvalueof var="discountAmount" vartype="java.lang.Double" param="order.priceInfo.discountAmount"/>
      
      <c:if test="${discountAmount > 0}">
        <li>
          <span class="atg_store_orderSummaryLabel">
            <fmt:message key="common.discount"/>*
          </span>
          <span class="atg_store_orderSummaryItem">
            <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <dsp:param name="price" value="${-discountAmount}"/>
            </dsp:include>
          </span>
        </li>
      </c:if>

        <%-- Display shipping price. --%>
        <li>
          <span class="atg_store_orderSummaryLabel">
            <fmt:message key="common.shipping"/>
          </span>
          <span class="atg_store_orderSummaryItem">
            <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <dsp:param name="price" value="${shipping}"/>
            </dsp:include>
          </span>
        </li>

        <%-- Display taxes amount. --%>
        <li>
          <span class="atg_store_orderSummaryLabel">
            <fmt:message key="common.tax"/>
          </span>
          <span class="atg_store_orderSummaryItem">
            
            <dsp:getvalueof var="tax" vartype="java.lang.Double" param="order.priceInfo.tax" />
            
            <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <dsp:param name="price" value="${tax}"/>
            </dsp:include>
          
          </span>
        </li>
        <%-- Include coupon code gadget. This should not be displayed on Order Details page. --%>
        
        <c:if test="${empty submittedOrder}">
          
          <dsp:getvalueof var="skipForm" vartype="java.lang.Boolean" param="skipCouponFormDeclaration"/>
          
          <%-- Do not wrap Coupon Code gadget into a form, if it's not desirable. --%>
          <c:choose>
            <c:when test="${skipForm}">
              <li>
                <dsp:include page="/cart/gadgets/promotionCode.jsp" />
              </li>
            </c:when>
            <c:otherwise>
              <li>
                <dsp:form action="${pageContext.request.requestURI}" method="post" 
                          name="couponform" formid="couponform">
                  <dsp:include page="/cart/gadgets/promotionCode.jsp" />
                </dsp:form>
              </li>
            </c:otherwise>
          </c:choose>
        </c:if>
       
        <%--
          Calculate store credits for this order.
          On the Order Details page, store credits should be calculated from the order itself.
          On checkout pages, store credits should be calculated from the current profile.
        --%>
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

        <%-- Display calculated store credits amount only if there are credits to display. --%>
        <c:if test="${storeCreditAmount > .0}">
          <li>
            <span class="atg_store_orderSummaryLabel">
              <fmt:message key="checkout_onlineCredit.useOnlineCredit"/>
            </span>
            <span class="atg_store_orderSummaryItem">
              <dsp:include page="/global/gadgets/formattedPrice.jsp">
                <%--
                  If it's an order details page, display storeCreditAmount (got from order).
                  Otherwise compare order total and storeCreditAmount (available for the current user). 
                  If there are enough store credits to pay for order, display order's total. Display 
                  store credits otherwise.
                --%>
                <dsp:param name="price"
                           value="${empty submittedOrder ? (total > storeCreditAmount ? -storeCreditAmount : -total) : -storeCreditAmount}"/>
              </dsp:include>
            </span>
          </li>
        </c:if>

        <%--
          Display all available pricing adjustments (i.e. discounts) except the first one.
          The first adjustment is always order's raw sub-total, and hence doesn't contain a discount.
        --%>
        <c:if test="${fn:length(order.priceInfo.adjustments) > 1}">
          <li class="atg_store_appliedOrderDiscounts">
            <c:forEach var="priceAdjustment" varStatus="status" items="${order.priceInfo.adjustments}" begin="1">
              <preview:repositoryItem item="${priceAdjustment.pricingModel}">
                <%--div element was added as a wrapper. It makes possible to right click on it in preview--%>
                <div>
                  <c:out value="*"/>
                  <dsp:tomap var="pricingModel" value="${priceAdjustment.pricingModel}"/>
                  <c:out value="${pricingModel.description}" escapeXml="false"/>
                  <br/>
                </div>
              </preview:repositoryItem>
            </c:forEach>
          </li>
        </c:if>
        
        <%-- Check for stacking rules messages. Render a link if we have something to display --%>
        
        <%--
           This droplet performs a targeting operation with the help of its targeter, filter, and 
           sourceMap parameters, and sets the output elements parameter to the resulting array of target objects.
           
           In this case retrieve all pricing messages.
          
          Input Parameters
            targeter
              Slot with pricing messages.
            filter 
              A component to filter duplicate stacking rule messages.      
              
          Output Parameters
            element
              Message to display.    
         --%>
       
        <dsp:droplet name="TargetingArray">
          <dsp:param name="targeter" bean="CleanBeforePricingSlot"/>          

          <dsp:oparam name="output">
            <li class="atg_store_stackingMessage">
              <dsp:a href="${pageContext.request.contextPath}/global/gadgets/promotionDetailsPopup.jsp" 
                     target="popup">
                <fmt:message key="common.button.whereMyPromotionText"/>
              </dsp:a>
            </li>
          </dsp:oparam>
        </dsp:droplet>

      <li class="atg_store_orderTotal">
        <%-- Display order's total. --%>
          <span class="atg_store_orderSummaryLabel"><fmt:message key="common.total"/><fmt:message key="common.labelSeparator"/></span>
          <span class="atg_store_orderSummaryItem">
            <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <%-- If there are enough store credits to pay for order, display order's total as 0. --%>
              <dsp:param name="price" value="${total > storeCreditAmount ? total - storeCreditAmount : 0}"/>
            </dsp:include>
          </span>
      </li>
    </ul>
    <c:if test="${empty submittedOrder}">
      
      <%-- It's not an Order Details page, hence some buttons should be displayed. --%>
      <c:if test="${not empty isShoppingCart}">
        <%-- It's Shopping Cart page, display its buttons. --%>
        <dsp:include page="/cart/gadgets/actionItems.jsp" />
      </c:if>
      
      <c:if test="${(not empty currentStage) and (currentStage == 'confirm')}">
        <%-- It's Confirmation page, display its buttons. --%>
        <dsp:include page="/checkout/gadgets/confirmControls.jsp">
          <dsp:param name="expressCheckout" param="expressCheckout"/>
        </dsp:include>
      </c:if>
    
    </c:if>

    <%-- Display C2C button on Shopping Cart and checkout pages. --%>
    <c:if test="${empty submittedOrder}"> 
      <dsp:include page="/navigation/gadgets/clickToCallLink.jsp">
        <dsp:param name="pageName" value="cartSummary"/>
      </dsp:include>
    </c:if>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/checkoutOrderSummary.jsp#2 $$Change: 788278 $--%>

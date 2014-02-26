<%-- 
  This gadget renders brief order summary for the return related pages. It will display:
    
    1. order sub-total
    2. applied discounts
    3. shipping price
    4. billing price    
    5. order total

  Required parameters:
    order
      Specifies an order whose summary should be displayed.

  Optional parameters:
    priceListLocale
      Specifies a locale in which to format the price (as string).
      If not specified, locale will be taken from profile price list (Profile.priceList.locale).
    
--%>
<dsp:page>
  
  <dsp:getvalueof var="order"  param="order" />  

  <div class="atg_store_returnSummaryPart atg_store_returnOrderSummary">
    <%-- Display 'Order Details' header. --%>
    <h4>
      <fmt:message key="checkout_orderSummary.orderSummary"/><fmt:message key="common.labelSeparator"/>
    </h4>
    <%-- And render order's details. --%>
    <ul class="atg_store_orderSubTotals">
      <%-- Display order subtotal. --%>
      <li class="subtotal">
        <span class="atg_store_orderSummaryLabel">
          <fmt:message key="common.subTotal"/><fmt:message key="common.labelSeparator"/>
        </span>
        <span class="atg_store_orderSummaryItem">
          
          <dsp:getvalueof var="rawSubtotal" vartype="java.lang.Double" param="order.priceInfo.rawSubtotal"/>
          
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${rawSubtotal}"/>
            <dsp:param name="priceListLocale" param="priceListLocale" />
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
            <fmt:message key="common.discount"/>*<fmt:message key="common.labelSeparator"/>
          </span>
          <span class="atg_store_orderSummaryItem">
            <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <dsp:param name="price" value="${-discountAmount}"/>
              <dsp:param name="priceListLocale" param="priceListLocale" />
            </dsp:include>
          </span>
        </li>
      </c:if>

      <%-- Display shipping price. --%>
      <li>
        <span class="atg_store_orderSummaryLabel">
          <fmt:message key="common.shipping"/><fmt:message key="common.labelSeparator"/>
        </span>
        <span class="atg_store_orderSummaryItem">
        
          <dsp:getvalueof var="shipping" vartype="java.lang.Double" param="order.priceInfo.shipping" />
          
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${shipping}"/>
            <dsp:param name="priceListLocale" param="priceListLocale" />
          </dsp:include>
        </span>
      </li>

      <%-- Display taxes amount. --%>
      <li>
        <span class="atg_store_orderSummaryLabel">
          <fmt:message key="common.tax"/><fmt:message key="common.labelSeparator"/>
        </span>
        <span class="atg_store_orderSummaryItem">
          
          <dsp:getvalueof var="tax" vartype="java.lang.Double" param="order.priceInfo.tax" />
          
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${tax}"/>
            <dsp:param name="priceListLocale" param="priceListLocale" />
          </dsp:include>
        
        </span>
      </li>
     
      <%--
        Calculate store credits for this order.
        On the Order Details page, store credits should be calculated from the order itself.
        On checkout pages, store credits should be calculated from the current profile.
      --%>
      <dsp:getvalueof var="storeCreditAmount" vartype="java.lang.Double" param="order.storeCreditsAppliedTotal"/>
        
      
      <dsp:getvalueof var="total" vartype="java.lang.Double" param="order.priceInfo.total"/>

      <%-- Display calculated store credits amount only if there are credits to display. --%>
      <c:if test="${storeCreditAmount > .0}">
        <li>
          <span class="atg_store_orderSummaryLabel">
            <fmt:message key="checkout_onlineCredit.useOnlineCredit"/><fmt:message key="common.labelSeparator"/>
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
                         value="${-storeCreditAmount}"/>
              <dsp:param name="priceListLocale" param="priceListLocale" />
            </dsp:include>
          </span>
        </li>
      </c:if>

      <li class="totalRefundAmount">
        <%-- Display order's total. --%>
        <span class="atg_store_orderSummaryLabel"><fmt:message key="myaccount_returns_orderTotal"/><fmt:message key="common.labelSeparator"/></span>
        <span class="atg_store_orderSummaryItem">
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <%-- If there are enough store credits to pay for order, display order's total as 0. --%>
            <dsp:param name="price" value="${total > storeCreditAmount ? total - storeCreditAmount : 0}"/>
            <dsp:param name="priceListLocale" param="priceListLocale" />
          </dsp:include>
        </span>
      </li>
      
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
    </ul>
  
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/returnOrderSummary.jsp#1 $$Change: 788278 $--%>

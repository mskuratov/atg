<%--
  This gadget renders refund summary for the specified return request.
  
  Required parameters:
    returnRequest
      The ReturnReqeust object to display refund summary for.
      
  Optional parameters:
    isActiveReturn
      Boolean indicating whether the return request is currently active.
    priceListLocale
      Specifies a locale in which to format the price (as string).
      If not specified, locale will be taken from profile price list (Profile.priceList.locale). 
--%>
<dsp:page>

  <dsp:getvalueof var="returnRequest" param="returnRequest"/>
  <dsp:getvalueof var="isActiveReturn" param="isActiveReturn"/>

  <div class="atg_store_returnSummaryPart atg_store_refundSummary">
  
    <%-- Display Refund Summary header. --%>
    <h4>
      <fmt:message key="myaccount_refundSummary_title"/><fmt:message key="common.labelSeparator"/>
    </h4>
    
    <%-- And render order's details. --%>
    <ul class="atg_store_orderSubTotals">
    
      <%-- Display refund of return items. --%>
      <li class="subtotal">
        <span class="atg_store_orderSummaryLabel">
          <fmt:message key="myaccount_refundSummary_itemsRefund"/><fmt:message key="common.labelSeparator"/>
        </span>
        <span class="atg_store_orderSummaryItem">
          
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${returnRequest.totalReturnItemRefund}"/>
            <dsp:param name="priceListLocale" param="priceListLocale" />
          </dsp:include>
        
        </span>
      </li>
      
      <%-- Display total of non-return items adjustments. --%>
      <dsp:getvalueof var="nonReturnItemsAdjustments" value="${returnRequest.nonReturnItemSubtotalAdjustment}"/> 
      <li class="subtotal">
        <span class="atg_store_orderSummaryLabel">
          <fmt:message key="myaccount_refundSummary_nonReturnItemsAdjustment"/><c:out value="${nonReturnItemsAdjustments != 0 ? '*' : '' }"/><fmt:message key="common.labelSeparator"/>
        </span>
        <span class="atg_store_orderSummaryItem">
          
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${nonReturnItemsAdjustments}"/>
            <dsp:param name="priceListLocale" param="priceListLocale" />
          </dsp:include>
        
        </span>
      </li>
      
      <%-- Display shipping refund. --%>
      <li class="subtotal">
        <span class="atg_store_orderSummaryLabel">
          <fmt:message key="myaccount_refundSummary_shippingRefund"/><fmt:message key="common.labelSeparator"/>
        </span>
        <span class="atg_store_orderSummaryItem">
          
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${returnRequest.actualShippingRefund}"/>
            <dsp:param name="priceListLocale" param="priceListLocale" />
          </dsp:include>
        
        </span>
      </li>
      
      <%-- Display taxes refund. --%>
      <li class="subtotal">
        <span class="atg_store_orderSummaryLabel">
          <fmt:message key="myaccount_refundSummary_taxRefund"/><fmt:message key="common.labelSeparator"/>
        </span>
        <span class="atg_store_orderSummaryItem">
          
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${returnRequest.actualTaxRefund}"/>
            <dsp:param name="priceListLocale" param="priceListLocale" />
          </dsp:include>
        
        </span>
      </li>
      
      <%-- Total refund amount. --%>
      <li class="totalRefundAmount">
        <span class="atg_store_orderSummaryLabel">
          <fmt:message key="myaccount_refundSummary_totalRefund"/><fmt:message key="common.labelSeparator"/>
        </span>
        <span class="atg_store_orderSummaryItem">
          
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${returnRequest.totalRefundAmount}"/>
            <dsp:param name="priceListLocale" param="priceListLocale" />
          </dsp:include>
        
        </span>
      </li>
      
      <%-- Display link to the promotion amendments popup. --%>
           
      <c:if test="${not empty returnRequest.promotionValueAdjustments || nonReturnItemsAdjustments != 0}">
        <li class="promotionAmendments">
          <dsp:a href="${pageContext.request.contextPath}/myaccount/gadgets/returnPromotionAmendmentsPopup.jsp" 
                 target="popup">
            <c:out value="${nonReturnItemsAdjustments != 0 ? '*' : '' }"/><fmt:message key="myaccount_refundSummary_promotionAdjustmentsPopup"/>
            <c:if test="${not isActiveReturn}">
              <dsp:param name="returnId" value="${returnRequest.requestId}"/>
              <dsp:param name="priceListLocale" param="priceListLocale" />
            </c:if>
          </dsp:a>        
        </li> 
      </c:if>
    </ul>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/refundSummary.jsp#1 $$Change: 788278 $--%>

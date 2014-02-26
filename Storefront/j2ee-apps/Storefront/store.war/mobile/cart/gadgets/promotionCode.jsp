<%--
  This gadget renders either the text box to submit a "Promotion Code", or a message displaying
  the applied promotion code.

  Form Condition:
    This gadget must be contained inside of a form.
    "CartModifierFormHandler" must be invoked from a submit button in the form for these fields to be processed.

  Required parameters:
    isOrderReview
      Indicator if this order is in review step.
      Possible values:
        'true' = is in review step.
        'false' = otherwise
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>

  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="isOrderReview" param="isOrderReview"/>

  <dsp:getvalueof var="couponCode" bean="CouponFormHandler.currentCouponCode"/>
  <dsp:getvalueof var="formExceptions" bean="CouponFormHandler.formExceptions"/>

  <div class="couponCode">
    <input type="hidden" id="promotionPreviousCode" value="${couponCode}"/>
    <fmt:message var="promotionCode" key="mobile.cart.promotionCode"/>
    <c:choose>
      <c:when test="${not empty formExceptions}">
        <dsp:input bean="CouponFormHandler.couponCode" priority="10" type="text" id="promotionCodeInput"
                   placeholder="${promotionCode}" aria-label="${promotionCode}" autocomplete="off" size="10"
                   onblur="CRSMA.cart.applyCoupon()" onkeypress="CRSMA.cart.applyNewCouponOnEnter()"
                   class="promotionCodeInput errorState"/>
      </c:when>
      <c:otherwise>
        <c:choose>
          <c:when test="${isOrderReview}">
            <dsp:input bean="CouponFormHandler.couponCode" priority="10" type="text" id="promotionCodeInput"
                       placeholder="${promotionCode}" aria-label="${promotionCode}" autocomplete="off" size="10"
                       onblur="CRSMA.cart.applyCoupon()" onkeypress="CRSMA.cart.applyNewCouponOnEnter()"
                       class="promotionCodeInput" value="${couponCode}" disabled="true"/>
          </c:when>
          <c:otherwise>
            <dsp:input bean="CouponFormHandler.couponCode" priority="10" type="text" id="promotionCodeInput"
                       placeholder="${promotionCode}" aria-label="${promotionCode}" autocomplete="off" size="10"
                       onblur="CRSMA.cart.applyCoupon()" onkeypress="CRSMA.cart.applyNewCouponOnEnter()"
                       class="promotionCodeInput" value="${couponCode}"/>
          </c:otherwise>
        </c:choose>
        <label for="promotionCodeInput" onclick=""/>
      </c:otherwise>
    </c:choose>
    <dsp:input type="hidden" bean="CouponFormHandler.claimCoupon" value="updateTotalText"/>
  </div>

  <%-- ========== Redirection URLs ========== --%>
  <dsp:input type="hidden" bean="CouponFormHandler.applyCouponSuccessURL" value="${pageContext.request.requestURI}"/>
  <dsp:input type="hidden" bean="CouponFormHandler.applyCouponErrorURL" value="${pageContext.request.requestURI}"/>
  <dsp:input type="hidden" bean="CouponFormHandler.editCouponSuccessURL" value="${pageContext.request.requestURI}"/>
  <dsp:input type="hidden" bean="CouponFormHandler.editCouponErrorURL" value="${pageContext.request.requestURI}"/>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cart/gadgets/promotionCode.jsp#3 $$Change: 788278 $--%>

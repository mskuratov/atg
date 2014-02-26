<%-- 
  This gadget renders either the text box to submit a promotion code, or a message 
  displaying the applied promotion code with a "Remove Coupon" button. 
  This gadget must be contained inside of a form.

  Required parameters:
    None.

  Optional Parameters:
    couponCode
      An actual coupon code value.
    editCoupon
      boolean to indicate if we can edit a coupon or not.
    nickName
      The shipping address nickname.
    selectedAddress
      The selected shipping address.
    successURL
      The URL to navigate to when a submit action is successful.
    expressCheckout
      boolean to indicate whether express checkout is in progress or not
--%>

<dsp:page>
  <dsp:importbean bean="/atg/store/order/purchase/CouponFormHandler"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>

  <dsp:getvalueof var="couponCode" bean="CouponFormHandler.currentCouponCode"/>
  <dsp:getvalueof var="editCoupon" param="editCoupon"/>
  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
  <dsp:getvalueof var="expressCheckout" param="expressCheckout"/>
  <dsp:getvalueof var="nickName" param="nickName"/>
  <dsp:getvalueof var="selectedAddress" param="selectedAddress"/>
  <dsp:getvalueof var="successURL" param="successURL"/>

  <%-- Build the redirect URL --%>
  <c:choose>
    <%--
      If we have the nickName, selectedAddress and successURL parameters then we
      are on the shippingAddressEdit.jsp from the single shipping screen editing
      an address and need to pass these parameters back to the JSP.
    --%>
    <c:when test="${not empty nickName &&  not empty selectedAddress && not empty successURL}">
      <c:url var="couponTargetUrl" value="${requestURI}" context="/">
        <c:param name="nickName" value="${nickName}"/>
        <c:param name="selectedAddress" value="${selectedAddress}"/>
        <c:param name="successURL" value="${successURL}"/>
      </c:url>
    </c:when>
    <%--
      If we have the nickName and successURL parameters then we are on the 
      shippingAddressEdit.jsp from another screen, editing an address and need
      to pass these parameters back to the JSP.
    --%>
    <c:when test="${not empty nickName && not empty successURL}">
      <c:url var="couponTargetUrl" value="${requestURI}" context="/">
        <c:param name="nickName" value="${nickName}"/>
        <c:param name="successURL" value="${successURL}"/>
      </c:url>
    </c:when>
    <c:otherwise>
      <c:set var="couponTargetUrl" value="${requestURI}"/>
    </c:otherwise>
  </c:choose>

  <c:if test="${expressCheckout}">
    <c:url var="couponTargetUrl" value="${couponTargetUrl}" context="/">
      <c:param name="expressCheckout" value="true"/>
    </c:url>
  </c:if>

  <%--
    If there is an 'editCard' specified on the ProfileFormHandler, we should re-post it 
    with the next request. This property specifies which user's credit card should be edited 
    (mandatory for '/checkout/creditCardEdit.jsp'). This is why we have to re-post it when 
    applying a coupon.
  --%>
  <dsp:getvalueof var="selectedCard" vartype="java.lang.String" 
                  bean="/atg/userprofiling/ProfileFormHandler.editCard"/>

  <c:if test="${not empty selectedCard}">
    <dsp:input bean="/atg/userprofiling/ProfileFormHandler.editCard" type="hidden" value="${selectedCard}"/>
  </c:if>

  <c:choose>
    <%-- Is there a coupon code applied? --%>
    <c:when test="${not empty couponCode}">
      <c:choose>
        <%-- Are we editing the code? --%>
        <c:when test="${not empty editCoupon}">
          
          <%-- True, render input field and 'Apply Coupon' button. --%>
          <label class="atg_store_orderSummaryLabel" for="atg_store_promotionCodeInput">
            <fmt:message key="common.cart.promotionCode"/>
          </label>

          <span class="atg_store_orderSummaryItem atg_store_couponCode">
            <dsp:input bean="CouponFormHandler.couponCode" priority="10" type="text" id="atg_store_promotionCodeInput"
                       value="${couponCode}" autocomplete="off" dojoType="atg.store.widget.enterSubmit" 
                       targetButton="atg_store_applyCoupon" />
               
            <fmt:message var="updateTotalMsg" key="common.button.updateTotalText" />
            
            <dsp:input iclass="atg_store_textButton" id="atg_store_applyCoupon"
                       bean="CouponFormHandler.claimCoupon" type="submit" value="${updateTotalMsg}"/>
          </span>
       
        </c:when>
        <c:otherwise>
          <%-- Not in edit mode, just render the coupon code and 'Edit' link. --%>
          
          <span class="atg_store_orderSummaryLabel">
            <fmt:message key="common.cart.promotionCode"/>
          </span>

          <span class="atg_store_appliedCouponCode atg_store_orderSummaryItem">
            <%--
              Use hidden coupon field to maintain the existing coupon code as an
              empty coupon code will trigger it's removal.
            --%>
            <dsp:input bean="CouponFormHandler.couponCode" priority="10"
                       type="hidden" id="atg_store_promotionCodeInput" value="${couponCode}"/>

            <span>
              <fmt:message key="cart_promotionCode.orderMsg">
                <fmt:param value="${couponCode}"/>
              </fmt:message>
            </span>

            <%--
              If there is an 'editCard' specified on the ProfileFormHandler, we should re-post
              it with the next request. This property specifies which user's credit card should be
              edited (mandatory for '/checkout/creditCardEdit.jsp').
              That's why we have to re-post it when applying coupon.
            --%>
            <dsp:a href="${couponTargetUrl}" bean="/atg/userprofiling/ProfileFormHandler.editCard"
                   beanvalue="/atg/userprofiling/ProfileFormHandler.editCard">
              <dsp:param name="editCoupon" value="true"/>
              <fmt:message key="common.button.editText"/>
            </dsp:a>
          </span>
          
        </c:otherwise>
      </c:choose>
    </c:when>
    <c:otherwise>
      <%-- There is no coupon code applied. Render input field and the 'Claim Coupon' button. --%>
      <label class="atg_store_orderSummaryLabel" for="atg_store_promotionCodeInput">
        <fmt:message key="common.cart.promotionCode"/>
      </label>

      <span class="atg_store_couponCode atg_store_orderSummaryItem">
        <dsp:input bean="CouponFormHandler.couponCode" priority="10" type="text" 
                   id="atg_store_promotionCodeInput" autocomplete="off" dojoType="atg.store.widget.enterSubmit" 
                   targetButton="atg_store_applyCoupon" />

        <fmt:message var="updateTotalMsg" key="common.button.updateTotalText" />
        <dsp:input iclass="atg_store_textButton" id="atg_store_applyCoupon"
                   bean="CouponFormHandler.claimCoupon" type="submit" value="${updateTotalMsg}"/>
      </span>
      
    </c:otherwise>
  </c:choose>
  <%-- Additional input fields, success/error URLs. --%>
  <dsp:input bean="CouponFormHandler.applyCouponSuccessURL" type="hidden" value="${couponTargetUrl}"/>
  <dsp:input bean="CouponFormHandler.applyCouponErrorURL" type="hidden" value="${couponTargetUrl}"/>
  <dsp:input bean="CouponFormHandler.editCouponSuccessURL" type="hidden"  value="${couponTargetUrl}"/>
  <dsp:input bean="CouponFormHandler.editCouponErrorURL"  type="hidden"  value="${couponTargetUrl}"/>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/gadgets/promotionCode.jsp#2 $$Change: 788278 $--%>
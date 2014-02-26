<%-- 
  This is the last checkout step page.
  It displays all cart contents, shipping and billing information.
  It also displays button to confirm current order.

  Page includes:
    /mobile/global/gadgets/obtainHardgoodShippingGroup.jsp - Returns the following request-scoped variables:
      - hardgoodShippingGroups counter
      - hardgoodShippingGroup object
      - giftWrapCommerceItem
    /mobile/myaccount/myOrders.jsp - List of placed orders
    /mobile/global/gadgets/orderSummary.jsp - Renderer of order summary

  Required parameters:
    None

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>

  <dsp:getvalueof var="mobileStorePrefix" bean="/atg/store/StoreConfiguration.mobileStorePrefix"/>

  <dsp:param name="order" bean="ShoppingCart.current"/>

  <dsp:include page="${mobileStorePrefix}/global/gadgets/obtainHardgoodShippingGroup.jsp">
    <dsp:param name="order" param="order"/>
  </dsp:include>

  <%-- ========== Handle form exceptions ========== --%>
  <dsp:getvalueof var="formExceptions" bean="CommitOrderFormHandler.formExceptions"/>
  <jsp:useBean id="errorMap" class="java.util.HashMap"/>
  <c:if test="${not empty formExceptions}">
    <c:forEach var="formException" items="${formExceptions}">
      <c:set var="errorCode" value="${formException.errorCode}"/>
      <c:choose>
        <c:when test="${errorCode == 'invalidConfirmEmailAddress'}">
          <c:set target="${errorMap}" property="email" value="invalid"/>
        </c:when>
        <c:when test="${errorCode == 'confirmEmailAddressAlreadyExists'}">
          <c:set target="${errorMap}" property="email" value="inUse"/>
        </c:when>
      </c:choose>
    </c:forEach>
  </c:if>

  <c:choose>
    <c:when test="${requestScope.hardgoodShippingGroups != 1 || not empty requestScope.giftWrapCommerceItem}">
      <dsp:include page="${mobileStorePrefix}/myaccount/myOrders.jsp">
        <dsp:param name="redirectOrderId" param="order.id"/>
        <dsp:param name="hideOrderList" value="true"/>
        <dsp:param name="redirectUrl" value="/checkout/confirm.jsp"/>
      </dsp:include>
    </c:when>
    <c:otherwise>
      <fmt:message var="pageTitle" key="mobile.checkout_review_your_order.title"/>
      <crs:mobilePageContainer titleString="${pageTitle}">
        <jsp:body>
          <div>
            <h2><c:out value="${pageTitle}"/></h2>

            <%-- ========== Form ========== --%>
            <dsp:form formid="confirm" action="${pageContext.request.requestURI}" method="post">
              <dsp:include page="${mobileStorePrefix}/global/gadgets/orderSummary.jsp">
                <dsp:param name="order" param="order"/>
                <dsp:param name="isCheckout" value="true"/>
                <dsp:param name="hardgoodShippingGroup" value="${requestScope.hardgoodShippingGroup}"/>
              </dsp:include>

              <%-- ========== Redirection URLs ========== --%>
              <dsp:input bean="CommitOrderFormHandler.commitOrderSuccessURL" type="hidden" value="confirmResponse.jsp"/>
              <dsp:input bean="CommitOrderFormHandler.commitOrderErrorURL" type="hidden" value="confirm.jsp"/>

              <%-- Set the "salesChannel" property on the order to 'mobile' --%>
              <dsp:input bean="CommitOrderFormHandler.salesChannel" type="hidden" value="mobile"/>

              <dsp:droplet name="ProfileSecurityStatus">
                <dsp:oparam name="anonymous">
                  <h2><fmt:message key="mobile.checkout_confirmResponse.emailTitle"/></h2>
                  <div class="cartData">
                    <div class="content ${not empty errorMap['email'] ? 'errorState' : ''}">
                      <fmt:message key="mobile.checkout_confirmResponse.emailPlaceholder" var="emailHint"/>
                      <dsp:input type="email" id="confirmEmail" bean="CommitOrderFormHandler.confirmEmailAddress" name="email"
                                 placeholder="${emailHint}" aria-label="${emailHint}" autocapitalize="off"/>
                      <c:if test="${empty errorMap['email']}">
                        <fmt:message var="privacyTitle" key="mobile.company_privacyAndTerms.pageTitle"/>
                        <dsp:a page="${mobileStorePrefix}/company/terms.jsp" title="${privacyTitle}" class="icon-help"/>
                      </c:if>
                    </div>
                    <c:if test="${not empty errorMap['email']}">
                      <span class="errorMessage">
                        <fmt:message key="mobile.form.validation.${errorMap['email']}"/>
                      </span>
                    </c:if>
                  </div>
                </dsp:oparam>
              </dsp:droplet>

              <%-- "Submit" button --%>
              <div class="cartCheckout">
                <fmt:message var="submitBtnValue" key="mobile.checkout_confirmPlaceOrder.button.placeOrderText"/>
                <dsp:input bean="CommitOrderFormHandler.commitOrder" type="submit" class="mainActionButton" value="${submitBtnValue}"/>
              </div>
            </dsp:form>
          </div>
        </jsp:body>
      </crs:mobilePageContainer>
    </c:otherwise>
  </c:choose>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/checkout/confirm.jsp#3 $$Change: 788278 $--%>

<%--
  This page renders "My Orders" page.

  Page includes:
    /mobile/myaccount/gadgets/subheaderAccounts.jsp - Display subheader items
    /mobile/global/gadgets/obtainHardgoodShippingGroup.jsp - Returns the following request-scoped variables:
      - hardgoodShippingGroups counter
      - hardgoodShippingGroup object
      - giftWrapCommerceItem
    /global/util/orderState.jsp - Display order state
    /mobile/includes/crsRedirect.jspf - Renderer of the redirect prompt to the full CRS site

  Required parameters:
    None

  Optional parameters:
    redirectOrderId
      When not empty, "Redirect to the full CRS site" dialog appears
    hideOrderList
      When true, order list is hidden during the redirect dialog appeared. Used from confirm page
    redirectUrl
      Used in the modal dialog as redirection to the "full CRS" site link.
      If not defined, the redirection link points to the "full CRS" order detail page.
      Should not to contain the "orderId" parameter, use the "redirectOrderId" instead
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>

  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="redirectOrderId" param="redirectOrderId"/>

  <fmt:message var="pageTitle" key="mobile.myaccount_orders.title"/>
  <crs:mobilePageContainer titleString="${pageTitle}" displayModal="${empty redirectOrderId ? false : true}">
    <jsp:attribute name="modalContent">
      <%--
        If "redirectOrderId" is not empty then this page accessed from the "orderDetail.jsp" or
        from the "confirm.jsp" pages with order that does not supported in the mobile CRS.
        Order with multishipping group or with gift items handling and dialog template.
        Redirect dialog for full CRS.
      --%>
      <div id="modalMessageBox" ${empty redirectOrderId ? '' : 'style="display:block"'}>
        <div>
          <dsp:getvalueof param="redirectUrl" var="successURL"/>
          <c:if test="${empty successURL}">
            <c:set var="successURL" value="/myaccount/orderDetail.jsp"/>
          </c:if>
          <fmt:message var="topString" key="mobile.checkout_multishipping.information"/>
          <fmt:message var="bottomString" key="mobile.checkout_multishipping.redirectText"/>
          <dsp:getvalueof var="orderId" param="orderId"/>
          <%@include file="/mobile/includes/crsRedirect.jspf"%>
        </div>
      </div>
    </jsp:attribute>

    <jsp:body>
      <%-- ========== Subheader ========== --%>
      <fmt:message var="myOrders" key="mobile.myaccount_orders.title"/>
      <dsp:include page="./gadgets/subheaderAccounts.jsp">
        <dsp:param name="centerText" value="${myOrders}"/>
        <dsp:param name="highlight" value="center"/>
      </dsp:include>

      <div class="dataContainer">
        <div class="my_orders">
          <%-- ========== Form ========== --%>
          <form id="loadOrderDetailForm" action="orderDetail.jsp">
            <%-- This request parameter is used by "Redirect to the full CRS site" dialog (see above) --%>
            <input type="hidden" name="orderId" id="orderId"/>

            <dsp:droplet name="OrderLookup">
              <dsp:param name="userId" bean="Profile.id"/>
              <dsp:param name="sortBy" value="submittedDate"/>
              <dsp:param name="state" value="closed"/>
              <dsp:param name="numOrders" value="-1"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="orders" param="result"/>
                <ul class="dataList">
                  <c:forEach items="${orders}" var="order">
                    <dsp:include page="${mobileStorePrefix}/global/gadgets/obtainHardgoodShippingGroup.jsp">
                      <dsp:param name="order" value="${order}"/>
                    </dsp:include>

                    <c:set var="orderSelectJSFunction">
                      <c:choose>
                        <c:when test="${(requestScope.hardgoodShippingGroups != 1) || not empty requestScope.giftWrapCommerceItem}">CRSMA.myaccount.toggleRedirectWithOrderId('${order.id}');</c:when>
                        <c:otherwise>CRSMA.myaccount.loadOrderDetails('${order.id}');</c:otherwise>
                      </c:choose>
                    </c:set>
                    <li>
                      <a href="javascript:void(0)" onclick="${orderSelectJSFunction}" class="icon-ArrowRight">
                        <div class="content">
                          <div class="orderId">
                            <div>
                              <c:choose>
                                <c:when test="${not empty order.omsOrderId}">
                                  <c:out value="${order.omsOrderId}"/>
                                </c:when>
                                <c:otherwise>
                                  <c:out value="${order.id}"/>
                                </c:otherwise>
                              </c:choose>
                            </div>
                          </div>
                          <div class="orderLegend">
                            <div>
                              <fmt:message key="mobile.common.items"/>
                            </div>
                            <div>
                              <fmt:message key="mobile.myaccount_orders.orderPlaced"/>
                            </div>
                            <div>
                              <fmt:message key="mobile.common.status"/>
                            </div>
                          </div>
                          <div class="orderData">
                            <div>
                              <c:choose>
                                <c:when test="${order.containsGiftWrap}">
                                  <c:out value="${order.totalCommerceItemCount - 1}"/>
                                </c:when>
                                <c:otherwise>
                                  <c:out value="${order.totalCommerceItemCount}"/>
                                </c:otherwise>
                              </c:choose>
                            </div>
                            <div>
                              <dsp:getvalueof var="submittedDate" vartype="java.util.Date" param="order.submittedDate"/>
                              <dsp:getvalueof var="dateFormat" bean="LocaleTools.userFormattingLocaleHelper.datePatterns.shortWith4DigitYear"/>
                              <fmt:formatDate value="${order.submittedDate}" pattern="${dateFormat}"/>
                            </div>
                            <div>
                              <dsp:include page="/global/util/orderState.jsp">
                                <dsp:param name="order" value="${order}"/>
                              </dsp:include>
                            </div>
                          </div>
                        </div>
                      </a>
                    </li>
                  </c:forEach>
                </ul>
              </dsp:oparam>
              <dsp:oparam name="empty">
                <crs:messageContainer optionalClass="myOrdersEmpty" titleKey="myaccount_myOrders.noOrders"/>
              </dsp:oparam>
            </dsp:droplet>
          </form>
        </div>
      </div>

      <%-- Hide order list when the "hideOrderList" is set. See also "confirm.jsp" page --%>
      <dsp:getvalueof param="hideOrderList" var="hideOrderList"/>
      <c:if test="${hideOrderList}">
        <script>
          $(document).ready(function() {
            $("div.my_orders").hide();
            $("#modalOverlay").bind("click", function() {
              $("div.my_orders").show();
            });
          });
        </script>
      </c:if>
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/myaccount/myOrders.jsp#3 $$Change: 788278 $--%>

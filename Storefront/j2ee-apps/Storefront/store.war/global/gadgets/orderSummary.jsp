<%--
  This gadget displays complete order information. It displays order contents (all added products/SKUs),
  shipping and billing information, selected order extras (gift wrap and gift message).

  Required parameters:
    order
      Specifies an order to be displayed.

  Optional parameters:
    isCurrent
      Flags, if order to be displayed is current user's shopping cart. 'False' by default.
    hideSiteIndicator
      Flags, if site indicators should be hidden when rendering an order item.
--%>

<dsp:page>
  <dsp:getvalueof var="isCurrent" param="isCurrent"/>
  <dsp:getvalueof var="hideSiteIndicator" vartype="java.lang.String" param="hideSiteIndicator"/>
  
  <c:if test="${empty isCurrent}">
    <c:set var="isCurrent" value="false"/>
  </c:if>
  <dsp:getvalueof var="shippingGroups" vartype="java.util.Collection" param="order.shippingGroups"/>
  <div id="atg_store_shipmentInfoContainer">
    <div class="atg_store_shipmentInfo">
      <%-- Display proper order page header. --%>
      <c:choose>
        <c:when test="${not isCurrent}">
          <%-- It's not a shopping cart, display order status, placement date, etc. --%>
          <dsp:include page="/myaccount/gadgets/orderDetailIntro.jsp" />
        </c:when>
        <c:otherwise>
          <c:if test="${fn:length(shippingGroups) == 1}">
            <%-- It's Confirmation page and we have only one shipping group, display group's info. --%>
            <dsp:include page="/global/gadgets/orderSingleShippingInfo.jsp">
              <dsp:param name="isCurrent" value="${isCurrent}"/>
              <dsp:param name="shippingGroup" value="${shippingGroups[0]}"/>
            </dsp:include>
          </c:if>
        </c:otherwise>
      </c:choose>

      <%-- Display payment information here. --%>
      <dsp:include page="/checkout/gadgets/confirmPaymentOptions.jsp">
        <dsp:param name="isCurrent" value="${isCurrent}"/>
        <dsp:param name="order" param="order"/>
        <dsp:param name="expressCheckout" param="expressCheckout"/>
      </dsp:include>
    </div>
  </div>

  <c:choose>
    <%-- Multiple shipping groups case. --%>
    <c:when test="${fn:length(shippingGroups) > 1 || not isCurrent}">
      <c:forEach var="shippingGroup" items="${shippingGroups}" varStatus="shippingGroupStatus">
        <%-- Address and shipping information starts here. --%>
        <div id="atg_store_multishipGroupInfoContainer">
          <div class="atg_store_multishipGroupInfo">
            <dl class="atg_store_groupShippingAddress">
              <dt>
                <fmt:message key="checkout_confirmPaymentOptions.shipTo"/>:
              </dt>
              <dd>
                <%-- Display group's address. --%>
                <dsp:include page="/global/util/displayAddress.jsp">
                  <dsp:param name="address" value="${shippingGroup.shippingAddress}"/>
                </dsp:include>
                <%-- Display link to 'Change shipping info' page when rendering Confirm page. --%>
                <c:if test="${isCurrent}">
                  <dsp:a page="/checkout/shipping.jsp" title="">
                    <span><fmt:message key="common.button.editText" /></span>
                  </dsp:a>
                </c:if>
              </dd>
            </dl>
            <%-- Display shipping method. --%>
            <dl class="atg_store_groupPaymentMethod">
              <dt>
                <fmt:message key="checkout_confirmPaymentOptions.viaMethod"/>:
              </dt>
              <dd>
                <div class="shipMethod">
                  <c:choose>
                    <c:when test="${not empty shippingGroup.shippingMethod}">
                      <span>
                        <fmt:message key="common.delivery${fn:replace(shippingGroup.shippingMethod, ' ', '')}"/>
                      </span>
                    </c:when>
                    <c:otherwise>
                      <fmt:message key="common.noValue"/>
                    </c:otherwise>
                  </c:choose>
                  <%-- Display link to 'Change shipping info' page when rendering Confirm page. --%>
                  <c:if test="${isCurrent}">
                    <dsp:a page="/checkout/shipping.jsp" title="">
                      <fmt:message key="common.button.editText" />
                    </dsp:a>
                  </c:if>
                </div>
              </dd>
            </dl>
          </div>
        </div>

        <c:choose>
          <c:when test="${fn:length(shippingGroup.commerceItemRelationships) > 0}">
            <%-- Render all group's items. --%>        
            <fmt:message var="tableSummary" key="myaccount_orderSummary.tableSummary"/>        
            <table id="atg_store_itemTable" summary="${tableSummary}">
              <%-- proceed shipping group --%>
              <dsp:include page="/global/gadgets/orderItems.jsp">
                <dsp:param name="order" param="order"/>
                <dsp:param name="commerceItemRelationships" value="${shippingGroup.commerceItemRelationships}"/>
                <dsp:param name="displayProductAsLink" param="displayProductAsLink"/>
                <dsp:param name="displayAvailabilityMessage" value="${isCurrent}"/>
                <dsp:param name="hideSiteIndicator" value="${hideSiteIndicator}"/>
              </dsp:include>
            </table>
          </c:when>
          <c:otherwise>
            <crs:messageContainer titleKey="myaccount_orderSummary.noItems"/>
          </c:otherwise>
        </c:choose>
      </c:forEach>
    </c:when>
    <c:otherwise>
      <%-- Single shipping group case. --%>
      <div id="atg_store_cartContainer">
        <dsp:getvalueof var="commerceItems" vartype="java.lang.Object" param="order.commerceItems"/>
        <c:choose>
          <c:when test="${not empty commerceItems}">
            <%-- We've display shipping group info already, just display shipping group's items. --%>
            <dsp:getvalueof id="size" value="${fn:length(commerceItems)}"/>
            
            <fmt:message var="tableSummary" key="myaccount_orderSummary.tableSummary"/>  
            <table id="atg_store_itemTable" summary="${tableSummary}">
              <%-- Do not display availability messages for commerce items in the submitted order case. --%>
              <dsp:include page="/global/gadgets/orderItems.jsp">
                <dsp:param name="order" param="order"/>
                <dsp:param name="commerceItems" value="${commerceItems}"/>
                <dsp:param name="displayProductAsLink" param="displayProductAsLink"/>
                <dsp:param name="displayAvailabilityMessage" value="${isCurrent}"/>
                <dsp:param name="hideSiteIndicator" value="${hideSiteIndicator}"/>
              </dsp:include>
            </table>
          </c:when>
        </c:choose>
      </div>
    </c:otherwise>
  </c:choose>

  <%-- Render order extras at the very end. --%>
  <dsp:include page="/global/gadgets/orderExtras.jsp">
    <dsp:param name="order" param="order"/>
    <dsp:param name="displayAvailabilityMessage" value="${isCurrent}"/>
  </dsp:include>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/orderSummary.jsp#3 $$Change: 788278 $--%>

<%--
  This gadget renders 'Order Extras' section for an order specified.
  It indicates, if Gift Wrap was selected and renders a gift message added.

  Required parameters:
    order
      Specifies an order, whose order extras should be rendered.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:getvalueof var="containsGiftMessage" vartype="java.lang.String" param="order.containsGiftMessage"/>
  <dsp:getvalueof var="commerceItems" vartype="java.lang.Object" param="order.commerceItems"/>
  <dsp:getvalueof var="hideSiteIndicator" vartype="java.lang.String" param="hideSiteIndicator"/>

  <%-- Display contents only if we have either gift message or gift wrap. --%>
  <dsp:getvalueof var="giftWrapItem" param="order.giftWrapItem"/>  
                
  <c:if test="${not empty giftWrapItem || containsGiftMessage == 'true'}">
    
    <fmt:message var="tableSummary" key="checkout_confirmExtras.tableSummary"/>
    <table id="atg_store_itemTable" summary="${tableSummary}">
      <%-- First, display section header. --%>
      <tr>
        <dsp:getvalueof var="hideSiteIndicator" vartype="java.lang.String" param="hideSiteIndicator"/>
        <th class="atg_store_orderExtraHeader" scope="col"
            colspan="${empty hideSiteIndicator or (hideSiteIndicator == 'false') ? '6' : '5'}">
          <fmt:message key="checkout_confirmExtras.title"/>
        </th>
      </tr>

      <%-- Gift wrap goes first. --%>
      <c:if test="${not empty giftWrapItem}">
        <dsp:include page="/global/gadgets/orderItemRenderer.jsp">
          <dsp:param name="currentItem" value="${giftWrapItem}"/>
          <dsp:param name="count" value="1"/>
          <dsp:param name="size" value="1"/>
          <dsp:param name="hideSiteIndicator" value="${hideSiteIndicator}"/>
          <dsp:param name="displaySiteIcon" value="false"/>
        </dsp:include>
      </c:if>

      <%-- Then display gift message. --%>
      <dsp:include page="/checkout/gadgets/confirmGiftMessage.jsp">
        <dsp:param name="order" param="order"/>
        <dsp:param name="isCurrent" param="isCurrent"/>
      </dsp:include>
    </table>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/orderExtras.jsp#2 $$Change: 742374 $--%>
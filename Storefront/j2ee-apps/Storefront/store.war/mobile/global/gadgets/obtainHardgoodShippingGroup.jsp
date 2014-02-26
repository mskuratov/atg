<%--
  This gadget is intended to obtain the "hardgoodShippingGroups" counter,
  "hardgoodShippingGroup" object and "giftWrapCommerceItem".
  The "hardgoodShippingGroup" object can be used only if the "hardgoodShippingGroups" counter is 1.
  The "giftWrapCommerceItem" is set if it present in the order otherwise it removed from request scope,
  so you should test it for not empty before using.

  Required parameters:
    order
      Order to make calculations. It could be as processed order as shopping cart order

  Output JSTL variables (request-scope):
    hardgoodShippingGroups
      Count of the hardgood shipping groups in the order
    hardgoodShippingGroup
      Found out a hardgood shipping group object if present
    giftWrapCommerceItem
      Gift wrap for the order if present otherwise this variable will be reset to empty
--%>
<dsp:page>
  <%-- Check if order has more than one shipping group --%>
  <c:set var="hardgoodShippingGroups" value="0" scope="request"/>
  <c:remove var="hardgoodShippingGroup" scope="request"/>

  <dsp:getvalueof var="shippingGroups" param="order.shippingGroups"/>
  <c:forEach var="shippingGroup" items="${shippingGroups}">
    <dsp:param name="shippingGroup" value="${shippingGroup}"/>
    <c:set var="itemsSize" value="${fn:length(shippingGroup.commerceItemRelationships)}"/>
    <c:if test="${itemsSize > 0}">
      <c:if test="${shippingGroup.shippingGroupClassType == 'hardgoodShippingGroup'}">
        <c:set var="hardgoodShippingGroups" value="${requestScope.hardgoodShippingGroups + 1}" scope="request"/>
        <c:set var="hardgoodShippingGroup" value="${shippingGroup}" scope="request"/>
      </c:if>
    </c:if>
  </c:forEach>

  <c:remove var="giftWrapCommerceItem" scope="request"/>
  <dsp:getvalueof var="commerceItems" param="order.commerceItems"/>
  <c:forEach var="currentItem" items="${commerceItems}">
    <dsp:param name="currentItem" value="${currentItem}"/>
    <dsp:getvalueof var="commerceItemClassType" param="currentItem.commerceItemClassType"/>
    <c:if test="${commerceItemClassType == 'giftWrapCommerceItem'}">
      <c:set var="giftWrapCommerceItem" value="${currentItem}" scope="request"/>
    </c:if>
  </c:forEach>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/global/gadgets/obtainHardgoodShippingGroup.jsp#3 $$Change: 793006 $--%>

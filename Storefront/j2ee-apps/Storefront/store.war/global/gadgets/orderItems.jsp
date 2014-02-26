<%--
  This gadget displays order items based on the specified CommerceItems or CommerceItemRelationships.
  If CommerceItems are specified, they will be rendered. CommerceItemRelationships will be rendered otherwise.

  Required parameters:
    commerceItems
    commerceItemRelationships
      One of them should be specified. These parameters specify a collection of order items to be rendered on the page.

  Optional parameters:
    hideSiteIndicator
      Flags, if site indicator should not be displayed for an order item.
    displayProductAsLink
      Flags, if product details should be displayed as link.
    displayAvailabilityMessage
      Defines whether to display inventory availability message, or not.
--%>
<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/SharingSitesDroplet" />

  <dsp:getvalueof var="commerceItems" param="commerceItems" />
  <dsp:getvalueof var="commerceItemRelationships" param="commerceItemRelationships" />
  <dsp:getvalueof var="hideSiteIndicator" vartype="java.lang.String" param="hideSiteIndicator" />

  <%-- Display heading of the outer table. Display site indicator's cell only if it's needed. --%>
  <thead>
    <tr>
      <c:if test="${empty hideSiteIndicator or (hideSiteIndicator == 'false')}">
        <th class="site" scope="col"><fmt:message key="common.site" /></th>
      </c:if>
      <th class="item" colspan="2" scope="col"><fmt:message key="common.item" /></th>
      <th class="quantity" scope="col"><fmt:message key="common.qty" /></th>
      <th class="price" scope="col"><fmt:message key="common.price" /></th>
      <th class="total" scope="col"><fmt:message key="common.total" /></th>
    </tr>
  </thead>

  <%-- Now choose, which collection should be used for rendering. --%>
  <c:choose>
    <c:when test="${commerceItems != null}">
      <%-- commerceItems specified, use it. --%>
      <c:forEach var="currentItem" items="${commerceItems}" varStatus="status">
        <dsp:param name="currentItem" value="${currentItem}" />
        <dsp:getvalueof var="commerceItemClassType" param="currentItem.commerceItemClassType" />
        <%-- We will not render a Gift Wrap. --%>
        <c:if test="${commerceItemClassType != 'giftWrapCommerceItem'}">
          <dsp:include page="/global/gadgets/orderItemRenderer.jsp">
            <dsp:param name="order" param="order"/>
            <dsp:param name="currentItem" value="${currentItem}" />
            <dsp:param name="count" value="${status.count}" />
            <dsp:param name="size" value="${size}" />
            <dsp:param name="displayProductAsLink" param="displayProductAsLink" />
            <dsp:param name="hideSiteIndicator" value="${hideSiteIndicator}" />
          </dsp:include>
        </c:if>
      </c:forEach>
    </c:when>
    <c:otherwise>
      <%-- No commerceItems specified, display relationships instead. --%>
      <c:forEach var="commerceItemRelationship" items="${commerceItemRelationships}" varStatus="status">
        <dsp:param name="currentItem" value="${commerceItemRelationship.commerceItem}" />
        <dsp:getvalueof var="commerceItemClassType" param="currentItem.commerceItemClassType" />
        <%-- Do not display a Gift Wrap. --%>
        <c:if test="${commerceItemClassType != 'giftWrapCommerceItem'}">
          
          <%-- Price beans will be used later. --%>
          <dsp:include page="/global/gadgets/orderItemRenderer.jsp">
            <dsp:param name="order" param="order"/>
            <dsp:param name="commerceItemRelationship" value="${commerceItemRelationship}"/>
            <dsp:param name="currentItem" param="currentItem" />
            <dsp:param name="count" value="${status.count}" />
            <dsp:param name="size" value="${size}" />
            <dsp:param name="displayProductAsLink" param="displayProductAsLink" />
            <dsp:param name="displayAvailabilityMessage" param="displayAvailabilityMessage"/>
            <dsp:param name="hideSiteIndicator" value="${hideSiteIndicator}" />
          </dsp:include>
           
        </c:if>
      </c:forEach>
    </c:otherwise>
  </c:choose>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/orderItems.jsp#3 $$Change: 788278 $--%>
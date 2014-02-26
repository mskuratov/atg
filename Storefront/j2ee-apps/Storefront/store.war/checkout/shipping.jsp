<%-- 
  This page is an entry point into the shipping process. It determines, whether single or multiple 
  shipping page should be displayed and renders it. This page also adds cart items stored in the 
  UserItems component for the newly registered users.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>

  <%-- 
    If the order has any hard goods shipping groups, figure out which 
    hard goods page to use.
  --%>
  <dsp:getvalueof var="anyHardgoodShippingGroups" vartype="java.lang.String"
                  bean="ShippingGroupFormHandler.anyHardgoodShippingGroups"/>

  <%-- 
    Will be true if the order has more than one hardgood shipping group
    with commerce item relationships.
   --%>                  
  <dsp:getvalueof var="isMultipleHardgoodShippingGroups"
                  bean="ShippingGroupFormHandler.multipleHardgoodShippingGroupsWithRelationships"/>                  

  <c:choose>
    <c:when test='${anyHardgoodShippingGroups}'>
      <%-- 
        If the order has more than one hard goods shipping group, go to the
        multi-shipping group page. Otherwise, single shipping group page.
      --%>
      <c:choose>
        <%-- 
          show multi-shipping groups page if we have more than one gift shipping group 
          or at least one gift shipping group and one and more non-gift shipping groups.
        --%>
        <c:when test='${isMultipleHardgoodShippingGroups}'>
          <dsp:include page="shippingMultiple.jsp">
            <dsp:param name="init" value="true"/>
          </dsp:include>
        </c:when>
        <%-- Single shipping group? Then we'll display single shipping page. --%>
        <c:otherwise>
          <dsp:include page="shippingSingle.jsp">
            <dsp:param name="init" value="true"/>
          </dsp:include>
        </c:otherwise>
      </c:choose>
    </c:when>
  </c:choose>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/shipping.jsp#2 $$Change: 788278 $ --%>

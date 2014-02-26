<%@ page contentType="application/json; charset=UTF-8"%>

<%--
  This page renders the contents of the cart as JSON data. 
  This is the top-level container page that just sets the appropriate MIME type and includes
  the real data-generating page.
  
  Required parameters:
    None.
  
  Optional parameters:
    None.
--%>

<dsp:page>

  <dsp:getvalueof bean="/atg/commerce/ShoppingCart.current.CommerceItemCount" var="itemCount" />
  <dsp:importbean bean="/atg/store/profile/RequestBean"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/store/droplet/RemovedItemCheck"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
  
  <%--
    Check the current order for removed items. Removed items can exist when the
    order has been modified by multiple browsers. If a removed item is found,
    the order will be invalidated and the RepriceOrderDroplet will be invoked.
    
    Input Parameters:
      order
        The order that will be inspected.
      invalidateOrder (optional)
        Flag (true/false) that indicates that the order should be invalidated when 
        a removed item is found. If not set to 'true', the order will not be invalidated.
    
    Output Parameters:
      None.
      
    Open Parameters:
      true
        Serviced when a removed item is found in the order.
      false
        Serviced when a removed item is NOT found in the order.
  --%>
  <dsp:droplet name="RemovedItemCheck">
    <dsp:param name="order" bean="ShoppingCart.current"/>
    <dsp:param name="invalidateOrder" value="true"/>
    
    <dsp:oparam name="true">
      <%--
        This droplet executes 'repriceAndUpdateOrder' chain. It uses the current cart 
        as input order parameter for the chain in question.
      
        Output parameters:
          pipelineResult
            Result returned from the pipeline, if execution is successful.
          exception
            Exception thrown by the pipeline, if any.
    
        Open parameters:
          failure
            Rendered, if the pipeline has thrown an exception.
          successWithErrors
            Rendered, if pipeline successfully finished, but returned error.
          success
            Rendered, if pipeline successfully finished without errors returned.
    
        Input parameters:
          pricingOp
            Pricing operation to be performed on the current order.
      --%>
      <dsp:droplet name="RepriceOrderDroplet">
        <dsp:param name="pricingOp" value="ORDER_SUBTOTAL"/>
      </dsp:droplet>
    </dsp:oparam>
    
  </dsp:droplet>

  <c:choose>
    <c:when test="${itemCount==0}">
      <%-- Cart is empty --%>
      <json:object>
        <json:object name="itemsContainer">
          <json:property name="itemCount" value="${0}" />
          <json:property name="itemsQuantity" value="${0}" />
        </json:object>
      </json:object>
    </c:when>
    <c:otherwise>
      <json:object>

        <%-- Cart is not empty - render contents of cart --%>
        <dsp:include page="cartItems.jsp" />

        <%-- Add messages--%>
        <dsp:include page="cartMessages.jsp" />
        
      </json:object>
    </c:otherwise>
  </c:choose>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/json/cartContents.jsp#2 $$Change: 788278 $--%>

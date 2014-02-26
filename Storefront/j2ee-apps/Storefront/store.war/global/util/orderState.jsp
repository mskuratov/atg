<%--
  This gadget displays order state.
  
  Required parameters:
    order
      The order to display state for.
      
  Optional parameters:
    None.
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/commerce/order/OrderStatesDetailed"/>
    
  <%--
    Translates an order's state value into it's description.
    
    Input parameters:
      state
        The state value of order.
      elementName
        The optional parameter that should be used for the name of the description value 
        which is bound into the scope of the output oparam.
        
    Output parameters:
      element or specified in the <elementName> parameter
        Localized state description.
   --%>
  <dsp:droplet name="OrderStatesDetailed">
    <dsp:param name="state" param="order.stateAsString"/>
    <dsp:param name="elementName" value="orderStateDescription"/>
    <dsp:oparam name="output">
      <dsp:valueof param="orderStateDescription"><fmt:message key="common.orderProcessing"/></dsp:valueof>
    </dsp:oparam>
    <dsp:oparam name="error"><fmt:message key="common.orderProcessing"/></dsp:oparam>
  </dsp:droplet>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/util/orderState.jsp#2 $$Change: 788278 $--%>

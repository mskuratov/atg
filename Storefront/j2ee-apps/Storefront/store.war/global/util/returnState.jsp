<%--
  This gadget displays return state.
  
  Required parameters:
    returnRequest
      The return request to display state for.
      
  Optional parameters:
    None.
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnStateDescriptions"/>

  <%--
    Translates a return request's state value into it's description.
    
    Input parameters:
      state
        The state value of return request to describe.
      elementName
        The optional parameter that should be used for the name of the description value 
        which is bound into the scope of the output oparam.
        
    Output parameters:
      element or specified in the <elementName> parameter
        Localized state description.
   --%>
  <dsp:droplet name="ReturnStateDescriptions">
    <dsp:param name="state" param="returnRequest.state"/>
    <dsp:param name="elementName" value="returnStateDescription"/>
    <dsp:oparam name="output">
      <dsp:valueof param="returnStateDescription"><fmt:message key="common.returnDefaultState"/></dsp:valueof>
    </dsp:oparam>
    <dsp:oparam name="error"><fmt:message key="common.returnDefaultState"/></dsp:oparam>
    <dsp:oparam name="unset"><fmt:message key="common.returnDefaultState"/></dsp:oparam>
  </dsp:droplet>

</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/util/returnState.jsp#1 $$Change: 788278 $--%>

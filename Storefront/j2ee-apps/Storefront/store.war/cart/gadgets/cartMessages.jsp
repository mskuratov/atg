<%--
  This gadget displays messages from GWP promotions.
  
  Required Parameters
    None
    
  Optional Parameters
    mode
      silent mode means we don't want to display messages but invoke
      TargetingForEach to clean up message Slot
--%>
<dsp:page>

  <dsp:importbean bean="/atg/targeting/TargetingArray"/>
  <dsp:importbean bean="/atg/commerce/pricing/NoCleanBeforePricingSlot"/>
  <dsp:importbean bean="/atg/store/collections/filter/PromotionMessagesFilter"/>
  
  <dsp:getvalueof var="mode" param="mode"/>
  
  <%--
    This droplet performs a targeting operation with the help of its targeter, filter, and 
    sourceMap parameters, and sets the output elements parameter to the resulting array of target objects.
  
    In this case we will retrieve all pricing messages.
    
    Input Parameters
      targeter
        Slot with pricing messages
      filter 
        A component to filter duplicate messages from the items returned by targeter       

    Output Parameters
      element
        Message to display    
  --%>
  <dsp:droplet name="TargetingArray">
    <dsp:param name="targeter" bean="NoCleanBeforePricingSlot"/>
    <dsp:param name="filter" bean="PromotionMessagesFilter"/>
    <dsp:oparam name="output">
      <c:if test="${mode != 'silent'}">
      
        <dsp:getvalueof var="elements" param="elements"/>
        
        <c:forEach var="element" items="${elements}">
        
          <dsp:setvalue param="element" value="${element}"/>
          <dsp:getvalueof var="messageSummary" param="element.summary"/>
          
          <c:if test="${not empty messageSummary}">
              
            <div id="atg_store_formValidationError">
              <div class="errorMessage">
                
                <dsp:getvalueof var="title" param="element.identifier"/>
                
                <fmt:message key="${title}" var="messageTitle"/>
                
                <c:if test="${not empty messageTitle}">
                  <c:out value="${messageTitle}"/>&nbsp;
                </c:if>
                
                <dsp:valueof param="element.summary"/>
              
              </div>  
            </div>
          </c:if>
        </c:forEach>
      </c:if>      
    </dsp:oparam>
  </dsp:droplet>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/gadgets/cartMessages.jsp#2 $$Change: 788278 $--%>
<%--
  This gadget displays radio button per shipping method available. It also displays shipping price
  with current shipping method.

  Required parameters:
    None.

  Optional parameters:
    shippingGroup
      This shipping group will be used to calculate available shipping methods.
--%>
<dsp:page>

  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
  <dsp:importbean bean="/atg/commerce/pricing/AvailableShippingMethods"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  
  <dsp:getvalueof var="shippingGroup" param="shippingGroup"/> 
  
  <div class="atg_store_shippingOptionsContainer">
    <h3>
      <fmt:message key="checkout_shippingOptions.availableShippingMethods"/>
    </h3>
    
    <fieldset class="atg_store_AvailableShippingMethods">
      <ul>
        
        <%-- If shipping group is not passed, get first non-gift shipping group with relationships --%>
        <c:if test="${empty shippingGroup}">
          <dsp:getvalueof var="shippingGroup" 
            bean="ShippingGroupFormHandler.firstNonGiftHardgoodShippingGroupWithRels"/>
        </c:if>
        
        <%-- If there is no selected shipping group, take first gift shipping group. --%>
        <c:if test="${empty shippingGroup}">
          
          <dsp:getvalueof var="giftShippingGroups" vartype="java.lang.Object" 
                          bean="ShippingGroupFormHandler.giftShippingGroups"/>
          
          <c:if test="${not empty giftShippingGroups}">
            <dsp:getvalueof var="shippingGroup" value="${giftShippingGroups[0]}"/>
          </c:if>
        </c:if>

        <%-- Get current shipping method defined in the shipping group --%>
        <dsp:getvalueof value="${shippingGroup.shippingMethod}" var="currentMethod"/>

        <%--
          Iterates over the list of available shipping methods and determine default one.
          
          Input parameters:
            shippingGroup
              Shipping group. We pass the first one from the list of shipping groups in the current 
              order (shopping cart). We always have at least one hardgood shipping group in the cart. 
          
          Output parameters:
            availableShippingMethods
              List of shipping method codes 
        --%>
        <dsp:droplet name="AvailableShippingMethods">
          <dsp:param name="shippingGroup" value="${shippingGroup}"/>
          <dsp:oparam name="output">
            
            <dsp:getvalueof var="availableShippingMethods" vartype="java.lang.Object"
                            param="availableShippingMethods"/>
          
            <%-- Make sure our currently selected shipping method is available --%>
            <c:if test="${not empty currentMethod}">
              <c:set var="isCurrentInAvailableMethods" value="false"/>
              <c:forEach var="method" items="${availableShippingMethods}" varStatus="status">
                <c:if test="${currentMethod eq method}">
                  <c:set var="isCurrentInAvailableMethods" value="true"/>
                </c:if>
              </c:forEach>
            </c:if>
            
            <%-- Set a default shipping method if our current one isnt available --%>
            <c:if test="${empty currentMethod or not isCurrentInAvailableMethods}">
              <dsp:getvalueof bean="Profile.defaultCarrier" var="currentMethod"/>
            </c:if>

            <c:forEach var="method" items="${availableShippingMethods}" varStatus="status">
              
              <dsp:param name="method" value="${method}"/>
            
              <%-- 
                Used to determine shipping costs for the shipping group with specified 
                shipping method. The class's service method calls into the ShippingPricingEngine 
                priceShippingGroup method to get the ShippingPriceInfo for the specified shipping 
                group and the shipping method.
              
                 Required parameters:
                  shippingGroup
                    The ShippingGroup to price.
                  shippingMethod
                    The Shipping method to price with.
                  
                  Open parameters:
                    output
                      Rendered if the shipping cost is determined.
                  
                  Output parameters:
                    shippingPrice
                      A double value that corresponds to the shipping cost of the specified
                      shipping group with specified shipping method. 
              --%>
              <dsp:droplet name="/atg/store/pricing/PriceShippingMethod">
                <dsp:param name="shippingGroup" value="${shippingGroup}"/>
                <dsp:param name="shippingMethod" param="method"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="shippingPrice" param="shippingPrice" />
                </dsp:oparam>
              </dsp:droplet>
              
              <c:set var="shippingMethod" value="${fn:replace(method, ' ', '')}"/>
              <c:set var="shippingMethodResourceKey" value="checkout_shipping.delivery${shippingMethod}"/>
              <c:set var="shippingMethodContentResourceKey" value="${shippingMethodResourceKey}Content"/>
              
              <li class="${status.first ? 'first' : ''}${status.last ? 'last' : ''}">
                <c:choose>
                  <c:when test="${(currentMethod eq method) or (empty currentMethod and status.first)}">
                    <c:set value="${true}" var="isMethodAlreadyChosen"/>
                  </c:when>
                  <c:otherwise>
                    <c:set value="${false}" var="isMethodAlreadyChosen"/>
                  </c:otherwise>
                </c:choose>
                
                <%-- Display radio button for each shipping method. --%>
                <dsp:input type="radio" iclass="radio" checked="${isMethodAlreadyChosen}"
                           bean="ShippingGroupFormHandler.shippingMethod" paramvalue="method"
                           id="atg_store_shipping${shippingMethod}"/>
                
                <%-- Display name of the shipping method --%>
                <label for="atg_store_shipping${shippingMethod}">
                  <span class="atg_store_shippingMethodTitle">
                   
                    <fmt:message key="${shippingMethodResourceKey}"/>
                    <fmt:message key="common.labelSeparator"/>
                    
                    <%-- and its price --%>
                    <dsp:include page="/global/gadgets/formattedPrice.jsp">
                      <dsp:param name="price" value="${shippingPrice}"/>
                    </dsp:include>
                  
                  </span>
                </label>
                
              </li>
            </c:forEach>
          </dsp:oparam>
        </dsp:droplet><%-- End Available Shipping Methods Droplet --%>
      </ul>
    </fieldset>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/shippingOptions.jsp#2 $$Change: 788278 $--%>
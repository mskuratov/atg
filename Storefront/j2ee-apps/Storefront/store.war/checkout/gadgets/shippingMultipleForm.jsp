<%-- 
  This page renders the shipping-multiple form. It renders necessary hidden fields 
  and includes shipipngMultipleDestinations.jsp page.

  Required parameters:
    None.

  Optional parameters:
    init
      When set to true shipping related commerce objects are initialized.
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupDroplet"/>  
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
    
  <div id="atg_store_checkout" class="atg_store_main">
    <div class="atg_store_checkoutOption">
    
      <%-- INITALIZE COMMERCE SHIPPING OBJECTS --%>
      <dsp:getvalueof var="init" param="init"/> 
      
      <c:if test='${init == "true"}'>
        
        <dsp:getvalueof var="transient" bean="Profile.transient"/>
        
        <%--
          Used to initialize ShippingGroups and CommerceItemShippingInfo objects. We don't want to 
          clear shipping groups if the user is transient, if they re-enter the checkout previously
          entered addresses will appear. 
      
          Input Parameters:
            createOneInfoPerUnit
              Creates one CommerceItemShippingInfo for each individual unit contained by a commerce item.
            clear
              clear both the CommerceItemShippingInfoContainer and the ShippingGroupMapContainer.
            shippingGroupTypes
              Comma separated list of ShippingGroup types used to determine which ShippingGroupInitializer 
              components are executed.
            initShippingGroups
              ShippingGroup types will be initialized.
            initBasedOnOrder
              create a CommerceItemShippingInfo for each ShippingGroupCommerceItemRelationships in the order.
        
          Open Parameters:
            output
        --%>
        <dsp:droplet name="ShippingGroupDroplet">
          <dsp:param name="createOneInfoPerUnit" value="true"/>
          <dsp:param name="clearShippingInfos" param="init"/>
          <dsp:param name="clearShippingGroups" value="${not transient}"/>
          <dsp:param name="shippingGroupTypes" value="hardgoodShippingGroup"/>
          <dsp:param name="initShippingGroups" param="init"/>
          <dsp:param name="initBasedOnOrder" param="init"/>
          <dsp:oparam name="output"/>
        </dsp:droplet>
      </c:if>
      
      <%-- INITALIZE MULTI SHIPPING SPECIFIC OBJECTS --%>
      <dsp:setvalue bean="ShippingGroupFormHandler.initMultipleShippingForm" value=""/>
      
      <%-- 
        Check if user is anonymous or the session has expired. If so set the sessionExpirationURL
    
        Open parameters:
          anonymous - user is not logged in
          default - user has been recognized
      --%>
      <dsp:droplet name="ProfileSecurityStatus">
        <dsp:oparam name="anonymous">
          <%-- User is anonymous --%>
          <dsp:input type="hidden" bean="ShippingGroupFormHandler.sessionExpirationURL" 
                     value="${originatingRequest.contextPath}/index.jsp"/>
        </dsp:oparam>
        <dsp:oparam name="default">
          <dsp:input type="hidden" bean="ShippingGroupFormHandler.sessionExpirationURL" 
                     value="${originatingRequest.contextPath}/checkout/login.jsp"/>
        </dsp:oparam>
      </dsp:droplet>
  
      <%-- Success/error URLs. --%>
      <dsp:input type="hidden" bean="ShippingGroupFormHandler.shipToMultipleAddressesSuccessURL" 
                 value="billing.jsp"/>
      <dsp:input type="hidden" bean="ShippingGroupFormHandler.shipToMultipleAddressesErrorURL" 
                 value="shippingMultiple.jsp?init=true"/>
      <dsp:input type="hidden" bean="ShippingGroupFormHandler.address.email" beanvalue="Profile.email"/>
  
      <%-- Render all necessary visible form elements. --%>
      <dsp:include page="shippingMultipleDestinations.jsp"/>
  
      <%-- Display necessary buttons. --%>
      <fieldset class="atg_store_checkoutContinue">
        <div class="atg_store_formActions">
          
          <%-- Display 'Move to Billing' button. --%>
          <fmt:message var="continueButtonText" key="common.button.continueText"/>
          
          <span class="atg_store_basicButton">
            <dsp:input type="submit"  bean="ShippingGroupFormHandler.shipToMultipleAddresses" 
                       value="${continueButtonText}"/>
          </span>
          
          <%-- Display 'Ship to Single' button if needed. --%>
          <dsp:a href="${pageContext.request.contextPath}/checkout/shippingSingle.jsp">
            <dsp:param name="init" value="true"/>
            <fmt:message  key="common.button.singleAddressShipping"/>
          </dsp:a>
        
        </div>
      </fieldset>
    </div>
  </div>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/shippingMultipleForm.jsp#2 $$Change: 788278 $--%>

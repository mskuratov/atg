<%--
  This page renders the "Where did my promotion go?" details as a pop-up.
  
  Required Parameters:
    None.
    
  Optional Parameters:
    None.  
--%>

<dsp:page>

  <dsp:importbean bean="/atg/targeting/TargetingArray"/>
  <dsp:importbean bean="/atg/commerce/pricing/CleanBeforePricingSlot"/>
  
  <fmt:message var="promotionPopupTitle" key="global_promotionDetailsPopup.popupTitle"/>
  
  <crs:popupPageContainer pageTitle="${promotionPopupTitle}"
                          divId="atg_store_promotionDetails"
                          titleKey="global_promotionDetailsPopup.title">
                          
    <%--
      Retrieve all stacking rules messages
      
      Input Parameters
        targeter
          Slot with pricing messages
        filter 
          A component to filter duplicate stacking rule messages  
          
      Output Parameters
        elements
          Array of message to display    
     --%>
    <dsp:droplet name="TargetingArray">
      <dsp:param name="targeter" bean="CleanBeforePricingSlot"/>

      <dsp:oparam name="output">
        <dsp:getvalueof var="elements" param="elements"/>
        <ul class="promoList">
          <c:forEach var="element" items="${elements}">
            <dsp:setvalue param="element" value="${element}"/>
            <li>
              <p><dsp:valueof param="element.summary"/></p>
            </li>  
          </c:forEach>
        </ul>
      </dsp:oparam>
    </dsp:droplet>
  </crs:popupPageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/promotionDetailsPopup.jsp#1 $$Change: 735822 $ --%>

<%-- 
  This gadget renders available gift place holders for the current order (shopping cart).

  Required parameters:
    currentSiteSharesCart
      this flag determines if the current site can share shopping cart. If yes,
      we will render additional column for the site indicator.

  Optional parameters:
    None.
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/promotion/GiftWithPurchaseSelectionsDroplet"/>
  <dsp:importbean bean="/atg/commerce/promotion/GiftWithPurchaseFormHandler"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/commerce/promotion/PromotionLookup"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  
  <dsp:getvalueof var="currentSiteSharesCart" param="currentSiteSharesCart"/>
  
  <c:set var="gwpRowCount" value="0"/>
  
  <%--
    This droplet retrieves all of the gift with purchase selections 
    on the current order.
    
    Input Parameters
      order
        current order to process
        
      onlyOutputAvailableSelections
        if true then only selections 
        where quantityAvaiableForSelection is > 0 will be output
      
    Output Parameters
      selections
        collection of Selections beans with gift information    
  --%>
  <dsp:droplet name="GiftWithPurchaseSelectionsDroplet">
    <dsp:param name="order" bean="ShoppingCart.current"/>
    <dsp:param name="onlyOutputAvailableSelections" value="true" />
    
    <dsp:oparam name="output">
      
      <dsp:getvalueof var="selections" param="selections"/>
      
      <c:forEach var="selection" items="${selections}">
        <dsp:param name="selection" value="${selection}"/>
        
        <%-- For every available quantity render separate row --%>
        <dsp:getvalueof var="availableQuantity" param="selection.quantityAvailableForSelection"/>
        
        <%-- Split available quantity by 1 --%>
        <c:forEach var="i" begin="1" end="${availableQuantity}">
          
          <tr>
            
            <%-- Display empty site place holder --%>
            <c:if test="${currentSiteSharesCart == true}">
              <td class="site">
              </td>
            </c:if>

            <fmt:message key="common.freeGiftPlaceholder" var="giftPlaceholderTitle"/>
            
            <td class="image">
              <%-- Gift place holder image --%>
              <img alt="${giftPlaceholderTitle}" 
                   src="/crsdocroot/content/images/products/small/GWP_GiftWithPurchase_small.jpg">
            </td>

            <td class="item giftItemName" scope="row" abbr="<fmt:message key="common.freeGiftPlaceholder"/>">
              <span class="itemName"><fmt:message key="common.freeGiftPlaceholder"/></span>

              <%-- Link to the non-JS gift selection page. Pass gift selection information --%>
              <noscript>  
                <dsp:a href="noJsGiftSelection.jsp" title="${selectButtonText}" 
                       iclass="atg_store_basicButton atg_store_gwpSelectButtonNoJs">
                  <dsp:param name="giftType" param="selection.giftType"/>
                  <dsp:param name="giftDetail" param="selection.giftDetail"/>
                  <dsp:param name="giftHashCode" param="selection.giftHashCode"/>
                  <dsp:param name="promotionId" param="selection.promotionId"/>
                  <span>
                    <fmt:message key="common.select"/>
                  </span>
                </dsp:a>
              </noscript>
              
              <%-- Construct an URL with gift selector information --%>
              <c:url var="selectionUrl" value="gadgets/giftSelection.jsp">
                <c:param name="giftType" value="${selection.giftType}"/>
                <c:param name="giftDetail" value="${selection.giftDetail}"/>
                <c:param name="giftHashCode" value="${selection.giftHashCode}"/>
                <c:param name="promotionId" value="${selection.promotionId}"/>
              </c:url>
              
              <%--
                Link to the JS-based pop over dialog.
                We need to initialize HREF with proper link onclick first.
               --%>
               <a href="#" class="atg_store_basicButton atg_store_gwpSelectButton" 
                  onclick="dijit.byId('giftSelectorDialog').href='${selectionUrl}'; dijit.byId('giftSelectorDialog').refresh(); dijit.byId('giftSelectorDialog').show();">
                 <span>
                   <fmt:message key="common.select"/>
                 </span>
               </a>
               
            </td>

            <td class="cartActions">
              <ul> 
                <li>
                  <%-- Remove gift place holder --%>
                  <fmt:message key="common.button.removeText" var="removeText"/>
                 
                  <%-- 
                    Add the GWP placeholder promotion id and hashcode for this placeholder into 
                    the associated array in the formhandler. When removing the placeholder, these 
                    can then be referenced in the formhandler by using the 'gwpRowCount' value as 
                    the index into the array.
                  --%>
                  <dsp:input type="hidden" bean="CartModifierFormHandler.gwpPlaceholderPromotionIds" 
                             paramvalue="selection.promotionId"/>
                  <dsp:input type="hidden" bean="CartModifierFormHandler.gwpPlaceholderHashCodes" 
                             paramvalue="selection.giftHashCode"/>

                  <dsp:input iclass="atg_store_textButton atg_store_actionDelete" 
                             type="submit" submitvalue="${gwpRowCount}"
                             bean="CartModifierFormHandler.removeGwpPlaceholderFromOrder" value="${removeText}"/>

                  <c:set var="gwpRowCount" value="${gwpRowCount + 1}"/>
                </li>
              </ul>
            </td>

            <td class="price">
              <%-- We offer gifts for free --%>
              <fmt:message key="common.FREE"/>
              
              <%-- 
                Gift place holder doesn't provide any promotion notes but provides the promotion ID instead.
                We will retrieve promotion repository item using PromotionLookup and then
                get promotion's display name.
                
                Input Parameters
                  id
                    promotion ID
               
                Open Parameters
                  output
                    Rendered if the item was found in the repository.
                  
                Output Parameters
                  element
                    promotion repository item  
              --%>
              <dsp:droplet name="PromotionLookup">
                <dsp:param name="id" param="selection.promotionId"/>
                
                <dsp:oparam name="output">
                  
                  <%-- display promotion name under 'FREE' price --%>
                  <span class="atg_store_discountNote">
                    <dsp:valueof param="element.displayName" valueishtml="true"/>
                  </span>  
                </dsp:oparam>
              </dsp:droplet>
            </td>

            <%-- Gift quantity is already constant --%>
            <td class="quantity">
              <fmt:message key="common.singleItem"/>
            </td>
  
            <%-- Total gift price is always free --%>
            <td class="total">
              <fmt:message key="common.FREE"/>
            </td>
          </tr>          
        </c:forEach>

      </c:forEach>  
      
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/gadgets/giftPlaceholders.jsp#2 $$Change: 788278 $--%>

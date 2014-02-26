<%-- 
  This page renders JSON data when there are items in the cart.
  We use the following structure:
  - commerce items (regular or gift)
  - gift place holders
  
  Required parameters:
    None.
  
  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/commerce/promotion/GiftWithPurchaseSelectionsDroplet"/>
  
  <dsp:getvalueof var="items" bean="ShoppingCart.current.commerceItems"/>
  <dsp:getvalueof var="itemCount" bean="ShoppingCart.current.CommerceItemCount" />
  <dsp:getvalueof var="currencyCode" bean="ShoppingCart.current.priceInfo.currencyCode"/>
  <dsp:getvalueof var="subtotal" bean="ShoppingCart.current.priceInfo.amount"/>
  
  <c:set var="itemsQuantity" value="${0}"/>
  
  <c:forEach var="item" items="${items}">
    <dsp:param name="item" value="${item}"/>
    
    <dsp:getvalueof var="commerceItemClassType" param="item.commerceItemClassType"/>
    
    <c:if test="${commerceItemClassType != 'giftWrapCommerceItem'}">
      <c:set var="itemsQuantity" value="${itemsQuantity + item.quantity}"/>
    </c:if>
  </c:forEach>
  
  <json:object name="itemsContainer">
    <json:property name="itemCount" value="${itemCount}"/>
    <json:property name="subtotal">
      <dsp:include page="/global/gadgets/formattedPrice.jsp">
        <dsp:param name="price" value="${subtotal}"/>
      </dsp:include>
    </json:property>
    
    <json:array name="items">
      <c:forEach var="item" items="${items}">
        <dsp:param name="item" value="${item}"/>
        <dsp:getvalueof var="commerceItemClassType" param="item.commerceItemClassType"/>
      
        <c:if test="${commerceItemClassType != 'giftWrapCommerceItem'}">
        
          <%--
            Retrieve all of the gift with purchase selection items
            for the particular commerce item.
            
            Input Parameters:
              order
                order to be inspected
              item
                item to be inspected
            
            Output Parameters:
              selections
                a collection of GiftWithPurchaseSelection beans,
                contains all the necessary info about gift selections
            
            Open Parameters:
              output
                item is a gift item
              empty
                item is not a gift, i.e. regular commerce item            
           --%>
          <dsp:droplet name="GiftWithPurchaseSelectionsDroplet">
            <dsp:param name="order" bean="ShoppingCart.current"/>
            <dsp:param name="item" param="item"/>
            
            <%-- This item is a gift --%>
            <dsp:oparam name="output">
            
              <%-- Overall quantity of gifts --%>
              <c:set var="overallGiftQty" value="0"/>
              <dsp:getvalueof var="commerceItemQty" value="${item.quantity}"/> 
            
              <%-- Get the quantity of all items selected as gift --%>
              <dsp:getvalueof var="selections" param="selections" />
                    
              <c:forEach var="selection" items="${selections}">
                <dsp:param name="selection" value="${selection}" />
                
                <dsp:getvalueof var="automaticQuantity" param="selection.automaticQuantity"/>
                <dsp:getvalueof var="targetedQuantity" param="selection.targetedQuantity"/>
                <dsp:getvalueof var="selectedQuantity" param="selection.selectedQuantity"/>
                
                <c:set var="overallGiftQty" value="${automaticQuantity + targetedQuantity + selectedQuantity}"/>
              </c:forEach>
              
              <%-- 
                Split regular commerce items from gift items, for instance we
                have 3 'Cowgirl Bag' items in the shopping cart and 2 of them we got for free.
                We want to display 2 free items in one line and other items in another line. 
                
                Pass GWP promotion ID through excludeModelId parameter so that free gift quantity and price
                will not be included into the commerce item's quantity / price details. Free gift will be
                displayed on the separate line later. 
               --%>
              <c:if test="${overallGiftQty < commerceItemQty}">
                <dsp:include page="cartItem.jsp">
                  <dsp:param name="currentItem" value="${item}" />
                  <dsp:param name="itemQty" value="${commerceItemQty - overallGiftQty}"/>
                  <dsp:param name="excludeModelId" param="selection.promotionId"/>  
                </dsp:include>
              </c:if>  
              
              <%--
                Include free gift item. Pass special 'isGift' flag to hide item's price
                and a gift quantity which is a sum of all available quantities.
              --%>
              <dsp:include page="cartItem.jsp">
                <dsp:param name="isGift" value="true"/>
                <dsp:param name="currentItem" value="${item}" />
                <dsp:param name="itemQty" value="${overallGiftQty}"/>
              </dsp:include>
            </dsp:oparam>
            
            <%-- This item is a regular commerce item --%>
            <dsp:oparam name="empty">
              <dsp:include page="cartItem.jsp">
                <dsp:param name="currentItem" value="${item}" />
              </dsp:include>
            </dsp:oparam>
          </dsp:droplet>
          
        </c:if>
      </c:forEach>
      
      <%--
        This droplet retrieves all of the gift with purchase selections on the current order.
        In this case we append gift place holders to the each item in the returned selections list.
        
        Input Parameters:
          order
            current order from the cart.
          onlyOutputAvailableSelections
            true flag means that only selections where quantityAvaiableForSelection is > 0 will be output.  
            
        Output Parameters:
          selections
            collection of Selection beans with gift information.
          quantityToBeSelected
            quantity of items still to be selected.
            
        Open Parameters:
          output
            in case commerce item has been added as a gift or re-priced.
      --%> 
      <dsp:droplet name="GiftWithPurchaseSelectionsDroplet">
        <dsp:param name="order" bean="ShoppingCart.current"/>
        <%-- define null to reset previous item settings to process the whole order --%>
        <dsp:param name="item" value="${null}"/>
        <dsp:param name="onlyOutputAvailableSelections" value="true" />
        
        <dsp:oparam name="output">
          <%-- Retrieve quantity to be selected --%>
          <dsp:getvalueof var="quantityToBeSelected" param="quantityToBeSelected" scope="request"/>
          
          <dsp:getvalueof var="selections" param="selections"/>
          
          <c:forEach var="selection" items="${selections}">
            <dsp:param name="selection" value="${selection}"/>
            
            <dsp:include page="giftPlaceholder.jsp">
              <dsp:param name="currentSelection" param="selection" />
            </dsp:include>
            
          </c:forEach>
        </dsp:oparam>
      </dsp:droplet>  
      
    </json:array>
    
    <%-- Overall quantity is a sum of commerce items and gifts available for selection --%>
    <json:property name="itemsQuantity" value="${itemsQuantity + quantityToBeSelected}"/>
    
  </json:object>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/json/cartItems.jsp#2 $$Change: 788278 $--%>

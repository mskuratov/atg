<%-- 
  This gadget renders the header for the item list on the Shopping Cart page. It then 
  iterates through the cart, rendering each item. Details included with each item are:
    
    - Availability.
    - A visual site indicator for the site the item was added from.
    - An image of the item.
    - A list of SKU properties (size, color etc).
    - Buttons such as 'delete', 'add to comparisons' and 'add to wishlist'.
    - Price and quantity of item.
  
  This gadget must be contained inside of a form.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/CartSharingSitesDroplet" />
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/commerce/promotion/GiftWithPurchaseSelectionsDroplet"/>
  <dsp:importbean bean="/atg/commerce/promotion/PromotionLookup"/> 

  <%-- Get the items in the shopping cart --%>
  <dsp:getvalueof var="items" vartype="java.lang.Object" bean="ShoppingCart.current.commerceItems"/> 

  <c:if test="${not empty items}">
    <%--
      CartSharingSitesDroplet returns a collection of sites that share the shopping
  	  cart shareable (atg.ShoppingCart) with the current site. You may optionally exclude 
      the current site from the result.

      Input Parameters:
        excludeInputSite
          Boolean indicating if the returned sites list should include the current site.
   
      Open Parameters:
        output
          This parameter is rendered once, if a collection of sites is found.
   
      Output Parameters:
        sites
          The list of sharing sites.
    --%>
    <dsp:droplet name="CartSharingSitesDroplet">
      <dsp:param name="excludeInputSite" value="true"/>
      <dsp:oparam name="output">
        <c:set var="currentSiteSharesCart" value="true"/>
      </dsp:oparam>
      <dsp:oparam name="empty">
        <c:set var="currentSiteSharesCart" value="false"/>
      </dsp:oparam>
    </dsp:droplet>
        
    <fmt:message var="cartItemsTableSummary" key="cart_cartItemsTable.tableSummary"/>
    
    <table id="atg_store_itemTable" summary="${cartItemsTableSummary}"  
           cellspacing="0" cellpadding="0" border="0">
      <%-- 
        Display the headers in the form of site, item, price, quantity, total for the items 
        in the shopping cart. 
      --%>
      <thead>
        <tr>
          <%-- We will display site indicator only if current site shares the cart with some other site. --%>
          <c:if test="${currentSiteSharesCart == true}">
            <th class="site" scope="col"><fmt:message key="common.site"/></th>
          </c:if>
          <th class="item" colspan="3" scope="col"><fmt:message key="common.item"/></th>
          <th class="price" scope="col"><fmt:message key="common.price"/></th>
          <th class="quantity" scope="col"><fmt:message key="common.qty" /></th>
          <th class="total" scope="col"><fmt:message key="common.total"/></th>
        </tr>
      </thead>
      <tbody>
        <%-- Render each item in the shopping cart --%>
        <c:forEach var="currentItem" items="${items}" varStatus="status">
          <dsp:param name="currentItem" value="${currentItem}"/>
          
          <dsp:getvalueof id="count" value="${status.count}"/>
          <dsp:getvalueof var="commerceItemClassType" param="currentItem.commerceItemClassType"/>
          
          <c:choose>
            <c:when test='${commerceItemClassType == "giftWrapCommerceItem"}'>
              <%-- 
                Filter out the giftWrapCommerceItem, but add as a hidden input so it doesn't
                get removed from the cart during update process.
              --%>
              <input type="hidden" name="<dsp:valueof param='currentItem.id'/>" 
                     value="<dsp:valueof param='currentItem.quantity'/>">
            </c:when>
            <c:otherwise>
              <%-- 
                This droplet is used to retrieve all of the gift with purchase selections on an 
                order or a commerce item. If the commerce item is supplied then the selections 
                only apply to that item.
                
                In this case, check if this item has been added as a gift. If yes, it will be associated selections.
                
                Input Parameters:
                  order
                    current order from the cart
                  item
                    commerce item to check
                    
                Output Parameters:
                  selections
                    collection of Selection beans with gift information   
                    
                Open Parameters:
                  output
                    in case commerce item has been added as a gift or re-priced
                  empty
                    regular commerce item      
              --%> 
              <dsp:droplet name="GiftWithPurchaseSelectionsDroplet">
                <dsp:param name="order" bean="ShoppingCart.current"/>
                <dsp:param name="item" param="currentItem" />

                <%-- This commerce item have been added by GWP --%>
                <dsp:oparam name="output">
                  
                  <dsp:getvalueof var="commerceItemQty" param='currentItem.quantity'/>                  
                  <dsp:getvalueof var="selections" param="selections" />
                  
                  <c:forEach var="selection" items="${selections}">
                    
                    <dsp:param name="selection" value="${selection}" />
                    <c:set var="gwpCount" value="0"/>
                    
                   <%-- 
                     Even in case this item has been added as a gift, there is also a possibility
                     the Shopper added the same item(s). Check selection quantity vs commerce item
                     quantity. If commerce item quantity is greater, that means we have gift items
                     and commerce items. Display additional row as we do for regular commerce items.
                   --%>
                   <dsp:getvalueof var="automaticQuantity" param="selection.automaticQuantity"/>
                   <dsp:getvalueof var="targetedQuantity" param="selection.targetedQuantity"/>
                   <dsp:getvalueof var="selectedQuantity" param="selection.selectedQuantity"/>
          
                   <dsp:getvalueof var="selectedQty" 
                                   value="${automaticQuantity + targetedQuantity + selectedQuantity}"/>
                     
                   <c:if test="${commerceItemQty > selectedQty}">
                     
                     <dsp:getvalueof var="missedQty" value="${commerceItemQty - selectedQty }"/>
                     <c:set var="gwpCount" value="${gwpCount + 1}"/>
                     
                     <tr class="<crs:listClass count="${count}" size="${fn:length(items)}" selected="false"/>">
                        
                        <%-- Display site indicator only if current site shares the cart with some other site. --%>
                        <c:if test="${currentSiteSharesCart == true}">
                          
                          <%-- Visual indication of the site that the product belongs to. --%>
                          <td class="site">
                            <dsp:include page="/global/gadgets/siteIndicator.jsp">
                              <dsp:param name="mode" value="icon"/>
                              <dsp:param name="siteId" param="currentItem.auxiliaryData.siteId"/>
                              <dsp:param name="product" param="currentItem.auxiliaryData.productRef"/>
                            </dsp:include>
                          </td>
                        </c:if>
                        
                        <td class="image">
                          <dsp:include page="cartItemImage.jsp">
                            <dsp:param name="commerceItem" param="currentItem"/>
                          </dsp:include>
                        </td>
                           
                        <dsp:getvalueof var="productDisplayName" 
                                        param="currentItem.auxiliaryData.catalogRef.displayName"/>
                        
                        <c:if test="${empty productDisplayName}">
                          <dsp:getvalueof var="productDisplayName" 
                                          param="currentItem.auxiliaryData.productRef.displayName"/>
                          <c:if test="${empty productDisplayName}">
                            <fmt:message var="productDisplayName" key="common.noDisplayName" />
                          </c:if> 
                        </c:if>  
                          
                        <td class="item" scope="row" abbr="${productDisplayName}">
                          
                          <%-- Link back to the product detail page. --%>
                          <dsp:getvalueof var="url" vartype="java.lang.Object" 
                                          param="currentItem.auxiliaryData.productRef.template.url"/>
                          
                          <c:choose>
                            <c:when test="${not empty url}">
                              <dsp:include page="/global/gadgets/crossSiteLink.jsp">
                                <dsp:param name="item" param="currentItem"/>
                                <dsp:param name="skuDisplayName" 
                                           param="currentItem.auxiliaryData.catalogRef.displayName"/>
                              </dsp:include>
                            </c:when>
                            <c:otherwise>
                              <dsp:valueof value="${productDisplayName}"/>
                            </c:otherwise>
                          </c:choose>
        
                          <%-- Render all SKU-specific properties for the current item. --%>
                          <dsp:include page="/global/util/displaySkuProperties.jsp">
                            <dsp:param name="product" param="currentItem.auxiliaryData.productRef"/>
                            <dsp:param name="sku" param="currentItem.auxiliaryData.catalogRef"/>
                            <dsp:param name="displayAvailabilityMessage" value="true"/>
                          </dsp:include>
                        </td>
          
                        <%-- Display all necessary buttons for the current item. --%>
                        <td class="cartActions">
                          <ul>
                            <dsp:include page="itemListingButtons.jsp">
                              <%-- Count is used to construct input names. Don't use dynamic names. --%>
                              <dsp:param name="count" value="${count}"/>
                              <dsp:param name="gwpCount" value="${gwpCount}"/>
                              <dsp:param name="navigable" 
                                         param="currentItem.auxiliaryData.productRef.NavigableProducts"/>
                              <%-- Pass quantity to display --%>
                              <dsp:param name="missedQty" value="${missedQty}"/>
                            </dsp:include>
                          </ul>
                        </td>
          
                        <td class="price">
                          <dsp:include page="detailedItemPrice.jsp">
                            <dsp:param name="excludeModelId" param="selection.promotionId"/>
                          </dsp:include>
                        </td>
          
                        <td class="quantity"> 
                                                                       
                          <%-- Don't allow user to modify samples or collateral in cart. --%>
                          <dsp:getvalueof var="auxiliaryDataType" vartype="java.lang.String" 
                                          param="currentItem.auxiliaryData.catalogRef.type"/>
                         
                          <c:choose>
                            <c:when test='${auxiliaryDataType == "sampleSku"}'>
                              <input type="hidden" name="<dsp:valueof param='currentItem.id'/>" 
                                     value="<dsp:valueof param='currentItem.quantity'/>">        
                              
                              <dsp:valueof param="currentItem.quantity"/>
                            
                            </c:when>
                            <c:otherwise>
                              <fieldset>
                                <input class="text qty atg_store_numericInput" type="text" 
                                       title="<fmt:message key="common.qty" />" 
                                       name="<dsp:valueof param='currentItem.id'/>"
                                       value="<dsp:valueof value='${missedQty}'/>" 
                                       dojoType="atg.store.widget.enterSubmit"
                                       targetButton="atg_store_update">
        
                                <fmt:message var="updateItemText" key="common.button.updateItemText"/>
                                
                                <dsp:input id="atg_store_update" iclass="atg_store_textButton" type="submit"
                                           bean="CartModifierFormHandler.update" value="${updateItemText}"/>
                              </fieldset>
                            </c:otherwise>
                          </c:choose>
                        </td>
          
                        <td class="total">
                          
                          <dsp:getvalueof var="amount" vartype="java.lang.Double" param="currentItem.priceInfo.amount"/>
                          <dsp:getvalueof var="currencyCode" vartype="java.lang.String"
                                          bean="ShoppingCart.current.priceInfo.currencyCode"/>
                          
                          <dsp:include page="/global/gadgets/formattedPrice.jsp">
                            <dsp:param name="price" value="${amount}"/>
                          </dsp:include>
                        
                        </td>
                      </tr>
                       
                    </c:if>
                    
                    <%-- 
                      Process selections that have been added automatically, i.e.
                      items auto added as gifts and re-priced as free 
                     --%>
                    
                    <c:forEach begin="1" end="${automaticQuantity}">
                      <c:set var="gwpCount" value="${gwpCount + 1}"/>
                      <dsp:include page="/cart/gadgets/giftItem.jsp">
                        <dsp:param name="selection" param="selection"/>
                        <dsp:param name="currentItem" param="currentItem"/>
                        <dsp:param name="count" value="${count}"/>
                        <dsp:param name="gwpCount" value="${gwpCount}"/>
                        <dsp:param name="currentSiteSharesCart" value="${currentSiteSharesCart}"/>
                      </dsp:include>                        
                    </c:forEach>
                      
                    <%-- 
                      Process selections that have been targeted, i.e. items added as regular commerce 
                      items but re-priced as free gifts. 
                    --%>
                    <c:forEach begin="1" end="${targetedQuantity}">
                      <c:set var="gwpCount" value="${gwpCount + 1}"/>
                      <dsp:include page="/cart/gadgets/giftItem.jsp">
                        <dsp:param name="selection" param="selection"/>
                        <dsp:param name="currentItem" param="currentItem"/>
                        <dsp:param name="count" value="${count}"/>
                        <dsp:param name="gwpCount" value="${gwpCount}"/>
                        <dsp:param name="currentSiteSharesCart" value="${currentSiteSharesCart}"/>
                      </dsp:include>                        
                    </c:forEach>
                      
                    <%--
                      Selected quantity for the gift commerce item could be more than 1. This situation 
                      could occur if the shopper selected several identical gifts. That means we'll have 
                      1 commerce item with quantity > 1, but we'd like to display one gift item per line, 
                      so we will display several identical gift items in the shopping cart.
                    --%>   
                    <c:forEach begin="1" end="${selectedQuantity}">
                      <c:set var="gwpCount" value="${gwpCount + 1}"/>
                      <dsp:include page="/cart/gadgets/giftItem.jsp">
                        <dsp:param name="selection" param="selection"/>
                        <dsp:param name="currentItem" param="currentItem"/>
                        <dsp:param name="count" value="${count}"/>
                        <dsp:param name="gwpCount" value="${gwpCount}"/>
                        <dsp:param name="currentSiteSharesCart" value="${currentSiteSharesCart}"/>
                      </dsp:include>
                    </c:forEach>                    

                  </c:forEach>
                </dsp:oparam>
                
                <%-- This is regular commerce item --%>
                <dsp:oparam name="empty">
                
                  <tr class="<crs:listClass count="${count}" size="${fn:length(items)}" selected="false"/>">
                    <%-- Display site indicator only if current site shares the cart with some other site. --%>
                    <c:if test="${currentSiteSharesCart == true}">
                      <%-- Visual indication of the site that the product belongs to. --%>
                      <td class="site">
                        <dsp:include page="/global/gadgets/siteIndicator.jsp">
                          <dsp:param name="mode" value="icon"/>
                          <dsp:param name="siteId" param="currentItem.auxiliaryData.siteId"/>
                          <dsp:param name="product" param="currentItem.auxiliaryData.productRef"/>
                        </dsp:include>
                      </td>
                    </c:if>
                      
                    <td class="image">
                      <dsp:include page="cartItemImage.jsp">
                        <dsp:param name="commerceItem" param="currentItem"/>
                      </dsp:include>
                    </td>
                    
                    <dsp:getvalueof var="productDisplayName" param="currentItem.auxiliaryData.catalogRef.displayName"/>
                    
                    <c:if test="${empty productDisplayName}">
                      
                      <dsp:getvalueof var="productDisplayName" param="currentItem.auxiliaryData.productRef.displayName"/>
                      
                      <c:if test="${empty productDisplayName}">
                        <fmt:message var="productDisplayName" key="common.noDisplayName" />
                      </c:if>
                    </c:if>    
                        
                            
                    <td class="item" scope="row" abbr="${productDisplayName}">
                      
                      <%-- Link back to the product detail page. --%>
                      <dsp:getvalueof var="url" vartype="java.lang.Object" 
                                      param="currentItem.auxiliaryData.productRef.template.url"/>
                                      
                      <c:choose>
                        <c:when test="${not empty url}">
                          <dsp:include page="/global/gadgets/crossSiteLink.jsp">
                            <dsp:param name="item" param="currentItem"/>
                            <dsp:param name="skuDisplayName" param="currentItem.auxiliaryData.catalogRef.displayName"/>
                          </dsp:include>
                        </c:when>
                        <c:otherwise>
                          <dsp:valueof value="${productDisplayName}"/>
                        </c:otherwise>
                      </c:choose>
    
                      <%-- Render all SKU-specific properties for the current item. --%>
                      <dsp:include page="/global/util/displaySkuProperties.jsp">
                        <dsp:param name="product" param="currentItem.auxiliaryData.productRef"/>
                        <dsp:param name="sku" param="currentItem.auxiliaryData.catalogRef"/>
                        <dsp:param name="displayAvailabilityMessage" value="true"/>
                      </dsp:include>
                    </td>
      
                    <%-- Display all necessary buttons for the current item. --%>
                    <td class="cartActions">
                      <ul>
                        <dsp:include page="itemListingButtons.jsp">
                          <%-- Count is used to construct input names. Don't use dynamic names. --%>
                          <dsp:param name="count" value="${count}"/>
                          <dsp:param name="navigable" param="currentItem.auxiliaryData.productRef.NavigableProducts"/>
                        </dsp:include>
                      </ul>
                    </td>
    
                    <td class="price">
                      <dsp:include page="detailedItemPrice.jsp"/>
                    </td>
      
                    <td class="quantity">
                      
                      <%-- Don't allow user to modify samples or collateral in cart. --%>
                      <dsp:getvalueof var="auxiliaryDataType" vartype="java.lang.String" 
                                      param="currentItem.auxiliaryData.catalogRef.type"/>
                                      
                      <c:choose>
                        <c:when test='${auxiliaryDataType == "sampleSku"}'>
                          <input type="hidden" name="<dsp:valueof param='currentItem.id'/>" 
                                 value="<dsp:valueof param='currentItem.quantity'/>">
                                 
                          <dsp:valueof param="currentItem.quantity"/>
                          
                        </c:when>
                        <c:otherwise>
                          <fieldset>
                            <input class="text qty atg_store_numericInput" type="text" 
                                   title="<fmt:message key="common.qty" />" name="<dsp:valueof param='currentItem.id'/>"
                                   value="<dsp:valueof param='currentItem.quantity'/>" 
                                   dojoType="atg.store.widget.enterSubmit" targetButton="atg_store_update">
    
                            <fmt:message var="updateItemText" key="common.button.updateItemText"/>
                            
                            <dsp:input id="atg_store_update" iclass="atg_store_textButton" type="submit"
                                       bean="CartModifierFormHandler.update" value="${updateItemText}"/>
                                       
                          </fieldset>
                        </c:otherwise>
                      </c:choose>
                    </td>
      
                    <td class="total">
                      <dsp:getvalueof var="amount" vartype="java.lang.Double" param="currentItem.priceInfo.amount"/>
                      <dsp:getvalueof var="currencyCode" vartype="java.lang.String"
                                      bean="ShoppingCart.current.priceInfo.currencyCode"/>
                                      
                      <dsp:include page="/global/gadgets/formattedPrice.jsp">
                        <dsp:param name="price" value="${amount}"/>
                      </dsp:include>
                      
                    </td>
                  </tr>
                </dsp:oparam>
                
              </dsp:droplet>                
            </c:otherwise>
          </c:choose>
        </c:forEach>
        
        <%-- Include gift place holders offered by 'Gift With Purchase' promotions --%>
        <dsp:include page="/cart/gadgets/giftPlaceholders.jsp">
          <dsp:param name="currentSiteSharesCart" value="${currentSiteSharesCart}" />
        </dsp:include>

        <%-- Display div for pop over --%>
        <div id="giftSelectorDialog" dojoType="atg.store.Dialog" href="gadgets/giftSelection.jsp"
             style="overflow:visible; width: 410px; height: 620px; display:none;">
        </div>

      </tbody>
    </table>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/gadgets/cartItems.jsp#3 $$Change: 788278 $--%>

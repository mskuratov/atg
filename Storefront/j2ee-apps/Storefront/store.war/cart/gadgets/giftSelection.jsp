<%-- 
  This is pop over dialog for gift selection

  Required Parameters:
    promotionId
      The promotion ID of the gift selection.
    giftType
      type of the gift selection
    giftDetail
      detail of the gift, usually repository ID
    giftHashCode
      hash code for the gift selection

  Optional Parameters:
    commerceItemId
      ID of the selected commerce item. Not null if 
      user selects gift previously and wants to change selection
    selectedSkuId
      ID of the selected SKU item
    selectedProductId
      ID of the selected product associated with the selected SKU
    selectedColor
      Currently selected color.
    selectedSize
      Currently selected size. 
--%>
<dsp:page>

  <dsp:importbean bean="/atg/commerce/promotion/GiftWithPurchaseSelectionChoicesDroplet"/>
  <dsp:importbean bean="/atg/commerce/promotion/GiftWithPurchaseFormHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/commerce/collections/filter/droplet/InventoryFilterDroplet"/>
  <dsp:importbean bean="/atg/registry/CollectionFilters/GWPSelectionInventoryFilter"/>
  
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  
  <%-- Unpack parameters --%>
  <dsp:getvalueof var="giftType" param="giftType"/>
  <dsp:getvalueof var="giftDetail" param="giftDetail"/>
  <dsp:getvalueof var="promotionId" param="promotionId"/>
  <dsp:getvalueof var="giftHashCode" param="giftHashCode"/>
  <dsp:getvalueof var="commerceItemId" param="commerceItemId"/>
  <dsp:getvalueof var="selectedSkuId" param="selectedSkuId"/>
  <dsp:getvalueof var="selectedProductId" param="selectedProductId"/>
  <dsp:getvalueof var="selectedSize" param="selectedSize"/>
  <dsp:getvalueof var="selectedColor" param="selectedColor"/>
  
  <div class="atg_store_main" id="atg_store_shoppingCart">
    <div id="atg_store_gwpContainer">
    
      <h3><fmt:message key="cart_noJsGiftSelection.title"/></h3>
      <a href="#" class="atg_store_gwpClose" 
         onclick="dijit.byId('giftSelectorDialog').hide();"><fmt:message key="common.closeWindowText"/></a>
      
      <div id="atg_store_pickerValidationError">
      </div>
   
      <dsp:droplet name="GiftWithPurchaseSelectionChoicesDroplet">
        <dsp:param name="giftType" value="${giftType}"/>
        <dsp:param name="giftDetail" value="${giftDetail}"/>
        <dsp:param name="alwaysReturnSkus" value="${true}"/>
        
        <dsp:oparam name="output">
          <div id="atg_store_gwpSelector">
                  
            <dsp:getvalueof var="choices" param="choices"/>
            
            <%--
              Check inventory status for gift choices
              
              Input parameters
                collection
                  collection of GWP Selection beans
                filter
                  filter service which checks inventory status of selection's skus  
             --%>
            <dsp:droplet name="InventoryFilterDroplet">
              <dsp:param name="filter" bean="GWPSelectionInventoryFilter"/>
              <dsp:param name="collection" value="${choices}"/>
              
              <dsp:oparam name="output">
                <fmt:message var="tableSummary" key="GWP.GiftSelectionTableSummary"/>
                <table cellspacing="0" cellpadding="0" border="0" id="atg_store_itemTable" summary="${tableSummary}">
                <tbody>
                  
                  <tr class="atg_store_giftSelectionTableHeader">
                    <th scope="col"><fmt:message key="common.item"/></th>
                    <th scope="col"><fmt:message key="common.color"/>/<fmt:message key="common.size"/></th>
                    <th scope="col"><fmt:message key="common.select"/></th>
                  </tr>
                  
                  <c:choose>
                    <c:when test="${giftType == 'skuContentGroup'}">
                      <%--
                        The free gift selection is made up of a SKU content group. Each SKU will
                        be displayed on the separate line.
                      --%>
                      
                      <%--
                        Iterates over the collection of sorted GWP selection beans
                        ordered by product's display name.
                        
                        Input Parameters
                          array
                            The filtered collection we wish to iterate over.
                             
                        Open Parameters
                          outputStart
                            Anything we want to output on the first iteration goes into this. 
                          output
                            Rendered for each element in the collection.
                          outputEnd
                            Anything we want to output on the last iteration goes into this.
                      --%>
                      <dsp:droplet name="ForEach">
                        <dsp:param name="array" param="filteredCollection" />
                        
                        <dsp:oparam name="output">
                          <dsp:setvalue param="choice" paramvalue="element"/>
                          <dsp:getvalueof var="productId" param="choice.product.repositoryId"/>
                          <dsp:getvalueof var="count" param="count"/>
                          <dsp:getvalueof var="size" param="size"/>
                          <dsp:setvalue param="sku" paramvalue="choice.skus[0]"/>
                          <dsp:getvalueof var="skuId" param="sku.repositoryId"/>
                          
                          <%-- 
                            Check choice.product.sku type
                           --%>
                           <tr>
                             <dsp:getvalueof var="skuType" param="sku.type"/>
                             <c:choose>
                               <c:when test="${skuType == 'sku'}">
                                  
                                  <dsp:getvalueof var="skuDisplayName" param="sku.displayName"/>
                                
                                  <c:if test="${empty skuDisplayName}">
                                    <fmt:message var="skuDisplayName" key="common.noDisplayName" />
                                  </c:if>
              
                               
                                  <td class="image">
                                    <img src="<dsp:valueof param='choice.product.smallImage.url'/>" />
                                  </td>
                
                                  <td class="item" scope="row" abbr="${skuDisplayName}">
                                    <label for="atg_store_giftProduct${count}">
                                      <span class="itemName"><c:out value="${skuDisplayName}"/></span>
                                    </label>
                                  </td>
                                
                               </c:when>
                               <c:otherwise>
                                
                                 <td class="image">
                                   <img src="<dsp:valueof param='choice.product.smallImage.url'/>" alt="${productName}"/>
                                 </td>
                                 
                                 <dsp:getvalueof var="productDisplayName" param="choice.product.displayName"/>
                                 
                                 <c:if test="${empty productDisplayName}">
                                   <fmt:message var="productDisplayName" key="common.noDisplayName" />
                                 </c:if>
               
                                 <td class="item" scope="row" abbr="${productDisplayName}">
                                   <label for="atg_store_giftProduct${count}">
                                     <span class="itemName"><c:out value="${productDisplayName}"/></span>
                                   </label>
                                   
                                   <div id="atg_gift_picker_${productId}">
                                     <div class="atg_store_pickerContainer">
                                       <div class="atg_store_colorPicker">
                                         <label class="atg_store_pickerLabel">
                                           <fmt:message key="common.${skuType == 'clothing-sku' ? 'color' : 'woodFinish'}"/>
                                           <fmt:message key="common.labelSeparator"/>
                                         </label>     
                                         <span class="selector">
                                           
                                           <dsp:getvalueof var="colorName" param="sku.color"/>
                                           <dsp:getvalueof var="imageurl" vartype="java.lang.String" param="sku.colorSwatch.url"/>
    
                                           <a class="atg_store_pickerAttribute" href="javascript:void(0)" title="${colorName}"
                                              onclick="atg.store.picker.clickGiftSku('${productId}', '${skuId}', '${skuType}' ,'atg_store_giftProduct${count}');">
                                             <dsp:img src="${imageurl}" width="15" height="15" alt="${colorName}"/>
                                           </a>
         
                                           <%-- Display selected color name --%>
                                           <span class="active">
                                             <c:out value="${colorName}" escapeXml="true"/>       
                                           </span>
                                         </span>
                                       </div>
        
                                       <%-- For clothing SKU, display size too --%>
                                       <c:if test="${skuType == 'clothing-sku'}"> 
                                         <div class="atg_store_sizePicker">
                                           <label class="atg_store_pickerLabel">
                                             <fmt:message key="common.size"/><fmt:message key="common.labelSeparator"/>
                                           </label>
   
                                           <span class="selector">
                                             <dsp:getvalueof var="sizeName" param="sku.size"/>
          
                                             <fmt:message var="oneSize" key="common.size.oneSize"/>
  
                                             <a class="atg_store_pickerAttribute${oneSize == sizeName ? ' atg_store_oneSize' : ''}" href="javascript:void(0)"
                                                title="${sizeName}" onClick="atg.store.picker.clickGiftSku('${productId}', '${skuId}', '${skuType}' ,'atg_store_giftProduct${count}');">
                                               <c:out value="${sizeName}"/>
                                             </a>
                                           </span>
                                         </div>   
                                       </c:if>
            
                                     </div>
                                   </div>
                                   
                                 </td>
                                
                                
                               </c:otherwise>
                             </c:choose>
                             
                             <td class="atg_store_gwpSelect">
                               
                               <c:choose>
                                 <c:when test="${size > 1}">
                                   <%--
                                     As we have more than 1 rows display radio buttons. When selecting specific gift product / SKU
                                     using the radio button the JavaScript clickGiftSku function updates form handlers properties dependent
                                     on the selected product / SKU.
                                   --%>
                                 
                                   <c:choose>
                                     <c:when test="${skuId == selectedSkuId}">
                                       <input type="radio" name="giftProductId" 
                                              value="${skuId}"
                                              checked="checked" 
                                              onclick="atg.store.picker.clickGiftSku('${productId}', '${skuId}', '${skuType}');"
                                              id="atg_store_giftProduct${count}">
                                     </c:when>
                                     <c:otherwise>
                                       <input type="radio" name="giftProductId" 
                                              value="${productId}"
                                              onclick="atg.store.picker.clickGiftSku('${productId}', '${skuId}', '${skuType}');"
                                              id="atg_store_giftProduct${count}">
                                     </c:otherwise>                    
                                   </c:choose>
                                 </c:when>
                                 <c:otherwise>
                                   <c:set var="selectedProductId" value="${productId}" />
                                   <c:set var="selectedSkuId" value="${skuId}" />
                                   <c:set var="selectedSkuType" value="${skuType}" />
                                 </c:otherwise>
                               </c:choose>
                             </td>
                             
                           </tr>
                        </dsp:oparam>
                      </dsp:droplet>
                                    
                    </c:when>
                    <c:otherwise>
                    
                      <%-- 
                        Variable to count number of gift item. Is used to generate 
                        not unique ids for labels and inputs                  
                      --%>
                      <c:set var="count" value="0"/>
                      <%--
                        Iterates over the collection of sorted GWP selection beans
                        ordered by product's display name.
                        
                        Input Parameters
                          array
                            The filtered collection we wish to iterate over.
                             
                        Open Parameters
                          outputStart
                            Anything we want to output on the first iteration goes into this.
                            In this case we will output the title of the recently viewed
                            items feature along with an HTML unordered list opening tag. 
                          output
                            Rendered for each element in the collection.
                            In this case each product in the filteredCollection will
                            displayed.
                          outputEnd
                            Anything we want to output on the last iteration goes into this.
                            In this case we will close add an HTML unordered list tag.
                       --%>
                      
                      <dsp:droplet name="ForEach">
                        <dsp:param name="array" param="filteredCollection" />
                        
                        <dsp:oparam name="output">
                          <dsp:setvalue param="choice" paramvalue="element"/>
                          <dsp:getvalueof var="productId" param="choice.product.repositoryId"/>
                          <dsp:getvalueof var="count" param="count"/>
                          <dsp:getvalueof var="size" param="size"/>
                          <%-- 
                            Check choice.product.sku type
                           --%>
                           <dsp:getvalueof var="skuType" param="choice.skus[0].type"/>
                           <dsp:getvalueof var="skus" param="choice.skus"/>
                           
                           <%--
                             Check if we will display only one gift selection row, so that to decide whether
                             we need to display radio buttons for each row.
                           --%>
                           <c:if test="${empty isOneRow}">
                             <c:set var="isOneRow" value="${size == 1 && !(skuType == 'sku' && fn:length(skus)>1)}"/>
                           </c:if>
                           
                           <c:choose>
                            <c:when test="${skuType == 'sku'}">
                              
                              <c:forEach var="sku" items="${skus}" varStatus="status">
                                <dsp:setvalue param="sku" value="${sku}"/>
                                <dsp:getvalueof var="skuId" param="sku.repositoryId"/>
                                <dsp:getvalueof var="skuDisplayName" param="sku.displayName"/>
                                
                                <c:if test="${empty skuDisplayName}">
                                  <fmt:message var="skuDisplayName" key="common.noDisplayName" />
                                </c:if>
              
                                <tr>
                                  <td class="image">
                                    <img src="<dsp:valueof param='choice.product.smallImage.url'/>" />
                                  </td>
                
                                  <td class="item" scope="row" abbr="${skuDisplayName}">
                                  
                                    <c:set var="count" value="${count + 1}"/>
                                     
                                    <label for="atg_store_giftProduct${count}">
                                      <span class="itemName"><c:out value="${skuDisplayName}"/></span>
                                    </label>
                                    
                                    <%-- We can't attach form under forEach because it will break table layout --%>
                                    <c:if test="${status.index == 0}">
                                      <dsp:form formid="colorsizerefreshform_${productId}" id="colorsizerefreshform_${productId}" method="post"
                                                action="${pageContext.request.contextPath}/cart/gadgets/giftSelection.jsp">
                                        <input name="skuId" type="hidden" value='<dsp:valueof param="choice.skus[0].repositoryId"/>'/>
                                        <input name="productId" type="hidden" value='<dsp:valueof param="choice.product.repositoryId"/>'/>
                                        <input name="skuType" type="hidden" value="sku"/>
                                      </dsp:form>
                                    </c:if>
                                  </td>
                                  
                                  <dsp:getvalueof var="productId" param="choice.product.repositoryId"/>
                                  
                                  <td class="atg_store_gwpSelect">
                                    
                                    <c:choose>
                                      <c:when test="${!isOneRow}">
                                        <%--
                                          As we have more than 1 rows display radio buttons. When selecting specific gift product / SKU
                                          using the radio button the JavaScript clickGiftSku function updates form handlers properties dependent
                                          on the selected product / SKU.
                                        --%>
                                      
                                        <c:choose>
                                          <c:when test="${skuId == selectedSkuId}">
                                            <input type="radio" name="giftProductId" 
                                                   value="${productId}"
                                                   checked="checked" 
                                                   onclick="atg.store.picker.clickGiftProduct('${productId}', '${skuId}');"
                                                   id="atg_store_giftProduct${count}">
                                          </c:when>
                                          <c:otherwise>
                                            <input type="radio" name="giftProductId" 
                                                   value="${productId}"
                                                   onclick="atg.store.picker.clickGiftProduct('${productId}', '${skuId}');"
                                                   id="atg_store_giftProduct${count}">
                                          </c:otherwise>                    
                                        </c:choose>
                                      </c:when>
                                      <c:otherwise>
                                        <c:set var="selectedProductId" value="${productId}" />
                                        <c:set var="selectedSkuId" value="${skuId}" />
                                        <c:set var="selectedSkuType" value="${skuType}" />
                                      </c:otherwise>
                                    </c:choose>
                                    
                                  </td>
                                </tr>
                                
                              </c:forEach> 
                              
                            </c:when>
                            <c:otherwise>
                              <tr>
                                <td class="image">
                                  <img src="<dsp:valueof param='choice.product.smallImage.url'/>" alt="${productName}"/>
                                </td>
                                
                                <dsp:getvalueof var="productDisplayName" param="choice.product.displayName"/>
                                <c:if test="${empty productDisplayName}">
                                  <fmt:message var="productDisplayName" key="common.noDisplayName" />
                                </c:if>
              
                                <td class="item" scope="row" abbr="${productDisplayName}">
                                
                                  <c:set var="count" value="${count + 1}"/>
                                  
                                  <label for="atg_store_giftProduct${count}">
                                    <span class="itemName"><c:out value="${productDisplayName}"/></span>
                                  </label>
                                                                    
                                  <dsp:include page="/cart/gadgets/giftPickerContent.jsp">
                                    <dsp:param name="productId" param="choice.product.repositoryId"/>
                                    <dsp:param name="gwpRadioId" value="atg_store_giftProduct${count}"/>
                                    <dsp:param name="selectedSize" value="${(productId == selectedProductId)?selectedSize:''}"/>
                                    <dsp:param name="selectedColor" value="${(productId == selectedProductId)?selectedColor:''}"/>
                                  </dsp:include>
                                   
                                </td>
                                
                                <td class="atg_store_gwpSelect">
                                  
                                  <c:choose>
                                    <c:when test="${!isOneRow}">
                                      <%--
                                        As we have more than 1 rows display radio buttons. When selecting specific gift product / SKU
                                        using the radio button the JavaScript clickGiftSku function updates form handlers properties dependent
                                        on the selected product / SKU.
                                      --%>
                                    
                                      <c:choose>
                                        <c:when test="${productId == selectedProductId}">
                                          <input type="radio" name="giftProductId" 
                                                 value="${productId}"
                                                 checked="checked" 
                                                 onclick="atg.store.picker.clickGiftProduct('${productId}');"
                                                 id="atg_store_giftProduct${count}">
                                        </c:when>
                                        <c:otherwise>
                                          <input type="radio" name="giftProductId" 
                                                 value="${productId}"
                                                 onclick="atg.store.picker.clickGiftProduct('${productId}');"
                                                 id="atg_store_giftProduct${count}">
                                        </c:otherwise>                    
                                      </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                      <c:set var="selectedProductId" value="${productId}" />
                                    </c:otherwise>
                                  </c:choose>
                                  
                                </td>
                              </tr>
                            </c:otherwise>
                           </c:choose>
                        </dsp:oparam>
                      </dsp:droplet>
                    </c:otherwise>
                  </c:choose>
                    
                </tbody>
              </table>
            </dsp:oparam>
            
            <%-- Display message if all items are out of stock --%>
            <dsp:oparam name="empty">
            
              <%-- Out of stock error message --%>
              <div class="errorMessage">
                <p>
                  <fmt:message key="gwp.itemsOutOfStock"/>
                </p>  
              </div>  
              
              <%-- Flag that all gifts are out of stock --%>
              <c:set var="itemsOutOfStock" value="true" />
              
            </dsp:oparam>
          </dsp:droplet>
              
          </div>
        </dsp:oparam>
        
        <dsp:oparam name="empty">
          <%-- Gifts are not available message --%>
          <div class="errorMessage">
            <p>
              <fmt:message key="gwp.itemsOutOfStock"/>
            </p>  
          </div>  
              
          <%-- Flag that all gifts are not avalibale --%>
          <c:set var="itemsNotAvailable" value="true" />
        </dsp:oparam>
      </dsp:droplet>  
    <div id="atg_store_gwpFooter">

      <c:choose>
        <c:when test="${(itemsOutOfStock ne true) and (itemsNotAvailable ne true)}"> 
    
          <dsp:getvalueof var="contextroot" bean="/OriginatingRequest.contextPath"/>
          
          <c:url var="selectionUrl" value="cart/gadgets/giftSelection.jsp">
            <c:param name="giftType" value="${giftType}"/>
            <c:param name="giftDetail" value="${giftDetail}"/>
            <c:param name="giftHashCode" value="${giftHashCode}"/>
            <c:param name="promotionId" value="${promotionId}"/>
            <c:param name="selectedSkuId" value="${selectedSkuId}"/>
            <c:param name="selectedProductId" value="${selectedProductId}"/>
            <c:param name="commerceItemId" value="${commerceItemId}"/>
          </c:url>
          
          <dsp:form id="gwpform" name="gwpform" formid="gwpform" 
                    action="${originatingRequest.requestURI}" method="post" >
        
            <fmt:message key="common.button.addToCartText" var="addToCartCaption"/>
              
            <%-- 
              If commerce item ID has been passed in, we need to replace 
              that item with a new one.
            --%>
            <c:if test="${not empty commerceItemId}">
              <dsp:input type="hidden"
                         bean="GiftWithPurchaseFormHandler.currentSelectedItemId" 
                         value="${commerceItemId}"/>
            </c:if>           
            
            <dsp:input type="hidden"
                       bean="GiftWithPurchaseFormHandler.promotionId" 
                       value="${promotionId}"/>
                       
            <dsp:input type="hidden"
                       bean="GiftWithPurchaseFormHandler.giftHashCode" 
                       value="${giftHashCode}"/>
            
            <%-- We already added a SKU --%>          
            <dsp:input type="hidden"
                       bean="GiftWithPurchaseFormHandler.quantity" 
                       value="${1}"/>
                                  
            <%-- Redirection URLs --%>
            <dsp:input type="hidden"
                       bean="GiftWithPurchaseFormHandler.makeGiftSelectionSuccessURL" 
                       value="${contextroot}/cart/cart.jsp"/>

            <dsp:input type="hidden"
                       bean="GiftWithPurchaseFormHandler.sessionExpirationURL" 
                       value="${contextroot}/global/sessionExpired.jsp"/>
     
            <dsp:input type="hidden"
                       bean="GiftWithPurchaseFormHandler.makeGiftSelectionErrorURL"
                       value="${contextroot}/${selectionUrl}"/>
                       
            <%-- URLs for the AJAX response. --%>
            <dsp:input bean="GiftWithPurchaseFormHandler.ajaxMakeGiftSelectionSuccessURL" type="hidden"
                       value="${contextroot}/cart/json/giftSelectionStatus.jsp"/>
                       
            <dsp:input bean="GiftWithPurchaseFormHandler.ajaxMakeGiftSelectionErrorURL" type="hidden" 
                       value="${contextroot}/cart/json/giftErrors.jsp"/>
                       
            <dsp:input type="hidden" 
                       bean="GiftWithPurchaseFormHandler.ajaxMakeGiftSelectionTimeoutURL" 
                       value="${contextroot}/cart/json/giftSelectionTimeout.jsp"/> 
    
            <%-- These parameters will be selected via JavaScript --%>
            <dsp:input type="hidden" bean="GiftWithPurchaseFormHandler.productId" priority="10" 
                       value="${selectedProductId}"/>
            <dsp:input type="hidden" bean="GiftWithPurchaseFormHandler.skuId" priority="11" 
                       value="${selectedSkuId}"/>
            <dsp:input type="hidden" bean="GiftWithPurchaseFormHandler.skuType" priority="12"
                       value="${(empty selectedSkuType)?'clothing':selectedSkuType}"/>
            <dsp:input type="hidden" bean="GiftWithPurchaseFormHandler.requiredSkuAttributes.color" priority="13" />
            <dsp:input type="hidden" bean="GiftWithPurchaseFormHandler.requiredSkuAttributes.size" priority="14" />
    
          
            <%-- Apply selection --%>
            <span class="atg_store_basicButton add_to_cart_link">
              <fmt:message key="common.button.addToCartText" var="addToCartText"/>
              <fmt:message key="navigation_richCart.addingToCart" var="addingToCartText"/>      
              <dsp:input type="submit" priority="-1"
                         bean="GiftWithPurchaseFormHandler.makeGiftSelection" 
                         iclass="atg_behavior_addItemToCart" 
                         value="${addToCartCaption}" 
                         onclick="atg.store.picker.clickAddGWithPurchaseToCart(this, '${addingToCartText}'); return false;"/> 
            </span>

          </dsp:form>
        </c:when>
        
        <%-- No items available for selection, display 'Cancel' button only --%>
        <c:otherwise>
          <%-- Cancel button --%>
          <a href="#" onclick="dijit.byId('giftSelectorDialog').hide();" class="atg_store_basicButton secondary">
            <span>
              <fmt:message key="common.button.cancelText"/>
            </span>
          </a>
          
        </c:otherwise>
      </c:choose>  
    </div>
    
    
    </div>
    </div>
    
  <script type="text/javascript">
    atg.store.picker.hijackAddToCartNodes();
  </script>  

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/gadgets/giftSelection.jsp#2 $$Change: 788278 $--%>

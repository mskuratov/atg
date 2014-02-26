<%-- 
  This gadget renders available gifts for the given gift selection

  Required Parameters:
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
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/promotion/GiftWithPurchaseSelectionChoicesDroplet"/>
  <dsp:importbean bean="/atg/commerce/promotion/GiftWithPurchaseFormHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/commerce/collections/filter/droplet/InventoryFilterDroplet"/>
  <dsp:importbean bean="/atg/registry/CollectionFilters/GWPSelectionInventoryFilter"/>
  <dsp:importbean bean="/atg/registry/CollectionFilters/GWPSkuInventoryFilter"/>
  <dsp:importbean bean="/atg/store/droplet/CatalogItemFilterDroplet" />
  
  <dsp:getvalueof var="giftType" param="giftType"/>
  <dsp:getvalueof var="giftDetail" param="giftDetail"/>
  <dsp:getvalueof var="promotionId" param="promotionId"/>
  <dsp:getvalueof var="giftHashCode" param="giftHashCode"/>
  <dsp:getvalueof var="commerceItemId" param="commerceItemId"/>
  <dsp:getvalueof var="selectedSkuId" param="selectedSkuId"/>
  <dsp:getvalueof var="selectedProductId" param="selectedProductId"/>
    
  <crs:pageContainer divId="atg_store_gwpNoJS" bodyClass="atg_store_pageCart atg_store_gwpNoJS" 
                     titleKey="" index="false" follow="false">
  
    <jsp:attribute name="formErrorsRenderer">
      <%-- Display error messages from GWP form handler --%>
      <div id="atg_store_formValidationError">        
        <dsp:include page="/global/gadgets/errorMessage.jsp">
          <dsp:param name="formHandler" bean="GiftWithPurchaseFormHandler"/>
          <dsp:param name="divid" value="errorMessage"/>
        </dsp:include>
      </div>
    </jsp:attribute>
  
    <jsp:body>
      <div id="atg_store_contentHeader">
        <h2 class="title"><fmt:message  key="common.cart.shoppingCart"/></h2>
      </div>
  
      <div class="atg_store_main" id="atg_store_shoppingCart">
    
        <%-- Display error messages from GWP form handler --%>
        <div id="atg_store_formValidationError">        
          <dsp:include page="/global/gadgets/errorMessage.jsp">
            <dsp:param name="formHandler" bean="GiftWithPurchaseFormHandler"/>
            <dsp:param name="divid" value="errorMessage"/>
          </dsp:include>
        </div>
        
        <dsp:form action="${pageContext.request.requestURI}" method="post" name="gwpform" formid="gwpform">
  
          <%--
            Retrieve products associated with GWP
           --%>
          <dsp:droplet name="GiftWithPurchaseSelectionChoicesDroplet">
            <dsp:param name="giftType" value="${giftType}"/>
            <dsp:param name="giftDetail" value="${giftDetail}"/>
            <dsp:param name="alwaysReturnSkus" value="${true}"/>
          
            <dsp:oparam name="output">
                       
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
                <dsp:param name="collection" param="choices"/>
              
                <dsp:oparam name="output">
                   
                  <fmt:message var="tableSummary" key="GWP.GiftSelectionTableSummary"/>
                  <table cellspacing="0" cellpadding="0" border="0" id="atg_store_itemTable"
                    summary="${tableSummary}">
                    <thead>
                      <fmt:message var="rowValue" key="cart_noJsGiftSelection.title"/>
                      <tr>
                        <th colspan="3" scope="row" abbr="${rowValue}"><c:out value="${rowValue}"/></th>
                      </tr>
                    </thead>
                    <tbody>
                  
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
                          <dsp:getvalueof var="size" param="size"/>
                          <dsp:getvalueof var="count" param="count"/>
                                  
                          <tr class="<crs:listClass count="${count}" size="${size}" selected="false"/>">
                            <td class="image">
                              <img src="<dsp:valueof param='choice.product.smallImage.url'/>" alt="${productName}"/>
                            </td>
                          
                            <dsp:getvalueof var="itemName" param="choice.product.displayName"/>
                            
                            <c:if test="${empty itemName}">
                              <fmt:message var="itemName" key="common.noDisplayName" />
                            </c:if>
                          
                            <td class="item" scope="row" abbr="${itemName}">
                          
                              <label for="atg_store_giftProduct${count}">                   
                                <span class="itemName"><c:out value="${itemName}"/></span>
                              </label>  
                            
                              <div class="atg_store_pickerContainer">
                                 
                                <%-- A 'Color/Size' title. --%>
                                <fmt:message var="color_title" key="common.color"/>
                                <fmt:message var="separator_title" key="common.separator"/>
                                <fmt:message var="size_title" key="common.size"/>
                                                            
                                <dsp:getvalueof var="product" param="choice.product"/>
                              
                                <%-- Drop down list with SKUs --%>
                                <c:choose>
                                  <c:when test="${giftType == 'skuContentGroup'}">
                                  
                                    <%--
                                      In the case of SKU content group we are passing product ID through request parameter for
                                      each promotion ID - SKU ID combination. As with the existing UI design we can only pass
                                      promotion ID and SKU ID directly through the form inputs, then the product ID will be retrieved
                                      from the corresponding request parameter named as {promo_id}_{sku_id}.
                                    --%>
                                    <dsp:getvalueof var="sku" param="choice.skus[0]"/>
                                    <input name="${promotionId}_${sku.repositoryId}" type="hidden" value="${product.repositoryId}">
                                    
                                    <select title="${color_title}${separator_title}${size_title}">
                                    
                                      <dsp:getvalueof var="sku" param="choice.skus[0]"/>
                                      <dsp:getvalueof var="skuType" param="choice.skus[0].type"/>
                                  
                                      <dsp:setvalue param="sku" value="${sku}"/>
                                      
                                      <option value="${sku.repositoryId}" selected="true">
                                      
                                        <c:choose>
                                          <c:when test="${skuType == 'clothing-sku'}">
                                            <%-- clothing SKU is displayed in the following format: '[SKU ID]: [Color]/[Size] FREE' --%>
                                            <dsp:valueof param="sku.repositoryId"/><fmt:message key="common.labelSeparator"/>
                                            <dsp:valueof param="sku.color"/><fmt:message key="common.separator"/><dsp:valueof param="sku.size"/>
                                            <fmt:message key="common.FREE"/>
                                          </c:when>
                                          <c:when test="${skuType == 'furniture-sku'}">
                                            <%-- furniture SKU is displayed in the following format: '[SKU ID]: [Wood finish] FREE' --%>
                                            <dsp:valueof param="sku.repositoryId"/><fmt:message key="common.labelSeparator"/>
                                            <dsp:valueof param="sku.woodFinish"/>
                                            <fmt:message key="common.FREE"/>  
                                          </c:when>
                                          <c:otherwise>
                                            <dsp:valueof param="sku.repositoryId"/><fmt:message key="common.labelSeparator"/>
                                            <dsp:valueof param="sku.displayName"/>
                                            <fmt:message key="common.FREE"/>  
                                          </c:otherwise>
                                        </c:choose>
                                        
                                      </option>
                                          
                                    </select>
                                                 
                                  </div>
                                </td>
                                
                                <td class="atg_store_gwpSelect">
                                 
                                  <%--
                                    In the case of SKU content group we are selecting directly SKU repository item
                                    through this radio input.
                                  --%>
                                 
                                  <c:choose>
                                    <c:when test="${size > 1 }">
                                      <c:choose>
                                        <c:when test="${sku.repositoryId == selectedSkuId}">
                                          <dsp:input type="radio" 
                                                 bean="GiftWithPurchaseFormHandler.skuId" 
                                                 paramvalue="sku.repositoryId"
                                                 checked="true" id="atg_store_giftSku${count}"/>
                                        </c:when>
                                        <c:otherwise>
                                          <dsp:input type="radio" 
                                                 bean="GiftWithPurchaseFormHandler.skuId" 
                                                 paramvalue="sku.repositoryId" 
                                                 id="atg_store_giftSku${count}"/>
                                        </c:otherwise>
                                      </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                      <%-- Do not display radio buttons if only one SKU row is displayed --%>
                                      <dsp:input type="hidden" bean="GiftWithPurchaseFormHandler.skuId" paramvalue="sku.repositoryId"/>
                                    </c:otherwise>
                                  </c:choose>
                                
                                </td>

                                  </c:when>
                                  <c:otherwise>
                                  
                                    <%--
                                      Not SKU content group case. We are passing the selected SKU ID for the given promotion ID and
                                      product ID through  the request parameter named {promo_id}_{product_id}. As with the existing UI
                                      design we can only pass promotion ID and selected product ID directly through the form inputs,
                                      then the selected SKU ID will be retrieved from the corresponding request parameter named as 
                                      {promo_id}_{product_id}.
                                    --%>
                                    <select name="${promotionId}_${product.repositoryId}" title="${color_title}${separator_title}${size_title}">
                                    
                                    <dsp:getvalueof var="skuType" param="choice.skus[0].type"/>
                                    
                                    <%--
                                    This droplet filters out items with invalid dates.

                                    Input parameters:
                                      collection
                                        Collection of items to be filtered. 

                                    Output parameters:
                                      filteredCollection
                                        Resulting filtered collection.

                                    Open parameters:
                                      output
                                        Always rendered.
                                    --%>                            
                                   <dsp:droplet name="CatalogItemFilterDroplet">
                                     <dsp:param name="filter" bean="/atg/store/collections/filter/CatalogItemFilter"/>
                                     <dsp:param name="collection" param="choice.skus"/>
                                     <dsp:oparam name="output">
                                       <dsp:getvalueof var="collection" param="filteredCollection"/>
                                     </dsp:oparam>
                                     <dsp:oparam name="empty">
                                       <dsp:getvalueof var="collection" param="filteredCollection"/>                                       
                                     </dsp:oparam>                               
                                   </dsp:droplet>
                                   
                                    <%-- Filters out SKUs that are out of stock.
                                    
                                      Input parameters
                                        collection
                                          collection of SKUs
                                        filter
                                          filter that filters out out of stock SKUs.  
                                     --%>
                                    <dsp:droplet name="InventoryFilterDroplet">
                                      <dsp:param name="filter" bean="GWPSkuInventoryFilter"/>
                                      <dsp:param name="collection" value="${collection}"/>
                                      
                                      <dsp:oparam name="output">
                                                                          
                                        <dsp:getvalueof var="filteredCollection" param="filteredCollection"/>
  
                                        <c:forEach var="sku" items="${filteredCollection}">
                                          <dsp:setvalue param="sku" value="${sku}"/>
                                          <c:choose>
                                            <c:when test="${sku.repositoryId == selectedSkuId}">
                                              <option value="${sku.repositoryId}" selected="true">
                                            </c:when>
                                            <c:otherwise>
                                              <option value="${sku.repositoryId}">
                                            </c:otherwise>
                                          </c:choose>
                                          
                                          <c:choose>
                                            <c:when test="${skuType == 'clothing-sku'}">
                                              <%-- clothing SKU is displayed in the following format: '[SKU ID]: [Color]/[Size] FREE' --%>
                                              <dsp:valueof param="sku.repositoryId"/><fmt:message key="common.labelSeparator"/>
                                              <dsp:valueof param="sku.color"/><fmt:message key="common.separator"/><dsp:valueof param="sku.size"/>
                                              <fmt:message key="common.FREE"/>
                                            </c:when>
                                            <c:when test="${skuType == 'furniture-sku'}">
                                              <%-- furniture SKU is displayed in the following format: '[SKU ID]: [Wood finish] FREE' --%>
                                              <dsp:valueof param="sku.repositoryId"/><fmt:message key="common.labelSeparator"/>
                                              <dsp:valueof param="sku.woodFinish"/>
                                              <fmt:message key="common.FREE"/>  
                                            </c:when>
                                            <c:otherwise>
                                              <dsp:valueof param="sku.repositoryId"/><fmt:message key="common.labelSeparator"/>
                                              <dsp:valueof param="sku.displayName"/>
                                              <fmt:message key="common.FREE"/>  
                                            </c:otherwise>
                                          </c:choose>                                            
                                          </option>
                                        </c:forEach>
                                      </dsp:oparam>
                                    </dsp:droplet>
                                  </select>
                                               
                                </div>
                              </td>
                              
                              <td class="atg_store_gwpSelect">
                                <%--
                                  In the case of not SKU content group we are selecting product repository item
                                  through this radio input.
                                --%>
                                <c:choose>
                                  <c:when test="${size > 1}">
                                    <c:choose>
                                      <c:when test="${product.repositoryId == selectedProductId}">
                                        <dsp:input type="radio" 
                                                   bean="GiftWithPurchaseFormHandler.productId" 
                                                   paramvalue="choice.product.repositoryId"
                                                   checked="true" id="atg_store_giftProduct${count}"/>
                                      </c:when>
                                      <c:otherwise>
                                        <dsp:input type="radio" 
                                                   bean="GiftWithPurchaseFormHandler.productId" 
                                                   paramvalue="choice.product.repositoryId" 
                                                   id="atg_store_giftProduct${count}"/>
                                      </c:otherwise>
                                    </c:choose>
                                  </c:when>
                                  <c:otherwise>
                                    <%-- Do not display radio buttons if only one product row is displayed --%>
                                    <dsp:input type="hidden" bean="GiftWithPurchaseFormHandler.productId" paramvalue="choice.product.repositoryId"/>
                                  </c:otherwise>
                                </c:choose>

                              </td>
                                  </c:otherwise>
                                </c:choose>
     
                          </tr>
                        </dsp:oparam>
                      </dsp:droplet>
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
                 
                  <%-- flag that all gifts are out of stock --%>
                  <c:set var="itemsOutOfStock" value="true" />
                 
                </dsp:oparam> 
              </dsp:droplet>
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
        
          <%-- We already adds one SKU --%>          
          <dsp:input type="hidden" 
                     bean="GiftWithPurchaseFormHandler.quantity" 
                     value="${1}"/>
                              
          <%-- Redirection URLs --%>
          <dsp:getvalueof var="contextroot" bean="/OriginatingRequest.contextPath"/>
        
          <c:url var="selectionUrl" value="cart/gadgets/noJsGiftSelection.jsp">
            <c:param name="giftType" value="${giftType}"/>
            <c:param name="giftDetail" value="${giftDetail}"/>
            <c:param name="giftHashCode" value="${giftHashCode}"/>
            <c:param name="promotionId" value="${promotionId}"/>
            <c:param name="selectedSkuId" value="${selectedSkuId}"/>
            <c:param name="selectedProductId" value="${selectedProductId}"/>
            <c:param name="commerceItemId" value="${commerceItemId}"/>
          </c:url>
        
          <dsp:input type="hidden" 
                     bean="GiftWithPurchaseFormHandler.makeGiftSelectionSuccessURL" 
                     value="${contextroot}/cart/cart.jsp"/>
        
          <dsp:input type="hidden" 
                     bean="GiftWithPurchaseFormHandler.makeGiftSelectionErrorURL"
                     value="${contextroot}/${selectionUrl}"/>       
                     
          <dsp:input type="hidden" 
                     bean="GiftWithPurchaseFormHandler.sessionExpirationURL" 
                     value="${contextroot}/global/sessionExpired.jsp"/>
  
          <div id="atg_store_gwpFooter" class="atg_store_formActions">
            <%-- Apply selection --%>
            <c:if test="${(itemsOutOfStock ne true) and (itemsNotAvailable ne true)}">
          
              <fmt:message key="common.button.addToCartText" var="addToCartCaption"/>
                        
              <div class="atg_store_formActionItem">
                <span class="atg_store_basicButton add_to_cart_link"> 
                  <dsp:input type="submit" 
                             bean="GiftWithPurchaseFormHandler.makeGiftSelection" 
                             iclass="atg_behavior_addItemToCart" 
                             value="${addToCartCaption}" /> 
                </span>
              </div>
            </c:if>
            <%-- Back to the cart --%>
            <div class="atg_store_formActionItem">
              <dsp:a page="/cart/cart.jsp" iclass="atg_store_basicButton secondary">
                <span>
                  <fmt:message key="common.button.cancelText"/>
                </span>
              </dsp:a>
            </div>
          </div>
        </dsp:form>
      </div>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/gadgets/noJsGiftSelection.jsp#2 $$Change: 788278 $--%>
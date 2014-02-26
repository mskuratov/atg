<%--
  This page displays a non-JavaScript version of SKU picker
  
  Required parameters
    product
      current displayed product
    skus
      collection of product's child SKUs
    
  Optional parameters    
    categoryId
      current displayed category ID
  
--%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/store/droplet/WoodFinishDroplet"/>
  
  <%-- Get useful properties as EL variables, for future use in the URL construction processes --%>
  <dsp:getvalueof var="contextRoot" vartype="java.lang.String"  bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="uri" vartype="java.lang.String"  bean="/OriginatingRequest.requestURI"/>
  <dsp:getvalueof var="productId" param="product.repositoryId"/>
  <dsp:getvalueof var="categoryId" param="categoryId"/>
  <dsp:getvalueof var="status" param="status"/>

  <c:choose>
    <%-- When unavailable status display the please notify me dialog --%>
    <c:when test="${status == 'unavailable'}">
      <dsp:include page="/browse/noJavascriptNotifyMeRequest.jsp">
        <dsp:param name="redirectURL" value="${uri}?productId=${productId}&categoryId=${categoryId}"/>
        <dsp:param name="productId" param="productId"/>
        <dsp:param name="skuId" param="skuId"/>
      </dsp:include>
    </c:when>
    <%-- When emailSent display the we'll notify you dialog --%>
    <c:when test="${status == 'emailSent'}">
      <dsp:include page="/browse/noJavascriptNotifyMeConfirm.jsp"/>
    </c:when>
    <c:otherwise>
      <%-- All contents are displayed as a single form, cause there is a need to work simultaneously with several form handlers 
      and there is a need to send multiple parameters in a single user click --%>
      <div id="no_js_picker_contents">
        <%--
          This droplet calculates available wood finishes for a collection of SKUs specified.
          It also searches for a SKU specified by its wood finish property.

          Input parameters:
            product
              Specifies a product to be processed.
            skus
              Collection of child SKUs.

          Output parameters:
            comparisonsContainsProduct
              Flags, if current product is already added to the Comparisons List.
            showEmailAFriend
              Flags, if 'Email a Friend' button should be displayed.
            showGiftlists
              Flags, if 'Add to Giftlist' button should be displayed.
            wishlistContainsSku
              Flags, if user wishlist already contains the SKU specified.
            giftlists
              Collection of user's giftlists filtered by site.            

          Open parameters:
            output
              Always rendered.
        --%>
        <dsp:droplet name="WoodFinishDroplet">
          <dsp:param name="skus" param="filteredCollection"/>
          <dsp:param name="product" param="product"/>
          <dsp:oparam name="output">  
            <dsp:form method="post" action="${uri}" id="pickerForm" formid="pickerForm">
              <dsp:input type="hidden" bean="CartModifierFormHandler.skuType" value="furniture" />
              <div id="atg_store_picker">
                <div class="atg_store_selectAttributes">
                  <div class="atg_store_pickerContainer">
                    <div class="atg_store_productPrice">
                      <dsp:include page="/global/gadgets/priceRange.jsp">
                        <dsp:param name="product" param="product"/>
                      </dsp:include>
                    </div>
      
                    <label class="atg_store_pickerLabel" for="atg_store_noJSPicker">
                      <%-- A 'Wood Finish' label --%>
                      <fmt:message key="common.woodFinish"/>:
                    </label>
                    <%-- Start of Wood Finish selector pane --%>
                    <div class="atg_store_woodFinishPicker">
                      <dsp:select bean="/atg/store/profile/RequestBean.skuIdToAdd" priority="10" id="atg_store_noJSPicker">
                        <dsp:option beanvalue="Constants.null"> 
                          <fmt:message key="common.selectFinish"/>
                        </dsp:option>

                        <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                          <dsp:param name="array" param="skus"/>
                          
                          <dsp:oparam name="output">
                            <dsp:setvalue param="sku" paramvalue="element"/>
                            <dsp:getvalueof var="skuId" vartype="java.lang.String" param="sku.repositoryId"/>
                            <dsp:option value="${skuId}">
                              <%-- Each SKU is displayed in the following format: '[SKU ID]: [Wood Finish] $[Price]' --%>
                              <dsp:valueof param="sku.repositoryId"/><fmt:message key="common.labelSeparator"/>
                              <dsp:valueof param="sku.woodFinish"/>
                                
                              <dsp:droplet name="/atg/commerce/pricing/priceLists/PriceDroplet">
                                <dsp:param name="product" param="product"/>
                                <dsp:param name="sku" param="sku"/>
                                <dsp:oparam name="output">
                                  <dsp:getvalueof var="listPrice" param="price.listPrice"/>
                                      
                                  <dsp:droplet name="/atg/commerce/pricing/priceLists/PriceDroplet">
                                    <dsp:param name="priceList" bean="Profile.salePriceList"/>
                                    <dsp:oparam name="output">
                                      <%-- Found sale price, display it instead of list price --%>
                                      <dsp:getvalueof var="salePrice" param="price.listPrice"/>
                                      <dsp:include page="/global/gadgets/formattedPrice.jsp">
                                        <dsp:param name="price" value="${salePrice}"/>
                                      </dsp:include>
                                    </dsp:oparam>
                                    
                                    <dsp:oparam name="empty">
                                      <%-- Item is not on sale, display list price --%>
                                      <dsp:include page="/global/gadgets/formattedPrice.jsp">
                                        <dsp:param name="price" value="${listPrice}"/>
                                      </dsp:include>
                                    </dsp:oparam>
                                  </dsp:droplet>
                                </dsp:oparam>
                              </dsp:droplet>
                            </dsp:option>
                          </dsp:oparam>
                        </dsp:droplet>
                      </dsp:select>
                    </div><%-- end of atg_store_woodFinishPicker --%>
                    
                    <%-- Start of Add to Cart pane --%>
                    <div class="atg_store_addQty">
                      <div class="atg_store_quantity"> 
                      
                        <c:set var="qty_input_id" value="atg_store_enterQty_nojs"/>
                        <%-- If status parameter is not empty, generate input id using status.count --%>
                        <c:if test="${not empty status.count}">
                          <c:set var="qty_input_id" value="atg_store_enterQty_nojs_${status.count}"/>
                        </c:if>
                                             
                        <label class="atg_store_pickerLabel" for="${qty_input_id}">
                          <fmt:message key="common.qty"/><fmt:message key="common.labelSeparator"/>
                        </label>
                        <dsp:input bean="CartModifierFormHandler.addItemToOrderErrorURL" 
                                   value="${uri}?productId=${productId}&categoryId=${categoryId}" type="hidden"/>
                        <dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL" value="${contextRoot}/cart/cart.jsp" type="hidden"/>
                        <dsp:input bean="CartModifierFormHandler.productId" paramvalue="product.repositoryId" type="hidden"/>
                        <dsp:input bean="CartModifierFormHandler.quantity" id="${qty_input_id}" class="atg_store_quantityField_nojs" value="1" size="2" maxlength="5" />
                      
                        <dsp:input bean="CartModifierFormHandler.skuUnavailableURL" 
                                   value="${uri}?productId=${productId}&categoryId=${categoryId}&status=unavailable&skuId=${skuId}" type="hidden"/>
                      </div>
                      <div class="atg_store_productAvailability">
                        <span class="atg_store_basicButton add_to_cart_link">
                          <fmt:message key="common.button.addToCartText" var="addToCartCaption"/>
                          <dsp:input bean="CartModifierFormHandler.addItemToOrder" type="submit" iclass="atg_behavior_addItemToCart" value="${addToCartCaption}"/>
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div><%-- End of atg_store_picker --%>
  
              <%-- All picker buttons. --%>
              <dsp:include page="pickerActions.jsp">
                <dsp:param name="comparisonsContainsProduct" param="comparisonsContainsProduct"/>
                <dsp:param name="showEmailAFriend" param="showEmailAFriend"/>
                <dsp:param name="showGiftlists" param="showGiftlists"/>
                <dsp:param name="wishlistContainsSku" param="wishlistContainsSku"/>
                <dsp:param name="giftlists" param="giftlists"/>
              </dsp:include>
            </dsp:form>
          </dsp:oparam>
        </dsp:droplet>
      </div>
    </c:otherwise>
  </c:choose>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/noJsWoodFinishPickerLayout.jsp#2 $$Change: 788278 $--%>

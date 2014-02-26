<%--
  This gadget displays a non-JavaScript version of the SKU picker.
  It displays a dropdown box with all child SKUs and all necessary buttons.

  Required parameters:
    product
      Currently displayed product.
    skus
      Collection of product's child SKUs.

  Optional parameters:
    categoryId
      Category the product is viewed from.
    status
      Specifies, what should be displayed instead of picker. Allowed values are 'unavailable' and 'emailSent'.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/store/droplet/ColorSizeDroplet"/>

  <%-- Get useful properties as EL variables, for future use in the URL construction processes. --%>
  <dsp:getvalueof var="contextRoot" vartype="java.lang.String"  bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="uri" vartype="java.lang.String"  bean="/OriginatingRequest.requestURI"/>
  <dsp:getvalueof var="productId" param="product.repositoryId"/>
  <dsp:getvalueof var="categoryId" param="categoryId"/>
  <dsp:getvalueof var="status" param="status"/>

  <%--
    At first we just display picker with all child SKUs (we don't apply any filtering process to these SKUs).
    If the user selects unavailable SKU and tries to add it to cart, he will be redirected here with '?status=unavailable' parameter.
    If this is the case, we should display 'e-mail me when available' dialog instead of picker.
    When the user specifies the e-mail address, he will be redirected here with '?status=emailSent' parameter.
    If this is the case, we should display 'we will inform you' message.
  --%>
  <c:choose>
    <%-- When unavailable status, display the 'please notify me' dialog. --%>
    <c:when test="${status == 'unavailable'}">
      <dsp:include page="/browse/noJavascriptNotifyMeRequest.jsp">
        <dsp:param name="redirectURL" value="${uri}?productId=${productId}&categoryId=${categoryId}"/>
        <dsp:param name="productId" param="productId"/>
        <dsp:param name="skuId" param="skuId"/>
      </dsp:include>
    </c:when>
    <%-- When emailSent display, display the 'we will notify you' dialog. --%>
    <c:when test="${status == 'emailSent'}">
      <dsp:include page="/browse/noJavascriptNotifyMeConfirm.jsp"/>
    </c:when>
    <c:otherwise>
      <%--
        Display all child SKUs and all necessary buttons.
        All contents are displayed as a single form, cause there is a need to work simultaneously with several form handlers
        and there is a need to send multiple parameters in a single user click.
      --%>
      <div id="no_js_picker_contents">
        <%--
          This droplet calculates available colors and sizes for a collection of SKUs specified.
          It also searches for a SKU specified by its color and size properties.

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
        <dsp:droplet name="ColorSizeDroplet">
          <dsp:param name="skus" param="skus"/>
          <dsp:param name="product" param="product"/>
          <dsp:oparam name="output">      
            <dsp:form method="post" action="${uri}" id="pickerForm" formid="pickerForm">
              <%-- Set the sku type we're displaying a clothing-sku. Will be used later. --%>
              <dsp:input type="hidden" bean="CartModifierFormHandler.skuType" value="clothing" />
                
              <%-- Display price range for all SKUs. --%>
              <div class="atg_store_productPrice">
                <dsp:include page="/global/gadgets/priceRange.jsp">
                  <dsp:param name="product" param="product"/>
                </dsp:include>
              </div>
                
              <div class="atg_store_pickerContainer">
                <div class="atg_store_colorSizePicker">
                  <%-- A 'Color/Size' label. --%>
                  <label class="atg_store_pickerLabel" for="atg_store_noJSPicker">
                    <fmt:message key="common.color"/><fmt:message key="common.separator"/><fmt:message key="common.size"/>:
                  </label>
                     
                  <%-- The number of skus in the sku picker. --%>
                  <c:set var="skuCount" value="0"/>
                        
                  <%-- Start of Color/Size selector pane. --%>
                  
                  <%--
                    SKU ID to be added into shopping cart or into gift/wish list is saved into RequestBean,
                    then this ID is taken by form handlers during their initialization (there are links in the *.properties files
                    from form handlers to the RequestBean).
                    This solution enables us to save value into multiple form handlers properties from a single HTML element.
                  --%>
                  <dsp:select bean="/atg/store/profile/RequestBean.skuIdToAdd" priority="10" id="atg_store_noJSPicker">
                    <dsp:option beanvalue="Constants.null" > 
                      <fmt:message key="common.selectColorAndSize"/>
                    </dsp:option>
                    <%--
                      This droplet iterates over collection of items specified.
                               
                      Input parameters:
                        array
                          Collection of items to be iterated.
                             
                      Output parameters:
                        element
                          Current item.
                         
                      Open parameters:
                        output
                          Rendered for each iteration.
                    --%>
                    <dsp:getvalueof var="skus" param="skus"/>
                    <c:forEach var="sku" items="${skus}">
                       
                      <dsp:setvalue param="sku" value="${sku}"/>
                      <dsp:getvalueof var="skuId" vartype="java.lang.String" param="sku.repositoryId"/>
                      <%-- Render an option for the current SKU. --%>
                      <dsp:option value="${skuId}">
                        <%-- Count the number of SKUs. --%>
                        <c:set var="skuCount" value="${skuCount + 1}"/>
                        <%-- Each SKU is displayed in the following format: '[SKU ID]: [Color]/[Size] $[Price]' --%>
                        <dsp:valueof param="sku.repositoryId"/><fmt:message key="common.labelSeparator"/>
                        <dsp:valueof param="sku.color"/><fmt:message key="common.separator"/><dsp:valueof param="sku.size"/>
                        <%--
                          This droplet calculates a price for the given SKU and product.
                               
                          Input parameters:
                            product
                              Specifies a product whose price sould be calculated.
                            sku
                              Specifies a SKU whose price should be calculated.
                              
                          Output parameters:
                            price
                              A special bean with all price-related information.
                              
                          Open parameters:
                            output
                              Always rendered.
                        --%>
                        <dsp:droplet name="/atg/commerce/pricing/priceLists/PriceDroplet">
                          <dsp:param name="product" param="product"/>
                          <dsp:param name="sku" param="sku"/>
                          <dsp:oparam name="output">
                            <dsp:getvalueof var="listPrice" param="price.listPrice"/>
                            <%--
                              This droplet calculates a price for the given SKU and product.
                              
                              Input parameters:
                                product
                                  Specifies a product whose price sould be calculated.
                                sku
                                  Specifies a SKU whose price should be calculated.
                                priceList
                                  Specifies a price list the price should be taken from.
                              
                              Output parameters:
                                price
                                  A special bean with all price-related information.
                              
                              Open parameters:
                                output
                                  Rendered when the price is found in the price list specified.
                                empty
                                  Rendered when unable to find the price in the price list specified.
                            --%>
                            <dsp:droplet name="/atg/commerce/pricing/priceLists/PriceDroplet">
                              <dsp:param name="priceList" bean="Profile.salePriceList"/>
                              <dsp:oparam name="output">
                                <%-- Found sale price, display it instead of list price. --%>
                                <dsp:getvalueof var="salePrice" param="price.listPrice"/>
                                <dsp:include page="/global/gadgets/formattedPrice.jsp">
                                  <dsp:param name="price" value="${salePrice}"/>
                                </dsp:include>
                              </dsp:oparam>
                              <dsp:oparam name="empty">
                                <%-- Item is not on sale, display list price. --%>
                                <dsp:include page="/global/gadgets/formattedPrice.jsp">
                                  <dsp:param name="price" value="${listPrice}"/>
                                </dsp:include>
                              </dsp:oparam>
                            </dsp:droplet>
                          </dsp:oparam>
                        </dsp:droplet>
                      </dsp:option>
                    </c:forEach>  
                  </dsp:select>
                               
                  <%-- We have more than one SKU, display a size chart. --%>
                  <c:if test="${skuCount > 1}">
                    <span class="details">
                      <a href="gadgets/sizeChart.jsp" target="_blank" class="chart atg_store_help">
                        <fmt:message key="browse_picker.sizeChart"/>
                      </a>
                    </span>
                  </c:if>
                </div><%-- end of atg_store_colorSizePicker --%>
              </div><%-- End of atg_store_picker --%>
              <%-- Start of Add to Cart pane. --%>
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
                  <%-- Auxiliary properties for the cart form handler. --%>
                  <dsp:input bean="CartModifierFormHandler.addItemToOrderErrorURL"
                             value="${uri}?productId=${productId}&categoryId=${categoryId}" type="hidden"/>
                  <dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL" value="${contextRoot}/cart/cart.jsp" type="hidden"/>
                  <dsp:input bean="CartModifierFormHandler.productId" paramvalue="product.repositoryId" type="hidden"/>
                  <%-- How many SKUs should we add to cart? --%>
                  <dsp:input bean="CartModifierFormHandler.quantity" class="atg_store_quantityField_nojs" value="1"
                             id="${qty_input_id}" size="2" maxlength="5" />
                    
                  <%-- If selected SKU is unavailable, the user will be redirected here. --%>
                  <dsp:input bean="CartModifierFormHandler.skuUnavailableURL"
                             value="${uri}?productId=${productId}&categoryId=${categoryId}&status=unavailable&skuId=${skuId}" type="hidden"/>
                </div>
                              
                <%-- Render 'Add to Cart' button. --%>
                <div class="atg_store_productAvailability">
                  <span class="atg_store_basicButton add_to_cart_link">
                    <fmt:message key="common.button.addToCartText" var="addToCartCaption"/>
                    <dsp:input bean="CartModifierFormHandler.addItemToOrder" type="submit" iclass="atg_behavior_addItemToCart"
                               value="${addToCartCaption}"/>
                  </span>
                </div>
              </div>          
                                
             
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
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/noJsPickerLayout.jsp#2 $$Change: 788278 $--%>

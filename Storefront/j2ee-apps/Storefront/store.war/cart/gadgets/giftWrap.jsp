<%--
  This gadget renders an 'Include Gift Wrap?' part of the Shopping Cart page.
  It must be included into a <dsp:form> tag.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/store/droplet/ShowGiftWrap"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/registry/RepositoryTargeters/ProductCatalog/GiftWrapItem"/>
  <dsp:importbean bean="/atg/targeting/TargetingFirst"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
  <dsp:importbean bean="/atg/store/droplet/ItemValidatorDroplet" />
  <dsp:importbean bean="/atg/store/collections/filter/CatalogItemValidatorFilter" />

  <div class="atg_store_giftWrap">
    <h4 class="atg_store_wrapInfo">
      <fmt:message key="cart_giftWrapRenderer.giftWrapDesiredQuestion"/>
    </h4>
    <fieldset>
      <ul class="atg_store_wrapOptions">
        <%-- 
          Checks to see if the gift wrap option should be shown or not.
          If the order has any item that is not gift wrappable, don't show the option.

          If specific order types should not get the gift wrap option, the name of the order class 
          type can be added here as an condition with no content.

          Input Parameters:
            order
              The current shopping cart.

          Open Parameters:
            true
              If we should show gift wrap option.
            false
              If we shouldn't show gift wrap option.
        --%>
        <dsp:droplet name="ShowGiftWrap">
          <dsp:param name="order" bean="ShoppingCart.current"/>
          <dsp:oparam name="true">
            <%--
              TargetingFirst is used to perform a targeting operation with
              the help of its targeter. We pick the first item from the array
              returned by the targeting operation. We use this to retrieve ID
              of the global category refineElement.

              Input Parameters:
                fireViewItemEvent
                  Whether to fire off ViewItem event or not.
                targeter
                  Specifies the targeter service that will perform the targeting.                          
                elementName
                  The name of the targetedProduct

              Open Parameters:
                empty
                  This optional parameter is rendered if the targeting operation 
                  returns no matching items.
                output
                  At least 1 target was found.

              Output Parameters:
                element
                  The result of a target operation.
            --%>
            <dsp:droplet name="TargetingFirst">
              <dsp:param name="fireViewItemEvent" value="false"/>
              <dsp:param name="targeter" bean="GiftWrapItem"/>
              <dsp:param name="elementName" value="targetedProduct"/>
              <dsp:param name="filter" bean="CatalogItemValidatorFilter"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="giftWrapChecked" vartype="java.lang.Boolean" 
                                bean="ShoppingCart.current.containsGiftWrap"/>
               
                <%-- Retrieve SKU for gift wrap to validate it--%>
                <dsp:droplet name="SKULookup">
                  <dsp:param name="id" param="targetedProduct.childSkus[0].repositoryId"/>
                  <dsp:param name="elementName" value="sku"/>
                  <dsp:oparam name="output">
                  
                    <%-- Apply validators to SKU --%>
                    <dsp:droplet name="ItemValidatorDroplet">
                      <dsp:param name="item" param="sku"/>
                      <dsp:oparam name="true">
               
                        <dsp:getvalueof var="giftWrapChecked" vartype="java.lang.Boolean" 
                                        bean="ShoppingCart.current.containsGiftWrap"/>
                        <li>       
                          <dsp:input type="hidden" bean="CartModifierFormHandler.giftWrapSkuId" 
                                     paramvalue="targetedProduct.childSkus[0].repositoryId"/>
                          <dsp:input type="hidden" bean="CartModifierFormHandler.giftWrapProductId" 
                                     paramvalue="targetedProduct.repositoryId"/>
                          <dsp:input iclass="checkbox" bean="CartModifierFormHandler.giftWrapSelected" 
                                     type="checkbox" checked="${giftWrapChecked}" name="atg_store_addWrap" 
                                     onclick="atg.store.util.autoSelectGiftNote()" id="atg_store_addWrap"/>

                          <%-- Get the gift wrap price from the supplied SKU --%>
                          <c:set var="giftWrapPrice">
                            <dsp:include page="/global/gadgets/priceLookup.jsp">
                              <dsp:param name="product" param="targetedProduct"/>
                              <dsp:param name="sku" param="targetedProduct.childSKUs[0]"/>
                            </dsp:include>
                          </c:set> 
                  
                          <%--Display the price of the gift wrapping service in this page. --%>
                          <label for="atg_store_addWrap">
                            <fmt:message key="cart_giftWrapRenderer.addGiftWrap">
                              <fmt:param>
                               ${giftWrapPrice}
                              </fmt:param>
                            </fmt:message>
                          </label>

                          <%-- Popup the gift wrap details page --%>
                          <dsp:a href="${pageContext.request.contextPath}/cart/gadgets/giftWrapDetailsPopup.jsp" 
                                 target="popup">
                            <dsp:param name="giftWrapPrice" value="${giftWrapPrice}"/>
                            <fmt:message key="common.button.detailsText"/>
                          </dsp:a>
                        </li>
                      
                      </dsp:oparam>
                    </dsp:droplet> <%-- End ItemValidatorDroplet droplet --%>  
                  </dsp:oparam>
                </dsp:droplet> <%-- End SKULookup droplet --%>    
              </dsp:oparam>
            </dsp:droplet> <%-- End TargetingFirst droplet --%>
          </dsp:oparam>
        </dsp:droplet> <%-- End ShowGiftWrap droplet --%>
        <li>
          <dsp:getvalueof var="giftNotePopulated" vartype="java.lang.Boolean" 
                          bean="ShoppingCart.current.containsGiftMessage"/>
          <dsp:getvalueof var="giftNoteShouldBeAdded" vartype="java.lang.Boolean" 
                          bean="ShoppingCart.current.shouldAddGiftNote"/>
          <dsp:input iclass="checkbox" type="checkbox" name="atg_store_addNote" id="atg_store_addNote"
                     bean="CartModifierFormHandler.giftNoteSelected" checked="${giftNotePopulated || giftNoteShouldBeAdded}"/>
          <label for="atg_store_addNote">
            <fmt:message key="cart_giftWrapRenderer.addGiftNote"/>
          </label>
        </li>
      </ul>
    </fieldset>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/gadgets/giftWrap.jsp#2 $$Change: 788278 $--%>
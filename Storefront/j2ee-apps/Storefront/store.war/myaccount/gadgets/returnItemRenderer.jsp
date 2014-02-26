<%--
  This gadget renders a product-details row for a return item specified along with return quantity
  and return reason selection controls.
  It displays item's site, product image, SKU info, total refund for the item (for review return and return detail pages).
  For returnable items return quantity and reason controls are included.
  For non-returnable items the non-returnable state description is displayed.
  
  For return selection page the gadget should be included into the form element.

  Required parameters:
    returnItem
      Return item to be rendered.

  Optional parameters:
    returnable
      Indicates whether items is returnable or not. If not specified the page uses IsReturnable droplet
      to get this information.
    returnableDescription
      the description of returnable state.
    shippingGroupIndex
      The index of ReturnShippingGroup to which current return item belongs. This parameter is required
      in the non-review mode.
    itemIndex
      The index of return item in the ReturnShippingGroup's itemList. This parameter is required
      in the non-review mode.
    reviewMode
      Indicates whether return items should be displayed in the review mode. If so no select controls
      are displayed for the return quantity and reason but only already selected quantity and reason
      are displayed in the non-editable way.
    activeReturn
      Indicates whether return request is currently active.
    priceListLocale
      Specifies a locale in which to format the price (as string).
      If not specified, locale will be taken from profile price list (Profile.priceList.locale).
--%>

<dsp:page>

  <dsp:importbean bean="/atg/commerce/custsvc/returns/BaseReturnFormHandler"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/IsReturnable"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnReasonLookupDroplet"/>
  
  <%-- Retrieve commerce item, product and SKU for the specified return item. --%>
  <dsp:param name="commerceItem" param="returnItem.commerceItem"/>
  <dsp:param name="product"  param="commerceItem.auxiliaryData.productRef"/>
  <dsp:param name="sku"  param="commerceItem.auxiliaryData.catalogRef"/>
  
  <dsp:getvalueof var="returnItem" param="returnItem"/>
  <dsp:getvalueof var="reviewMode" param="reviewMode"/>
  <dsp:getvalueof var="returnable" param="returnable"/>
  <dsp:getvalueof var="returnableDescription" param="returnableDescription"/>
  <dsp:getvalueof var="activeReturn" param="activeReturn"/>
  
  <%-- Get IDs for missing product and SKU substitution product / SKU --%>
  <dsp:getvalueof var="missingProductId" vartype="java.lang.String"
                  bean="/atg/commerce/order/processor/SetProductRefs.substituteDeletedProductId"/>
  <dsp:getvalueof var="missingProductSkuId" vartype="java.lang.String" 
                  bean="/atg/commerce/order/processor/SetCatalogRefs.substituteDeletedSkuId"/>
    
  <%--
    Check whether the given return item is navigable so that to determine whether we can display
    navigable link for it.
  --%>
  <dsp:getvalueof param="product.NavigableProducts" var="navigable" vartype="java.lang.Boolean"/>
  
  <%-- Determine whether the item is returnable --%>
  <c:if test="${empty returnable}">
    <dsp:droplet name="IsReturnable">
      <dsp:param name="item" param="commerceItem"/>
      <dsp:oparam name="true">
        <c:set var="returnable" value="${true}"/>
      </dsp:oparam>
      <dsp:oparam name="false">
        <c:set var="returnable" value="${false}"/>
        <dsp:getvalueof param="returnableDescription" var="returnableDescription"/>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>
      
  <tr>

    <%-- Display site indicator. --%>
          
    <td class="site">
          
      <dsp:include page="/global/gadgets/siteIndicator.jsp">
        <dsp:param name="mode" value="icon"/>              
        <dsp:param name="siteId" param="commerceItem.auxiliaryData.siteId"/>
        <dsp:param name="product" param="product"/>
      </dsp:include>
          
    </td>
            
    <%-- Display product's image. Only navigable non-missing products will be displayed as link. --%>    
    
    <td class="image">
      <dsp:include page="/cart/gadgets/cartItemImage.jsp">
        <dsp:param name="commerceItem" param="commerceItem" />
        <dsp:param name="displayAsLink" value="${reviewMode && !activeReturn && navigable && missingProductId != param.product.repositoryId}"/>  
      </dsp:include>
         
    </td>
        
            
    <%-- Get product display name. If SKU is deleted, don't take in account SKU name --%>  
    <c:if test="${missingProductSkuId != param.sku.repositoryId}">  
      <dsp:getvalueof var="productDisplayName" param="sku.displayName"/>
    </c:if>        
    <c:if test="${empty productDisplayName}">
      <dsp:getvalueof var="productDisplayName" param="product.displayName"/>
      <c:if test="${empty productDisplayName}">
        <fmt:message var="productDisplayName" key="common.noDisplayName" />
      </c:if>
    </c:if>
    
    <%-- Display item-related info: display name, SKU attributes, SKU ID. --%>
        
    <td class="item" scope="row" abbr="${productDisplayName}">
    
      <span class="itemName">
      
        <%-- Display product name as link, if proper template is defined for the current product. --%>
        <dsp:getvalueof var="pageurl" idtype="java.lang.String" param="product.template.url"/>
        
        <c:choose>
          <c:when test="${reviewMode && !activeReturn && not empty pageurl && navigable && missingProductId != param.product.repositoryId}">
          
            <%-- Build site-aware link. --%>
            <dsp:include page="/global/gadgets/crossSiteLink.jsp">
              <dsp:param name="item" param="commerceItem"/>
            </dsp:include>
               
          </c:when>
          <c:otherwise>
          
            <%-- Either we have no proper URL, or product is not navigable, or deleted. Do not display a link. --%>
            <span class="atg_store_productTitle"><dsp:valueof value="${productDisplayName}"/></span>
            
          </c:otherwise>
        </c:choose>
      </span>
      
      <%--
        Render SKU-related properties (like color/size/finish). We do not display availability message
        here as it's not relevant in the returns context.
      --%>
      <dsp:include page="/global/util/displaySkuProperties.jsp">
        <dsp:param name="product" param="product"/>
        <dsp:param name="sku" param="sku"/>
        <dsp:param name="displayAvailabilityMessage" value="false"/>
      </dsp:include>
         
    </td>
    
    <%--
      Check in which mode the page is displayed: return review mode or return selection. For return selection mode
      return quantity and reason controls will be displayed. For return review mode selected returned quantity and
      reason will be displayed.
    --%>
    
    <c:choose>
      <c:when test="${reviewMode}">
      
        <%-- It's return review mode. --%>
        
        <td class="returnQuantity">
          <span class="quantity">
            <fmt:formatNumber value="${returnItem.quantityToReturn}" type="number"/>
          </span>
          
          <dsp:getvalueof var="availableQuantity" param="returnItem.quantityAvailable" />
          <c:if test="${activeReturn}">
            <span>
              <fmt:message key="myaccount_returnItemsSelect_ofQuantity">
                <fmt:param value="${availableQuantity}"/>
              </fmt:message>
            </span>
          </c:if>
    
        </td>
        
        <%-- Display item's total refund. --%>
        <td class="refundTotal">
          <p class="price">
            <dsp:include page="/global/gadgets/formattedPrice.jsp">
              <dsp:param name="price" param="returnItem.refundAmount"/>
              <dsp:param name="priceListLocale" param="priceListLocale" />
            </dsp:include>
          </p>
        </td>
            
        <td class="returnReason">
          
          <span class="returnReason">
          
            <dsp:droplet name="ReturnReasonLookupDroplet">
              <dsp:param name="id" param="returnItem.returnReason"/>
              <dsp:param name="elementName" value="returnReason"/>
              <dsp:oparam name="output">
                <dsp:valueof param="returnReason.readableDescription"/>
              </dsp:oparam>
            </dsp:droplet>
            
          </span>
              
        </td>
        
      </c:when>
      <c:otherwise>
      
        <%-- Return Items selection mode. --%>
        
        <%--
          If item is returnable display return quantity and reason controls otherwise display
          non-returnable state description
        --%>
        <c:choose>
          <c:when test="${returnable}">
              
            <td class="returnQuantity">
            
              <dsp:getvalueof var="availableQuantity" param="returnItem.quantityAvailable" />
              <fmt:message var="quantityToReturnLabel" key="myaccount_returnItemsSelect_returnQuantity"/>

              <dsp:input bean="BaseReturnFormHandler.returnRequest.shippingGroupList[param:shippingGroupIndex].itemList[param:itemIndex].quantityToReturn"
                         type="number"
                         min="0"
                         max="${availableQuantity}"
                         class="text qty"
                         title="${quantityToReturnLabel}" />
                         
                      
                                       
              <span>
                <fmt:message key="myaccount_returnItemsSelect_ofQuantity">
                  <fmt:param value="${availableQuantity}"/>
                </fmt:message>
              </span>
              
              
    
            </td>
            
            <td class="returnReason">

              <fmt:message var="returnReasonLabel" key="myaccount_returnItemsSelect_returnReason"/>
            
              <dsp:select bean="BaseReturnFormHandler.returnRequest.shippingGroupList[param:shippingGroupIndex].itemList[param:itemIndex].returnReason" title="${returnReasonLabel}">
                <dsp:option value=""><fmt:message key="myaccount_returnItemsSelect_selectReturnReason"/></dsp:option>
                <dsp:droplet name="ForEach">
                  <dsp:param bean="BaseReturnFormHandler.reasonCodes" name="array"/>
                  <dsp:param name="elementName" value="reasonCode"/>
                  <dsp:param name="sortProperties" value="+readableDescription"/>
                  <dsp:oparam name="output">
                    <dsp:option paramvalue="reasonCode.repositoryId">
                      <dsp:valueof param="reasonCode.readableDescription"/>
                    </dsp:option>
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:select>
              
            </td>
          
          </c:when>
          <c:otherwise>
          
            <%-- Non-returnable item. Just display non-returnable state description --%>
            <td colspan="2" class="nonReturnableReason">
              <span>${returnableDescription}</span>
            </td>
          </c:otherwise>
        </c:choose>
      
      </c:otherwise>
    </c:choose>
      
  </tr>
         
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/returnItemRenderer.jsp#1 $$Change: 788278 $--%>
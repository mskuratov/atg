<%-- 
  This gadget renders the row for the gift item, i.e. commerce item
  that has been added as a gift or re-priced as free.
  
  Required parameters:
    currentItem
      commerce item that have been added as a gift
    priceBean
      Price bean object containing gift item pricing details.
    count
      commerce item count       

  Optional parameters:
    hideSiteIndicator
      Flags, if site indicator should be hidden when rendering an item.
    displayProductAsLink
      Flags, if product details should be displayed as link.
    displaySiteIcon
      Flags, if site icon should be displayed when rendering a site indicator.
    displayAvailabilityMessage
      Defines whether to display inventory availability message, or not.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/promotion/PromotionLookup"/>

  <dsp:getvalueof var="missingProductId" vartype="java.lang.String" 
                  bean="/atg/commerce/order/processor/SetProductRefs.substituteDeletedProductId"/>
  <dsp:getvalueof var="currentItem" vartype="atg.commerce.order.CommerceItem" param="currentItem"/>
  <dsp:getvalueof param="currentItem.auxiliaryData.productRef.NavigableProducts" var="navigable" vartype="java.lang.Boolean"/>
  <dsp:getvalueof var="displayProductAsLink" vartype="java.lang.Boolean" param="displayProductAsLink"/>
  <dsp:getvalueof var="hideSiteIndicator" vartype="java.lang.String" param="hideSiteIndicator" />
  
  <tr>
    <%-- Display site indicator, if needed. --%>
    <c:if test="${empty hideSiteIndicator or (hideSiteIndicator == 'false')}">
      <td class="site">
        <dsp:getvalueof var="displaySiteIcon" vartype="java.lang.Boolean" param="displaySiteIcon"/>
        <c:if test="${displaySiteIcon or empty displaySiteIcon}">
          <dsp:include page="/global/gadgets/siteIndicator.jsp">
            <dsp:param name="mode" value="icon"/>              
            <dsp:param name="siteId" param="currentItem.auxiliaryData.siteId"/>
            <dsp:param name="product" param="currentItem.auxiliaryData.productRef"/>
          </dsp:include>
        </c:if>
      </td>
    </c:if>
  
    <%-- Display product icon. Only navigable non-missing products will be displayed as link. --%>
    <td class="image">
      <c:choose>
        <c:when test="${missingProductId != currentItem.auxiliaryData.productRef.repositoryId && navigable && displayProductAsLink}">
          <dsp:include page="/cart/gadgets/cartItemImage.jsp">
            <dsp:param name="commerceItem" param="currentItem" />
          </dsp:include>  
        </c:when>
        <c:otherwise>
          <dsp:include page="/cart/gadgets/cartItemImage.jsp">
            <dsp:param name="commerceItem" param="currentItem" />
            <dsp:param name="displayAsLink" value="false"/>  
          </dsp:include>
        </c:otherwise>
      </c:choose>
    </td>
  
    <%-- Display item-related info. --%>
    <dsp:getvalueof var="productDisplayName" param="currentItem.auxiliaryData.productRef.displayName"/>
    <c:if test="${empty productDisplayName}">
      <dsp:getvalueof var="productDisplayName" param="currentItem.auxiliaryData.productRef.displayName"/>
       <c:if test="${empty productDisplayName}">
         <fmt:message var="productDisplayName" key="common.noDisplayName" />
       </c:if>
    </c:if>
    
  
    <td class="item" scope="row" abbr="${rowTitle}">
      <span class="itemName">
        <%-- Display product name as link, if proper template is defined for the current product. --%>
        <dsp:getvalueof var="pageurl" idtype="java.lang.String" param="currentItem.auxiliaryData.productRef.template.url"/>
        <c:choose>
          <c:when test="${empty pageurl || !navigable}">
            <%-- Either we have no proper URL, or product is not navigable. Do not display link. --%>
            <dsp:valueof value="${productDisplayName}"/>
          </c:when>
          <c:otherwise>
            <c:choose>
              <c:when test="${(missingProductId != currentItem.auxiliaryData.productRef.repositoryId or empty missingProductId) 
                              and displayProductAsLink}">
                <%-- Build site-aware link. --%>
                <dsp:include page="/global/gadgets/crossSiteLink.jsp">
                  <dsp:param name="item" param="currentItem"/>
                </dsp:include>
              </c:when>
              <c:otherwise>
                <dsp:valueof value="${productDisplayName}"/>
              </c:otherwise>
            </c:choose>
          </c:otherwise>
        </c:choose>
      </span>
      <%-- Render SKU-related properties (like color/size/finish). --%>
      <dsp:include page="/global/util/displaySkuProperties.jsp">
        <dsp:param name="product" param="currentItem.auxiliaryData.productRef"/>
        <dsp:param name="sku" param="currentItem.auxiliaryData.catalogRef"/>
        <dsp:param name="displayAvailabilityMessage" param="displayAvailabilityMessage"/>
      </dsp:include>
    </td>
  
    <%-- Include item's quantity and detailed price (including discounts applied). --%>
    <td  class="atg_store_quantityPrice price" colspan="2">
      <span class="quantity">
        <%-- 
          Display '1' as item quantity
        --%>
        1
        <fmt:message key="common.atRateOf"/>
      </span>  
      
      <span class="price">
        <span><fmt:message key="common.FREE"/></span>
      </span>  
      
      <dsp:include page="/cart/gadgets/displayItemPricePromotions.jsp">
            <dsp:param name="currentItem" param="currentItem"/>
            <dsp:param name="unitPriceBean" param="priceBean"/>
          </dsp:include>
    </td>
  
    <%-- Display item's total. --%>
    <td class="total">
      <p class="price">
        <fmt:message key="common.equals"/>
        <fmt:message key="common.FREE"/>
      </p>
  
      <c:if test="${editItems}">
        <ul class="atg_store_actionItems">
          <dsp:include page="../../cart/gadgets/itemListingButtons.jsp">
            <dsp:param name="navigable" value="${navigable}"/>
          </dsp:include>
        </ul>
      </c:if>
    </td>
  </tr>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/orderGiftItem.jsp#3 $$Change: 788278 $--%>
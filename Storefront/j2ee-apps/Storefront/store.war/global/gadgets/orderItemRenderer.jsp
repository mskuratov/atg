<%--
  This gadget renders a product-details row for a commerce item specified.
  It displays item's site, product icon, SKU info, quantity, price and applied discounts.

  Required parameters:
    order
      Order to be proceed.
    currentItem
      Item to be rendered.

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

  <dsp:importbean bean="/atg/store/droplet/StorePriceBeansDroplet"/>
    
  <dsp:getvalueof var="hideSiteIndicator" vartype="java.lang.String" param="hideSiteIndicator"/>
  <dsp:getvalueof var="missingProductId" vartype="java.lang.String" bean="/atg/commerce/order/processor/SetProductRefs.substituteDeletedProductId"/>
  <dsp:getvalueof var="missingProductSkuId" vartype="java.lang.String" bean="/atg/commerce/order/processor/SetCatalogRefs.substituteDeletedSkuId"/>
  <dsp:getvalueof var="currentItem" vartype="atg.commerce.order.CommerceItem" param="currentItem"/>
  <dsp:getvalueof var="commerceItemRelationship" param="commerceItemRelationship"/>
  <dsp:getvalueof param="currentItem.auxiliaryData.productRef.NavigableProducts" var="navigable" vartype="java.lang.Boolean"/>
  <dsp:getvalueof var="displayProductAsLink" vartype="java.lang.Boolean" param="displayProductAsLink"/>

  <%-- Set default value of displayProductAsLink to false. --%>
  <c:if test="${empty displayProductAsLink}">
    <c:set var="displayProductAsLink" value="${false}"/>
  </c:if>
    
    <c:choose>
      <c:when test="${not empty commerceItemRelationship}">
      
    
    <%--
        Generate price beans for each product row on page. We will use them to display all applied
        discounts per cart item.
        
        Input Parameters:
          order
            The order to generate the price beans for.
          
        Open Parameters:
          output
            Always rendered.
          
        Output Parameters:
          priceBeansMap
            A map whose key is the commerce item id and whose value is a price bean.
      --%> 
      <dsp:droplet name="/atg/store/droplet/StorePriceBeansDroplet">
        
        <dsp:param name="order" param="order"/>
        <dsp:param name="relationship" param="commerceItemRelationship" />

      <%-- Gift with purchase item --%>  
      <dsp:oparam name="output">
        <%-- This commerce item have been added by GWP, display edit link --%>
        <dsp:getvalueof var="priceBeans" param="priceBeans" />
        <dsp:getvalueof var="priceBeansQuantity" param="priceBeansQuantity" />
        <dsp:getvalueof var="priceBeansAmount" param="priceBeansAmount" />
        <dsp:getvalueof var="gwpPriceBeansQuantity" param="gwpPriceBeansQuantity" />
      </dsp:oparam>
      </dsp:droplet>
      </c:when>
      <c:otherwise>
        <%--
        Generate price beans for each product row on page. We will use them to display all applied
        discounts per cart item.
        
        Input Parameters:
          order
            The order to generate the price beans for.
          
        Open Parameters:
          output
            Always rendered.
          
        Output Parameters:
          priceBeansMap
            A map whose key is the commerce item id and whose value is a price bean.
      --%> 
      <dsp:droplet name="/atg/store/droplet/StorePriceBeansDroplet">
        
        <dsp:param name="order" param="order"/>
        <dsp:param name="item" param="currentItem" />

      <%-- Gift with purchase item --%>  
      <dsp:oparam name="output">
        <%-- This commerce item have been added by GWP, display edit link --%>
        <dsp:getvalueof var="priceBeans" param="priceBeans" />
        <dsp:getvalueof var="priceBeansQuantity" param="priceBeansQuantity" />
        <dsp:getvalueof var="priceBeansAmount" param="priceBeansAmount" />
        <dsp:getvalueof var="gwpPriceBeansQuantity" param="gwpPriceBeansQuantity" />
      </dsp:oparam>
      </dsp:droplet>
      </c:otherwise>
      </c:choose>
      
          
          <%-- Check if the given commerce item contains regular items except gifts --%>
          <dsp:getvalueof var="nonGWPQuantity" value="${priceBeansQuantity - gwpPriceBeansQuantity }"/>
                     
          <c:if test="${nonGWPQuantity > 0}">
          
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
            <dsp:getvalueof var="productDisplayName" param="currentItem.auxiliaryData.catalogRef.displayName"/>
            <c:if test="${empty productDisplayName}">
              <dsp:getvalueof var="productDisplayName" param="currentItem.auxiliaryData.productRef.displayName"/>
              <c:if test="${empty productDisplayName}">
                <fmt:message var="productDisplayName" key="common.noDisplayName" />
              </c:if>
            </c:if>
        
            <td class="item" scope="row" abbr="${productDisplayName}">
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
              <dsp:include page="/checkout/gadgets/confirmDetailedItemPrice.jsp" >
                <dsp:param name="currentItem" param="currentItem"/>
                <dsp:param name="priceBeans" value="${priceBeans}"/>
              </dsp:include>
            </td>
        
            <%-- Display item's total. --%>
            <td class="total">
              <%--
                If there are price beans calculated, display their amount, not commerce item's (SG-CI relationship case).
                This gadget is being used for displaying commerce item's details in two cases.
                First is single shipping group case. In this case one can take total and quantity from commerce item itself,
                because commerce item is not being splitted into several rows.
                Second is multiple shipping groups case. In this case different rows can have different discounts applied to their items
                (in case of item-level discounts applied). Hence we have to display quantity, total and discounts from price beans generated for this
                row (or relationship).
              --%>
              <dsp:getvalueof var="amount" vartype="java.lang.Double" value="${priceBeansAmount}"/>
              <c:if test="${empty amount}">
                <dsp:getvalueof var="amount" vartype="java.lang.Double" param="currentItem.priceInfo.amount" />
              </c:if>
              <p class="price">
                <fmt:message key="common.equals"/>
                <dsp:include page="/global/gadgets/formattedPrice.jsp">
                  <dsp:param name="price" value="${amount }"/>
                </dsp:include>
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
          </c:if>
          
          <%-- 
            Display free gifts on separate rows with quantity 1. 
           --%>
          <c:if test="${gwpPriceBeansQuantity > 0}">
          <c:forEach var="priceBean" items="${priceBeans}">
            <c:if test="${priceBean.giftWithPurchase}">
              <c:forEach begin="1" end="${priceBean.quantity}">
                <dsp:include page="/global/gadgets/orderGiftItem.jsp">
                  <dsp:param name="priceBean" value="${priceBean}"/>
                  <dsp:param name="currentItem" param="currentItem"/>
                  <dsp:param name="count" value="${count}"/>
                <dsp:param name="hideSiteIndicator" param="hideSiteIndicator"/>
                <dsp:param name="displaySiteIcon" param="displaySiteIcon"/>
                <dsp:param name="displayProductAsLink" param="displayProductAsLink"/>
                <dsp:param name="displayAvailabilityMessage" param="displayAvailabilityMessage"/>
            </dsp:include>                   
                 
          </c:forEach>
          </c:if>
        </c:forEach>
        </c:if>
         
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/orderItemRenderer.jsp#3 $$Change: 788278 $--%>
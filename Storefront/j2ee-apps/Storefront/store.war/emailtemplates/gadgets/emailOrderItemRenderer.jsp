<%--
  This page renders the specified commerce item relationship details for order confirmation and 
  order shipped email templates. It is considered to be included inside of table with commerce items 
  relationships list for each individual commerce item row.

  Required parameters:
    order
      The order that the commerce item belongs to 
    commerceItemRel
      The commerce item relationship to display details for
    httpServer
      The URL prefix that is used to build fully-qualified absolute URLs

  Optional parameters:
    priceListLocale
      The locale to use for prices formatting
    locale
      Locale to append to the URL as URL parameter so that the pages to which user navigates using the URL
      will be rendered in the same language as the email.
--%>
<dsp:page>

  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/store/droplet/StorePriceBeansDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
    
  <dsp:getvalueof id="httpServer" param="httpServer"/>
  <dsp:getvalueof id="locale" param="locale"/>
  
  <%-- Get commerce item from commerce item relationship --%>
  <dsp:param name="commerceItem" param="commerceItemRel.commerceItem"/>
  
  <%-- 
    Generates price beans for the specified shipping group to commerce item 
    relationship.
              
    Input parameters:
      relationship
        shipping group to commerce item relationship for which price bean should
        be generated. 
       
    Output parameters:
      priceBeans
        price bean for the relationship specified.
      priceBeansQuantity
        Total items quantity for the relationship specified.
      priceBeansAmount
        The total amount for the relationship specified.
   --%>
  <dsp:droplet name="/atg/store/droplet/StorePriceBeansDroplet">
    <dsp:param name="relationship" param="commerceItemRel" />

    <%-- Gift with purchase item --%>  
    <dsp:oparam name="output">
      <%-- This commerce item have been added by GWP, display edit link --%>
      <dsp:getvalueof var="priceBeans" param="priceBeans" />
      <dsp:getvalueof var="priceBeansQuantity" param="priceBeansQuantity" />
      <dsp:getvalueof var="priceBeansAmount" param="priceBeansAmount" />
      <dsp:getvalueof var="gwpPriceBeansQuantity" param="gwpPriceBeansQuantity" />
    </dsp:oparam>
  </dsp:droplet>
      
  <%-- Check if the given commerce item contains regular items except gifts --%>
  <dsp:getvalueof var="nonGWPQuantity" value="${priceBeansQuantity - gwpPriceBeansQuantity }"/>
 
                   
  <%-- Split gift items and regular items --%>                   
  <c:if test="${nonGWPQuantity > 0}">
  
    <tr>
      <%-- Site icon --%>
      <td style="font-family:Tahoma,Arial,sans-serif;font-size:12px;width:60px;height:60px; border-bottom: 1px solid #666666;">
        
        <dsp:include page="/global/gadgets/siteIndicator.jsp">
          <dsp:param name="mode" value="icon"/>              
          <dsp:param name="siteId" param="commerceItem.auxiliaryData.siteId"/>
          <dsp:param name="product" param="commerceItem.auxiliaryData.productRef"/>
          <dsp:param name="absoluteResourcePath" value="true"/>
        </dsp:include>
       
      </td>

      <dsp:getvalueof var="productDisplayName" param="commerceItem.auxiliaryData.catalogRef.displayName"/> 
      <c:if test="${empty productDisplayName}">
        <dsp:getvalueof var="productDisplayName" param="commerceItem.auxiliaryData.productRef.displayName"/>
        <c:if test="${empty productDisplayName}">
          <fmt:message var="productDisplayName" key="common.noDisplayName" />
        </c:if>  
      </c:if> 

      <%-- Item's display name as link to the product page. --%>
      <td scope="row" abbr="${productDisplayName}" style="font-family:Tahoma,Arial,sans-serif;font-size:12px;width:170px;border-bottom: 1px solid #666666;" >
        <%-- Display name --%>              
        <span style="font-size:14px;color:#333;">
          <dsp:include page="/global/gadgets/productLinkGenerator.jsp">
            <dsp:param name="product" param="commerceItem.auxiliaryData.productRef"/>
            <dsp:param name="siteId" param="commerceItem.auxiliaryData.siteId" />
            <dsp:param name="forceFullSite" value="${true}"/>
          </dsp:include>

          <c:url var="productUrl" value="${httpServer}${productUrl}">
            <c:if test="${not empty locale}">
              <c:param name="locale">${locale}</c:param>
            </c:if>
          </c:url>
  
          <%-- Check whether item is navigable, if so display its name as link to product page. --%>
          <dsp:getvalueof var="navigable" vartype="java.lang.Boolean" param="commerceItem.auxiliaryData.productRef.NavigableProducts"/>
          <c:choose>
            <c:when test="${!navigable}">                   
              <c:out value="${productDisplayName}"/>
            </c:when>
            <c:otherwise>
              <dsp:a href="${productUrl}">
                <c:out value="${productDisplayName}"/>                        
              </dsp:a>
            </c:otherwise>
          </c:choose>
        </span>
        
        <%-- Check the SKU type to display type-specific properties --%>
        <dsp:getvalueof var="skuType" vartype="java.lang.String" param="commerceItem.auxiliaryData.catalogRef.type"/>
        <c:choose>
          <%-- 
            For 'clothing-sku' SKU type display the following properties:
              1. size
              2. color
          --%>
          <c:when test="${skuType == 'clothing-sku'}">
            <dsp:getvalueof var="catalogRefSize" param="commerceItem.auxiliaryData.catalogRef.size"/>
            <c:if test="${not empty catalogRefSize}">
              <p>
                <span style="font-size:12px;color:#666666;">
                  <fmt:message key="common.size"/><fmt:message key="common.labelSeparator"/>
                </span>
                <span style="font-size:12px;color:#000000;">${catalogRefSize}</span>
              </p>
            </c:if>
          
            <dsp:getvalueof var="catalogRefColor"    param="commerceItem.auxiliaryData.catalogRef.color"/>
            <c:if test="${not empty catalogRefColor}">
              <p>
                <span style="font-size:12px;color:#666666;">
                  <fmt:message key="common.color"/><fmt:message key="common.labelSeparator"/>
                </span>
                <span style="font-size:12px;color:#000000;">${catalogRefColor}</span>
              </p>
            </c:if>
          </c:when>
          <%-- 
            For 'furniture-sku' SKU type display the following properties:
              1. woodFinish
          --%>
          <c:when test="${skuType == 'furniture-sku'}">
            <dsp:getvalueof var="catalogRefWoodFinish" param="commerceItem.auxiliaryData.catalogRef.woodFinish"/>
            <c:if test="${not empty catalogRefWoodFinish}">
              <p>
                <span style="font-size:12px;color:#666666;">
                  <fmt:message key="common.woodFinish"/><fmt:message key="common.labelSeparator"/>
                </span>
                <span style="font-size:12px;color:#000000;">${catalogRefWoodFinish}</span>
              </p>
            </c:if>
          </c:when>
        </c:choose>
        
        <%-- SKU description --%>
        <dsp:getvalueof var="catalogRefDescription" 
                        param="commerceItem.auxiliaryData.catalogRef.description"/>
        <c:if test="${not empty catalogRefDescription}">
          <p>${catalogRefDescription}</p>
        </c:if>
       
      </td>
     
      <td colspan="2" style="font-family:Tahoma,Arial,sans-serif;font-size:12px;color#666666;width:205px;border-bottom: 1px solid #666666;">
                    
       
        <table style="border-collapse: collapse; width: 215px;" summary="" role="presentation">
          <c:forEach var="unitPriceBean" items="${priceBeans}">
                    
            <dsp:param name="unitPriceBean" value="${unitPriceBean}"/>
            
            <%-- 
              Check if the given price bean contains GWP model. If yes,
              just skip it - no need to display 0.0 price, it will be
              handled in another item line.
             --%>
             <c:if test="${!unitPriceBean.giftWithPurchase}">
              <tr>
                <%-- Quantity --%>
                <td style="width:65px;color:#666666;font-size:12px;font-family:Tahoma,Arial,sans-serif; text-align: center;">
                  <dsp:getvalueof var="quantity" vartype="java.lang.Double" param="unitPriceBean.quantity"/>
                  <fmt:formatNumber value="${quantity}" type="number"/>
                  <fmt:message key="common.atRateOf"/>
                </td>
                <%-- Price --%>
                <td style="color:#666666;font-size:12px;font-family:Tahoma,Arial,sans-serif; width: 150px; ">
                  <dsp:getvalueof var="unitPrice" vartype="java.lang.Double" param="unitPriceBean.unitPrice"/>
                  <%-- Format price using the specified price list locale --%>
                  <dsp:include page="/global/gadgets/formattedPrice.jsp">
                    <dsp:param name="price" value="${unitPrice }"/>
                    <dsp:param name="priceListLocale" param="priceListLocale"/>
                  </dsp:include>
        
                  <%-- Discount message --%>
                  <dsp:include page="/emailtemplates/gadgets/emailDisplayItemPricePromotions.jsp">
                    <dsp:param name="currentItem" param="commerceItem"/>
                    <dsp:param name="unitPriceBean" param="unitPriceBean"/>
                  </dsp:include>
                 
                </td>
              </tr>
            </c:if>
          </c:forEach>
        </table>
      </td>

      <%-- Total commerce item's amount --%>
      <td align="right" style="font-family:Tahoma,Arial,sans-serif;font-size:12px;color:#000000; white-space:nowrap;border-bottom: 1px solid #666666;">
        <fmt:message key="common.equals"/>
        <span style="color:#000000">
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${priceBeansAmount}"/>
            <dsp:param name="priceListLocale" param="priceListLocale"/>
          </dsp:include>     
        </span>             
      </td>
       
    </tr>          
  </c:if>

  <%-- 
    Display selections on separate rows with quantity 1. 
  --%>
  <c:if test="${gwpPriceBeansQuantity > 0}">
    <c:forEach var="priceBean" items="${priceBeans}">
      <c:if test="${priceBean.giftWithPurchase}">
        <c:forEach begin="1" end="${priceBean.quantity}">
          <dsp:include page="/emailtemplates/gadgets/emailOrderGiftItem.jsp">
            <dsp:param name="priceBean" value="${priceBean}"/>
            <dsp:param name="commerceItem" param="commerceItem"/>
            <dsp:param name="httpServer" param="httpServer"/>
            <dsp:param name="locale" param="locale"/>
          </dsp:include>                        
        </c:forEach>
      </c:if>
    </c:forEach>
  </c:if>
  
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/emailOrderItemRenderer.jsp#4 $$Change: 793384 $--%>
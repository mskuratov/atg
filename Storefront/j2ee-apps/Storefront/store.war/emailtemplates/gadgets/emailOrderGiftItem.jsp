<%-- 
  This gadget renders the row for the gift item, i.e. commerce item
  that has been added as a gift or re-priced as free.
  
  Required parameters:
    commerceItem
      commerce item that have been added as a gift
    priceBean
      The price bean object with pricing details of free gift item.
    httpServer
      The URL prefix that is used to build fully-qualified absolute URLs       

  Optional parameters:
    locale
      Locale to append to the URL as URL parameter so that the pages to which user navigates using the URL
      will be rendered in the same language as the email.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/promotion/PromotionLookup"/>

  <dsp:getvalueof var="missingProductId" vartype="java.lang.String" 
                  bean="/atg/commerce/order/processor/SetProductRefs.substituteDeletedProductId"/>
  <dsp:getvalueof var="currentItem" vartype="atg.commerce.order.CommerceItem" param="currentItem"/>
  <dsp:getvalueof param="currentItem.auxiliaryData.productRef.NavigableProducts" var="navigable" vartype="java.lang.Boolean"/>
  
  
  <dsp:getvalueof id="httpServer" param="httpServer"/>
  <dsp:getvalueof id="locale" param="locale"/>
  
  <tr>
  
    <%-- Site icon --%>
    <td style="font-family:Tahoma,Arial,sans-serif;font-size:12px;width:60px;height:60px;  border-bottom: 1px solid #666666;">
      
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
    <td style="font-family:Tahoma,Arial,sans-serif;font-size:12px;width:170px; border-bottom: 1px solid #666666;" scope="row" abbr="${productDisplayName}">
      <%-- Display name --%>
      <span style="font-size:14px;color:#333;">
        <dsp:include page="/global/gadgets/productLinkGenerator.jsp">
          <dsp:param name="product" param="commerceItem.auxiliaryData.productRef"/>
          <dsp:param name="siteId" param="commerceItem.auxiliaryData.siteId"/>
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



    <%-- This commerce item have been added by GWP, display edit link --%>
    <td colspan="2" style="font-family:Tahoma,Arial,sans-serif;font-size:12px;color#666666;width:205px; border-bottom: 1px solid #666666;">
      <%--
        The commerce item was discounted, loop through all price details and
        display each as separate line.
       --%>
      <table style="border-collapse: collapse; width: 215px;" summary="" role="presentation">
        <tr>
          <%-- Quantity --%>
          <td style="width:65px;color:#666666;font-size:12px;font-family:Tahoma,Arial,sans-serif;text-align: center;">
            <%-- 
              Display '1' as item quantity
            --%>
            1
            <fmt:message key="common.atRateOf"/>
          </td>
          <%-- Price --%>
          <td style="color:#666666;font-size:12px;font-family:Tahoma,Arial,sans-serif; width: 150px; ">
            
            <fmt:message key="common.FREE"/>
            
            <dsp:include page="/emailtemplates/gadgets/emailDisplayItemPricePromotions.jsp">
              <dsp:param name="currentItem" param="commerceItem"/>
              <dsp:param name="unitPriceBean" param="priceBean"/>
            </dsp:include>
          </td>
        </tr>
      </table>
    </td>

    <%-- Total commerce item's amount --%>
    <td align="right" style="font-family:Tahoma,Arial,sans-serif;font-size:12px;color:#000000; white-space:nowrap; border-bottom: 1px solid #666666;">
      <fmt:message key="common.equals"/>
      <span style="color:#000000">
        <fmt:message key="common.FREE"/>
      </span>             
    </td>
  </tr>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/emailOrderGiftItem.jsp#3 $$Change: 788278 $--%>
<%--
  This gadget renders a return item detail row for email templates.
  It displays item's site, product image, SKU info, total refund price, returned quantity and
  reason.

  Required parameters:
    returnItem
      Return item to be rendered.
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
  
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnReasonLookupDroplet"/>
  
  <dsp:getvalueof var="returnItem" param="returnItem"/>
  <dsp:getvalueof id="httpServer" param="httpServer"/>
  <dsp:getvalueof id="locale" param="locale"/>
  
  <%-- Retrieve commerce item, product and SKU for the specified return item. --%>
  <dsp:param name="commerceItem" param="returnItem.commerceItem"/>
  <dsp:param name="product"  param="commerceItem.auxiliaryData.productRef"/>
  <dsp:param name="sku"  param="commerceItem.auxiliaryData.catalogRef"/>
  
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
  
  <tr>

    <%-- Display site indicator. --%>
    
    <td style="font-family:Tahoma,Arial,sans-serif;font-size:12px;width:60px;height:60px; border-bottom: 1px solid #666666;">
          
      <dsp:include page="/global/gadgets/siteIndicator.jsp">
        <dsp:param name="mode" value="icon"/>              
        <dsp:param name="siteId" param="commerceItem.auxiliaryData.siteId"/>
        <dsp:param name="product" param="product"/>
        <dsp:param name="absoluteResourcePath" value="true"/>
      </dsp:include>
         
    </td>
    
    <%-- Display item-related info: display name, SKU attributes, SKU ID. --%>
    
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
    
    <%-- Display product name as link, if proper template is defined for the current product. --%>
    <dsp:getvalueof var="pageurl" idtype="java.lang.String" param="product.template.url"/>

    <%-- Item's display name as link to the product page. --%>
    <td scope="row" abbr="${productDisplayName}" style="font-family:Tahoma,Arial,sans-serif;font-size:12px;width:170px; border-bottom: 1px solid #666666;">
    
      <%-- Display name --%>
      <span style="font-size:14px;color:#333;">        
        <c:out value="${productDisplayName}"/>
      </span>
        
      <%-- Check the SKU type to display type-specific properties --%>
      <dsp:getvalueof var="skuType" vartype="java.lang.String" param="sku.type"/>
      
      <c:choose>
        <%-- 
          For 'clothing-sku' SKU type display the following properties:
            1. size
            2. color
        --%>
        <c:when test="${skuType == 'clothing-sku'}">
        
          <dsp:getvalueof var="catalogRefSize" param="sku.size"/>
          <c:if test="${not empty catalogRefSize}">
            <p>
              <span style="font-size:12px;color:#666666;">
                <fmt:message key="common.size"/><fmt:message key="common.labelSeparator"/>
              </span>
              <span style="font-size:12px;color:#000000;">${catalogRefSize}</span>
            </p>
          </c:if>
          
          <dsp:getvalueof var="catalogRefColor"    param="sku.color"/>
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
          <dsp:getvalueof var="catalogRefWoodFinish" param="sku.woodFinish"/>
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
      <dsp:getvalueof var="catalogRefDescription" param="sku.description"/>
      <c:if test="${not empty catalogRefDescription}">
        <p>${catalogRefDescription}</p>
      </c:if>
       
    </td>
      
    <td style="font-family:Tahoma,Arial,sans-serif;font-size:12px;width:60px;height:60px; border-bottom: 1px solid #666666;">
      <span style="color:#000000">
        <fmt:formatNumber value="${returnItem.quantityToReturn}" type="number"/>
      </span>
  
    </td>
    
    <%-- Display item's total refund. --%>
    <td style="font-family:Tahoma,Arial,sans-serif;font-size:12px;width:60px;height:60px; border-bottom: 1px solid #666666;">
      <span style="color:#000000">
        <dsp:include page="/global/gadgets/formattedPrice.jsp">
          <dsp:param name="price" param="returnItem.refundAmount"/>
          <dsp:param name="priceListLocale" param="priceListLocale"/>
        </dsp:include>
      </span>
    </td>
          
    <td align="right" style="font-family:Tahoma,Arial,sans-serif;font-size:12px;color:#000000; white-space:nowrap; border-bottom: 1px solid #666666;">
        
      <span style="color:#000000">
        
        <dsp:droplet name="ReturnReasonLookupDroplet">
          <dsp:param name="id" param="returnItem.returnReason"/>
          <dsp:param name="elementName" value="returnReason"/>
          <dsp:oparam name="output">
            <dsp:valueof param="returnReason.readableDescription"/>
          </dsp:oparam>
        </dsp:droplet>
          
      </span>
              
    </td>
        
  </tr>
         
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/emailReturnItemRenderer.jsp#1 $$Change: 788278 $--%>
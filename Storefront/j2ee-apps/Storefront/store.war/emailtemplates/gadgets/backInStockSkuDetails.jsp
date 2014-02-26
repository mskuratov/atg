<%--
  This page displays SKU details, like color, size, price, image, etc.
  
  Required parameters:
    sku
      The SKU item that is back in stock.
    product
      Product repository item whose SKU's details are shown
      
  Optional parameters:
    None.
--%>
<dsp:page>
    
  <%-- Begin Sku List --%>
  <table border="0" cellpadding="0" cellspacing="0" 
         style="color:#666;font-family:Verdana,arial,sans-serif;font-size:14px"
         summary="" role="presentation">
    
    <dsp:getvalueof var="productName" vartype="java.lang.String" param="product.displayName"/>
    
    <%-- Display SKU details --%>
    <tr>
      
      <%-- Display SKU's ID --%>
      <td width="230" style="color:#666;font-family:Verdana,arial,sans-serif;font-size:14px">
        <fmt:message key='common.item'/><fmt:message key='common.numberSymbol'/><fmt:message key='common.labelSeparator'/>
        <dsp:valueof param="sku.repositoryId">
          <fmt:message key="common.IdDefault"/>
        </dsp:valueof><br />
        
        <%-- Display SKU's price --%>
        <fmt:message key='common.price'/><fmt:message key='common.labelSeparator'/>
        <dsp:include page="/emailtemplates/gadgets/priceLookup.jsp">
          <dsp:param name="product" param="product"/>
          <dsp:param name="sku" param="product.childSKUs[0]"/>
        </dsp:include><br />

        <%-- Display SKU's color and size if its type is 'clothing-sku'. --%>
        <dsp:getvalueof var="skuType" vartype="java.lang.String" param="sku.type"/>
        <c:choose>
          <c:when test="${skuType == 'clothing-sku'}">
            <dsp:getvalueof param="sku.size" var="skuSize"/>
            <c:if test="${not empty skuSize}">
              <fmt:message key='common.size'/><fmt:message key='common.labelSeparator'/>
              <dsp:valueof param="sku.size"/><br />
            </c:if>
          
            <dsp:getvalueof param="sku.color" var="skuColor"/>
            <c:if test="${not empty skuColor}"> 
              <fmt:message key='common.color'/><fmt:message key='common.labelSeparator'/>
              <dsp:valueof param="sku.color"/><br />
            </c:if>
          </c:when>
          <c:when test="${skuType == 'furniture-sku'}">
            <dsp:getvalueof param="sku.woodFinish" var="skuWoodFinish"/>
            <c:if test="${not empty skuWoodFinish}"> 
              <fmt:message key='common.woodFinish'/><fmt:message key='common.labelSeparator'/>
              <dsp:valueof param="sku.woodFinish"/><br />
            </c:if>
          </c:when>
        </c:choose>
      </td> 
      
    </tr>
      
  </table>
 <%-- End of SKU details --%>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/backInStockSkuDetails.jsp#1 $$Change: 735822 $--%>

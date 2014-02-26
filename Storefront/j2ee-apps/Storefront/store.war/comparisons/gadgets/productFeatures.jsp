<%-- 
  This page generates one row in a product comparison table, with
  each cell containing a list of property values, each value from one
  child item (sku or feature).

  Required Parameters:
    None
      
  Optional Parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/comparison/ProductList"/>
  <dsp:importbean bean="/atg/store/droplet/ComparisonRowExistsDroplet"/>  

  <dsp:getvalueof var="productListItems" vartype="java.lang.Object" bean="ProductList.items"/>
  
  <%-- Make sure we have items in the comparisons list --%>
  <c:if test="${not empty productListItems}">
    <%--
      ComparisonRowExistsDroplet is used to determine whether or not there is
      at least one object in the items input parameter containing non null value
      for the specified property. For example, if an item has a wood finish
      property then we will render wood finish for all items in the comparisons
      list. If there is no item with a wood finish property then the wood finish
      row is not rendered.
          
      Input Parameters:
        items - A collection of items
            
        propertyName - A property to test for
             
        sourceType - Where the propertyName property exists, either "sku" which
                     indicates the property exists on the childSkus or "product"
            
      Open Parameters:
        output - Rendered if 1 or more objects have a non null value for a 
                 certain property         
    --%>
    <dsp:droplet name="ComparisonRowExistsDroplet">
      <dsp:param name="items" value="${productListItems}"/>
      <dsp:param name="propertyName" value="features"/>
      <dsp:param name="sourceType" value="product"/>
      <dsp:oparam name="output">
        <tr>
          <%-- For each product in the comparisons list render its features --%>
          <c:forEach var="productListItem" items="${productListItems}" varStatus="status">
            <dsp:param name="productListItem" value="${productListItem}"/>        
            
            <td class="atg_store_compareFeatures atg_store_compareItemDetails" >
              <%-- Display the property name --%>
              <dl>
              <dt>
                <fmt:message key="common.features"/><fmt:message key="common.labelSeparator" />
              </dt>
              
              <dd>
                <%-- Get this items features --%>               
                <dsp:getvalueof var="productFeatures" param="productListItem.product.features"/>
                <dsp:getvalueof var="size" value="${fn:length(productFeatures)}"/>
                    
                <%-- For each feature render its display name --%>
                <c:forEach var="feature" items="${productFeatures}" varStatus="featureStatus">
                  <dsp:param name="feature" value="${feature}"/>
                  <dsp:getvalueof var="count" value="${featureStatus.count}"/>
                  
                  <%-- Separate features with a comma --%>    
                  <c:choose>
                    <c:when test="${count < size}">
                      <dsp:valueof param="feature.displayName"></dsp:valueof>,
                    </c:when>
                    
                    <c:otherwise>
                      <dsp:valueof param="feature.displayName"></dsp:valueof>
                    </c:otherwise>
                  </c:choose>           
                </c:forEach>
              </dd>
              </dl>
            </td>
          </c:forEach>
        </tr>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/comparisons/gadgets/productFeatures.jsp#1 $$Change: 735822 $--%>

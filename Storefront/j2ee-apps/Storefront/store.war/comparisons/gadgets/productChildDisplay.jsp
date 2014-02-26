<%-- 
  This page renders the Colors, Size and Wood Finish information that appears on the
  product comparisons page. This information is taken from the childSkus of each
  product.
    
  Required Parameters:
    properties
      A map of properties and the keys in the resource bundle which hold their
      actual display names.
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/droplet/PropertyValueCollection"/>
  <dsp:importbean bean="/atg/store/droplet/ComparisonRowExistsDroplet"/>
  <dsp:importbean bean="/atg/commerce/catalog/comparison/ProductList"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

  <dsp:getvalueof var="properties" vartype="java.util.Map" param="properties"/>
  <dsp:getvalueof var="items" vartype="java.lang.Object" bean="ProductList.items"/>

  <%-- Make sure we have items in the comparisons list --%>
  <c:if test="${not empty items}">
    
    <%-- Get properties from map sorted by keys --%>
    <dsp:droplet name="ForEach" array="${properties}" var="property" 
                 sortProperties="_key">   
      <dsp:oparam name="output">           
        <tr>
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
            <dsp:param name="items" value="${items}"/>
            <dsp:param name="propertyName" value="${property.key}"/>
            <dsp:param name="sourceType" value="sku"/>
            <dsp:oparam name="output">
              <c:forEach var="item" items="${items}" varStatus="status">
                <dsp:param name="entry" value="${item}"/>
                <td class="atg_store_compareItemDetails">
                  <dl>
                  <%-- Display the property name --%>
                  <dt>                  
                    <fmt:message key="${property.element}"/><fmt:message key="common.labelSeparator"/>
                  </dt>
                
                  <dd>
                    <%--
                      CatalogTools.propertyToFilterMap contains property names and filters that should
                      be used on the property values. The filters can be passed to the 
                      PropertyValueCollection droplet and used for tasks such as ordering property
                      values. 
                    --%>
                    <dsp:getvalueof var="filterName" 
                                    bean="/atg/commerce/catalog/CatalogTools.propertyToFilterMap.${property.key}"/>
                    <dsp:getvalueof var="filter" bean="${filterName}"/>
                  
                    <%--
                      PropertyValueCollection is used to create a collection of property
                      values for a collection of repository items. An optional CollectionFilter
                      can be applied to the values to filter and/or sort them. For example
                      if a collection of skus had the property color, then the output would 
                      be a collection of the colors from each sku.
                    
                      Input Parameters:
                        items - The collection of items that will be used to create the 
                                collection of property values
                      
                        propertyName - The property to use
                      
                        filter - An optional CollectionFilter
                      
                      Open Parameters:
                        output - Rendered if there are no errors
                      
                      Output Parameters:
                        values - A collection of property values
                    --%>                  
                    <dsp:droplet name="PropertyValueCollection">
                      <dsp:param name="items" param="entry.product.childSKUs"/>
                      <dsp:param name="propertyName" value="${property.key}"/>
                      <dsp:param name="filter" value="${filter}"/>
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="values" vartype="java.lang.Object" param="values"/>                    
                        <c:forEach var="value" items="${values}" varStatus="valueStatus">
                          <c:out value="${value}"/>
                          <c:if test="${valueStatus.count < fn:length(values)}">/</c:if>
                        </c:forEach>
                      </dsp:oparam>
                    </dsp:droplet>
                  
                  </dd>
                  </dl>
                </td>
              </c:forEach>
            </dsp:oparam>
          </dsp:droplet>
        </tr>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/comparisons/gadgets/productChildDisplay.jsp#1 $$Change: 735822 $--%>

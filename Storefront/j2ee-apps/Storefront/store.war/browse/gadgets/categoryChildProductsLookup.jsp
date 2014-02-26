<%-- 
  This gadget looks up child categories to display for the page. Accounts for pagination and sorting settings.
    
  Required Parameters:
    contentItem
      The category child products content item.
    productsRenderer
      The path to JSP gadget that should be used for rendering category child products. 
   
  Optional Parameters:   
    shareableTypeId
      The Id of site group to use when look up for category products.
    sort
      The property name to sort products by.
    p
      The page number.    
    viewAll
      The boolean indicating whether all products should be displayed on the page ignoring the products per page
      setting.
--%>
<dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/SharingSitesDroplet"/>
  <dsp:importbean bean="/atg/store/droplet/CatalogItemFilterDroplet"/>
  
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  
  <dsp:getvalueof var="contentItem" param="contentItem"/>

  <dsp:getvalueof var="shareableTypeId" param="shareableTypeId"/>
  <dsp:getvalueof var="filterByCatalog" param="filterByCatalog"/>
  <dsp:getvalueof var="productsRenderer" param="productsRenderer"/>

  <c:if test="${not empty shareableTypeId}">
  
    <%--
      Get a list of sites associated with the supplied shareable type.
      
      Input Parameters:
        shareableTypeId
          The shareable type that the list of sites returned will belong to.
  
      Open Parameters:
        output
          Serviced when no errors occur.
    
      Output Parameters:
        sites
          List of sites associated with the shareableTypeid.
    --%>
    <dsp:droplet name="SharingSitesDroplet">
      <dsp:param name="shareableTypeId" value="${shareableTypeId}"/>
      
      <dsp:oparam name="output">
        <dsp:getvalueof var="sites" param="sites"/>
      </dsp:oparam>
      
    </dsp:droplet>
  
  </c:if>
    
  <c:set var="arraySplitSize" value="${contentItem.recsPerPage}"/>
  
  <dsp:getvalueof var="p" param="p"/>
  <c:if test="${empty p}">
    <c:set var="p" value="1" />
  </c:if>
  
  <dsp:getvalueof var="viewAll" param="viewAll"/>
  <c:if test="${empty viewAll}">
    <c:set var="viewAll" value="false" />
  </c:if>
  
  <c:if test="${not empty contentItem.categoryId}">
   
    <%--
      Get the category repository object according to the id

      Input Parameters:
        id - The ID of the category we want to look up

      Open Parameters:
        output - Serviced when no errors occur
        error - Serviced when an error was encountered when looking up the category

      Output Parameters:
        element - The category whose ID matches the 'id' input parameter  
    --%>
    <dsp:droplet name="CategoryLookup">
      <dsp:param name="id" value="${contentItem.categoryId}"/>
      <dsp:param name="sites" value="${sites}"/>
      <dsp:param name="filterByCatalog" value="${not empty filterByCatalog ? filterByCatalog : 'true'}"/>
      <dsp:param name="filterBySite" value="true"/>

      <dsp:oparam name="output">     
        <dsp:getvalueof var="productList" param="element.childProducts"/>
        <%--
            This droplet filters out products with invalid dates.

            Input parameters:
              collection
                Collection of items to be filtered. 

            Output parameters:
              filteredCollection
                Resulting filtered collection.

            Open parameters:
              output
                Always rendered.
          --%>
          <dsp:droplet name="CatalogItemFilterDroplet">
            <dsp:param name="collection" value="${productList}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="productList" param="filteredCollection"/>
            </dsp:oparam>
            <dsp:oparam name="empty">
              <dsp:getvalueof var="productList" param="filteredCollection"/>
            </dsp:oparam>
          </dsp:droplet>
        
        <%-- Setup Range droplet parameters --%>
        <c:choose>
          <c:when test="${viewAll eq 'true'}">
            <c:set var="howMany" value="5000" />
            <c:set var="start" value="1" />
          </c:when>
          <c:when test="${(fn:length(productList)) <= arraySplitSize}">
            <c:set var="howMany" value="${arraySplitSize}" />
            <c:set var="start" value="${1}" />
          </c:when>
          <c:otherwise>
            <c:set var="howMany" value="${arraySplitSize}" />
            <c:set var="start" value="${((p - 1) * howMany) + 1}" />
          </c:otherwise>
        </c:choose>
        
        <dsp:include page="${productsRenderer}">
          <dsp:param name="contentItem" value="${contentItem}"/>
          <dsp:param name="products" value="${productList}"/>
          <dsp:param name="sortSelection" param="sort"/>
          <dsp:param name="howMany" value="${howMany}"/>
          <dsp:param name="start" value="${start}"/>
          <dsp:param name="p" value="${p}"/>
          <dsp:param name="viewAll" value="${viewAll}"/>
        </dsp:include>
        
      </dsp:oparam>
    </dsp:droplet>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/categoryChildProductsLookup.jsp#2 $$Change: 788278 $ --%>

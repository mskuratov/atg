<%--
  Track the shopper's category navigation to provide the appropriate breadcrumbs.
  
  Required Parameters:
    None         

  Optional Parameters:
    None     
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean bean="/atg/store/catalog/CatalogNavigation"/>
  <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistoryCollector"/>

  <dsp:getvalueof var="categoryNavIds" bean="CatalogNavigation.categoryNavigationPath" />

  <c:choose>
    
    <%-- 
      The catgoryNavigationPath from the CatalogNavigation component is the 
      full category path from and including the current category to the top 
      level category.
    --%>
    <c:when test="${!empty categoryNavIds}">
      
      <%-- 
        For each category, from the current to the top level category add this 
        category repository item to the navigation history collector.
      --%>
      <c:forEach var="categoryNavId" items="${categoryNavIds}" varStatus="status">

        <c:set var="navAction" value="push"/>
        <c:if test="${status.first}">
          <c:set var="navAction" value="jump"/>
        </c:if>

        <%--
          Get the category according to the ID
         
            Input Parameters:
              id - The ID of the category we want to look up
          
            Open Parameters:
              output - Serviced when no errors occur
          
            Output Parameters:
              element - The category whose ID matches the 'id' input parameter  
        --%>
        <dsp:droplet name="CategoryLookup">
          <dsp:param name="id" value="${categoryNavId}"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="currentCategory" param="element" vartype="java.lang.Object" scope="request"/>
            
            <%--
              Add category to the stack of visited locations.  
           
              Input Parameters:
                navAction - navigation action, either 'push' or 'jump'
                item - this is the item to add to the breadcrumb stack
            
              Open Parameters:
                None.
            
              Output Parameters:
                None.
             --%>
            <dsp:droplet name="CatalogNavHistoryCollector">
              <dsp:param name="item" value="${currentCategory}" />
              <dsp:param name="navAction" value="${navAction}" />
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
      </c:forEach>
    </c:when>
    <c:otherwise>
      
      <%--
        If categoryNavIds is empty, use the shopper's current category as the 
        default category item.   
     
        Input Parameters:
          navAction - 'jump' means that we reset history and add the current 
                       category as the first entry
          item - this is the item to add to the breadcrumb stack
      
        Open Parameters:
          None.
      
        Output Parameters:
          None.
       --%>
      <dsp:getvalueof var="currentCategory" bean="CatalogNavigation.currentCategory" />
      <c:if test="${not empty currentCategory}">
        
        <%--
          Add category to the stack of visited locations.  
           
          Input Parameters:
            navAction - navigation action, either 'push' or 'jump'
            item - this is the item to add to the breadcrumb stack
            
          Open Parameters:
            None.
            
          Output Parameters:
            None.
        --%>
        <dsp:droplet name="CatalogNavHistoryCollector">
          <dsp:param name="item" value="${currentCategory}"/>
          <dsp:param name="navAction" value="jump" />
        </dsp:droplet>
      </c:if>
    </c:otherwise>
  </c:choose>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/breadcrumbsNavigation.jsp#3 $$Change: 788278 $--%>

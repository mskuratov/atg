<%--
  This tag generates a URL for a 'Continue Shopping' link and stores this in the 'continueShoppingURL' page scope variable.
  The resulting 'Continue Shopping' link will be to the top level category associated with the shopper's last browsed category,
  or to '/index.jsp' if this doesn't exist or is not valid for the users current context.

  The JSP including this tag can then use 'continueShoppingURL' to display the 'Continue Shopping' link.

  Required attributes:
    None.
 
  Optional attributes:
    None.

  Example:
    <crs:continueShopping>
      <c:set var="continueShoppingLink" value="/index.jsp"/>

      <c:if test="${not empty continueShoppingURL}">
        <c:set var="continueShoppingLink" value="${continueShoppingURL}"/>
      </c:if>

      <dsp:a href="${continueShoppingLink}">
        <fmt:message key="common.button.continueShoppingText"/>
      </dsp:a>
    </crs:continueShopping>
--%>

<%@include file="/includes/taglibs.jspf"%>
<%@include file="/includes/context.jspf"%>

<%-- 'continueShoppingURL' page scope variable to store the URL --%>
<%@ variable name-given="continueShoppingURL" variable-class="java.lang.String" %>

<dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
<dsp:importbean bean="/atg/commerce/endeca/cache/DimensionValueCacheDroplet"/>
<dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory"/>


<%-- Obtain the current request context root path --%>
<dsp:getvalueof var="contextroot" vartype="java.lang.String" bean="/OriginatingRequest.contextPath"/>

<%-- Obtain the top level category from the shopper browse navigation history --%>
<dsp:getvalueof var="topLevelCategory" bean="CatalogNavHistory.topLevelCategory"/>


<%--
  Generate the link to the category page or set the continue shopping link to '/index.jsp'.
--%>
<c:choose>
  <c:when test="${not empty topLevelCategory}">
    <%--
      Check if the category belongs to the user's current Profile catalog and if the
      item belongs to the current site. 
      
      Input parameters:
        id
          The repository id for the catalog.

      Output parameters:
        output
          If the category belongs to the user's current Profile catalog.
        wrongCatalog 
          If the category does not belong to the user's current catalog.
        noCatalog 
          If the current user does not have a catalog.
        wrongSite  
          If the category is not found on the current site.
        error
          If an error occurred during lookup.
        empty
          If the category is not found.
    --%>
    <dsp:droplet name="CategoryLookup">
      <dsp:param name="id" value="${topLevelCategory.repositoryId}"/>

      <dsp:oparam name="output">
        <%--
          Look in a cache of ATG repository Id to Endeca navigationStates for the URL
          that will be used to navigate to when an Endeca driven category page is clicked.
            
          Input Parameters:
            repositoryId - The repository id of the value we want to retrieve from the cache.
        
          Open Parameters:
            output - Serviced when no errors occur.
       
          Output Parameters:
            dimensionValueCacheEntry - The entry in the cache for this particular
                                       repository id and ancestors. 
        --%>
        <dsp:droplet name="DimensionValueCacheDroplet">
          <dsp:param name="repositoryId" value="${topLevelCategory.repositoryId}"/>

          <dsp:oparam name="output">
            <dsp:getvalueof var="categoryCacheEntry" param="dimensionValueCacheEntry" />
          </dsp:oparam>
        </dsp:droplet>

        <c:choose>
          <c:when test="${not empty categoryCacheEntry.url and 
                          currentCategory.repositoryId != topLevelCategory.repositoryId}">
            <dsp:getvalueof var="categoryId" vartype="java.lang.String" param="element.repositoryId"/>
            <c:url var="continueShoppingURL" context="${contextroot}" value="${categoryCacheEntry.url}"/>
          </c:when>
          <c:otherwise>
             <c:set var="continueShoppingURL" value="${contextroot}/index.jsp"/>
          </c:otherwise>
        </c:choose><%-- End is empty check on category --%>
      </dsp:oparam>

      <dsp:oparam name="wrongCatalog">
        <%-- Category not found in this catalog lookup. --%>
        <c:url var="continueShoppingURL" context="${contextroot}" value="/index.jsp"/>
      </dsp:oparam>

      <dsp:oparam name="noCatalog">
        <%-- No catalog found in this catalog lookup. --%>
        <c:url var="continueShoppingURL" context="${contextroot}" value="/index.jsp"/>
      </dsp:oparam>

      <dsp:oparam name="wrongSite">
        <%-- Category not found in current site catalog. --%>
        <c:url var="continueShoppingURL" context="${contextroot}" value="/index.jsp"/>
      </dsp:oparam>

      <dsp:oparam name="error">
        <%-- An error has occurred during lookup. --%>
        <c:url var="continueShoppingURL" context="${contextroot}" value="/index.jsp"/>
      </dsp:oparam>

      <dsp:oparam name="empty">
        <%-- Category not found with lookup. --%>
        <c:url var="continueShoppingURL" context="${contextroot}" value="/index.jsp"/>
      </dsp:oparam>
    </dsp:droplet>
  </c:when>
  <c:otherwise>
    <%-- No category available in navigation history. --%>
    <c:url var="continueShoppingURL" context="${contextroot}" value="/index.jsp"/>
  </c:otherwise>
</c:choose>

<jsp:doBody/>

<%-- @version $Id$$Change$--%>

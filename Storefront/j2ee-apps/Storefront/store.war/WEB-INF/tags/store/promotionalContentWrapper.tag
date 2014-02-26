<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:dsp="http://www.atg.com/taglibs/daf/dspjspTaglib1_1"
    xmlns:c="http://java.sun.com/jsp/jstl/core" version="2.0">
  <jsp:directive.tag language="java" body-content="scriptless"/>

  <!-- 
    This tag renders a link to proper place specified by the promotional content.
    The inner contents of this link should be provided by tag's body.

    If associatedCategory has an URL, render link to this category;
    If associatedProduct has an URL, render link to this product;
    If promotionalContent has an URL on it, render link to this place;
    If promotionalContent has an associated site, render link to this site's home page;
    Otherwise just render the body.    

    Page scope variables set:
      var 
        The variable specified by this name will contain a name of linked object (category, product or promotion).

    Required attributes:
      promotionalContent 
        The promotion to be linked.
      
    Optional attributes:
      None.
  -->

  <!-- 
    Output variable, the variable specified by this attribute will contain a title of linked element (category, product or promotion).
  -->
  <jsp:directive.attribute name="var" rtexprvalue="false" required="true"/>
  <jsp:directive.variable name-from-attribute="var" alias="title" scope="NESTED"/>

  <dsp:page>
    <div class="atg_store_categoryPromotion">
      <dsp:getvalueof var="categoryUrl" vartype="java.lang.String" param="promotionalContent.associatedCategory.template.url"/>
      <dsp:getvalueof var="prodUrl" vartype="java.lang.String" param="promotionalContent.associatedProduct.template.url"/>
      <dsp:getvalueof var="pageUrl" vartype="java.lang.String" param="promotionalContent.linkUrl"/>
      <dsp:getvalueof var="siteId" vartype="java.lang.String" param="promotionalContent.associatedSite"/>
      <dsp:getvalueof var="omitTooltip" vartype="java.lang.Boolean" param="omitTooltip"/>

      <!-- 
        It's OK to have several 'c:when' tags in a row, only the first one with 'true' test result will be rendered,
        all others will be ignored by server.
      -->
      <c:choose>
        <c:when test="${not empty categoryUrl}">
          <!-- Do we have a link to category? If yes, make a link to it and display body content. -->
          <dsp:getvalueof var="categoryDisplayName" vartype="java.lang.String" param="promotionalContent.associatedCategory.displayName"/>
          <dsp:getvalueof var="promotionalContentName" vartype="java.lang.String" param="promotionalContent.displayName"/>

          <!-- Escape category's name, cause it can contain restricted characters. -->
          <c:set var="title">
            <c:out value="${promotionalContentName}" escapeXml="true"/>
          </c:set>

          <c:if test="${!omitTooltip}">
            <c:set var="tooltip" value="${title}"/>  
          </c:if>
          
          <!--
            Look in a cache of ATG repository Id to Endeca navigationStates for the 
            URL that will be used to navigate to when an Endeca driven category page
            is clicked.
                  
            Input Parameters:
              repositoryId - The repository id of the value we want to retrieve from
                             the cache.
              
            Open Parameters:
              output - Serviced when no errors occur.
             
            Output Parameters:
              dimensionValueCacheEntry - The entry in the cache for this particular
                                         repository id and ancestors. 
          -->
          <dsp:droplet name="/atg/commerce/endeca/cache/DimensionValueCacheDroplet">
            <dsp:param name="repositoryId" param="promotionalContent.associatedCategory.repositoryId"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="dimensionValueCacheEntry" param="dimensionValueCacheEntry" />
            </dsp:oparam>
          </dsp:droplet>

          <!-- There should only be one category in the dimensionValueCacheList, so use its URL. -->
          <dsp:a page="${dimensionValueCacheEntry.url}" title="${tooltip}">
            <jsp:doBody/>
          </dsp:a>
        
        </c:when>
        <c:when test="${not empty prodUrl}">
          <!-- Do we have a link to product? If yes, make a link to it and display body content. -->
          <dsp:getvalueof var="productDisplayName" vartype="java.lang.String" param="promotionalContent.associatedProduct.displayName"/>

          <!-- Escape product's name, cause it can contain restricted characters. -->
          <c:set var="title">
            <c:out value="${productDisplayName}" escapeXml="true"/>
          </c:set>

          <dsp:a page="${prodUrl}" title="${title}">
            <dsp:param name="productId" param="promotionalContent.associatedProduct.repositoryId"/>
            <jsp:doBody/>
          </dsp:a>
        </c:when>
        <c:when test="${not empty pageUrl}">
          <!-- Do the promotion itself points somewhere? If yes, link to this place and render body. -->
          <dsp:getvalueof var="promotionalContentDisplayName" vartype="java.lang.String" param="promotionalContent.displayName"/>

          <!-- Escape promotion's name, cause it can contain restricted characters. -->
          <c:set var="title">
            <c:out value="${promotionalContentDisplayName}" escapeXml="true"/>
          </c:set>

          <dsp:a page="${pageUrl}" title="${title}">
            <jsp:doBody/>
          </dsp:a>
        </c:when>
        <c:when test="${not empty siteId}">
          <!-- Do we have a link to some site? If yes, make a link to its home page. -->
          <dsp:include page="/global/gadgets/crossSiteLinkGenerator.jsp">
            <dsp:param name="siteId" value="${siteId}"/>
            <dsp:param name="customUrl" value="/index.jsp"/>
          </dsp:include>

          <dsp:getvalueof var="promotionalContentDisplayName" vartype="java.lang.String" param="promotionalContent.displayName"/>

          <!-- Escape promotion's name, cause it can contain restricted characters. -->
          <c:set var="title">
            <c:out value="${promotionalContentDisplayName}" escapeXml="true"/>
          </c:set>

          <dsp:a href="${siteLinkUrl}" title="${title}">
            <jsp:doBody/>
          </dsp:a>
        </c:when>
        <c:otherwise>
          <!-- Nothing helped, just render the body. -->
          <jsp:doBody/>
        </c:otherwise>
      </c:choose>
    </div>
  </dsp:page>
</jsp:root>
<!-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/WEB-INF/tags/store/promotionalContentWrapper.tag#2 $$Change: 742374 $ -->

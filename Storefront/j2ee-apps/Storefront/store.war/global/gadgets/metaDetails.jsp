<%--
  Global gadget used for rendering SEO meta tags
  
  Required parameters:
    None.

  Optional parameters:
    catalogItem
      Product or category item used to combine product/category attributes like keywords
      or description with static SEO content from repository.
    contentKey
      A key used to search SEOTags repository item, if not provided
      the servlet path is used instead.
 --%>
<dsp:page>
  
  <dsp:getvalueof var="catalogItem"  param="catalogItem"/>
  <dsp:getvalueof var="title"  param="title"/>
  
  <%-- Get request's servlet path to use as a key for SEOTags item if the
       specific key is not provided --%>
  <c:set var="pageUrl" value="${pageContext.request.servletPath}" />
  <c:set var="key" value="${(not empty contentKey) ? contentKey : pageUrl}"/>
  
  <%-- Get current site ID --%>
  <dsp:getvalueof var="site" bean="/atg/multisite/Site.id"/>
  
  <%--
    Build RQL query to find appropriate SETTags item. The item we are looking for
    should have the specified key and belong to the specified site.
   --%>
  <c:set var="queryRQL" value="${(not empty site) ? 'key = :key AND sites INCLUDES :site' : 'key = :key'}"/>

  <%--
    This droplet executes a RQL query and renders its 'output'
    parameter for a selected subset of the elements returned by the query.
    Along with other droplet input parameters we are passing key and site
    parameters that are declared in the RQL query.
    
    Input parameters:
      repository
        The repository upon which to execute RQL query.
      itemDescriptor
        The item descriptor naming the item type to query.
      howMany
        How many items to display. In out case is set to 1, as only one SEOTags
        item should be processed for the current page.
      queryRQL
        RQL query to execute.
   --%>
  <dsp:droplet name="/atg/dynamo/droplet/RQLQueryRange">
    <dsp:param name="repository" value="/atg/seo/SEORepository" />
    <dsp:param name="itemDescriptor" value="SEOTags" />
    <dsp:param name="howMany" value="1" />
    <dsp:param name="key" value="${key}" />
    <dsp:param name="site" bean="/atg/multisite/Site.id"/>
    <dsp:param name="queryRQL" value="${queryRQL}" />
    <dsp:oparam name="output">
      <%-- Retrieve title, description and keywords from found SEOTags item. --%>
      <dsp:getvalueof var="title" param="element.title"/>
      <dsp:getvalueof var="description" param="element.description"/>
      <dsp:getvalueof var="keywords" param="element.keywords"/>
    </dsp:oparam> 
  </dsp:droplet>

  <%--
    Add product/category specific information to title and meta keywords/description if
    catalogItem is passed in.
   --%>
      
  <c:if test="${not empty catalogItem}">
    <dsp:getvalueof var="itemName" param="catalogItem.displayName"/>
    <dsp:getvalueof var="itemDescription" param="catalogItem.longDescription"/>
    <dsp:getvalueof var="itemKeywords" param="catalogItem.keywords"/>
    <c:if test="${not empty itemName}">
      <c:set var="title" value="${itemName} ${title}"/> 
    </c:if>
    <c:if test="${not empty itemDescription}">
      <c:set var="description" value="${itemDescription} ${description}"/>
    </c:if>
    <c:if test="${not empty itemKeywords}">
      <c:set var="keywords" value="${fn:substring(itemKeywords,fn:indexOf(itemKeywords,'[')+1,fn:indexOf(itemKeywords,']'))},${keywords}"/>
    </c:if>
  </c:if> 
  
  <%--
    Display page's title. If there are no title that is build from SEOTags and 
    product\category items just display default title.
   --%>
  <c:choose>
    <c:when test="${not empty title}">    
      <title>${title}</title>
    </c:when>  
    <c:otherwise>
      <title>
        <fmt:message key="common.storeTitle">
          <fmt:param>
            <crs:outMessage key="common.storeName"/>
          </fmt:param>
        </fmt:message>
      </title> 
    </c:otherwise>
  </c:choose>
  
  <%-- Page's meta description --%>
  <c:if test="${not empty description}">
    <%-- Remove double quotes from description --%>
    <c:set var="description" value="${fn:replace(description, '\"', '')}" />    
    <meta name="description" content="${description}" />
  </c:if>     
       
  <%-- Page's meta keywords --%>
  <c:if test="${not empty keywords}">
    <%-- Remove double quotes from description --%>
    <c:set var="keywords" value="${fn:replace(keywords, '\"', '')}" />
    <meta name="keywords" content="${keywords}"/>
  </c:if>
  
  <%-- Author meta tag --%>
  <fmt:message var="author" key="common.author" />
  <meta name="author" content="${author}"/>
     
  <%-- Dublin Core meta tags --%>
  <link rel="schema.DC" href="http://www.purl.org/dc/elements/1.1/" />
  <link rel="schema.DCTERMS" href="http://www.purl.org/dc/terms/" />
  <link rel="schema.DCMITYPE" href="http://www.purl.org/dc/dcmitype/" />

  <%--DC.DC.title content will be the same as page's title content --%>
  <meta name="DC.title" content="${title}" />
  
  <meta name="DC.creator" content="${author}" />
    
  <%--DC.subject content will be the same as keywords meta tag content --%>
  <meta name="DC.subject" content="${keywords}" />
    
  <%--DC.DC.description content will be the same as description meta tag content --%>
  <meta name="DC.description" content="${description}" />
    
  <%-- a person, an organization, or a service responsible for making the resource available --%>
  <meta name="DC.publisher" content="${author}" />
    
  <meta name="DC.type" scheme="DCTERMS.DCMIType" content="Text" />
   
  <meta name="DC.format" content="text/html" />
    
  <meta name="DC.language" scheme="RFC1766" content="en" />
    
  <meta name="DC.rights" content="/company/terms.jsp" />
    
    
  <meta name="DC.identifier" scheme="DCTERMS.URI" content="${pageUrl}" />
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/metaDetails.jsp#2 $$Change: 788278 $--%>
<%-- 
  This page renders the following links:
    1) home - Homepage address
    2) up - If PDP, last item from breadcrumbs; if CDP, last but one item from breadcrumbs.
            Rendered if producId or categoryId is set.
    3) chapter - Link to top-level category (item with index=1). Rendered if producId or categoryId is set.
    4) section - Link to current category (last item in breadcrumbs). Rendered if producId or categoryId is set.
    5) prev - If productId is set, link to previous product (if any) returned by the ProductNeighboursDroplet
    6) next - If productId is set, link to next product (if any) returned by the ProductNeighboursDroplet
    7) canonical - The canonical link for product or category
    8) alternate - The canonical link for the same product or category, but with different locale
  
  Optional parameter:
    categoryId the category id
    productId the product id
--%>
<dsp:page>
  <dsp:getvalueof var="categoryId" param="categoryId" />
  <dsp:getvalueof var="productId" param="productId" />
  
  <dsp:importbean bean="/atg/repository/seo/BrowserTyperDroplet"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean bean="/atg/store/droplet/DocumentLinksDroplet"/>
  <dsp:importbean bean="/atg/multisite/Site"/>

  <%-- Eval base part of link --%>
  <dsp:getvalueof var="serverName" vartype="java.lang.String" bean="/atg/dynamo/Configuration.siteHttpServerName" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" bean="/atg/dynamo/Configuration.siteHttpServerPort" />
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}" />
  <dsp:getvalueof var="siteId" vartype="java.lang.String" bean="Site.id"/>
  <%--
    BrowserTyperDroplet is used to determine the browser used in a request.
    Here we use this determine if the request was made by a search spider. 
    The document links are SEO focussed and we should not display these for
    standard browser types e.g firefox, IE.
    Rendering these links for firefox may cause inadvertent issues with features
    such as 'recently viewed' due to firefox performing a page prefetch on the
    'next' relation link hint.
            
    Open Parameters:
      output - Serviced when no errors occur
             
    Output Parameters:
      browserType - The browser type.
  --%>
  <dsp:droplet name="BrowserTyperDroplet" var="currentBrowser">
    <dsp:oparam name="output">

      <%-- Check if we're dealing with a search spider 'bot --%> 
      <c:if test="${currentBrowser.browserType eq 'robot'}">

        <c:if test="${not empty productId}">
          <%--
            This droplet searches for a product in the ProductCatalog repository.
          
            Input parameters:
              id
                A product's ID to be found.
          
            Output parameters:
              element
                A product found.
         
            Open parameters:
              output
                Rendered when product is found.      
          --%>
          <dsp:droplet name="ProductLookup">
            <dsp:param name="id" param="productId"/>
            <dsp:oparam name="output">
               <dsp:getvalueof var="currentProduct" param="element"/>
            </dsp:oparam> 
          </dsp:droplet>
        </c:if>
          
        <c:if test="${not empty categoryId}">
          <%--
            This droplet looks up category by its ID in the product catalog.
        
            Input parameters:
              id
                Specifies a category to be found.
        
            Output parameters:
              element
                Category found.
         
            Open parameters:
              output
                Rendered when category is found.
          --%>
          <dsp:droplet name="CategoryLookup">
            <dsp:param name="id" param="categoryId" />
            <dsp:oparam name="output">
              <dsp:getvalueof var="currentCategory" param="element"/>
            </dsp:oparam>
          </dsp:droplet>  
        </c:if>
        
        <c:if test="${not empty categoryId || not empty productId}">
          <%-- 
            This droplet returns next canonical links:
             1) home - Homepage address
             2) up - If on the product detail page, last item from breadcrumbs; if on the category page, last but one item from breadcrumbs.
                Rendered if producId or categoryId is set.
             3) chapter - Link to top-level category (item with index=1). Rendered if productId or categoryId is set.
             4) section - Link to current category (last item in breadcrumbs). Rendered if productId or categoryId is set.
             5) prev - If productId is set, link to previous product (if any)
             6) next - If productId is set, link to next product (if any)
             7) canonical - The canonical link for product or category
             8) alternate - The canonical link for the same product or category, but with different locale
         
            Input parameters
              currentCategory
                The currently viewed category
              currentProduct 
                The currently viewed product
              currentSiteId
                The id of current site
             
            Output parameters:
              rel
                Link rel attribute. It can take flowing values:
                home, up, chapter, section, prev, next, canonical, alternate 
              href
                Contains the link href attribute
              langhref
                Rendered only for links with rel="alternate". Contains the link langhref attribute.
              lang
                Rendered only for links with rel="alternate". Contains the link lang attribute.
            
            Open parameters:
              output
                Rendered if some link was generated. Will be rendered as many times as many links will be generated by droplet. 
              empty
                Rendered if  no links were generated.
          --%>
          <dsp:droplet name="DocumentLinksDroplet" currentCategory="${currentCategory}" currentProduct="${currentProduct}"
                     currentSiteId="${siteId}"  var="linkDetails">
            <%-- 
              Output parameter is rendered if some link was generated. 
              Will be rendered as many times as many links will be generated by droplet. 
            --%>
            <dsp:oparam name="output">
                            
              <c:choose>
                <c:when test="${linkDetails.rel == 'alternate'}">                           
                  <link rel="${linkDetails.rel}" lang="${linkDetails.lang}" hreflang="${linkDetails.hreflang}" href="${httpServer}${linkDetails.href}"/>
                </c:when>
                <c:otherwise>
                  <link rel="${linkDetails.rel}" href="${httpServer}${linkDetails.href}" />
                </c:otherwise>
              </c:choose>     
                    
            </dsp:oparam>    
          </dsp:droplet> 
        </c:if>
      </c:if>
    </dsp:oparam>
  </dsp:droplet>
  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/documentLinks.jsp#1 $$Change: 735822 $--%>
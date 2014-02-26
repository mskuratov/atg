<%--
  Tag that acts as a container for all top level pages, it includes all relevant header, footer and nav elements. 

  The body of this tag should include any required gadgets.

  If any of the divId, titleKey or textKey attributes are set, then the pageIntro gadget will be included. 
  If none of these attributes are specified, then the pageIntro gadget will not be included.

  The tag accepts the following fragments:
    navigationAddition 
      Specifies a fragment that will be included at the end of the left nav gadgets.  
      For example,
        <jsp:attribute name="leftNavigationAddition">
           ....
        </jsp:attribute>
    subNavigation 
      Specifies a fragment that will be contain sub navigation gadgets.
      For example,
        <jsp:attribute name="subNavigation">
           ....
        </jsp:attribute>
    SEOTagRenderer 
      Specifies a fragment that will render SEO meta tags.
      For example,
        <jsp:attribute name="SEOTagRenderer">
           ....
        </jsp:attribute>      
                    

  Required attributes:
    None.

  Optional attributes: 
    divId 
      Id for the containing div, passed to the pageIntro gadget.
    copyrightDivId 
      Id for the containing div, passed to the copyright gadget. 
      If 'copyrightDivId' is not specified then the default value 'atg_store_copyright' will be used.
    bodyClass 
      Class name that will be used in the page's '<body>' tag.
    contentClass 
      Class name that will be used for the page's 'content' '<div>' tag.
    titleKey 
      Resource bundle key for the title, passed to the 'pageIntro' gadget.
    textKey 
      Resource bundle key for the intro text, passed to the 'pageIntro' gadget.
    titleString 
      Title string, passed to the 'pageIntro' gadget.
    textString 
      Intro text string, passed to the 'pageIntro' gadget.
    index 
      Boolean flag indicating if indexing instruction is passed to robot '<meta>' tag and specifies whether a page 
        should be indexed by search robots; Defaults to true.
    follow 
      Boolean flag indicating if indexing instruction is passed to robot '<meta>' tag and specifies whether a page 
        should be followed by search robots; Defaults to true.
    levelNeeded
      String representation of level required for displaying the current page. All levels defined in the
      /atg/store/states/CheckoutProgressStates.checkoutProgressLevels property.
    redirectURL
      If current page is not allowed, request will be redirected to this URL.
    a11yNavDiv
      The nav div to skip to
    a11yNavTextKey
      The skip nav link text
    a11yContentDiv
      The content div to skip to
    a11yContentTextKey
      The skip nav content link text
--%>

<%@ include file="/includes/taglibs.jspf" %>
<%@ include file="/includes/context.jspf" %>

<%@ tag language="java" %>

<%@ attribute name="divId" %>
<%@ attribute name="copyrightDivId" %>
<%@ attribute name="bodyClass" %>
<%@ attribute name="contentClass" %>
<%@ attribute name="titleKey" %>
<%@ attribute name="textKey" %>
<%@ attribute name="titleString" %>
<%@ attribute name="textString" %>
<%@ attribute name="index" %>
<%@ attribute name="follow" %>
<%@ attribute name="navigationAddition" fragment="true" %>
<%@ attribute name="subNavigation" fragment="true" %>
<%@ attribute name="SEOTagRenderer" fragment="true" %>
<%@ attribute name="levelNeeded" %>
<%@ attribute name="redirectURL" %>
<%@ attribute name="selpage" %>
<%@ attribute name="a11yNavDiv" %>
<%@ attribute name="a11yNavTextKey" %>
<%@ attribute name="a11yContentDiv" %>
<%@ attribute name="a11yContentTextKey" %>
<%@ attribute name="formErrorsRenderer" fragment="true" %>
 
<dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
<dsp:importbean bean="/atg/endeca/assembler/droplet/InvokeAssembler"/>
<dsp:importbean bean="/atg/store/catalog/CatalogNavigation"/>

<dsp:droplet name="InvokeAssembler">
  <dsp:param name="contentCollection" value="/content/Shared/Global Search Configuration/Search Box"/>
  <dsp:oparam name="output">
    <dsp:getvalueof var="searchBox" vartype="com.endeca.infront.assembler.ContentItem" param="contentItem" />
  </dsp:oparam>
</dsp:droplet>
 

<%-- 
  Check if displaying of current page is allowed. Do it first because this page
  may get redirected. Catch exceptions, because redirect throws SkipPageException.
--%>
<c:catch var="skipPageException">
  <c:if test="${(not empty levelNeeded) and (not empty redirectURL)}">
    <dsp:getvalueof bean="/atg/store/states/CheckoutProgressStates.currentLevelAsInt" id="currentLevel" idtype="java.lang.Integer"/>
    <dsp:getvalueof bean="/atg/store/states/CheckoutProgressStates.checkoutProgressLevels.${levelNeeded}" id="needed" idtype="java.lang.Integer"/>
    <c:if test="${currentLevel < needed}">
      <c:redirect url="${redirectURL}"/>
    </c:if>
  </c:if>
</c:catch>

<%-- Track the user's navigation to provide the appropriate breadcrumbs --%>
<dsp:include page="/browse/gadgets/breadcrumbsNavigation.jsp"/>

<%-- Update last browsed category in profile --%>
<dsp:getvalueof var="currentCategoryValid" bean="CatalogNavigation.currentCategoryValid" />
<c:if test="${currentCategoryValid}">
  <dsp:getvalueof var="currentCategory" bean="CatalogNavigation.currentCategory" />
  <dsp:include page="/browse/gadgets/categoryLastBrowsed.jsp">
    <dsp:param name="categoryLastBrowsed" value="${currentCategory}"/>
  </dsp:include>
</c:if>

<%-- In case user is browsing some category notify any interested components. --%>
<dsp:include page="/browse/gadgets/catalogItemBrowsed.jsp"/>

<%--
  Specify a default value if copyrightDivId attribute is not set from the page 
  containing this tag.
--%>
<c:if test="${empty copyrightDivId}">
  <c:set var="copyrightDivId" value="atg_store_copyright" />
</c:if>

<jsp:invoke fragment="SEOTagRenderer" var="SEOTagRendererContent"/>

<%-- Setup the page e.g CSS, JS, Page Icon etc --%>
<dsp:include page="/includes/pageStart.jsp">
  <dsp:param name="bodyClass" value="${bodyClass}"/>
  <dsp:param name="index" value="${index}"/>
  <dsp:param name="follow" value="${follow}"/>
  <dsp:param name="SEOTagRendererContent" value="${SEOTagRendererContent}" />
</dsp:include>

<%-- Container for the page --%>
<div id="atg_store_container">

  <%-- Display any error messages. --%>
  <div style="display:none">
    <jsp:invoke fragment="formErrorsRenderer"/>
  </div>

  <%-- Accessibility skip nav links --%>
  <c:if test="${empty a11yNavDiv || empty a11yNavTextKey}">
    <c:set var="a11yNavDiv" value="atg_store_catNav"/>
    <c:set var="a11yNavTextKey" value="skipnav.skipToNavigation"/>
  </c:if>
  <c:if test="${empty a11yContentDiv || empty a11yContentTextKey}">
    <c:set var="a11yContentDiv" value="atg_store_content"/>
    <c:set var="a11yContentTextKey" value="skipnav.skipToContent"/>
  </c:if>
  <ol id="atg_store_accessibility_nav">
    <li><a href="#<c:out value='${a11yNavDiv}'/>"><fmt:message key="${a11yNavTextKey}"/></a></li>
    <li><a href="#<c:out value='${a11yContentDiv}'/>"><fmt:message key="${a11yContentTextKey}"/></a></li>
  </ol>  
  <hr/>
      
  <%--
    Include language picker, region picker, site picker
  --%>
  <div id="atg_store_locale">
    <dsp:include page="/navigation/gadgets/sites.jsp"/>
    <dsp:include page="/navigation/gadgets/regions.jsp"/>
    <dsp:include page="/navigation/gadgets/languages.jsp"/>
  </div>

  <%-- Start the main page content --%>
  <div id="atg_store_main">
  
    <%-- Header gadgets --%>
    <div id="atg_store_header">

      <%-- Site Specific Logo --%>
      <dsp:getvalueof var="logoTitle" bean="/atg/multisite/Site.name"/>
      <dsp:getvalueof var="logoImage" bean="/atg/multisite/Site.largeSiteIcon"/>
      <h1 id="atg_store_logo" title="${logoTitle}">        
        <dsp:a page="/index.jsp" title="${logoTitle}">
          <img border="0" alt="${logoTitle}" src="${logoImage}">
        </dsp:a>
      </h1>
      
      <%-- Render the search --%>
      <c:if test="${not empty searchBox}">
        <dsp:renderContentItem contentItem="${searchBox}" />
      </c:if>
    
      <%-- Navigation Area (Login, Account, Orders, Help, Logout) --%>
      <dsp:include page="/navigation/gadgets/welcome.jsp">
        <dsp:param name="selpage" value="${selpage}"/>
      </dsp:include>
        
      <%-- Personal Navigation Area (Promotions, Comparisons, Gift/Wish list, Rich Cart, Checkout) --%>
      <dsp:getvalueof id="repriceOrder" param="repriceOrder"/>
    
      <c:if test="${repriceOrder}">
        <%--
          This droplet executes 'repriceAndUpdateOrder' chain. It uses current cart as input order parameter
          for the chain in question.
    
          Input parameters:
            pricingOp
              Pricing operation to be executed.
    
          Output parameters:
            pipelineResult
              Result returned from the pipeline, if execution is successful.
            exception
              Exception thrown by the pipeline, if any.
    
          Open parameters:
            failure
              Rendered, if the pipeline has thrown an exception.
            successWithErrors
              Rendered, if pipeline successfully finished, but returned error.
            success
              Rendered, if pipeline successfully finished without errors returned.
        --%>
        <dsp:droplet name="RepriceOrderDroplet">
          <dsp:param name="pricingOp" value="ORDER_SUBTOTAL"/>
        </dsp:droplet>

      </c:if>
                
      <dsp:include page="/navigation/gadgets/personalNavigation.jsp">            
        <dsp:param name="selpage" value="${selpage}"/>
      </dsp:include>
        
      <%-- Top level category navigation bar --%>
      <div id="atg_store_catNavContainer">            
        <ul id="atg_store_catNav">
          <dsp:include page="/navigation/gadgets/catalog.jsp" />
        </ul>
      </div>
      <hr/>
    </div>
    <%-- End Header gadgets --%>
  
    <%-- Main Content --%>
    <div id="atg_store_contentContainer">
      <div id="atg_store_content" class="${contentClass}">
        <c:if test="${!empty divId and (!empty titleKey or !empty textKey or !empty titleString or !empty textString)}">
          <dsp:include page="/global/gadgets/pageIntro.jsp">
            <dsp:param name="divId" value="${divId}"/>
            <dsp:param name="titleKey" value="${titleKey}"/>
            <dsp:param name="textKey" value="${textKey}"/>
            <dsp:param name="titleString" value="${titleString}"/>
            <dsp:param name="textString" value="${textString}"/>
          </dsp:include>
        </c:if>

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
            wrongSite
              Rendered if the item being looked up is not accessible through the 
              current user's site. The item is still accessible if you need it.
        --%>
        <dsp:droplet name="/atg/commerce/catalog/CategoryLookup">
          <dsp:param name="id" value="${currentCategory}"/>
        
          <dsp:oparam name="output">
            <c:set var="categoryBelongsToSite" value="true"/>
          </dsp:oparam>
          <dsp:oparam name="wrongSite">
            <c:set var="categoryBelongsToSite" value="false"/>
          </dsp:oparam>
        </dsp:droplet>
       
        <c:choose>
          <c:when test="${currentCategoryValid and categoryBelongsToSite == 'true' or empty currentCategory}">
                
            <%-- Category is available in the current site or not on category page, render body. --%>
            <jsp:doBody/>

          </c:when>
          <c:otherwise> 
            <dsp:setvalue bean="CatalogNavigation.currentCategoryValid" value="true"/>

            <c:choose>
              <c:when test="${categoryBelongsToSite == 'false'}">
                <%-- 
                  Display 'Category not found in site' error message along with 'Continue Shopping' link. 
                --%>
                <fmt:message var="errorMessage" key="common.categoryNotFoundForSite">
                  <fmt:param value="${currentCategory}"/>
                  <fmt:param value="${logoTitle}"/>
                </fmt:message>
              </c:when>
              <c:otherwise>
                <%-- 
                  Display 'Category not found' error message along with 'Continue Shopping' link. 
                --%>       
                <fmt:message var="errorMessage" key="common.categoryNotAvailable"/> 
              </c:otherwise>
            </c:choose>
              
            <%-- Render the main content --%>
            <div class="atg_store_main">
            
              <crs:messageContainer titleText="${errorMessage}">  
                <jsp:body>
                
                  <%--  Continue shopping link--%>
                  <crs:continueShopping>
                     <%-- Use the continueShoppingURL defined by crs:continueShopping tag.--%>
                     <fmt:message var="linkTitle" key="common.button.continueShoppingText"/>
                     
                     <dsp:a href="${continueShoppingURL}" iclass="atg_store_basicButton" title="${linkTitle}">
                       <span>
                         <dsp:valueof value="${linkTitle}"/>
                       </span>
                     </dsp:a>
                   </crs:continueShopping>
                   
                 </jsp:body>
               </crs:messageContainer>
               
            </div>

          </c:otherwise>
        </c:choose> 
  
        <jsp:invoke fragment="navigationAddition"/>
        <jsp:invoke fragment="subNavigation"/>
      </div> 
     <hr/>
    </div>
    <%-- End Content --%>
           
    <%-- Footer gadgets --%>
    <div id="atg_store_footer">
      <%-- Navigation that appears across the bottom of the page --%>
      <dsp:include page="/navigation/gadgets/tertiaryNavigation.jsp"/>
      <%-- Display Copyright --%>
      <dsp:include page="/global/gadgets/copyright.jsp">
       <dsp:param name="copyrightDivId" value="${copyrightDivId}"/>
      </dsp:include>
    </div>
    <%-- End Footer --%>
  </div> 
</div>

<%-- When Javascript needs to be added at the very bottom of the page --%>
<dsp:include page="/includes/pageEnd.jsp"/>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/main/storefront/j2ee/storefront.war/global/gadgets/pageIntro.jsp#1 $$Change: 524649 $ --%>

<%-- 
  This page displays category not available message.
  
  Required parameters:
    None.
  
  Optional parameters:
    categoryId
      The ID of the category that is not available.
    site
      The site name on which category is not available.
    errorMsg
      Any specific error message to display instead of default one.
 --%>
<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest" />

  <dsp:getvalueof id="categoryId" param="categoryId"/>
  <dsp:getvalueof id="site" param="site"/>
  <dsp:getvalueof id="errorMsg" param="errorMsg"/>
  
  <crs:getMessage var="storeName" key="common.storeName"/>
  <fmt:message var="separator" key="common.textSeparator"/>
  <fmt:message var="categoryNotAvailable" key="common.categoryNotAvailableTitle"/>
  
  <crs:pageContainer bodyClass="atg_store_pageProductDetail atg_store_productNotAvailable">
  
    <jsp:attribute name="SEOTagRenderer">
      <%--
        Include SEO meta data and indicate that the title should be '<StoreName> - Product Not Available'.
      --%>
      <dsp:include page="/global/gadgets/metaDetails.jsp">
        <dsp:param name="title" value="${storeName} ${separator} ${categoryNotAvailable}"/>
      </dsp:include>
    </jsp:attribute>
    
    <jsp:body>
     
      <div id="atg_store_contentHeader"></div>
    
      <%--
        Define the correct error message to display, if any specific error message is passed in
        display it, if not, display default error message. If site name or category ID is passed
        include them into the default error message.
       --%>      
      <c:choose>
        <%-- Display error message that is passed in. --%>
        <c:when test="${not empty errorMsg}">
          <c:set var="errorMessage" value="${errorMsg}" />
        </c:when>
        
        <%-- If we have a site specific message display that --%>
        <c:when test="${not empty site && not empty categoryId}">
          <fmt:message var="errorMessage" key="common.categoryNotFoundForSite">
            <fmt:param value="${categoryId}"/>
            <fmt:param value="${site}"/>
          </fmt:message>
        </c:when>
        
        <%-- If we have a categoryId --%>
        <c:when test="${not empty categoryId}">
          <fmt:message var="errorMessage" key="common.categoryNotFound">
            <fmt:param value="${categoryId}"/>
          </fmt:message>
        </c:when>
        
        <%-- Display a message without the categoryId --%>
        <c:otherwise>
          <fmt:message var="errorMessage" key="common.categoryIdNotFound"/>
        </c:otherwise>
      </c:choose>
      
      <%-- 
        Display 'Category not found' error message along with 'Continue Shopping'
        link.
       --%>    
      <crs:messageContainer titleText="${errorMessage}">
        <jsp:body>
          <%-- Continue shopping link --%>
          <crs:continueShopping>
            <%-- Use the continueShoppingURL defined by crs:continueShopping tag. --%>
            <fmt:message var="linkTitle" key="common.button.continueShoppingText"/>
            <dsp:a href="${continueShoppingURL}" iclass="atg_store_basicButton" title="${linkTitle}">
              <span>
                <dsp:valueof value="${linkTitle}"/>
              </span>
            </dsp:a>              
          </crs:continueShopping>
        </jsp:body>
      </crs:messageContainer>

    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/categoryNotAvailable.jsp#2 $$Change: 788278 $--%>

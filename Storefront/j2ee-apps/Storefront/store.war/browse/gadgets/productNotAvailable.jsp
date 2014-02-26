<%--
  This gadget renders a "Sorry, can't find a product" message.

  Required parameters:
    None.

  Optional parameters:
    productId
      Specifies a product we were unable to find by its ID.
    site
      Specifies a current site name.
     errorMsg
      Any specific error message to display instead of default one.
--%>
<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest" />
  <dsp:importbean bean="/atg/dynamo/droplet/ComponentExists"/>  

  <dsp:getvalueof id="productId" param="productId"/>
  <dsp:getvalueof id="site" param="site"/>
  <dsp:getvalueof var="errorMessage" param="errorMsg"/>
    
  <crs:getMessage var="storeName" key="common.storeName"/>
  <fmt:message var="separator" key="common.textSeparator"/>
  <fmt:message var="productNotAvailable" key="common.productNotAvailableTitle"/>
  
  <crs:pageContainer bodyClass="atg_store_pageProductDetail atg_store_productNotAvailable">
  
    <jsp:attribute name="SEOTagRenderer">
      <%--
        Include SEO meta data and indicate that the title should be '<StoreName> - Product Not Available'.
      --%>
      <dsp:include page="/global/gadgets/metaDetails.jsp">
        <dsp:param name="title" value="${storeName} ${separator} ${productNotAvailable}"/>
      </dsp:include>
    </jsp:attribute>
    
    <jsp:body>
      <div id="atg_store_contentHeader">
      </div>

      <%-- Determines message to display. --%>
      <c:if test="${empty errorMessage}">      
        <c:choose>
          <%-- If we have a site specific message display to display. --%>        
          <c:when test="${not empty site && not empty productId}">
            <fmt:message var="errorMessage" key="common.productNotFoundForSite">
              <fmt:param value="${productId}"/>
              <fmt:param value="${site}"/>
            </fmt:message>
          </c:when>
          <%-- If we have a productId specified. --%>
          <c:when test="${not empty productId}">
            <fmt:message var="errorMessage" key="common.productNotFound">
              <fmt:param value="${productId}"/>
            </fmt:message>
          </c:when>
          <%-- Display a generic error message. --%>
          <c:otherwise>
            <fmt:message var="errorMessage" key="common.productIdNotFound"/>
          </c:otherwise>
        </c:choose>
      </c:if>

      <%-- Render the message. --%>
      <crs:messageContainer titleText="${errorMessage}">
        <jsp:body>
          <%-- Continue shopping link. --%>
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

  <%--
    ComponentExists droplet conditionally renders one of its output parameters
    depending on whether or not a specified Nucleus path currently refers to a
    non-null object. It used to query whether a particular component has been
    instantiated, in this case the KnowledgeBaseProcessor. 
      
    Input Parameters:
      path - The path to a component
       
    Open Parameters:
      true
        Rendered if the component 'path' has been instantiated.
      false
        Rendered if the component 'path' has not been instantiated. 
  --%>      
  <dsp:droplet name="ComponentExists" path="/atg/adc/KnowledgeBaseProcessor"> 
    <dsp:oparam name="true">
      <%--
        Don't insert the RightNow javascript onto this page as we don't need the
        widget displayed. 
        
        Input Parameters:
          client - The value of the  ADC client whose auto-tagging should be 
                   suppressed.
      --%>
      <dsp:droplet name="/atg/adc/droplet/NoTag">
        <dsp:param name="client" value="crs.knowledgebase"/>
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
   
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/productNotAvailable.jsp#4 $$Change: 790435 $ --%>
<%-- 
  Gadget for showing user related promotional content as well as global promotional content.
  
  When a target link URL is defined for a promotion, it can link to categories/sub-categories, 
  products and home pages etc. The promotion can also have no target link.
  
  Includes pricePromos.jsp for rendering User specific promotional content.
  Includes linkedImageText.jsp for rendering Global promotional content.

  Required Parameters:
   divId
    The divId to be used.
  
  Optional Parameters:
    headerKey
      The key to the resource used for the header.
--%>
<dsp:page>

  <dsp:importbean bean="/atg/store/droplet/PromotionFilterDroplet"/>
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/targeting/TargetingArray"/>
  <dsp:importbean bean="/atg/targeting/TargetingForEach"/>

  <dsp:getvalueof var="divId" vartype="java.lang.String" param="divId"/>
  <dsp:getvalueof var="headerKey" vartype="java.lang.String" param="headerKey"/>
  
  <c:set var="counterloop" value="0" />
  <c:set var="numberOfColumns" value="2" />
  
  <div id="${divId}">
  
    <%--
      PromotionFilterDroplet retrieves and filters promotions according
      to theirs site group and start\end dates
            
      Open Parameters:
        output - always served
             
      Output Parameters:
        filteredPromotions -filtered promotions collection
    --%> 
    <dsp:droplet name="PromotionFilterDroplet">
      <dsp:oparam name="output">
        <dsp:getvalueof var="allPromotions" param="filteredPromotions"/>
      </dsp:oparam>
    </dsp:droplet>
    
    <c:choose>
      <c:when test="${empty allPromotions}">
        <fmt:message var="errorMsg" key="browse_promotions.promotionsNotAvailbale"/>
        <crs:messageContainer titleText="${errorMsg}">
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
        
      </c:when>
      <c:otherwise>
        <dsp:getvalueof var="size" vartype="java.lang.Integer" value="${fn:length(allPromotions)}"/>
        
        <c:if test="${!empty headerKey}">
          <h3>
            <fmt:message key="${headerKey}"/>
          </h3>
        </c:if>
        
        <ul>
        
        <c:forEach var="allPromotion" items="${allPromotions}" varStatus="allPromotionStatus">
          <preview:repositoryItem item="${allPromotion}">
            <c:set var="counterloop" value="${counterloop+1}" />
          
            <dsp:getvalueof id="count" value="${allPromotionStatus.count}"/>
            <dsp:param name="allPromotion" value="${allPromotion}"/>
          
            <c:if test="${counterloop % numberOfColumns == 0}">
              <li class="atg_store_promo lastCol">
            </c:if>
          
            <c:if test="${counterloop % numberOfColumns != 0}">
              <li class="atg_store_promo">
            </c:if>

            <dsp:getvalueof var="media" param="allPromotion.media"/>
          
            <c:if test="${not empty media}">
              <dsp:getvalueof id="promotionDisplayName" idtype="java.lang.String" param="allPromotion.displayName" />
            
              <c:set var="promotionDisplayName">
                <c:out value="${promotionDisplayName}" escapeXml="true"/>
              </c:set>
             
              <%-- Image URL --%>
              <dsp:getvalueof var="imageURL" vartype="String" param="allPromotion.media.large.url"/>
           
              <%-- Current site base URL --%>
              <dsp:getvalueof var="currentSiteId" bean="Site.id" />

              <%-- Current site Target Link URL --%>
              <dsp:getvalueof var="url" vartype="String" param="allPromotion.media.${currentSiteId}.url"/>

              <c:if test="${empty url}">
                <dsp:getvalueof var="url" vartype="String" param="allPromotion.media.targetLink.url"/>
              </c:if>

              <%--
                This droplet's main purpose is to determine an Endeca driven target link URL.
                
                The URL passed into this droplet can be in any format but a substitution will only be attempted 
                with what's between particular opening and closing tokens (if they exist). A URL that will be 
                processed in this way should be in the form of /crs/storeus{categoryId=catMen}.
                
                The targetLinkURL generated will replace the enclosed category ID value with
                it's corresponding DimensionValueCacheObject URL i.e. crs/storeus/browse?N=10115 
                or an SEO formatted URL such as /crs/storeus/browse/_/Category_Men/N_7tq.
                
                Input parameters:
                  url - A URL that will be processed to try to generate a corresponding Endeca driven targetLinkURL.
        
                Open Parameters:
                  output - Always serviced.
                       
                Output Parameters:
                  targetLinkURL - A generated Endeca driven target link URL when a substitution has taken place.
                  
                                - The original passed in URL when no opening and closing tokens have been found.
                                
                                - An empty string when the original passed in URL is empty or when a corresponding 
                                  dimension value ID can't be found.
              --%>
              <dsp:droplet name="/atg/endeca/store/droplet/TargetLinkURLDroplet">
                <dsp:param name="url" value="${url}"/>
              
                <dsp:oparam name="output">
                  <dsp:getvalueof var="targetLinkURL" param="targetLinkURL"/>
                </dsp:oparam>
              </dsp:droplet>
             
            </c:if>
              
            <c:choose>
              <c:when test="${not empty targetLinkURL}">
                <dsp:a href="${targetLinkURL}">
                  <c:if test="${not empty imageURL}">                
                    <img src="${imageURL}" alt="${promotionDisplayName}" />
                  </c:if>

                  <dsp:getvalueof id="description" param="allPromotion.description"/>
                
                  <c:if test="${not empty description}">              
                    <span class="atg_store_promoCopy">
                      <dsp:valueof value="${description}" valueishtml="true"/>
                    </span>
                  </c:if>
              
                </dsp:a>
              </c:when>
              <c:otherwise>
              
                <c:if test="${not empty imageURL}">                
                  <img src="${imageURL}" alt="${promotionDisplayName}" />
                </c:if>
            
                <dsp:getvalueof id="description" param="allPromotion.description"/>
              
                <c:if test="${not empty description}">
                  <span class="atg_store_promoCopy">
                    <dsp:valueof value="${description}" valueishtml="true"/>
                  </span>
                </c:if>
            
              </c:otherwise>
            </c:choose>
          
            </li>
          
            <c:if test="${counterloop % numberOfColumns == 0 && count != size}">
              <c:set var="counterloop" value="0" />
            </c:if>
        
         </preview:repositoryItem>
        </c:forEach>
      </c:otherwise>
    </c:choose>

  </div>
 
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/promotions.jsp#3 $$Change: 793981 $--%>
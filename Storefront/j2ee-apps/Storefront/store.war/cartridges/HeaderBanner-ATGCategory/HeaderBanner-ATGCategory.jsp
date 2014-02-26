<%-- 
  This page lays out the elements that make up the category results banner.
    
  Required Parameters:
    None.
   
  Optional Parameters:
    None.
--%>
<dsp:page>

  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/> 

  <%-- We only want to render the cartridge if a background image exists --%>
  <c:if test="${not empty contentItem.backgroundBannerURL}">

    <c:set var="style" 
           value="background:url(${contentItem.backgroundBannerURL}) no-repeat scroll left bottom transparent; 
                  margin-bottom:-40px;"/>
    
    <div id="atg_store_contentHeader" style="${not empty contentItem.backgroundBannerURL ? style : ' '}">    
      
      <c:if test="${not empty contentItem.promotionalContentId}">
        
        <%-- 
          Use the promotionalContent ID to retrieve promotion repository item. We can then
          use this item to retrieve the promotionalContent template URL and image.
          
          Input Parameters
            id
              promotionalContent ID for the item we wish to retrieve.
            
          Output Parameters
            element
              The promotionalContent repository item.  
        --%>
        <dsp:droplet name="/atg/commerce/promotion/PromotionalContentLookup">      
          <dsp:param name="id" value="${contentItem.promotionalContentId}"/>
    
          <dsp:oparam name="output">
            
            <div id="atg_store_contentHeadPromo">
              <div class="atg_store_promotionItem">
              
                <span class="atg_store_promotionItem">
                  <dsp:getvalueof var="templateUrl" param="element.template.url" />
        
                  <c:if test="${not empty templateUrl}">
                    
                    <dsp:getvalueof var="pageurl" vartype="java.lang.String"
                                    param="element.template.url">
      
                      <dsp:include page="${pageurl}">
                        <dsp:param name="promotionalContent" param="element" />
                        <dsp:param name="imageHeight" value="134" />
                        <dsp:param name="imageWidth" value="752" />
                      </dsp:include>
                    </dsp:getvalueof>
                    
                  </c:if>    
                </span>
                
              </div>
            </div>
          </dsp:oparam>
        </dsp:droplet>
      </c:if>
  
      <dsp:getvalueof var="currentCategoryId" 
                      bean="/atg/endeca/assembler/cartridge/StoreCartridgeTools.currentCategoryId"/>
                      
      <preview:repositoryItem itemId="${currentCategoryId}" 
                              itemType="category"
                              repositoryName="ProductCatalog">
        <h2 class="title">
          <c:out value="${contentItem.headerTitle}" /> 
        </h2>
        
      </preview:repositoryItem>
      
    </div>
 
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cartridges/HeaderBanner-ATGCategory/HeaderBanner-ATGCategory.jsp#1 $$Change: 742374 $ --%>
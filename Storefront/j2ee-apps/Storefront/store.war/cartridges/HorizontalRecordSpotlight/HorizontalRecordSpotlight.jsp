<%-- 
  This page lays out the elements that make up the Endeca driven horizontal spotlight.
    
  Required Parameters:
    None.
   
  Optional Parameters:
    None.
--%>
<dsp:page>

  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  
  <dsp:getvalueof var="contentItem" 
                  vartype="com.endeca.infront.assembler.ContentItem" 
                  value="${originatingRequest.contentItem}"/> 
          
  <c:if test="${not empty contentItem.records}">
    
    <div id="atg_store_spotlight">
    
      <ul class="atg_store_product">
      
        <dsp:getvalueof var="size" value="${fn:length(contentItem.records)}"/>

        <%-- 
          The title and 'See All' link should only be displayed when set in the ContentItem 
          and when at least one spotlight product has been displayed on the page. 
          
          One scenario of when these variables are needed, is when a shareable type (used in 
          this cartridge renderer) doesn't cover all of the product records returned in the 
          ContentItem.  
        --%>
        <c:set var="displayTitle" value="false"/>
        <c:set var="displaySeeAllLink" value="false"/>
        
        <c:forEach var="record" items="${contentItem.records}" varStatus="status" begin="0" end="4">
        
          <%-- 
            Each 'Record' in the list will contain a product repository id that will be used
            to retrieve the actual product items. 
          --%>
          <c:set var="productId" value="${record.attributes['product.repositoryId']}"/>
        
          <%--
            Get the product item using the ID returned from the record attribute.
                 
            Input Parameters:
              id
                The ID of the product we want to look up

            Open Parameters:
              output
                Serviced when no errors occur.
          
            Output Parameters:
              element
                The product whose ID matches the 'id' input parameter.
          --%>
          <dsp:droplet name="ProductLookup">
            <dsp:param name="id" value="${productId}"/>
            <dsp:param name="filterBySite" value="false"/>
            <dsp:param name="filterByCatalog" value="false"/>
            
            <dsp:oparam name="output">
              <dsp:setvalue param="product" paramvalue="element"/> 
              <dsp:getvalueof var="templateUrl" param="product.template.url" />
              
              <%-- Display only products with properly configured template. --%>
              <c:if test="${not empty templateUrl}">
                        
                <%-- 
                  Since we are now diplaying at least one product, the title and 
                  'See All' link can be displayed. 
                --%>
                <c:if test="${not displayTitle}">

                  <c:set var="displayTitle" value="true"/>
                  <c:set var="displaySeeAllLink" value="true"/>
                  
                  <div id="atg_store_spotlightText">
                    <h3>
                      <crs:outMessage key="horizontal_record_spotlightTitle" />
                    </h3>
                  </div>

                </c:if>

                <li class="<crs:listClass count="${status.count}" 
                                          size="${ size < 5 ? size : 4}" 
                                          selected="false"/>">
                                          
                  <%-- Render spotlight product info. --%>
                  <dsp:include page="/global/gadgets/promotedProductRenderer.jsp">
                    <dsp:param name="product" param="product" />
                    <dsp:param name="imagesize" value="medium"/>
                  </dsp:include>
                  
                </li>
                 
              </c:if>
              
            </dsp:oparam>
          </dsp:droplet>
          
        </c:forEach>     
      </ul>
                    
    </div>
    
    <c:if test="${not empty contentItem.seeAllLink and displaySeeAllLink}">
      <div id="atg_store_spotlightSeeAllLink">
        <c:url value="${contentItem.seeAllLink.navigationState}" var="seeAllAction"/>
        
        <a href="${seeAllAction}">
          <crs:outMessage key="horizontal_record_spotlightSeeAllText" />
        </a>
      </div>
    </c:if>
    
  </c:if>

</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cartridges/HorizontalRecordSpotlight/HorizontalRecordSpotlight.jsp#1 $$Change: 742374 $ --%>

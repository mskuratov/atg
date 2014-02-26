<%--
  Renders Product Spotlight Targeter
--%>
<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  
  <dsp:getvalueof var="content" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/> 
  <dsp:getvalueof var="items" value="${content.items}"/> 

  <c:if test="${not empty items}">
    <div id="atg_store_prodList">
    
      <div id="atg_store_spotlightText">
        <h3>
          <%-- Display the title for this spotlight. --%>
          <crs:outMessage key="productTargeter_spotlightTitle" />
        </h3>
      </div>

      <ul class="atg_store_product">
      
        <c:forEach items="${items}" var="item">  
          <li>
            <dsp:include page="/global/gadgets/promotedProductRenderer.jsp">
              <dsp:param name="product" value="${item}"/>
              <dsp:param name="imagesize" value="medium"/>
            </dsp:include>
          </li>
        </c:forEach> 
       
      </ul>
                    
    </div>
  </c:if>
  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cartridges/ProductSpotlight-ATGTargeter/ProductSpotlight-ATGTargeter.jsp#1 $$Change: 742374 $ --%>
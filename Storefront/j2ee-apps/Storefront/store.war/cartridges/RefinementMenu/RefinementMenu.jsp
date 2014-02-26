<%-- 
  This page lays out the elements that make up the RefinementMenu.
    
  Required Parameters:
    contentItem
      The content parameter represents an unselected dimension refinement.
   
  Optional Parameters:

--%>
<dsp:page>
  
  <dsp:getvalueof var="contextPath" vartype="java.lang.String"  bean="/OriginatingRequest.contextPath"/>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  
  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/>
  <c:if test="${not empty(contentItem.refinements)}" >
    <div class="atg_store_facetsGroup_options_catsub">
      <%-- Dimension name - use the dimension name as the look up for internationalization --%>
      <fmt:message var="dimensionHeader" key="${contentItem.dimensionName}"/>
      <c:if test="${empty(dimensionHeader)}" >
        <c:set var="dimensionHeader" value="${contentItem.name}"></c:set>
      </c:if>
      <h5>
        <c:out value="${dimensionHeader}"/>
      </h5>
      
      <%-- Dimension values --%>
      <div>
        <ul class="atg_store_facetOptions" dojoattachpoint="containerNode">
      
          <c:choose>
            <c:when test="${contentItem.dimensionName eq 'product.category'}">
              <c:forEach var="refinement" items="${contentItem.refinements}">
                <c:set var="categoryId" value="${refinement.properties['category.repositoryId'] }"/>
                  <preview:repositoryItem itemId="${categoryId}" itemType="category"
                                          repositoryName="ProductCatalog">
                    <li>
                     <dsp:a href="${contextPath}${refinement.contentPath}${refinement.navigationState}" title="${refinement.label}">
                       <c:out value="${refinement.label}"/> <span class="productCount">(<c:out value="${refinement.count}"/>)</span>
                     </dsp:a>
                   </li>
                 </preview:repositoryItem>
               </c:forEach>
         
             </c:when>
             <c:otherwise>
               <c:forEach var="refinement" items="${contentItem.refinements}">
                 <li>
                   <dsp:a href="${contextPath}${refinement.contentPath}${refinement.navigationState}" title="${refinement.label}">
                     <c:out value="${refinement.label}"/> <span class="productCount">(<c:out value="${refinement.count}"/>)</span>
                   </dsp:a>
                 </li>
               </c:forEach>
             </c:otherwise>
           </c:choose>
            
          <%-- Display the more link --%>
          <c:if test="${!empty(contentItem.moreLink)}">
            <fmt:message var="moreText" key="facet.panel.more" />
            <li>
              <dsp:include page="/global/renderNavLink.jsp">
                <dsp:param name="navAction" value="${contentItem.moreLink}"/>
                <dsp:param name="text" value="${moreText}"/>
                <dsp:param name="titleText" value="${moreText}"/>
              </dsp:include>
            </li>
          </c:if>
          <%-- Display the less link --%>
          <c:if test="${!empty(contentItem.lessLink)}">
            
            <fmt:message var="lessText" key="facet.panel.less" />
            <li>
              <dsp:include page="/global/renderNavLink.jsp">
                <dsp:param name="navAction" value="${contentItem.lessLink}"/>
                <dsp:param name="text" value="${lessText}"/>
                <dsp:param name="titleText" value="${lessText}"/>
              </dsp:include>
            </li>
          </c:if>
        </ul>
      </div>
    </div>  
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cartridges/RefinementMenu/RefinementMenu.jsp#2 $$Change: 768606 $--%>

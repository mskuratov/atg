<%--
  ~ Copyright 2001, 2012, Oracle and/or its affiliates. All rights reserved.
  ~ Oracle and Java are registered trademarks of Oracle and/or its
  ~ affiliates. Other names may be trademarks of their respective owners.
  ~ UNIX is a registered trademark of The Open Group.

 
  This page lays out the elements that make up a two column page.
    
  Required Parameters:
    contentItem
      The two column page content item to render.
   
  Optional Parameters:

--%>
<dsp:page>

  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/> 

  <crs:pageContainer divId="atg_store_facetGlossaryIntro" contentClass="category"
                     index="false" follow="false"
                     bodyClass="category atg_store_leftCol">
    <jsp:body>
      <%-- Render the header --%>
      <c:if test="${not empty contentItem.HeaderContent}">
        <c:forEach var="element" items="${contentItem.HeaderContent}">
          <dsp:renderContentItem contentItem="${element}"/>
        </c:forEach>
      </c:if>
      <%-- Render the main content --%>
      <div id="atg_store_two_column_main" class="atg_store_main">
        <div id="ajaxContainer" >
          <div divId="ajaxRefreshableContent">
            <c:forEach var="element" items="${contentItem.MainContent}">
              <dsp:renderContentItem contentItem="${element}"/>
            </c:forEach>
          </div>
        </div>
      </div>
      <%-- Render the left content --%>     
      <div class="aside">
        <c:forEach var="element" items="${contentItem.SecondaryContent}">
          <dsp:renderContentItem contentItem="${element}"/>
        </c:forEach>
      </div>
    
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cartridges/TwoColumnPage/TwoColumnPage.jsp#4 $$Change: 795551 $--%>

<%--
  ~ Copyright 2001, 2012, Oracle and/or its affiliates. All rights reserved.
  ~ Oracle and Java are registered trademarks of Oracle and/or its
  ~ affiliates. Other names may be trademarks of their respective owners.
  ~ UNIX is a registered trademark of The Open Group.

 
  This page lays out the elements that make up the search results page.
    
  Required Parameters:
    contentItem
      The guided navigation content item to render.
   
  Optional Parameters:

--%>
<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/> 
  
  <div id="atg_store_facets" class="atg_store_facetsGroup">
    <c:if test="${not empty contentItem.navigation}">
      <c:forEach var="element" items="${contentItem.navigation}"> 
        <dsp:renderContentItem contentItem="${element}"/>
      </c:forEach>
    </c:if>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cartridges/GuidedNavigation/GuidedNavigation.jsp#2 $$Change: 768606 $--%>

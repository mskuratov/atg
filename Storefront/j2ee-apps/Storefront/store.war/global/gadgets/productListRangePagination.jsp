<%--  
  Renders page numbers and view all link

  Required Parameters:
    arraySplitSize
      Number of items to be displayed per page.
    start
      Start index of the item to be rendered on this page.
    size
      Total number of items to be displayed
    p
      Current page number.

  Optional Parameters:
    top
      Where to render the pagination links on the page. Set to true for top set of links,
      false for the bottom set.
    viewAll
      Set to true if 'view all' has been requested.

--%>
<dsp:page>
  <dsp:getvalueof var="size" idtype="java.lang.Integer" param="size"/>
  <dsp:getvalueof var="start" idtype="java.lang.String" param="start"/>
  <dsp:getvalueof var="viewAll" param="viewAll"/>
  <dsp:getvalueof var="top" param="top"/>
  <dsp:getvalueof var="p" param="p"/>
  <dsp:getvalueof var="originatingRequestURL" bean="/OriginatingRequest.requestURI"/>
     
  <%--
    Get URL parameters that will be passed to javascript functions with escaped
    XML specific characters so that to prevent using them in XSS attacks.
  --%>
  <c:set var="arraySplitSize"><dsp:valueof param="arraySplitSize" valueishtml="false"/></c:set>
  <c:set var="selectedHowMany"><dsp:valueof param="size" valueishtml="false"/></c:set> 
  
  <c:if test="${empty start && not empty p}">
    <c:set var="start" value="${(p - 1) * arraySplitSize + 1}"/>
  </c:if>

  <%-- 
    No need to show the pagination when the total number of items
    is less or equal to the defined number of items per page
  --%>
  <c:if test="${size > arraySplitSize}">
    
    <crs:pagination size="${size}" arraySplitSize="${arraySplitSize}" start="${start}" viewAll="${viewAll}" top="${top}">
  
      <jsp:attribute name="pageLinkRenderer"> <%-- RENDER PAGE LINKS --%>   
        <%-- Build links href --%>
        <c:set var="url" value="${originatingRequestURL}"/>
        <c:set var="pageNum" value="${linkText}"/>
        
        <%@include file="/navigation/gadgets/navLinkHelper.jspf" %>
        
        <a href="${url}" title="${linkTitle}" class="${linkClass}">
          ${linkText}
        </a>
      </jsp:attribute>
      
      
    </crs:pagination>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/productListRangePagination.jsp#3 $$Change: 788278 $ --%>

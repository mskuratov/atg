<%-- 
  This tag determines the paging links to display. 
  If the link is currently selected, it is displayed as plain text. 
  If the link is not selected, the fragment 'pageLinkRenderer' or 'viewAllLinkRenderer' is invoked.

  Page scope variables set:
    linkTitle
      The title to display for the link.
    linkText
      The text to display for the link.
    startValue
      The first item on the page the link it for.
     viewAllLinkClass
      The class to use for the view all link.

  Required attributes:
    size 
      Size of the listing to be displayed.
    arraySplitSize 
      Number of items to show on each page.
    start 
      Index (1 based) of first element being shown on this page.
    top 
      Boolean flag; Set to true if this is the top set of links, false if it is the bottom set.

  Optional attributes:
    viewAll 
      Boolean flag to indicate if we display all pages; Defaults to false.
  --%>

<%@include file="/includes/taglibs.jspf"%>
<%@include file="/includes/context.jspf"%>

<%@ tag body-content="empty" %>

<%@ attribute name="size" required="true" type="java.lang.Integer" %>
<%@ attribute name="arraySplitSize" required="true" type="java.lang.Integer" %>
<%@ attribute name="start" required="true" %>
<%@ attribute name="viewAll" required="true" type="java.lang.Boolean" %>
<%@ attribute name="top" required="true" type="java.lang.Boolean" %>

<%@ attribute name="pageLinkRenderer" fragment="true" %>
<%@ attribute name="viewAllLinkRenderer" fragment="true" %>

<%@ variable name-given="linkTitle" variable-class="java.lang.String" %>
<%@ variable name-given="linkText" variable-class="java.lang.String" %>
<%@ variable name-given="linkClass" variable-class="java.lang.String" %>
<%@ variable name-given="startValue" variable-class="java.lang.String" %>
<%@ variable name-given="selected" variable-class="java.lang.Boolean" %>

<dsp:page>
  <dsp:importbean bean="/atg/store/droplet/ArraySubsetHelper"/>

  <%-- Ensure that we have a reasonable value for array split size. --%>
  <c:if test="${arraySplitSize <= 0}"> 
    <c:set var="arraySplitSize" value="10"/>
  </c:if>
       
  <div class="atg_store_pagination">
    <ul id="atg_store_pagination">
      <%-- Show each page link. --%>
      <dsp:getvalueof var="p_finalPageSize" value="${size/arraySplitSize + 1}" />

      <%-- Don't add extra page if it divides evenly. --%>
      <c:if test="${size % arraySplitSize == 0}">    
        <dsp:getvalueof var="p_finalPageSize" value="${size/arraySplitSize}" />
      </c:if>
        
      <%-- Display the pagination links --%>
      <c:forEach var="i" begin="1" end="${p_finalPageSize}" step="1" varStatus ="status">
        <c:set var="selected" value="${(empty start && i == 1) || ((i - 1) * arraySplitSize + 1) == start}"/>
        <li class="<crs:listClass count="${i}" size="${size/arraySplitSize + 1}" selected="${selected}"/>">
          <fmt:message var="linkTitle" key="common.button.paginationTitle">
            <fmt:param value="${i}"/>
          </fmt:message>

          <c:set var="linkText" value="${i}"/>
          <dsp:getvalueof id="startValue" value="${(i - 1) * arraySplitSize + 1}"/>
          
          <%-- Determine if this link should use the disabledLink class --%>
          <c:choose>
            <c:when test="${selected && !viewAll}">
              <c:set var="linkClass" value="disabledLink"/>
            </c:when>
            <c:otherwise>
              <c:set var="linkClass" value=""/>
            </c:otherwise>
          </c:choose>

          <jsp:invoke fragment="pageLinkRenderer"/>
        </li>
      </c:forEach>

      <%-- Show the "view all" link if required. --%>
      <c:if test="${size > arraySplitSize}">
        
        <li class="atg_store_paginationViewAll ${viewAll ? 'active' : ''}">
          <fmt:message var="linkText" key="common.button.viewAllText"/>
          <fmt:message var="linkTitle" key="common.button.viewAllTitle"/>
          
          <%-- Determine if viewAll link should use the disabledLink class --%>
          <c:choose>
            <c:when test="${viewAll}">
              <c:set var="linkClass" value="disabledLink"/>
            </c:when>
            <c:otherwise>
              <c:set var="linkClass" value=""/>
            </c:otherwise>
          </c:choose>
          
          <jsp:invoke fragment="viewAllLinkRenderer"/>
        </li>
        
      </c:if>
    </ul>
  </div>
</dsp:page>

<%-- @version $Id$$Change$ --%>

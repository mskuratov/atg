<%--  
  This page is a global page for handling pagination (used OOTB by /myaccount/gadgets/myOrders.jsp).
  
  Required parameters
    arraySplitSize
      number of items to show on each page.
    start
      index (1 based) of first element to show on this page.
    size
      size of the product listing to be displayed
    top
      true if this is the top set of links, false if it is the bottom set
      
  Optional parameters:
    viewAll
      set to true if viewAll has been requested.      
--%>
<dsp:page>
  <dsp:getvalueof id="size" idtype="java.lang.Integer" param="size"/>
  <dsp:getvalueof id="arraySplitSize" idtype="java.lang.Integer" param="arraySplitSize"/>
  
  <%-- This line is added as weblogic 10.0 converting Integer parameter as Long --%>
  <c:set var="arraySplitSize" value="${arraySplitSize}"/>
  
  <dsp:getvalueof id="start" idtype="java.lang.String" param="start"/>
  <dsp:getvalueof id="viewAll" param="viewAll"/>
  <dsp:getvalueof id="top" param="top"/>

  <c:if test="${size > arraySplitSize}">
  
    <%-- 
      No need to show the pagination when the number of items is less or equal
      to the defined number of items per page
     --%>
    <crs:pagination size="${size}" arraySplitSize="${arraySplitSize}" start="${start}"
                    viewAll="${viewAll}" top="${top}">
      <jsp:attribute name="pageLinkRenderer">
        <c:choose>
          <c:when test="${selected && !viewAll}">
            <c:out value="${linkText}"/>
          </c:when>
          <c:otherwise>
            <dsp:a href="${pageContext.request.requestURI}" title="${linkTitle}">
              <dsp:param name="start" value="${startValue}"/>
              ${linkText}
            </dsp:a>
          </c:otherwise>
        </c:choose>
      </jsp:attribute>
      <jsp:attribute name="viewAllLinkRenderer">
        <c:choose>
          <c:when test="${viewAll}">
            <c:out value="${linkText}"/>
          </c:when>
          <c:otherwise>
            <dsp:a href="${pageContext.request.requestURI}" title="${linkTitle}"
               iclass="${viewAllLinkClass}">
               <dsp:param name="viewAll" value="true"/>
               <dsp:param name="howMany" param="size"/>
              ${linkText}
            </dsp:a>
          </c:otherwise>
        </c:choose>
      </jsp:attribute>
    </crs:pagination>
  </c:if>  

</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/pagination.jsp#1 $$Change: 735822 $ --%>

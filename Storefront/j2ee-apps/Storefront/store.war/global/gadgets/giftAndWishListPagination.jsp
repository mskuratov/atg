<%--
  This page is a global page for displaying pagination related items and their links
  for GiftLists/WishList pages.
  
  Required parameters:
    arraySplitSize
      Number of items to show on each page.
    start
      Index (1 based) of first element to show on this page.
    size
      Size of the product Listing to be displayed
    itemList
      the list of items to split with pages
    top
      true if this is the set of pagination links displayed on top of item list, false if 
      it is the bottom set
    giftlistId
      the id of the gift/wish list being displayed
    
  Optional parameters:  
    viewAll
      Set to true if all items should has been requested to display.
    productId
      The ID of product added to giftlist. 
 --%>
<dsp:page>
  
  <dsp:getvalueof id="size" idtype="java.lang.Integer" param="size"/>
  <dsp:getvalueof id="arraySplitSize" idtype="java.lang.Integer" param="arraySplitSize"/>
  <%-- This line is added as weblogic 10.0 converting Integer parameter as Long --%>
  <c:set var="arraySplitSize" value="${arraySplitSize}"/>
  <dsp:getvalueof id="start" idtype="java.lang.String" param="start"/>
  <dsp:getvalueof id="viewAll" param="viewAll"/>
  <dsp:getvalueof id="top" param="top"/>
  <dsp:getvalueof id="giftlistId" param="giftlistId"/>
  <dsp:getvalueof id="productId" idtype="java.lang.String" param="productId"/>

  <c:if test="${size > arraySplitSize}">
    <crs:pagination size="${size}" arraySplitSize="${arraySplitSize}" start="${start}"
                    viewAll="${viewAll}" top="${top}">
                    
      <jsp:attribute name="pageLinkRenderer">
        <c:choose>
          <%-- We're rendering current page number. --%>
          <c:when test="${selected && !viewAll}">
            <c:out value="${linkText}"/>
          </c:when>
          <%-- We're rendering some other page number. --%>
          <c:otherwise>
            <%-- Specify how ordinary page link should be displayed --%>
            <dsp:a href="${pageContext.request.requestURI}" title="${linkTitle}" iclass="${linkClass}">
              <dsp:param name="giftlistId" param="giftlistId"/>
              <c:if test="${!empty productId}">
                <dsp:param name="productId" value="${productId}"/>
              </c:if>
              <dsp:param name="start" value="${startValue}"/>
              ${linkText}
            </dsp:a>
          </c:otherwise>
        </c:choose>
      </jsp:attribute>
      
      <jsp:attribute name="viewAllLinkRenderer">
        <c:choose>
          <%-- Currently selected 'View All' item. --%>
          <c:when test="${viewAll}">
            <c:out value="${linkText}"/>
          </c:when>
          <c:otherwise>
            <%-- Specify how 'View All' page link should be displayed --%>
            <dsp:a href="${pageContext.request.requestURI}" title="${linkTitle}" iclass="${linkClass}">
              <dsp:param name="viewAll" value="true"/>
              <dsp:param name="giftlistId" param="giftlistId"/>
              <c:if test="${!empty productId}">
                <dsp:param name="productId" value="${productId}"/>
              </c:if>
              <dsp:param name="howMany" param="size"/>
              ${linkText}
            </dsp:a>
          </c:otherwise>
        </c:choose>
      </jsp:attribute>
    </crs:pagination>
  </c:if>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/giftAndWishListPagination.jsp#1 $$Change: 735822 $ --%>

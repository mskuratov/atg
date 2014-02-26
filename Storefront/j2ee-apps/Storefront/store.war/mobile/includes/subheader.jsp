<%--
  This page displays the subheader which contains three horizontal cells.
  Currently (Android 2.3.3), the min-width attribute does not work with tables,
  so the width for any empty cell is explicitly set to 30%, to provide proper spacing within the table.

  Required parameters:
    leftText
      Text specified in the left container
    centerText
      Text specified in the center container
    rightText
      Text specified in the right container

  Optional parameters:
    highlight
      Determines which part should be highlighted. Must be one of 'left', 'center', 'right'
    leftURL
      Url to link in the left cell. If no url is specified, a link is not provided
    centerURL
      Url to link in the center cell. If no url is specified, a link is not provided
    rightURL
      Url to link in the right cell. If no url is specified, a link is not provided
--%>
<dsp:page>
  <dsp:getvalueof var="leftText" param="leftText"/>
  <dsp:getvalueof var="centerText" param="centerText"/>
  <dsp:getvalueof var="rightText" param="rightText"/>
  <dsp:getvalueof var="highlight" param="highlight"/>
  <c:if test="${not empty highlight}">
    <dsp:getvalueof var="highlightLeft" value="${highlight == 'left'}"/>
    <dsp:getvalueof var="highlightCenter" value="${highlight == 'center'}"/>
    <dsp:getvalueof var="highlightRight" value="${highlight == 'right'}"/>
  </c:if>
  <dsp:getvalueof var="leftURL" param="leftURL"/>
  <dsp:getvalueof var="centerURL" param="centerURL"/>
  <dsp:getvalueof var="rightURL" param="rightURL"/>

  <c:set var="dividerBarCenterState" value="${highlightCenter ? 'hide' : (empty centerText) ? 'hide' : 'show'}"/>
  <c:set var="dividerBarRightState" value="${highlightRight ? 'hide' : (empty rightText) ? 'hide' : 'show'}"/>
  <c:set var="highlightLeftClass" value="${highlightLeft ? 'highlight' : ''}"/>
  <c:set var="highlightCenterClass" value="${highlightCenter ? 'highlight' : ''}"/>
  <c:set var="highlightRightClass" value="${highlightRight ? 'highlight' : ''}"/>

  <div class="subHeader">
    <div class="dividerBar ${highlightLeftClass}">
      <c:choose>
        <c:when test="${not empty leftURL}">
          <dsp:a page="${leftURL}">${leftText}</dsp:a>
        </c:when>
        <c:otherwise>
          <div>${leftText}</div>
        </c:otherwise>
      </c:choose>
    </div>
    <div class="dividerBar ${dividerBarCenterState} ${highlightCenterClass}">
      <c:choose>
        <c:when test="${not empty centerURL}">
          <dsp:a page="${centerURL}">${centerText}</dsp:a>
        </c:when>
        <c:otherwise>
          <div>${centerText}</div>
        </c:otherwise>
      </c:choose>
    </div>
    <div class="dividerBar ${dividerBarRightState} ${highlightRightClass}">
      <c:choose>
        <c:when test="${not empty rightURL}">
          <dsp:a page="${rightURL}">${rightText}</dsp:a>
        </c:when>
        <c:otherwise>
          <div>${rightText}</div>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/includes/subheader.jsp#2 $$Change: 742374 $--%>

<%-- 
  This page displays promotion image without description.
  Required Parameters:
    promotionalContent
      the promotionalContent repository item to display   
    
  Optional Parameters:
    omitTooltip
      Boolean parameter. If omitTooltip = false tooltip will be displayed.
      If omitTooltip = true tooltip will not be displayed
--%>

<dsp:page>  
  <crs:promotionalContentWrapper var="title">
    <jsp:body>
  
      <dsp:getvalueof var="imageurl" vartype="java.lang.String" param="promotionalContent.derivedImage"/>
      <dsp:getvalueof var="displayName" vartype="java.lang.String" param="promotionalContent.displayName"/>
      <dsp:getvalueof var="storeDisplayName" vartype="java.lang.String" param="promotionalContent.storeDisplayName"/>
      <dsp:getvalueof var="omitTooltip" vartype="java.lang.Boolean" param="omitTooltip"/>
      
      <%--
        Display promotion image.
        Display tooltip if omitTooltip = false.
       --%>
      <span class="atg_store_promotionItem">
      
        <c:choose>
          <c:when test="${empty imageurl}">
            <fmt:message key="common.image"/>
          </c:when>
          <c:otherwise>
            <c:if test="${!omitTooltip}">
              <c:choose>
                <c:when test="${not empty storeDisplayName}">
                  <c:set var="tooltip" value="${storeDisplayName}"/>
                </c:when>
                <c:otherwise>
                  <c:set var="tooltip" value="${displayName}"/>
                </c:otherwise>
              </c:choose>
            </c:if>
            <dsp:img src="${imageurl}" alt="${tooltip}"/>
          </c:otherwise>
        </c:choose>
      </span>
      
    </jsp:body>
  </crs:promotionalContentWrapper>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/promo/gadgets/linkedImage.jsp#1 $$Change: 735822 $--%>

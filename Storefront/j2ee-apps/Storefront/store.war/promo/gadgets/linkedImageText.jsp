<%-- 
  This page displays promotion image with description. 
   
  Required Parameters:
    promotionalContent
      the promotionalContent repository item to display
    
  Optional Parameters:
    None   
--%>

<dsp:page>  
  <crs:promotionalContentWrapper var="title">
    <jsp:body>
    
      <dsp:getvalueof var="imageurl" vartype="java.lang.String" param="promotionalContent.derivedImage"/>
      
      <%-- Display promotion image. --%>
      <span class="atg_store_promotionItem">
        <c:choose>
          <c:when test="${empty imageurl}">
            <fmt:message key="common.image"/>
          </c:when>
          <c:otherwise>
            <dsp:img src="${imageurl}" alt=""/>
          </c:otherwise>
        </c:choose>
      </span>
      
      <%-- Display promotion description. --%>
      <dsp:getvalueof id="description" param="promotionalContent.description"/>
      <c:if test="${not empty description}">
        <span class="atg_store_promoCopy">
          <%-- Do not escape description, cause it can contain HTML to be displayed. --%>
          <c:out value="${description}" escapeXml="false"/>
        </span>
      </c:if>
    </jsp:body>
  </crs:promotionalContentWrapper>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/promo/gadgets/linkedImageText.jsp#1 $$Change: 735822 $--%>

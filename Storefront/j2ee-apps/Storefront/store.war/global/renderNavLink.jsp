<%--
  Renders navigation links. navAction is an endeca NavigationAction.
--%>
<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}"/>
  <dsp:getvalueof var="navAction" vartype="com.endeca.infront.cartridge.model.NavigationAction" param="navAction"/> 
  <dsp:getvalueof var="linkText" param="text"/> 
  <dsp:getvalueof var="showRemoveLink" vartype="java.lang.Boolean" param="showRemoveLink"/>
  <dsp:getvalueof var="titleText" vartype="java.lang.String" param="titleText"/>

    <c:choose>
    
      <c:when test="${(not empty navAction.contentPath) && (not empty titleText)}">
        <dsp:a href="${contextPath}${navAction.contentPath}${navAction.navigationState}" class="${ showRemoveLink == true ? 'atg_store_actionDelete' : ''}" 
          title="${titleText}"><c:out value="${linkText}"/></dsp:a>
      </c:when>
      <c:when test="${(not empty navAction.contentPath) && (empty titleText)}">
        <dsp:a href="${contextPath}${navAction.contentPath}${navAction.navigationState}" class="${ showRemoveLink == true ? 'atg_store_actionDelete' : ''}"><c:out value="${linkText}"/></dsp:a>
      </c:when>
      <c:when test="${(empty navAction.contentPath) && (not empty titleText)}">
        <dsp:a href="${contextPath}${navAction.navigationState}" class="${ showRemoveLink == true ? 'atg_store_actionDelete' : ''}"
          title="${titleText}"><c:out value="${linkText}"/></dsp:a>
      </c:when>
      <c:otherwise>
        <dsp:a href="${contextPath}${navAction.navigationState}" class="${ showRemoveLink == true ? 'atg_store_actionDelete' : ''}"><c:out value="${linkText}"/></dsp:a>
      </c:otherwise>
    </c:choose>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/renderNavLink.jsp#2 $$Change: 768606 $--%>

<%--
  PageSlot
  
  Passes the contents of the PageSlot to a renderer JSP that knows how to
  handle the contents particular type.
--%>
<dsp:page>

  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/> 

  <endeca:includeSlot contentItem="${contentItem}">
    <c:forEach var="element" items="${contentItem.contents}">
      <dsp:renderContentItem contentItem="${element}"/>
    </c:forEach>
  </endeca:includeSlot>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cartridges/PageSlot/PageSlot.jsp#2 $$Change: 768606 $--%>

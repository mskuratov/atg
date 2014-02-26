<%--
  Displays a list of SKU properties in form of <span class="property">...</span definitions, based on the SKU type.

  Page includes:
    None

  Required parameters:
    sku
      SKU repository item, this item holds parameters to be displayed

  Optional parameters:
    None
--%>
<dsp:page>
  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="skuType" vartype="java.lang.String" param="sku.type"/>

  <c:choose>
    <%-- 
      clothing-sku displays the following properties:
        1. color
        2. size
    --%>
    <c:when test="${skuType == 'clothing-sku'}">
      <dsp:getvalueof var="size" vartype="java.lang.String" param="sku.size"/>
      <dsp:getvalueof var="color" vartype="java.lang.String" param="sku.color"/>
      <c:if test="${not empty color}">
        <span class="property color"><c:out value="${color}"/></span><c:if test="${not empty size}"><c:out value=","/></c:if>
      </c:if>
      <c:if test="${not empty size}">
        <span class="property size"><c:out value="${size}"/></span>
      </c:if>
    </c:when>
    <%-- 
      furntirue-sku displays the following properties:
        1. woodFinish
    --%>
    <c:when test="${skuType == 'furniture-sku'}">
      <dsp:getvalueof var="woodFinish" vartype="java.lang.String" param="sku.woodFinish"/>
      <c:if test="${not empty woodFinish}">
        <span class="property size"><c:out value="${woodFinish}"/></span>
      </c:if>
    </c:when>
  </c:choose>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/global/util/displaySkuProperties.jsp#2 $$Change: 742374 $--%>

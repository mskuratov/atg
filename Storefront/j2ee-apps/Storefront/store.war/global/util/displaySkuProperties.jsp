<%--
  Displays a list of SKU properties in form of a table based on the SKU type.
  
  Required parameters:
    sku
      A SKU repository item, this item holds parameters to be displayed
    product
      A product repository item, this item contains the previous parameter's SKU
      
  Optional parameters:
    displayAvailabilityMessage
      Defines whether to display inventory availability message, or not; default is true.
      Should be set to 'false' for submitted orders as inventory system can not be used
      for determining SKU state for already submitted orders (commerce item's state should
      be checked instead).
--%>
<dsp:page>
  <fmt:message var="tableSummary" key="common.SKUproperties_tableSummary"/>
  <div class="propertyContainer">
    <dsp:getvalueof var="skuType" vartype="java.lang.String" param="sku.type"/>
    <c:choose>
      <%-- 
        For the clothing-sku type display the following properties:
          1. size
          2. color
      --%>
      <c:when test="${skuType == 'clothing-sku'}">
        <dsp:getvalueof var="size" vartype="java.lang.String" param="sku.size"/>
        <dsp:getvalueof var="color" vartype="java.lang.String" param="sku.color"/>
        <c:if test="${not empty size}">
          
          <div class="itemProperty">
            <span class="propertyDetails">
              <span class="propertyLabel">
                <fmt:message key="common.size"/><fmt:message key="common.labelSeparator"/>
              </span>
              <span class="propertyValue">
                <c:out value="${size}"/>
              </span>
            </span>
          </div>
        </c:if>
        <c:if test="${not empty color}">
          <div class="itemProperty">
            <span class="propertyDetails">
              <span class="propertyLabel">
                <fmt:message key="common.color"/><fmt:message key="common.labelSeparator"/>
              </span>
              <span class="propertyValue">
                <c:out value="${color}"/>
              </span>
            </span>
          </div>
        </c:if>
      </c:when>
      <%-- 
        For the furniture-sku type display the following properties:
          1. woodFinish
      --%>
      <c:when test="${skuType == 'furniture-sku'}">
        <dsp:getvalueof var="woodFinish" vartype="java.lang.String" param="sku.woodFinish"/>
        <c:if test="${not empty woodFinish}">
          <div class="itemProperty">
            <span class="propertyDetails">
              <span class="propertyLabel">
                <fmt:message key="common.woodFinish"/><fmt:message key="common.labelSeparator"/>
             </span>
             <span class="propertyValue">
                <c:out value="${woodFinish}"/>
             </span>
           </span>
          </div>
        </c:if>
      </c:when>
    </c:choose>
    
    <%-- For each SKU type display SKU ID and availability message --%>
    <dsp:getvalueof var="displayAvailabilityMessage" vartype="java.lang.Boolean" 
                    param="displayAvailabilityMessage"/>
    <c:if test="${empty displayAvailabilityMessage}">
      <c:set var="displayAvailabilityMessage" value="true"/>
    </c:if>
    <div class="itemProperty">
      <c:choose>
        <c:when test="${displayAvailabilityMessage}">
          <span class="propertyDetails">
            <span class="propertyLabel">
              <dsp:valueof param="sku.repositoryId"/><fmt:message key="common.labelSeparator"/>
            </span>
            <span class="propertyValue">
              <dsp:include page="/global/gadgets/skuAvailabilityLookup.jsp">
                <dsp:param name="product" param="product"/>
                <dsp:param name="skuId" param="sku.repositoryId"/>
              </dsp:include>
              <c:if test="${not empty availabilityMessage}">
                <c:out value="${availabilityMessage}"/>
              </c:if>
            </span>
          </span>
        </c:when>
        <c:otherwise>
          <div class="itemProperty">
            <span class="propertyDetails">
              <span class="propertyLabel">
                <dsp:valueof param="sku.repositoryId"/>
              </span>
            </span>
          </div>
        </c:otherwise>
      </c:choose>    
    </div>
    </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/util/displaySkuProperties.jsp#2 $$Change: 788278 $--%>

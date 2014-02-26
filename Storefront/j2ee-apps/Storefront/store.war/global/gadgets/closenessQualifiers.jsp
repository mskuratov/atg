<%-- 
  Displays closenessQualifiers of all types
  (item, order, shipping, tax) of the current order.
  
  Required Parameters:
    None.  
    
  Optional Parameters:
    None.   
--%>

<dsp:page>

<dsp:importbean bean="/atg/commerce/promotion/ClosenessQualifierDroplet"/>

<%-- This droplet consolidates all of the different types of closenessQualifiers
  (item, order, shipping, tax) of the current order into a single List and returns 
   it in an output parameter.    
   
    Input parameters:
      type
        The type of closenessQualifier to be returned.  Possible values: "item", "order", 
        "shipping", "tax", or "all".  If null or "all", all closenessQualifiers will
        be returned.
        
    Open parameters:
      output
        If there are closenessQualifiers returned, this open parameter will be rendered.  
        
    Output parameters:
      closenessQualifiers
        If input parameter "elementName" is null, this will hold the requested closenessQualifiers.          
 --%>
<dsp:droplet name="ClosenessQualifierDroplet">
  <dsp:param name="type" value="all"/>
  <dsp:oparam name="output">
    <dsp:getvalueof var="closenessQualifiers" vartype="java.lang.Object" param="closenessQualifiers"/>
    <c:if test="${not empty closenessQualifiers}">
      <span class="atg_store_closenessQualifier">
        <c:forEach var="closenessQualifier" items="${closenessQualifiers}">
          <dsp:param name="qualifier" value="${closenessQualifier}"/>
            <dsp:valueof param="qualifier.name" valueishtml="true"/>
        </c:forEach>
      </span>
    </c:if>
  </dsp:oparam>
</dsp:droplet>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/closenessQualifiers.jsp#1 $$Change: 735822 $--%>

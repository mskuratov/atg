<%-- 
  Show the image of the product, or if you have the SKU image let us show it 
  instead. If no SKU image is available, select the best matching product that 
  will allow us to display an image.

  Required Parameters:
    commerceItem
      The commerce item whose image we should display.
      
  Optional Parameters:
    displayAsLink
      If set to false, then image won't not be link.

  Form Conditions:
    This gadget must be contained inside of a form.
    
    CartModifierFormHandler must be invoked from a submit button in this form 
    for fields in this page to be processed.
--%>

<dsp:page>
  
  <%-- This value determines whether to provide a link to an image or not --%>
  <dsp:getvalueof var="linkImage" vartype="java.lang.String" param="displayAsLink"/>
  <c:if test="${empty linkImage}">
    <c:set var="linkImage" value="true"/>
  </c:if>
  
  <%-- 
    This will determine if an alternate image is available. The alternative image will override 
    the product thumbnail image but the product will be used to provide the necessary link to 
    the product detail page.
  --%>
  <dsp:getvalueof var="imageUrl" param="commerceItem.auxiliaryData.catalogRef.smallImage.url"/>

  <c:choose>
    <c:when test="${not empty imageUrl}">
      <dsp:include page="/browse/gadgets/productImgCart.jsp">
        <dsp:param name="product" param="commerceItem.auxiliaryData.productRef"/>
        <dsp:param name="alternateImage" param="commerceItem.auxiliaryData.catalogRef.smallImage"/>
        <dsp:param name="linkImage" value="${linkImage}"/>
      </dsp:include>
    </c:when>
    <c:otherwise>
      <dsp:include page="/browse/gadgets/productImgCart.jsp">
        <dsp:param name="product" param="commerceItem.auxiliaryData.productRef"/>
        <dsp:param name="linkImage" value="${linkImage}"/>
      </dsp:include>
    </c:otherwise>
  </c:choose><%-- End is empty check on the SKU thumbnail image --%>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/gadgets/cartItemImage.jsp#2 $$Change: 788278 $--%>

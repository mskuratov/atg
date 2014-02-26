<%--
  This gadget renders forms for color/size picker for the gift item. 
  It displays a set of buttons to select appropriate color and size (that is appropriate SKU).
  
  Required parameters:
    productId
      Specifies a currently viewed product.
      
    gwpRadioId
      The radio button associated with the color/size picker.
      
    requestURI
      The request URI to be used.  
        
    contextPath
     Context path to be used.
        
    gwpRadioId
      The radio button associated with the color/size picker. 
        
  Optional parameters:      
    colors
      The colors to be displayed in the picker .
        
    sizes
      The sizes to be displayed in the picker.
    
    selectedColor
      The selected color.    
      
    selectedSize
      The selected size.  
   
    colorIsSelected
      Indicates if color is selected.  
    
--%>

<dsp:page>
 
  <dsp:getvalueof var="productId" param="productId"/>
  <dsp:getvalueof var="requestURI" param="requestURI"/>
  <dsp:getvalueof var="skuType" param="skuType"/>
  <dsp:getvalueof var="colors" param="colors"/>
  <dsp:getvalueof var="sizes" param="sizes"/>
  <dsp:getvalueof var="contextPath" param="contextPath"/>
  <dsp:getvalueof var="selectedColor" param="selectedColor"/>
  <dsp:getvalueof var="selectedSize" param="selectedSize"/>
  <dsp:getvalueof var="colorIsSelected" param="colorIsSelected"/> 
  <dsp:getvalueof var="gwpRadioId" param="gwpRadioId"/> 
  
  <div id="gift_contents_${productId}">
    <dsp:form id="addToCart_${productId}" formid="selectGift_${productId}"
              action="${requestURI}" method="post" name="selectGift">
      <div id="atg_gift_picker_${productId}">
        <div class="atg_store_pickerContainer">      
          <%@ include file="/cart/gadgets/giftColorPicker.jspf" %>
          <%-- For clothing sku dispaly size picker --%>
          <c:if test="${skuType == 'clothing'}">
            <%@ include file="/cart/gadgets/giftSizePicker.jspf" %>
          </c:if>
        </div>
      </div>
    </dsp:form>
  </div>
              
  <%-- Include invisible form, it's used for refreshing the picker. --%>
  <dsp:form formid="colorsizerefreshform_${productId}" id="colorsizerefreshform_${productId}" method="post"
            action="${contextPath}/cart/gadgets/giftPickerContent.jsp">
    <input name="skuId" type="hidden" value='<dsp:valueof param="selectedSku.repositoryId"/>'/>        
    <input name="productId" type="hidden" value='<dsp:valueof param="productId"/>'/>
    <input name="gwpRadioId" type="hidden" value='<dsp:valueof param="gwpRadioId"/>'/>
    <input name="skuType" type="hidden" value="${skuType}"/>
                        
    <c:if test="${fn:length(colors) > 0}">
     <dsp:getvalueof var="colorIsSelected" param="colorIsSelected"/>
     <dsp:getvalueof var="selectedColorValue" value=""/>
     <c:if test="${colorIsSelected}">
       <dsp:getvalueof var="selectedColor" param="selectedColor"/>
       <dsp:getvalueof var="selectedColorValue" value="${selectedColor}"/>
     </c:if>
     <input name="selectedColor" type="hidden" value='${selectedColorValue}'/>                       
    </c:if>  
    
     <c:if test="${skuType == 'clothing'}">
    <c:if test="${fn:length(sizes) > 0}">
      <dsp:getvalueof var="selectedSize" param="selectedSize"/>
      <input name="selectedSize" type="hidden" value='${selectedSize}'/>
    </c:if>                
    </c:if>
    
    <%-- 
      The value of selectedProductId will only be taken from this hidden field when
      the this form is refreshed via client script, so it is ok to always set
      selectedProductId to the current product id. When the form has not been
      refreshed via client sscript then the value of selectedProductId will be
      taken from the queryString (if it is present).
    --%>
    <input name="selectedProductId" type="hidden" value='<dsp:valueof param="productId"/>'/>
    
  </dsp:form>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/gadgets/giftPickerForms.jsp#2 $$Change: 791340 $--%>

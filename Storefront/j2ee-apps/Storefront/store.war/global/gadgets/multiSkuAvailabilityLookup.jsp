<%-- 
  This gadget gets the inventory availability of a SKU. It assumes it will be called
  multiple times with different SKUs. If specified SKU has availability state higher
  then the previous SKUs the state will be updated otherwise no updates will be made.
  As a result this gadget determines the highest inventory state for a set of SKUs and
  specifies final "Add To Cart" text and availability message.
  
  The gadget will set the following request-scoped page variables:
    availabilityMessage
      The prefix for a specific skus availability message (i.e., "Preorderable until 
      <date>")
    showNotifyButton
      A boolean detailing if the showNotifyButton should be shown.
    finalAddButtonText
      The text that should be displayed on the final "Add To Cart" button ("Add To Cart", 
      "Preorder", etc.)
    finalAddButtonTitle
      The title that should be displayed on the final "Add To Cart" button ("Add To Cart",
      "Preorder", etc.)
       
  Note that this button keeps internal state in request variables, so it cannot be
  called in two different blocks in one request expecting it to render a different
  'finalAddButtonText' for each block.
  
  Required parameters:
    product
      The product item whose inventory is to be checked
    skuId
      The ID of the sku whose inventory is to be checked
      
  Optional parameters:
    None.  

  Form Condition:
    This gadget must be contained inside of a form. CartModifierFormHandler must be invoked from 
    a submit button in this form for fields in this page to be processed
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>
  <dsp:importbean bean="/atg/store/droplet/SkuAvailabilityLookup"/>
    
  <%--
    This droplet makes an inventory availability lookup for the product/SKU pair specified.

    Input parameters:
      product
        Specifies a product to be checked.
      skuId
        Specifies a SKU to be checked.

    Output parameters:
      availabilityDate
        Specifies a date, when the product/SKU will be available.

    Open parameters:
      available
        Rendered if product/SKU is available.
      preorderable
        Rendered if product/SKU is available for preorder.
      backorderable
        Rendered if product/SKU is available for backorder.
      unavailable
        Rendered if product/SKU is not available.
  --%>
  <dsp:droplet name="SkuAvailabilityLookup">
    <dsp:param name="product" param="product"/>
    <dsp:param name="skuId" param="skuId"/>
  
    <dsp:oparam name="available">
      <%-- Item is in stock --%>
      <fmt:message var="finalAddButtonText" key="common.button.addToCartText" scope="request"/>
      <fmt:message var="finalAddButtonTitle" key="common.button.addToCartTitle" scope="request"/>
      <dsp:getvalueof id="showNotifyButton" idtype="java.lang.Boolean" value="${false}" scope="request"/>
      <dsp:getvalueof id="_multiSkuAvailabilityLookup_finalButtonToShow" idtype="java.lang.String"
                      param="available" scope="request"/>
      <dsp:getvalueof id="availabilityMessage" idtype="java.lang.String" value="" scope="request"/>
    </dsp:oparam>
  
    <dsp:oparam name="preorderable">
      <%-- SKU is available for preorder. Check its availability date and set output variables. --%>
      <dsp:getvalueof id="showNotifyButton" idtype="java.lang.Boolean" value="${false}" scope="request"/>
      <c:choose>
        <c:when test="${_multiSkuAvailabilityLookup_finalButtonToShow == 'preorderable'}">
          <%-- There was already SKU with preorderable state, no updates should be made  --%>
        </c:when>
        <c:when test="${_multiSkuAvailabilityLookup_finalButtonToShow == 'unset'}">
          <%--
            It's first SKU from the set of SKUs, so just update all variable according to 
            preorderable state.
           --%>
          <dsp:getvalueof id="_multiSkuAvailabilityLookup_finalButtonToShow" idtype="java.lang.String"
                          param="preorderable" scope="request"/>
          <fmt:message var="finalAddButtonText" key="common.button.preorderText" scope="request"/>
          <fmt:message var="finalAddButtonTitle" key="common.button.preorderTitle" scope="request"/>
        </c:when>
        <c:otherwise>
          <dsp:getvalueof id="_multiSkuAvailabilityLookup_finalButtonToShow" idtype="java.lang.String"
                          param="available" scope="request"/>
          <fmt:message var="finalAddButtonText" key="common.button.addToCartText" scope="request"/>
          <fmt:message var="finalAddButtonTitle" key="common.button.addToCartTitle" scope="request"/>
        </c:otherwise>
      </c:choose>

      <%-- Specify availability message with date if available --%>
      <dsp:getvalueof var="availabilityDate" param="availabilityDate"/>
      <c:choose>
        <c:when test="${not empty availabilityDate}">
          <dsp:getvalueof id="availDateId" idtype="java.util.Date" param="availabilityDate"/>
          
          <dsp:getvalueof var="dateFormat" 
                          bean="LocaleTools.userFormattingLocaleHelper.datePatterns.shortWith4DigitYear" />
          
          <fmt:formatDate var="addToCartDate" value="${availDateId}" pattern="dateFormat"/>
          <fmt:message var="availabilityMessage" key="common.preorderableUntil" scope="request">
            <fmt:param>
              <span class="date numerical">${addToCartDate}</span>
            </fmt:param>
          </fmt:message>
        </c:when>
        <c:otherwise>
          <fmt:message var="availabilityMessage" key="common.preorderable" scope="request"/>
        </c:otherwise>
      </c:choose>

    </dsp:oparam>
  
    <dsp:oparam name="backorderable">
      <%-- SKU is available for backorder. Check its availability date and set output variables. --%>
      <dsp:getvalueof id="showNotifyButton" idtype="java.lang.Boolean" value="${false}" scope="request"/>
      <c:choose>
        <c:when test="${_multiSkuAvailabilityLookup_finalButtonToShow == 'backorderable'}">
          <%-- There was already SKU with preorderable state, no updates should be made  --%>
        </c:when>
        <c:when test="${_multiSkuAvailabilityLookup_finalButtonToShow == 'unset'}">
          <%--
            It's first SKU from the set of SKUs, so just update all variable according to 
            backorderable state.
           --%>
          <dsp:getvalueof id="_multiSkuAvailabilityLookup_finalButtonToShow" idtype="java.lang.String"
                          param="backorderable" scope="request"/>
          <fmt:message var="finalAddButtonText" key="common.button.backorderText" scope="request"/>
          <fmt:message var="finalAddButtonTitle" key="common.button.backorderTitle" scope="request"/>
        </c:when>
        <c:otherwise>
          <dsp:getvalueof id="_multiSkuAvailabilityLookup_finalButtonToShow" idtype="java.lang.String"
                          param="available" scope="request"/>
          <fmt:message var="finalAddButtonText" key="common.button.addToCartText" scope="request"/>
          <fmt:message var="finalAddButtonTitle" key="common.button.addToCartTitle" scope="request"/>
        </c:otherwise>
      </c:choose>

      <dsp:getvalueof var="availabilityDate" param="availabilityDate"/>
      <c:choose>
        <c:when test="${empty availabilityDate}">
          <fmt:message var="availabilityMessage" key="common.backorderable" scope="request"/>
        </c:when>
        <c:otherwise>
          <dsp:getvalueof id="availDateId" idtype="java.util.Date" param="availabilityDate"/>
          
          <dsp:getvalueof var="dateFormat" 
                          bean="LocaleTools.userFormattingLocaleHelper.datePatterns.shortWith4DigitYear" />
                                  
          <fmt:formatDate var="addToCartDate" value="${availDateId}" pattern="${dateFormat}"/>
          <fmt:message var="availabilityMessage" key="common.backorderableUntil" scope="request">
            <fmt:param>
              <span class="date numerical">${addToCartDate}</span>
            </fmt:param>
          </fmt:message>
        </c:otherwise>
      </c:choose>

    </dsp:oparam>
  
    <dsp:oparam name="default">
      <%-- Item is out of stock! --%>
      <dsp:getvalueof id="showNotifyButton" idtype="java.lang.Boolean" value="${true}" scope="request"/>
      <%--
        The _multiSkuAvailabilityLookup_finalButtonToShow does not change since this item cannot be
        added to the cart.
       --%>
      <fmt:message var="availabilityMessage" key="common.temporarilyOutOfStock" scope="request"/>
      <fmt:message var="addButtonText" key="common.button.emailMeInStockText" scope="request"/>
      <fmt:message var="addButtonTitle" key="common.button.emailMeInStockTitle" scope="request"/>
    </dsp:oparam>
  </dsp:droplet>
  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/multiSkuAvailabilityLookup.jsp#3 $$Change: 788278 $--%>

<%--
  This gadget gets the inventory availability for the SKU specified.
  This gadget sets the following request-scoped variables as a result of invoking:
    addButtonText
      Name that should be displayed on the 'Add to Cart' button.
    addButtonTitle
      Title that should be displayed with the 'Add to Cart' button.
    availabilityMessage
      Prefix for an availability message (i.e. 'Preorderable until').
    availabilityType
      The oparam name returned from the SkuAvailabilityLookup droplet.

  Required parameters:
    product
      Currently viewed product.
    skuId
      Specifies a SKU whose inventory is to be checked.

  Optional parameters:
    showUnavailable
      Flags, if 'Add to Cart' should be displayed for the unavailable SKUs.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>
  <dsp:importbean bean="/atg/store/droplet/SkuAvailabilityLookup"/>
  <%--
    This droplet renders its 'output' parameter wrapped into a transaction.

    Input parameters:
      transAttribute
        Specifies a transaction attribute (required, requiresNew, etc.)

    Output parameters:
      None.

    Open parameters:
      output
        Always rendered in a single transaction.
  --%>
  <dsp:droplet name="/atg/dynamo/transaction/droplet/Transaction">
    <dsp:param name="transAttribute" value="required"/>
    <dsp:oparam name="output">
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
          <%-- SKU is available, set output variables. --%>
          <dsp:getvalueof id="availabilityType" idtype="java.lang.String" value="available" scope="request"/>
          <fmt:message var="addButtonText" key="common.button.addToCartText" scope="request"/>
          <fmt:message var="addButtonTitle" key="common.button.addToCartTitle" scope="request"/>
          <fmt:message var="availabilityMessage" key="common.available" scope="request"/>
        </dsp:oparam>
        <dsp:oparam name="preorderable">
          <%-- SKU is available for preorder. Check its availability date and set output variables. --%>
          <dsp:getvalueof id="availabilityType" idtype="java.lang.String" value="preorderable" scope="request"/>
          <fmt:message var="addButtonText" key="common.button.preorderText" scope="request"/>
          <fmt:message var="addButtonTitle" key="common.button.preorderTitle" scope="request"/>
          <dsp:getvalueof var="availabilityDate" param="availabilityDate"/>
          <c:choose>
            <c:when test="${not empty availabilityDate}">
              <dsp:getvalueof id="availDateId" idtype="java.util.Date" param="availabilityDate"/>
              
              <dsp:getvalueof var="dateFormat" 
                              bean="LocaleTools.userFormattingLocaleHelper.datePatterns.shortWith4DigitYear" />
              
              <fmt:formatDate var="addToCartDate" value="${availDateId}" pattern="${dateFormat}"/>
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
          <%-- SKU/product is available for backorder. Check its availability date and set output variables. --%>
          <dsp:getvalueof id="availabilityType" idtype="java.lang.String" value="backorderable" scope="request"/>
          <fmt:message var="addButtonText" key="common.button.backorderText" scope="request"/>
          <fmt:message var="addButtonTitle" key="common.button.backorderTitle" scope="request"/>
          <dsp:getvalueof var="availabilityDate" param="availabilityDate"/>
          <c:choose>
            <c:when test="${not empty availabilityDate}">
              <dsp:getvalueof id="availDateId" idtype="java.util.Date" param="availabilityDate" scope="request"/>
              
              <dsp:getvalueof var="dateFormat" 
                              bean="LocaleTools.userFormattingLocaleHelper.datePatterns.shortWith4DigitYear" />
                                  
              <fmt:formatDate var="addToCartDate" value="${availDateId}" pattern="${dateFormat}"/>
              <fmt:message var="availabilityMessage" key="common.backorderableUntil" scope="request">
                <fmt:param>
                  <span class="date numerical">${addToCartDate}</span>
                </fmt:param>
              </fmt:message>
            </c:when>
            <c:otherwise>
              <fmt:message var="availabilityMessage" key="common.backorderable" scope="request"/>
            </c:otherwise>
          </c:choose>
        </dsp:oparam>
        <dsp:oparam name="unavailable">
          <%-- SKU is not available. Set output variables only if showUnavailable is true. --%>
          <dsp:getvalueof id="availabilityType" idtype="java.lang.String" value="unavailable" scope="request"/>
          <fmt:message var="availabilityMessage" key="common.temporarilyOutOfStock" scope="request"/>
          <dsp:getvalueof id="showUnavailable" param="showUnavailable"/>
          <c:choose>
            <c:when test="${!empty showUnavailable && showUnavailable == 'true'}">
              <fmt:message var="addButtonText" key="common.button.emailMeInStockText" scope="request"/>
              <fmt:message var="addButtonTitle" key="common.button.emailMeInStockTitle" scope="request"/>
            </c:when>
            <c:otherwise>
              <dsp:getvalueof id="addButtonText" idtype="java.lang.String" value="" scope="request"/>
              <dsp:getvalueof id="addButtonTitle" idtype="java.lang.String" value="" scope="request"/>
            </c:otherwise>
          </c:choose>
        </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/skuAvailabilityLookup.jsp#1 $$Change: 735822 $--%>

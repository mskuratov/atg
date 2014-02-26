<%--
  This gadget renders a 'Back in stock notification' form. It displays an e-mail input field and a submit button.

  Required parameters:
    productId
      ID of unavailable product.
    skuId
      ID of unavailable SKU.

  Optional parameters:
    redirectURL
      If specified, the user will be redirected to this URL on form submit.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/store/inventory/BackInStockFormHandler"/>

  <dsp:getvalueof var="redirectURL" param="redirectURL"/> 
  <dsp:getvalueof id="contextroot" idtype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
  
  <c:url var="errorUrl" value="/browse/notifyMeRequestPopup.jsp">
    <c:param name="skuId"><dsp:valueof param="skuId"/></c:param>
    <c:param name="productId"><dsp:valueof param="productId"/></c:param>
  </c:url>

  <%-- Show Form Errors --%>
  <dsp:include page="/global/gadgets/errorMessage.jsp">
    <dsp:param name="formHandler" bean="BackInStockFormHandler"/>
  </dsp:include>

  <dsp:form method="post" action="${contextroot}/browse/notifyMeConfirmPopup.jsp"
            formid="notifyMe" id="atg_store_notifyMeRequestForm">
    <%-- Form properties, SKU and products that are unavailable. --%>
    <dsp:input bean="BackInStockFormHandler.catalogRefId" type="hidden"
               paramvalue="skuId"/>
    <dsp:input bean="BackInStockFormHandler.productId" type="hidden"
               paramvalue="productId"/>

    <%-- Set the standard URLs --%>
    <dsp:input bean="BackInStockFormHandler.successURL" type="hidden"
               value="${contextroot}/browse/notifyMeConfirmPopup.jsp"/>
    
    <dsp:input bean="BackInStockFormHandler.errorURL" type="hidden"
               value="${errorUrl}"/>

    <%-- Set the noJavascriptSuccessURL, if these URLs are specified the user will be redirected there. --%>
    <c:if test="${not empty redirectURL}">
      <%-- Build success and error URLs based on the passed redirect URL --%>
      <c:url var="redirectSuccessURL" value="${redirectURL}" context="/">
        <c:param name="status" value="emailSent"/>
      </c:url>
  
      <c:url var="redirectErrorURL" value="${redirectURL}" context="/">
        <c:param name="skuId"><dsp:valueof param="skuId"/></c:param>
        <c:param name="status" value="unavailable"/>
      </c:url>
      <dsp:input bean="BackInStockFormHandler.noJavascriptSuccessURL" value="${redirectSuccessURL}" 
                 type="hidden"/>
      <dsp:input bean="BackInStockFormHandler.noJavascriptErrorURL" value="${redirectErrorURL}" 
                 type="hidden"/>
    </c:if>

    <%-- E-mail address input field. --%>
    <ul class="atg_store_basicForm">
      <li class="atg_store_email">
        <label for="atg_store_emailInput" class="required">
          <span><fmt:message key="common.emailAddress"/></span>
          <span class="required"><fmt:message key="common.requiredFields"/></span>
        </label>
        <dsp:input bean="BackInStockFormHandler.emailAddress" type="text" maxlength="255"
                   size="48" beanvalue="/atg/userprofiling/Profile.email" id="atg_store_emailInput"/>
      </li>
    </ul>

    <%-- Submit button. --%>
    <div class="atg_store_formActions">
      <fmt:message var="submitText" key="common.button.submitText"/>
      <fmt:message var="submitTitle" key="common.button.submitTitle"/>
      <span class="atg_store_basicButton tertiary">
        <dsp:input bean="BackInStockFormHandler.notifyMe" type="submit" title="${submitTitle}"
                   value="${submitText}" iclass="atg_store_actionSubmit"/>
      </span>
    </div>
  </dsp:form>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/notifyMeRequest.jsp#2 $$Change: 788278 $ --%>

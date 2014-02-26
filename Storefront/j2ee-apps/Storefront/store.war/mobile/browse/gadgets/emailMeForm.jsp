<%--
  This page renders form for email notification: "When the product is in stock, please let me know".

  Required parameters:
    selectedSkuId
      ID of the SKU that has been selected
    productId
      ID of the product that has been selected

  Optional parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/inventory/BackInStockFormHandler"/>
  <dsp:importbean bean="/atg/store/profile/SessionBean"/>

  <div id="emailMePopup">
    <%-- ========== Form ========== --%>
    <dsp:form formid="emailMeForm" method="post" name="emailMeForm"
              action="${siteContextPath}/browse/gadgets/emailMeFormErrors.jsp" onsubmit="CRSMA.product.emailMeSubmit(event);">
      <%-- ========== Redirection URLs ========== --%>
      <dsp:input bean="BackInStockFormHandler.successURL" type="hidden" value=""/>
      <dsp:input bean="BackInStockFormHandler.errorURL" type="hidden" value=""/>

      <%-- Form properties, SKU and products that are unavailable --%>
      <dsp:input id="emailMeSkuId" bean="BackInStockFormHandler.catalogRefId" type="hidden" paramvalue="selectedSkuId"/>
      <dsp:input bean="BackInStockFormHandler.productId" type="hidden" paramvalue="productId"/>

      <ul class="dataList" role="presentation" onclick="event.stopPropagation();">
        <li><div class="content"><fmt:message key="mobile.notifyMe_whenInStock"/></div></li>
        <li id="emailAddressRow">
          <div class="content">
            <fmt:message key="mobile.common.email" var="hintText"/>
            <dsp:input id="rememberMeEmailAddress" bean="BackInStockFormHandler.emailAddress" type="email"
                       beanvalue="/atg/userprofiling/Profile.email" onclick="event.stopPropagation();"
                       placeholder="${hintText}" aria-label="${hintText}" autocapitalize="off" autocorrect="off"/>
          </div>
          <span class="errorMessage">
            <fmt:message key="mobile.form.validation.invalid"/>
          </span>
        </li>
        <li>
          <div class="content">
            <fmt:message var="privacyTitle" key="mobile.company_privacyAndTerms.pageTitle"/>
            <dsp:a page="/company/terms.jsp" title="${privacyTitle}" class="icon-help email-me"/>
            <dsp:input name="rememberEmail" type="hidden" bean="SessionBean.values.rememberedEmail" value=""/>
            <input id="rememberCheckbox" type="checkbox" name="rememberCheckbox" checked="checked"/>
            <label for="rememberCheckbox" onclick=""><fmt:message key="mobile.notifyMe_rememberForCheckout"/></label>
          </div>
        </li>
      </ul>
      <fmt:message key="mobile.common.button.submitText" var="submitText"/>
      <dsp:input type="hidden" bean="BackInStockFormHandler.notifyMe" value="${submitText}"/>
    </dsp:form>
  </div>

  <div id="emailMeConfirm">
    <ul class="dataList" summary="" role="presentation" datatable="0">
      <li>
        <div class="content"><fmt:message key="mobile.notifyMe_title"/></div>
      </li>
      <li>
        <div class="content">
          <p><fmt:message key="mobile.notifyMe_thankYouMsg"/></p>
        </div>
      </li>
    </ul>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/browse/gadgets/emailMeForm.jsp#2 $$Change: 742374 $ --%>

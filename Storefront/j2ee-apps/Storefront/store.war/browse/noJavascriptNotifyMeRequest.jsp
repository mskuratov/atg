<%--
  This gadget renders a 'Notify me when product is available' dialog to be displayed instead of picker, if the product is not available.
  It's a JavaScript-disabled version of this dialog.

  Required parameters:
    productId
      Specifies an unavailable product of interest.
    skuId
      Specifies an unavailable SKU of interest.
    redirectURL
      The user will be redirected to this URL, when e-mail is specified.
--%>

<dsp:page>
  <dsp:getvalueof var="redirectURL" param="redirectURL"/>
  <dsp:getvalueof var="skuId" param="skuId"/>
  <dsp:getvalueof var="productId" param="productId"/>

  <div id="atg_store_noJsNotifyMeRequest">
    <crs:messageWithDefault key="browse_notifyMeRequestPopup.title"/>
    <c:if test="${!empty messageText}">
      <h2 class="title">
        ${messageText}
      </h2>
    </c:if>
    <crs:messageWithDefault key="browse_notifyMeRequestPopup.intro"/>
    <c:if test="${!empty messageText}">
      <p>
        ${messageText}
      </p>
    </c:if>

    <%-- Include all necessary input fields. --%>
    <dsp:include page="/browse/gadgets/notifyMeRequest.jsp">
      <dsp:param name="redirectURL" param="redirectURL"/>
      <dsp:param name="skuId" param="skuId"/>
      <dsp:param name="productId" param="productId"/>
    </dsp:include>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/noJavascriptNotifyMeRequest.jsp#1 $$Change: 735822 $--%>

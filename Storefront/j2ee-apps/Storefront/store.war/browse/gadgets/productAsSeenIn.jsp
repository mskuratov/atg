<%--
  This page displays a link to the As Seen In page with the text of the As Seen In Source,
  if the product has an As Seen In object associated with it.

  Required parameters:
    product
      Specifies a product to be processed.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>
  
  <dsp:getvalueof id="product" param="product"/>
  <dsp:getvalueof var="productAsSeenIn" param="product.asSeenIn"/>

  <%-- Display page contents only if we have an 'AsSeenIn' item associated with the product. --%>
  <c:if test="${not empty productAsSeenIn}">
    <div id="atg_store_productAsSeenIn">
      <dsp:getvalueof var="productId" vartype="java.lang.String" param="product.repositoryId"/>
      <dsp:getvalueof var="asSeenInSource" vartype="java.lang.String" param="product.asSeenIn.source"/>
      <dsp:getvalueof var="asSeenInDate" vartype="java.util.Date" param="product.asSeenIn.date"/>
      <c:set var="pageUrl" value="../asSeenIn.jsp#"/>
    
      <dsp:getvalueof var="dateFormat" 
                      bean="LocaleTools.userFormattingLocaleHelper.datePatterns.shortWith4DigitYear" />

      <%-- Link itself. --%>
      <dsp:a page="${pageUrl}${productId}">
        <fmt:message key="browse_productAsSeenIn.seenIn">
          <fmt:param value="${asSeenInSource}"/>
          <fmt:param>
            <fmt:formatDate value="${asSeenInDate}" pattern="${dateFormat}"/>
          </fmt:param>
        </fmt:message>
      </dsp:a>
    </div>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/productAsSeenIn.jsp#1 $$Change: 735822 $ --%>

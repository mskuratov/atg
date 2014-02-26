<%--
  This gadget returns ISO 4217 currency code and currency symbol corresponding to price list locale
  (Profile.priceList.locale).
  If the "Profile.priceList.locale" returns empty locale, the "RequestLocale.locale" is used.
  Data are returned in the following request-scoped variables:
    currencyCode
      The currency code (ISO 4217)
    currencySymbol
      The currency symbol
--%>
<dsp:page>
  <dsp:getvalueof var="locale" vartype="java.lang.String" param="/atg/userprofiling/Profile.priceList.locale"/>
  <c:if test="${empty locale}">
    <dsp:getvalueof var="locale" vartype="java.lang.String" bean="/atg/dynamo/servlet/RequestLocale.locale"/>
  </c:if>
  <c:set var="currencyCode" scope="request" value=""/>
  <c:set var="currencySymbol" scope="request" value=""/>  

  <%-- Calculate currency code for price list locale --%>
  <c:if test="${not empty locale}">
    <dsp:droplet name="/atg/commerce/pricing/CurrencyCodeDroplet">
      <dsp:param name="locale" value="${locale}"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="currencyCode" vartype="java.lang.String" param="currencyCode" scope="request"/>

        <%--
          The code below is because the "CurrencyCodeDroplet" returns currency mnemonic for some currencies,
          and not a currency sign.
          For example, for euro it returns EUR, instead of the euro sign.
        --%>
        <c:choose>
          <c:when test="${currencyCode == 'USD'}">
            <c:set var="currencySymbol" value="$" scope="request"/>
          </c:when>
          <c:when test="${currencyCode == 'EUR'}">
            <%-- NOTE The space before euro sign is because the euro sign follows the price --%>
            <c:set var="currencySymbol" value=" &euro;" scope="request"/>
          </c:when>
          <c:otherwise>
            <c:set var="currencySymbol" value="" scope="request"/>
          </c:otherwise>
        </c:choose>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/global/util/currencyCode.jsp#3 $$Change: 793440 $--%>

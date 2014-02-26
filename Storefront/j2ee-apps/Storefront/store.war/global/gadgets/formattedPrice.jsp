<%--
  This gadget renders a price accordingly to the locale specified.

  Required parameters:
    price
      Price to be formatted.

  Optional parameters:
    priceListLocale
      Specifies a locale in which to format the price (as string).
      If not specified, locale will be taken from profile price list (Profile.priceList.locale).
    saveFormattedPrice
      If true, the price rendered will not be displayed to user,
      it will be saved into "formattedPrice" request-scoped variable instead.
--%>
<dsp:page>
  <dsp:getvalueof var="price" vartype="java.lang.Double" param="price"/>
  <dsp:getvalueof var="saveFormattedPrice" param="saveFormattedPrice"/>

  <%-- Set locale as the "Default price list locale", if not specified --%>
  <dsp:getvalueof var="locale" vartype="java.lang.String" param="priceListLocale"/>
  <c:if test="${empty locale}">
    <dsp:getvalueof var="locale" vartype="java.lang.String" bean="/atg/userprofiling/Profile.priceList.locale"/>
  </c:if>

  <%-- Format price --%>
  <dsp:droplet name="/atg/dynamo/droplet/CurrencyFormatter">
    <dsp:param name="currency" value="${price}"/>
    <dsp:param name="locale" value="${locale}"/>
    <dsp:oparam name="output">
      <c:choose>
        <c:when test="${saveFormattedPrice}">
          <dsp:getvalueof var="formattedPrice" scope="request" vartype="java.lang.String" param="formattedCurrency"/>
          <c:if test="${empty formattedPrice}">
            <c:set var="formattedPrice" scope="request" value="${price}"/>
          </c:if>
        </c:when>
        <c:otherwise>
          <dsp:valueof param="formattedCurrency">${price}</dsp:valueof>
        </c:otherwise>
      </c:choose>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/formattedPrice.jsp#2 $$Change: 742374 $--%>

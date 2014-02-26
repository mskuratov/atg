<%--
  This page displays the subheader for the "My Account" pages.
  This subheader specifies both the text and URL for the left and right cell.
  Other than that, it passes the parameters it receives to "subheader.jsp".

  Page includes:
    /mobile/includes/subheader.jsp - Subheader core

  Required parameters:
    None

  Optional parameters:
    centerText
      Text specified in the center container
    centerURL
      Url to link in the center cell. If no url is specified, a link is not provided
    rightText
      Text specified in the right container
    highlight
      Determines which part should be highlighted. Must be one of "left", "center", "right"

  NOTE: There is no option for a "rightURL" because it would not make sense to have one
        in the "My Account" section.
--%>
<dsp:page>
  <dsp:getvalueof var="mobileStorePrefix" bean="/atg/store/StoreConfiguration.mobileStorePrefix"/>

  <fmt:message var="myaccountTitle" key="mobile.myaccount_myAccountMenu.myAccount"/>

  <dsp:include page="${mobileStorePrefix}/includes/subheader.jsp">
    <dsp:param name="leftText" value="${myaccountTitle}"/>
    <dsp:param name="leftURL" value="${mobileStorePrefix}/myaccount/profile.jsp"/>

    <dsp:param name="centerText" param="centerText"/>
    <dsp:param name="centerURL" param="centerURL"/>

    <dsp:param name="rightText" param="rightText"/>
    <dsp:param name="rightURL" value=""/>

    <dsp:param name="highlight" param="highlight"/>
  </dsp:include>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/myaccount/gadgets/subheaderAccounts.jsp#2 $$Change: 742374 $--%>

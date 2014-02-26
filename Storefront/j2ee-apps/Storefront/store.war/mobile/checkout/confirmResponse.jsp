<%--
  This page is displayed after the user successfully places an order.

  Page includes:
    /mobile/global/gadgets/registration.jsp - Registration form

  Required parameters:
    None

  Optional parameters:
    registrationErrors
      ???
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>
  <dsp:importbean bean="/atg/userprofiling/Profile" var="profilebean"/>

  <dsp:getvalueof var="registrationErrors" param="registrationErrors"/>

  <fmt:message var="pageTitle" key="mobile.checkout_confirm.title"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <h2><fmt:message key="mobile.checkout_title.orderPlaced"/></h2>

    <dsp:droplet name="ProfileSecurityStatus">
      <dsp:oparam name="anonymous">
        <%-- Confirmation page for anonimous user --%>
        <div class="dataContainer">
          <div align="center" class="actionConfirmation">
            <c:choose>
              <c:when test="${not empty profilebean.email}">
                <fmt:message key="mobile.checkout_confirmResponse.emailText"/>
                <br/>
                <strong><c:out value="${profilebean.email}"/></strong>
              </c:when>
              <c:otherwise>
                <fmt:message key="mobile.checkout_confirmResponse.omsOrderId"/>
              <br/>
              <strong><dsp:valueof bean="ShoppingCart.last.id"/></strong>
              </c:otherwise>
            </c:choose>
          </div>
          <br/>

          <ul class="dataList">
            <li class="icon-ArrowDown ${registrationErrors == 'true' ? 'turn' : ''}" onclick="CRSMA.global.loginPageClick('regRow', this);">
                <div class="content">
                 <a href="javascript:void(0);" onclick="">
                  <span class="parentSpan">
                    <span class="title">
                      <fmt:message key="mobile.checkout_confirmResponse.registerTitle"/>
                    </span>
                    <span class="rightContentContainer">
                      <div class="arrow"></div>
                    </span>
                  </span>
                 </a>
               </div>
            </li>
            <li id="regRow" class="${registrationErrors != 'true' ? 'hidden ' : ''}expandable">
              <%--
                Propagate Locale from request (RequestLocale) to "profile.jsp".
                This is needed to set user's language after registration as he set it while being anonimous.
              --%>
              <dsp:getvalueof var="currentLocale" vartype="java.lang.String" bean="/atg/dynamo/servlet/RequestLocale.localeString"/>
              <dsp:include page="${mobileStorePrefix}/global/gadgets/registration.jsp">
                <dsp:param name="successUrl" value="../myaccount/profile.jsp?locale=${currentLocale}"/>
              </dsp:include>
            </li>
          </ul>
          <br/>
        </div>
      </dsp:oparam>
      <dsp:oparam name="default">
        <%-- Confirmation page for registered user --%>
        <div align="center" class="actionConfirmation">
          <fmt:message key="mobile.checkout_confirmResponse.emailText"/>
          <br/>
          <strong><c:out value="${profilebean.email}"/></strong>
        </div>
        <br/>
        <div class="dataContainer">
          <ul class="dataList">
            <li>
              <dsp:a page="${mobileStorePrefix}/myaccount/orderDetail.jsp" class="icon-ArrowRight">
                <dsp:param name="orderId" bean="ShoppingCart.last.id"/>
                <span class="content"><fmt:message key="mobile.myaccount_checkout_confirmation_viewOrder"/></span>
              </dsp:a>
            </li>
            <li>
              <dsp:a page="${mobileStorePrefix}/myaccount/myOrders.jsp" class="icon-ArrowRight">
                <span class="content"><fmt:message key="mobile.myaccount_checkout_confirmation_viewOrders"/></span>
              </dsp:a>
            </li>
          </ul>
        </div>
        <br/>
      </dsp:oparam>
    </dsp:droplet>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/checkout/confirmResponse.jsp#3 $$Change: 788278 $ --%>

<%-- 
  This gadget displays the Checkout Progress bar.
  We provide the following bar designs:
    - Login
    - Register
    - 1.Shipping 2.Billing 3.Confirm

  Required parameters:
    currentStage
      Specifies a checkout progress stage.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:getvalueof var="currentStage" vartype="java.lang.String" param="currentStage" />
  <%-- Choose, which bar we should render. --%>
  <c:choose>
    <c:when test="${currentStage == 'login'}">
      <%-- Simple login bar. --%>
      <ol class="atg_store_checkoutProgress">
        <li class="login current">
          <span class="atg_store_checkoutStageName">
            <fmt:message key="checkout_checkoutProgress.login"/>
          </span>
        </li>
      </ol>
    </c:when>
    <c:when test="${currentStage == 'register'}">
      <%-- Simple registration bar. --%>
      <ol class="atg_store_checkoutProgress">
        <li class="register current">
          <span class="atg_store_checkoutStageName">
            <fmt:message key="checkout_checkoutProgress.register"/>
          </span>
        </li>
      </ol>
    </c:when>
    <c:when test="${currentStage == 'shipping' || currentStage == 'billing' || currentStage == 'confirm'}">
      <%-- Shipping, billing and confirmation bars looks similarly, display them together. --%>
      <ol class="atg_store_checkoutProgress">
        <li class="shipping${currentStage == 'shipping' ? ' current' : ''}">
          <span class="atg_store_stageNumber">1</span>
          <span class="atg_store_checkoutStageName">
            <fmt:message key="checkout_checkoutProgress.shipping"/>
          </span>
        </li>

        <li class="billing${currentStage == 'billing' ? ' current' : ''}">
          <span class="atg_store_stageNumber">2</span>
          <span class="atg_store_checkoutStageName">
            <fmt:message key="checkout_checkoutProgress.billing"/>
          </span>
        </li>

        <li class="confirm${currentStage == 'confirm' ? ' current' : ''}">
          <span class="atg_store_stageNumber">3</span>
          <span class="atg_store_checkoutStageName">
            <fmt:message key="checkout_checkoutProgress.confirm"/>
          </span>
        </li>
      </ol>
    </c:when>
  </c:choose>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/checkoutProgress.jsp#2 $$Change: 788278 $--%>
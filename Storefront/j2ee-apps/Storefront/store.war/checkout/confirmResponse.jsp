<%--
  This is the final 'Checkout Success' page. It checks, if current user is already registered or not. 
  If the user is not registered, this page will display the Register pane with all necessary input fields.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>

  <%-- Check, if current user is not registered (or transient). --%>
  <dsp:getvalueof var="transient" vartype="java.lang.String" bean="Profile.transient"/>
  
  <c:if test='${transient == "true"}'>
    <c:set var="contentClass" value="atg_store_confirmAndRegister"/>    
    <c:set var="bodyClass" value="Register"/>
  </c:if>

  <crs:pageContainer divId="atg_store_cart" index="false" follow="false"
                     bodyClass="atg_store_confirmResponse${bodyClass} atg_store_checkout" contentClass="${contentClass}">
    <jsp:body>
      <div id="atg_store_checkout">
        
        <fmt:message key="checkout_title.orderPlaced" var="title"/>
        
        <crs:checkoutContainer currentStage="success" title="${title}">
          <jsp:body>
            <div id="atg_store_orderConrifmResponse" class="atg_store_main atg_store_checkoutOption">
              
              <%-- 
                Offer registration for anonymous users, display 'Success' message for others.
                Check Profile's security status for that.
                
                Open parameters:
                  loggedIn
                    User is already logged in with login/password.
                  default
                    User is anonymous or logged in from cookie.
                --%>
              <dsp:droplet name="ProfileSecurityStatus">
                <dsp:oparam name="loggedIn">
                  <dsp:include page="gadgets/confirmResponse.jsp"/>
                </dsp:oparam>
                <dsp:oparam name="default">
                  <dsp:include page="gadgets/confirmAndRegister.jsp"/>
                  <c:set var="anonymous" value="true"/>
                </dsp:oparam>
              </dsp:droplet>  
            </div>

            <%-- display benefits section outside of "atg_store_main" div for anonymous users --%>
            <c:if test="${anonymous}">
              <dsp:include page="/myaccount/gadgets/benefits.jsp"/>
            </c:if>
            
          </jsp:body>
        </crs:checkoutContainer>
      </div>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/confirmResponse.jsp#2 $$Change: 788278 $--%>

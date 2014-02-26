<%--
  This page shows pop-up with instructions what should be entered in credit card's security code field.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>
  <crs:popupPageContainer divId="atg_store_whatsThisPopup"
                          titleKey="checkout_whatsThisPopup.title">
    <jsp:body>
      <div id="atg_store_whatsThisPopupContent">
        
        <p><fmt:message key="checkout_whatsThisPopup.intro"/></p>
        <p><fmt:message key="checkout_whatsThisPopup.instructions"/></p>
        
        <div class="atg_Store_visa">
          
          <h3>
            <fmt:message key="common.visa"/>, <fmt:message key="common.masterCard"/>, <fmt:message key="common.discover"/>
          </h3>
          
          <p>
            <fmt:message key="checkout_whatsThisPopup.instructions.visa"/>
          </p>
         
         <img alt="<fmt:message key='common.visa'/>" src="/crsdocroot/content/images/storefront/cc_visamcdisc.png" />
        
        </div>
        <div class="atg_store_amex">
          
          <h3>
            <fmt:message key="common.americanExpress"/>
          </h3>
          <p>
            <fmt:message key="checkout_whatsThisPopup.instructions.americanExpress"/>
          </p>
          
          <img alt="<fmt:message key='common.americanExpress'/>" src="/crsdocroot/content/images/storefront/cc_amex.png" />
          
      </div>
    </jsp:body>    
  </crs:popupPageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/whatsThisPopup.jsp#2 $$Change: 788278 $ --%>

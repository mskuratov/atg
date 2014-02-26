<%-- 
  This page displays Shipping Rates of the Merchant 

  Required Parameters:
    None
    
  Optional Parameters:
    None

--%>
<dsp:page>
  <crs:pageContainer divId="atg_store_company" 
                     bodyClass="atg_store_shipping atg_store_company atg_store_leftCol"
                     titleKey=""
                     selpage="shipping">
    <div id="atg_store_contentHeader">
      <h2 class="title">
        <fmt:message key="company_shipping.title"/>
      </h2>
    </div>
    <div class="atg_store_main">
      <crs:outMessage key="company_shipping.text" enablePreview="${true}"/>
    </div>
    
    <%-- 
      This page renders navigation links to ancillary pages related to
      company policies, help and contact information 
    --%>
    <div class="atg_store_companyNavigation aside">
      <dsp:include page="/company/gadgets/navigationPanel.jsp">
        <dsp:param name="selpage" value="shipping"/>
      </dsp:include>
    </div>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/company/shipping.jsp#2 $$Change: 788278 $--%>
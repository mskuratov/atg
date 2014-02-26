<%-- 
  This page is the company's "Return Policy" page 

  Required Parameters:
    None
    
  Optional Parameters:
    None

--%>
<dsp:page>
  <crs:pageContainer divId="atg_store_company" 
                     bodyClass="atg_store_returns atg_store_leftCol atg_store_company"
                     titleKey=""
                     selpage="returns">
    <crs:getMessage var="storeName" key="common.storeName"/>
    <div id="atg_store_contentHeader">
      <h2 class="title">
        <fmt:message key="company_returnPolicyPopup.title"/>
      </h2>
    </div>
    <div class="atg_store_main">
      <p>
        <crs:outMessage key="company_returnPolicyPopup.text" storeName="${storeName}" enablePreview="${true}"/>
      </p>
    </div>

    <%-- 
      This page renders navigation links to ancillary pages related to
      company policies, help and contact information 
    --%>
    <div class="atg_store_companyNavigation aside">
      <dsp:include page="/company/gadgets/navigationPanel.jsp">
        <dsp:param name="selpage" value="returns"/>
      </dsp:include>
    </div>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/company/returns.jsp#2 $$Change: 788278 $--%>

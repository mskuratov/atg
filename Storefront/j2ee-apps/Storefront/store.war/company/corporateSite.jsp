<%-- 
  This page is the company's "Corporate Site" page
  Page contains link to company's corporate site. 

  Required Parameters:
    None
    
  Optional Parameters:
    None

--%>
<dsp:page>
  <crs:pageContainer divId="atg_store_company" 
                     bodyClass="atg_store_corporate atg_store_leftCol atg_store_company"
                     titleKey="">
    <crs:getMessage var="corporateSiteLink" key="common.corporateSiteLink"/>

    <div id="atg_store_contentHeader">
      <h2 class="title">
        <fmt:message key="company.corp_site.corpSite"/>
      </h2>
    </div>
    
    <div class="atg_store_main">
      <p>
        <crs:outMessage key="company.corp_site.clickToGoToCorpSite" 
                        corporateSiteLink="${corporateSiteLink}"
                        enablePreview="${true}"/>
      </p>
    </div>
    
    <%-- 
      This page renders navigation links to ancillary pages related to
      company policies, help and contact information 
    --%>
    <div class="atg_store_companyNavigation aside">
      <dsp:include page="/company/gadgets/navigationPanel.jsp">
        <dsp:param name="selpage" value="corpSite"/>
      </dsp:include>
    </div>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/company/corporateSite.jsp#2 $$Change: 788278 $--%>


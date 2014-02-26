<%-- 
  This page is the company's "About Us" page.
  General information about the company. 

  Required Parameters:
    None

  Optional Parameters:
    None

--%>
<dsp:page>
  <crs:pageContainer divId="atg_store_company"
                     bodyClass="atg_store_aboutUs atg_store_leftCol atg_store_company">
    <crs:getMessage var="storeName" key="common.storeName"/>

    <div id="atg_store_contentHeader">
      <h2 class="title">
        <crs:outMessage key="company_aboutUs.title"/>
      </h2>
    </div>  

    <%-- Information about company --%>
    <div class="atg_store_main">
      <h3>
        <fmt:message key="company_aboutUs.ourHistory"/>
      </h3>
      <p>
        <crs:outMessage key="company_aboutUs.aboutUs" storeName="${storeName}" enablePreview="${true}"/>
      </p>
    </div>
    
    <%-- 
      This page renders navigation links to ancillary pages related to
      company policies, help and contact information 
    --%>  
    <div class="atg_store_companyNavigation aside">
      <dsp:include page="/company/gadgets/navigationPanel.jsp">
        <dsp:param name="selpage" value="aboutUs"/>
      </dsp:include>
    </div>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/company/aboutUs.jsp#2 $$Change: 788278 $--%>

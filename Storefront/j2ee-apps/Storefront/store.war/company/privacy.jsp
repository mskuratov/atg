<%-- 
  This page displays company's Privacy Policy. 

  Required Parameters:
    None
    
  Optional Parameters:
    None

--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/droplet/StoreText"/>
  <crs:pageContainer divId="atg_store_company"
                     bodyClass="atg_store_privacy atg_store_leftCol atg_store_company"
                     titleKey=""
                     index="false"
                     follow="false"
                     selpage="privacy">
  <crs:getMessage var="storeName" key="common.storeName"/>
  
  <%-- Page title --%>    
  <div id="atg_store_contentHeader">
    <h2 class="title">
      <fmt:message key="company_privacy.title"/>
    </h2>
  </div>

  <div class="atg_store_main">      
    <%-- 
       Performs a look up in the repository to obtain list
       of localized resource text messages
                    
       Input parameters:
         key
           the key code to use when looking up the resources list in the repository
         storeName
           name of store to be put in message template in the resources list          
                
       Output parameters:
         output
           this parameter is rendered for once for each element in the resources list
    --%>     
    <dsp:droplet name="StoreText">
      <dsp:param name="key" value="privacyPolicy"/>
      <dsp:param name="storeName" value="${storeName}"/>
        
      <dsp:oparam name="output">
        <dsp:getvalueof var="message" param="message"/>
        <dsp:getvalueof var="item" param="item"/>
        <preview:repositoryItem item="${item}">
          <p><c:out value="${message}" escapeXml="false"/></p>
        </preview:repositoryItem>
      </dsp:oparam>       
    </dsp:droplet>    
  </div>
    
    <%-- 
      This page renders navigation links to ancillary pages related to
      company policies, help and contact information 
    --%>
    <div class="atg_store_companyNavigation aside">
      <dsp:include page="/company/gadgets/navigationPanel.jsp">
        <dsp:param name="selpage" value="privacy"/>
      </dsp:include>
    </div>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/company/privacy.jsp#2 $$Change: 788278 $--%>

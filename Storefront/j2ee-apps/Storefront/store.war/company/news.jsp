<%-- 
  This page contains the latest news about the company. 

  Required Parameters:
    None
    
  Optional Parameters:
    None

--%>
<dsp:page>  
  <dsp:importbean bean="/atg/store/droplet/StoreText"/>
  
  <crs:pageContainer divId="atg_store_company"
                     bodyClass="atg_store_news atg_store_leftCol atg_store_company">
    <div id="atg_store_contentHeader">
      <h2 class="title">
        <fmt:message key="company_news.title"/>
      </h2>
    </div>
    <div class="atg_store_main">
      <crs:getMessage var="storeName" key="common.storeName"/>
      <c:set var="tag" value="news"/>
      <%-- Droplet displays news messages for specified store. --%>
      <dsp:droplet name="StoreText">
        <dsp:param name="key" value="${tag}"/>
        <dsp:param name="storeName" value="${storeName}"/>
        
        <dsp:oparam name="output">
          <dsp:getvalueof var="message" param="message"/>
          <dsp:getvalueof var="item" param="item"/>
          <preview:repositoryItem item="${item}">
            <span><c:out value="${message}" escapeXml="false"/></span>
          </preview:repositoryItem>
        </dsp:oparam>
        
        <dsp:oparam name="empty">
          <crs:getMessage var="noNews" key="company.news.noNews"/>
          <p>
            <c:out value="${noNews}" escapeXml="false"/>
          </p>
        </dsp:oparam>
      </dsp:droplet>
    </div>

    <%-- 
      This page renders navigation links to ancillary pages related to
      company policies, help and contact information 
    --%>
    <div class="atg_store_companyNavigation aside">
      <dsp:include page="/company/gadgets/navigationPanel.jsp">
        <dsp:param name="selpage" value="news"/>
      </dsp:include>
    </div>  
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/company/news.jsp#2 $$Change: 788278 $--%>

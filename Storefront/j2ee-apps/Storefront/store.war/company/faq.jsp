<%-- 
  This page is the company's "FAQ" page.

  Required Parameters:
    None
    
  Optional Parameters:
    None

--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/droplet/StoreText"/>
  
  <crs:pageContainer divId="atg_store_company"
                     bodyClass="atg_store_faq atg_store_leftCol atg_store_company"
                     titleKey="">
                     
  <crs:getMessage var="storeName" key="common.storeName"/>
  
  <%-- Page title --%>    
  <div id="atg_store_contentHeader">
    <h2 class="title">
      <fmt:message key="company_help.title"/>
    </h2>
  </div>

  <div class="atg_store_main">    
    <h3><fmt:message key="company_help.subTitle"/></h3>
    
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
      <dsp:param name="key" value="faq-list"/>
      <dsp:param name="storeName" value="${storeName}"/>
        
        <dsp:oparam name="output">
          <%-- key of the returned item --%>
          <dsp:getvalueof var="itemKey" param="item.key"/>
           
          <%-- 
            Each item is a list of two messages: question(first item) and 
            answer(second item). Use the droplet one more time to get this messages.      
          --%>   
          <dsp:droplet name="StoreText">
            <dsp:param name="key" value="${itemKey}"/>
            <dsp:param name="storeName" value="${storeName}"/>
          
            <dsp:oparam name="output">               
              <dsp:getvalueof var="index" param="index"/>
              <dsp:getvalueof var="message" param="message"/>
              <dsp:getvalueof var="item" param="item"/>
            
             <%-- 
               Use different formats for question(first item in the list) 
               and answer(second item in the list)
             --%>
            <preview:repositoryItem item="${item}">
              <c:choose>
                <c:when test="${index == 0}">
                  <h4><c:out value="${message}" escapeXml="false"/></h4>
                </c:when>
                <c:otherwise>
                  <p><c:out value="${message}" escapeXml="false"/></p>
                </c:otherwise>
              </c:choose>
            </preview:repositoryItem>
          </dsp:oparam>         
        </dsp:droplet>             
      </dsp:oparam>       
    </dsp:droplet>        
   </div>
    
    <%-- 
      This page renders navigation links to ancillary pages related to
      company policies, help and contact information 
    --%>
    <div class="atg_store_companyNavigation aside">
      <dsp:include page="/company/gadgets/navigationPanel.jsp">
        <dsp:param name="selpage" value="faq"/>
      </dsp:include>
    </div>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/company/faq.jsp#2 $$Change: 788278 $--%>
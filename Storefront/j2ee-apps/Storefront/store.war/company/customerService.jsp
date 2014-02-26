<%-- 
  This page is the company's "customer service" page
  Page contains company's contact information
  (Phone, fax, mail, e-mail).  

  Required Parameters:
    None
    
  Optional Parameters:
    None

--%>
<dsp:page>
  
  <dsp:importbean bean="/atg/store/droplet/StoreText"/>

  <crs:pageContainer divId="atg_store_company"
                     bodyClass="atg_store_contact atg_store_leftCol atg_store_company"
                     index="false" follow="false"
                     selpage="customerService">
    <%-- Page title --%>                 
    <div id="atg_store_contentHeader">
      <h2 class="title">
        <fmt:message key="company.gadget.customer_service.title"/>
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
        <dsp:param name="key" value="customerService"/>
        <dsp:param name="storeName" value="${storeName}"/>
                 
        <dsp:oparam name="output">
          <%-- key of the returned item --%>
          <dsp:getvalueof var="itemKey" param="item.key"/>
          <%-- index of return item in the list --%>
          <dsp:getvalueof var="itemIndex" param="index"/>
          
          <%-- 
            Each item is a list of two messages: title of customer service(first item)
            and the content(second item). Use the droplet one more time to get this messages.      
          --%>  
          <dsp:droplet name="StoreText">
            <dsp:param name="key" value="${itemKey}"/>
            <dsp:param name="storeName" value="${storeName}"/>
            
            <dsp:oparam name="output">
              <dsp:getvalueof var="index" param="index"/>         
              <dsp:getvalueof var="message" param="message"/>         
                
              <%-- Use different formats for title messages and content messages--%>
              <c:choose>
                <c:when test="${index == 0}">
                  <h3><c:out value="${message}" escapeXml="false"/></h3>
                </c:when>
                <c:otherwise>
                  <p><c:out value="${message}" escapeXml="false"/></p>
                </c:otherwise>
              </c:choose>         
            </dsp:oparam>         
          </dsp:droplet>   
          <%-- 
            Customer service byPhone is first in the list of services.
            If customer service is byPhone include gadgets/clickToCallLink.jsp
          --%>  
          <c:choose>               
            <c:when test="${itemIndex == 0}">
              <dsp:include page="/navigation/gadgets/clickToCallLink.jsp">
                <dsp:param name="pageName" value="contactUs"/>
              </dsp:include>
            </c:when>
          </c:choose>          
        </dsp:oparam>       
      </dsp:droplet>  
    </div>    
   
    <%-- 
      This page renders navigation links to ancillary pages related to
      company policies, help and contact information 
    --%>    
    <div class="atg_store_companyNavigation aside">
      <dsp:include page="/company/gadgets/navigationPanel.jsp">
        <dsp:param name="selpage" value="customerService"/>
      </dsp:include>
    </div>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/company/customerService.jsp#1 $$Change: 735822 $--%>
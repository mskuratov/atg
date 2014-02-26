<%-- 
  This is the company's "Employment" page that gives information 
  about Employment opportunities/benefits.  

  Required Parameters:
    None
    
  Optional Parameters:
    None
 
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/droplet/StoreText"/>
  
  <crs:pageContainer divId="atg_store_company"
                     bodyClass="atg_store_careers atg_store_leftCol atg_store_company"
                     index="false"
                     follow="false">
   <crs:getMessage var="storeName" key="common.storeName"/>
    
   <%-- Page title --%>    
   <div id="atg_store_contentHeader">
     <h2 class="title">
       <fmt:message key="company_employment.title"/>
     </h2>
   </div>

   <div class="atg_store_main">
     <h3>
       <crs:outMessage key="company_employment.welcome" storeName="${storeName}"/>
     </h3>
     
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
       <dsp:param name="key" value="employmentPurpose"/>
       <dsp:param name="storeName" value="${storeName}"/>
       
       <dsp:oparam name="output">
         <%-- key of the returned item --%> 
         <dsp:getvalueof var="itemKey" param="item.key"/>   
                 
          <%-- 
            Some items are lists of messages. Using the droplet one more time 
            to get this messages or the message from the item.      
          --%>          
         <dsp:droplet name="StoreText">
           <dsp:param name="key" value="${itemKey}"/>
           <dsp:param name="storeName" value="${storeName}"/>
          
           <dsp:oparam name="output">
             <%-- index of return item in the list--%>
             <dsp:getvalueof var="index" param="index"/>
             <%-- index of return item in the list --%>
             <dsp:getvalueof var="message" param="message"/>
             <%-- size of the list --%>
             <dsp:getvalueof var="size" param="size"/>
             <%-- number of returned item in the list --%>
             <dsp:getvalueof var="count" param="count"/>
             <%-- store text item --%>
             <dsp:getvalueof var="item" param="item"/>
            
             <c:choose>
               <c:when test="${index == 0}">
                 <preview:repositoryItem item="${item}">
                   <p><c:out value="${message}" escapeXml="false"/></p>               
                 </preview:repositoryItem>
               </c:when>
               <c:otherwise>  
                 <%-- 
                   If index not 0, it mean we have a list. 
                   Putting correct html tags in the begging and at the end of the list. 
                 --%>       
                 <c:if test="${count == 2}"> 
                   <ul>
                 </c:if>    
                 <preview:repositoryItem item="${item}">
                   <li><c:out value="${message}" escapeXml="false"/></li>   
                 </preview:repositoryItem>
                 <c:if test="${count == size}">
                   </ul>
                 </c:if>                           
               </c:otherwise>
             </c:choose>        
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
        <dsp:param name="selpage" value="employment"/>
      </dsp:include>
    </div>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/company/employment.jsp#2 $$Change: 788278 $--%>

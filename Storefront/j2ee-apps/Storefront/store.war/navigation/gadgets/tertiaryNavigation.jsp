<%--
  This page appears in the bottom of every page. It renders the navigation menu
  at the bottom of the page and contains links to some top-level pages. A user
  can click a link on this navigation menu and will be directed to the desired
  page.
  
  Required Parameters:  
    None
  
  Optional Parameters:
    None
--%>
<dsp:page> 
  <div id="atg_store_tertiaryNavigation">
     <ul>
     
        <%-- 'About Us' link --%>
        <li>
          <fmt:message var="linkText" key="navigation_tertiaryNavigation.aboutUs"/>
          <dsp:a page="/company/aboutUs.jsp" title="${linkText}">            
            <c:out value="${linkText}"/>
          </dsp:a>
        </li>

        <%-- 'Stores' link --%>
        <li>
          <fmt:message var="linkText" key="navigation_tertiaryNavigation.storeLocator"/>
          <dsp:a page="/company/stores.jsp" title="${linkText}">            
            <c:out value="${linkText}"/>
          </dsp:a>
        </li>
        
        <%-- 'Corporate Site' link --%>
        <li>
          <fmt:message var="linkText" key="navigation_tertiaryNavigation.corporateSite"/>
          <dsp:a page="/company/corporateSite.jsp" title="${linkText}">            
            <c:out value="${linkText}"/>
          </dsp:a>
        </li>
        
        <%-- 'Careers' link --%>
         <li>
           <fmt:message var="linkText" key="navigation_tertiaryNavigation.careers"/>
           <dsp:a page="/company/employment.jsp" title="${linkText}">             
             <c:out value="${linkText}"/>
           </dsp:a>
         </li>
         
        <%-- 'News' link --%>
        <li>
          <fmt:message var="linkText" key="navigation_tertiaryNavigation.news"/>
          <dsp:a page="/company/news.jsp" title="${linkText}">            
            <c:out value="${linkText}"/>
          </dsp:a>
        </li>
        
        <%-- 'As Seen In' link --%>
        <li>
          <fmt:message var="linkText" key="common.foot.seen"/>
          <dsp:a page="/browse/asSeenIn.jsp" title="${linkText}">            
            <c:out value="${linkText}"/>
            </dsp:a>
        </li>
 
        <%-- 'FAQ' link --%>
         <li>
           <fmt:message var="linkText" key="navigation_tertiaryNavigation.faq"/>
           <dsp:a page="/company/faq.jsp" title="${linkText}">             
             <c:out value="${linkText}"/>
           </dsp:a>
         </li>
         
        <%-- 'Privacy' link --%>
        <li>
          <fmt:message var="linkText" key="navigation_tertiaryNavigation.privacy"/>
          <dsp:a page="/company/privacy.jsp" title="${linkText}">            
            <c:out value="${linkText}"/>
          </dsp:a>
        </li>      
      
        <%-- 'Terms' link --%>
        <li>
          <fmt:message var="linkText" key="navigation_tertiaryNavigation.terms"/>
          <dsp:a page="/company/terms.jsp" title="${linkText}">            
            <c:out value="${linkText}"/>
          </dsp:a>
        </li>

        <%-- 'Shipping' link --%>
        <li>
          <fmt:message var="linkText" key="common.foot.shipping"/>
          <dsp:a page="/company/shipping.jsp" title="${linkText}">            
            <c:out value="${linkText}"/>
          </dsp:a>
        </li>      

        <%-- 'Returns' link --%>
        <li>
          <fmt:message var="linkText" key="common.foot.return"/>
          <dsp:a page="/company/returns.jsp" title="${linkText}">            
            <c:out value="${linkText}"/>
          </dsp:a>
        </li>      

        <%-- 'Contact Us' link --%>
        <li>
          <fmt:message var="linkText" key="navigation_tertiaryNavigation.contactUs"/>
          <dsp:a page="/company/customerService.jsp" title="${linkText}">            
            <c:out value="${linkText}"/>
          </dsp:a>
        </li>
        
      </ul>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/navigation/gadgets/tertiaryNavigation.jsp#1 $$Change: 735822 $ --%>

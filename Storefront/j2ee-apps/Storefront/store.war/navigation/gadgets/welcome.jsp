<%--
  This page renders a welcome message and the user navigation menu which
  includes Login, Account, Orders, Help, Logout. The welcome message is 
  personalized to include the users name if the user is known. A menu link can
  be clicked and the user will be directed to the desired page.
  
  Required Parameters:
    selpage
      Indicates the currently active tab, used for styling
    
  Optional Parameters:
    None 
--%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/userprofiling/PropertyManager"/>
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/dynamo/servlet/RequestLocale"/>
  
  <dsp:getvalueof var="activeTab" param="selpage"/>
  
  <dsp:getvalueof var="isTransient" bean="Profile.transient"/>
  <dsp:getvalueof var="origRequest" vartype="java.lang.String" 
                  bean="/OriginatingRequest.requestURI" />
  <dsp:getvalueof id="userLocale" vartype="java.lang.String"  bean="RequestLocale.locale"/>

  <div id="atg_store_userAccountNav">
        
    <c:choose>
      <%-- The user is known --%>
      <c:when test="${not isTransient}">
        <dsp:getvalueof id="firstName" bean="Profile.firstName"/>
        <c:if test="${empty firstName}">
          <fmt:message var="firstName" key="navigation_welcome.firstName"/>
        </c:if>

        <ul>
          <%-- Display a personalized welcome message and a 'not you?' link --%>
          <li class="first atg_store_welcomeMessage">
            
            <span class="atg_store_welcome">
              <fmt:message var="notText" key="navigation_welcome.notText"/>
            
              <fmt:message key="navigation.welcomeback">
                <fmt:param>
                  <span class="atg_store_loggedInUserName"><c:out value="${firstName}"/>
                </fmt:param>
              </fmt:message>
              </span>
            </span>
            <dsp:a page="/" title="${notText}" iclass="atg_store_logoutLink">
              <dsp:property bean="ProfileFormHandler.logoutSuccessURL" 
                            value="myaccount/login.jsp?locale=${userLocale}"/>
              <dsp:property bean="ProfileFormHandler.logout" value="true"/>
              <c:out value="${notText}"/>
            </dsp:a>            
          </li>
            
          <%-- Account link --%>
          <fmt:message var="accountTitle" key="navigation.welcome.account"/>           
          <li class="${activeTab=='MY PROFILE' ? ' active' : ' '} atg_store_accountLink">
            <dsp:a page="/myaccount/profile.jsp" title="${accountTitle}" 
                   iclass="atg_store_navAccount">                
                <c:out value="${accountTitle}"/>
            </dsp:a>
          </li>
            
          <%-- Orders link --%>
          <fmt:message var="orderTitle" key="navigation.welcome.orders"/>
          <li class="${activeTab=='MY ORDERS' ? ' active' : ' '}">
            <dsp:a page="/myaccount/myOrders.jsp" title="${orderTitle}" 
                   iclass="atg_store_navAccount">              
              <c:out value="${orderTitle}"/>
            </dsp:a>
          </li>
            
          <%-- Help link --%>
          <fmt:message var="helpTitle" key="navigation.welcome.help"/>
          <li class="${activeTab=='customerService' ? ' active' : ' '}">
            <dsp:a page="/company/customerService.jsp" title="${helpTitle}">              
              <c:out value="${helpTitle}"/>
            </dsp:a>
          </li>
            
          <%-- Logout link --%>
          <fmt:message var="logoutTitle" key="navigation.welcome.logout"/>
          <li class="last">
            <dsp:a page="/" title="${logoutTitle}">
              <dsp:property bean="ProfileFormHandler.logoutSuccessURL" 
                            value="myaccount/login.jsp?locale=${userLocale}"/>
              <dsp:property bean="ProfileFormHandler.logout" value="true"/>
              <c:out value="${logoutTitle}"/>
            </dsp:a>
          </li>
   
        </ul>
      </c:when>
          
      <%-- The User is not known --%>
      <c:otherwise>
        <ul>
          <%-- Display a welcome message --%>
          <li class="first atg_store_welcomeMessage">
            <span class="atg_store_welcome">
              <fmt:message key="navigation.welcome"/>
            </span>
          </li>
          
          <%-- Login link --%>
          <fmt:message var="loginTitle" key="checkout_checkoutProgress.login"/>
          <li>
            <dsp:a page="/myaccount/login.jsp" title="${loginTitle}">
              <dsp:param name="loginFromHeader" value="yes"/>
              <c:out value="${loginTitle}"/>
            </dsp:a>
          </li>
          
          <%-- Help link --%>
          <fmt:message var="helpTitle" key="navigation.welcome.help"/>          
          <li class="${activeTab=='customerService' ? ' active' : ' last'}">
            <dsp:a page="/company/customerService.jsp" title="${helpTitle}">              
              <c:out value="${helpTitle}"/>
            </dsp:a>
          </li>
          
        </ul>
      </c:otherwise>
    </c:choose>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/navigation/gadgets/welcome.jsp#2 $$Change: 788278 $ --%>

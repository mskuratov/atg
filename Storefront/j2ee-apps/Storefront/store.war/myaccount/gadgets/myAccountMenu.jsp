<%--
  This page renders 'My Account' menu for navigating to profile information,
  payment settings, addresses, wish lists, gift lists and previous orders. 
    
  Required parameters:
    selpage
      indicates which menu option is currently selected
      
  Optional parameters
    None      
--%>

<dsp:page>
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/dynamo/servlet/RequestLocale"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>  
  
  <%-- Unpack selected tab --%>
  <dsp:getvalueof var="activeTab" param="selpage"/>
  <div id="atg_store_myAccountNav" class="aside">
  <ul>

    <%-- Profile information --%>
    <fmt:message var="linkText" key="myaccount_myAccountMenu.profile" />
    <c:choose>
      <c:when test="${activeTab == 'MY PROFILE'}">
        <li class="first current">
      </c:when>
      <c:otherwise>
        <li class="first">
      </c:otherwise>
    </c:choose>
    <dsp:a page="../profile.jsp" title="${linkText}">      
      ${linkText}
    </dsp:a>
    </li>

    <%-- Payment info --%>
    <fmt:message var="linkText" key="myaccount_myAccountMenu.paymentInfo" />
    <c:choose>
      <c:when test="${activeTab == 'PAYMENT INFO'}">
        <li class="current">
      </c:when>
      <c:otherwise>
        <li>
      </c:otherwise>
    </c:choose>
    <dsp:a page="../paymentInfo.jsp" title="${linkText}">${linkText}</dsp:a>
    </li>

    <%-- Address book --%>
    <fmt:message var="linkText" key="myaccount_myAccountMenu.addressBook"/>
    <c:choose>
     <c:when test="${activeTab == 'ADDRESS BOOK'}">
       <li class="current">
     </c:when>
     <c:otherwise>
       <li>
     </c:otherwise>
    </c:choose>    
      <dsp:a page="../addressBook.jsp" title="${linkText}">${linkText}</dsp:a>
    </li>
    
    <%-- Orders --%>
    <fmt:message var="linkText" key="myaccount_myAccountMenu.myOrders" />
    <c:choose>
      <c:when test="${activeTab == 'MY ORDERS'}">
        <li class="current">
      </c:when>
      <c:otherwise>
        <li>
      </c:otherwise>
    </c:choose>
    <dsp:a page="../myOrders.jsp" title="${linkText}">      
         ${linkText}
       </dsp:a>
    </li>
    
    <%-- Returns History --%>
    <fmt:message var="linkText" key="myaccount_myAccountMenu.myReturns" />
    <c:choose>
      <c:when test="${activeTab == 'MY RETURNS'}">
        <li class="current">
      </c:when>
      <c:otherwise>
        <li>
      </c:otherwise>
    </c:choose>
    <dsp:a page="../myReturns.jsp" title="${linkText}">      
         ${linkText}
       </dsp:a>
    </li>

    <%-- Wish lists --%>
    <fmt:message var="linkText" key="myaccount_myAccountMenu.myWishList" />
    <c:choose>
      <c:when test="${activeTab == 'WISHLIST'}">
        <li class="current">
      </c:when>
      <c:otherwise>
        <li>
      </c:otherwise>
    </c:choose>
    <dsp:a page="../myWishList.jsp" title="${linkText}">      
       ${linkText}</dsp:a>
    </li>

    <%-- Gift lists --%>
    <fmt:message var="linkText" key="myaccount_myAccountMenu.giftLists" />
    <c:choose>
      <c:when test="${activeTab == 'GIFT LISTS'}">
        <li class="current">
      </c:when>
      <c:otherwise>
        <li>
      </c:otherwise>
    </c:choose>
    <dsp:a page="../giftListHome.jsp" title="${linkText}">      
       ${linkText}
     </dsp:a>
    </li>

    <%-- Log out --%>
   <li class="atg_store_myAccountNavLogout">
     <dsp:getvalueof id="userLocale" vartype="java.lang.String"  bean="RequestLocale.locale"/>
     <dsp:a page="/" title="Logout" value="logout" iclass="atg_store_logoutLink">
       <dsp:property bean="ProfileFormHandler.logoutSuccessURL" 
                     value="myaccount/login.jsp?locale=${userLocale}"/>
       <dsp:property bean="ProfileFormHandler.logout" value="true"/>
       <fmt:message key="navigation.welcome.logout"/>
     </dsp:a>
   </li>

  </ul>
  
  <%-- 
    Include 'Click to Call' gadget under the menu items
  --%>
  <dsp:include page="/navigation/gadgets/clickToCallLink.jsp">
    <dsp:param name="pageName" value="orderHistory"/>
  </dsp:include>
</div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/myAccountMenu.jsp#2 $$Change: 788278 $--%>

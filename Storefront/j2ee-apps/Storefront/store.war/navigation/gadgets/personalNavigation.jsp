<%--
  Displays the Promotions, Comparisons, Gift Lists and Wish List links in the
  navigation menu. A user is able to click on one of the previously mentioned
  links and will be directed to the desired page.
  
  Required Parameters:
    selpage
      Indicates the currently active tab, used for styling
    
  Optional Parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/profile/SessionBean"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>

  <dsp:getvalueof var="activeTab" param="selpage"/>

  <div id="atg_store_personalNav">
    <ul id="atg_store_personalNavItems">        
      
      <%-- PROMOTIONS LINK --%>     
      <li class ="${activeTab=='PROMOTIONS' ? ' active' : ' '}">
        <fmt:message var="itemLabel" key="navigation_personalNavigation.promotions"/>
        <fmt:message var="itemTitle" key="navigation_personalNavigation.linkTitle">
          <fmt:param value="${itemLabel}"/>
        </fmt:message>
        <dsp:a id="productPromotions" page="/promo/promotions.jsp" title="${itemTitle}"
               iclass="atg_store_navPromotions">          
          <c:out value="${itemLabel}"/>
        </dsp:a>
      </li>     
       
      <%-- COMPARISONS LINK --%>
      <li class ="${activeTab=='COMPARISONS' ? ' active' : ' '}">
        <fmt:message var="itemLabel" key="navigation_personalNavigation.comparisons"/>
        <fmt:message var="itemTitle" key="navigation_personalNavigation.linkTitle">
          <fmt:param value="${itemLabel}"/>
        </fmt:message>
        <dsp:a id="productComparisons" page="/comparisons/productComparisons.jsp" title="${itemTitle}"
               iclass="atg_store_navComparisons">          
          <c:out value="${itemLabel}"/>
        </dsp:a>
      </li>     
           
      <%-- GIFT LIST LINK --%>      
      <li class ="${activeTab=='GIFT LISTS' ? 'atg_store_giftListsNav active atg_store_dropDownParent' : 'atg_store_giftListsNav atg_store_dropDownParent'}">       
        <fmt:message var="itemLabel" key="navigation_personalNavigation.giftList"/>
        <a id="giftList" href="#" iclass="atg_store_navGiftList">
          <c:out value="${itemLabel}"/>
        </a>
        
        <div class="atg_store_giftListMenuContainer atg_store_dropDownChild" id="atg_store_giftListMenuContainer">
          <ul class="atg_store_giftListMenu">
            <%--
              Determine security status of the current profile
              and display appropriate links to the Gift lists.
              
              Input parameters:
                None
                
              Open parameters:
                loggedIn
                  user is logged in with login/password
                default
                  Profile's security status is either anonymous or logged in from cookie                
            --%>
            <dsp:droplet name="ProfileSecurityStatus">

              <%-- Logged in user --%>
              <dsp:oparam name="loggedIn">
                <c:url value="/myaccount/giftListHome.jsp" var="giftlistHomeUrl" scope="page"/>
              </dsp:oparam>
              
              <%-- Anonymous user or logged in from cookie --%>
              <dsp:oparam name="default">
                <c:url value="/myaccount/login.jsp?error=giftlistNotLoggedIn" 
                       var="giftlistHomeUrl" scope="page"/>
              </dsp:oparam>
            </dsp:droplet>
            
            <%-- Create the Find a gift list link --%>
            <c:url value="/giftlists/giftListSearch.jsp" var="findGiftUrl" scope="page">
              <c:param name="resetFormErrors">true</c:param>
            </c:url>
            <li>
              <dsp:a href="${findGiftUrl}">                
                <fmt:message key="navigation_personalNavigation.giftList.findGiftlist"/>
              </dsp:a>
            </li>
            
            <%-- Create the Create a gift list link --%>
            <li>
              <dsp:a href="${giftlistHomeUrl}">
                <dsp:property bean="SessionBean.values.loginSuccessURL" value="giftListHome.jsp"/>                
                <fmt:message key="navigation_personalNavigation.giftList.createGiftlist"/>
              </dsp:a>
            </li>
            
            <%-- Create the Update a gift list link --%>
            <li>
              <dsp:a href="${giftlistHomeUrl}">
                <dsp:property bean="SessionBean.values.loginSuccessURL" value="giftListHome.jsp"/>                
                <fmt:message key="navigation_personalNavigation.giftList.editGiftlist"/>
              </dsp:a>
            </li>
          </ul>
        </div>
      </li>

      <%-- WISH LISTS LINK --%>
      <li class ="${activeTab=='WISHLIST' ? ' active' : ' '}">
        <fmt:message var="itemLabel" key="navigation_personalNavigation.wishList"/>
        <fmt:message var="itemTitle" key="navigation_personalNavigation.linkTitle">
          <fmt:param value="${itemLabel}"/>
        </fmt:message>

        <%--
          Determine security status of the current profile
          and display appropriate links to the Wish list.
          
          Input parameters:
            None
            
          Open parameters:
            loggedIn
              user is logged in with login/password
            default
              Profile's security status is either anonymous or logged in from cookie                
        --%>
        <dsp:droplet name="ProfileSecurityStatus">
          
          <%-- Logged in User --%>
          <dsp:oparam name="loggedIn">
            <dsp:a id="myWishList" page="/myaccount/myWishList.jsp" title="${itemTitle}"
                   iclass="atg_store_navWishList">              
              <c:out value="${itemLabel}"/>
            </dsp:a>
          </dsp:oparam>
          
          <%-- Anonymous user or logged in from cookie --%>
          <dsp:oparam name="default">
            <dsp:a id="myWishList" page="/global/util/loginRedirect.jsp?error=wishlistNotLoggedIn"
                   title="${itemTitle}" iclass="atg_store_navWishList">              
              <dsp:property bean="SessionBean.values.loginSuccessURL" value="myWishList.jsp"/>
              <c:out value="${itemLabel}"/>
            </dsp:a>
          </dsp:oparam>
          
        </dsp:droplet>
      </li>
      
      <%-- RICH CART --%>
      <li class="atg_store_viewCart" id="atg_store_richCartTarget">
        <dsp:include page="/navigation/gadgets/richCart.jsp"/>
      </li>
      
      <%-- CHECKOUT LINK --%>
      <li class="atg_store_checkoutLink last">
        <dsp:include page="/navigation/gadgets/shoppingCart.jsp"/>
      </li>
      
    </ul>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/navigation/gadgets/personalNavigation.jsp#1 $$Change: 735822 $ --%>

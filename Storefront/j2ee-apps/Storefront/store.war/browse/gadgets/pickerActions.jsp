<%--
  This gadget renders a <div> with all necessary buttons to be dislpayed on the product details page.
  These buttons are:
    - Add to Giftlist
    - Add to Wishlist
    - Add to Comparisons
    - Email a Friend

  Required parameters:
    product
      currently viewed product
    comparisonsContainsProduct
      boolean value, indicates if comparisons list already contains the product specified
    showEmailAFriend
      boolean value, indicates if email a friend button should be displayed
    showGiftlists
      boolean value, indicates if add to giftlist button should be displayed
    wishlistContainsSku
      boolean value, indicates if wishlist already contains currently selected sku
    giftlists
      all proper user giftlists (i.e. created on a proper site)
      
   Optional parameters:
    giftlistsContainingSku
      All the giftlists containing the selected SKU
--%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>

  <dsp:getvalueof var="giftlistsContainingSku"  param="giftlistsContainingSku"/>
   
  <div class="atg_store_pickerActions">
    <ul id="moreactions">
      <%-- Include hidden auxiliary input fields. --%>
      <dsp:include page="/browse/gadgets/pickerAddToGiftListFormParams.jsp">
        <dsp:param name="product" param="product" />
        <dsp:param name="sku" param="selectedSku"/>
        <dsp:param name="categoryId" param="categoryId"/>
      </dsp:include>

      <dsp:getvalueof var="categoryId" vartype="java.lang.String" param="categoryId"/>
      <dsp:getvalueof var="productId" vartype="java.lang.String" param="product.repositoryId"/>
      
      <%-- Product page URL --%>
      <dsp:getvalueof var="templateURL" vartype="java.lang.String" param="product.template.url"/>
      <c:url value="${templateURL}" var="productUrl" scope="page">
        <c:param name="productId" value="${productId}"/>
        <c:param name="categoryId" value="${categoryId}"/>
        <c:param name="categoryNavIds"><dsp:valueof param="categoryNavIds"/></c:param>
      </c:url>

      <%-- Display 'Gift List' button for non-anonymous users with already created gift lists. --%>
      <dsp:getvalueof var="showGiftlists" vartype="java.lang.Boolean" param="showGiftlists"/>
      <c:if test="${showGiftlists}">
        <dsp:getvalueof var="giftlists" vartype="java.util.Collection" param="giftlists"/>
       
        <li class="atg_store_giftListsActions atg_store_dropDownParent">
          <a href="#"><fmt:message key="common.button.addToGiftListText" /></a>
          <div class="atg_store_giftListMenuContainer atg_store_dropDownChild">
            <h4><fmt:message key="common.giftListHeader"/><fmt:message key="common.labelSeparator"/></h4>
            <ul class="atg_store_giftListMenu">
              <dsp:getvalueof var="productId" vartype="java.lang.String" param="product.repositoryId"/>
              
              <%-- On Error stay on the product page --%>
              <dsp:input type="hidden" bean="CartModifierFormHandler.addItemToGiftlistErrorURL" 
                         value="${productUrl}"/>

              <%-- Iterate over user's gift lists and render buttons. --%>
              <c:forEach var="giftlist" items="${giftlists}" varStatus="status">
                <dsp:param name="giftlist" value="${giftlist}" />
                <dsp:getvalueof var="giftListId" param="giftlist.repositoryId"/>
                <dsp:contains var="isSkuInGiftlist" values="${giftlistsContainingSku}" object="${giftListId}"/>
                
                  <%-- On Success Go to the GiftList page --%>
                  <c:url var="successUrl" scope="page" value="/myaccount/giftListEdit.jsp">
                    <c:param name="productId"><dsp:valueof param="product.repositoryId"/></c:param>
                    <c:param name="giftlistId">${giftListId}</c:param>
                  </c:url>

                  <li class="atg_store_giftListsActions ${isSkuInGiftlist ? 'atg_store_viewGiftlist' :  ' '}">
                  <c:choose>
                    <c:when test="${not isSkuInGiftlist}">
                      <%-- Show "Add to Gift List" link, if actionFlag is true --%>
                      <dsp:getvalueof var="giftlistId" bean="Profile.wishlist.id"/>
                      <fmt:message var="addToGiftListText" key="addGiftList_${status.count}"/>
                      <%--
                        Set 'Add to Gift list' success URL through the request parameter named by Gift list ID as
                        we have multiple gift lists in the form
                      --%>
                      <input type="hidden" name="${giftListId}" value="${successUrl}" />
                  
                      <dsp:getvalueof var="addItemToGiftlistText" param="giftlist.eventName"/>
                      <dsp:input type="submit" bean="CartModifierFormHandler.addItemToGiftlist" 
                                 value="${addItemToGiftlistText}"
                                 submitvalue="${giftListId}" iclass="atg_store_textButton" 
                                 name="addGiftList_${status.count}"/>
                    </c:when>
                    <c:otherwise>
                      <%-- General URL to gift list home --%>
                      <c:url var="giftListHomeUrl" value="/myaccount/giftListEdit.jsp" scope="page">
                        <c:param name="giftlistId" value="${giftListId}"/>
                      </c:url>  
                      <%-- Otherwise just navigate the user to his giftlists value="${productUrl}"--%>
                      <input type="hidden" name="${giftListId}" value="${giftListHomeUrl}" />
                      <dsp:getvalueof var="addItemToGiftlistText" param="giftlist.eventName"/>
                      <dsp:input type="submit" bean="CartModifierFormHandler.addItemToGiftlist" 
                                 value="${addItemToGiftlistText}"
                                 submitvalue="${giftListId}" iclass="atg_store_textButton" 
                                 name="addGiftList_${status.count}"/> 
                    </c:otherwise>
                  </c:choose>
                </li>
              </c:forEach>
            </ul>
          </div>
        </li>
      </c:if>

      <%-- Check if selected SKU already added to a wishlist. If so, modify wishlist button style. --%>
      <dsp:getvalueof var="wishlistContainsSku" vartype="java.lang.Boolean" param="wishlistContainsSku"/>
      <li class="atg_store_wishListsActions ${wishlistContainsSku ? 'atg_store_viewWishlist' : ''}">
        <c:choose>
          <c:when test="${not wishlistContainsSku}">
            <%-- Show "Add to Wish List" link, if actionFlag is true --%>
            
            <dsp:getvalueof var="wishListId" bean="Profile.wishlist.id"/>

            <fmt:message var="addToWishListText" key="common.button.addToWishListText"/>
            <%--
              Set 'Add to Wish list' success URL through the request parameter named by wish list ID as
              we have multiple gift lists in the form
             --%>
            <input type="hidden" name="${wishListId}" value="${productUrl}" />
           
            <dsp:input type="hidden" bean="CartModifierFormHandler.addItemToGiftlistErrorURL" value="${productUrl}"/>
            <dsp:input type="submit" iclass="atg_store_textButton" bean="CartModifierFormHandler.addItemToGiftlist" 
                       value="${addToWishListText}" submitvalue="${wishListId}" name="addWishList" />
          </c:when>
          <c:otherwise>
            <%-- Otherwise just navigate the user to his wishlist --%>
            <c:url value="/myaccount/myWishList.jsp" var="wishListUrl" scope="page"/>
            <dsp:a href="${wishListUrl}">
              <fmt:message key="common.button.addToWishListText"/>
            </dsp:a>
          </c:otherwise>
        </c:choose>
      </li>

      <%-- Display proper Comparisons button. --%>
      <dsp:getvalueof var="comparisonsContainsProduct" vartype="java.lang.Boolean" param="comparisonsContainsProduct"/>
      <li class="atg_store_compareActions ${comparisonsContainsProduct ? 'atg_store_viewComparisons' : ''}">
        <c:choose>
          <c:when test="${comparisonsContainsProduct}">
            <%-- View Comparisons button. --%>
            <fmt:message var="linkTitle" key="browse_productAction.addToComparisonsSubmit"/>
            <dsp:a page="/comparisons/productComparisons.jsp" title="${linkTitle}">
              <fmt:message  key="browse_productAction.addToComparisonsSubmit"/>
            </dsp:a>
          </c:when>
          <c:otherwise>
            <%--
              We should display the Add to Comparisons button.

              This droplet calculates a default site-aware URL for the product catalog item specified.

              Input parameters:
                item
                  Product catalog item an URL should be calculated for.

              Output parameters:
                url
                  URL calculated.

              Open parameters:
                output
                  Always rendered.
            --%>
            <dsp:droplet name="/atg/repository/seo/CatalogItemLink">
              <dsp:param name="item" param="product"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="productUrl" vartype="java.lang.String" param="url"/>
              </dsp:oparam>
            </dsp:droplet>

            <%-- Construct an URL that will be used as a success/error URL. --%>
            <c:url var="url" value="${productUrl}">
              <c:param name="productId"><dsp:valueof param="product.repositoryId"/></c:param>
              <c:param name="categoryId"><dsp:valueof param="categoryId"/></c:param>
              <c:param name="categoryNavIds"><dsp:valueof param="categoryNavIds"/></c:param>
              <c:param name="navAction"><dsp:valueof param="navAction"/></c:param>
              <c:param name="navCount"><dsp:valueof param="navCount"/></c:param>
            </c:url>

            <%-- Button itself. --%>
            <fmt:message var="addToComparisonsText" key="browse_productAction.addToComparisonsSubmit"/>
            <dsp:input type="hidden" bean="ProductListHandler.addProductSuccessURL" value="${url}"/>
            <dsp:input type="hidden" bean="ProductListHandler.addProductErrorURL" value="${url}"/>
            <dsp:input type="hidden" bean="ProductListHandler.productID" paramvalue="product.repositoryId"/>
            <dsp:input type="submit" iclass="atg_store_textButton" bean="ProductListHandler.addProduct" value="${addToComparisonsText}"
                       submitvalue="true" />
          </c:otherwise>
        </c:choose>
      </li>

      <%-- Email a friend link. --%>
      <dsp:getvalueof var="showEmailAFriend" vartype="java.lang.Boolean" param="showEmailAFriend"/>
      <c:if test="${showEmailAFriend}">
        <li class="atg_store_emailActions">
          <fmt:message var="linkTitle" key="browse_productAction.emailFriendTitle"/>
          <dsp:a page="/browse/emailAFriend.jsp?productId=${productId}&categoryId=${categoryId}"
                 title="${linkTitle}" target="popupLarge">
            <fmt:message key="browse_productAction.emailFriendLink"/>
          </dsp:a>
        </li>
      </c:if>
    </ul>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/pickerActions.jsp#2 $$Change: 788278 $--%>
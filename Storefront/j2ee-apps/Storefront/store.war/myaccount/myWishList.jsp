<%--
  This page displays the items on the shoppers wish list

  Required parameters:
    None
          
  Optional parameters:
    giftlistId
      the Id of the Wish List to manage
    giftId  
      the Id of the item to remove from the specified Wish List
    howMany
      how many items to display on page 
    start
      page index      
--%>

<dsp:page>

  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Range"/>
  <dsp:importbean bean="/atg/commerce/collections/filter/droplet/GiftlistSiteFilterDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/CartSharingSitesDroplet" />
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
  
  <crs:pageContainer index="false" follow="false" 
                     bodyClass="atg_store_myAccountPage atg_store_leftCol atg_store_wishList"
                     selpage="WISHLIST">
    <jsp:body>
    
      <%-- Page title --%>
      <div id="atg_store_contentHeader">
        <h2 class="title">
          <fmt:message key="myaccount_myWishList.title"/>
        </h2>
      </div>
      
      <%-- Left-hand menu --%>
      <dsp:include page="gadgets/myAccountMenu.jsp">
        <dsp:param name="selpage" value="WISHLIST" />
      </dsp:include>

      <%-- Display list of whishlist items --%>
      <div class="atg_store_main atg_store_myAccount">
      
      <%-- Deletes an item from the Wish List if requested --%>
      <dsp:include page="gadgets/removeGiftListItem.jsp">
        <dsp:param name="giftId" param="giftId"/>
        <dsp:param name="giftlistId" param="giftlistId"/>
      </dsp:include>
    
      <dsp:setvalue beanvalue="Profile.wishlist" param="wishlist"/>
      <dsp:setvalue paramvalue="wishlist.giftlistItems" param="items"/>
      <dsp:setvalue paramvalue="wishlist.id" param="giftlistId"/>
      
      <%-- Set the defaults for page navigation --%>
      <dsp:getvalueof var="pageSize" vartype="java.lang.Object" bean="/atg/multisite/SiteContext.site.defaultPageSize"/>
      <dsp:getvalueof id="howMany" param="howMany"/>
      <c:if test="${empty howMany}">
        <c:set var="howMany" value="${pageSize}"/>
      </c:if>
      <dsp:getvalueof id="start" param="start"/>
      <c:if test="${empty start}">
        <c:set var="start" value="1"/>
      </c:if>
        
      <fmt:message var="tableSummary" key="myaccount_myWishList.listTableSummary" />
    
      <%--
        Retrieves wish list items available for this site
        
        Input parameters:
          collection
            wishlist items
        
        Output parameters:
          filteredCollection
            collection filtered using GiftlistSiteFilterDroplet.filter service.
            See atg/commerce/collections/filter/droplet/GiftlistSiteFilterDroplet 
            for details
       --%>
      <dsp:droplet name="GiftlistSiteFilterDroplet">
        <dsp:param name="collection" param="items"/>
        
        <%--
          If collection is empty display message 
         --%>
        <dsp:oparam name="empty">
          <crs:messageContainer titleKey="myaccount_myWishList.noFavorites">
            <jsp:body>
              <div class="atg_store_formActions">
    
                  <dsp:form action="${pageContext.request.requestURI}"
                            method="post" formid="wishListEmptyContinueForm">
            
                    <%-- Display form errors if any --%>
                    <dsp:include page="/myaccount/gadgets/myAccountErrorMessage.jsp">
                      <dsp:param name="formHandler" bean="GiftlistFormHandler"/>
                    </dsp:include>
            
                    <%-- Render the Continue Shopping button --%>
                    <crs:continueShopping>
                      <dsp:input type="hidden" bean="CartModifierFormHandler.cancelURL"
                                 value="${continueShoppingURL}"/>
                    </crs:continueShopping>
                    
                    <fmt:message key="common.button.continueShoppingText" var="continueShopping"/>
                    <span class="atg_store_basicButton secondary">
                      <dsp:input  type="submit" bean="CartModifierFormHandler.cancel"
                                  value="${continueShopping}"
                                  iclass="atg_store_button"/>
                    </span>
                  </dsp:form>
            
                </div>    
            </jsp:body>
          </crs:messageContainer>
        </dsp:oparam>
        
        <%--
          Filtered collection is not empty, proceed
         --%>
        <dsp:oparam name="output">
          <dsp:setvalue param="filteredItems" paramvalue="filteredCollection" />
    
          <%--
            Use Range droplet to split wishlist items by pages
            
            Input parameters:
              array
                collection of filtered items 
              howMany
                define how many items to display in 'oparam' section
              start
                starting index
                  
             Output parameters:
                element
                  current Giftitem object  
           --%>
          <dsp:droplet name="Range">
            <dsp:param name="array" param="filteredItems"/>
            <dsp:param name="howMany" value="${howMany}"/>
            <dsp:param name="start" value="${start}"/>
    
            <%-- Start of wishlist items table --%>
            <dsp:oparam name="outputStart">
    
              <%-- Show top pagination links --%>
              <dsp:include page="/global/gadgets/giftAndWishListPagination.jsp">
                <dsp:param name="arraySplitSize" value="${pageSize}"/>
                <dsp:param name="size" param="size"/>
                <dsp:param name="start" value="${start}"/>
                <dsp:param name="top" value="${true}"/>
                <dsp:param name="giftlistId" param="giftlistId"/>
              </dsp:include>
    
              <table valign="top" id="atg_store_itemTable" class="atg_store_myWishListTable" summary="${tableSummary}" cellspacing="0" cellpadding="0">
                <thead>
                  <tr>
                    <%--
                      Display site indicator only for 
                      sites that share atg.ShoppingCart shareable,
                      otherwise don't display site column. 
                     --%>
					<%--
                      CartSharingSitesDroplet returns a collection of sites that share the shopping
	                  cart shareable (atg.ShoppingCart) with the current site.
	                  You may optionally exclude the current site from the result.

                      Input Parameters:
                        excludeInputSite - Should the returned sites include the current
   
                      Open Parameters:
                        output - This parameter is rendered once, if a collection of sites
                                 is found.
   
                      Output Parameters:
                        sites - The list of sharing sites.
                    --%>
                    <dsp:droplet name="CartSharingSitesDroplet">
                      <dsp:param name="excludeInputSite" value="true"/>
                      <dsp:oparam name="output">
                        <th scope="col">
                          <fmt:message key="common.site"/>
                        </th>
                      </dsp:oparam>
                    </dsp:droplet>                         
                    
                    <th scope="col" colspan="2" class="item">
                      <fmt:message key="common.item"/>
                    </th>
                    <th scope="col" class="price">
                      <fmt:message key="common.price"/>
                    </th>
                    <th scope="col" class="quantity">
                      <fmt:message key="common.qty"/>
                    </th>
                  </tr>
                </thead>
                <tbody>
              </dsp:oparam>
              <dsp:oparam name="output">
                
                <dsp:setvalue param="giftItem" paramvalue="element"/>
                <dsp:getvalueof id="count" param="count"/>
                <dsp:getvalueof id="size" param="size"/>
                 
                <%--
                  Retrieve product for the given giftItem.
                  
                  
                  Input parameters:
                    id 
                      product Id
                    filterByCatalog
                      set to false to display items from another catalog
                    filterBySite
                      set to false to display items from another sites                  
                  
                  Output parameters:
                    element
                      product item
                 --%>
                <dsp:droplet name="ProductLookup">
                  <dsp:param name="id" param="giftItem.productId"/>
                  <dsp:param name="filterByCatalog" value="false"/>
                  <dsp:param name="filterBySite" value="false"/>
                  
                  <dsp:oparam name="output">
                    <tr class="<crs:listClass count="${count}" size="${size}" selected="false"/>">
                      <%-- Render a row-entry for each Gift List item --%>
                      <dsp:include page="gadgets/wishListRow.jsp">
                        <dsp:param name="product" param="element"/>
                        <dsp:param name="giftlistId" param="giftlistId"/>
                        <dsp:param name="giftItem" param="giftItem"/>
                      </dsp:include>
                    </tr>
                  </dsp:oparam>
                </dsp:droplet> <%-- ProducLookup --%>
    
              </dsp:oparam>
    
              <%-- End of wishlist items table --%>          
              <dsp:oparam name="outputEnd">
                </tbody>
              </table>
      
                <%-- Bottom pagination links --%>
                <dsp:include page="/global/gadgets/giftAndWishListPagination.jsp">
                  <dsp:param name="arraySplitSize" value="${pageSize}"/>
                  <dsp:param name="size" param="size"/>
                  <dsp:param name="start" value="${start}"/>
                  <dsp:param name="top" value="${false}"/>
                  <dsp:param name="giftlistId" param="giftlistId"/>
                </dsp:include>
                
                <%-- Display 'Continue Shopping' button --%>
               <div class="atg_store_formActions">
    
                <div class="atg_store_continue">
                  <dsp:form action="${pageContext.request.requestURI}"
                            method="post" formid="wishListContinueForm">
            
                    <%-- Display form errors if any --%>
                    <dsp:include page="/myaccount/gadgets/myAccountErrorMessage.jsp">
                      <dsp:param name="formHandler" bean="GiftlistFormHandler"/>
                    </dsp:include>
            
                    <%-- Render the Continue Shopping button --%>
                    <crs:continueShopping>
                      <dsp:input type="hidden" bean="CartModifierFormHandler.cancelURL"
                                 value="${continueShoppingURL}"/>
                    </crs:continueShopping>
                    
                    <fmt:message key="common.button.continueShoppingText" var="continueShopping"/>
                    <span class="atg_store_basicButton secondary">
                      <dsp:input  type="submit" bean="CartModifierFormHandler.cancel"
                                  value="${continueShopping}"
                                  iclass="atg_store_button"/>
                    </span>
                  </dsp:form>
            
                </div>    
              </div>
            </dsp:oparam>         
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>
      
        
      </div>
      
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/myWishList.jsp#3 $$Change: 788278 $--%>

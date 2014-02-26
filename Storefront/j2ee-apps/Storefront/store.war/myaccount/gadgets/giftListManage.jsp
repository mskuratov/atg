<%--
  This page displays all the items in a selected giftList along with remove and add to cart action buttons
  for each one.
  If number of items in gift list is bigger than a configured value then pagination links are displayed,
  they allow to select the range of items to view. Page size is configured in site configuration.
  Also pagination links allow to view all items on one page.
  
  Required parameters:
    giftlistId
      The id of the giftlist to be viewed

  Optional parameters:
    viewAll
      A pagination parameter, if 'true' all items are displayed on one page.
    start
      A pagination parameter, specifies an index of first item that should be displayed on a page.
      Default value is 1.
    howMany
      A pagination parameter, specifies how many items should be displayed per page.
    productId
      Product ID. In case we reached this page from product page.
    giftId
      Gift item ID to remove from list.
--%>
<dsp:page>
 
 
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistLookupDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Range"/>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/CartSharingSitesDroplet" />

  <div id="atg_store_giftListManage">

    <dsp:getvalueof id="productId" idtype="java.lang.String" param="productId"/>
    <dsp:getvalueof var="giftlistId" vartype="java.lang.String" param="giftlistId"/>
    
    <%-- 
      Initialize pagination parameters:
        howMany
          if howMany parameter is not specified the default page size is used that is configured
          in current site configuration.
        start
          if start parameter is not specified set it to 1.  
    --%>
    <dsp:getvalueof var="pageSize" vartype="java.lang.Object" bean="/atg/multisite/SiteContext.site.defaultPageSize"/>
    <dsp:getvalueof id="howMany" param="howMany"/>
    <c:if test="${empty howMany}">
      <c:set var="howMany" value="${pageSize}"/>
    </c:if>
    <dsp:getvalueof id="start" param="start"/>
    <c:if test="${empty start}">
      <c:set var="start" value="1"/>
    </c:if>

    <%-- General URL to gift list home --%>
    <c:url value="giftListHome.jsp" var="giftListHomeUrl" scope="page"/>      
       
    <%--  Page URL for  viewAll action --%>
    <c:url var="pathViewAll" value="giftListEdit.jsp">
      <c:param name="giftlistId" value="${giftlistId}"/>
      <c:param name="howMany" value="${howMany}"/>
      <c:param name="viewAll" value="true"/>      
    </c:url>

    <%-- Page URL when viewAll not selected --%>
    <c:url var="pagePath" value="giftListEdit.jsp">
      <c:param name="giftlistId" value="${giftlistId}"/>
      <c:param name="start" value="${start}"/>      
    </c:url>
    
    <%-- Page URL for error conditions --%>
    <c:url var="errorPath" value="giftListEdit.jsp">
      <c:param name="giftlistId" value="${giftlistId}"/>
      <c:param name="start" value="${start}"/>      
    </c:url>

    <%-- Begin displaying of gift list --%>
    
    <%--
      Include removeGiftListItem.jsp gadget that check whether giftId parameter is specified and
      if so removes item from gift list by calling RemoveItemFromGiftlist form handler.
     --%>
    <dsp:include page="removeGiftListItem.jsp">
      <dsp:param name="giftId" param="giftId"/>
      <dsp:param name="giftlistId" param="giftlistId"/>
    </dsp:include>

    <fmt:message var="saveText" key="common.button.saveChanges" />
    <fmt:message var="saveTitle" key="common.button.saveChangesTitle" />
    <fmt:message var="cancelText" key="common.button.cancelText" />
    <fmt:message var="cancelTitle" key="common.button.cancelTitle" />
    
    <%-- 
      Finds gift list repository item with a given ID. 
          
      Input parameters:
        id
          ID of gift list to lookup for.
           
      Output parameters:
        element
          gift list repository item
           
      Open parameters:
        output
          Rendered if the item was found in the repository                  
    --%>
    <dsp:droplet name="GiftlistLookupDroplet">
      <dsp:param name="id" param="giftlistId"/>
      <dsp:oparam name="output">
        <dsp:setvalue paramvalue="element" param="giftlist"/>
        
        <%--
          We must first make sure that the found gift list is one that belongs to the user 
          to prevent users from passing gift list ID that isn't theirs
        --%>
        <dsp:droplet name="Compare">
          <dsp:param name="obj1" bean="Profile.id"/>
          <dsp:param name="obj2" param="giftlist.owner.id" />
          <dsp:oparam name="equal">

            <div id="atg_store_giftList">
              <dsp:input bean="GiftlistFormHandler.giftlistId" type="hidden"   
                         paramvalue="giftlistId"/>
              <dsp:input bean="GiftlistFormHandler.updateGiftlistAndItemsSuccessURL"  
                         type="hidden" value="${giftListHomeUrl}"/>
                         
              <dsp:input bean="GiftlistFormHandler.updateGiftlistAndItemsErrorURL" 
                             type="hidden" value="${errorPath}"/>
              
              <%-- 
                Determine proper cancel URL. In case when product ID is specified then cancel
                URL should point to product page from which user navigated to gift list page.
                Otherwise gift lists home page will be used.
                 --%>
              <c:choose>
                <c:when test="${!empty productId}">
                  <crs:continueShopping>
                    <dsp:input type="hidden" bean="GiftlistFormHandler.cancelURL"
                               value="${continueShoppingURL}"/>
                  </crs:continueShopping>
               </c:when>
               <c:otherwise>
                 <dsp:input bean="GiftlistFormHandler.cancelURL" type="hidden" 
                            value="${giftListHomeUrl}"/>
                </c:otherwise>
              </c:choose>

              <%--
                This droplet renders output parameter for a subset of items of its array parameter.
                
                Input parameters:
                  array
                    An array of items from which to extract the subset of items.
                  howMany
                    Specifies the number of items to include in subset of items.
                  start
                    Specifies the starting index (1-based).
                
                Output parameters:
                  element
                    This parameter is set to the current element of the array each
                    time the output parameter is rendered.
                    
                Open parameters:
                  outputStart
                    This parameter is rendered before any output tags if the subset of
                    the array being displayed is not empty.
                  outputEnd
                    This parameter is rendered after all output tags if the subset of
                    the array being displayed is not empty.
                  output
                    This parameter is rendered once for each element in the subset of the
                    array that gets displayed.
                  empty
                    This parameter is rendered if the array itself, or the
                    requested subset of the array, contains no elements.
              --%>
              <dsp:droplet name="Range">
                <dsp:param name="array" param="giftlist.giftlistItems"/>
                <dsp:param name="howMany" value="${howMany}"/>
                <dsp:param name="start" value="${start}"/>
                <dsp:oparam name="empty">
                  <%-- 
                    The array is empty, display 'no items' message specifically formatted 
                    through the messageContainer tag. 
                    --%>
                  <crs:messageContainer titleKey="myaccount_giftListManage.noItemsInList"/>
                </dsp:oparam>

                <dsp:oparam name="outputStart">
                  <%-- Table header for gift items list  --%>
                  <p><fmt:message var="tableSummary" key="myaccount_giftListManage.tableSummary"/></p>
                  <table summary="${tableSummary}" cellspacing="0" cellpadding="0" id="atg_store_itemTable">

                    <!-- Show paging links -->
                    <dsp:include page="/global/gadgets/giftAndWishListPagination.jsp">
                      <dsp:param name="itemList" param="giftlist.giftlistItems"/>
                      <dsp:param name="arraySplitSize" value="${pageSize}"/>
                      <dsp:param name="size" param="size"/>
                      <dsp:param name="start" value="${start}"/>
                      <dsp:param name="top" value="${true}"/>
                      <dsp:param name="giftlistId" param="giftlistId"/>
                      <c:if test="${!empty productId}">
                        <dsp:param name="productId" param="${productId}"/>  
                      </c:if>
                    </dsp:include>
                    <thead>
                      <tr>
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
                            <%-- 
                              There are sites that share shopping cart with the current one, so 
                              a column with site icons will be displayed. 
                             --%>
                            <th scope="col" class="site"><fmt:message key="common.site"/></th>
                            <c:set var="displaySiteIndicator" value="true"/>
                          </dsp:oparam>
                        </dsp:droplet>                          
                        <th class="item" colspan="2" scope="col" ><fmt:message key="common.item"/></th>
                        <th class="price" scope="col" ><fmt:message key="common.price"/></th>
                        <th class="quantity" scope="col" ><fmt:message key="myaccount_giftListManage.want"/></th>
                        <th class="remain" scope="col"colspan="2" ><fmt:message key="myaccount_giftListManage.need"/></th>
                      </tr>
                    </thead>
                    <tbody>
                </dsp:oparam>

                <dsp:oparam name="output">
                  <dsp:setvalue param="giftlistItem" paramvalue="element"/>
                  <dsp:param name="count" param="count"/>
                  <dsp:param name="size" param="size"/>
                  
                  <%--
                    Lookup for a product repository item that corresponds to the current gift item.
                    By default ProductLookup droplet renders output parameter only for products that belong
                    to the current catalog and site. We don't need to perform such kind of filtering
                    here: so filterBySite and filterBycatalog input parameters are set to be 'false'.
                    
                    It is supposed that gift list contains only items from sites that are in one
                    site group with the current one according to GiftlistManager settings, so we 
                    don't perform additional filtering of items based on the shareable type.
                  --%>
                  <dsp:droplet name="/atg/commerce/catalog/ProductLookup">
                    <dsp:param name="id" param="giftlistItem.productId"/>
                    <dsp:param name="filterBySite" value="false"/>
                    <dsp:param name="filterByCatalog" value="false"/>
                    <dsp:oparam name="output">
                      <%-- Display gift item details and remove \ add to cart buttons for it. --%>
                      <dsp:include page="manageYourGiftListProductRow.jsp">
                        <dsp:param name="giftlistItem" param="giftlistItem"/>
                        <dsp:param name="giftlist" param="giftlist"/>
                        <dsp:param name="count" param="count"/>
                        <dsp:param name="size" param="size"/>
                        <dsp:param name="product" param="element"/>
                        <dsp:param name="displaySiteIndicator" value="${displaySiteIndicator}"/>
                      </dsp:include>
                    </dsp:oparam>
                  </dsp:droplet>
                </dsp:oparam>

                <dsp:oparam name="outputEnd">
                    </tbody>
                  </table>
                  
                  <dsp:getvalueof var="giftlistItems" vartype="java.lang.Object" param="giftlist.giftlistItems"/>
                  <c:forEach var="giftlistItem" items="${giftlistItems}">
                    <dsp:param name="giftlistItem" value="${giftlistItem}"/>
                    <input name="<dsp:valueof param="giftlistItem.id"/>" type="hidden" value="<dsp:valueof param="giftlistItem.quantityDesired"/>"/>
                  </c:forEach>
                  
                  <!-- Show paging links -->
                  <dsp:include page="/global/gadgets/giftAndWishListPagination.jsp">
                    <dsp:param name="itemList" param="giftlist.giftlistItems"/>
                    <dsp:param name="arraySplitSize" value="${pageSize}"/>
                    <dsp:param name="size" param="size"/>
                    <dsp:param name="start" value="${start}"/>
                    <dsp:param name="top" value="${false}"/>
                    <dsp:param name="giftlistId" param="giftlistId"/>
                    <c:if test="${!empty productId}">
                      <dsp:param name="productId" param="${productId}"/>  
                    </c:if>
                  </dsp:include>
                </dsp:oparam>
              </dsp:droplet> <%-- End Range Droplet --%>
            </div>
          </dsp:oparam>
        </dsp:droplet><%-- End of Compare Droplet --%>
        
        <%-- Action buttons --%>
        <fieldset class="atg_store_actionItems atg_store_formActions">
          <%-- Update gift list and items submit button --%>
          <div class="atg_store_formActionItem">
            <span class="atg_store_basicButton">
              <dsp:input bean="GiftlistFormHandler.updateGiftlistAndItems" type="submit" title="${saveTitle}" 
                         value="${saveText}" name="atg_store_giftListUpdate" id="atg_store_giftListSubmit"/>
            </span>
          </div>
          
          <%-- If product Id is specified display Continue shopping button, otherwise Cancel button. --%>
          <c:choose>
            <c:when test="${empty productId}">
              <div class="atg_store_formActionItem">
                <span class="atg_store_basicButton secondary">
                  <dsp:input bean="GiftlistFormHandler.cancel" type="submit" title="${cancelTitle}" 
                             value="${cancelText}" id="atg_store_giftListCancel"/>
                </span>
              </div>
            </c:when>
            <c:otherwise>
              <fmt:message key="common.button.continueShoppingText" var="continueShopping"/>
              <span class="atg_store_basicButton secondary">
                <dsp:input bean="GiftlistFormHandler.cancel" type="submit" title="${cancelTitle}" 
                           value="${continueShopping}" id="atg_store_giftListCancel"/>
              </span>
            </c:otherwise>
          </c:choose>
        </fieldset>
      </dsp:oparam>
    </dsp:droplet><%-- End of GiftlistLookup Droplet --%>
  </div>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/giftListManage.jsp#2 $$Change: 742374 $--%>

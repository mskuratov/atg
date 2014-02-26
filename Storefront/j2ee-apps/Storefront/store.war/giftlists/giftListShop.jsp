<%-- 
  This page displays gift list details and content like name, event type, date, description, 
  list of gift items in gift list. For each gift item corresponding SKU details are displayed 
  like image, price, size, color, availability, quantity needed and requested, etc.
  The page allows user to perform actions on gift item according to their availability status:
  'add to cart', 'backorder', 'preorder' or 'email me when back in stock'. Also quantity of items
  can be specified for all type of actions except of 'email me when back in stock' action.
  
  If number of items in gift list is bigger than a configured value then pagination links are displayed,
  they allow to select the range of items to view. Page size is configured in site configuration.
  Also pagination links allow to view all items on one page.
     
  Required parameters: 
    giftListId 
      Id of Gift List from which shopper plans to purchase gifts
         
  Optional parameters:
    viewAll
      A pagination parameter, if 'true' all items are displayed on one page.
    start
      A pagination parameter, specifies an index of first item that should be displayed on a page.
      Default value is 1.
    howMany
      A pagination parameter, specifies how many items should be displayed per page.
 --%>  
<dsp:page>
  <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>
  <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>      
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistLookupDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Range"/>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/CartSharingSitesDroplet" />
  
  <crs:pageContainer divId="atg_store_giftListIntro" 
                     titleKey=""
                     bodyClass="atg_store_pageGiftList"
                     selpage="GIFT LISTS">
  
    <jsp:attribute name="formErrorsRenderer">    
      <dsp:include page="/global/gadgets/displayErrorMessage.jsp">
        <dsp:param name="formHandler" bean="CartModifierFormHandler"/>
      </dsp:include>
    </jsp:attribute>
  
    <jsp:body>
    
      <%-- 
        Initialize pagination parameters:
          howMany
            if howMany parameter is not specified the default page size is used that is configered
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

      <dsp:getvalueof var="contextroot" vartype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
    
      <div id="atg_store_contentHeader">
        <h2 class="title">
          <fmt:message key="common.giftListTitle"/>
        </h2>
      </div>
    
      <div id="atg_store_giftListShop">      
      
        <%-- 
          Display error messages for CartModifierFormHandler if some error occurred during
          adding item to cart.
         --%>
        <dsp:include page="/global/gadgets/displayErrorMessage.jsp">
          <dsp:param name="formHandler" bean="CartModifierFormHandler"/>
        </dsp:include>
      
        <%-- 
          Lookup for a gift list with a given ID. 
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
          
            <dsp:setvalue param="giftlist" paramvalue="element"/>
            <div id="atg_store_giftListShopHeader">
              <%-- Display gift list's details. --%>
              <dl>
                <%-- Gift List Title --%>
                <dt><dsp:valueof param="giftlist.eventName"/></dt>
                <%-- Gift List Owner --%>
                <dd>
                  <span><dsp:valueof param="giftlist.owner.firstName"/></span>
                  <span><dsp:valueof param="giftlist.owner.lastName"/></span>
                </dd>
                <%-- Gift List Type --%>
                <dd>
                  <dsp:getvalueof var="eventType" param="giftlist.eventType"/>
                  <c:set var="eventTypeResourceKey" value="${fn:replace(eventType, ' ', '')}"/>
                  <c:set var="eventTypeResourceKey" value="giftlist.eventType${eventTypeResourceKey}"/>
                  <fmt:message key="${eventTypeResourceKey}"/>
                </dd>
                <%-- Gift List Date --%>
                <dsp:getvalueof var="eventDate" vartype="java.util.Date" param="giftlist.eventDate"/>
              
                <dsp:getvalueof var="dateFormat" 
                                bean="LocaleTools.userFormattingLocaleHelper.datePatterns.shortWith4DigitYear" />
                                  
                <dd><fmt:formatDate value="${eventDate}" pattern="${dateFormat}" /></dd>
              </dl>          
              <%-- Gift List Description, if it exists --%>
              <dsp:getvalueof var="listDescription" param="giftlist.description"/>
              <c:if test="${not empty listDescription}">
                <p class="atg_store_giftListDescription">
                  <dsp:valueof param="giftlist.description"/>
                </p>
              </c:if>             
              <%-- Gift List Special Instructions, if it exists --%>
              <dsp:getvalueof var="listInstructions" param="giftlist.instructions"/>
              <c:if test="${not empty listInstructions}">
                <p class="atg_store_giftListSpecialInstructions"><dsp:valueof param="giftlist.instructions"/></p>
              </c:if>
            </div>
            <dsp:getvalueof var="giftlistId" vartype="java.lang.Double" param="giftlistId"/>
          
            <%-- Construct error URL based on pagination settings.  --%>
            <c:url var="errorPath" value="/giftlists/giftListShop.jsp">
              <c:param name="giftlistId">${giftlistId}</c:param>
              <c:choose>
                <c:when test="${empty param.viewAll}">
                  <c:param name="start">${start}</c:param>
                </c:when>
                <c:otherwise>
                  <c:param name="howMany">${howMany}</c:param>
                  <c:param name="viewAll">true</c:param>
                </c:otherwise>
              </c:choose>
            </c:url>

            <%-- 
              Display the list of items in gift list, for each item an action button will be
              displayed that corresponds to gift item availability status.
             --%>
               
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
                  The array is empty, display 'no items' message formatted through the 
                  messageContainer tag.
                 --%>
                <crs:messageContainer titleKey="giftlist_giftListShop.noItemInList"/>
              </dsp:oparam>
  
              <dsp:oparam name="outputStart">
  
                <%-- Show paging links --%>
                <dsp:include page="/global/gadgets/giftAndWishListPagination.jsp">
                  <dsp:param name="arraySplitSize" value="${pageSize}"/>
                  <dsp:param name="size" param="size"/>
                  <dsp:param name="start" value="${start}"/>
                  <dsp:param name="top" value="${true}"/>
                  <dsp:param name="giftlistId" param="giftlistId"/>
                </dsp:include>
                
                <%-- Table header for gift items list  --%>
                <fmt:message var="productList" key="giftlist_giftListShop.productList"/>
                <table summary="${productList}" id="atg_store_itemTable" cellspacing="0" cellpadding="0">
                  <thead>
                    <tr>
                      <%-- 
                        Determine whether site icons should be displayed for each gift item.
                        If there are other sites that share shopping cart with the current one then
                        site icons will be displayed, otherwise no site icons will be displayed. 
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
                          <%--
                            There are sites that share shopping cart with the current one, so 
                            a column with site icons will be displayed.
                           --%>
                          <th scope="col" class="site"><fmt:message key="common.site"/></th>
                          <c:set var="displaySiteIndicator" value="true"/>
                        </dsp:oparam>
                      </dsp:droplet>
                      <th scope="col" class="item" colspan="2"><fmt:message key="common.item"/></th>
                      <th scope="col" class="price" class="numerical"><fmt:message key="common.price"/></th>
                      <th scope="col" class="remain"><fmt:message key="giftlist_giftListShop.wants"/></th>
                      <th scope="col" class="requstd" class="numerical"><fmt:message key="giftlist_giftListShop.needs"/></th>
                      <th scope="col" class="quantity"><fmt:message key="common.qty"/></th>                      
                    </tr>
                  </thead>
                <tbody>
              </dsp:oparam>

              <dsp:oparam name="output">
                <dsp:setvalue param="giftlistItem" paramvalue="element"/>
              
                <%--
                  Lookup for a product repository item that corresponds to the current gift item.
                  ProductLookup droplet renders output parameter only for products that belong
                  to the current catalog and site. We don't need to perform such kind of filtering
                  here: we want to display items from other sites and catalogs as well. 
                  So the 'filterBySite' and 'filterByCatalog' parameter are false.                 
                  It is supposed that gift list contains only items from sites that are in one
                  site group with the current one according to GiftlistManager settings, so we 
                  don't perform additional filtering of items based on the shareable type.
                 --%>
                <dsp:droplet name="ProductLookup">
                  <dsp:param name="id" param="giftlistItem.productId"/>
                  <dsp:param name="filterBySite" value="false"/>
                  <dsp:param name="filterByCatalog" value="false"/>
                  <dsp:oparam name="output">
                    <%-- Display gift item details and action button for it. --%>
                    <dsp:include page="gadgets/giftListShopProductRow.jsp">
                      <dsp:param name="giftlistItem" param="giftlistItem"/>
                      <dsp:param name="giftlist" param="giftlist"/>
                      <dsp:param name="count" param="count"/>
                      <dsp:param name="size" param="size"/>
                      <dsp:param name="displaySiteIndicator" value="${displaySiteIndicator}"/>
                      <dsp:param name="errorPath" value="${errorPath}"/>
                      <dsp:param name="product" param="element"/>
                    </dsp:include>                      
                  </dsp:oparam>                
                </dsp:droplet>
              </dsp:oparam>

              <dsp:oparam name="outputEnd">
                  </tbody>
                </table>
                <%-- Display 'Find another Gift list' button that return user to gift list search page. --%>
                <c:url var="giftListSearchUrl" value="/giftlists/giftListSearch.jsp">
                  <c:param name="isNewSearch">true</c:param>
                </c:url>
                <div class="atg_store_formActions">
                  <fieldset class="atg_store_actionItems">
                    <div class="atg_store_formControls">
                      <dsp:a href="${giftListSearchUrl}" iclass="atg_store_basicButton secondary">
                        <span><fmt:message key="giftlist_giftListShop.findAnotherGiftList"/></span>
                      </dsp:a>
                    </div>
                  </fieldset>
                </div>
                <%-- Show paging links --%>
                <dsp:include page="/global/gadgets/giftAndWishListPagination.jsp">
                  <dsp:param name="arraySplitSize" value="${pageSize}"/>
                  <dsp:param name="size" param="size"/>
                  <dsp:param name="start" value="${start}"/>
                  <dsp:param name="top" value="${false}"/>
                  <dsp:param name="giftlistId" param="giftlistId"/>
                </dsp:include>
              </dsp:oparam>
            </dsp:droplet>
          
          </dsp:oparam>
          <dsp:oparam name="empty">
            <crs:messageContainer titleKey="giftlist_giftListShop.giftListNotFound" />
          </dsp:oparam>
        </dsp:droplet>
      </div>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/giftlists/giftListShop.jsp#3 $$Change: 788278 $ --%>

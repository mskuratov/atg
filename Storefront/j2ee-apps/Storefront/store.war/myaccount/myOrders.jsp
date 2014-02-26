<%--
  This page displays list of orders
  available for the current profile
  
  Required parameters:
    None
    
  Optional parameters:
    howMany
      defines how many orders display on page
    start
      start index of the order to display
    viewAll
      if true, display all available orders on a single page  
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
  <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  
  <crs:pageContainer divId="atg_store_myOrdersIntro" 
                     index="false" follow="false"
                     bodyClass="atg_store_myAccountPage atg_store_leftCol"
                     selpage="MY ORDERS">
    <jsp:body>

      <%-- Page title --%>
      <div id="atg_store_contentHeader">
        <h2 class="title">
          <fmt:message key="myaccount_myOrders.title"/>
        </h2>
      </div>
      
      <%-- Left-hand menu --%>
      <dsp:include page="gadgets/myAccountMenu.jsp">
         <dsp:param name="selpage" value="MY ORDERS" />
      </dsp:include>
      
      <%-- List of orders --%>
      <div class="atg_store_main atg_store_myAccount">
      
        <%-- Get default page size for the current site --%>
        <dsp:getvalueof var="defaultPageSize" bean="/atg/multisite/SiteContext.site.defaultPageSize"/>
      
        <%-- Set the defaults for page navigation --%>
        <dsp:getvalueof var="pageSize" value="${defaultPageSize}"/>
        <dsp:getvalueof id="howMany" param="howMany"/>
        <dsp:getvalueof id="start" param="start"/>
        <dsp:getvalueof id="viewAll" param="viewAll"/>
        
        <c:if test="${empty start && !viewAll}">
          <c:set var="start" value="1"/>
        </c:if>
      
        <c:if test="${empty howMany}">
          <c:set var="howMany" value="${pageSize}"/>
        </c:if>
        
        <%--
          Retrieve orders for the Profile
          
          Input parameters:
            userId 
              Profile Id
            sortBy
              property to sort by, we pass in 'submittedDate' to sort orders by date
            state
              one of the two 'virtual' states, could be 'open' or 'closed'. Real states
              are controlled via 'closedStates' and 'openStates' which are comma-separated
              list of 'real' states. In this case we want to return only 'closed' orders
              (i.e. submitted, processing, and so on). See /atg/commerce/order/OrderLookup
              configuration settings for details
            numOrders
              number of orders to return     
              
          Output parameters:
            result
              array of Order objects
            totalCount
              total number of order objects
            count
              size of the array of order objects
        --%>
        <dsp:droplet name="OrderLookup">
          <dsp:param name="userId" bean="Profile.id"/>
          <dsp:param name="sortBy" value="submittedDate"/>
          <dsp:param name="state" value="closed"/>
          <dsp:param name="numOrders" value="${howMany}"/>
          <%-- 
            First order query starts from 0, so set startIndex to start - 1 
            (start = 1 for the first page)
           --%>
          <dsp:param name="startIndex" value="${start-1}"/>
          
          <dsp:oparam name="output">
          
            <fmt:message var="tableSummary" key="myaccount_myOrders.tableSummary" />
            <table class="atg_store_myOrdersTable atg_store_dataTable" border="0" summary="${tableSummary}" cellspacing="0" cellpadding="0">
              <%-- Top pagination controls --%>
              <dsp:include page="/global/gadgets/pagination.jsp">
                <dsp:param name="arraySplitSize" value="${defaultPageSize}"/>
                <dsp:param name="size" param="totalCount"/>
                <dsp:param name="start" value="${start}"/>
                <dsp:param name="top" value="${true}"/>
              </dsp:include>
                
              <%-- Table header --%>  
              <thead>
                <tr>
                <th class="site" scope="col">
                  <fmt:message key="common.site"/>
                </th>
                <th scope="col">
                  <fmt:message key="myaccount_myOrders.orderNumber"/>
                </th>
                <th scope="col">
                  <fmt:message key="common.items"/>
                </th>
                 <th scope="col">
                    <fmt:message key="common.orderPlaced"/>
                 </th>
                 <th scope="col" colspan="2">
                   <fmt:message key="common.status"/>
                 </th>
  
                </tr>
              </thead>
  
              <tbody>
                <dsp:getvalueof var="orders" param="result"/>
                <dsp:getvalueof var="size" param="count" />
                
                <%-- 
                  Iterate over the collection of orders and display order information:
                  order #, site information, order status, and number of items in order
                  --%>
                <c:forEach var="order" items="${orders}" varStatus="status">
                  <dsp:param name="order" value="${order}"/>
                  <tr class="<crs:listClass count="${status.count}" size="${size}" selected="false"/>">
                    
                    <td class="site">
                      <%--
                        Display site information as icon
                       --%>
                      <dsp:include page="/global/gadgets/siteIndicator.jsp">
                        <dsp:param name="mode" value="icon"/>              
                        <dsp:param name="siteId" param="order.siteId"/>
                      </dsp:include>
                    </td>
                    
                    <%--
                      Display order Id as link to 'Order Details' page
                     --%>
                    <dsp:getvalueof var="rowTitle" param="order.omsOrderId"/>
                    <c:if test="${empty rowTitle}">
                      <dsp:getvalueof var="rowTitle" param="order.id"/>
                    </c:if>
                    
                    <td class="numerical" scope="row" abbr="${rowTitle}">
                       <fmt:message var="orderNumberLinkTitle" key="myaccount_myOrders.orderNumberLinkTitle" />
                       <dsp:a iclass="atg_store_myOrdersCancel" page="orderDetail.jsp" 
                              title="${orderNumberLinkTitle}">
                       <dsp:getvalueof var="omsOrderId" param="order.omsOrderId"/>
                       <c:choose>
                         <c:when test="${not empty omsOrderId}">
                           <dsp:valueof param="order.omsOrderId"/>
                         </c:when>
                         <c:otherwise>
                           <dsp:valueof param="order.id"/>
                         </c:otherwise>
                       </c:choose>
                       <dsp:param name="orderId" param="order.id"/>                       
                      </dsp:a>
                    </td>
                    
                    <%--
                      We don't want to include gift wrap into the total number of items in order.
                      If order contains gift wrap (which is only one per order) then we return 
                      order.totalCommerceItemCount - 1 
                     --%>
                    <td>
                      <dsp:getvalueof var="totalItems" vartype="java.lang.Long" scope="page"
                                      param="order.originalTotalItemsCount"/>
                      <dsp:getvalueof var="containsWrap" vartype="java.lang.Boolean" scope="page"
                                      param="order.containsGiftWrap"/>
                      <c:if test="${containsWrap}">
                        <c:set var="totalItems" value="${totalItems - 1}"/>
                      </c:if>
                      <c:out value="${totalItems}"/>
                      <fmt:message key="common.items"/>
                    </td>
                    
                    <%--
                      Order submitted date
                     --%>
                    <td class="date numerical">
                      <dsp:getvalueof var="submittedDate" vartype="java.util.Date" param="order.submittedDate"/>

                      <dsp:getvalueof var="dateFormat" 
                                      bean="LocaleTools.userFormattingLocaleHelper.datePatterns.shortWith4DigitYear" />  
                                      
                      <fmt:formatDate value="${submittedDate}" pattern="${dateFormat}"/>
                    </td>
    
                     <%-- Order status --%>
                     <td class="atg_store_orderState">
                       <dsp:include page="/global/util/orderState.jsp"/>
                     </td>
                     
                     <%-- Link to 'Order Details' page --%>
                     <td align="right">
                       <fmt:message var="viewOrderDetailsTitle" key="common.button.viewDetailsTitle" />
                
                       <dsp:a page="orderDetail.jsp" title="${viewOrderDetailsTitle}">
                        <dsp:param name="orderId" param="order.id"/>                        
                        <fmt:message key="common.viewDetails"/>
                      </dsp:a>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>  
            </table>
            
            <%-- Bottom pagination controls --%>
            <dsp:include page="/global/gadgets/pagination.jsp">
              <dsp:param name="arraySplitSize" value="${defaultPageSize}"/>
              <dsp:param name="size" param="totalCount"/>
              <dsp:param name="start" value="${start}"/>
              <dsp:param name="top" value="${false}"/>
            </dsp:include>              
          </dsp:oparam>

          <%-- Display message if user does not have orders --%>
          <dsp:oparam name="empty">
            <crs:messageContainer optionalClass="atg_store_myOrdersEmpty" 
                                  titleKey="myaccount_myOrders.noOrders"/>
          </dsp:oparam>
        </dsp:droplet> 
      </div>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/myOrders.jsp#2 $$Change: 788278 $--%>

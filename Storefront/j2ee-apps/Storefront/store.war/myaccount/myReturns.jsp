<%--
  This page displays list of returns
  available for the current profile
  
  Required parameters:
    None
    
  Optional parameters:
    howMany
      defines how many returns display on page
    start
      start index of the return to display
    viewAll
      if true, display all available returns on a single page  
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnRequestLookup"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/BaseReturnFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>
  
  <crs:pageContainer index="false" follow="false"
                     bodyClass="atg_store_myAccountPage atg_store_leftCol"
                     selpage="MY RETURNS">
    <jsp:body>

      <%-- Page title --%>
      <div id="atg_store_contentHeader">
        <h2 class="title">
          <fmt:message key="myaccount_myReturns.title"/>
        </h2>
      </div>
      
      <%-- Left-hand menu --%>
      <dsp:include page="gadgets/myAccountMenu.jsp">
         <dsp:param name="selpage" value="MY RETURNS" />
      </dsp:include>
      
      <%-- List of returns --%>
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
          Retrieve return requests for the Profile
          
          Input parameters:
            userId 
              Profile Id
            sortBy
              property to sort by, we pass in 'createdDate' to sort returns by date
            numOrders
              number of return requests to return     
              
          Output parameters:
            result
              array of Order objects
            totalCount
              total number of order objects
            count
              size of the array of order objects
        --%>
        <dsp:droplet name="ReturnRequestLookup">
          <dsp:param name="userId" bean="Profile.id"/>
          <dsp:param name="sortBy" value="createdDate"/>
          <dsp:param name="numReturnRequests" value="${howMany}"/>
          <%-- 
            First return request query starts from 0, so set startIndex to start - 1 
            (start = 1 for the first page)
           --%>
          <dsp:param name="startIndex" value="${start-1}"/>
          
          <dsp:oparam name="output">
          
            <fmt:message var="tableSummary" key="myaccount_myReturns.tableSummary" />
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
                    <fmt:message key="myaccount_myReturns.returnNumber"/>
                  </th>
                  <th scope="col">
                    <fmt:message key="common.returnCreated"/>
                  </th>
                  <th scope="col">
                    <fmt:message key="common.items"/>
                  </th>
                  <th scope="col" colspan="2">
                    <fmt:message key="myaccount_myReturns_returnStatus.title"/>
                  </th>
                </tr>
              </thead>
  
              <tbody>
                <dsp:getvalueof var="returns" param="result"/>
                <dsp:getvalueof var="size" param="count" />
                
                <%-- 
                  Iterate over the collection of return requests and display return information:
                  return #, site information, return status, and number of items in return
                  --%>
                <c:forEach var="return" items="${returns}" varStatus="status">
                  <dsp:param name="returnRequest" value="${return}"/>
                  <tr class="<crs:listClass count="${status.count}" size="${size}" selected="false"/>">
                    
                    <td class="site">
                      <%--
                        Display site information as icon
                       --%>
                      <dsp:include page="/global/gadgets/siteIndicator.jsp">
                        <dsp:param name="mode" value="icon"/>              
                        <dsp:param name="siteId" param="returnRequest.order.siteId"/>
                      </dsp:include>
                    </td>
                    
                    <%--
                      Display returnRequest Id as link to 'Return Details' page
                     --%>
                    <dsp:getvalueof var="rowTitle" param="returnRequest.requestId"/>                    
                    
                    <td class="numerical" scope="row" abbr="${rowTitle}">
                       <fmt:message var="returnNumberLinkTitle" key="myaccount_myReturns.returnNumberLinkTitle" />
                       <dsp:a iclass="atg_store_myOrdersCancel" page="returnDetail.jsp" 
                              title="${returnNumberLinkTitle}">
                       <dsp:valueof param="returnRequest.requestId"/>                       
                       <dsp:param name="returnRequestId" param="returnRequest.requestId"/>                       
                      </dsp:a>
                    </td>
                    
                    <%--
                      Return submission date
                     --%>
                    <td class="date numerical">
                      <dsp:getvalueof var="submittedDate" vartype="java.util.Date" param="returnRequest.authorizationDate"/>

                      <dsp:getvalueof var="dateFormat" 
                                      bean="LocaleTools.userFormattingLocaleHelper.datePatterns.shortWith4DigitYear" />  
                                      
                      <fmt:formatDate value="${submittedDate}" pattern="${dateFormat}"/>
                    </td>
                    
                    <%--
                      Display returnRequest return and exchange item counts
                     --%>
                    <td>
                      <dsp:getvalueof var="returnItemsCount" vartype="java.lang.Long" scope="page" param="returnRequest.returnItemCount"/>
                         
                      <c:if test="${returnItemsCount > 0}">   
                        <c:out value="${returnItemsCount}"/>
                        <fmt:message key="common.returned"/>
                      </c:if>   
                      <br/>
                      
                      <dsp:getvalueof var="replacementOrderId" param="returnRequest.replacementOrder.id"/>   
                      <c:if test="${not empty replacementOrderId}">
                        <dsp:getvalueof var="totalItems" vartype="java.lang.Long" scope="page"
                                      param="returnRequest.replacementOrder.originalTotalItemsCount"/>
                         <dsp:getvalueof var="containsWrap" vartype="java.lang.Boolean" scope="page"
                                      param="returnRequest.replacementOrder.containsGiftWrap"/>
                        <c:if test="${containsWrap}">
                          <c:set var="totalItems" value="${totalItems - 1}"/>
                        </c:if>
                        <c:out value="${totalItems}"/>
                                                
                        <fmt:message key="common.exchanged"/> > 
                        
                        <%-- Display 'Replacement Order' link --%>                
                        <span class="value">
                          <fmt:message var="viewOrderDetailsTitle" key="myaccount_returnDetail.fromOrder" />
                          <dsp:a page="orderDetail.jsp" title="${viewOrderDetailsTitle}">
                            <dsp:param name="orderId" value="${replacementOrderId}"/>
                            <dsp:valueof value="${replacementOrderId}"/>
                          </dsp:a>
                        </span>
                      </c:if>               
                      
                    </td>
    
                    <%-- Return status --%>
                    <td class="atg_store_orderState">
                      <dsp:include page="/global/util/returnState.jsp">
                        <dsp:param name="returnRequest" param="returnRequest"/>
                      </dsp:include>
                    </td>
                     
                    <%-- Link to 'Return Details' page --%>
                    <td align="right">
                      <fmt:message var="viewReturnDetailsTitle" key="common.button.viewDetailsTitle" />
                
                      <dsp:a page="returnDetail.jsp" title="${viewReturnDetailsTitle}">
                        <dsp:param name="returnRequestId" param="returnRequest.requestId"/>                        
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

          <%-- Display message if user does not have returns --%>
          <dsp:oparam name="empty">
            <crs:messageContainer titleKey="myaccount_myReturns.noReturns"/>
          </dsp:oparam>
        </dsp:droplet> 
      </div>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/myReturns.jsp#1 $$Change: 788278 $--%>

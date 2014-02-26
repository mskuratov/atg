<%-- 
  Outlay page which lookups the order on the bases of orderId
     
  Required parameters:
    orderId
      The order to be rendered
      
  Optional parameters:
    None
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/BaseReturnFormHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/SitesShareShareableDroplet"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/IsReturnable"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnDroplet"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/IsReturnActive"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/store/droplet/ProfileSecurityStatus"/>

  <crs:pageContainer divId="atg_store_orderDetail" 
                     index="false" follow="false"
                     bodyClass="atg_store_orderDetails atg_store_myAccountPage atg_store_threeCol"
                     selpage="MY ORDERS" >
    <jsp:body>

      <%-- Page title --%>
      <div id="atg_store_contentHeader">
        <h2 class="title">
          <fmt:message key="myaccount_orderDetail.title"/><fmt:message key="common.labelSeparator"/><dsp:valueof param="orderId"/>
        </h2>
      </div>
      
      <%-- Left-hand menu --%>
      <dsp:include page="gadgets/myAccountMenu.jsp">
        <dsp:param name="selpage" value="MY ORDERS" />
      </dsp:include>

      <%--
        Retrieve an Order object base on it's id.
        
        Input parameters:
          orderId
            orderID
          returnIncompleteOrder
            This parameter indicates that if set to "false", the order should not be
            returned when it is in an incomplete state i.e. the shopper hasn't placed the order
            yet. If this parameter is set to "true" or not specified, the order will be returned
            regardless if it has been placed or not.
         
        Open Parameters:
          output
            This parameter is rendered when an order has been returned.
          error
            This parameter is rendered when and order can't be retrieved.
        
        Output parameters:
          result
            Order object
       --%>
      <dsp:droplet name="OrderLookup">
        <dsp:param name="orderId" param="orderId"/>
        
        <dsp:oparam name="error">
          
          <crs:messageContainer>
            <jsp:attribute name="titleText">
              <dsp:valueof param="errorMsg"/>
            </jsp:attribute>
          </crs:messageContainer>

        </dsp:oparam>
        <dsp:oparam name="output">
        
          <%-- 
            Retrieve the price lists's locale used for order. It will be used to
            format prices correctly. 
            
            We can't use Profile's price list here as already submitted order can be priced 
            with price list different from current profile's price list.
          --%>
               
          <dsp:getvalueof var="commerceItems" vartype="java.lang.Object" param="result.commerceItems"/>     
          <c:if test="${not empty commerceItems}">
            <dsp:getvalueof var="priceListLocale" vartype="java.lang.String" param="result.commerceItems[0].priceInfo.priceList.locale"/>
          </c:if>
          
          <%-- Order summary --%>
          <dsp:include page="/checkout/gadgets/checkoutOrderSummary.jsp">
            <dsp:param name="order" param="result"/>
            <dsp:param name="submittedOrder" value="true"/>
            <dsp:param name="priceListLocale" value="${priceListLocale}"/>
          </dsp:include>
        
          <div class="atg_store_main atg_store_myAccount atg_store_myOrderDetail">
          
            <%-- Display errors if any --%>
            <div id="atg_store_formValidationError">        
              <dsp:include page="/global/gadgets/errorMessage.jsp">
                <dsp:param name="formHandler" bean="BaseReturnFormHandler"/>
                <dsp:param name="divid" value="errorMessage"/>
              </dsp:include>
            </div>
            
            <dsp:include page="/global/gadgets/orderSummary.jsp">
              <dsp:param name="order" param="result"/>
              <dsp:param name="hideSiteIndicator" value="false"/>
              <dsp:param name="displayProductAsLink" value="true"/>
              <dsp:param name="priceListLocale" value="${priceListLocale}"/>
            </dsp:include>             
          </div>
          
          <%-- Do not display return button and returns links for anonymous user. --%>
          
          <dsp:droplet name="ProfileSecurityStatus">
            <dsp:oparam name="anonymous">
              <c:set var="anonymousUser" value="true"/>
            </dsp:oparam>
          </dsp:droplet>
          
          <c:if test="${not anonymousUser}">
          
            <div class="atg_store_returnActions">
              <%--
                Droplet tests if 2 sites share a ShareableType. The site ids are set
                with the siteId and otherSiteId parameters. If the siteId isn't provided, 
                the current site will be used. The ShareableType can be specified by the 
                shareableTypeId parameter. 
                
                Input parameters:
                  otherSiteId
                    The side id to test
                  shareableTypeId
                    The shareable type
                    
                Open Parameters:
                  true
                    This parameter is rendered once if the sites share the shareable.
                  false
                    This parameter is rendered once if the sites do not share the shareable              
               --%>
              <dsp:droplet name="SitesShareShareableDroplet">
                <dsp:param name="shareableTypeId" value="atg.ShoppingCart"/>
                <dsp:param name="otherSiteId" param="result.siteId"/>
                <dsp:oparam name="true">
                  
                  <%--
                    This droplet deteremines if a given order or commerce item is returnable. 
                    
                    Input params:
                      order
                        Order object or order repository item 
                        
                    Open Parameters:
                      true
                        This parameter is rendered once if item is returnable.
                      false
                        This parameter is rendered once if item is not returnable.       
                  --%>            
                  <dsp:droplet name="IsReturnable">
                    <dsp:param name="order" param="result"/>
                    <dsp:oparam name="true">              
    
                      <dsp:getvalueof var="returnExist" value="false"/>  
                      <%-- Get currently active return request --%>
                      <dsp:droplet name="IsReturnActive">
                        <dsp:oparam name="true">                     
                          <dsp:getvalueof var="returnOrderId" param="returnRequest.order.id"/>   
                          <dsp:getvalueof var="currentOrderId" param="result.id"/> 
                          <%-- If active return exist and belong to current order set returnExist to true --%>
                          <c:if test="${currentOrderId eq returnOrderId}">
                            <dsp:getvalueof var="returnExist" value="true"/>  
                          </c:if>                       
                        </dsp:oparam>
                      </dsp:droplet>              
                                
                      <fmt:message var="returnButtonTitle" key="myaccount_orderDetail_returnItemsButton"/>                     
                      <c:choose>
                        <%-- If active return to this order exist just redirect to returnItemsSelection page --%>
                        <c:when test="${returnExist}">
                          <div class="atg_store_formActionItem">
                            <dsp:a page="returnItemsSelection.jsp" title="${returnButtonTitle}" iclass="atg_store_basicButton">
                              <dsp:param name="orderId" param="result.id"/>  
                              <span>
                                <dsp:valueof value="${returnButtonTitle}"/>     
                              </span>               
                            </dsp:a>
                          </div>
                        </c:when>
                        <%-- If no active return to this order exist create it and redirect to returnItemsSelection page --%>
                        <c:otherwise>
                          <dsp:form action="${pageContext.request.requestURI}" method="post">
                            <dsp:input type="hidden" bean="BaseReturnFormHandler.createReturnRequestSuccessURL"
                                       value="${pageContext.request.contextPath}/myaccount/returnItemsSelection.jsp?orderId=${param.orderId}"/>
                            <dsp:input type="hidden" bean="BaseReturnFormHandler.createReturnRequestErrorURL" 
                                       value="${pageContext.request.requestURI}?orderId=${param.orderId}"/>
                            <dsp:input type="hidden" bean="BaseReturnFormHandler.returnOrderId" 
                                       paramvalue="orderId"/>
                            <span class="atg_store_basicButton">           
                              <dsp:input type="submit" bean="BaseReturnFormHandler.createReturnRequest" 
                                         value="${returnButtonTitle}" title="${returnButtonTitle}"/>       
                            </span>                
                          </dsp:form>
                        </c:otherwise>
                      </c:choose>   
                      
                    </dsp:oparam>
                    <dsp:oparam name="false">
                      <!-- Display the message with returnable state description -->
                      <span class="atg_store_returnActionMessage"><dsp:valueof param="returnableDescription"/></span>
                  </dsp:oparam>
                 </dsp:droplet>
    
                </dsp:oparam>
                <dsp:oparam name="false">
                  <!-- The order cant be returned on this site. Display the corresponding message -->
                  <span class="atg_store_returnActionMessage"><fmt:message key="myaccount_orderDetailReturns.invalidSite"/></span>
                </dsp:oparam>
              </dsp:droplet>
            
              
              <%--
                This droplet returns the returns & exchanges associated with a given order.
                
                Input params:
                  orderId
                    The order id.
                  searchByReplacementId
                    Indicates if search should be done for replacement id as well.               
                        
                Open Parameters:
                  output
                    Rendered once if there are returns & exchanges associated with a given order found                
           
                Output parameters:
                  result
                    An array of returns & exchanges.
               --%>
              <dsp:droplet name="ReturnDroplet">
                <dsp:param name="orderId" param="result.id"/>
                <dsp:param name="resultName" value="relatedReturnRequests"/>
                <dsp:param name="searchByReplacementId" value="true"/>
                <dsp:oparam name="output">
                
                  <dsp:getvalueof var="relatedReturnRequests" param="relatedReturnRequests"/>              
                  <dsp:getvalueof var="currentOrderId" param="orderId"/>
                  
                  <%-- Render links to the returns originating from the current order --%>
                             
                  <c:set var="titleShown" value="false"/>
                  <dsp:droplet name="ForEach" array="${relatedReturnRequests}" elementName="return">             
                    <dsp:oparam name="output">
                    
                      <dsp:getvalueof var="return" param="return"/>
                      
                      <%-- Display only returns that originate from the current order. --%>
                      <c:if test="${return.order.id == currentOrderId}">
                        <div class="atg_store_returnsExchangesFromThisOrder">
                          <dsp:getvalueof var="creationDate" vartype="java.util.Date" param="return.createdDate"/>
                          
                          <c:if test="${not titleShown}">
                            <fmt:message key="myaccount_orderDetailReturns.returnsFromThisOrder"/><fmt:message key="common.labelSeparator"/></br>
                            <c:set var="titleShown" value="true"/>
                          </c:if>
                          
                          <dsp:a page="returnDetail.jsp" title="${viewReturnsDetailsTitle}">
                            <dsp:param name="returnRequestId" value="${return.repositoryId}"/>
                            <fmt:message key="myaccount_orderDetailReturns.idDateTemplate">
                              <fmt:param value="${return.repositoryId}"/>
                              <fmt:param>
                                <span class="date numerical"><fmt:formatDate value="${creationDate}" dateStyle="short"/></span>
                              </fmt:param>
                            </fmt:message>
                          </dsp:a>                  
                        </div>
                      </c:if>
                      
                    </dsp:oparam>
                  </dsp:droplet>
                         
                  
                  <%-- Render links to the exchange orders originating from the current order --%>
                  <c:set var="titleShown" value="false"/>
                  <%-- Iterate ober the relatedReturnRequests and display "Exchanges from this order:" links--%>
                  <dsp:droplet name="ForEach" array="${relatedReturnRequests}" elementName="return">             
                    <dsp:oparam name="output">
                
                      <dsp:getvalueof var="replacementOrderId" param="return.replacementOrderId"/>
                      <!-- If replacement order id exist and if it dosn't equal current order id generate link -->
                      <c:if test="${not empty replacementOrderId}">
                        <%--
                          It's a return request that is a part of an exchange. Check whether the associated exchange
                          is made from the current order or current order itself is exchange. If replacementOrderId
                          of the return reqeust is not the same as the current order ID then we are dealing with the
                          exchange from the current order.
                         --%>
                        
                        <c:choose>
                          <c:when test="${currentOrderId ne replacementOrderId}">
                            <div class="atg_store_returnsExchangesFromThisOrder">
                              <%--
                                It's a return request that is a reault of the exchange from the current order. Display a link
                                to the corresponding exchange order.
                               --%>
                              <c:if test="${not titleShown}">
                                <fmt:message key="myaccount_orderDetailReturns.exchangesFromThisOrder"/><fmt:message key="common.labelSeparator"/></br>    
                                <c:set var="titleShown" value="true"/>
                              </c:if>
                           
                              <dsp:getvalueof var="creationDate" vartype="java.util.Date" param="return.createdDate"/>                
                              <fmt:message var="viewReturnsDetailsTitle" key="common.button.viewDetailsTitle" />
                              <dsp:a page="orderDetail.jsp" title="${viewReturnsDetailsTitle}">
                                <dsp:param name="orderId" value="${replacementOrderId}"/>
                                <fmt:message key="myaccount_orderDetailReturns.idDateTemplate">
                                  <fmt:param value="${replacementOrderId}"/>
                                  <fmt:param>
                                    <span class="date numerical"><fmt:formatDate value="${creationDate}" dateStyle="short"/></span>
                                  </fmt:param>
                                </fmt:message>
                              </dsp:a>                  
                            </div>
                            
                          </c:when>
                          <c:otherwise>
                          
                            <%--
                              The current order is an exchange order itself. Store the id of the original order
                              to display the link to the original order later on the page.
                            --%>
                            <dsp:getvalueof var="originalOrderId" param="return.order.id"/>
                            
                          </c:otherwise>
                        </c:choose>
                        
                        
                      </c:if>                  
                    </dsp:oparam>
                  </dsp:droplet><%-- End of ForEach droplet --%>
                    
                  <!--
                    Check whether current order is an exchange order and if so display the link to the
                    original order.
                  -->
                  <c:if test="${not empty originalOrderId}">
                    
                    <br><fmt:message key="myaccount_orderDetailReturns.thisOrderExchange"/><fmt:message key="common.labelSeparator"/></br>
                    
                    <%-- Lookup original order to get it creation date. --%>
                    
                    <%--
                      Retrieve an Order object base on it's id.
            
                      Input parameters:
                        orderId
                          orderID
                        returnIncompleteOrder
                          This parameter indicates that if set to "false", the order should not be
                          returned when it is in an incomplete state i.e. the shopper hasn't placed the order
                          yet. If this parameter is set to "true" or not specified, the order will be returned
                          regardless if it has been placed or not.
                       
                      Open Parameters:
                        output
                          This parameter is rendered when an order has been returned.
                        error
                          This parameter is rendered when and order can't be retrieved.
                      
                      Output parameters:
                        result
                          Order object
                     --%>
                    <dsp:droplet name="OrderLookup">
                      <dsp:param name="orderId" value="${originalOrderId}"/>
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="creationDate" vartype="java.util.Date" param="result.submittedDate"/>
                      </dsp:oparam>
                    </dsp:droplet>
                                  
                    <br>
                    <fmt:message var="viewReturnsDetailsTitle" key="common.button.viewDetailsTitle" />
                    
                    <dsp:a page="orderDetail.jsp" title="${viewReturnsDetailsTitle}">
                      <dsp:param name="orderId" value="${originalOrderId}"/>
                      <fmt:message key="myaccount_orderDetailReturns.idDateTemplate">
                        <fmt:param value="${originalOrderId}"/>
                        <fmt:param>
                          <span class="date numerical"><fmt:formatDate value="${creationDate}" dateStyle="short"/></span>
                        </fmt:param>
                      </fmt:message>
                    </dsp:a>                  
                    </br>
                    
                  </c:if> 
                  
                </dsp:oparam>
              </dsp:droplet><%-- End of ReturnDroplet --%>
            </div>
          </c:if>
                  
        </dsp:oparam>
      </dsp:droplet>
      
    </jsp:body>
  </crs:pageContainer>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/orderDetail.jsp#2 $$Change: 788278 $--%>
<%--
  Return items selection page. This page lists all items in the order and allows to select any returnable items
  to include into a return request. Provides both individual items return and the whole order return. The reason
  of the return can be specified for each type of return.
  
  The page doesn't creates return request for the specified order. It is assumed that it has been already
  created before redirecting to this page. If no active return request exists for the given order ID
  an error message is displayed.
  
  Required parameters:
    orderId
      the order ID for which the active return request should be displayed.
      
  Optional parameters:
    None.
--%>
<dsp:page>

  <dsp:importbean bean="/atg/commerce/custsvc/returns/BaseReturnFormHandler"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/IsReturnActive"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/IsReturnable"/>
  <dsp:importbean bean="/atg/commerce/order/OrderLookup"/>

  <crs:pageContainer divId="atg_store_returnItems" 
                     index="false" follow="false"
                     bodyClass="atg_store_returnItem atg_store_myAccountPage atg_store_leftCol"
                     selpage="MY ORDERS" >
    <jsp:body>

      <%-- Page title --%>
      <div id="atg_store_contentHeader">
        <div id="atg_store_returnItemSelectionProgressContainer">
        
          <h2 class="title"><fmt:message key="myaccount_returnsProgressBarHeader"/><fmt:message key="common.labelSeparator"/></h2>

          <dsp:include page="/myaccount/gadgets/returnProgress.jsp">
            <dsp:param name="currentStage" value="select"/>
          </dsp:include>
        </div>
      </div>
      
      <%-- Left-hand menu --%>
      <dsp:include page="gadgets/myAccountMenu.jsp">
        <dsp:param name="selpage" value="MY ORDERS" />
      </dsp:include>
      
      <div class="atg_store_main atg_store_myAccount atg_store_myReturnItemSelection">
      
        <%-- Display form handler's error messages here. --%>
        <div id="atg_store_formValidationError">
          <dsp:include page="/myaccount/gadgets/myAccountErrorMessage.jsp">
            <dsp:param name="formHandler" bean="BaseReturnFormHandler"/>
            <dsp:param name="errorMessageClass" value="errorMessage"/>
          </dsp:include>
        </div>
        
        <dsp:getvalueof var="orderId" param="orderId"/>
        
        <c:choose>
          <c:when test="${ not empty orderId }">
          
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
                <%-- No order found for the specified ID. --%> 
                <fmt:message var="errorMsg" key="myaccount_returnItemsSelect_noOrderIdError">
                  <fmt:param><dsp:valueof param="orderId"/></fmt:param>
                </fmt:message>
                <crs:messageContainer titleText="${errorMsg}"/>
              </dsp:oparam>
              <dsp:oparam name="output">
              
                <%-- Get currently active return request --%>
                <dsp:droplet name="IsReturnActive">
                  <dsp:oparam name="true">
              
                    <%--
                      There is currently active return request. Check whether return's order ID is the
                      same as the specified order.
                    --%>
                  
                    <dsp:getvalueof var="returnOrderId" param="returnRequest.order.id"/>
                    <c:choose>
                      <c:when test="${returnOrderId == orderId}">
                    
                        <%-- Display all return items in the request grouped by shipping group --%>
                      
                        <c:url var="returnsSelectionPage" value="/myaccount/returnItemsSelection.jsp">
                          <c:param name="orderId" value="${orderId}"/>
                        </c:url>
                         
                        <c:url var="orderDetailUrl" value="/myaccount/orderDetail.jsp">
                          <c:param name="orderId" value="${orderId }"/>
                        </c:url>
                          
                          <%-- Include form for individual items selection --%>
                          <dsp:form action="${returnsSelectionPage}" id="selectReturnItems"
                                    formid="selectReturnItems" method="post">
                                  
                                  
                            <%-- Iterate through ReturnShippingGroups of return request. --%>
                            <dsp:getvalueof var="returnShippingGroupList" bean="BaseReturnFormHandler.returnRequest.shippingGroupList"/>
                            
                            <c:forEach var="returnShippingGroup" items="${returnShippingGroupList}"
                                       varStatus="shippingGroupListStatus">
                                        
                              <c:set var="returnItems" value="${returnShippingGroup.itemList}" />  
        
                              <c:if test="${not empty returnItems}">                    
        
                                <%-- Display shipping group's address and shipping method --%>
                                <div id="atg_store_shipmentInfoContainer">
                                  <div class="atg_store_shipmentInfo">
                                
                                    <dsp:include page="/global/gadgets/orderSingleShippingInfo.jsp">
                                      <dsp:param name="shippingGroup" value="${returnShippingGroup.shippingGroup}"/>
                                    </dsp:include>
                            
                                  </div>
                                </div>
                            
                                <%-- Display all return items of the current ReturnShippingGroup --%>
                                 
                                <c:set var="returnableItemsCounter" value="0"/>
                                <c:set var="nonReturnableItemsCounter" value="0"/>
                            
                                <%--
                                  Iterate through all return items in the ReturnShippingGroup and display only those that are
                                  returnable. Non-returnable items will be displayed separately.
                                --%>
                                <c:forEach var="returnItem" items="${returnItems}" varStatus="itemsStatus">
        
                                  <%-- Do not render gift wrap items. They are not returnable. --%>
                                  <c:if test="${returnItem.commerceItem.commerceItemClassType != 'giftWrapCommerceItem'}">
                                  
                                    <dsp:droplet name="IsReturnable">
                                      <dsp:param name="item" value="${returnItem.commerceItem}"/>
                                      <dsp:oparam name="true">
                                        <c:set var="returnable" value="${true}"/>
                                        <c:set var="returnableItemsCounter" value="${returnableItemsCounter + 1}"/>
                                      </dsp:oparam>
                                      <dsp:oparam name="false">
                                        <c:set var="returnable" value="${false}"/>
                                        <c:set var="nonReturnableItemsCounter" value="${nonReturnableItemsCounter + 1}"/>
                                      </dsp:oparam>
                                    </dsp:droplet>
                                      
                                    <c:if test="${returnable}">
                                    
                                      <c:if test="${returnableItemsCounter == 1 }">
                                      
                                        <%-- If it's a first returnable item display table header here. --%>
                                        <fmt:message var="tableSummary" key="myaccount_returnItems_tableSummary"/>
                                        <table id="atg_store_itemTable" summary="${tableSummary}">                                      
                                          <thead>
                                            <tr>
                                             <th class="site" scope="col"><fmt:message key="common.site" /></th>
                                              <th class="item" colspan="2" scope="col"><fmt:message key="common.item" /></th>
                                              <th class="returnQuantity" scope="col"><fmt:message key="myaccount_returnItemsSelect_returnQuantity"/></th>
                                              <th class="returnReason" scope="col"><fmt:message key="myaccount_returnItemsSelect_returnReason"/></th>
                                            </tr>
                                          </thead>
                                          <tbody>
                                        
                                      </c:if>
                                  
                                      <dsp:include page="/myaccount/gadgets/returnItemRenderer.jsp">
                                        <dsp:param name="returnItem" value="${returnItem}" />
                                        <dsp:param name="returnable" value="${true}" />
                                        <dsp:param name="shippingGroupIndex" value="${shippingGroupListStatus.index}" />
                                        <dsp:param name="itemIndex" value="${itemsStatus.index}" />
                                        <dsp:param name="activeReturn" value="${true}" />
                                      </dsp:include>
                                      
                                      
                                    </c:if>
                                  </c:if>
                                </c:forEach>
                                
                                <%-- Close table in case there were any returnable items to list --%>
                                <c:if test="${returnableItemsCounter > 0}">
                                    </tbody>
                                  </table>
                                </c:if>
                                    
                                <%--
                                  If there are non-returnable items iterate through return items of ReturnShippingGroup
                                  once more in order to display non-returnable items.
                                --%>
                                
                                <c:if test="${nonReturnableItemsCounter > 0}">
                                
                                  <%-- Display 'Non-Returnable items header here.--%>
                                  <h4 class="atg_store_returnsTableTitle"><fmt:message key="myaccount_returnItemsSelect_nonReturnableItemsTitle"/></h4>
                                  
                                  <fmt:message var="tableSummary" key="myaccount_returnItems_tableSummary"/>
                                  <table id="atg_store_itemTable" summary="${tableSummary}">
                                  
                                    <thead>
                                      <tr>
                                        <th class="site" scope="col"><fmt:message key="common.site" /></th>
                                        <th class="item" colspan="2" scope="col"><fmt:message key="common.item" /></th>
                                        <th class="returnReason" colspan="2" scope="col"><fmt:message key="myaccount_returnItemsSelect_returnReason"/></th>
                                      </tr>
                                    </thead>
                                    <tbody>                             
                                      
                                    
                                      <c:forEach var="returnItem" items="${returnItems}" varStatus="itemsStatus">
              
                                        <%-- Do not render gift wrap items. They are not returnable. --%>
                                        <c:if test="${returnItem.commerceItem.commerceItemClassType != 'giftWrapCommerceItem'}">
                                        
                                          <dsp:droplet name="IsReturnable">
                                            <dsp:param name="item" value="${returnItem.commerceItem}"/>
                                            <dsp:oparam name="true">
                                              <c:set var="returnable" value="${true}"/>
                                            </dsp:oparam>
                                            <dsp:oparam name="false">
                                              <c:set var="returnable" value="${false}"/>
                                              <dsp:getvalueof param="returnableDescription" var="returnableDescription"/>
                                            </dsp:oparam>
                                          </dsp:droplet>
                                         
                                          <c:if test="${not returnable}">
                                        
                                            <dsp:include page="/myaccount/gadgets/returnItemRenderer.jsp">
                                              <dsp:param name="returnItem" value="${returnItem}" />
                                              <dsp:param name="returnable" value="${false}" />
                                              <dsp:param name="returnableDescription" value="${returnableDescription}" />
                                            </dsp:include>
                                          </c:if>
                                        </c:if>
                                      </c:forEach>
                                  
                                    </tbody>
                                
                                  </table>
                                </c:if>
                                    
                              </c:if>
                            
                            </c:forEach>
        
                            <%-- Specify form handler's success and error URLs. --%>
                            <dsp:input bean="BaseReturnFormHandler.selectItemsSuccessURL" type="hidden"
                                       value="${pageContext.request.contextPath}/myaccount/confirmReturn.jsp" />
                                       
                            <dsp:input bean="BaseReturnFormHandler.selectItemsErrorURL" type="hidden"
                                       value="${returnsSelectionPage}" />
        
                            <%-- Returns Selection submit button --%>
                            <fmt:message var="returnSelectedItemsText" 
                                         key="myaccount_returnItemsSelect_returnSelectedItemsButton"/>
                            <div class="returnAction">
                              <span class="atg_store_basicButton">
                              <dsp:input type="submit"
                                         bean="BaseReturnFormHandler.selectItems" 
                                         value="${returnSelectedItemsText}"
                                         title="${returnSelectedItemsText}"/>
                              </span>
                            </div>
                            
                          </dsp:form>
                        
                        
                        <%--
                          Form for the whole order return. Upon form submission all returnable items
                          will be included into the return request.
                        --%>
                        <div class="returnAction">
                          <dsp:form action="${returnsSelectionPage}" id="returnAllOrder"
                                    formid="returnAllOrder" method="post">
                             
                            <%-- Specify form handler's success and error URLs. --%>       
                            <dsp:input bean="BaseReturnFormHandler.selectItemsSuccessURL" type="hidden"
                                       value="${pageContext.request.contextPath}/myaccount/confirmReturn.jsp" />
                                       
                            <dsp:input bean="BaseReturnFormHandler.selectItemsErrorURL" type="hidden"
                                       value="${returnsSelectionPage}" />
                                       
                            <dsp:input bean="BaseReturnFormHandler.selectAllItems" type="hidden"
                                       value="${true}" />
                             
                            <fmt:message var="returnReasonLabel" key="myaccount_returnItemsSelect_returnReason"/>

                            <%-- Select control for selecting return reason --%>           
                            <dsp:select bean="BaseReturnFormHandler.selectAllItemsReturnReason" title="${returnReasonLabel}">
                              <dsp:option value=""><fmt:message key="myaccount_returnItemsSelect_selectReturnReason"/></dsp:option>
                              
                              <%-- Iterate through all available return reasons --%>
                              <dsp:droplet name="ForEach">
                                <dsp:param bean="BaseReturnFormHandler.reasonCodes" name="array"/>
                                <dsp:param name="elementName" value="reasonCode"/>
                                <dsp:param name="sortProperties" value="+readableDescription"/>
                                <dsp:oparam name="output">
                                  <dsp:option paramvalue="reasonCode.repositoryId">
                                  <dsp:valueof param="reasonCode.readableDescription"/>
                                  </dsp:option>
                                </dsp:oparam>
                              </dsp:droplet>
                            </dsp:select>
                                         
                            <%-- Returns Selection submit button --%>
                            <fmt:message var="returnAllItemsText" 
                                         key="myaccount_returnItemsSelect_returnAllItemsButton"/>                             
                            <span class="atg_store_basicButton">
                            <dsp:input type="submit"
                                       bean="BaseReturnFormHandler.selectItems" 
                                       value="${returnAllItemsText}"
                                       title="${returnAllItemsText}"/>
                            </span>
                          </dsp:form>
                        </div>
                        
                        <%-- Cancel Return form --%>
                        <div class="returnAction">
                        
                          <dsp:form action="${returnsSelectionPage}" id="cancelOrder"
                                    formid="cancelOrder" method="post">
                            
                            <%-- Specify 'Cancel Return' handler's success and error URLs. --%>        
                            <dsp:input bean="BaseReturnFormHandler.cancelReturnRequestSuccessURL" type="hidden"
                                       value="${orderDetailUrl}" />
                                       
                            <dsp:input bean="BaseReturnFormHandler.cancelReturnRequestErrorURL" type="hidden"
                                       value="${returnsSelectionPage}" />
                                       
                            <%-- Cancel Return submit button --%>
                            <fmt:message var="cancelReturnText" 
                                         key="myaccount_returnItemsSelect_cancelReturnButton"/>                             
                            <dsp:input bean="BaseReturnFormHandler.cancelReturnRequest"
                                       type="submit"
                                       value="${cancelReturnText}"
                                       iclass="atg_store_textButton"/>     
                                    
                          </dsp:form>
                        </div>
                        
                      </c:when>
                      <c:otherwise>
                      
                        <%--
                          The currently active return is not for the same order ID as specified in the page's
                          parameter. This can happen if user goes to this page bypassing normal navigation from
                          order detail page or for some other error. Display error message.
                        --%>
                        <crs:messageContainer titleKey="myaccount_returnItemsSelect_noActiveReturnError"/>
                        
                      </c:otherwise>
                    </c:choose>
                  </dsp:oparam>
                  <dsp:oparam name="false">
                  
                    <%-- No active return request is found. Display an error message. --%>
                    <crs:messageContainer titleKey="myaccount_returnItemsSelect_noActiveReturnError"/>
                     
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet>
          </c:when>
          <c:otherwise>
          
            <%-- No order ID is specified for the page. --%>
            
            <fmt:message var="errorMsg" key="myaccount_returnItemsSelect_noOrderIdError">
              <fmt:param><dsp:valueof param="orderId"/></fmt:param>
            </fmt:message>
            
            <crs:messageContainer titleText="${errorMsg}"/>
                
          </c:otherwise>
        </c:choose>
          
      </div>
      
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/returnItemsSelection.jsp#1 $$Change: 788278 $--%>
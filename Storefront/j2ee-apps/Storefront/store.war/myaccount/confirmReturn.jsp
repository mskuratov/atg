<%--
  A Review page for the return request. This page lists all items selected for the return and includes action buttons
  for return submission, canceling return or return modification.All return items are displayed split by 
  commerce item / shipping group relationships.
  
  Required parameters:
    None.
      
  Optional parameters:
    None.
--%>
<dsp:page>

  <dsp:importbean bean="/atg/commerce/custsvc/returns/BaseReturnFormHandler"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/IsReturnActive"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

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
            <dsp:param name="currentStage" value="confirm"/>
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
        
        <%-- Get currently active return request --%>
        <dsp:droplet name="IsReturnActive">
          <dsp:oparam name="true">
      
            <%--
              There is currently active return request.
            --%>
          
            <dsp:getvalueof var="returnOrderId" param="returnRequest.order.id"/>
            
            <%-- Display all return items in the request grouped by shipping group --%>
          
            <c:url var="returnsSelectionUrl" value="/myaccount/returnItemsSelection.jsp">
              <c:param name="orderId" value="${returnOrderId}"/>
            </c:url>
             
            <c:url var="orderDetailUrl" value="/myaccount/orderDetail.jsp">
              <c:param name="orderId" value="${returnOrderId }"/>
            </c:url>
            
            <c:url var="confirmReturnResponseUrl" value="/myaccount/confirmReturnResponse.jsp"/>
          
            <div id="atg_store_returnReview">
            
              <dsp:include page="/myaccount/gadgets/returnItems.jsp">
                <dsp:param name="returnRequest" param="returnRequest" />
                <dsp:param name="activeReturn" value="true" />
              </dsp:include>
               
            </div>
            
            <div id="atg_store_returnReviewSummary">
            
              <%-- Display refund payment methods --%>
              <dsp:include page="/myaccount/gadgets/refundMethods.jsp">
                <dsp:param name="returnRequest" param="returnRequest"/>
              </dsp:include>
            
              <%-- Original order summary --%>
              <dsp:include page="/myaccount/gadgets/returnOrderSummary.jsp">
                <dsp:param name="order" param="returnRequest.order"/>
              </dsp:include>
              
              <%-- Refund summary --%>
              <dsp:include page="/myaccount/gadgets/refundSummary.jsp">
                <dsp:param name="returnRequest" param="returnRequest"/>
                <dsp:param name="isActiveReturn" value="true"/>
              </dsp:include>
            
            </div>
            
            <%-- Form for return submission. --%>
            <div id="atg_store_submitReturn">
              <dsp:form action="${confirmReturnResponseUrl}" id="submitReturn"
                        formid="submitReturn" method="post">
                 
                <%-- Specify form handler's success and error URLs. --%>       
                <dsp:input bean="BaseReturnFormHandler.confirmReturnSuccessURL" type="hidden"
                           value="${confirmReturnResponseUrl}" />
                           
                <dsp:input bean="BaseReturnFormHandler.confirmReturnErrorURL" type="hidden"
                           value="${pageContext.request.requestURI}" />
                             
                <%-- Returns Selection submit button --%>
                <fmt:message var="submitReturnText" 
                             key="myaccount_returnReview_submitReturnButton"/>
                <span class="atg_store_basicButton">
                  <dsp:input bean="BaseReturnFormHandler.confirmReturn"
                    type="submit"
                    value="${submitReturnText}" />
                </span>
                        
              </dsp:form>
            </div>
            
            <%-- Addition action buttons: Modify Return link and Cancel Return button. --%>
            <div id="atg_store_returnActionButtons">
              <ul>
                <%-- Link back to the Return Items Selection page --%>
                <li>
                  <dsp:a href="${returnsSelectionUrl}">
                    <fmt:message key="myaccount_returnReview_modifyReturnButton"/>
                  </dsp:a>
                </li>
                <li>&nbsp;|&nbsp;</li>
                <li>
                  <%-- Cancel Return form --%>
                  <dsp:form action="${orderDetailUrl}" id="cancelOrder"
                        formid="cancelOrder" method="post">
                
                    <%-- Specify 'Cancel Return' handler's success and error URLs. --%>        
                    <dsp:input bean="BaseReturnFormHandler.cancelReturnRequestSuccessURL" type="hidden"
                               value="${orderDetailUrl}" />
                               
                    <dsp:input bean="BaseReturnFormHandler.cancelReturnRequestErrorURL" type="hidden"
                               value="${returnsSelectionUrl}" />
                               
                    <%-- Cancel Return submit button --%>
                    <fmt:message var="cancelReturnText" 
                                 key="myaccount_returnItemsSelect_cancelReturnButton"/>                             
                    <dsp:input bean="BaseReturnFormHandler.cancelReturnRequest" type="submit"
                               value="${cancelReturnText}"
                               iclass="atg_store_textButton" />      
                            
                  </dsp:form>
                
                </li>
              
              </ul>
              
            </div>
             
          </dsp:oparam>
          <dsp:oparam name="false">
          
            <%-- No active return request is found. Display an error message. --%>
            <fmt:message key="myaccount_returnItemsSelect_noActiveReturnError">
              <fmt:param><fmt:message key="myaccount_orderDetail_returnItemsButton"/></fmt:param>
            </fmt:message>
             
          </dsp:oparam>
        </dsp:droplet>
        
      </div>
      
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/confirmReturn.jsp#1 $$Change: 788278 $--%>
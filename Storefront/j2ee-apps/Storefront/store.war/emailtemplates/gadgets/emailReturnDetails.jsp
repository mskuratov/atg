<%--
  This gadget renders the order return information:
    - return number
    - link to the return details on the site
    - link to the original order 
    - date of return submission
    - return's state
    - return items, quantities and return reasons
    - refund payment methods
    - refund summary
    - original order summary
    
  The gadget is intended to be used inside email templates and has inline styles.
  
  Required parameters:
    returnRequest
      The return request to render information of.
    httpServer
      URL prefix that includes protocol, host and port
      
  Optional parameters:
    locale
      Locale to append to the URL as URL parameter so that the pages to which user navigates using the URL
      will be rendered in the same language as the email.
    returnItemToQuantityReceived
      The map where the keys are return Item IDs and values are received quantity of the item. This parameter is
      specified only for Items Received Confirmation email. 
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  
  <dsp:getvalueof var="returnRequestId" param="returnRequest.requestId"/>
  <dsp:getvalueof var="returnItemToQuantityReceived" param="returnItemToQuantityReceived"/>
  
  <table border="0" cellpadding="0" cellspacing="0" width="100%" summary="" role="presentation"
             style="margin-bottom:4px;color:#666;font-family:Tahoma,Arial,sans-serif;font-size:14px; border-collapse: collapse;">
             
    <tr>
      <td colspan="5" 
          style="color:#666;font-family:Tahoma,Arial,sans-serif;font-size:14px;padding-bottom:20px">
      
        <%-- Display link to return details only for registered users --%>
        <dsp:getvalueof var="isTransient" bean="Profile.transient"/>
        <c:if test="${!isTransient}">
          <fmt:message key="emailtemplates_returnConfirmation.track">
            <fmt:param>
              <fmt:message var="linkText" key="emailtemplates_returnConfirmation.returnDetails"/>
              <dsp:include page="/emailtemplates/gadgets/emailSiteLinkDisplay.jsp">
                <dsp:param name="path" value="/myaccount/gadgets/loginReturnDetail.jsp"/>
                <dsp:param name="queryParams" value="returnRequestId=${returnRequestId}"/>
                <dsp:param name="locale" param="locale"/>
                <dsp:param name="httpServer" param="httpServer"/>
                <dsp:param name="linkText" value="${linkText}"/>
              </dsp:include>
            </fmt:param>
          </fmt:message>
        </c:if>
      </td>
    </tr>
    
    <%-- Return Number --%>
    <tr>
      <td style="color:#666;font-family:Tahoma,Arial,sans-serif;font-size:16px;font-weight:bold;padding-bottom:10px;">
        <fmt:message key="emailtemplates_returnConfirmation.returnNumber"/>
      </td>
      <td style="color:#000;font-family:Tahoma,Arial,sans-serif;font-size:12px;font-weight:bold;padding-bottom:10px;">
        <dsp:valueof value="${returnRequestId}"/>
      </td>
    </tr>
    
    <%-- Link to the original order. --%>
    <tr>
      <td style="color:#666;font-family:Tahoma,Arial,sans-serif;font-size:16px;font-weight:bold;padding-bottom:10px;">
        <fmt:message key="emailtemplates_returnConfirmation.originalOrderNumber"/>
      </td>
      <td style="color:#000;font-family:Tahoma,Arial,sans-serif;font-size:12px;font-weight:bold;padding-bottom:10px;">
        <%-- Display link to order details only for registered users --%>
        <dsp:getvalueof var="isTransient" bean="Profile.transient"/>
        <dsp:getvalueof var="orderId" param="returnRequest.order.id"/>
        <c:choose>
          <c:when test="${!isTransient}">
            <dsp:include page="/emailtemplates/gadgets/emailSiteLinkDisplay.jsp">
              <dsp:param name="path" value="/myaccount/gadgets/loginOrderDetail.jsp"/>
              <dsp:param name="queryParams" value="orderId=${orderId}"/>
              <dsp:param name="locale" param="locale"/>
              <dsp:param name="httpServer" param="httpServer"/>
              <dsp:param name="linkText" value="${orderId}"/>
            </dsp:include>
          </c:when>
          <c:otherwise>
            <dsp:valueof value="${orderId}"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    
    <%-- Return's submission date. --%>
    <tr>
      <td style="color:#666;font-family:Tahoma,Arial,sans-serif;font-size:16px;font-weight:bold;padding-bottom:10px;padding-right:5px;white-space:nowrap;">
        <fmt:message key="emailtemplates_returnConfirmation.placedOn"/>
      </td>
      <td style="color:#000;font-family:Tahoma,Arial,sans-serif;font-size:12px;font-weight:bold;padding-bottom:10px;white-space:nowrap;">
        <dsp:getvalueof var="submittedDate" vartype="java.util.Date" param="returnRequest.authorizationDate"/>
        <fmt:formatDate value="${submittedDate}" type="both" dateStyle="long"/>
      </td>
    </tr>
    
    <%-- Return's current state --%>
    <tr>
      <td style="color:#666;font-family:Tahoma,Arial,sans-serif;font-size:16px;font-weight:bold;padding-bottom:10px;">
        <fmt:message key="emailtemplates_returnConfirmation.status"/>
      </td>
      <td style="color:#000;font-family:Tahoma,Arial,sans-serif;font-size:12px;font-weight:bold;padding-bottom:10px;">
        <dsp:getvalueof var="status" param="returnRequest.state"/>
        <c:choose>
          <c:when test="${status eq 'PENDING_CUSTOMER_ACTION' }">
          
            <%-- For just placed return display 'Return Placed' status. --%>
            <fmt:message key="emailtemplates_returnConfirmation.returnPlaced"/>
            
          </c:when>
          <c:otherwise>
          
            <dsp:include page="/global/util/returnState.jsp">
              <dsp:param name="returnRequest" param="returnRequest"/>
            </dsp:include>
            
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    
    <tr>
      <td colspan="5">
        <hr size="1">
      </td>
    </tr>
    
    <dsp:getvalueof var="returnItemList" param="returnRequest.returnItemList" />
    <c:if test="${not empty returnItemList}">
    
      <%-- Determine return's price list locale. --%>
      <dsp:tomap var="priceList" param="returnRequest.returnItemList[0].commerceItem.priceInfo.priceList" />
      <dsp:getvalueof var="priceListLocale" vartype="java.lang.String" value="${priceList.locale}"/>
    
    
      <%-- Iterate through ReturnShippingGroups of return request. --%>
      <dsp:getvalueof var="returnShippingGroupList" param="returnRequest.shippingGroupList"/>
          
      <c:forEach var="returnShippingGroup" items="${returnShippingGroupList}"
                 varStatus="shippingGroupListStatus">
                           
        <%-- Display all return items of the current ReturnShippingGroup --%>
      
        <c:set var="returnItemsCounter" value="0" />
        <c:set var="shippingGroupId" value="${returnShippingGroup.shippingGroup.id }"/>
     
        <c:forEach var="returnItem" items="${returnItemList}">
        
          <%-- Check whether this is the Received Items Confirmation email --%>
          <c:if test="${not empty returnItemToQuantityReceived }">
            <dsp:getvalueof var="receivedQuantity" param="returnItemToQuantityReceived.${returnItem.id}"/>
          </c:if>

          <%--
            Check whether return item belongs to the current shipping group.
            For Received Items Confirmation email additionally check whether received quantity is not
            empty. If all checks are passed displayed return item.
          --%>
          <c:if test="${returnItem.shippingGroupId == shippingGroupId && (empty returnItemToQuantityReceived or (not empty receivedQuantity))}">
       
            <c:set var="returnItemsCounter" value="${returnItemsCounter + 1}" />
       
            <c:if test="${returnItemsCounter == 1}">
              <%--
                It's the first return item in the shipping group. So display shipping group details
                and table header.
              --%>
           
              <%-- Display shipping group's address and shipping method --%>
        
              <dsp:include page="/emailtemplates/gadgets/emailShippingAddressRenderer.jsp">
                <dsp:param name="shippingGroup" value="${returnShippingGroup.shippingGroup}"/>
              </dsp:include>
            
              <%-- Table header for items list --%>
              <fmt:message var="tableSummary" key="emailtemplates_returns.tableSummary"/>
              
              <tr>
                <td colspan="5"> 
                  <table summary="${tableSummary}" cellspacing="0"  cellpadding="2">
                    <tr style="border-collapse: collapse;">
                      <th scope="col" style="width:60px;color:#0a3d56;font-family:Tahoma,Arial,sans-serif;font-size:14px;font-weight:bold; border-bottom: 1px solid #666666;">
                        <fmt:message key="emailtemplates_returns.itemSite"/>
                      </th>  
                      <th scope="col" style="color:#0a3d56;font-family:Tahoma,Arial,sans-serif;font-size:14px;font-weight:bold;width:237px; border-bottom: 1px solid #666666;">
                        <fmt:message key="emailtemplates_returns.itemName"/>
                      </th>
                      <th scope="col" align="left" style="text-align:left;color:#0a3d56;font-family:Tahoma,Arial,sans-serif;font-size:14px;font-weight:bold;width:150px; border-bottom: 1px solid #666666;">
                        <fmt:message key="emailtemplates_returns.itemReturnQuantity"/>
                      </th>
                      <th scope="col" style="color:#0a3d56;font-family:Tahoma,Arial,sans-serif;font-size:14px;font-weight:bold; width: 65px; border-bottom: 1px solid #666666;">
                        <fmt:message key="emailtemplates_returns.itemTotalRefund"/>
                      </th>
                      <th scope="col" align="right" style="color:#0a3d56;font-family:Tahoma,Arial,sans-serif;font-size:14px;font-weight:bold; width: 170px; border-bottom: 1px solid #666666;">
                        <fmt:message key="emailtemplates_returns.itemReturnReason"/>
                      </th>
                    </tr> 
            
            </c:if>
            
            <%-- Return Item details row --%>
          
            <dsp:include page="/emailtemplates/gadgets/emailReturnItemRenderer.jsp">
              <dsp:param name="returnItem" value="${returnItem}"/>
              <dsp:param name="priceListLocale" value="${priceListLocale}"/>
              <dsp:param name="httpServer" param="httpServer"/>
              <dsp:param name="locale" param="locale"/>
            </dsp:include>
          
          </c:if>
        
        </c:forEach>
        
        <%-- If there were return items in this shipping group include closing tags. --%>
        <c:if test="${returnItemsCounter > 0}">
              </table>
            </td>
          </tr>
        </c:if>
      
      </c:forEach>
    
    </c:if>
    
    <%-- Do not display return summary for Return Items Recieved email template --%>
    
    <c:if test="${empty returnItemToQuantityReceived}">
    
      <tr>
        <td colspan="5">&nbsp;</td>
      </tr>

      <tr>
      
        <%-- Refund methods --%>
        <td colspan="3" valign="top">
          <dsp:include page="/emailtemplates/gadgets/emailRefundMethods.jsp">
            <dsp:param name="returnRequest" param="returnRequest"/>    
            <dsp:param name="priceListLocale" value="${priceListLocale}"/>
          </dsp:include>
        </td>
  
        <%-- Refund summary --%>
        <td colspan="2" valign="top">
          
          <dsp:include page="/emailtemplates/gadgets/emailRefundSummary.jsp">
            <dsp:param name="returnRequest" param="returnRequest"/>    
            <dsp:param name="priceListLocale" value="${priceListLocale}"/>
          </dsp:include>
          
        </td>
       
      </tr>
      
      <tr>
        <td colspan="5">
          <hr size="1">
        </td>
      </tr>
    
    </c:if>
    
   
    
  </table>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/emailReturnDetails.jsp#2 $$Change: 788846 $--%>
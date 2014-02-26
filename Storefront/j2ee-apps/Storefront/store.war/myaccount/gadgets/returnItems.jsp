<%--
  This gadget renders all selected for return items grouped by shipping group.
  
  Required parameters:
    returnRequest
      The return request to display return items for.
      
  Optional parameters:
    activeReturn
      Indicates whether return request is currently active.
    priceListLocale
      Specifies a locale in which to format the price (as string).
      If not specified, locale will be taken from profile price list (Profile.priceList.locale).
--%>
<dsp:page>
  

  <%-- Iterate through ReturnShippingGroups of return request. --%>
  <dsp:getvalueof var="returnShippingGroupList" param="returnRequest.shippingGroupList"/>
              
  <c:forEach var="returnShippingGroup" items="${returnShippingGroupList}"
             varStatus="shippingGroupListStatus">
                               
    <%-- Display all return items of the current ReturnShippingGroup --%>
                  
    <dsp:getvalueof var="returnItemList" param="returnRequest.returnItemList" />
    <c:set var="returnItemsCounter" value="0" />
    <c:set var="shippingGroupId" value="${returnShippingGroup.shippingGroup.id }"/>
         
    <c:forEach var="returnItem" items="${returnItemList}">

      <%--
        Check whether the quantity to return is bigger than 0. If so we will display
        the item. If quantity is 0 than item is not included into the return.
      --%>
      <c:if test="${returnItem.quantityToReturn > 0 and returnItem.shippingGroupId == shippingGroupId}">
           
        <c:set var="returnItemsCounter" value="${returnItemsCounter + 1}" />
           
        <c:if test="${returnItemsCounter == 1}">
          <%--
            It's the first return item in the shipping group. So display shipping group details
            and table header.
          --%>
               
          <%-- Display shipping group's address and shipping method --%>
          <div id="atg_store_shipmentInfoContainer">
            <div class="atg_store_shipmentInfo">
            
              <dsp:include page="/global/gadgets/orderSingleShippingInfo.jsp">
                <dsp:param name="shippingGroup" value="${returnShippingGroup.shippingGroup}"/>
              </dsp:include>
        
            </div>
          </div>
            
          <fmt:message var="tableSummary" key="myaccount_returnItems_tableSummary"/>
          <table id="atg_store_itemTable" summary="${tableSummary}">
  
            <%-- Display heading of the return items table. --%>
            <thead>
              <tr>
                <th class="site" scope="col"><fmt:message key="common.site" /></th>
                <th class="item" colspan="2" scope="col"><fmt:message key="common.item" /></th>
                <th class="returnQuantity" scope="col"><fmt:message key="myaccount_returnItemsSelect_returnQuantity"/></th>
                <th class="refundTotal" scope="col"><fmt:message key="myaccount_returnReview_totalRefund" /></th>
                <th class="returnReason" scope="col"><fmt:message key="myaccount_returnItemsSelect_returnReason"/></th>
              </tr>
            </thead>
            
            <tbody>
          
        </c:if>
          
        <dsp:include page="/myaccount/gadgets/returnItemRenderer.jsp">
          <dsp:param name="returnItem" value="${returnItem}" />
          <dsp:param name="reviewMode" value="true" />
          <dsp:param name="activeReturn" param="activeReturn" />
          <dsp:param name="priceListLocale" param="priceListLocale" />
        </dsp:include>
        
      </c:if>
    </c:forEach>
    
    <%-- Check whether any return items were displayed for this shipping group and if so close the table. --%>
    <c:if test="${returnItemsCounter > 0}">
          
        </tbody>
      </table>
        
    </c:if>
  
  </c:forEach>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/returnItems.jsp#1 $$Change: 788278 $--%>
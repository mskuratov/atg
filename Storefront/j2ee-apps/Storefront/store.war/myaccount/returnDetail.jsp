<%--
 his page lists all items of the return. All return items are displayed split by 
  commerce item / shipping group relationships.
  
  Required parameters:
    returnRequestId
      The return request id
      
  Optional parameters:
    None.
--%>
<dsp:page>

  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnRequestLookup"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>

  <crs:pageContainer divId="atg_store_returnItems" 
                     index="false" follow="false"
                     bodyClass="atg_store_returnItem atg_store_myAccountPage atg_store_leftCol"
                     selpage="MY RETURNS" >
    <jsp:body>

      <%-- Page title --%>
      <div id="atg_store_contentHeader">
        <div id="atg_store_checkoutProgressContainer">
        
          <h2 class="title"><fmt:message key="myaccount_returnDetail.title"/><fmt:message key="common.labelSeparator"/>
            <dsp:valueof param="returnRequestId"/>          
          </h2>
          
        </div>
      </div>
      
      <%-- Left-hand menu --%>
      <dsp:include page="gadgets/myAccountMenu.jsp">
        <dsp:param name="selpage" value="MY RETURNS" />
      </dsp:include>
      
      <div class="atg_store_main atg_store_myAccount">

      <%--
          Retrieve return requests for the given id
          
          Input parameters:
            returnRequestId 
              The id of the return to get
           
              
          Output parameters:
            result
              The return request object
        --%>
        <dsp:droplet name="ReturnRequestLookup">
          <dsp:param name="returnRequestId" param="returnRequestId"/>
          <dsp:oparam name="output">
            <dsp:setvalue param="returnRequest" paramvalue="result"/>             
             
            <div class="atg_store_returnDetailsHeader">
              <%-- Display return request status --%>
              <span class="label"><fmt:message key="common.status"/><fmt:message key="common.labelSeparator"/></span>
              <span class="value">
                <dsp:include page="/global/util/returnState.jsp">
                  <dsp:param name="returnRequest" param="returnRequest"/>
                </dsp:include>
              </span>
             
              <%-- Display submission date --%>
              <span class="label">
                <fmt:message key="common.returnCreated"/><fmt:message key="common.labelSeparator"/>
              </span>
              
              <dsp:getvalueof var="submittedDate" vartype="java.util.Date" 
                              param="returnRequest.authorizationDate"/>
              <dsp:getvalueof var="dateFormat" 
                              bean="LocaleTools.userFormattingLocaleHelper.datePatterns.shortWith4DigitYear" />
                                
              <span class="value">
                <fmt:formatDate value="${submittedDate}" pattern="${dateFormat}"/>
              </span>
            
              <%-- Display 'From Order' link --%>             
              <dsp:getvalueof var="returnOrderId" param="returnRequest.order.id"/>
              
              <span class="label">
                <fmt:message key="myaccount_returnDetail.fromOrder"/>
                <fmt:message var="viewOrderDetailsTitle" key="myaccount_returnDetail.fromOrder" />
              </span>
              
              <span class="value">
                <dsp:a page="orderDetail.jsp" title="${viewOrderDetailsTitle}">
                  <dsp:param name="orderId" value="${returnOrderId}"/>
                  <dsp:valueof value="${returnOrderId}"/>
                </dsp:a>
              </span>
             
              <%-- Display 'Replacement Order' link --%>
              <dsp:getvalueof var="replacementOrderId" param="returnRequest.replacementOrder.id"/>
              
              <c:if test="${not empty replacementOrderId}">
                <span class="label">
                  <fmt:message key="myaccount_returnDetail.replacementOrder"/>
                </span>
                
                <fmt:message var="viewOrderDetailsTitle" key="myaccount_returnDetail.replacementOrder" />
                
                <span class="value">
                  <dsp:a page="orderDetail.jsp" title="${viewOrderDetailsTitle}">
                    <dsp:param name="orderId" value="${replacementOrderId}"/>
                    <dsp:valueof value="${replacementOrderId}"/>
                  </dsp:a>
                </span>
              </c:if>
            </div>
                    
            <%-- Display all return items in the request grouped by shipping group --%>
            
            <%-- 
              Retrieve the price lists's locale used for return request. It will be the same as
              the original order pricelist's locale. Price list is not saved within order's price info
              so retrieve it from any commerce item's price info.
            
              We can't use Profile's price list here as already submitted return request can be priced 
              with price list different from current profile's price list.
            --%>
               
            <dsp:getvalueof var="commerceItems" vartype="java.lang.Object" param="returnRequest.order.commerceItems"/>
                 
            <c:if test="${not empty commerceItems}">
              <dsp:getvalueof var="priceListLocale" vartype="java.lang.String" 
                              param="returnRequest.order.commerceItems[0].priceInfo.priceList.locale"/>
            </c:if>
          
            <div id="atg_store_returnReview">
            
               <dsp:include page="/myaccount/gadgets/returnItems.jsp">
                 <dsp:param name="returnRequest" param="returnRequest" />
                 <dsp:param name="priceListLocale" value="${priceListLocale}" />
               </dsp:include>
                             
            </div>
            
            <div id="atg_store_returnReviewSummary">
            
              <%-- Display refund payment methods --%>
              <dsp:include page="/myaccount/gadgets/refundMethods.jsp">
                <dsp:param name="returnRequest" param="returnRequest"/>
                <dsp:param name="priceListLocale" value="${priceListLocale}" />
              </dsp:include>
            
              <%-- Original order summary --%>
              <dsp:include page="/myaccount/gadgets/returnOrderSummary.jsp">
                <dsp:param name="order" param="returnRequest.order"/>
                <dsp:param name="priceListLocale" value="${priceListLocale}" />
              </dsp:include>
              
              <%-- Refund summary --%>
              <dsp:include page="/myaccount/gadgets/refundSummary.jsp">
                <dsp:param name="returnRequest" param="returnRequest"/>
                <dsp:param name="hidePromotionalAmendments" value="true"/>
                <dsp:param name="priceListLocale" value="${priceListLocale}" />
              </dsp:include>
            
            </div>
          </dsp:oparam>
          <dsp:oparam name="empty">      
            <%-- No return request with given id is found. Display an error message. --%>
            <crs:messageContainer>
              <jsp:attribute name="titleText">            
                <fmt:message key="myaccount_myReturns_noSuchReturn">
                  <fmt:param><dsp:valueof param="requestId"/></fmt:param>
                </fmt:message>
              </jsp:attribute>
            </crs:messageContainer>          
          </dsp:oparam>
          <dsp:oparam name="error">      
            <%-- An error occurred during return request retrieving.--%>
            <crs:messageContainer>  
              <jsp:attribute name="titleText">          
                <dsp:valueof param="errorMsg"/>
              </jsp:attribute>
            </crs:messageContainer>
          </dsp:oparam>
          
        </dsp:droplet>
      
      </div>
      
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/returnDetail.jsp#1 $$Change: 788278 $--%>
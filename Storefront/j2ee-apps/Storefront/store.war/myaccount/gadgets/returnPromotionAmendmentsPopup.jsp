<%--
  This page renders the promotions amendments happened during order repricing as a result
  of a return. The optional returnId parameter can be passed to the page. If returnId is specified
  the promotion adjustment values are displayed for the specified return. Otherwise the current
  active return request is used.
  
  Required Parameters:
    None.
  
  Optional parameters:
    returnId
      The ID of the return request to promotion amendments for
    priceListLocale
      Specifies a locale in which to format the price (as string).
      If not specified, locale will be taken from profile price list (Profile.priceList.locale).
--%>

<dsp:page>

  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnRequestLookup"/>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  <dsp:importbean bean="/atg/commerce/promotion/PromotionLookup"/>
  
  <fmt:message var="popupTitle" key="myaccount_refundSummary_promotionAdjustments_popup.title"/>
  
  <crs:popupPageContainer divId="atg_store_promotionAmendments" pageTitle="${popupTitle}">
     
    <dsp:getvalueof var="returnId" param="returnId"/>
    
    <c:choose>
      <c:when test="${not empty returnId}">
      
        <%-- Lookup for a return request with the specified ID --%> 
        <dsp:droplet name="ReturnRequestLookup">
          <dsp:param name="returnRequestId" value="${returnId}"/>
          <dsp:oparam name="output">
          
            <dsp:getvalueof var="returnRequest" param="result"/>
           
          </dsp:oparam>
          <dsp:oparam name="empty">
           
          <%-- No return request is found, display a corresponding message  --%> 
            <fmt:message key="myaccount_returns_noReturnRequestFound">
              <fmt:param value="${returnId}"/>
            </fmt:message>
             
          </dsp:oparam>
          <dsp:oparam name="error">
             
            <%-- Some error occurred during return request item lookup, display the error message. --%>
            <dsp:valueof param="errorMsg"/>
           
          </dsp:oparam>
        </dsp:droplet>
       
      </c:when>
      <c:otherwise>
         
        <%-- No return request ID is specified for the page, use currently active return request --%>       
        <dsp:getvalueof var="returnRequest" bean="ShoppingCart.returnRequest"/>
         
      </c:otherwise>
    </c:choose>
    
    <%-- If there are non-return item pricing adjustments display the corresponding message --%>
    <c:if test="${not empty returnRequest && returnRequest.nonReturnItemSubtotalAdjustment != 0}">
      <div class="adjustmentContainer">
      <h2 class="title">
        <fmt:message key="myaccount_refundSummary_pricingAdjustments_popup.title"/>
      </h2>
      <p>
        <fmt:message key="myaccount_refundSummary_pricingAdjustments_popup.message"/>
      </p>
      </div>
    </c:if>
     
    <c:if test="${not empty returnRequest && not empty returnRequest.promotionValueAdjustments}">
      <div class="adjustmentContainer">
      <h2 class="title">
        <fmt:message key="myaccount_refundSummary_promotionAdjustments_popup.title"/>
      </h2>
      
      <p>
        <fmt:message key="myaccount_refundSummary_promotionAdjustments_popup.message"/>
      </p>      
       
     
       <dsp:getvalueof var="promotionAdjustments" value="${returnRequest.promotionValueAdjustments}"/>
       
       
       <table>
         <c:forEach var="promotionAdjustment" items="${promotionAdjustments}">
         
           <%-- Lookup promotion item by its ID --%>
           <dsp:droplet name="PromotionLookup">
             <dsp:param name="id" value="${promotionAdjustment.key}"/>
             <dsp:param name="elementName" value="promotion"/>
             <dsp:oparam name="output">
               <tr>               
                 <td><dsp:valueof param="promotion.description" valueishtml="true"/></td>
               </tr>
             </dsp:oparam>
           </dsp:droplet>
             
         </c:forEach>
        
       </table>
      </div>
     </c:if>
     
     
  </crs:popupPageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/returnPromotionAmendmentsPopup.jsp#4 $$Change: 790144 $ --%>

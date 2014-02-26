<%--
  Offer Shopper to move back to the shopping cart if there are 
  available unselected gifts. 
  
  Required Parameters:
    None
    
  Optional Parameters:
    expressCheckout
      true if Shopper choose Express Checkout option
    loginDuringCheckout
      true if Shopper must be logged in to proceed checkout    
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/commerce/promotion/GiftWithPurchaseFormHandler"/>
  
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="express" param="express"/>
  <dsp:getvalueof var="login" param="loginDuringCheckout"/>
  
  <crs:pageContainer divId="atg_store_cart" titleKey="" 
                     index="false" follow="false" bodyClass="atg_store_pageCart atg_store_giftNotSelected">
                       
    <jsp:body>
      
      <div id="atg_store_contentHeader">
        <h2 class="title">
          <fmt:message  key="common.cart.shoppingCart"/>
        </h2>
      </div>
      
      <%--
        This messageContainer tag will render a general application message box with the 
        included content.  
       --%>
      <crs:messageContainer id="atg_store_confirmResponse" titleKey="cart_giftNotSelected.title">
        <jsp:body>
          
          <fmt:message key="cart_giftNotSelected.text"/>
          
          <dsp:a page="/cart/cart.jsp" iclass="atg_store_basicButton">
            <span>
              <fmt:message key="cart_giftNotSelected.backToCartButton"/>
            </span>
          </dsp:a>
          
          <dsp:a page="${contextPath}/checkout/shipping.jsp" iclass="atg_store_basicButton">
            <span>                     
              <fmt:message key="cart_giftNotSelected.proceedToCheckoutButton"/>
            </span>
            
            <%-- Ensure user is logged in --%>
            <c:choose>
              <c:when test="${login}">
                <c:choose>
                  <c:when test="${express}">
                    <dsp:property bean="GiftWithPurchaseFormHandler.removeAllSelectableQuantitySuccessURL" 
                                  value="${contextPath}/checkout/login.jsp?express=true"/>
                  </c:when>
                  <c:otherwise>
                    <dsp:property bean="GiftWithPurchaseFormHandler.removeAllSelectableQuantitySuccessURL" 
                                  value="${contextPath}/checkout/login.jsp"/>
                  </c:otherwise>
                </c:choose>
              </c:when>
              <c:otherwise>
                <c:choose>
                  <c:when test="${express}">
                    <dsp:property bean="GiftWithPurchaseFormHandler.removeAllSelectableQuantitySuccessURL" 
                                  value="${contextPath}/checkout/confirm.jsp?expressCheckout=true"/>
                  </c:when>
                  <c:otherwise>
                    <dsp:property bean="GiftWithPurchaseFormHandler.removeAllSelectableQuantitySuccessURL" 
                                  value="${contextPath}/checkout/shipping.jsp"/>
                  </c:otherwise>
                </c:choose>
                
              </c:otherwise>
            </c:choose>                
            <dsp:property bean="GiftWithPurchaseFormHandler.removeAllSelectableQuantity" value="true"/>
          </dsp:a>
              
        </jsp:body>
      </crs:messageContainer>  
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/giftNotSelected.jsp#2 $$Change: 788278 $--%>

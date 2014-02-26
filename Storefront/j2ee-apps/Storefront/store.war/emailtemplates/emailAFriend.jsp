<%--
  Email a Friend email template. The template contains the information about a recommended 
  product along with link to the product's page.
  
  Required parameters:    
    productId
      The ID of the product that's recommended
    recipientName
      Name of the Shopper who is receiving the Email
    senderName
      Name of the Shopper who is sending the Email
      
  Optional parameters:
    message
      Optional Message to be delivered as part of the Email
    locale
      Locale that specifies in which language email should be rendered.
--%>
<dsp:page>
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  
  <%-- Email subject --%>
  <fmt:message var="emailSubject" key="emailtemplates_emailAFriend.subject">
    <fmt:param>
      <dsp:valueof bean="Site.name" />
    </fmt:param>
  </fmt:message>
    
  <crs:emailPageContainer divId="atg_store_emailTemplateIntro" 
                          messageSubjectString="${emailSubject}"
                          displayProfileLink="false">

    <jsp:body>
      
      <dsp:getvalueof var="locale" param="locale"/>

      <%-- Get URL prefix built by emailPageContainer tag --%>
      <dsp:getvalueof var="httpServer" param="httpServer"/>
  
      <%-- 
        Lookup for a product with a given ID.
     
        Input parameters:
          id
            ID of product to lookup for.
        
        Output parameters:
          element
            Product repository item with the given ID
        
        Open parameters:
          output
            Rendered if the item was found in the repository                  
      --%>
      <dsp:droplet name="ProductLookup">
        <dsp:param name="id" param="productId"/>
        <dsp:oparam name="output">

          <%-- Name a product parameter so we can keep track of things --%>
          <dsp:setvalue param="product" paramvalue="element"/>
          
          <%-- Get cross site link for product template --%>
          <dsp:include page="/global/gadgets/productLinkGenerator.jsp">
            <dsp:param name="product" param="product"/>
            <dsp:param name="siteId" bean="/atg/multisite/Site.id"/>
          </dsp:include>
      
          <%-- Build fully-qualified product URL --%>  
          <c:url var="productUrl" value="${httpServer}${productUrl}">
            <c:if test="${not empty locale}">
              <c:param name="locale">${locale}</c:param>
            </c:if>
          </c:url>     
      
          <div style="width:100%">
          
            <%--
              Greeting message with information about the sender's name, which product he is recommending 
              and sender's own message to the recipient.
            --%>
            <dsp:include page="gadgets/emailAFriendMessage.jsp">
              <dsp:param name="recipientName" param="recipientName"/>
              <dsp:param name="senderName" param="senderName"/>
              <dsp:param name="message" param="message"/>
              <dsp:param name="product" param="product"/>
            </dsp:include>

            <%-- Recommended product details --%>
            <dsp:include page="gadgets/emailAFriendProductDetails.jsp">
              <dsp:param name="productUrl" value="${productUrl}"/>
              <dsp:param name="product" param="product"/>
              <dsp:param name="httpServer" param="httpServer"/>
              <dsp:param name="imageRoot" param="imageRoot"/>
            </dsp:include>
          </div>
                
        </dsp:oparam>
    
        <dsp:oparam name="empty">
          <%-- Product is not found for a given ID, display corresponding message --%>
          <fmt:message key="common.productNotFound">
            <fmt:param>
              <dsp:valueof param="productId">
                <fmt:message key="common.productIdDefault"/>
              </dsp:valueof>
            </fmt:param>
          </fmt:message>
        </dsp:oparam>
  
      </dsp:droplet>      
    </jsp:body>

  </crs:emailPageContainer>

</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/emailAFriend.jsp#1 $$Change: 735822 $--%>

<%--  
  This page displays a confirmation message an email has been sent.
  
  Required Parameters: 
    productId
      Id of the product to be referenced in the email.
    categoryId
      Parent category id of the referenced product.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  
  <crs:popupPageContainer divId="atg_store_emailConfirmIntro" titleKey="browse_emailAFriendConfirm.title">
    <jsp:body>

      <div id="atg_store_emailConfirm">
        <%--
          The ProductLookup droplet looks for a RepositoryItem by its id from within a
          Repository. If the item is found, it will check whether the item belongs 
          to the user's catalog in his current Profile and if the item belongs to the 
          current site.
    
          Required Parameters:
            id
              The id of the item to lookup.
    
          Open Parameters:
            output
              If the item is found.
            empty  
              If the item is not found.
          
          Output Parameters:
            element
              Set to the RepositoryItem corresponding to the id supplied.
        --%>
        <dsp:droplet name="ProductLookup">
          <dsp:param name="id" param="productId"/>
          <dsp:oparam name="output">
    
            <%-- Name a product parameter so we can keep track of things --%>
            <dsp:setvalue param="product" paramvalue="element"/>
    
            <%-- Show confirmation message --%>
            <h2 class="atg_store_subHeadCustom">
              <fmt:message key="browse_emailAFriendConfirm.messageSent" />
            </h2>
    
            <dsp:getvalueof var="recipientName" vartype="java.lang.String" param="recipientName"/>
            <dsp:getvalueof var="recipientEmail" vartype="java.lang.String" param="recipientEmail"/>
    
            <p>
              <%--
                Email sent confirmation message. Display recipient name and email escaping
                XML specific characters to prevent using them for XSS attacks.
               --%>
              <fmt:message key="browse_emailAFriendConfirm.emailDelivered">
                <fmt:param value="${fn:escapeXml(recipientName)}"/>
                <fmt:param value="${fn:escapeXml(recipientEmail)}"/>
                <fmt:param>
                  <dsp:include page="/browse/gadgets/productName.jsp">
                    <dsp:param name="showAsLink" value="false"/>
                  </dsp:include>
                </fmt:param>
              </fmt:message>
            </p>
    
            <fmt:message var="closeButtonText" key="common.closeWindowText"/>
         
            <dsp:getvalueof var="pageurl" vartype="java.lang.String" param="product.template.url"/>
            <c:choose>
              <c:when test="${not empty pageurl}">
                <c:url var="pageurl" value="${pageurl}">
                  <c:param name="productId"><dsp:valueof param="productId"/></c:param>
                  <c:param name="categoryId"><dsp:valueof param="categoryId"/></c:param>
                </c:url>
                <%-- Product Template is set --%>
                <div class="atg_store_formActions"  id="atg_store_popupCloseButton">
                  <dsp:a href="${pageurl}" iclass="atg_store_basicButton" onclick="window.close();">
                    <span>${closeButtonText}</span>
                  </dsp:a>
                </div>
              </c:when>
              <c:otherwise>
                <div class="atg_store_formActions"  id="atg_store_popupCloseButton">
                  <%-- Product Template not set --%>
                  <dsp:a page="/index.jsp" iclass="atg_store_basicButton" onclick="window.close();">
                    ${closeButtonText}
                  </dsp:a>
                </div>
              </c:otherwise>
            </c:choose>
          </dsp:oparam>
          <dsp:oparam name="empty">
            <fmt:message key="common.productNotFound">
              <fmt:param>
                <dsp:valueof param="productId">
                  <fmt:message key="common.productIdDefault"/>
                </dsp:valueof>
              </fmt:param>
            </fmt:message>
          </dsp:oparam>
        </dsp:droplet>
      </div>
    </jsp:body>
  </crs:popupPageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/emailAFriendConfirm.jsp#1 $$Change: 735822 $--%>

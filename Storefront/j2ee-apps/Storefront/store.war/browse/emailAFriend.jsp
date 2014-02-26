<%--  
  This page presents the email a friend form including a particular category and product:
  
  Required Properties: 
    productId
      Id of the product to be referenced in the email.
    categoryId
      Parent category id of the referenced product.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/store/email/EmailAFriendFormHandler"/>
  
  <crs:popupPageContainer divId="atg_store_emailAFriendIntro" titleKey="browse_emailAFriend.title">
  
    <jsp:attribute name="formErrorsRenderer">
      <%-- Display error messages if any above the accessibility navigation --%>  
      <dsp:include page="/global/gadgets/displayErrorMessage.jsp">
        <dsp:param name="formHandler" bean="EmailAFriendFormHandler"/>
        <dsp:param name="submitFieldKey" value="common.button.sendText"/>
      </dsp:include>
    </jsp:attribute>
    
    <jsp:body>
      <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
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
          <dsp:getvalueof var="product" param="element" vartype="java.lang.Object" scope="request"/>
          
          <%-- Include emailAFriendContainer.jsp to hold the relevant information about the product --%>
          <dsp:include page="/browse/emailAFriendContainer.jsp">
            <dsp:param name="product" value="${product}"/>
            <dsp:param name="categoryId" param="categoryId"/>          
            <dsp:param name="templateUrl" value="/emailtemplates/emailAFriend.jsp"/>
           </dsp:include>
           
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
    </jsp:body>
  </crs:popupPageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/emailAFriend.jsp#1 $$Change: 735822 $--%>

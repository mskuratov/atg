<%-- 
  This page displays user's saved payment information (credit cards and online credits)
  
  Required parameters:
    None
    
  Optional parameters:
    None  
--%>
<dsp:page>

  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst"/>
  <dsp:importbean bean="/atg/commerce/claimable/AvailableStoreCredits"/>
  
  <crs:pageContainer divId="atg_store_paymentInfoIntro" 
                     index="false" follow="false"
                     bodyClass="atg_store_myAccountPage atg_store_leftCol">    
    <jsp:body>
    
      <%-- Page title --%>
      <div id="atg_store_contentHeader">
        <h2 class="title"><fmt:message key="myaccount_paymentInfo.title"/></h2>
      </div>
      
      <%-- Left-hand menu --%>
      <dsp:include page="gadgets/myAccountMenu.jsp">
        <dsp:param name="selpage" value="PAYMENT INFO" />
      </dsp:include>
  
      <%--
        Displays list of available credit cards and online store credits
       --%>
      <div class="atg_store_main atg_store_myAccount">
      
        <%-- Display errors if any --%>
        <div id="atg_store_formValidationError">        
          <dsp:include page="/global/gadgets/errorMessage.jsp">
            <dsp:param name="formHandler" bean="ProfileFormHandler"/>
            <dsp:param name="divid" value="errorMessage"/>
          </dsp:include>
        </div>
      
        <%-- Get URLs to redirect to --%>
        <dsp:getvalueof id="requestURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
        <dsp:getvalueof id="cancelURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
        
        <%-- Get Profile's default credit card --%>
        <dsp:getvalueof var="defaultCreditCard" bean="Profile.defaultCreditCard"/>
      
        <%-- 
          Iterate through all this user's credit cards, sorting the array so that the
          default credit card will be the first.
          
          Input parameters:
            defaultId
              repository Id of item that will be the first in the array,
              in this case it is an Id of Profile's default credit card
            map
              Map of repository items that will be converted into array,
              in this case Profile's credit cards
            sortByKeys
              returning array will be sorted by keys (card nicknames)
              
          Output parameters:
            sortedArray
              array of sorted cards with default card in the first place 
        --%>
        <dsp:droplet name="MapToArrayDefaultFirst">
          <dsp:param name="defaultId" value="${defaultCreditCard.repositoryId}"/>
          <dsp:param name="sortByKeys" value="true"/>
          <dsp:param name="map" bean="Profile.creditCards"/>
          
          <dsp:oparam name="empty">
             <%--
              If no saved credit cards, offer to create a new one
             --%>
            <crs:messageContainer titleKey="myaccount_storedCreditCards.noStoredCreditCards">
              <jsp:body>
                
                <%-- Display available store credits --%>
                <%--
                  Get overall amount of available store credits
                  
                  Input parameters:
                    profile
                      user Profile
                  Output parameters:
                    overallAvailableAmount
                      sum of available store credit amounts
                 --%>
                <dsp:droplet name="AvailableStoreCredits">
                  <dsp:param name="profile" bean="Profile"/>
                  
                  <dsp:oparam name="output">
                    <div id="atg_store_onlineCredits">
                      <fmt:message key="myaccount_onlineCredits.savedOnlineCredits"/>
          
                      <%-- Display total online credits value --%>
                      <span class="atg_store_onlineCreditTotal">
                        <dsp:include page="/global/gadgets/formattedPrice.jsp">
                           <dsp:param name="price" param="overallAvailableAmount"/>
                         </dsp:include>
                      </span>
                    </div>
                  </dsp:oparam>
                </dsp:droplet> 
                
                <div class="atg_store_formActions">
                  <dsp:a page="newCreditCard.jsp" iclass="atg_store_basicButton">
                    <span><fmt:message key="myaccount_paymentInfoCardAddEdit.addNewCreditCard"/></span>
                  </dsp:a>
                </div>
              </jsp:body>   
            </crs:messageContainer>
          </dsp:oparam>
          
          <dsp:oparam name="output">
            <div id="atg_store_storedCreditCards">
              <dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/>
              
              <%-- 
                Iterate over the array of credit cards and display card information
                and edit/remove links 
               --%>
              <c:forEach var="creditCard" items="${sortedArray}" varStatus="status">
                <dsp:setvalue param="creditCard" value="${creditCard}"/>
                
                <%-- 
                  If default credit card is defined, it will be always the first
                --%>
                <c:choose>
                  <c:when test="${status.count == 1 and not empty defaultCreditCard}">
                    <div class="atg_store_paymentInfoGroup atg_store_addressGroupDefault">
                      <dl>
                        <%-- 
                          Display address nickname and 'Default Shipping' link 
                          if it is the default value
                          --%>
                        <dt class="atg_store_defaultCreditCard">
                          <dsp:valueof param="creditCard.key"/>
                            <dsp:a page="/myaccount/profileDefaults.jsp" title="${defaultAddressTitle}">
                              <span><fmt:message key="common.default"/></span>
                            </dsp:a>
                        </dt>    
                  </c:when>
                  <c:otherwise>
                    <div class="atg_store_paymentInfoGroup">
                      <dl>
                        <%-- Display static link --%>
                        <dt><dsp:valueof param="creditCard.key"/></dt>
                  </c:otherwise>
                </c:choose>
                
                <%-- Display credit card details --%>
                <dsp:include page="/global/util/displayCreditCard.jsp">
                  <dsp:param name="creditCard" param="creditCard.value"/>
                  <dsp:param name="displayCardHolder" value="true"/>
                </dsp:include>
                
                </dl>
                  
                    
                <%-- Display Edit/Remove links --%>
                <ul class="atg_store_storedCreditCardsActions">
                  
                  <%-- 'Edit' link --%>
                  <fmt:message var="editCardTitle" key="common.button.editCardTitle"/>
                  <li class="<crs:listClass count="1" size="2" selected="false"/>">
                    <dsp:a page="accountCardEdit.jsp" title="${editCardTitle}">
                      <dsp:param name="successURL" bean="/OriginatingRequest.requestURI"/>
                      <dsp:param name="cancelURL" value="${cancelURL}?preFillValues=false"/>
                      <dsp:param name="nickName" value="${creditCard.key}"/>
                      <span><fmt:message key="common.button.editText"/></span>
                    </dsp:a>
                  </li>

                  <%-- 'Remove' link --%>
                  <fmt:message var="removeCardTitle" key="myaccount_storedCreditCards.removeCardTitle"/>
                  <li class="<crs:listClass count="2" size="2" selected="false"/>">
                    <dsp:a bean="ProfileFormHandler.removeCard" href="${requestURL}"
                           paramvalue="creditCard.key" title="${removeCardTitle}">
                     <span><fmt:message key="common.button.removeText"/></span>
                    </dsp:a>
                  </li>
                </ul>
                </div>
              </c:forEach>
            </div>
            
            <%-- Online credits available for the user --%>
            <%--
              Get overall amount of available store credits
              
              Input parameters:
                profile
                  user Profile
              Output parameters:
                overallAvailableAmount
                  sum of available store credit amounts
             --%>
            <dsp:droplet name="AvailableStoreCredits">
              <dsp:param name="profile" bean="Profile"/>
              
              <dsp:oparam name="output">
                <div id="atg_store_onlineCredits">
                  <fmt:message key="myaccount_onlineCredits.savedOnlineCredits"/>
      
                  <%-- Display total online credits value --%>
                  <span class="atg_store_onlineCreditTotal">
                    <dsp:include page="/global/gadgets/formattedPrice.jsp">
                       <dsp:param name="price" param="overallAvailableAmount"/>
                     </dsp:include>
                  </span>
                </div>
              </dsp:oparam>
            </dsp:droplet> 

            <%-- Link to 'Add a New Card' page --%>
            <div class="atg_store_formActions">
              <dsp:a page="newCreditCard.jsp" iclass="atg_store_basicButton">
                <span><fmt:message key="myaccount_paymentInfoCardAddEdit.addNewCreditCard"/></span>
              </dsp:a>
            </div>
          </dsp:oparam>
        </dsp:droplet> 
      </div>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/paymentInfo.jsp#2 $$Change: 788278 $--%>

<%--  
  This page displays search form for gift lists that allows to search by owner's
  first or\and last name.
  
  This layout page includes the following gadgets:
    displayErrorMessage gadget - displays error messages occurred during search;
    giftListSearch gadget - displays the search form itself.
    
  Required parameters:
    None.
        
  Optional parameters:
    isNewSearch 
      If 'true', all form errors will be removed, and form inputs will be cleared. 
 --%>
<dsp:page>
  
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistSearch"/>

  <crs:pageContainer divId="atg_store_giftListIntro" titleKey="" 
                     bodyClass="atg_store_pageGiftList" selpage="GIFT LISTS">
    <jsp:body>
      <div id="atg_store_giftListSearchContainer">
        <div id="atg_store_contentHeader">
          <h2 class="title">
            <fmt:message key="navigation_personalNavigation.giftList.findGiftlist"/>
          </h2>
        </div>
        
        <%-- Clear form handler's inputs and errors if isNewSearch parameter is 'true' --%>    
        <dsp:getvalueof var="isNewSearch" param="isNewSearch"/>
        <c:if test="${isNewSearch}">
          <dsp:setvalue bean="/atg/commerce/gifts/GiftlistSearch.clearForm" value="true"/>
        </c:if>
      
        <%-- Display GiftlistSearch form handler's error messages. --%>
        <dsp:include page="/global/gadgets/displayErrorMessage.jsp">
          <dsp:param name="formHandler" bean="GiftlistSearch"/>
          <dsp:param name="submitFieldKey" value="common.button.searchText"/>
        </dsp:include>
        <div class="atg_store_giftListSearch">
          <h3>
            <fmt:message key="giftlist_giftListSearch.wantToGiveGift"/>
          </h3>
          
          <%-- Include gift list search form --%>
          <dsp:include page="gadgets/giftListSearch.jsp" />
            
        </div>
      </div>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/giftlists/giftListSearch.jsp#1 $$Change: 735822 $ --%>
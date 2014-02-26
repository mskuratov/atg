<%--
  This page displays search form for gift lists along with search results.
  
  This layout page includes the following gadgets:
    giftListSearch gadget - displays gift list search form;
    displayErrorMessage gadget - displays error messages occurred during search;
    giftListSearchResults gadget - displays gift list search results.
    
  Required parameters:
    None.
        
  Optional parameters:
    None. 
 --%>
<dsp:page>
  
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistSearch"/>

  <crs:pageContainer divId="atg_store_giftListIntro" titleKey="" 
                     bodyClass="atg_store_giftListSearchResults atg_store_pageGiftList"
                     selpage="GIFT LISTS">
    <jsp:body>
      <div id="atg_store_giftListSearch">
        <div id="atg_store_contentHeader">
          <h2 class="title">
            <fmt:message key="navigation_personalNavigation.giftList.findGiftlist"/>
          </h2>
          
          <%-- Gift List Search form --%>
          <div class="atg_store_giftListSearch">
            <dsp:include page="gadgets/giftListSearch.jsp" />              
          </div>
        </div>

        <%-- Display GiftlistSearch form handler's error messages. --%>
        <span>
          <dsp:include page="/global/gadgets/displayErrorMessage.jsp">
            <dsp:param name="formHandler" bean="GiftlistSearch"/>
            <dsp:param name="submitFieldKey" value="common.button.searchText"/>
          </dsp:include>
        </span>

        <%-- Search Results --%>
        <dsp:include page="gadgets/giftListSearchResults.jsp" />
      </div>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/giftlists/giftListSearchResults.jsp#1 $$Change: 735822 $ --%>
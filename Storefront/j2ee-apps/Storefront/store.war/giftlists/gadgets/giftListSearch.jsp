<%--
  This gadget displays gift list search form.
  
  Required parameters:
    None.
      
  Optional parameters:
    None.
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/commerce/gifts/GiftlistSearch"/>
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
    
  <c:url value="giftListSearchResults.jsp" var="giftSearchResultsUrl" scope="page"/>     
   
  <%-- Gift List Search form --%>
  <dsp:form action="${originatingRequest.requestURI}" method="post"
            formid="searchGiftlist" name="searchGiftlist">
    <ul class="atg_store_basicForm">
      <dsp:input bean="GiftlistSearch.searchSuccessURL" value="${giftSearchResultsUrl}" type="hidden"/>
      <dsp:input bean="GiftlistSearch.searchErrorURL" value="${originatingRequest.requestURI}" type="hidden"/>
                 
      <%-- Gift list owner's first name input--%>           
      <li>
        <label for="atg_store_firstNameInput">
          <fmt:message key="giftlist_giftListSearch.firstName"/>
        </label>
        <dsp:getvalueof var="firstName" vartype="java.lang.String" bean="GiftlistSearch.propertyValues.firstName"/>
        <dsp:input bean="GiftlistSearch.propertyValues.firstName" type="text" id="atg_store_firstNameInput"/>
      </li>
      
      <%-- Gift list owner's last name input--%>
      <li>
        <label for="atg_store_lastNameInput">
          <fmt:message key="common.lastName"/>
        </label>
        <dsp:getvalueof var="lastName" vartype="java.lang.String" bean="GiftlistSearch.propertyValues.lastName"/>
        <dsp:input bean="GiftlistSearch.propertyValues.lastName" type="text" id="atg_store_lastNameInput"/>
      </li>
        
      <%-- Search form's submit button  --%>
      <li>
        <div class="atg_store_formActions">
          <fmt:message var="searchText" key="common.button.searchText"/>
          <fmt:message var="searchTitle" key="common.button.searchTitle"/>
          <span class="atg_store_basicButton">
            <dsp:input type="submit"  bean="GiftlistSearch.search" iclass="atg_store_actionSubmit"
                       title="${searchTitle}" value="${searchText}" 
                       id="atg_store_giftListSearchSubmit"/>
          </span>
        </div>
      </li>
    </ul>
  </dsp:form>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/giftlists/gadgets/giftListSearch.jsp#1 $$Change: 735822 $ --%>

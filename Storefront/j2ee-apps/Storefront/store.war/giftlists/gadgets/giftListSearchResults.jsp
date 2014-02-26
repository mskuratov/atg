<%--
  This gadget displays the results from gift list search.
  
  Required parameters:
    None.
        
  Optional parameters:
    None.
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/commerce/gifts/GiftlistSearch"/>
  <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>
  
  <div id="atg_store_giftListSearchResults">

    <%--
      Filters out deleted gift lists. For the case when some gift list was deleted by owner after
      it was added to search results. Filtering will work in case when user refreshes search 
      results page without researching.
      
      Input parameters:
        collection
          A collection of items from which deleted repository items should be filtered out.
          
      Output parameters:
        filteredCollection
          Filtered collection of items.
          
      Open parameters:
        output
         Rendered if filtered collection is not empty.
     --%>
    <dsp:droplet name="/atg/store/droplet/GiftListFilterDroplet">
      <dsp:param name="collection"  bean="GiftlistSearch.searchResults"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="filteredGiftlistSearchResults" param="filteredCollection"/>
        <dsp:getvalueof var="size" vartype="java.lang.Integer" value="${fn:length(filteredGiftlistSearchResults)}"/>
        <fmt:message var="giftListSummary" key="common.giftListSummary"/>
        
        <table class="atg_store_dataTable" summary="${giftListSummary}" cellspacing="0" cellpadding="0">
        
          <thead>
            <tr>
              <th scope="col" class="atg_store_event">
                <fmt:message key="giftlists_giftListSearchResults.recipient"/>
              </th>
              <th scope="col" class="atg_store_eventDescription">
                <fmt:message key="common.giftListTitle"/>
              </th>
              <th scope="col" class="atg_store_eventType">
                <fmt:message key="common.type"/>
              </th>
              <th scope="col" class="atg_store_date">
                <fmt:message key="common.date"/>
              </th>
            </tr>
          </thead>
          <tbody>
          
            <%-- Loop through all search results --%>
            <c:forEach var="searchResult" items="${filteredGiftlistSearchResults}" 
                       varStatus="searchResultStatus">
              <dsp:param name="giftlist" value="${searchResult}"/>
              <dsp:getvalueof var="count" idtype="int" value="${searchResultStatus.count}"/>
              <dsp:getvalueof var="index" idtype="int" value="${searchResultStatus.index}"/>
              <tr class="<crs:listClass count='${count}' size='${size}' selected='false'/>">
                
                <%-- Display Recipient's name as link to gift list --%>
                <td class="atg_store_recipient">
                  <dsp:getvalueof id="occassion" param="giftlist.eventName"/>
                  <dsp:getvalueof var="middleName" param="giftlist.owner.middleName"/>

                  <dsp:a href="../giftListShop.jsp" title="${fn:escapeXml(occassion)}">
                    <dsp:param name="giftlistId" param="giftlist.repositoryId"/>
                    <dsp:valueof param="giftlist.owner.firstName"/>
                    <c:choose>
                      <c:when test="${not empty middleName}">
                        <c:out value=" ${middleName} "/>
                      </c:when>
                      <c:otherwise>
                        <c:out value=" "/>
                      </c:otherwise>
                    </c:choose>
                    <dsp:valueof param="giftlist.owner.lastName"/>
                  </dsp:a>
                </td>
                
                <%-- Event's name and description --%>
                <dsp:getvalueof var="giftListEventName" param="giftlist.eventName"/>
                <dsp:getvalueof var="giftListEventDescription" param="giftlist.description"/>
                <td class="atg_store_eventDescription" scope="row" abbr="${fn:escapeXml(giftListEventName)} : ${fn:escapeXml(giftListEventDescription)}">
                  <dl>
                    <dt>
                      <dsp:valueof param="giftlist.eventName"/>
                    </dt>
                    <dd>
                      <dsp:valueof param="giftlist.description"/>
                    </dd>
                  </dl>
                </td>
                
                <%-- Event's type --%>
                <td class="atg_store_eventType">
                  <%-- 
                    To get localized name for event type construct resource key by removing
                    spaces from original event type name and adding a 'giftlist.eventType' prefix.
                   --%>
                  <dsp:getvalueof var="eventType" param="giftlist.eventType"/>
                  <c:set var="eventTypeResourceKey" value="${fn:replace(eventType, ' ', '')}"/>
                  <c:set var="eventTypeResourceKey" value="giftlist.eventType${eventTypeResourceKey}"/>
                  <fmt:message key="${eventTypeResourceKey}"/>
                </td>
                
                <%-- Event's date --%>
                <td class="atg_store_date">
                  <dsp:getvalueof var="eventDate" vartype="java.util.Date" param="giftlist.eventDate"/>
                  
                  <dsp:getvalueof var="dateFormat" 
                                  bean="LocaleTools.userFormattingLocaleHelper.datePatterns.shortWith4DigitYear" />
                                  
                  <fmt:formatDate value="${eventDate}" pattern="${dateFormat}" />
                </td>
              </tr>
            </c:forEach>

          </tbody>
        </table>
          
      </dsp:oparam>
      <dsp:oparam name="empty">
        <%-- If no valid search results display 'no results' message. --%>
        <crs:messageContainer titleKey="giftlists_giftListSearchResults.noMatchOfGift" />
      </dsp:oparam>
    </dsp:droplet>
    
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/giftlists/gadgets/giftListSearchResults.jsp#1 $$Change: 735822 $ --%>

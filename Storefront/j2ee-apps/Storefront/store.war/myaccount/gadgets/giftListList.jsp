<%-- 
  This page displays the all gift lists for the current profile and site group that share
  shopping cart with the current site.
  
  Required parameters:
    None
    
  Optional parameters:
    None    
--%>
<dsp:page>

  <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
  <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/commerce/collections/filter/droplet/GiftlistSiteFilterDroplet"/>

  <div id="atg_store_giftListList">
    
    <fmt:message var="giftListSummary" key="common.giftListSummary"/>
    <table summary="${giftListSummary}" cellspacing="0" cellpadding="0" class="atg_store_dataTable">
    
      <%--
        The GiftlistSiteFilterDroplet filters out gift list that doesn't belong to sites
        that are in one site group with the current one.
        
        Input parameters:
          collection
            Collection of gift list to filter.
            
        Output parameters:
          filteredCollection
            Filtered collection of gift lists.
            
        Open parameters:
          output
            The parameter is rendered if filtered collection is not empty.
          empty
            The parameter is rendered if filtered collection is empty.
      --%>
      <dsp:droplet name="GiftlistSiteFilterDroplet">
        <dsp:param name="collection"  bean="/atg/userprofiling/Profile.giftlists"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="giftlists" param="filteredCollection" />

          <thead>
            <tr>
              <th class="atg_store_giftlistEventName" scope="col">
                <fmt:message key="common.giftListTitle"/>
              </th>
              <th class="atg_store_giftlistEventType" scope="col">
                <fmt:message key="common.type"/>
              </th>
              <th class="atg_store_giftlistEventDate" scope="col">
                <fmt:message key="common.date"/>
              </th>
              <th class="atg_store_giftlistEventItems" colspan="2" scope="col">
                <fmt:message key="myaccount_giftListAdd.privacySetting"/>
              </th>
            </tr>
          </thead>
          <tbody>
            <%-- Get the number of items in filtered collection. --%>
            <dsp:getvalueof var="size" idtype="int" value="${fn:length(giftlists)}"/>
            
            <%-- 
              Loop through  the list of filtered gift lists and display them along 
              with Edit/Remove action links. 
             --%>
            <c:forEach var="giftlist" items="${giftlists}" varStatus="giftlistStatus">
              <dsp:getvalueof var="count" idtype="int" value="${giftlistStatus.count}"/>
              <dsp:getvalueof var="index" idtype="int" value="${giftlistStatus.index}"/>
              <dsp:param name="giftlist" value="${giftlist}"/>
                          
              <tr class="<crs:listClass count='${count}' size='${size}' selected='false'/>">
              
                <%-- Display gift list's name as a link to 'Edit Gift List' page. --%>
                <dsp:getvalueof var="eventName" vartype="java.lang.String" param="giftlist.eventName"/>
                
                <td class="atg_store_giftListName" scope="row" abbr="${fn:escapeXml(eventName)}">
                  <fmt:message var="viewListTitle" key="myaccount_giftListList.viewListTitle"/>
                  <dsp:a href="../giftListEdit.jsp" title="${viewListTitle}">
                    <dsp:param name="giftlistId" param="giftlist.repositoryId"/>
                    <c:out value="${eventName}"/>                    
                  </dsp:a>
                </td>

                <%-- Event's type of gift list --%>
                <td valign="middle" class="atg_store_giftListType">
                  <%-- 
                    To get localized version for name of event type the resource key is constructed
                    by removing spaces from original name of event type and by adding 'giftlist.eventType'
                    prefix. 
                   --%>
                  <dsp:getvalueof var="eventType" param="giftlist.eventType"/>
                  <c:set var="eventTypeResourceKey" value="${fn:replace(eventType, ' ', '')}"/>
                  <c:set var="eventTypeResourceKey" value="giftlist.eventType${eventTypeResourceKey}"/>
                  <fmt:message key="${eventTypeResourceKey}"/>
                </td>
                
                <%-- Event's date. --%>
                <td class="date numerical" valign="middle" class="atg_store_giftListDate">
                  <dsp:getvalueof var="eventDate" vartype="java.util.Date" param="giftlist.eventDate"/>
                  
                  <dsp:getvalueof var="dateFormat" 
                                  bean="LocaleTools.userFormattingLocaleHelper.datePatterns.shortWith4DigitYear" />
                                  
                  <fmt:formatDate value="${eventDate}" pattern="${dateFormat}"/>
                </td> 
                
                <%-- Privacy setting of gift list, can be public or private. --%>           
                <td class="atg_store_giftListPrivacy">
                  <dsp:getvalueof var="isGiftListPublished" param="giftlist.published"/>
                  <c:choose>
                    <c:when test="${isGiftListPublished}">
                      <fmt:message key="myaccount_giftListAdd.public"/>
                    </c:when>
                    <c:otherwise>
                      <fmt:message key="myaccount_giftListAdd.private"/>
                    </c:otherwise>
                  </c:choose>
                </td>
                
                <%-- Display edit and remove action links for gift list. --%>
                <td valign="middle" align="right" class="atg_store_giftListEditRemove">

                  <%-- Edit link, takes user to the 'Edit Gift List' page. --%>
                  <fmt:message var="editTitle" key="myaccount_giftListList.button.editTitle"/>                  
                  <dsp:a href="../giftListEdit.jsp" title="${editTitle}" iclass="atg_store_giftListEdit">
                    <dsp:param name="giftlistId" param="giftlist.repositoryId"/>                    
                    <fmt:message key="common.button.editText"/>
                  </dsp:a>                    
                  
                  <%-- Remove link, upon click invokes 'deleteGiftlist' handler of GiftlistFormHandler. --%>
                  <fmt:message var="deleteTitle" key="myaccount_giftListList.button.deleteTitle"/>
                  <dsp:a href="../giftListHome.jsp" value="" 
                         title="${deleteTitle}" iclass="atg_store_giftListRemove">                    
                    <%-- Load the values into the handler bean --%>
                    <dsp:property bean="GiftlistFormHandler.giftlistId" 
                                  paramvalue="giftlist.repositoryId" name="gl_rId" />
                    <%-- Now call the method on the form handler bean --%>
                    <dsp:property bean="GiftlistFormHandler.deleteGiftlist" value="" />
                    <fmt:message key="common.button.removeText"/>
                  </dsp:a>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </dsp:oparam>
        <dsp:oparam name="empty">
        
          <%-- 
            There are no gift lists to display. Show 'no results' message specially formatted by
            messageContainer tag. 
           --%>
          <crs:messageContainer titleKey="myaccount_giftListList.availableGiftLists"
                                messageKey="myaccount_giftListList.accountHaveNoGiftLists"/>
        </dsp:oparam>
      </dsp:droplet>
    </table>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/giftListList.jsp#1 $$Change: 735822 $ --%>


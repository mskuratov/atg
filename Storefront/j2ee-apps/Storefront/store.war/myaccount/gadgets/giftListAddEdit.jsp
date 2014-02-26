<%-- 
  This page display a form for adding new gift list. This gadget is used either for adding a new 
  gift list or editing existing gift list. If gift list ID is passed to the page the corresponding
  gift list properties values are loaded into the form, otherwise the form is considered for
  adding new gift list.
  
  Required parameters:
    None
  
  Optional parameters:
    gadgetTitle
      Title to display above 'add \ edit gift list' form. This  parameter is passed from either
      giftListHome.jsp or giftListEdit.jsp.
    giftlistId
      Gift List ID in the case of 'edit gift list' action. It is passed from giftListEdit.jsp
    initForm
      If 'true', will initialize form with previously entered data stored into the session. This
      is used when returning back to 'Add Gift List' form after adding new gift list address.
--%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistLookupDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>  

  <dsp:getvalueof var="giftlistId" param="giftlistId"/>
  <dsp:getvalueof var="gadgetTitle" param="gadgetTitle"/>
  <dsp:getvalueof var="initForm" param="initForm"/>
  <dsp:getvalueof  var="dateTokens" bean="LocaleTools.userFormattingLocaleHelper.dateTokens"/>
  
  <%-- 
    Determine whether the gadget should be used in Add mode or in
    Edit mode. If gift list ID is passed to this gadget the Edit mode will be used
    otherwise - the Add mode.
   --%>
  <c:set var="editMode" value="${not empty giftlistId}"/>
 
  <c:url value="giftListHome.jsp" var="giftListHomeUrl" scope="page"/>    
  
  <div id="atg_store_addGiftList">
    
    <%-- 
      If gift list ID is passed to the page then edit gift list form should be displayed.
      In this case we need to populate GiftListFormHandler with gift list's properties values.
    --%>
    <dsp:getvalueof var="giftlistId" param="giftlistId"/>
    <c:choose>
      <c:when test="${editMode}">
        <%-- 
          The GiftlistLookupDroplet retrieves the gift list with a given ID. 
          
          Input parameters:
            id
              ID of gift list to lookup for.
               
          Output parameters:
            element
              gift list repository item
               
          Open parameters:
            output
              Rendered if the item was found in the repository                  
        --%>
        <dsp:droplet name="GiftlistLookupDroplet">
          <dsp:param name="id" param="giftlistId"/>
          <dsp:oparam name="output">

            <%--
              We must first make sure that the found gift list is one that belongs to the user 
              to prevent users from passing gift list ID that isn't theirs
            --%>
            <dsp:droplet name="Compare">
              <dsp:param name="obj1" bean="Profile.id"/>
              <dsp:param name="obj2" param="element.owner.id" />
              <dsp:oparam name="equal">
                <%-- 
                  Owner of the found gift list is the same as current profile, so we can 
                  proceed with loading gift list into the GiftlistFormHandler form handler.
                --%>
                <dsp:setvalue bean="GiftlistFormHandler.giftlist" paramvalue="element"/>
              </dsp:oparam>
            </dsp:droplet><%-- End of Compare droplet --%>

          </dsp:oparam>
        </dsp:droplet><%-- End of GiftlistLookupDroplet --%>
      </c:when>
      <c:otherwise>
        <%-- It's "Add new gift list" mode. --%>
        
        <%-- 
          If 'initForm' parameter is 'true' initialize form with values previously stored into
          the session. This is used when returning back to 'Add Gift List' form after adding
          new gift list address.
         --%>
        <c:if test="${initForm}">
          <dsp:setvalue bean="GiftlistFormHandler.initializeGiftListForm" value=""/>
        </c:if>
        
      </c:otherwise>
    </c:choose>    

    <%-- Display title above the form if it is provided. --%>
    <c:if test="${!empty gadgetTitle}">
      <h3><fmt:message key="${gadgetTitle}"/></h3>
    </c:if>

    <fieldset>
      <%-- Display the set of required form parameters. --%>
      <ul class="atg_store_basicForm">
        <c:choose>
          <c:when test="${editMode}">
            <%-- 
              It's the 'edit gift list' case. Specify Success URL for 'addAddress' action and 
              set giftlistId and unmodified event name in form handler.
             --%>
            <dsp:input bean="GiftlistFormHandler.giftlistId" type="hidden" 
                       beanvalue="GiftlistFormHandler.giftlistId"/>
                       
            <dsp:input bean="GiftlistFormHandler.unmodifiedEventName" type="hidden" 
                       beanvalue="GiftlistFormHandler.unmodifiedEventName"/>           
                       
            <c:url value="giftListEdit.jsp" var="addAddressSuccessURL" scope="page">
              <c:param name="giftlistId">${giftlistId}</c:param>
            </c:url>
          </c:when>
          <c:otherwise>
            <%-- 
              It's the 'add gift list' case. Specify Error and Success URLs for 'saveGiftlist' 
              action and Success URL for 'addAddress' action.
             --%>
            <dsp:input bean="GiftlistFormHandler.saveGiftlistSuccessURL" type="hidden" 
                       value="${giftListHomeUrl}"/>
            <dsp:input bean="GiftlistFormHandler.saveGiftlistErrorURL" type="hidden" 
                       value="${giftListHomeUrl}"/>
            <c:url value="giftListHome.jsp" var="addAddressSuccessURL" scope="page">
              <c:param name="initForm">true</c:param>              
            </c:url>
          </c:otherwise>
        </c:choose>
        
        <%-- Gift list's name --%>
        <li>
          <label for="atg_store_giftListAddEventName" class="required">
            <fmt:message key="myaccount_giftListAdd.giftListName"/>
            <span class="required">*</span>
          </label>
          <dsp:input bean="GiftlistFormHandler.eventName" size="27" maxlength="64"
                     name="atg_store_giftListAddEventName" type="text"
                     id="atg_store_giftListAddEventName" required="true" iclass="required"/>
        </li>
        
        <%-- Select inputs for gift list's date --%>
        <li class="atg_store_giftListSelectDate">
          <label class="required">
            <fmt:message key="common.date"/>
            <span class="required">*</span>
          </label>

          <dsp:getvalueof var="formHandlerName" bean="${GiftlistFormHandler}.name"/>
          <c:set var="formHandlerPath" value="/atg/commerce/gifts/GiftlistFormHandler"/>

          <!-- Includes a Date field having a Locale specific day,month,year pattern -->
          <dsp:include page="/myaccount/gadgets/datePicker.jsp">
            <dsp:param name="formHandlerComponentMonth" value="${formHandlerPath}.month"/>
            <dsp:param name="formHandlerComponentDay" value="${formHandlerPath}.date"/>
            <dsp:param name="formHandlerComponentYear" value="${formHandlerPath}.year"/>
            <dsp:param name="dayTitleKey" value="myaccount_giftListAdd.giftLisEventDayTitle"/>
            <dsp:param name="monthTitleKey" value="myaccount_giftListAdd.giftLisEventMonthTitle"/>
            <dsp:param name="yearTitleKey" value="myaccount_giftListAdd.giftLisEventYearTitle"/>
            <dsp:param name="displayDateLables" value="true"/>
            <dsp:param name="numberOfYears" value="5"/>
          </dsp:include> 
        
        </li>
        
        <%-- Shipping address where gift list items should be shipped. --%>
        <li>
          <label for="atg_store_giftListAddShippingAddress" class="required">
            <fmt:message key="myaccount_giftListAdd.shipTo"/>
            <span class="required">*</span>
          </label>
          
          <%-- 
            Determine which shipping address should be selected by default for
            this gift list. Addresses are chosen in the following order:
            1) An address that has just been created on the GL page
            2) An address previously associated with this giftlist
            3) Profiles default shipping address
          --%>
          <dsp:getvalueof var="selectedGiftlistShippingAddress" bean="ProfileFormHandler.newAddressId"/>
          <c:if test="${empty selectedGiftlistShippingAddress}">
            <dsp:getvalueof var="selectedGiftlistShippingAddress" bean="GiftlistFormHandler.shippingAddressId"/>
          </c:if>
          <c:if test="${empty selectedGiftlistShippingAddress}">
            <dsp:getvalueof var="selectedGiftlistShippingAddress" bean="Profile.shippingAddress.repositoryId"/>
          </c:if>

          <dsp:select bean="GiftlistFormHandler.shippingAddressId" id="atg_store_giftListAddShippingAddress"
                      name="atg_store_giftListAddShippingAddress" required="true" iclass="custom_select">
                      
            <%-- Get the list of secondary addresses from profile --%>
            <dsp:getvalueof var="secondaryAddresses" bean="Profile.secondaryAddresses"/>

            <c:if test="${empty selectedGiftlistShippingAddress && empty secondaryAddresses}">
              <dsp:option value="">
                <fmt:message key="common.noneSpecified"/>
              </dsp:option>
            </c:if>
            
            <c:forEach var="shippingAddr" items="${secondaryAddresses}">
              <dsp:param name="address" value="${shippingAddr.value}"/>
              <dsp:param name="addressKey" value="${shippingAddr.key}"/>
              <dsp:getvalueof var="addressId" param="address.repositoryId" vartype="java.lang.String"/>
              
              <%--
                Render the address options, selecting the default for this gift
                list if there is one, otherwise we don't care what address is 
                pre-selected.
              --%> 
              <dsp:option value="${addressId}" selected="${addressId eq selectedGiftlistShippingAddress}">
                <dsp:valueof param="addressKey">
                  <fmt:message key="common.undefined"/>
                </dsp:valueof>
              </dsp:option>
            </c:forEach>
          </dsp:select>

          <%-- Display submit button for moving to 'new gift list address' page.  --%>         
          <fmt:message var="addNewAddressText" key="myaccount_giftListAdd.addNewAddress"/>
          
          <%-- Success URL for "moveToNewGiftListAddress" action --%>
          <c:url value="giftAddressAdd.jsp" var="moveToNewGiftListAddressSuccessURL" scope="page">
            <c:param name="showCancel">true</c:param>
            <c:param name="successURL">${addAddressSuccessURL}</c:param>
          </c:url>
          <dsp:input type="hidden" bean="GiftlistFormHandler.moveToNewGiftListAddressSuccessURL"
                     value="${moveToNewGiftListAddressSuccessURL}"/>
                     
          <span class="option">
            <dsp:input type="submit" bean="GiftlistFormHandler.moveToNewGiftListAddress"
                     value="${addNewAddressText}" 
                     iclass="atg_store_textButton"/>
          </span>
        </li>
          
        <%-- Event's type --%>
        <li>
          <label for="atg_store_giftListAddEventType" class="required">
            <fmt:message key="common.type"/>
            <span class="required">*</span>
          </label>
          <dsp:select bean="GiftlistFormHandler.eventType" id="atg_store_giftListAddEventType"
                      name="atg_store_giftListAddEventType" required="true" iclass="custom_select">
            <dsp:option value=""><fmt:message key="common.select"/></dsp:option>
            
            <%--
              The PossibleValues droplet will return all possible values for gift list's 
              eventType enumerated property. 
              
              Input parameters:
                itemDescriptorName
                  item descriptor name 
                propertyName
                  property name for which possible values should be returned
                returnValueObjects
                  If 'true', returns results as PossibleValue objects, rather than
                  raw values. This parameter is set to 'true' as we need both localized and
                  not-localized versions of eventType values. The localized one will be displayed
                  on UI, the not-localized one will be passed to form handler.
                repository
                  The repository the item belongs to
                comparator
                  The comparator object that should be used for sorting possible values.
                  
                Output parameters:
                  values
                    Collection of property's values or collection of corresponding PossibleValue objects
                    
                Open parameters:
                  output
                    The parameter is rendered if possible values are calculated successfully.
             --%>          
            <dsp:droplet name="/atg/dynamo/droplet/PossibleValues">
              <dsp:param name="itemDescriptorName" value="gift-list"/>
              <dsp:param name="propertyName" value="eventType"/>
              <dsp:param name="returnValueObjects" value="true"/>
              <dsp:param name="repository" bean="/atg/commerce/gifts/Giftlists"/>
              <dsp:param name="comparator" bean="/atg/commerce/gifts/GiftlistPossibleValuesComparator"/>
     
              <dsp:oparam name="output">
                <dsp:getvalueof var="eventTypes" vartype="java.lang.Object" param="values"/>
                <c:forEach var="eventType" items="${eventTypes}">
                  <dsp:param name="eventType" value="${eventType}"/>
                  <dsp:option value="${eventType.settableValue}">
                    ${eventType.localizedLabel}
                  </dsp:option>
                </c:forEach>
              </dsp:oparam>
            </dsp:droplet><%-- End Possible Values --%>
          </dsp:select>
        </li>
        
        <%-- Gift list privacy setting, can be public or private.  --%>
        <li>
          <label for="atg_store_giftListAddGiftListStatus" class="required">
            <fmt:message key="myaccount_giftListAdd.privacySetting"/>
            <span class="required">*</span>
          </label>
          <dsp:select bean="GiftlistFormHandler.isPublished" id="atg_store_giftListAddGiftListStatus"
                      name="published" required="true" iclass="custom_select">
            <dsp:option value="true">
              <fmt:message key="myaccount_giftListAdd.publicAnyoneCanSee"/>
            </dsp:option>
            <dsp:option value="false">
              <fmt:message key="myaccount_giftListAdd.viewableOnlyByYou"/>
            </dsp:option>
          </dsp:select>
        </li>
      </ul>
      
      <%-- Display the set of optional form parameters. --%>
      <ul class="atg_store_basicForm">

        <%-- Event's description textarea --%>
        <li>
          <label for="atg_store_giftListAddEventDescription">
            <fmt:message key="common.description"/>
            <span class="subLabel">
              <fmt:message key="common.optional"/>
            <span>
          </label>
          
          <dsp:getvalueof var="giftListDescription" bean="GiftlistFormHandler.description"/>
          <dsp:textarea iclass="custom_textarea textAreaCount" cols="27" maxlength="254"
                        bean="GiftlistFormHandler.description" name="atg_store_giftListAddEventDescription"
                        id="atg_store_giftListAddEventDescription"/>
                        
          <%-- Display the characters counter that show how many characters can be still entered. --%>
          <span class="charCounter option">
            <fmt:message key="common.charactersUsed">
              <fmt:param>
                <strong>${(not empty giftListDescription)? fn:length(giftListDescription):0}</strong>
              </fmt:param>
              <fmt:param>
                <em>254</em>
              </fmt:param>
            </fmt:message>
          </span>
        </li>

        <%-- Event's special instructions textarea --%>
        <li>
          <label for="atg_store_giftListAddSpecialInstructionsId">
            <fmt:message key="myaccount_giftListAdd.specialInstructions"/>
               <span class="subLabel"><fmt:message key="common.optional"/></span>
          </label>
          <dsp:getvalueof var="giftListInstructions" bean="GiftlistFormHandler.instructions"/>
          <dsp:textarea iclass="custom_textarea textAreaCount" cols="27" maxlength="254"
                        bean="GiftlistFormHandler.instructions"
                        name="atg_store_giftListAddSpecialInstructions"
                        id="atg_store_giftListAddSpecialInstructionsId"/>
                        
          <%-- Display the characters counter that show how many characters can be still entered. --%>              
          <span class="charCounter option">
            <fmt:message key="common.charactersUsed">
              <fmt:param>
                <strong>${(not empty giftListInstructions)? fn:length(giftListInstructions):0}</strong>
              </fmt:param>
              <fmt:param>
                <em>254</em>
              </fmt:param>
            </fmt:message>
          </span>
        </li>
      </ul>
    </fieldset>
    
    <%-- Display 'save gift list button' for new gift list form--%>    
    <c:if test="${not editMode}">
      <fmt:message  var="saveText" key="myaccount_giftListAdd.saveGiftList"/>
      <div class="atg_store_formActions">
        <span class="atg_store_basicButton">
          <dsp:input bean="GiftlistFormHandler.saveGiftlist" type="submit" value="${saveText}" id="atg_store_saveGiftLift"/>
        </span>
      </div>
    </c:if>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/giftListAddEdit.jsp#4 $$Change: 791999 $--%>
<%--
  This page removes the given gift list item from gift list or wish list.
   
  Required parameters:
    giftId
      Id of the item to be deleted from the gift\wish list
    giftlistId
      id of the gift\wish list from which item is to be deleted
      
  Optional parameters:
    None       
--%>
<dsp:page>

 
  <%-- 
    Check if parameter giftId has been passed to the page.
    If so, then call RemoveItemFromGiftlist droplet to remove item from the giftlist
    --%>
     
  <dsp:getvalueof var="giftId" param="giftId"/>
  <c:if test="${not empty giftId}">
    
    <%--
      This droplet looks up an item in a giftlist.  If the item is found,
      it is removed from gift list.
      
      Input parameters:
        giftId
          Id of the gift item
        giftlistId
          Id of the gift list
          
      Open parameters:
        error
          The parameter is rendered if error occurred.
    --%>
    <dsp:droplet name="/atg/commerce/gifts/RemoveItemFromGiftlist">
      <dsp:param name="giftlistId" param="giftlistId"/>
      <dsp:param name="giftId" param="giftId"/>
      <dsp:oparam name="error">
        <fmt:message key="myaccount_removeFromList.msgNoGiftListOrNotOwner" />
      </dsp:oparam>
    </dsp:droplet>
  </c:if>

</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/removeGiftListItem.jsp#1 $$Change: 735822 $--%>
<%--
  This gadget sends ViewItem event when user navigates to category page.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page>

  <dsp:importbean bean="/atg/endeca/assembler/cartridge/StoreCartridgeTools"/>
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
  <dsp:importbean bean="/atg/store/catalog/CatalogNavigation"/>
  
  <%-- Check whether use is viewing the category page. --%>
  <dsp:getvalueof var="isUserOnCategoryPage" bean="StoreCartridgeTools.userOnCategoryPage"/>
  
  <c:if test="${isUserOnCategoryPage }">
  
    <%--
      The user is currently on the category page so lookup the category repository item
      and fire ViewItem event for the category.
     --%>
    <%--
      Get the category according to the ID
 
      Input Parameters:
        id - The ID of the category we want to look up
   
      Open Parameters:
        output - Serviced when no errors occur
        error - Serviced when an error was encountered when looking up the category
    
      Output Parameters:
        element - The category whose ID matches the 'id' input parameter  
    --%>
    <dsp:droplet name="CategoryLookup">
      <dsp:param name="id" bean="CatalogNavigation.currentCategory"/>      
      <dsp:oparam name="output">
        <%-- Send 'Category Browsed' event --%>
        <dsp:droplet name="/atg/commerce/catalog/CategoryBrowsed">
          <dsp:param name="eventobject" param="element"/>    
        </dsp:droplet>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/catalogItemBrowsed.jsp#1 $$Change: 742374 $--%>

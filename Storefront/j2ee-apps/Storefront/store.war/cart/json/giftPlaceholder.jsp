<%-- 
  Renders gift place holder.
  
  Required Parameters:
    currentSelection
      The item that is currently being processed.
      
  Optional parameters:
    None.
--%>
<dsp:page>

  <json:object>
    <json:property name="name">
      <fmt:message key="common.freeGiftPlaceholder" />
    </json:property>
    
    <%-- No need to provide URL, this item is not clickable --%>
    <json:property name="url" />
    
    <json:property name="imageUrl">
      <c:out value="/crsdocroot/content/images/products/small/GWP_GiftWithPurchase_small.jpg" escapeXml="false"/>
    </json:property>
    
    <%-- Don't display placeholder as a link --%>
    <json:property name="linkItem" value="${false}"/>
    
    <json:array name="prices">
      <json:object>
        <json:property name="quantity">
          <dsp:valueof param="currentSelection.quantityAvailableForSelection"/>
        </json:property>
        <json:property name="price">
          <fmt:message key="common.FREE" />
        </json:property>
      </json:object>
    </json:array>
  </json:object>
  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/json/giftPlaceholder.jsp#2 $$Change: 788278 $--%>

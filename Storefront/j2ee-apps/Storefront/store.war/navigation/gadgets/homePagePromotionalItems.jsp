<%-- 
  Renders 3 promotional items on the home page that appear as part of the main 
  page image. A user is able to click on the promotional item and is directed to
  the product detail page.
  
  Required Parameters:
    None
    
  Optional Parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/targeting/TargetingRandom"/>
  
  <%-- Assume we have no home promotional items to display --%>
  <c:set var="displayHomePromotionalItems" value="false"/>
  
  <%--
    TargetingRandom is used to perform a targeting operation with the help
    of its targeter. We randomly pick an item from the array returned by the
    targeting operation. Here we use this to determine if any promotional items 
    are available to display on the home page.
   
    Input Parameters:
      targeter - Specifies the targeter service that will perform
                 the targeting
   
    Open Parameters:
      output - At least 1 target was found
  --%>
  <dsp:droplet name="TargetingRandom">
    <dsp:param name="targeter" bean="/atg/registry/Slots/PromotedProduct1"/>
      <dsp:param name="fireViewItemEvent" value="false"/>
      <dsp:oparam name="output">
        <c:set var="displayHomePromotionalItems" value="true"/>
      </dsp:oparam>
  </dsp:droplet>
  <c:if test="${!displayHomePromotionalItems}">
    <dsp:droplet name="TargetingRandom">
    <dsp:param name="fireViewItemEvent" value="false"/>
    <dsp:param name="targeter" bean="/atg/registry/Slots/PromotedProduct2"/>
      <dsp:oparam name="output">
        <c:set var="displayHomePromotionalItems" value="true"/>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>
  <c:if test="${!displayHomePromotionalItems}">
    <dsp:droplet name="TargetingRandom">
    <dsp:param name="fireViewItemEvent" value="false"/>
    <dsp:param name="targeter" bean="/atg/registry/Slots/PromotedProduct3"/>
      <dsp:oparam name="output">
        <c:set var="displayHomePromotionalItems" value="true"/>
      </dsp:oparam>
    </dsp:droplet>
  </c:if>
  
  <%-- If we have any home promotional items display them --%>
  <c:if test="${displayHomePromotionalItems}">
    <div id="atg_store_homePromotionalItems">
      <ul class="atg_store_product">
        <%-- The first promotional product --%>
        <dsp:droplet name="TargetingRandom">
          <dsp:param name="howMany" value="1"/>
          <dsp:param name="targeter" bean="/atg/registry/Slots/PromotedProduct1"/>
          <dsp:param name="fireViewItemEvent" value="false"/>
          <dsp:param name="elementName" value="product"/>
          <dsp:param name="filter" bean="/atg/store/collections/filter/CatalogItemValidatorFilter"/>
          <dsp:oparam name="output">
            <dsp:include page="/promo/gadgets/homePromotionalItemRenderer.jsp"/>
          </dsp:oparam>
        </dsp:droplet>
            
        <%-- The second promotional product --%>
        <dsp:droplet name="TargetingRandom">
          <dsp:param name="howMany" value="1"/>
          <dsp:param name="targeter" bean="/atg/registry/Slots/PromotedProduct2"/>
          <dsp:param name="fireViewItemEvent" value="false"/>
          <dsp:param name="elementName" value="product"/>
          <dsp:param name="filter" bean="/atg/store/collections/filter/CatalogItemValidatorFilter"/>
          <dsp:oparam name="output">
            <dsp:include page="/promo/gadgets/homePromotionalItemRenderer.jsp"/>
          </dsp:oparam>
        </dsp:droplet>
    
        <%-- The third promotional product --%>
        <dsp:droplet name="TargetingRandom">
          <dsp:param name="howMany" value="1"/>
          <dsp:param name="targeter" bean="/atg/registry/Slots/PromotedProduct3"/>
          <dsp:param name="fireViewItemEvent" value="false"/>
          <dsp:param name="elementName" value="product"/>
          <dsp:param name="filter" bean="/atg/store/collections/filter/CatalogItemValidatorFilter"/>
          <dsp:oparam name="output">
            <dsp:include page="/promo/gadgets/homePromotionalItemRenderer.jsp"/>
          </dsp:oparam>
        </dsp:droplet>
      </ul>
    </div>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/navigation/gadgets/homePagePromotionalItems.jsp#2 $$Change: 788278 $--%>
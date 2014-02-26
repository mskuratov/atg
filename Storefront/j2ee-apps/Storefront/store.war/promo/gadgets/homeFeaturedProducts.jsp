<%--
  This page displays home featured products using TargetingRandom droplet.
  
  Required Parameters:
    None
    
  Optional Parameters:
    None  
 --%>
 
<dsp:page>
  <dsp:importbean bean="/atg/targeting/TargetingRandom"/>
  
  <%-- Render medium images for the featured products --%>
  <dsp:param name="imagesize" value="medium"/>

  <div class="atg_store_homepage_products">
    <ul class="atg_store_product">
      <li>
        
        <%-- The first featured product --%>
        <dsp:droplet name="TargetingRandom">
          <dsp:param name="howMany" value="1"/>
          <dsp:param name="targeter" bean="/atg/registry/Slots/FeaturedProduct1"/>
          <dsp:param name="fireViewItemEvent" value="false"/>
          <dsp:param name="elementName" value="product"/>
          <dsp:param name="filter" bean="/atg/store/collections/filter/CatalogItemValidatorFilter"/>         
          <dsp:oparam name="output">
            <dsp:include page="/global/gadgets/promotedProductRenderer.jsp"/>
          </dsp:oparam>
        </dsp:droplet>       
      </li>          
      <li>
        <%-- The second featured product --%>
        <dsp:droplet name="TargetingRandom">
          <dsp:param name="howMany" value="1"/>
          <dsp:param name="targeter" bean="/atg/registry/Slots/FeaturedProduct2"/>
          <dsp:param name="fireViewItemEvent" value="false"/>
          <dsp:param name="elementName" value="product"/>
          <dsp:param name="filter" bean="/atg/store/collections/filter/CatalogItemValidatorFilter"/>
          <dsp:oparam name="output">
            <dsp:include page="/global/gadgets/promotedProductRenderer.jsp"/>
          </dsp:oparam>
        </dsp:droplet>    
      </li>      
      <li>
        <%-- The third featured product --%>
        <dsp:droplet name="TargetingRandom">
          <dsp:param name="howMany" value="1"/>
          <dsp:param name="targeter" bean="/atg/registry/Slots/FeaturedProduct3"/>
          <dsp:param name="fireViewItemEvent" value="false"/>
          <dsp:param name="elementName" value="product"/>
          <dsp:param name="filter" bean="/atg/store/collections/filter/CatalogItemValidatorFilter"/>
          <dsp:oparam name="output">
            <dsp:include page="/global/gadgets/promotedProductRenderer.jsp"/>
          </dsp:oparam>
        </dsp:droplet>           
      </li>      
      <li>
        <%-- The forth featured product --%>
        <dsp:droplet name="TargetingRandom">
          <dsp:param name="howMany" value="1"/>
          <dsp:param name="targeter" bean="/atg/registry/Slots/FeaturedProduct4"/>
          <dsp:param name="fireViewItemEvent" value="false"/>
          <dsp:param name="elementName" value="product"/>
          <dsp:param name="filter" bean="/atg/store/collections/filter/CatalogItemValidatorFilter"/>
          <dsp:oparam name="output">
            <dsp:include page="/global/gadgets/promotedProductRenderer.jsp"/>
          </dsp:oparam>
        </dsp:droplet>   
      </li>      
      <li>
        <%-- The fifth featured product --%>
        <dsp:droplet name="TargetingRandom">
          <dsp:param name="howMany" value="1"/>
          <dsp:param name="targeter" bean="/atg/registry/Slots/FeaturedProduct5"/>
          <dsp:param name="fireViewItemEvent" value="false"/>
          <dsp:param name="elementName" value="product"/>
          <dsp:param name="filter" bean="/atg/store/collections/filter/CatalogItemValidatorFilter"/>
          <dsp:oparam name="output">
            <dsp:include page="/global/gadgets/promotedProductRenderer.jsp"/>
          </dsp:oparam>
        </dsp:droplet>   
      </li>      
    </ul>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/promo/gadgets/homeFeaturedProducts.jsp#2 $$Change: 788278 $--%>

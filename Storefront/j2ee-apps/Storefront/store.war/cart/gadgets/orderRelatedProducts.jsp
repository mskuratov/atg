<%--
  Displays the products related to the order that appear on the checkout page.
  A user is able to click on a related product and is directed to the product
  display page.
  
  Required Parameters:
    None
  
  Optional Parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/targeting/TargetingRandom"/>
  
  <%--
    TargetingRandom is used to perform a targeting operation with the help
    of its targeter. We randomly pick an item from the array returned by the
    targeting operation. Here we use this to check if the RelatedItemsOfCart
    targeter contains any items.
   
    Input Parameters:
      targeter
        Specifies the targeter service that will perform the targeting.
      howMany
        the maximum number of targeted items to return.
      fireViewItemEvent
        A boolean indicating whether this type of event should be fired.
   
     Open Parameters:
       outputStart
         Rendered before any output tags if the result array is not empty.
       output
         At least 1 target was found.
       outputEnd
         Rendered after all output tags if the result array is not empty.
      
     Output Parameters:
       element
         the result of a target operation.
  --%>
  <dsp:droplet name="TargetingRandom">
    <dsp:param name="howMany" value="4"/>
    <dsp:param name="targeter" bean="/atg/registry/Slots/RelatedItemsOfCart"/>
    <dsp:param name="fireViewItemEvent" value="false"/>
    <dsp:param name="filter" bean="/atg/store/collections/filter/CatalogItemValidatorFilter"/>
    
    <%-- 
      Render text and divs first only if there is at least 1 item in the output
    --%>
    <dsp:oparam name="outputStart">
      <div id="atg_store_product_relatedProducts">
        <h3>
          <fmt:message  key="common.cart.youMayLike"/>
        </h3>

        <ul class="atg_store_product">
    </dsp:oparam>
    
    <%-- Display the related products --%>
    <dsp:oparam name="output">
      <dsp:getvalueof var="count" param="count"/>
      <li class="<crs:listClass count="${count}" size="4" selected="false"/>">
        <%--
          Use the promotedProductRenderer.jsp to render the products image,
          display name and price passing in the product repository item
        --%>
        <dsp:include page="/global/gadgets/promotedProductRenderer.jsp">
          <dsp:param name="product" param="element"/>
          <dsp:param name="imagesize" value="medium"/>
        </dsp:include>
      </li>
    </dsp:oparam>
    
    <%--
      Render closing tags for the ones previously opened in outputStart 
      only if there is at least one item in the output
    --%>
    <dsp:oparam name="outputEnd">
        </ul>
      </div>
    </dsp:oparam>
    
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/gadgets/orderRelatedProducts.jsp#2 $$Change: 788278 $--%>

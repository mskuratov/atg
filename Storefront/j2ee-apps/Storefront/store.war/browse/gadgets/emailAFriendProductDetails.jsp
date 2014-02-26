<%--
  This page renders brief product details about the product being emailed.
  
  Details include:
    product image.
    product name.
    product price.
    product description.
  
  Required Parameters: 
    product
      Repository item of the product being emailed about.
--%>

<dsp:page>

  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  
  <%-- ************************* begin product description ************************* --%>
  <div class="atg_store_emailProduct">
    <%-- Show basic information --%>
      <div class="atg_store_productImage">
        <dsp:include page="/browse/gadgets/productImg.jsp">
          <dsp:param name="showAsLink" value="false"/>
          <dsp:param name="image" param="product.smallImage"/>     
          <dsp:param name="defaultImageSize" value="small"/>     
        </dsp:include>
      </div>
    
      <div class="atg_store_productInfo">
        <div class="atg_store_productTitle">
          <dsp:include page="/browse/gadgets/productName.jsp">
            <dsp:param name="showAsLink" value="false"/>
          </dsp:include>
        </div>
        
        <div class="atg_store_emailProductPrice">
          <%-- Check the size of the sku array to see how we handle things --%>
          <dsp:getvalueof var="childSKUs" param="product.childSKUs"/>
          <c:set var="totalSKUs" value="${fn:length(childSKUs)}"/>
          
          <%--
            The Compare droplet renders one of its open parameters based on
            the relative values of the obj1 and obj2 input parameters.
            
            Input Parameters:
              obj1 - The first object to be compared (e.g obj1 > obj2)
              obj2 - The second object to be compared
              
            Open Parameters:
              greaterthan - Rendered obj1 > obj2
              default - Rendered If either obj1 or obj2 is null
          --%> 
          <dsp:droplet name="Compare">
            <dsp:param name="obj1" value="${totalSKUs}" converter="number" />
            <dsp:param name="obj2" value="1" converter="number" />
            <dsp:oparam name="equal">
              <%-- Display Price --%>
              <dsp:param name="sku" param="product.childSKUs[0]" />
              <dsp:include page="/global/gadgets/priceLookup.jsp" />
            </dsp:oparam>
            <dsp:oparam name="default">
              <%-- Display Price Range --%>
              <dsp:include page="/global/gadgets/priceRange.jsp" />
            </dsp:oparam>
          </dsp:droplet> <%-- End Compare droplet to see if the product has a single sku --%>
        </div>
        
        <div class="atg_store_productDescription">
          <p>
            <dsp:valueof param="product.longDescription" valueishtml="true"/>
          </p>
        </div>
      </div>
    </div>
  <%-- ************************* end product description ************************* --%>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/emailAFriendProductDetails.jsp#2 $$Change: 788278 $--%>

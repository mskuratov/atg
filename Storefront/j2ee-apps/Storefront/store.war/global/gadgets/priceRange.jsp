<%-- 
  On this page we want to show both the standard price range and the sale price 
  range (if one exists). To do so, we call the PriceRangeDroplet twice, once for
  the standard prices and a second time for the sale prices.

  Required Parameters:
    product
      the product repository item to display a range of prices for
  
  Optional Parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  <dsp:importbean bean="/atg/commerce/pricing/PriceRangeDroplet"/>

  <%-- 
    PriceRangeDroplet is used to generate the highest and the lowest prices for
    a product based on its child SKUs.
    
    In this call to the PriceRangeDroplet, we override the salePriceList parameter
    with the shopper's standard price list (profile.priceList). In so doing, we
    force both the priceList and the salePriceList parameters to reference the
    standard price list. This effectively limits the calculation to take into 
    account only the standard prices. 
        
    For Example, if we have the following price lists for a product:
    listPrice: 100
    salePrice 80
    
    Sale price always wins if it exists, so we force the use of listPrice which will return
    highestPrice:100
    lowestPrice:100
    
    The same goes if a product has a price range, the range will only use the sale prices if
    they exist.
    
    Input Parameters:
      productId - The product repository id
      
      salePriceList - The sale price list
      
    Open Parameters:
      output - Rendered when no errors occur
      
    Output Parameters:
      highestListPrice - The highest price in the standard price list
      
      lowestListPrice - The lowest price in the standard price list
  --%>   
  <dsp:droplet name="PriceRangeDroplet">
    <dsp:param name="productId" param="product.repositoryId"/>
    <dsp:param name="salePriceList" bean="Profile.priceList"/>
    <dsp:oparam name="output">
      <%-- The values of the range calculated from the standard pricelist. --%>
      <dsp:getvalueof var="highestListPrice" vartype="java.lang.Double" param="highestPrice"/>
      <dsp:getvalueof var="lowestListPrice" vartype="java.lang.Double" param="lowestPrice"/>
    </dsp:oparam>
  </dsp:droplet>
  
  <%-- 
    PriceRangeDroplet is used to generate the highest and the lowest prices for
    a product based on its child SKUs.
    
    For the second call to the droplet, we want to use the sale prices 
    in the calculation, so we don't provide any parameter overrides. 
    This means PriceRangeDroplet uses its defaults, profile.priceList 
    and profile.salePriceList, to calculate the lowest and highest prices. 
    
    For Example, if we have the following price lists for a product:
    listPrice: 100
    salePrice 80
    
    Sale price always wins if it exists, so in this case we are returned
    highestPrice:80
    lowestPrice:80
    
    The same goes if a product has a price range, the range will only use the sale prices if
    they exist.
    
    Input Parameters:
      productId - The product repository id
      
    Open Parameters:
      output - rendered when no errors occur
      
    Output Parameters:
      highestPrice - The highest price in the sale price list
      
      lowestPrice - The lowest price in the sale price list
  --%>  
  <dsp:droplet name="PriceRangeDroplet">
    <dsp:param name="productId" param="product.repositoryId"/>           
    <dsp:oparam name="output">
      <dsp:getvalueof var="highestPrice" vartype="java.lang.Double" param="highestPrice"/>
      <dsp:getvalueof var="lowestPrice" vartype="java.lang.Double" param="lowestPrice"/>
      
      <%-- 
        Compare the values returned during the two calls to the PriceRangeDroplet. If they differ,
        it means that the sale price list returned a lower value than the standard price list, in 
        which case, the sale price range should be rendered along with the standard price range.
        
        Because we didnt overwrite the salePricelist in this PriceRangeDroplet call the highestPrice
        output parameter will be the highest price from the salePriceList if it exists, otherwise it
        will be the highest price from the standard priceList. The lowest price output parameter 
        will be the lowest price from the salePriceList if it exists, otherwise it will be the 
        lowest price from the standard priceList.
      --%>
      <c:if test="${highestListPrice != highestPrice || lowestListPrice != lowestPrice}">
        <c:set var="showSalePrice" value="true"/>        
      </c:if>

      <%--
        The Compare droplet renders one of its open parameters based on
        the relative values of the obj1 and obj2 input parameters. Here we
        use it to determine if we have a price range or a single price, if
        we have a range the prices are different, if its a single price the
        prices are the same.
              
        Input Parameters:
          obj1 - The first object to be compared (e.g obj1 > obj2)
                      
          obj2 - The second object to be compared
                      
        Open Parameters:               
          default - Rendered If either obj1 or obj2 is null or if no other
          oparam is rendered
                
          equal - Render If obj1 is equal to obj2
      --%> 
      <dsp:droplet name="Compare">
        <dsp:param name="obj1" param="lowestPrice" converter="number"/>
        <dsp:param name="obj2" param="highestPrice" converter="number"/>
        <%-- A single price --%>
        <dsp:oparam name="equal">
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${lowestPrice }"/>
          </dsp:include>
        </dsp:oparam>
        <%-- Price range --%>
        <dsp:oparam name="default">
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${lowestPrice }"/>
          </dsp:include> -
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${highestPrice }"/>
          </dsp:include>
        </dsp:oparam>
      </dsp:droplet>

      <%-- 
        If the sale price was rendered, then also render the standard 
        price range.
      --%>
      <c:if test="${showSalePrice}">
        <%--
          The Compare droplet renders one of its open parameters based on
          the relative values of the obj1 and obj2 input parameters. Here we
          use it to determine if we have a price range or a single price, if
          we have a range the prices are different, if its a single price the
          prices are the same.
              
          Input Parameters:
            obj1 - The first object to be compared (e.g obj1 > obj2)
                    
            obj2 - The second object to be compared
                     
          Open Parameters:               
            default - Rendered If either obj1 or obj2 is null or if no other
            oparam is rendered
                
            equal - Render If obj1 is equal to obj2
        --%> 
        <dsp:droplet name="Compare">
          <dsp:param name="obj1" value="${lowestListPrice}" converter="number"/>
          <dsp:param name="obj2" value="${highestListPrice}" converter="number"/>
          <%-- A single price --%>
          <dsp:oparam name="equal">
            <span class="atg_store_oldPrice">
              <fmt:message key="common.price.old"/>
              <del>
                <dsp:include page="/global/gadgets/formattedPrice.jsp">
                  <dsp:param name="price" value="${lowestListPrice }"/>
                </dsp:include>
              </del>
            </span>
          </dsp:oparam>
          <%-- A price range --%>
          <dsp:oparam name="default">
            <span class="atg_store_oldPrice">
              <fmt:message key="common.price.old"/>
              <del>
                <dsp:include page="/global/gadgets/formattedPrice.jsp">
                  <dsp:param name="price" value="${lowestListPrice }"/>
                </dsp:include> -
                <dsp:include page="/global/gadgets/formattedPrice.jsp">
                  <dsp:param name="price" value="${highestListPrice }"/>
                </dsp:include>
              </del>
            </span>
          </dsp:oparam>
        </dsp:droplet>
        
      </c:if>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/priceRange.jsp#1 $$Change: 735822 $--%>

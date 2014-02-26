<%--
  This page is used for the products that have several SKUs. It renders
  a range of prices for such products.
  
  Required parameters:
    product
      The product repository item to display a range of prices for
  
  Optional parameters: 
    None.
--%>
<dsp:page>
  
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  <dsp:importbean bean="/atg/commerce/pricing/PriceRangeDroplet"/>
    
  <%-- Get list price range for product --%>  
  <dsp:droplet name="PriceRangeDroplet">
    <dsp:param name="productId" param="product.repositoryId"/>
    <dsp:param name="salePriceList" bean="Profile.priceList"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="highestListPrice" vartype="java.lang.Double" param="highestPrice"/>
      <dsp:getvalueof var="lowestListPrice" vartype="java.lang.Double" param="lowestPrice"/>
    </dsp:oparam>
  </dsp:droplet>
  
  <%-- Get sale price range for product --%>
  <dsp:droplet name="PriceRangeDroplet">
    <dsp:param name="productId" param="product.repositoryId"/>           
    <dsp:oparam name="output">
      <dsp:getvalueof var="highestPrice" vartype="java.lang.Double" param="highestPrice"/>
      <dsp:getvalueof var="lowestPrice" vartype="java.lang.Double" param="lowestPrice"/>
      
      <c:if test="${highestListPrice != highestPrice || lowestListPrice != lowestPrice}">
        <c:set var="showSalePrice" value="true"/>
        <span class="atg_store_newPrice">         
      </c:if>

      <%--
        Compare the lowest and the highest prices, if they are not equal display them
        as range.
       --%>
      <dsp:droplet name="Compare">
        <dsp:param name="obj1" param="lowestPrice" converter="number"/>
        <dsp:param name="obj2" param="highestPrice" converter="number"/>
        <dsp:oparam name="equal">
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${lowestPrice }"/>
          </dsp:include>
        </dsp:oparam>
        <dsp:oparam name="default">
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${lowestPrice }"/>
          </dsp:include> -
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${highestPrice }"/>
          </dsp:include>
        </dsp:oparam>
      </dsp:droplet>

      <c:if test="${showSalePrice}">
        <%-- Display list price range --%>
        </span>
        <dsp:droplet name="Compare">
          <dsp:param name="obj1" value="${lowestListPrice}" converter="number"/>
          <dsp:param name="obj2" value="${highestListPrice}" converter="number"/>
          <dsp:oparam name="equal">
            <span class="atg_store_oldPrice">
              <fmt:message key="common.price.old"/>
              <span style="text-decoration: line-through;">
                <dsp:include page="/global/gadgets/formattedPrice.jsp">
                  <dsp:param name="price" value="${lowestListPrice }"/>
                </dsp:include>
              </span>
            </span>
          </dsp:oparam>
          <dsp:oparam name="default">
            <span class="atg_store_oldPrice">
              <fmt:message key="common.price.old"/>
              <span style="text-decoration: line-through;">
                <dsp:include page="/global/gadgets/formattedPrice.jsp">
                  <dsp:param name="price" value="${lowestListPrice }"/>
                </dsp:include> -
                <dsp:include page="/global/gadgets/formattedPrice.jsp">
                  <dsp:param name="price" value="${highestListPrice }"/>
                </dsp:include>
              </span>
            </span>
          </dsp:oparam>
        </dsp:droplet>
       
      </c:if>
      
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/priceRange.jsp#1 $$Change: 735822 $--%>

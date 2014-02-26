<%--
  This page just looks the price up in the list price and sales price price lists
  
  Required parameters:
    product
      The product repository item whose price to display
    sku
      The sku repository item for the product whose price to display
      
  Optional parameters:
    None.
--%>
<dsp:page>
  
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  
  <%--
    The first call to price droplet is going to get the price from the profile's list price or 
    the default price list.
   --%>
  <%--
    Given a productId, a skuId, and a price list, returns the given price.
    
    Input parameters:
      product
        The product whose price we are interested in.
      sku
        The SKU whose price we are interested in.
      priceList
        The priceList that we should retrieve the price from.
      
    Output parameters:
      price
        The found price for the SKU.
      
    Open parameters:
      output
        This parameter is rendered when price exist for the given SKU.
  --%>   
  <dsp:droplet name="PriceDroplet">
    <dsp:param name="product" param="product"/>
    <dsp:param name="sku" param="sku"/>
    <dsp:oparam name="output">
      <dsp:setvalue param="theListPrice" paramvalue="price"/>
      
      <%-- The second call is in case the sale price exists --%>
      <dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
      <c:choose>
        <c:when test="${not empty profileSalePriceList}">
          <dsp:droplet name="PriceDroplet">
            <dsp:param name="priceList" bean="Profile.salePriceList"/>
            <dsp:oparam name="output">
              <%-- There are both list and sale prices for the SKU, display them both. --%>
              <span class="atg_store_newPrice">
                <dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
                <dsp:include page="/global/gadgets/formattedPrice.jsp">
                  <dsp:param name="price" value="${listPrice }"/>
                </dsp:include>
              </span>
              <span class="atg_store_oldPrice">
                <fmt:message key="common.price.old"/>
                <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
                
                <span style="text-decoration: line-through;">
                  <dsp:include page="/global/gadgets/formattedPrice.jsp">
                    <dsp:param name="price" value="${price }"/>
                  </dsp:include>
                </span>
              </span>
            </dsp:oparam>
            <dsp:oparam name="empty">
              <%-- The sale price for the SKU is not found. Display the list price only --%>
              <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
              <dsp:include page="/global/gadgets/formattedPrice.jsp">
                <dsp:param name="price" value="${price }"/>
              </dsp:include>
            </dsp:oparam>
          </dsp:droplet><%-- End price droplet on sale price --%>
        </c:when>
        <c:otherwise>
          <%-- There is no sale price list. Display the SKU's list price only --%>
          <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
          <dsp:include page="/global/gadgets/formattedPrice.jsp">
            <dsp:param name="price" value="${price }"/>
          </dsp:include>
        </c:otherwise>
      </c:choose>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/priceLookup.jsp#1 $$Change: 735822 $--%>

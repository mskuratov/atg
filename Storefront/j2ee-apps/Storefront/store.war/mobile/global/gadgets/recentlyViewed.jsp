<%--
  Renders "Recently Viewed Items" slider panel based on "Profile.recentlyViewedProducts"
  user profile collection.
  It's also possible to exclude particular products from the list and
  limit the number of products that can be displayed.

  Page includes:
    /mobile/global/gadgets/productsHorizontalList.jsp - Renders the slider

  Required Parameters:
    None

  Optional Parameters:
    exclude
      This can be a product ID, a list of product IDs or List of
      product "RepositoryItems" that are to be excluded from the
      "Recently Viewed Items" list.
    size
      The number of products that are to be displayed in the
      "Recently Viewed Items" list. A default size is defined in
      the "RecentlyViewedFilterDroplet" "filter" component.
      The default value (if this parameter is not specified) is 5.
    index
      The index of the Recently Viewed slider. This is only needed if 
      there are going to be other sliders on the page. In this case, 
      the including page needs to specify the index of the Recently 
      Viewed slider.
--%>
<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/store/droplet/RecentlyViewedFilterDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>

  <dsp:droplet name="RecentlyViewedFilterDroplet">
    <dsp:param name="collection" bean="Profile.recentlyViewedProducts"/>
    <dsp:param name="exclude" param="exclude"/>
    <dsp:param name="size" param="size"/>
    
    <dsp:getvalueof var="index" param="index"/>
    <c:if test="${empty index}">
      <c:set var="index" value="1"/>
    </c:if>

    <dsp:oparam name="output">
      <div class="sliderTitle">
        <fmt:message key="mobile.browse_recentlyViewedProducts.title"/>
      </div>
      <dsp:include page="${mobileStorePrefix}/global/gadgets/productsHorizontalList.jsp">
        <dsp:param name="products" param="filteredCollection" />
        <dsp:param name="index" value="${index}" />
      </dsp:include>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/global/gadgets/recentlyViewed.jsp#4 $$Change: 790558 $--%>

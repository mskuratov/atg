<%--
  This gadget updates category last browsed property on the current profile.
  If no category ID specified, default value would be taken from the CatalogNavHistory collection.

  Required parameters:
    None.

  Optional parameters:
    categoryLastBrowsed
      Category ID to update profile's property with.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory"/>
  <dsp:importbean bean="/atg/userprofiling/Profile" />

  <dsp:getvalueof var="categoryLastBrowsed" param="categoryLastBrowsed"/>
  <c:if test="${empty categoryLastBrowsed}">
    <%-- No ID specified. Get top level category from navigation history component. --%>
    <dsp:getvalueof var="navHistory" bean="CatalogNavHistory.navHistory"/>
    <c:if test="${not empty navHistory && fn:length(navHistory) > 1}">
      <dsp:getvalueof var="categoryLastBrowsed" bean="CatalogNavHistory.navHistory[1].repositoryId"/>
    </c:if>     
  </c:if>

  <%-- Update profile's property. --%>
  <c:if test="${not empty categoryLastBrowsed}">
    <dsp:setvalue bean="Profile.categoryLastBrowsed" value="${categoryLastBrowsed}"/>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/categoryLastBrowsed.jsp#2 $$Change: 742374 $--%>

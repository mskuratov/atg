<%--
  This page requests the user to enter his/her email address where to send
  notification message.
  
  Required parameters:
    skuId
      The ID of the SKU to notify about when it is available.
    productId
      The ID of the product to notify about when it is available.
  
  Optional parameters:
    None.  
 --%>
<dsp:page> 

  <crs:popupPageContainer divId="atg_store_notifyMeRequest"
                          titleKey="browse_notifyMeRequestPopup.title"
                          textKey="browse_notifyMeRequestPopup.intro">
    <dsp:include page="/browse/gadgets/notifyMeRequest.jsp">
      <dsp:param name="skuId" param="skuId"/>
      <dsp:param name="productId" param="productId"/>
    </dsp:include>
  </crs:popupPageContainer>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/notifyMeRequestPopup.jsp#1 $$Change: 735822 $ --%>

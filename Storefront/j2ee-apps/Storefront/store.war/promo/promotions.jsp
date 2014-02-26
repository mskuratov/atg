<%--
  Top-level page for showing user related as well 
  as global promotions available on the Store
  
  Required Parameters:
    None

  Optional Parameters:
    None  
--%>

<dsp:page>
  <crs:pageContainer divId="atg_store_promotionsIntro" titleKey="" 
                     bodyClass="atg_store_promotions"
                     selpage="PROMOTIONS">
    <div id="atg_store_contentHeader">
      <h2 class="title">
        <fmt:message key="browse_promotions.title"/>
      </h2>
    </div>
    <dsp:include page="/global/gadgets/promotions.jsp">
      <dsp:param name="divId" value="atg_store_promotions"/>
    </dsp:include>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/promo/promotions.jsp#1 $$Change: 735822 $--%>
<%--
  This page is called upon session expiry which renders a session expired message.
--%>
<dsp:page>

  <crs:pageContainer divId="atg_store_sessionExpiredIntro" index="false" follow="false" bodyClass="atg_store_sessionExpired">

    <div id="atg_store_contentHeader">
      <h2 class="title">
        <fmt:message key="global_sessionExpired.title"/>
      </h2>
    </div>
    
    <crs:messageContainer id="atg_store_sessionExpired" titleKey="global_sessionExpired.sessionExpireMsg"/>

  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/sessionExpired.jsp#1 $$Change: 735822 $--%>

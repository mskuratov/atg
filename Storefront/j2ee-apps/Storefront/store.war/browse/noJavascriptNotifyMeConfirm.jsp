<%--
  Renders the "We'll Notify You" dialog when javascript is turned off.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>

<dsp:page> 
  <div id="atg_store_notifyMeConfirm">
    <crs:messageWithDefault key="browse_notifyMeConfirmPopup.title"/>
    <c:if test="${!empty messageText}">
      <h2 class="title">
        ${messageText}
      </h2>
    </c:if>
    <crs:messageWithDefault key="browse_notifyMeConfirmPopup.intro"/>
    <c:if test="${!empty messageText}">
      <p>
        ${messageText}
      </p>
    </c:if>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/noJavascriptNotifyMeConfirm.jsp#1 $$Change: 735822 $--%>

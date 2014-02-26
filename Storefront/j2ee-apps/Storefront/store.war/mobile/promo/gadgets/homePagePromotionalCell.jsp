<%--
  Renders promotional item cell html.

  Required Parameters:
    promotionalContent
      Promotional item
    childTargeter
      Used Targeter

  Optional Parameters:
    None
--%>
<dsp:page>
  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="promotionalContent" param="promotionalContent"/>
  <dsp:getvalueof var="childTargeter" param="childTargeter"/>

  <div class="cell">
    <div class="homePromotionalWrap">
      <img src="${promotionalContent.derivedImage}" alt="${promotionalContent.displayName}"
           class="homePromotionalImage" style="background-image:url(${promotionalContent.description})"/>
      <input type="hidden" value="${childTargeter}"/>
    </div>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/promo/gadgets/homePagePromotionalCell.jsp#2 $$Change: 742374 $--%>

<%--
  This gadget calculates a property that should be displayed for the specified product. And then it includes a
  moreDetailsDisplay page to render this property.

  Required parameters:
    product
      Specifies a product whose parameters should be displayed.

  Optional parameters:
    None.
--%>

<dsp:page>
  <div class="atg_store_productDescription">
    <div id="description">
      <dsp:valueof param="product.longDescription" valueishtml="true">
        <fmt:message key="common.longDescriptionDefault"/>
      </dsp:valueof>
    </div>

    <div id="features">
      <dsp:getvalueof var="productFeatures" param="product.features"/>
      <dl>
        <c:forEach var="feature" items="${productFeatures}">
          <dsp:param name="feature" value="${feature}"/>
          <dt>
            <dsp:valueof param="feature.displayName">
              <fmt:message key="browse_moreDetails.highlightNameDefault" />
            </dsp:valueof>
          </dt>
        </c:forEach>
      </dl>
    </div>

    <fmt:message var="giftWrapMessage" key="common.itemGiftWrapIneligible"/>
    <dsp:getvalueof var="childSKUs" vartype="java.util.Collection" param="product.childSKUs"/>
    <c:forEach var="childSku" items="${childSKUs}">
      <dsp:param name="childSku" value="${childSku}"/>
      <dsp:getvalueof var="ifSkuGiftwrappable" vartype="java.lang.Boolean" param="childSku.giftWrapEligible"/>
      <c:if test="${ifSkuGiftwrappable}">
        <c:remove var="giftWrapMessage"/>
      </c:if>
    </c:forEach>

    <c:if test="${not empty giftWrapMessage}">
      <div id="gift_wrap_not_eligible">
        <c:out value="${giftWrapMessage}" escapeXml="false"/>
      </div>
    </c:if>

    <dsp:include page="/navigation/gadgets/clickToCallLink.jsp">
      <dsp:param name="pageName" value="productDetail"/>
    </dsp:include>
    
   <%-- Display link to knowledge base search feature --%>
    <dsp:include page="/navigation/gadgets/knowledgeBase.jsp">
    </dsp:include>
    
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/moreDetails.jsp#2 $$Change: 788278 $ --%>

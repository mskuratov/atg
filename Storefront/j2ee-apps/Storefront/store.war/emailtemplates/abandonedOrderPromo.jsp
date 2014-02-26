<%--
  Abandoned order promotion email template. The template contains abandoned order promotional
  content with description.
  
  Required parameters:
    None. 
      
  Optional parameters:
    locale
      Locale that specifies in which language email should be rendered.
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/registry/RepositoryTargeters/ProductCatalog/AbandonedOrderPromotion"/>
  <dsp:importbean bean="/atg/targeting/TargetingFirst"/>

  <%-- Get sender email address for promotional emails from site configuration.  --%>
  <dsp:getvalueof var="promotionFromAddress" bean="Site.promotionEmailAddress" />

  <%-- Email subject --%>
  <fmt:message var="emailSubject" key="emailtemplates_abandonedOrderPromo.subject">
    <fmt:param>
      <dsp:valueof bean="Site.name" />
    </fmt:param>
  </fmt:message>

  <crs:emailPageContainer divId="atg_store_abandonedOrderPromoIntro" 
                          messageSubjectString="${emailSubject}"
                          messageFromAddressString="${promotionFromAddress}">
    <jsp:body>
      <%-- Get URL prefix built by emailPageContainer tag--%>
      <dsp:getvalueof var="httpServer" param="httpServer"/>

      <table border="0" cellpadding="0" cellspacing="0" width="609" 
             style="font-size:14px;margin-top:0px;margin-bottom:30px"
             summary="" role="presentation">
        <tr>
          <%-- Email content. --%>
          <td style="color:#666;font-family:Tahoma,Arial,sans-serif;">
            <fmt:message key="emailtemplates_abandonedOrderPromo.greeting">
              <fmt:param>
                <dsp:valueof bean="Profile.firstName"/>
              </fmt:param>
            </fmt:message>
  
            <br /><br />
            <fmt:message key="emailtemplates_abandonedOrderPromo.discountOnNextOrder">
              <fmt:param>
                <dsp:valueof bean="Site.name" />
              </fmt:param>
            </fmt:message>
            <br /><br />
  
            <%--
              This droplet performs a targeting operation with the help of the specified 
              targeter, and renders its output parameter 'howMany' times (the default is 1).  
              At each iteration, the output 'element' parameter is set to 
              the next item in the resulting array of target objects.
              
              Input parameters:
                targeter
                  Targeter to perform the targeting. AbandonedOrderPromotion targeter is 
                  specified here that return promotionalContent item for abandoned order
                  promotion
                fireViewItemEvent
                  Boolean that specifies whether to fire view item event.
                filter
                  Filter that will be applied to the result collection.  
                  
              Output parameters:
                element
                  Each time the output parameter is rendered, this parameter is set 
                  to the next item taken from the array returned by the targeting operation.
                  In this case element parameter will contain promotionalContent item
                  for abandoned order promotion.
                  
               Open parameters:
                 output
                   This parameter is rendered the number of times specified by 
                   the 'howMany' parameter (or until all the targeting results 
                   are displayed). In this case only 1 promotional content is returned
                   by AbandonedOrderPromotion targeter.   
             --%>
            <dsp:droplet name="TargetingFirst">
              <dsp:param name="fireViewItemEvent" value="false"/>
              <dsp:param name="targeter" bean="AbandonedOrderPromotion"/>
              <dsp:param name="filter" bean="/atg/store/collections/filter/PromotionValidatorFilter"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="promotionBanner" param="element.derivedImage"/>
                <dsp:getvalueof var="promotionDisplayName" param="element.displayName"/>
                <dsp:include page="/emailtemplates/gadgets/emailSiteLinkDisplay.jsp">
                  <dsp:param name="path" value="/index.jsp"/>
                  <dsp:param name="locale" param="locale"/>
                  <dsp:param name="httpServer" value="${httpServer}"/>
                  <dsp:param name="imageUrl" value="${httpServer}${promotionBanner}"/>
                  <dsp:param name="imageAltText" value="${promotionDisplayName}"/>
                </dsp:include>
  
              </dsp:oparam>
            </dsp:droplet>
  
          </td>
        </tr>
      </table>
    </jsp:body>

  </crs:emailPageContainer>

</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/abandonedOrderPromo.jsp#2 $$Change: 788278 $--%>
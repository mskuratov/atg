<%-- 
  Renders the home page hero image and uses the homePromotionalItems.jsp to 
  render promotional items on the home page that appear as part of the home 
  page hero image. The home page hero image depends on which user segment the 
  current user belongs to. User segments are a way to divide and target users,
  for example, "People whose gender is female" may be part of a 'Women Only' 
  user segment. 
  
  Required Parameters:
    None
    
  Optional Parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/Cache"/>
  <dsp:importbean bean="/atg/registry/Slots/HomeTheme"/>
  <dsp:importbean bean="/atg/targeting/TargetingRandom"/>
  <dsp:importbean bean="/atg/targeting/TargetingForEach"/>
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean var="requestLocale" bean="/atg/dynamo/servlet/RequestLocale" />
  <dsp:importbean bean="/atg/store/droplet/ItemValidatorDroplet" />

  
  <div id="atg_store_homePageHero">
    <%--
      TargetingRandom is used to perform a targeting operation with the help
      of its targeter. We randomly pick an item from the array returned by the
      targeting operation. Here we use it to retrieve the home page hero image,
      in order to display it.
     
      Input Parameters:
        targeter - Specifies the targeter service that will perform
                   the targeting
     
      Open Parameters:
        output - At least 1 target was found
        
      Output Parameters:
        element - the result of a target operation
      
    --%>
    <dsp:droplet name="TargetingRandom">
      <dsp:param name="targeter" bean="HomeTheme"/>
      <dsp:oparam name="output">
        <dsp:setvalue param="promotionalContent" paramvalue="element"/>
        
        <dsp:droplet name="ItemValidatorDroplet">
          <dsp:param name="item" param="promotionalContent"/>
          <dsp:oparam name="true">
            <dsp:getvalueof var="promoId" param="promotionalContent.repositoryId"/>
            <dsp:getvalueof var="currentSiteId" bean="Site.id"/>
        
            <%--
              Cache is used to cache the contents of its open parameter "output". It
              improves performance for pages that generate dynamic content which is
              the same for all users.
          
              Input Parameters:
                key - contains a value that uniquely identifies the content
          
              Open Parameters:
                output - The serviced value is cached for use next time     
            --%>
            <dsp:droplet name="Cache">
              <%-- 
                The key generated will be the same if the promoId is the same, the siteId is the
                same and the locale is the same. This means that this promotion on this site will be
                loaded from the cache for every user on this particular locale. 
              --%>
              <dsp:param name="key" 
                         value="bp_prhm_${promoId}_${currentSiteId}_${requestLocale.locale.language}"/>
              <dsp:oparam name="output">

                <%-- 
                  DISPLAY HOME PAGE IMAGE
                  Check to see if we have a template, if so display it. The image 
                  or template displayed depends on the particular user segment the current
                  user is in.
                --%>
                <dsp:getvalueof id="pageurl" idtype="java.lang.String" 
                                param="promotionalContent.template.url"/>
                <c:if test="${not empty pageurl}">
                  <dsp:include page="${pageurl}">
                    <dsp:param name="promotionalContent" param="promotionalContent"/>
                    <dsp:param name="omitTooltip" value="false"/>
                  </dsp:include>
                </c:if>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
 
      </dsp:oparam>
    </dsp:droplet>
    
    <%-- 
      DISPLAY HOME PAGE PROMOTIONS
      The promotions displayed depends on the particular group the current
      user is in.
     --%>
    <dsp:include page="/navigation/gadgets/homePagePromotionalItems.jsp" />
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/navigation/gadgets/homePagePromotions.jsp#2 $$Change: 788278 $--%>

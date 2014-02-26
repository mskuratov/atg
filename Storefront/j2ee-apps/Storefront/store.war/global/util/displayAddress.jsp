<%--
  This gadget displays an address information on the page. Address nickname is not displayed here.

  Required parameters:
    address
      Address item to be displayed.

  Optional parameters:
    private
      Flags, if we should not display address itself. Person's name only will be displayed.
    displayName
      Flags, whether person's name should be displayed.
--%>

<dsp:page>
  <%--
    Condition to determine the CountryCode so that Country-specific Address formats can be rendered.
    Currently only default has been included for US Address formats.
    The condition is used as a place holder for future extensibility when multiple countries will be supported.
  --%>

  <dsp:getvalueof var="address" param="address"/>

  <c:choose>
    <%-- 
      Only try to display address information when the address firstName is not null. If
      firstName is null, no address exists as first name is a required field when creating
      an address.
    --%>
    <c:when test="${not empty address.firstName}">
  
      <dsp:getvalueof var="addressValue" param="address.country"/>
      <c:choose>
        <c:when test='${addressValue == ""}'/>
        <c:otherwise>
          <%-- U.S. Address format. --%>
          <dsp:getvalueof var="displayName" param="displayName"/>
          
          <div class="vcard">
            <c:if test="${empty displayName or displayName}">     
              <div class="fn">
                <%-- First, display personal information. --%>
                <span><dsp:valueof param="address.firstName"/></span>
                <span><dsp:valueof param="address.middleName"/></span>
                <span><dsp:valueof param="address.lastName"/></span>
              </div>  
            </c:if>
    
            <div class="adr">
              <dsp:getvalueof var="private" param="private"/>
              <c:choose>
                <c:when test="${private == 'true'}">
                  <%-- Do Not Display Address Details since it is private --%>
                </c:when>
                <c:otherwise>
                  <div class="street-address">
                    <dsp:valueof param="address.address1"/>
                  </div>
                  <div class="street-address">
                    <dsp:getvalueof var="address2" param="address.address2"/>
                    <c:if test="${not empty address2}">
                      <dsp:valueof param="address.address2"/>
                    </c:if>
                  </div>
                </c:otherwise>
              </c:choose>
    
              <%-- Now display state. --%>
              <span class="locality"><dsp:valueof param="address.city"/><fmt:message key="common.comma"/></span>
              <dsp:getvalueof var="state" param="address.state"/>
              <c:if test="${not empty state}">
                <span class="region"><dsp:valueof param="address.state"/></span>
              </c:if>
    
              <%-- ZIP-code and country name. --%>
              <span class="postal-code"><dsp:valueof param="address.postalCode"/></span>
              <div class="country-name">
                <dsp:droplet name="/atg/store/droplet/CountryListDroplet">
                  <dsp:param name="userLocale" bean="/atg/dynamo/servlet/RequestLocale.locale" />
                  <dsp:param name="countryCode" param="address.country"/>
                  <dsp:oparam name="false">
                    <span class="country-name"><dsp:valueof param="countryDetail.displayName" /></span>
                  </dsp:oparam>
                </dsp:droplet>
              </div>
            </div>
    
            <%-- Display phone number. --%>
            <div class="tel"><dsp:valueof param="address.phoneNumber"/></div>
          </div>       
        </c:otherwise>
      </c:choose>
    
    </c:when>
    <c:otherwise>
      <fmt:message key="common.noValue"/>   
    </c:otherwise>
  </c:choose>
      
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/util/displayAddress.jsp#2 $$Change: 788278 $--%>

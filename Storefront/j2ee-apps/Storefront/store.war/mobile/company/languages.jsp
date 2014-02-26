<%--
  Displays a list of available languages.

  Page includes:
    None

  Required parameters:
    None

  Optional parameters:
    locale
      If specified, this page redirects to "moreInfo.jsp"
--%>
<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/ComponentExists"/>
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/store/droplet/DisplayLanguagesDroplet"/>

  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="locale" param="locale"/>

  <dsp:getvalueof var="mobileStorePrefix" bean="/atg/store/StoreConfiguration.mobileStorePrefix"/>

  <%--
    Page redirect logic must be outside "mobilePageContainer".
    If user has already selected a language, go back to the "More" page.
  --%>
  <c:if test="${!empty locale}">
    <c:redirect url="${mobileStorePrefix}/company/moreInfo.jsp"/>
  </c:if>

  <fmt:message var="pageTitle" key="mobile.company.languagesTitle"/>
  <crs:mobilePageContainer titleString="${pageTitle}">
    <jsp:body>
      <dsp:droplet name="ComponentExists">
        <dsp:param name="path" value="/atg/modules/InternationalStore"/>
        <dsp:oparam name="true">
          <%--
            DisplayLanguagesDroplet takes a list of language keys, and returns a
            list of objects associating those keys with a display string
            representing the language.

            Input Parameters:
              languages
                Available language codes for a particular site, e.g. [en,es]
              countryCode
                The country code e.g 'US'

            Open Parameters:
              output
                Serviced when there are no errors

            Output Parameters:
              currentSelection
                The index of the currently selected locale in "displayLanguages" according to the request
              displayLanguages
                List of objects associating the language codes with display languages
          --%>
          <dsp:droplet name="DisplayLanguagesDroplet">
            <dsp:param name="languages" bean="Site.languages"/>
            <dsp:param name="countryCode" bean="Site.defaultCountry"/>
            <dsp:oparam name="output">
              <dsp:getvalueof id="currentSelection" param="currentSelection"/>
              <dsp:getvalueof var="displayLanguages" param="displayLanguages"/>

              <%-- Check if there are alternate languages available for this Site --%>
              <c:if test="${not empty displayLanguages}">
                <%-- Render each alternate languages display name --%>
                <div class="dataContainer">
                  <ul class="dataList">
                    <c:forEach var="language" items="${displayLanguages}" varStatus="languageStatus">
                      <c:set var="isSelected" value="${languageStatus.index == currentSelection ? 'checked' : ''}"/>
                      <%-- 
                        If the language is the current language just render text, otherwise render a clickable link
                      --%>
                      <li>
                        <div class="content">
                          <input name="language" id="lang_${languageStatus.index}" type="radio"
                                 onclick="CRSMA.global.gotoURL(this.value);" value="${language.linkURL}" ${isSelected}/>
                          <label for="lang_${languageStatus.index}" onclick=""><c:out value="${language.displayLanguage}"/></label>
                        </div>
                      </li>
                    </c:forEach>
                  </ul>
                </div>
              </c:if>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>
    </jsp:body>
  </crs:mobilePageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/company/languages.jsp#3 $$Change: 788278 $ --%>

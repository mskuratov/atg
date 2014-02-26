<%--
  This page renders the available languages for a particular site. On clicking 
  one of the language links rendered by this jsp the store page will re-load
  and the text displayed in the newly selected language.
  
  Required Parameters:
    None
  
  Optional Parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/ComponentExists" />
  <dsp:importbean bean="/atg/store/droplet/DisplayLanguagesDroplet" />
  <dsp:importbean bean="/atg/store/profile/SessionBean" />
  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/repository/seo/CatalogItemLink"/>
  <dsp:importbean bean="/atg/repository/seo/ProductLookupItemLink"/>
  <dsp:importbean bean="/atg/repository/seo/BrowserTyperDroplet"/>
    
  <dl id="atg_store_languages">
    <%--
      ComponentExists droplet conditionally renders one of its output parameters
      depending on whether or not a specified Nucleus path currently refers to a
      non-null object.  It it used to query whether a particular component has been
      instantiated, in this case the InternationalStore. If the InternationalStore
      component has been instantiated then we render the alternate languages.
      
      Input Parameters:
        path - The path to a component
        
      Open Parameters:
        true - Rendered if the component 'path' has been instantiated.      
    --%>
    <dsp:droplet name="ComponentExists">
      <dsp:param name="path" value="/atg/modules/InternationalStore" />
      <dsp:oparam name="true">
        <%--
          DisplayLanguagesDroplet takes a list of language keys, and returns a
          list of objects associating those keys with a display string
          representing the language.
          
          Input Paramaters:
            languages - Available language codes for a particular site, e.g
                        [en,es]
            
            countryCode - The country code e.g 'US'
            
          Open Parameters:
            output - Serviced when there are no errors
            
          Output Parameters:
            currentSelection - The index of the currently selected locale in 
                               displayLanguages according to the request
            
            displayLanguages - A list of objects associating the language codes
                               with display languages           
        --%>
        <dsp:droplet name="DisplayLanguagesDroplet">
          <dsp:param name="languages" bean="Site.languages" />
          <dsp:param name="countryCode" bean="Site.defaultCountry" />
          <dsp:oparam name="output">
            <dsp:getvalueof id="currentSelection" param="currentSelection"/>
            <dsp:getvalueof var="displayLanguages" param="displayLanguages"/>
            
            <%-- Check if there are alternate languages available for this Site --%>
            <c:if test="${not empty displayLanguages}">
              <%-- Display the Languages title --%>
              <dt>
                <fmt:message key="navigation_languages.languagesTitle"/>
                <fmt:message key="common.labelSeparator"/>
              </dt>
              
              <%-- Render each alternate languages display name --%>
              <dsp:getvalueof id="size" value="${fn:length(displayLanguages)}"/>
              <c:forEach var="language" items="${displayLanguages}" varStatus="languageStatus">
                <c:set var="isSelected" value="${languageStatus.index == currentSelection}"/>
                
                <%-- 
                  If the language is the current language just render text,
                  otherwise render a clickable link
                --%>
                <dd class="<crs:listClass count="${languageStatus.count}" 
                                          size="${size}" selected="${isSelected}"/>">
                  <c:choose>
                    <c:when test="${isSelected == 'true'}">
                      <dsp:valueof value="${language.displayLanguage}"/>
                    </c:when>
                    <c:otherwise>
                      
                      <%--
                        This droplet determines user's browser type.
                    
                        Input parameters:
                          None.
                    
                        Output parameters:
                          browserType
                            Specifies a user's browser type.
                    
                        Open parameters:
                          output
                            Always rendered.
                      --%>
                      <dsp:droplet name="BrowserTyperDroplet">
                        <dsp:oparam name="output">
                          
                          <dsp:getvalueof var="browserType" param="browserType"/>
                          <dsp:getvalueof var="isProductPage" param="productPage"/>
                          <dsp:getvalueof var="isCategoryPage" param="categoryPage"/>
                          
                          <%--
                            Check whether we are dealing with search spider.
                            If so, and we are on product or category page generate
                            corresponding static links, otherwise display link
                            generated for this language by DisplayLanguagesDroplet
                          --%>
                          <c:choose>
                            <c:when test="${(browserType eq 'robot') and isProductPage}">
                              <%--
                                We are on product detail page and the browser type is 'robot',
                                so generate static product page link
                              --%>
                              
                              <%--
                                This droplet generates a site-aware URL for the product item
                                based on the current request's browser type.
                      
                                Input parameters:
                                  item
                                    Product catalog item an URL should be calculated for.
                      
                                Output parameters:
                                  url
                                    URL calculated.
                      
                                Open parameters:
                                  output
                                    Always rendered.
                              --%>
                              <dsp:droplet name="ProductLookupItemLink">
                                <dsp:param name="item" param="product"/>
                                <dsp:param name="categoryId" param="categoryId"/>
                                <dsp:param name="locale" value="${language.locale}"/>
                                <dsp:oparam name="output">
                                  <dsp:getvalueof id="url" idtype="String" param="url"/>
                                   <dsp:a page="${url}" title="${language.displayLanguage}">
                                      <c:out value="${language.displayLanguage}"/>
                                   </dsp:a>
                                </dsp:oparam>
                              </dsp:droplet>
                            </c:when>
                            <c:when test="${(browserType eq 'robot') and isCategoryPage}">
                               <%--
                                We are on category landing page and the browser type is 'robot',
                                so generate static category page link.
                              --%>
                              
                              <%--
                                This droplet generates a site-aware URL for the product or category
                                based on the current request's browser type.
                      
                                Input parameters:
                                  item
                                    Product catalog item an URL should be calculated for.
                      
                                Output parameters:
                                  url
                                    URL calculated.
                      
                                Open parameters:
                                  output
                                    Always rendered.
                              --%>
                              <dsp:droplet name="CatalogItemLink">
                                <dsp:param name="item" param="category"/>
                                <dsp:param name="locale" value="${language.locale}"/>
                                <dsp:oparam name="output">
                                  <dsp:getvalueof id="url" idtype="String" param="url"/>
                                   <dsp:a page="${url}" title="${language.displayLanguage}">
                                      <c:out value="${language.displayLanguage}"/>
                                   </dsp:a>
                                </dsp:oparam>
                              </dsp:droplet>
                            </c:when>
                            <c:otherwise>
                              <%--
                                It's not product or category page, or browser type is not a 'robot'.
                                So no static link should be generated.
                               --%>
                              <dsp:a href="${language.linkURL}" title="${language.displayLanguage}">
                                <c:out value="${language.displayLanguage}"/>
                              </dsp:a>
                            </c:otherwise>
                            
                          </c:choose>
                        </dsp:oparam>
                      </dsp:droplet>
                              
                    </c:otherwise>
                  </c:choose>
                </dd>
              </c:forEach>
            </c:if>
          </dsp:oparam>
        </dsp:droplet>
      </dsp:oparam>
    </dsp:droplet>
  </dl>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/navigation/gadgets/languages.jsp#1 $$Change: 735822 $--%>
